package com.ssrs.platform.extend;

import com.ssrs.framework.extend.IExtendItem;

import java.util.Map;

/**
 * 日志类型接口
 *
 * @author ssrs
 */
public interface ILogType extends IExtendItem {
    /**
     * 根据字类型ID返回字类型名称
     */
    public Map<String, String> getSubTypes();

    /**
     * 将Message解析为可读的信息
     */
    public void decodeMessage(String msg);
}
