package com.ssrs.platform.model;

import com.ssrs.framework.core.modelmapper.Convert;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ssrs
 */
@Setter
@Getter
public class CodeModel extends Convert {

    /**
     * 代码类别
     */
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

    /**
     * id
     */
    private String id;

    /**
     * 是否可以添加代码项
     */
    private boolean allowAddItem;

    /**
     * 是否固定代码项
     */
    private boolean isFixed;
}
