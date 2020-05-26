package com.ssrs.platform.util;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.platform.exception.AuthCodeException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 验证码公用类
 *
 */
public class AuthCodeUtil {
    private static final Log log = LogFactory.get(AuthCodeUtil.class);
    public static final int DefaultExpireIn = 30 * 60;
    private static final char[] DEFAULT_CHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p', 'r', 'w', 'x', 'y', '2', '3',
        '4', '5', '6', '7', '8'};
    private static final int CHARS_LENG = DEFAULT_CHARS.length - 1;
    private static final int MaxWidth = 300;
    private static final int MaxHeight = 105;
    private static final Random random = new SecureRandom();
    public static final int DefaultWidth = 80;
    public static final int DefaultHeight = 28;

    public static final String IMAGE_CODE_KEY = "image";
    public static final String IMAGE_VERIFY_CODE_KEY = "authCode";
    public static final String AUTH_CODE_ENCRYPT = "encryptCode";
    public static final String AUTH_CODE_EXPIRE_TIME = "expireTime";

    public static AuthCode getAuthCode(int width, int height, int expireIn) {
        if (width > MaxWidth) {
            width = MaxWidth;
        }
        if (height > MaxHeight) {
            height = MaxHeight;
        }
        // 计算字体大小
        int fontSize = width / 3;
        // 计算验证码位置
        int wordPlace = (height / 2 + fontSize / 3);
        // 创建缓存图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        // 设置背景色
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, width, height);
        // 设置字体
        g.setFont(new Font("Serif", Font.ITALIC, fontSize));
        // 随机产生4条干扰线
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(20 + rand(110), 20 + rand(110), 20 + rand(110)));
            int x = rand(width);
            int y = rand(height);
            int xl = rand(width);
            int yl = rand(height);
            g.drawLine(x, y, x + xl, y + yl);
        }
        // 取验证码（5位）
        int randLen = 5;
        StringBuffer sRand = new StringBuffer(randLen);
        for (int i = 0; i < randLen; i++) {
            String rand = String.valueOf(DEFAULT_CHARS[rand(CHARS_LENG)]);
            sRand.append(rand);
            g.setColor(new Color(20 + rand(110), 20 + rand(110), 20 + rand(110)));
            g.drawString(rand, fontSize * i / 2 + fontSize / 4, wordPlace);
        }
        g.dispose();
        return new AuthCode(image, sRand.toString(), expireIn);
    }

    private static int rand(int max) {
        return random.nextInt(max + 1);
    }

    public static AuthCode getAuthCode() {
        return getAuthCode(DefaultWidth, DefaultHeight, DefaultExpireIn);
    }

    public static Map<String, Object> generate(){
        Map<String, Object> result = new HashMap<>();
        try {
            AuthCodeUtil.AuthCode authCode = AuthCodeUtil.getAuthCode();
            long expireTimestamp = authCode.getExpireIn().toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ImageIO.write(authCode.getImage(), "PNG", bs);
            String image = Base64.encode(bs.toByteArray());
            String encryptCode = SecureUtil.aes().encryptHex(authCode.getCode() + expireTimestamp);
            result.put(IMAGE_CODE_KEY, image);
            result.put(AUTH_CODE_ENCRYPT, encryptCode);
            result.put(AUTH_CODE_EXPIRE_TIME, expireTimestamp);
        }catch (Exception e){
            log.error("生成验证码失败");
        }
        return result;
    }

    public static void validate(String authCodeEncrypt, String authCodeExpireTimeStr, String inputKey) {
        if (StrUtil.isEmpty(authCodeEncrypt) || StrUtil.isEmpty(authCodeExpireTimeStr) || !NumberUtil.isLong(authCodeExpireTimeStr)) {
            throw new AuthCodeException("请输入验证码");
        }
        long authCodeExpireTime = Long.parseLong(authCodeExpireTimeStr);
        String cacheedCode = null;
        long cachedTimeStamp = 0;
        try {
            String decrypted = SecureUtil.aes().decryptStr(authCodeEncrypt);
            cacheedCode = decrypted.substring(0, 5);
            cachedTimeStamp = Long.parseLong(decrypted.substring(5));
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthCodeException("验证码错误，请重新输入");
        }
        if (cachedTimeStamp != authCodeExpireTime) {
            throw new AuthCodeException("验证码错误，请重新输入");
        }
        if (authCodeExpireTime < System.currentTimeMillis()) {
            throw new AuthCodeException("验证码超时，请刷新重试");
        }
        if (StrUtil.isEmpty(inputKey)) {
            throw new AuthCodeException("请输入验证码重试");
        }
        if (!cacheedCode.equalsIgnoreCase(inputKey)) {
            throw new AuthCodeException("验证码错误，请重新输入");
        }
    }

    public static void validate(HttpServletRequest request) {
        String authCodeEncrypt = request.getParameter(AUTH_CODE_ENCRYPT);
        String authCodeExpireTimeStr = request.getParameter(AUTH_CODE_EXPIRE_TIME);
        String inputKey = request.getParameter(IMAGE_VERIFY_CODE_KEY);
        validate(authCodeEncrypt, authCodeExpireTimeStr, inputKey);
    }

    /**
     * 验证码对象
     */
    public static class AuthCode {
        BufferedImage image;
        String code;
        LocalDateTime expireIn;

        AuthCode(BufferedImage image, String code, int expireIn) {
            this.image = image;
            this.code = code;
            this.expireIn = LocalDateTime.now().plusSeconds(expireIn);
        }

        public LocalDateTime getExpireIn() {
            return expireIn;
        }

        public void setExpireIn(LocalDateTime expireIn) {
            this.expireIn = expireIn;
        }

        public BufferedImage getImage() {
            return image;
        }

        public void setImage(BufferedImage image) {
            this.image = image;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
