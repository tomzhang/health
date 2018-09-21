package com.dachen.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloopen.rest.sdk.CCPRestSDK;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.service.SmsLogService;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;
import com.mobsms.sdk.SmsEnum;
import com.ucpaas.restsdk.UcPaasRestSdk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class SMSManager implements ISMSManager {
	
	private String app_id;
	private String account_sid;
	private String auth_token ;
	
	private String z_app_id;
	private String z_account_sid;
	private String z_auth_token ;
	
	private String url;
	private String port;
	
	@Resource
	private SmsLogService smsLogService;
	
	@Resource
    private MobSmsSdk mobSmsSdk;
	
	/**
	 * 短信模版的id
	 */
	private String sms_templateId;
	/**
	 * 验证码保留时间（分钟）
	 */
	private int activeTime;
	
	/**
	 * 忘记密码短信验证码保留时间(seconds)
	 */
	public static int forgetPasswordCodeActiveTime=2*60;//seconds
	
	@Autowired
	private JedisTemplate jedisTemplate;
	
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private RemoteSysManagerUtil remoteSysManagerUtil;
	
	/**
	 * 发送语音信息地址
	 */
	private static final String SEND_VOICE_SERVICE_URL = "http://SMSSERVICE/voice/sendVoice";

	private Logger logger = LoggerFactory.getLogger(SMSManager.class);
	
	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author 李淼淼
	 * @date 2015年8月4日
	 */
	@Override
	public boolean isAvailable(String telephone, String randcode,String templateId) {
		String key = MessageFormat.format("randcode:{0}", telephone+"@"+templateId);
		String _randcode = jedisTemplate.get(key);
		return randcode.equals(_randcode);
	}
	
	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author 李淼淼
	 * @date 2015年8月4日
	 */
	@Override
	public JSONMessage getSMSCode(String telephone, String templateId) {
		String key = MessageFormat.format("randcode:{0}", telephone + "@" + templateId);
		Long ttl = jedisTemplate.ttl(key);
		if (ttl > 60) {// 客户端显示时间为60秒倒计时
			throw new ServiceException("请不要频繁请求短信验证码，等待" + (ttl - 60) + "秒后再次请求");
		}
		JSONMessage jMessage;
		try {
			String param1 = StringUtil.random4Code();
			String param2 = String.valueOf(activeTime);// 两分钟
			JSONObject jsonObject = baseDataService.toContentAndThirdId("0019", param1, param2);
			logger.info("发送的短信验证码的内容是：{}", jsonObject.toJSONString());
			mobSmsSdk.send(telephone, jsonObject.getString("content"), SmsEnum.Signature.yishengquan7.getExt(), jsonObject.getInteger("tencentId"));
			jMessage = JSONMessage.success("验证码发送成功");
			jedisTemplate.set(key, param1);
			jedisTemplate.expire(key, activeTime * 60);
		} catch (Exception e) {
			jMessage = Constants.Result.InternalException;
		}

		return jMessage;
	}
	
	@Override
	public JSONMessage getVoiceCode(String telephone, String templateId) {
		String key = MessageFormat.format("randcode:{0}", telephone + "@" + templateId);
		Long ttl = jedisTemplate.ttl(key);
		if (ttl > 60) {// 客户端显示时间为60秒倒计时
			throw new ServiceException("请不要频繁请求语音验证码，等待" + (ttl - 60) + "秒后再次请求");
		}
		String verifyCode = StringUtil.random4Code();
		JSONMessage jMessage;
		try {
			boolean json = sendVoiceCode(telephone, verifyCode);
			if (json) {
				jedisTemplate.set(key, verifyCode);
				jedisTemplate.expire(key, activeTime * 60);
				jMessage = JSONMessage.success("验证码拨打成功");
				jMessage.put("respCode", "000000");
			} else {
				jMessage = JSONMessage.success("验证码拨打失败");
			}
		} catch (Exception e) {
			jMessage = Constants.Result.InternalException;
		}
		return jMessage;
	}

	/**
	 *
	 * @param telephone
	 * @param verifyCode
	 * @return
	 */
	public boolean sendVoiceCode(String telephone, String verifyCode) {
	    Map<String, String> param = new HashMap<>();
	    param.put("tel", telephone);
	    param.put("verifyCode", verifyCode);
		String result = remoteSysManagerUtil.post(SMSManager.SEND_VOICE_SERVICE_URL, param);
		smsLogService.log(ReqUtil.instance.getUserId()+"", telephone, verifyCode ,result);
		JSONObject json = JSON.parseObject(JSON.parse(result).toString());
		json = JSON.parseObject(json.getString("resp"));
        if(json.getString("respCode").equals("000000")){
            return true;
        }else{
            return false;
        }
	}
	
	/**
     * 发送语音验证码
     * <code>
     * --------------------------
     * 已过时：2017/08/30将方法由sendVoiceCode(String telephone, String verifyCode)修改为：
     * sendVoiceCodeOld(String telephone, String verifyCode)
     * 同时sendVoiceCode(String telephone, String verifyCode)使用新的语音发送方式实现
     * --------------------------
     * </code>
     * @deprecated
     */
    public boolean sendVoiceCodeOld(String telephone, String verifyCode) {
        String result = UcPaasRestSdk.voiceCode(true, z_account_sid, z_auth_token, z_app_id, telephone, verifyCode);
        smsLogService.log(ReqUtil.instance.getUserId()+"", telephone, verifyCode ,result);
        JSONObject json = JSON.parseObject(result);
        json = JSON.parseObject(json.getString("resp"));
        if(json.getString("respCode").equals("000000")){
            return true;
        }else{
            return false;
        }
    }
	
	public String getVoiceResult(String telephone, String verifyCode){
		return UcPaasRestSdk.voiceCode(true, z_account_sid, z_auth_token, z_app_id, telephone, verifyCode);
	}
	
	public boolean sendVoiceNotify(String to, String content) {
		//文本类型
		String authToken = z_auth_token;
		String accountSid = z_account_sid;
		String appId = z_app_id;
//		String toSerNum = SysConfig.getInstance().getProperty("from_ser_num");//回拨显示的电话号码
        String toSerNum = PropertiesUtil.getContextProperty("from_ser_num");//回拨显示的电话号码
		
		String result = UcPaasRestSdk.voiceNotify(true, accountSid, authToken, appId, to, content, toSerNum);
		
		smsLogService.log(ReqUtil.instance.getUserId()+"", to, content ,result);
		JSONObject json = JSON.parseObject(result);
		json = JSON.parseObject(json.getString("resp"));
		if(json.getString("respCode").equals("000000")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 发短信的工具方法
	 * @param telephone
	 * @param params
	 * @param templateId
	 * @return
	 */
	public Result sendSms(String telephone,String[]params,String templateId) {
		
		HashMap<String, Object> result = null;
		//初始化SDK
		CCPRestSDK restAPI = new CCPRestSDK();
		
		//******************************注释*********************************************
		//*初始化服务器地址和端口                                                       *
		//*沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
		//*生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883");       *
		//*******************************************************************************
		restAPI.init(url, port);
		
		//******************************注释*********************************************
		//*初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN     *
		//*ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
		//*参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。                   *
		//*******************************************************************************
		restAPI.setAccount(account_sid, auth_token);
		
		
		//******************************注释*********************************************
		//*初始化应用ID                                                                 *
		//*测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID     *
		//*应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
		//*******************************************************************************
		restAPI.setAppId(app_id);
		
		
		//******************************注释****************************************************************
		//*调用发送模板短信的接口发送短信                                                                  *
		//*参数顺序说明：                                                                                  *
		//*第一个参数:是要发送的手机号码，可以用逗号分隔，一次最多支持100个手机号                          *
		//*第二个参数:是模板ID，在平台上创建的短信模板的ID值；测试的时候可以使用系统的默认模板，id为1。    *
		//*系统默认模板的内容为“【云通讯】您使用的是云通讯短信模板，您的验证码是{1}，请于{2}分钟内正确输入”*
		//*第三个参数是要替换的内容数组。																														       *
		//**************************************************************************************************
		
		//**************************************举例说明***********************************************************************
		//*假设您用测试Demo的APP ID，则需使用默认模板ID 1，发送手机号是13800000000，传入参数为6532和5，则调用方式为           *
		//*result = restAPI.sendTemplateSMS("13800000000","1" ,new String[]{"6532","5"});																		  *
		//*则13800000000手机号收到的短信内容是：【云通讯】您使用的是云通讯短信模板，您的验证码是6532，请于5分钟内正确输入     *
		//*********************************************************************************************************************
		if(templateId==null)
		{
			templateId ="1";
		}
		result = restAPI.sendTemplateSMS(telephone,templateId ,params);
		
		StringBuffer content=new StringBuffer();
		for (String param : params) {
			content.append(param);
			content.append(",");
		}
		smsLogService.log(ReqUtil.instance.getUserId()+"", telephone, content.toString(),result.toString());
		
		System.out.println("SDKTestGetSubAccounts result=" + result);
		Result s = new Result();
		s.setRes_code((String)result.get("statusCode"));
		s.setRes_message((String)result.get("statusMsg"));
		if("000000".equals(result.get("statusCode"))){
//			//正常返回输出data包体信息（map）
			s.setRes_code("true");
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			
			s.setDateCreated((String)data.get("dateCreated"));
		}else{
			s.setRes_code("false");
			s.setRes_message((String)result.get("statusMsg"));
			//异常返回输出错误码和错误信息
//			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
		}
		
		return s;
	}
	
	private static class Result {
		private String res_code;
		private String res_message;
		private String dateCreated;
//		private String smsMessageSid;
		
		public String getRes_code() {
			return res_code;
		}
		public void setRes_code(String res_code) {
			this.res_code = res_code;
		}
		public String getRes_message() {
			return res_message;
		}
		public void setRes_message(String res_message) {
			this.res_message = res_message;
		}
		public String getDateCreated() {
			return dateCreated;
		}
		public void setDateCreated(String dateCreated) {
			this.dateCreated = dateCreated;
		}
//		public String getSmsMessageSid() {
//			return smsMessageSid;
//		}
//		public void setSmsMessageSid(String smsMessageSid) {
//			this.smsMessageSid = smsMessageSid;
//		}
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getAccount_sid() {
		return account_sid;
	}

	public void setAccount_sid(String account_sid) {
		this.account_sid = account_sid;
	}

	public String getAuth_token() {
		return auth_token;
	}

	public void setAuth_token(String auth_token) {
		this.auth_token = auth_token;
	}

	public String getZ_app_id() {
		return z_app_id;
	}

	public void setZ_app_id(String z_app_id) {
		this.z_app_id = z_app_id;
	}

	public String getZ_account_sid() {
		return z_account_sid;
	}

	public void setZ_account_sid(String z_account_sid) {
		this.z_account_sid = z_account_sid;
	}

	public String getZ_auth_token() {
		return z_auth_token;
	}

	public void setZ_auth_token(String z_auth_token) {
		this.z_auth_token = z_auth_token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSms_templateId() {
		return sms_templateId;
	}

	public void setSms_templateId(String sms_templateId) {
		this.sms_templateId = sms_templateId;
	}

	public int getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(int activeTime) {
		this.activeTime = activeTime;
	}

	/**
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @author 李淼淼
	 * @date 2015年8月4日
	 */
	@Override
	public boolean sendRandCode(String telephone, String code) {
		String param2 = String.valueOf(activeTime);// 两分钟
		JSONObject jsonObject = baseDataService.toContentAndThirdId("0019", code, param2);
		logger.info("发送的短信验证码的内容是：{}", jsonObject.toJSONString());
		mobSmsSdk.send(telephone, jsonObject.getString("content"), SmsEnum.Signature.yishengquan7.getExt(), jsonObject.getInteger("tencentId"));
		return true;
	}

	@Override
	public boolean sendSMS(String phone, String content) {
		return sendSMS(phone, content, null);
	}

	@Override
	public boolean sendSMS(String phone, String content, String signature) {
		CCPRestSDK restAPI = new CCPRestSDK();

		// ******************************注释*********************************************
		// *初始化服务器地址和端口 *
		// *沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
		// *生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883"); *
		// *******************************************************************************
		restAPI.init(url, port);

		// ******************************注释*********************************************
		// *初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN *
		// *ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
		// *参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。 *
		// *******************************************************************************
		restAPI.setAccount(account_sid, auth_token);

		// ******************************注释*********************************************
		// *初始化应用ID *
		// *测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID *
		// *应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
		// *******************************************************************************
		restAPI.setAppId(app_id);

		// ******************************注释****************************************************************
		// *调用发送模板短信的接口发送短信 *
		// *参数顺序说明： *
		// *第一个参数:是要发送的手机号码，可以用逗号分隔，一次最多支持100个手机号 *
		// *第二个参数:是模板ID，在平台上创建的短信模板的ID值；测试的时候可以使用系统的默认模板，id为1。 *
		// *系统默认模板的内容为“【云通讯】您使用的是云通讯短信模板，您的验证码是{1}，请于{2}分钟内正确输入”*
		// *第三个参数是要替换的内容数组。 *
		// **************************************************************************************************

		// **************************************举例说明***********************************************************************
		// *假设您用测试Demo的APP ID，则需使用默认模板ID 1，发送手机号是13800000000，传入参数为6532和5，则调用方式为
		// *
		// *result = restAPI.sendTemplateSMS("13800000000","1" ,new
		// String[]{"6532","5"}); *
		// *则13800000000手机号收到的短信内容是：【云通讯】您使用的是云通讯短信模板，您的验证码是6532，请于5分钟内正确输入 *
		// *********************************************************************************************************************


//		result = restAPI.sendTemplateSMS(telephone, templateId, new String[]{code,(activeTime)+""});
		mobSmsSdk.send(phone, content, signature);
		return true;
	}
}
