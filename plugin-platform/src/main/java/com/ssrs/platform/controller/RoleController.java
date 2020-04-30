package com.ssrs.platform.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.PrivilegeModel;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.bl.PrivBL;
import com.ssrs.platform.model.entity.Privilege;
import com.ssrs.platform.model.entity.Role;
import com.ssrs.platform.model.entity.UserRole;
import com.ssrs.platform.model.parm.RoleParm;
import com.ssrs.platform.service.IPrivilegeService;
import com.ssrs.platform.service.IRoleService;
import com.ssrs.platform.service.IUserRoleService;
import com.ssrs.platform.util.Page;
import com.ssrs.platform.util.PlatformCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 角色定义表 前端控制器
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
@RestController
@RequestMapping("/api/role")
public class RoleController extends BaseController {
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IPrivilegeService privilegeService;
    @Autowired
    private IUserRoleService userRoleService;

    @Priv
    @GetMapping
    public ApiResponses<Page> list(@RequestParam Map<String, Object> params) {
        Page page = roleService.selectPage(params);
        return success(page);
    }

    @Priv
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> create(@Validated(RoleParm.Create.class) @RequestBody RoleParm roleParm) {
        Role role = roleParm.convert(Role.class);
        Role exitRole = roleService.getById(role.getRoleCode());
        if (exitRole != null) {
            return failure("该角色代码已经存在");
        }
        roleService.save(role);
        Privilege priv = new Privilege();
        priv.setOwnerType(PrivilegeModel.OwnerType_Role);
        priv.setOwner(role.getRoleCode());
        privilegeService.save(priv);
        // 缓存角色信息
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_Role, role.getRoleCode(), role);
        // 角色添加后的扩展点
        // ExtendManager.invoke(AfterRoleAddAction.ExtendPointID, new Object[] { role, priv });
        return success("添加成功");
    }

    @Priv
    @PutMapping("/{roleCode}")
    public ApiResponses<String> update(@PathVariable String roleCode, @Validated(RoleParm.Update.class) @RequestBody RoleParm roleParm) {
        Role role = roleParm.convert(Role.class);
        Role exitRole = roleService.getById(roleCode);
        if (exitRole == null) {
            return failure("没有找到角色");
        }
        roleService.updateById(role);
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_Role, role.getRoleCode(), role);
        // 角色修改后的扩展点
        // ExtendManager.invoke(AfterRoleModifyAction.ExtendPointID, new Object[] { role , PrivBL.getRolePriv(role.getRoleCode())});
        return success("修改成功");
    }

    @Priv
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> delete(@PathVariable String id) {
        Role role = roleService.getById(id);
        PrivBL.assertBranch(role.getBranchInnercode());
        // 删除角色
        roleService.removeById(id);
        // 删除角色与用户的关系
        userRoleService.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getRoleCode, id));
        //删除角色的权限
        privilegeService.remove(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_Role).eq(Privilege::getOwner, id));
        // 删除缓存
        PlatformCache.removeRole(role.getRoleCode());
        // 删除角色后的扩展点
        //ExtendManager.invoke(AfterRoleDeleteAction.ExtendPointID, new Object[] { role, userRoleSet, privilege });
        return success("删除成功");
    }

}
