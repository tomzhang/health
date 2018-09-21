package com.dachen.health.pack.incomeNew.entity.param;

import java.util.List;

import com.dachen.health.pack.incomeNew.entity.po.Incomelog;

public class IncomelogParam extends Incomelog {
	
	private String childName;
	private String telephone;
	
	private Long startTime;
	private Long endTime;
	private String oType;
	private Integer orderType;
	private Integer packType;
	private Integer kj;
	
	private List<Integer> uIdList;//用户列表
	
	
	private int pageIndex =0;
	private int pageSize = 15;
	private int start;
	
	
	
	
	
	public String getChildName() {
		return childName;
	}
	public void setChildName(String childName) {
		this.childName = childName;
	}
	public String getTelephone() {
		return telephone;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
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
		return start = (pageIndex*pageSize);
	}
	public String getoType() {
		return oType;
	}
	public void setoType(String oType) {
		this.oType = oType;
	}
	public Integer getPackType() {
		return packType;
	}
	public void setPackType(Integer packType) {
		this.packType = packType;
	}
	public Integer getOrderType() {
		return orderType;
	}
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	public Integer getKj() {
		return kj;
	}
	public void setKj(Integer kj) {
		this.kj = kj;
	}
	public List<Integer> getuIdList() {
		return uIdList;
	}
	public void setuIdList(List<Integer> uIdList) {
		this.uIdList = uIdList;
	}
	
}
