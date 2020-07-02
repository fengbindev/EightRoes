package com.ssrs.platform.priv;

/**
 * 账户配置项表：菜单权限类
 *
 * @author ssrs
 */
public class AccountConfigPriv extends AbstractMenuPriv {

	public static final String MenuID = "AccountConfigPriv";
	public static final String Save = MenuID + ".Save";

	public AccountConfigPriv() {
		super(MenuID, "账户安全", null);
		addItem(Save, "保存");
	}

}
