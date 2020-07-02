package com.ssrs.framework.schedule;


import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 定时任务管理器
 *
 * @author ssrs
 */
public class SystemTaskManager extends AbstractTaskManager {
    public static final String ID = "SYSTEM";
    private static final Log log = LogFactory.get();
    private static boolean startFlag = false;
    private static Lock lock = new ReentrantLock();
    private static SystemTaskManager instance = new SystemTaskManager();

    public static SystemTaskManager getInstance() {
        return instance;
    }

    @Override
    public void startAllTask() {
        if (startFlag) {
            log.warn("The system task service has begun!");
            return;
        }
        lock.lock();
        try {
            List<SystemTask> systemTaskList = SystemTaskService.getInstance().getAll();
            for (SystemTask task : systemTaskList) {
                if (!task.isDisabled()) {
                    CronUtil.schedule(task.getExtendItemID(), task.getCronExpression(), task);
                }
            }
            CronUtil.setMatchSecond(true); // 支持秒级任务（默认分级）
            CronUtil.start();
            startFlag = true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stopAllTask() {
        if (!startFlag) {
            log.warn("The system task service not yet begun!");
            return;
        }
        lock.lock();
        try {
            CronUtil.stop();
        } finally {
            lock.unlock();
        }

    }

    @Override
    public String getTaskCronExpression(String id) {
        SystemTask task = SystemTaskService.getInstance().get(id);
        if (task == null) {
            log.error("not fount this task:{}", id);
            return null;
        }
        return task.getCronExpression();
    }

    public SystemTask getTask(String id) {
        return SystemTaskService.getInstance().get(id);
    }

    public List<SystemTask> getAllTask() {
        return SystemTaskService.getInstance().getAll();
    }

    @Override
    public void execute(String id) {
        Task task = CronUtil.getScheduler().getTask(id);
        if (task == null) {
            log.error("not fount this task:{}", id);
            return;
        }
        task.execute();
    }
}
