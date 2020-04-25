package com.ssrs.platform.priv;

public class BranchManagerPriv extends AbstractMenuPriv{
    private static final String MenuID = "BranchManagerPriv";
    private static final String Add = MenuID + ".Add";
    private static final String Edit = MenuID + ".Edit";
    private static final String Delete = MenuID + ".Delete";
    private static final String PrivRange = MenuID + ".PrivRange";

    public BranchManagerPriv() {
        super(MenuID, "机构管理", null);
        addItem(Add, "添加");
        addItem(Edit, "编辑");
        addItem(Delete, "删除");
        addItem(PrivRange, "权限范围");
    }
}
