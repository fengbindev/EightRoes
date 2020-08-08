package com.ssrs.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.annotations.Insert;

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
@TableName("sys_code")
public class Code implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 代码类别
     */
    @TableId(type = IdType.INPUT)
    private String codeType;

    /**
     * 代码父类
     */
    private String parentCode;

    /**
     * 代码值
     */
    private String codeValue;

    /**
     * 代码名称
     */
    private String codeName;

    /**
     * 代码顺序
     */
    private Long codeOrder;

    /**
     * 图标
     */
    private String icon;

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


    public static final String CODE_TYPE = "code_type";

    public static final String PARENT_CODE = "parent_code";

    public static final String CODE_VALUE = "code_value";

    public static final String CODE_NAME = "code_name";

    public static final String CODE_ORDER = "code_order";

    public static final String ICON = "icon";

    public static final String MEMO = "memo";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

}
