package com.ssrs.platform.extend.item;

import com.ssrs.platform.extend.ILogType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ssrs
 */
public class OperateLog implements ILogType {
    public static final String ID = "OperateLog";

    public static final String SUBTYPE_ADD = "Add";
    public static final String SUBTYPE_DELETE = "Delete";
    public static final String SUBTYPE_EDIT = "Edit";
    public static final String SUBTYPE_ENABLE = "Enable";
    public static final String SUBTYPE_DISABLE = "Disable";
    public static final String SUBTYPE_CHANGEPASSWORD = "ChangePassword";
    public static final String SUBTYPE_IMPORTDB = "ImportDB";
    public static final String SUBTYPE_EXPORTDB = "ExportDB";

    private Map<String, String> map;

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public String getExtendItemName() {
        return "用户操作日志";
    }

    @Override
    public Map<String, String> getSubTypes() {
        if (map == null) {
            map = new HashMap<>();
            map.put(SUBTYPE_ADD, "新建");
            map.put(SUBTYPE_DELETE, "删除");
            map.put(SUBTYPE_EDIT, "编辑");
            map.put(SUBTYPE_ENABLE, "启用");
            map.put(SUBTYPE_DISABLE, "停用");
            map.put(SUBTYPE_CHANGEPASSWORD, "修改密码");
            map.put(SUBTYPE_IMPORTDB, "导入");
            map.put(SUBTYPE_EXPORTDB, "导出");
        }
        return map;
    }

    @Override
    public void decodeMessage(String msg) {

    }
}
