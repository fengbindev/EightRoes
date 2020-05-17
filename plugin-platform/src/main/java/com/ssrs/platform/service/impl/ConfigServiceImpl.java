package com.ssrs.platform.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.extend.ConfigService;
import com.ssrs.platform.model.entity.Config;
import com.ssrs.platform.mapper.ConfigMapper;
import com.ssrs.platform.service.IConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ssrs.platform.util.PlatformUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-02-25
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements IConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(Dict dict) {
        List<FixedConfigItem> items = ConfigService.getInstance().getAll();
        for (FixedConfigItem fci : items) {
            String value = dict.getStr(fci.getExtendItemID());
            if (value != null) {
                Config config = getById(fci.getExtendItemID());
                if (ObjectUtil.isNotEmpty(config)) {
                    if (StrUtil.isEmpty(value)) {
                        removeById(config.getCode());
                        continue;
                    }
                    config.setValue(value);
                    config.setName(fci.getExtendItemName());
                    updateById(config);
                } else {
                    if (StrUtil.isEmpty(value)) {
                        continue;
                    }
                    config.setValue(value);
                    config.setName(fci.getExtendItemName());
                    save(config);
                }
            }
        }
        // 更新缓存
        for (FixedConfigItem fci : items) {
            com.ssrs.framework.Config.removeValue(fci.getExtendItemID());
        }
        PlatformUtil.loadDBConfig();
    }
}
