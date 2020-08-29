package com.ssrs.elasticsearch.model.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author ssrs
 */
@Data
public class NewWordAddForm {
    @NotEmpty(message = "新词不能为空！")
    private String word;
}
