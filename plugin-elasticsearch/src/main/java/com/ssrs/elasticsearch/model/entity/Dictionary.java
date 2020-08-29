package com.ssrs.elasticsearch.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author ssrs
 * @since 2020-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dictionary")
public class Dictionary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String type;

    private String rootWord;

    private String synWords;

    private String info;

    private String status;

    private String memo;

    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


    public static final String ID = "id";

    public static final String TYPE = "type";

    public static final String ROOT_WORD = "root_word";

    public static final String SYN_WORDS = "syn_words";

    public static final String INFO = "info";

    public static final String STATUS = "status";

    public static final String MEMO = "memo";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

}
