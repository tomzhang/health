package com.dachen.health.teachCenter.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "i_article")
public class Article {
	
	@Id
	private ObjectId  id;
	
	private String title;
	private Integer isShare;//1：是，0：否 分享到患中心
	private Integer isShow;//1：是，0：否  显示到正文
	private String copyPath;
	private String copy_small;
	private String diseaseId;//病情分类
	private String description;
	private String author;//文章作者（可知道 是集团的那个医生）
	private String[] tags;
	
	private Integer createType;//1：中心，2：集团，3：个体医生  
	private String createrId;//创建者ID（当前登录系统者ID）也可是集团ID
	
	private long creatTime;
	private long lastUpdateTime;
	private String  content;
	private String url;//文章的静态地址
	private long visitCount;//访问量
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDiseaseId() {
		return diseaseId;
	}
	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}
	public String getCopyPath() {
		return copyPath;
	}
	public void setCopyPath(String copyPath) {
		this.copyPath = copyPath;
	}
	
	public String getCopy_small() {
		return copy_small;
	}
	public void setCopy_small(String copy_small) {
		this.copy_small = copy_small;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Integer getCreateType() {
		return createType;
	}
	public void setCreateType(Integer createType) {
		this.createType = createType;
	}
	public String getCreaterId() {
		return createrId;
	}
	public void setCreaterId(String createrId) {
		this.createrId = createrId;
	}
	public long getCreatTime() {
		return creatTime;
	}
	public void setCreatTime(long creatTime) {
		this.creatTime = creatTime;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Integer getIsShare() {
		return isShare;
	}
	public void setIsShare(Integer isShare) {
		this.isShare = isShare;
	}
	public Integer getIsShow() {
		return isShow;
	}
	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getVisitCount() {
		return visitCount;
	}
	public void setVisitCount(long visitCount) {
		this.visitCount = visitCount;
	}
	
	
}
