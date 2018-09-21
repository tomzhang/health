package com.dachen.health.controller.user;

import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.model.vo.PatientVo;
import com.dachen.util.BeanUtil;
import com.dachen.util.Json;
import org.apache.xmlbeans.impl.regex.REUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.RelationType;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.user.entity.param.TagParam;
import com.dachen.health.user.service.IRelationService;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired 
    private IRelationService relationService;
    
    @Autowired
    IPatientService patientService;

    
    /**
     * @api {get} /patient/getDoctors 患者获取医生
     * @apiVersion 1.0.0
     * @apiName getDoctors
     * @apiGroup 患者
     * @apiDescription 患者获取我的医生
     *
     * @apiParam  {String} access_token token
     *
     * @apiSuccess {Object[]} users                     用户列表
     * @apiSuccess {String}   users.userId              用户id
     * @apiSuccess {String}   users.name                用户姓名
     * @apiSuccess {String}   users.telephone           手机
     * @apiSuccess {Number}   users.sex                 性别
     * @apiSuccess {String}   users.headPicFileName     头像
     * @apiSuccess {String}   users.hospital            医院
     * @apiSuccess {String}   users.departments         科室
     * @apiSuccess {String}   users.title               职称
     * @apiSuccess {String}   users.groupId             所在集团id
     * @apiSuccess {String}   users.groupName           所在集团名称
     * @apiSuccess {Object[]} users.setting                   设置
     * @apiSuccess {Number}   users.setting.defriend          拉黑  1:否，2:是
     * @apiSuccess {Number}   users.setting.topNews           消息置顶 1:否，2：是
     * @apiSuccess {Number}   users.setting.messageMasking    消息屏蔽 1：否，2：是
     * @apiSuccess {Number}   users.setting.collection        收藏 1：否，2：是
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getDoctors")
    public JSONMessage getDoctors() {
        return JSONMessage.success(null, relationService.getRelations(RelationType.doctorPatient, ReqUtil.instance.getUserId()));
    }

    /**
     * @api {get} /patient/getFriends 患者获取好友
     * @apiVersion 1.0.0
     * @apiName getFriends
     * @apiGroup 患者
     * @apiDescription 患者获取好友
     *
     * @apiParam  {String} access_token token
     *
     * @apiSuccess {Object[]} users                     用户列表
     * @apiSuccess {String}   users.userId              用户id
     * @apiSuccess {String}   users.name                用户姓名
     * @apiSuccess {String}   users.telephone           用户姓名
     * @apiSuccess {Number}   users.sex                 用户姓名
     * @apiSuccess {String}   users.headPicFileName     用户姓名
     * @apiSuccess {Object[]} users.setting                   设置
     * @apiSuccess {Number}   users.setting.defriend          拉黑  1:否，2:是
     * @apiSuccess {Number}   users.setting.topNews           消息置顶 1:否，2：是
     * @apiSuccess {Number}   users.setting.messageMasking    消息屏蔽 1：否，2：是
     * @apiSuccess {Number}   users.setting.collection        收藏 1：否，2：是
     * @apiSuccess {Object[]} tags                      标签分类
     * @apiSuccess {Number}   tags.tagName              标签名
     * @apiSuccess {Number}   tags.count                会员数量
     * @apiSuccess {Number[]} tags.userIds              会员id数组
     * @apiSuccess {Map}      collection                收藏
     * @apiSuccess {String}   collection.tagName        个人收藏
     * @apiSuccess {Number[]} collection.userIds        会员id数组
     * @apiSuccess {Object[]} without                   无标签
     * @apiSuccess {Number[]} without.userIds           会员id数组
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getFriends")
    public JSONMessage getFriends() {
        return JSONMessage.success(null, relationService.getRelationAndTag(RelationType.patientFriend, ReqUtil.instance.getUserId()));
    }


    /**
     * @api {get} /patient/getFriendTags 患者获取标签（好友）
     * @apiVersion 1.0.0
     * @apiName getFriendTags
     * @apiGroup 患者
     * @apiDescription 患者获取添加的标签（好友）
     *
     * @apiParam  {String} access_token token
     *
     * @apiSuccess {String[]} data  标签数组          
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getFriendTags")
    public JSONMessage getFriendTags() {
        return JSONMessage.success(null, relationService.getTag(ReqUtil.instance.getUserId(), UserEnum.TagType.patientFriend));
    }

    /**
     * @api {post} /patient/addFriendTag 患者设置标签（好友）
     * @apiVersion 1.0.0
     * @apiName addFriendTag
     * @apiGroup 患者
     * @apiDescription 患者设置标签（好友），只有oldName为空时为添加，不为空时修改；先设置标签，然后加入或减少好友
     *
     * @apiParam  {String} access_token     token
     * @apiParam  {String} tagName          标签名
     * @apiParam  {String} oldName          修改标签时原始名称
     * @apiParam  {Number[]} userIds        用户id数组
     *
     * @apiSuccess {Number=1} resultCode    返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/addFriendTag")
    public JSONMessage addFriendTag(TagParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setTagType(UserEnum.TagType.patientFriend.getIndex());
        relationService.addTag(param);
        return JSONMessage.success();
    }

    /**
     * @api {get} /patient/updateFriendTag 患者修改好友标签
     * @apiVersion 1.0.0
     * @apiName updateFriendTag
     * @apiGroup 患者
     * @apiDescription 患者修改某个好友标签，可选择所有添加的好友标签
     *
     * @apiParam   {String}   access_token     token
     * @apiParam   {String}   id               好友id
     * @apiParam   {String[]} tagNames         标签数组
     *
     * @apiSuccess {Number=1} resultCode       返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/updateFriendTag")
    public JSONMessage updateFriendTag(TagParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setTagType(UserEnum.TagType.patientFriend.getIndex());
        relationService.updateUserTag(param);
        return JSONMessage.success();
    }

    /**
     * @api {get} /patient/deleteFriendTag 患者删除标签（好友）
     * @apiVersion 1.0.0
     * @apiName deleteFriendTag
     * @apiGroup 患者
     * @apiDescription 患者删除标签（好友），删除标签及标签下好友
     *
     * @apiParam   {String}   access_token     token
     * @apiParam   {String}   tagName          标签名称
     *
     * @apiSuccess {Number=1} resultCode       返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/deleteFriendTag")
    public JSONMessage deleteFriendTag(TagParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setTagType(UserEnum.TagType.patientFriend.getIndex());
        relationService.deleteTag(param);
        return JSONMessage.success();
    }
    
    /**
     * @api {get} /patient/getPatientsByTelephone 获取患者列表
     * @apiVersion 1.0.0
     * @apiName getPatientsByTelephone
     * @apiGroup 患者
     * @apiDescription 获取患者列表
     *
     * @apiParam   {String}   access_token     token
     * @apiParam   {String}   telephone	     电话		             
     *
     * @apiSuccess {Number=1} resultCode       返回状态码
     * @apiSuccess {String} id             患者id
     * @apiSuccess {String} userName       姓名
     * @apiSuccess {String} sex 			性别		   
     * @apiSuccess {String} birthday       生日
     * @apiSuccess {String} relation       关系
     * @apiSuccess {String} area       所在地区
     * @apiSuccess {String} telephone       手机号
     * @apiSuccess {String} ageStr       年龄
     * @apiSuccess {String} topPath       头像地址
     * @apiSuccess {String} idcard       身份证号码
     * @apiSuccess {String} idtype       身份证类型  1身份证 2护照  3军官  4 台胞  5香港身份证 
     * @apiSuccess {String} height       身高
     * @apiSuccess {String} weight       体重
     *
     * @apiAuthor  wangl
     * @date 2016年6月22日18:12:29
     */
    @RequestMapping("/getPatientsByTelephone")
    public JSONMessage getPatientsByTelephone(@RequestParam(required=true)String telephone) {
        return JSONMessage.success(patientService.getPatientsByTelephone(telephone));
    }

    /**
     * @api {get} /patient/getPatientById 获取患者信息
     * @apiVersion 1.0.0
     * @apiName getPatientById
     * @apiGroup 患者
     * @apiDescription 获取患者列表
     *
     * @apiParam   {String}   access_token     token
     * @apiParam   {String}   patientId	     患者id
     *
     * @apiSuccess {Number=1} resultCode       返回状态码
     * @apiSuccess {String} id           患者id
     * @apiSuccess {String} userName           姓名
     * @apiSuccess {String} sex           性别
     * @apiSuccess {String} birthday           生日
     * @apiSuccess {String} relation           关系
     * @apiSuccess {String} area           所在地区
     * @apiSuccess {String} userId           所属用户id
     * @apiSuccess {String} telephone        手机号
     * @apiSuccess {String} ageStr          年龄
     * @apiSuccess {String} topPath       头像地址
     * @apiSuccess {String} idcard           身份证号码
     * @apiSuccess {String} idtype           身份证类型  1身份证 2护照  3军官  4 台胞  5香港身份证
     * @apiSuccess {String} height           身高
     * @apiSuccess {String} weight           体重
     * @apiSuccess {String} marriage         婚姻
     * @apiSuccess {String} professional       职业
     * @apiSuccess {String} checkInStatus          报道状态
     *
     * @apiAuthor  wangl
     * @date 2016年6月22日18:12:29
     */
    @RequestMapping("/getPatientById")
    public JSONMessage getPatientById(Integer patientId) {
        Patient patient = patientService.findByPk(patientId);
        PatientVo patientVo = BeanUtil.copy(patient, PatientVo.class);
        patientVo.setAgeStr(patient.getAgeStr());
        return JSONMessage.success(patientVo);
    }
    
}
