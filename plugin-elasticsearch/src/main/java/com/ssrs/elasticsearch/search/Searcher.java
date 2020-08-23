package com.ssrs.elasticsearch.search;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ssrs.elasticsearch.code.OrderType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Searcher {

    public List<String> _source;

    public JsonObject query;

    public Highlight highlight;

    public Map<String, Map<String, ? extends Object>> aggs;

    public long from;

    public int size;

    public List<Map<String, ? extends Object>> sort;

    /**
     * 默认高亮标签
     */
    public static final String DEFAULT_PRE_TAG = "<font style='color:red'>";
    public static final String DEFAULT_POST_TAG = "</font>";

    public Searcher setQuery(JsonObject query) {
        this.query = query;
        return this;
    }

    /**
     * 聚合条件
     *
     * @author steven
     */
    public static class Aggs {

        public String name;
        public Map<String, Object> aggsMap = new HashMap<String, Object>();

        public Aggs(String field, int size) {
            this.name = "aggs_" + field;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("field", field);
            map.put("size", size);
            aggsMap.put("terms", map);
        }

    }

    /**
     * 高亮条件
     *
     * @author steven
     */
    public static class Highlight {
        public List<String> pre_tags;
        public List<String> post_tags;
        public Map<String, Map<String, ? extends Object>> fields;

        public Highlight() {
            /*
             * 设置高亮标签
             */
            if (pre_tags == null) {
                pre_tags = new ArrayList<String>();
            }
            pre_tags.add(DEFAULT_PRE_TAG);
            if (post_tags == null) {
                post_tags = new ArrayList<String>();
            }
            post_tags.add(DEFAULT_POST_TAG);
        }

        public Highlight addField(Field field) {
            if (fields == null) {
                fields = new HashMap<String, Map<String, ? extends Object>>();
            }
            Map<String, Integer> conditionMap = new HashMap<String, Integer>();
            if (field.numberOfFragments > 0) {
                conditionMap.put("number_of_fragments", field.numberOfFragments);
            }
            if (field.fragmentSize > 0) {
                conditionMap.put("fragment_size", field.fragmentSize);
            }
            fields.put(field.fieldName, conditionMap);
            return this;
        }
    }

    /**
     * 高亮字段条件
     *
     * @author steven
     */
    public static class Field {
        String fieldName;
        int numberOfFragments;
        int fragmentSize;

        public Field(String fieldName) {
            this.fieldName = fieldName;
        }

        public Field setNumberOfFragments(int numberOfFragments) {
            this.numberOfFragments = numberOfFragments;
            return this;
        }

        public Field setFragmentSize(int fragmentSize) {
            this.fragmentSize = fragmentSize;
            return this;
        }

        public Field(String fieldName, int numberOfFragments, int fragmentSize) {
            this.fieldName = fieldName;
            this.numberOfFragments = numberOfFragments;
            this.fragmentSize = fragmentSize;
        }
    }

    /**
     * 排序条件，默认排序不需要设置。设置此条件后，排序根据设置字段的倒序或正序排序，忽略得分
     *
     * @author steven
     */
    public static class Sort {
        public Map<String, Map<String, String>> sort = new HashMap<String, Map<String, String>>();

        public Sort(String field) {
            Map<String, String> order = new HashMap<String, String>();
            order.put("order", OrderType.ASC);
            sort.put(field, order);
        }

        public Sort(String field, String orderType) {
            Map<String, String> order = new HashMap<String, String>();
            order.put("order", orderType);
            sort.put(field, order);
        }
    }

    /**
     * 检索显示字段
     *
     * @param source
     * @return
     */
    public Searcher addSource(String source) {
        if (this._source == null) {
            this._source = new ArrayList<String>();
        }
        this._source.add(source);
        return this;
    }

    /**
     * 添加聚合条件
     *
     * @param aggs
     * @return
     */
    public Searcher addAggs(Aggs aggs) {
        if (this.aggs == null) {
            this.aggs = new HashMap<String, Map<String, ? extends Object>>();
        }
        this.aggs.put(aggs.name, aggs.aggsMap);
        return this;
    }

    /**
     * 取数据起始位置
     *
     * @param from
     * @return
     */
    public Searcher from(int from) {
        this.from = from;
        return this;
    }

    /**
     * 分页大小
     *
     * @param size
     * @return
     */
    public Searcher size(int size) {
        this.size = size;
        return this;
    }

    /**
     * 高亮设置
     *
     * @param highlight
     * @return
     */
    public Searcher highlight(Highlight highlight) {
        this.highlight = highlight;
        return this;
    }

    /**
     * 添加排序
     *
     * @param sort
     */
    public Searcher addSort(Sort sort) {
        if (this.sort == null) {
            this.sort = new ArrayList<Map<String, ? extends Object>>();
        }
        this.sort.add(sort.sort);
        return this;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
