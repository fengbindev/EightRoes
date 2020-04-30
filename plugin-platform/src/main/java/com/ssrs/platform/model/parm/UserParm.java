package com.ssrs.platform.model.parm;

import com.ssrs.framework.core.modelmapper.Convert;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserParm extends Convert {
    @NotBlank(message = "用户名不能为空")
    private String username;
    private String realname;
    @NotBlank(groups = {UserParm.Update.class}, message = "密码不能为空")
    private String password;
    @Email(message="邮箱格式错误")
    private String email;
    private String mobile;
    @NotBlank(message = "所属机构不能为空")
    private String branchInnercode;
    @NotBlank(message = "所属角色不能为空")
    private String roles;

    public interface Create {
    }

    public interface Update {
    }
}
