package com.dachen.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.exception.ServiceException;
import com.dachen.util.JSONUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.StringUtil;
import com.ucpaas.restsdk.UcPaasRestSdk;
import com.ucpaas.restsdk.models.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConferenceManager implements IConferenceManager {
	private String z_app_id;
	private String z_account_sid;
	private String z_auth_token ;

	private static String accountSid() {
        return PropertiesUtil.getContextProperty(
                "accountSid");
    }
	private static String authToken(){
        return PropertiesUtil.getContextProperty(
                "authToken");
    }
	private static String appId() {
        return PropertiesUtil.getContextProperty("appId");
    }

	
	@Override
	public Map<String,Object> createConference(Integer maxMember, Integer duration, Integer playTone) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
//			String result = UcPaasRestSdk.createConference(true,z_account_sid,z_auth_token,z_app_id,maxMember,duration,playTone);
			String result = UcPaasRestSdk.createConference(true,accountSid(),authToken(),appId(),maxMember,duration,playTone);
			JSONObject json = JSON.parseObject(result);
			json = JSON.parseObject(json.getString("resp"));
			map.put("code", json.get("respCode"));
			map.put("create", false);
			map.put("reason", "请求失败");
			map.put("confId", "0");
			if(json.get("respCode").equals("0")){
				map.put("confId", json.get("confId").toString());
				map.put("create", true);
				map.put("reason", "请求成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public Map<String,Object> removeConference(String confId, String callId) {
		Map<String, Object> map = new HashMap<String, Object>();
//		String result = UcPaasRestSdk.removeConference(true,z_account_sid,z_auth_token,z_app_id, confId, callId);
		String result = UcPaasRestSdk.removeConference(true,accountSid(),authToken(),appId(),confId, callId);
		JSONObject json = JSON.parseObject(result);
		json = JSON.parseObject(json.getString("resp"));
		map.put("code", json.get("respCode"));
		map.put("remove", false);
		map.put("reason", "请求失败");
		if(json.get("respCode").equals("0")){
			map.put("remove", true);
			map.put("reason", "请求成功");
		}
		return map;
	}
	
	@Override
	public Map<String,Object> dismissConference(String confId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtil.isEmpty(confId)){
				throw new ServiceException("会议ID不能为空");
			}
//			String result = UcPaasRestSdk.dismissConference(true,z_account_sid,z_auth_token,z_app_id,confId);
			String result = UcPaasRestSdk.dismissConference(true,accountSid(),authToken(),appId(),confId);
			JSONObject json = JSON.parseObject(result);
			json = JSON.parseObject(json.getString("resp"));
			map.put("code", json.get("respCode"));
			map.put("dismiss", false);
			map.put("reason", "请求失败");
			if(json.get("respCode").equals("0")){
				map.put(" dismiss", true);
				map.put("reason", "请求成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public Map<String,Object> inviteConference(String confId, List<String> list){
//		该接口为异步接口，操作结果通过“成员加入”通知。
//		说明：
//		①会议中可以无主持人角色，也支持1个或多个主持人存在；
//		②若当前会议中无主持人，则不能使用成员静音、禁听、会议放音、录音等会控功能；
//		③若需要调用这些接口进行会议控制管理时，则必须保证会议中至少存在一个主持人角色的成员；
//		④会议中只要存在主持人角色，所有与会成员都可以调用上述的会控接口，也就是说，会控能力并非是主持人角色的成员所具有的能力，开发者可以控制应用进行权限分配；
		if(StringUtil.isEmpty(confId)){
			throw new ServiceException("会议标识不能为空");
		}
		if(list == null || list.isEmpty()){
			throw new ServiceException("邀请人不能为空");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Member> memberList = new ArrayList<Member>();
			for(String str : list){
				Member member = JSONUtil.parseObject(Member.class, str);
				memberList.add(member);
			}
			if(StringUtil.isEmpty(confId)){
				throw new ServiceException("会议ID不能为空");
			}
			if(list == null || list.isEmpty()){
				throw new ServiceException("邀请人不能为空");
			}
//			String result = UcPaasRestSdk.inviteConference(true, z_account_sid, z_auth_token, z_app_id, confId, memberList);
			String result = UcPaasRestSdk.inviteConference(true,accountSid(),authToken(),appId(), confId, memberList);
			JSONObject json = JSON.parseObject(result);
			json = JSON.parseObject(json.getString("resp"));
			map.put("code", json.get("respCode"));
			map.put("invite", false);
			map.put("reason", "请求失败");
			if(json.get("respCode").equals("0")){
				map.put("invite", true);
				map.put("reason", "请求成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	
	@Override
	public JSONMessage muteConference(String confId, String callId) {
		JSONMessage jMessage;
		try {
			if(StringUtil.isEmpty(confId)){
				throw new ServiceException("会议标识不能为空");
			}
			if(StringUtil.isEmpty(callId)){
				throw new ServiceException("静音者号码不能为空");
			}
			
//			String result = UcPaasRestSdk.muteConference(true, z_account_sid, z_auth_token, z_app_id, confId, callId);
			String result = UcPaasRestSdk.muteConference(true,accountSid() , authToken(), appId(), confId, callId);
			JSONObject json = JSON.parseObject(result);
			json = JSON.parseObject(json.getString("resp"));
			if(json.get("respCode").equals("0")){
				jMessage = JSONMessage.success("静音成功");
			}else {
				jMessage = JSONMessage.success("静音失败");
			}
			jMessage.put("respCode", json.get("respCode"));
			
		} catch (Exception e) {
			jMessage = Constants.Result.InternalException;
		}
		return jMessage;
	}

	@Override
	public JSONMessage unMuteConference(String confId, String callId) {
		JSONMessage jMessage;
		try {
			if(StringUtil.isEmpty(confId)){
				throw new ServiceException("会议标识不能为空");
			}
			if(StringUtil.isEmpty(callId)){
				throw new ServiceException("取消静音者号码不能为空");
			}
			
//			String result = UcPaasRestSdk.unMuteConference(true, z_account_sid, z_auth_token, z_app_id, confId, callId);
			String result = UcPaasRestSdk.unMuteConference(true, accountSid(), authToken(), appId(), confId, callId);
			JSONObject json = JSON.parseObject(result);
			json = JSON.parseObject(json.getString("resp"));
			if(json.get("respCode").equals("0")){
				jMessage = JSONMessage.success("取消静音成功");
			}else {
				jMessage = JSONMessage.success("取消静音失败");
			}
			jMessage.put("respCode", json.get("respCode"));
			
		} catch (Exception e) {
			jMessage = Constants.Result.InternalException;
		}
		return jMessage;
	}
	
	@Override
	public Map<String,Object> deafConference(String confId, String callId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtil.isEmpty(confId)){
				throw new ServiceException("会议标识不能为空");
			}
			if(StringUtil.isEmpty(callId)){
				throw new ServiceException("禁听者号码不能为空");
			}
			
//			String result = UcPaasRestSdk.deafConference(true, z_account_sid, z_auth_token, z_app_id, confId, callId);
			String result = UcPaasRestSdk.deafConference(true, accountSid(), authToken(), appId(), confId, callId);
			JSONObject json = JSON.parseObject(result);
			json = JSON.parseObject(json.getString("resp"));
			map.put("code", json.get("respCode"));
			map.put("deaf", false);
			map.put("reason", "请求失败");
			if(json.get("respCode").equals("0")){
				map.put("reason", "请求成功");
				map.put("deaf", true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public Map<String,Object> unDeafConference(String confId, String callId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtil.isEmpty(confId)){
				throw new ServiceException("会议标识不能为空");
			}
			if(StringUtil.isEmpty(callId)){
				throw new ServiceException("禁听者号码不能为空");
			}
			
//			String result = UcPaasRestSdk.unDeafConference(true, z_account_sid, z_auth_token, z_app_id, confId, callId);
			String result = UcPaasRestSdk.unDeafConference(true, accountSid(), authToken(), appId(), confId, callId);
			JSONObject json = JSON.parseObject(result);
			json = JSON.parseObject(json.getString("resp"));
			map.put("code", json.get("respCode"));
			map.put("unDeaf", false);
			map.put("reason", "请求失败");
			if(json.get("respCode").equals("0")){
				map.put("reason", "请求成功");
				map.put("unDeaf", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public Map<String, Object> queryConference(String confId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtil.isEmpty(confId)){
				throw new ServiceException("会议标识不能为空");
			}
//			String result = UcPaasRestSdk.queryConference(true, z_account_sid, z_auth_token, z_app_id, confId);
			String result = UcPaasRestSdk.queryConference(true, accountSid(), authToken(), appId(), confId);
			JSONObject json = JSON.parseObject(result);
			json = JSON.parseObject(json.getString("resp"));
			map.put("code", json.get("respCode"));
			map.put("unDeaf", false);
			map.put("reason", "请求失败");
			if(json.get("respCode").equals("0")){
				map.put("reason", "请求成功");
				map.put("unDeaf", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public Map<String,Object> recordConference(String confId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtil.isEmpty(confId)){
				throw new ServiceException("会议标识不能为空");
			}
			
//			String result = UcPaasRestSdk.recordConference(true, z_account_sid, z_auth_token, z_app_id, confId);
			String result = UcPaasRestSdk.recordConference(true, accountSid(), authToken(), appId(), confId);
			JSONObject json = JSON.parseObject(result);
			json = JSON.parseObject(json.getString("resp"));
			map.put("code", json.get("respCode"));
			map.put("record", false);
			map.put("reason", "请求失败");
			if(json.get("respCode").equals("0")){
				map.put("record", true);
				map.put("reason", "请求成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public Map<String,Object> stopRecordConference(String confId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtil.isEmpty(confId)){
				throw new ServiceException("会议标识不能为空");
			}
			
//			String result = UcPaasRestSdk.stopRecordConference(true, z_account_sid, z_auth_token, z_app_id, confId);
			String result = UcPaasRestSdk.stopRecordConference(true, accountSid(), authToken(), appId(), confId);
			JSONObject json = JSON.parseObject(result);
			json = JSON.parseObject(json.getString("resp"));
			map.put("code", json.get("respCode"));
			map.put("stopRecord", false);
			map.put("reason", "请求失败");
			if(json.get("respCode").equals("0")){
				map.put("stopRecord", true);
				map.put("reason", "请求成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
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
}
