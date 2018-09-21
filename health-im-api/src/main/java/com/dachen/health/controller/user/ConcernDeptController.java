package com.dachen.health.controller.user;

import com.dachen.commons.JSONMessage;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.service.impl.ConcernDeptService;
import com.dachen.health.commons.vo.ConcernDept;
import com.dachen.util.ReqUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sharp on 2017/11/28.
 */
@RestController
@RequestMapping("/user/dept")
@Api(value = "关注科室",description = "医生关注的科室")
public class ConcernDeptController extends AbstractController {

    @Autowired
    ConcernDeptService concernDeptService;

    @ApiOperation(value = "添加科室")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JSONMessage setDepts(@RequestBody ConcernDept dept) {
        Integer userId = ReqUtil.getCurrentUserId();
        return JSONMessage.success(concernDeptService.setDepts(userId, dept.getDeptIds()));
    }

    @ApiOperation(value = "查询用户关注的科室", response = DeptVO.class)
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public JSONMessage findDepts(@PathVariable("userId") Integer userId) {
        return JSONMessage.success(concernDeptService.findDepts(userId));
    }

    @ApiOperation(value = "查询用户所有的科室，（包含住院科室，第一位）", response = DeptVO.class)
    @RequestMapping(value = "/all/{userId}", method = RequestMethod.GET)
    public JSONMessage findAllDepts(@PathVariable("userId") Integer userId) {
        return JSONMessage.success(concernDeptService.findAllDepts(userId));
    }
    
    @ApiOperation(value = "查询关注科室的用户id", response = DeptVO.class)
    @RequestMapping(value = "/concernUserIds/{deptId}/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public JSONMessage findConcernUserIds(@PathVariable("deptId") String deptId,
                                        @PathVariable("pageIndex") int pageIndex,
                                        @PathVariable("pageSize") int pageSize) {
        return JSONMessage.success(concernDeptService.findConcernDeptUserIds(deptId, pageIndex, pageSize));
    }
}
