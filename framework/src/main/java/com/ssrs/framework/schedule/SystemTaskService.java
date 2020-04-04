package com.ssrs.framework.schedule;

import com.ssrs.framework.extend.AbstractExtendService;

/**
 *系统定时任务扩展服务类
 */
public class SystemTaskService extends AbstractExtendService<SystemTask> {

    public static SystemTaskService getInstance() {
        return findInstance(SystemTaskService.class);
    }
}
