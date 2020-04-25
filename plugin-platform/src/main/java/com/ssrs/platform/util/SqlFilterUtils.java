package com.ssrs.platform.util;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.web.exception.RRException;


/**
 * @Description:
 * @Author: ssrs
 * @CreateDate: 2019/9/22 11:28
 * @UpdateUser: ssrs
 * @UpdateDate: 2019/9/22 11:28
 * @Version: 1.0
 */
public class SqlFilterUtils {
    /**
     * SQL注入过滤
     *
     * @param str 待验证的字符串
     */
    public static String sqlInject(String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        //去掉'|"|;|\字符
        str = StrUtil.replace(str, "'", "");
        str = StrUtil.replace(str, "\"", "");
        str = StrUtil.replace(str, ";", "");
        str = StrUtil.replace(str, "\\", "");

        //转换成小写
        str = str.toLowerCase();

        //非法字符
        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop"};

        //判断是否包含非法字符
        for (String keyword : keywords) {
            if (str.indexOf(keyword) != -1) {
                throw new RRException("包含非法字符");
            }
        }

        return str;
    }
}
