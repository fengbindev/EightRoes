package com.ssrs.framework.security;

import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.framework.web.ErrorCodeEnum;
import com.ssrs.framework.web.util.ApplicationUtils;
import com.ssrs.framework.web.util.ResponseUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * 自定义权限处理器
 */
public class PermissionHandler extends AuthorizingAnnotationHandler {

    public PermissionHandler() {
        super(Priv.class);
    }

    @Override
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (a instanceof Priv) {
            Priv annotation = (Priv) a;
            String[] perms = annotation.value();
            Subject subject = getSubject();
            // 不需要登录
            if (!annotation.login()) {
                return;
            }
            // 只需要登录
            if (annotation.login() && perms.length == 0 ) {
                if (subject.isAuthenticated()){
                    return;
                }else {
                    throw new ShiroException("No Authenticated!");
                }
            }
            // 多个权限，有一个就通过
            boolean hasAtLeastOnePermission = false;
            for (String permission : perms) {
                if (subject.isPermitted(permission)) {
                    hasAtLeastOnePermission = true;
                    break;
                }
            }
            // Cause the exception if none of the role match,
            // note that the exception message will be a bit misleading
            if (!hasAtLeastOnePermission) {
                subject.checkPermission(perms[0]);
            }
        }
    }
}
