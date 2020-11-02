package com.ssrs.platform.bl;

import cn.hutool.json.JSONUtil;
import com.ssrs.framework.Current;
import com.ssrs.framework.User;
import com.ssrs.platform.model.entity.OperateLog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ssrs
 * Date: Created in 2020/7/25 17:02
 * Description:
 */
public class LogBL {

    private static ReentrantLock lock = new ReentrantLock();


    /**
     * 保存登陆日志
     *
     * @param type
     * @param subType
     * @param logMessage
     */
    public static void addUserLog(String type, String subType, String logMessage) {
        OperateLog operateLog = new OperateLog();
        operateLog.setUserName(User.getUserName() == null ? "-" : User.getUserName());
        operateLog.setIp(Current.getRequest() == null ? "-" : Current.getRequest().getClientIP());
        operateLog.setLogMessage(logMessage);
        operateLog.setLogType(type);
        operateLog.setSubType(subType);
        if (lock.tryLock()) {
            try {
                LogTheadPool.getInstance().addTask(new OperateLogTask(operateLog));
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 保存登陆日志
     *
     * @param type
     * @param operateType
     * @param logMessage
     * @param result
     * @param message
     */
    public static void addSqlLog(String type, String operateType, String logMessage, String result, String message) {
        OperateLog operateLog = new OperateLog();
        operateLog.setUserName(User.getUserName() == null ? "-" : User.getUserName());
        operateLog.setIp(Current.getRequest() == null ? "-" : Current.getRequest().getClientIP());
        operateLog.setLogMessage(logMessage);
        operateLog.setLogType(type);
//        operateLog.setSubType(subType);
        operateLog.setOperateType(operateType);
        operateLog.setResult(result);
        operateLog.setMessage(message);
        if (lock.tryLock()) {
            try {
                LogTheadPool.getInstance().addTask(new OperateLogTask(operateLog));
            } finally {
                lock.unlock();
            }

        }
    }

    /**
     * 保存系统操作日志
     *
     * @param type
     * @param operateType
     * @param logMessage
     * @param result
     * @param message
     */
    public static void addOperateLog(String type, String operateType, String logMessage, String result, String
            message) {
        OperateLog operateLog = new OperateLog();
        operateLog.setUserName(User.getUserName() == null ? "-" : User.getUserName());
        operateLog.setIp(Current.getRequest() == null ? "-" : Current.getRequest().getClientIP());
        operateLog.setLogMessage(logMessage);
        operateLog.setLogType(type);
//        operateLog.setSubType(subType);
        operateLog.setOperateType(operateType);
        Map<String, Object> requestData = new HashMap<>(Current.getRequest());
        if (requestData.containsKey("password")) {
            requestData.put("password", "******");
            requestData.remove("confirmPassword");
        }
        if (requestData.containsKey("confirmPassword")) {
            requestData.put("confirmPassword", "******");
        }
        String requestBody = JSONUtil.toJsonStr(requestData);
        operateLog.setDetailMessage(requestBody == null ? null : (requestBody.length() > 4000 ? requestBody.substring(0, 4000) : requestBody));
        String url = Current.getRequest().getURL();
        operateLog.setUrl(url);
        operateLog.setResult(result);
        operateLog.setMessage(message);
        if (lock.tryLock()) {
            try {
                LogTheadPool.getInstance().addTask(new OperateLogTask(operateLog));
            } finally {
                lock.unlock();
            }
        }
    }

}
