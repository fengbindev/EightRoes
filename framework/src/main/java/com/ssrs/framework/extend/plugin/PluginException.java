package com.ssrs.framework.extend.plugin;

/**
 * 插件异常类
 *
 * @author ssrs
 */
public class PluginException extends Exception {
    private static final long serialVersionUID = 1L;

    private String message;

    public PluginException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
