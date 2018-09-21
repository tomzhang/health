package com.dachen.health.community.dao.impl;

import CommonParam.CommonParam;
import com.dachen.health.commons.constants.CommunityEnum;
import com.dachen.health.community.dao.ITopicDao;
import com.dachen.health.community.entity.param.TopicParam;
import com.dachen.health.community.entity.po.Topic;
import com.dachen.util.StringUtil;
import org.bson.types.ObjectId;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.aggregation.Group;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public class TopicDaoImpl  extends BaseDaoImpl<Topic> implements ITopicDao {


	@Override
	public List<Topic> findNoUpPageTopic(TopicParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("communityType",CommunityEnum.CommunityType.医生交流社区.getIndex());
		q.filter("groupId",param.getGroupId());
		q.filter("top",null);
		if(StringUtil.isNotEmpty(param.getCircleId())){
			q.filter("circleId", param.getCircleId());
		}
		q.order("-top,-createTime");
		q.offset(param.getStart()).limit(param.getPageSize());
		return q.asList();
	}

	@Override
	public Long findNoUpPagePageCount(TopicParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("communityType",CommunityEnum.CommunityType.医生交流社区.getIndex());
		q.filter("groupId",param.getGroupId());
		q.filter("top",null);
		if(StringUtil.isNotEmpty(param.getCircleId())){
			q.filter("circleId", param.getCircleId());
		}

		return q.countAll();
	}

	@Override
	public List<Topic> findUpPageTopic(TopicParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("communityType",CommunityEnum.CommunityType.医生交流社区.getIndex());
		q.filter("groupId",param.getGroupId());
		q.filter("top !=",null);
		if(StringUtil.isNotEmpty(param.getCircleId())){
			q.filter("circleId", param.getCircleId());
		}
		q.order("-top,-createTime");
		//不进行分页处理
		//q.offset(param.getStart()).limit(param.getPageSize());
		return q.asList();
	}


	@Override
	public List<Topic> findPageTopic(TopicParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("communityType",CommunityEnum.CommunityType.医生交流社区.getIndex());
		q.filter("groupId",param.getGroupId());

		if(StringUtil.isNotEmpty(param.getCircleId())){
			q.filter("circleId", param.getCircleId());
		}
		q.order("-top,-createTime");
		q.offset(param.getStart()).limit(param.getPageSize());
		return q.asList();
		
		
		
	}

	@Override
	public Long findPageCount(TopicParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("communityType",CommunityEnum.CommunityType.医生交流社区.getIndex());
		q.filter("groupId",param.getGroupId());
		if(StringUtil.isNotEmpty(param.getCircleId())){
			q.filter("circleId", param.getCircleId());
		}
		
		return q.countAll();
	}

	@Override
	public List<Topic> findTopicByUser(TopicParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		if(param.getSaveType().intValue()==0){
			q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		}else{
			q.filter("state", CommunityEnum.TopicState.草稿.getIndex());
		}

		q.filter("communityType",CommunityEnum.CommunityType.医生交流社区.getIndex());
		q.filter("groupId",param.getGroupId());
		q.filter("createUserId", param.getCreateUserId());
		q.order("-createTime");
		q.offset(param.getStart()).limit(param.getPageSize());
		
		return q.asList();
	}

	@Override
	public Long findTopicByUserCount(TopicParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		if(param.getSaveType().intValue()==0){
			q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		}else{
			q.filter("state", CommunityEnum.TopicState.草稿.getIndex());
		}
		q.filter("createUserId", param.getCreateUserId());
		q.filter("groupId",param.getGroupId());
		q.filter("communityType",CommunityEnum.CommunityType.医生交流社区.getIndex());
		return q.countAll();
	}

	@Override
	public List<Topic> findMyCollectTopicList(CommonParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("_id in", param.getCollectObjectIds());
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("groupId", param.getGroupId());
		q.order("-createTime");
		q.offset(param.getStart()).limit(param.getPageSize());
		return q.asList();
	}

	@Override
	public Long findMyCollectTopicCount(CommonParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("_id in", param.getCollectObjectIds());
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("groupId", param.getGroupId());
		
		return q.countAll();
	}

	@Override
	public List<String> findMyTopicId(int userId,String groupId) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("createUserId", userId);
		q.filter("groupId",groupId);
		q.filter("communityType", CommunityEnum.CommunityType.医生交流社区.getIndex());
		q.retrievedFields(true, "_id");
		List<Topic> listTopic=q.asList();
		List<String> topicId=new ArrayList<String>();
		
		for(Topic topic:listTopic){
			topicId.add(topic.getId());
		}
		return topicId;
	}

	@Override
	public List<Topic> findMyCollectTopicListAll(CommonParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("_id in", param.getCollectObjectIds());
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("groupId", param.getGroupId());
		q.order("-createTime");
		//q.offset(param.getStart()).limit(param.getPageSize());
		return q.asList();
	}

	@Override
	public long getCircleCount(String circelId) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("circleId", circelId);
		return q.countAll();
	}

	@Override
	public Topic getFirstTopTopic(String groupId) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("groupId", groupId);
		q.order("-top");
		return q.get();
	}

	@Override
	public Topic getUpTopTopic(String groupId, Long top) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("groupId", groupId);
		q.filter("top > ", top);
		q.order("top");
		return q.get();
	}

	@Override
	public Query<Topic> getByUserTopic(List<Integer> userIds,String groupId,String circleId) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("groupId", groupId);
		if(StringUtil.isNotEmpty(circleId)){
			q.filter("circleId",circleId);
		}
		q.filter("createUserId in",userIds);
		q.order("-createTime");
		return q;
	}

	@Override
	public List<Topic> findTitleTopic(String keyWord, String groupId,List<Integer> userIds,String circleId) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("groupId", groupId);
		if(userIds!=null&userIds.size()!=0){
			q.filter("createUserId nin",userIds);
		}
		if(StringUtil.isNotEmpty(circleId)){
			q.filter("circleId",circleId);
		}
		Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
		q.field("topicContent.title").equal(pattern);
//		q.or(
//				q.criteria("topicContent.text").equal(pattern),
//				q.criteria("topicContent.title").equal(pattern),
//				q.criteria("topicContent.richText").equal(pattern)
//		);

		q.order("-createTime");
		
	
		return q.asList();
	}
	
	@Override
	public List<Topic> findContentTopic(String keyWord, String groupId, List<Integer> userIds, List<ObjectId> ids,
			String circleId) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("groupId", groupId);
		if(userIds!=null&&userIds.size()!=0){
			q.filter("createUserId nin",userIds);
		}
		if(ids!=null&&ids.size()!=0){
			q.filter("_id nin",ids);
		}
		if(StringUtil.isNotEmpty(circleId)){
			q.filter("circleId",circleId);
		}
		
		Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
		//q.field("topicContent.text").equal(pattern);
		q.or(
				q.criteria("topicContent.text").equal(pattern),
				q.criteria("topicContent.richText").equal(pattern)
		);

		q.order("-createTime");
		
	
		return q.asList();
	}
	
	@Override
	public Long getLikeCount(Integer userId,String groupId){
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("groupId", groupId);
		q.filter("createUserId", userId);
		AggregationPipeline pipeline=dsForRW.createAggregation(Topic.class);
		pipeline.match(q).group(
				Group.id(Group.grouping("null")),//不分组
				Group.grouping("likeCount", Group.sum("likeCount")) //分组后累加金额
		);
		Iterator<Topic> iterator = pipeline.aggregate(Topic.class);
		Long sumAmt = 0L;
		//解析查询结果
		if (iterator.hasNext()) {
			Topic statResult = iterator.next();
			sumAmt = statResult.getLikeCount();
		}
		return sumAmt;
		
	}
	
	
	public Topic removeTopicAttr(String id,String key){
		Query<Topic> q = dsForRW.createQuery(Topic.class).filter("_id", new ObjectId(id));
		UpdateOperations<Topic> ops = dsForRW.createUpdateOperations(Topic.class);
		
		ops.unset(key);
		
		return dsForRW.findAndModify(q, ops);
		
	}

	@Override
	public List<Topic> findPcTopic(TopicParam param) {
		Query<Topic> q = dsForRW.createQuery(Topic.class);
		q.filter("state", CommunityEnum.TopicState.正常.getIndex());
		q.filter("communityType",CommunityEnum.CommunityType.医生交流社区.getIndex());
		q.filter("groupId",param.getGroupId());
		if(StringUtil.isNotEmpty(param.getCircleId())){
			q.filter("circleId", param.getCircleId());
		}
		q.order("-top,-createTime");
		q.offset(param.getStart()).limit(param.getPageSize());
		return q.asList();
	}

	
	
}
