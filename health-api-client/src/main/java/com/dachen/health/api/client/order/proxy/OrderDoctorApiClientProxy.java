package com.dachen.health.api.client.order.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.order.entity.COrderDoctor;
import com.dachen.health.api.client.pack.entity.CDoctoreRatioVO;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class OrderDoctorApiClientProxy extends HealthApiClientProxy {

	public List<COrderDoctor> findByOrder(Integer orderId) throws HttpApiException {
		try {
			String url = "order/doctor/byOrder/{orderId}";
			List<COrderDoctor> list = this.openRequestList(url, COrderDoctor.class, orderId.toString());
			return list;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public List<CDoctoreRatioVO> getDoctorRatiosByOrder(Integer orderId, Integer mainDoctorId) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("mainDoctorId", mainDoctorId.toString());
		try {
			String url = "order/doctor/getDoctorRatiosByOrder/{orderId}";
			List<CDoctoreRatioVO> list = this.openRequestList(url, params, CDoctoreRatioVO.class, orderId.toString());
			return list;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
