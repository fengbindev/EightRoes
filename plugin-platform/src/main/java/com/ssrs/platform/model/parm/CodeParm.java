package com.ssrs.platform.model.parm;

import com.ssrs.framework.core.modelmapper.Convert;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author ssrs
 */
@Getter
@Setter
public class CodeParm extends Convert {
    /**
     * 代码类别
     */
    @NotBlank(groups = {Create.class, Update.class, ItemCreate.class, ItemUpdate.class}, message = "代码类别不能为空")
    private String codeType;

    /**
     * 代码父类
     */
    @NotBlank(groups = {ItemCreate.class, ItemUpdate.class}, message = "代码父类不能为空")
    private String parentCode;

    /**
     * 代码值
     */
    @NotBlank(groups = {ItemCreate.class, ItemUpdate.class}, message = "代码值不能为空")
    private String codeValue;

    /**
     * 代码名称
     */
    @NotBlank(groups = {Create.class, Update.class, ItemCreate.class, ItemUpdate.class}, message = "代码名称不能为空")
    private String codeName;

    /**
     * 备注
     */
    private String memo;


    public interface Create {

    }

    public interface Update {

    }

    public interface ItemCreate {

    }

    public interface ItemUpdate {

    }
}
