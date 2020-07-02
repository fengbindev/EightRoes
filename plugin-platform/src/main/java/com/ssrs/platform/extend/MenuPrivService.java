package com.ssrs.platform.extend;

import com.ssrs.framework.extend.AbstractExtendService;
import com.ssrs.platform.MenuManager;
import com.ssrs.platform.model.Menu;
import com.ssrs.platform.priv.AbstractMenuPriv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 权限项扩展服务
 *
 * @author ssrs
 */
public class MenuPrivService extends AbstractExtendService<AbstractMenuPriv> {

    public static MenuPrivService getInstance() {
        return findInstance(MenuPrivService.class);
    }

    public static List<Menu> getAllMenus() {
        Map<String, Menu> menus = MenuManager.getMenus();
        List<Menu> menuList = new ArrayList<>();
        menus.forEach((menuId, menu) -> {
            menuList.add(menu);
        });
        return menuList;
    }
}
