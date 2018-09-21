package com.dachen.health.commons.constants;

public enum EvaluationItemEnum {

	good(1, "好评"),
	general(2, "一般"),
	worse(3, "差评");
	
	private Integer index;
	private String title;
	
	private EvaluationItemEnum(int index, String title) {
		this.index = index;
		this.title = title;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

}
