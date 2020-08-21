package com.ssrs.elasticsearch.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;
import com.ssrs.platform.code.YesOrNo;

/**
 * @author ssrs
 */
public class SynonymSwitchConfig extends FixedConfigItem {

    public static final String ID = "com.ssrs.elasticsearch.config.SynonymSwitchConfig";

    public SynonymSwitchConfig() {
        super(ID, DataType.ShortText, ControlType.Radio, "是否开启同义词");
        super.addOption(YesOrNo.Yes, "是");
        super.addOption(YesOrNo.No, "否");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isNotEmpty(v)) {
            return v;
        }
        return YesOrNo.No;
    }

}