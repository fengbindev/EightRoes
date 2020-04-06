package com.ssrs.framework.web;

import com.ssrs.framework.web.util.RequestUtils;
import com.ssrs.framework.web.util.ResponseUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 通用错误处理器
 * </p>
 *
 * @author Caratacus
 */
@SuppressWarnings({"Annotator", "SyntaxError"})
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "error";
    }

    @RequestMapping
    public void error(HttpServletRequest request,
                      HttpServletResponse response) {
        request = new RequestWrapper(request);
        ErrorCodeEnum errorCode;
        switch (response.getStatus()) {
            case HttpServletResponse.SC_BAD_REQUEST:
                errorCode = ErrorCodeEnum.BAD_REQUEST;
                break;
            case HttpServletResponse.SC_UNAUTHORIZED:
                errorCode = ErrorCodeEnum.UNAUTHORIZED;
                break;
            case HttpServletResponse.SC_FORBIDDEN:
                errorCode = ErrorCodeEnum.FORBIDDEN;
                break;
            case HttpServletResponse.SC_NOT_FOUND:
                errorCode = ErrorCodeEnum.NOT_FOUND;
                break;
            case HttpServletResponse.SC_METHOD_NOT_ALLOWED:
                errorCode = ErrorCodeEnum.METHOD_NOT_ALLOWED;
                break;
            case HttpServletResponse.SC_NOT_ACCEPTABLE:
                errorCode = ErrorCodeEnum.NOT_ACCEPTABLE;
                break;
            case HttpServletResponse.SC_LENGTH_REQUIRED:
                errorCode = ErrorCodeEnum.LENGTH_REQUIRED;
                break;
            case HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE:
                errorCode = ErrorCodeEnum.UNSUPPORTED_MEDIA_TYPE;
                break;
            case HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE:
                errorCode = ErrorCodeEnum.REQUESTED_RANGE_NOT_SATISFIABLE;
                break;
            case HttpServletResponse.SC_SERVICE_UNAVAILABLE:
                errorCode = ErrorCodeEnum.SERVICE_UNAVAILABLE;
                break;
            default:
                errorCode = ErrorCodeEnum.INTERNAL_SERVER_ERROR;
        }
        ResponseUtils.sendFail(request, response, errorCode);
    }

}
