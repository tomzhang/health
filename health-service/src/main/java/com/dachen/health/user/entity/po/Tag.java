package com.dachen.health.user.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;

import com.dachen.health.commons.constants.UserEnum;

/**
 * ProjectName： health-service<br>
 * ClassName： Tag<br>
 * Description： 标签实体<br>
 * 
 * @author fanp
 * @crateTime 2015年6月30日
 * @version 1.0.0
 */
@Entity(value = "u_tag", noClassnameStored = true)
@Indexes(@Index("userId,tagName"))
public class Tag {

    @Id
    private ObjectId id;

    /* 用户id */
    @Indexed
    private Integer userId;

    /* 标签名称 */
    private String tagName;

    /** 
     * 标签分类
     * @see UserEnum.TagType
     */
    private Integer tagType;
    
    /* 标签顺序 */
    private Integer seq;
    
    /* 是否系统标签 */
    private boolean isSys;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

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

    public Integer getTagType() {
        return tagType;
    }

    public void setTagType(Integer tagType) {
        this.tagType = tagType;
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

}
