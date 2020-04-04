package com.ssrs.framework.web.exception;

/**
 * @Description: 正则表达式解析异常
 * @Author: ssrs
 * @CreateDate: 2019/9/22 11:31
 * @UpdateUser: ssrs
 * @UpdateDate: 2019/9/22 11:31
 * @Version: 1.0
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
