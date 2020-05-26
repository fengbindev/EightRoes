package com.ssrs.platform.controller;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.ssrs.framework.Current;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.platform.util.AuthCodeUtil;
import com.ssrs.platform.util.CookieUtil;
import com.ssrs.platform.util.ExpiringSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * 显示验证码图片的URL
 *
 */
@Controller
public class AuthCodeURLHandler {
	private static ExpiringSet<String> checkList = new ExpiringSet<String>(60 * 5, 60, true);

	public static final String DefaultAuthKey = "_EIGHTROES_AUTHCODE";
	public static final String WidthKey = "width";
	public static final String HeightKey = "height";

	@Priv(login = false)
	@GetMapping("/authCode.png")
	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		int width = AuthCodeUtil.DefaultWidth;
		int height = AuthCodeUtil.DefaultHeight;
		if (NumberUtil.isInteger(request.getParameter(WidthKey))) {
			width = Integer.valueOf(request.getParameter(WidthKey));
		}
		if (NumberUtil.isInteger(request.getParameter(HeightKey))) {
			height = Integer.valueOf(request.getParameter(HeightKey));
		}
        AuthCodeUtil.AuthCode image = AuthCodeUtil.getAuthCode(width,height,0);
		long time = System.currentTimeMillis();
		String key = SecureUtil.md5(time + image.getCode()) + "_" + time;
		response.addCookie(new Cookie(DefaultAuthKey, key));
		checkList.add(key);
		writeImage(response, image.getImage());
	}

	/**
	 * 校验验证码。注意：会从Current.getRequest()中读取Cookie信息,默认情况验证一次就失效
	 */
	public static boolean verify(String authCode) {
		return verify(authCode, true);
	}
	
	/**
	 * 校验验证码。注意：会从Current.getRequest()中读取Cookie信息,验收是否失效通过destroy参数决定
	 */
	public static boolean verify(String authCode, boolean destroy) {
		String key = CookieUtil.getCookieValue(Current.getRequest().getServletRequest(), DefaultAuthKey);
		if (ObjectUtil.isEmpty(authCode) || ObjectUtil.isEmpty(key) || key.indexOf('_') <= 0) {
			return false;
		}
        return Arrays.stream(StrUtil.split(key, ",")).anyMatch(s -> verify(authCode, s, destroy));
    }

    private static boolean verify(String authCode, String key, boolean destroy) {
        int i = key.indexOf('_');
        String time = key.substring(i + 1);
        long c = Long.parseLong(time);
        if (System.currentTimeMillis() - c > 15 * 60 * 1000) {// 15分钟有效
            return false;
        }
        String md5 = key.substring(0, i);
        authCode = SecureUtil.md5(time + authCode);
        boolean flag = authCode.equals(md5);
        if (flag && destroy) {
            if (!checkList.contains(key)) {
                return false;
            }
            checkList.remove(key);
        }
        return flag;
    }

	@Priv(login = false)
	@GetMapping("/api/authCode.png")
	public void uiAuthCode(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		this.handle(request, response);
	}


	private void writeImage(HttpServletResponse response, BufferedImage bi) {
		response.setHeader("Cache-Control", "private,no-cache,no-store");
		response.setContentType("image/png");
		try {
			ImageIO.write(bi, "png", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
