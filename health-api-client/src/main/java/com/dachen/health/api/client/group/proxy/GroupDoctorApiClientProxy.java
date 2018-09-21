package com.dachen.health.api.client.group.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.group.entity.CGroup;
import com.dachen.health.api.client.group.entity.CGroupDoctor;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class GroupDoctorApiClientProxy extends HealthApiClientProxy {

	/**
	 * 获取集团医生实体
	 * @param groupId 集团id
	 * @param doctorId 医生id
	 * @return
	 * @throws HttpApiException 
	 */
	public CGroupDoctor findByUK(String groupId, Integer doctorId) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("groupId", groupId.toString());
		params.put("doctorId", doctorId.toString());
		try {
			String url = "group/doctor/findByUK";
			CGroupDoctor c = this.openRequest(url, params, CGroupDoctor.class);
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 判断医生是否在集团中
	 * @param groupId 集团id
	 * @param doctorId 医生id
	 * @return
	 * @throws HttpApiException 
	 */
	public Boolean exists(String groupId, Integer doctorId) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("groupId", groupId.toString());
		params.put("doctorId", doctorId.toString());
		try {
			String url = "group/doctor/exists";
			Boolean c = this.openRequest(url, params, Boolean.class);
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 获取集团名称
	 * @param doctorId
	 * @return
	 * @throws HttpApiException
	 */
	public String findDoctorGroupName(Integer doctorId) throws HttpApiException {
		try {
			String url = "group/doctor/findDoctorGroupName/{doctorId}";
			String name = this.openRequest(url, String.class, doctorId.toString());
			return name;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 获取医生所在的活跃集团的ids
	 * @param doctorId
	 * @return
	 * @throws HttpApiException
	 */
	public List<String> getActiveGroupIdListByDoctor(Integer doctorId) throws HttpApiException {
		try {
			String url = "group/doctor/getActiveGroupIdListByDoctor/{doctorId}";
			List<String> list = this.openRequestList(url, String.class, doctorId.toString());
			return list;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 获取医生所在的活跃集团的列表
	 * @param doctorId
	 * @return
	 * @throws HttpApiException
	 */
	public List<CGroup> getActiveGroupListByDoctor(Integer doctorId) throws HttpApiException {
		try {
			String url = "group/doctor/getActiveGroupListByDoctor/{doctorId}";
			List<CGroup> list = this.openRequestList(url, CGroup.class, doctorId.toString());
			return list;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
