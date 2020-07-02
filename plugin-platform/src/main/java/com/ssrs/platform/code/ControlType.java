package com.ssrs.platform.code;

import com.ssrs.platform.FixedCodeType;

/**
 * 配置项控件类型
 *
 * @author ssrs
 */
public class ControlType extends FixedCodeType {
    public static final String CODETYPE = "ControlType";

    public static final String Text = "Text";
    public static final String Password = "Password";
    public static final String Selector = "Select";
    public static final String TextArea = "TextArea";
    public static final String Radio = "Radio";
    public static final String Checkbox = "Checkbox";
    public static final String DateSelector = "Date";
    public static final String DateTimeSelector = "DateTime";
    public static final String TimeSelector = "Time";

    public ControlType() {
        super(CODETYPE, "控件类型", true, false);
        addFixedItem(ControlType.Checkbox, "多选框", null);
        addFixedItem(ControlType.DateSelector, "日期选择框", null);
        addFixedItem(ControlType.DateTimeSelector, "日期时间选择框", null);
        addFixedItem(ControlType.Radio, "单选框", null);
        addFixedItem(ControlType.Selector, "下拉框", null);
        addFixedItem(ControlType.Text, "输入框", null);
        addFixedItem(ControlType.Password, "密码输入框", null);
        addFixedItem(ControlType.TextArea, "多行文本框", null);
    }

    public boolean isText(String type) {
        return Text.equals(type);
    }

    public boolean isPassword(String type) {
        return Password.equals(type);
    }

    public boolean isSelector(String type) {
        return Selector.equals(type);
    }

    public boolean isTextArea(String type) {
        return TextArea.equals(type);
    }

    public boolean isRadio(String type) {
        return Radio.equals(type);
    }

    public boolean isCheckbox(String type) {
        return Checkbox.equals(type);
    }

    public boolean isDateSelector(String type) {
        return DateSelector.equals(type);
    }

    public boolean isDateTimeSelector(String type) {
        return DateTimeSelector.equals(type);
    }
}

