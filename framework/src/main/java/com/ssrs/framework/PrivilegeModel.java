package com.ssrs.framework;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * 权限集合。每一个用户会话中都有一个本类的实例。
 */
public class PrivilegeModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String OwnerType_Branch = "B";

    public static final String OwnerType_Role = "R";

    public static final String OwnerType_User = "U";

    public static final int Flag_Allow = 1;

    public static final int Flag_NotSet = 0;

    public static final String Or = "||";

    private static final String CompressedPrefix = "Compressed\n";

    Map<String, Integer> old = MapUtil.newHashMap();

    Map<String, Integer> keys = MapUtil.newHashMap();


    private static char[] bitChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '$', '%', 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static boolean isID(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static int charToFlag(char flag) {
        int i = 0;
        if (flag == 36 || flag == 37) {
            i = flag;
        } else if (flag < 58) {// 数字
            i = flag - 48;
        } else if (flag < 91) {// 大写字母
            i = flag - 27;
        } else if (flag < 123) {// 小写字母
            i = flag - 87;
        }
        return i;
    }

    /**
     * 检查是否有权限
     *
     * @param k 权限字符
     * @return ture:有，false：没有
     */
    public boolean hasPriv(String k) {
        if (k == null) {
            return false;
        }
        return keys.containsKey(k);
    }

    public void add(String k) {
        put(Flag_Allow, k);
    }

    public void put(int flag, String k) {
        if (k == null) {
            return;
        }
        keys.put(k, flag);
    }

    public boolean containsKey(String k) {
        return hasPriv(k);
    }

    public String getString(String k) {
        return hasPriv(k) ? "1" : "0";
    }

    /**
     * 权限交集操作。<br>
     * 在用户登录后获得权限集合时必须要先将自身权限和所在机构权限范围求交集。这是为了处理以下情况：
     * 用户A在机构Org1下获得了权限P，保存在自身的权限记录里。然后修改A的机构为Org2，但Org2的权限范围不包括P。
     */
    public void intersect(PrivilegeModel p) {
        if (p == null) {
            return;
        }
        for (String k : keys.keySet()) {
            if (!p.keys.containsKey(k)) {
                keys.remove(k);
            }
        }
    }

    /**
     * 合并另一个权限对象的值。本方法主要用于两个用途：<br>
     * 1、用户登录时将用户所属角色权限赋予用户。<br>
     * 2、用户代理其他用户操作。
     */
    public void union(PrivilegeModel p) {
        if (p == null) {
            return;
        }
        keys.putAll(p.keys);
    }

    public void parse(String privs) {
        clear();
        if (StrUtil.isEmpty(privs)) {
            return;
        }
        if (privs.startsWith("{")) {// 位标识的字符串
            JSONObject all = JSONUtil.parseObj(privs);
            JSONObject jo = all.getJSONObject("keys");
            for (Map.Entry<String, Object> entry : jo.entrySet()) {
                keys.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()));
            }
            old.clear();
        }
    }

    public void remove(String k) {
        if (k == null) {
            return;
        }
        keys.remove(k);
    }

    private void clear() {
        keys.clear();
    }

    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("keys", keys);
        return JSONUtil.toJsonStr(map);
    }
}

