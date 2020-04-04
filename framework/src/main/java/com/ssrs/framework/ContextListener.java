package com.ssrs.framework;

import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


@WebListener
public class ContextListener implements ServletContextListener {
    public ContextListener() {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        FrameworkAutoConfiguration fac = (FrameworkAutoConfiguration) WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext()).getBean(FrameworkAutoConfiguration.class);
        fac.destroy();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        FrameworkAutoConfiguration fac = (FrameworkAutoConfiguration)WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext()).getBean(FrameworkAutoConfiguration.class);
        fac.init();
    }
}

