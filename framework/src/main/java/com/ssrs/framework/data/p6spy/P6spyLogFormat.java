package com.ssrs.framework.data.p6spy;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * <p> P6spy SQL 日志格式化 </p>
 *
 * @author ssrs
 */
public class P6spyLogFormat implements MessageFormattingStrategy {

    @Override
    public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql, final String url) {
        return StrUtil.isNotEmpty(sql) ? new StringBuilder().append(" Execute SQL：").append(sql.replaceAll("[\\s]+", StringPool.SPACE)).toString() : null;
    }
}
