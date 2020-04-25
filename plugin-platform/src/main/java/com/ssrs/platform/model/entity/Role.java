package com.ssrs.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色定义表
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色代码
     */
    @TableId(type = IdType.INPUT)
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 增加人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;


    public static final String ROLE_CODE = "role_code";

    public static final String ROLE_NAME = "role_name";

    public static final String BRANCH_INNERCODE = "branch_innercode";

    public static final String MEMO = "memo";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPDATE_USER = "update_user";

}
