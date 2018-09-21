package com.dachen.health.api.client.user.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.user.entity.CCarePlanDoctorVO;
import com.dachen.health.api.client.user.entity.CUser;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class UserApiClientProxy extends HealthApiClientProxy {

	/**
	 * 
	 * @param id
	 * @return
	 * @throws HttpApiException
	 */
	public CUser findById(Integer id) throws HttpApiException {
		try {
			String url = "user/{id}";
			CUser cuser = this.openRequest(url, CUser.class, id.toString());
			return cuser;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 * @param ids
	 * @return
	 * @throws HttpApiException
	 */
	public List<CUser> findByIds(List<Integer> ids) throws HttpApiException {
		if (null == ids || 0 == ids.size()) {
			throw new HttpApiException("参数有误");
		}
		Map<String, String> params = new HashMap<String, String>(1);
		putArrayIfNotBlank(params, "ids", ids);
		try {
			String url = "user/findByIds";
			List<CUser> c = this.openRequestList(url, params, CUser.class);
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param doctorIds
	 * @param status
	 * @return
	 * @throws HttpApiException
	 */
	public List<CCarePlanDoctorVO> findUserByDocs(List<Integer> doctorIds, Integer status) throws HttpApiException {
		if (null == doctorIds || 0 == doctorIds.size()) {
			throw new HttpApiException("参数有误");
		}
		Map<String, String> params = new HashMap<String, String>(2);
		putArrayIfNotBlank(params, "doctorIds", doctorIds);
		putIfNotBlank(params, "status", status);
		try {
			String url = "user/findUserByDocs";
			List<CCarePlanDoctorVO> list = this.openRequestList(url, params, CCarePlanDoctorVO.class);
			return list;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
