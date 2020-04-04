package com.ssrs.framework.schedule;


public abstract class AbstractTaskManager {

    /**
     * 启动所有定时任务
     */
    public  abstract void startAllTask();

    /**
     * 停止所有定时任务
     */
    public abstract void stopAllTask();

    /**
     * 返回某个任务的Cron表达式
     */
    public abstract String getTaskCronExpression(String id);

    /**
     * 执行指定id的任务
     */
    public abstract void execute(String id);
}
