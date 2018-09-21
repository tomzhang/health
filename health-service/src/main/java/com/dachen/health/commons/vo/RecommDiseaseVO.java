package com.dachen.health.commons.vo;

public class RecommDiseaseVO {
	
	private String diseasesId;
	
	private String diseasesName;
	//科室对应的图片路径
	private String imgPath;
	
	//更多图片路径
	private String morePath;

	public String getDiseasesId() {
		return diseasesId;
	}

	public void setDiseasesId(String diseasesId) {
		this.diseasesId = diseasesId;
	}

	public String getDiseasesName() {
		return diseasesName;
	}

	public void setDiseasesName(String diseasesName) {
		this.diseasesName = diseasesName;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getMorePath() {
		return morePath;
	}

	public void setMorePath(String morePath) {
		this.morePath = morePath;
	}
}
