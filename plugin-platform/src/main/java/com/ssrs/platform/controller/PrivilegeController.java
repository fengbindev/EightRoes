package com.ssrs.platform.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ssrs.framework.PrivilegeModel;
import com.ssrs.framework.User;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.bl.PrivBL;
import com.ssrs.platform.extend.MenuPrivService;
import com.ssrs.platform.model.Menu;
import com.ssrs.platform.priv.AbstractMenuPriv;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
@RestController
@RequestMapping("/api/privilege/id/{typeID}/type/{type}")
public class PrivilegeController extends BaseController {

    @Priv
    @GetMapping("/menus")
    public ApiResponses<JSONObject> get(@PathVariable String typeID, @PathVariable String type) {
        return success(getMenusPriv(typeID, type));
    }

    @Priv
    @PutMapping("/menus")
    public ApiResponses<String> update(@PathVariable String typeID, @PathVariable String type, @RequestParam String data) {
        String[] arr = StrUtil.split(data, ",");
        ArrayList<String> keys = CollUtil.toList(arr);
        PrivBL.setPriv(keys, typeID, type);
        return success("保存成功");
    }

    private JSONObject getMenusPriv(String _id, String _type) {
        final String id = _id;
        final String type = _type;
        List<Menu> allMenus = MenuPrivService.getAllMenus();
        final PrivilegeModel p = PrivBL.getCurrentPrivilege(type, id);
        boolean fullDisabled = !PrivBL.canSetPriv(type, User.getUserName());

        // 是否具备完全权限
        PrivilegeModel uncheckablePriv = PrivBL.getUncheckablePrivilege(type, id);
        boolean fullPrivFlag = PrivBL.getFullPrivFlag(type, id);
        allMenus = allMenus.stream().filter(menu -> p.hasPriv(menu.getId()) || PrivBL.isInBranchPrivRange(type, id, menu.getId())).collect(Collectors.toList());
        JSONObject ret = new JSONObject();
        // 所要选中的checkbox
        List<String> values = new ArrayList<>();
        JSONArray ja = new JSONArray();
        Map<String, JSONObject> map = MapUtil.newHashMap();
        for (Menu menu : allMenus) {
            JSONObject jo = new JSONObject();
            String menuID = menu.getId();
            AbstractMenuPriv ap = MenuPrivService.getInstance().get(menuID);
            if (ap == null) {
                continue;
            }
            jo.set("id", menuID);
            jo.set("parentId", menu.getParentId());
            jo.set("name", menu.getName());
            // 如果满足条件则是选中状态
            if (p.hasPriv(menuID) || fullPrivFlag || uncheckablePriv.hasPriv(menuID)) {
                values.add(menuID);
            }
            // 如果满足条件则是禁用状态
            if (fullPrivFlag || uncheckablePriv.hasPriv(menuID) || fullDisabled) {
                jo.set("disabled", true);
            } else {
                jo.set("disabled", false);
            }
            JSONArray jaItem = new JSONArray();
            jo.set("items", jaItem);
            ja.add(jo);
            map.put(menuID, jo);
            Dict items = ap.getPrivItems();
            for (String item : items.keySet()) {
                if (!p.hasPriv(item) && !PrivBL.isInBranchPrivRange(type, id, item)) {
                    continue;
                }
                JSONObject joItem = new JSONObject();
                joItem.set("id", item);
                joItem.set("parentId", menuID);
                joItem.set("name", items.get(item));
                if (p.hasPriv(item) || fullPrivFlag || uncheckablePriv.hasPriv(item)) {
                    values.add(item);
                }
                if (fullPrivFlag || uncheckablePriv.hasPriv(item) || fullDisabled) {
                    joItem.set("disabled", true);
                } else {
                    joItem.set("disabled", false);
                }
                jaItem.add(joItem);
            }
        }
        ret.set("value", values);
        ret.set("tree", ja);
        return ret;
    }

}
