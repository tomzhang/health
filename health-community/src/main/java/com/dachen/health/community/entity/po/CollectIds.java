package com.dachen.health.community.entity.po;

public class CollectIds {
	private String topicId;
	private Long time;
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	@Override
	public boolean equals(Object obj) {
		if(this.getTopicId().equals(obj.toString())){
			return true;
		}
		
		return false;
	}
	
	
}
