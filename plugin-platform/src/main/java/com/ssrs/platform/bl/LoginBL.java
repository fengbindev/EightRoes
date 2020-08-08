package com.ssrs.platform.bl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.BetweenFormater;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.Config;
import com.ssrs.framework.Current;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.platform.code.OverLoginCountType;
import com.ssrs.platform.code.YesOrNo;
import com.ssrs.platform.config.AdminUserName;
import com.ssrs.platform.controller.handler.AuthCodeURLHandler;
import com.ssrs.platform.extend.item.UserLoginLog;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.service.IUserService;
import com.ssrs.platform.util.LoginContext;
import com.ssrs.platform.util.PasswordUtil;
import com.ssrs.platform.util.PlatformCache;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * 登录逻辑处理
 *
 * @author ssrs
 */
@Component
public class LoginBL {
    private static Log log = LogFactory.get(LoginBL.class);
    private static IUserService userService;

    public LoginBL(IUserService userService) {
        LoginBL.userService = userService;
    }

    public static void validateLoginData(LoginContext context) {
        String username = context.userName;
        String verifyCode = context.authCode;
        boolean showVerifyCode = context.request.getBool("showVerifyCode", false);
        if (StrUtil.isNotEmpty(verifyCode)) {
            showVerifyCode = true;
        }
        // 如果用户名在wrongList中并且没有验证码的时候提示请输入验证码！
        if (context.wrongList.contains(username)) {
            showVerifyCode = true;
            if (StrUtil.isEmpty(verifyCode)) {
                context.status = 2;
                context.message = "请输入验证码";
                return;
            }
        }
        if (showVerifyCode) {
            if (!AuthCodeURLHandler.verify(verifyCode)) {
                context.status = 3;
                context.message = "验证码已过期";
                return;
            }
        }
        context.status = 1; // 通过
    }

    /**
     * 是否开启三级等保安全
     *
     * @return
     */
    public static boolean isOpenAccountSecurity() {
        String isOpenThreeSecurity = Config.getValue("isOpenThreeSecurity");
        if (StrUtil.isEmpty(isOpenThreeSecurity)) {
            isOpenThreeSecurity = YesOrNo.No;
        }
        if (YesOrNo.No.equalsIgnoreCase(isOpenThreeSecurity)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检验密码是否正确
     *
     * @param password
     * @param userPassword
     * @return
     */
    public static boolean validatePassword(String password, String userPassword) {
        if (!PasswordUtil.verify(password, userPassword)) {
            return false;
        }
        return true;
    }

    /**
     * 执行三级等保安全
     *
     * @param context
     * @param user
     */
    public static void executeAccountSecurity(LoginContext context, User user) {
        try {
            String specifyOverTimeLock = Config.getValue("specifyOverTimeLock");
            if (StrUtil.isEmpty(specifyOverTimeLock)) {
                if (validatePassword(context.password, user.getPassword())) {
                    passwordIsExpiration(context, user);
                } else {
                    context.status = 0;
                    context.message = "用户名或密码错误";
                }
                return;
            }
            // 读取用 户配置表中，密码输入错误后最大的尝试次数
            String maxLoginCount = Config.getValue("maxLoginCount");
            long repeatCount = Convert.toLong(maxLoginCount);
            // 错误重复次数为0不受限制，直接检查密码
            if (repeatCount == 0) {
                return;
            }
            // 超过时间锁定类型
            String overLoginCountType = Config.getValue("overLoginCountType");
            // 1天 86400s
            if (OverLoginCountType.TIME_LOCK.equalsIgnoreCase(overLoginCountType)) {
                if (user.getLoginErrorTime() != null) {
                    if ((System.currentTimeMillis() - user.getLoginErrorTime().toInstant(ZoneOffset.of("+8")).toEpochMilli()) / 1000 >= 86400) {
                        user.setLoginErrorCount(0);
                    }
                }
            }
            // 禁止登录时间
            if (user.getForbiddenLoginTime() != null) {
                long forbiddenLoginTime = user.getForbiddenLoginTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
                long betweenMs = forbiddenLoginTime - System.currentTimeMillis();

                if (betweenMs > 0) {
                    String message = StrUtil.indexedFormat("您密码错误重试次数过于频繁，账号临时锁定，请在{0}后尝试", DateUtil.formatBetween(betweenMs, BetweenFormater.Level.SECOND));
                    context.status = 9;
                    context.message = message;
                    return;
                } else {
                    user.setStatus(YesOrNo.Yes);
                    user.setLoginErrorCount(0);
                    user.setForbiddenLoginTime(null);
                }
            }
            // 密码不正确的时候执行
            if (!validatePassword(context.password, user.getPassword())) {
                context.wrongList.add(user.getUserName());
                // 获取用户已经尝试的次数
                int valueOf = Convert.toInt(user.getLoginErrorCount(), 0);
                valueOf++;
                user.setLoginErrorCount(valueOf);
                user.setLoginErrorTime(LocalDateTime.now());

                // 设置完登录错误次数，判断登陆次数是否超限
                isLoginCountOverrun(context, repeatCount, user, overLoginCountType);
                // context.status = 0;
            } else {
                // 密码是否过期
                passwordIsExpiration(context, user);
            }
            userService.updateById(user);
            FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_User, user.getUserName(), user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 超过登录错误最大次数，判断登陆次数是否超限，超限后执行相应的锁定方式
     *
     * @param repeatCount
     * @param user
     * @param overLoginCountType
     */
    private static void isLoginCountOverrun(LoginContext context, long repeatCount, User user, String overLoginCountType) {
        if (Convert.toInt(user.getLoginErrorCount(), 0) >= repeatCount) {
            // B为指定的账号锁定时长，在制定时间内禁止登录
            if (OverLoginCountType.TIME_LOCK.equalsIgnoreCase(overLoginCountType)) {
                // 获取锁定的时长
                String lockTime = Config.getValue("lockTime");
                if (StrUtil.isNotEmpty(lockTime)) {
                    long forbiddenTimeDay = Convert.toLong(lockTime, 0L);
                    if (Convert.toLong(lockTime, 0L) > 0L) {
                        LocalDateTime forbiddenTime = LocalDateTime.now().plusDays(forbiddenTimeDay);
                        user.setForbiddenLoginTime(forbiddenTime);
                        user.setStatus(YesOrNo.No);
                        String message = StrUtil.indexedFormat("您密码错误重试次数过于频繁，账号临时锁定，请在{0}后尝试", DateUtil.formatBetween(forbiddenTimeDay * 1000 * 86400, BetweenFormater.Level.SECOND));
                        context.status = 10;
                        context.message = message;
                        return;
                    }
                }
            } else { // A 直接锁定账号
                if (YesOrNo.Yes.equals(user.getStatus())) {
                    if (!AdminUserName.getValue().equalsIgnoreCase(user.getUserName())) { // 超级管理员不锁定
                        user.setStatus(YesOrNo.No);
                        context.status = 11;
                        context.message = "用户名或密码错误，该用户处于停用状态，请联系管理员！";
                        return;
                    }
                } else {
                    context.status = 11;
                    context.message = "用户名或密码错误，该用户处于停用状态，请联系管理员！";
                    return;
                }
            }
        } else {
            String message = StrUtil.indexedFormat("您还有{0}次尝试次数", repeatCount - Convert.toInt(user.getLoginErrorCount(), 0));
            if (OverLoginCountType.TIME_LOCK.equalsIgnoreCase(overLoginCountType)) {
                log.debug("BeforeLoginCheckAction ——> [用户:" + user.getUserName() + "] 还有" + (repeatCount - Convert.toInt(user.getLoginErrorCount(), 0))
                        + "尝试！");
                context.status = 12;
                context.message = "用户名或密码错误," + message;
                return;
            }
            // 管理员不能锁定账户，当账户安全设置为锁定账户时候则直接检查密码是否正确
            if (AdminUserName.getValue().equalsIgnoreCase(user.getUserName())
                    && OverLoginCountType.ACCESS_LOCK.equalsIgnoreCase(overLoginCountType)) {
                return;
            } else {
                context.status = 13;
                context.message = "用户名或密码错误," + message;
                return;
            }
        }
    }

    /**
     * 检查密码是否过期
     *
     * @param context
     * @param user
     */
    private static void passwordIsExpiration(LoginContext context, User user) {
        // 密码过期时间
        String expiration = Config.getValue("expiration");
        if (StrUtil.isNotEmpty(expiration)) {
            int expirationCount = Convert.toInt(expiration);
            if (expirationCount != 0 && user.getLastModifyPassTime() != null) {
                LocalDateTime addExpirationDay = user.getLastModifyPassTime().plusDays(expirationCount);
                if (LocalDateTime.now().isAfter(addExpirationDay)) {
                    log.info("BeforeLoginCheckAction ——> 账户密码过期！");
                    context.status = 14;
                    context.message = "您的密码已过期";
                    return;
                }
                // TODO 密码过期时间小于3天向用户发送通知
            }
        }
    }

    /**
     * 登录成功后处理
     *
     * @param user
     * @param context
     */
    public static void afterLogin(User user, LoginContext context) {
        // 三级等保功能，判断是否要修改密码
        if (ObjectUtil.isEmpty(user.getLastLoginTime()) && StrUtil.isEmpty(user.getLastLoginIp())
                && YesOrNo.Yes.equalsIgnoreCase(user.getModifyPassStatus())) {
            context.status = 30000; // 初始密码通知

        } else if (YesOrNo.Yes.equalsIgnoreCase(user.getModifyPassStatus())) {
            context.status = 20000; // 密码重置通知
        }
        if (Convert.toInt(user.getLoginErrorCount(), 0)> 0) {
            user.setLoginErrorCount(0);
            user.setForbiddenLoginTime(null);
            user.setModifyPassStatus(YesOrNo.No);
            context.status = 1;
        }
    }

    public static void login(User user) {
        com.ssrs.framework.User.setUserName(user.getUserName());
        if(StrUtil.isNotEmpty(user.getRealName())){
            com.ssrs.framework.User.setRealName(user.getRealName());
        }else {
            com.ssrs.framework.User.setRealName("");
        }
        com.ssrs.framework.User.setBranchInnerCode(user.getBranchInnercode());
        com.ssrs.framework.User.setBranchAdministrator(YesOrNo.isYes(user.getBranchAdmin()));
        Map<String, Object> map = Convert.toMap(String.class, Object.class, user);
        com.ssrs.framework.User.getCurrent().putAll(map);
        com.ssrs.framework.User.setLogin(true);
        com.ssrs.framework.User.setPrivilegeModel(PrivBL.getUserPriv(user.getUserName()));

        user.setLastLoginIp(Current.getRequest().getClientIP());
        user.setLastLoginTime(LocalDateTime.now());
        userService.updateById(user);
        // 记录用户登录日志
        LogBL.addUserLog(UserLoginLog.ID, UserLoginLog.SUBTYPE_LOGIN, UserLoginLog.LOGIN);
    }
}
