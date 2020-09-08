package com.ssrs.elasticsearch.model.vo;

import lombok.Data;

/**
 * @author ssrs
 */
@Data
public class IndexList2Vo {
    private String indicesName;
    private String alias;
    private String numberOfShards;
    private String numberOfReplicas;
    private String creationDate;
}
