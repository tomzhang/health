package com.dachen.health.community.service.impl;

import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.CommunityEnum;
import com.dachen.health.commons.constants.CommunitySendEvent;
import com.dachen.health.community.dao.ICommunityUsersDao;
import com.dachen.health.community.dao.IReplyDao;
import com.dachen.health.community.dao.ITopicDao;
import com.dachen.health.community.entity.param.ReplyParam;
import com.dachen.health.community.entity.po.Accessory;
import com.dachen.health.community.entity.po.CommunityUsers;
import com.dachen.health.community.entity.po.Reply;
import com.dachen.health.community.entity.po.Topic;
import com.dachen.health.community.entity.vo.MyReplyVo;
import com.dachen.health.community.entity.vo.ReplyVo;
import com.dachen.health.community.entity.vo.ToMyReplyVo;
import com.dachen.health.community.service.IReplyService;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.vo.GroupVO;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.util.DateUtil;
import com.dachen.util.ReqUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ReplyServiceImpl implements IReplyService {
	@Autowired
	private IReplyDao replyDao;
	@Autowired
	private ITopicDao topicDao;
	@Autowired
	private ICommunityUsersDao communityUserDao;
	@Autowired
	private IGroupDoctorDao groupDoctorDao;
	
	@Override
	public void replyTips(ReplyParam param) {
		Topic topic=topicDao.getByPK(Topic.class, param.getTopicId());
		
		Reply reply=new Reply();
		reply.setContent(param.getContent());
		reply.setTopicId(param.getTopicId());
		reply.setCreateUserId(param.getCreateUserId());
		Accessory accessory=new Accessory();
		accessory.setImgUrls(param.getImgUrls());
		reply.setAccessory(accessory);
		reply.setState(CommunityEnum.ReplyState.正常.getIndex());
		reply.setTime(System.currentTimeMillis());
		reply.setGroupId(param.getGroupId());//医生主集团id
		replyDao.insert(reply);
		/**
		 * 更新最新回帖时间
		 */
		this.updateTopicTime(param.getTopicId());
		//更新回帖数量
		this.replyAmount(param.getTopicId(), 1L);
		//自己回复自己的帖子，不需要有通知有更新
		if(ReqUtil.instance.getUserId()!=topic.getCreateUserId()){
			this.message(topic.getCreateUserId(), topic.getGroupId());			
			new Thread(){
				public void run(){
					GroupVO groupVo=groupDoctorDao.findGroupById(topic.getGroupId());
					Map map=new HashMap();
					map.put("type", 2);//新消息提醒
					map.put("groupId",topic.getGroupId());
					map.put("groupName",groupVo.getName());
					//发送给回复者
					CommunitySendEvent.sendEvent(EventEnum.COMMUNITY_NEW_DYNAMIC, topic.getCreateUserId(),map);
					
				   }
			}.start();
		}
		//标识对应的用户有帖子需要更新
		
		
	}
	
	/**
	 * 更新帖子的最新更新时间
	 * @author liming
	 */
	private Topic updateTopicTime(String topicId){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("updateTime", System.currentTimeMillis());
		return topicDao.update(Topic.class, topicId, map);
	}
	/**
	 * 标识对应的记录有未读消息
	 * @author liming
	 */
	private void message(Integer userId,String groupId){
		CommunityUsers user=communityUserDao.getBygroupId(userId, groupId);
		if(user==null){
			user=new CommunityUsers();
			user.setGroupId(groupId);
			user.setUserId(userId);
			user.setMessage(CommunityEnum.CommunityMessage.有未读.getIndex());
			communityUserDao.insert(user);
		}else{
			Map map=new HashMap<>();
			map.put("message", CommunityEnum.CommunityMessage.有未读.getIndex());
			communityUserDao.update(CommunityUsers.class, user.getId(), map);
		}
		
	}
	/**
	 * 更新回帖数量
	 * @author liming
	 */
	private void replyAmount(String topicId,Long param){
		
		Topic topic=topicDao.getByPK(Topic.class, topicId);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("replies",topic.getReplies()+param );
		topicDao.update(Topic.class, topicId, map);
	}
	
	@Override
	public void replyUsers(ReplyParam param) {
		Reply reply=new Reply();
		reply.setContent(param.getContent());
		reply.setTopicId(param.getTopicId());
		reply.setCreateUserId(param.getCreateUserId());
		Accessory accessory=new Accessory();
		accessory.setImgUrls(param.getImgUrls());
		reply.setAccessory(accessory);
		reply.setState(CommunityEnum.ReplyState.正常.getIndex());
		reply.setTime(System.currentTimeMillis());
		reply.setToUserId(param.getToUserId());//回复的用户id
		reply.setToReplyId(param.getToReplyId());//回复的帖子id
		reply.setGroupId(param.getGroupId());
		replyDao.insert(reply);
		//主帖需要更新最新回复时间
		Topic topic=this.updateTopicTime(param.getTopicId());
		//相应的记录数加1
		this.replyAmount(param.getTopicId(), 1L);
		if(ReqUtil.instance.getUserId()!=param.getToUserId()){
			//对应的回复人加1
			this.message(param.getToUserId(), param.getGroupId());
			//对应的消息通过im指令加通知有未读消息
			new Thread(){
				public void run(){
					GroupVO groupVo=groupDoctorDao.findGroupById(param.getGroupId());
					Map map=new HashMap();
					map.put("type", 2);//新消息提醒
					map.put("groupId",param.getGroupId());
					map.put("groupName",groupVo.getName());
					//发送给会负责
					CommunitySendEvent.sendEvent(EventEnum.COMMUNITY_NEW_DYNAMIC, param.getToUserId(),map);
					//发送给帖子创建者
					CommunitySendEvent.sendEvent(EventEnum.COMMUNITY_NEW_DYNAMIC, topic.getCreateUserId(),map);
				   }
			}.start();
		}
		
		
		
	}

	@Override
	public PageVO findReplyList(ReplyParam param) {
		//查询主帖
		Topic topic=topicDao.getByPK(Topic.class, param.getTopicId());
		if(topic==null){
			throw new ServiceException("主帖不存在");
		}
		List<Reply> listReply=replyDao.findReplyList(param);
		List<ReplyVo> listReplyVo=new ArrayList<ReplyVo>();
		int i=0;
		for(Reply reply:listReply){
			i++;
			ReplyVo replyVo=new ReplyVo();
			replyVo.setContent(reply.getContent());
			UserSession replyUser=ReqUtil.instance.getUser(reply.getCreateUserId());
			replyVo.setReplyUserId(reply.getCreateUserId());
			replyVo.setId(reply.getId());
			replyVo.setReplyName(replyUser.getName());
			replyVo.setReplyHeadUrl(replyUser.getHeadImgPath());
			replyVo.setTopicId(reply.getTopicId());
			replyVo.setTime(DateUtil.toCommunityTime(reply.getTime()));
			replyVo.setImgUrls(reply.getAccessory()==null?null:reply.getAccessory().getImgUrls());
			
			if(reply.getToUserId()!=null&&reply.getToUserId()!=0){
				replyVo.setToUsesId(reply.getToUserId());
				UserSession toUser=ReqUtil.instance.getUser(reply.getToUserId());
				replyVo.setToUserName(toUser.getName());
				replyVo.setToUserHeadUrl(toUser.getHeadImgPath());
			}
			//如果是自己发表的帖子，评论自己可以全部都删除
			if(topic.getCreateUserId()==ReqUtil.instance.getUserId()){
				replyVo.setDelete("1");
			}else{
				//判断是否能够删除帖子
				replyVo.setDelete(ReqUtil.instance.getUserId()==reply.getCreateUserId()?"1":"0");
			}
			
		
			replyVo.setId(reply.getId());
			//显示楼层
			if(param.getPageIndex()==0){
				if(i==1){
					replyVo.setFloor("沙发");
				}else if(i==2){
					replyVo.setFloor("板凳");
				}else if(i==3){
					replyVo.setFloor("地板");
				}else{
					replyVo.setFloor(param.getStart()+i+"楼");
				}
				
			}else{
				replyVo.setFloor(param.getStart()+i+"楼");
			}
			listReplyVo.add(replyVo);
		}
		
		return new PageVO(listReplyVo, replyDao.findReplyCount(param), param.getPageIndex(), param.getPageSize());
		
	}

	@Override
	public void delete(String id) {
		Reply reply=replyDao.getByPK(Reply.class, id);
		if(reply==null){
			throw new ServiceException("该条记录不存在");
		}
		if(reply.getState().equals(CommunityEnum.ReplyState.已删除.getIndex())){
			throw new ServiceException("改条记录已被删除");
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("state", CommunityEnum.ReplyState.已删除.getIndex());
		replyDao.update(Reply.class, id, map);
		/**
		 * 删除回帖，对应的评论数要减去1
		 */
		this.replyAmount(reply.getTopicId(), -1L);
	}
	@Override
	public PageVO findMyReplyList(ReplyParam param){
		
		List<Reply> listReply =replyDao.findMyReplyList(param);
		List<MyReplyVo> listMyReply=new ArrayList<MyReplyVo>();
		UserSession user=ReqUtil.instance.getUser();
		for(Reply reply:listReply){
			MyReplyVo myReplyVo=new MyReplyVo();
			myReplyVo.setTopicId(reply.getTopicId());
			myReplyVo.setTime(DateUtil.toCommunityTime(reply.getTime()));
			myReplyVo.setHeadUrl(user.getHeadImgPath());
			myReplyVo.setUserName(user.getName());
			myReplyVo.setContent(reply.getContent());
			//判断是回复主帖还是回复的个人
			if(StringUtils.isEmpty(reply.getToReplyId())){
				//回复主帖
				Topic toTopic=topicDao.getByPK(Topic.class, reply.getTopicId());
				
				myReplyVo.setToContent(toTopic.getTopicContent().getTitle());//注入标题
				myReplyVo.setToUserName(ReqUtil.instance.getUser(toTopic.getCreateUserId()).getName());//注入姓名
				
			}else{
				//回复个人
				Reply toReply=replyDao.getByPK(Reply.class, reply.getToReplyId());
				myReplyVo.setToContent(toReply.getContent());
				myReplyVo.setToUserName(ReqUtil.instance.getUser(toReply.getCreateUserId()).getName());
				
			}
			listMyReply.add(myReplyVo);
		}
		
		return new PageVO(listMyReply, replyDao.findMyReplyCount(param.getCreateUserId(),param.getGroupId()), param.getPageIndex(), param.getPageSize());
		
	}

	@Override
	public PageVO findMyToReplyList(ReplyParam param) {
		//获取我发表的帖子集合
		List<String> topicIds=topicDao.findMyTopicId(ReqUtil.instance.getUserId(), param.getGroupId());
		List<Reply> listReply=replyDao.findMyToReplyList(param,topicIds);
		List<ToMyReplyVo> listToMyReplyVo=new ArrayList<ToMyReplyVo>();
		
		for(Reply reply:listReply){
			UserSession touser=ReqUtil.instance.getUser(reply.getCreateUserId());
			ToMyReplyVo toReplyVo=new ToMyReplyVo();
			toReplyVo.setToUserId(reply.getCreateUserId());
			toReplyVo.setTime(DateUtil.toCommunityTime(reply.getTime()));
			toReplyVo.setToUserName(touser.getName());
			toReplyVo.setToUserHeadUrl(touser.getHeadImgPath());
			toReplyVo.setReplyId(reply.getId());
			toReplyVo.setTopicId(reply.getTopicId());
			toReplyVo.setToContent(reply.getContent());
			toReplyVo.setMyUserId(ReqUtil.instance.getUserId());
			if(StringUtils.isEmpty(reply.getToReplyId())){
				Topic toTopic=topicDao.getByPK(Topic.class, reply.getTopicId());
				toReplyVo.setContent(toTopic.getTopicContent().getTitle());
				toReplyVo.setUserName(ReqUtil.instance.getUser(toTopic.getCreateUserId()).getName());
				toReplyVo.setUserId(toTopic.getCreateUserId());
			}else{
				Reply toReply=replyDao.getByPK(Reply.class, reply.getToReplyId());
				toReplyVo.setContent(toReply.getContent());
				toReplyVo.setUserName(ReqUtil.instance.getUser(toReply.getCreateUserId()).getName());
				toReplyVo.setUserId(toReply.getCreateUserId());
			}
			
			listToMyReplyVo.add(toReplyVo);
		}
		//清除未读消息
		this.clearMessage(ReqUtil.instance.getUserId(), param.getGroupId());
		return new PageVO(listToMyReplyVo, replyDao.findMyToReplyCount(param, topicIds), param.getPageIndex(), param.getPageSize());
	}

	/**
	 * 清楚未读消息的通用方法
	 * @param userId
	 * @param groupId
     */
	private void clearMessage(Integer userId,String groupId){
		CommunityUsers user=communityUserDao.getBygroupId(userId, groupId);
		if(user==null){
			user=new CommunityUsers();
			user.setGroupId(groupId);
			user.setUserId(userId);
			user.setMessage(CommunityEnum.CommunityMessage.没未读.getIndex());
			communityUserDao.insert(user);
		}else{
			Map map=new HashMap<>();
			map.put("message", CommunityEnum.CommunityMessage.没未读.getIndex());
			communityUserDao.update(CommunityUsers.class, user.getId(), map);
		}
	}
	
		
	
	
	
}
