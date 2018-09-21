package com.dachen.health.document.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

@Entity(value = "t_document",noClassnameStored=true)
public class Document {
	
	@Id
	private ObjectId id;
	private String title;//标题
	private Integer documentType;//文档类型
	private String contentType;//内容分类
	
	@NotSaved
	private String TypeName;//分类名称
	
	private String copyPath;//封面
	private Integer isShowImg;
	private String thumPath;//缩略图
	private String description;//摘要
	private String content;//内容
	private String url;//文档url
	private Integer isShow;//是否显示（科普默认全部显示）
	private Integer isTop;//是否置顶
	private Integer weight;//排序权重
	private Integer enabled;//是否删除
	private long visitCount;//浏览量
	private long createTime;//创建时间
	private long lastUpdateTime;//最后更新时间
	private String recommendDoctId;// documentType=4时必传
	private Integer isRecommend;//是否在H5页跳转至详情页
	private RecommendDetails recommendDetails; //文档推荐类型

	/**是否为外部广告**/
	private Boolean externalAd;

	public Boolean getExternalAd() {
		return externalAd;
	}

	public void setExternalAd(Boolean externalAd) {
		this.externalAd = externalAd;
	}

	public Integer getIsRecommend() {
		return isRecommend;
	}
	public void setIsRecommend(Integer isRecommend) {
		this.isRecommend = isRecommend;
	}
	public RecommendDetails getRecommendDetails() {
		return recommendDetails;
	}
	public void setRecommendDetails(RecommendDetails recommendDetails) {
		this.recommendDetails = recommendDetails;
	}
	public String getThumPath() {
		return thumPath;
	}
	public void setThumPath(String thumPath) {
		this.thumPath = thumPath;
	}
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
	public Integer getDocumentType() {
		return documentType;
	}
	public void setDocumentType(Integer documentType) {
		this.documentType = documentType;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getCopyPath() {
		return copyPath;
	}
	public void setCopyPath(String copyPath) {
		this.copyPath = copyPath;
	}
	public Integer getIsShowImg() {
		return isShowImg;
	}
	public void setIsShowImg(Integer isShowImg) {
		this.isShowImg = isShowImg;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public Integer getIsShow() {
		return isShow;
	}
	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}
	public Integer getIsTop() {
		return isTop;
	}
	public void setIsTop(Integer isTop) {
		this.isTop = isTop;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public Integer getEnabled() {
		return enabled;
	}
	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
	public long getVisitCount() {
		return visitCount;
	}
	public void setVisitCount(long visitCount) {
		this.visitCount = visitCount;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getRecommendDoctId() {
		return recommendDoctId;
	}
	public void setRecommendDoctId(String recommendDoctId) {
		this.recommendDoctId = recommendDoctId;
	}
	public String getTypeName() {
		return TypeName;
	}
	public void setTypeName(String typeName) {
		TypeName = typeName;
	}
	
	
}
