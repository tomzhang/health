package com.dachen.health.controller.pack.consultation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.consult.Service.ConsultationFriendService;
import com.dachen.health.pack.consult.entity.po.ConsultationApplyFriend;

@RestController
@RequestMapping("/consultation/friend")
public class ConsultationFriendController {

	@Autowired
	ConsultationFriendService consultationFriendServiceImpl;
	/**
	 * 
	 * @api {get/post} /consultation/friend/searchAssistantDoctors 大医生搜索小医生
	 * @apiVersion 1.0.0
	 * @apiName searchAssistantDoctors
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：大医生搜索小医生
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			number 			                         医生号或手机号（必传）
	 * 
	 * @apiSuccess {Integer}         doctorId                医生Id
	 * @apiSuccess {String}          name                    姓名
     * @apiSuccess {String}          title                   医生职称名称
     * @apiSuccess {String}          hospital                医生所属医院名称
     * @apiSuccess {String}          doctorGroupName         医生主集团名称       
     * @apiSuccess {String}          headPicFileName         图片URL     
     * @apiSuccess {String}          introduction            医生介绍
     * @apiSuccess {String}          departments             医生部门
     * @apiSuccess {String}          skill                   擅长
     * @apiSuccess {String}          applyMessage            申请留言
     * @apiSuccess {String}          doctorNum               医生号
     * @apiSuccess {String}          applyStatus             1、已申请，2、已被申请，3、无关系，4，已收藏，5，已是好友
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "searchAssistantDoctors")
	public JSONMessage searchAssistantDoctors(@RequestParam(required = true) String number){
		return JSONMessage.success(consultationFriendServiceImpl.searchAssistantDoctors(number));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/searchConsultationDoctors 小医生搜索开通了会诊服务的大医生列表
	 * @apiVersion 1.0.0
	 * @apiName searchConsultationDoctors
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：小医生搜索开通了会诊服务的大医生列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			areaCode 			            区域编号（可不传）
	 * @apiParam {String} 			deptId 			                        部门编号（可不传）
	 * @apiParam {String} 			name 			                        医生姓名（可不传）
	 * @apiParam {Integer} 			pageIndex 			            页数（可不传）
	 * @apiParam {Integer} 			pageSize 			             条数（可不传）
	 * 
	 * @apiSuccess {Integer}         pageCount              总页数
	 * @apiSuccess {List}            pageData               对象列表
	 * @apiSuccess {String}          pageData.userId        用户id(既doctorId)
	 * @apiSuccess {String}          pageData.name          用户姓名
     * @apiSuccess {String}          pageData.hospital      医生所属医院名称
     * @apiSuccess {String}          pageData.headPicFileName         医生职称名称
     * @apiSuccess {String}          pageData.consultationPrice 会诊费用
     * @apiSuccess {String}          pageData.applyStatus   1、已申请，2、已被申请，3、无关系，4，已收藏，5，已是好友
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "searchConsultationDoctors")
	public JSONMessage searchConsultationDoctors(Integer areaCode,
												 String  name,
												 String deptId,
												 Integer pageIndex,
												 Integer pageSize){
		return JSONMessage.success(consultationFriendServiceImpl.searchConsultationDoctors(areaCode,name,deptId,pageIndex,pageSize));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/searchConsultationDoctorsByKeyword 助手医生搜索开通了会诊服务的专家医生列表(根据医生名或医院名称)
	 * @apiVersion 1.0.0
	 * @apiName searchConsultationDoctorsByKeyword
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：助手医生搜索开通了会诊服务的专家医生列表(根据医生名或医院名称)
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			keyword 			            关键字（可不传）
	 * @apiParam {Integer} 			pageIndex 			            页数（可不传）
	 * @apiParam {Integer} 			pageSize 			             条数（可不传）
	 * 
	 * @apiSuccess {Integer}         pageCount              总页数
	 * @apiSuccess {List}            pageData               对象列表
	 * @apiSuccess {String}          pageData.userId        用户id(既doctorId)
	 * @apiSuccess {String}          pageData.name          用户姓名
     * @apiSuccess {String}          pageData.hospital      医生所属医院名称
     * @apiSuccess {String}          pageData.headPicFileName         医生职称名称
     * @apiSuccess {String}          pageData.consultationPrice 会诊费用
     * @apiSuccess {String}          pageData.applyStatus   1、已申请，2、已被申请，3、无关系，4，已收藏，5，已是好友
     * @apiAuthor  wangl
     * @date 2016年1月26日
	 */
	@RequestMapping(value = "searchConsultationDoctorsByKeyword")
	public JSONMessage searchConsultationDoctorsByKeyword(String keyword,
												 Integer pageIndex,
												 Integer pageSize){
		return JSONMessage.success(consultationFriendServiceImpl.searchConsultationDoctorsByKeyword(keyword,pageIndex,pageSize));
	}

	/**
	 * 
	 * @api {get/post} /consultation/friend/doctorDetail 查看医生详情
	 * @apiVersion 1.0.0
	 * @apiName doctorDetail
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：大小医生在好友申请列表或搜索医生列表中点击进入详情
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			doctorId 			            医生id 必传
	 * @apiParam {Integer} 			roleType 				当前用户角色（1：专家，2：助手）必传
	 * 
	 * @apiSuccess {Integer}         doctorId                医生Id
	 * @apiSuccess {String}          name                    姓名
     * @apiSuccess {String}          title                   医生职称名称
     * @apiSuccess {String}          hospital                医生所属医院名称
     * @apiSuccess {String}          doctorGroupName         医生主集团名称       
     * @apiSuccess {String}          headPicFileName         图片URL     
     * @apiSuccess {String}          introduction            医生介绍
     * @apiSuccess {String}          departments             医生部门
     * @apiSuccess {String}          skill                   擅长
     * @apiSuccess {String}          applyMessage            申请留言
     * @apiSuccess {String}          consultationPrice       会诊价格
     * @apiSuccess {String}          doctorNum               医生号
     * @apiSuccess {String}          consultationRequired    大医生会诊要求
     * @apiSuccess {String}          applyStatus             1、已申请，2、已被申请，3、无关系，4、已收藏，5，已是好友
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "doctorDetail")
	public JSONMessage doctorDetail(@RequestParam(required = true) Integer doctorId,
									@RequestParam(required = true) Integer roleType){
		return JSONMessage.success(consultationFriendServiceImpl.doctorDetail(doctorId,roleType));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/applyFriends 申请成为会诊好友
	 * @apiVersion 1.0.0
	 * @apiName applyFriends
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：申请成为会诊好友
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			consultationDoctorId 	大医生id
	 * @apiParam {Integer} 			unionDoctorId 			小医生id
	 * @apiParam {Integer} 			applyType 			            申请类型(1：大主动申请小 , 2:小主动申请大)
	 * @apiParam {String} 			applyMessage 			申请留言
	 * 
	 * @apiSuccess {String}         resultCode               1：成功
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "applyFriends")
	public JSONMessage applyFriends(ConsultationApplyFriend consultationApplyFriend){
		consultationFriendServiceImpl.applyFriends(consultationApplyFriend);
		return JSONMessage.success();
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/getFriendsByRoleType 查看会诊好友列表
	 * @apiVersion 1.0.0
	 * @apiName getFriendsByRoleType
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：查看会诊好友列表 大医生、小医生查看的列表分类不一样 需要区分对待
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			doctorId 				医生Id
	 * @apiParam {Integer} 			roleType 			            当前医生角色(1：专家 , 2:助手)
	 * @apiParam {Integer} 			pageEnterType 			获取专家列表页面入口(1：会诊医生管理页面 , 2:发起会诊选择专家页面)，助手列表不需要传该参数
	 * 
	 * @apiSuccess {List}            data                   对象列表
	 * @apiSuccess {String}          data.deptId            科室id(大医生列表使用，收藏专家则为空)
	 * @apiSuccess {String}          data.deptName          科室名称(大医生列表使用)
	 * @apiSuccess {String}          data.firstLetter       名称首字母(小医生列表使用)
	 * @apiSuccess {List}            data.doctors           医生列表
     * @apiSuccess {Integer}         data.doctors.userId    医生Id
     * @apiSuccess {String}          data.doctors.name       医生姓名
     * @apiSuccess {String}          data.doctors.title      医生职称(小医生列表使用)
     * @apiSuccess {String}          data.doctors.doctorGroupName      医生所属剧团
     * @apiSuccess {String}          data.doctors.hospital      医生所属集团
     * @apiSuccess {String}          data.doctors.headPicFileName        图片URL
     * @apiSuccess {Integer}         data.doctors.consultationPrice      会诊费用(大医生列表使用)
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "getFriendsByRoleType")
	public JSONMessage getFriendsByRoleType(@RequestParam(required=true) Integer doctorId,
											@RequestParam(required=true) Integer roleType,
											@RequestParam(required=false) Integer pageEnterType
											){
		return JSONMessage.success(consultationFriendServiceImpl.getFriendsByRoleType(doctorId,roleType,pageEnterType));
	}
	
	
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/getApplyFriendByRoleType 查看会诊好友申请列表
	 * @apiVersion 1.0.0
	 * @apiName getApplyFriendByRoleType
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：查看会诊好友申请列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			doctorId 				医生Id
	 * @apiParam {Integer} 			roleType 			           当前 医生角色(1：专家, 2:助手)
	 * @apiParam {Integer} 			pageIndex 			            页码数
	 * @apiParam {Integer} 			pageSize 			            条数
	 * 
	 * @apiSuccess {Integer}         pageCount              总页数
	 * @apiSuccess {List}            pageData               对象列表
	 * @apiSuccess {String}          pageData.consultationApplyFriendId            申请好友记录Id
	 * @apiSuccess {String}          pageData.doctorId      医生Id
	 * @apiSuccess {String}          pageData.name          用户姓名
     * @apiSuccess {String}          pageData.hospital      医生所属医院名称
     * @apiSuccess {String}          pageData.title         医生职称名称
     * @apiSuccess {String}          pageData.departments       部门名称
     * @apiSuccess {String}          pageData.headPicFileName        图片URL
     * @apiSuccess {String}          pageData.applyMessage         申请留言
     * @apiSuccess {String}          pageData.applyTime            申请时间
     * @apiSuccess {String}          pageData.applyStatus   1、已申请，2、已被申请，3、无关系，4，已收藏，5，已是好友
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "getApplyFriendByRoleType")
	public JSONMessage getApplyFriendByRoleType(@RequestParam(required=true) Integer doctorId,
												@RequestParam(required=true) Integer roleType,
												Integer pageIndex,
												Integer pageSize){
		return JSONMessage.success(consultationFriendServiceImpl.getApplyFriendByRoleType(doctorId,roleType,pageIndex,pageSize));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/processFriendsApply 同意或忽略好友
	 * @apiVersion 1.0.0
	 * @apiName processFriendsApply
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：同意或忽略好友
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			consultationApplyFriendId 	申请好友记录Id
	 * @apiParam {Integer} 			status 			        2:同意，3：忽略
	 * 
	 * @apiSuccess {String}         resultCode              1:成功          
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "processFriendsApply")
	public JSONMessage processFriendsApply(String consultationApplyFriendId,Integer status){
		consultationFriendServiceImpl.processFriendsApply(consultationApplyFriendId,status);
		return JSONMessage.success();
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/isSpecialist 当前医生是否是会诊医生
	 * @apiVersion 1.0.0
	 * @apiName isSpecialist
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：当前医生是否是会诊医生
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			doctorId 	                                    医生id
	 * 
	 * @apiSuccess {Boolean}         data              true:是，false：否          
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "isSpecialist")
	public JSONMessage isSpecialist(Integer doctorId){
		return JSONMessage.success(consultationFriendServiceImpl.isSpecialist(doctorId));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/getPatientIllcaseList 获取医生患者列表
	 * @apiVersion 1.0.0
	 * @apiName getPatientIllcaseList
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：获取医生患者列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			doctorId 	                                    医生id
	 * @apiParam {Integer} 			pageIndex 			            页码数
	 * @apiParam {Integer} 			pageSize 			            条数
	 * 
	 * @apiSuccess {Integer}        pageCount               总页数          
	 * @apiSuccess {Integer}        pageData                对象列表        
	 * @apiSuccess {Integer}        pageData.userId         患者对应的用户id          
	 * @apiSuccess {Integer}        pageData.patientId      患者Id
	 * @apiSuccess {Integer}        pageData.patientName    患者姓名
	 *       
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "getPatientIllcaseList")
	public JSONMessage getPatientIllcaseList(Integer doctorId,Integer pageIndex,Integer pageSize){
		return JSONMessage.success(consultationFriendServiceImpl.getPatientIllcaseList(doctorId,pageIndex,pageSize));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/getConsultationDoctorNum 会诊医生数量
	 * @apiVersion 1.0.0
	 * @apiName getConsultationDoctorNum
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景： 会诊医生数量
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			doctorId 	                                    医生id(主诊医生)
	 * 
	 * @apiSuccess {Integer}        data                    专家数量     
	 *       
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "getConsultationDoctorNum")
	public JSONMessage getConsultationDoctorNum(Integer doctorId){
		return JSONMessage.success(consultationFriendServiceImpl.getConsultationDoctorNum(doctorId));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/getUnionDoctorNum 主诊医生数量
	 * @apiVersion 1.0.0
	 * @apiName getUnionDoctorNum
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：主诊医生数量
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			doctorId 	                                    医生id(主诊医生)
	 * 
	 * @apiSuccess {Integer}        data                    专家数量     
	 *       
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "getUnionDoctorNum")
	public JSONMessage getUnionDoctorNum(Integer doctorId){
		return JSONMessage.success(consultationFriendServiceImpl.getUnionDoctorNum(doctorId));
	}
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/getDoctorApplyNum 获取会诊好友的总数量
	 * @apiVersion 1.0.0
	 * @apiName getDoctorApplyNum
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景：会诊大厅上面显示的总数量
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			doctorId 	                                    医生id(主诊医生)
	 * @apiParam {Integer} 			applyType 	                                    申请类型(1：获取大医生的申请数量 , 2:获取小医生的申请数量)不传 就是获取所有的申请数量
	 * 
	 * @apiSuccess {Integer}        data                    申请数量    
	 *       
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "getDoctorApplyNum")
	public JSONMessage getDoctorApplyNum(Integer doctorId,Integer applyType){
		return JSONMessage.success(consultationFriendServiceImpl.getDoctorApplyNum(doctorId,applyType));
	}
	
	/**
	 * 
	 * @api {get/post} /consultation/friend/collectOperate 主诊医生收藏或取消收藏会诊医生
	 * @apiVersion 1.0.0
	 * @apiName collectOperate
	 * @apiGroup 会诊好友关系
	 * @apiDescription 使用场景： 主诊医生收藏或取消收藏会诊医生
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			unionDoctorId 	                         医生id(主诊医生既当前医生Id)  必传
	 * @apiParam {Integer} 			consultationDoctorId 	 会诊医生id   必传
	 * @apiParam {Integer} 			operateIndex 	                         操作指令（1：收藏，2：取消收藏） 必传
	 * 
	 * @apiSuccess {Integer}        data                    1 
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "collectOperate")
	public JSONMessage collectOperate(@RequestParam(required = true) Integer unionDoctorId,
							   @RequestParam(required = true) Integer consultationDoctorId,
							   @RequestParam(required = true) Integer operateIndex
							   ){
		consultationFriendServiceImpl.collectOperate(unionDoctorId,consultationDoctorId,operateIndex);
		return JSONMessage.success();
	}
	
}
