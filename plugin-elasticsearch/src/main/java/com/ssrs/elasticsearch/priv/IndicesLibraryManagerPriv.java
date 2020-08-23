package com.ssrs.elasticsearch.priv;


import com.ssrs.platform.priv.AbstractMenuPriv;

public class IndicesLibraryManagerPriv extends AbstractMenuPriv {

    public static final String MenuID = "ElasticSearch.IndicesLibraryManager";

    public static final String INDEX_DELETE = MenuID + ".IndexDelete";        // 索引删除

    public IndicesLibraryManagerPriv() {
        super(MenuID, "索引删除", null);
        addItem(INDEX_DELETE, "删除索引");
    }
}
