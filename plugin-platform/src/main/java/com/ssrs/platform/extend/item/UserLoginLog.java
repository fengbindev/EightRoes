package com.ssrs.platform.extend.item;

import com.ssrs.platform.MenuManager;
import com.ssrs.platform.extend.ILogType;
import com.ssrs.platform.model.Menu;

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
            map.put("User Login", "用户登陆");
        }
        return map;
    }

    @Override
    public void decodeMessage(String msg) {

    }

}
