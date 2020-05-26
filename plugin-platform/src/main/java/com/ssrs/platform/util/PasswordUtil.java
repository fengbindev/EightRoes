package com.ssrs.platform.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;

public class PasswordUtil {

    /**
     * 生成密码MD5
     * @param password
     * @return
     */
    public static String generate(String password) {
        String salt = leftPad(String.valueOf(RandomUtil.randomInt(99999999)), '0', 8) + leftPad(String.valueOf(RandomUtil.randomInt(99999999)), '0', 8);
        password = SecureUtil.md5(password + salt);
        char[] cs = new char[48];

        for(int i = 0; i < 48; i += 3) {
            cs[i] = password.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
        }

        return new String(cs);
    }

    /**
     * 校验密码
     * @param password 原密码
     * @param md5 生成的MD5
     * @return
     */
    public static boolean verify(String password, String md5) {
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];

        for(int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }

        String salt = new String(cs2);
        return SecureUtil.md5(password + salt).equals(new String(cs1));
    }


    public static String leftPad(String srcString, char c, int length) {
        if (srcString == null) {
            srcString = "";
        }

        int tLen = srcString.length();
        if (tLen >= length) {
            return srcString;
        } else {
            int iMax = length - tLen;
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < iMax; ++i) {
                sb.append(c);
            }

            sb.append(srcString);
            return sb.toString();
        }
    }
    public static void main(String[] args) {
        String password = generate("21232f297a57a5a743894a0e4a801fc3");
        System.out.println(password);
    }
}

