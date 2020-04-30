package com.ssrs.platform.service;

import com.ssrs.platform.model.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ssrs.platform.util.Page;

import java.util.Map;

/**
 * <p>
 * 角色定义表 服务类
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
public interface IRoleService extends IService<Role> {

    Page selectPage(Map<String, Object> params);
}
