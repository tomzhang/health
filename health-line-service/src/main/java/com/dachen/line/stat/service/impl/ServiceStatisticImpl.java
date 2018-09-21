package com.dachen.line.stat.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.line.stat.dao.IServiceStatisticDao;
import com.dachen.line.stat.entity.vo.ServiceStatistic;
import com.dachen.line.stat.service.IServiceStatistic;

/**
 * 护士订单服务
 * 
 * @author liwei
 * @date 2015/8/19
 */
@Service
public class ServiceStatisticImpl implements IServiceStatistic {

	@Autowired
	private IServiceStatisticDao serviceStatisticDao;

	public ServiceStatistic getServiceStatisticService() {
		return serviceStatisticDao.getServiceStatisticService();
	}

	public void updateTotalReceptionNum() {
		System.out.println("执行来一次"+new Date());
		serviceStatisticDao.updateTotalReceptionNum();
	}
}
