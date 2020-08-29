package com.ssrs.framework.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ssrs.framework.web.ApiException;
import com.ssrs.framework.web.ErrorCode;
import com.ssrs.framework.web.ErrorCodeEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言封装
 *
 * @author ssrs
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiAssert {

    /**
     * obj1 eq obj2
     *
     * @param obj1
     * @param obj2
     * @param errorCodeEnum
     */
    public static void equals(ErrorCodeEnum errorCodeEnum, Object obj1, Object obj2) {
        if (Objects.equals(obj1, obj2)) {
            failure(errorCodeEnum);
        }
    }

    public static void isTrue(ErrorCodeEnum errorCodeEnum, boolean condition) {
        if (condition) {
            failure(errorCodeEnum);
        }
    }

    public static void isFalse(ErrorCodeEnum errorCodeEnum, boolean condition) {
        if (!condition) {
            failure(errorCodeEnum);
        }
    }

    public static void isNull(ErrorCodeEnum errorCodeEnum, Object... conditions) {
        if (ObjectUtil.isNull(conditions)) {
            failure(errorCodeEnum);
        }
    }

    public static void notNull(ErrorCodeEnum errorCodeEnum, Object... conditions) {
        if (ObjectUtil.isNotNull(conditions)) {
            failure(errorCodeEnum);
        }
    }

    /**
     * <p>
     * 失败结果
     * </p>
     *
     * @param errorCodeEnum 异常错误码
     */
    public static void failure(ErrorCodeEnum errorCodeEnum) {
        throw new ApiException(errorCodeEnum);
    }

    public static void notEmpty(ErrorCodeEnum errorCodeEnum, Object[] array) {
        if (ObjectUtil.isNotEmpty(array)) {
            failure(errorCodeEnum);
        }
    }

    public static void anyNullElements(ErrorCodeEnum errorCodeEnum, Object[] array) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    failure(errorCodeEnum);
                }
            }
        }
    }

    public static void notEmpty(ErrorCodeEnum errorCodeEnum, Collection<?> collection) {
        if (CollUtil.isNotEmpty(collection)) {
            failure(errorCodeEnum);
        }
    }

    public static void notEmpty(ErrorCodeEnum errorCodeEnum, Map<?, ?> map) {
        if (MapUtil.isNotEmpty(map)) {
            failure(errorCodeEnum);
        }
    }

    public static void isEmpty(ErrorCodeEnum errorCodeEnum, Collection<?> collection) {
        if (CollUtil.isEmpty(collection)) {
            failure(errorCodeEnum);
        }
    }


    public static void isEmpty(ErrorCodeEnum errorCodeEnum, Map<?, ?> map) {
        if (MapUtil.isEmpty(map)) {
            failure(errorCodeEnum);
        }
    }


    public static void equals(ErrorCode errorCode, Object obj1, Object obj2) {
        if (Objects.equals(obj1, obj2)) {
            failure(errorCode);
        }
    }

    public static void isTrue(ErrorCode errorCode, boolean condition) {
        if (condition) {
            failure(errorCode);
        }
    }

    public static void isFalse(ErrorCode errorCode, boolean condition) {
        if (!condition) {
            failure(errorCode);
        }
    }

    public static void isNull(ErrorCode errorCode, Object... conditions) {
        if (ObjectUtil.isNull(conditions)) {
            failure(errorCode);
        }
    }

    public static void notNull(ErrorCode errorCode, Object... conditions) {
        if (ObjectUtil.isNotNull(conditions)) {
            failure(errorCode);
        }
    }

    /**
     * <p>
     * 失败结果
     * </p>
     *
     * @param errorCode 异常错误码
     */
    public static void failure(ErrorCode errorCode) {
        throw new ApiException(errorCode);
    }

    public static void notEmpty(ErrorCode errorCode, Object[] array) {
        if (ObjectUtil.isNotEmpty(array)) {
            failure(errorCode);
        }
    }

    public static void anyNullElements(ErrorCode errorCode, Object[] array) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    failure(errorCode);
                }
            }
        }
    }

    public static void notEmpty(ErrorCode errorCode, Collection<?> collection) {
        if (CollUtil.isNotEmpty(collection)) {
            failure(errorCode);
        }
    }

    public static void notEmpty(ErrorCode errorCode, Map<?, ?> map) {
        if (MapUtil.isNotEmpty(map)) {
            failure(errorCode);
        }
    }

    public static void isEmpty(ErrorCode errorCode, Collection<?> collection) {
        if (CollUtil.isEmpty(collection)) {
            failure(errorCode);
        }
    }

    public static void isEmpty(ErrorCode errorCode, Map<?, ?> map) {
        if (MapUtil.isEmpty(map)) {
            failure(errorCode);
        }
    }

}
