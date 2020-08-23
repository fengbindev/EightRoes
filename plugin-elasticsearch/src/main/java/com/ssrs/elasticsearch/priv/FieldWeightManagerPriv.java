package com.ssrs.elasticsearch.priv;


import com.ssrs.platform.priv.AbstractMenuPriv;

/**
 * 检索后台按钮权限
 *
 * @author ssrs
 */
public class FieldWeightManagerPriv extends AbstractMenuPriv {

    public static final String MenuID = "ElasticSearch.FieldWeightManager";

    public static final String FIELD_WEIGHT_EDIT = MenuID + "." + "FieldWeightEdit"; // 字段权重修改按钮

    public FieldWeightManagerPriv() {
        super(MenuID, "字段权重", null);
        addItem(FIELD_WEIGHT_EDIT, "修改");
    }
}
