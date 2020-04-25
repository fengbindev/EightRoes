package com.ssrs.platform.priv;

import cn.hutool.core.lang.Dict;
import com.ssrs.framework.extend.IExtendItem;

public abstract class AbstractMenuPriv implements IExtendItem {
    private String menuId;
    private String name;
    private String memo;
    private Dict privItems = Dict.create();


    public AbstractMenuPriv(String menuId, String name, String memo) {
        this.memo = memo;
        this.menuId = menuId;
        this.name = name;
    }

    @Override
    public String getExtendItemID() {
        return menuId;
    }

    public void addItem(String itemID, String name) {
        privItems.put(itemID, name);
    }

    public Dict getPrivItems() {
        return privItems;
    }

    @Override
    public String getExtendItemName() {
//        return MenuManager.getMenu(menuId).getName();
        return name;
    }

    public String getMemo() {
        return memo;
    }

    public String getMenuId() {
        return menuId;
    }

    public String getName() {
        return name;
    }
}
