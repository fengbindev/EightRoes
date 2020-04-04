package com.ssrs.framework.web;

import cn.hutool.core.util.ObjectUtil;
import com.ssrs.framework.web.util.HtmlFilterUtils;
import com.ssrs.framework.web.util.RequestUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;


/**
 * @Description: Request包装类
 * <p>
 * 1.预防xss攻击
 * 2.拓展requestbody无限获取(HttpServletRequestWrapper只能获取一次)
 * </p>
 * @Author: ssrs
 * @CreateDate: 2019/9/21 22:17
 * @UpdateUser: ssrs
 * @UpdateDate: 2019/9/21 22:17
 * @Version: 1.0
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    //html过滤
    private final static HtmlFilterUtils htmlFilter = new HtmlFilterUtils();
    /**
     * 存储requestBody byte[]
     */
    private final byte[] body;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        this.body = RequestUtils.getByteBody(request);
    }

    @Override
    public BufferedReader getReader() {
        ServletInputStream inputStream = getInputStream();
        return Objects.isNull(inputStream) ? null : new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public ServletInputStream getInputStream() {
        if (ObjectUtil.isNull(body)) {
            return null;
        }
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            @SuppressWarnings("EmptyMethod")
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public synchronized void reset() {
                bais.reset();
            }
        };
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = htmlEscape(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        return htmlEscape(value);
    }

    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value instanceof String) {
            htmlEscape((String) value);
        }
        return value;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return htmlEscape(value);
    }

    @Override
    public String getQueryString() {
        String value = super.getQueryString();
        if (value == null) {
            return null;
        }
        return htmlEscape(value);
    }

    /**
     * 转义html标签达到预防xss攻击效果
     *
     * @param str
     */
    protected String htmlEscape(String str) {
        return htmlFilter.filter(str);
    }

}