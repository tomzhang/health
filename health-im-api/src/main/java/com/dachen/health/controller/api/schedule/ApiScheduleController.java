package com.dachen.health.controller.api.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.api.client.schedule.entity.CCareItemSchedule;
import com.dachen.health.api.client.schedule.entity.CCareItemScheduleUpdate;
import com.dachen.health.pack.schedule.dao.IScheduleDao;
import com.dachen.health.pack.schedule.entity.po.Schedule;
import com.dachen.health.pack.schedule.service.IScheduleService;
import com.dachen.util.JSONUtil;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/schedule")
public class ApiScheduleController extends ApiBaseController {

	@Resource
	protected IScheduleService scheduleService;

	@Resource
	protected IScheduleDao scheduleDao;

	@RequestMapping(value = "createItem", method = RequestMethod.POST)
	public JSONMessage createItem(@RequestParam(required = true) String scheduleJson) {
		String tag = "createItem";
		logger.info("{}. scheduleJson={}", tag, scheduleJson);

		Schedule schedule = JSONUtil.parseObject(Schedule.class, scheduleJson);

		schedule = this.scheduleDao.save(schedule);

		return JSONMessage.success(null, schedule);
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "createBatchByCareItems", method = RequestMethod.POST)
	public JSONMessage createBatchByCareItems(@RequestParam(required = true) String schedulesJson) {
		String tag = "createBatchByCareItems";
		logger.info("{}. schedulesJson={}", tag, schedulesJson);

		List list = JSONUtil.parseObject(List.class, schedulesJson);
		if (CollectionUtils.isEmpty(list)) {
			return JSONMessage.failure("schedulesJson is empty!");
		}
		
		Integer count = list.size();
		List<CCareItemSchedule> careItemSchedules = new ArrayList<CCareItemSchedule>(count);
		for (Object o:list) {
			CCareItemSchedule careItemSchedule = JSONUtil.parseObject(CCareItemSchedule.class, o.toString());
			careItemSchedules.add(careItemSchedule);
		}
		scheduleService.createCareItemSchedules(careItemSchedules);
		
		return JSONMessage.success(null, count);
	}

	@RequestMapping(value = "deleteByCareItems", method = RequestMethod.POST)
	public JSONMessage deleteByCareItems(String[] careItemIds, Long deadline) {
		String tag = "createItem";
		logger.info("{}. careItemIds={}, deadline={}", tag, careItemIds, deadline);

		if (null == careItemIds || 0 == careItemIds.length) {
			return JSONMessage.success();
		}

		this.scheduleService.deleteByCareItems(Arrays.asList(careItemIds), deadline);

		return JSONMessage.success(null, true);
	}

	@RequestMapping(value = "updateScheduleTimeByCareItem", method = RequestMethod.POST)
	public JSONMessage updateScheduleTimeByCareItem(String careItemId, Long fullSendTime) {
		String tag = "updateScheduleTimeByCareItem";
		logger.info("{}. careItemId={}, fullSendTime={}", tag, careItemId, fullSendTime);

		boolean ret = scheduleDao.updateByCareItemId(careItemId, fullSendTime);
		return JSONMessage.success(null, ret);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "updateScheduleTimeByCareItems", method = RequestMethod.POST)
	public JSONMessage updateScheduleTimeByCareItems(@RequestParam(required=true) String jsonStr) {
		String tag = "updateScheduleTimeByCareItems";
		logger.info("{}. jsonStr={}", tag, jsonStr);
		
		List list = JSONUtil.parseObject(List.class, jsonStr);
		if (CollectionUtils.isEmpty(list)) {
			return JSONMessage.success();
		}
		
		List<CCareItemScheduleUpdate> updates = new ArrayList<CCareItemScheduleUpdate>(list.size());
		for (Object o:list) {
			updates.add(JSONUtil.parseObject(CCareItemScheduleUpdate.class, o.toString()));
		}

		int ret = scheduleService.updateCareItemSchedules(updates);
		return JSONMessage.success(null, ret);
	}

}
