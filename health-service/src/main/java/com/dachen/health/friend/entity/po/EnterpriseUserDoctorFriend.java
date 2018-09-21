package com.dachen.health.friend.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

/**
 * create table "u_doctor_sales(参考u_doctor_friend表)" 
(
   ID                   varchar(0)                     not null,
   用户ID                 int                            null,
   医生ID                 int                            null,
   状态                   int                            null,
   拉黑                   int                            null,
   收藏                   int                            null,
   消息屏蔽                 int                            null,
   constraint "PK_U_DOCTOR_SALES(参考U_DOCTOR_F" primary key clustered (ID)
);
 * @author weilit
 *
 */
@Entity(value = "u_doctor_sales", noClassnameStored = true)
@Indexes(@Index("userId,status"))
public class EnterpriseUserDoctorFriend {

    @Id
    private String id;
    //用户ID
    private Integer userId;
    //医生ID
    private Integer toUserId;

    /* 添加时间 */
    private Long createTime;

    /* 状态 */
    private Integer status;
    
    /*  拉黑 */
    private Integer shield;
    
    /* 收藏*/
    private Integer collection;

    /* 消息屏蔽 */
    private Integer msgShield;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getToUserId() {
		return toUserId;
	}

	public void setToUserId(Integer toUserId) {
		this.toUserId = toUserId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getShield() {
		return shield;
	}

	public void setShield(Integer shield) {
		this.shield = shield;
	}

	public Integer getCollection() {
		return collection;
	}

	public void setCollection(Integer collection) {
		this.collection = collection;
	}

	public Integer getMsgShield() {
		return msgShield;
	}

	public void setMsgShield(Integer msgShield) {
		this.msgShield = msgShield;
	}

}
