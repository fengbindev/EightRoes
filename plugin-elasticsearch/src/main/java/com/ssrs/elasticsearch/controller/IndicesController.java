package com.ssrs.elasticsearch.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.ssrs.elasticsearch.code.HttpMethod;
import com.ssrs.elasticsearch.code.OrderType;
import com.ssrs.elasticsearch.config.EsServerConfig;
import com.ssrs.elasticsearch.model.form.IndexPageForm;
import com.ssrs.elasticsearch.model.vo.IndexList2Vo;
import com.ssrs.elasticsearch.model.vo.IndexListVo;
import com.ssrs.elasticsearch.priv.IndicesLibraryManagerPriv;
import com.ssrs.elasticsearch.priv.IndicesManagerPriv;
import com.ssrs.elasticsearch.search.Searcher;
import com.ssrs.elasticsearch.search.query.BoolQueryBuilder;
import com.ssrs.elasticsearch.search.query.QueryBuilder;
import com.ssrs.elasticsearch.search.query.QueryBuilders;
import com.ssrs.elasticsearch.service.ClientService;
import com.ssrs.elasticsearch.service.IIndex;
import com.ssrs.elasticsearch.service.IndexService;
import com.ssrs.elasticsearch.service.SearchService;
import com.ssrs.elasticsearch.task.IndexRebuildTask;
import com.ssrs.elasticsearch.util.IndexTypeLock;
import com.ssrs.framework.config.AppDataPath;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.util.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author ssrs
 */
@RestController
@RequestMapping("/api/indices")
@Slf4j
public class IndicesController extends BaseController {

    /**
     * 获取所有索引类型
     *
     * @return
     */
    @Priv
    @GetMapping("/types")
    public ApiResponses<Map<String, String>> typeMap() {
        Map<String, String> map = IndexService.getTypeMap();
        return success(map);
    }

    /**
     * @param id   索引id
     * @param type 索引类型
     * @return
     * @Description: 获取一条索引的详细信息
     */
    @Priv
    @GetMapping("/{id}/{type}")
    public ApiResponses<String> init(@PathVariable String id, @PathVariable String type) {
        RestClient client = new ClientService().getClient();
        Response indexResponse = null;
        try {
            indexResponse = client.performRequest(HttpMethod.GET,
                    "/" + IIndex.getIndexId() + "/" + type + "/" + id + "?pretty",
                    Collections.<String, String>emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = indexResponse.getEntity();
        String response = "{}";
        if (httpEntity != null) {
            InputStream instreams = null;
            try {
                instreams = httpEntity.getContent();
                client.close();
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            response = IoUtil.read(instreams, StandardCharsets.UTF_8);
        }
        return success("查询成功", response);
    }

    /**
     * @param type 索引类型
     * @return
     * @Description: 获取索引列表
     */
    @SuppressWarnings("unchecked")
    @Priv
    @GetMapping("/indexList/{type}")
    public ApiResponses<Page> indexList(@PathVariable String type, @Validated IndexPageForm indexPageForm) {
        Page page = new Page();
        List<IndexListVo> data = new ArrayList<>();
        String searchKey = indexPageForm.getSearchKey();
        Map<String, String> typeMap = IndexService.getTypeMap();
        int pageIndex = indexPageForm.getPageNo();
        int pageSize = indexPageForm.getPageSize();
        if (pageSize == 0) {
            pageSize = 10;
        }
        Searcher searcher = new Searcher().from(pageIndex * pageSize).size(pageSize);// 分页
        searcher.addSource("title");
        searcher.addSource("summary");
        searcher.highlight(new Searcher.Highlight()
                .addField(new Searcher.Field("title"))
                .addField(new Searcher.Field("summary").setFragmentSize(40).setNumberOfFragments(4))); // 高亮
        searcher.addSort(new Searcher.Sort("id", OrderType.ASC));
        QueryBuilder qb = new BoolQueryBuilder().should(QueryBuilders.matchAll());
        if (StrUtil.isNotEmpty(searchKey)) {
            qb = new BoolQueryBuilder()
                    .must(QueryBuilders.multiMatchQuery(searchKey, new String[]{"title", "summary"}, "or", "100%"));
        }
        searcher.setQuery(qb.build());
        IndexListVo item = new IndexListVo();
        Map<String, Object> result = null;
        if (StrUtil.isEmpty(type) || "all".equals(type)) {
            type = IndexService.getAllType();
        }
        if (StrUtil.isEmpty(type)) {
            return success(page);
        }
        try {
            result = new SearchService().doSourceSearch(IIndex.getIndexId(), type, searcher.toString());
        } catch (IOException e) {
            log.error("elasticsearch查询错误");
            e.printStackTrace();
            return success(page);
        }
        double total = ((Map<String, Double>) result.get("hits")).get("total");
        List<Object> hits = (List<Object>) ((Map<String, Object>) result.get("hits")).get("hits");
        for (Object object : hits) {
            Map<String, Object> hit = (Map<String, Object>) object;
            String ID = (String) hit.get("_id");
            String fileType = (String) hit.get("_type");
            Map<String, Object> source = (Map<String, Object>) (hit.get("_source"));
            Map<String, Object> highlight = (Map<String, Object>) (hit.get("highlight"));
            String title = (String) source.get("title");
            String summary = null;
            if (null != highlight && highlight.containsKey("summary")) {
                summary = ((List<String>) highlight.get("summary")).get(0);
            } else {
                summary = (String) source.get("summary");
            }
            item.setId(ID);
            item.setType(typeMap.get(fileType));
            item.setTitle(title);
            item.setSummary(summary);
            data.add(item);
        }

        page.setTotalCount((int) total);
        return success(page);
    }

    /**
     * @param type 索引类型
     * @Description: 索引重建
     */
    @Priv(IndicesManagerPriv.INDEX_REBUILD)
    @GetMapping("/reIndex/{type}")
    public ApiResponses<String> reIndex(@PathVariable String type) {
        new Thread(() -> {
            if ("all".equals(type)) {
                List<IIndex> indexList = IndexService.getInstance().getAll();
                for (IIndex index : indexList) {
                    if (IndexTypeLock.isLock(index)) {
                        continue;
                    }
                    IndexTypeLock.lock(index);
                    index.build(new Date(0));
                    index.delete(new Date(0));
                    IndexTypeLock.unlock(index);
                }
            } else {
                IIndex index = IndexService.getInstance().get(type);
                if (IndexTypeLock.isLock(index)) {
                    return;
                }
                index.build(new Date(0));
                index.delete(new Date(0));
                IndexTypeLock.unlock(index);
            }
        }).start();
        return success("索引重建开始执行");
    }

    /**
     * @Description: 全库索引重建
     */
    @Priv(IndicesManagerPriv.INDEX_REBUILD)
    @GetMapping("/reIndexAll")
    public synchronized ApiResponses<String> rebuildAllIndex() {
        if (IndexRebuildTask.rebuildAllLock) {
            return failure("正在重建，请勿重复点击！");
        }
        IndexRebuildTask.rebuildAllLock = true;
        IIndex.updateIndexId();
        new Thread(() -> {
            try {
                List<IIndex> indexList = IndexService.getInstance().getAll();
                for (IIndex index : indexList) {
                    index.build(new Date(0));
                    index.delete(new Date(0));
                }
                String alias = IIndex.getIndexAlias();
                // 已存在的索引需要删除
                String name1 = new IndexService().getIndexIdByAlias(alias);
                String name2 = IIndex.getIndexId();
                if (!name1.equals(name2)) {
                    if (IndexService.delete(alias)) {
                        log.debug("删除旧的索引库:" + name1);
                        Files.deleteIfExists(Paths.get(AppDataPath.getValue() + "essearch/es_index.time"));
                        log.info("删除成功");
                    } else {
                        log.info("删除失败");
                    }
                }
                new IndexService().addAlias();
            } catch (Exception e) {
                log.error("全库索引重建失败");
                e.printStackTrace();
            } finally {
                IndexRebuildTask.rebuildAllLock = false;
            }
        }).start();
        return success("开始重建，请勿重复点击！");
    }

    /**
     * @Description: 锁释放
     */
    @Priv(IndicesManagerPriv.INDEX_RELEASE_LOCK)
    @GetMapping("/releaseIndexLock")
    public ApiResponses<String> releaseIndexLock() {
        IndexRebuildTask.rebuildAllLock = false;
        IndexTypeLock.unlockAll();
        return success("索引锁释放成功");
    }

    /**
     * @return
     * @Description: 获取索引库列表
     */
    @SuppressWarnings("unchecked")
    @Priv
    @GetMapping("/indexList")
    public ApiResponses<Page> indexList() {
        Page page = new Page();
        List<IndexList2Vo> list2VoList = new ArrayList<>();
        if (StrUtil.isEmpty(EsServerConfig.getValue())) {
            return success(page);
        }
        RestClient client = new ClientService().getClient();
        HttpEntity entity = null;
        Response indexResponse = null;
        try {
            indexResponse = client.performRequest(HttpMethod.GET, "/_cluster/state",
                    Collections.<String, String>emptyMap());
            entity = indexResponse.getEntity();
            client.close();
        } catch (IOException e) {
            log.error("索引库不存在");
            e.printStackTrace();
            page.setData(list2VoList);
            return success(page);
        }
        if (entity != null) {
            try {
                InputStream content = entity.getContent();
                String response = IoUtil.read(content, StandardCharsets.UTF_8);
                Gson gson = new Gson();
                Map<String, Object> responseMap = gson.fromJson(response, Map.class);
                Map<String, Object> metadata = (Map<String, Object>) responseMap.get("metadata");
                Map<String, Object> indicies = (Map<String, Object>) metadata.get("indices");
                for (Map.Entry<String, Object> e : indicies.entrySet()) {
                    List<String> alias = new ArrayList<>();
                    StringBuilder aliasBuffer = new StringBuilder();
                    String indicesName = e.getKey();
                    Map<String, Object> indexInfo = (Map<String, Object>) e.getValue();
                    alias = (List<String>) indexInfo.get("aliases");
                    if (alias != null) {
                        for (String a : alias) {
                            aliasBuffer.append(a);
                        }
                    }
                    Map<String, Object> indexMap = (Map<String, Object>) ((Map<String, Object>) indexInfo
                            .get("settings")).get("index");
                    Object numberOfShards = indexMap.get("number_of_shards");
                    Object numberOfReplicas = indexMap.get("number_of_replicas");
                    Object creationDate = indexMap.get("creation_date");
                    IndexList2Vo indexList2Vo = new IndexList2Vo();
                    indexList2Vo.setIndicesName(indicesName);
                    indexList2Vo.setAlias(aliasBuffer.toString());
                    indexList2Vo.setNumberOfShards(Convert.toStr(numberOfShards));
                    indexList2Vo.setNumberOfReplicas(Convert.toStr(numberOfReplicas));
                    indexList2Vo.setCreationDate(DateUtil.date(Convert.toLong(creationDate)).toString());
                    list2VoList.add(indexList2Vo);
                }
            } catch (UnsupportedOperationException | IOException e) {
                log.error("索引库查询错误");
                e.printStackTrace();
                page.setData(list2VoList);
                return success(page);
            }
        }
        page.setData(list2VoList);
        return success(page);
    }

    /**
     * @param name 索引库名称
     * @return
     * @Description: 获取索引库详细信息
     */
    @Priv
    @GetMapping("/index/info/{name}")
    public ApiResponses<String> indexInfo(@PathVariable String name) {
        RestClient client = new ClientService().getClient();
        Response indexResponse = null;
        if (StrUtil.isEmpty(name)) {
            return success("OK", "{}");
        }
        try {
            indexResponse = client.performRequest(HttpMethod.GET, "/" + name + "?pretty",
                    Collections.<String, String>emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = indexResponse.getEntity();
        String response = "{}";
        if (httpEntity != null) {
            InputStream instreams = null;
            try {
                instreams = httpEntity.getContent();
                client.close();
            } catch (UnsupportedOperationException | IOException e) {
                log.error("索引库查询失败！");
                e.printStackTrace();
            }
            response = IoUtil.read(instreams, StandardCharsets.UTF_8);
        }
        return success("OK", response);
    }

    private static boolean deleteIndexLock = false;

    /**
     * @param name 索引库名称
     * @Description: 删除索引库
     */
    @Priv(IndicesLibraryManagerPriv.INDEX_DELETE)
    @DeleteMapping("/{name}")
    public ApiResponses<String> deleteIndex(@PathVariable String name) {
        if (deleteIndexLock) {
            return failure("任务正在执行,请稍后");
        }
        if (StrUtil.isEmpty(name)) {
            return failure("索引名错误,删除失败!");
        }
        deleteIndexLock = true;
        RestClient client = new ClientService().getClient();
        HttpEntity entity = null;
        Response indexResponse = null;
        String[] indexArr = name.split(",");
        for (String indexName : indexArr) {
            try {
                indexResponse = client.performRequest(HttpMethod.DELETE, "/" + indexName,
                        Collections.<String, String>emptyMap());
                entity = indexResponse.getEntity();
                if (entity != null) {
                    String response = IoUtil.read(entity.getContent(), StandardCharsets.UTF_8);
                    log.debug(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
                deleteIndexLock = false;
                return failure("删除失败,请查看后台日志");
            } finally {
            }
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            deleteIndexLock = false;
        }
        return success("删除成功!");
    }

}
