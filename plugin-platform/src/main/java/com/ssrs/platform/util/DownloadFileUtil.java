package com.ssrs.platform.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author ssrs
 */
public class DownloadFileUtil {

    private static Log log = LogFactory.get(DownloadFileUtil.class);

    /**
     * 设置下载文件名
     *
     * @param request
     * @param response
     * @param fileName
     */
    public static void setDownloadFileName(HttpServletRequest request, HttpServletResponse response, String fileName) {
        try {
            response.resetBuffer();
            response.setContentType("application/octet-stream");
            String userAgent = request.getHeader("User-Agent");
            if (!StrUtil.isNotEmpty(userAgent) || userAgent.toLowerCase().indexOf("msie") < 0 && userAgent.toLowerCase().indexOf("trident") < 0) {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            } else {
                fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
            }

            response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    /**
     * 下载文件
     *
     * @param request
     * @param response
     * @param fileName
     * @param is
     */
    public static void download(HttpServletRequest request, HttpServletResponse response, String fileName, InputStream is) {
        try {
            setDownloadFileName(request, response, fileName);
            if (is == null) {
                return;
            }
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int read = -1;
            try {
                while ((read = is.read(buffer)) != -1) {
                    if (read > 0) {
                        byte[] chunk = null;
                        if (read == 1024) {
                            chunk = buffer;
                        } else {
                            chunk = new byte[read];
                            System.arraycopy(buffer, 0, chunk, 0, read);
                        }
                        os.write(chunk);
                        os.flush();
                    }
                }
            } finally {
                is.close();
            }
            os.flush();
            os.close();
        } catch (IOException e) {
            log.warn("IOUtil.download:IO ends by user!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
