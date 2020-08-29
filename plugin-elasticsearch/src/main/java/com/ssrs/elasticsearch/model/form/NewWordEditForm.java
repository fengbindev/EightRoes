package com.ssrs.elasticsearch.model.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author ssrs
 */
@Data
public class NewWordEditForm {
    @NotEmpty(message = "请输入新词！")
    private String word;
}
