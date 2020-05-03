package com.ssrs.platform.priv;

public class UserManagerPriv extends AbstractMenuPriv {
    public static final String MenuID = "UserManagerPriv";
    public static final String Add = MenuID + ".Add";
    public static final String Edit = MenuID + ".Edit";
    public static final String Delete = MenuID + ".Delete";
    public static final String PrivRange = MenuID + ".PrivRange";

    public UserManagerPriv() {
        super(MenuID, "用户管理", null);
        addItem(Add, "添加");
        addItem(Edit, "编辑");
        addItem(Delete, "删除");
        addItem(PrivRange, "数据权限");
    }
}
