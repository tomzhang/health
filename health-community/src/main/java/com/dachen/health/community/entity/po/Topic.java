package com.dachen.health.community.entity.po;

import java.io.Serializable;
import java.util.Set;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 帖子主体
 * @Description 
 * @title Topic
 * @author liminng
 * @data 2016年7月26日
 */
@Entity(value="c_community_topic", noClassnameStored = true)
public class Topic implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5502882310225844659L;
	@Id	
	private String id;
//	private ObjectId _id;
	/**
	 * 页面浏览量
	 */
	private Long pageView;
	/**
	 * 回复数量
	 */
	private Long replies;
	/**
	 * 点赞数量
	 */
	private Long likeCount;
	/**
	 * 状态
	 */
	private String state;
	/**
	 * 社区名称
	 */
	private String communityType;
	/**
	 * 帖子类别
	 */
	private String type;
	/**
	 * 帖子内容
	 */
	private TopicContent topicContent;
	/**
	 * 附件
	 */
	private Accessory accessory;
	/**
	 * 最后更新时间
	 */
	private Long updateTime;
	/**
	 * 点赞用户
	 */
	private Set<Integer> likeUsers;
	/**
	 * 集团id
	 */
	private String groupId;
	/**
	 * 创建用户
	 */
	private Integer createUserId;
	/**
	 * 创建时间
	 */
	private Long createTime;
	/**
	 * 收藏时间
	 * 并不进行数据库存储，只是用来排序
	 */
	private Long collectTime;
	/**
	 * 置顶标示
	 */
	private Long top;
	/**
	 * 圈子
	 */
	
	private String circleId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getPageView() {
		return pageView;
	}
	public void setPageView(Long pageView) {
		this.pageView = pageView;
	}
	public Long getReplies() {
		return replies;
	}
	public void setReplies(Long replies) {
		this.replies = replies;
	}
	public Long getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCommunityType() {
		return communityType;
	}
	public void setCommunityType(String communityType) {
		this.communityType = communityType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public TopicContent getTopicContent() {
		return topicContent;
	}
	public void setTopicContent(TopicContent topicContent) {
		this.topicContent = topicContent;
	}
	public Accessory getAccessory() {
		return accessory;
	}
	public void setAccessory(Accessory accessory) {
		this.accessory = accessory;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public Set<Integer> getLikeUsers() {
		return likeUsers;
	}
	public void setLikeUsers(Set<Integer> likeUsers) {
		this.likeUsers = likeUsers;
	}
	
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public Integer getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}
	public Long getCollectTime() {
		return collectTime;
	}
	public void setCollectTime(Long collectTime) {
		this.collectTime = collectTime;
	}
	public Long getTop() {
		return top;
	}
	public void setTop(Long top) {
		this.top = top;
	}
	public String getCircleId() {
		return circleId;
	}
	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}
//	public ObjectId get_id() {
//		return _id;
//	}
//	public void set_id(ObjectId _id) {
//		this._id = _id;
//	}
	
	
	
	
}
