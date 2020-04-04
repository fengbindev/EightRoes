package com.ssrs.framework.web.swaggermodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("分页结果model")
public class DataGridModel<T> {
	@ApiModelProperty(value = "每页数量", example = "10")
	private long pageSize;
	@ApiModelProperty(value = "页码", example = "1")
	private long pageIndex;
	@ApiModelProperty(value = "总数量", example = "132")
	private long total;
	@ApiModelProperty(value = "业务数据", dataType = "List")
	private List<T> datalist;

	public long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	public long getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(long pageIndex) {
		this.pageIndex = pageIndex;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getData() {
		return datalist;
	}

	public void setData(List<T> data) {
		this.datalist = data;
	}

}
