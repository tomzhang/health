package com.dachen.health.pack.order.entity.vo;

import java.util.List;

public class CareSimpleVO {
	private Integer packId;
	/**健康关怀名称**/
	private String name;
	/**健康关怀价格**/
	private Long price;
	/**健康关怀的医生**/
	private List<DoctoreRatioVO> doctoreRatios;
	public Integer getPackId() {
		return packId;
	}
	public void setPackId(Integer packId) {
		this.packId = packId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}
	public List<DoctoreRatioVO> getDoctoreRatios() {
		return doctoreRatios;
	}
	public void setDoctoreRatios(List<DoctoreRatioVO> doctoreRatios) {
		this.doctoreRatios = doctoreRatios;
	}
}
