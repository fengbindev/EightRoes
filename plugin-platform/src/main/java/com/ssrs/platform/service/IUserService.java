package com.ssrs.platform.service;

import com.ssrs.platform.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * @author ssrs
 * @since 2020-04-30
 */
public interface IUserService extends IService<User> {

    User getOneByUserName(String key);
}
