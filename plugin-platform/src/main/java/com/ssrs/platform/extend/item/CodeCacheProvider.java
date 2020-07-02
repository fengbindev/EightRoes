package com.ssrs.platform.extend.item;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.cache.CacheDataProvider;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.model.entity.Code;
import com.ssrs.platform.service.ICodeService;

/**
 * 代码项缓存提供者
 *
 * @author ssrs
 */
public class CodeCacheProvider extends CacheDataProvider {
    public static final String ProviderID = "code";

    @Override
    public void onKeyNotFound(String type, String key) {
        ICodeService codeService = SpringUtil.getBean(ICodeService.class);
        Code code = codeService.getOne(Wrappers.<Code>lambdaQuery().eq(Code::getCodeType, type).eq(Code::getCodeValue, key));
        FrameworkCacheManager.set(ProviderID, code.getCodeType(), code.getCodeValue(), code);
    }

    @Override
    public String getExtendItemID() {
        return ProviderID;
    }

    @Override
    public String getExtendItemName() {
        return "代码项缓存提供者";
    }

    /**
     * 获取指定代码类别下的指定代码项
     */
    public static Code get(String type, String codeValue) {
        return (Code) FrameworkCacheManager.get(ProviderID, type, codeValue);
    }

    public static void setCode(Code code) {
        if (code == null || "System".equals(code.getParentCode())) {
            return;
        }
        FrameworkCacheManager.set(ProviderID, code.getCodeType(), code.getCodeValue(), code);
    }

    public static void removeCode(Code code) {
        if (code == null) {
            return;
        }
        if ("System".equals(code.getParentCode())) {
            FrameworkCacheManager.removeType(ProviderID, code.getCodeType());
        } else {
            FrameworkCacheManager.removeType(ProviderID, code.getCodeType());
        }
    }

    public static void removeCode(String codeType) {
        if (StrUtil.isEmpty(codeType)) {
            return;
        }
        FrameworkCacheManager.removeType(ProviderID, codeType);
    }


}
