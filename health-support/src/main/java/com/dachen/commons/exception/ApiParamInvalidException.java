package com.dachen.commons.exception;

public class ApiParamInvalidException extends BaseException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param params
	 */
	public ApiParamInvalidException(String message, Object... params) {
		super(message, params);
	}

	/**
	 * @param message
	 * @param cause
	 * @param params
	 */
	public ApiParamInvalidException(String message, Throwable cause, Object... params) {
		super(message, cause, params);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ApiParamInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ApiParamInvalidException(String message) {
		super(message);
	}
}
