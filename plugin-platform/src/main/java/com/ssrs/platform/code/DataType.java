package com.ssrs.platform.code;

import com.ssrs.platform.FixedCodeType;

import java.util.HashMap;
import java.util.Map;
/**
 * 配置项数据类型
 *
 * @author ssrs
 */
public class DataType extends FixedCodeType {
    public static final String CODETYPE = "DataType";

    public static final String ShortText = "ShortText";
    public static final String MediumText = "MediumText";
    public static final String LargeText = "LargeText";
    public static final String ClobText = "ClobText";
    public static final String Long = "Long";
    public static final String Double = "Double";
    public static final String Datetime = "Datetime";

    public String DataType;

    public int Count;

    public DataType() {
        super(CODETYPE, "数据类型", false, false);
        addFixedItem(MediumText, "字符串(200)", null);
        addFixedItem(ShortText, "字符串(50)", null);
        addFixedItem(LargeText, "字符串(2000)", null);
        addFixedItem(ClobText, "字符串(不限长度)", null);
        addFixedItem(Long, "长整型", null);
        addFixedItem(Double, "双字节浮点", null);
        addFixedItem(Datetime, "日期时间", null);
    }

    public static boolean isClobText(String type) {
        return ClobText.equals(type);
    }

    public static boolean isDate(String type) {
        return Datetime.equals(type);
    }

    public static boolean isDouble(String type) {
        return Double.equals(type);
    }

    public static boolean isLargeText(String type) {
        return LargeText.equals(type);
    }

    public static boolean isLong(String type) {
        return Long.equals(type);
    }

    public static boolean isMediumText(String type) {
        return MediumText.equals(type);
    }

    public static boolean isShortText(String type) {
        return ShortText.equals(type);
    }

    private static class TypesHolder {
        final static Map<String, Integer> types = new HashMap<>();

        static {
            types.put(ShortText, 25);
            types.put(MediumText, 25);
            types.put(LargeText, 4);
            types.put(ClobText, 1);
            types.put(Long, 10);
            types.put(Double, 10);
            types.put(Datetime, 10);
        }
    }

    public static Map<String, Integer> getFieldTypes() {
        return TypesHolder.types;
    }
}
