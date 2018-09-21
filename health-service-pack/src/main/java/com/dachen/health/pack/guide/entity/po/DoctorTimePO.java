package com.dachen.health.pack.guide.entity.po;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "t_doctor_time",noClassnameStored = true)
//@Indexes( {@Index("groupId, state"), @Index("guideId, startTime")})  
public class DoctorTimePO {
	/*医生Id*/
	@Id
	private Integer doctorId;
	
	private List<Time>timeList;
	
	private List<Remark> remarkList;
	
	
	public List<Remark> getRemarkList() {
		return remarkList;
	}

	public void setRemarkList(List<Remark> remarkList) {
		this.remarkList = remarkList;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public List<Time> getTimeList() {
		return timeList;
	}

	public void setTimeList(List<Time> timeList) {
		this.timeList = timeList;
	}
	
	public static class Time{
//		private String day;
		private Long start;
		
		private Long end;
		
		/*预约次数*/
		private int count;
		public Time()
		{
			
		}
		public Time(Long start,Long end,int count)
		{
			this.start = start;
			this.end = end;
			this.count = count;
		}
		public Long getStart() {
			return start;
		}

		public void setStart(Long start) {
			this.start = start;
		}
		
		public Long getEnd() {
			return end;
		}

		public void setEnd(Long end) {
			this.end = end;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}
	
	
	public static class Remark{
		private String remark;
		private String guideName;//导医名字
		private String guideId;//导医名字
		private Long createTime;//创建时间
		public Remark(){
		}
		public Remark(String remark,String guideName,String guideId,Long createTime){
			this.remark = remark;
			this.guideName = guideName;
			this.guideId = guideId;
			this.createTime = createTime;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public String getGuideName() {
			return guideName;
		}
		public void setGuideName(String guideName) {
			this.guideName = guideName;
		}
		public String getGuideId() {
			return guideId;
		}
		public void setGuideId(String guideId) {
			this.guideId = guideId;
		}
		public Long getCreateTime() {
			return createTime;
		}
		public void setCreateTime(Long createTime) {
			this.createTime = createTime;
		}
	}

}
