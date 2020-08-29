package com.ssrs.elasticsearch.model.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author ssrs
 */
@Data
public class StopWordEditForm {
    @NotEmpty(message = "请输入停用词！")
    private String word;
}
