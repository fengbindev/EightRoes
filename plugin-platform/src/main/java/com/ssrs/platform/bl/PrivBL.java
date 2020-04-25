package com.ssrs.platform.bl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.Current;
import com.ssrs.framework.PrivilegeModel;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.platform.config.AdminUserName;
import com.ssrs.platform.extend.MenuPrivService;
import com.ssrs.platform.model.entity.Branch;
import com.ssrs.platform.model.entity.Privilege;
import com.ssrs.platform.model.entity.Role;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.priv.AbstractMenuPriv;
import com.ssrs.platform.service.IBranchService;
import com.ssrs.platform.service.IPrivilegeService;
import com.ssrs.platform.util.PlatformCache;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

@Component
public class PrivBL {
    private static Log log = LogFactory.get();
    private static IBranchService branchService;
    private static IPrivilegeService privilegeService;

    public PrivBL(IBranchService branchService, IPrivilegeService privilegeService) {
        PrivBL.branchService = branchService;
        PrivBL.privilegeService = privilegeService;
    }

    /**
     * 获取缓存在上下文中的当前操作对象的权限集合
     */
    public static PrivilegeModel getCurrentPrivilege(String type, String id) {
        PrivilegeModel p = (PrivilegeModel) Current.get("_CurrentPriv");
        if (p == null) {
            initCurrent(type, id);
            p = (PrivilegeModel) Current.get("_CurrentPriv");
        }
        if (p == null) {
            p = new PrivilegeModel();
        }
        return p;
    }

    /**
     * 初始化当前上下文变量。因为权限项会在前台页面中多次展现，所以需要将一些变量缓存到上下文中，以提高性能。
     */
    private static void initCurrent(String type, String id) {
        PrivilegeModel p = getPrivilege(type, id);
        Current.put("_CurrentPriv", p);
        Current.put("_CurrentBranchPriv", getBranchPrivilegeRange(type, id));
        Current.put("_FullPrivFlag", getFullPrivFlag(type, id));
        Branch branch = getBranch(type, id);
        boolean flag = false;
        if (branch != null) {
            flag = getFullPrivFlag(PrivilegeModel.OwnerType_Branch, branch.getBranchInnercode());
        }
        Current.put("_BranchFullPrivFlag", flag);
    }

    /**
     * 获得当前的机构权限集合
     */
    public static PrivilegeModel getBranchPrivilegeRange(String type, String id) {
        Branch b = getBranch(type, id);
        return getBranchPrivilegeRange(b);
    }

    /**
     * 获取当前机构权限集合
     *
     * @param branch
     * @return
     */
    public static PrivilegeModel getBranchPrivilegeRange(Branch branch) {
        PrivilegeModel p = null;
        if (branch != null && branch.getTreeLevel() > 1) {// 顶级机构没有缓存权限
            if (getBranchPriv(branch.getBranchInnercode()) != null) {
                p = ObjectUtil.clone(getBranchPriv(branch.getBranchInnercode()));
                if (!AdminUserName.getValue().equals(Current.getUser().getUserName())) {
                    // 这里不能直接操作p,因为p在缓存中有公共引用
                    p.intersect(Current.getUser().getPrivilegeModel());
                }
            }
        }
        if (p == null) {
            p = new PrivilegeModel();
        }
        return p;
    }

    /**
     * 返回指定type和id的权限集合
     */
    public static PrivilegeModel getPrivilege(String type, String id) {
        PrivilegeModel p = null;
        if (PrivilegeModel.OwnerType_Role.equals(type) && StrUtil.isNotEmpty(id)) {
            p = getRolePriv(id);
        } else if (PrivilegeModel.OwnerType_Branch.equals(type) && StrUtil.isNotEmpty(id)) {
            p = getBranchPriv(id);
        } else if (PrivilegeModel.OwnerType_User.equals(type) && StrUtil.isNotEmpty(id)) {
            User user = PlatformCache.getUser(id);
            PrivilegeModel bp = getBranchPriv(user.getBranchInnercode());
            Privilege privilege = privilegeService.getOne(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_User).eq(Privilege::getOwner, id));
            p = new PrivilegeModel();
            if (privilege != null) {
                String privs = privilege.getPrivs();
                p.parse(privs);
            }
            if (!getFullPrivFlag(PrivilegeModel.OwnerType_Branch, user.getBranchInnercode())) {
                p.intersect(bp);
            }
            String roleCodes = PlatformCache.getUserRole(id);
            for (String code : StrUtil.split(roleCodes, ",")) {
                if (ObjectUtil.isEmpty(code)) {
                    continue;
                }
                PrivilegeModel p2 = getRolePriv(code);
                if (p2 != null) {
                    p.union(p2);
                }
            }
            // 机构管理员
            List<Branch> branchList = branchService.list(Wrappers.<Branch>lambdaQuery().like(Branch::getManager, id));
            for (Branch branch : branchList) {
                String[] us = StrUtil.split(branch.getManager(), ",");
                for (String u : us) {
                    if (u.trim().equals(id)) {
                        bp = getBranchPriv(branch.getBranchInnercode());
                        p.union(bp);
                    }
                }
            }
        }
        if (p == null) {// 未为用户或者角色设置过权限
            p = new PrivilegeModel();
        }
        return p;
    }

    /**
     * 判断指定对象是否具有全部的权限
     */
    public static boolean getFullPrivFlag(String type, String id) {
        boolean fullPrivFlag = false;
        if (PrivilegeModel.OwnerType_User.equals(type)) {
            fullPrivFlag = fullPrivFlag || AdminUserName.getValue().equals(id);
        }
        if (PrivilegeModel.OwnerType_Branch.equals(type)) {
            fullPrivFlag = fullPrivFlag || (ObjectUtil.isNotEmpty(PlatformCache.getBranch(id)) && (PlatformCache.getBranch(id).getTreeLevel() == 1));
        }
        return fullPrivFlag;
    }

    public static PrivilegeModel getBranchPriv(String branchInnerCode) {
        if (StrUtil.isEmpty(branchInnerCode)) {
            return null;
        }
        return (PrivilegeModel) FrameworkCacheManager.get(PlatformCache.ProviderID, PlatformCache.Type_BranchPriv, branchInnerCode);
    }

    public static PrivilegeModel getRolePriv(String roleCode) {
        if (StrUtil.isEmpty(roleCode)) {
            return null;
        }
        return (PrivilegeModel) FrameworkCacheManager.get(PlatformCache.ProviderID, PlatformCache.Type_RolePriv, roleCode);
    }

    /**
     * 权限item是否在当前操作对象所属机构允许的权限范围之内
     */
    public static boolean isInBranchPrivRange(String type, String id, String item) {
        PrivilegeModel p = (PrivilegeModel) Current.get("_CurrentBranchPriv");
        if (p == null) {
            initCurrent(type, id);
            p = (PrivilegeModel) Current.get("_CurrentBranchPriv");
        }
        if ((Boolean) Current.get("_BranchFullPrivFlag") == true) {
            return true;
        }
        if (p != null && p.hasPriv(item)) {
            return true;
        }
        return false;
    }

    /**
     * 用户无法修改的权限（用户继承的机构和角色权限不能修改）
     *
     * @param type
     * @param id
     * @return
     */
    public static PrivilegeModel getUncheckablePrivilege(String type, String id) {
        PrivilegeModel p = new PrivilegeModel();
        if (PrivilegeModel.OwnerType_User.equals(type) && ObjectUtil.isNotEmpty(id)) {
            // 将继承自角色的权限放到UncheckableMap中
            String roleCodes = PlatformCache.getUserRole(id);
            for (String code : StrUtil.split(roleCodes, ",")) {
                PrivilegeModel p2 = getRolePriv(code);
                if (p2 != null) {
                    p.union(p2);
                }
            }
            // 机构管理员
            List<Branch> branchList = branchService.list(Wrappers.<Branch>lambdaQuery().like(Branch::getManager, id));
            for (Branch branch : branchList) {
                String[] us = StrUtil.split(branch.getManager(), ",");
                for (String u : us) {
                    if (u.trim().equals(id)) {
                        p.union(getBranchPriv(branch.getBranchInnercode()));
                    }
                }
            }
        }
        return p;
    }

    /**
     * 获得当前的所属机构
     */
    public static Branch getBranch(String type, String id) {
        String branchInnerCode = null;
        if (PrivilegeModel.OwnerType_Role.equals(type) && StrUtil.isNotEmpty(id)) {
            branchInnerCode = PlatformCache.getRole(id).getBranchInnercode();
        } else if (PrivilegeModel.OwnerType_Branch.equals(type) && StrUtil.isNotEmpty(id)) {
            // 如果不是一级机构则返回他的上级机构
            if (id.length() > 4) {
                branchInnerCode = id.substring(0, id.length() - 4);
            } else {
                branchInnerCode = id;
            }
        } else if (PrivilegeModel.OwnerType_User.equals(type) && StrUtil.isNotEmpty(id)) {
            User user = PlatformCache.getUser(id);
            if (user != null) {
                branchInnerCode = user.getBranchInnercode();
            }
        }
        return PlatformCache.getBranch(branchInnerCode);
    }

    /**
     * 获取用户的权限。用户的权限=（用户本身的权限与所在机构权限的交集）+用户所拥有的所有角色的合集
     *
     * @return
     */
    public static PrivilegeModel getUserPriv(String id) {
        if (StrUtil.isEmpty(id)) {
            return null;
        }
        User user = PlatformCache.getUser(id);
        if (user == null) {
            return null;
        }
        PrivilegeModel p = new PrivilegeModel();
        if (AdminUserName.getValue().equals(user.getUsername())) {
            for (AbstractMenuPriv priv : MenuPrivService.getInstance().getAll()) {
                p.put(PrivilegeModel.Flag_Allow, priv.getExtendItemID());
                for (String privID : priv.getPrivItems().keySet()) {
                    p.put(PrivilegeModel.Flag_Allow, privID);
                }
            }
        } else {
            PrivilegeModel bp = getBranchPriv(user.getBranchInnercode());
            p = new PrivilegeModel();
            Privilege privilege = privilegeService.getOne(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_User).eq(Privilege::getOwner, id));
            if (privilege != null) {
                String priv = privilege.getPrivs();
                p.parse(priv);
            }
            if (!getFullPrivFlag(PrivilegeModel.OwnerType_Branch, user.getBranchInnercode())) {
                p.intersect(bp);
            }
            String roleCodes = PlatformCache.getUserRole(user.getUsername());
            for (String roleCode : StrUtil.split(roleCodes, ",")) {
                if (ObjectUtil.isEmpty(roleCode)) {
                    continue;
                }
                PrivilegeModel p2 = getRolePriv(roleCode);
                p.union(p2);
            }
            // 机构管理员
            List<Branch> branchList = branchService.list(Wrappers.<Branch>lambdaQuery().like(Branch::getManager, id));
            for (Branch branch : branchList) {
                String[] us = StrUtil.split(branch.getManager(), ",");
                for (String u : us) {
                    if (u.trim().equals(id)) {
                        bp = getBranchPriv(branch.getBranchInnercode());
                        p.union(bp);
                    }
                }
            }
        }
        return p;
    }

    /**
     * 当前用户是否可以操作指定的权限项
     *
     * @param ownerType 权限拥有者类型
     * @param owner     权限拥有者
     * @param id        权限项ID
     * @return
     */
    public static boolean canSetPriv(String ownerType, String owner, String id) {
        String branchInnerCode = null;
        if (PrivilegeModel.OwnerType_Branch.equals(ownerType)) {
            branchInnerCode = owner;
        } else if (PrivilegeModel.OwnerType_Role.equals(ownerType)) {
            Role role = PlatformCache.getRole(owner);
            if (role != null) {
                branchInnerCode = role.getBranchInnercode();
            }
        } else if (PrivilegeModel.OwnerType_User.equals(ownerType)) {
            User user = PlatformCache.getUser(owner);
            if (user != null) {
                branchInnerCode = user.getBranchInnercode();
            }
        }
        if (ObjectUtil.isEmpty(branchInnerCode)) {
            log.warn("Privilege owner not exists:" + ownerType + "-" + owner);
            return false;
        }
        if (StrUtil.isEmpty(Current.getUser().getBranchInnerCode())) {
            return false;
        }
        if (!branchInnerCode.startsWith(Current.getUser().getBranchInnerCode())) {// 只能操作本机构下的用户的权限
            return false;
        }
        if (!PrivBL.isInBranchPrivRange(ownerType, owner, id)) {
            return false;// 权限项不在机构的范围之内的不能操作
        }
        return true;
    }

    /**
     * 判断用户是否有设置权限
     *
     * @param ownerType
     * @param owner
     * @return
     */
    public static boolean canSetPriv(String ownerType, String owner) {
        String setPrivID = "";
//        if (PrivilegeModel.OwnerType_Branch.equals(type)) {
//            setPrivID = BranchPriv.SetPrivRange;
//        } else if (Privilege.OwnerType_User.equals(type)) {
//            setPrivID = UserPriv.SetPriv;
//        } else if (Privilege.OwnerType_Role.equals(type)) {
//            setPrivID = RolePriv.SetPriv;
//        }
//        PrivilegeModel p = getUserPriv(userName);
//        return p.hasPriv(setPrivID);
        return true;
    }

    /**
     * 保存权限到数据库，保存前会逐项校验当前用户是否有权限操作相应权限项
     */
    public static void setPriv(List<String> keys, String id, String type) {
        Privilege privilege = privilegeService.getOne(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, type).eq(Privilege::getOwner, id));

        // 先过滤掉当前用户没有权限操作的权限项，以及目标拥有者所在机构不能拥有的权限项
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (!canSetPriv(type, id, key)) {
                iterator.remove();
            }
        }
        if (privilege != null) {
            Set<String> unCheckedSet = new HashSet<>();
            PrivilegeModel oldP = new PrivilegeModel();
            // 原有权限
            oldP.parse(privilege.getPrivs());
            PrivilegeModel p = new PrivilegeModel();
            for (String key : keys) {
                p.add(key);
                // 得到本次取消的权限
                if (!oldP.hasPriv(key)) {
                    unCheckedSet.add(key);
                }
            }
            privilege.setPrivs(p.toString());
            privilegeService.update(Wrappers.<Privilege>lambdaUpdate()
                    .set(Privilege::getPrivs, privilege.getPrivs())
                    .set(Privilege::getUpdateTime, LocalDateTime.now())
                    .set(Privilege::getUpdateUser, Current.getUser().getUserName())
                    .eq(Privilege::getOwnerType, type)
                    .eq(Privilege::getOwner, id));
            if (PrivilegeModel.OwnerType_Role.equals(privilege.getOwnerType())) {
                FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_RolePriv, privilege.getOwner());
            } else if (PrivilegeModel.OwnerType_Branch.equals(privilege.getOwnerType())) {
                FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_BranchPriv, privilege.getOwner());
            }
            // 如果是修改机构权限，则将本次取消选中的权限项从子机构、机构下用户、机构下角色的权限中去掉
            if (PrivilegeModel.OwnerType_Branch.equals(type)) { // 如果是机构权限，则子机构、角色、用户都需要修改(去掉多余的权限)
                List<Privilege> privilegeList = privilegeService.list(Wrappers.<Privilege>lambdaQuery()
                        // 获取子机构
                        .eq(Privilege::getOwnerType, type)
                        .likeRight(Privilege::getOwner, id)
                        .ne(Privilege::getOwner, id)
                        // 获取机构下角色
                        .or(privilegeLambdaQueryWrapper -> privilegeLambdaQueryWrapper.exists(String.format("select 1 from sys_role where branch_innercode like '%s' and sys_privilege.owner_type='%s' and  sys_privilege.owner=role_code", id + "%", PrivilegeModel.OwnerType_Role)))
                        // 获取机构下用户
                        .or(privilegeLambdaQueryWrapper -> privilegeLambdaQueryWrapper.exists(String.format("select 1 from sys_user where branch_innercode like '%s' and sys_privilege.owner_type='%s' and  sys_privilege.owner=username", id + "%", PrivilegeModel.OwnerType_User)))
                );
                for (Privilege child : privilegeList) {
                    // 将本次取消选中的权限项从子机构、机构下用户、机构下角色的权限中去掉
                    PrivilegeModel priv = new PrivilegeModel();
                    priv.parse(child.getPrivs());
                    for (String unCheckKey : unCheckedSet) {
                        priv.remove(unCheckKey);
                    }
                    child.setPrivs(priv.toString());
                    privilegeService.update(Wrappers.<Privilege>lambdaUpdate()
                            .set(Privilege::getPrivs, child.getPrivs())
                            .set(Privilege::getUpdateTime, LocalDateTime.now())
                            .set(Privilege::getUpdateUser, Current.getUser().getUserName())
                            .eq(Privilege::getOwnerType, child.getOwnerType())
                            .eq(Privilege::getOwner, child.getOwner()));
                    if (PrivilegeModel.OwnerType_Role.equals(child.getOwnerType())) {
                        FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_RolePriv, child.getOwner());
                    } else if (PrivilegeModel.OwnerType_Branch.equals(child.getOwnerType())) {
                        FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_BranchPriv, child.getOwner());
                    }
                }
            }
        } else {
            PrivilegeModel p = new PrivilegeModel();
            for (String key : keys) {
                p.put(PrivilegeModel.Flag_Allow, key);
            }
            privilege = new Privilege();
            privilege.setOwner(id);
            privilege.setOwnerType(type);
            privilege.setPrivs(p.toString());
            privilegeService.save(privilege);
            if (PrivilegeModel.OwnerType_Role.equals(privilege.getOwnerType())) {
                FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_RolePriv, privilege.getOwner());
            } else if (PrivilegeModel.OwnerType_Branch.equals(privilege.getOwnerType())) {
                FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_BranchPriv, privilege.getOwner());
            }
        }
    }
}
