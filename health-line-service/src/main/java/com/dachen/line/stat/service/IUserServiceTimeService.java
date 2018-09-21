package com.dachen.line.stat.service;

import java.util.List;
import java.util.Map;

import com.dachen.line.stat.entity.vo.NurseDutyTime;

public interface IUserServiceTimeService {
	
	/**
	 * 获取护士的服务时间列表
	 * @param userId
	 * @return
	 */
	public List<NurseDutyTime> getUserServiceTimeList(Integer userId);

	
	/**
	 * 设置护士的服务时间
	 */
	public void updateUserServiceTime(Integer userId,String time,Integer status);
	
	
	/**
	 * 校验护士服务时间设置
	 * @param status   
	 * 如果查询用户的时间没空，就返回101 如果用户的时间记录数目小于等于2  就返回 102   否则 转改都为成功
	101：用户没有设置时间（判断依据用户从未设置过服务时间，或是当前时间超过最大预约时间）
	102：用户需要扩大设置时间范围（判断依据用户设置的最大的时间，距离当前的时间小于2天，则提醒设置扩大服务时间）

	 */
	public Map<String,Object> checkUserServiceTimeSet(Integer userId);
}
