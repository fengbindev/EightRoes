package com.ssrs.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统用户表
 * </p>
 *
 * @author ssrs
 * @since 2020-04-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 头像
     */
    private String avatar;

    private String branchInnercode;

    private String branchAdmin;

    private String lastLoginIp;

    /**
     * 状态 0：禁用 1：正常
     */
    private String status;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime lastLoginTime;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime loginErrorTime;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer loginErrorCount;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String modifyPassStatus;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime forbiddenLoginTime;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime lastModifyPassTime;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改者
     */
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


    public static final String ID = "id";

    public static final String USER_NAME = "user_name";

    public static final String NICK_NAME = "nick_name";

    public static final String REAL_NAME = "real_name";

    public static final String PASSWORD = "password";

    public static final String EMAIL = "email";

    public static final String MOBILE = "mobile";

    public static final String AVATAR = "avatar";

    public static final String BRANCH_INNERCODE = "branch_innercode";

    public static final String BRANCH_ADMIN = "branch_admin";

    public static final String LAST_LOGIN_IP = "last_login_ip";

    public static final String STATUS = "status";

    public static final String LAST_LOGIN_TIME = "last_login_time";

    public static final String LOGIN_ERROR_TIME = "login_error_time";

    public static final String LOGIN_ERROR_COUNT = "login_error_count";

    public static final String MODIFY_PASS_STATUS = "modify_pass_status";

    public static final String FORBIDDEN_LOGIN_TIME = "forbidden_login_time";

    public static final String LAST_MODIFY_PASS_TIME = "last_modify_pass_time";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

}
