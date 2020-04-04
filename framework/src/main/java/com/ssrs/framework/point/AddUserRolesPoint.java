package com.ssrs.framework.point;

import com.ssrs.framework.extend.ExtendException;
import com.ssrs.framework.extend.IExtendAction;

import java.util.Set;


public abstract class AddUserRolesPoint implements IExtendAction {
    public static final String ID = "com.ssrs.framework.point.AddUserRolesPoint";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        return execute();
    }

    public abstract Set<String> execute();
}
