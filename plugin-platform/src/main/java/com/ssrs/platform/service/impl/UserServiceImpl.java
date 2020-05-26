package com.ssrs.platform.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.Config;
import com.ssrs.framework.Current;
import com.ssrs.framework.PrivilegeModel;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.core.OperateReport;
import com.ssrs.platform.bl.PrivBL;
import com.ssrs.platform.code.NotIncludeUserInfo;
import com.ssrs.platform.code.PasswordCharacterSpecification;
import com.ssrs.platform.code.YesOrNo;
import com.ssrs.platform.config.AdminUserName;
import com.ssrs.platform.model.entity.Privilege;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.mapper.UserMapper;
import com.ssrs.platform.model.entity.UserRole;
import com.ssrs.platform.model.parm.UserParm;
import com.ssrs.platform.service.IPrivilegeService;
import com.ssrs.platform.service.IUserRoleService;
import com.ssrs.platform.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ssrs.platform.util.PasswordUtil;
import com.ssrs.platform.util.PlatformCache;
import com.ssrs.platform.util.PlatformUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-04-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private static Log log = LogFactory.get();
    @Autowired
    private IPrivilegeService privilegeService;
    @Autowired
    private IUserRoleService userRoleService;

    @Override
    public User getOneByUserName(String userName) {
        User user = baseMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, userName));
        return user;
    }

    public static Pattern UserPattern = Pattern.compile("[\\w@\\.\u4e00-\u9fa5]{1,20}", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Reg:同时包含字母和数字
     **/
    public static final Pattern INCLUDELETTERNUMBER_REGEX = Pattern.compile("(?!([a-zA-Z]+|\\d+)$)[a-zA-Z\\d]+");
    /**
     * Reg:同时包含大小写字母和数字
     **/
    public static final Pattern INCLUDE_UPLETTER_LOLTTER_NUMBER_REGEX = Pattern.compile("(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).+");
    /**
     * Reg:同时包含大小写字母数字、下划线,特殊符号
     **/
    public static final Pattern INCLUDE_ALL_REGEX = Pattern.compile("(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[!@#$%^&*.=\\-\\+()]).+");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperateReport addUser(UserParm userParm) {
        OperateReport operateReport = new OperateReport(true);
        String userName = userParm.getUserName();
        if (!UserPattern.matcher(userName).matches()) {
            operateReport.setSuccess(false);
            operateReport.setMessage("最大长度20，仅允许字母/数字/下划线/点/@符号");
            return operateReport;
        }
        User user = userParm.convert(User.class);
        user.setBranchAdmin(YesOrNo.No);
        User exit = getOneByUserName(userName);
        if (ObjectUtil.isNotEmpty(exit)) {
            operateReport.setSuccess(false);
            operateReport.setMessage("用户已经存在");
            return operateReport;
        }
        PrivBL.assertBranch(user.getBranchInnercode());
        String msg = beforeUpdatePassword(user.getPassword(), user);
        if (StrUtil.isNotEmpty(msg)) {
            operateReport.setSuccess(false);
            operateReport.setMessage(msg);
            return operateReport;
        }
        user.setPassword(PasswordUtil.generate(user.getPassword()));
        // 新增用户默认设置为下次登录修改密码状态
        String isOpenThreeSecurity = Config.getValue("isOpenThreeSecurity");
        String nextLoginUpdatePwd = Config.getValue("nextLoginUpdatePwd");
        if (StrUtil.isNotEmpty(isOpenThreeSecurity) && YesOrNo.Yes.equals(isOpenThreeSecurity)
                && StrUtil.isNotEmpty(nextLoginUpdatePwd) && YesOrNo.Yes.equalsIgnoreCase(nextLoginUpdatePwd)) {
            user.setModifyPassStatus(YesOrNo.Yes);
        }
        // TODO 创建用户，加入密码记录
        if (StrUtil.isEmpty(user.getStatus())) {
            user.setStatus(YesOrNo.Yes); // 新建用户默认为启用状态
        }
        save(user);
        // 添加一条权限记录
        Privilege priv = new Privilege();
        priv.setOwnerType(PrivilegeModel.OwnerType_User);
        priv.setOwner(user.getUserName());
        privilegeService.save(priv);
        // 添加角色
        String roleCodes = userParm.getRoles();
        if (StrUtil.isEmpty(roleCodes)) {
            operateReport.setData(new Object[]{user, priv, null});
            return operateReport;
        }
        String[] RoleCodes = roleCodes.split(",");
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_User, user.getUserName(), user);
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_UserRole, user.getUserName(), roleCodes);
        List<UserRole> userRoleSet = new ArrayList<>();
        for (String roleCode : RoleCodes) {
            if (StrUtil.isEmpty(roleCode) || StrUtil.isEmpty(user.getUserName())) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setUserName(user.getUserName());
            userRole.setRoleCode(roleCode);
            userRoleService.save(userRole);
            userRoleSet.add(userRole);
        }
        operateReport.setData(new Object[]{user, priv, userRoleSet});
        return operateReport;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperateReport saveUser(UserParm userParm) {
        OperateReport operateReport = new OperateReport(true);
        String userName = userParm.getUserName();
        User user = getOneByUserName(userName);
        String oldPassword = user.getPassword();
        String oldBranch = user.getBranchInnercode();
        if (ObjectUtil.isEmpty(user)) {
            operateReport.setSuccess(false);
            operateReport.setMessage("用户不存在");
            return operateReport;
        }
        user.setRealName(userParm.getRealName());
        user.setEmail(userParm.getEmail());
        user.setMobile(userParm.getMobile());
        user.setBranchInnercode(userParm.getBranchInnercode());
        if (AdminUserName.getValue().equals(user.getUserName()) && YesOrNo.isNo(user.getStatus())) {
            operateReport.setSuccess(false);
            operateReport.setMessage("超级管理员不允许禁用！");
            return operateReport;
        }
        PrivBL.assertBranch(user.getBranchInnercode());
        user.setPassword(oldPassword);
        if (StrUtil.isEmpty(user.getStatus())) {
            user.setStatus(YesOrNo.Yes); // 若未设置状态则默认为启用状态
        }
        updateById(user);
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_User, user.getUserName(), user);
        // 处理老角色
        userRoleService.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserName, user.getUserName()));
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_UserRole, user.getUserName(), "");
        if (StrUtil.isNotEmpty(user.getBranchInnercode()) && !user.getBranchInnercode().equals(oldBranch)) {
            // 用户机构改变时更新用户权限,取新机构权限和用户权限的交集
            Privilege privilege = privilegeService.getOne(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_User).eq(Privilege::getOwner, user.getUserName()));
            if (privilege != null) {
                PrivilegeModel bp = PrivBL.getBranchPriv(user.getBranchInnercode());
                PrivilegeModel p = new PrivilegeModel();
                String privs = privilege.getPrivs();
                p.parse(privs);
                if (!PrivBL.getFullPrivFlag(PrivilegeModel.OwnerType_Branch, user.getBranchInnercode())) {
                    p.intersect(bp);
                }
                privilege.setPrivs(p.toString());
                privilegeService.update(new Privilege(), Wrappers.<Privilege>lambdaUpdate()
                        .set(Privilege::getPrivs, privilege.getPrivs())
                        .eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_User)
                        .eq(Privilege::getOwner, user.getUserName()));
            }
        }
        // 添加角色
        String roleCodes = userParm.getRoles();
        if (StrUtil.isEmpty(roleCodes)) {
            operateReport.setData(new Object[]{user, roleCodes});
            return operateReport;
        }
        String[] RoleCodes = roleCodes.split(",");
        for (String roleCode : RoleCodes) {
            if (StrUtil.isEmpty(roleCode) || StrUtil.isEmpty(user.getUserName())) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setUserName(user.getUserName());
            userRole.setRoleCode(roleCode);
            userRoleService.save(userRole);
        }
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_UserRole, user.getUserName(), roleCodes);
        operateReport.setData(new Object[]{user, roleCodes});
        return operateReport;
    }

    /**
     * 删除一个用户需要做的步骤： 更新机构中的用户数 删除用户与角色的关系 更新角色中的用户数 删除这个用户的所有权限记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperateReport deleteUser(String userNames) {
        OperateReport operateReport = new OperateReport(true);
        String[] userNameArr = userNames.split(",");
        List<User> userList = list(Wrappers.<User>lambdaQuery().in(User::getUserName, (Object[]) userNameArr));
        for (User user : userList) {
            PrivBL.assertBranch(user.getBranchInnercode());
            if (Current.getUser().getUserName().equals(user.getUserName())) {
                operateReport.setSuccess(false);
                operateReport.setMessage("不能删除当前用户自己!");
                return operateReport;
            }
            if (AdminUserName.getValue().equalsIgnoreCase(user.getUserName())) {
                operateReport.setSuccess(false);
                operateReport.setMessage(AdminUserName.getValue() + "为系统默认的管理员，不能删除!");
                return operateReport;
            }
            FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_User, user.getUserName());
            FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_UserRole, user.getUserName());
            // 删除用户与机构的关系
            userRoleService.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserName, user.getUserName()));
            // 删除用户的权限
            privilegeService.remove(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_User).eq(Privilege::getOwner, user.getUserName()));
            // 删除用户
            remove(Wrappers.<User>lambdaQuery().eq(User::getUserName, user.getUserName()));
            // TODO 用户删除后删除密码历史记录
        }
        operateReport.setData(userList);
        return operateReport;
    }

    /**
     * 修改密码之前执行三级等保功能
     *
     * @param password
     * @param user
     * @return
     */
    private String beforeUpdatePassword(String password, User user) {
        // 未启用三级等保安全功能
        String isOpenThreeSecurity = Config.getValue("isOpenThreeSecurity");
        if (StrUtil.isEmpty(isOpenThreeSecurity)) {
            isOpenThreeSecurity = YesOrNo.No;
        }
        if (YesOrNo.No.equalsIgnoreCase(isOpenThreeSecurity)) {
            return null;
        }
        // 读取账户配置项表，获取字符配置值
        String charRequire = Config.getValue("passwordCharacterSpecification");
        // 读取配置项表，获取不包含用户信息的值
        String notIncludeUserInfo = Config.getValue("notIncludeUserInfo");

        // 判断密码是否开启重复性检查
        String isOpenRecentlyCheck = Config.getValue("isOpenRecentlyCheck");
        // 如果为Y，说明开启重复性检查
        if (YesOrNo.Yes.equalsIgnoreCase(isOpenRecentlyCheck)) {
            int count = Convert.toInt(Config.getValue("repeatCount"));
            // count不为空并且count不能为0的，为0则跳过，继续执行
            if (count > 0) {
                // TODO 密码重复性校验
            }
        }
        StringBuilder builder = new StringBuilder(1000);
        if (PasswordCharacterSpecification.NO_REQUIRED.equalsIgnoreCase(charRequire)) {

        } else if (PasswordCharacterSpecification.INCLUDE_LETTER_NUMBER.equalsIgnoreCase(charRequire)) {
            // 密码是没有加密的密码
            if (!INCLUDELETTERNUMBER_REGEX.matcher(password).matches()) {
                builder.append("必须同时包含字母和数字");
            }
        } else if (PasswordCharacterSpecification.INCLUDE_UPLETTER_LOLTTER_NUMBER.equalsIgnoreCase(charRequire)) {
            if (!INCLUDE_UPLETTER_LOLTTER_NUMBER_REGEX.matcher(password).matches()) {
                builder.append("必须同时包含大写字母、小写字母、数字");
            }
        } else if (PasswordCharacterSpecification.INCLUDE_ALL.equalsIgnoreCase(charRequire)) {
            if (!INCLUDE_ALL_REGEX.matcher(password).matches()) {
                builder.append("必须同时包含大、小写字母、特殊字符、数字");
            }
        }
        if (!StrUtil.isEmpty(builder.toString())) {
            log.info("BeforeUpdatePasswordCheck ——> :" + StrUtil.format("密码中{0}", builder.toString()));
            return StrUtil.format("密码中{0}", builder.toString());
        }

        // 3.密码中不能包含用户信息
        if (StrUtil.isNotEmpty(notIncludeUserInfo)) {
            StringBuilder message = new StringBuilder();
            String[] noVals = notIncludeUserInfo.split(",");
            int length = noVals.length;
            Map<String, Object> codeMap = PlatformUtil.getCodeMap(NotIncludeUserInfo.CODE_TYPE);
            for (int i = 0; noVals != null && i < length; i++) {
                String value = NotIncludeUserInfo.getUserValueByCode(noVals[i], user);
                if (StrUtil.isEmpty(value)) {
                    continue;
                }

                if (password.contains(value)) {
                    message.append(codeMap.get(noVals[i])).append(",");
                }
            }
            if (message.toString().length() > 0) {
                log.info("BeforeUpdatePasswordCheck ——> :" + "密码中不能包含" + message.substring(0, message.toString().length() - 1).toString());
                return "密码中不能包含" + message.substring(0, message.toString().length() - 1).toString();
            }
        }
        return null;
    }
}
