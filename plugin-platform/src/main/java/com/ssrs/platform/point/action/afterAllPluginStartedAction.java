package com.ssrs.platform.point.action;

import com.ssrs.framework.point.AfterAllPluginStartedPoint;
import com.ssrs.platform.extend.CodeService;

/**
 * 所有插件加载完成后扩展行为
 *
 * @author ssrs
 */
public class afterAllPluginStartedAction extends AfterAllPluginStartedPoint {
    @Override
    public void execute() {
        // 将各插件注册的Code持久化到数据库中
        CodeService.init();
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
