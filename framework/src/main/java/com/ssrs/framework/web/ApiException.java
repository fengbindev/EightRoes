package com.ssrs.framework.web;


import cn.hutool.core.convert.Convert;

/**
 * 接口异常
 *
 * @author ssrs
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final ErrorCode errorCode;

    public ApiException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.message());
        this.errorCode = errorCodeEnum.convert();
    }

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getError());
        this.errorCode = errorCode;

    }

    public ApiException(int httpCode, String message) {
        super(message);
        ErrorCode errorCode = new ErrorCode(Convert.toStr(httpCode), httpCode, message);
        this.errorCode = errorCode;
    }

    public ApiException(String error, int httpCode, String message) {
        super(message);
        ErrorCode errorCode = new ErrorCode(message, httpCode, message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
