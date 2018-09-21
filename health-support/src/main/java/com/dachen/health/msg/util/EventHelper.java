package com.dachen.health.msg.util;

import java.util.Map;

import com.dachen.commons.exception.ServiceException;
import com.dachen.im.server.enums.EventEnum;

public class EventHelper {
	
	public static boolean checkEventParam(EventEnum eventType,Map<String,Object>param)throws ServiceException
	{
		if(eventType==null)
		{
			throw new ServiceException(100401, "sendEvent:eventType is error"); 	
		}
		
		if(param==null)
		{
			throw new ServiceException(100401, "sendEvent:param is empty"); 	
		}
		if(eventType.getValue().equals(EventEnum.ADD_FRIEND)
				|| eventType.getValue().equals(EventEnum.DEL_FRIEND))
		{
			if(!param.containsKey("from") || !param.containsKey("to"))
			{
				throw new ServiceException(100401, "sendEvent:param is error,from or to is null"); 	
			}
		}
		else if(eventType.getValue().equals(EventEnum.PROFILE_CHANGE))
		{
			if(!param.containsKey("userId"))
			{
				throw new ServiceException(100401, "sendEvent:param is error,userId is null"); 	
			}
		}
		return true;
	}
}
