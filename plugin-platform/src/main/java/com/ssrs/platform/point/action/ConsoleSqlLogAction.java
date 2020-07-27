package com.ssrs.platform.point.action;

import com.ssrs.framework.point.ConsoleSqlLogPoint;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.service.IOperateLogService;

/**
 * @author ssrs
 * Date: Created in 2020/7/27 20:33
 * Description: 打印sql日志扩展行为
 */
public class ConsoleSqlLogAction extends ConsoleSqlLogPoint {
    private static final String ID = "com.ssrs.platform.point.action.ConsoleSqlLogAction";

    private IOperateLogService operateLogService = SpringUtil.getBean(IOperateLogService.class);


    @Override
    public void execute(Integer connectionId, String now, Long elapsed, String prepared, String sql, String url, String msg) {
        System.out.println("打印sql日志" + msg);
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
