package com.ssrs.platform.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.Current;
import com.ssrs.framework.core.OperateReport;
import com.ssrs.framework.extend.ExtendManager;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.model.entity.Branch;
import com.ssrs.platform.model.entity.Role;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.model.parm.UserParm;
import com.ssrs.platform.model.query.RoleQuery;
import com.ssrs.platform.model.query.UserQuery;
import com.ssrs.platform.point.AfterUserAddPoint;
import com.ssrs.platform.point.AfterUserDeletePoint;
import com.ssrs.platform.point.AfterUserModifyPoint;
import com.ssrs.platform.service.IRoleService;
import com.ssrs.platform.service.IUserRoleService;
import com.ssrs.platform.service.IUserService;
import com.ssrs.platform.util.Page;
import com.ssrs.platform.util.PlatformCache;
import com.ssrs.platform.util.PlatformUtil;
import com.ssrs.platform.util.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author ssrs
 * @since 2020-04-18
 */
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private IRoleService roleService;

    @Priv
    @GetMapping
    public ApiResponses<Page> list(@RequestParam Map<String, Object> params) {
        String branchInnercode = (String) params.get("branchInnercode");
        String realname = (String) params.get("realname");
        String username = (String) params.get("username");
        String status = (String) params.get("status");
        if (StrUtil.isEmpty(branchInnercode)) {
            branchInnercode = Current.getUser().getBranchInnerCode();
        }
        IPage<User> ipage = userService.page(new Query<User>().getPage(params), Wrappers.<User>lambdaQuery()
                .select(User::getId, User::getUserName, User::getRealName, User::getBranchInnercode, User::getLastModifyPassTime, User::getStatus, User::getEmail, User::getMobile)
                .likeRight(StrUtil.isNotEmpty(username), User::getUserName, username)
                .likeRight(StrUtil.isNotEmpty(realname), User::getRealName, realname)
                .eq(StrUtil.isNotEmpty(status), User::getStatus, status)
                .likeRight(StrUtil.isNotEmpty(branchInnercode), User::getBranchInnercode, branchInnercode)
        );
        Page page = new Page(ipage);
        List<User> list = (List<User>) page.getData();
        List<UserQuery> userQueryList = new ArrayList<>(list.size());
        for (User user : list) {
            UserQuery userQuery = BeanUtil.toBean(user, UserQuery.class);
            Branch branch = PlatformCache.getBranch(user.getBranchInnercode());
            if (branch != null) {
                userQuery.setBranchName(branch.getName());
            }
            List<String> roles = PlatformUtil.getRoleCodesByUserName(userQuery.getUserName());
            if (roles != null) {
                JSONArray rolesJa = new JSONArray();
                for (String rolecode : roles) {
                    JSONObject o = new JSONObject();
                    String name = PlatformUtil.getRoleName(rolecode);
                    o.set("roleCode", rolecode);
                    o.set("roleName", name);
                    rolesJa.add(o);
                }
                userQuery.setRoles(rolesJa);
            }
            userQueryList.add(userQuery);
        }
        page.setData(userQueryList);
        return success(page);
    }

    @Priv
    @GetMapping("/role")
    public ApiResponses<List<RoleQuery>> roleList(@RequestParam Map<String, Object> params) {
        String branchInnercode = (String) params.get("branchInnercode");
        LambdaQueryWrapper<Role> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(Role::getRoleCode, Role::getRoleName, Role::getBranchInnercode, Role::getMemo);
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
        lambdaQueryWrapper.orderByAsc(Role::getBranchInnercode, Role::getCreateTime);
        List<Role> roleQueries = roleService.list(lambdaQueryWrapper);
        List<RoleQuery> data = new ArrayList<>();
        if (CollUtil.isEmpty(roleQueries)) {
            return success(data);
        }
        for (Role role : roleQueries) {
            RoleQuery roleQuery = BeanUtil.toBean(role, RoleQuery.class);
            roleQuery.setBranchName(PlatformCache.getBranch(roleQuery.getBranchInnercode()).getName());
            data.add(roleQuery);
        }
        return success(data);
    }

    @Priv
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> create(@RequestBody UserParm userParm) {
        OperateReport operateReport = userService.addUser(userParm);
        if (!operateReport.isSuccess()) {
            return failure(operateReport.getMessage());
        }
        // 用户添加完成后的扩展点
        ExtendManager.invoke(AfterUserAddPoint.ID, (Object[]) operateReport.getData());
        return success("添加成功");
    }

    @Priv
    @PutMapping("/{username}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> update(@PathVariable("username") String username, @RequestBody UserParm userParm) {
        OperateReport operateReport = userService.saveUser(userParm);
        if (!operateReport.isSuccess()) {
            return failure(operateReport.getMessage());
        }
        // 用户修改完成后的扩展点
         ExtendManager.invoke(AfterUserModifyPoint.ID, (Object[]) operateReport.getData());
        return success("保存成功");
    }

    @Priv
    @DeleteMapping("/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> delete(@PathVariable("ids") String ids) {
        OperateReport operateReport = userService.deleteUser(ids);
        if (!operateReport.isSuccess()) {
            return failure(operateReport.getMessage());
        }
        // 用户删除完成后的扩展点
         ExtendManager.invoke(AfterUserDeletePoint.ID, (Object[]) operateReport.getData());
        return success("删除成功");
    }
}
