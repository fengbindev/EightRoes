package com.ssrs.platform.bl;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ssrs
 * Date: Created in 2020/7/25 13:43
 * Description: 日志插入线程池
 */
public class LogTheadPool {
    private static Object mutex = new Object();
    private static LogTheadPool instance;
    private ThreadPoolExecutor threadPool;

    private static final int CorePoolSize = 0; // 线程的数量保持在池中，即使他们处于闲置状态
    private static final int MaximumPoolSize = Integer.MAX_VALUE; // 在池中允许的最大线程数。
    private static final int KeepAliveTime = 60; // 当线程数大于核心，多余的空闲线程终止前等待新的任务的时间。
    private static final TimeUnit KeepAliveTimeUnit = TimeUnit.SECONDS; // KeepAliveTime的单位。

    private LogTheadPool() {
        // CachedThreadPool 快速处理大量耗时较短的任务
        this.threadPool = new ThreadPoolExecutor(CorePoolSize, MaximumPoolSize, KeepAliveTime, KeepAliveTimeUnit, new SynchronousQueue<Runnable>());
    }

    public static LogTheadPool getInstance() {
        synchronized (mutex) {
            if (instance == null) {
                synchronized (mutex) {
                    if (instance == null) {
                        instance = new LogTheadPool();
                    }
                }
            }
        }
        return instance;
    }

    public synchronized void addTask(Runnable task) {
        this.threadPool.execute(task);

    }

    public int getActiveCount() {
        return this.threadPool.getActiveCount();
    }

    public int getQueueSize() {
        return this.threadPool.getQueue().size();
    }
}
