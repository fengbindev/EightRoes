package com.ssrs.elasticsearch.priv;


import com.ssrs.platform.priv.AbstractMenuPriv;

/**
 * 检索后台按钮权限
 *
 * @author ssrs
 */
public class SearchWordManagerPriv extends AbstractMenuPriv {

    public static final String MenuID = "ElasticSearch.SearchWordManager";

    public static final String NEW_WORD_ADD = MenuID + ".NewWordAdd";       // 新词添加按钮
    public static final String NEW_WORD_EDIT = MenuID + ".NewWordEdit";     // 新词修改按钮
    public static final String NEW_WORD_DEL = MenuID + ".NewWordDel";       // 新词删除按钮
    public static final String NEW_WORD_SYNC = MenuID + ".NewWordSync";     // 生成新词词典按钮

    public static final String STOP_WORD_ADD = MenuID + ".StopWordAdd";     // 停用词添加按钮
    public static final String STOP_WORD_EDIT = MenuID + ".StopWordEdit";   // 停用词修改按钮
    public static final String STOP_WORD_DEL = MenuID + ".StopWordDel";     // 停用词删除按钮
    public static final String STOP_WORD_SYNC = MenuID + ".StopWordSync";   // 生成停用词词典按钮

    public SearchWordManagerPriv() {
        super(MenuID, "词典管理", null);
        addItem(NEW_WORD_ADD, "新词添加");
        addItem(NEW_WORD_EDIT, "新词修改");
        addItem(NEW_WORD_DEL, "新词删除");
        addItem(NEW_WORD_SYNC, "生成新词词典");
        addItem(STOP_WORD_ADD, "停用词添加");
        addItem(STOP_WORD_EDIT, "停用词修改");
        addItem(STOP_WORD_DEL, "停用词删除");
        addItem(STOP_WORD_SYNC, "生成停用词词典");
    }
}
