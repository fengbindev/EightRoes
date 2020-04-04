package com.ssrs.platform.service.impl;

import com.ssrs.platform.model.entity.Config;
import com.ssrs.platform.mapper.ConfigMapper;
import com.ssrs.platform.service.IConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-02-25
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements IConfigService {

}
