package com.dachen.pub.service.impl;

import com.dachen.pub.util.PubaccHelper;
import com.dachen.sdk.exception.HttpApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.data.response.SendMsgResult;
import com.dachen.pub.model.param.PubParam;
import com.dachen.pub.model.po.PubPO;
import com.dachen.pub.service.PubAccountService;
import com.dachen.pub.util.PubUtils;

@Service
public class PubAccountServiceImpl implements PubAccountService{
	private static Logger logger = LoggerFactory.getLogger(PubAccountServiceImpl.class);

	@Autowired
	protected PubaccHelper pubaccHelper;
		
	public GroupInfo getGroupInfo(String groupId,String currentUserId,boolean needUser) throws HttpApiException {
		GroupInfo groupInfo = pubaccHelper.getGroupInfo(groupId, currentUserId, needUser);
//		if(groupInfo!=null && groupInfo.getUserList()!=null){
//			List<GroupUserInfo>userList=groupInfo.getUserList();
//			for(GroupUserInfo groupUser:userList){
//				String id = groupUser.getId();
//				UserSession user = ReqUtil.instance.getUser(Integer.valueOf(id));
//				if(user!=null){
//					groupUser.setName(user.getName());
//					groupUser.setPic(user.getHeadImgPath());
//					groupUser.setUserType(user.getUserType());
//				}
//			}
//		}
		return groupInfo;
	}
	
	public void savePub(PubParam pubParam) throws HttpApiException {
		pubaccHelper.savePub(pubParam);
	}
	
	@Deprecated
	public PubPO getPub(String pid) throws HttpApiException {
		return pubaccHelper.getPub(pid);
	}
	
	@Deprecated
	public void saveDoctorPub(PubParam pubParam) throws HttpApiException {
		pubParam.setPid(PubUtils.PUB_DOC_PREFIX + pubParam.getPid());
		pubParam.setPhotourl(pubParam.getPhotourl());
		pubParam.setName("医生公众号_" + pubParam.getName());//
		pubParam.setNickName(pubParam.getName());
		savePub(pubParam);
	}
	
	
	/**
	 * 公告号消息（回复消息的时候调用）
	 * @param msg
	 * @return
	 */
	@Deprecated
	public SendMsgResult sendMsg(MessageVO msg) throws HttpApiException {
		return pubaccHelper.sendMsg(msg);
	}
	
}
