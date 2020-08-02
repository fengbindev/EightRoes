package com.ssrs.platform.controller;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.config.AppDataPath;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.extend.item.OperateLog;
import com.ssrs.platform.extend.item.SqlLog;
import com.ssrs.platform.extend.item.UserLoginLog;
import com.ssrs.platform.service.IOperateLogService;
import com.ssrs.platform.util.DownloadFileUtil;
import com.ssrs.platform.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ssrs
 * @since 2020-07-25
 */
@RestController
@RequestMapping("/api/operateLog")
public class LogController extends BaseController {

    @Autowired
    private IOperateLogService operateLogService;

    @Priv
    @GetMapping("/userloginlog")
    public ApiResponses<Page> getUserLoginLog(@RequestParam Map<String, Object> params) {
        Page page = operateLogService.getLogByType(UserLoginLog.ID, params);
        return success(page);
    }

    @Priv
    @GetMapping("/useroperatelog")
    public ApiResponses<Page> getUserOperateLog(@RequestParam Map<String, Object> params) {
        Page page = operateLogService.getLogByType(OperateLog.ID, params);
        return success(page);
    }

    @Priv
    @GetMapping("/sqllog")
    public ApiResponses<Page> getSqlLog(@RequestParam Map<String, Object> params) {
        Page page = operateLogService.getLogByType(SqlLog.ID, params);
        return success(page);
    }

    @Priv
    @GetMapping("/logfiles")
    public ApiResponses<Page> logFile(@RequestParam Map<String, Object> params) {
        int pageNo = Convert.toInt(params.get("pageNo"), 1);
        int pageSize = Convert.toInt(params.get("pageSize"), 10);
        Page page = new Page();
        File logsDir = new File(AppDataPath.getValue() + "logs/");
        FileUtil.mkdir(logsDir.getAbsolutePath());
        final String fileName = Convert.toStr(params.get("fileName"), "");
        final long startTime = Convert.toLong(params.get("startTime"), 0L);
        final long endTime = Convert.toLong(params.get("endTime"), 0L);
        File[] files = logsDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                if (name.endsWith(".svn") || name.endsWith(".temp") || name.endsWith(".deploy") || name.endsWith(".deploytemp")) {
                    return false;
                }
                if (StrUtil.isNotEmpty(fileName)) {
                    if (name.indexOf(fileName) < 0) {
                        return false;
                    }
                }
                if (startTime > 0) {
                    if (file.lastModified() < new Date(startTime).getTime()) {
                        return false;
                    }
                }
                if (endTime > 0) {
                    if (file.lastModified() > new Date(endTime).getTime()) {
                        return false;
                    }
                }
                return true;
            }
        });
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o2.lastModified() - o1.lastModified() > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        int i = 0;
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        page.setTotalCount(files.length);
        int start = (pageNo - 1) * pageSize;
        int end = pageNo * pageSize;
        List<Map<String, Object>> list = new ArrayList<>();
        for (File f : files) {
            i++;
            if (i <= start) {
                continue;
            }
            if (i > end) {
                break;
            }

            Date d = new Date(f.lastModified());
            long length = f.length() / 1024;
            if (f.length() % 1024 != 0) {
                length++;
            }
            DecimalFormat df = new DecimalFormat("#,###");
            String size = df.format(length) + " KB";
            Map<String, Object> map = new HashMap<>();
            map.put("fileName", f.getName());
            map.put("fileSize", size);
            map.put("lastModifyTime", DateUtil.formatDateTime(d));
            list.add(map);
        }
        page.setData(list);
        return success(page);
    }

    @Priv
    @GetMapping("/download")
    public ApiResponses<String> downloadLogFiles(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String fileName = httpServletRequest.getParameter("fileName");
        String filePath = AppDataPath.getValue() + "logs/" + fileName;
        filePath = FileUtil.normalize(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            DownloadFileUtil.download(httpServletRequest, httpServletResponse, fileName, fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return failure("File not found: " + fileName);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return null;
    }
}
