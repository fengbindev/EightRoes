package com.ssrs.framework.web.swaggermodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * @author ssrs
 */
@Data
@ApiModel(value = "分页数据", description = "分页需要的表单数据")
public class PageForm {
    /**
     * 页码
     */
    @Min(value = 1, message = "页码输入有误")
    @ApiModelProperty(value = "页码 从第一页开始 1", required = true)
    private Integer pageNo;

    /**
     * 每页显示的数量
     */
    @Range(min = 1, max = 100, message = "每页显示的数量输入有误")
    @ApiModelProperty(value = "每页显示的数量 范围在1~100", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "排序字段", required = false)
    public String sidx;

    @ApiModelProperty(value = "排序类型（asc/desc）", required = false)
    public  String order;

    /**
     * 计算当前页 ,方便mysql 进行分页查询
     *
     * @return 返回 pageForm
     */
    @ApiModelProperty(hidden = true)
    public PageForm calcCurrent() {
        pageNo = (pageNo - 1) * pageSize;
        return this;
    }
}
