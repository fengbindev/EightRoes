package com.ssrs.aliyunoss.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class OssAccessKeyId extends FixedConfigItem {

    public static final String ID = "com.ssrs.aliyunoss.config.OssAccessKeyId";

    public OssAccessKeyId() {
        super(ID, DataType.ShortText, ControlType.Text, "AccessKey");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isEmpty(v)) {
            return "";
        }
        return v;
    }
}
