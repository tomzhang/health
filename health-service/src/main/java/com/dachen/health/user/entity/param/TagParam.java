package com.dachen.health.user.entity.param;

/**
 * ProjectName： health-service<br>
 * ClassName： TagParam<br>
 * Description： 标签设置信息参数类<br>
 * 
 * @author fanp
 * @crateTime 2015年7月2日
 * @version 1.0.0
 */
public class TagParam {

    /* 当前用户id */
    private Integer userId;

    private Integer patientId;

    /* 标签名称 */
    private String tagName;

    /* 标签原始名称 */
    private String oldName;

    /* 标签下用户id */
    private Integer[] userIds;

    /* 标签分类 */
    private Integer tagType;

    /* 标签名称 */
    private String[] tagNames;

    /* 好友id */
    private Integer id;
    
    /* 顺序 */
    private Integer seq;
    
    /* 是否系统标签 */
    private boolean isSys;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public Integer[] getUserIds() {
        return userIds;
    }

    public void setUserIds(Integer[] userIds) {
        this.userIds = userIds;
    }

    public Integer getTagType() {
        return tagType;
    }

    public void setTagType(Integer tagType) {
        this.tagType = tagType;
    }

    public String[] getTagNames() {
        return tagNames;
    }

    public void setTagNames(String[] tagNames) {
        this.tagNames = tagNames;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public boolean isSys() {
		return isSys;
	}

	public void setSys(boolean isSys) {
		this.isSys = isSys;
	}

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }
}
