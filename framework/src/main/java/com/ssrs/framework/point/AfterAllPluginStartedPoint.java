package com.ssrs.framework.point;

import com.ssrs.framework.extend.ExtendException;
import com.ssrs.framework.extend.IExtendAction;

/**
 * 所有插件加载完成后扩展点
 *
 * @author ssrs
 */
public abstract class AfterAllPluginStartedPoint implements IExtendAction {
    public static final String ID = "com.ssrs.framework.point.AfterAllPluginStartedPoint";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        execute();
        return null;
    }

    public abstract void execute();
}
