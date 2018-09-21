package com.dachen.health.recommend.service;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.recommend.entity.vo.IntegralPackVO;
import com.dachen.sdk.exception.HttpApiException;

public interface IIntegralDoctorService {
	PageVO getIntegralDoctorList(Integer pageIndex, Integer pageSize);
	List<IntegralPackVO> getIntegralPackByDoctorId(Integer doctorId) throws HttpApiException;
	boolean checkPatientPoint(Integer packId, Integer userId) throws HttpApiException;
}
