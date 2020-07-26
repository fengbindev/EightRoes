package com.ssrs.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ssrs.platform.model.entity.OperateLog;
import com.ssrs.platform.util.Page;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ssrs
 * @since 2020-07-25
 */
public interface IOperateLogService extends IService<OperateLog> {

    /**
     * 通过类型获取日志
     *
     * @param type
     * @param params
     * @return
     */
    Page getLogByType(String type, Map<String, Object> params);
}
