package com.ssrs.platform.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.config.AppDataPath;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.service.IUserService;
import com.ssrs.platform.util.IdenticonRenderer;
import com.ssrs.platform.util.NineBlockIdenticonRenderer2;
import com.ssrs.platform.util.PlatformCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedHashMap;

@Controller
public class AvatarHandler {
    private static final String IDENTICON_IMAGE_FORMAT = "png";
    static LinkedHashMap<String, String> mimeMap = new LinkedHashMap<String, String>();

    @Autowired
    private IUserService userService;

    static {
        mimeMap.put("gif", "image/gif");
        mimeMap.put("jpg", "image/jpeg");
        mimeMap.put("png", "image/png");
    }

    @Priv
    @GetMapping("/api/avatar/{userName}")
    public void handle(HttpServletRequest request, HttpServletResponse response, @PathVariable String userName)
            throws IOException, ServletException {
        if (StrUtil.isEmpty(userName)) {
            return;
        }
        int hashCode = Math.abs(userName.hashCode());

        String avatarPath = AppDataPath.getValue() + "avatar/";// 存放头像文件的位置
        String filename = avatarPath + userName;
        if (!new File(avatarPath).exists()) {
            new File(avatarPath).mkdir();
        }
        // TODO: 取缓存头像
        File f = null;
        for (String key : mimeMap.keySet()) {
            f = new File(filename + "." + key);
            if (f.exists()) {
                break;
            }
        }
        if (!f.exists()) {
            User user = PlatformCache.getUser(userName);
            if (ObjectUtil.isNull(user)) {// 对于不存在的用户，返回同一个头像，避免被攻击在服务器上生成大量头像
                userName = "0";
                hashCode = Math.abs(-272761807);
            }
            f = new File(avatarPath + userName + "." + IDENTICON_IMAGE_FORMAT);
            IdenticonRenderer renderer = new NineBlockIdenticonRenderer2();
            RenderedImage image = renderer.render(hashCode, 96);
            ImageIO.write(image, IDENTICON_IMAGE_FORMAT, f);
        }

        response.setHeader("Last-Modified", DateUtil.format(new Date(f.lastModified()), DatePattern.HTTP_DATETIME_FORMAT));
        String since = request.getHeader("If-Modified-Since");
        if (StrUtil.isNotEmpty(since)) {
            Date d = DateUtil.parse(since, DatePattern.HTTP_DATETIME_FORMAT);
            if (d != null && d.getTime() >= f.lastModified()) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;// 直接从缓存中获取
            }
        }
        String fileName = f.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (StrUtil.isNotEmpty(ext)) {
            String mimeType = mimeMap.get(ext);
            if (mimeType != null) {
                response.setHeader("Content-Type", mimeType);
            } else {
                response.setHeader("Content-Type", "image/" + ext);
            }

        }

        FileInputStream fis = new FileInputStream(f);
        OutputStream os = response.getOutputStream();
        int len = 0;
        long size = f.length();
        response.setHeader("Content-Length", "" + size);
        byte[] bs = new byte[1024 * 100];

        while ((len = fis.read(bs)) != -1) {
            os.write(bs, 0, len);
            response.flushBuffer();
        }

        fis.close();
        return;
    }

}
