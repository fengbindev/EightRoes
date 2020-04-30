package com.ssrs.platform.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.PrivilegeModel;
import com.ssrs.framework.cache.CacheDataProvider;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.model.entity.*;
import com.ssrs.platform.service.*;

import java.util.List;

/**
 * 平台相关的缓存项，包括用户、角色、用户角色关联 <br>
 */
public class PlatformCache extends CacheDataProvider {
    public static final String ProviderID = "Platform";
    public static final String Type_UserRole = "UserRole";
    public static final String Type_User = "User";
    public static final String Type_Role = "Role";
    public static final String Type_Branch = "Branch";
    public static final String Type_RolePriv = "RolePriv";
    public static final String Type_BranchPriv = "BranchPriv";

    public static Branch getBranch(String innerCode) {
        if (StrUtil.isEmpty(innerCode)) {
            return null;
        }
        return (Branch) FrameworkCacheManager.get(ProviderID, Type_Branch, innerCode);
    }

    public static String getUserRole(String userName) {
        if (StrUtil.isEmpty(userName)) {
            return null;
        }
        return (String) FrameworkCacheManager.get(ProviderID, Type_UserRole, userName);
    }

    public static Role getRole(String roleCode) {
        if (StrUtil.isEmpty(roleCode)) {
            return null;
        }
        return (Role) FrameworkCacheManager.get(ProviderID, Type_Role, roleCode);
    }

    public static User getUser(String userName) {
        return (User) FrameworkCacheManager.get(ProviderID, Type_User, userName);
    }

    public static void removeRole(String roleCode) {
        FrameworkCacheManager.remove(ProviderID, Type_Role, roleCode);
    }

    public static void addUserRole(String userName, String roleCode) {
        String roles = (String) FrameworkCacheManager.get(ProviderID, Type_UserRole, userName);
        if (StrUtil.isEmpty(roles)) {
            FrameworkCacheManager.set(ProviderID, Type_UserRole, userName, roleCode);
        } else {
            FrameworkCacheManager.set(ProviderID, Type_UserRole, userName, roles + "," + roleCode);
        }
    }

    public static void removeUserRole(String userName, String roleCode) {
        String roles = (String) FrameworkCacheManager.get(ProviderID, Type_UserRole, userName);
        if (StrUtil.isEmpty(roles)) {
            return;
        } else {
            String ur = "," + roles + ",";
            if (ur.indexOf(roleCode) >= 0) {
                ur = StrUtil.replace(ur, roleCode, ",");
            }
            ur = ur.substring(0, ur.length() - 1);
            FrameworkCacheManager.set(ProviderID, Type_UserRole, userName, ur);
        }
    }

    @Override
    public String getExtendItemID() {
        return ProviderID;
    }

    @Override
    public String getExtendItemName() {
        return "平台缓存";
    }

    @Override
    public void onKeyNotFound(String type, String key) {
        if (Type_UserRole.equals(type)) {
            IUserRoleService userRoleService = SpringUtil.getBean(IUserRoleService.class);
            List<UserRole> userRoles = userRoleService.list(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUsername, key));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < userRoles.size(); i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(userRoles.get(i).getRoleCode());
            }
            if (userRoles.size() > 0) {
                FrameworkCacheManager.set(ProviderID, type, key, sb.toString());
            } else {
                // 说明没有任何角色
                FrameworkCacheManager.set(ProviderID, type, key, "");
            }
        } else if (Type_User.equals(type)) {
            IUserService userService = SpringUtil.getBean(IUserService.class);
            User user = userService.getOneByUserName(key);
            if (user != null) {
                FrameworkCacheManager.set(ProviderID, type, key, user);
            }
        } else if (Type_Role.equals(type)) {
            IRoleService roleService = SpringUtil.getBean(IRoleService.class);
            Role role = roleService.getOne(Wrappers.<Role>lambdaQuery().eq(Role::getRoleCode, key));
            if (role != null) {
                FrameworkCacheManager.set(ProviderID, type, key, role);
            }
        } else if (Type_Branch.equals(type)) {
            IBranchService branchService = SpringUtil.getBean(IBranchService.class);
            Branch branch = branchService.getOne(Wrappers.<Branch>lambdaQuery().eq(Branch::getBranchInnercode, key));
            if (branch != null) {
                FrameworkCacheManager.set(ProviderID, type, key, branch);
            }
        } else if (Type_RolePriv.equals(type)) {
            IPrivilegeService privilegeService = SpringUtil.getBean(IPrivilegeService.class);
            Privilege privilege = privilegeService.getOne(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_Role).eq(Privilege::getOwner, key));
            if (privilege != null) {
                PrivilegeModel p = new PrivilegeModel();
                p.parse(privilege.getPrivs());
                FrameworkCacheManager.set(ProviderID, type, key, p);
            }
        } else if (Type_BranchPriv.equals(type)) {
            IPrivilegeService privilegeService = SpringUtil.getBean(IPrivilegeService.class);
            Privilege privilege = privilegeService.getOne(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_Branch).eq(Privilege::getOwner, key));
            if (privilege != null) {
                PrivilegeModel p = new PrivilegeModel();
                p.parse(privilege.getPrivs());
                FrameworkCacheManager.set(ProviderID, type, key, p);
            }
        }
    }
}
