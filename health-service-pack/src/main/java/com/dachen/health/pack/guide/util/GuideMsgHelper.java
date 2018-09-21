package com.dachen.health.pack.guide.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dachen.im.server.data.MsgDocument;
import com.dachen.sdk.exception.HttpApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.msg.entity.vo.EventResult;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.im.server.constant.SysConstant;
import com.dachen.im.server.constant.SysConstant.SysUserEnum;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.request.EventListRequest;
import com.dachen.im.server.data.response.SendMsgResult;
import com.dachen.im.server.enums.MsgTypeEnum;
import com.dachen.im.server.enums.SysGroupEnum;

@Component
public class GuideMsgHelper {
	@Autowired
	private IMsgService msgService;
	
	private static GuideMsgHelper instance;
	
	private GuideMsgHelper()
	{
		instance = this;
	}
	
	public static GuideMsgHelper getInstance()
	{
		return instance;
	}
	
	/**
	 * 发送通知消息（目前的通知消息都是图文类型），其中会话组都为GROUP_002（通知），消息发送者都是系统用户。且都需要推送
	 * @param toUserIds：指定发送对象（发送给系统组的时候此字段不能为空，发送给其他类型的组时如果此字段不空，表示消息只发给组里面指定的人）
	 * @param imgTextMsg：图文消息的内容
	 * @return
	 */
	public SendMsgResult sendNotifyMsg(String toUserIds,ImgTextMsg imgTextMsg) throws HttpApiException {
		String gid = SysGroupEnum.TODO_NOTIFY.getValue();
		String fromUserId = SysUserEnum.SYS_001.getUserId();
		return this.sendImgTextMsg(gid, fromUserId, toUserIds, imgTextMsg, true);
	}
	
	/**
	 * 发送图文消息
	 * @param gid 组Id，为空默认为GROUP_002（通知）
	 * @param fromUserId：消息发送者，为空默认为系统用户（0）
	 * @param toUserIds：指定发送对象（发送给系统组的时候此字段不能为空，发送给其他类型的组时如果此字段不空，表示消息只发给组里面指定的人）
	 * @param imgTextMsg：图文消息的内容
	 * @param isPush：是否推送
	 * @return
	 */
	public SendMsgResult sendImgTextMsg(String gid,String fromUserId,String toUserIds,ImgTextMsg imgTextMsg,boolean isPush) throws HttpApiException {
		if(fromUserId == null || fromUserId.trim().length()==0)
		{
			fromUserId = SysUserEnum.SYS_001.getUserId();
		}
		if(gid == null || gid.trim().length()==0)
		{
			gid = SysGroupEnum.TODO_NOTIFY.getValue();
			if(toUserIds == null || toUserIds.trim().length()==0)
			{
				throw new ServiceException("消息发送失败：发送给系统通知组的时候，ToUserId不能为空");
			}
		}

		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
		mpt.add(imgTextMsg);
		
		MessageVO msg=new MessageVO();
		msg.setType(MsgTypeEnum.TEXT_IMG.getValue());
		msg.setMpt(mpt);
		msg.setIsPush(String.valueOf(isPush));
		msg.setFromUserId(fromUserId);
		msg.setGid(gid);
		if(toUserIds != null && toUserIds.trim().length()>0)
		{
			msg.setToUserId(toUserIds);
		}
		return msgService.baseSendMsg(msg);
	}


	/**
	 * 发送图文消息
	 * @param gid 组Id
	 * @param fromUserId：消息发送者，为空默认为系统用户（0）
	 * @param toUserIds：指定发送对象（发送给系统组的时候此字段不能为空，发送给其他类型的组时如果此字段不空，表示消息只发给组里面指定的人）
	 * @param msgDocument：文档消息的内容
	 * @param isPush：是否推送
	 * @return
	 */
	public SendMsgResult sendMsgDocument(String gid, String fromUserId, String toUserIds, MsgDocument msgDocument, boolean isPush) throws HttpApiException {
		if(StringUtils.isBlank(fromUserId))
		{
			fromUserId = SysUserEnum.SYS_001.getUserId();
		}
		if(StringUtils.isBlank(gid))
		{
			throw new ServiceException("消息发送失败：发送电子病历卡片失败，gid不能为空");
		}

		MessageVO msg=new MessageVO();
		msg.setType(MsgTypeEnum.DOCUMENT.getValue());
		msg.setDocument(msgDocument);
		msg.setIsPush(String.valueOf(isPush));
		msg.setFromUserId(fromUserId);
		msg.setGid(gid);
		if(StringUtils.isNotBlank(toUserIds))
		{
			msg.setToUserId(toUserIds);
		}
		return msgService.baseSendMsg(msg);
	}


	/**
	 * 发送提醒消息,消息发送者都是系统用户
	 * @param gid：组Id，不为空;
	 * @param content：消息的内容
	 * @param isPush：是否推送
	 * @return
	 */
	public SendMsgResult sendRemindMsg(String gid,String content,boolean isPush,Map<String,Object>param) throws HttpApiException {
		MessageVO message = new MessageVO();
		message.setType(MsgTypeEnum.REMIND.getValue());
		message.setContent(content);
		message.setFromUserId(SysConstant.SysUserEnum.SYS_001.getUserId());
		message.setGid(gid);
		message.setIsPush(String.valueOf(isPush));
		message.setParam(param);
		return msgService.baseSendMsg(message);
	}
	
	/**
	 * 发送分割线消息,消息发送者都是系统用户
	 * @param gid：组Id，不为空;
	 * @param content：消息的内容
	 * @param isPush：是否推送
	 * @return
	 */
	public SendMsgResult sendLineMsg(String gid,String content,boolean isPush,Map<String,Object>param) throws HttpApiException {
		MessageVO message = new MessageVO();
		message.setType(MsgTypeEnum.LINE.getValue());
		message.setContent(content);
		message.setFromUserId(SysConstant.SysUserEnum.SYS_001.getUserId());
		message.setGid(gid);
		message.setIsPush(String.valueOf(isPush));
		message.setParam(param);
		return msgService.baseSendMsg(message);
	}
	
	public SendMsgResult sendMsg(MsgTypeEnum msgType,String gid,Integer fromUserId,String toUserIds,String content,boolean isPush) throws HttpApiException {
		String userId = null;
		if(fromUserId == null || fromUserId==0)
		{
			userId = SysUserEnum.SYS_001.getUserId();
		}
		else
		{
			userId = String.valueOf(fromUserId);
		}
		if(msgType==null)
		{
			msgType = MsgTypeEnum.TEXT;
		}
		MessageVO msg=new MessageVO();
		msg.setType(msgType.getValue());
		msg.setContent(content);
		msg.setFromUserId(userId);
		msg.setGid(gid);
		msg.setIsPush(String.valueOf(isPush));
		if(toUserIds == null || toUserIds.trim().length()==0)
		{
			msg.setToUserId(toUserIds);
		}
		return msgService.baseSendMsg(msg);
	}
	
	public static EventResult getEventList(EventListRequest requestMsg)
	{
		try{
			if(requestMsg.getTs()==null)
			{
				requestMsg.setTs(0L);
			}
			JSON imGetMsg = MsgHelper.eventList(requestMsg);
			if(imGetMsg==null)
			{
				return null;
			}
			EventResult result = JSON.toJavaObject(imGetMsg,EventResult.class);
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
