package com.ssrs.elasticsearch.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ssrs.elasticsearch.code.HttpMethod;
import com.ssrs.elasticsearch.entity.BasicDoc;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 索引批处理服务
 */
@Slf4j
public class BulkService {

    ClientService clientService = new ClientService();

    private RestClient client = null;

    private String type;

    private String operator;

    private String index;

    private StringBuilder buffer = new StringBuilder();

    public static final String OP_INDEX = "index";

    public static final String OP_CREATE = "create";

    public static final String OP_UPDATE = "update";

    public static final String OP_DELETE = "delete";

    Map<String, Map<String, String>> meta = new HashMap<>();

    private long cellCount;

    /**
     * @param clz      索引描述类
     * @param operator 操作方式 OP_INDEX、OP_CREATE、OP_UPDATE、OP_DELETE
     * @param type     类型名称
     * @Description: bulk 批量操作， 初始化时需要指定操作类型、index 和 type
     */
    public BulkService(Class<? extends BasicDoc> clz, String operator, String type) {
        this.client = clientService.getClient();
        this.operator = operator;
        this.type = type;
        this.index = IIndex.getIndexId();
        try {
            // 查看索引库是否已经存在
            Response response = client.performRequest(HttpMethod.GET, "/" + index, Collections.<String, String>emptyMap());
            if (operator.equals(OP_DELETE)) { // 删除操作跳过创建mapping步骤
                return;
            }
            // 判断type对应的mapping是否创建
            JSONObject json = JSONUtil.parseObj(EntityUtils.toString(response.getEntity()));
            JSONObject mappings = json.getJSONObject(index).getJSONObject("mappings").getJSONObject(type);
            if (ObjectUtil.isEmpty(mappings)) {
                new MappingService(client, clz, type).createMapping();
            }
        } catch (Exception e) {
            try {
                new MappingService(client, clz, type).createMapping();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * 可用于 create、 update、index 批量操作
     *
     * @param t   操作对象
     * @param id  document id
     * @param <T>
     * @throws IOException
     */
    public <T> void addCell(T t, long id) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String cell = gson.toJson(t);
        Map<String, String> cellMeta = new HashMap<>();
        cellMeta.put("_index", index);
        cellMeta.put("_type", type);
        cellMeta.put("_id", String.valueOf(id));
        meta.put(operator, cellMeta);
        String metaJson = gson.toJson(meta);
        buffer.append(metaJson);
        buffer.append("\n");
        buffer.append(cell);
        buffer.append("\n");
        cellCount++;
    }

    /**
     * 只用于删除操作。
     *
     * @param id 要删除的document id
     * @throws IOException
     */
    public void addCell(long id) {
        Gson gson = new Gson();
        Map<String, String> cellMeta = new HashMap<>();
        cellMeta.put("_index", index);
        cellMeta.put("_type", type);
        cellMeta.put("_id", String.valueOf(id));
        meta.put(OP_DELETE, cellMeta);
        String metaJson = gson.toJson(meta);
        buffer.append(metaJson);
        buffer.append("\n");
        cellCount++;
    }

    public boolean bulk() throws IOException {
        if (cellCount <= 0) {
            return false;
        }
        HttpEntity entity = new NStringEntity(buffer.toString(), ContentType.APPLICATION_JSON);
        Response indexResponse = client.performRequest(HttpMethod.PUT, "/_bulk", Collections.<String, String>emptyMap(),
                entity);
        HttpEntity httpEntity = indexResponse.getEntity();
        if (httpEntity != null) {
            InputStream instreams = httpEntity.getContent();
            String str = IoUtil.read(instreams, StandardCharsets.UTF_8);
            log.debug("Response: " + str);
        }
        buffer = new StringBuilder();
        return indexResponse.getStatusLine().getStatusCode() == 200;
    }

    public void close() {
        try {
            client.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        } // 执行结束，关闭client
    }

    @Override
    protected void finalize() throws Throwable {
        if (client != null) {
            close();
            log.debug("client closed by gc !");
        }
    }

}
