package com.ssrs.framework.web;

import com.ssrs.framework.ResponseData;
import com.ssrs.framework.web.util.ResponseUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 返回值封装类
 *
 * @author ssrs
 */
public class ApiResponses<T> implements Serializable, IResponseBodyData {

    private static final long serialVersionUID = 1L;

    /**
     * 不需要返回结果
     */
    public static ApiResponses<Void> empty() {
        return SuccessResponses.<Void>builder().status(ResponseData.SUCCESS).build();

    }

    /**
     * 成功返回
     *
     * @param object
     */
    public static <T> ApiResponses<T> success(T object) {
        return SuccessResponses.<T>builder().status(ResponseData.SUCCESS).data(object).build();

    }

    /**
     * 成功返回
     *
     * @param message
     * @param object
     */
    public static <T> ApiResponses<T> success(String message, T object) {
        return SuccessResponses.<T>builder().status(ResponseData.SUCCESS).message(message).data(object).build();

    }

    /**
     * 失败返回
     *
     * @param errorCode
     * @param exception
     */
    public static <T> ApiResponses<T> failure(ErrorCode errorCode, Exception exception) {
        return ResponseUtils.exceptionMsg(FaileResponses.builder().message(errorCode.getMessage()), exception)
                .error(errorCode.getError())
                .time(LocalDateTime.now())
                .status(errorCode.getHttpCode())
                .build();
    }

}
