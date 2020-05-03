package com.ssrs.platform.point;

import com.ssrs.framework.extend.ExtendException;
import com.ssrs.framework.extend.IExtendAction;

/**
 * 角色添加后的扩展点
 */
public abstract class AfterRoleAddPoint implements IExtendAction {
    public static final String ID = "com.ssrs.platform.point.AfterRoleAddPoint";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        execute();
        return null;
    }

    public abstract void execute();
}
