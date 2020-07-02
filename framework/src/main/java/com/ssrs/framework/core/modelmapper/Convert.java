package com.ssrs.framework.core.modelmapper;

import java.io.Serializable;

/**
 * Bean转换类，用于实体类互转
 *
 * @author ssrs
 */
public class Convert implements Serializable {

    /**
     * 获取自动转换后的JavaBean对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T convert(Class<T> clazz) {
        return BeanConverter.convert(clazz, this);
    }
}
