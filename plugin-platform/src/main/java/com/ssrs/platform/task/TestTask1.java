package com.ssrs.platform.task;

import com.ssrs.framework.schedule.SystemTask;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ssrs
 */
@Slf4j
public class TestTask1 extends SystemTask {

    @Override
    public void execute() {
        log.info("---------------测试定时任务1---------------");
    }

    @Override
    public String getDefaultCronExpression() {
        return "0/10 * * * * ?";
    }

    @Override
    public String getExtendItemID() {
        return "com.ssrs.platform.task.TestTask1";
    }

    @Override
    public String getExtendItemName() {
        return "测试定时任务1";
    }
}
