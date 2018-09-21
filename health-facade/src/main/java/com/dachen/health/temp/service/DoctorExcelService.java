package com.dachen.health.temp.service;

import java.util.List;
import java.util.Map;

import com.dachen.health.group.group.entity.vo.DoctorExcelVo;
import com.dachen.health.group.group.entity.vo.OrderExcelVo;
import com.dachen.health.group.group.entity.vo.PatientExcelVo;
import com.dachen.health.user.entity.po.Doctor;

public interface DoctorExcelService {
	
	List<DoctorExcelVo> getDoctorStatVos(Integer status, Long start, Long end);
	
	/**
	 * 设置医生是否含有某类的套餐
	 * @param doctorExcelVos
	 * @param adviseType 1：图文咨询；2：电话咨询；3：健康关怀
	 */
	void setHasAdvise(List<DoctorExcelVo> doctorExcelVos, int adviseType);
	
	List<PatientExcelVo> getPatientExcelVo(Integer status, Long start, Long end);
	
	/**
	 * 获取订单的信息
	 * @param packType
	 * @param start
	 * @param end
	 * @return
	 */
	List<OrderExcelVo> getByPackTypeAndTime(Integer packType, Long start, Long end);
	
	/**
	 * 设置未激活患者的邀请人的信息
	 * @param patientExcelVos
	 */
	void setInviterInfo(List<PatientExcelVo> patientExcelVos);
	
	/**
	 * 获取开启某种类型套餐的医生的总数
	 * @param type
	 * @return
	 */
	int getAdviseDoctorCount(Integer type);

	/**
	 * 获取开启某种类型套餐的医生
	 * @param index
	 * @return
	 */
	List<DoctorExcelVo> getAdviseDoctor(Integer type);
	
	/**
	 * 组装关怀计划最后一次答题时间
	 * @param doctorExcelVos
	 * @return
	 */
	List<OrderExcelVo> setCareLastAnswerTime(List<OrderExcelVo> orderExcelVos);
	
	/**
	 * 获取标题
	 * @param orderExcelVos
	 * @return
	 */
	Map<String, List<String>> getLongTitle(List<OrderExcelVo> orderExcelVos);
}
