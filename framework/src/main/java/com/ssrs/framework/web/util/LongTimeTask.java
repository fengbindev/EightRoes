package com.ssrs.framework.web.util;

import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.User;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 长时间任务，当一个操作需要较长执行时间时，可以使用次类异步执行，如大文件导入，上传。
 *
 * @author ssrs
 */
@Slf4j
public abstract class LongTimeTask extends Thread {
    private static Map<Long, LongTimeTask> map = new HashMap<>();
    private static long IDBase = System.currentTimeMillis();
    private static final int MaxListSize = 1000;
    /**
     * 任务id
     */
    private long id;
    /**
     * 消息列表
     */
    private ArrayList<String> list;
    /**
     * 任务进度
     */
    protected int percent;
    /**
     * 当前任务描述
     */
    protected String currentInfo;
    /**
     * 任务完成描述
     */
    private String finishedInfo;
    /**
     * 任务错误信息
     */
    protected ArrayList<String> errors;
    /**
     * 任务停止标识
     */
    private boolean stopFlag;
    /**
     * 任务完成标识
     */
    private boolean finishFlag;
    /**
     * 执行任务用户
     */
    private User.UserData user;
    /**
     * 任务类型
     */
    private String type;
    /**
     * 停止时间
     */
    private long stopTime;
    /**
     * 任务锁
     */
    private static ReentrantLock lock = new ReentrantLock();


    /**
     * 创建一个空的任务
     *
     * @return
     */
    public static LongTimeTask createEmptyInstance() {
        return new LongTimeTask(false) {
            @Override
            public void execute() {
            }
        };
    }

    /**
     * 通过任务id获取任务
     *
     * @param id
     * @return
     */
    public static LongTimeTask getInstanceById(long id) {
        return (LongTimeTask) map.get(new Long(id));
    }

    /**
     * 获取所有的任务
     *
     * @return
     */
    public static Collection<LongTimeTask> getAllInstance() {
        return map.values();
    }

    /**
     * 通过id删除任务
     *
     * @param id
     */
    public static void removeInstanceById(long id) {
        map.remove(new Long(id));
    }

    /**
     * 通过任务类型取消任务
     *
     * @param type
     * @return
     */
    public static String cancelByType(String type) {
        String message = "不存在此类型的任务" + ":" + type;
        LongTimeTask ltt = getInstanceByType(type);
        if (ltt != null) {
            ltt.stopTask();
            message = "任务已经停止！";
        }

        return message;
    }

    /**
     * 通过任务类型获取任务
     *
     * @param type
     * @return
     */
    public static LongTimeTask getInstanceByType(String type) {
        if (StrUtil.isNotEmpty(type)) {
            long current = System.currentTimeMillis();
            Iterator var3 = map.keySet().iterator();

            while (var3.hasNext()) {
                Long key = (Long) var3.next();
                LongTimeTask ltt = (LongTimeTask) map.get(key);
                if (type.equals(ltt.getType())) {
                    if (current - ltt.stopTime > 60000L) {
                        map.remove(key);
                        return null;
                    }

                    return ltt;
                }
            }
        }

        return null;
    }

    public LongTimeTask() {
        this(true);
    }

    private LongTimeTask(boolean flag) {
        this.list = new ArrayList();
        this.errors = new ArrayList();
        this.stopTime = System.currentTimeMillis() + 1440000L;
        if (flag) {
            this.setName("LongTimeTask Thread");
            this.id = (long) (IDBase++);
            map.put(new Long(this.id), this);
            this.clearStopedTask();
        }

    }

    /**
     * 清除停止的任务
     */
    private void clearStopedTask() {
        lock.lock();

        try {
            long current = System.currentTimeMillis();
            Iterator iterator = map.keySet().iterator();

            while (iterator.hasNext()) {
                Long k = (Long) iterator.next();
                LongTimeTask ltt = (LongTimeTask) map.get(k);
                if (current - ltt.stopTime > 60000L) {
                    map.remove(k);
                }
            }
        } finally {
            lock.unlock();
        }

    }

    /**
     * 获取任务id
     *
     * @return
     */
    public long getTaskID() {
        return this.id;
    }

    public void info(String message) {
        log.info(message);
        this.list.add(message);
        if (this.list.size() > 1000) {
            this.list.remove(0);
        }

    }

    /**
     * 获取所有消息，并清除
     *
     * @return
     */
    public String[] getMessages() {
        String[] arr = new String[this.list.size()];

        for (int i = 0; i < arr.length; ++i) {
            arr[i] = (String) this.list.get(i);
        }

        this.list.clear();
        return arr;
    }

    @Override
    public void run() {
        if (StrUtil.isNotEmpty(this.type)) {
            LongTimeTask ltt = getInstanceByType(this.type);
            if (ltt != null && ltt != this && !ltt.finishFlag) {
                return;
            }
        }

        try {
            User.setCurrent(this.user);
            this.execute();
            this.finishFlag = true;
        } catch (Exception e) {
            this.interrupt();
        } finally {
            this.stopTime = System.currentTimeMillis();
        }

    }

    public abstract void execute();

    /**
     * 是否任务已经停止
     *
     * @return
     */
    public boolean checkStop() {
        return this.stopFlag;
    }

    /**
     * 停止任务
     */
    public void stopTask() {
        this.clearStopedTask();
        this.stopFlag = true;
    }

    /**
     * 获取任务执行进度
     *
     * @return
     */
    public int getPercent() {
        return this.percent;
    }

    /**
     * 设置任务执行进度
     *
     * @param percent
     */
    public void setPercent(int percent) {
        this.percent = percent;
    }

    /**
     * 设置任务当前执行信息描述
     *
     * @param currentInfo
     */
    public void setCurrentInfo(String currentInfo) {
        this.currentInfo = currentInfo;
        log.info(currentInfo);
    }

    /**
     * 获取任务当前执行信息描述
     */
    public String getCurrentInfo() {
        return this.currentInfo;
    }

    /**
     * 设置任务完成信息
     *
     * @param finishedInfo
     */
    public void setFinishedInfo(String finishedInfo) {
        this.setPercent(100);
        this.finishedInfo = finishedInfo;
        log.info(finishedInfo);
    }

    /**
     * 获取任务完成信息
     *
     * @return
     */
    public String getFinishedInfo() {
        return this.finishedInfo;
    }

    /**
     * 设置执行任务用户
     *
     * @param user
     */
    public void setUser(User.UserData user) {
        this.user = user;
    }

    /**
     * 添加任务错误信息
     *
     * @param error
     */
    public void addError(String error) {
        this.errors.add(error);
    }

    /**
     * 获取任务错误信息
     *
     * @return
     */
    public List<String> getAllErrors() {
        return this.errors;
    }

    /**
     * 获取任务类型
     *
     * @return
     */
    public String getType() {
        return this.type;
    }

    /**
     * 设置任务类型
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }
}
