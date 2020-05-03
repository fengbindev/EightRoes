package com.ssrs.platform.point;

import com.ssrs.framework.extend.ExtendException;
import com.ssrs.framework.extend.IExtendAction;

/**
 * 角色修改后的扩展点
 */
public abstract class AfterRoleModifyPoint implements IExtendAction {
    public static final String ID = "com.ssrs.platform.point.AfterRoleModifyPoint";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        execute();
        return null;
    }

    public abstract void execute();
}