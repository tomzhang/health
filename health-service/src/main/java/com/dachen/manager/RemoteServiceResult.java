package com.dachen.manager;

public class RemoteServiceResult {
//    public static final Result EMPTY = Result.build((Object) null);

//    public static final int COMMON_ERROR = 100;
//    public static final int TOKEN_ERROR = 100001;
//    public static final int TOKEN_INVALID = 1030102;
//    public static final int PARAM_ERROR = 100200;


	private String detailMsg;
	private Object data;
	private int resultCode = 1;

//    public static Result build(Object data) {
//        Result result = new Result();
//        result.data = data;
//        return result;
//    }

//    public static Result build(int errorCode, String error) {
//        Result result = new Result();
//        result.resultCode = errorCode;
//        result.detailMsg = error;
//        return result;
//    }

//    public static Result build(Throwable e) {
//        Result result = new Result();
//        result.resultCode = COMMON_ERROR;
//        if (e instanceof NullPointerException) {
//            result.detailMsg = "空指针错误";
//        } else {
//            result.detailMsg = e.getMessage();
//        }
//        return result;
//    }

	public void setResultMsg(String resultMsg) {
		this.detailMsg = resultMsg;
	}

	public String getDetailMsg() {
		return detailMsg;
	}

	public Object getData() {
		return data;
	}

	public int getResultCode() {
		return resultCode;
	}
//    public boolean sucess(){
//        return resultCode==1;
//    }

	public void setData(Object data) {
		this.data = data;
	}

	public void setResultCode(int code) {
		this.resultCode = code;
	}

}
