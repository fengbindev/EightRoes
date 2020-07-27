package com.ssrs.platform.code;

import com.ssrs.platform.FixedCodeType;

/**
 * @author ssrs
 * Date: Created in 2020/7/27 19:44
 * Description:
 */
public class OperateLogType extends FixedCodeType {
    public static final String CODETYPE = "OperateLogType";

    public static final String ADD = "Add";
    public static final String DELETE = "Delete";
    public static final String EDIT = "Edit";
    public static final String ENABLE = "Enable";
    public static final String DISABLE = "Disable";
    public static final String CHANGEPASSWORD = "ChangePassword";
    public static final String IMPORTDB = "ImportDB";
    public static final String EXPORTDB = "ExportDB";

    public OperateLogType() {
        super(CODETYPE, "日志操作类型", true, false);
        addFixedItem(ADD, "新建", null);
        addFixedItem(DELETE, "删除", null);
        addFixedItem(EDIT, "编辑", null);
        addFixedItem(ENABLE, "启用", null);
        addFixedItem(DISABLE, "停用", null);
        addFixedItem(CHANGEPASSWORD, "修改密码", null);
        addFixedItem(IMPORTDB, "导入", null);
        addFixedItem(EXPORTDB, "导出", null);
    }
}
