package com.ssrs.platform.extend;

import com.ssrs.framework.extend.AbstractExtendService;
import com.ssrs.platform.FixedConfigItem;

/**
 * 配置项扩展服务
 *
 * @author ssrs
 */
public class ConfigService extends AbstractExtendService<FixedConfigItem> {

    public static ConfigService getInstance() {
        return findInstance(ConfigService.class);
    }
}
