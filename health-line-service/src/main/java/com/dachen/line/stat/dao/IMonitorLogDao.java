package com.dachen.line.stat.dao;

import com.dachen.line.stat.entity.vo.MonitorLog;



/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IMonitorLogDao {
	
	/**
	 * 获取单个异常记录
	 * @return
	 */
	public MonitorLog getMonitorLogById(String  id);
	
	
	/**
	 * 获取异常列表
	 * @return
	 */
	public MonitorLog getMonitorLogList(String serviceId,int code,int type);
	
	/**
	 * 插入
	 * @param userId
	 * @return
	 */
	public Object  insertUserMonitorLog(MonitorLog monitorlog);
}
