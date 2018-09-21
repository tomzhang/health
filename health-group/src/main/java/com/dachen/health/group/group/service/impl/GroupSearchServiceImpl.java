package com.dachen.health.group.group.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.health.commons.vo.CarePlanDoctorVO;
import com.dachen.health.commons.vo.RecommDiseaseVO;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.document.dao.IDocumentDao;
import com.dachen.health.document.entity.param.DocumentParam;
import com.dachen.health.document.entity.vo.DocumentVO;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.department.dao.IDepartmentDoctorDao;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IGroupSearchDao;
import com.dachen.health.group.group.entity.param.GroupSearchParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.vo.GroupSearchVO;
import com.dachen.health.group.group.service.IGroupSearchService;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

/**
 * 
 * @author xie pei
 * @date 2015/9/28
 */
@Service
public class GroupSearchServiceImpl implements IGroupSearchService{

	@Autowired
    protected IGroupDao groupDao;
	
	@Autowired
    protected DiseaseTypeRepository diseaseTypeRepository;
	
	@Autowired
    protected IGroupDoctorDao gdocDao;
	
	@Autowired
    protected IGroupSearchDao groupSearchDao;
	
	@Autowired
    protected IDepartmentDoctorDao departmentDoctorDao;
	
	@Autowired
    protected IDocumentDao documentDao;
	
	@Resource
    protected IQrCodeService qrcodeService;
	
	@Autowired
    protected IGroupDoctorDao groupDoctorDao;
	
	@Autowired
    protected UserRepository userRepository;
	
    /**
     * </p>获取全部医生集团</p>
     * 
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    public List<GroupSearchVO> findAllGroup(GroupSearchParam param) {
        return groupSearchDao.findAllGroup(param);
    }

    /**
     * </p>搜索医生集团（集团名／医生名／病种 ）</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    public List<GroupSearchVO> findeGroupByKeyWord(GroupSearchParam param) {
        if(StringUtil.isBlank(param.getKeyword())){
            return null;
        }
        param.setDiseaseId(null);
        return groupSearchDao.findGroupFromEs(param);
    }

    /**
     * </p>搜索医生（集团名／医生名／病种 ）</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    public List<GroupSearchVO> findDoctoreByKeyWord(GroupSearchParam param) {
        if(StringUtil.isBlank(param.getKeyword())){
            return null;
        }
        param.setDiseaseId(null);
        return groupSearchDao.findDoctor(param);
    }

    /**
     * </p>根据病种搜索医生集团</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    public List<GroupSearchVO> findGroupByDisease(GroupSearchParam param) {
        if(StringUtil.isBlank(param.getDiseaseId())){
            return null;
        }
        param.setKeyword(null);
        return groupSearchDao.findGroup(param);
    }

	@Override
	public List<GroupSearchVO> findGroupByDiseaseIds(List<String> diseaseIds, int pageIndex, int pageSize) {
		return groupSearchDao.findGroupIds(diseaseIds,pageIndex,pageSize);
	}

	/**
     * </p>根据病种搜索医生</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    public List<GroupSearchVO> findDoctorByDisease(GroupSearchParam param) {
        if(StringUtil.isBlank(param.getDiseaseId())){
            return null;
        }
        param.setKeyword(null);
        return groupSearchDao.findDoctor(param);
    }
    
    
    /**
     * </p>获取集团基本信息</p>
     * 
     * @param param
     * @return
     * @author xiepei
     * @date 2015年9月28日
     */
	public GroupSearchVO findGroupBaseInfo(GroupSearchParam param) {
		
		if(StringUtil.isBlank(param.getDocGroupId())) {
			throw new ServiceException("集团ID不能为空!");
		}
		
		Group group = groupDao.getById(param.getDocGroupId());
		
		if(group==null) {
			throw new ServiceException("集团基本信息不存在!");
		}
		
		GroupSearchVO groupBaseInfo = new GroupSearchVO();
		
		if(group.getConfig() != null){
			groupBaseInfo.setMemberApply(group.getConfig().isMemberApply());
			groupBaseInfo.setMemberInvite(group.getConfig().isMemberInvite());
		}
		
		groupBaseInfo.setGroupId(group.getId());
		groupBaseInfo.setIntroduction(group.getIntroduction());
		groupBaseInfo.setGroupName(group.getName());
		
		//查询该集团所有状态为c的医生，并统计其就诊量
		int cureNum = groupSearchDao.getGroupCureNum(param.getDocGroupId());
		groupBaseInfo.setCureNum(cureNum);
		groupBaseInfo.setCertStatus(group.getCertStatus());
		groupBaseInfo.setCertPath(group.getLogoUrl());
		List<String> listGroupIds = new ArrayList<String>();
		listGroupIds.add(param.getDocGroupId());
		Map<String,Integer> mapResult = groupSearchDao.getGroupExperNum(listGroupIds,1);
		Integer expertNum = mapResult.get(param.getDocGroupId());
		groupBaseInfo.setExpertNum(expertNum == null ? 0 : expertNum);
		
		//添加集团擅长的病种信息  add by wangqiao 2016-01-18
		String  disease = groupSearchDao.getDisease(group.getDiseaselist());
		groupBaseInfo.setDisease(disease);
		if(StringUtil.isNotBlank(group.getDocumentId())){
			DocumentParam dparam = new DocumentParam();
			dparam.setId(group.getDocumentId());
			DocumentVO doc = documentDao.getDocumentDetail(dparam);
			if(doc != null){
				groupBaseInfo.setBannerUrl(doc.getCopyPath());
				groupBaseInfo.setContentUrl(doc.getUrl());
			}
		}
		groupBaseInfo.setGroupPageURL(GroupUtil.GROUP_PAGE_URL() + param.getDocGroupId());
		groupBaseInfo.setGroupQrCodeURL(qrcodeService.generateQr(param.getDocGroupId(), "3"));
		return groupBaseInfo;
	}

	
	/**
     * </p>获取推荐病种</p>
     * 
     * @param param
     * @return
     * @author xiepei
     * @date 2015年9月28日
     */
	public List<RecommDiseaseVO> findRecommDisease(GroupSearchParam param) {
		param.setPageIndex(0);
		param.setPageSize(7);
		return diseaseTypeRepository.findDiseaseType(param);
	}

	
	/**
     * </p>根据集团ID获取专家信息</p>
     * 
     * @param param
     * @return
     * @author xiepei
     * @date 2015年9月28日
     */
	public PageVO findDoctorByGroup(GroupSearchParam param) {
		
		if(StringUtil.isBlank(param.getDocGroupId()) && StringUtil.isBlank(param.getSpecialistId())) {
			throw new ServiceException("科室ID或集团ID 至少需要一个!");
		}
		return departmentDoctorDao.findDocGroupDoctorInfo(param);
		
	}
	
	/**
     * </p>根据集团ID或科室ID获取医生信息</p>
     * 
     * @param param
     * @return
     * @author xiepei
     * @date 2015年9月28日
     */
	public PageVO findProDoctorByGroupId(GroupSearchParam param) {
		
		Group group = groupDao.getById(param.getDocGroupId(), null);
		if(group==null) {
			throw new ServiceException("找不到集团信息!");
		}
		if(group.getExpertIds()!=null) {
			param.setDocIds(group.getExpertIds());
		}
		return gdocDao.findDoctorByGroup(param);
	}
	
	/**
     * </p>根据集团ID获取病情信息</p>
     * 
     * @param param
     * @return
     * @author xiepei
     * @date 2015年9月28日
     */
	public List<RecommDiseaseVO> findRecommDiseaseByGroup(GroupSearchParam param) {
		Group group = groupDao.getById(param.getDocGroupId(), null);
		if(group==null) {
			throw new ServiceException("找不到集团信息!");
		}
		if(group.getDiseaselist()!=null && group.getDiseaselist().size()>0) {
			param.setPageSize(1000);
			List<RecommDiseaseVO> recommDiseases = diseaseTypeRepository.findDiseaseType(param,group.getDiseaselist());
			return recommDiseases;
		}
		return new ArrayList<RecommDiseaseVO>();
	}
	
	
	public PageVO findDoctorOnlineByGroup(GroupSearchParam param) {
		
		if(StringUtils.isBlank(param.getDocGroupId())) {
			throw new ServiceException("医生集团ID不能为空!");
		}
		return departmentDoctorDao.findDocGroupOnlineDoctorInfo(param);
	}
	
    /* (non-Javadoc)
     * @see com.dachen.health.group.group.service.IGroupSearchService#findGroupByName(com.dachen.health.group.group.entity.param.GroupSearchParam)
     */
    public List<GroupSearchVO> findGroupByName(GroupSearchParam param) {
    	//检验登录用户是否 医生   userType=3
    	UserSession userSession = ReqUtil.instance.getUser();
    	if(userSession ==null || userSession.getUserType() != UserEnum.UserType.doctor.getIndex()){
    		throw new ServiceException("请使用医生账号查询可加入的医生圈子");
    	}
    	param.setDoctorId(userSession.getUserId());
    	
        return groupSearchDao.findGroupByName(param);
    }
    
	/* (non-Javadoc)
	 * @see com.dachen.health.group.group.service.IGroupSearchService#findGroupDoctorStatus(com.dachen.health.group.group.entity.param.GroupSearchParam)
	 */
	public GroupSearchVO findGroupDoctorStatus(GroupSearchParam param){
    	//检验登录用户是否 医生   userType=3
    	UserSession userSession = ReqUtil.instance.getUser();
    	if(userSession ==null || userSession.getUserType() != UserEnum.UserType.doctor.getIndex()){
    		throw new ServiceException("请使用医生账号进行查询");
    	}
    	if(param==null || param.getDocGroupId() == null || StringUtils.isEmpty(param.getDocGroupId())){
    		throw new ServiceException("医生集团ID不能为空!");
    	}
    	
    	param.setDoctorId(userSession.getUserId());
    	
    	return groupSearchDao.findGroupDoctorStatus(param);
	}

	@Override
	public void wrapGroupNames(List<CarePlanDoctorVO> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		
		for (CarePlanDoctorVO carePlanDoctorVO : list) {
			Integer mainDoctorId = carePlanDoctorVO.getDoctorId();
			String groupName = groupSearchDao.findDoctorGroupName(mainDoctorId);
			carePlanDoctorVO.setGroupName(groupName);
		}		
	}

}
