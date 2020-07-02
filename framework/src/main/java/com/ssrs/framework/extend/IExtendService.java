package com.ssrs.framework.extend;

import java.util.List;

/**
 * 扩展服接口
 *
 * @author ssrs
 */
public interface IExtendService<T extends IExtendItem> {
    void register(IExtendItem item);

    T get(String id);

    T remove(String id);

    List<T> getAll();

    void destory();
}
