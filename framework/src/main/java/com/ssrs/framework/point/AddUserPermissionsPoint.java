package com.ssrs.framework.point;

import com.ssrs.framework.extend.ExtendException;
import com.ssrs.framework.extend.IExtendAction;

import java.util.Set;

/**
 * 添加用户权限项扩展点
 *
 * @author ssrs
 */
public abstract class AddUserPermissionsPoint implements IExtendAction {
    public static final String ID = "com.ssrs.framework.point.AddUserPermissionsPoint";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        return execute();
    }

    public abstract Set<String> execute();
}
