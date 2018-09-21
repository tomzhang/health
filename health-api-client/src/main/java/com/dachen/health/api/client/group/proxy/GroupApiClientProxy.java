package com.dachen.health.api.client.group.proxy;

import org.springframework.stereotype.Component;
import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.group.entity.CGroup;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class GroupApiClientProxy extends HealthApiClientProxy {

	/**
	 * 获取集团实体
	 * 
	 * @param id
	 * @return CGroup
	 * @throws HttpApiException 
	 */
	public CGroup findById(String id) throws HttpApiException {
		try {
			String url = "group/findById/{id}";
			CGroup c = this.openRequest(url, CGroup.class, id);
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
