package com.ssrs.elasticsearch.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ssrs.elasticsearch.model.entity.FieldWeight;
import com.ssrs.elasticsearch.search.query.BoolQueryBuilder;
import com.ssrs.elasticsearch.search.query.QueryBuilders;
import com.ssrs.elasticsearch.service.IFieldWeightService;
import com.ssrs.framework.util.SpringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class QueryHelper {
    public static long lastUpdateTime; // 上次更新字段权重时间
    public static int intervalHour = 1;// 更新间隔,小时
    public static Map<String, String> fieldWeights = new HashMap<>(); // 字段权重

    /**
     * 获取检索的fields及对应的权重数组,为空则返回除Title外所有权重字段
     */
    public static String[] getFields(String field) {
        updateFieldAndCode();
        if (StrUtil.isNotEmpty(field) && fieldWeights.containsKey(field)) {
            return new String[]{field + "^" + fieldWeights.get(field)};
        } else {
            String[] searchFields = new String[fieldWeights.size()];
            int i = 0;
            for (Entry<String, String> e : fieldWeights.entrySet()) {
                searchFields[i++] = e.getKey() + "^" + e.getValue();
            }
            return searchFields;
        }
    }

    /**
     * 更新状态字段和code
     */
    public static void updateFieldAndCode() {
        if (System.currentTimeMillis() - lastUpdateTime < intervalHour * 1000 * 60 * 60) {
            return;
        }
        synchronized (fieldWeights) {
            IFieldWeightService fieldWeightService = SpringUtil.getBean(IFieldWeightService.class);
            List<FieldWeight> fieldWeights = fieldWeightService.list();
            QueryHelper.fieldWeights = new HashMap<>();
            for (FieldWeight fieldWeight : fieldWeights) {
                QueryHelper.fieldWeights.put(fieldWeight.getField(), Convert.toStr(fieldWeight.getWeight()));
            }
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    /**
     * 添加执行时间
     */
    public static JSONObject addCostData(JSONObject json, String path, Object obj) {
        json.put(path, obj);
        return json;
    }


    public static void findCodeName(JSONArray codes, JSONArray relas) {
        if (codes.size() == 0 || relas.size() == 0) {
            return;
        }
        Map<String, String> rela = new HashMap<>();
        for (Object object : relas) {
            try {
                JSONObject json = JSONUtil.parseObj(object.toString());
                String key = json.getStr("key");
                String[] name = key.split("@");
                rela.put(name[0], name[1]);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        for (Object object : codes) {
            JSONObject json = (JSONObject) object;
            json.put("value", rela.get(json.getStr("key")));
        }
    }

    /**
     * @param boolQB 主体qb
     * @param data   参与检索的数据
     * @param field  参与检索的字段
     * @throws UnsupportedEncodingException
     * @Description: 添加分类检索
     */
    public static void makeShouldBuilders(BoolQueryBuilder boolQB, String data, String field) throws UnsupportedEncodingException {
        if (StrUtil.isEmpty(data)) {
            return;
        }
        String[] arr = data.split(",");
        BoolQueryBuilder shouldQuery = new BoolQueryBuilder();
        for (String s : arr) {
            shouldQuery.should(QueryBuilders.termQuery(field, URLDecoder.decode(s, StandardCharsets.UTF_8.name())));
        }
        boolQB.must(shouldQuery);
    }

}
