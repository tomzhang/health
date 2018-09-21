package com.dachen.health.api.client.document.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.document.entity.CDocument;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class DocumentApiClientProxy extends HealthApiClientProxy {

	/**
	 * 获取健康科普实体信息
	 * @param id
	 * @return CDocument
	 * @throws HttpApiException 
	 */
	public CDocument findById(String id) throws HttpApiException {
		try {
			String url = "document/{id}";
			CDocument c = this.openRequest(url, CDocument.class, id);
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 获取健康科普实体信息列表
	 * @param ids
	 * @return
	 * @throws HttpApiException 
	 */
	public List<CDocument> findByIds(List<String> ids) throws HttpApiException {
		if (null == ids || 0 == ids.size()) {
			return null;
		}
		Map<String, String> params = new HashMap<String, String>(1);
		this.putArrayIfNotBlank(params, "ids", ids);
		try {
			String url = "document/findByIds";
			List<CDocument> c = this.openRequestList(url, params, CDocument.class);
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
