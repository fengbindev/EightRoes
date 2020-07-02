package com.ssrs.framework.web.exception;

/**
 * 权限注解不存在异常
 *
 * @author ssrs
 */
public class NoPrivException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoPrivException(String message) {
        super(message);
    }

    public NoPrivException(Throwable throwable) {
        super(throwable);
    }

    public NoPrivException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
