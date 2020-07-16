package com.ssrs.platform.extend;

import com.ssrs.framework.extend.AbstractExtendService;
import com.ssrs.platform.FixedCodeType;

/**
 * 日志类型扩展服务
 *
 * @author ssrs
 */
public class LogTypeService extends AbstractExtendService<ILogType> {

    public static LogTypeService getInstance() {
        return findInstance(LogTypeService.class);
    }
}
