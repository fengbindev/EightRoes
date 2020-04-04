package com.ssrs.framework.security;

import com.ssrs.framework.security.annotation.Priv;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * 自定义的Shiro注解拦截器
 * @author BBF
 */

public class ShiroAdvisor extends AuthorizationAttributeSourceAdvisor {
    /**
     * Create a new AuthorizationAttributeSourceAdvisor.
     */
    public ShiroAdvisor() {
        // 这里可以添加多个
        setAdvice(new PermissionAopInterceptor());
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean matches(Method method, Class targetClass) {
        Method m = method;
        if (targetClass != null) {
            try {
                m = targetClass.getMethod(m.getName(), m.getParameterTypes());
                return this.isFrameAnnotation(m);
            } catch (NoSuchMethodException ignored) {
                //default return value is false.  If we can't find the method, then obviously
                //there is no annotation, so just use the default return value.
            }
        }
        return super.matches(method, targetClass);
    }

    private boolean isFrameAnnotation(Method method) {
        return null != AnnotationUtils.findAnnotation(method, Priv.class);
    }
}
