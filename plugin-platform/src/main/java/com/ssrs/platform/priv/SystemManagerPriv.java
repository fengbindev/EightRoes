package com.ssrs.platform.priv;

/**
 * 系统管理菜单权限项
 *
 * @author ssrs
 */
public class SystemManagerPriv extends AbstractMenuPriv {
    private static final String MenuID = "SystemManagerPriv";

    public SystemManagerPriv() {
        super(MenuID, "系统管理", null);
    }
}
