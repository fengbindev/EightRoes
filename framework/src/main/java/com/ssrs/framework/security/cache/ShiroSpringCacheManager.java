package com.ssrs.framework.security.cache;

/**
 * @author ssrs
 */

import com.ssrs.framework.Config;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * <p> 自定义cacheManage 扩张shiro里面的缓存 </p>
 * <description>
 * 引入自己定义的CacheManager
 * </description>
 */
@Component
public class ShiroSpringCacheManager implements CacheManager, Destroyable {

    @Autowired
    @Lazy
    private org.springframework.cache.CacheManager cacheManager;

    public org.springframework.cache.CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public void destroy() throws Exception {
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        if (name == null) {
            return null;
        }
        // 新建一个ShiroSpringCache 将Bean放入并实例化
        return new ShiroSpringCache<K, V>(Config.getAppCode() + "-" + name, getCacheManager());
    }
}