package com.mobsms.sdk;

import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.commons.service.SmsLogService;
import com.dachen.health.commons.vo.User;
import com.dachen.im.server.enums.AppEnum;
import com.dachen.util.HeaderInfo;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * mobsms 短信发送工具
 * 
 * @author vincent
 *
 */
public class MobSmsSdk {
	
	private final static Logger logger = LoggerFactory.getLogger(MobSmsSdk.class);

//	private final static String host = SysConfig.getInstance().getProperty("mobsms.url");
//	
//	private final static String username = SysConfig.getInstance().getProperty("mobsms.username");
//	
//	private final static String password = SysConfig.getInstance().getProperty("mobsms.password");
	
	@Resource
	private SmsLogService smsLogService;

	@Autowired
	private RibbonManager ribbonManager;
	
//	@Resource
//	private UserRepository userRepository;

//	xuanguan("0","【玄关健康】"),
//	kangzhe("1","【康哲公司】"),
//	IBD("2","【IBD中心】"),
//	bodejialian("3","【博德嘉联】"),
//	kangzhenongye("4","【康哲农业】"),
//	yaoqiquan("5","【药企圈】"),
//	yishengquan("6","【医生圈】");
//	yishengquan7("7","【医生圈】");

	/**
	 * <p>短信发送</p>
	 *
	 * <p>修改思路：<br>
	 * 1、如果手机号在用户表中注册了，则在发送短信方法中根据手机号取用户的来源，确定使用哪个通道发短信，如果取不到则默认用玄关。<br>
	 * 2、如果手机号在用户表中未注册，则在发送短信业务调用处取发起人的来源，决定使用哪个通道发短信。</p>
	 * <p>用户的身份确定规则：<br>
	 * 1、一个手机号如果是在客户端注册，则根据不同的端标记此手机是博德的还是玄关的，<br>
	 * 2、如果是邀请注册的，则根据邀请人的身份来确定是哪个来源<br>
	 * 3、每次登陆不同的端，重置用户来源。</p>
	 * <p>其他情形：<br>
	 * 1、如果一个手机号在系统中有多个用户，则取第一个用户的来源（假设有多个用户的话，则认为来源应该相同）<br>
	 * 2、根据不同的来源，短信内容不一样的情况，则需要在调用处通过传不同参数来区分。</p>
	 *
	 * @param phone
	 * @param content
	 * @author 			李淼淼
	 * @date 2015年8月26日
	 */
	public Map<String, String> send(String phone, String content) {

		return send(phone, content, null,ReqUtil.instance.getHeaderInfo());
	}
	public Map<String, String> send(String phone, String content, String signature){
		return send(phone, content, signature,ReqUtil.instance.getHeaderInfo());
	}

	/**
	 * 提供选择不同的通道
	 * @param phone
	 * @param content
	 * @param ext
	 * @param tencentId
	 *
	 * desc: ext = 6 或 ext = 7 都是
	 */
	public void send(String phone, String content, Integer ext, Integer tencentId){
		Long startTime = System.currentTimeMillis();
		try{
			Map<String, String> reqParam = Maps.newHashMap();
			reqParam.put("tel", phone);
			reqParam.put("content", content);
			if(ext == null)ext = 6;
			reqParam.put("ext", String.valueOf(ext));
			if(tencentId != null){
				reqParam.put("tencentId",String.valueOf(tencentId));
			}
			String result = ribbonManager.post("http://SMSSERVICE/inner_api/sendAllByExt",reqParam);
			logger.info("发送短息那结果= {}", result);
		}finally {
			Long endTime = System.currentTimeMillis();
			logger.info(". phone={}, content={}, ext={}, spent {} ms", phone, content, ext, (endTime-startTime));
		}
	}

	public Map<String, String> send(String phone, String content, String signature, Integer tencentId){
		Long startTime = System.currentTimeMillis();
		Map<String, String> ret = new HashMap<>();
		try{
			Map<String, String> map = new HashMap<>();
			map.put("tel", phone);
			map.put("content", content);
			String ext = "0";
			HeaderInfo headerInfo = ReqUtil.instance.getHeaderInfo();
			String appName = headerInfo.getAppName();
			if(AppEnum.XG_YSQ.value().equals(appName) || AppEnum.XG_YSQ_WEB.value().equals(appName))
				ext = "6";
			if(StringUtil.equals(BaseConstants.XG_YSQ_APP, signature))
				ext = "6";
			map.put("ext", ext);
			if(tencentId != null){
				map.put("tencentId",tencentId+"");
			}
			String result = ribbonManager.post("http://SMSSERVICE/inner_api/sendAllByExt",map);
			ret.put("result", result);
		}finally {
			Long endTime = System.currentTimeMillis();
			logger.info(". phone={}, content={}, signature={}, spent {} ms", phone, content, signature, (endTime-startTime));
		}
		return ret;
	}


	public Map<String, String> send(String phone, String content, HeaderInfo headerInfo){
		return send(phone, content, null,headerInfo);
	}
	public Map<String, String> send(String phone, String content, String signature, HeaderInfo headerInfo) {
		Long startTime = System.currentTimeMillis();
		Map<String, String> ret = new HashMap<>();
		try {
			Map<String, String> map = new HashMap<>();
			map.put("tel", phone);
			map.put("content", content);
			String ext = "0";
			if (headerInfo == null)
				headerInfo = ReqUtil.instance.getHeaderInfo();
			String appName = headerInfo.getAppName();
			if (AppEnum.XG_YSQ.value().equals(appName) || AppEnum.XG_YSQ_WEB.value().equals(appName))
				ext = "6";
			/**
			 * 本需求很无奈
			 * 通过医生端的web管理后台发送的部分短信也需要将signature 修改为医生圈 /(ㄒoㄒ)/~~
			 */
			if (StringUtil.equals(BaseConstants.XG_YSQ_APP, signature))
				ext = "6";
			map.put("ext", ext);
			String result = ribbonManager.post("http://SMSSERVICE/inner_api/sendAllByExt", map);
			ret.put("result", result);
		} finally {
			Long endTime = System.currentTimeMillis();
			logger.info(". phone={}, content={}, signature={}, spent {} ms", phone, content, signature, (endTime - startTime));
		}

		return ret;
	}
	
	public static void main(String[] args) {
		Map<String, String> data = new MobSmsSdk().send("13145835257", "你好 ");
		System.out.println(data);
	}

	public boolean isBDJL(User user) {
		return false;
	}

	public boolean isBDJL(String telephone) {
		return false;
	}
}
