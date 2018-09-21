package com.dachen.health.group.common.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.service.ICommonService;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.vo.GroupHospitalDoctorVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 查询医生信息的 service实现
 * @author wangqiao 重构
 * @date 2016年4月22日
 */
@Service
@Deprecated
public class CommonServiceImpl extends NoSqlRepository implements ICommonService {

	@Autowired
    protected IBaseUserService baseUserService;
	
	@Autowired
    protected IGroupDao groupDao;

	@Autowired
    protected IGroupDoctorService groupDoctorService;
	
	@Autowired
    protected UserManager userManager;

	@Override
	@Deprecated
	public void verificationUserByDoctorId(Integer doctorId) {
		
		User doc = userManager.getUser(doctorId);

		GroupHospitalDoctorVO groupHospitalDoctorVO= groupDoctorService.getGroupHospitalDoctorByDoctorId(doctorId);
		if(groupHospitalDoctorVO!=null)
			return ;
					
		if(doc.getStatus() != 1) {
			throw new ServiceException("帐号没有审核");
		}
		if (doc.getUserType() == null) {
			throw new ServiceException("帐号类型为空");
		}
		if(doc.getUserType() == 3 || doc.getUserType() == 5) {
			return ;
		}
		throw new ServiceException("帐号类型验证失败");
	}

}
