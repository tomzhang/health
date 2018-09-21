package com.dachen.health.community.dao;

import java.util.List;

import com.dachen.health.community.entity.param.ReplyParam;
import com.dachen.health.community.entity.po.Reply;

public interface IReplyDao extends BaseDao<Reply>{
	public List<Reply> findReplyList(ReplyParam param);
	public Long findReplyCount(ReplyParam param);
	
	/**
	 * 根据帖子id，查找最新的两条回复
	 * @author liming
	 */
	public List<Reply> find2ReplyList(String id);
	/**
	 * 查找我我回复的帖子列表
	 * @author liming
	 */
	public List<Reply> findMyReplyList(ReplyParam param);
	
	public Long findMyReplyCount(Integer userId,String groupId);
	 
	public List<Reply> findMyToReplyList(ReplyParam param,List<String> topicId);
	
	public Long findMyToReplyCount(ReplyParam param,List<String> topicId);
	
	
	

}
