package com.ssrs.platform.code;

import com.ssrs.platform.FixedCodeType;

/**
 * 密码字符规范要求类型代码项
 *
 */
public class PasswordCharacterSpecification extends FixedCodeType {

    public static final String CHARACTER_REQUIREMENTS = "PasswordCharacterSpecification";
    public static final String NO_REQUIRED = "A";
    public static final String INCLUDE_LETTER_NUMBER = "B";
    public static final String INCLUDE_UPLETTER_LOLTTER_NUMBER = "C";
    public static final String INCLUDE_ALL = "D";

    public PasswordCharacterSpecification() {

        super(CHARACTER_REQUIREMENTS, "密码字符要求", true, false);

        // 无要求
        addFixedItem(NO_REQUIRED, "无要求", null);
        // 必须同时包含字母和数字
        addFixedItem(INCLUDE_LETTER_NUMBER, "必须同时包含字母和数字", null);
        // 必须同时包含大写字母，小写字母，数字
        addFixedItem(INCLUDE_UPLETTER_LOLTTER_NUMBER, "必须同时包含大写字母、小写字母、数字", null);
        // 必须同时包含大、小写字母，特殊字符、数字
        addFixedItem(INCLUDE_ALL, "必须同时包含大、小写字母、特殊字符、数字", null);

    }

}
