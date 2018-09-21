package com.dachen.health.msg.util;

import java.util.ArrayList;
import java.util.List;

import com.dachen.commons.constants.UserSession;
import com.dachen.commons.support.spring.SpringBeansUtils;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.service.impl.UserManagerImpl;
import com.dachen.health.commons.vo.User;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.data.response.GroupUserInfo;
import com.dachen.im.server.data.response.MsgGroupDetail;
import com.dachen.im.server.enums.GroupTypeEnum;
import com.dachen.im.server.enums.MsgTypeEnum;
import com.dachen.im.server.enums.RelationTypeEnum;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.StringUtils;

public class IMUtils {
	public static boolean isSysGroup(MsgGroupDetail groupInfo)
    {
    	return RelationTypeEnum.SYS.getValue().equals(groupInfo.getRtype()) 
    			|| GroupTypeEnum.TODO_NOTIFY.getValue() == groupInfo.getType()
    			|| GroupTypeEnum.NOTIFICATION.getValue() == groupInfo.getType() ;
    }
	 
//	private static final int maxLength= 3;
	/*public static String buildGroupName(UserManager userManager,List<String>userIdList)
	{
		StringBuffer sb = new StringBuffer();
		int size = userIdList.size();
		boolean isMore = size>maxLength?true:false;
		for(int i=0;i<size;i++)
		{
			if(i==maxLength)
			{
				break;
			}
			if(sb.length()>0)
			{
				sb.append("、");
			}
			int userId =Integer.valueOf(userIdList.get(i));
			UserSession userSession = userManager.getUserById(userId);
			sb.append(userSession.getName());
//			User user=userManager.getUser(Integer.valueOf(userIdList.get(i)));
		}
		if(isMore)
		{
			sb.append("等");
		}
		return sb.toString();
	}
	*/
	/**
	 * 为组头像路径添加ip:port,组成完整的组头像路径
	 * @param pic
	 * @return
	 */
	/*private static String buildGroupPicPath(String pic)
	{
		if(StringUtils.isEmpty(pic))
		{
			pic = "/group/defalut.jpg";
		}
		if(!pic.contains( PropertiesUtil.getHeaderPrefix() )){
			pic = PropertiesUtil.getHeaderPrefix() + "/"+ pic;
		}
		return pic;
	}*/
	
	/**
	 * 创建默认组头像（4宫格）
	 * @param userManager
	 * @param groupId
	 * @param userIdList
	 * @param oldPicName：老的头像名称，如果不为空则为替换
	 * @return
	 */
	/*public static String createGroupPic(UserManager userManager,String groupId,List<Integer>userIdList,String oldPicName)
	{
		int size = userIdList.size();
		java.util.Collections.sort(userIdList);
		Map<Integer,String>userPic = new HashMap<Integer,String>();
		for(int i=0;i<size;i++)
		{
			if(userPic.size()>=4)
			{
				break;
			}
//			int userId =Integer.valueOf(userIdList.get(i).toString());
			int userId = userIdList.get(i);
			UserSession user=userManager.getUserById(userId);
			if(!StringUtils.isEmpty(user.getHeadPicFileName()))
			{
				userPic.put(userId,user.getHeadPicFileName());
			}
		}
		String json = JSON.toJSONString(userPic);
		try {
			json = java.net.URLEncoder.encode(json,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Map<String,String> paramMap = new HashMap<String,String>();	
		paramMap.put("users", json);
		paramMap.put("groupId", groupId);
		paramMap.put("gpic", oldPicName);
		String picPath = null;
		try
		{
			picPath = (String)MsgHelper.uploadGetInf("groupAvatar", paramMap);
		}
		catch(ServiceException e)
		{
			e.printStackTrace();
		}
		return picPath;
	}*/
	
	public static String getRtype(Integer userType1, Integer userType2) {
		if(userType1<userType2)
		{
		  return userType1+"_"+userType2;
		}
		else
		{
			return userType2+"_"+userType1;	
		}
	}
	
	/**
	 * 1、判断消息接收者（toUserId）是否将消息发送者（fromUserId）拉黑
	 *  如果发送者(fromUserId)在消息接收者(toUserId)的黑名单中，则不推送
	 *  setting.defriend(好友关系表)
	 * 
	 * 2、 判断消息接收者（toUserId）是否屏蔽消息发送者（fromUserId）的消息。如果屏蔽，则不推送
	 *  setting.messageMasking(好友关系表) 消息屏蔽 1：否，2：是
	 *  
	 * 3、判断某个用户设置的是否接收通知标记
	 * 	"user.settings.ispushflag" 是否接收通知：1正常接收，2不接收
	 */
	public static void setIsPush(MessageVO msg,String currentUserId)
	{
		if(msg.getType()==MsgTypeEnum.LINE.getValue())
		{
			msg.setIsPush("false");
			return;
		}
		if(currentUserId==null)
		{
			msg.setIsPush("true");
			return;
		}
		if(msg.getFromUserId().equals(msg.getToUserId()) || currentUserId.equals(msg.getToUserId()))
		{
			//判断是否推送(如果是发送给自己的不推送)
			msg.setIsPush("false");
		}
		else if(StringUtils.isEmpty(msg.getIsPush()))
		{
			msg.setIsPush("true");
		}
//		MsgTypeEnum msgType = MsgTypeEnum.getEnum(msg.getType());
		 
		/*int fromUserId = Integer.valueOf(fromUserIdStr);
		int toUserId = Integer.valueOf(toUserIdStr);
		
		//消息接收者是否接收通知
		String value = getUserSetting(toUserId);
		if(value!=null)
		{
			int receiveNotice = Integer.valueOf(value);
			isPush = receiveNotice==2?true:false;
		}
		//是否屏蔽消息
		if(isPush)
		{
			value = getRelationSetting(toUserId,fromUserId);
			if(!StringUtils.isEmpty(value))
			{
				int defriend = Integer.valueOf(value.split(",")[1]);
				isPush = defriend==2?true:false;
			}
		}*/
	}
	
	/**
	 * 从缓存中获取用户设置：是否接收通知:1正常接收，2不接收
	 * @param userId
	 * @return
	 */
    /*public String getUserSetting(int userId)
    {
    	String key = KeyGenerate.genUserSettingKey(userId);
    	return jedisTemplate.get(key);
    }
    
    *//**
   	 * 从缓存中获取用户好友设置：例如值为“1,2”，表示defriend=1，messageMasking=2
   	 * @param userId
   	 * @return
   	 *//*
    public String getRelationSetting(int userId,int toUserId)
    {
       	String key = KeyGenerate.buildRelationSettingKey(userId,toUserId);
       	return jedisTemplate.get(key);
    }*/
    
	/**
	 * 设置组成员信息
	 * @param userManager
	 * @param group
	 * @return
	 */
	/*public static Object buildGroupInfo(GroupInfo group,UserSession currentUser)
    {
		if(group==null)
		{
			return null;
		}
		Integer currentUserId = currentUser==null?null:currentUser.getUserId();
		UserManager userManager = SpringBeansUtils.getBean(UserManagerImpl.BEAN_ID);
		List<Integer>userIds = group.getUserIds();
		if(userIds!=null && userIds.size()>0)
		{
			boolean isDouble = false;
			if(group.getType() == GroupTypeEnum.DOUBLE.getValue())
			{
				isDouble = true;
			}
			List<User> list = userManager.getHeaderPicName(userIds);
			List<GroupUserInfo>userList = new ArrayList<GroupUserInfo>(userIds.size());
			GroupUserInfo userInfo = null;
			for(User user:list)
			{
				userInfo = new GroupUserInfo();
				userInfo.setId(String.valueOf(user.getUserId()));
				userInfo.setName(user.getName());
				userInfo.setPic(user.getHeadPicFileName());
				userInfo.setUserType(user.getUserType());
				userList.add(userInfo);
				//双人会话组显示对方名称
				if(isDouble && currentUserId!=null && user.getUserId().intValue()!=currentUserId)
				{
					group.setGname(user.getName());
					group.setGpic(user.getHeadPicFileName());
				}
			}
			group.setUserList(userList);
			group.setUserIds(null);
		}
		
		String gpic = buildGroupPicPath(group.getGpic());
		group.setGpic(gpic);
		return group;
    }*/
  /*  
	*//**
	 *  根据组图像名称,返回图像中包含的用户个数
	 *//*
    public static int getUserCount(String groupId,String picName)
    {
    	if(picName==null)
    	{
    		return 0;
    	}
    	int beginIndex = picName.indexOf(groupId);
    	int endIndex = 0;
    	if(picName.endsWith(".jpg"))
    	{
    		endIndex = picName.indexOf(".jpg");
    	}
    	else if(picName.endsWith(".JPG"))
    	{
    		endIndex = picName.indexOf(".JPG");
    	}
    	else
    	{
    		endIndex = picName.length();
    	}
    		
    	String userCount = picName.substring(beginIndex+(groupId.length()+1),endIndex);
    	if(StringUtils.isNumber(userCount))
    	{
    		return Integer.valueOf(userCount);
    	}
    	return 0;
    }*/
    
    public static List<GroupUserInfo> getGroupUserList(List<Object> userIds)
    {
    	if(userIds==null || userIds.size()==0)
    	{
    		return null;
    	}
    	List<GroupUserInfo>userList = new ArrayList<GroupUserInfo>();
    	for(Object uid:userIds)
    	{
    		if(uid==null)
    		{
    			continue;
    		}
    		UserSession user = ReqUtil.instance.getUser(Integer.valueOf(uid.toString()));
    		if(user!=null)
    		{
    			GroupUserInfo groupUserInfo = new GroupUserInfo();
    			groupUserInfo.setId(uid.toString());
    			groupUserInfo.setName(user.getName());
    			groupUserInfo.setPic(user.getHeadImgPath());
    			groupUserInfo.setUserType(user.getUserType());
    			userList.add(groupUserInfo);
    		}
    	}
    	return userList;
    }
    
    /*public static void setGroupUser(List<GroupUserInfo> users)
    {
    	if(users!=null && users.size()>0)
    	{
    		for(GroupUserInfo groupUser:users)
    		{
    			UserSession user = ReqUtil.instance.getUser(Integer.valueOf(groupUser.getId()));
    			if(StringUtil.isEmpty(groupUser.getName()))
    			{
    				groupUser.setName(user.getName());
    			}
    			groupUser.setPic(user.getHeadImgPath());
    			groupUser.setUserType(user.getUserType());
    			if(groupUser.getRole()==null || groupUser.getRole()==0)
    			{
    				groupUser.setRole(user.getUserType());
    			}
    		}
    	}
    }*/
}
