package com.dachen.health.polling.service;

import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.request.MsgListRequestMessage;
import com.dachen.im.server.data.response.MsgListResult;
import com.dachen.im.server.data.response.SendMsgResult;
import com.dachen.sdk.exception.HttpApiException;

public interface IPollingService {
	
	/**
	 * 发送消息
	 * @param msg
	 * @return
	 */
	public SendMsgResult sendMsg(MessageVO msg) throws HttpApiException;
	
	
	public void sendOvertimeMsg(String gid) throws HttpApiException;
	
	public MsgListResult getMsg(MsgListRequestMessage msgGetParam);
	
}
