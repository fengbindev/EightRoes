package com.ssrs.framework.security;

import com.ssrs.framework.security.cache.ShiroSpringCacheManager;
import com.ssrs.framework.security.filter.JWTFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroAutoConfiguration {

    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setUsePrefix(true);
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Bean
    public Realm realm() {
        return new JWTRealm();
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        return new DefaultShiroFilterChainDefinition();
    }

    @Bean
    public DefaultWebSecurityManager securityManager(Realm realm, ShiroSpringCacheManager cacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setCacheManager(cacheManager);
        return securityManager;
    }

    /**
     * 启用注解拦截方式
     *
     * @return AuthorizationAttributeSourceAdvisor
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new ShiroAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filters = new HashMap<>();
        JWTFilter value = new JWTFilter();
        value.setAuthzScheme("Bearer");
        value.setUrlPathHelper(new UrlPathHelper());
        value.setPathMatcher(new AntPathMatcher());
        filters.put("jwt", value);
        shiroFilterFactoryBean.setFilters(filters);
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/api/login", "anon");
        filterMap.put("/api/preview/**", "anon");
        filterMap.put("/api/authcode", "anon");
        filterMap.put("/api/api-docs/**", "anon");
        filterMap.put("/api/api-configuration/**", "anon");
        filterMap.put("/swagger-resources/configuration/ui", "anon");
        filterMap.put("/swagger-resources/configuration/security", "anon");
        filterMap.put("/swagger-resources", "anon");
        filterMap.put("/v2/api-docs-ext", "anon");
        filterMap.put("/**/**.*", "anon");
        filterMap.put("/**", "jwt");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return shiroFilterFactoryBean;
    }
}
