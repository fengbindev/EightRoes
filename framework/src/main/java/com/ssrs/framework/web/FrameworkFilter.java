package com.ssrs.framework.web;

import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
