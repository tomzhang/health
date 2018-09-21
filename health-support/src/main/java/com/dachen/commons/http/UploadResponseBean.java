package com.dachen.commons.http;

public class UploadResponseBean {
	private int total;
	
	private UploadResponseDataBean data;
	
	private int failure;
	
	private int resultCode;
	
	private int success;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public UploadResponseDataBean getData() {
		return data;
	}

	public void setData(UploadResponseDataBean data) {
		this.data = data;
	}

	public int getFailure() {
		return failure;
	}

	public void setFailure(int failure) {
		this.failure = failure;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}
	
	
}
