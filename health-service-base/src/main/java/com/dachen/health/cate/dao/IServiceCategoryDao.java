package com.dachen.health.cate.dao;

import com.dachen.health.cate.entity.param.ServiceCategoryParam;
import com.dachen.health.cate.entity.po.ServiceCategory;

public interface IServiceCategoryDao {
	/**
	 * 根据id或者groupId获取ServiceCategory
	 * @param param
	 * @return
	 */
	ServiceCategory getServiceCategory(ServiceCategoryParam param);
}
