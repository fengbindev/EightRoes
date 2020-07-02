package com.ssrs.framework.security;

import org.apache.shiro.spring.aop.SpringAnnotationResolver;
import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;

/**
 * 自定义注解的AOP拦截器
 *
 * @author ssrs
 */
public class PermissionAopInterceptor extends AopAllianceAnnotationsAuthorizingMethodInterceptor {
    public PermissionAopInterceptor() {
        super();
        // 添加自定义的注解拦截器
        this.methodInterceptors.add(new PermissionMethodInterceptor(new SpringAnnotationResolver()));
    }
}