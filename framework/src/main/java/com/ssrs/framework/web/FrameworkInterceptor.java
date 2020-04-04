package com.ssrs.framework.web;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.Current;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
public class FrameworkInterceptor implements HandlerInterceptor {
    private static final Log log = LogFactory.get();
    private String[] emptyArray = new String[]{""};


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerObj = (HandlerMethod) handler;
            // 错误交由Srping处理
            if (handlerObj.getBean() instanceof BasicErrorController) {
                return true;
            }
            RequestMapping requestMappingAnnotation = handlerObj.getBeanType().getAnnotation(RequestMapping.class);
            RequestMapping methodMappingAnnotation = handlerObj.getMethodAnnotation(RequestMapping.class);
            if (Objects.isNull(requestMappingAnnotation) && Objects.isNull(methodMappingAnnotation)) {
                return false;
            }
            String[] requestMappings = Objects.nonNull(requestMappingAnnotation) ? requestMappingAnnotation.value() : emptyArray;
            String[] methodMappings = Objects.nonNull(methodMappingAnnotation) ? methodMappingAnnotation.path() : emptyArray;
            Set<String> mappings = new HashSet<>(1);
            for (String reqMapping : requestMappings) {
                if (methodMappings.length>0){
                    for (String methodMapping : methodMappings) {
                        mappings.add(reqMapping + methodMapping);
                    }
                }else{
                    mappings.add(reqMapping);
                }
            }
            request.setAttribute(APICons.API_MAPPING, ArrayUtil.join(mappings.toArray(), ";"));
            request.setAttribute(APICons.API_BEGIN_TIME, System.currentTimeMillis());
            request.setAttribute(APICons.API_METHOD, request.getMethod());
            request.setAttribute(APICons.API_REQURL, request.getRequestURI());
        }
        Current.prepare(request, response);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}
