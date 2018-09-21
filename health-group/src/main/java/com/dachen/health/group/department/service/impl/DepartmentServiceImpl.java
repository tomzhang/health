package com.dachen.health.group.department.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.group.department.dao.IDepartmentDao;
import com.dachen.health.group.department.entity.param.DepartmentParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.vo.DepartmentDoctorVO;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.department.service.IDepartmentService;
import com.dachen.util.StringUtil;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
@Service
public class DepartmentServiceImpl implements IDepartmentService {

	@Autowired
	protected IDepartmentDao deparDao;

	@Override
	public boolean addDepartment(Department department) {
		if(StringUtil.isEmpty(department.getName())) {
			throw new ServiceException("名称为空");
		}
		if(StringUtil.isEmpty(department.getParentId())) {
			department.setParentId("0");
		}
		if(StringUtil.isEmpty(department.getGroupId())) {
			throw new ServiceException("集团Id为空");
		}
		return deparDao.add(department);
	}

//	@Override
//	public boolean updateDepartment(Department department) {
//		if(StringUtil.isEmpty(department.getId())) {
//			throw new ServiceException("id为空");
//		}
//		return deparDao.update(department);
//	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.service.IDepartmentService#updateDepartmentById(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	public void updateDepartmentById(String id,String name,String description,String parentId,Integer operationUserId){
		//参数校验
		if(StringUtils.isEmpty(id)){
			throw new ServiceException("参数id不能为空");
		}
		//持久化
		deparDao.updateById(id, name, description, parentId, operationUserId);
	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.service.IDepartmentService#deleteDepartment(java.lang.String[])
	 */
	@Override
	public void deleteDepartment(String[] ids) {
		List<DepartmentDoctorVO> ddList = this.includeDoctorByIds(ids);
		if(null != ddList && 0 != ddList.size()) {
			throw new ServiceException("请先删除组织架构下的医生");
		}
		List<Department> deparList = this.includeSubjectByIds(ids);
		if(null != deparList && 0 != deparList.size()) {
			throw new ServiceException("请先删除子组织架构");
		}
		deparDao.delete(ids);
	}

//	@Override
//	public List<DepartmentVO> searchDepartment(DepartmentParam department) {
//		return deparDao.search(department);
//	}

	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.service.IDepartmentService#searchDepartment(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<DepartmentVO> searchDepartment(String groupId,String parentId,String name,String description){
		//参数校验,都允许为空
		
		return deparDao.searchDepartment(groupId, parentId, name, description);
		
	}
	
	
	/**
     * </p>根据id查找</p>
     * @param id
     * @return
     * @author fanp
     * @date 2015年8月14日
     */
//    public DepartmentVO getOne(ObjectId id){
//        return deparDao.getOne(id);
//    }
    
    /**
     * 查询当前组织是否有子组织
     * @param ids
     * @return
     */
    private List<Department> includeSubjectByIds(String[] ids) {
    	return deparDao.findSubjectByIds(ids);
    }
    
    /***
     * 查询当前组织Id是否有医生
     */
    private List<DepartmentDoctorVO> includeDoctorByIds(String[] ids) {
    	return deparDao.findDoctorByIds(ids);
    }

	@Override
	public List<DepartmentVO> findAllListById(String groupId) {
		List<String> idList = new ArrayList<String>();
		idList.add(groupId);
		if(StringUtil.isEmpty(groupId)) {
			throw new ServiceException("集团Id为空");
		}
		return deparDao.findListById(groupId,"0");
	}
	
	/**
     * </p>获取子组织机构</p>
     * @param groupId
     * @param departmentId
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    public List<String> getSubDepartment(String groupId,String departmentId){
        List<String> departmentIds = new ArrayList<String>();
        
        //查询子科室
//        DepartmentParam department = new DepartmentParam();
//        department.setParentId(departmentId);
//        List<DepartmentVO> list= this.searchDepartment(department);
        List<DepartmentVO> list= this.searchDepartment(null, departmentId, null, null);
        
        
        //获取所有科室id
        getIds(list, departmentIds);
        
        return departmentIds;
    }
    
    //递归获取id
    private List<String> getIds(List<DepartmentVO> list,List<String> ids){
        if(list.size()>0){
            for(DepartmentVO vo:list){
                ids.add(vo.getId());
                getIds(vo.getSubList(),ids);
            }
        }
        return ids;
    }
    
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.service.IDepartmentService#getDepartmentById(java.lang.String)
	 */
    @Override
	public Department getDepartmentById(String departmentId){
		//参数校验
    	if(StringUtils.isEmpty(departmentId)){
			throw new ServiceException("组织Id不能为空");
    	}
    	
    	return deparDao.getDepartmentById(departmentId);
    	
	}
    
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.service.IDepartmentService#getDepartmentByName(java.lang.String, java.lang.String)
	 */
    @Override
	public Department getDepartmentByName(String name,String groupId){
		//参数校验
    	if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("集团Id不能为空");
    	}
    	if(StringUtils.isEmpty(name)){
			throw new ServiceException("组织名称不能为空");
    	}
    	return deparDao.getDepartmentByName(name,groupId);
		
	}
    
	/* (non-Javadoc)
	 * @see com.dachen.health.group.department.service.IDepartmentService#createDepartment(java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public Department addDepartment(String groupId,String name,String parentId , Integer doctorId){
		//参数校验
    	if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("集团Id不能为空");
    	}
    	if(StringUtils.isEmpty(name)){
			throw new ServiceException("组织名称不能为空");
    	}
    	if(StringUtils.isEmpty(parentId)){
    		parentId = "0";
    	}
		Department newDepartment = new Department();
		newDepartment.setGroupId(groupId);
		newDepartment.setParentId(parentId);
		newDepartment.setName(name);
		newDepartment.setCreatorDate(System.currentTimeMillis());
		newDepartment.setUpdatorDate(System.currentTimeMillis());
		newDepartment.setCreator(doctorId);
		newDepartment.setUpdator(doctorId);
		
		deparDao.add(newDepartment);
		
    	return deparDao.getDepartmentByName(name,groupId);
		
	}

    @Override
    public List<Department> findListByIds(Collection<String> ids) {
        if (null == ids || 0 == ids.size()) {
            return null;
        }
        return deparDao.findListByIds(ids);
    }

}
