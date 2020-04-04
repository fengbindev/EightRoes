package com.ssrs.framework.security.filter;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.security.JWTToken;
import com.ssrs.framework.web.ErrorCodeEnum;
import com.ssrs.framework.web.util.ResponseUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JWTFilter extends BasicHttpAuthenticationFilter {

    private PathMatcher pathMatcher;
    private UrlPathHelper urlPathHelper;


    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
        //获取请求token
        String token = getToken((HttpServletRequest) servletRequest);
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return new JWTToken(token);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (((HttpServletRequest) request).getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        //获取请求token，如果token不存在，直接返回401
        String token = getToken((HttpServletRequest) request);
        if (StrUtil.isBlank(token)) {
            return true;
        }
        return executeLogin(request, response);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        return sendUnauthorizedFail(request, response);
    }

    /**
     * 获取请求的token
     */
    protected String getToken(HttpServletRequest request) {
        //从header中获取token
        String token = request.getHeader(AUTHORIZATION_HEADER);
        return StrUtil.isBlank(token) ? null : token.replaceFirst("Bearer ", "");
    }

    /**
     * 无权限
     */
    protected boolean sendForbiddenFail(ServletRequest request, ServletResponse response) {
        ResponseUtils.sendFail(WebUtils.toHttp(request), WebUtils.toHttp(response), ErrorCodeEnum.FORBIDDEN);
        try {
            WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 路径不存在
     */
    protected boolean sendNotFoundFail(ServletRequest request, ServletResponse response) {
        ResponseUtils.sendFail(WebUtils.toHttp(request), WebUtils.toHttp(response), ErrorCodeEnum.NOT_FOUND);
        try {
            WebUtils.toHttp(response).sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 未认证
     */
    protected boolean sendUnauthorizedFail(ServletRequest request, ServletResponse response) {
        ResponseUtils.sendFail(WebUtils.toHttp(request), WebUtils.toHttp(response), ErrorCodeEnum.UNAUTHORIZED);
        try {
            WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        try {
            return super.executeLogin(request, response);
        } catch (Exception ignored) {
        }
        return false;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

}