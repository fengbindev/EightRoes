package com.ssrs.platform;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import com.ssrs.framework.extend.plugin.PluginManager;
import com.ssrs.platform.extend.MenuPrivService;
import com.ssrs.platform.model.Menu;
import com.ssrs.platform.priv.AbstractMenuPriv;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class MenuManager {
    private static Map<String, Menu> menus;
    private static ReentrantLock lock = new ReentrantLock();

    static Map<String, Menu> load() {
        menus = MapUtil.newHashMap();
        PluginManager.getInstance().init();
        List<AbstractMenuPriv> abstractMenuPrivs = MenuPrivService.getInstance().getAll();
        for (AbstractMenuPriv menuPriv : abstractMenuPrivs) {
            Menu menu = new Menu();
            menu.setId(menuPriv.getMenuId());
            menu.setParentId("0");
            menu.setName(menuPriv.getName());
            menu.setMemo(menuPriv.getMemo());
            Dict privItems = menuPriv.getPrivItems();
            privItems.forEach((key, value) -> {
                Menu children = new Menu();
                children.setId(key);
                children.setParentId(menu.getId());
                children.setName(Convert.toStr(value));
                menu.setChildren(children);
            });
            menus.put(menuPriv.getMenuId(), menu);
        }
        return menus;
    }

    public static Map<String, Menu> getMenus() {
        if (menus == null) {
            lock.lock();
            try {
                if (menus == null) {
                    menus = load();
                }
            } finally {
                lock.unlock();
            }
        }
        return menus;
    }

    public static Menu getMenu(String id) {
        return menus.get(id);
    }

}