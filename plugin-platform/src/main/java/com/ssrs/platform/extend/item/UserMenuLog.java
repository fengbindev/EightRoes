package com.ssrs.platform.extend.item;

import com.ssrs.platform.MenuManager;
import com.ssrs.platform.extend.ILogType;
import com.ssrs.platform.model.Menu;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户菜单操作日志
 *
 * @author ssrs
 */
public class UserMenuLog implements ILogType {
    public static final String ID = "UserMenuLog";
    
    private Map<String, String> map;

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public String getExtendItemName() {
        return "用户菜单操作日志";
    }

    @Override
    public Map<String, String> getSubTypes() {
        if (map == null) {
            map = new HashMap<>();
            Map<String, Menu> menus = MenuManager.getMenus();
            if (menus != null && menus.size() > 0) {
                for (Menu menu : menus.values()) {
                    map.put("Visit " + menu.getId(), menu.getName());
                }
            }
        }
        return map;
    }

    @Override
    public String decodeMessage(String msg) {
        return msg;
    }

}
