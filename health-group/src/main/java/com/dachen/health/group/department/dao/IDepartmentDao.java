package com.dachen.health.group.department.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;

import com.dachen.health.group.department.entity.param.DepartmentParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.vo.DepartmentDoctorVO;
import com.dachen.health.group.department.entity.vo.DepartmentVO;

public interface IDepartmentDao {


	/**
	 * 新增 部门
	 * @author wangqiao
	 * @date 2016年4月26日 重构
	 * @param department
	 * @return
	 */
	public boolean add(Department department);
	
	/**
     * </p>更新（修改）部门</p>
     * @param department
     * @return boolean
     * @author pijingwei
     * @date 2015年8月10日
     */
//	boolean update(Department department);
	
	
	/**
	 * 根据id，更新名称，描述和父节点信息
	 * @author wangqiao 重构
	 * @date 2016年4月26日
	 * @param id
	 * @param name
	 * @param description
	 * @param parentId
	 */
	public void updateById(String id,String name,String description,String parentId,Integer operationUserId);
	

	/**
	 * 根据id 批量删除 部门
	 * @author wangqiao 重构
	 * @date 2016年4月26日
	 * @param ids
	 */
	public void delete(String[] ids);
	
	/**
     * </p>根据搜索条件获取所有部门列表</p>
     * @param department
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月10日
     */
//	List<DepartmentVO> search(DepartmentParam department);
	
	/**
	 * 根据条件查询部门信息（包括每个部门的子节点）
	 * @author wangqiao
	 * @date 2016年4月28日
	 * @param groupId 集团id
	 * @param parentId 父节点id（默认为0，即一级节点）
	 * @param name   部门名称
	 * @param description 部门描述
	 * @return
	 */
	public List<DepartmentVO> searchDepartment(String groupId,String parentId,String name,String description);
	
	/**
     * </p>根据groupId查找当前集团下是否拥有科室</p>
     * @param department
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月10日
     */
//	int findCountByGroupIds(String... ids);
	
	/**
     * </p>根据id查找</p>
     * @param id
     * @return
     * @author fanp
     * @date 2015年8月14日
     */
//    DepartmentVO getOne(ObjectId id);
    


	/**
     * </p>根据组织Id集合，获取组织详细信息列表</p>
     * @return
     * @author pijingwei
     * @date 2015年8月15日
     */
	List<Department> findListByIds(Collection<String> ids);

	/**
     * </p>查询当前组织是否有子组织</p>
     * @param ids
     * @return List<Department>
     * @author pijingwei
     * @date 2015年8月16日
     */
	List<Department> findSubjectByIds(String[] ids);

	
	/**
     * </p>查询当前组织Id是否有医生</p>
     * @param ids
     * @return List<DepartmentDoctorVO>
     * @author pijingwei
     * @date 2015年8月16日
     */
	List<DepartmentDoctorVO> findDoctorByIds(String[] ids);

	/**
	 * </p>根据id查找</p>
	 * @param groupId
	 * @return List<DepartmentVO>
	 * @author pijingwei
	 * @date 2015年8月18日
	 */
	List<DepartmentVO> findListById(String groupId,String parentId);
	
	/**
	 * 通过组织id读取组织信息
	 * @author wangqiao
	 * @date 2016年3月29日
	 * @param departmentId
	 * @return
	 */
	public Department getDepartmentById(String departmentId);
	
	/**
	 * 通过组织名称和集团id查询 组织信息
	 * @author wangqiao
	 * @date 2016年3月30日
	 * @param name
	 * @param groupId
	 * @return
	 */
	public Department getDepartmentByName(String name,String groupId);
}
