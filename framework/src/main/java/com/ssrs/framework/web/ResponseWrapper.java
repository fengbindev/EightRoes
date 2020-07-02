package com.ssrs.framework.web;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.web.util.JacksonUtils;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * response包装类
 *
 * @author ssrs
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    private static final Log log = LogFactory.get();

    private ErrorCode errorcode;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public ResponseWrapper(HttpServletResponse response, ErrorCode errorcode) {
        super(response);
        setErrorCode(errorcode);
    }

    /**
     * 获取ErrorCode
     *
     * @return
     */
    public ErrorCode getErrorCode() {
        return errorcode;
    }

    /**
     * 设置ErrorCode
     *
     * @param errorCode
     */
    public void setErrorCode(ErrorCode errorCode) {
        if (Objects.nonNull(errorCode)) {
            this.errorcode = errorCode;
            super.setStatus(this.errorcode.getHttpCode());
        }
    }

    /**
     * 向外输出错误信息
     *
     * @param e
     * @throws IOException
     */
    public void writerErrorMsg(Exception e) {
        if (Objects.isNull(errorcode)) {
            log.warn("Warn: ErrorCodeEnum cannot be null, Skip the implementation of the method.");
            return;
        }
        printWriterApiResponses(ApiResponses.failure(this.getErrorCode(), e));
    }

    /**
     * 设置ApiErrorMsg
     */
    public void writerErrorMsg() {
        writerErrorMsg(null);
    }

    /**
     * 向外输出ApiResponses
     *
     * @param apiResponses
     */
    public void printWriterApiResponses(ApiResponses apiResponses) {
        writeValueAsJson(apiResponses);
    }

    /**
     * 向外输出json对象
     *
     * @param obj
     */
    public void writeValueAsJson(Object obj) {
        if (super.isCommitted()) {
            log.warn("Warn: Response isCommitted, Skip the implementation of the method.");
            return;
        }
        super.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        super.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = super.getWriter()) {
            writer.print(JacksonUtils.toJson(obj));
            writer.flush();
        } catch (IOException e) {
            log.warn("Error: Response printJson faild, stackTrace: {}", e);
        }
    }

}
