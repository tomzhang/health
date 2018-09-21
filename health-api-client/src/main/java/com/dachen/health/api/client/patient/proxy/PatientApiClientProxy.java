package com.dachen.health.api.client.patient.proxy;

import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.patient.entity.CPatient;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class PatientApiClientProxy extends HealthApiClientProxy {

	public CPatient findById(Integer id) throws HttpApiException {
		try {
			String url = "patient/{id}";
			CPatient c = this.openRequest(url, CPatient.class, id.toString());
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
