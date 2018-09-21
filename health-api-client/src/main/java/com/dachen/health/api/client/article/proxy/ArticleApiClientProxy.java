package com.dachen.health.api.client.article.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.article.entity.CMedicalKnowledge;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class ArticleApiClientProxy extends HealthApiClientProxy {

	/**
	 * 查找就医知识
	 * 
	 * @param id 就医知识文档id
	 * @return CMedicalKnowledge 就医知识实体
	 * 
	 * @throws HttpApiException
	 */
	public CMedicalKnowledge findById(String id) throws HttpApiException {
		try {
			String url = "article/findById/{id}";
			CMedicalKnowledge c = this.openRequest(url, CMedicalKnowledge.class, id.toString());
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 查找就医知识列表
	 * 
	 * @param ids 就医知识文档ids
	 * @return
	 * @throws HttpApiException
	 */
	public List<CMedicalKnowledge> findByIds(List<String> ids) throws HttpApiException {
		if (null == ids || 0 == ids.size()) {
			return null;
		}
		
		Map<String, String> params = new HashMap<String, String>(1);
		putArrayIfNotBlank(params, "ids", ids);
		try {
			String url = "article/findByIds";
			List<CMedicalKnowledge> c = this.openRequestList(url, params, CMedicalKnowledge.class);
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
