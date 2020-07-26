package com.ssrs.platform.extend.item;

import cn.hutool.core.util.StrUtil;
import com.ssrs.platform.extend.ILogType;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户登陆日志
 *
 * @author ssrs
 */
public class UserLoginLog implements ILogType {
    public static final String ID = "UserLoginLog";

    public static final String SUBTYPE_LOGIN = "login"; // 登陆

    private Map<String, String> map;
    public static final String LOGIN = "User Login";

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public String getExtendItemName() {
        return "用户登陆日志";
    }

    @Override
    public Map<String, String> getSubTypes() {
        if (map == null) {
            map = new HashMap<>();
            map.put(LOGIN, "用户登陆");
        }
        return map;
    }

    @Override
    public String decodeMessage(String msg) {
        if (StrUtil.equals(LOGIN, msg)) {
            return "用户登陆";
        }
        return msg;
    }

}
