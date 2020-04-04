package com.ssrs.framework.core.modelmapper;

import java.io.Serializable;

/**
 * @Description: Bean转换类，用于实体类互转
 * @Author: ssrs
 * @CreateDate: 2019/8/24 16:59
 * @UpdateUser: ssrs
 * @UpdateDate: 2019/8/24 16:59
 * @Version: 1.0
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
