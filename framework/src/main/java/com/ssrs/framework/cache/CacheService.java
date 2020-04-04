package com.ssrs.framework.cache;

import com.ssrs.framework.extend.AbstractExtendService;

public class CacheService extends AbstractExtendService<CacheDataProvider> {
    private static CacheService instance = null;

    public static CacheService getInstance() {
        if (instance == null) {
            instance = findInstance(CacheService.class);
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }
}
