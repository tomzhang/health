package com.dachen.health.pack.order.service;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.schedule.entity.vo.DocScheduleVO;
import com.dachen.health.pack.schedule.entity.vo.ScheduleParam;
import com.dachen.health.pack.schedule.entity.vo.ScheduleRecordVO;
import com.dachen.health.pack.schedule.entity.vo.ScheduleVO;
import com.dachen.sdk.exception.HttpApiException;

public interface IOrderExpandService {

	
	/**
     * 获取医生日程
     */
    DocScheduleVO getDocSchedule(ScheduleParam param) throws HttpApiException;
    
    /**
     * 获取日程列表，包含订单详情
     */
    List<ScheduleVO> scheduleDetail(ScheduleParam param) throws HttpApiException;
    
    /**
     * 获取患者日程
     */
    PageVO getPatSchedule(ScheduleParam param) throws HttpApiException;
    
    /**
     * 统计医生日程记录
     * @param param
     * @return
     */
    List<ScheduleRecordVO> getDocScheduleRecord(ScheduleParam param);
    
    
    /**
     * 获取日程集合
     * @param param
     * @return
     */
    PageVO getSchedules(ScheduleParam param) throws HttpApiException;
    /**
     * 获取导医当天之后尚未进行服务的订单数目（条件：1-当天时间之后 2-尚未拨打电话已经拨打电话失败的订单）
     * @param param
     * @return
     */
    int getNoServiceCount(ScheduleParam param) throws HttpApiException;
}
