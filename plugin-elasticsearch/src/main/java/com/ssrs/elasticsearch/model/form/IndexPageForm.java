package com.ssrs.elasticsearch.model.form;

import com.ssrs.framework.web.swaggermodel.PageForm;
import lombok.Data;

/**
 * @author ssrs
 */
@Data
public class IndexPageForm extends PageForm {

    private String searchKey;
}
