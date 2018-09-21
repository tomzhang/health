package com.dachen.health.controller.inner;

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.service.impl.ConcernDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author cuizhiquan
 * @Description
 * @date 2017/12/12 15:34
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("/inner_api/user/dept")
@Api(value = "关注科室",description = "医生关注的科室")
public class InnerConcernDeptController {

    @Autowired
    ConcernDeptService concernDeptService;

    @ApiOperation(value = "查询用户关注的科室")
    @RequestMapping(value = "/findDepts", method = RequestMethod.POST)
    public JSONMessage findDepts(@RequestParam Integer userId) {
        return JSONMessage.success(concernDeptService.findDepts(userId));
    }

    @ApiOperation(value = "查询用户所有的科室，（包含住院科室，第一位）")
    @RequestMapping(value = "/findAllDepts", method = RequestMethod.POST)
    public JSONMessage findAllDepts(@RequestParam Integer userId) {
        return JSONMessage.success(concernDeptService.findAllDepts(userId));
    }
}
