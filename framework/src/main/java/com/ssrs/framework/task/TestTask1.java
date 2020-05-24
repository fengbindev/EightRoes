package com.ssrs.framework.task;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.schedule.SystemTask;

/**
 * @author ssrs
 */
public class TestTask1 extends SystemTask {
    private Log log = LogFactory.get(TestTask1.class);

    @Override
    public void execute() {
        log.info("------------------正在执行task1---------------");
    }

    @Override
    public String getDefaultCronExpression() {
        return "0/10 * * * * ?";
    }

    @Override
    public String getExtendItemID() {
        return "com.ssrs.framework.task.TestTask1";
    }

    @Override
    public String getExtendItemName() {
        return "测试任务1";
    }
}
