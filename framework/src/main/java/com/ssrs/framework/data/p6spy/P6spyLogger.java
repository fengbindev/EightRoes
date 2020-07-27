package com.ssrs.framework.data.p6spy;

import cn.hutool.core.util.StrUtil;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import com.ssrs.framework.extend.ExtendManager;
import com.ssrs.framework.point.AfterAllPluginStartedPoint;
import com.ssrs.framework.point.ConsoleSqlLogPoint;
import lombok.extern.slf4j.Slf4j;

/**
 * P6spy日志实现
 *
 * <p>
 * https://blog.csdn.net/z69183787/article/details/43453581
 *
 * @author ssrs
 * @see FormattedLogger
 * <p/>
 */
@Slf4j
public class P6spyLogger extends FormattedLogger {

    @Override
    public void logException(Exception e) {
        log.info("", e);
    }

    @Override
    public void logText(String text) {
        log.info(text);
    }

    @Override
    public void logSQL(int connectionId, String now, long elapsed,
                       Category category, String prepared, String sql, String url) {
        final String msg = strategy.formatMessage(connectionId, now, elapsed,
                category.toString(), prepared, sql, url);

        if (StrUtil.isEmpty(msg)) {
            return;
        }
        ExtendManager.invoke(ConsoleSqlLogPoint.ID, new Object[]{connectionId, now, elapsed, prepared, sql, url, msg});
        if (Category.ERROR.equals(category)) {
            log.error(msg);
        } else if (Category.WARN.equals(category)) {
            log.warn(msg);
        } else if (Category.DEBUG.equals(category)) {
            log.debug(msg);
        } else {
            log.info(msg);
        }
    }

    @Override
    public boolean isCategoryEnabled(Category category) {
        if (Category.ERROR.equals(category)) {
            return log.isErrorEnabled();
        } else if (Category.WARN.equals(category)) {
            return log.isWarnEnabled();
        } else if (Category.DEBUG.equals(category)) {
            return log.isDebugEnabled();
        } else {
            return log.isInfoEnabled();
        }
    }
}
