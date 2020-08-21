package com.ssrs.elasticsearch.config;

import cn.hutool.core.util.NumberUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class MaxResultWindowConfig extends FixedConfigItem {

    public static final String ID = "com.ssrs.elasticsearch.config.MaxResultWindowConfig";

    public MaxResultWindowConfig() {
        super(ID, DataType.ShortText, ControlType.Text, "最大搜索结果数量");
    }

    public static int getValue() {
        String v = Config.getValue(ID);
        if (!NumberUtil.isInteger(v)) {
            return 1000000;
        }
        return Integer.parseInt(v);
    }
}
