package com.dachen.line.stat.dao;

import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.line.stat.entity.vo.LineService;

/**
 * 护士订单服务
 * 
 * @author weilit 2015 12 04
 */
public interface ILineServiceDao {

	/**
	 * 获取单个检查项目对象
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public LineService getLineServiceById(String id);

	public boolean checkLineServiceByTitle(String title);

	/**
	 * 删除检查项目
	 * 
	 * @param userId
	 * @return
	 */
	public void deleteUserLineService(String LineServiceId);

	/**
	 * 插入检查项目
	 * 
	 * @param userId
	 * @return
	 */
	public Object insertUserLineService(LineService LineService);
	
	
	/**
	 * 如果存在就不插入，不存在就插入
	 * @return
	 */
	public Object insertUserLineServiceAndCheck(LineService LineService);
	
	/**
	 * 获取基础检查项
	 * @param id
	 * @return
	 */
	 public CheckSuggest getCheckSuggestById(String id);
	 
		
}
