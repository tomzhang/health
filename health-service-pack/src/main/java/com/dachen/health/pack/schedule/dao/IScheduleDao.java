package com.dachen.health.pack.schedule.dao;

import java.util.List;

import org.mongodb.morphia.query.Query;

import com.dachen.health.pack.schedule.entity.po.Schedule;
import com.dachen.health.pack.schedule.entity.vo.ScheduleParam;

public interface IScheduleDao {

	public Schedule save(Schedule schedule);
	
	public boolean update(Schedule schedule);
	
	boolean updateByCareItemId(String careItemId, Long scheduleTime);
	
	boolean updateByRelationId(String relationId, Long scheduleTime);
	
	public boolean updateByRelationId(String relationId, Integer status);
	
	public boolean delete(String id);
	
	Schedule getByRelationId(String relationId);
	
	Schedule getByCareItemId(String careItemId, Long deadline);
	
	Query<Schedule> getSchedules(ScheduleParam param);
	
	List<Schedule> getSendSchedule(Long leftTime, Long rigthTime);
	
	Query<Schedule> getSchedulesCount(ScheduleParam param);
	List<Schedule> getSendSchedule(Long leftTime,Long rigthTime,String title);
	
}
