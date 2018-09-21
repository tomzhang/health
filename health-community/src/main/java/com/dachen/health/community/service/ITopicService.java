package com.dachen.health.community.service;

import CommonParam.CommonParam;
import com.dachen.commons.page.PageVO;
import com.dachen.health.community.entity.param.TopicParam;
import com.dachen.health.community.entity.vo.TopicVo;
import com.dachen.health.community.entity.vo.UpTopic;

import java.util.List;

public interface ITopicService {
	/**
	 * 发表帖子
	 * @author liming
	 */
	public void publish(TopicParam topic);
	/**
	 * 删除帖子
	 * @author liming
	 */
	public void deleteTopic(String id);
	/**
	 * 点赞帖子
	 * @author liming
	 */
	public void likeTopic(String id,Integer userId,String type);
	/**
	 * 查看帖子列表(非置顶帖子)
	 * @author liming
	 */
	public PageVO findAllTopic(TopicParam params,Integer userId);

	/**
	 * 查看所有置顶帖子
	 * @param param
	 * @return
     */
	public List<UpTopic> findAllUpTopic(TopicParam param);
	/**
	 * 查看主帖详情接口
	 * @author liming
	 */
	public TopicVo findbyTopicDetail(String id,Integer userId,String groupId);
	/**
	 * 获取我发表的帖子
	 * @author liming
	 */
	public PageVO findTopicByUserId(TopicParam param);
	/**
	 * 查看我收藏的帖子
	 * @param param
	 * @return
	 */
	public PageVO findMyCollectTopicList(CommonParam param);
	/**
	 * 置顶帖子
	 * @param id
	 */
	public void topTopic(String id,String groupId);
	/**
	 * 上移帖子
	 * @param id
	 * @param groupId
	 */
	public void moveTopic(String id,String groupId);
	/**
	 * 取消置顶
	 * @param id
	 */
	public void undoTopTopic(String id);
	/**
	 * 按照关键字搜索
	 * @param param
	 * @return
	 */
	public PageVO findTopicKeyWord(TopicParam param);
	/**
	 * pc端查询所有帖子
	 * @param param
	 * @return
	 */
	public PageVO findPcTopic(TopicParam param);

	/**
	 * 编辑帖子
	 * @param param
     */
	public void editTopic(TopicParam param);
}
