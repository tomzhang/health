package com.dachen.health.pack.patient.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.pack.patient.model.Disease;
import com.dachen.health.pack.patient.model.DiseaseParam;

public interface IDiseaseService extends IBaseService<Disease, Integer> {

	
	/**
	 * 
	 * 根据患者id查找
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	public List<Disease> findByPatient(int patientId);
	
	/**
	 * 
	 * 根据创建人查找
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	public List<Disease> findByCreateUser(int createUserId);
	
	
	public Integer addDisease(DiseaseParam param)throws ServiceException;
	
	public void updateDisease(Disease intance,Integer userId)throws ServiceException;
	/**
	 * 更新基础订单信息
	 * @param intance
	 * @param userId
	 * @throws ServiceException
	 */
	public void updateBaseDisease(Disease intance,Integer userId)throws ServiceException;
	
	public void updateDisease(DiseaseParam intance,Integer id)throws ServiceException;
	
	public void updateDiseaseMsg(Disease intance)throws ServiceException;

	public void updateRelationIllCase(Map<String, Object> params);

}
