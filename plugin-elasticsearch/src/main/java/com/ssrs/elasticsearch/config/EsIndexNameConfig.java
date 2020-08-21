package com.ssrs.elasticsearch.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class EsIndexNameConfig extends FixedConfigItem {

    public static final String ID = "com.ssrs.elasticsearch.config.EsIndexNameConfig";

    public EsIndexNameConfig() {
        super(ID, DataType.ShortText, ControlType.Text, "ES索引名称");

    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isNotEmpty(v)) {
            return v;
        }
        return null;
    }
}
