package com.ssrs.framework.cache;

/**
 * 配置缓存提供者
 *
 * @author ssrs
 */
public class ConfigCacheProvider extends CacheDataProvider {
    public static final String ProviderID = "config";

    @Override
    public void onKeyNotFound(String type, String key) {
    }

    @Override
    public String getExtendItemID() {
        return ProviderID;
    }

    @Override
    public String getExtendItemName() {
        return "配置项缓存提供者";
    }
}
