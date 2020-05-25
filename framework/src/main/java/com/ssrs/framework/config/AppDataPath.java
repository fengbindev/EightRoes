package com.ssrs.framework.config;

import com.ssrs.framework.Config;

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
