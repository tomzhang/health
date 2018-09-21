package com.dachen.health.community.entity.param;

import com.dachen.commons.page.PageVO;
import com.dachen.health.community.entity.po.Files;
import com.dachen.health.community.entity.po.Video;

import java.util.List;

public class TopicParam extends PageVO{
	/**
	 * id
	 */
	private String id;
	/**
	 * 类型
	 */
	private String Type="0";
	/**
	 * 集团id
	 */
	private String groupId;
	/**
	 * 创建用户的id
	 */
	private Integer createUserId;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 集团标签
	 */
	private List<String> label;
	/**
	 * 简单文本
	 */
	private String text;
	/**
	 * 富文本
	 */
	private String richText;
	/**
	 * 摘要
	 */
	private String digest;
	/**
	 * 作者名称
	 */
	private String authorName;
	/**
	 * 地址名称
	 */
	private List<String> imgUrls;
	/**
	 * 附件信息
	 */
	private List<Files> files;
	/**
	 * 视频信息
	 */
	private List<Video> video;
	/**
	 * 圈子id
	 */
	private String circleId;
	/**
	 * 搜索的关键词
	 */
	private String keyWord;
	/**
	 * 保存模式
	 */
	private Integer saveType=0;
	/**
	 * 题图数组
	 */
	private List<String> digestImgUrls;
	/**
	 * 富文本长度
	 */
	private Integer richTextLength;

	public Integer getRichTextLength() {
		return richTextLength;
	}

	public void setRichTextLength(Integer richTextLength) {
		this.richTextLength = richTextLength;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public Integer getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getRichText() {
		return richText;
	}
	public void setRichText(String richText) {
		this.richText = richText;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public List<String> getImgUrls() {
		return imgUrls;
	}
	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String> getLabel() {
		return label;
	}
	public void setLabel(List<String> label) {
		this.label = label;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
	public String getCircleId() {
		return circleId;
	}
	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public Integer getSaveType() {
		return saveType;
	}

	public void setSaveType(Integer saveType) {
		this.saveType = saveType;
	}

	public List<String> getDigestImgUrls() {
		return digestImgUrls;
	}

	public void setDigestImgUrls(List<String> digestImgUrls) {
		this.digestImgUrls = digestImgUrls;
	}

}
