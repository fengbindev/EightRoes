package com.ssrs.elasticsearch.util;


import com.ssrs.elasticsearch.service.IIndex;

import java.util.ArrayList;
import java.util.List;

public class IndexTypeLock {

	private static List<String> typeLock = new ArrayList<String>();

	public static synchronized boolean isLock(IIndex index) {
		return typeLock.contains(index.getExtendItemID());
	}

	public static void lock(IIndex index) {
		typeLock.add(index.getExtendItemID());
	}

	public static void unlock(IIndex index) {
		typeLock.remove(index.getExtendItemID());
	}

	public static void unlockAll() {
		typeLock.clear();
	}
}
