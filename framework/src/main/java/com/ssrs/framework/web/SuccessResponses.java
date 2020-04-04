package com.ssrs.framework.web;

import lombok.*;

@Getter
@ToString
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponses<T> extends ApiResponses{

    private static final long serialVersionUID = 1L;
    /**
     * http 状态码
     */
    private Integer status;

    /**
     * 成功消息
     */
    private String message;
    /**
     * 结果集返回
     */
    private T data;
}
