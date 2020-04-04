package com.ssrs.framework.web.swaggermodel;

import io.swagger.annotations.ApiModelProperty;

public class SimpleResultModel {
	@ApiModelProperty(value="是否成功: 1-成功 or 0-失败",example="1")
	private int status;
	@ApiModelProperty(value="描述性原因",example="操作成功!")
	private String message;

	public SimpleResultModel(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public SimpleResultModel(){

    }

	public static  SimpleResultModel success(String message) {
		return message(true, message);
	}

	public static  SimpleResultModel success() {
		return message(true, "success");
	}

	public static  SimpleResultModel failure(String msg) {
		return message(false, msg);
	}

	public static  SimpleResultModel failure() {
		return message(false, "failure");
	}


	public static  SimpleResultModel message(boolean isSuccess,String message) {
		return new SimpleResultModel(isSuccess?1:0, message);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
