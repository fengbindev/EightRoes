package com.ssrs.framework.extend;

/**
 * 扩展点接口
 *
 * @author ssrs
 */
public interface IExtendAction {
    /**
     * 扩展逻辑
     */
    Object execute(Object[] args) throws ExtendException;

    /**
     * 是否可用
     */
    boolean isUsable();
}
