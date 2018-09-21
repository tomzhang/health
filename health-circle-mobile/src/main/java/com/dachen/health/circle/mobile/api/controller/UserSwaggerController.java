package com.dachen.health.circle.mobile.api.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.param.OpenDoctorParam;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.service.IHospitalService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.OpenDoctorExtVO;
import com.dachen.health.commons.vo.OpenDoctorVO;
import com.dachen.health.group.department.entity.po.Department;
import com.dachen.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 钟良
 * @desc
 * @date:2017/8/10 14:59 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Api(value = "用户相关服务",description = "用户相关服务")
@RestController
@RequestMapping("/user")
public class UserSwaggerController {

    @Autowired
    private UserManager userManager;
    @Autowired
    private IHospitalService hospitalService;

    @ApiOperation(value = "按企业Id查找医生", notes = "按企业Id查找医生", response = OpenDoctorVO.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orgId", value = "企业Id", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "appKey", value = "appKey", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "pageIndex", value = "页码", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "每页数据大小", required = true, dataType = "int", paramType = "query"),
    })
    @RequestMapping(value = "/getSimpleDoctorListByOrgId", method = RequestMethod.GET)
    public JSONMessage getSimpleDoctorListByOrgId(@RequestParam String orgId, @RequestParam String appKey,
            @RequestParam(defaultValue="0") Long ts, @RequestParam(defaultValue="0") Integer pageIndex,
            @RequestParam(defaultValue="15") Integer pageSize) {
        if (StringUtil.isBlank(orgId)) {
            throw new ServiceException("企业id不能为空");
        }
        if (StringUtil.isBlank(appKey)) {
            throw new ServiceException("appKey不能为空");
        }
        return JSONMessage.success(userManager.findDoctorsByOrgId(appKey, orgId, null, null, null,null, UserStatus.normal.getIndex(), ts, pageIndex, pageSize));
    }

    @ApiOperation(value = "按企业Id查找所有状态医生", notes = "按企业Id查找所有状态医生", response = OpenDoctorVO.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orgId", value = "企业Id", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "hospitalId", value = "医院Id", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "deptId", value = "科室ID", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "name", value = "医生名称关键字", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "userId", value = "userId", required = false, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "appKey", value = "appKey", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "pageIndex", value = "页码", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "每页数据大小", required = true, dataType = "int", paramType = "query"),
    })
    @RequestMapping(value = "/getAllSimpleDoctorListByOrgId", method = RequestMethod.GET)
    public JSONMessage getAllSimpleDoctorListByOrgId(@RequestParam String orgId,
        @RequestParam String appKey,
        String hospitalId,
        String deptId,
        String name,
        Integer userId,
        @RequestParam(defaultValue="0") Long ts, @RequestParam(defaultValue="0") Integer pageIndex,
        @RequestParam(defaultValue="15") Integer pageSize) {
        if (StringUtil.isBlank(orgId)) {
            throw new ServiceException("企业id不能为空");
        }
        if (StringUtil.isBlank(appKey)) {
            throw new ServiceException("appKey不能为空");
        }
        return JSONMessage.success(userManager.findDoctorsByOrgId(appKey, orgId, hospitalId, deptId, name, userId, null, ts, pageIndex, pageSize));
    }


    @ApiOperation(value = "按修改时间查找所有医生", response = OpenDoctorExtVO.class)
    @RequestMapping(value = "/findDoctorInfoByModifyTime", method = RequestMethod.GET)
    public JSONMessage findDoctorInfoByModifyTime(OpenDoctorParam param) {
        return JSONMessage.success(userManager.findDoctorInfoByModifyTime(param));
    }
    
    @ApiOperation(value = "获取医院信息更新信息更新时间", notes = "获取医院信息更新信息更新时间", response = PageVO.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orgId", value = "企业Id", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "appKey", value = "appKey", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "updateTime", value = "更新时间", required = true, dataType = "Long", paramType = "query"),
        @ApiImplicitParam(name = "pageIndex", value = "页码", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "每页数据大小", required = true, dataType = "int", paramType = "query"),
    })
    @RequestMapping(value = "/getHospitalByUpdate", method = RequestMethod.POST)
    public JSONMessage getHospitalByUpdate(@RequestParam String orgId, @RequestParam String appKey, @RequestParam(defaultValue="0") Long updateTime, 
            @RequestParam(defaultValue="0") Integer pageIndex, @RequestParam(defaultValue="15") Integer pageSize,
        @RequestParam(required = false) String hospital, @RequestParam(required = false) String hospitalId) {
//        if (StringUtil.isBlank(orgId)) {
//            throw new ServiceException("企业id不能为空");
//        }
//        if (StringUtil.isBlank(appKey)) {
//            throw new ServiceException("appKey不能为空");
//        }
        return JSONMessage.success(hospitalService.getHospitalByUpdate(appKey, orgId, updateTime, pageIndex, pageSize, hospital, hospitalId));
    }

    @ApiOperation(value = "获取科室信息", notes = "获取科室信息", response = Department.class)
    @RequestMapping(value = "/getDepartments", method = RequestMethod.GET)
    public JSONMessage getDepartments(@RequestParam String orgId, @RequestParam String appKey) {
//        if (StringUtil.isBlank(orgId)) {
//            throw new ServiceException("企业id不能为空");
//        }
//        if (StringUtil.isBlank(appKey)) {
//            throw new ServiceException("appKey不能为空");
//        }
        return JSONMessage.success(hospitalService.getDepartments(appKey, orgId));
    }

}
