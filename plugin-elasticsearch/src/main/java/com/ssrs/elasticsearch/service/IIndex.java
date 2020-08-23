package com.ssrs.elasticsearch.service;

import cn.hutool.core.util.StrUtil;
import com.ssrs.elasticsearch.config.EsIndexNameConfig;
import com.ssrs.framework.extend.IExtendItem;

import java.util.Date;

public abstract class IIndex implements IExtendItem {

	private static String INDEX_ID = "";
	private static String INDEX_ALIAS = "Booklib";

	public abstract String getTypeName();

	public abstract void build(Date date);

	public abstract void delete(Date date);

	public static String getIndexId() {
		if (StrUtil.isEmpty(INDEX_ID)) {
			INDEX_ID = new IndexService().getIndexIdByAlias(getIndexAlias());
		}
		if (StrUtil.isEmpty(INDEX_ID)) {
			INDEX_ID = INDEX_ALIAS + "_" + System.currentTimeMillis();
		}
		return INDEX_ID.toLowerCase();
	}

	public static String getIndexAlias() {
		if (StrUtil.isNotEmpty(EsIndexNameConfig.getValue())) {
			INDEX_ALIAS = EsIndexNameConfig.getValue();
		}
		return INDEX_ALIAS.toLowerCase();
	}

	public static void updateIndexId() {
		INDEX_ID = INDEX_ALIAS + "_" + System.currentTimeMillis();
	}

	public String getExtendItemID() {
		return getIndexId();
	}

}
