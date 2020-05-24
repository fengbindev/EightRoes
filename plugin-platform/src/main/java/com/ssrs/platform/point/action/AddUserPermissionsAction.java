package com.ssrs.platform.point.action;

import com.ssrs.framework.PrivilegeModel;
import com.ssrs.framework.User;
import com.ssrs.framework.point.AddUserPermissionsPoint;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.bl.PrivBL;
import com.ssrs.platform.service.IUserService;

import java.util.Set;


public class AddUserPermissionsAction extends AddUserPermissionsPoint {
    private static final String ID = "com.ssrs.platform.point.action.AddUserPermissionsAction";

    private IUserService userService = SpringUtil.getBean(IUserService.class);

    @Override
    public Set<String> execute() {
        PrivilegeModel privilege = PrivBL.getUserPriv(User.getUserName());
        Set<String> menuPrivSet = privilege.getMenuPrivSet();
        return menuPrivSet;
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
