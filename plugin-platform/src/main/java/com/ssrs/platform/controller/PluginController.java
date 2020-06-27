package com.ssrs.platform.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ssrs.framework.extend.ExtendActionConfig;
import com.ssrs.framework.extend.ExtendItemConfig;
import com.ssrs.framework.extend.ExtendPointConfig;
import com.ssrs.framework.extend.ExtendServiceConfig;
import com.ssrs.framework.extend.plugin.PluginConfig;
import com.ssrs.framework.extend.plugin.PluginManager;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author ssrs
 */
@RestController
@RequestMapping("/api/plugins")
public class PluginController extends BaseController {

    @Priv
    @GetMapping
    public ApiResponses<JSONArray> list() {
        JSONArray plugins = new JSONArray();
        ArrayList<PluginConfig> lists = PluginManager.getInstance().getAllPluginConfig();
        for (PluginConfig pc : lists) {
            JSONObject jo = new JSONObject();
            plugins.add(jo);
            jo.set("key", pc.getID());
            jo.set("title", pc.getName());
            jo.set("status", pc.isEnabled());
            jo.set("statusName", pc.isEnabled() ? "启用" : "停用");
            jo.set("version", pc.getVersion());
            jo.set("provider", pc.getProvider());
            jo.set("author", pc.getAuthor());
            jo.set("description", pc.getDescription());
        }
        JSONObject jo = new JSONObject();
        jo.set("key", "0");
        jo.set("icon", "file-ppt");
        jo.set("title", "插件列表");
        jo.set("children", plugins);

        JSONArray rest = new JSONArray();
        rest.add(jo);

        return success(rest);
    }

    @Priv
    @GetMapping("/{pluginId:.+}")
    public ApiResponses<JSONObject> get(@PathVariable String pluginId) {
        JSONObject jo = new JSONObject();
        JSONArray ja1 = new JSONArray();
        JSONArray ja2 = new JSONArray();
        JSONArray ja3 = new JSONArray();
        JSONArray ja4 = new JSONArray();
        PluginConfig pluginConfig = PluginManager.getInstance().getPluginConfig(pluginId);
        if (pluginConfig == null) {
            return failure("插件不存在");
        }
        // 扩展点
        Map<String, ExtendPointConfig> extendPoints = pluginConfig.getExtendPoints();
        extendPoints.forEach((id, extendPointConfig) -> {
            JSONObject jo1 = new JSONObject();
            jo1.set("id", id);
            jo1.set("desc", extendPointConfig.getDescription());
            ja1.add(jo1);
        });
        // 扩展行为
        Map<String, ExtendActionConfig> extendActions = pluginConfig.getExtendActions();
        extendActions.forEach((id, extendAction) -> {
            JSONObject jo2 = new JSONObject();
            jo2.set("id", id);
            jo2.set("desc", extendAction.getDescription());
            ja2.add(jo2);
        });
        // 扩展服务
        Map<String, ExtendServiceConfig> extendServices = pluginConfig.getExtendServices();
        extendServices.forEach((id, extendService) -> {
            JSONObject jo3 = new JSONObject();
            jo3.set("id", id);
            jo3.set("desc", extendService.getDescription());
            ja3.add(jo3);
        });
        // 扩展项
        Map<String, ExtendItemConfig> extendItems = pluginConfig.getExtendItems();
        extendItems.forEach((id, extendItem) -> {
            JSONObject jo4 = new JSONObject();
            jo4.set("id", id);
            jo4.set("desc", extendItem.getDescription());
            ja3.add(jo4);
        });
        jo.set("extendPoints", ja1);
        jo.set("extendActions", ja2);
        jo.set("extendServices", ja3);
        jo.set("extendItems", ja4);
        return success(jo);
    }
}
