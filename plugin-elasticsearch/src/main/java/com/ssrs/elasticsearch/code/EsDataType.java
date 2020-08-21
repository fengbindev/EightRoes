package com.ssrs.elasticsearch.code;

/**
 * @author ssrs
 */
public class EsDataType {
    // 字符串类型
    public static final String TEXT = "text";
    public static final String KEYWORD = "keyword";

    // 数字类型
    public static final String LONG = "long";
    public static final String INTEGER = "integer";
    public static final String SHORT = "short";
    public static final String BYTE = "byte";
    public static final String DOUBLE = "double";
    public static final String FLOAT = "float";

    // 日期类型
    public static final String DATE = "date";

    // 布尔类型
    public static final String BOOLEAN = "boolean";

    // 二进制类型
    public static final String BINARY = "binary";

    // 范围类型
    public static final String INTEGER_RANGE = "integer_range";
    public static final String FLOAT_RANGE = "float_range";
    public static final String LONG_RANGE = "long_range";
    public static final String DOUBLE_RANGE = "double_range";
    public static final String DATE_RANGE = "date_range";

    // 混合类型
    public static final String NESTED = "nested";
    public static final String OBJECT = "object";
}

