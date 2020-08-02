package com.ssrs.platform.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ssrs.platform.extend.ILogType;
import com.ssrs.platform.extend.LogTypeService;
import com.ssrs.platform.mapper.OperateLogMapper;
import com.ssrs.platform.model.entity.OperateLog;
import com.ssrs.platform.service.IOperateLogService;
import com.ssrs.platform.util.Page;
import com.ssrs.platform.util.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-07-25
 */
@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements IOperateLogService {

    /**
     * 通过类型获取日志
     *
     * @param type
     * @param params
     * @return
     */
    @Override
    public Page getLogByType(String type, Map<String, Object> params) {
        ILogType logType = LogTypeService.getInstance().get(type);
        IPage<OperateLog> page = page(new Query<OperateLog>().getPage(params, "id", false), new LambdaQueryWrapper<OperateLog>()
                .eq(OperateLog::getLogType, type)
                .eq(ObjectUtil.isNotEmpty(params.get("subType")), OperateLog::getSubType, params.get("subType"))
                .eq(ObjectUtil.isNotEmpty(params.get("operateType")), OperateLog::getOperateType, params.get("operateType"))
                .like(ObjectUtil.isNotEmpty(params.get("userName")), OperateLog::getUserName, params.get("userName"))
                .like(ObjectUtil.isNotEmpty(params.get("ip")), OperateLog::getIp, params.get("ip"))
                .like(ObjectUtil.isNotEmpty(params.get("message")), OperateLog::getIp, params.get("message"))
                .ge(ObjectUtil.isNotEmpty(params.get("startTime")), OperateLog::getCreateTime, params.get("startTime"))
                .le(ObjectUtil.isNotEmpty(params.get("endTime")), OperateLog::getCreateTime, params.get("endTime")));
        List<OperateLog> records = page.getRecords().stream().peek(log -> {
            log.setMemo(logType.decodeMessage(log.getLogMessage()));
        }).collect(Collectors.toList());
        page.setRecords(records);
        return new Page(page);
    }
}
