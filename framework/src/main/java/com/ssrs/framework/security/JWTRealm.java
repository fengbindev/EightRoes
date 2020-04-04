package com.ssrs.framework.security;

import cn.hutool.core.collection.CollUtil;
import com.ssrs.framework.extend.ExtendManager;
import com.ssrs.framework.point.AddUserPermissionsPoint;
import com.ssrs.framework.point.AddUserRolesPoint;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: shiro的权授权与认证
 * @Author: ssrs
 * @CreateDate: 2019/8/31 12:17
 * @UpdateUser: ssrs
 * @UpdateDate: 2019/8/31 12:17
 * @Version: 1.0
 */
public class JWTRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 授权
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object[] permissions = ExtendManager.invoke(AddUserPermissionsPoint.ID, new Object[]{});
        Object[] roles = ExtendManager.invoke(AddUserRolesPoint.ID, new Object[]{});
        Set<String> permissionSet = permissions == null ? CollUtil.newHashSet() : Arrays.stream(permissions).flatMap(x -> ((Set<String>) x).stream()).collect(Collectors.toSet());
        Set<String> roleSet = roles == null ? CollUtil.newHashSet() : Arrays.stream(roles).flatMap(x -> ((Set<String>) x).stream()).collect(Collectors.toSet());
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissionSet);
        simpleAuthorizationInfo.setRoles(roleSet);
        return simpleAuthorizationInfo;
    }

    /**
     * 认证
     *
     * @param auth
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String accessToken = (String) auth.getPrincipal();
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(accessToken, accessToken, getName());
        return info;
    }
}
