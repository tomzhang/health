package com.dachen.health.group.group.service;

import com.dachen.health.group.group.entity.param.GroupSettingParam;


public interface IGroupSettingService {
	
	//给医生集团设置专长
	public void setSpecialty(GroupSettingParam param);
	
	//设置权重
	public void setWeights(GroupSettingParam param);
	
	/**
	 * 设置预约专家
	 * @param param
	 */
	public void setResExpert(GroupSettingParam param);
	
	/**
	 * 设置医生在集团消息免打扰
	 * @param param
	 */
	public void setMsgDisturb(GroupSettingParam param);
	
	public boolean updateDutyTime(String groupId, String dutyStartTime, String dutyEndTime);
	
}
