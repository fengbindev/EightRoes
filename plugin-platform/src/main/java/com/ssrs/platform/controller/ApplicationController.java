package com.ssrs.platform.controller;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ssrs.framework.Config;
import com.ssrs.framework.User;
import com.ssrs.framework.extend.ExtendManager;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.bl.PrivBL;
import com.ssrs.platform.config.AdminUserName;
import com.ssrs.platform.point.ApplicationPrivsExtendPoint;
import com.ssrs.platform.util.PlatformCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/application")
public class ApplicationController extends BaseController {

    @Priv
    @GetMapping("/privs")
    public ApiResponses<JSONObject> getPrivs() {
        String userName = User.getUserName();
        com.ssrs.platform.model.entity.User user = PlatformCache.getUser(userName);
        JSONObject jo = JSONUtil.parseObj(PrivBL.getUserPriv(userName).toString());
        jo.set("adminUserName", AdminUserName.getValue());
        jo.set("appName", Config.getAppName());
        jo.set("userName", userName);
        jo.set("realName", user.getRealName());
        // 页面获取权限后的扩展点
        ExtendManager.invoke(ApplicationPrivsExtendPoint.ID, new Object[] { jo });
        return success(jo);
    }
}
