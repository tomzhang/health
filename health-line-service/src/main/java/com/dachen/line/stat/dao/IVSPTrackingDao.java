package com.dachen.line.stat.dao;

import java.util.List;

import com.dachen.line.stat.entity.vo.VSPTracking;



/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IVSPTrackingDao {
	
	/**
	 * 查询制定字段条件下的下面的医院
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<VSPTracking> getVSPTrackingList(String column,Object sourceId);
	
	
	/**
	 * 批量插入用户服务数据
	 * @param userId
	 * @return
	 */
	public void  insertVSPTracking(VSPTracking trace);
	
	
	/**
	 * 根据订单id查询所有于此订单有关的记录--其实主要是为了查询code
	 * @param orderId
	 * @return
	 */
	public List<Integer> getTrackListByOrderId(String orderId);
}
