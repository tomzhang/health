package com.dachen.health.user;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventProducer;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.support.spring.SpringBeansUtils;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.mq.MQConstant;
import com.dachen.mq.producer.BasicProducer;
import com.dachen.pub.model.param.PubParam;
import com.dachen.pub.service.PubAccountService;
import com.dachen.pub.util.PubUtils;
import com.dachen.util.QiniuUtil;
import com.dachen.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserInfoNotify {
	private static Logger log = LoggerFactory.getLogger(UserInfoNotify.class);
	
	public static void notifyUserUpdate(int userId) {
		List<String> userList = new ArrayList<>();
		userList.add(String.valueOf(userId));
		BasicProducer.fanoutMessage(MQConstant.EXCHANGE_USER_CHANGE, JSON.toJSONString(userList));
//		EcEvent event = EcEvent.build(EventType.UserBaseInfoUpdated).param(
//				"userList", JSON.toJSONString(userList));
//		EventProducer.fireEvent(event);
	}

	public static void notifyUserRegister(User user) {
		log.info("用户注册MQ:{},json:{}", UserEnum.DOCTOR_INFO_REGISTER, "user = [" + JSON.toJSONString(user) + "]");
		BasicProducer.fanoutMessage(UserEnum.DOCTOR_INFO_REGISTER, JSON.toJSONString(user));
	}
	
	public static void clearUserToken(List<String> tokenList) {
		EcEvent event = EcEvent.build(EventType.UserTokenInvalid).param(
				"tokenList", JSON.toJSONString(tokenList));
		EventProducer.fireEvent(event);
	}
	public static void notifyPatientMessageUpdate(int userId) {
		List<String> userList = new ArrayList<String>();
		userList.add(String.valueOf(userId));
		
		String jsonUserId = JSON.toJSONString(userList);
		log.info("fanoutMessage to {},{}",MQConstant.EXCHANGE_PATIENT_MESSAGE_CHANGE,jsonUserId);
		BasicProducer.fanoutMessage(MQConstant.EXCHANGE_PATIENT_MESSAGE_CHANGE, jsonUserId);
	}
	/**
	 * 用户头像修改
	 * @param userId
	 * @param bucket 七牛空间名
	 * @param key 对应的是key而不是url。（去掉域名）
	 */
	public static void notifyUserPicUpdate(User user, String bucket) {
		Integer userId = user.getUserId();
		Integer userType = user.getUserType();
		String headPicFileName = user.getHeadPicFileName();
		if (StringUtil.isBlank(headPicFileName))
		{
			return;
		}
		if(userType==UserEnum.UserType.doctor.getIndex()){
			try{
				PubParam pubParam = new PubParam();
				pubParam.setPid(PubUtils.PUB_DOC_PREFIX+userId);
				pubParam.setPhotourl(headPicFileName);
				pubParam.setName("医生公众号_"+user.getName());//
				pubParam.setNickName(user.getName());
				PubAccountService pubAccountService = SpringBeansUtils.getBeane(PubAccountService.class);
				pubAccountService.savePub(pubParam);
			}
			catch(Exception e){
			}
		}
		
		String key = "";
		if (headPicFileName.contains(QiniuUtil.QINIU_DOMAIN())) {
			String[] arr = headPicFileName.split(QiniuUtil.QINIU_DOMAIN());
			key = arr[arr.length-1];//此时key前面多了一个"/"
			if (StringUtil.isNotBlank(key)) {
				key = key.replaceFirst("/", "");
			}
		}
		if (StringUtil.isBlank(key) && headPicFileName.contains("/")) {
			String[] arr = headPicFileName.split("/");
			key = arr[arr.length-1];
		}
		
//		EcEvent event = EcEvent.build(EventType.UserPicUpdated)
//				.param("userId", userId)
//				.param("userType", userType)
//				.param("bucket", bucket)
//				.param("key",key);
//		EventProducer.fireEvent(event);
	}
	
	/**
	 * 用户注册成功
	 * @param userId
	 */
	public static void registerSuccessNotify(Integer userId) {
		EcEvent event = EcEvent.build(EventType.UserRegisterSuccess)
				.param("userId", userId);
		EventProducer.fireEvent(event);
	}
	
	/**
	 *  同步修改患者本人信息
	 * @param userId
	 */
	public static void patientInfoModifyNotify(Integer userId) {
		EcEvent event = EcEvent.build(EventType.User1InfoUpdated)
				.param("userId", userId);
		EventProducer.fireEvent(event);
	}
	
	/**
	 * 用户激活成功
	 * @param userId
	 */
	public static void userActivateNotify(Integer userId) {
		EcEvent event = EcEvent.build(EventType.UserActivateSuccess)
				.param("userId", userId);
		EventProducer.fireEvent(event);
	}
	
}
