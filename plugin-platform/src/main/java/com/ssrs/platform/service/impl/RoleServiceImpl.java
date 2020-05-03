package com.ssrs.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ssrs.platform.model.entity.Role;
import com.ssrs.platform.mapper.RoleMapper;
import com.ssrs.platform.model.query.RoleQuery;
import com.ssrs.platform.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ssrs.platform.util.Page;
import com.ssrs.platform.util.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

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
