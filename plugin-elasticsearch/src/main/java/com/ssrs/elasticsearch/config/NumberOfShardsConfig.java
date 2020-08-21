package com.ssrs.elasticsearch.config;

import cn.hutool.core.util.NumberUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * 索引分片数量，该配置只在索引不存在时生效。
 * @author ssrs
 */
public class NumberOfShardsConfig extends FixedConfigItem {

    public static final String ID = "com.ssrs.elasticsearch.config.NumberOfShardsConfig";

    public NumberOfShardsConfig() {
        super(ID, DataType.ShortText, ControlType.Text, "索引分片数量");
    }

    public static int getValue() {
        String v = Config.getValue(ID);
        if (!NumberUtil.isInteger(v)) {
            return 5;
        }
        return Integer.parseInt(v);
    }

}