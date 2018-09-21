package com.dachen.health.cate.service;

import com.dachen.health.cate.entity.vo.ServiceCategoryVO;

public interface IServiceCategoryService {
	
	ServiceCategoryVO getServiceCategoryById(String id);
	
	ServiceCategoryVO getServiceCategoryByGroupId(String groupId);

}
