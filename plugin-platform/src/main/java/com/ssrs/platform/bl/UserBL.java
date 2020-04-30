package com.ssrs.platform.bl;

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
import com.ssrs.platform.code.YesOrNo;
import com.ssrs.platform.config.AdminUserName;
import com.ssrs.platform.model.entity.Privilege;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.model.entity.UserRole;
import com.ssrs.platform.model.parm.UserParm;
import com.ssrs.platform.service.IPrivilegeService;
import com.ssrs.platform.service.IUserRoleService;
import com.ssrs.platform.service.IUserService;
import com.ssrs.platform.util.PlatformCache;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class UserBL {
    private static Log log = LogFactory.get();
    private static IUserService userService;
    private static IPrivilegeService privilegeService;
    private static IUserRoleService userRoleService;

    public UserBL(IUserRoleService userRoleService, IUserService userService, IPrivilegeService privilegeService) {
        UserBL.userRoleService = userRoleService;
        UserBL.userService = userService;
        UserBL.privilegeService = privilegeService;
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

    public static OperateReport addUser(UserParm userParm) {
        OperateReport operateReport = new OperateReport(true);
        String userName = userParm.getUsername();
        if (!UserPattern.matcher(userName).matches()) {
            operateReport.setSuccess(false);
            operateReport.setMessage("最大长度20，仅允许字母/数字/下划线/点/@符号");
            return operateReport;
        }
        User user = userParm.convert(User.class);
        user.setBranchAdmin(YesOrNo.No);
        User exit = userService.getOneByUserName(userName);
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
        // TODO 密码加密
        //user.setPassword(PasswordUtil.generate(user.getPassword()));
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
        userService.save(user);
        // 添加一条权限记录
        Privilege priv = new Privilege();
        priv.setOwnerType(PrivilegeModel.OwnerType_User);
        priv.setOwner(user.getUsername());
        privilegeService.save(priv);
        // 添加角色
        String roleCodes = userParm.getRoles();
        if (StrUtil.isEmpty(roleCodes)) {
            operateReport.setData(new Object[]{user, priv, null});
            return operateReport;
        }
        String[] RoleCodes = roleCodes.split(",");
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_User, user.getUsername(), user);
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_UserRole, user.getUsername(), roleCodes);
        List<UserRole> userRoleSet = new ArrayList<>();
        for (String roleCode : RoleCodes) {
            if (StrUtil.isEmpty(roleCode) || StrUtil.isEmpty(user.getUsername())) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setUsername(user.getUsername());
            userRole.setRoleCode(roleCode);
            userRoleService.save(userRole);
            userRoleSet.add(userRole);
        }
        operateReport.setData(new Object[]{user, priv, userRoleSet});
        return operateReport;
    }


    public static OperateReport saveUser(UserParm userParm) {
        OperateReport operateReport = new OperateReport(true);
        String userName = userParm.getUsername();
        User user = userService.getOneByUserName(userName);
        String oldPassword = user.getPassword();
        String oldBranch = user.getBranchInnercode();
        if (ObjectUtil.isEmpty(user)) {
            operateReport.setSuccess(false);
            operateReport.setMessage("用户不存在");
            return operateReport;
        }
        user.setRealname(userParm.getRealname());
        user.setEmail(userParm.getEmail());
        user.setMobile(userParm.getMobile());
        user.setBranchInnercode(userParm.getBranchInnercode());
        if (AdminUserName.getValue().equals(user.getUsername()) && YesOrNo.isNo(user.getStatus())) {
            operateReport.setSuccess(false);
            operateReport.setMessage("超级管理员不允许禁用！");
            return operateReport;
        }
        PrivBL.assertBranch(user.getBranchInnercode());
        user.setPassword(oldPassword);
        if (StrUtil.isEmpty(user.getStatus())) {
            user.setStatus(YesOrNo.Yes); // 若未设置状态则默认为启用状态
        }
        userService.updateById(user);
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_User, user.getUsername(), user);
        // 处理老角色
        userRoleService.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUsername, user.getUsername()));
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_UserRole, user.getUsername(), "");
        if (StrUtil.isNotEmpty(user.getBranchInnercode()) && !user.getBranchInnercode().equals(oldBranch)) {
            // 用户机构改变时更新用户权限,取新机构权限和用户权限的交集
            Privilege privilege = privilegeService.getOne(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_User).eq(Privilege::getOwner, user.getUsername()));
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
                        .eq(Privilege::getOwner, user.getUsername()));
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
            if (StrUtil.isEmpty(roleCode) || StrUtil.isEmpty(user.getUsername())) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setUsername(user.getUsername());
            userRole.setRoleCode(roleCode);
            userRoleService.save(userRole);
        }
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_UserRole, user.getUsername(), roleCodes);
        operateReport.setData(new Object[]{user, roleCodes});
        return operateReport;
    }

    /**
     * 删除一个用户需要做的步骤： 更新机构中的用户数 删除用户与角色的关系 更新角色中的用户数 删除这个用户的所有权限记录
     */
    public static OperateReport deleteUser(String userNames) {
        OperateReport operateReport = new OperateReport(true);
        String[] userNameArr = userNames.split(",");
        List<User> userList = userService.list(Wrappers.<User>lambdaQuery().in(User::getUsername, userNameArr));
        for (User user : userList) {
            PrivBL.assertBranch(user.getBranchInnercode());
            if (Current.getUser().getUserName().equals(user.getUsername())) {
                operateReport.setSuccess(false);
                operateReport.setMessage("不能删除当前用户自己!");
                return operateReport;
            }
            if (AdminUserName.getValue().equalsIgnoreCase(user.getUsername())) {
                operateReport.setSuccess(false);
                operateReport.setMessage(AdminUserName.getValue() + "为系统默认的管理员，不能删除!");
                return operateReport;
            }
            FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_User, user.getUsername());
            FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_UserRole, user.getUsername());
            // 删除用户与机构的关系
            userRoleService.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUsername, user.getUsername()));
            // 删除用户的权限
            privilegeService.remove(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_User).eq(Privilege::getOwner, user.getUsername()));
            // 删除用户
            userService.remove(Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()));
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
    private static String beforeUpdatePassword(String password, User user) {
        return null;
    }
}
