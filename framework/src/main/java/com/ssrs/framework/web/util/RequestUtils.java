package com.ssrs.framework.web.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.web.HTTPMethod;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Request 请求工具类
 *
 * @author ssrs
 */
@SuppressWarnings("ALL")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestUtils {

    private static final Log log = LogFactory.get();

    /**
     * 判断请求方式GET
     *
     * @param request
     * @return
     */
    public static boolean isGet(HttpServletRequest request) {
        return HTTPMethod.GET.toString().equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求方式POST
     *
     * @param request
     * @return
     */
    public static boolean isPost(HttpServletRequest request) {
        return HTTPMethod.POST.toString().equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求方式PUT
     *
     * @param request
     * @return
     */
    public static boolean isPut(HttpServletRequest request) {
        return HTTPMethod.PUT.toString().equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求方式DELETE
     *
     * @param request
     * @return
     */
    public static boolean isDelete(HttpServletRequest request) {
        return HTTPMethod.DELETE.toString().equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求方式PATCH
     *
     * @param request
     * @return
     */
    public static boolean isPatch(HttpServletRequest request) {
        return HTTPMethod.PATCH.toString().equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求方式TRACE
     *
     * @param request
     * @return
     */
    public static boolean isTrace(HttpServletRequest request) {
        return HTTPMethod.TRACE.toString().equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求方式HEAD
     *
     * @param request
     * @return
     */
    public static boolean isHead(HttpServletRequest request) {
        return HTTPMethod.HEAD.toString().equalsIgnoreCase(request.getMethod());
    }

    /**
     * 判断请求方式OPTIONS
     *
     * @param request
     * @return
     */
    public static boolean isOptions(HttpServletRequest request) {
        return HTTPMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod());
    }

    /**
     * 获取请求
     *
     * @param request
     * @return
     */
    public static String getRequestBody(HttpServletRequest request) {
        String requestBody = null;
        if (isContainBody(request)) {
            try {
                StringWriter writer = new StringWriter();
                IoUtil.copy(request.getReader(), writer);
                requestBody = writer.toString();
            } catch (IOException ignored) {
            }
        }
        return requestBody;
    }

    /**
     * 获取请求
     *
     * @param request
     * @return
     */
    public static byte[] getByteBody(HttpServletRequest request) {
        byte[] body = new byte[0];
        try {
            body = StreamUtils.copyToByteArray(request.getInputStream());
        } catch (IOException e) {
            log.error("Error: Get RequestBody byte[] fail," + e);
        }
        return body;
    }

    /**
     * 是否包含请求体
     *
     * @param request
     * @return
     */
    public static boolean isContainBody(HttpServletRequest request) {
        return isPost(request) || isPut(request) || isPatch(request);
    }

}
