package com.ssrs.platform.model.query;

import cn.hutool.json.JSONArray;
import com.ssrs.framework.core.modelmapper.Convert;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserQuery extends Convert {

    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机
     */
    private String mobile;

    private String branchInnercode;

    private String status;

    private LocalDateTime lastModifyPassTime;

    private String branchName;

    private JSONArray roles;


}
