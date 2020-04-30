package com.ssrs.platform.model.parm;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AuthUser {

    @NotBlank(message = "用户名不能为空！")
    private String userName;

    @NotBlank(message = "密码不能为空！")
    private String password;

    private String code;

    private String uuid = "";

    @Override
    public String toString() {
        return "{userName=" + userName  + ", password= ******}";
    }
}
