package com.ssrs.platform.service.impl;

import com.ssrs.platform.model.entity.Role;
import com.ssrs.platform.mapper.RoleMapper;
import com.ssrs.platform.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色定义表 服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
