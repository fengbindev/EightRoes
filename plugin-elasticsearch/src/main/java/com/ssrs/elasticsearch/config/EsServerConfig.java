package com.ssrs.elasticsearch.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * ElasticSearch服务端地址， 可以配置多个，以分号分割
 * 如： 10.1.40.112:9200;10.1.40.113:9200
 * @author ssrs
 */
public class EsServerConfig extends FixedConfigItem {

    public static final String ID = "com.ssrs.elasticsearch.config.EsServerConfig";

    public EsServerConfig() {
        super(ID, DataType.ShortText, ControlType.Text, "ElasticSearch服务端地址");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isNotEmpty(v)) {
            return v;
        }
        return null;
    }

}
