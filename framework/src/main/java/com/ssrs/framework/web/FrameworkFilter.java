package com.ssrs.framework.web;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ssrs.framework.Config;
import com.ssrs.framework.web.util.IpUtils;
import com.ssrs.framework.web.util.LogUtils;
import com.ssrs.framework.web.util.RequestUtils;
import com.ssrs.framework.web.util.ResponseUtils;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Order(1)
@WebFilter(filterName = "frameworkFilter", urlPatterns = "/*")
public class FrameworkFilter implements Filter {

    @Override
    @SuppressWarnings("EmptyMethod")
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws ServletException, IOException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        chain.doFilter(new RequestWrapper(req), res);
    }

    @Override
    @SuppressWarnings("EmptyMethod")
    public void init(FilterConfig config) {
    }

}
