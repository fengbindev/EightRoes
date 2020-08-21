package com.ssrs.elasticsearch.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.elasticsearch.code.AnalyzerType;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class AnalyzerConfig extends FixedConfigItem {
    public static final String ID = "com.ssrs.elasticsearch.config.AnalyzerConfig";

    public AnalyzerConfig() {
        super(ID, DataType.ShortText, ControlType.Radio, "分词器");
        for (String type : AnalyzerType.getMap().keySet()) {
            super.addOption(type, AnalyzerType.getMap().get(type));
        }
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isNotEmpty(v)) {
            return v;
        }
        return AnalyzerType.HANLP_INDEX;
    }
}
