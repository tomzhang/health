package com.dachen.health.group.group.dao;

import java.util.List;

import com.dachen.health.group.group.entity.vo.DoctorExcelVo;
import com.dachen.health.group.group.entity.vo.GroupDoctorReVo;
import com.dachen.health.group.group.entity.vo.GroupExcelVo;
import com.dachen.health.group.group.entity.vo.OrderExcelVo;
import com.dachen.health.group.group.entity.vo.PatientExcelVo;
import com.dachen.health.group.group.entity.vo.UserExcelVo;

public interface DoctroExcelDao {
	
	List<DoctorExcelVo> getDoctorExcel(Integer status);
	
	List<DoctorExcelVo> getDoctorExcel(Integer status, Long start, Long end);
	
	List<DoctorExcelVo> getDoctorExcelByIds(List<Integer> ids);
	
	List<GroupDoctorReVo> getGroupDoctorReVo(List<Integer> doctorIds);
	
	List<GroupDoctorReVo> setGroupName(List<GroupDoctorReVo> groupDoctorReVos);
	
	List<GroupDoctorReVo> setInviterName(List<GroupDoctorReVo> groupDoctorReVos);
	
	List<PatientExcelVo> getPatientExcelVo(Integer status, Long start, Long end);
	
	List<UserExcelVo> getByIds(List<Integer> ids);
	
	List<GroupExcelVo> getGroupByIds(List<String> ids);
	
	List<OrderExcelVo> setCareItemIdAndAnswerTimes(List<OrderExcelVo> orderExcelVos);
	
	List<OrderExcelVo> setItemAnswerInfo(List<OrderExcelVo> orderExcelVos);

}
