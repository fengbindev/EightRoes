package com.ssrs.platform.util;

import cn.hutool.core.lang.Dict;
import com.ssrs.framework.Config;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.config.AdminUserName;
import com.ssrs.platform.extend.item.CodeCacheProvider;
import com.ssrs.platform.model.entity.Code;
import com.ssrs.platform.service.IConfigService;

import java.util.List;

public class PlatformUtil {
    private static final Object mutex = new Object();


    /**
     * 载入数据库配置中的配置项
     */
    public static void loadDBConfig() {
        synchronized (mutex) {
            try {
                IConfigService configService = SpringUtil.getBean(IConfigService.class);
                configService.list().forEach(config -> {
                    Config.getMap().put(config.getCode(), config.getValue());
                });
                Config.getMap().put("AdminUserName", AdminUserName.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将代码项转为map
     *
     * @param codeType /
     * @return /
     */
    public static Dict getCodeMap(String codeType) {
        Dict dict = Dict.create();
        List<String> codeCacheKey = FrameworkCacheManager.getTypeKeys(CodeCacheProvider.ProviderID, codeType);
        codeCacheKey.forEach(key -> {
            Code code = (Code) FrameworkCacheManager.get(CodeCacheProvider.ProviderID, codeType, key);
            dict.put(code.getCodeValue(), code.getCodeName());
        });
        return dict;
    }
}
