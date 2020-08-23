package com.ssrs.elasticsearch.service;

import cn.hutool.core.io.IoUtil;
import com.google.gson.Gson;
import com.ssrs.elasticsearch.code.HttpMethod;
import com.ssrs.framework.extend.AbstractExtendService;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 索引创建服务
 *
 * @author steven
 */
public class IndexService extends AbstractExtendService<IIndex> {

    public static IndexService getInstance() {
        return (IndexService) findInstance(IndexService.class);
    }

    /**
     * 获取分类类型Map
     *
     * @return
     */
    public static Map<String, String> getTypeMap() {
        Map<String, String> map = new HashMap<>();
        List<IIndex> indexList = IndexService.getInstance().getAll();
        for (IIndex index : indexList) {
            map.put(index.getExtendItemID(), index.getTypeName());
        }
        return map;
    }

    public static Map<String, String> getTypeIDMap() {
        Map<String, String> map = new HashMap<String, String>();
        List<IIndex> indexList = IndexService.getInstance().getAll();
        for (IIndex index : indexList) {
            map.put(index.getExtendItemID(), index.getTypeName());
        }
        return map;
    }

    /**
     * 获取以逗号(,)分割的所有类型字符串
     *
     * @return
     */
    public static String getAllType() {
        Set<String> set = IndexService.getTypeMap().keySet();
        StringBuffer buffer = new StringBuffer();
        for (String type : set) {
            buffer.append(",");
            buffer.append(type);
        }
        return buffer.substring(1, buffer.length());
    }

    public static boolean delete(String indexID) {
        RestClient client = new ClientService().getClient();
        try {
            client.performRequest(HttpMethod.DELETE, "/" + indexID);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 添加别名
    public boolean addAlias() throws IOException {
        RestClient client = new ClientService().getClient();
        Gson gson = new Gson();
        Map<String, Object> aliasMap = new HashMap<String, Object>();
        Map<String, Object> actionsMap = new HashMap<String, Object>();
        Map<String, Object> addMap = new HashMap<String, Object>();
        addMap.put("index", IIndex.getIndexId());
        addMap.put("alias", IIndex.getIndexAlias());
        actionsMap.put("add", addMap);
        aliasMap.put("actions", actionsMap);
        String aliasJson = gson.toJson(aliasMap);
        HttpEntity entity = new NStringEntity(aliasJson, ContentType.APPLICATION_JSON);
        Response aliasResponse = client.performRequest(HttpMethod.POST, "/_aliases", Collections.<String, String>emptyMap(), entity);
        return aliasResponse.getStatusLine().getStatusCode() == 200;
    }

    public String getIndexIdByAlias(String alias) {
        String indexId = null;
        try {
            Response response = new ClientService().getClient().performRequest(HttpMethod.GET, "/" + alias + "/_mapping",
                    Collections.<String, String>emptyMap());
            HttpEntity httpEntity = response.getEntity();
            String jsonStr = "{}";
            if (httpEntity != null) {
                InputStream instreams = httpEntity.getContent();
                jsonStr = IoUtil.read(instreams, StandardCharsets.UTF_8);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> map = new Gson().fromJson(jsonStr, Map.class);
            if (map.size() > 0) {
                indexId = (String) map.keySet().toArray()[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexId;
    }

}
