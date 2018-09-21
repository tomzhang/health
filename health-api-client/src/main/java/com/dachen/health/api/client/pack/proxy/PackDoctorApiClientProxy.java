package com.dachen.health.api.client.pack.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.pack.entity.CDoctoreRatioVO;
import com.dachen.health.api.client.pack.entity.CPackDoctor;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class PackDoctorApiClientProxy extends HealthApiClientProxy {

	public List<CPackDoctor> findByPack(Integer packId) throws HttpApiException {
		try {
			String url = "pack/doctor/byPack/{packId}";
			List<CPackDoctor> list = this.openRequestList(url, CPackDoctor.class, packId.toString());
			return list;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public List<Integer> findDoctorIdListByPackId(Integer packId) throws HttpApiException {
		try {
			String url = "pack/doctor/findDoctorIdListByPackId/{packId}";
			List<Integer> list = this.openRequestList(url, Integer.class, packId.toString());
			return list;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public List<CDoctoreRatioVO> getDoctorRatiosByPack(Integer packId, Integer mainDoctorId) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("mainDoctorId", mainDoctorId.toString());
		try {
			String url = "pack/doctor/getDoctorRatiosByPack/{packId}";
			List<CDoctoreRatioVO> list = this.openRequestList(url, params, CDoctoreRatioVO.class, packId.toString());
			return list;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
