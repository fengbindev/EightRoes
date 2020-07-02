package com.ssrs.platform.priv;

/**
 * 角色管理菜单权限项
 *
 * @author ssrs
 */
public class RoleManagerPriv extends AbstractMenuPriv {
    public static final String MenuID = "RoleManagerPriv";
    public static final String Add = MenuID + ".Add";
    public static final String Edit = MenuID + ".Edit";
    public static final String Delete = MenuID + ".Delete";
    public static final String PrivRange = MenuID + ".PrivRange";

    public RoleManagerPriv() {
        super(MenuID, "角色管理", null);
        addItem(Add, "添加");
        addItem(Edit, "编辑");
        addItem(Delete, "删除");
        addItem(PrivRange, "数据权限");
    }
}
