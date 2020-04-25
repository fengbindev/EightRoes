package com.ssrs.platform.code;

import com.ssrs.platform.FixedCodeType;
import com.ssrs.platform.util.PlatformUtil;


public class YesOrNo extends FixedCodeType {
	public static final String CODETYPE = "YesOrNo";

	public static final String Yes = "Y";
	public static final String No = "N";

	public YesOrNo() {
		super(CODETYPE, "是或否", false, false);
		addFixedItem(Yes, "是", null);
		addFixedItem(No, "否", null);
	}

	public static boolean isYes(String str) {
		return Yes.equals(str);
	}

	public static boolean isNo(String str) {
		return !isYes(str);
	}

	public static String getName(String code) {
		return PlatformUtil.getCodeMap(CODETYPE).getStr(code);
	}
}
