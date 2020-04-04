package com.ssrs.platform.point.action;

import com.ssrs.framework.point.AddUserPermissionsPoint;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.service.IUserService;

import java.util.HashSet;
import java.util.Set;


public class AddUserPermissionsAction extends AddUserPermissionsPoint {
    private static final String ID = "com.ssrs.platform.point.action.AddUserPermissionsAction";

    private IUserService userService = SpringUtil.getBean(IUserService.class);

    @Override
    public Set<String> execute() {
        Set<String> set = new HashSet<>();
       // TODO 获取用户权限
        return set;
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
