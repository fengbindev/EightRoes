package com.ssrs.framework.web.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
* @Description:    设置spring上下文
* @Author:          ssrs
* @CreateDate:     2019/8/18 16:56
* @UpdateUser:     ssrs
* @UpdateDate:     2019/8/18 16:56
* @Version:        1.0
*/
@Component
public class ApplicationContextRegister implements ApplicationContextAware {

    private static ApplicationContext APPLICATION_CONTEXT;

    static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT;
    }

    /**
     * 设置spring上下文
     *
     * @param applicationContext spring上下文
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }
}
