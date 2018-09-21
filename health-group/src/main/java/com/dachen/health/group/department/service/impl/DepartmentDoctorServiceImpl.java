package com.dachen.health.group.department.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.group.service.IGroupDoctorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.group.department.dao.IDepartmentDao;
import com.dachen.health.group.department.dao.IDepartmentDoctorDao;
import com.dachen.health.group.department.entity.param.DepartmentDoctorParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.po.DepartmentDoctor;
import com.dachen.health.group.department.entity.vo.DepartmentDoctorVO;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.department.service.IDepartmentDoctorService;
import com.dachen.health.group.department.service.IDepartmentService;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
@Service
public class DepartmentDoctorServiceImpl implements IDepartmentDoctorService {

	@Autowired
	protected IDepartmentDoctorDao ddocDao;

    @Autowired
    protected IGroupDoctorService groupDoctorService;
	
	@Autowired
    protected IDepartmentService departmentService;
	
	@Autowired
    protected IBaseUserService baseUserService;
	
	@Autowired
    protected IBusinessServiceMsg businessMsgService;
	
	@Override
	public boolean saveDepartmentDoctor(DepartmentDoctor ddoc) {
		if(StringUtil.isEmpty(ddoc.getDepartmentId())) {
			throw new ServiceException("组织Id为空");
		}
		if(null == ddoc.getDoctorId()) {
			throw new ServiceException("医生Id为空");
		}
		if(StringUtil.isEmpty(ddoc.getGroupId())) {
			throw new ServiceException("集团Id为空");
		}
		GroupDoctor gdoc = new GroupDoctor();
		gdoc.setDoctorId(ddoc.getDoctorId());
		if(null == groupDoctorService.getById(gdoc)) {
			throw new ServiceException("当前医生还未加入集团，请先邀请加入");
		}
//		/** 先删除再保存 */
		ddocDao.deleteByAlsoId(ddoc);
		
		//发送科室变动指令
		businessMsgService.changeDeptGroupNotify(ddoc.getGroupId());
		
		return ddocDao.save(ddoc);
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.service.IDepartmentDoctorService#createDepartmentDoctor(java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public void createDepartmentDoctor(String groupId,String departmentId,Integer doctorId){
		if(StringUtil.isEmpty(departmentId)) {
			throw new ServiceException("组织Id为空");
		}
		if(null == doctorId || doctorId == 0) {
			throw new ServiceException("医生Id为空");
		}
		if(StringUtil.isEmpty(groupId)) {
			throw new ServiceException("集团Id为空");
		}
		DepartmentDoctor departmentDoctor = new DepartmentDoctor();
		departmentDoctor.setDepartmentId(departmentId);
		departmentDoctor.setDoctorId(doctorId);
		departmentDoctor.setGroupId(groupId);
		departmentDoctor.setCreatorDate(System.currentTimeMillis());
		departmentDoctor.setUpdatorDate(System.currentTimeMillis());

		saveDepartmentDoctor(departmentDoctor);
	}

	@Override
	public boolean updateDepartmentDoctor(DepartmentDoctor ddoc) {
		return ddocDao.update(ddoc);
	}

	@Override
	public boolean deleteDepartmentDoctor(String... ids) {
		return ddocDao.delete(ids);
	}

	@Override
	public PageVO searchDepartmentDoctor(DepartmentDoctorParam param) {
		if(StringUtil.isEmpty(param.getDepartmentId())) {
			throw new ServiceException("组织Id为空");
		}
		return ddocDao.search(param);
	}


	/**
     * </p>查找科室下的医生</p>
     * @param departmentId
     * @param type 1：查询当前组织架构，2：查询当前及子组织架构
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    public List<BaseUserVO> getDepartmentDoctor(String groupId,String departmentId,Integer type,String[] status,String consultationPackId){
        if(StringUtil.isBlank(groupId)){
            throw new ServiceException("集团id为空");
        }
        if(status == null || status.length == 0){
            //查询未分配
            List<Integer> doctorIds = ddocDao.getUndistributedDoctorId(groupId);
            return doctorIds.size()>0?baseUserService.getByIds(doctorIds.toArray(new Integer[]{})):null;
        }
        
        //查询正常的
        if(status.length==1 && status[0].equals("C")){
            if(StringUtil.isBlank(departmentId)){
                throw new ServiceException("组织机构为空");
            }
            List<String> departmentIds = new ArrayList<String>();
            
            if(type!=null && type == 2){
                //查询子科室
                departmentIds = departmentService.getSubDepartment(groupId, departmentId);
            }
            departmentIds.add(departmentId);
            
            List<Integer> doctorIds = ddocDao.getDepartmentDoctorId(groupId, departmentIds,status);
            
//            List<Integer> params = getParamsDoctorIds(groupId, consultationPackId, doctorIds);
            
            //针对会诊医生
            if(StringUtils.isBlank(consultationPackId)){
            	return doctorIds.size()>0?baseUserService.getByIds(doctorIds.toArray(new Integer[]{})):null;
            }else{
            	return doctorIds.size()>0?baseUserService.getTopLevelByIds(doctorIds.toArray(new Integer[]{})):null;
            }
        }
        
        //查询其他状态
        List<Integer> doctorIds = groupDoctorService.getDoctorByStatus(groupId, status);
       	return doctorIds.size()>0?baseUserService.getByIds(doctorIds.toArray(new Integer[]{})):null;
    }
    
    /**
     * </p>集团名医推荐查找科室下的医生</p>
     * @param departmentId
     * @param type 1：查询当前组织架构，2：查询当前及子组织架构
     * @return
     * @author 傅永德
     * @date 2015年9月21日
     */
    public List<BaseUserVO> getDepartmentRecommendDoctor(String groupId,String departmentId,Integer type,String[] status,String consultationPackId, List<Integer> recommendDoctorIds){
        if(StringUtil.isBlank(groupId)){
            throw new ServiceException("集团id为空");
        }
        
        if(status == null || status.length == 0){
            //查询未分配
            List<Integer> doctorIds = ddocDao.getUndistributedDoctorId(groupId);
            if (recommendDoctorIds != null && recommendDoctorIds.size() > 0) {
				doctorIds.removeAll(recommendDoctorIds);
			}
            return doctorIds.size()>0?baseUserService.getByIds(doctorIds.toArray(new Integer[]{}), UserEnum.UserStatus.normal.getIndex()):null;
        }
        
        //查询正常的
        if(status.length==1 && status[0].equals("C")){
            if(StringUtil.isBlank(departmentId)){
                throw new ServiceException("组织机构为空");
            }
            List<String> departmentIds = new ArrayList<String>();
            
            if(type!=null && type == 2){
                //查询子科室
                departmentIds = departmentService.getSubDepartment(groupId, departmentId);
            }
            departmentIds.add(departmentId);
            
            List<Integer> doctorIds = ddocDao.getDepartmentDoctorId(groupId, departmentIds,status);
            if (recommendDoctorIds != null && recommendDoctorIds.size() > 0) {
            	doctorIds.removeAll(recommendDoctorIds);
            }            
            List<Integer> params = getParamsDoctorIds(groupId, consultationPackId, doctorIds);
            //针对会诊医生
            if(StringUtils.isBlank(consultationPackId)){
            	return params.size()>0?baseUserService.getByIds(params.toArray(new Integer[]{}), UserEnum.UserStatus.normal.getIndex()):null;
            }else{
            	return params.size()>0?baseUserService.getTopLevelByIds(params.toArray(new Integer[]{})):null;
            }
        }
        
        //查询其他状态
        List<Integer> doctorIds = groupDoctorService.getDoctorByStatus(groupId, status);

        if (recommendDoctorIds != null && recommendDoctorIds.size() > 0) {
			doctorIds.removeAll(recommendDoctorIds);
		}
       	return doctorIds.size()>0?baseUserService.getByIds(doctorIds.toArray(new Integer[]{}), UserEnum.UserStatus.normal.getIndex()):null;
    }

	private List<Integer> getParamsDoctorIds(String groupId, String consultationPackId, List<Integer> doctorIds) {
		if(StringUtils.isBlank(consultationPackId)){
			return doctorIds;
		}
        List<Integer> removeIds = groupDoctorService.getNotInCurrentPackDoctorIds(groupId,consultationPackId);

		Set<Integer> distinctDoctorIds = new HashSet<Integer>(doctorIds);
		distinctDoctorIds.removeAll(removeIds);
		List<Integer> params = new ArrayList<Integer>(distinctDoctorIds);
		return params;
	}
    
    /**
     * </p>根据状态查找集团医生</p>
     * 
     * @param groupId
     * @param status
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    public List<BaseUserVO> getDeptDoctorByStatus(String groupId, String status){
        
        return null;
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Department> getDepartmentByDoctorId(Integer doctorId) {
		if(null == doctorId || !(doctorId instanceof Integer)) {
			throw new ServiceException("医生Id为空");
		}
		DepartmentDoctorParam param = new DepartmentDoctorParam();
		param.setDoctorId(doctorId);
		PageVO page = ddocDao.search(param);
		List<DepartmentDoctorVO> ddocvoList = (List<DepartmentDoctorVO>) page.getPageData();
		List<String> ids = new ArrayList<String>();
		for (DepartmentDoctorVO ddocvo : ddocvoList) {
			ids.add(ddocvo.getDepartmentId());
		}
		return departmentService.findListByIds(ids);
	}

	@Override
	public void saveDoctorIdByDepartmentIds(String[] departIds, Integer doctorId, String groupId) {
		if(null == doctorId) {
			throw new ServiceException("医生Id为空");
		}
		if(null == departIds || 0 == departIds.length) {
			throw new ServiceException("组织Id为空");
		}
		if(StringUtil.isEmpty(groupId)) {
			throw new ServiceException("集团Id为空");
		}
		//多集团改造，改为删除该集团相关的所有部门关联，add by wangqiao   2016-1-18
		ddocDao.deleteByDoctorIdAndGroupId(new Integer[]{doctorId},groupId);
		/* 先全部删除所有的关联再保存 */
//		ddocDao.deleteDoctorIdByDepartmentId(doctorId);
		
		
		
		List<DepartmentDoctor> ddList = new ArrayList<DepartmentDoctor>();
		for (String departmentId : departIds) {
			DepartmentDoctor ddoc = new DepartmentDoctor();
			ddoc.setDoctorId(doctorId);
			ddoc.setGroupId(groupId);
			ddoc.setDepartmentId(departmentId);
			ddoc.setCreator(ReqUtil.instance.getUserId());
			ddoc.setCreatorDate(new Date().getTime());
			ddoc.setUpdator(ReqUtil.instance.getUserId());
			ddoc.setUpdatorDate(new Date().getTime());
			ddList.add(ddoc);
		}
		
		//发送科室变动指令
        businessMsgService.changeDeptGroupNotify(groupId);
		
		ddocDao.save(ddList);
	}

	@Override
	public void saveDepartmentIdByDoctorIds(String departmentId, Integer[] doctorIds, String groupId) {
		if(StringUtil.isEmpty(departmentId)) {
			throw new ServiceException("组织Id为空");
		}
		if(null == doctorIds || 0 == doctorIds.length) {
			throw new ServiceException("医生Id为空");
		}
		if(StringUtil.isEmpty(groupId)) {
			throw new ServiceException("集团Id为空");
		}
		/* 先全部删除所有的关联再保存 */
//		ddocDao.deleteDoctorIdByDepartmentId(doctorIds);
		//多集团改造，改为删除该集团相关的所有部门关联，add by wangqiao   2016-1-18
		ddocDao.deleteByDoctorIdAndGroupId(doctorIds,groupId);
		
		
		List<DepartmentDoctor> ddList = new ArrayList<DepartmentDoctor>();
		for (Integer docId : doctorIds) {
			DepartmentDoctor ddoc = new DepartmentDoctor();
			ddoc.setDepartmentId(departmentId);
			ddoc.setDoctorId(docId);
			ddoc.setGroupId(groupId);
			ddoc.setDepartmentId(departmentId);
			ddoc.setCreator(ReqUtil.instance.getUserId());
			ddoc.setCreatorDate(new Date().getTime());
			ddoc.setUpdator(ReqUtil.instance.getUserId());
			ddoc.setUpdatorDate(new Date().getTime());
			ddList.add(ddoc);
		}
		//发送科室变动指令
        businessMsgService.changeDeptGroupNotify(groupId);
		ddocDao.save(ddList);
	}

	/**
     * </p>通过组织架构查找医生</p>
     * @param departmentId
     * @return
     * @author fanp
     * @date 2015年8月26日
     */
    public List<Integer> getDoctorIdsByDepartment(String departmentId){
        return ddocDao.getDoctorIdsByDepartment(departmentId);
    }
	
    /**
     * </p>查找属于该组织架构的医生</p>
     * @param doctorIds
     * @return
     * @author fanp
     * @date 2015年8月26日
     */
    public Set<Integer> getDoctorIdsByDepartment(List<String> departmentIds,List<Integer> doctorIds){
        return ddocDao.getDoctorIdsByDepartment(departmentIds, doctorIds);
    }
    
    @Override
    public DepartmentDoctor updateDepartment(DepartmentDoctorParam param) {
    	//发送科室变动指令
        businessMsgService.changeDeptGroupNotify(param.getGroupId());
    	return ddocDao.updateDepartment(param);
    }
    
    public List<DepartmentDoctor> getDepartmentDoctorByGroupIdAndDoctorId(String groupId, int doctorId) {
    	return ddocDao.getDepartmentDoctorByGroupIdAndDoctorId(groupId, doctorId);
    }
    
    /* (non-Javadoc)
     * @see com.dachen.health.group.department.service.IDepartmentDoctorService#getDepartmentFullName(java.lang.String, java.util.List)
     */
    @Override
    public Map<Integer, String> getDepartmentFullName(String groupId,List<Integer> doctorIdList){
    	return ddocDao.getDepartmentFullName(groupId,doctorIdList);
    	
    }
    
}
