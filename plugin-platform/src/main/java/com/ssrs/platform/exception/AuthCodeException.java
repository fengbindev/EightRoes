package com.ssrs.platform.exception;

/**
 * 验证码错误异常
 *
 * @author ssrs
 */
public class AuthCodeException extends RuntimeException {

    public AuthCodeException(String msg){
        super(msg);
    }

}
