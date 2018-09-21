package com.dachen.pub.service;

import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.data.response.SendMsgResult;
import com.dachen.pub.model.param.PubParam;
import com.dachen.pub.model.po.PubPO;
import com.dachen.sdk.exception.HttpApiException;

public interface PubAccountService {
	
	public void savePub(PubParam pubParam) throws HttpApiException;
	
	public void saveDoctorPub(PubParam pubParam) throws HttpApiException;
	
	/**
	 * 根据公共号Id获取会话组信息
	 * @param groupId ：公共号对应的会话组Id（pubId_userId）
	 * @param needUser
	 * @return
	 */
	public GroupInfo getGroupInfo(String groupId,String userId,boolean needUser) throws HttpApiException;
	
	/**
	 * 获取公共号基本信息
	 * @param pid 
	 * @return
	 */
	public PubPO getPub(String pid) throws HttpApiException;
	
	/**
	 * 发送信息
	 * @param msg
	 * @return
	 */
	public SendMsgResult sendMsg(MessageVO msg) throws HttpApiException;

}
