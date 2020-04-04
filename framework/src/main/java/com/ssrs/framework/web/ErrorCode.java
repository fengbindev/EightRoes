package com.ssrs.framework.web;

import lombok.*;

/**
* @Description:    错误信息Model
* @Author:          ssrs
* @CreateDate:     2019/8/18 16:36
* @UpdateUser:     ssrs
* @UpdateDate:     2019/8/18 16:36
* @Version:        1.0
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
