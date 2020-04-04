package com.ssrs.framework.util;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;

@Configuration
public class SpringUtil implements ApplicationContextAware {

    private volatile static ApplicationContext applicationContext;

    private synchronized static void setApplicationContexts(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * 容器加载完成
     *
     * @param applicationContext application
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setApplicationContexts(applicationContext);
    }

    /**
     * 获取applicationContext
     *
     * @return application
     */
    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null){
            applicationContext = Config.getWebApplicationContext();
        }
        Assert.notNull(applicationContext, "application is null");
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name 名称
     * @return 对象
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz class
     * @param <T>   对象
     * @return 对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name  名称
     * @param clazz class
     * @param <T>   对象
     * @return 对象
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 动态注入class
     *
     * @param tClass class
     * @param <T>    t
     * @return obj
     */
    public static <T> T registerSingleton(Class<T> tClass) {
        Objects.requireNonNull(tClass);
        // 创建bean
        AutowireCapableBeanFactory autowireCapableBeanFactory = getApplicationContext().getAutowireCapableBeanFactory();
        T obj = autowireCapableBeanFactory.createBean(tClass);
        String beanName = StrUtil.upperFirst(tClass.getSimpleName());
        registerSingleton(beanName, obj);
        return obj;
    }

    /**
     * 动态注入bean
     *
     * @param beanName beanName
     * @param object   值
     * @return 当前数量
     */
    public static int registerSingleton(String beanName, Object object) {
        // 注册
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) getApplicationContext();
        ConfigurableListableBeanFactory configurableListableBeanFactory = configurableApplicationContext.getBeanFactory();
        configurableListableBeanFactory.registerSingleton(beanName, object);
        return configurableListableBeanFactory.getSingletonCount();
    }

    public static void register(Class<?> tClass) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) getApplicationContext();
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(tClass);
        // 设置属性userService,此属性引用已经定义的bean:userService,这里userService已经被spring容器管理了.
        //        beanDefinitionBuilder.addPropertyReference("testService", "testService");
        // 注册bean
        String beanName = StrUtil.upperFirst(tClass.getSimpleName());
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
    }

}

