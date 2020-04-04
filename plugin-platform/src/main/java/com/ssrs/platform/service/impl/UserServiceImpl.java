package com.ssrs.platform.service.impl;

import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.mapper.UserMapper;
import com.ssrs.platform.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-02-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
