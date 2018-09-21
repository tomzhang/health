package com.dachen.health.community.entity.vo;

import com.dachen.health.community.entity.po.Files;
import com.dachen.health.community.entity.po.Video;

import java.util.List;
/**
 * 页面显示帖子id
 * @Description 
 * @title TopicVo
 * @author liminng
 * @data 2016年7月27日
 */
public class TopicVo {
	private String Id;
	/**
	 * 最新更新时间
	 */
	private String updateTime;
	

	
	private String userId;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 点赞数量
	 */
	private Long likeCount;
	/**
	 * 页面浏览量
	 */
	private Long pageView;
	/**
	 * 回复数量
	 */
	private Long replies;
	/**
	 * 是否已点赞
	 */
	private String like;
	/**
	 * 是否已收藏
	 */
	private String collect;
	/**
	 * 是否可以删除
	 */
	private String delete;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 出文本回复
	 */
	private String text;
	/**
	 * 集团标签
	 */
	private List<String> label;
	/**
	 * 创建用户的id
	 */
	private String createUserId;
	/**
	 * 创建人姓名
	 */
	private String createName;
	/**
	 * 发表人头像地址
	 */
	private String headUrl;
	/**
	 * 最新两条回复
	 */
	private List<ReplyVo> replys;
	/**
	 * 查看详情接口
	 */
	private String richText;
	/**
	 * 富文本长度
	 */
	private Integer richTextLength;
	/**
	 * 帖子类别
	 */
	private String type;
	/**
	 * 圈子id
	 */
	private String circleId;
	/**
	 * 圈子名称
	 */
	private String circleName;
	/**
	 * 附件信息
	 */
	private List<Files> files;
	/**
	 * 视频信息
	 */
	private List<Video> video;
	private List<String> imgUrls;
	/**
	 * 时候为置顶帖子
	 * 0 置顶
	 * 1 非置顶
	 */
	private String top;
	/**
	 * 集团id
	 */
	private String groupId;
	/**
	 * 帖子状态
	 */
	private String state;
	/**
	 * 摘要
	 */
	private String digest;
	/**
	 * 题图数组
	 */
	private List<String> digestImgUrls;

	public Integer getRichTextLength() {
		return richTextLength;
	}

	public void setRichTextLength(Integer richTextLength) {
		this.richTextLength = richTextLength;
	}

	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Long getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
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
	public String getLike() {
		return like;
	}
	public void setLike(String like) {
		this.like = like;
	}
	public String getCollect() {
		return collect;
	}
	public void setCollect(String collect) {
		this.collect = collect;
	}
	public String getDelete() {
		return delete;
	}
	public void setDelete(String delete) {
		this.delete = delete;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<String> getLabel() {
		return label;
	}
	public void setLabel(List<String> label) {
		this.label = label;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	public String getHeadUrl() {
		return headUrl;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	public List<ReplyVo> getReplys() {
		return replys;
	}
	public void setReplys(List<ReplyVo> replys) {
		this.replys = replys;
	}
	public String getRichText() {
		return richText;
	}
	public void setRichText(String richText) {
		this.richText = richText;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getImgUrls() {
		return imgUrls;
	}
	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}
	public String getCircleId() {
		return circleId;
	}
	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}
	public String getCircleName() {
		return circleName;
	}
	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}
	public List<Files> getFiles() {
		return files;
	}
	public void setFiles(List<Files> files) {
		this.files = files;
	}
	public List<Video> getVideo() {
		return video;
	}
	public void setVideo(List<Video> video) {
		this.video = video;
	}
	public String getTop() {
		return top;
	}
	public void setTop(String top) {
		this.top = top;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public List<String> getDigestImgUrls() {
		return digestImgUrls;
	}

	public void setDigestImgUrls(List<String> digestImgUrls) {
		this.digestImgUrls = digestImgUrls;
	}
}
