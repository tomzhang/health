package com.dachen.health.teachCenter.entity.param;

import java.util.List;

import com.dachen.commons.page.PageVO;
public class ArticleParam extends PageVO{
	
	protected int pageSize = 20;
	private String sortType;//排序顺序asc：升序，desc：降序
	private String sortBy;//排序字段
	private Integer collectType;
	private String visitorId;
	private String groupId;
	
	private List<String> groupIds;//多集团
	
	private String articleId;
	
	private String url;
//	private String collectorId;
	private Integer isShare;//是否分享到患中心
	private Integer isShow;//是否显示到正文
	private boolean isTop;
	private Integer priority;
	private String diseaseId;
	private String copyPath;
	private String copy_small;
	private String description;
	private String author;//文章作者
	private String[] tags;
	private String title;
	private Integer createType;//1：中心，2：集团，3：个体医生
	private String createrId;//创建都ID（当前登录系统者ID）
	private String content;//文章正文
	private long creatTime;
	private long lastUpdateTime;
	private Integer edited;//0:不可以编辑，1：可以编辑
	
	private List<String> ids;
	
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	public boolean isTop() {
		return isTop;
	}
	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public Integer getCollectType() {
		return collectType;
	}
	public void setCollectType(Integer collectType) {
		this.collectType = collectType;
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
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public void setCreaterId(String  createrId) {
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
	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	public String getVisitorId() {
		return visitorId;
	}

	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
//	public String getCollectorId() {
//		return collectorId;
//	}

//	public void setCollectorId(String collectorId) {
//		this.collectorId = collectorId;
//	}
	public String getSortType() {
		return sortType;
	}
	public void setSortType(String sortType) {
		this.sortType = sortType;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public Integer getEdited() {
		return edited;
	}
	public void setEdited(Integer edited) {
		this.edited = edited;
	}
	public List<String> getGroupIds() {
		return groupIds;
	}
	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	
	
}
