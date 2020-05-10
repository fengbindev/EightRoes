package com.ssrs.framework.security;

import com.ssrs.framework.Config;
import com.ssrs.framework.security.cache.ShiroSpringCacheManager;
import org.apache.shiro.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ssrs
 */
@Component
public class ShiroAuthorizationHelper {
    public static final String NAME = Config.getAppCode() + "-" + "com.ssrs.framework.security.JWTRealm";

    private static ShiroSpringCacheManager cacheManager;

    @Autowired
    public void setCacheManager(ShiroSpringCacheManager cacheManager) {
        ShiroAuthorizationHelper.cacheManager = cacheManager;
    }

    /**
     * 清除用户的授权信息
     *
     * @param username
     */
    public static void clearAuthorizationInfo(String username) {
        //Name.authorizationCache 为shiro自义cache名(shiroCasRealm为我们定义的reaml类的类名)
        Cache<Object, Object> cache = cacheManager.getCache(NAME + ".authorizationCache");
        cache.remove(username);
    }


}
