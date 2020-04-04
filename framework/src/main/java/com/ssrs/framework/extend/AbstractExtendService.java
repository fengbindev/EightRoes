package com.ssrs.framework.extend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扩展服务虚拟类，扩展服务可继承此类
 */
public class AbstractExtendService<T extends IExtendItem> implements IExtendService<T> {
    protected Map<String, T> itemMap = new HashMap<>();

    protected List<T> itemList = new ArrayList<T>(new ArrayList<T>());

    /**
     * 查找扩展服务的实例
     */
    protected static <S extends IExtendService<?>> S findInstance(Class<S> clazz) {
        if (clazz == null) {
            throw new ExtendException("ExtendService class can't be empty!");
        }
        ExtendServiceConfig config = ExtendManager.getInstance().findExtendServiceByClass(clazz.getName());
        if (config == null) {
            throw new ExtendException("ExtendService not found,class is " + clazz.getName());
        }
        S service = (S) config.getInstance();
        return service;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(IExtendItem item) {
        itemMap.put(item.getExtendItemID(), (T) item);
        prepareItemList();
    }

    @Override
    public T get(String id) {
        if (id == null) {
            return null;
        }
        return itemMap.get(id);
    }

    @Override
    public T remove(String id) {
        T ret = itemMap.remove(id);
        prepareItemList();
        return ret;
    }

    protected void prepareItemList() {
        itemList = new ArrayList<>(itemMap.values());
    }

    /**
     * 注意：有可能返回null
     */
    @Override
    public List<T> getAll() {
        return itemList;
    }

    public int size() {
        return itemList.size();
    }

    @Override
    public void destory() {
        itemMap.clear();
        itemList = new ArrayList<T>(new ArrayList<T>());
    }
}
