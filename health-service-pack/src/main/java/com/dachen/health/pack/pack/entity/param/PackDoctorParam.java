package com.dachen.health.pack.pack.entity.param;

import com.dachen.health.pack.pack.entity.po.PackDoctor;

import java.util.List;

public class PackDoctorParam {
	private Integer packId;
	private List<PackDoctor> packDoctorList;

	private List<String> goodsGroupIds;

	public List<String> getGoodsGroupIds() {
		return goodsGroupIds;
	}

	public void setGoodsGroupIds(List<String> goodsGroupIds) {
		this.goodsGroupIds = goodsGroupIds;
	}

	public Integer getPackId() {
		return packId;
	}
	public void setPackId(Integer packId) {
		this.packId = packId;
	}
	public List<PackDoctor> getPackDoctorList() {
		return packDoctorList;
	}
	public void setPackDoctorList(List<PackDoctor> packDoctorList) {
		this.packDoctorList = packDoctorList;
	}
	
	
    
}