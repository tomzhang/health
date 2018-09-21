package com.dachen.health.controller.pack.patient;

import java.util.List;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
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
import com.dachen.health.pack.patient.model.CareSummary;
import com.dachen.health.pack.patient.model.CureRecord;
import com.dachen.health.pack.patient.service.ICallResultService;
import com.dachen.health.pack.patient.service.ICareSummaryService;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

/**
 * 
 * @ClassName:  CareSummaryController   
 * @Description:关怀小结
 * @author: qinyuan.chen 
 * @date:   2016年12月8日 下午3:58:55   
 *
 */
@RestController
@RequestMapping(value="/careSummary")
public class CareSummaryController extends BaseController<CureRecord, Integer> {
	
	@Resource
	private ICareSummaryService service;
	
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
	

	/**
	 * 
	 * @api {[get,post]} /careSummary/createCareSummary 创建关怀小结
	 * @apiVersion 1.0.0
	 * @apiName create
	 * @apiGroup 关怀小结
	 * @apiDescription 关怀小结创建
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 				集团id
	 * @apiParam {Integer} 			orderId 				订单id
	 * @apiParam {Integer} 			doctorId 				医生id
	 * @apiParam {String} 			treatAdvise 			治疗建议
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
	 *      goodsName:         //药品名称
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
				"goodsName": "阿莫西林"
		    }
		]
	  * @author qinyuan.chen
	 * @date 2016年12月8日
	 */
	@RequestMapping("createCareSummary")
	public JSONMessage createCareSummary(String groupId, CareSummary intance,String images[],String voices[]) throws HttpApiException {
		if(intance.getOrderId()==null){
				throw new ServiceException("请求参数不正确");
			}	
		//修改订单所属集团，排除导医操作
		if (ReqUtil.instance.getUser().getUserType() == UserType.doctor.getIndex()) {
			orderService.updateGroupId(intance.getOrderId(), groupId);
		}
		
		if (intance.getDoctorId() == null) {
			Integer userId = ReqUtil.instance.getUserId();
			intance.setDoctorId(userId);
		}
		intance.setSubmitState("1");
		int ret=service.save(intance, images, voices);
		if(ret>0){
			//如果是医生填写诊疗纪录，则需要向导医发送系统通知
			service.sendNotice(intance);
			return JSONMessage.success("关怀小结创建成功",intance);
		}else{
			return JSONMessage.failure("关怀小结创建失败");
		}
	}

	
	
	
	/**
	 * 
	 * @api {[get,post]} /careSummary/findByOrder 根据订单查找关怀小结
	 * @apiVersion 1.0.0
	 * @apiName findByOrder
	 * @apiGroup 关怀小结
	 * @apiDescription  根据订单查找关怀小结
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

	 * @author qinyuan.chen
	 * @date 2016年12月8日
	 */
	@RequestMapping(value="findByOrder")
	public JSONMessage findByOrder(Integer orderId) throws HttpApiException {
		if(orderId==null){
			throw new ServiceException("参数不完整");
		}
		List<CareSummary> data=service.findByOrderId(orderId);
		if(data!=null&&data.size()>0){
			Order order = orderService.getOne(orderId);
			if (StringUtil.isNotBlank(order.getGroupId())) {
				Group group = groupService.getGroupById(order.getGroupId());
				for (CareSummary record : data) {
					record.setGroupId(group.getId());
					record.setGroupName(group.getName());
				}
			}
		}
		return JSONMessage.success(null, data);
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /careSummary/findById 根据关怀小结ID查找关怀小结
	 * @apiVersion 1.0.0
	 * @apiName findById
	 * @apiGroup 关怀小结
	 * @apiDescription  根据关怀小结ID查找关怀小结
	 * @apiParam {String} 				access_token 			token
	 * @apiParam {String} 				id 							关怀小结id
	 * 
	 * @apiSuccess {String} 			id 						关怀小结id
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

	 * @author qinyuan.chen
	 * @date 2016年12月8日
	 */
	public JSONMessage findById(Integer id) throws HttpApiException {
		if(id==null){
			throw new ServiceException("参数不完整");
		}
		CareSummary data=service.findByPk(id);
		if(data!=null){
			Order order = orderService.getOne(data.getOrderId());
			if (StringUtil.isNotBlank(order.getGroupId())) {
				Group group = groupService.getGroupById(order.getGroupId());
				data.setGroupId(group.getId());
				data.setGroupName(group.getName());
			}
		}
		return JSONMessage.success(null, data);
	}
	
	
	
}
