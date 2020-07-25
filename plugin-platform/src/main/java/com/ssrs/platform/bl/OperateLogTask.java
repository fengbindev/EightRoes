package com.ssrs.platform.bl;

import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.model.entity.OperateLog;
import com.ssrs.platform.service.IOperateLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ssrs
 * Date: Created in 2020/7/25 17:19
 * Description:
 */
@Slf4j
@AllArgsConstructor
public class OperateLogTask implements Runnable {

    private OperateLog operateLog;


    @Override
    public void run() {
        IOperateLogService operateLogService = SpringUtil.getBean(IOperateLogService.class);
        boolean save = operateLogService.save(operateLog);
        if (!save) {
            log.error("---------系统操作日志保存失败！----------");
        }

    }
}
