package com.dachen.health.group.group.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.group.company.dao.ICompanyDao;
import com.dachen.health.group.company.entity.po.Company;
import com.dachen.health.group.company.service.ICompanyService;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupSearchDao;
import com.dachen.health.group.group.entity.param.GroupCertParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupCertification;
import com.dachen.health.group.group.entity.vo.GroupVO;
import com.dachen.health.group.group.service.IGroupCertService;
import com.dachen.util.PropertiesUtil;

@Service
public class GroupCertServiceImpl implements IGroupCertService {

	@Autowired
    protected IGroupDao groupDao;

	@Autowired
    protected ICompanyService companyService;
	
	@Autowired
    protected IGroupSearchDao groupSearchDao;

	@Override
	public boolean submitCert(String groupId, Integer doctorId, GroupCertification certInfo) {
		if (groupId == null) {
			throw new IllegalArgumentException("invalid argument: groupId is null");
		}
		if (certInfo.getOrgCode() == null) {
			throw new ServiceException("组织机构代码不能为空");
		}
		if (certInfo.getIdImage() != null) {
			certInfo.setIdImage(PropertiesUtil.removeUrlPrefix(certInfo.getIdImage()));
		}
		if (certInfo.getOrgCodeImage() != null) {
			certInfo.setOrgCodeImage(PropertiesUtil.removeUrlPrefix(certInfo.getOrgCodeImage()));
		}
		if (certInfo.getLicenseImage() != null) {
			certInfo.setLicenseImage(PropertiesUtil.removeUrlPrefix(certInfo.getLicenseImage()));
		}
		return groupDao.submitCert(groupId, doctorId, certInfo);
	}
	
	@Override
	public GroupVO getGroupCert(String groupId) {
		if (groupId == null) {
			throw new IllegalArgumentException("invalid argument: groupId is null");
		}
		Group group = groupDao.getById(groupId);
		
		return toVO(group);
	}
	
	@Override
	public PageVO getGroupCerts(GroupCertParam param) {
		if (param.getStatus() == null) {
			throw new IllegalArgumentException("invalid argument: status is null");
		}
		
		Query<Group> q = groupDao.getGroupCerts(param);
		List<Group> gs = q.offset(param.getStart()).limit(param.getPageSize()).asList();
		
		List<GroupVO> list = new ArrayList<GroupVO>();
		for (Group g : gs) {
			GroupVO gvo = toVO(g);
			gvo.setOtherGroupCount(groupDao.retrievedGroups(g.getId(), false).countAll());
			list.add(gvo);
		}
		
    	return new PageVO(list, q.countAll(), param.getPageIndex(), param.getPageSize());
	}
	
	@Override
	public PageVO getOtherGroupCerts(GroupCertParam param) {
		if (param.getGroupId() == null) {
			throw new IllegalArgumentException("invalid argument: groupId is null");
		}
		
		Query<Group> q = groupDao.retrievedGroups(param.getGroupId(), false);
		List<Group> gs = q.offset(param.getStart()).limit(param.getPageSize()).asList();
		
		List<GroupVO> list = new ArrayList<GroupVO>();
		for (Group g : gs) {
			list.add(toVO(g));
		}
		return new PageVO(list, q.countAll(), param.getPageIndex(), param.getPageSize());
	}
	
	/**
	 * Group 转换 GroupVO
	 * @param g
	 * @return
	 */
	private GroupVO toVO(Group g) {
		GroupVO gvo = new GroupVO();
		gvo.setId(g.getId());
		gvo.setName(g.getName());
		gvo.setIntroduction(g.getIntroduction());
		gvo.setDiseaseName(groupSearchDao.getDisease(g.getDiseaselist()));
		gvo.setGroupIconPath(g.getLogoUrl());//(UserHelper.buildCertPath(g.getId(), g.getCreator()));
		gvo.setCertStatus(g.getCertStatus());
		if (g.getGroupCert() != null) {
			GroupCertification certInfo = g.getGroupCert();
			if (certInfo.getIdImage() != null) {
				certInfo.setIdImage(PropertiesUtil.addUrlPrefix(certInfo.getIdImage()));
			}
			if (certInfo.getOrgCodeImage() != null) {
				certInfo.setOrgCodeImage(PropertiesUtil.addUrlPrefix(certInfo.getOrgCodeImage()));
			}
			if (certInfo.getLicenseImage() != null) {
				certInfo.setLicenseImage(PropertiesUtil.addUrlPrefix(certInfo.getLicenseImage()));
			}
			gvo.setGroupCert(certInfo);
		}
		if(g.getConfig() != null){
			gvo.setOpenConsultation(g.getConfig().getOpenConsultation());
		}
		gvo.setProcessVTime(g.getProcessVTime());
		return gvo;
	}
	
	
	public boolean updateRemarks(String groupId, String remarks) {
		return groupDao.updateRemarks(groupId, remarks);
	}

	@Override
	public boolean passCert(String groupId) {
		if (groupId == null) {
			throw new IllegalArgumentException("invalid argument: groupId is null");
		}
		
		Group group = groupDao.getById(groupId);
		GroupCertification groupCert = group.getGroupCert();
		if (groupCert == null || groupCert.getOrgCode() == null) {
			throw new ServiceException("organizing institution bar code is empty");
		}
		
		Company company = companyService.getCompanyByOrgCode(groupCert.getOrgCode());
		if (company == null) {
//			company = new Company();
//			company.setName(groupCert.getCompanyName());
//			company.setDescription(groupCert.getBusinessScope());
//			company.setCorporation(groupCert.getCorporation());
//			company.setOrgCode(groupCert.getOrgCode());
//			company.setLicense(groupCert.getLicense());
//			company.setBankAccount(groupCert.getAccountName());
//			company.setBankNumber(groupCert.getBankAcct());
//			company.setOpenBank(groupCert.getOpenBank());
//			company.setStatus("P");
//			company.setCreator(group.getCreator());
//			company.setCreatorDate(System.currentTimeMillis());
//			company.setUpdator(group.getCreator());
//			company.setUpdatorDate(System.currentTimeMillis());
//			company = companyDao.save(company);
			company = companyService.addCompany(groupCert,group.getCreator());
		}
		
		return groupDao.passCert(groupId, company.getId());
	}
	
	@Override
	public boolean noPass(String groupId) {
		if (groupId == null) {
			throw new IllegalArgumentException("invalid argument: groupId is null");
		}
		
		return groupDao.noPass(groupId);
	}
	
	
	@Override
	public List<Group> getGroups(String companyId) {
		if (companyId == null) {
			throw new IllegalArgumentException("invalid argument: companyId is null");
		}
		return groupDao.getAllGroup(companyId);
	}
}
