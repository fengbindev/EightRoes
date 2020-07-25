package com.ssrs.platform.service.impl;

import com.ssrs.platform.model.entity.OperateLog;
import com.ssrs.platform.mapper.OperateLogMapper;
import com.ssrs.platform.service.IOperateLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-07-25
 */
@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements IOperateLogService {

}
