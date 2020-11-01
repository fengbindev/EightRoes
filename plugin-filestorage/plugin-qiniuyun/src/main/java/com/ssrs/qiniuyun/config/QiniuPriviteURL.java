package com.ssrs.qiniuyun.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class QiniuPriviteURL extends FixedConfigItem {
    public static final String ID = "com.ssrs.qiniuyun.config.QiniuPriviteURL";

    public QiniuPriviteURL() {
        super(ID, DataType.ShortText, ControlType.Text, "私有访问地址");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isEmpty(v)) {
            return "";
        }
        return v;
    }
}
