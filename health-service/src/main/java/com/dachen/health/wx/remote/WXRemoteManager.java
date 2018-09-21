package com.dachen.health.wx.remote;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.health.wx.model.TemplateParam;
import com.dachen.health.wx.model.WXUserInfo;

@Component
public class WXRemoteManager {
	
	private static final Logger logger = LoggerFactory.getLogger(WXRemoteManager.class);
	
	private static final String WX_BASE_URL = "http://WX/wx";
	
	@Autowired
	private RibbonManager ribbonManager;

	/**
	 * 生成带参数的二维码（永久二维码）
	 * @param sceneStr
	 * @return
	 */
	public String limitQrCode(String sceneStr) {
		return ribbonManager.post(append("/limitQrCode/{sceneStr}"), null, sceneStr);
	}
	
	/**
	 * 生成短链接
	 * @param longUrl
	 * @return
	 */
	public String shortUrl(String longUrl) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("longUrl", longUrl);
		return ribbonManager.post(append("/shortUrl"), paramMap);
	}
	
	/**
	 * 获取微信用户信息（通过公众号平台）
	 * @param code
	 * @return
	 */
	public WXUserInfo getUserInfoByPub(String code) {
		return ribbonManager.get(append("/user/pub/{code}"), WXUserInfo.class, code);
	}
	
	/**
	 * 获取微信用户信息（通过开放平台）
	 * @param code
	 * @return
	 */
	public WXUserInfo getUserInfoByOpen(String code) {
		return ribbonManager.get(append("/user/open/{code}"), WXUserInfo.class, code);
	}
	
	/**
	 * 是否关注了公众号（1：关注；0：未关注）
	 * @param code
	 * @return
	 */
	public String subscribe(String code) {
		return ribbonManager.get(append("/subscribe/{code}"), code);
	}
	
	/**
	 * 发送模板消息
	 * @param openId
	 * @param templateParam
	 * @return
	 */
	public void sendTemplateMsg(String openId, TemplateParam templateParam) {
		String response = ribbonManager.post(append("/sendTemplateMsg/{openId}"), templateParam, openId);
		logger.info("sendTemplateMsg response {}", response);
	}

	/**
	 * 积分变动通知（兼容旧接口，新的发送模板消息请使用 {@link WXRemoteManager#sendTemplateMsg(String, TemplateParam)}）
	 */
	public void pointsChangeNotify(String openId, String first, String username, String oncePoints, String totalPoints, String reason, String remark) {
		TemplateParam param = new TemplateParam.Builder().first(first).keyword1(username).keyword3(oncePoints).keyword4(totalPoints).keyword5(reason).remark(remark).build();
		String response = ribbonManager.post(append("/pointsChangeNotify/{openId}"), param, openId);
		logger.info("sendTemplateMsg response {}", response);
	}
	
	/**
	 * 购药提醒（兼容旧接口，新的发送模板消息请使用 {@link WXRemoteManager#sendTemplateMsg(String, TemplateParam)}）
	 */
	public void purchaseDrugNotify(String openId, String id, String first, String name, String quantity, String comment, String remark) {
		TemplateParam param = new TemplateParam.Builder().url(id).first(first).keyword1(name).keyword2(quantity).keyword3(comment).remark(remark).build();
		String response = ribbonManager.post(append("/purchaseDrugNotify/{openId}"), param, openId);
		logger.info("sendTemplateMsg response {}", response);
	}
	
	/**
	 * 患者报道通知（兼容旧接口，新的发送模板消息请使用 {@link WXRemoteManager#sendTemplateMsg(String, TemplateParam)}）
	 */
	public void checkInResultNotify(String openId, String url, String patientName, String doctorName, Integer checkInStatus) {
		//TemplateParam param1 = new TemplateParam.Builder().url(url).first(doctorName).keyword1(patientName).remark(checkInStatus+"").build();
		Map<String,String> param=new HashMap<>();
		param.put("url",url);
		param.put("first",doctorName);
		param.put("keyword1",patientName);
		param.put("remark", String.valueOf(checkInStatus));
		logger.info("sendTemplateMsg send params {}", ToStringBuilder.reflectionToString(param));
		String response = ribbonManager.post(append("/sendTemplateMsg/{openId}"), param, openId);
		logger.info("sendTemplateMsg response {}", response);
	}
	
	/**
	 * 获取微信二维码URL对应的业务ID
	 * @param wxkey
	 * @return
	 */
	public String getRelateBizId(String wxkey) {
		return ribbonManager.get(append("/bizId/{wxkey}"), wxkey);
	}
	
	private String append(String uri) {
		return new StringBuilder(WX_BASE_URL).append(uri).toString();
	}
}
