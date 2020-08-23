package com.ssrs.elasticsearch.entity;


import com.ssrs.elasticsearch.annotation.FieldWeight;
import com.ssrs.elasticsearch.annotation.Index;
import com.ssrs.elasticsearch.code.AnalyzerType;
import com.ssrs.elasticsearch.code.EsDataType;
import lombok.Data;

import java.util.Date;

/**
 * 文档描述基础model自己的创建的索引文档需要基础此类
 */
@Data
public class BasicDoc {

    @Index(type = EsDataType.LONG, stored = true)
    private Long ID;

    // 标题
    @FieldWeight
    @Index(type = EsDataType.TEXT, stored = true, analyzer = AnalyzerType.CONFIG)
    private String title;

    // 摘要
    @FieldWeight
    @Index(type = EsDataType.TEXT, stored = true, analyzer = AnalyzerType.CONFIG)
    private String summary;

    // 发布年份
    @Index(type = EsDataType.INTEGER, stored = true)
    private Integer publishYear;

    // 发布日期
    @Index(type = EsDataType.DATE, stored = true)
    private Date publishDate;

    // 封面
    @Index(type = EsDataType.KEYWORD, stored = true)
    private String logoFile;

}
