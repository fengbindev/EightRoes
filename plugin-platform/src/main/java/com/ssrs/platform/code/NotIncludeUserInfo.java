package com.ssrs.platform.code;

import com.ssrs.platform.FixedCodeType;
import com.ssrs.platform.model.entity.User;

public class NotIncludeUserInfo extends FixedCodeType {

	public static final String CODE_TYPE = "NotIncludeUserInfo";
	public static final String NOT_INCLUDE_USERNAME = "UserName";
	public static final String NOT_INCLUDE_REALNAME = "RealName";
	public static final String NOT_INCLUDE_EMAIL = "Email";
	public static final String NOT_INCLUDE_MOBILE = "Mobile";

	public NotIncludeUserInfo() {
		super(CODE_TYPE, "密码不包含用户信息", true, false);
		addFixedItem(NOT_INCLUDE_USERNAME, "用户名", null);
		addFixedItem(NOT_INCLUDE_REALNAME, "用户真实姓名", null);
		addFixedItem(NOT_INCLUDE_EMAIL, "电子邮箱", null);
		addFixedItem(NOT_INCLUDE_MOBILE, "手机号", null);
	}

	public static String getUserValueByCode(String code, User user) {
		if (NOT_INCLUDE_USERNAME.equals(code)){
			return user.getUserName();
		} else if (NOT_INCLUDE_REALNAME.equals(code)){
			return user.getRealName();
		} else if (NOT_INCLUDE_EMAIL.equals(code)){
			return user.getEmail();
		} else if (NOT_INCLUDE_MOBILE.equals(code)){
			return user.getMobile();
		}
		return null;
	}

}
