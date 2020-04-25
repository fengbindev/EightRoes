package com.ssrs.framework.web;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.Current;
import com.ssrs.framework.User;
import com.ssrs.framework.security.filter.JWTFilter;
import com.ssrs.framework.util.JWTTokenUtils;
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
        restoreCurrentUserData(request);
        return true;
    }

    /**
     * 准备当前请求线程用户信息
     * @param request
     */
    private void restoreCurrentUserData(HttpServletRequest request) {
        // TODO 后续改为从缓存中获取用户信息，不要放在请求头，防止请求头过大
        //从header中获取token
        String token = request.getHeader(JWTTokenUtils.TOKEN_HEADER);
        if (StrUtil.isBlank(token)){
            return;
        }
        token =  token.replaceFirst("Bearer ", "");
        if (JWTTokenUtils.isExpired(token)){
            return;
        }
        // TODO 这里目前有问题，token数据直接存到了map里，没有存到属性上，后续改为从缓存种获取时修复。
        User.UserData userData = JWTTokenUtils.getUserDate(token);
        userData.setUserName(userData.get("userName").toString());
        userData.setBranchInnerCode(userData.get("branchInnerCode").toString());
        Current.setUser(userData);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}
