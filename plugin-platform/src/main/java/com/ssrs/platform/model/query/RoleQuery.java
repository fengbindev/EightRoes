package com.ssrs.platform.model.query;

import com.ssrs.framework.core.modelmapper.Convert;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleQuery extends Convert {

    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 所属机构名称
     */
    private String branchName;

    /**
     * 机构内部编码
     */
    private String branchInnercode;

    /**
     * 备注
     */
    private String memo;

    /**
     * 增加时间
     */
    private LocalDateTime createTime;

    /**
     * 增加人
     */
    private String createUser;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    private String updateUser;
}
