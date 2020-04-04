package com.ssrs.framework.data.p6spy;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * @Description: P6spy SQL 日志格式化
 * @Author: ssrs
 * @CreateDate: 2019/8/25 12:07
 * @UpdateUser: ssrs
 * @UpdateDate: 2019/8/25 12:07
 * @Version: 1.0
 */
public class P6spyLogFormat implements MessageFormattingStrategy {

    @Override
    public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql, final String url) {
        return StringUtils.isNotEmpty(sql) ? new StringBuilder().append(" Execute SQL：").append(sql.replaceAll("[\\s]+", StringPool.SPACE)).toString() : null;
    }
}
