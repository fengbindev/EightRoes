package com.ssrs.elasticsearch.code;

import com.ssrs.elasticsearch.config.AnalyzerConfig;
import com.ssrs.platform.FixedCodeType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ssrs
 */
public class AnalyzerType {
    public static final String CODETYPE = "AnalyzerType";
    public final static String HANLP = "hanlp";                   // 默认分词
    public final static String HANLP_INDEX = "hanlp_index";       // 索引分词
    public final static String HANLP_STANDARD = "hanlp_standard"; // 标准分词
    public final static String HANLP_N_SHORT = "hanlp_n_short";   // N-最短路分词
    public final static String HANLP_DIJKSTRA = "hanlp_dijkstra"; // 最短路分词

    private AnalyzerType() {
    }

    private static Map<String, String> map = new HashMap<String, String>();
    static {
        map.put(HANLP, "默认分词");
        map.put(HANLP_INDEX, "索引分词");
        map.put(HANLP_STANDARD, "标准分词");
        map.put(HANLP_N_SHORT, "N-最短路分词");
        map.put(HANLP_DIJKSTRA, "最短路分词");
    }

    public static Map<String, String> getMap() {
        return map;
    }

    public static String getConfig() {
        return AnalyzerConfig.getValue();
    }
}
