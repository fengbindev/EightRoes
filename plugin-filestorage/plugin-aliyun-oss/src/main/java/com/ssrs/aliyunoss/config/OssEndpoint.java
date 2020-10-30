package com.ssrs.aliyunoss.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class OssEndpoint extends FixedConfigItem {

    public static final String ID = "com.ssrs.aliyunoss.config.OssEndpoint";

    public OssEndpoint() {
        super(ID, DataType.ShortText, ControlType.Text, "Endpoint");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isEmpty(v)) {
            return "";
        }
        return v;
    }
}
