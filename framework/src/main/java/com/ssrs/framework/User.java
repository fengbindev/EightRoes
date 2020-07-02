package com.ssrs.framework;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户数据全局访问类，一个线程内的所有代码都可以直接访问用户数据<br>
 *
 * @author ssrs
 */
public class User {

    /**
     * 获取当前用户名
     *
     * @return
     */
    public static String getUserName() {
        UserData ud = getCurrent();
        return ud == null ? null : ud.getUserName();
    }

    /**
     * 设置当前用户名
     */
    public static void setUserName(String username) {
        getCurrent(true).setUserName(username);
    }

    /**
     * 获取当前用户的真实名称
     */
    public static String getRealName() {
        UserData ud = getCurrent();
        return ud == null ? null : ud.getRealName();
    }

    /**
     * 设置当前用户的真实名称
     */
    public static void setRealName(String realName) {
        getCurrent(true).setRealName(realName);
    }

    /**
     * 获取当前用户的分支机构内部编码
     */
    public static String getBranchInnerCode() {
        UserData ud = getCurrent();
        return ud == null ? null : ud.getBranchInnerCode();
    }

    /**
     * 设置当前用户的分支机构内部编码
     */
    public static void setBranchInnerCode(String branchInnerCode) {
        getCurrent(true).setBranchInnerCode(branchInnerCode);
    }

    /**
     * 当前用户是否是机构管理员
     */
    public static boolean isBranchAdministrator() {
        UserData ud = getCurrent();
        return ud == null ? false : ud.isBranchAdministrator();
    }

    /**
     * 设置当前用户的分支机构内部编码
     */
    public static void setBranchAdministrator(boolean flag) {
        getCurrent(true).setBranchAdministrator(flag);
    }

    /**
     * 设置当前用户的分支机构内部编码
     */
    public static void setSessionId(String sessionId) {
        getCurrent(true).setSessionID(sessionId);
    }

    /**
     * 按key获取指定数据项
     */
    public static Object getValue(Object key) {
        UserData ud = getCurrent();
        return ud == null ? null : ud.get(key);
    }

    /**
     * 设置当前用户指定数据项
     */
    public static void setValue(String key, Object value) {
        Map<String, Object> map = getCurrent(true);
        map.put(key, value);
    }

    /**
     * 当前用户是否己登录
     */
    public static boolean isLogin() {
        UserData ud = getCurrent();
        return ud == null ? false : ud.isLogin();
    }

    /**
     * 设置当前用户的登录状态
     */
    public static void setLogin(boolean isLogin) {
        getCurrent(true).setLogin(isLogin);
    }

    /**
     * 设置当前用户对象
     */
    public static void setCurrent(UserData user) {
        Current.setUser(user);
        if (user == null || Current.getRequest() == null) {
            return;
        }
    }

    /**
     * 获取当前用户对象
     */
    public static UserData getCurrent() {
        return getCurrent(false);
    }

    protected static UserData getCurrent(boolean create) {// 如果Current中没有值且create为true，则置一个UserData到Current
        UserData ud = Current.getUser();
        if (ud == null) {
            ud = new UserData();
            if (create) {
                setCurrent(ud);
            }
        }
        return ud;
    }


    /**
     * 获得当前用户的权限集合
     */
    public static PrivilegeModel getPrivilege() {
        UserData ud = getCurrent();
        return ud == null ? null : ud.getPrivilegeModel();
    }

    /**
     * 设置当前用户的权限集合
     */
    public static void setPrivilegeModel(PrivilegeModel priv) {
        getCurrent(true).setPrivilegeModel(priv);
    }


    public static class UserData extends HashMap<String, Object> implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 用户状态
         */
        private String status;

        /**
         * 用户名
         */
        private String userName;

        /**
         * 用户真实姓名
         */
        private String realName;

        /**
         * 所属分支机构
         */
        private String branchInnerCode;

        /**
         * 是否是机构管理员
         */
        private boolean branchAdminFlag;

        /**
         * 是否己登录
         */
        private boolean isLogin = false;

        /**
         * 会话ID
         */
        private String sessionID;

        /**
         * 当前用户权限
         */
        private PrivilegeModel priv;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;

        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;

        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;

        }

        public String getBranchInnerCode() {
            return branchInnerCode;
        }

        public void setBranchInnerCode(String branchInnerCode) {
            this.branchInnerCode = branchInnerCode;

        }

        public boolean isBranchAdministrator() {
            return branchAdminFlag;
        }

        public void setBranchAdministrator(boolean flag) {
            this.branchAdminFlag = flag;

        }

        public boolean isLogin() {
            return isLogin;
        }

        public void setLogin(boolean isLogin) {
            this.isLogin = isLogin;

        }

        public String getSessionID() {
            return sessionID;
        }

        public void setSessionID(String sessionID) {
            this.sessionID = sessionID;
        }

        public PrivilegeModel getPrivilegeModel() {
            return null;
        }


        public void setPrivilegeModel(PrivilegeModel priv) {
            this.priv = priv;
        }
    }
}
