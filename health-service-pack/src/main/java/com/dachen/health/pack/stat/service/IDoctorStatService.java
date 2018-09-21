package com.dachen.health.pack.stat.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.commons.constants.UserEnum.RelationType;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.entity.param.GroupSearchParam;
import com.dachen.health.group.group.entity.vo.GroupSearchVO;
import com.dachen.health.pack.stat.entity.vo.DoctorStatVO;

import java.util.List;
import java.util.Map;

public interface IDoctorStatService {

	/**
	 * 今日门诊
	 */
	PageVO findOnDutyToday(GroupSearchParam param);
	
	/**
	 * 预约名医
	 */
	PageVO findOrderDoctor(GroupSearchParam param);
	
	/**
	 * 获取所有医生
	 * @param param
	 * @return
	 */
	PageVO findAllDoctor(GroupSearchParam param);
	
	/**
	 * 在线咨询（博德嘉联）
	 * @param param
	 * @return
	 */
	PageVO findOnlineConsultDoctor(GroupSearchParam param);
	
	/**
	 * 根据擅长病种过滤医生
	 */
	List<GroupSearchVO> findDoctorByDisease(String diseaseId);
    
	/**
	 * 根据擅长科室过滤医生
	 */
    List<GroupSearchVO> findDoctorByDept(String deptId);
    
	/**
	 * 转换成客户端所需要的数据格式
	 * @param users
	 * @return
	 */
    public List<DoctorStatVO> convert(List<User> users);
    
    public List<Integer> getOnlineDoctors();
    
    /**
     * 患者 我的医生
     * @param relationType
     * @param userId
     * @return
     */
    Map<String, Object> getDoctorPatient(RelationType relationType, Integer userId);
    
    PageVO getGroupOnlineDoctors(String groupId, Integer countryId, Integer provinceId, Integer cityId, String deptId, Integer pageIndex, Integer pageSize);
    
    /**
     * 医生名 ，集团名， 集团ID， 病种 ，
     * @param param
     * @return
     */
    Object searchDoctorByKeyWord(GroupSearchParam param);
    
    /**
     * 根据病种Id搜索对应的医生列表
     * @param param
     * @return
     */
    List<DoctorStatVO> searchDoctorByDiseaseId(GroupSearchParam param);

	PageVO findAppointmentDoctor(GroupSearchParam param);
    
	PageVO findDoctorsByGID(GroupSearchParam param);
	
	/**
     * 获取有医生的科室
     * @return
     */
    List<DeptVO> getAllDoctDepts(String groupId,String type);
    
    /**
     * 根据服务类型获取医生
     * @param groupId
     * @param packType
     * @return
     */
    PageVO getDoctorsByPackType(String groupId, Integer packType, Integer pageIndex, Integer pageSize);

	PageVO getRecommendDoctorByGroupId(String groupId, Integer pageIndex, Integer pageSize);

	/**
	 * 根据病种Id获取医生
	 * @param diseaseId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	PageVO getDoctorsByDiseaseId(String diseaseId, String lat, String lng,  Integer pageIndex, Integer pageSize);

	/**
	 * 根据病种Id集合获取医生
	 * @param diseaseId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	PageVO getDoctorsByDiseaseIds(List<String> diseaseId, String lat, String lng,  Integer pageIndex, Integer pageSize);
	
	/**
	 * 通过当前位置搜索附近医生
	 * 
	 * @param lng
	 * @param lat
	 * @return
	 */
	List<DoctorStatVO> findDoctorByLocation(String lng, String lat);
	
	/**
	 * 通过医院id搜索医生
	 * @param hospitalId
	 * @param lng
	 * @param lat
	 * @return
	 */
	List<DoctorStatVO> findDoctorByHospitalId(String hospitalId, String lng, String lat);
	
	/**
	 * 通过条件（城市+科室）搜索医生
	 * 
	 * @param city
	 * @param deptId
	 * @param lng
	 * @param lat
	 * @return
	 */
	List<DoctorStatVO> findDoctorByCondition(String city, String deptId, String lng, String lat);

	/**
	 *
	 * @param doctorIds
	 * @return
     */
	List<DoctorStatVO> findDoctorByGoodsGroupIds(List<Integer> doctorIds);
}
