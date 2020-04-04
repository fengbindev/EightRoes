package com.ssrs.framework;

import cn.hutool.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestData extends JSONObject {
    private static final long serialVersionUID = 1L;

    private Map<String, Object> headers;

    protected HttpServletRequest servletRequest;// 非servlet环境此值为null

    private String URL;

    private String queryString;

    private String clientIP;

    private String className;

    private String serverName;

    private String httpMethod;

    private int port;

    private String scheme;

    private String sessionID;

    /**
     * 返回当前请求的域名
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * 设置当前请求的域名，仅框架和测试代码需要调用本方法。
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * 返回当前请求URL中的端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置当前请求的端口，仅框架和测试代码需要调用本方法。
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 返回当前请求中的协议，如http/https
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * 设置当前请求的协议，仅框架和测试代码需要调用本方法。
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * 返回客户端IP地址
     */
    public String getClientIP() {
        return clientIP;
    }

    /**
     * 设置当前请求的客户端IP地址，仅框架和测试代码需要调用本方法。
     */
    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    /**
     * 返回当前请求的URL
     */
    public String getURL() {
        return URL;
    }

    /**
     * 设置当前请求的URL，仅框架和测试代码需要调用本方法。
     */
    public void setURL(String url) {
        URL = url;
    }

    /**
     * 获取http头
     */
    public Map<String, Object> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
            if (servletRequest != null) {
                Enumeration<?> e = servletRequest.getHeaderNames();
                while (e.hasMoreElements()) {
                    String k = e.nextElement().toString();
                    headers.put(k, servletRequest.getHeader(k));
                }
            }
        }
        return headers;
    }

    /**
     * 设置http头，仅框架和测试代码需要调用本方法。
     */
    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    /**
     * 获取当前请求的QueryString
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * 设置当前请求的QueryString，仅框架和测试代码需要调用本方法。
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }


    /**
     * 返回当前HttpSession对应的ID
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * 将当前HttpSession对应的ID赋值给RequestData，仅框架和测试代码需要调用本方法。
     */
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    /**
     * 返回当前的http方法
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * 设置当前的http方法，仅框架和测试代码需要调用本方法。
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    protected void setServletRequest(HttpServletRequest request) {
        this.servletRequest = request;
    }

    /**
     * 获取HttpServletRequest对象
     */
    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    /**
     * 清除数据以便复用
     */
    @Override
    public void clear() {
        headers = null;
        servletRequest = null;
        sessionID = null;
        URL = null;
        queryString = null;
    }
}
