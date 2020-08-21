package com.ssrs.elasticsearch.config;

import cn.hutool.core.util.NumberUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class WordsHitRatioConfig extends FixedConfigItem {

    public static final String ID = "com.ssrs.elasticsearch.config.WordsHitRatioConfig";

    public WordsHitRatioConfig() {
        super(ID, DataType.ShortText, ControlType.Text, "分词命中比率(1-100)");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (!NumberUtil.isInteger(v)) {
            return 100 + "%";
        }
        return v + "%";
    }

}
