package com.ssrs.platform.priv;

/**
 * 代码管理菜单权限项
 *
 * @author ssrs
 */
public class CodeManagerPriv extends AbstractMenuPriv {
    public static final String MenuID = "CodeManagerPriv";
    public static final String Add = MenuID + ".Add";
    public static final String Edit = MenuID + ".Edit";
    public static final String Delete = MenuID + ".Delete";

    public CodeManagerPriv() {
        super(MenuID, "代码管理", null);
        addItem(Add, "添加");
        addItem(Edit, "编辑");
        addItem(Delete, "删除");
    }
}
