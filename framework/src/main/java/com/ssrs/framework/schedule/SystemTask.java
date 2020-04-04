package com.ssrs.framework.schedule;


import cn.hutool.cron.task.Task;
import com.ssrs.framework.extend.IExtendItem;

public abstract class SystemTask implements IExtendItem, Task {
    private String cronExpression;
    private boolean disabled;

    /**
     * 执行任务
     */
    @Override
    public abstract void execute();

    public abstract String  getCronExpression();

    public  void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
