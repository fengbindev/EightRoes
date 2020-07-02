package com.ssrs.framework.web;

import lombok.*;

/**
 * 错误信息Model
 *
 * @author ssrs
 */
@Getter
@ToString
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ErrorCode {

    /**
     * 错误
     */
    private String error;
    /**
     * http状态码
     */
    private int httpCode;
    /**
     * 错误消息
     */
    private String message;
}
