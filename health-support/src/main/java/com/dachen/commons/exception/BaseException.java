package com.dachen.commons.exception;

import org.apache.commons.lang3.StringUtils;

import com.dachen.commons.constants.Constants.ResultCode;

public class BaseException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private Integer errcode = ResultCode.InternalException;

	public BaseException(String message, Throwable cause, Object... params){
		super(cause.getMessage()+":"+message+"@"+StringUtils.join(params), cause);
	}
	
	public BaseException(String message, Object... params){
		super(message+"@"+StringUtils.join(params));
	}
	
	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}	
	
	public BaseException(String message) {
		super(message);
	}

	public Integer getErrcode() {
		return errcode;
	}

	public void setErrcode(Integer errcode) {
		this.errcode = errcode;
	}
	
}
