
package com.ssrs.framework.web;


import com.ssrs.framework.web.exception.UnknownEnumException;

import javax.servlet.http.HttpServletResponse;

/**
* @Description:    错误信息枚举
* @Author:          ssrs
* @CreateDate:     2019/8/18 16:37
* @UpdateUser:     ssrs
* @UpdateDate:     2019/8/18 16:37
* @Version:        1.0
*/
public enum ErrorCodeEnum {

    /**
     * 400
     */
    BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST,  "请求参数错误或不完整"),
    /**
     * JSON格式错误
     */
    JSON_FORMAT_ERROR(HttpServletResponse.SC_BAD_REQUEST,  "JSON格式错误"),
    /**
     * 401
     */
    UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED,  "请先进行认证"),
    /**
     * 403
     */
    FORBIDDEN(HttpServletResponse.SC_FORBIDDEN,  "无权查看"),
    /**
     * 404
     */
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND,  "未找到该路径"),
    /**
     * 405
     */
    METHOD_NOT_ALLOWED(HttpServletResponse.SC_METHOD_NOT_ALLOWED,  "请求方式不支持"),
    /**
     * 406
     */
    NOT_ACCEPTABLE(HttpServletResponse.SC_NOT_ACCEPTABLE,  "不可接受该请求"),
    /**
     * 411
     */
    LENGTH_REQUIRED(HttpServletResponse.SC_LENGTH_REQUIRED,  "长度受限制"),
    /**
     * 415
     */
    UNSUPPORTED_MEDIA_TYPE(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,  "不支持的媒体类型"),
    /**
     * 416
     */
    REQUESTED_RANGE_NOT_SATISFIABLE(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE,  "不能满足请求的范围"),
    /**
     * 500
     */
    INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,  "服务器正在升级，请耐心等待"),
    /**
     * 503
     */
    SERVICE_UNAVAILABLE(HttpServletResponse.SC_SERVICE_UNAVAILABLE,  "请求超时"),
    /**
     * 演示系统，无法该操作
     */
    DEMO_SYSTEM_CANNOT_DO(HttpServletResponse.SC_SERVICE_UNAVAILABLE,  "演示系统，无法该操作");

    private final int httpCode;
    private final String message;

    ErrorCodeEnum(int httpCode, String message) {
        this.httpCode = httpCode;
        this.message = message;
    }

    /**
     * 转换为ErrorCode(自定义返回消息)
     *
     * @param msg
     * @return
     */
    public ErrorCode convert(String msg) {
        return ErrorCode.builder().httpCode(httpCode()).error(name()).message(msg).build();
    }

    /**
     * 转换为ErrorCode
     *
     * @return
     */
    public ErrorCode convert() {
        return ErrorCode.builder().httpCode(httpCode()).error(name()).message(message()).build();
    }

    public static ErrorCodeEnum getErrorCode(String errorCode) {
        ErrorCodeEnum[] enums = ErrorCodeEnum.values();
        for (ErrorCodeEnum errorCodeEnum : enums) {
            if (errorCodeEnum.name().equalsIgnoreCase(errorCode)) {
                return errorCodeEnum;
            }
        }
        throw new UnknownEnumException("Error: Unknown errorCode, or do not support changing errorCode!\n");
    }

    public int httpCode() {
        return this.httpCode;
    }

    public String message() {
        return this.message;
    }

}
