package com.dachen.health.pack.patient.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachen.health.pack.patient.service.IBaseService;
/**
 * 
 * ProjectName： health-service-pack<br>
 * ClassName： BaseServiceImpl<br>
 * Description： <br>
 * @author 李淼淼
 * @createTime 2015年8月12日
 * @version 1.0.0
 */
public abstract class BaseServiceImpl<T, PK> implements IBaseService<T, PK> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public BaseServiceImpl() {
		logger.info("init");
	}
}
