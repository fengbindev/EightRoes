package com.ssrs.platform.model.parm;

import com.ssrs.framework.core.modelmapper.Convert;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BranchParm extends Convert {
    @NotBlank(message = "机构编码不能为空")
    private String branchCode;
    @NotBlank(message = "机构名称不能为空")
    private String name;
    private String manager;
    private Long orderFlag;
    private String parentInnercode;
    private String phone;
    private String fax;
}
