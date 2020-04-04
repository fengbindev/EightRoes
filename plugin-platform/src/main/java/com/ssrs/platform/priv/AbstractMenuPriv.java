package com.ssrs.platform.priv;

import cn.hutool.core.lang.Dict;
import com.ssrs.framework.extend.IExtendItem;

public abstract class AbstractMenuPriv implements IExtendItem {
    private String menuId;
    private Dict privItems = Dict.create();
    private String memo;

    public AbstractMenuPriv(String menuId, String memo) {
        this.memo = memo;
        this.menuId = menuId;
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
        return null;
    }

    public String getMemo() {
        return memo;
    }
}
