package com.dachen.health.group.department.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.department.entity.param.DepartmentDoctorParam;
import com.dachen.health.group.department.entity.po.DepartmentDoctor;
import com.dachen.health.group.group.entity.param.GroupSearchParam;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
public interface IDepartmentDoctorDao {

	/**
     * </p>部门添加医生</p>
     * @param DepartmentDoctorVO
     * @return boolean
     * @author pijingwei
     * @date 2015年8月10日
     */
	boolean save(DepartmentDoctor ddoc);
	
	/**
     * </p>批量科室添加医生</p>
     * @param DepartmentDoctorVO
     * @return boolean
     * @author pijingwei
     * @date 2015年8月10日
     */
	boolean save(List<DepartmentDoctor> ddoc);
	
	/**
     * </p>更新（修改）部门中的医生</p>
     * @param DepartmentDoctorVO
     * @return boolean
     * @author pijingwei
     * @date 2015年8月10日
     */
	boolean update(DepartmentDoctor ddoc);
	
	/**
     * </p>删除部门中关联的医生</p>
     * @param ids
     * @return boolean
     * @author pijingwei
     * @date 2015年8月10日
     */
	boolean delete(String ...ids);
	
	/**
     * </p>根据搜索条件获取当前部门下所有的医生列表</p>
     * @param DepartmentParam
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月10日
     */
	PageVO search(DepartmentDoctorParam param);
	
	/**
     * </p>查找科室下的医生</p>
     * @param departmentId
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    List<Integer> getDepartmentDoctorId(String groupId,List<String> departmentIds,String[] statuses);

    public List<DepartmentDoctor> getDepartmentDoctorByGroupIdAndDoctorId(String groupId, int doctorId);
    
    /**
     * </p>查找未分配的医生</p>
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月23日
     */
    List<Integer> getUndistributedDoctorId(String groupId);
    
	/**
     * </p>根据医生Id，组织Id，删除组织与医生的关联</p>
     * @param DepartmentParam
     * @return
     * @author pijingwei
     * @date 2015年8月15日
     */
	void deleteByAlsoId(DepartmentDoctor ddoc);

	/**
     * </p>根据Id删除关联数据</p>
     * @param DepartmentParam
     * @return
     * @author pijingwei
     * @date 2015年8月15日
     */
	void deleteDoctorIdByDepartmentId(Integer... doctorIds);
	
	/**
	 * 根据医生id和集团id，删除该医生在该集团下的所有depart-doctor关系
	 * @param doctorId
	 * @param groupId
	 *@author wangqiao
	 *@date 2016年1月18日
	 */
	public void deleteByDoctorIdAndGroupId(Integer[] doctorIds,String groupId);

	/**
     * </p>根据医生Id获取存在的医生数据条数</p>
     * @param DepartmentParam
     * @return
     * @author pijingwei
     * @date 2015年8月15日
     */
	int getCountDepartmentDoctorByDoctorIds(List<Integer> docList);

	
	/**
	 * </p>通过组织架构查找医生</p>
	 * @param departmentId
	 * @return
	 * @author fanp
	 * @date 2015年8月26日
	 */
	List<Integer> getDoctorIdsByDepartment(String departmentId);
	
	/**
     * </p>查找属于该组织架构的医生</p>
     * @param groupId
     * @param doctorIds
     * @return
     * @author fanp
     * @date 2015年8月26日
     */
	Set<Integer> getDoctorIdsByDepartment(List<String> departmentIds,List<Integer> doctorIds);
	
	/**
     * </p>根据医生Id和集团Id删除所有相关的科室数据</p>
     * @param DepartmentParam
     * @return
     * @author pijingwei
     * @date 2015年8月15日
     */
	void deleteAllCorrelation(String groupId, Integer doctorId);

	/**
     * </p>根据groupId集合获取当前集团科室下分配的医生人数</p>
     * @param ids
     * @return int
     * @author pijingwei
     * @date 2015年8月28日
	 */
	int getCountByGroupIds(String[] ids);
	
	
	/**
	 * 根据医生集团ID 或  科室ID 查询所有医生信息
	 * @param docGroupId
	 * @param departmentId
	 * @return
	 */
	PageVO findDocGroupDoctorInfo(GroupSearchParam param);
	
	PageVO findDocGroupOnlineDoctorInfo(GroupSearchParam param);
	
	DepartmentDoctor updateDepartment(DepartmentDoctorParam param);
	
	/**
	 * 查询医生的部门名称
	 * @param groupId
	 * @param doctorIdList
	 * @return
	 *@author wangqiao
	 *@date 2016年2月18日
	 */
	public Map<Integer, String> getDepartmentFullName(String groupId,List<Integer> doctorIdList);
	
}
