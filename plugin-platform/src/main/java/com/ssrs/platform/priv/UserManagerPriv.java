package com.ssrs.platform.priv;

public class UserManagerPriv extends AbstractMenuPriv {
    private static final String MenuID = "UserManagerPriv";
    private static final String Add = MenuID + ".Add";
    private static final String Edit = MenuID + ".Edit";
    private static final String Delete = MenuID + ".Delete";

    public UserManagerPriv() {
        super(MenuID, "用户管理", null);
        addItem(Add, "添加");
        addItem(Edit, "编辑");
        addItem(Delete, "删除");
    }
}
