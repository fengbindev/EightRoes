package com.ssrs.platform.model.parm;

import com.ssrs.framework.core.modelmapper.Convert;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RoleParm extends Convert {
    @NotBlank(groups = {Create.class}, message = "角色编码不能为空")
    private String roleCode;
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    @NotBlank(message = "所属机构不能为空")
    private String branchInnercode;
    private String memo;

    public interface Create {

    }

    public interface Update {

    }
}
