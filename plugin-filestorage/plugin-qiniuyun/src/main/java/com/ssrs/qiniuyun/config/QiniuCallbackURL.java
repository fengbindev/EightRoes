package com.ssrs.qiniuyun.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class QiniuCallbackURL extends FixedConfigItem {
    public static final String ID = "com.ssrs.qiniuyun.config.QiniuCallbackURL";

    public QiniuCallbackURL() {
        super(ID, DataType.ShortText, ControlType.Text, "七牛回调地址");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isEmpty(v)) {
            return "";
        }
        return v;
    }
}