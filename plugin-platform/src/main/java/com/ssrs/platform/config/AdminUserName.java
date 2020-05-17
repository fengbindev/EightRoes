package com.ssrs.platform.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * 系统管理员的用户名
 */
public class AdminUserName extends FixedConfigItem {
    public static final String ID = "Platform.AdminUserName";

    public AdminUserName() {
        super(ID, DataType.ShortText, ControlType.Text, "系统管理员的用户名");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isEmpty(v)) {
            v = "admin";
        }
        return v;
    }

}
