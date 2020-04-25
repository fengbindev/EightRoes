package com.ssrs.platform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 最大号表
 * </p>
 *
 * @author ssrs
 * @since 2020-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_maxno")
public class Maxno implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 最大号类别
     */
    @TableId(type = IdType.INPUT)
    private String noType;

    /**
     * 最大号子类别
     */
    @TableField("no_subType")
    private String noSubType;

    /**
     * 当前最大号
     */
    @TableField("no_maxValue")
    private Long noMaxValue;


    public static final String NO_TYPE = "no_type";

    public static final String NO_SUBTYPE = "no_subType";

    public static final String NO_MAXVALUE = "no_maxValue";

}
