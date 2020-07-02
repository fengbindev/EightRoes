package com.ssrs.platform.priv;

/**
 * 机构管理菜单权限项
 *
 * @author ssrs
 */
public class BranchManagerPriv extends AbstractMenuPriv {
    public static final String MenuID = "BranchManagerPriv";
    public static final String Add = MenuID + ".Add";
    public static final String Edit = MenuID + ".Edit";
    public static final String Delete = MenuID + ".Delete";
    public static final String PrivRange = MenuID + ".PrivRange";

    public BranchManagerPriv() {
        super(MenuID, "机构管理", null);
        addItem(Add, "添加");
        addItem(Edit, "编辑");
        addItem(Delete, "删除");
        addItem(PrivRange, "权限范围");
    }
}
