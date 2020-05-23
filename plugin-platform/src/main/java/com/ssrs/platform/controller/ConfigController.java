package com.ssrs.platform.controller;


import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ssrs.framework.Config;
import com.ssrs.framework.extend.ExtendItemConfig;
import com.ssrs.framework.extend.plugin.PluginConfig;
import com.ssrs.framework.extend.plugin.PluginManager;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.FixedConfigItem;
import com.ssrs.platform.extend.ConfigService;
import com.ssrs.platform.priv.ConfigManagerPriv;
import com.ssrs.platform.service.IConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ssrs
 * @since 2020-02-25
 */
@RestController
@RequestMapping("/api/configs")
public class ConfigController extends BaseController {

    @Autowired
    private IConfigService configService;

    @Priv(ConfigManagerPriv.Save)
    @PutMapping("/saved")
    public ApiResponses<String> saved(String data) {
        JSONArray plugins = JSONUtil.parseArray(data);
        Dict dict = new Dict();
        for (Object o : plugins) {
            JSONObject jo = (JSONObject) o;
            for (Object p : jo.getJSONArray("configs")) {
                JSONObject jp = (JSONObject) p;
                dict.put(jp.getStr("id"), jp.getStr("value"));
            }
        }
        configService.saveConfig(dict);
        return success("保存成功");
    }

    @Priv(ConfigManagerPriv.MenuID)
    @GetMapping
    public ApiResponses<JSONArray> list() {
        // 获取所有配置项
        List<FixedConfigItem> items = ConfigService.getInstance().getAll();
        // 获取所有插件
        List<PluginConfig> plugins = PluginManager.getInstance().getAllPluginConfig();
        Map<String, JSONArray> itemConfigMap = new HashMap<>();
        // 按插件顺序输出
        JSONArray ja = new JSONArray();
        for (PluginConfig pc : plugins) {
            JSONObject plugin = new JSONObject();
            plugin.set("id", pc.getID());
            plugin.set("name", pc.getName());
            ja.add(plugin);
            JSONArray configs = new JSONArray();
            plugin.set("configs", configs);
            for (ExtendItemConfig eif : pc.getExtendItems().values()) {
                itemConfigMap.put(eif.getClassName(), configs);
            }
        }
        for (FixedConfigItem fc : items) {
            JSONArray configs = itemConfigMap.get(fc.getClass().getName());
            if (configs != null) {
                JSONObject config = new JSONObject();
                configs.add(config);
                config.set("id", fc.getExtendItemID());
                config.set("name", fc.getExtendItemName());
                config.set("dataType", fc.getDataType());
                config.set("controlType", fc.getControlType());
                config.set("value", Config.getValue(fc.getExtendItemID()));
                if (fc.getOptions() != null && fc.getOptions().size() > 0) {
                    config.set("options", fc.getOptions());
                }
            }
        }
        for (int i = 0; i < ja.size(); i++) {
            JSONObject o = ja.getJSONObject(i);
            if (o.getJSONArray("configs").size() == 0) {
                ja.remove(i);
                i--;
            }
        }
        return success(ja);
    }

}
