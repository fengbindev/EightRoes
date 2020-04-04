package com.ssrs.framework.web.exception;

/**
* @Description:    未知枚举 异常
* @Author:          ssrs
* @CreateDate:     2019/8/18 16:29
* @UpdateUser:     ssrs
* @UpdateDate:     2019/8/18 16:29
* @Version:        1.0
*/
public class UnknownEnumException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnknownEnumException(String message) {
        super(message);
    }

    public UnknownEnumException(Throwable throwable) {
        super(throwable);
    }

    public UnknownEnumException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
