package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.service.GroupDoctor2Service;
import com.dachen.health.circle.vo.MobileDoctorVO;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.health.group.group.service.impl.GroupDoctorServiceImpl;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import com.dachen.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/m/circle/group/doctor")
public class MobileCircleGroupDoctorController extends MobileCircleBaseController {

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    /**
     * @apiIgnore deprecated
     * @api {GET} /m/circle/group/doctor/findPage 获取医生成员列表
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.findPage
     * @apiGroup 科室医生
     * @apiDescription 获取医生成员列表
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId groupId
     * @apiParam {Integer} pageIndex pageIndex
     * @apiParam {Integer} pageSize pageSize
     *
     * @apiSuccess {Object} page
     * @apiAuthor 肖伟
     * @date 2017年05月09日
     */
    @RequestMapping("/findPage")
    public JSONMessage findPage(String groupId, @RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "20") Integer pageSize) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupDoctorVO> page = groupDoctor2Service.findPage(currentUserId, groupId, pageIndex, pageSize);
        return JSONMessage.success(null, page);
    }

    /**
     * @api {GET} /m/circle/group/doctor/findList 获取医生成员列表（全部医生）
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.findList
     * @apiGroup 科室医生
     * @apiDescription 获取医生成员列表（全部医生 假分页 其实是一次返回所有数据）
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId groupId
     * @apiParam {String} kw 搜索关键字
     * @apiParam {Integer} pageIndex 页码
     * @apiParam {Integer} pageSize 页面大小
     *
     * @apiSuccess {Object} page page
     * @apiSuccess {Long} page.total total
     * @apiSuccess {Integer} page.pageIndex pageIndex
     * @apiSuccess {Integer} page.pageSize pageSize
     * @apiSuccess {Object[]} list
     * @apiSuccess {String} list.id id
     * @apiSuccess {String} list.groupId groupId
     * @apiSuccess {String} list.doctorId doctorId
     * @apiSuccess {String} list.role admin表示管理员
     * @apiSuccess {String} list.doctor 医生对象
     * @apiSuccess {String} list.doctor.name 医生名称
     * @apiSuccess {String} list.doctor.headPicUrl 医生头像
     * @apiSuccess {String} list.doctor.title 医生职称
     * @apiSuccess {String} list.doctor.intro 简介
     *
     * @apiAuthor 肖伟
     * @date 2017年05月09日
     */
    @RequestMapping("/findList")
    public JSONMessage findList(@RequestParam String groupId, String kw,@RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupDoctorVO> page = null;
        if (StringUtils.isEmpty(kw)) {
            page = groupDoctor2Service.findPage(currentUserId, groupId,pageIndex,pageSize);
        } else {
            page = groupDoctor2Service.findListPage(currentUserId, groupId, kw,pageIndex,pageSize);
        }
        return JSONMessage.success(null, page);
    }

    /**
     * @api {GET} /m/circle/group/doctor/findNoManagerList 获取非管理员的医生成员列表
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.findNoManagerList
     * @apiGroup 科室医生
     * @apiDescription 获取非管理员的医生成员列表
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId groupId
     *
     * @apiSuccess {Object[]} groupDoctorList
     *
     * @apiAuthor 肖伟
     * @date 2017年05月22日
     */
    @RequestMapping("/findNoManagerList")
    public JSONMessage findList(@RequestParam String groupId) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        List<MobileGroupDoctorVO> page = groupDoctor2Service.findNoManagerListAndVO(currentUserId, groupId);
        return JSONMessage.success(null, page);
    }

    /**
     * @api {GET} /m/circle/group/doctor/findRecList 获取推荐邀请医生列表
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.findRecDoctorList
     * @apiGroup 科室医生
     * @apiDescription 获取推荐邀请医生列表
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId groupId
     *
     * @apiSuccess {Object[]} list
     * @apiSuccess {String} list.id id
     * @apiSuccess {String} list.groupId groupId
     * @apiSuccess {String} list.doctorId doctorId
     * @apiSuccess {String} list.role root表示超级管理员，admin表示管理员
     * @apiSuccess {String} list.doctor.name 医生名称
     * @apiSuccess {String} list.doctor.headPicUrl 医生头像
     * @apiSuccess {String} list.doctor.title 医生职称
     * @apiSuccess {String} list.doctor.intro 简介
     * @apiSuccess {String} list.status 状态
     *
     * @apiAuthor 肖伟
     * @date 2017年05月18日
     */
    @RequestMapping("/findRecList")
    public JSONMessage findRecList(String groupId) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        List<MobileGroupDoctorVO> ret = groupDoctor2Service.findRecInviteList(currentUserId, groupId);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/doctor/remove 移除医生成员
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.remove
     * @apiGroup 科室医生
     * @apiDescription 移除医生成员
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupDoctorId groupDoctorId
     *
     * @apiSuccess {Boolean} boolean true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年05月10日
     */
    @RequestMapping("/remove")
    public JSONMessage remove(@RequestParam(name="groupDoctorId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupDoctor2Service.remove(currentUserId, id);
        return JSONMessage.success(null, ret);
    }


    /**
     * @api {POST} /m/circle/group/doctor/quit 退出科室
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.quit
     * @apiGroup 科室医生
     * @apiDescription 退出科室
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupDoctorId groupDoctorId
     *
     * @apiSuccess {Boolean} success true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年5月16日
     */
    @RequestMapping("/quit")
    public JSONMessage quit(@RequestParam("groupDoctorId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupDoctor2Service.quitDept(currentUserId, id);
        return JSONMessage.success(null, ret);
    }
    /**
     * @api {POST} /m/circle/group/doctor/ifFriendAndInfo 是否好友和信息
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.ifFriendAndInfo
     * @apiGroup 科室医生
     * @apiDescription 是否好友和信息
     *
     * @apiParam {String} access_token token
     * @apiParam {Integer} toUserId toUserId
     *
     * @apiSuccess {Boolean} ifFriend true表示是好友，false表示不是好友
     * @apiSuccess {String} groupId  组织id
     * @apiSuccess {String} unionId 医联体id
     *
     * @apiAuthor 李敏
     * @date 2017年6月23日17:24:08
     */
    @RequestMapping("/ifFriendAndInfo")
    public JSONMessage ifFriendAndInfo(Integer toUserId) {
        Integer currentUserId = this.getCurrentUserId();
        Map<String, Object> map = groupDoctor2Service.ifFriendAndInfo(currentUserId, toUserId);
        return JSONMessage.success(null,map);
    }
}
