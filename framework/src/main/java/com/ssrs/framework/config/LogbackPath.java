package com.ssrs.framework.config;

import ch.qos.logback.core.PropertyDefinerBase;

/**
 * 日志存放位置
 *
 * @author ssrs
 */
public class LogbackPath extends PropertyDefinerBase {
    public LogbackPath() {
    }

    @Override
    public String getPropertyValue() {
        return AppDataPath.getValue();
    }
}
