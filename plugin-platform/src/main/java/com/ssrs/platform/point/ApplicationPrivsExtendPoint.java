package com.ssrs.platform.point;

import com.ssrs.framework.extend.ExtendException;
import com.ssrs.framework.extend.IExtendAction;

/**
 * 获取当前登录用户权限后的扩展点
 */
public abstract class ApplicationPrivsExtendPoint implements IExtendAction {
    public static final String ID = "com.ssrs.platform.point.ApplicationPrivsExtendPoint";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        execute();
        return null;
    }

    public abstract void execute();
}
