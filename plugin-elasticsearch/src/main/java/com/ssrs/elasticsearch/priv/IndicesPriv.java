package com.ssrs.elasticsearch.priv;


import com.ssrs.platform.priv.AbstractMenuPriv;

public class IndicesPriv extends AbstractMenuPriv {

    public static final String MenuID = "ElasticSearch.Indices";

    public IndicesPriv() {
        super(MenuID, "检索管理", null);
    }
}
