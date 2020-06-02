package com.ssrs.framework.web.config;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.web.FrameworkHandlerExceptionResolver;
import com.ssrs.framework.web.FrameworkInterceptor;
import com.ssrs.framework.web.IEnumConverterFactory;
import com.ssrs.framework.web.ValidatorCollectionImpl;
import com.ssrs.framework.web.util.JacksonUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    @Override
    public Validator getValidator() {
        return new SpringValidatorAdapter(new ValidatorCollectionImpl());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new IEnumConverterFactory());
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.forEach(JacksonUtils.wrapperObjectMapper());
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new FrameworkHandlerExceptionResolver());
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        CorsConfiguration conf = new CorsConfiguration();
        conf.addAllowedHeader("*");
        conf.addAllowedMethod("*");
        conf.addAllowedOrigin("*");
        //允许cookie
        conf.setAllowCredentials(true);
        conf.setMaxAge(3600L);
        conf.addExposedHeader("set-cookie");
        conf.addExposedHeader("access-control-allow-headers");
        conf.addExposedHeader("access-control-allow-methods");
        conf.addExposedHeader("access-control-allow-origin");
        conf.addExposedHeader("access-control-max-age");
        conf.addExposedHeader("X-Frame-Options");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf); //  对接口配置跨域设置
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }


    @Bean
    public FrameworkInterceptor securityInterceptor(){
        return new FrameworkInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor());
    }

    /**
     * 接收前端入参日期的转换处理
     *
     * @return
     */
    @Bean
    public Converter<String, LocalDateTime> LocalDateTimeConvert() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(@NonNull String source) {
                LocalDateTime date = null;
                if (StrUtil.isNotBlank(source)) {
                    date = LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                return date;
            }
        };
    }

    @Bean
    public Converter<String, LocalDate> LocalDateConvert() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(@NonNull String source) {
                LocalDate date = null;
                if (StrUtil.isNotBlank(source)) {
                    date = LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                return date;
            }
        };
    }

    @Bean
    public Converter<String, LocalTime> LocalTimeConvert() {
        return new Converter<String, LocalTime>() {
            @Override
            public LocalTime convert(@NonNull String source) {
                LocalTime time = null;
                if (StrUtil.isNotBlank(source)) {
                    time = LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm:ss"));
                }
                return time;
            }
        };
    }

}

