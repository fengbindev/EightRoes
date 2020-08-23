package com.ssrs.elasticsearch.service;

import cn.hutool.core.io.IoUtil;
import com.google.gson.Gson;
import com.ssrs.elasticsearch.code.HttpMethod;
import lombok.extern.slf4j.Slf4j;
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
 * 查询服务
 */
@Slf4j
public class SearchService {

	@SuppressWarnings("unchecked")
	public String doSearch(String index, String type, String query) throws IOException {
		Gson gson = new Gson();
		Map<String, Object> responseMap = doSourceSearch(index, type, query);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("total", ((Map<String, Object>) responseMap.get("hits")).get("total"));
		List<Object> hits = (List<Object>) ((Map<String, Object>) responseMap.get("hits")).get("hits");
		List<Object> resultHitList = new ArrayList<Object>();
		for (Object obj : hits) {
			Map<String, Object> hit = (Map<String, Object>) obj;
			Map<String, Object> resultHit = new HashMap<String, Object>();
			Map<String, Object> source = (Map<String, Object>) hit.get("_source");
			Map<String, Object> highlight = (Map<String, Object>) hit.get("highlight");
			resultHit.put("id", hit.get("_id"));
			resultHit.put("type", hit.get("_type"));
			resultHit.put("source", source);
			resultHit.put("highlight", highlight);
			resultHitList.add(resultHit);
		}
		Map<String, Object> aggsMap = new LinkedHashMap<String, Object>();

		if (responseMap.containsKey("aggregations")) {
			Map<String, Object> aggregations = (Map<String, Object>) responseMap.get("aggregations");
			for (String key : aggregations.keySet()) {
				String aggName = key.substring(5);
				List<Object> buckets = (List<Object>) ((Map<String, Object>) aggregations.get(key)).get("buckets");
				if (buckets != null) {
					aggsMap.put(aggName, buckets);
				} else {
					aggsMap.put(aggName, aggregations.get(key));
				}
			}
		}
		resultMap.put("hits", resultHitList);
		resultMap.put("aggs", aggsMap);
		return gson.toJson(resultMap);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> doSourceSearch(String index, String type, String query) throws IOException {
		log.debug("Search Param: " + query);
		RestClient client = new ClientService().getClient();
		Gson gson = new Gson();
		HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
		Response indexResponse = client.performRequest(HttpMethod.POST, "/" + index + "/" + type + "/_search?pretty",
				Collections.<String, String>emptyMap(), entity);
		HttpEntity httpEntity = indexResponse.getEntity();
		String response = "{}";
		if (httpEntity != null) {
			InputStream instreams = httpEntity.getContent();
			response = IoUtil.read(instreams, StandardCharsets.UTF_8);
		}
		Map<String, Object> responseMap = gson.fromJson(response, Map.class);
		client.close();
		return responseMap;
	}

	@SuppressWarnings("unchecked")
	public String doSearchYear(String index, String query) throws IOException {
		Gson gson = new Gson();
		Map<String, Object> responseMap = doYearSearch(index, query);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("total", ((Map<String, Object>) responseMap.get("hits")).get("total"));
		List<Object> hits = (List<Object>) ((Map<String, Object>) responseMap.get("hits")).get("hits");
		List<Object> resultHitList = new ArrayList<Object>();
		for (Object obj : hits) {
			Map<String, Object> hit = (Map<String, Object>) obj;
			Map<String, Object> resultHit = new HashMap<String, Object>();
			Map<String, Object> source = (Map<String, Object>) hit.get("_source");
			Map<String, Object> highlight = (Map<String, Object>) hit.get("highlight");
			resultHit.put("id", hit.get("_id"));
			resultHit.put("type", hit.get("_type"));
			resultHit.put("source", source);
			resultHit.put("highlight", highlight);
			resultHitList.add(resultHit);
		}
		Map<String, Object> aggsMap = new LinkedHashMap<String, Object>();

		if (responseMap.containsKey("aggregations")) {
			Map<String, Object> aggregations = (Map<String, Object>) responseMap.get("aggregations");
			for (String key : aggregations.keySet()) {
				String aggName = key.substring(5);
				List<Object> buckets = (List<Object>) ((Map<String, Object>) aggregations.get(key)).get("buckets");
				if (buckets != null) {
					aggsMap.put(aggName, buckets);
				} else {
					aggsMap.put(aggName, aggregations.get(key));
				}
			}
		}
		resultMap.put("hits", resultHitList);
		resultMap.put("aggs", aggsMap);
		return gson.toJson(resultMap);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> doYearSearch(String index, String query) throws IOException {
		log.debug("Search Param: " + query);
		RestClient client = new ClientService().getClient();
		Gson gson = new Gson();
		HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
		Response indexResponse = client.performRequest(HttpMethod.POST, "/" + index + "/_search?pretty",
				Collections.<String, String>emptyMap(), entity);
		HttpEntity httpEntity = indexResponse.getEntity();
		String response = "{}";
		if (httpEntity != null) {
			InputStream instreams = httpEntity.getContent();
			response = IoUtil.read(instreams, StandardCharsets.UTF_8);
		}
		Map<String, Object> responseMap = gson.fromJson(response, Map.class);
		client.close();
		return responseMap;
	}

	/**
	 * @Description 创建或更新索引，索引不存在则创建，索引已存在则更新
	 * @param type   类型
	 * @param dateId 数据ID
	 * @param json
	 * @return
	 */
	public boolean createOrUpdate(String type, String dateId, String json) {
		boolean result = false;
		RestClient client = new ClientService().getClient();
		HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
		try {
			Response indexResponse = client.performRequest(HttpMethod.POST,
					"/" + IIndex.getIndexAlias() + "/" + type + "/" + dateId + "?refresh", Collections.<String, String>emptyMap(),
					entity);
			result = indexResponse.getStatusLine().getStatusCode() == 201
					|| indexResponse.getStatusLine().getStatusCode() == 200;
		} catch (IOException e) {
			log.debug("索引创建或更新失败");
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * @Description 删除索引
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public boolean delete(String type, String id) {
		boolean result = false;
		RestClient client = new ClientService().getClient();
		try {
			String query = "{\r\n" + 
					"  \"query\": {\r\n" + 
					"    \"bool\": {\r\n" + 
					"      \"must\": [\r\n" + 
					"        {\r\n" + 
					"          \"match\": {\r\n" + 
					"            \"_type\": \"" + type + "\"\r\n" + 
					"          }\r\n" + 
					"        },\r\n" + 
					"        {\r\n" + 
					"          \"match\": {\r\n" + 
					"            \"_id\": \""+id+"\"\r\n" + 
					"          }\r\n" + 
					"        }\r\n" + 
					"      ]\r\n" + 
					"    }\r\n" + 
					"  }\r\n" + 
					"}";
			HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
			Response indexResponse = client.performRequest(HttpMethod.POST,
					"/" + IIndex.getIndexAlias() + "/_delete_by_query?refresh", Collections.<String, String>emptyMap(),
					entity);
			result = indexResponse.getStatusLine().getStatusCode() == 200;
		} catch (IOException e) {
			log.debug("删除索引失败");
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
