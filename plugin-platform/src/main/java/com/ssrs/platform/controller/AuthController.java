package com.ssrs.platform.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.Current;
import com.ssrs.framework.security.ShiroAuthorizationHelper;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.util.JWTTokenUtils;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.bl.LoginBL;
import com.ssrs.platform.code.YesOrNo;
import com.ssrs.platform.config.AdminUserName;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.model.parm.AuthUser;
import com.ssrs.platform.service.IUserService;
import com.ssrs.platform.util.ExpiringCacheSet;
import com.ssrs.platform.util.LoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {
    private static Set<String> wrongList = new ExpiringCacheSet<String>("AuthController");
    @Autowired
    private IUserService userService;

    @Priv(login = false)
    @PostMapping("/login")
    public ApiResponses<JSONObject> login(HttpServletRequest request, @Validated AuthUser authUser) {
        // TODO 密码加密
        LoginContext loginContext = new LoginContext();
        loginContext.request = Current.getRequest();
        loginContext.response = Current.getResponse();
        loginContext.userName = authUser.getUserName();
        loginContext.password = authUser.getPassword();
        loginContext.authCode = authUser.getVerifyCode();
        loginContext.wrongList = wrongList;
        LoginBL.validateLoginData(loginContext);
        if (loginContext.status != 1) {
            return failure(loginContext.status, loginContext.message);
        }
        // 验证用户信息
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, authUser.getUserName()));
        // 用户不存在
        if (ObjectUtil.isNull(user)) {
            wrongList.add(loginContext.userName);
            return failure("用户名或密码错误");
        }
        // 是否开启账户安全
        if (LoginBL.isOpenAccountSecurity()) {
            LoginBL.executeAccountSecurity(loginContext, user);
            if (loginContext.status != 1) {
                return failure(loginContext.status, loginContext.message);
            }
        }
        // 判断密码是否正确
        if (!LoginBL.validatePassword(loginContext.password, user.getPassword())) {
            wrongList.add(loginContext.userName);
            return failure("用户名或密码错误");
        }

        if (!AdminUserName.getValue().equalsIgnoreCase(user.getUserName()) && YesOrNo.isNo(user.getStatus())) {
            return failure("该用户处于停用状态，请联系管理员！");
        }
        LoginBL.afterLogin(user, loginContext);
        if (loginContext.status != 1) {
            return failure(loginContext.status, loginContext.message);
        }
        LoginBL.login(user);
        wrongList.remove(user.getUserName());
        // 生成token
        JSONObject webToken = JWTTokenUtils.createWebToken(user.getUserName());
        // 清除权限缓存
        ShiroAuthorizationHelper.clearAuthorizationInfo(user.getUserName());
        return success(webToken);

    }

}
