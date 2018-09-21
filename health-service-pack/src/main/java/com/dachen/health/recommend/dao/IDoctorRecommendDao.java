package com.dachen.health.recommend.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongodb.morphia.query.Query;

import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.recommend.entity.param.DoctorRecommendParam;
import com.dachen.health.recommend.entity.po.DoctorRecommend;
import com.dachen.health.recommend.entity.vo.DoctorRecommendVO;

public interface IDoctorRecommendDao {
	
	
	/**
	 * 根据groupId doctorId 获取对应的cgroupDoctor Id
	 * @param groupId
	 * @param doctorId
	 * @return
	 */
	String getGroupDoctorId(String groupId,String doctorId); 
	
	/**
	 * 根据集团Id去 t_doctor_recommend 去查权重
	 * @param groupId
	 * @param isFirst true 第一个，false 最后一个
	 * @return
	 */
	Integer getWeightByGroupId(String groupId,boolean isFirst);
	
	List<DoctorRecommendVO> getWeightList(String groupId);
	
	
	/**
	 * 添加到数据库
	 * @param param
	 * @return
	 */
	DoctorRecommendVO addDoctorRecommend(DoctorRecommendParam param);
	
	/**
	 * 根据Id获取对应推荐名医信息
	 * @param Id
	 * @return
	 */
	DoctorRecommendVO getDoctorRecommendById(String id);
	
	/**
	 * 根据ID删除对应推荐名医
	 * @param id
	 * @return
	 */
	boolean delDoctorRecommendById(String id);
	
	boolean updateDoctorRecommend(DoctorRecommend dr);
	
	/**
	 * 根据 c_group_Doctor表里ID列表去 t_doctor_recommend表里查 
	 * isApp true查推荐，其它所有
	 * @param list
	 * @return
	 */
	Query<DoctorRecommendVO> getDoctorRecommendQuery(List<String> list,boolean isApp);
	
	/**
	 * 根据groupId 列表获取对应的c_group_doctor表里Id和doctorId
	 * @param groupId
	 * @return
	 */
	Map<Integer, String> getGroupDoctorInfosByGroupId(String groupId);
	
	String getAllGroupIds(Integer docId);
	
	Query<DoctorRecommendVO> getDoctorRecommendInPlatform();
	
	Set<Integer> searchDoctorIdsByName(String keyword);

	List<Integer> getDoctorIds(String groupId, Integer pageIndex, Integer pageSize);
	
	Long getDoctorIdsCount(String groupId);
	
	/**
	 * 查找所有的平台推荐医生
	 * @param groupId
	 * @return
	 */
	List<DoctorRecommendVO> getAllPlatformDoctors();
	
	DoctorRecommend getByGroupAndGroupDocId(String groupId, String groupDoctorId);
	
	/**
	 * 根据集团id查询医生的Id
	 * @param groupId
	 * @return
	 */
	List<Integer> getDoctorIdsByGroupId(String groupId);
	
	List<User> getUserByName(String name);
}
