package com.ssrs.platform.service.impl;

import com.ssrs.platform.model.entity.Privilege;
import com.ssrs.platform.mapper.PrivilegeMapper;
import com.ssrs.platform.service.IPrivilegeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
@Service
public class PrivilegeServiceImpl extends ServiceImpl<PrivilegeMapper, Privilege> implements IPrivilegeService {

}
