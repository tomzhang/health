package com.dachen.health.commons.constants;

import com.dachen.health.msg.util.MsgHelper;
import com.dachen.im.server.data.EventVO;
import com.dachen.im.server.enums.EventEnum;

import java.util.List;
import java.util.Map;

public class CommunitySendEvent {
	  public static void sendEvent(EventEnum eventEnum,List<String>userList,Map param,String userId){
	    	StringBuffer userIds = new StringBuffer();
			for(String uid:userList){
				if(userId.equals(uid)){
					continue;
				}
				if(userIds.length()>0){
					userIds.append("|");
				}
				userIds.append(uid);
			}
			EventVO eventVO = new EventVO();
			eventVO.setEventType(eventEnum.getValue());
			eventVO.setUserId(userIds.toString());
			eventVO.setParam(param);
			MsgHelper.sendEvent(eventVO);
	    }
	  
	  public static void sendEvent(EventEnum eventEnum,Integer userId,Map param){
			EventVO eventVO = new EventVO();
			eventVO.setEventType(eventEnum.getValue());
			eventVO.setUserId(userId.toString());
			eventVO.setParam(param);
			MsgHelper.sendEvent(eventVO);
	    }
}
