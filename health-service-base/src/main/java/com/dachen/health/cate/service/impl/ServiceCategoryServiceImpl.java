package com.dachen.health.cate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.cate.dao.IServiceCategoryDao;
import com.dachen.health.cate.entity.param.ServiceCategoryParam;
import com.dachen.health.cate.entity.po.ServiceCategory;
import com.dachen.health.cate.entity.vo.ServiceCategoryVO;
import com.dachen.health.cate.service.IServiceCategoryService;
import com.dachen.util.BeanUtil;
import com.dachen.util.StringUtil;

@Service
public class ServiceCategoryServiceImpl implements IServiceCategoryService{
	
	@Autowired
	private IServiceCategoryDao serviceCategoryDao;

	@Override
	public ServiceCategoryVO getServiceCategoryById(String id) {
		if(StringUtil.isEmpty(id)){
			return null;
		}
		ServiceCategoryParam param = new ServiceCategoryParam();
		param.setId(id);
		ServiceCategory sc = serviceCategoryDao.getServiceCategory(param);
		return sc==null? null:BeanUtil.copy(sc, ServiceCategoryVO.class);
	}

	@Override
	public ServiceCategoryVO getServiceCategoryByGroupId(String groupId) {
		if(StringUtil.isEmpty(groupId)){
			return null;
		}
		ServiceCategoryParam param = new ServiceCategoryParam();
		param.setGroupId(groupId);
		ServiceCategory sc = serviceCategoryDao.getServiceCategory(param);
		return sc==null? null:BeanUtil.copy(sc, ServiceCategoryVO.class);
	}
}
