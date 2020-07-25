package com.ssrs.platform.extend.item;

import com.ssrs.platform.extend.ILogType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ssrs
 */
public class OperateLog implements ILogType {
    public static final String ID = "OperateLog";

    public static final String ADD = "Add";
    public static final String DELETE = "Delete";
    public static final String EDIT = "Edit";
    public static final String ENABLE = "Enable";
    public static final String DISABLE = "Disable";
    public static final String CHANGEPASSWORD = "ChangePassword";
    public static final String IMPORTDB = "ImportDB";
    public static final String EXPORTDB = "ExportDB";

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
            map.put(ADD, "新建");
            map.put(DELETE, "删除");
            map.put(EDIT, "编辑");
            map.put(ENABLE, "启用");
            map.put(DISABLE, "停用");
            map.put(CHANGEPASSWORD, "修改密码");
            map.put(IMPORTDB, "导入");
            map.put(EXPORTDB, "导出");
        }
        return map;
    }

    @Override
    public void decodeMessage(String msg) {
    }
}
