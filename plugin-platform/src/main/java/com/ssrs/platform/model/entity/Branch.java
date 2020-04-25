package com.ssrs.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 组织机构表
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_branch")
public class Branch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内部编码
     */
    @TableId(value = "branch_innercode", type = IdType.INPUT)
    private String branchInnercode;

    /**
     * 机构编码
     */
    private String branchCode;

    /**
     * 父级内部编码
     */
    private String parentInnercode;

    /**
     * 机构类型
     */
    private String type;

    /**
     * 排序字段
     */
    private Long orderFlag;

    /**
     * 名称
     */
    private String name;

    /**
     * 级别
     */
    private Long treeLevel;

    /**
     * 是否叶子节点
     */
    @TableField("isLeaf")
    private String isLeaf;

    /**
     * 电话
     */
    private String phone;

    /**
     * 传真
     */
    private String fax;

    /**
     * 主管人
     */
    private String manager;

    /**
     * 上级主管人
     */
    private String leader1;

    /**
     * 上级分管人
     */
    private String leader2;

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


    public static final String BRANCH_INNERCODE = "branch_innercode";

    public static final String BRANCH_CODE = "branch_code";

    public static final String PARENT_INNERCODE = "parent_innercode";

    public static final String TYPE = "type";

    public static final String ORDER_FLAG = "order_flag";

    public static final String NAME = "name";

    public static final String TREE_LEVEL = "tree_level";

    public static final String ISLEAF = "isLeaf";

    public static final String PHONE = "phone";

    public static final String FAX = "fax";

    public static final String MANAGER = "manager";

    public static final String LEADER1 = "leader1";

    public static final String LEADER2 = "leader2";

    public static final String MEMO = "memo";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPDATE_USER = "update_user";

}
