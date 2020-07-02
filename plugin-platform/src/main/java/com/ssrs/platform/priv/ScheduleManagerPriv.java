package com.ssrs.platform.priv;

/**
 * 定时任务权限
 *
 * @author ssrs
 */
public class ScheduleManagerPriv extends AbstractMenuPriv {

	public static final String MenuID = "ScheduleManagerPriv";
	public static final String Edit = MenuID + ".Edit";
	public static final String Run = MenuID + ".Run";

	public ScheduleManagerPriv() {
		super(MenuID, "定时任务", null);
		addItem(Edit, "编辑");
		addItem(Run, "立即执行");
	}

}
