package com.ssrs.framework;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigLoader {
    private static final Log log = LogFactory.get();
    private static boolean loaded = false;
    private static Map<String, String> configSetting = new HashMap<>(16);
    private static Setting setting = null;
    private static Lock lock = new ReentrantLock();

    public static void load() {
        try {
            lock.lock();
            if (loaded) {
                return;
            }
            TimeInterval timer = DateUtil.timer();
            setting = new Setting("framework.setting");
            setting.entrySet("App").forEach((item) -> configSetting.put("App." + item.getKey(), item.getValue()));
            long interval = timer.interval();
            loaded = true;
            log.info("Load framework.setting used {} ms", interval);
        } catch (Exception e) {
            log.error(e, "framework.setting failed to load ");
        } finally {
            lock.unlock();
        }
    }

    public static Map<String, String> getAppConfig() {
        load();
        return configSetting;
    }

    public static void reload() {
        if (!loaded) {
            return;
        }
        if (setting == null) {
            return;
        }
        loaded = false;
        setting.clear("App").load();
    }

}

