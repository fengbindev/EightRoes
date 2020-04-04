package com.ssrs.platform.extend;

import com.ssrs.framework.extend.AbstractExtendService;
import com.ssrs.platform.FixedConfigItem;

public class ConfigService extends AbstractExtendService<FixedConfigItem> {

    public static ConfigService getInstance() {
        return findInstance(ConfigService.class);
    }
}
