package com.ssrs.framework.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableCaching
@Component
public class FrameworkCacheManager {
    private static final Log log = LogFactory.get();
    // 缓存前缀，保证多个系统连接同一个缓存不会缓存key冲突
    private static final String cachePrefix = Config.getAppCode() + "_";
    public static CacheManager cacheManager;

    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        FrameworkCacheManager.cacheManager = cacheManager;
    }

    public static CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * 获取指定类型的的CacheProvider
     */
    public static CacheDataProvider getCache(String providerID) {
        return CacheService.getInstance().get(providerID);
    }

    private static void onKeyNotFound(CacheDataProvider cp, String type, String key) {
        boolean notFoundFlag = cp.OnNotFound;
        cp.OnNotFound = true;
        try {
            cp.onKeyNotFound(StrUtil.removePrefix(type, cachePrefix), StrUtil.removePrefix(key, cachePrefix));
        } finally {
            cp.OnNotFound = notFoundFlag;
        }
    }

    public static Object get(String providerID, String type, long key) {
        return get(providerID, type, key, true);
    }

    public static Object get(String providerID, String type, String key) {
        return get(providerID, type, key, true);
    }

    public static Object get(String providerID, String type, Object key, boolean warnOrThrow) {
        type = cachePrefix + type;
        key = cachePrefix + key;
        CacheDataProvider cp = getCache(providerID);
        if (cp == null) {
            if (warnOrThrow) {
                throw new RuntimeException("CacheProvider not found:" + providerID);
            }
            return null;
        }
        Cache cache = cacheManager.getCache(type);
        String sKey = String.valueOf(key);
        Cache.ValueWrapper valueWrapper = cache.get(sKey);
        if (valueWrapper == null) {
            cp.lock.lock();
            try {
                valueWrapper = cache.get(sKey);
                if (valueWrapper == null) {
                    onKeyNotFound(cp, type, sKey);
                }
                if (warnOrThrow) {
                    log.warn("Get cache data failed: Provider=" + providerID + ",Type=" + type + ",Key=" + sKey);
                }
                valueWrapper = cache.get(sKey);
                if (valueWrapper == null){
                    return null;
                }
                return  valueWrapper.get();
            } finally {
                cp.lock.unlock();
            }
        }
        return valueWrapper.get();
    }

    /**
     * 是否存在指定键值
     */
    public static boolean contains(String providerID, String type, Object key) {
        type = cachePrefix + type;
        key = cachePrefix + key;
        CacheDataProvider cp = getCache(providerID);
        if (cp == null) {
            return false;
        }
        Cache cache = cacheManager.getCache(type);
        if (cache == null) {
            return false;
        }
        String strKey = String.valueOf(key);
        Object value = cache.get(strKey).get();
        if (value == null) {
            return false;
        }
        return true;
    }

    public static void set(String providerID, String type, long key, Object value) {
        set(providerID, type, String.valueOf(key), value);
    }

    public static void set(String providerID, String type, String key, Object value) {
        type = cachePrefix + type;
        key = cachePrefix + key;
        CacheDataProvider cp = getCache(providerID);
        if (cp == null) {
            log.warn("未找到CacheProvider:" + providerID);
            return;
        }
        cp.lock.lock();
        try {
            cacheManager.getCache(type).put(key, value);
            putTypeKeys(providerID, type, key);
            cp.onKeySet(type, key, value);
        } finally {
            cp.lock.unlock();
        }
    }


    /**
     * 删除缓存数据
     *
     * @param providerID
     * @param type
     * @param key
     * @return 返回删除的数据, 如果没有数据返回null
     */
    public static Object remove(String providerID, String type, long key) {
        return remove(providerID, type, String.valueOf(key));
    }

    /**
     * 删除缓存数据
     *
     * @param providerID
     * @param type
     * @param key
     * @return
     */
    public static Object remove(String providerID, String type, String key) {
        type = cachePrefix + type;
        key = cachePrefix + key;
        CacheDataProvider cp = getCache(providerID);
        if (cp == null) {
            log.warn("CacheProvider not found:" + providerID);
            return null;
        }
        try {
            cp.lock.lock();
            Cache.ValueWrapper valueWrapper = cacheManager.getCache(type).get(key);
            if (ObjectUtil.isEmpty(valueWrapper)) {
                return null;
            }
            Object removeData = valueWrapper.get();
            cacheManager.getCache(type).evict(key);
            removeTypeKeys(providerID, type, key);
            return removeData;
        } finally {
            cp.lock.unlock();
        }
    }

    /**
     * 删除缓存类型，也可以通过本方法来更新整个类型的缓存
     *
     * @param providerID
     * @param type
     */
    public static void removeType(String providerID, String type) {
        type = cachePrefix + type;
        CacheDataProvider cp = getCache(providerID);
        if (cp == null) {
            log.warn("CacheProvider not found:" + providerID);
            return;
        }
        cp.lock.lock();
        try {
            cacheManager.getCache(type).clear();
        } finally {
            cp.lock.unlock();
        }
    }

    /**
     * 获取缓存类型中所有包含的keys（CacheManager没有获取缓存中所有key的方法）
     *
     * @param providerID
     * @param type
     * @return
     */
    public static List<String> getTypeKeys(String providerID, String type) {
        String keys = providerID + "_" + type + "_keys";
        List<String> keyList = cacheManager.getCache(type).get(keys, List.class);
        if (CollUtil.isEmpty(keyList)) {
            keyList = CollUtil.newArrayList();
        }
        return keyList;
    }

    /**
     * 添加缓存类型所包含的key
     *
     * @param providerID
     * @param type
     * @param key
     */
    public static void putTypeKeys(String providerID, String type, String key) {
        String keys = providerID + "_" + type + "_keys";
        List<String> keyList = cacheManager.getCache(type).get(keys, List.class);
        if (CollUtil.isEmpty(keyList)) {
            keyList = CollUtil.newArrayList();
        }
        keyList.add(key);
        cacheManager.getCache(type).put(keys, keyList);
    }

    /**
     * 删除缓存类型所包含的key
     *
     * @param providerID
     * @param type
     * @param key
     */
    public static void removeTypeKeys(String providerID, String type, String key) {
        String keys = providerID + "_" + type + "_keys";
        List<String> keyList = cacheManager.getCache(type).get(keys, List.class);
        if (CollUtil.isNotEmpty(keyList)) {
            keyList.remove(key);
        }
        cacheManager.getCache(type).put(keys, keyList);
    }


}
