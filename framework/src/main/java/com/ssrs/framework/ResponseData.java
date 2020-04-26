package com.ssrs.framework;

import cn.hutool.json.JSONObject;
import com.ssrs.framework.web.IResponseBodyData;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ResponseData extends JSONObject implements IResponseBodyData {
    private static final long serialVersionUID = 1L;
    /**
     * 成功状态
     */
    public static final int SUCCESS = 1;
    /**
     * 失败状态
     */
    public static final int FAILED = 0;
    /**
     * HTTP头，不区分大消息
     */
    private Map<String, String> headers = new HashMap<>();

    protected HttpServletResponse servletResponse;

    private int status;

    private String message;

    public ResponseData() {
        // 构造时默认给予status状态
        initData();
    }

    protected void initData() {
        setStatus(SUCCESS);
        setMessage("");
    }

    /**
     * 获取本次后台方法返回给JavaScript的消息
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置本次后台方法返回给JavaScript的消息，并将状态设为0
     */
    public void setFailedMessage(String message) {
        setStatusAndMessage(FAILED, message);
    }

    @Deprecated
    public void setError(String message) {
        setFailedMessage(message);
    }

    /**
     * 设置本次后台方法返回给JavaScript的消息
     */
    public void setMessage(String message) {
        setSuccessMessage(message);
    }

    /**
     * 将状态置为成功，并设置响应消息
     */
    public void setSuccessMessage(String message) {
        setStatusAndMessage(SUCCESS, message);
    }

    /**
     * 获取本次后台方法返回给JavaScript的状态码，一般情况下0表示执行异常，1表示执行成功
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置本次后台方法返回给JavaScript的状态码
     *
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
        set("status", status);
    }

    /**
     * 设置本次后台方法返回给JavaScript的消息及状态码
     *
     * @param status
     * @param message
     */
    public void setStatusAndMessage(int status, String message) {
        this.status = status;
        set("status", status);
        this.message = message;
        set("message", message);
    }

    @Deprecated
    public void setLogInfo(int status, String message) {
        setStatusAndMessage(status, message);
    }


    /**
     * 获取设置过的HTTP头
     *
     * @return
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 设置HTTP头
     */
    public void setHeader(String name, String value) {
        headers.put(name, value);
        if (servletResponse != null) {
            servletResponse.setHeader(name, value);
        }
    }

    /**
     * 清除数据以便复用
     */
    @Override
    public void clear() {
        headers.clear();
        servletResponse = null;
        super.clear();
        initData();
    }

    protected void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    /**
     * 获取HttpServletResponse对象
     */
    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

}
