package com.ssrs.framework.web.exception;

/**
 * 正则表达式解析异常
 *
 * @author ssrs
 */
public class RRException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RRException(String message) {
        super(message);
    }

    public RRException(Throwable throwable) {
        super(throwable);
    }

    public RRException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
