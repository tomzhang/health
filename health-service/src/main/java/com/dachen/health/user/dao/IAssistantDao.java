package com.dachen.health.user.dao;

import java.util.List;

import com.dachen.health.user.entity.po.DrugVerifyRecord;
import com.dachen.health.user.entity.vo.DoctorDetailVO;

public interface IAssistantDao {
	void addDrugVerifyRecord(DrugVerifyRecord verifyRecord);
	 
	/**
     * </p>获取医助存在医生的分管医院</p>
     * 
     * @param doctor
     * @return hospital:医院名称，hospitalId:医院编码
     * @author fanp
     * @date 2015年7月8日
     */
	List<DoctorDetailVO> getHospitals(Integer userId);
	
	/**
     * </p>获取医助分管医院的医生</p>
     * 
     * @param doctor
     * @return
     * @author fanp
     * @date 2015年7月8日
     */
	List<DoctorDetailVO> getHospitalDoctors(Integer userId,String hospitalId);
}
