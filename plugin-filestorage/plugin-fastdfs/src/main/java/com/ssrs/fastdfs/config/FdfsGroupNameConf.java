package com.ssrs.fastdfs.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class FdfsGroupNameConf extends FixedConfigItem {

    public static final String ID = "com.ssrs.fastdfs.config.FdfsGroupNameConf";

    public FdfsGroupNameConf() {
        super(ID, DataType.ShortText, ControlType.Text, "FastDFS默认组名");
    }

    public static String getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isEmpty(v)) {
            return "";
        }
        return v;
    }
}
