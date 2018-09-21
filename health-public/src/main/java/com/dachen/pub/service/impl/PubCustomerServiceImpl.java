package com.dachen.pub.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.dachen.pub.util.PubaccHelper;
import com.dachen.sdk.exception.HttpApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.constants.UserSession;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.pub.model.param.PubMsgParam;
import com.dachen.pub.model.po.PubPO;
import com.dachen.pub.service.PubAccountService;
import com.dachen.pub.service.PubCustomerService;
import com.dachen.pub.util.PubUtils;
import com.dachen.pub.util.WelcomeMessageHelper;
import com.dachen.util.ReqUtil;
@Service
public class PubCustomerServiceImpl implements PubCustomerService{

	@Autowired
	private PubAccountService pubAccountService;
	
	@Autowired
	private PubaccHelper pubaccHelper;
	
	public void welcome(int userId) throws HttpApiException {
		UserSession user = ReqUtil.instance.getUser(userId);
		if(user==null)
		{
			return;
		}
		int userType = user.getUserType();
		
		String terminal= user.getTerminal();
		String client = "";
		if (StringUtils.isNotEmpty(terminal) && !"null".equalsIgnoreCase(terminal)) {
			client = PubUtils.getClient(Integer.valueOf(terminal));//
		}
		PubPO pub = pubaccHelper.getCustomerPubInfo(userType,client);
		if(pub==null)
		{
			return;
		}
		String pubId =pub.getPid();
		List<ImgTextMsg>mpt =  WelcomeMessageHelper.getWelcomeMsg(client,userType);
		List<String> to = new ArrayList<String>(1);
		to.add(String.valueOf(userId));
		
		PubMsgParam pubMsg = new PubMsgParam();
		pubMsg.setModel(mpt.size()==1?2:3);//单/多图文消息
		pubMsg.setPubId(pubId);//公共号Id
		pubMsg.setSendType(1);//广播给指定用户
		pubMsg.setToAll(false);//不发送给所有人
		pubMsg.setTo(to);//指定人
		pubMsg.setToNews(false);//不往健康动态公共号转发
		pubMsg.setMpt(mpt);
		pubMsg.setSource("system");
		pubaccHelper.sendMsgToPub(pubMsg);
		
		if(userType==3 && !PubUtils.BDJL.equalsIgnoreCase(client))
		{
			mpt = WelcomeMessageHelper.getGroupHelpMsg();
			pubMsg = new PubMsgParam();
			pubMsg.setModel(3);//单/多图文消息
			pubMsg.setPubId(pubId);//公共号Id
			pubMsg.setSendType(1);//广播给指定用户
			pubMsg.setToAll(false);//不发送给所有人
			pubMsg.setTo(to);//指定人
			pubMsg.setToNews(false);//不往健康动态公共号转发
			pubMsg.setMpt(mpt);
			pubMsg.setSource("system");
			pubaccHelper.sendMsgToPub(pubMsg);
		}

	}
	
}
