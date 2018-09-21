package com.dachen.line.stat.dao;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.dachen.line.stat.entity.vo.UserHospital;



/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IUserHospitalDao {
	
	public boolean checkGetUserHospitalList(String hospitalId, String sourceId);
	
	/**
	 * 查询制定状态下面的所有的医院
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<Map<String,Object>> getCertificatedHospitalList(Integer status);
	
	/**
	 * 查询制定字段条件下的下面的医院
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<UserHospital> getUserHospitalList(String column,Object sourceId);
	/**
	 * 查询制定字段条件下的下面的医院
	 * @param column
	 * @param sourceId
	 * @return
	 */
	public List<ObjectId> getUserHospitalStringList(String column,Object sourceId);
	
	/**
	 * 批量插入用户服务数据
	 * @param userId
	 * @return
	 */
	public void  insertBatchUserHospital(List<UserHospital> hospital);
	
	/**
	 * 批量插入用户服务数据
	 * @param userId
	 * @return
	 */
	public void  insertUserHospital(UserHospital hospital);
}
