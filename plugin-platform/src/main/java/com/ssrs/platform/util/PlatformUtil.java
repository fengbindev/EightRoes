package com.ssrs.platform.util;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.Config;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.config.AdminUserName;
import com.ssrs.platform.extend.item.CodeCacheProvider;
import com.ssrs.platform.model.entity.Code;
import com.ssrs.platform.model.entity.Role;
import com.ssrs.platform.service.IConfigService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlatformUtil {
    private static final Object mutex = new Object();


    /**
     * 载入数据库配置中的配置项
     */
    public static void loadDBConfig() {
        synchronized (mutex) {
            try {
                IConfigService configService = SpringUtil.getBean(IConfigService.class);
                configService.list().forEach(config -> {
                    Config.getMap().put(config.getCode(), config.getValue());
                });
                Config.getMap().put("AdminUserName", AdminUserName.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将代码项转为map
     *
     * @param codeType /
     * @return /
     */
    public static Dict getCodeMap(String codeType) {
        Dict dict = Dict.create();
        List<String> codeCacheKey = FrameworkCacheManager.getTypeKeys(CodeCacheProvider.ProviderID, codeType);
        codeCacheKey.forEach(key -> {
            Code code = (Code) FrameworkCacheManager.get(CodeCacheProvider.ProviderID, codeType, key);
            dict.put(code.getCodeValue(), code.getCodeName());
        });
        return dict;
    }

    public static List<String> getRoleCodesByUserName(String userName) {
        String roles = (String) FrameworkCacheManager.get(PlatformCache.ProviderID, PlatformCache.Type_UserRole, userName);
        if (roles == null) {
            return null;
        }
        String[] arr = roles.split(",");
        Set<String> set = new HashSet<String>();
        for (String seg : arr) {
            if (StrUtil.isNotEmpty(seg)) {
                set.add(seg);
            }
        }
        ArrayList<String> list = new ArrayList<String>();
        if (set.size() > 0) {
            list.addAll(set);
            return list;
        }
        return null;
    }

    public static String getRoleName(String roleCode) {
        Role role = (Role) FrameworkCacheManager.get(PlatformCache.ProviderID, PlatformCache.Type_Role, roleCode);
        if (role == null) {
            return null;
        }
        return role.getRoleName();
    }
}
