package com.dachen.health.group.department.service;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;

import com.dachen.health.group.department.entity.param.DepartmentParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.vo.DepartmentVO;

public interface IDepartmentService {

	
	/**
	 * 新增部门
	 * @author wangqiao 重构
	 * @date 2016年4月26日
	 * @param department
	 * @return
	 */
	public boolean addDepartment(Department department);
	
	/**
     * </p>更新（修改）部门</p>
     * @param department
     * @return boolean
     * @author pijingwei
     * @date 2015年8月10日
     */
//	boolean updateDepartment(Department department);
	
	/**
	 * 通过id，更新部门的名称，描述和父节点信息
	 * @author wangqiao
	 * @date 2016年4月26日
	 * @param id
	 * @param name
	 * @param description
	 * @param parentId
	 * @param operationUserId
	 */
	public void updateDepartmentById(String id,String name,String description,String parentId,Integer operationUserId);
	

	/**
	 * 根据id 批量删除 部门
	 * @author wangqiao 重构
	 * @date 2016年4月26日
	 * @param ids
	 */
	public void deleteDepartment(String[] ids);
	
	/**
     * </p>根据搜索条件获取所有部门列表</p>
     * @param department
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月10日
     */
//	List<DepartmentVO> searchDepartment(DepartmentParam department);
	
	/**
	 * 根据条件查询部门信息（包括每个部门的子节点）
	 * @author wangqiao
	 * @date 2016年4月28日
	 * @param groupId
	 * @param parentId
	 * @param name
	 * @param description
	 * @return
	 */
	public List<DepartmentVO> searchDepartment(String groupId,String parentId,String name,String description);
	
	
	/**
	 * </p>根据id查找</p>
	 * @param id
	 * @return
	 * @author fanp
	 * @date 2015年8月14日
	 */
//	DepartmentVO getOne(ObjectId id);
	
//	public DepartmentVO getDepartmentVOById(String id);

	/**
	 * </p>根据id查找</p>
	 * @param groupId
	 * @return List<DepartmentVO>
	 * @author pijingwei
	 * @date 2015年8月18日
	 */
	List<DepartmentVO> findAllListById(String groupId);
	
	/**
	 * </p>获取子组织机构</p>
	 * @param groupId
	 * @param departmentId
	 * @return
	 * @author fanp
	 * @date 2015年9月21日
	 */
	List<String> getSubDepartment(String groupId,String departmentId);
	
	/**
	 * 通过组织id读取组织信息
	 * @author wangqiao
	 * @date 2016年3月29日
	 * @param departmentId
	 * @return
	 */
	public Department getDepartmentById(String departmentId);
	
	/**
	 * 通过部门名称和groupId查询 组织信息
	 * @author wangqiao
	 * @date 2016年3月30日
	 * @param name
	 * @param groupId
	 * @return
	 */
	public Department getDepartmentByName(String name,String groupId);
	
	/**
	 * 新建组织（部门）
	 * @author wangqiao
	 * @date 2016年3月30日
	 * @param groupId
	 * @param name
	 * @param parentId
	 * @return
	 */
	public Department addDepartment(String groupId,String name,String parentId,Integer doctorId);


    List<Department> findListByIds(Collection<String> ids);
}
