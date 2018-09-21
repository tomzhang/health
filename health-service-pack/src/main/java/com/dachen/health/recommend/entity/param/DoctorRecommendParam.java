package com.dachen.health.recommend.entity.param;

import com.dachen.health.recommend.entity.po.DoctorRecommend;

public class DoctorRecommendParam extends DoctorRecommend{
	
	private Integer doctorId;
	
	//运营平台添加多个医生
	private Integer[] doctorIds;
	
	private Boolean isApp = true;
	
	private String source;

	/**
	 * 返回的页码
	 */
	protected int pageIndex;
	
	/**
	 * 每页数据大小
	 */
	protected int pageSize;


	protected int start;

	
	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
	
	public Boolean getIsApp() {
		return isApp;
	}

	public void setIsApp(Boolean isApp) {
		this.isApp = isApp;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStart() {
		return start=(pageSize*pageIndex);
	}
	
	
	public Integer[] getDoctorIds() {
		return doctorIds;
	}
	
	public void setDoctorIds(Integer[] doctorIds) {
		this.doctorIds = doctorIds;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}


}
