package com.dachen.health.group.department.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.group.department.entity.param.DepartmentDoctorParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.po.DepartmentDoctor;
import com.dachen.health.group.department.entity.vo.DepartmentVO;

/**
 * 
 * @author pijingwei
 * @date 2015/8/11
 */
public interface IDepartmentDoctorService {

    /**
     * </p>部门添加医生</p>
     * 
     * @return boolean
     * @author pijingwei
     * @date 2015年8月10日
     */
    boolean saveDepartmentDoctor(DepartmentDoctor ddoc);
    
	/**
	 * 医生加入部门
	 * @author wangqiao
	 * @date 2016年3月30日
	 * @param groupId
	 * @param departmentId
	 * @param doctorId
	 */
	public void createDepartmentDoctor(String groupId,String departmentId,Integer doctorId);

    /**
     * </p>更新（修改）部门中的医生</p>
     * 
     * @return boolean
     * @author pijingwei
     * @date 2015年8月10日
     */
    boolean updateDepartmentDoctor(DepartmentDoctor ddoc);

    /**
     * </p>删除部门中关联的医生</p>
     * 
     * @param ids
     * @return boolean
     * @author pijingwei
     * @date 2015年8月10日
     */
    boolean deleteDepartmentDoctor(String... ids);

    /**
     * </p>根据搜索条件获取当前部门下所有的医生列表</p>
     * 
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月10日
     */
    PageVO searchDepartmentDoctor(DepartmentDoctorParam param);

    /**
     * </p>查找科室下的医生</p>
     * 
     * @param departmentId
     * @return
     * @author fanp
     * @param consultationPackId 
     * @date 2015年9月21日
     */
    List<BaseUserVO> getDepartmentDoctor(String groupId, String departmentId, Integer type,String[] status, String consultationPackId);
    
    /**
     * </p>查找科室下的医生</p>
     * 
     * @param departmentId
     * @return
     * @author fanp
     * @param consultationPackId 
     * @date 2015年9月21日
     */
    List<BaseUserVO> getDepartmentRecommendDoctor(String groupId, String departmentId, Integer type,String[] status, String consultationPackId, List<Integer> recommendDoctorIds);

    /**
     * </p>根据状态查找集团医生</p>
     * 
     * @param groupId
     * @param status
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    List<BaseUserVO> getDeptDoctorByStatus(String groupId, String status);

    /**
     * </p>根据医生Id，获取医生所属的所有组织架构列表</p>
     * 
     * @param doctorId
     * @return List<Department>
     * @author pijingwei
     * @date 2015年8月10日
     */
    List<Department> getDepartmentByDoctorId(Integer doctorId);

    /**
     * </p>将一个医生分配给多个科室</p>
     * 
     * @param departIds
     * @param doctorId
     * @param groupId
     * @return
     * @author pijingwei
     * @date 2015年8月10日
     */
    void saveDoctorIdByDepartmentIds(String[] departIds, Integer doctorId, String groupId);

    /**
     * </p>给一个科室同时添加多个医生</p>
     * 
     * @return List<Department>
     * @author pijingwei
     * @date 2015年8月10日
     */
    void saveDepartmentIdByDoctorIds(String departmentId, Integer[] doctorIds, String groupId);

    /**
     * </p>通过组织架构查找医生</p>
     * 
     * @param departmentId
     * @return
     * @author fanp
     * @date 2015年8月26日
     */
    List<Integer> getDoctorIdsByDepartment(String departmentId);

    /**
     * </p>查找属于该组织架构的医生</p>
     * 
     * @param doctorIds
     * @return
     * @author fanp
     * @date 2015年8月26日
     */
    Set<Integer> getDoctorIdsByDepartment(List<String> departmentIds, List<Integer> doctorIds);

    DepartmentDoctor updateDepartment(DepartmentDoctorParam param);
    
    public List<DepartmentDoctor> getDepartmentDoctorByGroupIdAndDoctorId(String groupId, int doctorId);
    
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
