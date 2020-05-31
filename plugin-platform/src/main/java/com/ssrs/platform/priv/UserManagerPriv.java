package com.ssrs.platform.priv;

public class UserManagerPriv extends AbstractMenuPriv {
    public static final String MenuID = "UserManagerPriv";
    public static final String Add = MenuID + ".Add";
    public static final String Edit = MenuID + ".Edit";
    public static final String Delete = MenuID + ".Delete";
    public static final String PrivRange = MenuID + ".PrivRange";
    public static final String Disable = MenuID + ".Disable";
    public static final String Enable = MenuID + ".Enable";
    public static final String ChangePassword = MenuID + ".ChangePassword";

    public UserManagerPriv() {
        super(MenuID, "用户管理", null);
        addItem(Add, "添加");
        addItem(Edit, "编辑");
        addItem(Delete, "删除");
        addItem(PrivRange, "数据权限");
        addItem(Disable, "禁用");
        addItem(Enable, "启用");
        addItem(ChangePassword, "修改密码");
    }
}
