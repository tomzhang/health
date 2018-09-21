package com.dachen.health.pack.schedule.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "t_schedule", noClassnameStored = true)
public class Schedule {

	@Id
	private ObjectId id;
	
	/**
	 * 日程主题
	 */
	private String title;
	
	/**
	 * 日程类型
	 * @see ScheduleType
	 */
	private Integer type;
	
	/**
	 * 关系Id，如订单Id
	 */
	private String relationId;
	
	/**
	 * 用户Id
	 */
	private Integer userId;
	
	/**
	 * 日程发生时间
	 */
	private Long scheduleTime;
	
	/**
	 * 创建时间
	 */
	private Long createTime;
	
	/**
	 * 发送提醒时间
	 */
	private Long sendTime;
	
	private String careItemId;
	
	
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getRelationId() {
		return relationId;
	}

	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Long getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Long scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	
	public Long getSendTime() {
		return sendTime;
	}

	public void setSendTime(Long sendTime) {
		this.sendTime = sendTime;
	}

	public String getCareItemId() {
		return careItemId;
	}

	public void setCareItemId(String careItemId) {
		this.careItemId = careItemId;
	}

	public enum ScheduleType {
		order(1, "订单日程"),
		illness(2, "病情跟踪"),
		life(3, "生活量表"),
		survey(4, "调查表"),
		check(5, "检查项"),
		remind(6, "提醒"),
		article(7,"患教资料");
		
		private int index;
		private String title;
		private ScheduleType(int index, String title) {
			this.index = index;
			this.title = title;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
	}

}
