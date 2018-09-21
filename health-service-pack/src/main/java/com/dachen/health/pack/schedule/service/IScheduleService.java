package com.dachen.health.pack.schedule.service;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.api.client.schedule.entity.CCareItemSchedule;
import com.dachen.health.api.client.schedule.entity.CCareItemScheduleUpdate;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.schedule.entity.po.Schedule;
import com.dachen.health.pack.schedule.entity.vo.ScheduleParam;
import com.dachen.health.pack.schedule.entity.vo.ScheduleVO;
import com.dachen.sdk.exception.HttpApiException;

public interface IScheduleService {

	/**
	 * 保存或修改日程
	 * @param relationId
	 * @param appointTime
	 * @param type
	 * @return
	 */
	void createOrderSchedule(Integer orderId, Long appointTime);
	
	void createGuiderSchedule(Order order, Long appointTime);
	
	/**
	 * 自动发送日程提醒（短信）
	 */
	void scheduleRemind();
	/**
	 * 自动发送预约名医的日程提醒(短信)
	 */
	void scheduleAppointment();
	
	/**
	 * 获取Schedule集合
	 * @param param
	 * @return
	 */
	List<Schedule> getSchedules(ScheduleParam param);
	
	/**
	 * 获取ScheduleVO集合
	 * @param param
	 * @return
	 */
	List<ScheduleVO> getScheduleVOs(ScheduleParam param) throws HttpApiException;
	
	
	/**
	 * 分页获取ScheduleVO集合
	 * @param param
	 * @param grouping 是否对数据按照日期分组
	 * @return
	 */
	PageVO getScheduleVOsPage(ScheduleParam param, boolean grouping) throws HttpApiException;
	
	/**
	 * 获取尚未进行服务的订单数
	 * @param param
	 * @param grouping 是否对数据按照日期分组
	 * @return
	 */
	int getNoServiceCount(ScheduleParam param, boolean grouping) throws HttpApiException;

	void deleteByCareItems(List<String> careItemIds, Long deadline);

	Schedule createCareItemSchedule(CCareItemSchedule careItemSchedule);
	
	void createCareItemSchedules(List<CCareItemSchedule> careItemSchedules);
	int updateCareItemSchedules(List<CCareItemScheduleUpdate> updates);
	
}
