package com.ssrs.platform.point;

import com.ssrs.framework.extend.ExtendException;
import com.ssrs.framework.extend.IExtendAction;

/**
 * 用户删除后的扩展点
 *
 * @author ssrs
 */
public abstract class AfterUserDeletePoint implements IExtendAction {
    public static final String ID = "com.ssrs.platform.point.AfterUserDeletePoint";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        execute();
        return null;
    }

    public abstract void execute();
}