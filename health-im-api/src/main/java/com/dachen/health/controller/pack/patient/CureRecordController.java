package com.dachen.health.controller.pack.patient;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.drug.api.entity.CGoodsView;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.service.DownTaskService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.pack.conference.dao.CallRecordRepository;
import com.dachen.health.pack.conference.service.ICallRecordService;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.CureRecord;
import com.dachen.health.pack.patient.service.ICallResultService;
import com.dachen.health.pack.patient.service.ICureRecordService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * ProjectName： health-im-api<br>
 * ClassName： DiseaseController<br>
 * Description： <br>
 * @author 李淼淼
 * @createTime 2015年8月12日
 * @version 1.0.0
 */
@RestController
@RequestMapping(value="/cureRecord")
public class CureRecordController extends BaseController<CureRecord, Integer> {
	
	@Resource
	private ICureRecordService service;
	
	@Resource
	private IOrderService orderService;
	
	@Resource
	private IGroupService groupService;
	
	@Resource
	private CallRecordRepository callRecordRepository;
	
	@Resource
	private DownTaskService downtaskservice;
	
	@Resource
	PatientMapper patientMapper;
	
	@Resource
	private UserManager userManager;
	
	@Resource
	private ICallResultService callResultService;
	
	@Resource
	private ICallRecordService callRecordService;
	
	public ICureRecordService getService() {
		return service;
	}

	/**
	 * 
	 * @api {[get,post]} /cureRecord/createCurrecord 咨询记录创建
	 * @apiVersion 1.0.0
	 * @apiName createCurrecord
	 * @apiGroup 咨询记录
	 * @apiDescription 咨询记录创建
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 				集团id
	 * @apiParam {Integer} 			orderId 				订单id
	 * @apiParam {Integer} 			doctorId 				医生id
	 * @apiParam {String} 			treatAdvise 			治疗建议
	 * @apiParam {String} 			submitState 			提交类型 0--保存 1--提交
	 * @apiParam {String} 			drugAdviseJson 			用药建议json字符串
	 * @apiParam {String} 			attention				检查建议Id，用逗号分隔，如：100,200,300
	 * @apiParam {String[]} 		images 					图片地址
	 * @apiParam {String[]} 		voices 					语音地址
	 * @apiParam {String} 			consultAdvise 			咨询结果
	 * @apiParam {String} 			isNeedHelp 			            是否需要导医帮忙
	 * @apiParam {String} 			submitState 			是否提交 0-是 1-否
	 * @apiParam {String} 			consultAdviseDiseases 	咨询结果病种id，用逗号分隔，如：100,200,300
	 * 
	 * @apiExample {javascript} Example usage:
	 * drugAdviseJson，如下：
	 * 
	 *  [
     *    {
     *      periodNum :     //用药周期 长度
     *      periodUnit:		//用药周期 单位（例如 日，月，周）
     *      periodTimes:    //用药周期 服药次数
     *      doseDays:        //服药持续时间
     *      doseQuantity:    //每次服药  数量
     *      doseUnit:		 //每次服药 单位（例如 粒，克，瓶）
     *      doseMothod:		 //服用方法
     *      goodsId:       //品种id
     *      goodsNumber:   //品种数量
     *      patients:		//药品使用 - 适用人群
     *    }...,
     * 
     *   ]  
     *   
	 *  例子：
	 *  [
		    {
		        "periodNum":3,
		        "periodUnit":"天",
		        "periodTimes":2,
		        "doseDays":6,
		        "doseQuantity":1,
		        "doseUnit":"包"
		        "doseMothod":"温水冲服",
		        "goodsId":"573c4e204203f30a7d7b0670",
		        "goodsNumber":2,
		        "patients":"儿童",
		    }
		]
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("createCurrecord")
	public JSONMessage create(String groupId, CureRecord intance,String images[],String voices[]) throws HttpApiException {
		if(intance.getOrderId()==null){
			throw new ServiceException(30003,"parameter [orderId] is null!");
		}
		//修改订单所属集团，排除导医操作
		if (ReqUtil.instance.getUser().getUserType() == UserType.doctor.getIndex()) {
			orderService.updateGroupId(intance.getOrderId(), groupId);
		}
		
		if (intance.getDoctorId() == null) {
			Integer userId = ReqUtil.instance.getUserId();
			intance.setDoctorId(userId);
		}
		
		int ret=service.save(intance, images, voices);
		if(ret>0){
			//如果是医生填写诊疗纪录，则需要向导医发送系统通知
			service.sendNotice(intance);
			return JSONMessage.success("咨询记录创建成功",intance);
		}else{
			return JSONMessage.failure("咨询记录创建失败");
		}
	}

	/**
	 * 
	 * @api {[get,post]} /cureRecord/updateCurrecord 咨询记录修改
	 * @apiVersion 1.0.0
	 * @apiName updateCurrecord
	 * @apiGroup 咨询记录
	 * @apiDescription 咨询记录修改
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 				集团Id
	 * @apiParam {String} 			id 						主键
	 * @apiParam {Integer} 			orderId 				订单id
	 * @apiParam {String} 			treatAdvise 			治疗建议
	 * @apiParam {String} 			drugAdviseJson 			用药建议json字符串(格式和创建的时候相同）
	 * @apiParam {String} 			attention				检查建议Id，用逗号分隔，如：100,200,300
	 * @apiParam {String} 			submitState				是否提交 0-是 1-否
	 * @apiParam {String[]} 		images 					图片地址
	 * @apiParam {String[]} 		voices 					语音地址
	 * @apiParam {String} 			consultAdvise 			咨询结果
	 * @apiParam {String} 			consultAdviseDiseases 	咨询结果病种id，用逗号分隔，如：100,200,300
	 * @apiParam {String} 			isNeedHelp 			    是否需要导医帮忙(0:需要；1：不需要）
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("updateCurrecord")
	public JSONMessage update(String groupId, CureRecord intance,String images[],String voices[]) throws HttpApiException {
		if(intance.getOrderId()==null){
			throw new ServiceException(30003,"parameter [orderId] is null!");
		}
		//修改订单所属集团，排除导医操作
		if (ReqUtil.instance.getUser().getUserType() == UserType.doctor.getIndex()) {
			orderService.updateGroupId(intance.getOrderId(), groupId);
		}
				
		int ret=service.update(intance, images, voices);
		if(ret>0){
			return JSONMessage.success("咨询记录更新成功",intance);
		}else if(ret==-1){
			return JSONMessage.success("保存记录更新成功");
		}else{
			return JSONMessage.failure("咨询记录更新失败");
		}
		
	}

	/**
	 * 
	 * @api {[get,post]} /cureRecord/deleteByPk 根据主键删除
	 * @apiVersion 1.0.0
	 * @apiName deleteByPk
	 * @apiGroup 咨询记录
	 * @apiDescription 根据主键删除病情
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			id 						id
	 * 
	 *
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value="deleteByPk")
	public JSONMessage deleteByPk(Integer id) {
		return super.deleteByPk(id);
		
	}
	
	/**
	 * 
	 * @api {[get,post]} /cureRecord/findById 根据主键查找
	 * @apiVersion 1.0.0
	 * @apiName findById
	 * @apiGroup 咨询记录
	 * @apiDescription 根据主键查找
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			id 						id
	 * 
	 * 
	 * @apiSuccess {String} 			orderId 				订单id
	 * @apiSuccess {String} 			createTime 				创建时间
	 * @apiSuccess {String} 			treatAdvise 			治疗建议
	 * @apiSuccess {String} 			drugAdvise 				用药建议
	 * @apiSuccess {String} 			attention 				检查建议（弃用）
	 * @apiSuccess {CheckSuggest[]}		checkSuggestList		检查建议对象数组（2015-11-16开始使用）
	 * @apiSuccess {String[]} 			images 					图片地址
	 * @apiSuccess {String[]} 			voices 					语音地址
	 * @apiSuccess {String} 			consultAdvise 			咨询结果
	 * @apiSuccess {DiseaseTypeVO[]}	consultAdviseDiseaseList  病种对象数组
	 * @apiSuccess {RecipeView}	RecipeView   处方对象，格式见下面
	 * 
	 * @apiExample {javascript} Example usage:
	 * "recipeView": {
            "recipeDetailList": [
                {
                    "goodsId": "药品Id",
                    "goodsManufacturer": "江中药业股份有限公司",
                    "goodsPackSpecification": "每盒4板；每板8片",
                    "goodsNumber": "品种数量",
                    "patients": "适用人群",
                    "periodUnit": "用药周期 单位（例如 日，月，周）",
                    "periodNum": "用药周期 长度",
                    "periodTimes": "用药周期 服药次数",
                    "doseQuantity": "每次服药  数量",
                    "doseUnit": "每次服药 单位（例如 粒，克，瓶）",
                    "doseMothod": "服药方法",
                    "doseDays": "服药持续时间",
                    "goodsTitle": "品种标题",
                }
            ]
        }
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value="findById")
	public JSONMessage findById(Integer id) throws HttpApiException {
		return super.findById(id);
	}
	
	/**
	 * 
	 * @api {[get,post]} /cureRecord/findByOrder 根据订单查找咨询记录
	 * @apiVersion 1.0.0
	 * @apiName findByOrder
	 * @apiGroup 咨询记录
	 * @apiDescription  根据订单查找咨询记录
	 * @apiParam {String} 				access_token 			token
	 * @apiParam {String} 				orderId 				订单id
	 * 
	 * @apiSuccess {String} 			orderId 				订单id
	 * @apiSuccess {String} 			groupId 				集团id
	 * @apiSuccess {String} 			groupName 				集团name
	 * @apiSuccess {String} 			createTime 				创建时间
	 * @apiSuccess {String} 			treatAdvise 			治疗建议
	 * @apiSuccess {String} 			drugAdvise 				用药建议
	 * @apiSuccess {String} 			attention 				检查建议（弃用）
	 * @apiSuccess {CheckSuggest[]} 	checkSuggestList		检查建议对象数组（2015-11-16开始使用）
	 * @apiSuccess {String[]} 			images 					图片地址
	 * @apiSuccess {String[]} 			voices 					语音地址
	 * @apiSuccess {String} 			consultAdvise 			咨询结果
	 * @apiSuccess {DiseaseTypeVO[]}	consultAdviseDiseaseList  病种对象数组

	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月13日
	 */
	@RequestMapping(value="findByOrder")
	public JSONMessage findByOrder(Integer orderId) throws HttpApiException {
		if(orderId==null){
			throw new ServiceException(30002, "parameter :[orderId] is null");
		}
		List<CureRecord> data=service.findByOrderId(orderId);
		Map<String,Object> map = new HashMap<String,Object>();
		Order order = orderService.getOne(orderId);
		if (StringUtil.isNotBlank(order.getGroupId())) {
			Group group = groupService.getGroupById(order.getGroupId());
			for (CureRecord record : data) {
				record.setGroupId(group.getId());
				record.setGroupName(group.getName());
			}
		}
		return JSONMessage.success(null, data);
				
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /cureRecord/findByPatientAndDoctor 根据患者和医生查找咨询记录
	 * @apiVersion 1.0.0
	 * @apiName findByPatientAndDoctor
	 * @apiGroup 咨询记录
	 * @apiDescription  根据患者和医生查找咨询记录
	 * @apiParam {String} 				access_token 			token
	 * @apiParam {String} 				orderId 				订单id
	 * 
	 * @apiSuccess {String} 			orderId 				订单id
	 * @apiSuccess {String} 			createTime 				创建时间
	 * @apiSuccess {String} 			treatAdvise 			治疗建议
	 * @apiSuccess {String} 			drugAdvise 				用药建议
	 * @apiSuccess {String} 			attention 				检查建议（弃用）
	 * @apiSuccess {CheckSuggest[]} 	checkSuggestList		检查建议对象数组（2015-11-16开始使用）
	 * @apiSuccess {String[]} 			images 					图片地址
	 * @apiSuccess {String[]} 			voices 					语音地址
	 * @apiSuccess {Integer} 			doctorId 				医生id
	 * @apiSuccess {Integer} 			patientId 				患者id
	 * @apiSuccess {String} 			consultAdvise 			咨询结果
	 * @apiSuccess {DiseaseTypeVO[]}	consultAdviseDiseaseList  病种对象数组
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月13日
	 */
	@RequestMapping(value="findByPatientAndDoctor")
	public JSONMessage findByPatientAndDoctor(Integer orderId) throws HttpApiException {
		Order order=orderService.getOne(orderId);
		
		if(order==null){
			throw new ServiceException(40005, "找不到对应订单");
		}
		
		Integer patientId=order.getPatientId();
		Integer doctorId=order.getDoctorId();
		
		List<CureRecord> data=service.findByPatientAndDoctor(patientId, doctorId);
		return JSONMessage.success(null, data);
				
	}
	
	/**
	 * 
	 * @api {[get,post]} /cureRecord/getUsageByDrugId 查找药品用法用量
	 * @apiVersion 1.0.0
	 * @apiName getUsageByDrugId
	 * @apiGroup 咨询记录
	 * @apiDescription 查找药品用法用量
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			drugId 				          药品id
	 * 
	 * 
	 * @apiSuccess {String}	generalName  药品名字
	 * @apiSuccess {String}	packSpecification  包装规格
	 * @apiSuccess {String}	manufacturer  厂家
	 * @apiSuccess {String}	imageUrl  图片
	 * @apiSuccess {List}	drugUsegeList  用法用量对象
	 * @apiSuccess {String}	drugUsegeList.method  用法
	 * @apiSuccess {String}	drugUsegeList.quantity  用量
	 * @apiSuccess {String}	drugUsegeList.quantity  用量
	 * @apiSuccess {String}	drugUsegeList.patients  适用范围
	 * @apiSuccess {String}	drugUsegeList.times  次数
	 * @apiSuccess {String}	drugUsegeList.unit  单位
	 * @apiSuccess {String}	drugUsegeList.periodNum  药品使用 - 用药周期
	 * @apiSuccess {String}	drugUsegeList.periodTime  药品使用 - 用药周期 是 天 周 月 年
	 * 
	 * 
	 * @apiExample {javascript} Example usage:
	 * {
		    "data": {
		        "generalName": "马来酸氯苯那敏那敏 ( 氯敏 ) 敏",
		        "imageUrl": "http://192.168.3.7:9002/web/files/7839ab3307ff4624b84723abd28a26b0/无标题.png",
		        "manufacturer": "深圳市康哲药业有限公司",
		        "packSpecification": "0.1g*20片",
		        "drugUsegeList": [
		            {
		                "method": "口服",
		                "patients": "成年人",
		                "periodNum": "药品使用 - 用药周期",
		                "periodTime": "药品使用 - 用药周期 是 天 周 月 年",
		                "quantity": "3粒",
		                "unit": "服用的单位",
		                "times": "药品使用 - 用药次数 一个周期 多少次"
		            }
		        ]
		    },
		    "resultCode": 1
		}
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value="getUsageByDrugId")
	public JSONMessage getUsageByDrugId(String drugId) throws HttpApiException {
		CGoodsView data=	service.getUsageByDrugId(drugId);
		return JSONMessage.success(null, data);
	}
	
	/**
	 * 
	 * @api {[get,post]} /cureRecord/getCommonDisease 获取医生常用疾病（按医生使用次数取前15位）
	 * @apiVersion 1.0.0
	 * @apiName getCommonDisease
	 * @apiGroup 咨询记录 
	 * @apiDescription  获取医生常用疾病（按医生使用次数取前15位）
	 * @apiParam {String} 				access_token 			token
	 * @apiParam {Integer} 				doctorId 				医生id
	 * 
	 * @apiSuccess {List} 	            list		                                  病种列表
	 * @apiSuccess {String} 			id 					           病种ID
	 * @apiSuccess {String} 			name 					病种名称
	 * 
	 * @author 张垠
	 * @date 2016年1月22日
	 */
	@RequestMapping(value="getCommonDisease")
	public JSONMessage getCommonDisease(Integer doctorId){
		return JSONMessage.success(null,service.getCommonDiseasesByDocId(doctorId));
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /cureRecord/pushMessageToDoctor 移动端登录的时候将医生需要处理的导医订单推送给医生
	 * @apiVersion 1.0.0
	 * @apiName pushMessageToDoctor
	 * @apiGroup 咨询记录 
	 * @apiDescription  移动端登录的时候将医生需要处理的导医订单推送给医生
	 * @apiParam {String} 				access_token 			token
	 * @apiParam {Integer} 				doctorId 				医生id
	 * @apiSuccess {Map} 	            map		                map对象
	 * @author 姜宏杰
	 * @date 2016年3月4日18:28:10
	 */
	@RequestMapping(value="pushMessageToDoctor")
	public JSONMessage pushMessageToDoctor(Integer doctorId){
		return JSONMessage.success(service.pushMessageToDoctor(doctorId));
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /cureRecord/updateCurrecordByGuide 导医修改咨询记录
	 * @apiVersion 1.0.0
	 * @apiName updateCurrecordByGuide
	 * @apiGroup 咨询记录
	 * @apiDescription  导医修改咨询记录
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 				集团Id
	 * @apiParam {String} 			id 						主键
	 * @apiParam {Integer} 			orderId 				订单id
	 * @apiParam {String} 			treatAdvise 			治疗建议
	 * @apiParam {String} 			drugAdviseJson 			用药建议json字符串(格式和创建的时候相同）
	 * @apiParam {String} 			attention				检查建议Id，用逗号分隔，如：100,200,300
	 * @apiParam {String} 			submitState				是否提交 0-是 1-否
	 * @apiParam {String[]} 		images 					图片地址
	 * @apiParam {String[]} 		voices 					语音地址
	 * @apiParam {String} 			consultAdvise 			咨询结果
	 * @apiParam {String} 			consultAdviseDiseases 	咨询结果病种id，用逗号分隔，如：100,200,300
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("updateCurrecordByGuide")
	public JSONMessage updateCurrecordByGuide(String groupId, CureRecord intance,String images[],String voices[]) throws HttpApiException {
		if(intance.getOrderId()==null){
			throw new ServiceException(30003,"parameter [orderId] is null!");
		}
		//修改订单所属集团，排除导医操作
		/*if (ReqUtil.instance.getUser().getUserType() == UserType.doctor.getIndex()) {
			orderService.updateGroupId(intance.getOrderId(), groupId);
		}*/
				
		int ret=service.update(intance, images, voices);
		if(ret>0){
			return JSONMessage.success(intance);
		}else{
			return JSONMessage.failure("咨询记录更新失败");
		}
		
	}
	
	/**
	 * 
	 * @api {[get,post]} /cureRecord/pleaseGuideHelp 请导医帮忙
	 * @apiVersion 1.0.0
	 * @apiName pleaseGuideHelp
	 * @apiGroup 咨询记录
	 * @apiDescription 请导医帮忙
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 				集团Id
	 * @apiParam {String} 			id 						主键
	 * @apiParam {Integer} 			orderId 				订单id
	 * @apiParam {String} 			treatAdvise 			治疗建议
	 * @apiParam {String} 			drugAdviseJson 			用药建议json字符串(格式和创建的时候相同）
	 * @apiParam {String} 			attention				检查建议Id，用逗号分隔，如：100,200,300
	 * @apiParam {String} 			submitState				是否提交 0-是 1-否
	 * @apiParam {String[]} 		images 					图片地址
	 * @apiParam {String[]} 		voices 					语音地址
	 * @apiParam {String} 			consultAdvise 			咨询结果
	 * @apiParam {String} 			consultAdviseDiseases 	咨询结果病种id，用逗号分隔，如：100,200,300
	 * 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("pleaseGuideHelp")
	public JSONMessage pleaseGuideHelp(String groupId, CureRecord intance,String images[],String voices[]) throws HttpApiException {
		if(intance.getOrderId()==null){
			throw new ServiceException(30003,"parameter [orderId] is null!");
		}
		int ret=service.update(intance, images, voices);
		if(ret>0){
			return JSONMessage.success(intance);
		}else{
			return JSONMessage.failure("咨询记录更新失败");
		}
		
	}
	/**
	 * 
	 * @api {[get,post]} /cureRecord/getVoiceByOrderId 根据订单id去查找通话录音信息
	 * @apiVersion 1.0.0
	 * @apiName getVoiceByOrderId
	 * @apiGroup 咨询记录
	 * @apiDescription 根据订单id去查找通话录音信息
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			orderId 				订单id
	 * 
	 * @apiAuthor 姜宏杰
	 * @author 姜宏杰
	 * @date 2016年3月7日11:20:11
	 */
	@RequestMapping("getVoiceByOrderId")
	public JSONMessage getVoiceByOrderId(Integer orderId) {
		if(orderId==null){
			throw new ServiceException(30003,"parameter [orderId] is null!");
		}
		return JSONMessage.success(service.getVoiceUrlByOrderId(orderId));
	}
}
