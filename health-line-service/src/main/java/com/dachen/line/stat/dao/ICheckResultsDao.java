package com.dachen.line.stat.dao;

import java.util.List;

import com.dachen.line.stat.entity.vo.CheckResults;



/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface ICheckResultsDao {
	
	/**
	 *修改图片
	 * @param userId
	 * @param serviceId
	 * @param status
	 */
	public void updateCheckResults(String  id, String imageId);
	
	/**
	 * 删除短信
	 * @param userId
	 * @return
	 */
	public void  deleteCheckResults(String CheckResultsId);
	
	
	/**
	 * 删除短信
	 * @param userId
	 * @return
	 */
	public Object  insertCheckResults(CheckResults CheckResults);
	
	
	/**
	 * 删除短信
	 * @param userId
	 * @return
	 */
	public void  insertBatchCheckResults(List<CheckResults> CheckResults);
	
	/**
	 * 查询制定字段条件下的下面的图片
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<CheckResults> getCheckResultsList(String column,
			Object sourceId);
	
	
	/**
	 * 查询制定字段条件下的下面的图片
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<String> getCheckResultsStringList(String column,
			Object sourceId);
	
}
