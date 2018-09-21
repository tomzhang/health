package com.dachen.health.pack.conference.entity.po;

import java.util.List;

import org.mongodb.morphia.annotations.NotSaved;

import com.dachen.health.pack.conference.entity.vo.ConfDetailVO;

public class ConfCall {
	
	private String  confId;//会议标识ID
	private String  creater; //会议创建者
	private Integer maxMember;//会议最大方数
	private Integer mediaType;//会议类型：1，语音会议；默认：语音会议
	private Integer duration;//会议时长
	private Integer playTone;//是否播放提示音：0不播放；1；播放。
	private Integer result;//
	private Integer status;//录音状态
	private String  recordUrl;//录音url
	
	
	@NotSaved
	private List<ConfDetailVO> list;//保证存入顺序，key为对应类型，value为对应实际纪录详情列表
	
	public String getConfId() {
		return confId;
	}
	public void setConfId(String confId) {
		this.confId = confId;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public Integer getMaxMember() {
		return maxMember;
	}
	public void setMaxMember(Integer maxMember) {
		this.maxMember = maxMember;
	}
	public Integer getMediaType() {
		return mediaType;
	}
	public void setMediaType(Integer mediaType) {
		this.mediaType = mediaType;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getPlayTone() {
		return playTone;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	public void setPlayTone(Integer playTone) {
		this.playTone = playTone;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getRecordUrl() {
		return recordUrl;
	}
	public void setRecordUrl(String recordUrl) {
		this.recordUrl = recordUrl;
	}
	public List<ConfDetailVO> getList() {
		return list;
	}
	public void setList(List<ConfDetailVO> list) {
		this.list = list;
	}
}
