package com.dachen.line.stat.entity.vo;


public class NurseStatistic {
	private String area;

	private String name;

	private String time;

	private String service;

	private String lineService;
	
	private String userImage;

	public String getLineService() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getArea()).append("的").append(getName())
				.append(getTime()).append("分钟前购买了").append(getService());
		lineService = buffer.toString();
		return lineService;
	}

	public void setLineService(String lineService) {
		this.lineService = lineService;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String toString() {
		return getLineService();
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}
	
}
