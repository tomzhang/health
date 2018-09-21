package com.dachen.health.controller.api.im;

import com.dachen.im.server.enums.SysGroupEnum;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.JSONMessage;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.enums.MsgTypeEnum;
import com.dachen.util.JSONUtil;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/im")
public class ApiIMController extends ApiBaseController {

	@Resource
	protected IBusinessServiceMsg businessServiceMsg;

	@SuppressWarnings("unchecked")
	@Deprecated
	@RequestMapping(value = "sendTextMsgToGid", method = RequestMethod.POST)
	public JSONMessage sendTextMsgToGid(Integer fromUserId, String gid, String mptJson, String paramJson, boolean isPush) throws HttpApiException {
		String tag = "sendTextMsgToGid";
		logger.info("{}. fromUserId={}, gid={}, mptJson={}, paramJson={}, isPush={}", tag, fromUserId, gid, mptJson,
				paramJson, isPush);
		
		List<ImgTextMsg> mpt = null;
		if (StringUtils.isNotBlank(mptJson)) {
			mpt = JSONUtil.parseObject(List.class, mptJson);
		}

		Map<String, Object> param = null;
		if (StringUtils.isNotBlank(paramJson)) {
			param = JSONUtil.parseObject(Map.class, paramJson);
		}
		businessServiceMsg.sendTextMsgToGid("" + fromUserId, gid, mpt, param, isPush);
		
		return JSONMessage.success(null, true);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "sendUserToUserParam", method = RequestMethod.POST)
	public JSONMessage sendUserToUserParam(Integer fromUserId, String gid, String content, String paramJson, Boolean isPush) throws HttpApiException {
		String tag = "sendUserToUserParam";
		logger.info("{}. fromUserId={}, gid={}, content={}, paramJson={}, isPush={}", tag, fromUserId, gid,content,
				paramJson, isPush);

		Map<String, Object> param = null;
		if (StringUtils.isNotBlank(paramJson)) {
			param = JSONUtil.parseObject(Map.class, paramJson);
		}
		
		businessServiceMsg.sendUserToUserParam("" + fromUserId, gid, content, param, isPush);
		
		return JSONMessage.success(null, true);
	}
	
	@RequestMapping(value = "sendUserToUserMsg", method = RequestMethod.POST)
	public JSONMessage sendUserToUserMsg(Integer fromUserId, String gid, String content) throws HttpApiException {
		String tag = "sendUserToUserMsg";
		logger.info("{}. fromUserId={}, gid={}, content={}", tag, fromUserId, gid, content);
		
		businessServiceMsg.sendUserToUserMsg("" + fromUserId, gid, content);
		
		return JSONMessage.success(null, true);
	}
	
	@RequestMapping(value = "sendNotifyMsgToUser", method = RequestMethod.POST)
	public JSONMessage sendNotifyMsgToUser(Integer toUserId, String gid, String content) throws HttpApiException {
		String tag = "sendNotifyMsgToUser";
		logger.info("{}. toUserId={}, gid={}, content={}", tag, toUserId, gid, content);
		
		businessServiceMsg.sendNotifyMsgToUser("" + toUserId, gid, content);
		
		return JSONMessage.success(null, true);
	}
	
	/**
	 * 发送im中提醒内容
	 */
	@RequestMapping(value = "sendNotifytoMyMsg", method = RequestMethod.POST)
	public JSONMessage sendNotifytoMyMsg(Integer toUserId, String gid, String content) throws HttpApiException {
		String tag = "sendNotifytoMyMsg";
		logger.info("{}. toUserId={}, gid={}, content={}", tag, toUserId, gid, content);
		
		businessServiceMsg.sendNotifytoMyMsg(toUserId + "", gid, content);
		return JSONMessage.success(null, true);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "drugRemindNotify", method = RequestMethod.POST)
	public JSONMessage drugRemindNotify(Integer userId, String paramJson) {
		String tag = "drugRemindNotify";
		logger.info("{}. userId={}, paramJson={}", tag, userId, paramJson);
		Map<String, Object> param = null;
		if (StringUtils.isNotBlank(paramJson)) {
			param = JSONUtil.parseObject(Map.class, paramJson);
		}
		
		businessServiceMsg.drugRemindNotify("" + userId, param);
		return JSONMessage.success(null, true);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "sendPushUserToUserParam", method = RequestMethod.POST)
	public JSONMessage sendPushUserToUserParam(Integer fromUserId, String gid, String content, String paramJson, Integer pushUser) throws HttpApiException {
		String tag = "sendPushUserToUserParam";
		logger.info("{}. fromUserId={}, gid={}, content={}, paramJson={}, pushUser={}", tag, fromUserId, gid, content, paramJson, pushUser);
		
		Map<String, Object> param = null;
		if (StringUtils.isNotBlank(paramJson)) {
			param = JSONUtil.parseObject(Map.class, paramJson);
		}
		
		businessServiceMsg.sendPushUserToUserParam("" + fromUserId, gid, content, param, pushUser);
		return JSONMessage.success(null, true);
	}
	
	@RequestMapping(value = "sendPushUsersToUserMsg", method = RequestMethod.POST)
	public JSONMessage sendPushUsersToUserMsg(Integer fromUserId, String gid, String content, String[] pushUsers) throws HttpApiException {
		String tag = "sendPushUsersToUserMsg";
		logger.info("{}. fromUserId={}, gid={}, content={}, pushUsers={}", tag, fromUserId, gid, content, pushUsers);
		
		businessServiceMsg.sendPushUsersToUserMsg("" + fromUserId, gid, content, new HashSet<String>(Arrays.asList(pushUsers)));
		return JSONMessage.success(null, true);
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "sendTextMsgToPushUser", method = RequestMethod.POST)
	public JSONMessage sendTextMsgToPushUser(Integer fromUserId, String gid, String mptJson, String paramJson, Integer pushUser) throws HttpApiException {
		String tag = "sendTextMsgToPushUser";
		logger.info("{}. fromUserId={}, gid={}, mptJson={}, paramJson={}, pushUser={}", tag, fromUserId, gid, mptJson, paramJson, pushUser);
		
		List<ImgTextMsg> mpt = null;
		if (StringUtils.isNotBlank(mptJson)) {
			mpt = JSONUtil.parseObject(List.class, mptJson);
		}
		Map<String, Object> param = null;
		if (StringUtils.isNotBlank(paramJson)) {
			param = JSONUtil.parseObject(Map.class, paramJson);
		}
		
		businessServiceMsg.sendTextMsgToPushUser(fromUserId.toString(), gid, mpt, param, pushUser);
		return JSONMessage.success(null, true);
	}
	
	@RequestMapping(value = "sendText", method = RequestMethod.POST)
	public JSONMessage sendText(@RequestParam Integer fromUserId, @RequestParam String gid, 
			@RequestParam String content, @RequestParam  Boolean isPush) {
		String tag = "sendText";
		logger.info("{}. fromUserId={}, gid={}, content={}, isPush={}", tag, fromUserId, gid, content, isPush);

		MessageVO msg = new MessageVO();
		msg.setType(MsgTypeEnum.TEXT.getValue());
		msg.setFromUserId(fromUserId.toString());
		msg.setGid(gid);
		msg.setContent(content);
		msg.setIsPush(String.valueOf(isPush));
		JSON json = MsgHelper.sendMsg(msg);
		logger.info(json != null ? json.toString() : "json is null");
		
		return JSONMessage.success(null, true);
	}
	
	@RequestMapping(value = "sendVoice", method = RequestMethod.POST)
    public JSONMessage sendVoice(@RequestParam Integer fromUserId, @RequestParam String gid, @RequestParam String voice, 
    		@RequestParam Integer time, @RequestParam Boolean isPush) {
		String tag = "sendVoice";

		logger.info("{}. fromUserId={}, gid={}, voice={}, time={}, isPush={}", tag, fromUserId, gid, voice, time, isPush);

		MessageVO messagevo = new MessageVO();
		messagevo.setFromUserId(fromUserId.toString());
		messagevo.setGid(gid);
		messagevo.setType(MsgTypeEnum.VOICE.getValue());
		messagevo.setIsPush(String.valueOf(isPush));
		messagevo.setTime(time + "");

		boolean success = initMessageParams(messagevo, voice);
		logger.debug("{}. success={}", tag, success);

		if (success) {
			JSON json = MsgHelper.sendMsg(messagevo);
			logger.debug("{}. json = {}", tag, json);
		}
		
		return JSONMessage.success(null, true);
	}

	@RequestMapping(value = "sendImages", method = RequestMethod.POST)
    public JSONMessage sendImages(@RequestParam Integer fromUserId, @RequestParam String gid, @RequestParam String[] imageList, @RequestParam Boolean isPush) {
		String tag = "sendImages";

		logger.info("{}. fromUserId={}, gid={}, imageList={}, isPush={}", tag, fromUserId, gid, imageList, isPush);

		MessageVO messagevo = new MessageVO();
		messagevo.setFromUserId(fromUserId.toString());
		messagevo.setGid(gid);
		messagevo.setType(MsgTypeEnum.PHOTO.getValue());
		messagevo.setIsPush(String.valueOf(isPush));

		for (String imageUrl : imageList) {
			boolean success = initMessageParams(messagevo, imageUrl);
			logger.debug("{}. success={}", tag, success);

			if (success) {
				JSON json = MsgHelper.sendMsg(messagevo);
				logger.debug("{}. json = {}", tag, json);
			}
		}
		
		return JSONMessage.success(null, true);
	}

    protected static final String splitSymbol = "&&&";
    protected static final String DEFAULT_PARAMS_SPLIT = "\\?";
    protected static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    protected static boolean initMessageParams(MessageVO messagevo, String url) {
		String split = DEFAULT_PARAMS_SPLIT;
		if (url.contains(splitSymbol)) {
			split = splitSymbol;
		}

		String[] splitArray = url.split(split);
		if (null == splitArray || 2 != splitArray.length) {
			return false;
		}

		// 设置uri
		String uri = url;
		if (splitSymbol.equals(split)) {
			uri = splitArray[0];
		}
		messagevo.setUri(uri);

		// 解析参数params
		String params = splitArray[1];
		List<NameValuePair> list = URLEncodedUtils.parse(params, DEFAULT_CHARSET);
		for (NameValuePair pair : list) {
			String key = pair.getName();
			String val = pair.getValue();

			if ("key".equals(key) || "xgKey".equals(key)) {
				messagevo.setName(val);
				messagevo.setKey(val);
				continue;
			}
			if ("format".equals(key) || "xgFormat".equals(key)) {
				messagevo.setFormat(val);
				continue;
			}
			if ("width".equals(key) || "xgWidth".equals(key)) {
				messagevo.setWidth(Integer.parseInt(val));
				continue;
			}
			if ("height".equals(key) || "xgHeight".equals(key)) {
				messagevo.setHeight(Integer.parseInt(val));
				continue;
			}
		}

		return true;
	}
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "sendImgTextMsg", method = RequestMethod.POST)
    public JSONMessage sendImgTextMsg(@RequestParam Integer fromUserId, @RequestParam String gid, 
    		@RequestParam String title, String picUrl, String digest, 
    		String url, @RequestParam String paramJson,
    		Boolean isPush, Integer pushUser) throws HttpApiException {
		String tag = "sendImages";

		logger.info("{}. fromUserId={}, gid={}, title={}, picUrl={}", tag, fromUserId, gid, title, picUrl);
		logger.info("{}. digest={}", tag, digest);
		logger.info("{}. url={}, paramJson={}, pushUser={}", tag, url, paramJson, pushUser);
		
		Map<String, Object> param = null;
		if (StringUtils.isNotBlank(paramJson)) {
			param = JSONUtil.parseObject(Map.class, paramJson);
		}
		
		ImgTextMsg imgTextMsg = new ImgTextMsg();
		imgTextMsg.setStyle(6);
		imgTextMsg.setTime(System.currentTimeMillis());
		imgTextMsg.setTitle(title);
		imgTextMsg.setPic(picUrl);
		imgTextMsg.setContent(digest);
		
		imgTextMsg.setUrl(url);
		imgTextMsg.setParam(param);

		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
		mpt.add(imgTextMsg);

		logger.info("{}. imgTextMsg={}", tag, JSON.toJSONString(imgTextMsg));

		businessServiceMsg.sendTextMsgToPushUser(fromUserId.toString(), gid, mpt, null, isPush, pushUser);
		
		return JSONMessage.success(null, true);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "sendTextMsg", method = RequestMethod.POST)
	public JSONMessage sendTextMsg(@RequestParam String toUserIds, @RequestParam String sysGroupJson,
		@RequestParam String title, String footer, String content, @RequestParam String paramJson)
		throws HttpApiException {
		String tag = "sendTextMsg";

		logger.info("{}. toUserIds={}, sysGroupJson={}, ", tag, toUserIds, sysGroupJson);
		logger.info("{}. title={}, footer={}, content={}", tag, title, footer, content);
		logger.info("{}. paramJson={}", tag, paramJson);

		Map<String, Object> param = null;
		if (StringUtils.isNotBlank(paramJson)) {
			param = JSONUtil.parseObject(Map.class, paramJson);
		}

		ImgTextMsg imgTextMsg = new ImgTextMsg();
		imgTextMsg.setStyle(7);
		imgTextMsg.setTime(System.currentTimeMillis());
		imgTextMsg.setTitle(title);
		imgTextMsg.setFooter(footer);
		imgTextMsg.setContent(content);

		imgTextMsg.setParam(param);

		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
		mpt.add(imgTextMsg);

		logger.info("{}. imgTextMsg={}", tag, JSON.toJSONString(imgTextMsg));

		//发送系统通知
		businessServiceMsg.sendTextMsg(toUserIds, SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);

		return JSONMessage.success(null, true);
	}
}
