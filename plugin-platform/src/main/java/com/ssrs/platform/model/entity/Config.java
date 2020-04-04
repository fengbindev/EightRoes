package com.ssrs.platform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ssrs
 * @since 2020-02-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_config")
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置项代码
     */
    private String code;

    /**
     * 配置项名称
     */
    private String name;

    /**
     * 配置项值
     */
    private String value;

    /**
     * 备注
     */
    private String memo;

    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


    public static final String CODE = "code";

    public static final String NAME = "name";

    public static final String VALUE = "value";

    public static final String MEMO = "memo";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

}
