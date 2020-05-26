package com.ssrs.platform.code;


import com.ssrs.platform.FixedCodeType;

public class OverLoginCountType extends FixedCodeType {

	public static final String CODE_TYPE = "OverLoginCountType";

	// 锁定账号
	public static final String ACCESS_LOCK = "A";
	// 指定时间内禁止登录
	public static final String TIME_LOCK = "B";

	public OverLoginCountType() {
		super(CODE_TYPE, "超过阀值处理方式", true, false);
		addFixedItem(ACCESS_LOCK, "锁定账号", null);
		addFixedItem(TIME_LOCK, "指定时间内禁止登录", null);
	}
}
