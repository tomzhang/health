package com.dachen.health.common.controller;

import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.Constants.ResultCode;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.service.AdminManager;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.manager.ISMSManager;
import com.dachen.medice.vo.AccessConfig;
import com.dachen.mq.producer.BasicProducer;
import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.util.CheckUtils;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Maps;
import com.qiniu.util.UrlSafeBase64;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {
    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);
	
	@Autowired
	private AdminManager adminManager;
	
	@Autowired
	private ISMSManager smsManager;

	@Autowired
	private UserManager userManager;
	
	@Autowired
	private IGroupDao groupDao;

    @Resource
    protected AsyncTaskPool asyncTaskPool;

    @Autowired
    private AccessConfig accessConfig;
	
	/**
     * @api  {get，post} /sms/randcode/getSMSCode 获取4位验证码
     * @apiVersion 1.0.0
     * @apiName getSMSCode
     * @apiGroup 验证
     * @apiDescription 获取4位验证码
     * @apiParam  {String}    telephone             手机号码
     * @apiParam  {String}    templateId            模板ID，非必填
     * 
     * @apiAuthor  张垠
     * @date 2015年11月6日
     */
	@RequestMapping("/sms/randcode/getSMSCode")
	public JSONMessage getSMSCode(@RequestParam String telephone,String templateId) {
		if (!ReqUtil.isAllowAccess(accessConfig)) {
			return JSONMessage.success("验证码发送成功!");
		}

		// templateId都为1 已发版bug后台处理
		templateId ="1";
		return smsManager.getSMSCode(telephone,templateId);
	}
	
	/**
     * @api  {get，post} /sms/randcode/getVoiceCode 获取4位语音验证码
     * @apiVersion 1.0.0
     * @apiName getVoiceCode
     * @apiGroup 验证
     * @apiDescription 获取4位语音验证码
     * @apiParam  {String}    telephone             手机号码
     * @apiParam  {String}    templateId          	模板ID，非必填
     * 
     * @apiAuthor  张垠
     * @date 2015年11月6日
     */
	@RequestMapping("/sms/randcode/getVoiceCode")
	public JSONMessage getVoiceCode(@RequestParam String telephone,String templateId){
		if (!ReqUtil.isAllowAccess(accessConfig)) {
			return JSONMessage.success("验证码拨打成功!");
		}

		// templateId都为1 已发版bug后台处理
		templateId ="1";
		return smsManager.getVoiceCode(telephone, templateId);
	}
	
	/**
     * @api  {get，post} /sms/randcode/verifyCode 校验验证码
     * @apiVersion 1.0.0
     * @apiName verifyCode
     * @apiGroup 验证
     * @apiDescription 校验验证码
     * @apiParam  {String}    telephone             手机号码
     * @apiParam  {String}    randcode              验证码
     * @apiParam  {String}    templateId            模板ID，非必填
     * 
     * @apiAuthor  张垠
     * @date 2015年11月6日
     */
	@RequestMapping("/sms/randcode/verifyCode")
	public JSONMessage verifyCode(@RequestParam String telephone,@RequestParam String randcode,String templateId) {
		// templateId都为1 已发版bug后台处理
		templateId ="1";
		boolean isOK = smsManager.isAvailable(telephone,randcode,templateId);
		JSONMessage jMessage;
		if(isOK)
		{
			jMessage = JSONMessage.success();
		}
		else
		{
			jMessage = JSONMessage.failure("验证码错误！");
		}
		return jMessage;
	}
	
	
	/**
     * @api  {get，post} /sms/verify/isJoinHospital 验证医生是否已经加入了别的医院
     * @apiVersion 1.0.0
     * @apiName isJoinHospital
     * @apiGroup 验证
     * @apiDescription 验证医生是否已经加入了别的医院
     * @apiParam  {String}    telephone             手机号码
     * 
     * @apiAuthor  姜宏杰
     * @date 2016年4月26日11:24:22
     */
	@RequestMapping(value = "/sms/verify/isJoinHospital")
	public JSONMessage isJoinHospital(@RequestParam(value = "telephone", required = true) String telephone) {
		if (!CheckUtils.checkMobile(telephone)) {
			return JSONMessage.failure("手机号码格式不正确");
		}
		User user = userManager.getUser(telephone, UserEnum.UserType.doctor.getIndex());
		if(user == null ){
			return JSONMessage.failure("手机号未注册");
		}else{
			//设置用户信息
			Map<String ,Object> retMap = new HashMap<String, Object>();
			retMap.put("doctorId", user.getUserId());
			retMap.put("telephone", user.getTelephone());
			retMap.put("name", user.getName());
			retMap.put("userStatues", user.getStatus());
			if(UserEnum.UserStatus.Unautherized.getIndex()==user.getStatus()){
				return  new JSONMessage(0, "手机号已注册未认证",retMap);
			}else{
				GroupDoctor checkGroup = groupDao.checkDoctor(user.getUserId());
				if(checkGroup!=null){
					Group gp = groupDao.getById(checkGroup.getGroupId());
					return  new JSONMessage(100, "该医生已经加入："+gp.getName());
				}else{
					return  JSONMessage.success("可以邀请！！！");
				}
			}
		}
		}

	/**
	 * @api  {get} /sms/verify/isJoinDept 验证医生是否已经加入了别的科室
	 * @apiVersion 1.0.0
	 * @apiName /sms/verify/isJoinDept
	 * @apiGroup 验证
	 * @apiDescription 验证医生是否已经加入了别的科室
	 *
	 * @apiParam  {String}    telephone             手机号码
	 * @apiParam  {String}    groupId				科室Id
	 * @apiParam {Integer}  inviterId       邀请人id
	 * @apiParam {Integer}  subsystem       来源子系统（医生圈-17、药企圈-16）
	 * @apiParam {String}   way             邀请方式（短信-sms，微信-wechat，二维码-qrcode）
	 *
	 * @apiSuccess {String} resultCode 返回状态码
	 *
	 * @apiAuthor  钟良
	 * @date 2017年6月1日
	 */
	@RequestMapping(value = "/sms/verify/isJoinDept", method = RequestMethod.GET)
	public JSONMessage isJoinDept(@RequestParam(value = "telephone") String telephone,
		@RequestParam(value = "groupId") String groupId, @RequestParam(value = "inviterId") Integer inviterId,
		@RequestParam(value = "subsystem") Integer subsystem, @RequestParam(value = "way") String way) {
		if (!CheckUtils.checkMobile(telephone)) {
			return JSONMessage.failure("手机号码格式不正确");
		}
		//被邀请人
		User user = userManager.getUser(telephone, UserEnum.UserType.doctor.getIndex());
		if (user == null) {
			return JSONMessage.failure("手机号未注册");
		} else {
			//设置用户信息
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("doctorId", user.getUserId());
			retMap.put("telephone", user.getTelephone());
			retMap.put("name", user.getName());
			retMap.put("userStatues", user.getStatus());
			if (UserEnum.UserStatus.normal.getIndex() != user.getStatus()) {
				// 手机号已经注册但是未认证通过的医生，如果收到了科室邀请，下次认证通过后需求自动加入科室，这里需要做个标记，标记为已被科室邀请过
				userManager.updateInvitationInfo(user.getUserId(), inviterId, subsystem, way, Boolean.TRUE);
				return new JSONMessage(98, "手机号已注册未认证", retMap);
			} else {
				Group joinGroup = groupDao.getById(groupId);
				if (joinGroup == null) {
					throw new ServiceException("科室Id不正确");
				}

				//判断医生的执业信息中的科室与待加入的科室是否一致
				if (user.getDoctor() != null && StringUtil.isNoneBlank(user.getDoctor().getDeptId())) {
					if (!user.getDoctor().getDeptId().equals(joinGroup.getDeptId())) {
						return new JSONMessage(99, "您的执业医院科室与该科室不一致，无法加入科室。");
					}
				}

				GroupDoctor checkGroup = groupDao.checkDept(user.getUserId());
				if (checkGroup != null) {
					Group gp = groupDao.getById(checkGroup.getGroupId());
					return new JSONMessage(100, "您已加入" + gp.getName() + "，请先退出已有科室，才能加入该科室。");
				} else {
					return JSONMessage.success("可以邀请！！！");
				}
			}
		}
	}

	/**
	 * 验证手机是否被注册
	 * @param telephone
	 * @return
	 */
	@RequestMapping(value = "/sms/verify/telephone")
	public JSONMessage virifyTelephone(@RequestParam(value = "telephone", required = true) String telephone,
			int userType) {
		boolean isCheck = userManager.isRegister(telephone, userType);

		if (!CheckUtils.checkMobile(telephone)) {
			throw new ServiceException("手机号码格式不正确");
		}

		if (UserEnum.UserType.enterpriseUser.getIndex() == userType) {

			User inactionUser = userManager.getUser(telephone, userType);
			if (null == inactionUser) {
				return new JSONMessage(100, "注册失败，请联系企业管理员！");
			} else {
				return JSONMessage.success("该手机号码未注册");
			}

		} else if (UserEnum.UserType.doctor.getIndex() == userType) {
			if (!isCheck) {
				return JSONMessage.success("该手机号码未注册");
			} else {
				User user = userManager.getUser(telephone, userType);
				// 设置用户信息
				Map<String, Object> retMap = new HashMap<String, Object>();
				retMap.put("doctorId", user.getUserId());
				retMap.put("telephone", user.getTelephone());
				retMap.put("name", user.getName());
				retMap.put("userStatues", user.getStatus());
				// if(StringUtils.isEmpty(user.getName())){
				if (UserEnum.UserStatus.Unautherized.getIndex() == user.getStatus()) {
					return new JSONMessage(0, "该手机号码已注册未认证", retMap);
				} else {
					return new JSONMessage(0, "该手机号码已注册", retMap);
				}
			}

		} else {
			if (isCheck) {
				return JSONMessage.failure("该手机号码已注册");
			} else {
				return JSONMessage.success("该手机号码未注册");
			}
		}
	}

    /**
     * 验证手机是否被注册，如果注册了就发送mq消息，药企圈消费此消息
     * @param telephone 手机号
     * @param inviterId 邀请人
     * @return
     */
    @RequestMapping(value = "/sms/verifyTelephoneAndSendMsgToMq")
    public JSONMessage verifyTelephoneAndSendMsgToMq(@RequestParam(value = "telephone") String telephone,
        @RequestParam("inviterId") Integer inviterId) {
        if (!CheckUtils.checkMobile(telephone)) {
            throw new ServiceException("手机号码格式不正确");
        }
        boolean isCheck = userManager.isRegister(telephone, UserType.doctor.getIndex());

        if (!isCheck) {
            return JSONMessage.success("该手机号码未注册");
        } else {
            User user = userManager.getUser(telephone, UserType.doctor.getIndex());

            //2、药企圈邀请医生注册，如果是已注册的医生，则直接加为好友
            // 而加好友是往mq中写消息，药企圈消费此消息加好友
            notifyDrugOrgAsync(inviterId, user.getUserId());

            // 设置用户信息
            Map<String, Object> retMap = new HashMap<String, Object>();
            retMap.put("doctorId", user.getUserId());
            retMap.put("telephone", user.getTelephone());
            retMap.put("name", user.getName());
            retMap.put("userStatues", user.getStatus());
            // if(StringUtils.isEmpty(user.getName())){
            if (UserEnum.UserStatus.Unautherized.getIndex() == user.getStatus()) {
                return new JSONMessage(0, "该手机号码已注册未认证", retMap);
            } else {
                return new JSONMessage(0, "该手机号码已注册", retMap);
            }
        }
    }

    private void notifyDrugOrgAsync(Integer inviterId, Integer doctorId) {
        this.asyncTaskPool.getPool().submit(() -> {
            try {
                // 往mq中写入注册成功信息，药企圈消费此信息
                sendMsgToMq(inviterId, doctorId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    private void sendMsgToMq(Integer inviterId, Integer doctorId) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("representUserId", inviterId);
        dataMap.put("doctorUserId", doctorId);
        BasicProducer.sendMessage("EXCHANGE-INVITE-DOCTOR-REGIST", JSONObject.toJSONString(dataMap));
    }


    /**
	 * @api  {get} /sms/verify/isTelRegistration 验证手机是否被注册
	 * @apiVersion 1.0.0
	 * @apiName /sms/verify/isTelRegistration
	 * @apiGroup 验证
	 * @apiDescription 验证手机是否被注册
	 *
	 * @apiParam {String}	telephone       手机号码
	 *
	 * @apiSuccess {String} resultCode 		返回状态码
	 * @apiSuccess {String} userName 		医生姓名
	 *
	 * @apiAuthor  钟良
	 * @date 2017年6月12日
	 */
	@RequestMapping(value = "/sms/verify/isTelRegistration")
	public JSONMessage isTelRegistration(@RequestParam String telephone) {
		if (!CheckUtils.checkMobile(telephone)) {
			throw new ServiceException("手机号码格式不正确");
		}
		User user = userManager.getUser(telephone, UserEnum.UserType.doctor.getIndex());
		if (user == null) {
			return JSONMessage.failure("该手机号码未注册");
		} else {
			Map<String, String> result = Maps.newHashMap();
			String userName = user.getName();
			if (StringUtils.isNotBlank(userName)){
				String formatStr = getFormatString(userName);
				result.put("userName", formatStr);
				result.put("userId", user.getUserId()+"");
			}
			return JSONMessage.success("该手机号码已注册", result);
		}
	}

	private static String getFormatString(String userName) {
		if (StringUtils.isBlank(userName)) {
			return "";
		}
		String str = "*%s";
		if (userName.length() == 2) {
			str = "*%s";
		}else if (userName.length() == 3) {
			str = "**%s";
		}else if (userName.length() == 4) {
			str = "***%s";
		}else if (userName.length() == 5) {
			str = "****%s";
		}

		return String.format(str, userName.substring(userName.length()-1, userName.length()));
	}



	@RequestMapping(value = "/config")
	public JSONMessage getConfig() {
		/*RedisLock lock = new RedisLock();
		String key = "123456789";
		lock.lock(key, LockType.order);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lock.unlock(key, LockType.order);*/
		return JSONMessage.success(null, adminManager.getConfig());
	}
	
	/**
	 * 获取七牛token
	 * @param bucket
	 * @param key
	 * @return
	 */
	//@RequestMapping(value = "/getQiniuToken")
	public JSONMessage getQiniuToken(String bucket, String key) {
		com.qiniu.util.StringMap putPolicy = new com.qiniu.util.StringMap();
		String fops = "imageView2/5/h/100";
		putPolicy.put("scope", bucket);// 目标空间名
		String smallFileKey = key + "_small";// 目标缩略图文件的key
		String encodedEntryURI = UrlSafeBase64.encodeToString(bucket + ":" + smallFileKey);
		putPolicy.put("persistentOps", fops + "|saveas/" + encodedEntryURI);
		// putPolicy.put("persistentNotifyUrl", "http://fake.com/qiniu/notify");
		putPolicy.put("persistentNotifyUrl", "");
		putPolicy.put("persistentPipeline", "thumbnail");

		String token = QiniuUtil.auth().uploadToken(bucket, key, 3600L, putPolicy);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("upToken", token);
		return JSONMessage.success(map);
	}
	
	public static void main(String[] args) {
		/*ConfigController config = new ConfigController();
		String token = ((Map<String, String>)config.getQiniuToken("avatar", "84a08b7a56774572bcc2067a5972f2ef").get("data")).get("token");
		UploadManager manger = new UploadManager();
		Response res;
		try {
			res = manger.put("E:\\Penguins.jpg", "84a08b7a56774572bcc2067a5972f2ef", token);
			System.out.println(res.bodyString());
		} catch (QiniuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		System.out.println(getFormatString(""));
	}
	
	@RequestMapping(value = "/common/getServerTime")
	public Object getServerTime() throws Exception {
		return JSONMessage.success(null, System.currentTimeMillis());
	}
	
	@RequestMapping(value = "/getContextProperty")
	public Object getContextProperty(String name){
		return JSONMessage.success(PropertiesUtil.getContextProperty(name));
	}
	
	@RequestMapping(value = "/setJesqueTime")
	public JSONMessage setJesqueTime(Long appointTime, Long payTime, Long paidTime) {
		if (appointTime != null) {
			JobTaskUtil.appointTime = appointTime;
		}
		if (payTime != null) {
			JobTaskUtil.payTime = payTime;
		}
		if (paidTime != null) {
			JobTaskUtil.paidTime = paidTime;
		}
		return JSONMessage.success("success");
	}
	
	@ApiOperation(value="发送短信验证码V2", httpMethod="POST", response=JSONMessage.class, notes = "成功返回：\"resultCode：1\"")
    @ApiImplicitParams({
    	@ApiImplicitParam(name="telephone", value="电话号码", required=true, dataType="String", paramType="query"),
    	@ApiImplicitParam(name="templateId", value="模板id", required=false, dataType="String", paramType="query"),
    	@ApiImplicitParam(name="sign", value="签名", required=true, dataType="String", paramType="query")
    })
	@RequestMapping("/nologin/sms/randcode/getSMSCodeV2")
	public JSONMessage getSMSCodeV2(@RequestParam String telephone,String templateId, @RequestParam String sign) {
		if (!ReqUtil.valiSMSSign(telephone, sign, accessConfig)) {
			return JSONMessage.failure("签名错误");
		}
		// templateId都为1 已发版bug后台处理
		templateId ="1";
		return smsManager.getSMSCode(telephone,templateId);
	}
	
	@ApiOperation(value="发送语音验证码V2", httpMethod="POST", response=JSONMessage.class, notes = "成功返回：\"resultCode：1\"")
	@ApiImplicitParams({
    	@ApiImplicitParam(name="telephone", value="电话号码", required=true, dataType="String", paramType="query"),
    	@ApiImplicitParam(name="templateId", value="模板id", required=false, dataType="String", paramType="query"),
    	@ApiImplicitParam(name="sign", value="签名", required=true, dataType="String", paramType="query")
    })
	@RequestMapping("/nologin/sms/randcode/getVoiceCodeV2")
	public JSONMessage getVoiceCodeV2(@RequestParam String telephone, String templateId, @RequestParam String sign){
		if (!ReqUtil.valiSMSSign(telephone, sign, accessConfig)) {
			return JSONMessage.failure("签名错误");
		}
		// templateId都为1 已发版bug后台处理
		templateId ="1";
		return smsManager.getVoiceCode(telephone, templateId);
	}
}
