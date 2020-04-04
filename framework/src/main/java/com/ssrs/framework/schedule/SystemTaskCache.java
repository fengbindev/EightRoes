package com.ssrs.framework.schedule;

import com.ssrs.framework.cache.CacheDataProvider;
import com.ssrs.framework.cache.FrameworkCacheManager;

/**
 * 定时任务缓存，当修改定时任务时存入缓存来实现cron表达式更新
 */
public class SystemTaskCache extends CacheDataProvider {
    private static final String ProviderID = "SystemTaskCache";
    private static final String Type = "SystemTask";

    @Override
    public void onKeyNotFound(String type, String key) {
        SystemTask task = SystemTaskService.getInstance().get(key);
        set(task);
    }

    @Override
    public String getExtendItemID() {
        return ProviderID;
    }

    @Override
    public String getExtendItemName() {
        return "定时任务缓存提供类";
    }

    /**
     * 获取缓存中的定时任务
     *
     * @param taskId 任务id
     * @return
     */
    public static SystemTask get(String taskId) {
        return (SystemTask) FrameworkCacheManager.get(ProviderID, Type, taskId);
    }

    public void set(SystemTask task) {
        if (task == null) {
            return;
        }
        FrameworkCacheManager.set(ProviderID, Type, task.getExtendItemID(), task);
    }

    public void remove(SystemTask task) {
        if (task == null) {
            return;
        }
        FrameworkCacheManager.remove(ProviderID, Type, task.getExtendItemID());
    }

}
