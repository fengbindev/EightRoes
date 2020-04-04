package com.ssrs.framework.web.swaggermodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssrs.framework.web.IResponseBodyData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("返回对象")
public final class ResultModel<T> implements IResponseBodyData {
	@ApiModelProperty(value = "是否成功: 1-成功 or 0-失败", example = "1")
	private int status;
	@ApiModelProperty(value = "描述性原因", example = "操作成功!")
	private String message;
	@ApiModelProperty("业务数据")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

	private ResultModel(int status, String message, T data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public static <T> ResultModel<T> success(T data) {
		return new ResultModel<>(1, "success", data);
	}

	public static <T> ResultModel<T> success(String message) {
		return new ResultModel<>(1, message, null);
	}

	public static <T> ResultModel<T> success(String message, T data) {
		return new ResultModel<>(1, message, data);
	}

	public static <T> ResultModel<T> failure() {
		return new ResultModel<>(0, "failure", null);
	}

	public static <T> ResultModel<T> failure(String message, T data) {
		return new ResultModel<>(0, message, data);
	}

	public static <T> ResultModel<T> failure(String message) {
		return new ResultModel<>(0, message, null);
	}

	public static <T> ResultModel<T> message(boolean isSuccess, String message) {
		return new ResultModel<>(isSuccess ? 1 : 0, message, null);
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

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}