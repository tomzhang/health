package com.dachen.health.pack.pack.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.entity.po.PackParam;
import com.dachen.health.pack.pack.entity.po.PackParam2;

public interface PackMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Pack record);

    Pack selectByPrimaryKey(Integer id);

    List<Pack> query(Pack pack);

    /**
     * 只查询图文、健康关怀、电话套餐
    **/
    List<Pack> queryForPrice(Pack pack);
    
    int updateByPrimaryKey(Pack record);
    
    List<Pack> queryByIds(PackParam packIds);
    
    // 根据医生Id数组批量查出来
//    List<Pack> queryByUserIds(List<Integer> doctorUserIds); // TODO 参数为List<Integer>不行
    List<Pack> queryByUserIds(PackParam packIds);
    
    /**
     * 查询有预约服务的医生
     * @param doctorUserIds
     * @return
     */
    List<Pack> getPack012Doctors(List<Integer> doctorUserIds);
    
    List<Pack> getFeeUpdatePack(PackParam2 packParam2);
    
    int updatePriceById(Pack record);
    
    Integer countCarePack(Pack record);

	/**
	 * 会诊的套餐修改为一个医生可以有多个套餐了，改该方法搜索时添加limit 1的逻辑，防止程序出错
	 * @param params
	 * @return
	 */
	Pack queryByDoctorIdAndType(Map<String, Integer> params);

	void deleteConsultationPackByDoctorId(Integer doctorId);
	/***begin add  by  liwei  2016年1月22日********/
	
	 List<Pack> selectPackDortorList(Integer packType);
	/***end add  by  liwei  2016年1月22日********/

	List<Integer> getAllBeSearchDoctorIds(ArrayList<Integer> doctorIds);

	void updateConsultationPackPrice(Map<String, Object> params);

	List<Integer> getConsultationDoctorId(List<Integer> friendIds);

	List<Integer> getConsultationDoctorIdNotInIds(Map<String, Object> map);

	/**
	 * 会诊的套餐修改为一个医生可以有多个套餐了，改该方法搜索时添加limit 1的逻辑，防止程序出错
	 * @param params
	 * @return
	 */
	Pack getDoctorPackDBData(Map<String, Integer> params);

	List<Integer> getAllConsultationDoctorIds();

	Pack getPackByGroupIdAndDoctorId(Map<String, Object> sqlParam);

	void updateDoctorAppointmentPack(Map<String, Object> sqlParam);

	List<Integer> getAppointmentDoctorIds(List<Integer> doctorIds);
    
	/**
	 * 获取开启某种套餐的医生
	 * @param packParam
	 * @return
	 */
	List<Integer> getAdviseDoctorIds(PackParam2 packParam);
	
	/**
	 * 获取开启某种套餐的医生(健康关怀用)
	 * @param packParam
	 * @return
	 */
	List<Pack> queryAdvise(PackParam2 packParam);
	
	/**
	 * 获取开启某种非免费套餐的医生
	 * @param packParam
	 * @return
	 */
	List<Integer> getNoFreeAdviseDoctorIds(PackParam2 packParam);
	
	/**
	 * 获取开启某种套餐的医生的数量
	 * @return
	 */
	int getAdviseDoctorCount(PackParam2 packParam);
	
	/**
	 * 获取开启某种套餐的医生的id
	 * @param type
	 * @return
	 */
	List<Integer> getAdviseAdviseOpenedDoctorIds(Integer type);
	
	List<Pack> getAdviseAdviseOpenedPacks(PackParam2 packParam);
	
	/**
	 * 获取开启某种套餐的医生
	 * @param packParam
	 * @return
	 */
	List<Integer> getAdviseDoctorIdsWithGroup(PackParam2 packParam);
	
	/**
	 * 根据医生id获取套餐信息
	 * @param doctorIds
	 * @return
	 */
	List<Pack> getAdviseByDoctorIds(List<Integer> doctorIds);
	
	/**
	 * 根据医生id获取非免费套餐信息
	 * @param doctorIds
	 * @return
	 */
	List<Pack> getNoFreeAdviseByDoctorIds(List<Integer> doctorIds);
	
	/**
	 * 根据医生id获取套餐信息
	 * @param packParam
	 * @return
	 */
	List<Pack> getAdviseByDoctorIdsWithGroup(PackParam2 packParam);
	
	/**
	 * 根据医生id和套餐类型来获取套餐信息
	 * @param packParam
	 * @return
	 */
	List<Pack> getPackByDoctorIdAndPackType(PackParam2 packParam);
	
	/**
	 * 根据医生id和套餐类型来获取非免费套餐信息
	 * @param packParam
	 * @return
	 */
	List<Pack> getNoFreePackByDoctorIdAndPackType(PackParam2 packParam);
	
	/**
	 * 获取套餐开通的医生的id
	 * @return
	 */
	List<Integer> getServerOnDoctorIds();
	
	List<Pack> getServiceOn();
	
	Pack getIntegralPack(Map<String, Object> sqlParam);
	
	int addIntegralPack(Pack pack);
	
	void delPackByConsultationId(String consultationId);
}