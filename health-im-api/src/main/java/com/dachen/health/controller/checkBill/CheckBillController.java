package com.dachen.health.controller.checkBill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.spring.SpringBeansUtils;
import com.dachen.health.checkbill.entity.po.CheckBill;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.checkbill.entity.vo.CheckBillPageVo;
import com.dachen.health.checkbill.entity.vo.CheckBusInfo;
import com.dachen.health.checkbill.entity.vo.CheckItemCount;
import com.dachen.health.checkbill.entity.vo.CheckItemSearchParam;
import com.dachen.health.checkbill.entity.vo.CheckbillSearchParam;
import com.dachen.health.checkbill.service.CheckBillService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.utils.JobTask;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.line.stat.service.IVServiceProcessService;
import com.dachen.util.QiniuUtil;
import com.dachen.util.ReqUtil;

 
@RestController
@RequestMapping("/checkbill")
public class CheckBillController {
	
	@Autowired
	CheckBillService checkBillService;
	
	@Autowired
	IOrderService orderService;
	
	@Autowired
	UserManager userManager;  
	
	@Autowired
	IPatientService patientServiceImpl;
	
	@Autowired
	IVServiceProcessService  vServiceProcessServiceImpl;

	@RequestMapping("/add")
	public JSONMessage add(CheckBill CheckBill) {
		return JSONMessage.success(null, checkBillService.addCheckBill(CheckBill));
	}
	
	/**
	 * @api {POST} /checkbill/updateCheckbill 修改检查单状态
	 * @apiVersion 1.0.0
	 * @apiName updateCheckbill
	 * @apiGroup 检查单
	 * @apiDescription 修改检查单状态
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			id 			                                   来源id
	 * @apiParam {Integer} 			checkBillStatus 	            状态
	 * 
	 * 
	 * @apiSuccess {Integer}          data               成功
     * @apiAuthor  wangl
     * @date 2015年12月22
	 */
	@RequestMapping(value= "updateCheckbill",method = RequestMethod.POST)
	public JSONMessage updateCheckbill(CheckBill checkBill) {
		return JSONMessage.success(null, checkBillService.updateCheckbill(checkBill));
	}
	
	/**
	 * @api {POST} /checkbill/updateCheckItem 修改检查项
	 * @apiVersion 1.0.0
	 * @apiName updateCheckItem
	 * @apiGroup 检查单
	 * @apiDescription 修改检查项
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String}           fromId                 检查单Id              
 	 * @apiParam {String} 	         id 			                     检查项id
	 * @apiParam {String[]} 	     imageList 			            图片数组
	 * @apiParam {Long} 	        visitingTime 			就诊时间
	 * @apiParam {String} 	        results 			           咨询结果反馈
	 * 
	 * 
	 * @apiSuccess {Integer}        data             成功
     * @apiAuthor  wangl
     * @date 2015年12月22
	 */
	@RequestMapping(value= "updateCheckItem",method = RequestMethod.POST)
	public JSONMessage updateCheckItem(CheckItem checkItem) {
		return JSONMessage.success(null, checkBillService.updateCheckItem(checkItem));
	}
	
	
	/**
	 * 
	 * @api {get/post} /checkbill/getCheckItemList 获取检查项列表
	 * @apiVersion 1.0.0
	 * @apiName getCheckItemList
	 * @apiGroup 检查单
	 * @apiDescription 获取检查项列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			fromId 			                        来源id
	 * @apiParam {Integer} 			patient 			           患者Id
	 * @apiParam {Integer} 			pageIndex 			            页码数
	 * @apiParam {Integer} 			pageSize 			            条数
	 * 
	 * 
	 * @apiSuccess {String}        total                     列表总记录数
	 * @apiSuccess {Object}        data                      数据对象
	 * 
	 * @apiSuccess {String}          id                     检查项ID
     * @apiSuccess {String}          type                   检查项类型
     * @apiSuccess {String}          itemName               检查项名称 
     * @apiSuccess {Integer}         from                   来源类型
     * @apiSuccess {String}          fromId                 来源ID
     * @apiSuccess {String}          results                检查结果回馈
     * @apiSuccess {String[]}        imageList              图片URL列表  
     * @apiSuccess {Long}            createTime             创建时间  
     * @apiSuccess {Long}            updateTime             修改时间  
     * @apiSuccess {Long}            visitingTime           就诊时间  
     * @apiAuthor  wangl
     * @date 2015年12月22
	 */
	@RequestMapping(value = "getCheckItemList")
	public JSONMessage getCheckItemListByFromId(CheckItemSearchParam csp) {
		return JSONMessage.success(null, checkBillService.getCheckItemListByFromId(csp));
	}
	
	
	/**
	 * 
	 * @api {get/post} /checkbill/getCheckBillDetail 获取检查单详情
	 * @apiVersion 1.0.0
	 * @apiName getCheckBillDetail
	 * @apiGroup 检查单
	 * @apiDescription 获取检查单详情
	 * @apiParam {String} 		   access_token 		  token
	 * @apiParam {String} 			checkbillId 		     检查单Id
     * 
	 * 
	 * @apiSuccess {Integer}        patientId                 患者Id
	 * @apiSuccess {String}        patientName               患者名称
	 * @apiSuccess {Integer}        doctorId                 医生Id
	 * @apiSuccess {String}        doctorName                医生名称
	 * @apiSuccess {String}        hospitalId                医院Id
	 * @apiSuccess {String}        hospitalName              医院名称
	 * @apiSuccess {Long}        createTime                创建时间
	 * @apiSuccess {Long}        price                     价格
	 * @apiSuccess {Object[]}        checkItemList               检查单对象
	 * @apiSuccess {String}        checkItemList[0].id           检查项记录Id
	 * @apiSuccess {String}        checkItemList[0].checkUpId    检查项Id
	 * @apiSuccess {String}        checkItemList[0].type         检查项类型
	 * @apiSuccess {String}        checkItemList[0].itemName     检查项名称
	 * @apiSuccess {Integer}        checkItemList[0].from    来源类型
	 * @apiSuccess {String}        checkItemList[0].fromId  来源Id
	 * @apiSuccess {String}        checkItemList[0].results 检查结果反馈
	 * @apiSuccess {String[]}      checkItemList[0].imageList     图片路径数组
	 * @apiSuccess {Long}          checkItemList[0].createTime    创建时间
	 * @apiSuccess {Object}           checkBusInfo    直通车对象
	 * @apiSuccess {String}           productTitle    标题
	 * @apiSuccess {String}           orderTime       订单时间
	 * @apiSuccess {Integer}           orderId        订单Id
	 * @apiSuccess {String}           checkBusDesc    描述
	 * @apiSuccess {String}           productPrice    价格
     * @apiAuthor  wangl
     * @date 2015年12月22
	 */
	@RequestMapping(value = "getCheckBillDetail")
	public JSONMessage getCheckBillDetail(String checkbillId) {
		CheckBill cb = checkBillService.getCheckBillDetail(checkbillId);
		Integer orderId = cb.getOrderId();
		Order order = orderService.getOne(orderId);
		CheckBillPageVo cbpv = setCbpvValue(order,cb);
		int status = cb.getCheckBillStatus();
		if(status != 1){
			Map<String,Object> checkBusMap = vServiceProcessServiceImpl.getCheckBillService(cb.getId(), cb.getCheckBillStatus());
			setCheckBusInfo(cbpv,checkBusMap);
		}
		return JSONMessage.success(null, cbpv);
	}
	
	private void setCheckBusInfo(CheckBillPageVo cbpv, Map<String, Object> checkBusMap) {
		if(checkBusMap != null){
			CheckBusInfo cbinfo = new CheckBusInfo();
			cbinfo.setCheckBusDesc(String.valueOf(checkBusMap.get("checkBusDesc")));
			cbinfo.setOrderId(String.valueOf(checkBusMap.get("orderId")));
			cbinfo.setOrderTime(String.valueOf(checkBusMap.get("orderTime")));
			cbinfo.setProductTitle(String.valueOf(checkBusMap.get("productTitle")));
			cbinfo.setProductPrice(String.valueOf(checkBusMap.get("productPrice")));
			cbpv.setCheckBusInfo(cbinfo);
		}
	}

	/**
	 * 
	 * @api {POST} /checkbill/batchAddOrUpdateCheckItem 批量添加或更新检查项
	 * @apiVersion 1.0.0
	 * @apiName batchAddOrUpdateCheckItem
	 * @apiGroup 检查单
	 * @apiDescription 线下服务添加检查项
	 * @apiParam {String} 		   access_token 		  token
	 * @apiParam {String} 		   checkItemString 		  {@link CheckItem}  checkItem序列化字符串
	 * @apiParam {String} 		   id 		              id
	 * @apiParam {String} 		   checkUpId 		      检查项基础数据表Id
	 * @apiParam {String} 		   type 		                  类型
	 * @apiParam {String} 		   itemName 		     名称
	 * @apiParam {Integer} 		   from 		                来源{1:医患系统，2：线下服务}
	 * @apiParam {String} 		   fromId 		                来源Id
	 * @apiParam {String} 		   results 		                检查结果反馈
	 * @apiParam {String[]} 	   imageList 	                 检查项图片路径	  
	 * @apiParam {Long} 		   createTime 		 创建时间 
     * 
	 * @apiSuccess {String}        data               返回成功
     * @apiAuthor  wangl
     * @date 2015年12月22
	 */
	@RequestMapping(value = "batchAddOrUpdateCheckItem",method = RequestMethod.POST)
	public JSONMessage batchAddOrUpdateCheckItem(@RequestParam(required=true) String checkItemString) {
		return JSONMessage.success(null, checkBillService.batchAddOrUpdateCheckItem(checkItemString));
	}
	
	
	/**
	 * 
	 * @api {get/post} /checkbill/getCheckbillList
	 * @apiVersion 1.0.0
	 * @apiName getCheckbillList
	 * @apiGroup 检查单
	 * @apiDescription 获取检查单列表服务
	 * @apiParam {String} 		   access_token 		  token
	 * @apiParam {Integer} 		   patientId 		                  患者Id
	 * @apiParam {Integer} 			pageIndex 			            页码数
	 * @apiParam {Integer} 			pageSize 			            条数
     * 
	 * 
	 * @apiSuccess {Integer}        total                    列表总记录数
	 * @apiSuccess {Object}        data                      数据对象
	 * 
	 * @apiSuccess {String}        id                        检查项Id
	 * @apiSuccess {Integer}        orderType                订单类型
	 * @apiSuccess {Integer}        packType                 套餐类型
	 * @apiSuccess {Integer}        checkBillStatus          检查项状态
	 * @apiSuccess {Integer}        patientId                患者Id
	 * @apiSuccess {String}        patientName               患者名称
	 * @apiSuccess {Integer}        doctorId                 医生Id
	 * @apiSuccess {String}        doctorName                医生名称
	 * @apiSuccess {String}        hospitalId                医院Id
	 * @apiSuccess {String}        hospitalName              医院名称
	 * @apiSuccess {Long}          createTime                创建时间
	 * @apiSuccess {Long}          price                     价格
	 * @apiSuccess {Object[]}        checkItemList               检查单对象
	 * @apiSuccess {String}        checkItemList[0].id           检查项记录Id
	 * @apiSuccess {String}        checkItemList[0].checkUpId    检查项Id
	 * @apiSuccess {String}        checkItemList[0].type         检查项类型
	 * @apiSuccess {String}        checkItemList[0].itemName     检查项名称
	 * @apiSuccess {Integer}        checkItemList[0].from    来源类型
	 * @apiSuccess {String}        checkItemList[0].fromId  来源Id
	 * @apiSuccess {String}        checkItemList[0].results 检查结果反馈
	 * @apiSuccess {String[]}      imageList                图片路径数组
	 * @apiSuccess {Long}          createTime               创建时间
     * @apiAuthor  wangl
     * @date 2015年12月22
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "getCheckbillList")
	public JSONMessage getCheckbillList(CheckbillSearchParam csp) {
		//获取当前用户的所有患者ids
		List<Integer> patientIds = patientServiceImpl.getPatientIdsByUserId(ReqUtil.instance.getUserId());
		if(patientIds == null)
			return JSONMessage.success(null,null);
		csp.setPatientIds(patientIds);
		PageVO pageVo = checkBillService.getCheckBillPageVo(csp); 
		List<CheckBill> cbs = (List<CheckBill>) pageVo.getPageData();
		if(cbs != null && cbs.size() > 0){
			List<CheckBillPageVo> cbpv = new ArrayList<CheckBillPageVo>();
			for (CheckBill cb : cbs) {
				Integer orderId = cb.getOrderId();
				if (null == orderId) {
					continue;
				}
				Order order = orderService.getOne(orderId);
				cbpv.add(setCbpvValue(order,cb));
			}
			pageVo.setPageData(cbpv);
		}
		return JSONMessage.success(null,pageVo);
	}


	@SuppressWarnings("unchecked")
	private CheckBillPageVo setCbpvValue(Order order, CheckBill cb) {
		CheckBillPageVo cbpv = new CheckBillPageVo();
		if(order != null && cb != null){
			cbpv.setCreateTime(cb.getCreateTime());
			cbpv.setDoctorId(order.getDoctorId());
			cbpv.setId(cb.getId());
			cbpv.setOrderId(order.getId());
			cbpv.setOrderType(order.getOrderType());
			cbpv.setPackType(order.getPackType());
			cbpv.setCheckBillStatus(cb.getCheckBillStatus());

			User user = userManager.getUser(order.getDoctorId());
			Doctor doctor = user.getDoctor();
			if(user.getDoctor() != null){
				cbpv.setDoctorName(user.getName());
				cbpv.setHospitalId(doctor.getHospitalId());
				cbpv.setHospitalName(doctor.getHospital());
			}
			cbpv.setPatientName(ReqUtil.instance.getUser().getName());
			cbpv.setPrice(order.getPrice());
			
			CheckItemSearchParam csp = new CheckItemSearchParam();
			csp.setFromId(cb.getId());
			csp.setPageSize(Integer.MAX_VALUE);
			cbpv.setCheckItemList((List<CheckItem>) checkBillService.getCheckItemListByFromId(csp).getPageData());
		}
		return cbpv;
	}
	
	
	/**
	 * 
	 * @api {get/post} /checkbill/getCheckItemCount
	 * @apiVersion 1.0.0
	 * @apiName getCheckItemCount
	 * @apiGroup 检查单
	 * @apiDescription 获取按照患者检查项次数统计的检查项列表
	 * @apiParam {String} 		   access_token 		  token
	 * @apiParam {Integer} 		   patientId 		                   患者Id
     * 
	 * 
	 * @apiSuccess {object[]}        data                   数据对象数组
	 * @apiSuccess {String}          data[0].checkUpId      检查项id
	 * @apiSuccess {String}          data[0].itemName       检查项名称
	 * @apiSuccess {Long}            data[0].count          检查项次数
     * @apiAuthor  wangl
     * @date 2015年12月25日
	 */
	@RequestMapping(value = "getCheckItemCount")
	public JSONMessage getCheckItemCount(Integer patientId){
		List<CheckItemCount> list = checkBillService.getCheckItemCount(patientId);
		return JSONMessage.success(null,list);
	}
	
	/**
	 * 
	 * @api {get/post} /checkbill/getCheckItemByClassify
	 * @apiVersion 1.0.0
	 * @apiName getCheckItemByClassify
	 * @apiGroup 检查单
	 * @apiDescription 获取患者某一个检查项列表
	 * @apiParam {String} 		   access_token 		  token
	 * @apiParam {String} 		   checkUpId 		                  检查项id
	 * @apiParam {Integer} 		   patientId 		                 患者Id
     * 
	 * 
	 * @apiSuccess {object[]}        data                   数据对象数组
	 * @apiSuccess {String}          data[0].itemName       检查项名称
	 * @apiSuccess {String}          data[0].results        检查项咨询结果
	 * @apiSuccess {String[]}        data[0].imageList      检查项图片路径列表
	 * @apiSuccess {Long}            data[0].visitingTime   检查项诊断时间
     * @apiAuthor  wangl
     * @date 2015年12月25日
	 */
	@RequestMapping(value = "getCheckItemByClassify")
	public JSONMessage getCheckItemByClassify(@RequestParam(required=true) String checkUpId,
											  Integer patientId){
		List<CheckItem> list = checkBillService.getCheckItemByClassify(patientId,checkUpId);
		return JSONMessage.success(null,list);
	}
	
	
	/**
	 * 
	 * @api {get/post} /checkbill/getCheckItemDetailById
	 * @apiVersion 1.0.0
	 * @apiName getCheckItemDetailById
	 * @apiGroup 检查单
	 * @apiDescription 根据检查项id获取检查项详情
	 * @apiParam {String} 		   access_token 		  token
	 * @apiParam {String} 		   checkItemId 		                  检查项id
     * 
	 * 
	 * @apiSuccess {object}          data                   数据对象数组
	 * @apiSuccess {String}          data.itemName       检查项名称
	 * @apiSuccess {String}          data.results        检查项咨询结果
	 * @apiSuccess {String[]}        data.imageList      检查项图片路径列表
	 * @apiSuccess {String}          data.id             检查项id
	 * @apiSuccess {String}          data.checkUpId      检查项基础数据id
	 * @apiSuccess {Long}            data.createTime     检查项创建时间
	 * @apiSuccess {Long}            data.updateTime     检查项更新时间
	 * @apiSuccess {Long}            data.visitingTime   检查项诊断时间
     * @apiAuthor  wangl
     * @date 2015年12月25日
	 */
	@RequestMapping(value = "getCheckItemDetailById")
	public JSONMessage getCheckItemDetailById(String checkItemId){
		CheckItem item = checkBillService.getCheckItemById(checkItemId);
		return JSONMessage.success(null,item);
	}

	@RequestMapping(value = "test")
	public JSONMessage test(){
		JobTask job = SpringBeansUtils.getBean("JobTask");
		System.out.println(job);
		return JSONMessage.success();
	}
	
	/**
	 * 
	 * @api {get/post} /checkbill/getCheckItem 根据检查项id获取检查项详情
	 * @apiVersion 1.0.0
	 * @apiName getCheckItem
	 * @apiGroup 检查单
	 * @apiDescription 根据检查项id获取检查项详情
	 * @apiParam {String} 		   access_token 		  token
	 * @apiParam {String} 		   checkItemId 		                  检查项id
     * 
	 * 
	 * @apiSuccess {object}          data                   数据对象数组
	 * @apiSuccess {String}          data.checkName       	检查项名称
	 * @apiSuccess {String}          data.checkupId        	检查项id
	 * @apiSuccess {Long}        	 data.suggestCheckTime  建议检查时间
	 * @apiSuccess {String}          data.attention         注意事项
	 * @apiSuccess {String[]}        data.indicatorIds      指标的ids
     * @apiSuccess {Object[]}        data.checkSuggestItems 指标
	 * @apiSuccess {ObjectId}        data.checkSuggestItems.id                指标id
	 * @apiSuccess {String}          data.checkSuggestItems.checkupId         检查项id
	 * @apiSuccess {String}          data.checkSuggestItems.name              指标名称
	 * @apiSuccess {String}          data.checkSuggestItems.alias             缩写或别名
	 * @apiSuccess {String}          data.checkSuggestItems.unit              单位
	 * @apiSuccess {String}          data.checkSuggestItems.regionLeft        范围（左）
	 * @apiSuccess {String}          data.checkSuggestItems.regionRight       范围（右）
	 * @apiSuccess {Integer}         data.checkSuggestItems.fitSex            性别（1男，2女）
	 * @apiSuccess {Long}            data.checkSuggestItems.createTime        创建时间
	 * @apiSuccess {Long}            data.checkSuggestItems.updateTime        更新时间
	 * @apiSuccess {boolean}         data.checkSuggestItems.doctorCare        医生是否关注
     * @apiAuthor  傅永德
     * @date 2016年12月25日
	 */
	@RequestMapping(value = "getCheckItem")
    public JSONMessage getCheckItem(
    		@RequestParam(name="checkItemId")String checkItemId
	) {
    	return JSONMessage.success(checkBillService.getCheckItem(checkItemId));
    }

    /**
     *
     * @api {get/post} /checkbill/isCheckItemFinish  根据检查项id获取患者是否上传结果
     * @apiVersion 1.0.0
     * @apiName isCheckItemFinish
     * @apiGroup 检查单
     * @apiDescription 根据检查项id获取患者是否上传结果
     * @apiParam {String} 		   access_token 		  token
     * @apiParam {String} 		   checkItemId 		                  检查项id
     *
     *
     * @apiSuccess {object}          data
     * @apiSuccess {boolean}        data.isCheckItemFinish  检查单是否上传结果（true上传，false未上传）
     * @apiSuccess {boolean}        data.patientId  患者id（当isCheckBillFinish为true时返回）
     * @apiSuccess {boolean}        data.checkUpId  检查项id
     * @apiAuthor  傅永德
     * @date 2016年12月25日
     */
    @RequestMapping(value = "isCheckItemFinish")
    public JSONMessage isCheckBillFinish(
            @RequestParam(name="checkItemId")String checkItemId
    ) {
        return JSONMessage.success(checkBillService.isCheckItemFinish(checkItemId));
    }

}
