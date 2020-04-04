package com.ssrs.framework;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class Current {
    /**
     * 各线程数据分离
     */
    private static ThreadLocal<CurrentData> current = new ThreadLocal<CurrentData>();


    /**
     * 清除当前数据
     */
    public static void clear() {
        if (current.get() != null) {
            current.get().clear();
        }
    }

    /**
     * 设置线程上下文有效的变量
     */
    public static void put(String key, Object value) {
        CurrentData data = current.get();
        if (data == null) {
            data = new CurrentData();
            data.values = new HashMap<>();
            current.set(data);
        } else if (data.values == null) {
            data.values = new HashMap<>();
        }
        if (value instanceof Map) {
            Map<?, ?> vmap = (Map<?, ?>) value;
            for (Object k : vmap.keySet()) {
                data.values.put(key + "." + k, vmap.get(k));
            }
        }
        data.values.put(key, value);
    }

    /**
     * 获得线程上下文有效的变量
     */
    public static Object get(String key) {
        CurrentData data = current.get();
        if (data == null) {
            return null;
        }
        return data.values.get(key);
    }

    /**
     * 获得线程上下文有效的所有变量
     */
    public static Map<String, Object> getValues() {// NO_UCD
        CurrentData data = current.get();
        if (data == null) {
            return null;
        }
        return data.values;
    }

    /**
     * 准备Current数据
     */
    public static void prepare(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        CurrentData cd = current.get();
        if (cd == null) {
            cd = new CurrentData();
            current.set(cd);
        }
        if (servletRequest != null) {
            RequestData request = Current.getRequest();
            request.setServletRequest(servletRequest);
            Current.getResponse().setServletResponse(servletResponse);
            request.setQueryString(servletRequest.getQueryString());
            request.setHttpMethod(servletRequest.getMethod());
            request.putAll(ServletUtil.getParamMap(servletRequest));
            if (StrUtil.isEmpty(request.getURL())) {
                if (StrUtil.isEmpty(servletRequest.getQueryString())) {
                    request.setURL(servletRequest.getRequestURL().toString());
                } else {
                    request.setURL(servletRequest.getRequestURL().append("?").append(servletRequest.getQueryString()).toString());
                }
            }
            request.setClientIP(ServletUtil.getClientIP(servletRequest));
            request.setServerName(servletRequest.getServerName());
            request.setPort(servletRequest.getServerPort());
            request.setScheme(servletRequest.getScheme());
            HttpSession session = servletRequest.getSession(false);
            if (session != null) {
                request.setSessionID(session.getId());
            }
        }
    }

    /**
     * 获取当前请求中的RequestImpl对象
     *
     * @return
     */
    public static RequestData getRequest() {
        CurrentData data = current.get();
        if (data == null) {
            return null;
        }
        return data.request;
    }

    /**
     * 获取当前请求中的ResponseImpl对象
     *
     * @return
     */
    public static ResponseData getResponse() {
        CurrentData data = current.get();
        if (data == null) {
            return null;
        }
        return data.response;
    }


    /**
     * 设置用户
     */
    public static void setUser(User.UserData ud) {
        CurrentData data = current.get();
        if (data == null) {
            data = new CurrentData();
            current.set(data);
        }
        data.userData = ud;
    }

    /**
     * 获取用户
     */
    public static User.UserData getUser() {
        CurrentData data = current.get();
        if (data == null) {
            return null;
        }
        return data.userData;
    }


    /**
     * 获取当前数据
     *
     * @return
     */
    public static CurrentData getCurrentData() {
        return current.get();
    }

    public static class CurrentData {
        public User.UserData userData;
        public RequestData request = new RequestData();
        public ResponseData response = new ResponseData();
        public Map<String, Object> values = new HashMap<>();

        /**
         * 清空数据
         */
        public void clear() {
            userData = null;
            if (values != null) {
                values.clear();
            }
            if (request != null) {
                request.clear();
            }
            if (response != null) {
                response.clear();
            }
        }
    }
}
