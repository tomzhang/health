package com.dachen.health.controller.pack.consultation;

import java.util.Map;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.consult.Service.ElectronicIllCaseService;
import com.dachen.health.pack.consult.entity.po.IllCasePatientInfo;
import com.dachen.health.pack.consult.entity.po.IllCaseTypeContent;
import com.dachen.health.pack.consult.entity.vo.ConsultationEnum;

@RestController
@RequestMapping("/illcase")
public class ElectronicIllCaseController {

	@Autowired
	ElectronicIllCaseService electronicIllCaseService;
	
	/**
	 * 
	 * @api {get/post} /illcase/getIllCasePatient 获取电子病历中患者的信息
	 * @apiVersion 1.0.0
	 * @apiName getIllCasePatient
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：从电子病历详情页面点击获取患者信息
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			illCaseInfoId 			电子病历id(从主诉点进去之后查询患者详情使用)
	 * @apiParam {String} 			patientId 				患者id(用户的患者列表页面使用)
	 * 
     * @apiSuccess {Integer}         illCaseInfoId          电子病历id
     * @apiSuccess {String}          patientName            患者名字
     * @apiSuccess {Short}            sex                   性别（1男，2女，3保密）
     * @apiSuccess {Integer}            age                  年龄 
     * @apiSuccess {Integer}            height             身高  
     * @apiSuccess {Integer}            weight             体重
     * @apiSuccess {String}             area               地址
     * @apiSuccess {String}            telephone          联系电话  
     * @apiSuccess {Boolean}            isMarried          是否结婚  （true:已婚，false：未婚）
     * @apiSuccess {Integer}            job                工作
     * @apiAuthor  wangl
     * @date 2016年1月11日
	 */
	@RequestMapping(value= "getIllCasePatient")
	public JSONMessage getIllCasePatient(String illCaseInfoId , Integer patientId){
		return JSONMessage.success(electronicIllCaseService.getIllCasePatient(illCaseInfoId,patientId));
	}
	
	
	/**
	 * 
	 * @api {post} /illcase/updateIllCasePatient 修改电子病历的患者信息
	 * @apiVersion 1.0.0
	 * @apiName updateIllCasePatient
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：在电子病历患者详情页面点击保存按钮（全量更新）
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			illCaseInfoId 			电子病历Id(必传)
     * @apiParam {String}           patientName            患者名字
     * @apiParam {Integer}          sex                    性别（1男，2女，3保密）
     * @apiParam {Integer}          age                年龄 
     * @apiParam {Integer}          height             身高 
     * @apiParam {Integer}          weight             体重
     * @apiParam {String}           area               地址
     * @apiParam {String}           telephone          联系电话  
     * @apiParam {String}           job                工作
     * @apiParam {Boolean}          isMarried          是否结婚  （true:已婚，false：未婚）
     * 
     * @apiSuccess {Integer}         illCaseInfoId          电子病历id
     * @apiSuccess {String}          patientName            患者名字
     * @apiSuccess {Short}            sex                   性别（1男，2女，3保密）
     * @apiSuccess {Integer}            age                  年龄 
     * @apiSuccess {Integer}            height             身高  
     * @apiSuccess {Integer}            weight             体重
     * @apiSuccess {String}             area               地址
     * @apiSuccess {String}            telephone          联系电话  
     * @apiSuccess {Boolean}            isMarried          是否结婚  （true:已婚，false：未婚）
     * @apiSuccess {Integer}            job                工作
     * @apiAuthor  wangl
     * @date 2016年1月11日
	 */
	@RequestMapping(value= "updateIllCasePatient",method = RequestMethod.POST)
	public JSONMessage updateIllCasePatient(IllCasePatientInfo illCasePatientInfo){
		return JSONMessage.success(null,electronicIllCaseService.updateIllCasePatient(illCasePatientInfo));
	}
	
	
	/**
	 * 
	 * @api {post} /illcase/createIllCaseInfo 添加患者病情资料
	 * @apiVersion 1.0.0
	 * @apiName createIllCaseInfo
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：添加患者病情资料
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer}           patientId           患者id(必传)
	 * @apiParam {Integer}           userId              用户id(必传)
	 * @apiParam {Integer}           doctorId            医生id(必传)
     * 
     * 
     * @apiSuccess {Object[]}          data              基础数据对象
	 * @apiSuccess {String}          data.illCaseInfoId         电子病历id
     * @apiSuccess {String}          data.illCaseTypeId         电子病历内容项id
     * @apiSuccess {Integer}         data.typeName             电子病历内容项名称
     * @apiAuthor  wangl
     * @date 2016年3月2日
	 */
	@RequestMapping(value= "createIllCaseInfo")
	public JSONMessage createIllCaseInfo(@RequestParam(required=true) Integer patientId,
										  @RequestParam(required=true) Integer userId,
										  @RequestParam(required=true) Integer doctorId
									  ){
		Map<String,Object> map = electronicIllCaseService.createAndGet(patientId,userId,doctorId,null,ConsultationEnum.IllCaseTreatType.consultation.getIndex());
		if(map != null){
			return JSONMessage.success(map.get("dataList"));
		}
		return JSONMessage.success();
	}
	
	
	/**
	 * 
	 * @api {post} /illcase/toCreate 进入添加患者病情资料页面
	 * @apiVersion 1.0.0
	 * @apiName toCreate
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：进入添加患者病情资料页面
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer}           patientId           患者id(必传)
	 * @apiParam {Integer}           userId              用户id(必传)
	 * @apiParam {Integer}           doctorId            医生id(必传)
     * 
     * 
     * @apiSuccess {Object[]}       dataList              基础数据对象
	 * @apiSuccess {String}         dataList.illCaseInfoId         电子病历id
     * @apiSuccess {String}         dataList.illCaseTypeId         电子病历内容项id
     * @apiSuccess {Integer}        dataList.typeName             电子病历内容项名称
     * @apiSuccess {Object}         patient              		  患者基本信息
     * @apiSuccess {String}         patient.patientName             姓名
     * @apiSuccess {String}         patient.sex            			性别
     * @apiSuccess {String}         patient.ageStr             		年龄
     * @apiAuthor  wangl
     * @date 2016年3月26日
	 */
	@RequestMapping(value= "toCreate")
	public JSONMessage toCreate(@RequestParam(required=true) Integer patientId,
							  @RequestParam(required=true) Integer userId,
							  @RequestParam(required=true) Integer doctorId
									  ){
			return JSONMessage.success(electronicIllCaseService.createAndGet(patientId,userId,doctorId,null,ConsultationEnum.IllCaseTreatType.consultation.getIndex()));
	}
	
	
	/**
	 * 
	 * @api {post} /illcase/createOrGetInfoByOrderId 健康关怀im中进入创建或编辑电子病历页面
	 * @apiVersion 1.0.0
	 * @apiName createOrGetInfoByOrderId
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：健康关怀im中进入创建或编辑电子病历页面
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer}           orderId            医生id(必传)
     * 
     * @apiSuccess {String}          illCaseTypeList.illCaseInfoId         电子病历id
     * 
     * @apiSuccess {Object[]}       dataList              基础数据对象
	 * @apiSuccess {String}         dataList.illCaseInfoId         电子病历id
     * @apiSuccess {String}         dataList.illCaseTypeId         电子病历内容项id
     * @apiSuccess {Integer}        dataList.typeName             电子病历内容项名称
     * @apiSuccess {Object}         patient              		  患者基本信息
     * @apiSuccess {String}         patient.patientName             姓名
     * @apiSuccess {String}         patient.sex            			性别
     * @apiSuccess {String}         patient.ageStr             		年龄
     * @apiAuthor  wangl
     * @date 2016年3月2日
	 */
	@RequestMapping(value= "createOrGetInfoByOrderId")
	public JSONMessage createOrGetInfoByOrderId(@RequestParam(required=true) Integer orderId){
		return JSONMessage.success(electronicIllCaseService.createOrGetByOrderId(orderId));
	}
	
	/**
	 * 
	 * @api {post} /illcase/saveIllCaseTypeContent 修改电子病历信息 或者 新增就医资料
	 * @apiVersion 1.0.0
	 * @apiName saveIllCaseTypeContent
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：保存病历中的每一项基本资料如"就诊时间、主诉....." 或者 新增的就医资料。 注：该接口为全量保存
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String}           illCaseInfoId           电子病历id(必传)
     * @apiParam {String}           illCaseTypeId           电子病历内容类型id(必传)
     * @apiParam {Integer}          contentTxt              修改的文本值
     * @apiParam {String[]}         contentImages           上传的图片url数组 
     * 
	 * @apiSuccess {String}          illCaseInfoId         电子病历id
     * @apiSuccess {String}          illCaseTypeId         修改电子病历类型id
     * @apiSuccess {Integer}         contentTxt            修改的文本值
     * @apiSuccess {String[]}        contentImages         上传的图片url数组 
     * @apiSuccess {createTime}       createTime           创建时间
     * @apiAuthor  wangl
     * @date 2016年1月11日
	 */
	@RequestMapping(value= "saveIllCaseTypeContent",method = RequestMethod.POST)
	public JSONMessage saveIllCaseTypeContent(IllCaseTypeContent illCaseTypeContent){
		return JSONMessage.success(null,electronicIllCaseService.saveIllCaseTypeContent(illCaseTypeContent));
	}
	
	
	/**
	 * 
	 * @api {post} /illcase/getSeekIllInit 获取就医资料的类型列表
	 * @apiVersion 1.0.0
	 * @apiName saveIllCaseTypeContent
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：新增就医资料选择类型列表
	 * @apiParam {String} 			access_token 			token
     * 
	 * @apiSuccess {String}          id               电子病历内容类型id和API"/saveIllCaseTypeContent"中illCaseTypeId是同一个意思
     * @apiSuccess {String}          typeName         名称
     * @apiAuthor  wangl
     * @date 2016年1月11日
	 */
	@RequestMapping(value= "getSeekIllInit")
	public JSONMessage getSeekIllInit(){
		return JSONMessage.success(null,electronicIllCaseService.getSeekIllInit());
	}
	
	
	/**
	 * 
	 * @api {post} /illcase/isFinished 获取当前订单是否完善电子病历信息
	 * @apiVersion 1.0.0
	 * @apiName isFinished
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：获取当前订单是否完善电子病历信息
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			orderId 			    orderId
     * 
	 * @apiSuccess {boolean}         isFinished             是否完成
     * @apiSuccess {Integer}         patientId              患者id
     * @apiAuthor  wangl
     * @date 2016年1月11日
	 */
	@RequestMapping(value= "isFinished")
	public JSONMessage isFinished(@RequestParam(required=true) Integer orderId){
		return JSONMessage.success(null,electronicIllCaseService.isFinished(orderId));
	}
	
	/**
	 * 
	 * @api {post} /illcase/getIllCaseByOrderId 根据订单id获取电子病历详情
	 * @apiVersion 1.0.0
	 * @apiName getIllCaseByOrderId
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：从IM中获取电子病历
	 * 
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String}           orderId           		id(必传)
	 * 
	 * @apiSuccess {String}          orderId          		订单id
     * @apiSuccess {String}          orderNo			    订单号
     * @apiSuccess {String}          illCaseInfoId          病历id
     * @apiSuccess {String}          patientName            患者名字
     * @apiSuccess {String}          ageStr                    患者年龄 
     * @apiSuccess {Short}           sex                    患者性别
     * @apiSuccess {String}            area                    区域
     * @apiSuccess {Integer}           height                    身高
     * @apiSuccess {Integer}           weight                    体重
     * @apiSuccess {List}            baseContentList        电子病历基本信息
	 * @apiSuccess {String}          baseContentList.illCaseInfoId    电子病历id
     * @apiSuccess {String}          baseContentList.illCaseTypeId    修改电子病历类型id
     * @apiSuccess {Integer}         baseContentList.contentTxt       内容项文本值
     * @apiSuccess {String[]}        baseContentList.contentImages    上传的图片url数组 
     * @apiSuccess {String}          baseContentList.typeName         内容项名称 
     * 
     * @apiSuccess {List}          seekInfoList                     就医资料列表
     * @apiSuccess {Long}          seekInfoList.createTime              创建时间
     * @apiSuccess {String}        seekInfoList.age                 年龄 
     * @apiSuccess {Integer}       seekInfoList.type                类型（1:电子病历中手动添加的就医建议，2：诊疗记录中的建议，3、检查单中的建议）
     * 
	 * @apiSuccess {object}        seekInfoList.cureRecordAndDiseaseVo                  			诊疗记录建议数据对象和病情信息对象
	 * @apiSuccess {object}        seekInfoList.cureRecordAndDiseaseVo.cureRecord                  诊疗记录建议数据对象
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.treatAdvise 		治疗建议
	 * @apiSuccess {List} 	       seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList		                        检查建议对象数组
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList.id 			检查建议id
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList.name 		检查建议名称
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList.price 		检查建议价格
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList.remark 		检查建议备注
	 * @apiSuccess {boolean} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList.enable 		检查建议是否使用（true：使用，false：不使用）
	 * @apiSuccess {String[]} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.images 					            诊疗记录图片地址
	 * @apiSuccess {String[]} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.voices 					           诊疗记录语音地址
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.consultAdvise 			           咨询结果
	 * @apiSuccess {List}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.consultAdviseDiseaseList     病种对象数组
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.cureRecord.consultAdviseDiseaseList.id     病种id
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.cureRecord.consultAdviseDiseaseList.name   病种名称
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.cureRecord.drugAdvise   					    病种名称
	 * @apiSuccess {Object}        seekInfoList.cureRecordAndDiseaseVo.cureRecord.RecipeView     	   用药对象（具体请参考/cureRecord/findById）
	 * 
	 * @apiSuccess {object}        seekInfoList.cureRecordAndDiseaseVo.orderInfo                  订单信息
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.orderInfo.mainDoctorName             主医生
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.orderInfo.secondaryDoctorName        次医生
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.orderInfo.orderType                  订单类型
	 * @apiSuccess {Long}        seekInfoList.cureRecordAndDiseaseVo.orderInfo.beginTime                  订单信息
	 * @apiSuccess {Long}        seekInfoList.cureRecordAndDiseaseVo.orderInfo.endTime                    结束时间
	 * 
     * @apiSuccess {object}        seekInfoList.checkBillPageVo                          检查单数据对象
	 * @apiSuccess {Long}          seekInfoList.checkBillPageVo.price                    价格
	 * @apiSuccess {List}          seekInfoList.checkBillPageVo.checkItemList            检查单对象
	 * @apiSuccess {String}        seekInfoList.checkBillPageVo.checkItemList.itemName   检查项名称
	 * @apiSuccess {String}        seekInfoList.checkBillPageVo.checkItemList.results    检查结果反馈
	 * @apiSuccess {String[]}      seekInfoList.checkBillPageVo.checkItemList.imageList  图片路径数组
	 * @apiSuccess {Long}          seekInfoList.checkBillPageVo.checkItemList.createTime 创建时间
	 * 
     * @apiSuccess {object}        seekInfoList.illCaseTypeContentPageVo          手动添加的就医建议数据对象 
     * @apiSuccess {String}        seekInfoList.illCaseTypeContentPageVo.typeName            类型名称 
     * @apiSuccess {String}        seekInfoList.illCaseTypeContentPageVo.contentTxt          类型内容 
     * @apiSuccess {String[]}      seekInfoList.illCaseTypeContentPageVo.contentImages       图片URL数组 
     * 
     * @apiSuccess {object}        seekInfoList.illTransferRecord				                   转诊记录对象
     * @apiSuccess {Long}        seekInfoList.illTransferRecord.transferTime          转诊时间
     * @apiSuccess {Long}        seekInfoList.illTransferRecord.receiveTime           接诊时间
     * @apiSuccess {String}        seekInfoList.illTransferRecord.transferDoctorName         转诊医生名称
     * @apiSuccess {String}        seekInfoList.illTransferRecord.receiveDoctorName          接诊医生名称
     * 
     * @apiAuthor  wangl
     * @date 2016年3月28日
	 */
	@RequestMapping(value= "getIllCaseByOrderId")
	public JSONMessage getIllCaseByOrderId(@RequestParam(required=true) Integer orderId) throws HttpApiException {
		return JSONMessage.success(electronicIllCaseService.getIllCaseByOrderId(orderId));
	}

	/**
	 * 
	 * @api {post} /illcase/getIllRecordList 获取用户患者病情列表
	 * @apiVersion 1.0.0
	 * @apiName getIllRecordList
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景： 医生查看用户资料页面，和患者查看电子病历本
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			userId 					用户id(必传)
	 * @apiParam {Integer} 			doctorId 			    医生id（医生端传）
     * 
     * @apiSuccess {Integer}         userId          		用户id
     * @apiSuccess {String}         userName	                                   用户名称
     * @apiSuccess {String}          headPicFilleName       用户头像
     * @apiSuccess {Short}          sex                   性别
     * @apiSuccess {String}           ageStr                 年龄
     * @apiSuccess {String}            area          	 区域
     * @apiSuccess {String[]}         tags                   自定义标签
	 * 
     * @apiSuccess {Object[]}       patientIllRecordList    	患者列表
     * @apiSuccess {Integer}        patientIllRecordList.patientId     患者id
     * @apiSuccess {String}        	patientIllRecordList.patientName   患者名称
     * @apiSuccess {Short}          patientIllRecordList.sex         患者性别
     * @apiSuccess {Short}          patientIllRecordList.ageStr         患者年龄
     * 
     * @apiSuccess {Object[]}        patientIllRecordList.illRecordList         病情记录列表
     * @apiSuccess {String}          patientIllRecordList.illRecordList.illCaseInfoId         电子病历id
     * @apiSuccess {String}          patientIllRecordList.illRecordList.treatType         诊疗类型（1："图文咨询"，2："电话咨询"，3："会诊咨询"，4："门诊咨询",5:"健康关怀"）
     * @apiSuccess {String}          patientIllRecordList.illRecordList.mainCase         主诉
     * @apiSuccess {String[]}        patientIllRecordList.illRecordList.imageUrls        图片url
     * @apiSuccess {Long}          	 patientIllRecordList.illRecordList.updateTime         更新时间
     * @apiSuccess {String}          patientIllRecordList.illRecordList.doctorName     	    电子病历医生名称
     * 
     * @apiAuthor  wangl
     * @date 2016年3月1日
	 */
	@RequestMapping(value= "getIllRecordList")
	public JSONMessage getIllRecordList(@RequestParam(required=true) Integer userId,
										Integer doctorId){
		return JSONMessage.success(electronicIllCaseService.getIllRecordList(userId,doctorId));
	}
	
	/**
	 * 
	 * @api {post} /health/illcase/illRecords 我的病历夹
	 * @apiVersion 1.0.0
	 * @apiName illRecords
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景： 医生查看用户资料页面，和患者查看电子病历本
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			userId 					用户id(必传)
	 * @apiParam {Integer} 			doctorId 			    医生id（医生端传）
     * 
     * @apiSuccess {Integer}         userId          		用户id
     * @apiSuccess {String}         userName	                                   用户名称
     * @apiSuccess {String}          headPicFilleName       用户头像
     * @apiSuccess {Short}          sex                   性别
     * @apiSuccess {String}           ageStr                 年龄
     * @apiSuccess {String}            area          	 区域
     * @apiSuccess {String[]}         tags                   自定义标签
	 * 
     * @apiSuccess {Object[]}        patientIllRecordList    	患者列表
     * @apiSuccess {Integer}         patientIllRecordList.patientId     患者id
     * @apiSuccess {String}        patientIllRecordList.patientName   患者名称
     * @apiSuccess {Short}          patientIllRecordList.sex         患者性别
     * @apiSuccess {Short}          patientIllRecordList.ageStr         患者年龄
     * 
     * @apiSuccess {Object[]}          patientIllRecordList.illHistoryInfoItems         		病情记录列表
     * @apiSuccess {String}          patientIllRecordList.illHistoryInfoItems.infoId         	电子病历id
     * @apiSuccess {String}          patientIllRecordList.illHistoryInfoItems.illDesc         	病情描述
     * @apiSuccess {Long}          	 patientIllRecordList.illHistoryInfoItems.updateTime    更新时间
     * @apiSuccess {Long}          	 patientIllRecordList.illHistoryInfoItems.diseaseDuration    病症时长
     * @apiSuccess {Long}          	 patientIllRecordList.illHistoryInfoItems.diseaseName    疾病名称
     * @apiSuccess {Long}          	 patientIllRecordList.illHistoryInfoItems.doctorId    医生id
     * @apiSuccess {Long}          	 patientIllRecordList.illHistoryInfoItems.doctorName    医生名称
     * 
     * @apiAuthor  liangcs
     * @date 2016年12月15日
	 */
	@RequestMapping("illRecords")
	public JSONMessage getIllRecords(Integer userId, Integer doctorId) {
		return JSONMessage.success(electronicIllCaseService.getIllRecords(userId,doctorId));
	}


	/**
	 *
	 * @api {post} /health/illcase/getIllRecordsByPatientId 根据患者医生查找用户id
	 * @apiVersion 1.0.0
	 * @apiName getIllRecordsByPatientId
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景： 下单查找病例列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer}          patientId               患者id
	 * @apiParam {Integer}          doctorId               医生id（非必填）
	 *
	 * @apiSuccess {String}         infoId         		电子病历id
	 * @apiSuccess {String}          illDesc         	病情描述
	 * @apiSuccess {Long}          	 createTime    		创建时间
	 * @apiSuccess {Long}            updateTime         更新时间
	 * @apiSuccess {Long}          	diseaseDuration    病症时长
	 * @apiSuccess {Long}          	 diseaseName    	疾病名称
	 * @apiSuccess {Long}          	 doctorId    		医生id
	 * @apiSuccess {Long}          	 doctorName    		医生名称
	 *
	 * @apiAuthor  李明
	 * @date 2016年12月26日14:55:38
	 */
	@RequestMapping("getIllRecordsByPatientId")
	public JSONMessage getIllRecordsByPatientId(Integer userId,Integer patientId,Integer doctorId){
		return JSONMessage.success(electronicIllCaseService.getIllRecordsByPatientId(userId,patientId,doctorId));
	}

	/**
	 *
	 * @api {post} /health/illcase/getIllRecordsByDiseaseId 根据患者医生查找用户id
	 * @apiVersion 1.0.0
	 * @apiName getIllRecordsByDiseaseId
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景： 下单查找病例列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer}          patientId               患者id
	 * @apiParam {String}           diseaseId            疾病id
	 *
	 * @apiSuccess {String}         infoId         		电子病历id
	 * @apiSuccess {String}          illDesc         	病情描述
	 * @apiSuccess {Long}          	 createTime    		创建时间
	 * @apiSuccess {Long}            updateTime         更新时间
	 * @apiSuccess {Long}          	diseaseDuration    病症时长
	 * @apiSuccess {Long}          	 diseaseName    	疾病名称
	 * @apiSuccess {Long}          	 doctorId    		医生id
	 * @apiSuccess {Long}          	 doctorName    		医生名称
	 *
	 * @apiAuthor  李明
	 * @date 2016年12月26日14:55:38
	 */
	@RequestMapping("getIllRecordsByDiseaseId")
	public JSONMessage getIllRecordsByPatientIdAndDiseaseId(Integer patientId,String diseaseId){
		return JSONMessage.success(electronicIllCaseService.getIllRecordsByDiseaseId(patientId,diseaseId));
	}




	
	/**
	 * 
	 * @api {post} /illcase/getIllCaseById  查看患者病情资料详情
	 * @apiVersion 1.0.0
	 * @apiName getIllCaseById
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景： 从用户资料页面点击病情记录查看病情资料
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			illCaseInfoId 			病历id
     * 
	 * @apiSuccess {String}          orderId          		订单id
     * @apiSuccess {String}          orderNo			    订单号
     * @apiSuccess {String}          illCaseInfoId          病历id
     * @apiSuccess {String}          patientName            患者名字
     * @apiSuccess {String}          ageStr                    患者年龄 
     * @apiSuccess {Short}           sex                    患者性别
     * @apiSuccess {String}            area                    区域
     * @apiSuccess {Integer}           height                    身高
     * @apiSuccess {Integer}           weight                    体重
     * @apiSuccess {List}            baseContentList        电子病历基本信息
	 * @apiSuccess {String}          baseContentList.illCaseInfoId    电子病历id
     * @apiSuccess {String}          baseContentList.illCaseTypeId    修改电子病历类型id
     * @apiSuccess {Integer}         baseContentList.contentTxt       内容项文本值
     * @apiSuccess {String[]}        baseContentList.contentImages    上传的图片url数组 
     * @apiSuccess {String}          baseContentList.typeName         内容项名称 
     * 
     * @apiSuccess {List}          seekInfoList                     就医资料列表
     * @apiSuccess {Long}          seekInfoList.createTime              创建时间
     * @apiSuccess {String}        seekInfoList.age                 年龄 
     * @apiSuccess {Integer}       seekInfoList.type                类型（1:电子病历中手动添加的就医建议，2：诊疗记录中的建议，3、检查单中的建议）
     * 
	 * @apiSuccess {object}        seekInfoList.cureRecordAndDiseaseVo                  			诊疗记录建议数据对象和病情信息对象
	 * @apiSuccess {object}        seekInfoList.cureRecordAndDiseaseVo.cureRecord                  诊疗记录建议数据对象
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.treatAdvise 		治疗建议
	 * @apiSuccess {List} 	       seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList		                        检查建议对象数组
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList.id 			检查建议id
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList.name 		检查建议名称
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList.price 		检查建议价格
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList.remark 		检查建议备注
	 * @apiSuccess {boolean} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.checkSuggestList.enable 		检查建议是否使用（true：使用，false：不使用）
	 * @apiSuccess {String[]} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.images 					            诊疗记录图片地址
	 * @apiSuccess {String[]} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.voices 					           诊疗记录语音地址
	 * @apiSuccess {String} 	   seekInfoList.cureRecordAndDiseaseVo.cureRecord.consultAdvise 			           咨询结果
	 * @apiSuccess {List}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.consultAdviseDiseaseList     病种对象数组
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.cureRecord.consultAdviseDiseaseList.id     病种id
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.cureRecord.consultAdviseDiseaseList.name   病种名称
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.cureRecord.drugAdvise   					    病种名称
	 * @apiSuccess {Object}        seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList     	   用药对象
	 * @apiSuccess {List}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list     用药对象列表
	 * @apiSuccess {List}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.c_drug_usage_list     用药对象用法列表
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.c_drug_usage_list.patients     针对患者
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.c_drug_usage_list.quantity     用量
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.c_drug_usage_list.times     	     用药频率
	 * @apiSuccess {Object}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.c_drug_usage_list.period       用药周期对象
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.c_drug_usage_list.period.number       用药周期
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.c_drug_usage_list.period.text       用药周期(中文)
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.c_drug_usage_list.period.unit       用药周期（英文）
	 * @apiSuccess {Object}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.drug  药品对象
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.drug.id  药品id
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.drug.title 药品名     
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.general_name  通用名
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.manufacturer  描述  
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.pack_specification  20片/盒
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.requires_quantity 质量
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.title 药品名     
	 * @apiSuccess {Object}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.unit 单位
	 * @apiSuccess {Object}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.id	 单位id
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.unit.name  单位名称
	 * @apiSuccess {String}          seekInfoList.cureRecordAndDiseaseVo.cureRecord.patientDrugSuggestList.c_patient_drug_suggest_list.unit.title 单位文本
	 * 
	 * @apiSuccess {object}        seekInfoList.cureRecordAndDiseaseVo.orderInfo                  订单信息
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.orderInfo.mainDoctorName             主医生
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.orderInfo.secondaryDoctorName        次医生
	 * @apiSuccess {String}        seekInfoList.cureRecordAndDiseaseVo.orderInfo.orderType                  订单类型
	 * @apiSuccess {Long}        seekInfoList.cureRecordAndDiseaseVo.orderInfo.beginTime                  订单信息
	 * @apiSuccess {Long}        seekInfoList.cureRecordAndDiseaseVo.orderInfo.endTime                    结束时间
	 * 
     * @apiSuccess {object}        seekInfoList.checkBillPageVo                          检查单数据对象
	 * @apiSuccess {Long}          seekInfoList.checkBillPageVo.price                    价格
	 * @apiSuccess {List}          seekInfoList.checkBillPageVo.checkItemList            检查单对象
	 * @apiSuccess {String}        seekInfoList.checkBillPageVo.checkItemList.itemName   检查项名称
	 * @apiSuccess {String}        seekInfoList.checkBillPageVo.checkItemList.results    检查结果反馈
	 * @apiSuccess {String[]}      seekInfoList.checkBillPageVo.checkItemList.imageList  图片路径数组
	 * @apiSuccess {Long}          seekInfoList.checkBillPageVo.checkItemList.createTime 创建时间
	 * 
     * @apiSuccess {object}        seekInfoList.illCaseTypeContentPageVo          手动添加的就医建议数据对象 
     * @apiSuccess {String}        seekInfoList.illCaseTypeContentPageVo.typeName            类型名称 
     * @apiSuccess {String}        seekInfoList.illCaseTypeContentPageVo.contentTxt          类型内容 
     * @apiSuccess {String[]}      seekInfoList.illCaseTypeContentPageVo.contentImages       图片URL数组 
     * 
     * @apiSuccess {object}        seekInfoList.illTransferRecord				                   转诊记录对象
     * @apiSuccess {Long}        seekInfoList.illTransferRecord.transferTime          转诊时间
     * @apiSuccess {Long}        seekInfoList.illTransferRecord.receiveTime           接诊时间
     * @apiSuccess {String}        seekInfoList.illTransferRecord.transferDoctorName         转诊医生名称
     * @apiSuccess {String}        seekInfoList.illTransferRecord.receiveDoctorName          接诊医生名称
     * 
     * @apiAuthor  wangl
     * @date 2016年3月28日
	 */
	@RequestMapping(value= "getIllCaseById")
	public JSONMessage getIllCaseById(@RequestParam(required=true) String illCaseInfoId) throws HttpApiException {
		return JSONMessage.success(electronicIllCaseService.getIllCaseInfo(illCaseInfoId));
	}
	
	/**
	 * 
	 * @api {post} /illcase/getPatientIllcaseList 获取患者对应的医生列表
	 * @apiVersion 1.0.0
	 * @apiName getPatientIllcaseList
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景： 获取患者对应的医生列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			userId 			                         用户id(必传)
	 * @apiParam {Integer} 			patientId 			            患者id(必传)
	 * @apiParam {Integer} 			pageIndex 			            页数
	 * @apiParam {Integer} 			pageSize 			            条数
     * 
     * 
     * @apiSuccess {Integer}         pageCount                   总页数
	 * @apiSuccess {List}            pageData                    对象列表
	 * @apiSuccess {boolean}         illcaseInfoId             是否完成
     * @apiSuccess {Integer}         doctorId              医生Id
     * @apiSuccess {String}         doctorName             医生姓名
     * @apiSuccess {String}         mainCondition          主诉
     * @apiSuccess {Long}         updateTime               主诉填写时间
     * @apiAuthor  wangl
     * @date 2016年1月21日
	 */
	@RequestMapping(value= "getPatientIllcaseList")
	public JSONMessage getPatientIllcaseList(@RequestParam(required=true) Integer userId,
										@RequestParam(required=true) Integer patientId,
										Integer pageIndex,
										Integer pageSize){
		return JSONMessage.success(null,electronicIllCaseService.getPatientIllcaseList(userId,patientId,pageIndex,pageSize));
	}
	
	
	/**
	 * @api {post} /illcase/getIllcaseBaseContentById 根据单子病历id获取病历基础数据项
	 * @apiVersion 1.0.0
	 * @apiName getIllcaseBaseContentById
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：根据单子病历id获取病历基础数据项 ,注：treatType（1："图文咨询"，2："电话咨询"，3："会诊咨询"，4："门诊咨询",5:"健康关怀"）
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			illcaseInfoId 			 病历id(必传)
	 * 
	 * @apiSuccess {Object[]}            data                  数据对象列表
	 * @apiSuccess {String}              data.illCaseInfoId    电子病历id
     * @apiSuccess {String}              data.illCaseTypeId    修改电子病历类型id
     * @apiSuccess {Integer}             data.contentTxt       内容项文本值
     * @apiSuccess {String[]}            data.contentImages    上传的图片url数组 
     * @apiSuccess {String}              data.typeName         内容项名称
     *
	 * @return
	 */
	@RequestMapping(value= "getIllcaseBaseContentById")
	public JSONMessage getIllcaseBaseContentById(@RequestParam(required=true) String illcaseInfoId){
		Map<String,Object> map = electronicIllCaseService.getIllcaseBaseContentById(illcaseInfoId);
		if(map != null){
			return JSONMessage.success(map.get("dataList"));
		}else{
			return JSONMessage.success();
		}
	}
	
	
	/**
	 * @api {post} /illcase/getIllcaseBaseById 根据单子病历id获取病历基础数据项
	 * @apiVersion 1.0.0
	 * @apiName getIllcaseBaseById
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：更新电子病历每一项成功之后返回
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			illcaseInfoId 			 病历id(必传)
	 * 
     * @apiSuccess {Object[]}       dataList              基础数据对象
	 * @apiSuccess {String}         dataList.illCaseInfoId         电子病历id
     * @apiSuccess {String}         dataList.illCaseTypeId         电子病历内容项id
     * @apiSuccess {Integer}        dataList.typeName             电子病历内容项名称
     * @apiSuccess {Object}         patient              		  患者基本信息
     * @apiSuccess {String}         patient.patientName             姓名
     * @apiSuccess {String}         patient.sex            			性别
     * @apiSuccess {String}         patient.ageStr             		年龄
     * @apiAuthor  wangl
     * @date 2016年3月26日
	 */
	@RequestMapping(value= "getIllcaseBaseById")
	public JSONMessage getIllcaseBaseById(@RequestParam(required=true) String illcaseInfoId){
		return JSONMessage.success(electronicIllCaseService.getIllcaseBaseContentById(illcaseInfoId));
	}
	
	/**
	 * 
	 * @api {post/get} /illcase/updateIllCasetoSaved 更新电子病历到保存状态
	 * @apiVersion 1.0.0
	 * @apiName updateIllCasetoSaved
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：更新电子病历到保存状态
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			illcaseInfoId 			 电子病历id(必传)
     * 
     * @apiSuccess {object}         data                    1
     * @apiAuthor  wangl
     * @date 2016年1月21日
	 */
	@RequestMapping(value= "updateIllCasetoSaved")
	public JSONMessage updateIllCasetoSaved(@RequestParam(required=true) String illcaseInfoId){
		return JSONMessage.success(null,electronicIllCaseService.updateIllCasetoSaved(illcaseInfoId));
	}
	
	
	/**
	 * 
	 * @api {post/get} /illcase/getIllcase4CreateOrder 下订单时候获取电子病历数据
	 * @apiVersion 1.0.0
	 * @apiName getIllcase4CreateOrder
	 * @apiGroup 电子病历
	 * @apiDescription 使用场景：下订单时候获取电子病历数据
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			illcaseInfoId 			 电子病历id(必传)
     * 
     * @apiSuccess {object}         data                     1
     * @apiSuccess {String}         seeDoctorMsg             诊治情况
     * @apiSuccess {Boolean}         isSeeDoctor             是否就诊
     * @apiSuccess {String}         mainCase                 主诉
     * @apiSuccess {String[]}         imageUlrs              图片
     * @apiSuccess {String}         patientId                患者id
     * @apiSuccess {String}         patientName              患者名称
     * @apiSuccess {String}         telephone                电话
     * @apiAuthor  wangl
     * @date 2016年1月21日
	 */
	@RequestMapping(value= "getIllcase4CreateOrder")
	public JSONMessage getIllcase4CreateOrder(@RequestParam(required=true) String illcaseInfoId){
		return JSONMessage.success(electronicIllCaseService.getIllcase4CreateOrder(illcaseInfoId));
	}
	
	
	/*@RequestMapping(value= "batchSaveIllCaseTypeContent",method = RequestMethod.POST)
	public JSONMessage batchSaveIllCaseTypeContent(@RequestParam(required=true) String illCaseTypeContentString){
		return JSONMessage.success(null,electronicIllCaseService.batchSaveIllCaseTypeContent(illCaseTypeContentString));
	}*/
	
	/*@RequestMapping("/insertBaseData")
	public JSONMessage insertBaseData(){
		return JSONMessage.success(null,electronicIllCaseService.insertBaseData()); 
	}*/
	
	
	
}
