package com.dachen.health.controller.pack.order;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.UserSession;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.pack.order.entity.param.CheckInParam;
import com.dachen.health.pack.order.entity.param.CheckInParam.CheckInFrom;
import com.dachen.health.pack.order.entity.po.Case;
import com.dachen.health.pack.order.service.ICheckInService;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("/pack/checkIn")
public class CheckInController {

    @Autowired
    private ICheckInService checkInService;


    /**
     * @api {get} /pack/checkIn/add 患者报到
     * @apiVersion 1.0.0
     * @apiName add
     * @apiGroup 报到
     * @apiDescription 患者报到
     * @apiParam {String}    access_token          token
     * @apiParam {Integer}   patientId             选择的患者id
     * @apiParam {Integer}   doctorId              医生id
     * @apiParam {Integer}   phone                 手机
     * @apiParam {String[]} imageUrls             图像url
     * @apiParam {String}    hospital              就诊医院
     * @apiParam {String}    recordNum             病历号
     * @apiParam {Long}      lastCureTime          上次就诊时间,时间戳，精确到毫秒
     * @apiParam {String[]}    diseaseIds             病种ID
     * @apiParam {String}    message               留言
     * @apiSuccess {Integer}  data                  报到id
     * @apiAuthor 范鹏
     * @date 2015年9月8日
     */
    @RequestMapping("/add")
    public JSONMessage addCheckIn(CheckInParam param) throws HttpApiException {
        param.setUserId(ReqUtil.instance.getUserId());
        param.setCheckInFrom(CheckInFrom.App.getIndex());
        return JSONMessage.success(null, checkInService.add(param));
    }

    /**
     * @api {get} /pack/checkIn/addByWX 患者报到(微信端)
     * @apiVersion 1.0.0
     * @apiName addByWX
     * @apiGroup 报到
     * @apiDescription 微信端患者报到
     * @apiParam {String}    access_token          token
     * @apiParam {Integer}   patientId             选择的患者id
     * @apiParam {Integer}   doctorId              医生id
     * @apiParam {Integer}   phone                 手机
     * @apiParam {String[]}  imageUrls             图像url
     * @apiParam {String}    hospital              就诊医院
     * @apiParam {String}    recordNum             病历号
     * @apiParam {Long}      lastCureTime          上次就诊时间,时间戳，精确到毫秒
     * @apiParam {String}    diseaseIds             病种ID
     * @apiParam {String}    message               留言
     * @apiSuccess {Integer}  data                  报到id
     * @apiAuthor 范鹏
     * @date 2016年12月8日
     */
    @RequestMapping("/addByWX")
    public JSONMessage addCheckInByWX(CheckInParam param) throws HttpApiException {
        param.setCheckInFrom(CheckInFrom.WX.getIndex());
        param.setUserId(ReqUtil.instance.getUserId());
        return JSONMessage.success(null, checkInService.add(param));
    }

    /**
     * @api {[get,post]} 					/pack/checkIn/getDocRecomendDis					根据医生擅长推荐疾病
     * @apiVersion 1.0.0
     * @apiName getDocRecomendDis
     * @apiGroup 报到
     * @apiDescription 用户选择系统推荐疾病只能选第三级疾病，如果该医生设置擅长是第二级疾病系统时，把该疾病系统的第三级疾病展示出来，最多展示20个，可能为空。
     * @apiParam {String}    					access_token         		 凭证
     * @apiParam {Integer}    					doctorId       		 		医生用户ID
     * @apiSuccess {int} 							id 		病种id
     * @apiSuccess {int} 							name 						病种名称
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年9月21日
     */
    @RequestMapping("/getDocRecomendDis")
    public JSONMessage getDocRecomendDis(CheckInParam param) {
        Integer doctorId = ReqUtil.instance.getUserId();
        if (!Objects.isNull(doctorId) && Objects.isNull(param.getDoctorId())) {
            param.setDoctorId(doctorId);
        }
        return JSONMessage.success(null, checkInService.getRecommendDisease(param.getDoctorId()));
    }

    /**
     * @api {get} /pack/checkIn/getCheckInGiveStatus 获取医生报道开通服务状态
     * @apiVersion 1.0.0
     * @apiName getCheckInGiveStatus
     * @apiGroup 报到
     * @apiDescription 患者报到
     * @apiParam {String}    access_token          token
     * @apiSuccess {Integer}  data                  赠送开通状态 1：开通；0：未开通
     * @apiAuthor 张垠
     * @date 2016年15月6日
     */
    @RequestMapping("/getCheckInGiveStatus")
    public JSONMessage getCheckInGiveStatus(CheckInParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        return JSONMessage.success(null, checkInService.getCheckInStatus(param));
    }

    /**
     * @api {get} /pack/checkIn/getPatientsWithStatus 根据当前用户查询所有患者
     * @apiVersion 1.0.0
     * @apiName getPatientsWithStatus
     * @apiGroup 报到
     * @apiDescription 根据当前用户查询所有患者，并且返回其与医生是报道关系与否（本人信息排在首位）
     * @apiParam {String}    access_token          token
     * @apiParam {Integer}   doctorId              医生id
     * @apiSuccess {String} 			userName 				姓名
     * @apiSuccess {String} 			sex 					性别1男，2女 3 保密
     * @apiSuccess {Long} 				birthday 				生日（long（13））
     * @apiSuccess {String} 			relation 				关系
     * @apiSuccess {String} 			area 					所在地区
     * @apiSuccess {int} 				userId 					用户id
     * @apiSuccess {int} 				id	 					id(必填)
     * @apiSuccess {String} 			telephone 				手机号
     * @apiSuccess {String} 			age 					年龄
     * @apiSuccess {String} 			topPath 			           头像
     * @apiSuccess {String} 			idcard 			                     证件号码
     * @apiSuccess {Integer} 			idtype 			    	证件类型
     * @apiSuccess {String} 			weight 			  		体重
     * @apiSuccess {String} 			height 			                    身高
     * @apiSuccess {String} 			marriage 			          婚姻
     * @apiSuccess {String} 			professional			职业
     * @apiSuccess {Integer} 			checkInStatus			0:没有报道过；1：已报道医生未处理；2：已报道且医生已确定；3：已报道医生已取消
     * @apiAuthor 张垠
     * @date 2016年12月5日
     */
    @RequestMapping("/getPatientsWithStatus")
    public JSONMessage getPatienstWithStatus(CheckInParam param) {
        return JSONMessage.success(null, checkInService.getPatientsWithStatusByDocAndCreater(param.getDoctorId(), ReqUtil.instance.getUserId()));
    }

    /**
     * @api {get} /pack/checkIn/updateCheckInGiveStatus 更新医生报道赠送套套服务
     * @apiVersion 1.0.0
     * @apiName updateCheckInGiveStatus
     * @apiGroup 报到
     * @apiDescription 根据当前用户查询所有患者，并且返回其与医生是报道关系与否
     * @apiParam {String}    access_token          token
     * @apiParam {Integer}   status                1:开通  0：关闭
     * @apiSuccess {Boolean} 			result 				true/false
     * @apiAuthor 张垠
     * @date 2016年12月5日
     */
    @RequestMapping("/updateCheckInGiveStatus")
    public JSONMessage updateCheckInGiveStatus(Integer status) {
        return JSONMessage.success(null, checkInService.updateCheckInGiveStatus(ReqUtil.instance.getUserId(), status));
    }

    /**
     * @api {get} /pack/checkIn/list 报到列表
     * @apiVersion 1.0.0
     * @apiName list
     * @apiGroup 报到
     * @apiDescription 报到列表
     * @apiParam {String}         access_token             token
     * @apiParam {Integer}        status                   状态（1：新报道，2:忽略，3：确定），不传查所有
     * @apiParam {Integer}        pageIndex                查询页
     * @apiSuccess {String}        userName                 用户姓名
     * @apiSuccess {Integer}       checkInId                报到id
     * @apiSuccess {Long}          createTime               报到时间，时间戳，精确到毫秒
     * @apiSuccess {String}        patientName              患者姓名
     * @apiSuccess {Integer}       sex                      性别--1:男;2:女;3:保密
     * @apiSuccess {Integer}       age                      年龄
     * @apiSuccess {String}        topPath                  头像
     * @apiSuccess {String}        description              医生诊断
     * @apiSuccess {String}        message                  留言
     * @apiSuccess {Integer}       status                   状态（1：新报道，2：确定，3:忽略），不传查所有
     * @apiSuccess {Integer}       orderId                  报到生成的订单id（只有医生确认才有）
     * @apiSuccess {String}        area                     所在地区
     * @apiSuccess {String}        patientAge               患者年龄
     * @apiAuthor 范鹏
     * @date 2015年9月8日
     */
    @RequestMapping("/list")
    public JSONMessage checkInList(CheckInParam param) {
        param.setDoctorId(ReqUtil.instance.getUserId());
        return JSONMessage.success(null, checkInService.list(param));
    }

    /**
     * @api {get} /pack/checkIn/detail 报到详情
     * @apiVersion 1.0.0
     * @apiName detail
     * @apiGroup 报到
     * @apiDescription 报到详情
     * @apiParam {String}         access_token             token
     * @apiParam {Integer}        checkInId                报到id
     * @apiSuccess {String}        userName                 用户姓名
     * @apiSuccess {String}        patientName              姓名
     * @apiSuccess {Integer}       sex                      年龄
     * @apiSuccess {Long}          birthday                 出生riq
     * @apiSuccess {Integer}       age                      年龄
     * @apiSuccess {String}        phone                    手机
     * @apiSuccess {String}        message                  留言
     * @apiSuccess {String}        recordNum                病历号
     * @apiSuccess {String}        hospital                 就诊医院
     * @apiSuccess {Long}          lastCureTime             就诊时间,时间戳，精确到毫秒
     * @apiSuccess {String}        description              医生诊断
     * @apiSuccess {String[]}      imageUrls                图像路径
     * @apiSuccess {String}        area                     所在地区
     * @apiSuccess {String}        patientAge               患者年龄
     * @apiAuthor 范鹏
     * @date 2015年9月8日
     */
    @RequestMapping("/detail")
    public JSONMessage detail(CheckInParam param) {
        UserSession userSession = ReqUtil.instance.getUser();
        if (userSession.getUserType() == UserEnum.UserType.patient.getIndex()) {
            param.setUserId(ReqUtil.instance.getUserId());
        } else if (userSession.getUserType() == UserEnum.UserType.doctor.getIndex()) {
            param.setDoctorId(ReqUtil.instance.getUserId());
        }
        return JSONMessage.success(null, checkInService.detail(param));
    }

    /**
     * @api {get} /pack/checkIn/update 医生处理报到
     * @apiVersion 1.0.0
     * @apiName update
     * @apiGroup 报到
     * @apiDescription 医生处理报到, 如果确定，则生成完成的报到订单并成为好友
     * @apiParam {String}    access_token            token
     * @apiParam {Integer}   checkInId               报到id
     * @apiParam {Integer}   status                  状态(2：确认；3：忽略)
     * @apiSuccess {Integer}  orderId                 订单ID
     * @apiAuthor 范鹏
     * @date 2015年9月8日
     */
    @RequestMapping("update")
    public JSONMessage updateCheckIn(CheckInParam param) throws HttpApiException {
        param.setDoctorId(ReqUtil.instance.getUserId());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("orderId", checkInService.updateStatus(param));
        return JSONMessage.success(null, map);
    }


    /**
     * @api {get} /pack/checkIn/updateCase 修改病例信息
     * @apiVersion 1.0.0
     * @apiName updateCase
     * @apiGroup 报到
     * @apiDescription 医修改病例信息
     * @apiParam {String}    access_token           token
     * @apiParam {Integer}   id                     病例id
     * @apiParam {String}    message                病例留言
     * @apiParam {String}    description            诊断疾病描述
     * @apiParam {String[]}  caseImgs               病例图片
     * @apiAuthor 范鹏
     * @date 2015年9月8日
     */
    @RequestMapping("/updateCase")
    public JSONMessage updateCase(Case param) {
        param.setUserId(ReqUtil.instance.getUserId());
        checkInService.updateCase(param);
        return JSONMessage.success(null);
    }


    /**
     * @api {get} /pack/checkIn/getNewCheckInCount 获取新报道个数
     * @apiVersion 1.0.0
     * @apiName getNewCheckInCount
     * @apiGroup 报到
     * @apiDescription 获取新报道个数
     * @apiParam {String}    access_token            token
     * @apiParam {Integer}   doctorId                医生id
     * @apiSuccess {Integer}  checkInCount            报道个数
     * @apiAuthor 范鹏
     * @date 2015年9月8日
     */
    @RequestMapping("getNewCheckInCount")
    public JSONMessage getNewCheckInCount(Integer doctorId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("checkInCount", checkInService.getNewCheckInCount(doctorId));
        return JSONMessage.success(null, map);
    }

    /**
     * @api {get} /pack/checkIn/isCheckIn 是否已报到
     * @apiVersion 1.0.0
     * @apiName isCheckIn
     * @apiGroup 报到
     * @apiDescription 是否已报到
     * @apiParam {String}    access_token            token
     * @apiParam {Integer}   doctorId                医生id
     * @apiParam {Integer}   userId                  用户id
     * @apiSuccess {Integer}  isCheckIn               是否报到（1：已报到；0：未报到）
     * @apiAuthor 谢平
     * @date 2015年9月8日
     */
    @RequestMapping("isCheckIn")
    public JSONMessage isCheckIn(Integer doctorId, Integer userId) {
        if (userId == null) {
            userId = ReqUtil.instance.getUserId();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        boolean isCheckIn = checkInService.isCheckIn(doctorId, userId);
        map.put("isCheckIn", isCheckIn ? 1 : 0);
        return JSONMessage.success(null, map);
    }
}
