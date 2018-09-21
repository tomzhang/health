package com.dachen.health.teachCenter.entity.vo;

import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.user.entity.po.Doctor;

public class ArticleVO {

	private String id;
	private boolean isTop;
	private Integer priority;
	private long collectTime;
	private Doctor doctor;
	private String collectorId;
	private String groupId;
	private String groupName;// 集团名称
	private String authorName;// 作者名称
	private long visitCount;// 访问量
	private Integer useNum;//使用量

	private int isCollect;

	private String title;
	private Integer isShare;// 1：是，0：否 分享到患中心
	private Integer isShow;// 1：是，0：否 显示到正文
	private String copyPath;
	private String copy_small;
	private String diseaseId;// 病情分类
	private DiseaseTypeVO disease;
	private String description;
	private String author;// 文章作者（可知道 是集团的那个医生）
	private String[] tags;
	private DiseaseTypeVO[] tag;

	private Integer createType;// 1：中心，2：集团，3：个体医生
	private String createrId;// 创建者ID（当前登录系统者ID）也可是集团ID

	private long creatTime;
	private long lastUpdateTime;
	private String content;
	private String url;
	private Integer edited;//0:不可以编辑，1：可以编辑

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	
	

	public String getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(String collectorId) {
		this.collectorId = collectorId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public long getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(long visitCount) {
		this.visitCount = visitCount;
	}

	
	public Integer getUseNum() {
		return useNum;
	}

	public void setUseNum(Integer useNum) {
		this.useNum = useNum;
	}

	public int isCollect() {
		return isCollect;
	}

	public void setCollect(int isCollect) {
		this.isCollect = isCollect;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getCopyPath() {
		return copyPath;
	}

	public void setCopyPath(String copyPath) {
		this.copyPath = copyPath;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public Integer isShare() {
		return isShare;
	}

	public void setShare(Integer isShare) {
		this.isShare = isShare;
	}

	public String getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}
	
	public String getCopy_small() {
		return copy_small;
	}

	public void setCopy_small(String copy_small) {
		this.copy_small = copy_small;
	}

	public DiseaseTypeVO getDisease() {
		return disease;
	}

	public void setDisease(DiseaseTypeVO disease) {
		this.disease = disease;
	}

	public long getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(long collectTime) {
		this.collectTime = collectTime;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public DiseaseTypeVO[] getTag() {
		return tag;
	}

	public void setTag(DiseaseTypeVO[] tag) {
		this.tag = tag;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doc) {
		this.doctor = doc;
	}

	public Integer getEdited() {
		return edited;
	}

	public void setEdited(Integer edited) {
		this.edited = edited;
	}
	
}
