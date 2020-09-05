package com.ssrs.elasticsearch.priv;


import com.ssrs.platform.priv.AbstractMenuPriv;

public class IndicesManagerPriv extends AbstractMenuPriv {

    public static final String MenuID = "ElasticSearch.IndicesManager";

    public static final String INDEX_REBUILD = MenuID + ".IndexRebuild";          // 索引重建
    public static final String INDEX_RELEASE_LOCK = MenuID + ".IndexReleaseLock"; // 锁释放

    public IndicesManagerPriv() {
        super(MenuID, "索引管理", null);
        addItem(INDEX_REBUILD, "索引重建");
        addItem(INDEX_RELEASE_LOCK, "锁释放");
    }
}
