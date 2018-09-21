package com.dachen.health.api.client.schedule.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.schedule.entity.CCareItemSchedule;
import com.dachen.health.api.client.schedule.entity.CCareItemScheduleUpdate;
import com.dachen.health.api.client.schedule.entity.CSchedule;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class ScheduleApiClientProxy extends HealthApiClientProxy {
	
	/**
	 * 直接创建schedule
	 * 尽量避免使用此接口，应该调用创建业务对象的schedule，可参照createBatchByCareItems
	 * @param schedule
	 * @return 新创建的schedule
	 * @throws HttpApiException 
	 */
	@Deprecated
	public CSchedule createItem(CSchedule schedule) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		this.putJsonStrIfNotBlank(params, "scheduleJson", schedule);
		try {
			String url = "schedule/createItem";
			CSchedule ret = this.postRequest(url, params, CSchedule.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 批量创建关怀对象的schedule
	 * @param careItemSchedules
	 * @return 创建的个数
	 * @throws HttpApiException 
	 */
	public Integer createBatchByCareItems(List<CCareItemSchedule> careItemSchedules) throws HttpApiException {
		if (null == careItemSchedules || 0 == careItemSchedules.size()) {
			return 0;
		}
		
		Map<String, String> params = new HashMap<String, String>(1);
		this.putJsonStrIfNotBlank(params, "schedulesJson", careItemSchedules);
		try {
			String url = "schedule/createBatchByCareItems";
			Integer ret = this.postRequest(url, params, Integer.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 根据关怀项删除schedules
	 * @param careItemIdList 关怀项id列表
	 * @param deadLine 截止时间
	 * @throws HttpApiException
	 */
	public Boolean deleteByCareItems(List<String> careItemIdList, Long deadLine) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>();
		if (null == careItemIdList || 0 == careItemIdList.size()) {
			throw new HttpApiException("参数为空");
		}
		putArrayIfNotBlank(params, "careItemIds", careItemIdList);
		params.put("deadLine", deadLine.toString());
		try {
			String url = "schedule/deleteByCareItems";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	

	/**
	 * 根据careItem的调整更新schedule的scheduleTime
	 * 
	 * @param careItemId 关怀项id
	 * @param fullSendTime	关怀顶被调整成的发送时间
	 * @return
	 * @throws HttpApiException 
	 */
	public Boolean updateScheduleTimeByCareItem(String careItemId, Long fullSendTime) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("careItemId", careItemId.toString());
		params.put("fullSendTime", fullSendTime.toString());
		try {
			String url = "schedule/updateScheduleTimeByCareItem";
			Boolean ret = this.postRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 批量更新schedule的scheduleTime
	 * 
	 * @param updates
	 * @return
	 * @throws HttpApiException 
	 */
	public Integer updateScheduleTimeByCareItems(List<CCareItemScheduleUpdate> updates) throws HttpApiException {
		if (null == updates || 0 == updates.size()) {
			return 0;
		}
		Map<String, String> params = new HashMap<String, String>();
		this.putJsonStrIfNotBlank(params, "jsonStr", updates);
		try {
			String url = "schedule/updateScheduleTimeByCareItems";
			Integer ret = this.postRequest(url, params, Integer.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
