package com.ssrs.platform.point;

import com.ssrs.framework.extend.ExtendException;
import com.ssrs.framework.extend.IExtendAction;

/**
 * 机构修改后的扩展点
 */
public abstract class AfterBranchModifyPoint implements IExtendAction {
    public static final String ID = "com.ssrs.platform.point.AfterBranchModifyPoint";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        execute();
        return null;
    }

    public abstract void execute();
}
