package com.ssrs.platform.controller.handler;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.framework.web.util.LongTimeTask;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集群模式下，这两个接口要单独配置Nginx反向代理，只能代理到某一个应用服务，不能配置负载均衡！！！
 *
 * @author ssrs
 */
@RestController
@RequestMapping("/api/longtimetasks")
public class LongTimeTaskHandler extends BaseController {

    @Priv(login = false)
    @GetMapping("/{id}")
    public ApiResponses<Map<String, Object>> get(@PathVariable long id) {
        LongTimeTask ltt = LongTimeTask.getInstanceById(id);
        boolean completeflag = false;
        boolean errorflag = false;
        Map<String, Object> map = new HashMap<>();
        if (ltt != null && ltt.isAlive()) {
            map.put("currentInfo", StrUtil.isNotEmpty(ltt.getCurrentInfo()) ? ltt.getCurrentInfo() + "..." : "");
            map.put("messages", ArrayUtil.join(ltt.getMessages(), ","));
            map.put("percent", ltt.getPercent());
        } else {
            String finishInfo = "任务已经执行完成！";
            completeflag = true;
            if (ltt != null) {
                List<String> errors = ltt.getAllErrors();
                if (errors != null && errors.size() > 0) {
                    map.put("errors", errors);
                    errorflag = true;
                } else {
                    finishInfo = StrUtil.isNotEmpty(ltt.getFinishedInfo()) ? ltt.getFinishedInfo() : finishInfo;
                    map.put("currentInfo", finishInfo);
                }
            } else {
                map.put("currentInfo", finishInfo);
            }
            LongTimeTask.removeInstanceById(id);
        }
        map.put("completeFlag", completeflag);
        map.put("errorFlag", errorflag);
        return success(map);
    }

    @Priv(login = false)
    @PutMapping("/stoped/{id}")
    public ApiResponses<String> stoped(@PathVariable long id) {
        LongTimeTask ltt = LongTimeTask.getInstanceById(id);
        if (ltt != null) {
            ltt.stopTask();
        }
        return success("执行成功");
    }
}
