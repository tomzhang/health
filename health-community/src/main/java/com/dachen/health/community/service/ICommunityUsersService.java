package com.dachen.health.community.service;

import com.dachen.health.community.entity.po.GroupLabel;
import com.dachen.health.community.entity.vo.CommunityUserVo;

public interface ICommunityUsersService {
	/**
	 * 收藏帖子
	 * 
	 * @author liming
	 */
	public void collectTopic(String topicId, Integer userId, String groupId);

	/**
	 * 取消收藏帖子
	 * 
	 * @author liming
	 */
	public void deleteCollectTopic(String topicId, Integer userId, String groupId);

	/**
	 * 查找集团标签
	 * 
	 * @author liming
	 */
	public GroupLabel findByGoupId(String groupId);

	/**
	 * 获取用户信息
	 * @author liming
	 */
	public CommunityUserVo findUserInfo(String groupId, Integer userId);
	/**
	 * 获取置顶医生主页信息
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public CommunityUserVo findByUserIdInfo(String groupId,Integer userId);



}
