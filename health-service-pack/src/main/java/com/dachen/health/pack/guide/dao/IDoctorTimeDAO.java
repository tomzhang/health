package com.dachen.health.pack.guide.dao;

import com.dachen.health.pack.guide.entity.po.DoctorTimePO;

public interface IDoctorTimeDAO {
	public DoctorTimePO getDoctorTime(Integer doctorId);
	
	public DoctorTimePO getDoctorRemark(Integer doctorId);
	
	public DoctorTimePO removeDoctorTime(Integer doctorId,Long start,Long end);
	
	public DoctorTimePO addDoctorTime(Integer doctorId,Long start,Long end);
	
	public DoctorTimePO addDoctorRemark(Integer doctorId,String remark,String guideId,String guideName);
	
	public int addCount(Integer doctorId,Long start,Long end);
}
