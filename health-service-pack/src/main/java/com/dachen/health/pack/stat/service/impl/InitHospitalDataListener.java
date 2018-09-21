package com.dachen.health.pack.stat.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.google.common.collect.Maps;

/**
 * Spring 在web容器启动时执行初始化方法
 * 
 * @author 钟良
 * @date 2016-11-21
 *
 */
@Component
public class InitHospitalDataListener implements InitializingBean, ServletContextAware {
	//基础数据Dao
	@Autowired
	private IBaseDataDao baseDataDao;
	
	//缓存所有医院的经纬度
	private static Map<String, String> lngMap = Maps.newHashMap();
	private static Map<String, String> latMap = Maps.newHashMap();

	@Override
	public void setServletContext(ServletContext servletContext) {
		//所有医院列表，因为这里有可能是按照全国+科室去搜索医生。所以这里需要缓存所有医院的地理位置
		if(this.lngMap.isEmpty() && this.latMap.isEmpty()){
			List<HospitalVO> allHospitals = baseDataDao.getHospitalInfos(null, false);

			for (HospitalVO vo : allHospitals) {
				this.lngMap.put(vo.getId(), vo.getLng());
				this.latMap.put(vo.getId(), vo.getLat());
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public static Map<String, String> getLngMap() {
		return lngMap;
	}

	public static void setLngMap(Map<String, String> lngMap) {
		InitHospitalDataListener.lngMap = lngMap;
	}

	public static Map<String, String> getLatMap() {
		return latMap;
	}

	public static void setLatMap(Map<String, String> latMap) {
		InitHospitalDataListener.latMap = latMap;
	}

}
