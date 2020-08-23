package com.ssrs.elasticsearch.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.google.gson.Gson;
import com.ssrs.elasticsearch.annotation.Index;
import com.ssrs.elasticsearch.code.AnalyzerType;
import com.ssrs.elasticsearch.code.EsDataType;
import com.ssrs.elasticsearch.code.HttpMethod;
import com.ssrs.elasticsearch.config.AnalyzerConfig;
import com.ssrs.elasticsearch.config.MaxResultWindowConfig;
import com.ssrs.elasticsearch.config.NumberOfReplicasConfig;
import com.ssrs.elasticsearch.config.NumberOfShardsConfig;
import com.ssrs.elasticsearch.entity.BasicDoc;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappingService {

	private RestClient client = null;

	private String type;

	private String index;

	private String alias;

	private Map<String, Object> fieldsMap = null;

	public MappingService(RestClient client, Class<? extends BasicDoc> clz, String type) {
		this.client = client;
		this.index = IIndex.getIndexId();
		this.alias = IIndex.getIndexAlias();
		this.type = type;
		this.fieldsMap = mappingSettle(clz, index, type);
	}

	/**
	 * @throws IOException
	 */
	private void createIndex() throws IOException {
		// 判断索引是否存在
		try {
			client.performRequest(HttpMethod.GET, "/" + index, Collections.<String, String> emptyMap());
		} catch (ResponseException e) {
			Map<String, Object> indexMap = new HashMap<String, Object>();
			Map<String, Object> settingMap = new HashMap<String, Object>();
			settingMap.put("number_of_shards", NumberOfShardsConfig.getValue());
			settingMap.put("number_of_replicas", NumberOfReplicasConfig.getValue());
			settingMap.put("max_result_window", MaxResultWindowConfig.getValue());
			indexMap.put("settings", settingMap);
			String mappingJson = new Gson().toJson(indexMap);
			HttpEntity entity = new NStringEntity(mappingJson, ContentType.APPLICATION_JSON);
			client.performRequest(HttpMethod.PUT, "/" + index, Collections.<String, String> emptyMap(), entity);
			// 别名不存在时创建别名
			try {
				client.performRequest(HttpMethod.GET, "/" + alias, Collections.<String, String> emptyMap());
			} catch (IOException e1) {
				try {
					new IndexService().addAlias();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * 执行mapping创建
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean createMapping() throws IOException {
		createIndex();
		Gson gson = new Gson();
		String mappingJson = gson.toJson(fieldsMap);
		System.out.println(mappingJson);
		HttpEntity entity = new NStringEntity(mappingJson, ContentType.APPLICATION_JSON);
		Response indexResponse = client.performRequest(HttpMethod.POST, "/" + index + "/" + type + "/_mapping",
				Collections.<String, String> emptyMap(), entity);
		return indexResponse.getStatusLine().getStatusCode() == 200;
	}

	/**
	 * 构建maping
	 * 
	 * @param clz
	 * @param index
	 * @param type
	 * @return
	 */
	private Map<String, Object> mappingSettle(Class<? extends Object> clz, String index, String type) {
		Map<String, Object> typeMap = new HashMap<String, Object>();
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		Map<String, Object> fieldMap = new HashMap<String, Object>();
		Class<?> superClass = clz.getSuperclass();
		Field[] superClassFields = superClass.getDeclaredFields();
		Field[] currentFields = clz.getDeclaredFields();
		Field[] fields = ArrayUtil.addAll(superClassFields, currentFields);
		for (Field field : fields) {
			Index indexAnno = field.getAnnotation(Index.class);
			String analyzer = indexAnno.analyzer();
			if (AnalyzerType.CONFIG.equals(analyzer)) {
				analyzer = AnalyzerConfig.getValue();
			}
			String fieldType = indexAnno.type();
			boolean stored = indexAnno.stored();
			String fieldName = field.getName();
			Map<String, Object> fieldSchema = new HashMap<String, Object>();
			fieldSchema.put("type", fieldType);
			if (!EsDataType.OBJECT.equals(fieldType)) {
				fieldSchema.put("store", stored);
			}
			if (EsDataType.TEXT.equals(fieldType)) {
				fieldSchema.put("type", fieldType);
				if (null != analyzer && !"".equals(analyzer)) {
					fieldSchema.put("analyzer", analyzer);
					fieldSchema.put("search_analyzer", analyzer);
				}
			} else if (EsDataType.DATE.equals(fieldType)) {
				fieldSchema.put("format", "yyyy-MM-dd HH:mm:ss.S||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis");
			}
			fieldMap.put(fieldName, fieldSchema);
		}
		propertiesMap.put("properties", fieldMap);
		typeMap.put(type, propertiesMap);
		return typeMap;
	}
}
