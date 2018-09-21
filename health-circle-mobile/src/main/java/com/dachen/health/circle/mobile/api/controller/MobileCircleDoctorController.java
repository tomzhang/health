package com.dachen.health.circle.mobile.api.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.entity.DoctorFollow;
import com.dachen.health.circle.mobile.controller.MobileCircleBaseController;
import com.dachen.health.circle.service.DoctorFollowService;
import com.dachen.health.circle.service.GroupDoctor2Service;
import com.dachen.health.circle.service.User2Service;
import com.dachen.health.circle.vo.*;
import com.dachen.sdk.page.Pagination;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/m/circle/doctor")
@Api(value = "医生相关服务",description = "医生相关服务")
public class MobileCircleDoctorController extends MobileCircleBaseController {

    @Autowired
    protected User2Service user2Service;
    @Autowired
    protected DoctorFollowService doctorFollowService;
    @Autowired
    private GroupDoctor2Service groupDoctor2Service;

    @ApiIgnore
    @ApiOperation(value = "医生主页信息")
    @RequestMapping(value = "/getInfo")
    public JSONMessage getIntro(Integer userId) {
        Integer currentUserId = this.getCurrentUserId();
        MobileDoctorVO ret = user2Service.getInfo(userId);
        return JSONMessage.success(null, ret);
    }

    @ApiOperation(value = "医生主页信息",httpMethod="POST",response =MobileDoctorHomeVO.class)
    @RequestMapping(value = "/getDoctorHomePage")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "医生id", dataType = "Integer"),
            @ApiImplicitParam(name = "access_token",value = "token", dataType ="String" ,required = true)
    })
    public JSONMessage getDoctorHomePage(Integer userId){
        Integer currentUserId = this.getCurrentUserId();
        MobileDoctorHomeVO mobileDoctorHomeVO=user2Service.getDoctorHomePage(currentUserId,userId);
        Map<String,String> map= groupDoctor2Service.getBothFriendStatus(currentUserId, userId);
        String status = map.get("status");
        mobileDoctorHomeVO.setStatus(Integer.parseInt(status));
        if("5".equals(status)) {
            mobileDoctorHomeVO.setfId(map.get("fId"));
        }
        return JSONMessage.success(null,mobileDoctorHomeVO);
    }

    @ApiOperation(value = "医生圈首页信息（关注 粉丝 收藏数 ）",httpMethod="POST",response =MobileCircleHomeVO.class)
    @RequestMapping(value = "/getCircleHomePage")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token",value = "token", dataType ="String" ,required = true)
    })
    public JSONMessage getCircleHomePage(){
        Integer currentUserId = this.getCurrentUserId();
        MobileCircleHomeVO mobileCircleHomeVO=user2Service.getCircleIndex(currentUserId);
        return JSONMessage.success(null,mobileCircleHomeVO);
    }


    @ApiOperation(value = "关注列表",httpMethod="POST",response =MobileDoctorFollowVO.class)
    @RequestMapping(value="/doctorFollowerList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doctorId", value = "医生id", dataType = "Integer",required = true),
            @ApiImplicitParam(name = "pageIndex", value = "页数", dataType = "Integer", required = false , defaultValue = "0"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", dataType = "Integer", required = false , defaultValue = "15"),
            @ApiImplicitParam(name = "access_token",value = "token", dataType ="String" ,required = true)
    })
    public JSONMessage doctorFollowerList(Integer doctorId,@RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "15") Integer pageSize){
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileDoctorFollowVO> doctorFansPage = doctorFollowService.getDoctorFollowPage(currentUserId,doctorId, pageSize, pageIndex);
        return JSONMessage.success(null,doctorFansPage);
    }
    @ApiOperation(value = "粉丝列表" ,httpMethod="POST",response =MobileDoctorFollowVO.class)
    @RequestMapping(value = "/doctorFanList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doctorId", value = "医生id", dataType = "Integer",required = true),
            @ApiImplicitParam(name = "pageIndex", value = "页数", dataType = "Integer", required = false , defaultValue = "0"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", dataType = "Integer", required = false , defaultValue = "15"),
            @ApiImplicitParam(name = "access_token",value = "token", dataType ="String" ,required = true)
    })
    public JSONMessage doctorFanList(Integer doctorId,@RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "15") Integer pageSize){
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileDoctorFollowVO> doctorFollowPage = doctorFollowService.getDoctorFansPage(currentUserId,doctorId, pageSize, pageIndex);
        return JSONMessage.success(null,doctorFollowPage);
    }
    @ApiOperation(value = "搜索医生列表",httpMethod="POST",response =MobileDoctorFollowVO.class)
    @RequestMapping(value = "/searchDoctorList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页数", dataType = "Integer", defaultValue = "0"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", dataType = "Integer",  defaultValue = "10"),
            @ApiImplicitParam(name = "keyWord", value = "搜索关键字", dataType = "String"),
            @ApiImplicitParam(name = "access_token",value = "token", dataType ="String" , required = true)
    })
    public JSONMessage searchDoctorList(String keyWord,@RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "10") Integer pageSize){
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileDoctorBriefVo> pagination = user2Service.searchUserByKeyWord(currentUserId, keyWord, pageIndex, pageSize);
        return JSONMessage.success(null,pagination);
    }

    @ApiOperation(value = "关注医生",httpMethod="POST",notes = "关注成功返回true")
    @RequestMapping(value = "/follow")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doctorId", value = "医生id", dataType = "Integer"),
            @ApiImplicitParam(name = "access_token",value = "token", dataType ="String" ,required = true)
    })
    public JSONMessage follow(Integer doctorId){
        Integer currentUserId = this.getCurrentUserId();
        DoctorFollow doctorFollow = doctorFollowService.addFollow(doctorId, currentUserId);
        return JSONMessage.success(null,doctorFollow!=null);
    }

    @ApiOperation(value = "取关医生",httpMethod="POST",notes = "取关成功返回true")
    @RequestMapping(value = "/removeFollow")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doctorId", value = "医生id", dataType = "Integer"),
            @ApiImplicitParam(name = "access_token",value = "token", dataType ="String" ,required = true)
    })
    public JSONMessage removeFollow(Integer doctorId){
        Integer currentUserId = this.getCurrentUserId();
        int i = doctorFollowService.removeFollow(doctorId, currentUserId);
        return JSONMessage.success(null,i!=0);
    }

    @ApiOperation(value = "根据userId列表获取用户信息 分页",httpMethod="POST")
    @RequestMapping(value = "/getInfoPageByUserIds")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页数", dataType = "Integer", defaultValue = "0"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", dataType = "Integer",  defaultValue = "100"),
            @ApiImplicitParam(name = "userIds", value = "用户id列表", dataType = "Integer[]"),
            @ApiImplicitParam(name = "access_token",value = "token", dataType ="String" ,required = true)
    })
    public JSONMessage getInfoPageByUserIds(Integer[] userIds, Integer pageSize, Integer pageIndex){
        return JSONMessage.success(user2Service.getInfoByUserIds(!ObjectUtils.isEmpty(userIds) ? Arrays.asList(userIds) : new ArrayList<>(), pageSize,pageIndex));
    }




   /* @ApiIgnore
    @RequestMapping(value = "/create/wallet/acc/{token}", method = {RequestMethod.GET})
    public JSONMessage createWalletAcc(@PathVariable String token) {
        groupDoctor2Service.createWalletAcc(token);
        return JSONMessage.success();
    }*/
}
