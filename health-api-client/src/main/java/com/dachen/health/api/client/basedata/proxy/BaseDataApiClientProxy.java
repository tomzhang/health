package com.dachen.health.api.client.basedata.proxy;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.basedata.entity.CCheckup;
import com.dachen.health.api.client.basedata.entity.CDiseaseType;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.util.ExtTreeNode;

@Component
public class BaseDataApiClientProxy extends HealthApiClientProxy {

	/**
	 * 获取病种实体
	 * 
	 * @param id
	 *            病种id
	 * @return 病程实体
	 * @throws HttpApiException
	 */
	public CDiseaseType getDiseaseType(String id) throws HttpApiException {
		try {
			String url = "basedata/diseaseType/{id}";
			CDiseaseType c = this.openRequest(url, CDiseaseType.class, id.toString());
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}

	public List<CDiseaseType> getDiseaseTypes(Collection<String> ids) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		if (null == ids || 0 == ids.size()) {
			throw new HttpApiException("缺少参数");
		}
		putArrayIfNotBlank(params, "ids", ids);
		try {
			String url = "basedata/diseaseTypes";
			List<CDiseaseType> list = this.openRequestList(url, params, CDiseaseType.class);
			return list;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	public List<CCheckup> getCheckups(Collection<String> ids) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		if (null == ids || 0 == ids.size()) {
			throw new HttpApiException("缺少参数");
		}
		putArrayIfNotBlank(params, "ids", ids);
		try {
			String url = "basedata/checkups";
			List<CCheckup> list = this.openRequestList(url, params, CCheckup.class);
			return list;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 获取一级病种树
	 * 
	 * @param ids
	 * @return
	 */
	public List<ExtTreeNode> getLevel1DiseaseTypeTree(Collection<String> ids) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		if (null == ids || 0 == ids.size()) {
			throw new HttpApiException("缺少ids参数");
		}
		putArrayIfNotBlank(params, "ids", ids);
		try {
			String url = "basedata/getLevel1DiseaseTypeTree";
			List<ExtTreeNode> ret = this.openRequestList(url, params, ExtTreeNode.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}

	/**
	 * 通过病种ids获取以，号分隔病种name的字符串文本
	 * @param ids
	 * @return
	 * @throws HttpApiException
	 */
	public String findDiseaseOnIds(Collection<String> ids) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		if (null == ids || 0 == ids.size()) {
			throw new HttpApiException("缺少参数");
		}
		putArrayIfNotBlank(params, "diseaseIds", ids);
		try {
			String url = "basedata/findDiseaseOnIds";
			String names = this.openRequest(url, params, String.class);
			return names;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 格式化文案的内容
	 * 
	 * @param id 模板id
	 * @param args 模板参数值
	 * @return String 文本内容
	 * @throws HttpApiException
	 */
	public String toContent(String id, Object... args) throws HttpApiException {
		Map<String, String> params = null;
		if (null != args && args.length>0) {
			params = new HashMap<String, String>(1);
			this.putJsonStrIfNotBlank(params, "argsJson", args);
		}
		try {
			String url = "basedata/toContent/{id}";
			String msg = this.postRequest(url, params, String.class, id.toString());
			return msg;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 获取有数据的集团病种树
	 * 
	 * @param groupId 集团id
	 * @param tmpType 关怀计划的tmpType
	 * @return
	 * @throws HttpApiException
	 */
	public List<ExtTreeNode> getDiseaseTypeTree4CarePlan(String groupId, Integer tmpType) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(2);
		this.putIfNotBlank(params, "groupId", groupId);
		this.putIfNotBlank(params, "tmpType", tmpType);
		try {
			String url = "basedata/getDiseaseTypeTree4CarePlan";
			List<ExtTreeNode> ret = this.openRequestList(url, params, ExtTreeNode.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 根据病种ids获取病种树
	 * 
	 * @param ids
	 * @param onlyShowParentNode
	 * @return
	 * @throws HttpApiException
	 */
	public List<ExtTreeNode> getDiseaseTypeTree(List<String> ids, Boolean onlyShowParentNode) throws HttpApiException {
		if (null == ids ||
				0 == ids.size()) {
			return null;
		}
		Map<String, String> params = new HashMap<String, String>(2);
		this.putArrayIfNotBlank(params, "ids", ids);
		this.putIfNotBlank(params, "onlyShowParentNode", onlyShowParentNode);
		try {
			String url = "basedata/getDiseaseTypeTree";
			List<ExtTreeNode> ret = this.openRequestList(url, params, ExtTreeNode.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
