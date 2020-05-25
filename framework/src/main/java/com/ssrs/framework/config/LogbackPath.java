package com.ssrs.framework.config;

import ch.qos.logback.core.PropertyDefinerBase;

public class LogbackPath extends PropertyDefinerBase {
    public LogbackPath() {
    }

    @Override
    public String getPropertyValue() {
        return AppDataPath.getValue();
    }
}
