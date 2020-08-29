package com.ssrs.elasticsearch.model.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author ssrs
 */
@Data
public class StopWordAddForm {
    @NotEmpty(message = "停用词不能为空！")
    private String word;
}
