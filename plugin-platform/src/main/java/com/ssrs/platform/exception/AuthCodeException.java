package com.ssrs.platform.exception;

/**
 * 验证码错误异常
 */
public class AuthCodeException extends RuntimeException {

    public AuthCodeException(String msg){
        super(msg);
    }

}
