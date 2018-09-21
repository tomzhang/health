package com.dachen.commons.constants;

import com.dachen.commons.JSONMessage;
/**
 * 常量
 * 
 * @author luorc
 * 
 */
public interface Constants {

	public interface Expire {
	    static final int MINUTE1 = 600;
	    static final int HOUR1 = 60000;
		static final int DAY1 = 86400;
		static final int DAY7 = 604800;
		static final int HOUR12 = 43200;
	}

	public interface MsgType {
		static final Byte Audio = 3;
		static final Byte Image = 2;
		static final Byte Image_Audio = 5;
		static final Byte Text = 1;
		static final Byte Video = 4;
	}

	public interface Result {
	    static final JSONMessage InternalException = new JSONMessage(1020101, "服务器繁忙，请稍后再试！");//“接口内部异常”改成“服务器繁忙，请稍后再试！”
        static final JSONMessage ParamsAuthFail = new JSONMessage(1010101, "请求参数验证失败，缺少必填参数或参数错误");
        static final JSONMessage TokenEillegal = new JSONMessage(1030101, "缺少访问令牌");
        static final JSONMessage TokenInvalid = new JSONMessage(1030102, "访问令牌过期或无效");
	}

	public interface ResultCode {
		static final int Failure = 0;
		static final int InternalException = 1020101;
		static final int ParamsAuthFail = 1010101;
		static final int ParamsLack = 1010102;
		static final int TokenPass3M = 1010103;//token 3分钟失效 
		static final int ContextPass7DOrForbidden = 1010104;//token 已被禁用
		static final int Success = 1;
		int HttpApiException = 1050101;	// 远程调用异常
	}
	
	//缓存key前缀
    public interface CacheKeyPre {
        static final String Session = "session";//用户信息
        static final String SessionUserId = "session_userId";//用户id与token之间的缓存
        static final String DoctorCheck = "DoctorCheck";//客服审核医生
        static final String NurseCheck = "NurseCheck";//客服审核护士
        /*2016年6月1日09:47:39  liming*/
        static final String GuestSession="guestSession";//游客信息
		static final String USERID_TOKEN = "userId_token";//用户的context信息
        
    }
    
    //程序中一些常量的ID
    public interface Id {
    	/**博德嘉联**/
    	static String BDJL_SERVICE_CATEGORY_ID = "5721afe7f95c43d41203d233";
    	/**平台ID**/
    	static String PLATFORM_ID = "666666666666666666666666";
    }
    
    //用户context在redis中存储的key
    public interface ContextObjKey{
		static final String TOKEN = "token";
		static final String LAST_TIME = "lastTime";
		static final String TOKEN_STATUS = "tokenStatus";
	}
	
    //token的状态
	public interface TokenStatus{
		static final String NORMAL = "1";//正常
		static final String UNUSUAL = "0";//失效
	}
	
}
