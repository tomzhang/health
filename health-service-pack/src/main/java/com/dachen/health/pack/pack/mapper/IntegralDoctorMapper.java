package com.dachen.health.pack.pack.mapper;

import java.util.List;

import com.dachen.health.recommend.entity.param.IntegralDoctorParam;
import com.dachen.health.recommend.entity.vo.IntegralPackVO;

public interface IntegralDoctorMapper {
	List<Integer> getIntegralDoctorList(IntegralDoctorParam param);
	
	List<IntegralPackVO> getIntegralPackByDoctorId(Integer doctorId);
}
