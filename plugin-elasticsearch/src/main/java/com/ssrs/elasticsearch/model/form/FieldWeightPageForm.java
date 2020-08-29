package com.ssrs.elasticsearch.model.form;

import com.ssrs.framework.web.swaggermodel.PageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ssrs
 */
@ApiModel
@Data
public class FieldWeightPageForm extends PageForm {

    @ApiModelProperty("字段名")
    private String field;
}
