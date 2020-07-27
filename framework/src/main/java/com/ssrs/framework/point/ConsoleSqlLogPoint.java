package com.ssrs.framework.point;

import cn.hutool.core.convert.Convert;
import com.ssrs.framework.extend.ExtendException;
import com.ssrs.framework.extend.IExtendAction;

/**
 * @author ssrs
 * Date: Created in 2020/7/27 20:07
 * Description: sql日志打印扩展点
 */
public abstract class ConsoleSqlLogPoint implements IExtendAction {
    public static final String ID = "com.ssrs.framework.point.ConsoleSqlLogPoint";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        execute(Convert.toInt(args[0]), Convert.toStr(args[1]), Convert.toLong(args[2]), Convert.toStr(args[3]), Convert.toStr(args[4]), Convert.toStr((args[5])), Convert.toStr(args[6]));
        return null;
    }

    public abstract void execute(Integer connectionId, String now, Long elapsed, String prepared, String sql, String url, String msg);
}
