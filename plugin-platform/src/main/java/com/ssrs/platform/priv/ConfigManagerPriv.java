package com.ssrs.platform.priv;

/**
 * 配置管理菜单权限项
 *
 * @author ssrs
 */
public class ConfigManagerPriv extends AbstractMenuPriv {
    public static final String MenuID = "ConfigManagerPriv";
    public static final String Save = MenuID + ".Save";

    public ConfigManagerPriv() {
        super(MenuID, "配置管理", null);
        addItem(Save, "保存");
    }
}
