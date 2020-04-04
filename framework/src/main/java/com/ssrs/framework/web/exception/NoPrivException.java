package com.ssrs.framework.web.exception;

/**
* @Description:     权限注解不存在
* @Author:          ssrs
* @CreateDate:     2019/8/18 16:29
* @UpdateUser:     ssrs
* @UpdateDate:     2019/8/18 16:29
* @Version:        1.0
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
