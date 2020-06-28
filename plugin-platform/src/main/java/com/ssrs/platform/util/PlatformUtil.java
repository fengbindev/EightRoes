package com.ssrs.platform.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.task.Task;
import com.ssrs.framework.Config;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.schedule.SystemTask;
import com.ssrs.framework.schedule.SystemTaskCache;
import com.ssrs.framework.schedule.SystemTaskManager;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.code.YesOrNo;
import com.ssrs.platform.config.AdminUserName;
import com.ssrs.platform.extend.item.CodeCacheProvider;
import com.ssrs.platform.model.entity.Code;
import com.ssrs.platform.model.entity.Role;
import com.ssrs.platform.model.entity.Schedule;
import com.ssrs.platform.service.IConfigService;
import com.ssrs.platform.service.IScheduleService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlatformUtil {
    private static final Object mutex = new Object();


    /**
     * 载入数据库配置中的配置项
     */
    public static void loadDBConfig() {
        synchronized (mutex) {
            try {
                IConfigService configService = SpringUtil.getBean(IConfigService.class);
                configService.list().forEach(config -> {
                    Config.setValue(config.getCode(), config.getValue());
                });
                Config.setValue("AdminUserName", AdminUserName.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将代码项转为map
     *
     * @param codeType /
     * @return /
     */
    public static Dict getCodeMap(String codeType) {
        Dict dict = Dict.create();
        List<String> codeCacheKey = FrameworkCacheManager.getTypeKeys(CodeCacheProvider.ProviderID, codeType);
        codeCacheKey.forEach(key -> {
            Code code = (Code) FrameworkCacheManager.get(CodeCacheProvider.ProviderID, codeType, key);
            dict.put(code.getCodeValue(), code.getCodeName());
        });
        return dict;
    }

    public static List<String> getRoleCodesByUserName(String userName) {
        String roles = (String) FrameworkCacheManager.get(PlatformCache.ProviderID, PlatformCache.Type_UserRole, userName);
        if (roles == null) {
            return null;
        }
        String[] arr = roles.split(",");
        Set<String> set = new HashSet<String>();
        for (String seg : arr) {
            if (StrUtil.isNotEmpty(seg)) {
                set.add(seg);
            }
        }
        ArrayList<String> list = new ArrayList<String>();
        if (set.size() > 0) {
            list.addAll(set);
            return list;
        }
        return null;
    }

    public static String getRoleName(String roleCode) {
        Role role = (Role) FrameworkCacheManager.get(PlatformCache.ProviderID, PlatformCache.Type_Role, roleCode);
        if (role == null) {
            return null;
        }
        return role.getRoleName();
    }

    /**
     * 加载数据库定时任务
     */
    public static void loadDBSchedule() {
        SystemTaskManager.getInstance().startAllTask();
        IScheduleService scheduleService = SpringUtil.getBean(IScheduleService.class);
        List<Schedule> scheduleList = scheduleService.list();
        if (CollUtil.isNotEmpty(scheduleList)) {
            for (Schedule schedule : scheduleList) {
                SystemTask systemTask = SystemTaskCache.get(schedule.getSourceId());
                if (ObjectUtil.isNull(systemTask)) {
                    continue;
                }
                systemTask.setDisabled(YesOrNo.isNo(schedule.getIsUsing()));
                systemTask.setCronExpression(schedule.getCronExpression());
                if (YesOrNo.isYes(schedule.getIsUsing())) {
                    Task task = CronUtil.getScheduler().getTaskTable().getTask(schedule.getSourceId());
                    if (ObjectUtil.isEmpty(task)) {
                        CronUtil.schedule(schedule.getSourceId(), schedule.getCronExpression(), systemTask);
                    } else {
                        CronUtil.updatePattern(systemTask.getExtendItemID(), new CronPattern(schedule.getCronExpression()));
                    }
                } else {
                    CronUtil.remove(systemTask.getExtendItemID());
                }
                SystemTaskCache.set(systemTask);
            }
        }


    }
}
