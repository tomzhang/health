package com.dachen.health.circle.controller.api;

import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.AdScopeEnum;
import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.UserGroupAndUnionMap;
import com.dachen.health.circle.vo.UserGroupAndUnionIdMap;
import com.dachen.sdk.page.Pagination;
import com.dachen.web.ApiBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/circle/group")
public class ApiCircleGroupController extends ApiBaseController {

    @Autowired
    protected Group2Service group2Service;

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    @Autowired
    protected GroupUser2Service groupUser2Service;

    @Autowired
    protected User2Service user2Service;

    @Autowired
    protected GroupFollowService groupFollowService;
    @Autowired
    protected DoctorFollowService doctorFollowService;


    @RequestMapping(value = "/findUserGroupAndUnionMap", method = RequestMethod.GET)
    public JSONMessage findUserGroupAndUnionMap(@RequestParam Integer userId) {
        UserGroupAndUnionMap ret = group2Service.findUserGroupAndUnionMap(userId);
        return JSONMessage.success(null, ret);
    }

    @RequestMapping(value = "/findUserGroupAndUnionIdMap", method = RequestMethod.GET)
    public JSONMessage findUserGroupAndUnionIdMap(@RequestParam Integer userId) {
        UserGroupAndUnionIdMap ret = group2Service.findUserGroupAndUnionIdMap(userId);
        return JSONMessage.success(null, ret);
    }

    @RequestMapping(value = "/findDeptByUser/{userId}", method = RequestMethod.GET)
    public JSONMessage findDeptByUser(@PathVariable Integer userId) {
        Group2 group = group2Service.findDeptByUser(userId);
        return JSONMessage.success(null, group);
    }

    @RequestMapping(value = "/findDeptPage", method = RequestMethod.GET)
    public JSONMessage findDeptByUser(String kw, @RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "15") Integer pageSize) {
        Pagination<Group2> ret = group2Service.findPage(kw, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    @RequestMapping(value = "/findById/{id}", method = RequestMethod.GET)
    public JSONMessage findById(@PathVariable String id) {
        Group2 group = group2Service.findById(id);
        return JSONMessage.success(null, group);
    }

    @RequestMapping(value = "/findManagerIdList/{id}", method = RequestMethod.GET)
    public JSONMessage findManagerIdList(@PathVariable String id) {
        List<Integer> doctorIdList = groupUser2Service.findDoctorIdList(id);
        return JSONMessage.success(null, doctorIdList);
    }

    @RequestMapping(value = "/findDoctorIdList", method = RequestMethod.POST)
    public JSONMessage findDoctorIdList(@RequestParam(required = false) List<String> groupIdList, @RequestParam(required = false) List<String> unionIdList) {
        List<Integer> doctorIdList = groupDoctor2Service.findDoctorIdListByGroupAndUnions(groupIdList, unionIdList);
        return JSONMessage.success(null, doctorIdList);
    }
   @RequestMapping(value = "/getUserIdByAdSope", method = RequestMethod.POST)
   public JSONMessage getUserIdByAdSope(@RequestParam(required = true) String type,
                                        @RequestParam(required = false) String provinceJsons,
                                        @RequestParam(required = false) List<String> levels,
                                        @RequestParam(required = false) List<String> deptIds,
                                        @RequestParam(required = false) List<String> titles,
                                        @RequestParam(required = false) List<String> groupIds,
                                        @RequestParam(required = false) List<String> unionIds,
                                        @RequestParam(required = false) Boolean userCheck,
                                        @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                        @RequestParam(name = "pageSize", defaultValue = "50") Integer pageSize) {
       if (type.equals(AdScopeEnum.all.getIndex())) {
           if (null == userCheck){
               return JSONMessage.success(null, user2Service.getNormalUserPage(pageIndex, pageSize));
           }else {
               return JSONMessage.success(null, user2Service.getNormalUserPage(pageIndex, pageSize,userCheck));
           }

       } else if (type.equals(AdScopeEnum.condition.getIndex())) {
           if (levels == null) levels = new ArrayList<>();
           if (deptIds == null) deptIds = new ArrayList<>();
           if (titles == null) titles = new ArrayList<>();
           if (null == userCheck){
               return JSONMessage.success(null, user2Service.getNormalUserIdByCityAndLevelAndDepartmentsAndTitlePage(provinceJsons,
                       levels, deptIds, titles, pageIndex, pageSize));
           }else {
               return JSONMessage.success(null, user2Service.getNormalUserIdByCityAndLevelAndDepartmentsAndTitlePage(provinceJsons,
                       levels, deptIds, titles,userCheck, pageIndex, pageSize));
           }

       } else if (type.equals(AdScopeEnum.organization.getIndex())) {
           if (groupIds == null) groupIds = new ArrayList<>();
           if (unionIds == null) unionIds = new ArrayList<>();
           return JSONMessage.success(null, user2Service.getNormalUserIdByUnionIdAndGroupIdPage(groupIds, unionIds, pageIndex, pageSize));
       }
       return JSONMessage.success(null, null);
   }

    /**
     * 根据userId查询 出关注的所有科室的所有人员 （去重）
     * @param userId
     * @return
     */
    @RequestMapping(value = "getDeptUserByUserId/{userId}",method = RequestMethod.GET)
    public JSONMessage getDeptUserByUserId(@PathVariable Integer userId){
        return JSONMessage.success(null,groupFollowService.getDeptUserByUserId(userId));
    }

    /**
     * 根据userId 查询 粉丝的人员id列表
     * @param userId
     * @return
     */
    @RequestMapping(value = "getDoctorFansListByUserId/{userId}",method = RequestMethod.GET)
    public JSONMessage getDoctorFansListByUserId(@PathVariable Integer userId){
        return JSONMessage.success(null,doctorFollowService.getDoctorFansList(userId));
    }
    /**
     * 根据userId 查询 关注的人员id列表
     * @param userId
     * @return
     */
    @RequestMapping(value = "getDoctorFollowListByUserId/{userId}",method = RequestMethod.GET)
    public JSONMessage getDoctorFollowListByUserId(@PathVariable Integer userId){
        return JSONMessage.success(null,doctorFollowService.getDoctorFollowList(userId));
    }
}
