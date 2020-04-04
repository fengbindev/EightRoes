package com.ssrs.framework.web;


/**
* @Description:    接口异常
* @Author:          ssrs
* @CreateDate:     2019/8/18 16:37
* @UpdateUser:     ssrs
* @UpdateDate:     2019/8/18 16:37
* @Version:        1.0
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

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
