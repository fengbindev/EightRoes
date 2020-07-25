package com.ssrs.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2020-07-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_operate_log")
public class OperateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String userName;

    private String ip;

    private String memo;

    private String url;

    private String logType;

    private String subType;

    private String logMessage;

    private String operateType;

    private String detailMessage;

    private String result;

    private String message;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    public static final String ID = "id";

    public static final String USER_NAME = "user_name";

    public static final String IP = "ip";

    public static final String MEMO = "memo";

    public static final String URL = "url";

    public static final String LOG_TYPE = "log_type";

    public static final String SUB_TYPE = "sub_type";

    public static final String LOG_MESSAGE = "log_message";

    public static final String OPERATE_TYPE = "operate_type";

    public static final String DETAIL_MESSAGE = "detail_message";

    public static final String RESULT = "result";

    public static final String MESSAGE = "message";

    public static final String CREATE_TIME = "create_time";

}
