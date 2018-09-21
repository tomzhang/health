package com.dachen.health.msg.util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.Constants.ResultCode;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.net.HttpHelper;
import com.dachen.im.server.data.EventVO;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.UserSoundVO;
import com.dachen.im.server.data.request.AuthTokenRequestMessage;
import com.dachen.im.server.data.request.CreateGroupRequestMessage;
import com.dachen.im.server.data.request.EventListRequest;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.request.GroupListRequestMessage;
import com.dachen.im.server.data.request.GroupStateRequestMessage;
import com.dachen.im.server.data.request.MsgListRequestMessage;
import com.dachen.im.server.data.request.PubGroupRequest;
import com.dachen.im.server.data.request.PubRequestMessage;
import com.dachen.im.server.data.request.PubSendMessageRequest;
import com.dachen.im.server.data.request.PushMessageRequest;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;

public class MsgHelper {

	/**
	 * 修改组信息(关闭会话为服务端调用，其他都是客户端调用)
	 * @param imMsg
	 * @return
	 */
	public static JSON updateGroup(UpdateGroupRequestMessage imMsg) {  
		int act = imMsg.getAct();
		if(act==2 && imMsg.getFromUserId().equalsIgnoreCase(imMsg.getToUserId()))
		{
			act = 20;
		}
		String action=null;
		switch(act)
		{
			case 1:action ="addGroupUser.action";break;//增加组成员
			case 2:action ="delGroupUser.action";break;//删除组成员
			case 3:action ="updateGroupName.action";break;//修改组名称
			case 4:action ="updateGroupPic.action";break;//修改图标
			case 5:action ="updateState.action";break;//全部消息已读
			case 6:action ="delGroupRecord.action";break;//清空会话组信息
			case 7:action ="togglePush.action";break;//7=不提醒
			case 8:action ="togglePush.action";break;//8=提醒
			case 9:action ="closeGroup.action";break;//9=关闭会话
			case 20:action ="delGroup.action";break;//退群
		}
		
		return ImHelper.instance.postJson("inner/group",action, imMsg);
	}
	
	/**
	 * 创建会话接口（服务端和客户端都会调用）
	 * @param gname
	 * @param uids
	 * @param creator
	 * @param rtype
	 * @return
	 */
	public static JSON createGroup(CreateGroupRequestMessage requestMsg) {  
		return ImHelper.instance.postJson("inner/group","createGroup.action", requestMsg);
	} 
	
	/**
	 * 创建特殊会话——患者导医会话(服务端调用)
	 * @param requestMsg
	 * @return
	 */
	public static JSON createGroup2(CreateGroupRequestMessage requestMsg) {  
		return ImHelper.instance.postJson("inner/group","createGroup2.action", requestMsg);
	} 
	
	/**
	 * 获取会话（服务端和客户端都会调用）
	 */
	public static JSON groupInfo(GroupInfoRequestMessage requestMsg) {  
		return ImHelper.instance.postJson("inner/group","groupInfo.action", requestMsg);
	} 
	
	/**
	 * 客户端、服务端调用
	 * 更改会话组状态（会变更组用户时间线）
	 * 参数说明：
	 * bizStatus 会话组状态--目前用于跟订单相关的会话，对应sessionStatus
	 * gid:会话组ID
	 * params：需要更新的会话组的扩展信息
	 * toUserList：暂时无用
	 */
	public static JSON updateGroupBizState(GroupStateRequestMessage requestMsg) {  
		return ImHelper.instance.postJson("inner/group","updateBizState.action", requestMsg);
	}
	/**
	 * 创建会话接口——会话组成员待角色（每次都会创建新的会话，并不会根据成员判断会话是否已经存在）
	 * ---暂时没有地方调用，此接口提供给服务器调用
	 * @param gname
	 * @param uids
	 * @param creator
	 * @param rtype
	 * @return
	 */
//	public static JSON createGroupWithRole(CreateGroupRequestMessage requestMsg) {  
//		return ImHelper.instance.postJson("convers","createGroupWithRole.action", requestMsg);
//	} 
	/**
	 * 发送消息的接口（服务端和客户端都会调用）
	 * @param msg
	 * @param isPush
	 * @return
	 */
	public static JSON sendMsg(MessageVO requestMsg) {  
		return ImHelper.instance.postJson("inner","send.action", requestMsg);
	}
	/**
	 * 只推送，不发消息（服务端调用）
	 * @param requestMsg
	 * @return
	 */
	public static JSON push(PushMessageRequest requestMsg) {  
		return ImHelper.instance.postJson("inner","push.action", requestMsg);
	}	
	/**
	 * 获取指令的接口（目前在准备废弃的接口中使用getBusiness）
	 * @return
	 */
	public static JSON eventList(EventListRequest requestMsg) {  
		return ImHelper.instance.postJson("inner","eventList.action", requestMsg);
	}
	
	/**
	 * 发送指令的接口（服务端调用）
	 * @return
	 */
	public static JSON sendEvent(EventVO requestMsg) {
		return ImHelper.instance.postJson("inner","sendEvent.action", requestMsg);
	}
	
	/**
	 * 目前在服务端调用
	 * 只需要传userI字段和 sound（类似notice02.mp3）。
	 * @param 
	 */
	public static JSON setSound(UserSoundVO requestMsg) {  
		return ImHelper.instance.postJson("inner","setSound.action", requestMsg);
	} 
	
	/**
	 * 目前在服务端调用
	 * 只需要传userI字段，如果以后会根据业务类型设置不同的推送铃声，则需要传bizType字段。
	 * @param 
	 * @return Map<Integer,String>{key:bizType(现默认都是0),value:设置的铃声名称}
	 */
	public static JSON getSound(UserSoundVO requestMsg) {  
		return ImHelper.instance.postJson("inner","getSound.action", requestMsg);
	}
	
	/**
	 * 服务端和客户端都会调用（服务端的调用准备去掉）
	 * 手机设备注册（用于推送）
	 * @param request
	 * @return
	 */
	public static JSON registerDeviceToken(AuthTokenRequestMessage request) {
		return ImHelper.instance.postJson("inner","registerDeviceToken.action", request);
	}
	
	/**
	 * 服务端调用
	 * 用户退出登录时调用（用于推送）
	 * @param request
	 * @return
	 */
	public static JSON removeDeviceToken(AuthTokenRequestMessage request) {
		return ImHelper.instance.postJson("inner","removeDeviceToken.action", request);
	}
	
	public static JSON updateDeviceToken(AuthTokenRequestMessage request){
		return ImHelper.instance.postJson("inner","updateDeviceToken.action", request);
	}
	
	/**
	 *  服务端调用
	 * 用户设置--新消息是否通知改变时需要改变推送设备的状态
	 * @param request
	 * @return
	 */
	public static JSON updatePushStatus(AuthTokenRequestMessage request) {
		return ImHelper.instance.postJson("inner","updatePushStatus.action", request);
	}
	
	/**
	 * 创建公共号会话组
	 * @param requestMsg
	 * @return
	 */
	public static JSON createPubGroup(PubGroupRequest requestMsg) {  
		return ImHelper.instance.postJson("inner/pub","createPubGroup.action", requestMsg);
	} 
	
	public static JSON removeFromPub(PubGroupRequest requestMsg) {  
		return ImHelper.instance.postJson("inner/pub","delFromPubGroup.action", requestMsg);
	}

	/**
	 * 公共号广播信息
	 * @param requestMsg
	 * @return
	 */
	public static JSON saveAndSendMsg(PubRequestMessage requestMsg) {  
		return ImHelper.instance.postJson("inner/pub","saveAndSendMsg.action", requestMsg);
	}
	
	public static JSON savePubMsg(MessageVO requestMsg) {  
		return ImHelper.instance.postJson("inner/pub","saveMsg.action", requestMsg);
	}
	
	public static JSON sendPubMsg(PubSendMessageRequest requestMsg) {  
		return ImHelper.instance.postJson("inner/pub","sendMsg.action", requestMsg);
	}
	
	
	/**
	 * 新的获取会话列表接口
	 */
	public static JSON groupList_new(GroupListRequestMessage requestMsg) {  
		return ImHelper.instance.postJson("inner","groupList.action", requestMsg);
	} 

	/**
	 * 通过Im服务器获取7牛上传凭证
	 * @param requestMsg
	 * @return Map{
	 *   "upToken":"上传token"
	 * }
	 */
	public static String getUploadToken(String bucket) {  
		Map<String,String>param =new HashMap<String,String>();
		param.put("bucket", bucket);
		JSON json = ImHelper.instance.postJson("inner/file","getUpToken.action", param);
		Map<String,String>result = JSON.toJavaObject(json,Map.class);
		return result==null?null:result.get("upToken");
	}

	/**
	 * @deprecated
	 * 获取消息的接口（服务器调用，准备废弃使用。目前web导医使用）
	 * @param msg
	 * @param isPush	
	 * @return
	 */
	public static JSON msgList(MsgListRequestMessage requestMsg) {  
		return ImHelper.instance.postJson("convers","msgList.action", requestMsg);
	}
		
	/**
	 * @deprecated
	 * 获取会话列表 （服务器调用，准备废弃使用。目前web导医使用）
	 */
	public static JSON groupList(GroupListRequestMessage requestMsg) {  
		return ImHelper.instance.postJson("convers","groupList.action", requestMsg);
	}  
		
	
	
	/**
	 * 调用文件服务器接口
	 * @param action
	 * @param paramMap
	 * @return
	 */
	public static Object uploadGetInf(String action,Map<String,String> paramMap)
	{
		String url = ImHelper.getFileUploadUrl();
		if(url==null)
		{
			throw new ServiceException("请配置文件服务器的上传地址。");
		}
		if(!url.endsWith("/"))
		{
			url = url+"/";
		}
		String respContent = HttpHelper.get(url+"upload/"+action, paramMap);
		
		JSONMessage msg = JSON.parseObject(respContent, JSONMessage.class);
		if(msg.getResultCode().equals(ResultCode.Success))
		{
			return msg.getData();
		}
		else
		{
			 throw new ServiceException(msg.getResultMsg());
		}
	}
	
}
