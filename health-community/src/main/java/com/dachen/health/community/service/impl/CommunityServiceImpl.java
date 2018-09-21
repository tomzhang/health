package com.dachen.health.community.service.impl;

import CommonParam.CommonParam;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.CommunityEnum;
import com.dachen.health.community.dao.ICommunityUsersDao;
import com.dachen.health.community.dao.IGroupLabelDao;
import com.dachen.health.community.dao.IReplyDao;
import com.dachen.health.community.dao.ITopicDao;
import com.dachen.health.community.entity.param.TopicParam;
import com.dachen.health.community.entity.po.CollectIds;
import com.dachen.health.community.entity.po.CommunityUsers;
import com.dachen.health.community.entity.po.GroupLabel;
import com.dachen.health.community.entity.po.Topic;
import com.dachen.health.community.entity.vo.CommunityUserVo;
import com.dachen.health.community.service.ICommunityUsersService;
import com.dachen.util.ReqUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommunityServiceImpl implements ICommunityUsersService {
	@Autowired
	private ICommunityUsersDao communityUsersDao;
	@Autowired
	private IGroupLabelDao groupLabelDao;
	@Autowired
	private ITopicDao topicDao;
	@Autowired
	private IReplyDao replyDao;

	@Override
	public void collectTopic(String topicId, Integer userId, String groupId) {
		// 判断对应的记录是否存在，存在就更新，不存在就删除
		CommunityUsers communityUser = communityUsersDao.getBygroupId(userId, groupId);
		if (communityUser != null) {
			Set<CollectIds> collesTopic = communityUser.getCollects();
			if (collesTopic == null || collesTopic.size() == 0) {
				collesTopic = new HashSet<CollectIds>();
			}
			if (collesTopic.contains(topicId)) {
				throw new ServiceException("该条帖子已经收藏");
			}
			CollectIds collect = new CollectIds();
			collect.setTopicId(topicId);
			collect.setTime(System.currentTimeMillis());
			collesTopic.add(collect);
			Map map = new HashMap<>();
			map.put("collects", collesTopic);
			communityUsersDao.update(CommunityUsers.class, communityUser.getId(), map);
		} else {
			communityUser = new CommunityUsers();
			Set<CollectIds> collect = new HashSet();
			CollectIds collectids = new CollectIds();
			collectids.setTopicId(topicId);
			collectids.setTime(System.currentTimeMillis());
			collect.add(collectids);
			communityUser.setCollects(collect);
			communityUser.setUserId(userId);
			communityUser.setGroupId(groupId);
			communityUsersDao.insert(communityUser);
		}

	}

	@Override
	public void deleteCollectTopic(String topicId, Integer userId, String groupId) {
		CommunityUsers communityUser = communityUsersDao.getBygroupId(userId, groupId);
		if (StringUtils.isEmpty(communityUser.getId()) || communityUser.getClass() == null
				|| communityUser.getCollects().size() == 0) {
			throw new ServiceException("取消收藏失败");
		} else {
			Set<CollectIds> collect = communityUser.getCollects();
			Set<CollectIds> collectTemp = new HashSet<>(collect);
			for (CollectIds collectIds : collectTemp) {
				if (collectIds.equals(topicId)) {
					collect.remove(collectIds);
				}

			}
			communityUser.setCollects(collect);
			Map map = new HashMap<>();
			map.put("collects", collect);
			communityUsersDao.update(CommunityUsers.class, communityUser.getId(), map);
		}

	}

	@Override
	public GroupLabel findByGoupId(String groupId) {
		return groupLabelDao.get(GroupLabel.class, "groupId", groupId);
	}

	@Override
	public CommunityUserVo findUserInfo(String groupId, Integer userId) {
		CommunityUserVo userVo = new CommunityUserVo();
		UserSession userSession = ReqUtil.instance.getUser();
		userVo.setUserName(userSession.getName());
		userVo.setHeadUrl(userSession.getHeadImgPath());
		userVo.setUserId(userSession.getUserId());
		// 获取用户点赞数
		Long likecount = topicDao.getLikeCount(userId, groupId);
		userVo.setLikeCount(likecount);
		// 初始化未读消息
		userVo.setMessage(CommunityEnum.CommunityMessage.没未读.getIndex());
		// 获取收藏帖子数目
		CommunityUsers communityUser = communityUsersDao.getBygroupId(userId, groupId);
		if (communityUser == null || communityUser.getCollects() == null) {
			userVo.setCollectAmount(0L);
		} else {
			CommonParam param = new CommonParam();
			param.setCollects(communityUser.getCollects());
			param.setGroupId(groupId);
			List<Topic> listTopic = topicDao.findMyCollectTopicListAll(param);
			userVo.setCollectAmount((long) listTopic.size());
		}
		// 设置时候有未读消息
		if (communityUser != null && StringUtils.isNotEmpty(communityUser.getMessage())) {
			userVo.setMessage(communityUser.getMessage());
		}
		TopicParam param =new TopicParam();
		param.setCreateUserId(userId);
		param.setGroupId(groupId);
		// 获取我发表的帖子数
		userVo.setTopicAmount(topicDao.findTopicByUserCount(param));
		// 评论数
		userVo.setReplyAmount(replyDao.findMyReplyCount(userId, groupId));

		return userVo;
	}

	@Override
	public CommunityUserVo findByUserIdInfo(String groupId, Integer userId) {
		CommunityUserVo userVo = new CommunityUserVo();
		// 获取用户点赞数
		Long likecount = topicDao.getLikeCount(userId, groupId);
		userVo.setLikeCount(likecount);
		// 获取用户发表帖子数目
		TopicParam param =new TopicParam();
		param.setCreateUserId(userId);
		param.setGroupId(groupId);
		userVo.setTopicAmount(topicDao.findTopicByUserCount(param));
		return userVo;
	}



}
