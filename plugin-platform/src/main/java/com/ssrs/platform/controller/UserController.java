package com.ssrs.platform.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.Config;
import com.ssrs.framework.Current;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.core.OperateReport;
import com.ssrs.framework.extend.ExtendManager;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.bl.LoginBL;
import com.ssrs.platform.bl.PrivBL;
import com.ssrs.platform.code.YesOrNo;
import com.ssrs.platform.config.AdminUserName;
import com.ssrs.platform.model.entity.Branch;
import com.ssrs.platform.model.entity.Role;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.model.parm.UserParm;
import com.ssrs.platform.model.query.RoleQuery;
import com.ssrs.platform.model.query.UserQuery;
import com.ssrs.platform.point.AfterUserAddPoint;
import com.ssrs.platform.point.AfterUserDeletePoint;
import com.ssrs.platform.point.AfterUserModifyPoint;
import com.ssrs.platform.priv.UserManagerPriv;
import com.ssrs.platform.service.IRoleService;
import com.ssrs.platform.service.IUserRoleService;
import com.ssrs.platform.service.IUserService;
import com.ssrs.platform.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

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

    @Priv(UserManagerPriv.Add)
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> create(UserParm userParm) {
        try {
            userParm.setPassword(RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, userParm.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OperateReport operateReport = userService.addUser(userParm);
        if (!operateReport.isSuccess()) {
            return failure(operateReport.getMessage());
        }
        // 用户添加完成后的扩展点
        ExtendManager.invoke(AfterUserAddPoint.ID, new Object[]{operateReport.getData()});
        return success("添加成功");
    }

    @Priv(UserManagerPriv.Edit)
    @PutMapping("/{username}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> update(@PathVariable("username") String username, UserParm userParm) {
        OperateReport operateReport = userService.saveUser(userParm);
        if (!operateReport.isSuccess()) {
            return failure(operateReport.getMessage());
        }
        // 用户修改完成后的扩展点
        ExtendManager.invoke(AfterUserModifyPoint.ID, new Object[]{operateReport.getData()});
        return success("保存成功");
    }

    @Priv(UserManagerPriv.Delete)
    @DeleteMapping("/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> delete(@PathVariable("ids") String ids) {
        OperateReport operateReport = userService.deleteUser(ids);
        if (!operateReport.isSuccess()) {
            return failure(operateReport.getMessage());
        }
        // 用户删除完成后的扩展点
        ExtendManager.invoke(AfterUserDeletePoint.ID, new Object[]{operateReport.getData()});
        return success("删除成功");
    }

    /**
     * 修改密码初始化验证
     *
     * @return
     */
    @Priv(login = false)
    @GetMapping("/initpwdcheck")
    public ApiResponses<Map<String, Object>> initPwdCheck() {
        Map<String, Object> map = new HashMap<>();
        String minLen = Config.getValue("passwordMinLength");
        String maxLen = Config.getValue("passwordMaxLength");
        String isOpenThreeSecurity = Config.getValue("isOpenThreeSecurity");
        if (StrUtil.isEmpty(isOpenThreeSecurity) || YesOrNo.No.equalsIgnoreCase(isOpenThreeSecurity) || StrUtil.isEmpty(minLen)
                || StrUtil.isEmpty(maxLen)) {
            map.put("minLen", 6);
            map.put("maxLen", 30);
        } else {
            map.put("minLen", minLen);
            map.put("maxLen", maxLen);
        }
        return success(map);
    }

    /**
     * 登录页修改密码
     * @return
     */
    @Priv(login = false)
    @PutMapping("/changeloginpassword")
    public ApiResponses<String> changeLoginPassword() {
        OperateReport operateReport = changePassword(true);
        if (operateReport.isSuccess()) {
            return success("修改成功");
        } else {
            return failure(operateReport.getMessage());
        }

    }

    /**
     * 用户管理页修改用户密码
     * @return
     */
    @Priv(UserManagerPriv.ChangePassword)
    @PutMapping("/password")
    public ApiResponses<String> modifyPassword() {
        OperateReport operateReport = changePassword(false);
        if (operateReport.isSuccess()) {
            return success("修改成功");
        } else {
            return failure(operateReport.getMessage());
        }
    }

    private OperateReport changePassword(boolean isLogin) {
        OperateReport operateReport = new OperateReport(true);
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, Current.getRequest().getStr("userName")));
        if (user == null) {
            operateReport.setSuccess(false, "用户不存在");
            return operateReport;
        }
        PrivBL.assertBranch(user.getBranchInnercode());
        String password = Current.getRequest().getStr("password");
        String oldPassword = Current.getRequest().getStr("oldPassword");
        try {
            if (StrUtil.isNotEmpty(password)) {
                password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, password);
            }
            if (StrUtil.isNotEmpty(oldPassword)) {
                oldPassword = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, oldPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Current.getRequest().remove("password");
        Current.getRequest().remove("oldPassword");

        if (isLogin && !PasswordUtil.verify(oldPassword, user.getPassword())) {
            operateReport.setSuccess(false, "原密码不正确");
            return operateReport;
        }

        String msg = userService.beforeUpdatePassword(password, user);
        if (StrUtil.isNotEmpty(msg)) {
            operateReport.setSuccess(false, msg);
            return operateReport;
        }
        user.setPassword(PasswordUtil.generate(password));
        if (StrUtil.isNotEmpty(com.ssrs.framework.User.getUserName()) && !ObjectUtil.equal(com.ssrs.framework.User.getUserName(), user.getUserName())) {
            if (YesOrNo.isYes(Config.getValue("nextLoginUpdatePwd")) && YesOrNo.isYes(Config.getValue("isOpenThreeSecurity"))) {
                user.setModifyPassStatus(YesOrNo.Yes);
            } else {
                user.setModifyPassStatus(YesOrNo.No);
            }
        } else {
            user.setModifyPassStatus(YesOrNo.No);
        }
        user.setLoginErrorCount(0);
        // 添加本次修改密码的时间
        user.setLastModifyPassTime(LocalDateTime.now());
        userService.updateById(user);
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_User, user.getUserName(), user);
        if (isLogin) {
            LoginBL.login(user);
        }
        return operateReport;
    }

    @Priv(UserManagerPriv.Disable)
    @PutMapping("/disable/{id}")
    public ApiResponses<String> disable(@PathVariable String id) {
        Current.getRequest().set("userNames", id);
        OperateReport operateReport = setUserStatus(false);
        if (operateReport.isSuccess()){
            return success(operateReport.getMessage());
        } else {
            return failure(operateReport.getMessage());
        }
    }

    @Priv(UserManagerPriv.Enable)
    @PutMapping("/enable/{id}")
    public ApiResponses<String> enable(@PathVariable String id) {
        Current.getRequest().set("userNames", id);
        OperateReport operateReport = setUserStatus(true);
        if (operateReport.isSuccess()){
            return success(operateReport.getMessage());
        } else {
            return failure(operateReport.getMessage());
        }
    }

    /**
     * 设置用户启用禁用状态
     *
     * @param status
     */
    private OperateReport setUserStatus(boolean status) {
        OperateReport operateReport = new OperateReport(true);
        String statusStr = status ? YesOrNo.Yes : YesOrNo.No;
        String userNames =  Current.getRequest().getStr("userNames");
        List<User> userList = userService.list(Wrappers.<User>lambdaQuery().in(User::getUserName, userNames.split(",")));
        for (User user : userList) {
            // 判断用户如果是自己则不能设置启用停用
            if (user.getUserName().equalsIgnoreCase(Current.getUser().getUserName())) {
                operateReport.setSuccess(false, "不能操作自己");
                return operateReport;
            }
            PrivBL.assertBranch(user.getBranchInnercode());
            if (AdminUserName.getValue().equalsIgnoreCase(user.getUserName())) {
                operateReport.setSuccess(false, "不能禁用管理员");
                return operateReport;
            }
            user.setStatus(statusStr);
            FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_User, user.getUserName(), user);
        }
        boolean result = userService.updateBatchById(userList);
        if (result) {
            operateReport.setSuccess(true, "操作成功");
            return operateReport;
        } else {
            operateReport.setSuccess(false, "操作失败");
            return operateReport;
        }
    }
}
