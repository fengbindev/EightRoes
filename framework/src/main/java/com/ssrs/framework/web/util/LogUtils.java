package com.ssrs.framework.web.util;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.Config;

import java.util.Map;
import java.util.Optional;

/**
 * 请求日志工具类
 *
 */
public abstract class LogUtils {
    private static final Log log = LogFactory.get();
    /**
     * 获取日志对象
     *
     * @param beiginTime
     * @param parameterMap
     * @param requestBody
     * @param url
     * @param mapping
     * @param method
     * @param ip
     * @param object
     * @return
     */
    public static void printLog(Long beiginTime, Map<String, String[]> parameterMap, String requestBody, String url, String mapping, String method, String ip, Object object) {
        boolean printfFlag = Boolean.valueOf(Config.getValue("App.printfRequestLog"));
        if (!printfFlag){
            return;
        }
        com.ssrs.framework.web.Log  logInfo = com.ssrs.framework.web.Log .builder()
                //查询参数
                .parameterMap(parameterMap)
                //请求体
                .requestBody(Optional.ofNullable(JSONUtil.toJsonStr(requestBody)).orElse(requestBody))
                //请求路径
                .url(url)
                //请求mapping
                .mapping(mapping)
                //请求方法
                .method(method)
                .runTime((beiginTime != null ? System.currentTimeMillis() - beiginTime : 0) + "ms")
                .result(object)
                .ip(ip)
                .build();
        log.info(JacksonUtils.toJson(logInfo));
    }

    public static void doAfterReturning(Object ret) {
        ResponseUtils.writeValAsJson(ApplicationUtils.getRequest(), ret);
    }

}
