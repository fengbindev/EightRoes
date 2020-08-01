package com.ssrs.platform.controller;


import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.extend.item.OperateLog;
import com.ssrs.platform.extend.item.SqlLog;
import com.ssrs.platform.extend.item.UserLoginLog;
import com.ssrs.platform.service.IOperateLogService;
import com.ssrs.platform.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
        Page page  = operateLogService.getLogByType(UserLoginLog.ID, params);
        return success(page);
    }

    @Priv
    @GetMapping("/useroperatelog")
    public ApiResponses<Page>  getUserOperateLog(@RequestParam Map<String, Object> params) {
        Page page  = operateLogService.getLogByType(OperateLog.ID, params);
        return success(page);
    }

    @Priv
    @GetMapping("/sqllog")
    public ApiResponses<Page>  getSqlLog(@RequestParam Map<String, Object> params) {
        Page page  = operateLogService.getLogByType(SqlLog.ID, params);
        return success(page);
    }

}
