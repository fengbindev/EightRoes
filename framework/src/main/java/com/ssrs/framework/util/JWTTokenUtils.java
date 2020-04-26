package com.ssrs.framework.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import com.ssrs.framework.User;
import com.ssrs.framework.web.ApiException;
import com.ssrs.framework.web.ErrorCodeEnum;
import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: JWT token 生成工具类
 * @Author: ssrs
 * @CreateDate: 2019/8/18 16:52
 * @UpdateUser: ssrs
 * @UpdateDate: 2019/8/18 16:52
 * @Version: 1.0
 */
public abstract class JWTTokenUtils {

    private static final String SECRET = "1s6U65P4bAay14bMDgHWgtqaTHNTZPZNMDJu3k";

    public static String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_PREFIX_COOKIE = "Bearer+";
    private static final String ISS = "echisan";
    public static final String APP_HEADER = "APP-";
    public static final String WEB_HEADER = "WEB-";
    private static final long EXPIRATION = 3600L;
    public static long WEB_EXPIRATION = EXPIRATION * 1000 * 5;
    public static long APP_EXPIRATION = EXPIRATION * 1000 * 24 * 7;
    /**
     * subject的参数
     **/
    public static final String ClientName = "client";
    public static final String TypeName = "type";
    public static final String UserName = "name";
    public static final String Signature = "signature";

    public static final String Access_Token = "access_token";
    public static final String Expires_In = "expires_in";

    public static final String DeviceInfo = "deviceinfo";
    public static final String IMEI = "IMEI";


    private static JSONObject createToken(String subject, Map<String, Object> claims, long expiration) {
        JSONObject jo = new JSONObject();
        try {
            String access_token = Jwts.builder().signWith(SignatureAlgorithm.HS512, SECRET).setIssuer(ISS).setSubject(subject).setClaims(claims)
                    .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiration)).setIssuedAt(new Date()).compact();
            jo.set(Access_Token, TOKEN_PREFIX + access_token);
            jo.set(Expires_In, expiration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jo;
    }

    /**
     * 创建web登录Token
     */
    public static JSONObject createWebToken(User.UserData userData) {
        Map<String, Object> claims = new HashMap<>(1);
        Map<String, Object> map = BeanUtil.beanToMap(userData);
        claims.putAll(map);
        claims.put("username", userData.getUserName());
        return createToken(userData.getUserName(), claims, WEB_EXPIRATION);
    }

    /**
     * 创建APP登录Token
     */
    public static JSONObject createAppToken(User.UserData userData) {
        Map<String, Object> claims = new HashMap<>(1);
        claims.putAll(userData);
        claims.put("username", userData.getUserName());
        return createToken(userData.getUserName(), claims, APP_EXPIRATION);
    }

    /**
     * 解析Claims
     *
     * @param token
     * @return
     */
    public static Claims getClaim(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new ApiException(ErrorCodeEnum.UNAUTHORIZED);
        }
        return claims;
    }

    /**
     * 获取jwt发布时间
     */
    public static Date getIssuedAtDate(String token) {
        return getClaim(token).getIssuedAt();
    }


    public static String getUserName(String token) {
        return Convert.toStr(getClaim(token).get("username"));
    }

    public static User.UserData getUserDate(String token) {
        return BeanUtil.toBean(getClaim(token), User.UserData.class);
    }

    /**
     * 获取jwt失效时间
     */
    public static Date getExpirationDate(String token) {
        return getClaim(token).getExpiration();
    }

    /**
     * 解析token是否正确,不正确会报异常<br>
     */
    public static void parseToken(String token) throws JwtException {
        Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

    /**
     * 验证token是否失效
     *
     * @param token
     * @return true:过期   false:没过期
     */
    public static boolean isExpired(String token) {
        try {
            Claims claims = null;
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            final Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException expiredJwtException) {
            return true;
        }
    }

}
