package com.ssrs.framework.security.cache;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.shiro.cache.CacheException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Set;

/**
 * <p> 自定义缓存 </p>
 *
 * @author ssrs
 */
@SuppressWarnings("unchecked")
public class ShiroSpringCache<K, V> implements org.apache.shiro.cache.Cache<K, V> {
    private static final Log log = LogFactory.get();
    private CacheManager cacheManager;
    private Cache cache;

    public ShiroSpringCache(String name, CacheManager cacheManager) {
        if (name == null || cacheManager == null) {
            throw new IllegalArgumentException("cacheManager or CacheName cannot be null.");
        }
        this.cacheManager = cacheManager;
        //这里首先是从父类中获取这个cache,如果没有会创建一个Cache
        this.cache = cacheManager.getCache(name);
    }


    @Override
    public V get(K key) throws CacheException {
        if (key == null) {
            return null;
        }
        Cache.ValueWrapper valueWrapper = cache.get(key);
        if (valueWrapper == null) {
            return null;
        }
        return (V) valueWrapper.get();
    }

    @Override
    public V put(K key, V value) throws CacheException {
        cache.put(key, value);
        return get(key);
    }

    @Override
    public V remove(K key) throws CacheException {
        V v = get(key);
        cache.evict(key);//干掉这个名字为key的缓存
        return v;
    }

    @Override
    public void clear() throws CacheException {
        cache.clear();
    }

    @Override
    public int size() {
        return cacheManager.getCacheNames().size();
    }

    /**
     * 获取缓存中所的key值
     */
    @Override
    public Set<K> keys() {
        return (Set<K>) cacheManager.getCacheNames();
    }

    /**
     * 获取缓存中所有的values值
     */
    @Override
    public Collection<V> values() {
        return (Collection<V>) cache.get(cacheManager.getCacheNames()).get();
    }

    @Override
    public String toString() {
        return "ShiroSpringCache [cache=" + cache + "]";
    }
}