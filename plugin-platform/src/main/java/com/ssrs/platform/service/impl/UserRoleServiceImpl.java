package com.ssrs.platform.service.impl;

import com.ssrs.platform.model.entity.UserRole;
import com.ssrs.platform.mapper.UserRoleMapper;
import com.ssrs.platform.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关联 服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}
