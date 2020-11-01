package com.ssrs.qiniuyun.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class QiniuSecretKey extends FixedConfigItem {
    public static final String ID = "com.ssrs.qiniuyun.config.QiniuSecretKey";

    public QiniuSecretKey() {
        super(ID, DataType.ShortText, ControlType.Text, "SecretKey");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isEmpty(v)) {
            return "";
        }
        return v;
    }
}