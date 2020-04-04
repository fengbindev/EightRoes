package com.ssrs.framework.extend;


public class ExtendException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;

    public ExtendException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
