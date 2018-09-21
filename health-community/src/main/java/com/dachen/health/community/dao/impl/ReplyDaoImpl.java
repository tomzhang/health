package com.dachen.health.community.dao.impl;

import com.dachen.health.commons.constants.CommunityEnum;
import com.dachen.health.community.dao.IReplyDao;
import com.dachen.health.community.entity.param.ReplyParam;
import com.dachen.health.community.entity.po.Reply;
import com.dachen.util.ReqUtil;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class ReplyDaoImpl extends BaseDaoImpl<Reply> implements IReplyDao{

	@Override
	public List<Reply> findReplyList(ReplyParam param) {
		Query<Reply> q = dsForRW.createQuery(Reply.class);
		q.filter("topicId", param.getTopicId());
		q.filter("state", CommunityEnum.ReplyState.正常.getIndex());
		//q.filter("groupId", param.getGroupId());
		q.offset(param.getStart()).limit(param.getPageSize());
		return q.asList();
		
	}

	@Override
	public List<Reply> find2ReplyList(String id) {
		Query<Reply> q = dsForRW.createQuery(Reply.class);
		q.filter("topicId", id);
		q.filter("state", CommunityEnum.ReplyState.正常.getIndex());
		q.order("time");
		q.offset(0).limit(2);
		return q.asList();
	}

	@Override
	public Long findReplyCount(ReplyParam param) {
		// TODO Auto-generated method stub
		Query<Reply> q = dsForRW.createQuery(Reply.class);
		q.filter("topicId", param.getTopicId());
		q.filter("state", CommunityEnum.ReplyState.正常.getIndex());
		//q.filter("groupId", param.getGroupId());
		return q.countAll();
	}

	@Override
	public List<Reply> findMyReplyList(ReplyParam param) {
		Query<Reply> q = dsForRW.createQuery(Reply.class);
		q.filter("createUserId", param.getCreateUserId());
		q.filter("state", CommunityEnum.ReplyState.正常.getIndex());
		q.filter("groupId", param.getGroupId());
		q.order("-time");
		q.offset(param.getStart()).limit(param.getPageSize());
		return q.asList();
	}

	@Override
	public Long findMyReplyCount(Integer userId,String groupId) {
		Query<Reply> q = dsForRW.createQuery(Reply.class);
		q.filter("state", CommunityEnum.ReplyState.正常.getIndex());
		q.filter("createUserId", userId);
		q.filter("groupId", groupId);
		return q.countAll();
	}

	@Override
	public List<Reply> findMyToReplyList(ReplyParam param,List<String> topicId) {
		Query<Reply> q = dsForRW.createQuery(Reply.class);
		q.filter("state", CommunityEnum.ReplyState.正常.getIndex());
		q.filter("groupId", param.getGroupId());
		if(topicId==null||topicId.size()==0){
			q.filter("toUserId", ReqUtil.instance.getUserId());
		}else{
			q.or(
					q.criteria("toUserId").equal(ReqUtil.instance.getUserId()),
					q.criteria("topicId").in(topicId)
				);
		}
		q.order("-time");
		q.offset(param.getStart()).limit(param.getPageSize());
		return q.asList();
	}

	@Override
	public Long findMyToReplyCount(ReplyParam param,List<String> topicId) {
		Query<Reply> q = dsForRW.createQuery(Reply.class);
		q.filter("state", CommunityEnum.ReplyState.正常.getIndex());
		q.filter("groupId", param.getGroupId());
		if(topicId==null||topicId.size()==0){
			q.filter("toUserId", ReqUtil.instance.getUserId());
		}else{
			q.or(
					q.criteria("toUserId").equal(ReqUtil.instance.getUserId()),
					q.criteria("topicId").in(topicId)
				);
		}
		return q.countAll();
	}

}
