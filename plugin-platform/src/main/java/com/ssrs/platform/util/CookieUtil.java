package com.ssrs.platform.util;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CookieUtil {

    public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, boolean httpOnly,
                                 int maxAge, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAge);
        String domain = null;
        int version = 0;
        if (ObjectUtil.isNotEmpty(request.getCookies())) {
            for (Cookie c : request.getCookies()) {
                if (StrUtil.isNotEmpty(c.getDomain())) {
                    if (StrUtil.isNotEmpty(domain)) {
                        cookie.setDomain(domain);
                        cookie.setVersion(version);
                        break;
                    }
                }
            }
        }
        cookie.setPath(path);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, boolean httpOnly,
                                 int maxAge) {
        addCookie(request, response, name, value, httpOnly, maxAge, getDefaultPath());
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        deleteCookie(response, name, false);
    }

    public static void deleteCookie(HttpServletResponse response, String name, boolean httpOnly) {
        Cookie c = new Cookie(name, "");
        c.setSecure(false);
        c.setMaxAge(0);
        c.setHttpOnly(httpOnly);
        c.setPath(getDefaultPath());
        response.addCookie(c);
    }

    private static String getDefaultPath() {
        String path = Config.getContextPath();
        return normalizePath(path);
    }

    private static String normalizePath(String path) {
        if (path == null) {
            path = "/";
        }

        if (!path.endsWith("/")) {
            path = path + "/";
        }

        return path;
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null && StrUtil.isNotEmpty(name)) {
            List<Cookie> tokenCookie = Stream.of(request.getCookies()).filter(ck -> name.equals(ck.getName())).collect(Collectors.toList());
            if (tokenCookie != null && tokenCookie.size() > 0) {
                return tokenCookie.get(0);
            }
        }
        return null;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie c = getCookie(request, name);
        return c != null ? c.getValue() : null;
    }
}
