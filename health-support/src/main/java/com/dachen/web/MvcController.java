package com.dachen.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public abstract class MvcController {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public void init() throws Exception {

	}

	public HttpServletRequest getRequest() {
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
		return request;
	}
	
	protected String getParameter(String param){
		String result = this.getRequest().getParameter(param);
		if(StringUtils.isNotBlank(result))
			return result;
		return "";
	}
	
	public boolean needLogin() {
        return false;
    }
	
}
