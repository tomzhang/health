package com.dachen.line.stat.dao;

import java.util.List;

import com.dachen.line.stat.entity.vo.ServiceImage;



/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IServiceImageDao {
	
	/**
	 *修改图片
	 * @param userId
	 * @param serviceId
	 * @param status
	 */
	public void updateUserServiceImage(String  id, String imageId);
	
	/**
	 * 删除短信
	 * @param userId
	 * @return
	 */
	public void  deleteUserServiceImage(String ServiceImageId);
	
	
	/**
	 * 删除短信
	 * @param userId
	 * @return
	 */
	public Object  insertUserServiceImage(ServiceImage ServiceImage);
	
	/**
	 * 查询制定字段条件下的下面的图片
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<ServiceImage> getServiceImageList(String column,Object sourceId);
	
	public List<String> getServiceImageStringList(String column,
			Object sourceId);
	
}
