package com.dachen.health.community.dao;

import CommonParam.CommonParam;
import com.dachen.health.community.entity.param.TopicParam;
import com.dachen.health.community.entity.po.Topic;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import java.util.List;

public interface ITopicDao extends BaseDao<Topic>{
	/**
	 * 屏蔽置顶帖子
	 * @param param
	 * @return
     */
	public List<Topic> findNoUpPageTopic(TopicParam param);

	/**
	 * 查询置顶帖子
	 * @param param
	 * @return
     */
	public List<Topic> findUpPageTopic(TopicParam param);
	/**
	 * 屏蔽置顶帖子
	 * @param param
	 * @return
     */
	public Long findNoUpPagePageCount(TopicParam param);
	public List<Topic> findPageTopic(TopicParam param);
	public Long findPageCount(TopicParam param);
	public List<Topic> findTopicByUser(TopicParam param);
	public Long findTopicByUserCount(TopicParam param);
	
	public List<Topic> findMyCollectTopicList(CommonParam param);
	
	public Long findMyCollectTopicCount(CommonParam param);
	public List<String> findMyTopicId(int userId,String groupId);
	/**
	 * 查找全部的收藏帖子
	 * @author liming
	 */
	public List<Topic> findMyCollectTopicListAll(CommonParam param);
	/**
	 * 获取该圈子下面有多少条帖子
	 * @param circelId
	 * @return
	 */
	public long getCircleCount(String circelId);
	/**
	 * 获取该集团下面最大置顶记录
	 * @param groupId
	 * @return
	 */
	public Topic getFirstTopTopic(String groupId);
	/**
	 * 获取置顶项上一条记录的值
	 * @param groupId
	 * @param top
	 * @return
	 */
	public Topic getUpTopTopic(String groupId,Long top);

	/**
	 * 按照用户名，后去对应的帖子
	 * @param userIds
	 * @param groupId
     * @return
     */
	public Query<Topic> getByUserTopic(List<Integer> userIds,String groupId,String circleId);

	public List<Topic>  findTitleTopic(String keyWord,String groupId,List<Integer> userIds,String circleId);
	
	public List<Topic> findContentTopic(String keyWord,String groupId,List<Integer> userIds,List<ObjectId> ids,String circleId);
	/**
	 * 获取点赞数
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public Long getLikeCount(Integer userId,String groupId);
	/**
	 * 删除文档字段
	 * @param id
	 * @param key
	 * @return
	 */
	public Topic removeTopicAttr(String id,String key);
	
	public List<Topic> findPcTopic(TopicParam param);
	
}
