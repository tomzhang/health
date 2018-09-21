package com.dachen.health.circle.entity;

public class Video {

	/**
	 * 播放地址
	 */
	private String play_url;
	/**
	 * 第一帧图片的地址
	 */
	private String play_first;
	/**
	 * 视频播放时长
	 */
	private String play_time;
	/**
	 * 视频大小
	 */
	private String size;
	/**
	 * 文件后缀名
	 */
	private String suffix;

	public String getPlay_url() {
		return play_url;
	}

	public void setPlay_url(String play_url) {
		this.play_url = play_url;
	}

	public String getPlay_first() {
		return play_first;
	}

	public void setPlay_first(String play_first) {
		this.play_first = play_first;
	}

	public String getPlay_time() {
		return play_time;
	}

	public void setPlay_time(String play_time) {
		this.play_time = play_time;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}


}
