package com.ssrs.elasticsearch.code;

/**
 * @author ssrs
 */
public class FieldWeightStatus {
    public static final String ENABLED = "Y"; // 注解中存在该字段,状态可用
    public static final String DISABLED = "N";// 注解中该字段已删除,但库中该字段存在,状态不可用
}
