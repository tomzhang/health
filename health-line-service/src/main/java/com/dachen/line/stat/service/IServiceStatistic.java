package com.dachen.line.stat.service;

import com.dachen.line.stat.entity.vo.ServiceStatistic;


/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IServiceStatistic {
	
	public  ServiceStatistic getServiceStatisticService();
	
	public  void updateTotalReceptionNum();
	
}
