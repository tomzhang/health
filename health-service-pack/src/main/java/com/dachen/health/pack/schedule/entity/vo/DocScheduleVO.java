package com.dachen.health.pack.schedule.entity.vo;

import java.util.List;

public class DocScheduleVO {
	
	//医生订单日程
	private List<ScheduleVO> orderScheduleVo;
	
	
	//医生值班任务
	private List<ScheduleVO> onDutyScheduleVo;


	public List<ScheduleVO> getOrderScheduleVo() {
		return orderScheduleVo;
	}


	public void setOrderScheduleVo(List<ScheduleVO> orderScheduleVo) {
		this.orderScheduleVo = orderScheduleVo;
	}

	public List<ScheduleVO> getOnDutyScheduleVo() {
		return onDutyScheduleVo;
	}


	public void setOnDutyScheduleVo(List<ScheduleVO> onDutyScheduleVo) {
		this.onDutyScheduleVo = onDutyScheduleVo;
	}
	
	
	
	
	

}
