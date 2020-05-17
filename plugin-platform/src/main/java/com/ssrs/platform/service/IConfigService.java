package com.ssrs.platform.service;

import cn.hutool.core.lang.Dict;
import com.ssrs.platform.model.entity.Config;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ssrs
 * @since 2020-02-25
 */
public interface IConfigService extends IService<Config> {

    void saveConfig(Dict dict);
}
