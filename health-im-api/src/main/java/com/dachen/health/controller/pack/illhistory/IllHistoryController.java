package com.dachen.health.controller.pack.illhistory;

import com.dachen.commons.JSONMessage;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.pack.illhistory.service.IllHistoryInfoService;
import com.dachen.health.user.service.IRelationService;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by fuyongde on 2016/12/5.
 */
@RestController
@RequestMapping("/pack/illHistory")
public class IllHistoryController {

    @Autowired
    private IllHistoryInfoService illHistoryInfoService;

    @Autowired
    private IRelationService relationService;

    /**
     * @api {get} /pack/illHistory/addIllHistoryRecord	添加一条病程
     * @apiVersion 1.0.0
     * @apiName addIllHistoryRecord
     * @apiGroup 病历
     * @apiDescription 添加一条病程
     * @apiParam {String} 	access_token 		    token
     * @apiParam {Integer} 	doctorId                医生Id
     * @apiParam {String} 	illHistoryInfoId        病历id
     * @apiParam {String} 	illRecordTypeId         病程类型id
     * @apiParam {String} 	info                    备注信息
     * @apiParam {String} 	isSecret                是否允许患者查看
     * @apiParam {String} 	time                    病程时间
     * @apiParam {String[]} pics                    图片信息
     * @apiSuccess {Integer} 	resultCode			1 成功
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/addIllHistoryRecord")
    public JSONMessage addIllHistoryRecord(
            @RequestParam(name = "illHistoryInfoId") String illHistoryInfoId,
            @RequestParam(name = "doctorId", required = false) Integer doctorId,
            @RequestParam(name = "illRecordTypeId") String illRecordTypeId,
            @RequestParam(name = "illRecordTypeParentId", required = false) String illRecordTypeParentId,
            @RequestParam(name = "illRecordTypeSource") Integer illRecordTypeSource,
            @RequestParam(name = "illRecordTypeName") String illRecordTypeName,
            @RequestParam(name = "info", required = false) String info,
            @RequestParam(name = "isSecret", defaultValue = "false") Boolean isSecret,
            @RequestParam(name = "time") Long time,
            @RequestParam(name = "pics", required = false) String[] pics
    ) {
        illHistoryInfoService.addIllHistoryRecord(illHistoryInfoId, doctorId, illRecordTypeId, illRecordTypeParentId,
                illRecordTypeSource, illRecordTypeName, info, isSecret, pics, time);
        return JSONMessage.success();
    }

    /**
     * @api {get} /pack/illHistory/getIllHistoryRecordTypes	获取病程类型
     * @apiVersion 1.0.0
     * @apiName getIllHistoryRecordTypes
     * @apiGroup 病历
     * @apiDescription 获取病程类型，会根据医生和患者身份的不同返回不同的结果
     * @apiParam {String} 	access_token 		token
     * @apiParam {String} 	parentId 		    父级id（不传的话，默认为0）
     * @apiSuccess {String} 	resultCode      1 成功
     * @apiSuccess {String} 	data.all.id			id
     * @apiSuccess {String} 	data.all.name		名称
     * @apiSuccess {String} 	data.all.source		来源
     * @apiSuccess {String} 	data.all.parentId		父级id
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/getIllHistoryRecordTypes")
    public JSONMessage getIllHistoryRecordTypes(@RequestParam(name = "parentId", defaultValue = "0") String parentId) {
        return JSONMessage.success(illHistoryInfoService.getIllHistoryRecordTypes(parentId));
    }

    /**
     * @api {get} /pack/illHistory/fixIllHistoryInfos	根据历史订单修复病历信息
     * @apiVersion 1.0.0
     * @apiName fixIllHistoryInfos
     * @apiGroup 病历
     * @apiDescription 根据历史订单修复病历信息
     * @apiParam {String} 	access_token 		token
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/fixIllHistoryInfos")
    public JSONMessage fixIllHistoryInfos() {
        illHistoryInfoService.fixOldData();
        return JSONMessage.success();
    }

    /**
     * @api {get} /pack/illHistory/fixDoctorPatient	修复医患关系表
     * @apiVersion 1.0.0
     * @apiName fixIllHistoryInfos
     * @apiGroup 病历
     * @apiDescription 修复医患关系表
     * @apiParam {String} 	access_token 		token
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/fixDoctorPatient")
    public JSONMessage fixDoctorPatient() {
        illHistoryInfoService.fixDoctorPatient();
        return JSONMessage.success();
    }

    /**
     * @api {get} /pack/illHistory/getIllHistoryInfo	获取病历中的患者信息
     * @apiVersion 1.0.0
     * @apiName getIllHistoryInfo
     * @apiGroup 病历
     * @apiDescription 获取病历中的患者信息
     * @apiParam {Integer}       doctorId  医生的id
     * @apiParam {Integer}       patientId 患者的id
     * @apiParam {String}        illHistoryInfoId 病历id
     * @apiSuccess {String} 	 illHistoryInfoId      病历id
     * @apiSuccess {String} 	 briefHistroy      		简要病史
     * @apiSuccess {Object} 	 patient      患者
     * @apiSuccess {String} 	 patient.ageStr      患者年龄
     * @apiSuccess {String} 	 patient.area      患者地区
     * @apiSuccess {Long} 	     patient.birthday      生日
     * @apiSuccess {String} 	 patient.headPicFileName   头像
     * @apiSuccess {String} 	 patient.name      姓名
     * @apiSuccess {Integer} 	 patient.patientId     患者id
     * @apiSuccess {String} 	 patient.phone      患者电话
     * @apiSuccess {String} 	 patient.remark      患者备注
     * @apiSuccess {String} 	 patient.remarkName      患者备注名
     * @apiSuccess {Short} 	     patient.sex      性别（1男，2女）
     * @apiSuccess {Integer} 	 patient.userId      患者所属的用户的id
     * @apiSuccess {String[]} 	 patient.tags      患者的分组标签
     * @apiSuccess {Object} 	 illContentInfo      病情资料
     * @apiSuccess {String} 	 illContentInfo.illDesc         病情资料中的病情描述
     * @apiSuccess {String[]} 	 illContentInfo.pics         病情资料中医生上传的图片
     * @apiSuccess {String[]} 	 illContentInfo.patientPics         病情资料中患者上传的图片
     * @apiSuccess {String} 	 illContentInfo.illFertility         病情资料中的月经史
     * @apiSuccess {String} 	 illContentInfo.illHistroy         病情资料中的既往史
     * @apiSuccess {String} 	 illContentInfo.illHome         病情资料中的家庭史
     * @apiSuccess {String} 	 illContentInfo.illMoment         病情资料中的现病史
     * @apiSuccess {String} 	 illContentInfotreatment         病情资料中的诊治情况
     * @apiSuccess {List} 	     diagnoses      初步诊断
     * @apiSuccess {String} 	 DiagnosisVO.time     初步诊断时间
     * @apiSuccess {String} 	 DiagnosisVO.diseaseName     初步诊断疾病名称
     * @apiSuccess {String} 	 DiagnosisVO.content     初步诊断信息
     * @apiSuccess {Integer} 	 DiagnosisVO.userType     初步诊断用户类型1患者3医生
     * @apiSuccess {String} 	 DiagnosisVO.userName     初步诊断用户姓名
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/getIllHistoryInfo")
    public JSONMessage getIllHistoryInfo(
            @RequestParam(name = "doctorId") Integer doctorId,
            @RequestParam(name = "patientId") Integer patientId,
            @RequestParam(name = "illHistoryInfoId", required = false) String illHistoryInfoId
    ) {
        return JSONMessage.success(illHistoryInfoService.getillHistoryInfo(doctorId, patientId, illHistoryInfoId));
    }

    /**
     * @api {get} /pack/illHistory/getIllHistroyRecords	获取病程信息
     * @apiVersion 1.0.0
     * @apiName getIllHistroyRecords
     * @apiGroup 病历
     * @apiDescription 获取病程信息
     * @apiParam {Integer}       doctorId  医生的id
     * @apiParam {Integer}       patientId 患者的id
     * @apiParam {String}        illHistoryInfoId 病历id
     * @apiParam {Integer}       pageIndex 页码
     * @apiParam {Integer}       pageSize  页面大小
     *
     * @apiSuccess {Object} 	 data    病程信息
     * @apiSuccess {Integer} 	 data.pageData.id     病程id
     * @apiSuccess {String} 	 data.pageData.name     病程名称
     * @apiSuccess {Object} 	 data.pageData.illHistoryInfoId     病历id
     * @apiSuccess {Integer} 	 data.pageData.type     病程卡片类型（1检查项，2订单（包含图文、电话、积分、门诊），3健康关怀，4患者报道，5会诊，6用药，7手动添加的非检查项的项目）
     * @apiSuccess {Long} 	     data.pageData.createTime     该病程的创建时间
     * @apiSuccess {Long} 	     data.pageData.updateTime     该病程最后一次更新时间
     * @apiSuccess {Object} 	 data.pageData.recordCareVo   健康关怀卡片
     * @apiSuccess {String} 	 data.pageData.recordCareVo.name   健康关怀名称
     * @apiSuccess {Integer} 	 data.pageData.recordCareVo.orderId   健康关怀订单id
     * @apiSuccess {String} 	 data.pageData.recordCareVo.messageGroupId   健康关怀会话组id
     * @apiSuccess {Integer} 	 data.pageData.recordCareVo.mainDoctor   主医生
     * @apiSuccess {Integer[]} 	 data.pageData.recordCareVo.groupDoctors   医生组
     * @apiSuccess {Long} 	     data.pageData.recordCareVo.startTime   开始时间
     * @apiSuccess {String} 	 data.pageData.recordCareVo.diseaseId   所患疾病ID
     * @apiSuccess {String} 	 data.pageData.recordCareVo.disease   所患疾病
     * @apiSuccess {String} 	 data.pageData.recordCareVo.diseaseDuration   病症时长
     * @apiSuccess {String} 	 data.pageData.recordCareVo.diseaseDesc   病情描述
     * @apiSuccess {String[]} 	 data.pageData.recordCareVo.drugGoodsIds   药品id
     * @apiSuccess {Object} 	 data.pageData.recordCareVo.drugInfoVos    药品详情
     * @apiSuccess {Object} 	 data.pageData.recordCareVo.drugInfoVos.title    药品标题
     * @apiSuccess {Object} 	 data.pageData.recordCareVo.drugInfoVos.packSpecification    包装规格
     * @apiSuccess {Object} 	 data.pageData.recordCareVo.drugInfoVos.imageUrl    logo
     * @apiSuccess {Object} 	 data.pageData.recordCareVo.drugInfoVos.manufacturer    药厂
     * @apiSuccess {String} 	 data.pageData.recordCareVo.drugPicUrls   药瓶图片
     * @apiSuccess {String} 	 data.pageData.recordCareVo.drugCase   用药情况
     * @apiSuccess {String} 	 data.pageData.recordCareVo.hopeHelp   希望得到的帮助
     * @apiSuccess {String[]} 	 data.pageData.recordCareVo.pics   图片信息
     * @apiSuccess {Boolean} 	 data.pageData.recordCareVo.isSeeDoctor   是否就诊过
     * @apiSuccess {Boolean} 	 data.pageData.recordCareVo.isPay   是否支付
     * @apiSuccess {Object} 	 data.pageData.recordCareVo.orderResult   咨询结果
     * @apiSuccess {Long} 	     data.pageData.recordCareVo.orderResult.endTime     结束时间
     * @apiSuccess {String} 	 data.pageData.recordCareVo.orderResult.result     咨询结果
     * @apiSuccess {String[]} 	 data.pageData.recordCareVo.orderResult.pics     图片信息
     * @apiSuccess {String} 	 data.pageData.recordCareVo.orderResult.checkSuggestName     检查建议
     * @apiSuccess {String} 	 data.pageData.recordCareVo.orderResult.diagnosis     初步诊断
     * @apiSuccess {String} 	 data.pageData.recordCareVo.orderResult.drugAdvise     用药建议
     * @apiSuccess {Object} 	 data.pageData.recordCareVo.careTips   关怀小节
     * @apiSuccess {Long} 	     data.pageData.recordOrderVo.careTips.endTime     结束时间
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.result     咨询结果
     * @apiSuccess {String[]} 	 data.pageData.recordOrderVo.careTips.pics     图片信息
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.checkSuggestName     检查建议
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.diagnosis     初步诊断
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseId     用药建议
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo     用药建议的对象
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.id     用药建议的id
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList     用药建议的列表
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.doseMothor 服药方法
     * @apiSuccess {Integer} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.doseDays 服药持续天数
     * @apiSuccess {Integer} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.doseQuantity 每次服药的数量
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.doseMothod 服药方法
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.doseUnit 每次服药单位
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.doseUnitName 每次服药单位名称
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.goodsId 药品id
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.goodsTitle 药品title
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.goodsTradeName 药品名称
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.goodsGeneralName 通用名称
     * @apiSuccess {Integer} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.goodsNumber 药品数量（应购数量）
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.id id
     * @apiSuccess {Integer} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.periodNum 用药周期长度
     * @apiSuccess {Integer} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.periodTimes 用药周期服药次数
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.periodUnit 用药周期单位（天、周、月）
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.recipeId recipeId
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.careTips.drugAdviseVo.recipeDetailList.patients 适用人群
     *
     * @apiSuccess {Object} 	 data.pageData.recordOrderVo     订单卡片
     * @apiSuccess {Integer} 	 data.pageData.recordOrderVo.orderId     订单id
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.messageGroupId     会话组id
     * @apiSuccess {Long} 	     data.pageData.recordOrderVo.startTime     开始时间
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.diseaseId     所患疾病ID
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.disease     所患疾病
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.diseaseDuration     病症时长
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.diseaseDesc     病情描述
     * @apiSuccess {String[]} 	 data.pageData.recordOrderVo.drugGoodsIds     药品商品id
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.drugInfoVos    药品信息
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.drugInfoVos.title    药品标题
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.drugInfoVos.packSpecification    包装规格
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.drugInfoVos.imageUrl    logo
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.drugInfoVos.manufacturer    药厂
     *
     * @apiSuccess {String[]} 	 data.pageData.recordOrderVo.drugPicUrls     用药图片资料
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.drugCase     用药情况
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.hopeHelp     希望获得医生什么帮助
     * @apiSuccess {String[]} 	 data.pageData.recordOrderVo.pics     图片信息
     * @apiSuccess {Boolean} 	 data.pageData.recordOrderVo.isSeeDoctor     是否就诊过
     * @apiSuccess {Boolean} 	 data.pageData.recordOrderVo.isPay     是否已经付款
     * @apiSuccess {Object} 	 data.pageData.recordOrderVo.orderResult     咨询结果
     * @apiSuccess {Long} 	     data.pageData.recordOrderVo.orderResult.endTime     结束时间
     * @apiSuccess {Objcet} 	 data.pageData.recordOrderVo.orderResult.result     咨询结果
     * @apiSuccess {String[]} 	 data.pageData.recordOrderVo.orderResult.pics     图片信息
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.checkSuggestName     检查建议
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.diagnosis     初步诊断
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseId     用药建议
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo     用药建议的对象
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.id     用药建议的id
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList     用药建议的列表
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.doseMothor 服药方法
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.doseQuantity 每次服药数量
     * @apiSuccess {Integer} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.doseDays 服药持续天数
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.doseMothod 服药方法
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.doseUnit 每次服药单位
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.doseUnitName 每次服药单位名称
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.goodsId 药品id
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.goodsTitle 药品title
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.goodsTradeName 药品名称
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.goodsGeneralName 通用名称
     * @apiSuccess {Integer} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.goodsNumber 药品数量（应购数量）
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.id id
     * @apiSuccess {Integer} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.periodNum 用药周期长度
     * @apiSuccess {Integer} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.periodTimes 用药周期服药次数
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.periodUnit 用药周期单位（天、周、月）
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.recipeId recipeId
     * @apiSuccess {String} 	 data.pageData.recordOrderVo.orderResult.drugAdviseVo.recipeDetailList.patients 适用人群
     *
     * @apiSuccess {Object} 	 data.pageData.recordCheckInVo                                      患者报道卡片
     * @apiSuccess {Integer} 	 data.pageData.recordCheckInVo.orderId                              订单id
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.message                              留言
     * @apiSuccess {Long} 	     data.pageData.recordCheckInVo.checkInTime                          报道时间
     * @apiSuccess {String[]} 	 data.pageData.recordCheckInVo.pics                                 图片资料
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.hospital                             就诊医院
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.illHistoryInfoNo                     病历号
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.diseaseIds                           所患疾病
     * @apiSuccess {Long} 	     data.pageData.recordCheckInVo.lastTime                             最后就诊时间
     * @apiSuccess {Integer} 	 data.pageData.recordCheckInVo.orderType                            订单类型
     *
     * @apiSuccess {Object} 	 data.pageData.recordConsultiationVo                                会诊卡片
     * @apiSuccess {Integer} 	 data.pageData.recordConsultiationVo.orderId                        订单id
     * @apiSuccess {Integer} 	 data.pageData.recordConsultiationVo.consultationDoctor             会诊医生
     * @apiSuccess {String} 	 data.pageData.recordConsultiationVo.consultationDoctorName         会诊医生姓名
     * @apiSuccess {Integer} 	 data.pageData.recordConsultiationVo.mainDoctor                     主诊医生
     * @apiSuccess {String} 	 data.pageData.recordConsultiationVo.mainDoctorName                 主诊医生姓名
     * @apiSuccess {Long} 	     data.pageData.recordConsultiationVo.startTime                      开始时间
     * @apiSuccess {String[]} 	 data.pageData.recordConsultiationVo.pics                           图片信息
     * @apiSuccess {Long} 	     data.pageData.recordConsultiationVo.endTime                        结束时间
     * @apiSuccess {Boolean} 	 data.pageData.recordConsultiationVo.isPay                          是否支付
     * @apiSuccess {String} 	 data.pageData.recordConsultiationVo.messageGroupId                 会话组id
     *
     * @apiSuccess {Object} 	 data.pageData.recordCheckItemVo                                    检查项卡片
     * @apiSuccess {String} 	 data.pageData.recordCheckItemVo.checkItemId                        检查项的id
     * @apiSuccess {Integer} 	 data.pageData.recordCheckItemVo.creater                            创建人
     * @apiSuccess {String} 	 data.pageData.recordCheckItemVo.createrName                        创建人姓名
     * @apiSuccess {Integer} 	 data.pageData.recordCheckItemVo.createrType                        创建类型（1患者，2医生助手，3医生）
     * @apiSuccess {String[]} 	 data.pageData.recordCheckItemVo.pics                               检查项的图片信息
     * @apiSuccess {Integer} 	 data.pageData.recordCheckItemVo.info                               检查项填写的备注信息
     * @apiSuccess {Object} 	 data.pageData.recordCheckItemVo.isSecret                           患者是否允许查看
     * @apiSuccess {Object} 	 data.pageData.recordCheckItemVo.time                               检查时间
     *
     * @apiSuccess {Object} 	 data.pageData.recordNormalVo                                       手动添加的卡片
     * @apiSuccess {String} 	 data.pageData.recordNormalVo.recordType                            recordTypeId
     * @apiSuccess {String} 	 data.pageData.recordNormalVo.recordName                            名称
     * @apiSuccess {Long} 	     data.pageData.recordNormalVo.time                                  时间
     * @apiSuccess {String[]} 	 data.pageData.recordNormalVo.pics                                  图片信息
     * @apiSuccess {String} 	 data.pageData.recordNormalVo.info                                  病程信息
     * @apiSuccess {Boolean} 	 data.pageData.recordNormalVo.isSecret                              是否允许患者查看
     * @apiSuccess {Integer} 	 data.pageData.recordNormalVo.creater                               添加人
     * @apiSuccess {String} 	 data.pageData.recordNormalVo.createrName                           创建人姓名
     * @apiSuccess {Integer} 	 data.pageData.recordNormalVo.createrType                           创建类型（1患者，2医生助手，3医生）
     *
     * @apiSuccess {Object} 	 data.pageData.recordDrugVo     用药卡片
     * @apiSuccess {String} 	 data.pageData.recordDrugVo.drugCase  用药信息
     * @apiSuccess {String[]} 	 data.pageData.recordDrugVo.pics    图片
     * @apiSuccess {Object} 	 data.pageData.recordDrugVo.drugInfos  药品信息
     * @apiSuccess {String} 	 data.pageData.recordDrugVo.drugInfos.drugName 药品名称
     * @apiSuccess {String} 	 data.pageData.recordDrugVo.drugInfos.specification 规格
     * @apiSuccess {String} 	 data.pageData.recordDrugVo.drugInfos.packSpecification 包装规格
     * @apiSuccess {String} 	 data.pageData.recordDrugVo.drugInfos.drugImageUrl 药品图片链接
     * @apiSuccess {String} 	 data.pageData.recordDrugVo.drugInfos.manufacturer 生产厂家
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.orderId 订单id
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.message 报道留言
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.checkInTime 报道时间
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.pics 图片信息
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.hospital 医院
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.illHistoryInfoNo 病历号
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.diseaseIds 所患疾病的id
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.diseases 所患疾病的id
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.lastTime 最后就诊时间
     * @apiSuccess {String} 	 data.pageData.recordCheckInVo.orderType 订单类型
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/getIllHistroyRecords")
    public JSONMessage getIllHistroyRecords(
            @RequestParam(name = "doctorId") Integer doctorId,
            @RequestParam(name = "patientId") Integer patientId,
            @RequestParam(name = "illHistoryInfoId", required = false) String illHistoryInfoId,
            @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) throws HttpApiException {
        return JSONMessage.success(illHistoryInfoService.getillHistoryRecords(doctorId, patientId, illHistoryInfoId, pageIndex, pageSize));
    }

    /**
     * @api {get} /pack/illHistory/editPatientInfo	编辑患者信息
     * @apiVersion 1.0.0
     * @apiName editPatientInfo
     * @apiGroup 病历
     * @apiDescription 编辑患者信息
     * @apiParam {String} illHistoryInfoId 病历id（必填）
     * @apiParam {Integer} doctorId  医生的id（必填）
     * @apiParam {Integer} patientId 患者的id（必填）
     * @apiParam {String} remarkName 备注名（非必填）
     * @apiParam {String[]}  tags 分组标签（非必填）
     * @apiParam {String} remark 备注（非必填）
     * @apiParam {String} height 身高（非必填）
     * @apiParam {String} weight 体重（非必填）
     * @apiParam {String} marriage 婚姻（非必填）
     * @apiParam {String} job 职业（非必填）
     * @apiSuccess {Integer} 	resultCode      1 成功
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/editPatientInfo")
    public JSONMessage editPatientInfo(
            @RequestParam(name = "illHistoryInfoId", required = false) String illHistoryInfoId,
            @RequestParam(name = "doctorId") Integer doctorId,
            @RequestParam(name = "patientId") Integer patientId,
            @RequestParam(name = "remarkName", required = false) String remarkName,
            @RequestParam(name = "tags", required = false) String[] tags,
            @RequestParam(name = "remark", required = false) String remark,
            @RequestParam(name = "height", required = false) String height,
            @RequestParam(name = "weight", required = false) String weight,
            @RequestParam(name = "marriage", required = false) String marriage,
            @RequestParam(name = "job", required = false) String job
    ) {
        illHistoryInfoService.editPationInfo(illHistoryInfoId, doctorId, patientId, remarkName, tags, remark, height, weight, marriage, job);
        return JSONMessage.success();
    }

    /**
     * @api {get} /pack/illHistory/addDiagnosis	为病历添加初步诊断信息
     * @apiVersion 1.0.0
     * @apiName addDiagnosis
     * @apiGroup 病历
     * @apiDescription 为病历添加初步诊断信息
     * @apiParam {String} illHistoryInfoId 病历Id
     * @apiParam {String} content 初步诊断信息
     * @apiParam {String} diseaseId 疾病id
     * @apiSuccess {Integer} 	resultCode      1 成功
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/addDiagnosis")
    public JSONMessage addDiagnosis(
            @RequestParam(name = "illHistoryInfoId") String illHistoryInfoId,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "diseaseId") String diseaseId
    ) {
        illHistoryInfoService.addDiagnosis(illHistoryInfoId, content, diseaseId);
        return JSONMessage.success();
    }
    
    /**
     * @api {get} /pack/illHistory/getDiagnosis	获取初步诊断列表
     * @apiVersion 1.0.0
     * @apiName getDiagnosis
     * @apiGroup 病历
     * @apiDescription 获取初步诊断列表
     * @apiParam {String} 			illHistoryInfoId 	病历Id
     * @apiParam {Integer} 		pageIndex 			页码
     * @apiParam {Integer} 		pageSize 			页容量
     * 
     * @apiSuccess {Long} 			time      			创建时间
     * @apiSuccess {String} 		content      		内容
     * @apiSuccess {Integer} 		userType      		创建者类型1患者 3医生
     * @apiSuccess {String} 		userName      		创建者
     * @apiSuccess {Integer} 		resultCode      	1 成功
     * @apiAuthor liangcs
     * @date 2016年12月23日
     */
    @RequestMapping(value = "/getDiagnosis")
    public JSONMessage getDiagnosis(
    		@RequestParam(name = "illHistoryInfoId") String illHistoryInfoId,
    		@RequestParam(value="pageIndex", defaultValue="0")Integer pageIndex,
    		@RequestParam(value="pageSize", defaultValue="15")Integer pageSize) 
    {
    	return JSONMessage.success(illHistoryInfoService.getDiagnosis(illHistoryInfoId, pageIndex, pageSize));
    }

    /**
     * @api {get} /pack/illHistory/editIllContentInfo	编辑病情资料
     * @apiVersion 1.0.0
     * @apiName editIllContentInfo
     * @apiGroup 病历
     * @apiDescription 编辑病情资料
     * @apiParam {String} illHistoryInfoId 病历Id
     * @apiParam {List} illDesc 病情描述
     * @apiParam {String[]} pics 图片资料(医生和患者都用这个字段传图片，后台会根据角色的不同，存到对应的字段里)
     * @apiParam {String} treatment 诊治情况
     * @apiSuccess {Integer} 	resultCode      1 成功
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/editIllContentInfo")
    public JSONMessage editIllContentInfo(
            @RequestParam(name = "illDesc", required = false) String illDesc,
            @RequestParam(name = "pics", required = false) List<String> pics,
            @RequestParam(name = "treatment", required = false) String treatment,
            @RequestParam(name = "illHistoryInfoId") String illHistoryInfoId
    ) {
        illHistoryInfoService.editIllContentInfo(illHistoryInfoId, illDesc, pics, treatment);
        return JSONMessage.success();
    }

    /**
     * @api {get} /pack/illHistory/addDrugCase	病程中添加用药信息
     * @apiVersion 1.0.0
     * @apiName addDrugCase
     * @apiGroup 病历
     * @apiDescription 病程中添加用药信息
     * @apiParam {String} illHistoryInfoId 病历Id
     * @apiParam {String} drugInfos 用药信息（Json格式的字符串：[
     * {
     * "drugId" : "577e178ab522257e681a0d15",
     * "drugCount":1,
     * "drugName": "云南白药",
     * "specification": "10g",
     * "packSpecification": "1瓶",
     * "drugImageUrl": "http://xxx.xxx.com/ynby.jpg",
     * "manufacturer": "云南制药厂"
     * },
     * {
     * "drugId" : "577e178ab522257e681a0d15",
     * "drugCount":1,
     * "drugName": "云南黑药",
     * "specification": "10g",
     * "packSpecification": "1瓶",
     * "drugImageUrl": "http://xxx.xxx.com/ynhy.jpg",
     * "manufacturer": "云南制药厂"
     * }
     * ]）
     * @apiParam {String[]} pics 图片信息
     * @apiParam {Long} time 用药时间
     * @apiParam {String} drugInfos 用药情况
     * @apiSuccess {Integer} 	resultCode      1 成功
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/addDrugCase")
    public JSONMessage addDrugCase(
            @RequestParam(name = "drugInfos", required = false) String drugInfos,
            @RequestParam(name = "pics", required = false) List<String> pics,
            @RequestParam(name = "drugCase", required = false) String drugCase,
            @RequestParam(name = "time", required = false) Long time,
            @RequestParam(name = "illHistoryInfoId") String illHistoryInfoId
    ) {
        illHistoryInfoService.addDrugCase(illHistoryInfoId, drugInfos, pics, drugCase, time);
        return JSONMessage.success();
    }

    /**
     * @api {get} /pack/illHistory/addDrugCaseAndSendMsg	病程中添加用药信息并向会话组发送消息
     * @apiVersion 1.0.0
     * @apiName addDrugCaseAndSendMsg
     * @apiGroup 病历
     * @apiDescription 病程中添加用药信息
     * @apiParam {String} orderId 订单id
     * @apiParam {String} drugInfos 用药信息（Json格式的字符串：[
     * {
     * "drugId" : "577e178ab522257e681a0d15",
     * "drugCount":1,
     * "drugName": "云南白药",
     * "specification": "10g",
     * "packSpecification": "1瓶",
     * "drugImageUrl": "http://xxx.xxx.com/ynby.jpg",
     * "manufacturer": "云南制药厂"
     * },
     * {
     * "drugId" : "577e178ab522257e681a0d15",
     * "drugCount":1,
     * "drugName": "云南黑药",
     * "specification": "10g",
     * "packSpecification": "1瓶",
     * "drugImageUrl": "http://xxx.xxx.com/ynhy.jpg",
     * "manufacturer": "云南制药厂"
     * }
     * ]）
     * @apiParam {String[]} pics 图片信息
     * @apiParam {String} drugCase 用药情况
     * @apiParam {String} gid   会话组id
     * @apiSuccess {Integer} 	resultCode      1 成功
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/addDrugCaseAndSendMsg")
    public JSONMessage addDrugCaseAndSendMsg(
            @RequestParam(name = "drugInfos", required = false) String drugInfos,
            @RequestParam(name = "pics", required = false) List<String> pics,
            @RequestParam(name = "drugCase", required = false) String drugCase,
            @RequestParam(name = "orderId") Integer orderId,
            @RequestParam(name = "gid") String gid
    ) throws HttpApiException {
        illHistoryInfoService.addDrugCaseAndSendMsg(drugInfos, pics, drugCase, orderId, gid);
        return JSONMessage.success();
    }

    /**
     * @api {get} /pack/illHistory/sendCheckItem	医生发送检查单
     * @apiVersion 1.0.0
     * @apiName sendCheckItem
     * @apiGroup 病历
     * @apiDescription 病程中添加用药信息
     * @apiParam {String} gid 会话id
     * @apiParam {Integer} orderId 订单id
     * @apiParam {String} checkupId 检查项id
     * @apiParam {String} checkupName 检查项名称
     * @apiParam {String[]} indicatorIds   指标的id
     * @apiParam {Long} suggestCheckTime   建议检查时间
     * @apiParam {String} attention   注意事项
     * @apiSuccess {Integer} 	resultCode      1 成功
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/sendCheckItem")
    public JSONMessage sendCheckItem(
            @RequestParam(name = "gid") String gid,
            @RequestParam(name = "orderId") Integer orderId,
            @RequestParam(name = "checkupId") String checkupId,
            @RequestParam(name = "checkupName") String checkupName,
            @RequestParam(name = "indicatorIds") String[] indicatorIds,
            @RequestParam(name = "suggestCheckTime") Long suggestCheckTime,
            @RequestParam(name = "attention") String attention
    ) throws HttpApiException {
        //医生端发送检查单 要存储对应的指标，以及调用肖伟的接口发送代办事项
        illHistoryInfoService.sendCheckItem(gid, orderId, checkupId, checkupName, indicatorIds, suggestCheckTime, attention);
        return JSONMessage.success();
    }

    /**
     * @api {get} /pack/illHistory/addCheckItem	患者端点击IM卡片上传检查项
     * @apiVersion 1.0.0
     * @apiName addCheckItem
     * @apiGroup 病历
     * @apiDescription 病程中添加用药信息
     * @apiParam {String} gid 会话id
     * @apiParam {Integer} orderId 订单id
     * @apiParam {String} checkupId 检查项id
     * @apiParam {String} checkupName 检查项名称
     * @apiParam {Long} checkTime   检查时间
     * @apiParam {String} result 检查结果
     * @apiParam {String[]} pics 图片信息
     * @apiParam {String} checkItemId   检查单id
     * @apiSuccess {Integer} 	resultCode      1 成功
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/addCheckItem")
    public JSONMessage addCheckItem(
            @RequestParam(name = "gid", required = false) String gid,
            @RequestParam(name = "orderId") Integer orderId,
            @RequestParam(name = "checkupId") String checkupId,
            @RequestParam(name = "checkupName") String checkupName,
            @RequestParam(name = "checkTime", required = false) Long checkTime,
            @RequestParam(name = "result", required = false) String result,
            @RequestParam(name = "pics", required = false) String[] pics,
            @RequestParam(name = "checkItemId") String checkItemId
    ) throws HttpApiException {
        //患者端填写检查内容，插入病程，支持从im卡片和im右上角两个入口
        illHistoryInfoService.addCheckItem(gid, orderId, checkupId, checkupName, checkTime, result, pics, checkItemId);
        return JSONMessage.success();
    }

    /**
     * @api {get} /pack/illHistory/addCheckItemBySelf	患者端自己在IM右上角上传检查项
     * @apiVersion 1.0.0
     * @apiName addCheckItemBySelf
     * @apiGroup 病历
     * @apiDescription 患者端自己在IM右上角上传检查项
     * @apiParam {String} gid   会话组id
     * @apiParam {Integer} orderId   订单id
     * @apiParam {String} checkupId 检查项id
     * @apiParam {String} checkupName 检查项名称
     * @apiParam {Long} checkTime   检查时间
     * @apiParam {String} result 检查结果
     * @apiParam {String[]} pics 图片信息
     * @apiSuccess {Integer} 	resultCode      1 成功
     * @apiAuthor 傅永德
     * @date 2016年12月5日
     */
    @RequestMapping(value = "/addCheckItemBySelf")
    public JSONMessage addCheckItemBySelf(
            @RequestParam(name = "gid", required = false) String gid,
            @RequestParam(name = "orderId") Integer orderId,
            @RequestParam(name = "checkupId") String checkupId,
            @RequestParam(name = "checkupName") String checkupName,
            @RequestParam(name = "checkTime", required = false) Long checkTime,
            @RequestParam(name = "result", required = false) String result,
            @RequestParam(name = "pics", required = false) String[] pics
    ) throws HttpApiException {
        //患者端填写检查内容，插入病程，支持从im卡片和im右上角两个入口
        illHistoryInfoService.addCheckItemBySelf(gid, orderId, checkupId, checkupName, checkTime, result, pics);
        return JSONMessage.success();
    }
    
    /**
     * @api {get} /pack/illHistory/updateBriefHistroy	更新简要病史
     * @apiVersion 1.0.0
     * @apiName updateBriefHistroy
     * @apiGroup 病历
     * @apiDescription 更新简要病史
     * @apiParam {String} id   病历id
     * @apiParam {String} content  内容
     * 
     * @apiSuccess {Integer} 	resultCode      1 成功
     * @apiAuthor liangcs
     * @date 2016年12月22日
     */
    @RequestMapping(value = "updateBriefHistroy")
    public JSONMessage updateBriefHistroy(String id, String content) {
    	illHistoryInfoService.updateBriefHistroy(id, content);
    	return JSONMessage.success();
    }

    /**
     * @api {get} /pack/illHistory/analysisIllHistoryInfo	病历分析
     * @apiVersion 1.0.0
     * @apiName analysisIllHistoryInfo
     * @apiGroup 病历
     * @apiDescription 病历分析
     * @apiParam {String} illHistoryInfoId   病历id
     *
     * @apiSuccess {Integer} 	resultCode      1成功，0失败
     * @apiSuccess {Object} 	data
     * @apiSuccess {Boolean} 	data.display    是否显示（true：显示。false：不显示，此时不返回url字段）
     * @apiSuccess {Boolean} 	data.url        病历分析地址
     * @apiSuccess {Boolean} 	data.infoUrl    病历分析说明页面
     * @apiAuthor 傅永德
     * @date 2017年1月11日
     */
    @RequestMapping(value = "analysisIllHistoryInfo")
    public JSONMessage analysisIllHistoryInfo(String illHistoryInfoId) {
        return JSONMessage.success(illHistoryInfoService.analysisIllHistoryInfo(illHistoryInfoId));
    }

    @Autowired
    IMsgService iMsgService;

    @RequestMapping(value = "test")
    public JSONMessage test() throws HttpApiException {
        GroupInfoRequestMessage requestMessage = new GroupInfoRequestMessage();
        requestMessage.setGid("e3827b3680a345a69a153194bc7ba20e");
        requestMessage.setUserId("123132");
        return JSONMessage.success(iMsgService.getGroupInfo(requestMessage));
    }

}
