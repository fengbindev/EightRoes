package com.ssrs.platform;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Predicate;
import com.ssrs.framework.Config;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.util.JWTTokenUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ssrs
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

    private static final Log log = LogFactory.get(SwaggerConfiguration.class);

    public static final String API_PACKAGE = "api";
    public static final String FRONT_PACKAGE = "front";

    public static AbstractHandlerMethodMapping<RequestMappingInfo> requestHandlerMapping;
    private static Set<String> needTokenPath = new HashSet<String>();

    /**
     * 是否开启swagger，正式环境一般是需要关闭的
     */
    @Value("${swagger.enabled}")
    private boolean enableSwagger;

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;


    @PostConstruct
    @SuppressWarnings("unchecked")
    public static void init() {
        WebApplicationContext applicationContext = Config.getWebApplicationContext();
        if (applicationContext != null) {
            RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
            requestHandlerMapping = Config.getWebApplicationContext().getBean(AbstractHandlerMethodMapping.class);
            mapping.getHandlerMethods().forEach((request, method) -> {
                if (method.getMethod().isAnnotationPresent(Priv.class)) {
                    Priv priv = method.getMethod().getAnnotation(Priv.class);
                    String methodValue = method.getMethod().toGenericString();
                    String otherUrl = request.getPatternsCondition().getPatterns().iterator().next();
                    if (priv.login()) {
                        needTokenPath.add(otherUrl);
                    }
                }
            });
        } else {
            log.warn("WebApplicationContext not found!");
        }
    }

    private ApiInfo apiInfo(String groupName) {
        return new ApiInfoBuilder().title(Config.getAppName() + groupName + "接口文档").description("提供" + groupName + "接口服务的文档。")
                .termsOfServiceUrl("http://www.ssrsdev.top/eightroesadmin/").version(Config.getAppVersion()).build();
    }

    /*
     * 动态注册多个Docket
     * @return
     */
    @Bean
    public String createDocket() {
        // api接口
        BeanDefinitionBuilder apiBeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Docket.class);
        apiBeanDefinitionBuilder.addConstructorArgValue(DocumentationType.SWAGGER_2);
        BeanDefinition apiBeanDefinition = apiBeanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry apiBeanFactory = (BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
        apiBeanFactory.registerBeanDefinition(API_PACKAGE, apiBeanDefinition);
        Docket apiDocket = configurableApplicationContext.getBean(API_PACKAGE, Docket.class);
        apiDocket.groupName(API_PACKAGE).apiInfo(apiInfo("API")).enable(enableSwagger).select().apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.ant("/api/**")).build().securitySchemes(securitySchemes())
                .securityContexts(securityContexts(API_PACKAGE));

        // front接口
        BeanDefinitionBuilder frontBeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Docket.class);
        frontBeanDefinitionBuilder.addConstructorArgValue(DocumentationType.SWAGGER_2);
        BeanDefinition frontBeanDefinition = frontBeanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry frontBeanFactory = (BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
        frontBeanFactory.registerBeanDefinition(FRONT_PACKAGE, frontBeanDefinition);
        Docket frontDocket = configurableApplicationContext.getBean(FRONT_PACKAGE, Docket.class);
        frontDocket.groupName(FRONT_PACKAGE).apiInfo(apiInfo("网站服务")).enable(enableSwagger).select().apis(RequestHandlerSelectors.basePackage("com.ssrs"))
                .paths(PathSelectors.ant("/front/**")).build().securitySchemes(securitySchemes())
                .securityContexts(securityContexts(FRONT_PACKAGE));
        return "createDocket";
    }


    /**
     * 目前支持JWT验证方式
     *
     * @return
     */
    private List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeyList = new ArrayList<ApiKey>();
        apiKeyList.add(new ApiKey("Token认证", JWTTokenUtils.TOKEN_HEADER, "header"));
        return apiKeyList;
    }

    /**
     * 从上下文中告诉swagger哪些是需要做身份认证的接口，swagger本身表现为接口右侧加锁，swagger2markup转换文档时会加上授权认证的字样说明
     *
     * @return
     */
    private List<SecurityContext> securityContexts(String group) {
        List<SecurityContext> securityContexts = new ArrayList<>();
        if (API_PACKAGE.equals(group)) {
            securityContexts.add(SecurityContext.builder().securityReferences(defaultAuth()).forPaths(getNeedTokenPath()).build());
        } else if (FRONT_PACKAGE.equals(group)) {
            securityContexts.add(SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build());
        }

        return securityContexts;
    }

    /**
     * 验证网站接口哪些path需要认证
     *
     * @return
     */
    private Predicate<String> getNeedTokenPath() {
        return input -> needTokenPath.contains(input);
    }

    /**
     * 默认验证方式，全局
     *
     * @return
     */
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Token认证", authorizationScopes));
        return securityReferences;
    }

}
