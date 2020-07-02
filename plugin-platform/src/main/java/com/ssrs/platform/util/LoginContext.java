package com.ssrs.platform.util;

import com.ssrs.framework.RequestData;
import com.ssrs.framework.ResponseData;

import java.util.Set;

/**
 * 登陆上下文
 *
 * @author ssrs
 */
public class LoginContext {
    public RequestData request;
    public ResponseData response;
    public String userName;
    public String password;
    public String authCode;
    public int status;
    public String message;
    public Set<String> wrongList;
}