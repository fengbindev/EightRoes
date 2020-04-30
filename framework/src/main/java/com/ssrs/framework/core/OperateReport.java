package com.ssrs.framework.core;


import cn.hutool.json.JSONUtil;

/**
 * 操作结果类
 */
public class OperateReport {

	private boolean success;
	private String message;
	private Object data;

	public OperateReport(boolean success, String message, Object data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}

	public OperateReport(boolean success) {
		this.success = success;
	}

	public Object getData() {
		return this.data;
	}

	public String getMessage() {
		return this.message;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setSuccess(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public String toJSONString() {
		return JSONUtil.toJsonStr(this);
	}
}
