package com.dachen.health.common.advice;

import com.alibaba.fastjson.JSON;
import com.dachen.common.exception.CommonException;
import com.dachen.commons.exception.ServiceException;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.StringUtils;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private static final Logger logger = LoggerFactory.getLogger("exceptionLog");

    @ExceptionHandler(value = { Exception.class, RuntimeException.class })
    public void handleErrors(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        logger.error(request.getRequestURL().toString(),e);

        int resultCode = 1020101;
        String resultMsg = "服务器繁忙，请稍后再试！";//“接口内部异常”改成“服务器繁忙，请稍后再试！”
        String detailMsg = "";

        if (e instanceof MissingServletRequestParameterException) {
            resultCode = 1010101;
            resultMsg = "请求参数验证失败，缺少必填参数或参数错误";
        } else if (e instanceof BindException) {
            resultCode = 1010101;
            resultMsg = "请求参数验证失败，缺少必填参数或参数错误";

            BindException ex = (BindException) e;
            BindingResult bindingResult = ex.getBindingResult();
            List<ObjectError> errorList = bindingResult.getAllErrors();
            StringBuilder sb = new StringBuilder();
            for (ObjectError objectError:errorList) {
                if (StringUtils.isNotBlank(objectError.getDefaultMessage())) {
                    sb.append(objectError.getDefaultMessage());
                    sb.append("；");
                }
            }
            if (sb.length()>0) {
                sb.setLength(sb.length()-1);
                resultMsg = sb.toString();
            }
        } else if (e instanceof ServiceException) {
            ServiceException ex = ((ServiceException) e);
            resultCode = null == ex.getResultCode() ? 0 : ex.getResultCode();
            resultMsg = ex.getMessage();
        } else if (e instanceof com.dachen.sdk.exception.ServiceException) {
            com.dachen.sdk.exception.ServiceException ex = ((com.dachen.sdk.exception.ServiceException) e);
            resultCode = null == ex.getResultCode() ? 0 : ex.getResultCode();
            resultMsg = ex.getMessage();
        } else if (e instanceof TypeMismatchException) {
            resultMsg = e.getMessage();
        } else if (e instanceof HttpApiException) {
        	resultCode = ((HttpApiException)e).getErrcode();
            resultMsg = "远程调用异常，请稍后再试！";
        } else if (e instanceof CommonException) {
            resultCode = ((CommonException) e).getResultCode();
            resultMsg = e.getMessage();
            detailMsg = e.getMessage();
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            resultMsg = e.getMessage();
        } else {
            detailMsg = e.getMessage();
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("resultCode", resultCode);
        map.put("resultMsg", resultMsg);
        map.put("detailMsg", detailMsg);

        String text = JSON.toJSONString(map);

        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(text);
        response.getWriter().flush();
        response.getWriter().close();
    }

}
