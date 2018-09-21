package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.entity.GroupDoctor2;
import com.dachen.health.circle.entity.GroupUser2;
import com.dachen.health.circle.service.GroupDoctor2Service;
import com.dachen.health.circle.service.GroupUser2Service;
import com.dachen.health.circle.vo.MobileGroupUserVO;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/m/circle/group/user")
public class MobileCircleGroupUserController extends MobileCircleBaseController {

    @Autowired
    protected GroupUser2Service groupUser2Service;

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    /**
     * @api {GET} /m/circle/group/user/findPage 获取科室的管理员列表
     * @apiVersion 1.0.0
     * @apiName GroupUser.findPage
     * @apiGroup 科室管理员
     * @apiDescription 获取Group的管理员列表
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId groupId
     * @apiParam {Integer} pageIndex pageIndex
     * @apiParam {Integer} pageSize pageSize
     *
     * @apiSuccess {Object} page
     * @apiSuccess {Object[]} page.list
     * @apiSuccess {String} page.list.id id
     * @apiSuccess {String} page.list.groupUserId groupUserId
     * @apiSuccess {String} page.list.doctor
     * @apiSuccess {String} page.list.doctorId doctorId
     * @apiSuccess {String} page.list.doctor.name 医生名称
     * @apiSuccess {String} page.list.doctor.headPicUrl 医生头像
     * @apiSuccess {String} page.list.doctor.title 医生职称
     * @apiSuccess {String} page.list.doctor.intro 简介
     * @apiSuccess {Object} page.list.group 组织详情
     *
     * @apiAuthor 肖伟
     * @date 2017年05月09日
     */
    @RequestMapping("/findPage")
    public JSONMessage findPage(String groupId, @RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "20") Integer pageSize) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupUserVO> page = groupUser2Service.findPage(groupId, pageIndex, pageSize);
        return JSONMessage.success(null, page);
    }

    /**
     * @api {GET} /m/circle/group/user/findMyGroupList 获取我管理科室列表
     * @apiVersion 1.0.0
     * @apiName GroupUser.findMyGroupList
     * @apiGroup 科室管理员
     * @apiDescription 获取我管理科室列表
     *
     * @apiParam {String} access_token token
     *
     * @apiSuccess {Object[]} list
     * @apiSuccess {String} list.groupUserId groupUserId
     * @apiSuccess {String} list.groupId groupId
     * @apiSuccess {String} list.doctorId doctorId
     * @apiSuccess {String} list.doctor 医生详情
     * @apiSuccess {String} list.doctor.name 医生名称
     * @apiSuccess {String} list.doctor.headPicUrl 医生头像
     * @apiSuccess {String} list.doctor.title 医生职称
     * @apiSuccess {String} list.doctor.intro 简介
     * @apiSuccess {Object} list.group 组织详情
     *
     * @apiAuthor 肖伟
     * @date 2017年05月20日
     */
    @RequestMapping("/findMyGroupList")
    public JSONMessage findMyGroupList() throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        List<MobileGroupUserVO> page = groupUser2Service.findMyGroupList(currentUserId);
        return JSONMessage.success(null, page);
    }

    /**
     * @api {POST} /m/circle/group/user/remove 移除管理员
     * @apiVersion 1.0.0
     * @apiName GroupUser.remove
     * @apiGroup 科室管理员
     * @apiDescription 移除管理员
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupUserId groupUserId
     *
     * @apiSuccess {Boolean} success true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年05月10日
     */
    @RequestMapping("/remove")
    public JSONMessage remove(@RequestParam(name="groupUserId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupUser2Service.removeBySelf(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/user/addByGroupDoctor 添加为管理员
     * @apiVersion 1.0.0
     * @apiName GroupUser.addByGroupDoctor
     * @apiGroup 科室管理员
     * @apiDescription 从科室医生中选择一个添加为管理员
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupDoctorId groupDoctorId
     *
     * @apiSuccess {Boolean} boolean true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年05月10日
     */
    @RequestMapping("/addByGroupDoctor")
    public JSONMessage add(@RequestParam String groupDoctorId) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        GroupDoctor2 groupDoctor = groupDoctor2Service.findById(groupDoctorId);
        GroupUser2 ret = groupUser2Service.add(currentUserId, groupDoctor);
        return JSONMessage.success(null, ret);
    }
}
