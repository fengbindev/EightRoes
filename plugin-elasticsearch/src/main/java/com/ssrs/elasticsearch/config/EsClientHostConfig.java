package com.ssrs.elasticsearch.config;

/**
 * @author ssrs
 */

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;
import freemarker.template.utility.StringUtil;

/**
 * 用于给ES提供同义词或词典远程更新的地址，本地开发可以不配置， 默认使用站点动态应用地址。
 * 生产环境有内网地址和外网地址，一般站点动态影用地址使用外网地址。
 * 建议给“ES客户端根URL”配置单独的内网地址。
 * @author steven
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
