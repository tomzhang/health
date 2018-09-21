package com.dachen.health.pack.conference.entity.param;

import com.dachen.health.pack.conference.entity.po.CallRecord;

public class CallRecordParam extends CallRecord{

	private Long duration;

	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}

}
