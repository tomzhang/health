package com.dachen.health.api.client.im.proxy;

import com.dachen.im.server.enums.SysGroupEnum;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class ImApiClientProxy extends HealthApiClientProxy {
	
	
	public Boolean sendUserToUserParam(Integer fromUserId, String gid, String content, Map<String, Object> param) throws HttpApiException {
		return this.sendUserToUserParam(fromUserId, gid, content, param, null);
	}
	
	public Boolean sendUserToUserParam(Integer fromUserId, String gid, String content, Map<String, Object> param, Boolean isPush) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("fromUserId", fromUserId.toString());
		params.put("gid", gid.toString());
		params.put("content", content.toString());
		putJsonStrIfNotBlank(params, "paramJson", param);
		putIfNotBlank(params, "isPush", isPush);
		try {
			String url = "im/sendUserToUserParam";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public Boolean sendUserToUserMsg(Integer fromUserId, String gid, String content) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("fromUserId", fromUserId.toString());
		params.put("gid", gid.toString());
		params.put("content", content.toString());
		try {
			String url = "im/sendUserToUserMsg";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public Boolean sendNotifyMsgToUser(Integer toUserId, String gid, String content) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("toUserId", toUserId.toString());
		params.put("gid", gid.toString());
		params.put("content", content.toString());
		try {
			String url = "im/sendNotifyMsgToUser";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 发送im中提醒内容
	 * @throws HttpApiException 
	 */
	public Boolean sendNotifytoMyMsg(Integer toUserId, String gid, String content) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("toUserId", toUserId.toString());
		params.put("gid", gid.toString());
		params.put("content", content.toString());
		try {
			String url = "im/sendNotifytoMyMsg";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public Boolean drugRemindNotify(Integer userId, Map<String, Object> param) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId.toString());
		putJsonStrIfNotBlank(params, "paramJson", param);
		try {
			String url = "im/drugRemindNotify";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public Boolean sendPushUserToUserParam(Integer fromUserId, String gid, String content, Map<String,Object> param, Integer pushUser) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("fromUserId", fromUserId.toString());
		params.put("gid", gid.toString());
		params.put("content", content.toString());
		putJsonStrIfNotBlank(params, "paramJson", param);
		params.put("pushUser", pushUser.toString());
		try {
			String url = "im/sendPushUserToUserParam";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public Boolean sendPushUsersToUserMsg(Integer fromUserId, String gid, String content, Set<String> pushUsers) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(4);
		params.put("fromUserId", fromUserId.toString());
		params.put("gid", gid.toString());
		params.put("content", content.toString());
		putArrayIfNotBlank(params, "pushUsers", pushUsers);
		try {
			String url = "im/sendPushUsersToUserMsg";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public Boolean sendText(Integer fromUserId, String gid, String content, boolean isPush) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(5);
		params.put("fromUserId", fromUserId.toString());
		params.put("gid", gid.toString());
		params.put("content", content.toString());
		params.put("isPush", "" + isPush);
		try {
			String url = "im/sendText";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public Boolean sendVoice(Integer fromUserId, String gid, String voice, Integer time, boolean isPush) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(5);
		params.put("fromUserId", fromUserId.toString());
		params.put("gid", gid.toString());
		params.put("voice", voice.toString());
		params.put("time", time.toString());
		params.put("isPush", "" + isPush);
		try {
			String url = "im/sendVoice";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public Boolean sendImages(Integer fromUserId, String gid, List<String> imageList, boolean isPush) throws HttpApiException {
		if (null == imageList ||
				0 == imageList.size()) {
			throw new HttpApiException("imageList参数不能为空");
		}
		Map<String, String> params = new HashMap<String, String>(5);
		params.put("fromUserId", fromUserId.toString());
		params.put("gid", gid.toString());
		this.putArrayIfNotBlank(params, "imageList", imageList);
		params.put("isPush", "" + isPush);
		try {
			String url = "im/sendImage";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public Boolean sendImgTextMsg(Integer fromUserId, String gid, String title, String picUrl, String digest, 
    		String url, Map<String, Object> paramJson,
    		Boolean isPush, Integer pushUser) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(8);
		params.put("fromUserId", fromUserId.toString());
		params.put("gid", gid.toString());
		params.put("title", title.toString());
		params.put("picUrl", picUrl.toString());
		params.put("digest", digest.toString());
		this.putIfNotBlank(params, "url", url);
		this.putJsonStrIfNotBlank(params, "paramJson", paramJson);
		this.putIfNotBlank(params, "isPush", isPush);
		this.putIfNotBlank(params, "pushUser", pushUser);
		try {
			Boolean ret = this.postRequest("im/sendImgTextMsg", params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}

	public Boolean sendTextMsg(String toUserIds, String sysGroupJson, String title, String footer,
		String content, Map<String, Object> paramJson) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("toUserIds", toUserIds);
		params.put("sysGroupJson", sysGroupJson);
		params.put("title", title);
		params.put("footer", footer);
		params.put("content", content);
		this.putJsonStrIfNotBlank(params, "paramJson", paramJson);
		try {
			return this.postRequest("im/sendTextMsg", params, Boolean.class);
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}

