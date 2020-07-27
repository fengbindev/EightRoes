package com.ssrs.platform.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.Current;
import com.ssrs.framework.PrivilegeModel;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.extend.ExtendManager;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.bl.LogBL;
import com.ssrs.platform.bl.PrivBL;
import com.ssrs.platform.code.OperateLogType;
import com.ssrs.platform.extend.item.OperateLog;
import com.ssrs.platform.model.entity.Privilege;
import com.ssrs.platform.model.entity.Role;
import com.ssrs.platform.model.entity.UserRole;
import com.ssrs.platform.model.parm.RoleParm;
import com.ssrs.platform.model.query.RoleQuery;
import com.ssrs.platform.point.AfterRoleAddPoint;
import com.ssrs.platform.point.AfterRoleDeletePoint;
import com.ssrs.platform.point.AfterRoleModifyPoint;
import com.ssrs.platform.priv.RoleManagerPriv;
import com.ssrs.platform.service.IPrivilegeService;
import com.ssrs.platform.service.IRoleService;
import com.ssrs.platform.service.IUserRoleService;
import com.ssrs.platform.util.Page;
import com.ssrs.platform.util.PlatformCache;
import com.ssrs.platform.util.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
        String branchInnercode = (String) params.get("branchInnercode");
        String roleName = (String) params.get("roleName");
        LambdaQueryWrapper<Role> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (!Current.getUser().isBranchAdministrator()) {
            if (StrUtil.isEmpty(branchInnercode)) {
                lambdaQueryWrapper.likeRight(Role::getBranchInnercode, Current.getUser().getBranchInnerCode());
            } else {
                lambdaQueryWrapper.likeRight(Role::getBranchInnercode, branchInnercode);
            }
        } else {
            if (StrUtil.isNotEmpty(branchInnercode)) {
                lambdaQueryWrapper.likeRight(Role::getBranchInnercode, branchInnercode);
            }
        }
        lambdaQueryWrapper.like(StrUtil.isNotEmpty(roleName), Role::getRoleName, roleName);
        lambdaQueryWrapper.orderByAsc(Role::getBranchInnercode, Role::getCreateTime);
        IPage<Role> ipage = roleService.page(new Query<Role>().getPage(params), lambdaQueryWrapper);
        Page page = new Page(ipage);
        List<Role> data = (List<Role>) page.getData();
        if (CollUtil.isEmpty(data)) {
            return success(page);
        }
        List<RoleQuery> tmpData = new ArrayList<>(data.size());
        for (Role role : data) {
            RoleQuery roleQuery = BeanUtil.toBean(role, RoleQuery.class);
            roleQuery.setBranchName(PlatformCache.getBranch(roleQuery.getBranchInnercode()).getName());
            tmpData.add(roleQuery);
        }
        page.setData(tmpData);
        return success(page);
    }

    @Priv(RoleManagerPriv.Add)
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> create(@Validated(RoleParm.Create.class) RoleParm roleParm) {
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
        ExtendManager.invoke(AfterRoleAddPoint.ID, new Object[]{role, priv});
        LogBL.addOperateLog(OperateLog.ID, OperateLogType.ADD, "添加角色：" + roleParm.getRoleName(), "添加成功", null);
        return success("添加成功");
    }

    @Priv(RoleManagerPriv.Edit)
    @PutMapping("/{roleCode}")
    public ApiResponses<String> update(@PathVariable String roleCode, @Validated(RoleParm.Update.class) RoleParm roleParm) {
        Role role = roleParm.convert(Role.class);
        Role exitRole = roleService.getById(roleCode);
        if (exitRole == null) {
            return failure("没有找到角色");
        }
        roleService.updateById(role);
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_Role, role.getRoleCode(), role);
        // 角色修改后的扩展点
        ExtendManager.invoke(AfterRoleModifyPoint.ID, new Object[]{role, PrivBL.getRolePriv(role.getRoleCode())});
        LogBL.addOperateLog(OperateLog.ID, OperateLogType.EDIT, "修改角色：" + roleParm.getRoleName(), "修改成功", null);
        return success("修改成功");
    }

    @Priv(RoleManagerPriv.Delete)
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
        ExtendManager.invoke(AfterRoleDeletePoint.ID, new Object[]{role});
        LogBL.addOperateLog(OperateLog.ID, OperateLogType.DELETE, "删除角色：" + role.getRoleName(), "删除成功", null);
        return success("删除成功");
    }

}
