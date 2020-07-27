package com.ssrs.platform.extend.item;

import com.ssrs.platform.extend.ILogType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ssrs
 */
public class OperateLog implements ILogType {
    public static final String ID = "OperateLog";
    private Map<String, String> map;

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public String getExtendItemName() {
        return "用户操作日志";
    }

    @Override
    public Map<String, String> getSubTypes() {
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    @Override
    public String decodeMessage(String msg) {
        return msg;
    }
}
