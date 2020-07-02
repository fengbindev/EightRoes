package com.ssrs.framework.extend.plugin;

/**
 * 插件接口
 *
 * @author ssrs
 */
public interface IPlugin {
    /**
     * 插件安装
     */
    public void install() throws PluginException;// NO_UCD

    /**
     * 插件启动
     */
    public void start() throws PluginException;

    /**
     * 插件停用
     */
    public void stop() throws PluginException;

    /**
     * 插件卸载
     */
    public void uninstall() throws PluginException;// NO_UCD

    /**
     * 应用停止时调用本方法
     */
    public void destory();

    /**
     * 获得插件配置
     */
    public PluginConfig getConfig();
}
