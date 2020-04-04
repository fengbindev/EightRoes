package com.ssrs.framework.web;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class FaileResponses extends ApiResponses {
    
    private static final long serialVersionUID = 1L;
    /**
     * http 状态码
     */
    private Integer status;
    /**
     * 错误状态码
     */
    private String error;
    /**
     * 错误描述
     */
    private String message;
    /**
     * 异常信息
     */
    private String exception;
    /**
     * 当前时间戳
     */
    private LocalDateTime time;
}
