package com.ssrs.platform.point.action;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.point.ConsoleSqlLogPoint;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.bl.LogBL;
import com.ssrs.platform.code.OperateLogType;
import com.ssrs.platform.extend.item.SqlLog;
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
        if (!StrUtil.contains(prepared, "sys_operate_log")) {
            LogBL.addSqlLog(SqlLog.ID, OperateLogType.EXECUTESQL, msg, elapsed + "ms", prepared);
        }
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
