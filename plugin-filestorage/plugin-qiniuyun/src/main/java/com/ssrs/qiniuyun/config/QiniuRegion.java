package com.ssrs.qiniuyun.config;

import cn.hutool.core.util.StrUtil;
import com.qiniu.common.Zone;
import com.ssrs.framework.Config;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.code.ControlType;
import com.ssrs.platform.code.DataType;

/**
 * @author ssrs
 */
public class QiniuRegion extends FixedConfigItem {
    public static final String ID = "com.ssrs.qiniuyun.config.QiniuRegion";

    public QiniuRegion() {
        super(ID, DataType.ShortText, ControlType.Radio, "区域");
        super.addOption("z0", "华东");
        super.addOption("z1", "华北");
        super.addOption("z2", "华南");
        super.addOption("na0", "北美");
        super.addOption("as0", "东南亚");
    }

    public static Zone getValue() {
        String v = Config.getValue(ID);
        if (StrUtil.isEmpty(v)) {
            return Zone.autoZone();
        }
        if ("z0".equals(v)) {
            return Zone.zone0();
        }
        if ("z1".equals(v)) {
            return Zone.zone1();
        }
        if ("z2".equals(v)) {
            return Zone.zone2();
        }
        if ("na0".equals(v)) {
            return Zone.zone0();
        }
        if ("as0".equals(v)) {
            return Zone.zone0();
        }

        return Zone.autoZone();
    }
}