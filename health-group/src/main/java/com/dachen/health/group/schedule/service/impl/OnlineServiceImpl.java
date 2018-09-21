package com.dachen.health.group.schedule.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.group.department.entity.param.DepartmentParam;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.health.group.department.entity.vo.DepartmentVO;
import com.dachen.health.group.department.service.IDepartmentDoctorService;
import com.dachen.health.group.department.service.IDepartmentService;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.schedule.dao.IOnlineDao;
import com.dachen.health.group.schedule.entity.param.OnlineClinicDate;
import com.dachen.health.group.schedule.entity.param.OnlineParam;
import com.dachen.health.group.schedule.entity.po.Online;
import com.dachen.health.group.schedule.entity.po.OnlineDoctorInfo;
import com.dachen.health.group.schedule.entity.vo.OnlineVO;
import com.dachen.health.group.schedule.service.IOnlineService;

@Service
public class OnlineServiceImpl implements IOnlineService {

    @Autowired
    protected IOnlineDao onlineDao;

    @Autowired
    protected IDepartmentService departmentService;
    
    @Autowired
    protected IGroupService groupService;
    
    @Autowired
    protected IDepartmentDoctorService departmentDoctorService;
    
    @Autowired
    protected IBaseUserService baseUserService;
    

    /**
     * </p>添加在线值班信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月11日
     */
    public void add(OnlineParam param) {

        // 查询科室信息，获取集团id
//        DepartmentVO vo = departmentService.getOne(param.getDepartmentId());
        Department department = departmentService.getDepartmentById(param.getDepartmentId().toString());
        
        // 科室不存在
        if (department == null) {
            throw new ServiceException("科室不存在");
        }
        if (department.getGroupId() == null || department.getName() == null) {
            throw new ServiceException("集团不存在");
        }
        //判断集团是否存在
        Group group = groupService.getGroupById(department.getGroupId());
        if(group==null){
            throw new ServiceException("集团不存在");
        }

        // TODO 判断医生是否属于该集团，如果有不属于的为非法添加，则不入库
        //获取用户id
        List<Integer> ids = new ArrayList<Integer>();
        for (OnlineClinicDate week : param.getClinicDate()) {
            // 判断时间和医生id的正确性
            if (week.getWeek() != null && week.getPeriod() != null && week.getDoctors() != null 
                    && week.getWeek() >= 1 && week.getWeek() <= 7 
                    && week.getPeriod() >= 1 && week.getPeriod() <= 3) {
                for(OnlineDoctorInfo doctor:week.getDoctors()){
                    ids.add(doctor.getDoctorId());
                }
            }
        }
        if(ids.size()==0){
            throw new ServiceException("请选择正确的医生");
        }
        
        //筛选真实存在的医生
        
        //查询子科室
//        DepartmentParam department = new DepartmentParam();
//        department.setParentId(vo.getId());
//        List<DepartmentVO> list= departmentService.searchDepartment(department);
        List<DepartmentVO> list= departmentService.searchDepartment(null, department.getId(), null, null);
        
        //获取所有科室id
        List<String> departmentIds = new ArrayList<String>();
        departmentIds.add(department.getId());
        getIds(list, departmentIds);
        
        //获取所有科室下医生
        Set<Integer> doctorIds = departmentDoctorService.getDoctorIdsByDepartment(departmentIds, ids);
        //筛选状态正常的医生
//        if(doctorIds.size()>0){
//            List<BaseUserVO> normalList = baseUserService.getByIds(doctorIds.toArray(new Integer[]{}));
//            if(normalList.size()>0){
//                Set<Integer> normalIds = new HashSet<Integer>();
//                for(BaseUserVO normalVO: normalList){
//                    if(normalVO.getStatus() == UserEnum.UserStatus.normal.getIndex()){
//                        normalIds.add(normalVO.getUserId());
//                    }
//                }
//                doctorIds = normalIds;
//            }
//        }
        
        // 添加数据
        for (OnlineClinicDate week : param.getClinicDate()) {
            // 判断时间和医生id的正确性
            if (week.getWeek() != null && week.getPeriod() != null && week.getDoctors() != null 
                    && week.getWeek() >= 1 && week.getWeek() <= 7 
                    && week.getPeriod() >= 1 && week.getPeriod() <= 3) {
                
                //去除不存在该科室及子科室下的医生
                for(int j=0;j<week.getDoctors().size();j++){
                    if(!doctorIds.contains(week.getDoctors().get(j).getDoctorId())){
                        week.getDoctors().remove(j);
                        j--;
                    }
                }
                if(week.getDoctors().size()==0){
                    continue;
                }
                
                Online online = new Online();
                online.setGroupId(department.getGroupId());
                online.setDepartmentId(department.getId());
                online.setDepartment(department.getName());
                online.setWeek(week.getWeek());
                online.setPeriod(week.getPeriod());
                online.setDoctors(week.getDoctors());
                
                onlineDao.add(online);
            }
        }
    }

    /**
     * </p>按科室查找在线值班时间表,需查找子节点的数据</p>
     * 
     * @return List<OfflineVO> Set<hospital>
     * @author fanp
     * @date 2015年8月11日
     */
    public OnlineVO getAllByDept(String deptId) {
        //查找当前科室医生中已分配的医生
        List<OnlineClinicDate> onList = null;
        
        //查找科室所属集团
//        DepartmentVO dept = departmentService.getOne(new ObjectId(deptId));
        Department dept = departmentService.getDepartmentById(deptId);
        
        if (dept != null && dept.getGroupId() != null) {
          //查找当前科室的医生
          List<Integer> doctorIds = departmentDoctorService.getDoctorIdsByDepartment(deptId);
          
          //查找当前科室下的医生被其他科室分配情况
          onList = onlineDao.getOnlineByDoctorIds(doctorIds, dept.getGroupId());
        }
        
        //查找子科室
//        DepartmentParam department = new DepartmentParam();
//        department.setParentId(deptId);
//        List<DepartmentVO> list= departmentService.searchDepartment(department);
        List<DepartmentVO> list= departmentService.searchDepartment(null, deptId, null, null);
        
        //获取所有科室id
        List<String> departmentIds = new ArrayList<String>();
        getIds(list, departmentIds);
        
        //获取所有子科室下排班
        OnlineVO vo = onlineDao.getAllByDept(departmentIds);
        
        //取vo与onList并集
        List<OnlineClinicDate> subList = vo.getClinicDate();
        for(int j=0;j<subList.size();j++){
            for(int i=0;i<onList.size();i++){
                OnlineClinicDate sub = subList.get(j);
                OnlineClinicDate on = onList.get(i);
                
                if(sub.getWeek()==on.getWeek() && sub.getPeriod()==on.getPeriod()){
                    //同一个排班点
                    
                    sub.getDoctors().addAll(on.getDoctors());
                    onList.remove(i);
                    i--;
                }else{
                    if((sub.getWeek()==on.getWeek() && sub.getPeriod()>on.getPeriod())||
                            sub.getWeek()>on.getWeek()){
                        //子排班小于父排班，添加到父排班
                        
                        subList.add(j, on);
                        j++;
                        
                        onList.remove(i);
                        i--;
                    }else{
                        break;
                    }
                }
            }
        }
        //合并剩余排班
        subList.addAll(onList);
        
        return vo;
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
    
    /**
     * </p>按医生查找在线值班时间表</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    public Map<String, Object> getAllByDoctor(Integer doctorId) {
        //只有医生状态正常才显示值班
        //BaseUserVO user = baseUserService.getUser(doctorId);
        //if(user!=null && user.getStatus()!=null && user.getStatus() == UserEnum.UserStatus.normal.getIndex()){
            return onlineDao.getAllByDoctor(doctorId);
        //}
        //return null;
    }
    
    /**
     * </p>删除在线值班信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月13日
     */
    public void delete(OnlineParam param){
        if(param.getDoctorId() != null && param.getDepartmentId() != null &&
                param.getWeek() !=null && param.getPeriod() !=null){
            onlineDao.delete(param);
        }
    }
    
}
