package com.ssrs.platform.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.extend.ConfigService;
import com.ssrs.platform.model.entity.Config;
import com.ssrs.platform.service.IConfigService;
import com.ssrs.platform.util.PlatformUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author ssrs
 */
@RestController
@RequestMapping("/api/security")
public class SecurityController extends BaseController {
    @Autowired
    private IConfigService configService;
    public static ArrayList<String> securityParas = new ArrayList<>();

    static {
        // 是否开启三级等保
        securityParas.add("isOpenThreeSecurity");
        // 密码最小长度
        securityParas.add("passwordMinLength");
        // 密码最大长度
        securityParas.add("passwordMaxLength");
        // 密码字符要求
        securityParas.add("passwordCharacterSpecification");
        // 密码中不能包含的用户信息
        securityParas.add("notIncludeUserInfo");
        // 一天内密码错误次数超过最大重试次数锁定账号
        securityParas.add("specifyOverTimeLock");
        // 密码错误最大重试次数
        securityParas.add("maxLoginCount");
        // 超过密码错误最大重试次数处理方式
        securityParas.add("overLoginCountType");
        // 禁止登录时长
        securityParas.add("lockTime");
        // 指定密码过期时间
        securityParas.add("expiration");
        // 后台重置密码后下次登录是否强制修改密码
        securityParas.add("nextLoginUpdatePwd");
        // TODO 是否开启重复性检测
        securityParas.add("isOpenRecentlyCheck");
        // TODO 重复性检查记录数
        securityParas.add("repeatCount");
    }

    @Priv
    @GetMapping
    public ApiResponses<Map<String, Object>> list() {
        List<Config> configList = configService.list();
        Map<String, Object> map = new HashMap<String, Object>();
        for (Config config : configList) {
            if (ConfigService.getInstance().get(config.getCode()) == null) {
                if (securityParas.contains(config.getCode())) {
                    map.put(config.getCode(), config.getValue());
                }
            }
        }
        map.put("isOpenThreeSecurity", "Y".equals(Convert.toStr(map.get("isOpenThreeSecurity"))));
        map.put("isOpenRecentlyCheck", "Y".equals(Convert.toStr(map.get("isOpenRecentlyCheck"))));
        map.put("specifyOverTimeLock", "Y".equals(Convert.toStr(map.get("specifyOverTimeLock"))));
        map.put("nextLoginUpdatePwd", "Y".equals(Convert.toStr(map.get("nextLoginUpdatePwd"))));
        JSONArray notIncludeUserInfo = new JSONArray();
        if (map.containsKey("notIncludeUserInfo")) {
            String[] arr = StrUtil.split((String) map.get("notIncludeUserInfo"), ",");
            for (String string : arr) {
                notIncludeUserInfo.add(string);
            }
        }
        map.put("notIncludeUserInfo", notIncludeUserInfo);
        return success(map);
    }

    @Priv
    @PutMapping
    public ApiResponses<String> update(@RequestParam Map<String, Object> data) {
        JSONObject jo = JSONUtil.parseObj(data);
        for (Map.Entry<String, Object> entry : jo.entrySet()) {
            // 去掉从页面传过来的下拉框中的文本内容，只要下拉框的值
            if (entry.getKey().contains("_")) {
                continue;
            }
            String code = entry.getKey();
            if (code.equals("notIncludeUserInfo")) {
                JSONArray value = JSONUtil.parseArray(entry.getValue());
                entry.setValue(StrUtil.join(",", value));
            }
            if ("isOpenThreeSecurity".equals(code) || "isOpenRecentlyCheck".equals(code) || "specifyOverTimeLock".equals(code)
                    || "nextLoginUpdatePwd".equals(code)) {
                entry.setValue( Convert.toBool(entry.getValue()) ? "Y" : "N");
            }
            Config config = configService.getById(code);
            if (config != null) {
                if (StrUtil.isEmpty(entry.getValue().toString().trim())) {
                    com.ssrs.framework.Config.removeValue(entry.getKey());
                    configService.removeById(config.getCode());
                } else {
                    config.setValue(entry.getValue().toString().trim());
                    config.setName(entry.getKey());
                    configService.updateById(config);
                }
            } else {
                if (StrUtil.isEmpty(entry.getValue().toString().trim())) {
                    continue;
                } else {
                    config = new Config();
                    config.setCode(entry.getKey());
                    config.setName(entry.getKey());
                    config.setValue(entry.getValue().toString().trim());
                    configService.save(config);
                }
            }
        }
        PlatformUtil.loadDBConfig();
        return success("操作成功");
    }

}
