package com.ssrs.framework.cache;

import com.ssrs.framework.extend.IExtendItem;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class CacheDataProvider implements IExtendItem {
    protected Lock lock = new ReentrantLock();
    protected boolean OnNotFound = false; // 表明当前处于onKeyNotFound,OnTypeNotFound调用期间

    /**
     * 当缓存数据项置入时调用此方法。<br>
     * 同一份数据在多个子类型中有键值时可以通过覆盖本方法避免多次载入，以提高性能。
     *
     * @param type  子类型
     * @param key   数据项键
     * @param value 数据项值
     */
    public void onKeySet(String type, String key, Object value) {
    }

    /**
     * 当某个数据项没有找到时调用此方法。<br>
     * 子类通过实现本方法达到缓存加载数据项的效果。
     *
     * @param type 子类型
     * @param key  数据项键
     */
    public abstract void onKeyNotFound(String type, String key);

    /**
     * 销毁缓存数据
     */
    public void destory() {
        FrameworkCacheManager.getCacheManager().getCache(getExtendItemID()).clear();
    }
}
