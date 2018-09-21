package com.dachen.health.controller.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.JSONMessage;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.manager.IConferenceManager;
import com.dachen.manager.ISMSManager;
import com.dachen.util.RedisUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;
import com.ucpaas.restsdk.UcPaasRestSdk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("monitor")
public class MessageController {

	@Autowired
    private MobSmsSdk mobSmsSdk;
	@Autowired
	private IConferenceManager conferenceManager;
	@Autowired
	private ISMSManager smsManger;	
	@Autowired 
    private IBaseDataService baseDataService;
	
	
	/**
     * @api {get} /monitor/message 三方服务监控
     * @apiVersion 1.0.0
     * @apiName message
     * @apiGroup 服务监控
     * @apiDescription 三方服务（短信，语音，三方通话，双向回拔）监控
     *
     * @apiParam   {String}     access_token                token
     *
     * @apiSuccess {Integer}    smsCode                     短信响应码（0：成功，31：相同手机号重复提交，33：长度过大，37：余额不足，38：未知失败）
     * @apiSuccess {String}     smsDesc                   	短信响应码注解
     * @apiSuccess {String}     voiceCode                   语音响应码（0：成功，37：余额不足，38：请求失败）
     * @apiSuccess {String}     voiceDesc                	语音响应码注解
     * @apiSuccess {String}     conCode                     三方通话响应码（0：成功，37：余额不足，38：请求失败，30：超时未获取对应回调值异常，3000：请求第三方服务异常）
     * @apiSuccess {Integer}    conDesc                   	三方通话响应码注解
     * @apiSuccess {Integer}    callBackCode                双向回拔响应码（0：成功，37：余额不足，38：未知失败）
     * @apiSuccess {String}    callBackDesc                 双向回拔响应码注解
     *
     * @apiAuthor  张垠
     * @date 2016年4月14日
     */
	@RequestMapping("message")
	public JSONMessage getMessageAndVoiceService(){
		Map<String,String> result = new HashMap<String,String>();
		String tel = "13316827932";
		//短信
		sendSmsMessage(tel,result);
		//语音
		sendVoiceMessage(tel,result);
		//三方通话
		createConference(result);
		//双向通话
		callBack(result);
		
		return JSONMessage.success(null, result);
	}
	
	
	/**
     * @api {get} /monitor/service 健康监控
     * @apiVersion 1.0.0
     * @apiName service
     * @apiGroup 服务监控
     * @apiDescription 仅对服务是否可用监控，不对业务监控
     *
     * @apiSuccess {Integer}    serCode                     短信响应码（0：服务可用，38：未知失败）
     * @apiSuccess {String}     serDesc                   	短信响应码注解
     *
     * @apiAuthor  张垠
     * @date 2016年4月14日
     */
	@RequestMapping("service")
	public JSONMessage getHealthServiceNoToken(){
		Map<String,String> map = new HashMap<String,String>();
		
		try {
			baseDataService.getTitles();
			map.put("serCode", "0");
			map.put("serDesc", "服务正常");
		} catch (Exception e) {
			map.put("serCode", "38");
			map.put("serDesc", "未知异常"+e.getMessage());
		}
		
		return JSONMessage.success(null, map);
	}
	
	private void sendSmsMessage(String tel,Map<String,String> map){
		String code = StringUtil.random4Code();
		Map<String,String> maps = mobSmsSdk.send(tel, code);
		String str = maps.get("result");
		if(StringUtil.isEmpty(str)){
			map.put("smsCode", "38");
			map.put("smsDesc", "未知异常失败");
		}else{
			long resultCode = Long.valueOf(str);
			if(resultCode == -4){
				map.put("smsCode", "37");
				map.put("smsDesc", "余额不足");
			}else if(resultCode == -10){
				map.put("smsCode", "33");
				map.put("smsDesc", "长度过大");
			}else if(resultCode == -20){
				map.put("smsCode", "31");
				map.put("smsDesc", "相同手机号，相同内容重复提交");
			}else if(resultCode > 1){
				map.put("smsCode", "0");
				map.put("smsDesc", "成功");
			}else{
				map.put("smsCode", "38");
				map.put("smsDesc", "未知失败，失败码："+str);
			}
			
		}
	}
	
	private void sendVoiceMessage(String tel,Map<String,String> map){
		String code = StringUtil.random4Code();
		String result = smsManger.getVoiceResult(tel, code);
		
		JSONObject json = JSON.parseObject(result);
		json = JSON.parseObject(json.getString("resp"));
		if(json.getString("respCode").equals("000000")){
			map.put("voiceCode", "0");
			map.put("voiceDesc", "语音验证码请求成功");
		}else if("100001".equals(json.getString("respCode"))){
			//余额不足
			map.put("voiceCode","37");
			map.put("voiceDesc", "余额不足创建失败");
		}else{
			map.put("voiceCode", "38");
			map.put("voiceDesc", "语音验证码请求失败");
		}
	}
	
	private void callBack(Map<String,String> map){
		JSONObject ret =UcPaasRestSdk.callback("","", "","","");
		if("000000".equals(((JSONObject)ret.get("resp")).get("respCode"))){
			map.put("callBackCode", "0");
			map.put("callBackDesc", "拨打成功");
			
		}else if("100001".equals(((JSONObject)ret.get("resp")).get("respCode"))){
			//余额不足
			map.put("callBackCode","37");
			map.put("callBackDesc", "余额不足创建失败");
		}else{
			map.put("callBackCode", "38");
			map.put("callBackDesc", "拨打失败");
		}
	}
	
	private void createConference(Map<String,String> map){
		
		Map<String,Object> maps = conferenceManager.createConference(3, 3, 0);
		String confId = "";
		boolean createResult = false;
		//请求
		for(int i = 0; i<3; i++){
			if(maps.get("code").equals("0")){
				
				if (confId.equals("0")) {
					maps = conferenceManager.createConference(3, 3, 0);
					continue;
				}
				confId = maps.get("confId").toString();
				Map<String, String> smap = new HashMap<String,String>();
				smap.put("status", 100+"");//默认100为创建会议中
				RedisUtil.hmset(confId, smap,5*60*1000);
				createResult = true;
				break;
			}else{
				 maps = conferenceManager.createConference(3, 3, 0);
			}
		}
		
		//处理
		if(createResult){
			long currentTime = System.currentTimeMillis();
			while(System.currentTimeMillis() - currentTime < 20*1000){
				Map<String, String> result =RedisUtil.hgetAll(confId);
				if(result.get("status").equals("100")){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					if(result.get("status").equals("0")){
						//成功
						conferenceManager.dismissConference(confId);//解散会议
						map.put("conCode","0");
						map.put("conDesc", "服务正常");
					}else if(result.get("status").equals("37")){
						//余额不足
						map.put("conCode","37");
						map.put("conDesc", "余额不足创建失败");
					}else{
						//未知原因
						map.put("conCode","38");
						map.put("conDesc", "未知原因创建失败");
					}
					return ;
				}
			}
			
			map.put("conCode","30");
			map.put("conDesc", "超时未获取对应回调值异常");
		}else{
			map.put("conCode", "3000");
			map.put("conDesc", "请求第三方服务异常");
		}
	}

}
