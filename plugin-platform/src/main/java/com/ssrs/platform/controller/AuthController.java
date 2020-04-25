package com.ssrs.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.Current;
import com.ssrs.framework.util.JWTTokenUtils;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.util.ApiAssert;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.framework.web.ErrorCodeEnum;
import com.ssrs.platform.bl.PrivBL;
import com.ssrs.platform.code.YesOrNo;
import com.ssrs.platform.enums.StatusEnum;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.model.parm.AuthUser;
import com.ssrs.platform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {
    //    @Value("${rsa.private_key}")
//    private String privateKey;
    @Autowired
    private IUserService userService;

    @Priv(login = false)
    @PostMapping("/login")
    public ApiResponses<JSONObject> login(@Validated @RequestBody AuthUser authUser) {
        // TODO 校验验证码
        // 密码解密
//        RSA rsa = new RSA(privateKey, null);
//        String password = new String(rsa.decrypt(authUser.getPassword(), KeyType.PrivateKey));
//        authUser.setPassword(password);
        // 验证用户信息
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, authUser.getUsername()), true);
        // 用户不存在
        ApiAssert.notNull(ErrorCodeEnum.USERNAME_OR_PASSWORD_IS_WRONG, user);
        // 用户名密码错误
        ApiAssert.isTrue(ErrorCodeEnum.USERNAME_OR_PASSWORD_IS_WRONG, authUser.getPassword().equals(user.getPassword()));
        // 用户被禁用
        ApiAssert.isTrue(ErrorCodeEnum.USER_IS_DISABLED, !StatusEnum.NORMAL.equals(user.getStatus()));
        // TODO 三级等保功能
        // 设置当选用户信息
        // TODO 后续用户信息需要放到session中，防止请求head参数过大，浪费流量
        com.ssrs.framework.User.UserData userData = new com.ssrs.framework.User.UserData();
        userData.setUserName(user.getUsername());
        userData.setBranchAdministrator(YesOrNo.isYes(user.getBranchAdmin()));
        userData.setBranchInnerCode(user.getBranchInnercode());
        userData.setLogin(true);
        userData.setRealName(user.getRealname());
        userData.setStatus(user.getStatus().toString());
        userData.setPrivilegeModel(PrivBL.getUserPriv(user.getUsername()));
        JSONObject webToken = JWTTokenUtils.createWebToken(userData);
        return success(webToken);

    }

    @Priv(login = false)
    @GetMapping
    public ApiResponses<LocalDate> get() {
        return success(LocalDate.of(1988, 9, 8));
    }
}
