package com.ssrs.elasticsearch.config;


import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * 用于给ES提供同义词或词典远程更新的地址，本地开发可以不配置。
 * 建议给“ES客户端根URL”配置单独的内网地址。
 *
 * @author ssrs
 */
public class EsClientHostConfig extends FixedConfigItem {

    public static final String ID = "com.ssrs.elasticsearch.config.EsClientHostConfig";

    public EsClientHostConfig() {
        super(ID, DataType.ShortText, ControlType.Text, "ES客户端根URL");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isNotEmpty(v)) {
            return v;
        }
        return null;
    }

}
