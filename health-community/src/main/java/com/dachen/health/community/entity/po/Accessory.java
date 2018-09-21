package com.dachen.health.community.entity.po;

import java.util.List;

public class Accessory {
	private List<String> imgUrls;
	private List<Files> files;
	private List<Video> videos;
	private List<String> digestImgUrls;//题图数组

	public List<String> getDigestImgUrls() {
		return digestImgUrls;
	}

	public void setDigestImgUrls(List<String> digestImgUrls) {
		this.digestImgUrls = digestImgUrls;
	}

	public List<String> getImgUrls() {
		return imgUrls;
	}

	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}

	public List<Files> getFiles() {
		return files;
	}

	public void setFiles(List<Files> files) {
		this.files = files;
	}

	public List<Video> getVideos() {
		return videos;
	}

	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}
	
	
}
