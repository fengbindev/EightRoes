package com.ssrs.framework;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.extend.ExtendManager;
import com.ssrs.framework.schedule.SystemTaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


@Configuration
@EnableAutoConfiguration
@ComponentScan({"**.com.ssrs.**"})
@ServletComponentScan({"com.ssrs.framework"})
@Order(-2147483648)
@EnableCaching
public class FrameworkAutoConfiguration extends SpringBootServletInitializer {
    private static final Log log = LogFactory.get();
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private CacheManager cacheManager;
    private static boolean initFlag = false;
    private static boolean startupFlag = false;
    private static FrameworkAutoConfiguration instance;

    public FrameworkAutoConfiguration() {
        instance = this;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebUtils.setWebAppRootSystemProperty(servletContext);
        Config.setServletContext(servletContext);
        Config.init();
        super.onStartup(servletContext);
        startupFlag = true;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(new Class[]{FrameworkAutoConfiguration.class});
    }

    @PostConstruct
    public void init() {
        if (!startupFlag) {
            Config.setServletContext(servletContext);
            Config.init();
            startupFlag = true;
        }
        if (!initFlag) {
            FrameworkCacheManager.setCacheManager(cacheManager);
            Config.setWebApplicationContext(webApplicationContext);
            Config.setBuildInfo(getBuildProperties());
            ExtendManager.getInstance().start();
            SystemTaskManager.getInstance().startAllTask();
            log.info("----" + Config.getAppCode() + "(" + Config.getAppName() + "):  Initialized----");
            initFlag = true;
        }
    }

    @PreDestroy
    public void destroy() {
        SystemTaskManager.getInstance().stopAllTask();
        ExtendManager.getInstance().destory();
        initFlag = false;
    }

    public BuildProperties getBuildProperties() {
        try {
            return (BuildProperties) this.webApplicationContext.getBean(BuildProperties.class);
        } catch (Exception var2) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        if (instance != null && instance.webApplicationContext != null) {
            try {
                return instance.webApplicationContext.getBean(clazz);
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

        return null;
    }
}
