package com.dachen.util.regex;

/**
 * 
 * ProjectName： sys-framework<br>
 * ClassName： Result<br>
 * Description： 验证结果封装<br>
 * @author yujl
 * @crateTime 2014年12月29日
 * @version 1.0.0
 */
public class Result {
	// 是否成功 true：是，false：否
	private Boolean isSuccess;
	
	// 提示信息
	private String prompt;
	
	public Result() {
		super();
	}
	
	public Result(Boolean isSuccess) {
		super();
		this.isSuccess = isSuccess;
	}

	public Result(Boolean isSuccess, String prompt) {
		super();
		this.isSuccess = isSuccess;
		this.prompt = prompt;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
}