package com.ssrs.framework.web;

import com.ssrs.framework.Current;
import com.ssrs.framework.ResponseData;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Date;
import java.util.Map;

@Order(1)
@ControllerAdvice
public class RestResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> uiClazz = returnType.getMethod().getDeclaringClass();
        return converterType.equals(MappingJackson2HttpMessageConverter.class)
                && BaseController.class.isAssignableFrom(uiClazz);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body != null && body instanceof IResponseBodyData) {
            return body;
        }
        ResponseData r = Current.getResponse();
        if (body != null) {
            r.put("data", body);
        }
        if (r == null) {
            return null;
        }
        r.setHeader("Cache-Control", "no-cache");
        return r;
    }

}

