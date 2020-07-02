package com.ssrs.framework.extend.plugin;

/**
 * 插件虚拟类，插件类可基础此类
 *
 * @author ssrs
 */
public abstract class AbstractPlugin implements IPlugin {
    private PluginConfig config;

    @Override
    public void install() throws PluginException {
    }

    @Override
    public void uninstall() throws PluginException {
    }

    @Override
    public PluginConfig getConfig() {
        return config;
    }

    public void setConfig(PluginConfig config) {
        this.config = config;
    }

    @Override
    public void destory() {

    }
}
