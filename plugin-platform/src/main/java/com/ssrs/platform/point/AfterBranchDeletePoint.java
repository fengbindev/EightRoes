package com.ssrs.platform.point;

import com.ssrs.framework.extend.ExtendException;
import com.ssrs.framework.extend.IExtendAction;

/**
 * 机构删除后的扩展点
 */
public abstract class AfterBranchDeletePoint implements IExtendAction {
    public static final String ID = "com.ssrs.platform.point.AfterBranchDeletePoint";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        execute();
        return null;
    }

    public abstract void execute();
}