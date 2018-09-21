package com.dachen.health.api.client.pack.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.pack.entity.CPack;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class PackApiClientProxy extends HealthApiClientProxy {

	/**
	 * 根据id查找套餐实体
	 * @param id
	 * @return
	 * @throws HttpApiException
	 */
	public CPack findById(Integer id) throws HttpApiException {
		try {
			String url = "pack/{id}";
			CPack cpack = this.openRequest(url, CPack.class, id.toString());
			return cpack;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}

	/**
	 * 检查医生的套餐列表
	 * @param docId 医生id
	 * @param packType 套餐类型
	 * @return
	 * @throws HttpApiException
	 */
	@Deprecated
	public List<CPack> findByDoctorIdAndPackType(Integer docId, Integer packType) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("docId", docId.toString());
		params.put("packType", packType.toString());
		try {
			String url = "pack/findByDoctorIdAndPackType";
			List<CPack> cpacks = this.openRequestList(url, params, CPack.class);
			return cpacks;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 获取医生的关怀计划套餐的carePlanId列表
	 * @param doctorId 医生id
	 * @return
	 * @throws HttpApiException
	 */
	public List<String> findCarePlanIdListByDoctor(Integer doctorId) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("doctorId", doctorId.toString());
		try {
			String url = "pack/findCarePlanIdListByDoctor";
			List<String> ret = this.openRequestList(url, params, String.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 检查医生是否有某计划的套餐
	 * @param doctorUserId 医生id
	 * @param carePlanId
	 * @param carePlanSourceId
	 * @return
	 * @throws HttpApiException
	 */
	public Boolean ifAdded(Integer doctorUserId, String carePlanId, String carePlanSourceId) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(3);
		params.put("doctorUserId", doctorUserId.toString());
		params.put("carePlanId", carePlanId.toString());
		putIfNotBlank(params, "carePlanSourceId", carePlanSourceId);
		try {
			String url = "pack/ifAdded";
			Boolean ret = this.openRequest(url, params, Boolean.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
