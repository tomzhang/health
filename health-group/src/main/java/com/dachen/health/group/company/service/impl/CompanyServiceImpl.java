package com.dachen.health.group.company.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.GroupEnum.CompanyStatus;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.company.dao.ICompanyDao;
import com.dachen.health.group.company.entity.param.CompanyParam;
import com.dachen.health.group.company.entity.po.Company;
import com.dachen.health.group.company.entity.po.InviteCode;
import com.dachen.health.group.company.entity.vo.CompanyVO;
import com.dachen.health.group.company.service.ICompanyService;
import com.dachen.health.group.company.service.InviteCodeService;
import com.dachen.health.group.department.dao.IDepartmentDao;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupCertification;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

/**
 * 公司相关业务的接口实现
 * @author wangqiao 重构
 * @date 2016年4月26日
 */
@Service(CompanyServiceImpl.BEAN_ID)
public class CompanyServiceImpl implements ICompanyService {

	public static final String BEAN_ID = "CompanyServiceImpl";
	
	@Autowired
	protected ICompanyDao companyDao;
	
	@Autowired
    protected UserManager userManager;

	@Autowired
    protected IGroupDao groupDao;
	
	@Autowired
    protected IDepartmentDao deparDao;
	
	@Autowired
    protected InviteCodeService inviteCodeService;
	
	@Deprecated
	public Company saveCompany(Company company) {
		if(StringUtil.isEmpty(company.getLicense())) {
			throw new ServiceException("营业执照编号不能为空！");
		}
		if(StringUtil.isEmpty(company.getName())) {
			throw new ServiceException("公司名称不能为空！");
		}
//		DoctorBasicInfo doctor = userService.getDoctorBasicInfoById(ReqUtil.instance.getUserId());
		User doctor = userManager.getUser(ReqUtil.instance.getUserId());
		if(doctor.getUserType() != 5) {
			throw new ServiceException("用户类型错误，请用userType为5的用户登录");
		}
		company.setStatus("A");/**  A：审核中，P：审核通过，S：已停用 **/
		CompanyParam param = new CompanyParam();
		param.setCreator(company.getCreator());
		List<?> list = this.searchCompany(param).getPageData();
		if(null != list && 0 != list.size()) {
			CompanyVO com = (CompanyVO) list.get(0);
			if("A".equals(com.getStatus())) {
				throw new ServiceException("您创建的公司已在审核中！");
			} else {
				throw new ServiceException("您已创建公司帐号！");
			}
		}
		
		Company com = companyDao.add(company);
		
		//添加管理员
//		companyUserService.addCompanyManage(com.getId(), doctorId, operationUserId);
//		GroupUser cuser = new GroupUser();
//		cuser.setType(1);
//		cuser.setObjectId(com.getId());
//		cuser.setStatus("C");
//		cuser.setDoctorId(ReqUtil.instance.getUserId());
//		cuser.setCreator(ReqUtil.instance.getUserId());
//		cuser.setCreatorDate(new Date().getTime());
//		cuser.setUpdator(ReqUtil.instance.getUserId());
//		cuser.setUpdatorDate(new Date().getTime());
//		cuserDao.save(cuser);/** 添加到管理员列表 **/
		return com;
	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.service.ICompanyService#updateCompany(com.dachen.health.group.company.entity.po.Company)
	 */
	@Override
	public Company updateCompany(Company company) {
		//参数校验
		if(StringUtil.isEmpty(company.getId())) {
			throw new ServiceException("Id不能为空！");
		}
		return companyDao.update(company);
	}

	@Deprecated
	public boolean deleteCompany(Integer... ids) {
		if(null == ids) {
			throw new ServiceException("Id不能为空！");
		}
		return companyDao.delete(ids);
	}

	@Deprecated //业务有问题，暂时废弃
	public PageVO searchCompany(CompanyParam company) {
		return companyDao.search(company);
	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.service.ICompanyService#getCompanyById(java.lang.String)
	 */
	@Override
	public Company getCompanyById(String id) {
		if(StringUtil.isEmpty(id)) {
			throw new ServiceException("公司Id为空");
		}
		return companyDao.getById(id);
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.service.ICompanyService#getCompanyByOrgCode(java.lang.String)
	 */
	@Override
	public Company getCompanyByOrgCode(String orgCode){
		//参数校验
		if(StringUtils.isEmpty(orgCode)){
			throw new ServiceException("参数orgCode不能为空");
		}
		return companyDao.getByOrgCode(orgCode);
		
	}

	@Override
	@Deprecated //暂时废弃
	public Map<String, Object> addGroupByInviteCode(String code) {
		InviteCode invite = inviteCodeService.updateInviteCode(code);
		
		Group group = groupDao.getById(null, invite.getDoctorId());
		if(null == group) {
			throw new ServiceException("当前用户没有创建圈子");
		}
		group.setCompanyId(invite.getCompanyId());
		group.setUpdator(ReqUtil.instance.getUserId());
		group.setUpdatorDate(new Date().getTime());
		/* 更新集团信息 */
		groupDao.update(group);
		Company company = companyDao.getById(invite.getCompanyId());
		List<DepartmentVO> departmentList = deparDao.findListById(group.getId(),"0");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("company", company);
		data.put("group", group);
		data.put("departmentList", departmentList);
		return data;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.service.ICompanyService#addCompany(com.dachen.health.group.group.entity.po.GroupCertification, java.lang.Integer)
	 */
	@Override
	public 	Company addCompany(GroupCertification groupCert,Integer operationUserId){
		//参数校验
		if(groupCert == null){
			throw new ServiceException("参数不能为空");
		}
		//构造 company实体
		Company company = new Company();
		company.setName(groupCert.getCompanyName());
		company.setDescription(groupCert.getBusinessScope());
		company.setCorporation(groupCert.getCorporation());
		company.setOrgCode(groupCert.getOrgCode());
		company.setLicense(groupCert.getLicense());
		company.setBankAccount(groupCert.getAccountName());
		company.setBankNumber(groupCert.getBankAcct());
		company.setOpenBank(groupCert.getOpenBank());
		//状态为审核通过
		company.setStatus(CompanyStatus.PASSED.getIndex());
		
		company.setCreator(operationUserId);
		company.setCreatorDate(System.currentTimeMillis());
		company.setUpdator(operationUserId);
		company.setUpdatorDate(System.currentTimeMillis());
		//持久化
		company = companyDao.add(company);
		return company;
	}

}
