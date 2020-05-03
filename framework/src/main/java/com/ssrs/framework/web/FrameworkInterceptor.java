package com.ssrs.framework.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.Current;
import com.ssrs.framework.User;
import com.ssrs.framework.cache.FrameworkCacheManager;
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
import java.util.Map;
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
                if (methodMappings.length > 0) {
                    for (String methodMapping : methodMappings) {
                        mappings.add(reqMapping + methodMapping);
                    }
                } else {
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
     *
     * @param request
     */
    private void restoreCurrentUserData(HttpServletRequest request) {
        //从header中获取token
        String token = request.getHeader(JWTTokenUtils.TOKEN_HEADER);
        if (StrUtil.isBlank(token)) {
            return;
        }
        token = token.replaceFirst("Bearer ", "");
        if (JWTTokenUtils.isExpired(token)) {
            return;
        }
        String userName = JWTTokenUtils.getUserName(token);
        /**
         * TODO
         * 目前是从缓存中获取用户信息，感觉不太好，用户的缓存提供者并不在framework模块，导致提供者id和type写死了，没有做到模块分离
         * 目前有想到的几种方法但都不是很理想
         * （1）使用Cache模块重写Shior的SessionManage和CacheManager，登录完成后将用户信息放到Shiro的Session中去。已经在缓存中存了用户信息，再存一份就有点恶心了
         * （2）放到请求头的token中去，最开始就是这样的，但是考虑到token信息要尽量小，防止每次请求都要带上大量token影响带宽性能
         */
        Object user = FrameworkCacheManager.get("Platform", "User", userName);
        Map<String, Object> userMap = BeanUtil.beanToMap(user);
        User.setUserName(Convert.toStr(userMap.get("userName")));
        User.setRealName(Convert.toStr(userMap.get("realName")));
        User.setBranchAdministrator(StrUtil.equals("Y", Convert.toStr(userMap.get("branchAdmin"))));
        User.setBranchInnerCode(Convert.toStr(userMap.get("branchInnercode")));
        User.setSessionId(request.getSession().getId());
        User.setLogin(true);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Current.clear();// 确保Current中的数据被清空
    }
}
