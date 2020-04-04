package com.ssrs.platform;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.ssrs.framework.Config;
import com.ssrs.framework.extend.IExtendItem;

public class FixedConfigItem implements IExtendItem {
    private String code;
    private String dataType;
    private String controlType;
    private String memo;
    private Dict options;

    public FixedConfigItem(String code, String dataType, String controlType, String memo) {
        this.code = code;
        this.dataType = dataType;
        this.controlType = controlType;
        this.memo = memo;
    }

    public void addOption(String key, String value) {
        if (options == null) {
            options = Dict.create();
        }
        options.put(key, value);
    }


    public String getCode() {
        return code;
    }

    public String getDataType() {
        return dataType;
    }

    public String getControlType() {
        return controlType;
    }

    public Dict getOptions() {
        return options;
    }

    @Override
    public String getExtendItemID() {
        return getCode();
    }

    @Override
    public String getExtendItemName() {
        return getMemo();
    }

    public String getMemo() {
        return memo;
    }

    /**
     * 将一个配置项值中的${Parent}替换为当前应用的父路径，将${Self}替换为当前应用路径
     */
    public static String replacePathHolder(String v) {
        String path = Config.getApplicationRealPath();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String parentPath = FileUtil.normalize(path);
        if (parentPath.endsWith("/target/classes")) {// 判断如果是/target/classe目录说明是本地开发环境启动，默认Parent目录取项目目录
            parentPath = parentPath.substring(0, parentPath.length() - "/target/classes".length());
        } else {
            int endIndex = path.lastIndexOf("/");
            if (endIndex >= 0) {
                parentPath = path.substring(0, endIndex);
            } else {
                StaticLog.warn("Path ${Parent} :" + path);
            }
        }
        v = StrUtil.replace(v, "${Self}", path, true);
        v = StrUtil.replace(v, "%{Self}", path, true);
        v = StrUtil.replace(v, "${Parent}", parentPath, true);
        v = StrUtil.replace(v, "%{Parent}", parentPath, true);
        v = FileUtil.normalize(v);
        return v;
    }

}
