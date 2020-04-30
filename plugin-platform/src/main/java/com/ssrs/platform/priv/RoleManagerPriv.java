package com.ssrs.platform.priv;

public class RoleManagerPriv extends AbstractMenuPriv {
    private static final String MenuID = "RoleManagerPriv";
    private static final String Add = MenuID + ".Add";
    private static final String Edit = MenuID + ".Edit";
    private static final String Delete = MenuID + ".Delete";

    public RoleManagerPriv() {
        super(MenuID, "角色管理", null);
        addItem(Add, "添加");
        addItem(Edit, "编辑");
        addItem(Delete, "删除");
    }
}
