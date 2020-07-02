package com.ssrs.framework.config;

import com.ssrs.framework.Config;

/**
 * 日志/索引/缓存/文件等存放路径
 *
 * @author ssrs
 */
public class AppDataPath {
    public AppDataPath() {
    }

    public static String getValue() {
        return Config.getAppDataPath();
    }

    public static void setValue(String val) {
        Config.setAppDataPath(val);
    }
}
