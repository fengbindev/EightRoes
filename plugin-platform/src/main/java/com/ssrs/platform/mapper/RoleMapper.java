package com.ssrs.platform.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ssrs.platform.model.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ssrs.platform.model.query.RoleQuery;

/**
 * <p>
 * 角色定义表 Mapper 接口
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
public interface RoleMapper extends BaseMapper<Role> {

    IPage<RoleQuery> selectPage(IPage<?> page, String roleName);
}
