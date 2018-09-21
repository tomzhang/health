package com.dachen.health.controller.pack.consultation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.consult.Service.ConsultationPackService;
import com.dachen.health.pack.consult.entity.po.GroupConsultationPack;
import com.dachen.health.pack.consult.entity.vo.ConsultationPackParams;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("/consultation/pack/")
public class ConsultationPackController {

	@Autowired
	ConsultationPackService consultationPackServiceImpl;
	
	/**
	 * 
	 * @api {get/post} /consultation/pack/getList 获取集团会诊包列表
	 * @apiVersion 1.0.0
	 * @apiName getList
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 使用场景：web后台点击会诊策略类型进入会诊包列表页面
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 			  集团id
	 * @apiParam {Integer} 			pageIndex 			 页码
	 * @apiParam {Integer} 			pageSize 			一页多少条
	 * 
	 * @apiSuccess {Integer}         pageCount                   总页数
	 * @apiSuccess {List}            pageData                    对象列表
     * @apiSuccess {String}          pageData.id                 会诊包id
     * @apiSuccess {String}          pageData.consultationPackDesc     会诊包描述
     * @apiSuccess {Integer}         pageData.maxFee             最大价格       
     * @apiSuccess {Integer}         pageData.minFee             最小价格
     * @apiSuccess {Integer}         pageData.groupPercent       集团比例
     * @apiSuccess {Integer}         pageData.consultationDoctorPercent 会诊医生比例  
     * @apiSuccess {Integer}         pageData.unionDoctorPercent        医联体医生比例
     * @apiAuthor  wangl
     * @date 2016年1月14
	 */
	@RequestMapping(value = "getList")
	public JSONMessage getConsultPackList(ConsultationPackParams consultationPackParams){
		return JSONMessage.success(consultationPackServiceImpl.getConsultPackList(consultationPackParams));
	}
	
	/**
	 * 
	 * @api {get/post} /consultation/pack/getDetail 获取集团会诊包详情
	 * @apiVersion 1.0.0
	 * @apiName getDetail
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 使用场景：从会诊包列表中点击编辑进入编辑页面
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			consultationPackId 		会诊包id
	 * 
     * @apiSuccess {String} 			groupId 		                          集团Id
	 * @apiSuccess {String} 			consultationPackTitle 	    会诊包标题
	 * @apiSuccess {String} 			consultationPackDesc 		会诊包描述
	 * @apiSuccess {Integer} 			consultationPrice 		            会诊包价格
	 * @apiSuccess {Integer} 			consultationDoctor 	                       会诊主医生ID
	 * @apiSuccess {Integer} 			consultationDoctorPercent 	会诊主医生分成比例
	 * @apiSuccess {String} 			doctorPercents          	参与医生比例与参与医生ID 例：  "{\"doctor1\":percent1,\"doctor2\":percent2}";
	 * @apiSuccess {List} 			    doctorInfoList          	医生信息
     * @apiAuthor  wangl
     * @date 2016年1月14
	 */
	@RequestMapping(value = "getDetail")
	public JSONMessage getConsultPackDetail(@RequestParam(required = true) String consultationPackId){
		return JSONMessage.success(consultationPackServiceImpl.getConsultPackDetail(consultationPackId));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/pack/getDoctorList 获取集团会诊包详情
	 * @apiVersion 1.0.0
	 * @apiName getDoctorList
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 使用场景：从会诊包列表中点击编辑进入编辑页面
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			consultationPackId 		会诊包id
	 * 
	 * data
	 * @apiSuccess {List}            data                   医生集合
     * @apiSuccess {Integer}         data.userId            医生id
     * @apiSuccess {String}          data.name              医生名字
     * @apiSuccess {String}          data.headPicFileName   图像中的医生名字
     * @apiSuccess {String}          data.doctorGroupName   集团中的医生名字
     * @apiAuthor  wangl
     * @date 2016年1月14
	 */
	@RequestMapping(value = "getDoctorList")
	public JSONMessage getDoctorList(@RequestParam(required = true) String consultationPackId){
		return JSONMessage.success(consultationPackServiceImpl.getDoctorList(consultationPackId));
	}
	
	/**
	 * 
	 * @throws HttpApiException 
	 * @api {get/post} /consultation/pack/update 修改会诊包  以及 添加和删除会诊包医生
	 * @apiVersion 1.0.0
	 * @apiName update
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 使用场景：在会诊包编辑页面点击保存按钮，在会诊包添加医生页面点击确定
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			id 		                                                会诊包id
	* @apiParam {String} 			consultationPackTitle 		                                     会诊包标题
	 * @apiParam {String} 			consultationPackDesc 		会诊包描述
	 * @apiParam {Integer} 			consultationPrice 		            会诊包价格
	 * @apiParam {Integer} 			consultationDoctor 	                       会诊主医生ID
	 * @apiParam {Integer} 			consultationDoctorPercent 	会诊主医生分成比例
	 * @apiParam {String} 			doctorPercents          	参与医生比例与参与医生ID 例：  "{\"doctor1\":percent1,\"doctor2\":percent2}";
	 * 
	 * @apiSuccess {Integer}         resultCode(1)              操作成功
     * @apiAuthor  wangl
     * @date 2016年1月14
	 */
	@RequestMapping(value = "update")
	public JSONMessage updateConsultPack(ConsultationPackParams param) throws HttpApiException{
		consultationPackServiceImpl.updateConsultPack(param);
		return JSONMessage.success();
	}
	
	/**
	 * 
	 * @throws HttpApiException 
	 * @api {get/post} /consultation/pack/add 添加集团会诊包
	 * @apiVersion 1.0.0
	 * @apiName add
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 使用场景：在会诊包新增页面点击保存按钮
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 		                          集团Id
	 * @apiParam {String} 			consultationPackTitle 	    会诊包标题
	 * @apiParam {String} 			consultationPackDesc 		会诊包描述
	 * @apiParam {Integer} 			consultationPrice 		            会诊包价格
	 * @apiParam {Integer} 			consultationDoctor 	                       会诊主医生ID
	 * @apiParam {Integer} 			consultationDoctorPercent 	会诊主医生分成比例
	 * @apiParam {String} 			doctorPercents          	参与医生比例与参与医生ID 例：  "{\"doctor1\":percent1,\"doctor2\":percent2}";
	 * 
     * @apiSuccess {String}             id                 会诊包id
     * @apiSuccess {String} 			groupId 		                          集团Id
	 * @apiSuccess {String} 			title 		                                     会诊包标题
	 * @apiSuccess {String} 			consultationPackDesc 		会诊包描述
	 * @apiSuccess {Integer} 			consultationPrice 		            会诊包价格
	 * @apiSuccess {Integer} 			consultationDoctor 	                       会诊主医生ID
	 * @apiSuccess {Integer} 			consultationDoctorPercent 	会诊主医生分成比例
     * @apiAuthor  wangl
     * @date 2016年1月14
	 */
	@RequestMapping(value = "add")
	public JSONMessage addConsultPack(ConsultationPackParams param) throws HttpApiException{
		return JSONMessage.success(consultationPackServiceImpl.addConsultPack(param));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/pack/getNotInCurrentPackDoctorIds 获取集团没有在当前会诊包的医生ids
	 * @apiVersion 1.0.0
	 * @apiName getNotInCurrentPackDoctorIds
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 使用场景：后台管理系统web页面加载集团医生的树形结构
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 		                          集团Id
	 * @apiParam {String} 			consultationPackId 		 会诊包Id
	 * 
     * @apiSuccess {List}           data                    医生ids
     * @apiAuthor  wangl
     * @date 2016年1月14
	 */
	@RequestMapping(value = "getNotInCurrentPackDoctorIds")
	public JSONMessage getNotInCurrentPackDoctorIds(@RequestParam(required = true) String groupId,
													String consultationPackId){
		return JSONMessage.success(consultationPackServiceImpl.getNotInCurrentPackDoctorIds(groupId,consultationPackId));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/pack/delete 删除集团会诊包
	 * @apiVersion 1.0.0
	 * @apiName delete
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 使用场景：删除集团会诊包
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			consultationPackId 		 会诊包Id
	 * 
     * @apiSuccess {Integer}         resultCode             1:成功删除，2：删除失败
     * @apiAuthor  wangl
     * @date 2016年1月14
	 */
	@RequestMapping(value = "delete")
	public JSONMessage deleteConsultPack(@RequestParam(required = true) String consultationPackId){
		int code = consultationPackServiceImpl.deleteConsultPack(consultationPackId);
		return new JSONMessage(code,null);
	}
	
	/**
	 * 
	 * @api {get/post} /consultation/pack/openService 集团开通会诊包
	 * @apiVersion 1.0.0
	 * @apiName openService
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 使用场景：集团进入后台管理系统点击开通会诊服务按钮
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 		                        集团id
	 * 
     * @apiSuccess {Integer}         resultCode             1:成功删除
     * @apiAuthor  wangl
     * @date 2016年1月18日
	 */
	@RequestMapping(value = "openService")
	public JSONMessage openService(@RequestParam(required = true) String groupId){
		consultationPackServiceImpl.openService(groupId);
		return JSONMessage.success();
	}
	
	/**
	 * 
	 * @api {get/post} /consultation/pack/getOpenConsultation 获取集团开通会诊包状态
	 * @apiVersion 1.0.0
	 * @apiName getOpenConsultation
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 使用场景：获取集团开通会诊包状态
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 		                        集团id
	 * 
     * @apiSuccess {Integer}         resultCode             返回码
     * @apiSuccess {Integer}         data             		1：已开通，2：未开通
     * @apiAuthor  wangl
     * @date 2016年3月18日
	 */
	@RequestMapping(value = "getOpenConsultation")
	public JSONMessage getOpenConsultation(@RequestParam(required = true) String groupId){
		return JSONMessage.success(consultationPackServiceImpl.getOpenConsultation(groupId));
	}
	
	
	
	/*	Map<String, Object> userMap = new HashMap<String, Object>();
	userMap.put("userId", u.getUserId());
	userMap.put("name", u.getName());
	userMap.put("telephone", u.getTelephone());
	userMap.put("createTime", u.getCreateTime());
	userMap.put("status", u.getStatus());
	userMap.put("title", u.getDoctor().getTitle());
	userMap.put("departments", u.getDoctor().getDepartments());
	userMap.put("hospital", u.getDoctor().getHospital());
	userMap.put("headPicFileName", u.getHeadPicFileName());*/
	
	
	/**
	 * 
	 * @api {get/post} /consultation/pack/getPlatformSelectedDoctors 获取不在当前会诊包的平台集团医生
	 * @apiVersion 1.0.0
	 * @apiName getPlatformSelectedDoctors
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 使用场景： 获取不在当前会诊包的平台集团医生
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 		 		集团id
	 * @apiParam {String} 			consultationPackId 		 会诊包id
	 * @apiParam {String} 			keyWord 		 		关键字
	 * @apiParam {Integer} 			pageIndex 				页码数
	 * @apiParam {Integer} 			pageSize 				分页条数
	 * 
     * @apiSuccess {String}         resultCode             返回码
     * @apiSuccess {Integer}         userId             	用户id
     * @apiSuccess {String}         name             		名字
     * @apiSuccess {String}         telephone             	电话
     * @apiSuccess {Long}         createTime             	注册时间
     * @apiSuccess {String}         status             		状态
     * @apiSuccess {String}         title             		职称
     * @apiSuccess {String}         departments             不猛
     * @apiSuccess {String}         hospital             	医院
     * @apiSuccess {String}         headPicFileName         头像
     * @apiAuthor  wangl
     * @date 2016年3月28日
	 */
	@RequestMapping(value = "getPlatformSelectedDoctors")
	public JSONMessage getPlatformSelectedDoctors(@RequestParam(required = true)String groupId , 
													String consultationPackId ,
													String keyWord ,
													Integer pageIndex ,
													Integer pageSize){
		return JSONMessage.success(consultationPackServiceImpl.getPlatformSelectedDoctors(groupId,consultationPackId,keyWord,pageIndex,pageSize));
	}
	
	@RequestMapping(value = "syncData")
	public JSONMessage syncData() throws HttpApiException{
		consultationPackServiceImpl.syncData();
		return JSONMessage.success();
	}
	
	/**
	 * 
	 * @api {get/post} /consultation/pack/getNewConsultationPackList 获取会诊医生的会诊包
	 * @apiVersion 1.0.0
	 * @apiName getNewConsultationPackList
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 获取作为参与医生的会诊包列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 			  集团id
	 * @apiParam {Integer} 			doctorId 			 医生ID，默认当前登录用户
	 * @apiParam {Integer} 			pageIndex 			 页码
	 * @apiParam {Integer} 			pageSize 			一页多少条
	 * 
	 * @apiSuccess {Integer}         pageCount                   总页数
	 * @apiSuccess {List}            pageData                    对象列表
     * @apiSuccess {String}          pageData.id                 会诊包id
     * @apiSuccess {String}          pageData.consultationPackDesc       会诊包描述
     * @apiSuccess {String}          pageData.consultationPackTitle      会诊包标题
     * @apiSuccess {Integer}         pageData.consultationPrice          会诊包价格       
     * @apiSuccess {Integer}         pageData.times          发起次数      
     * @apiSuccess {Integer}         pageData.doctorId          主医生ID 
     * @apiSuccess {Integer}         pageData.doctorPic         主医生头相 
     * @apiSuccess {String}          pageData.doctorName        主医生名字      
     * @apiSuccess {String}          pageData.doctorTitle        主医生职称      
     * @apiSuccess {String}          pageData.doctorHostpital     主医生医院      
     * @apiSuccess {String}          pageData.doctorDept          主医生科室  
     * @apiSuccess {List}            pageData.doctorList             会诊包医生列表
     * @apiSuccess {Integer}         pageData.doctorList.id             参与医生Id
     * @apiSuccess {String}          pageData.doctorList.pic             参与医生头像
     * @apiSuccess {String}          pageData.doctorList.name             参与医生名字
     * @apiAuthor  zhy
     * @date 2017年2月23
	 */
	@RequestMapping("getNewConsultationPackList")
	public JSONMessage getConsultationPackList(ConsultationPackParams consultationPackParams){
		if(consultationPackParams.getDoctorId() == null){
			consultationPackParams.setDoctorId(ReqUtil.instance.getUserId());
		}
		return JSONMessage.success(consultationPackServiceImpl.getConsultationPackList(consultationPackParams));
	}
	
	
	/**
	 * 
	 * @api {get/post} /consultation/pack/getMyConsultationPackList 查询 当前医生 作为主诊医生会诊包列表
	 * @apiVersion 1.0.0
	 * @apiName getMyConsultationPackList
	 * @apiGroup 集团会诊包设置
	 * @apiDescription 查询 当前医生 作为主诊医生会诊包列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			groupId 			  集团id
	 * @apiParam {Integer} 			consultationDoctor 			 医生ID，默认当前登录用户
	 * @apiParam {Integer} 			pageIndex 			 页码
	 * @apiParam {Integer} 			pageSize 			一页多少条
	 * 
	 * @apiSuccess {Integer}         pageCount                   总页数
	 * @apiSuccess {List}            pageData                    对象列表
     * @apiSuccess {String}          pageData.id                 会诊包id
     * @apiSuccess {String}          pageData.consultationPackDesc       会诊包描述
     * @apiSuccess {String}          pageData.consultationPackTitle      会诊包标题
     * @apiSuccess {Integer}         pageData.consultationPrice          会诊包价格       
     * @apiSuccess {Integer}         pageData.doctorId          主医生ID 
     * @apiSuccess {Integer}         pageData.doctorPic         主医生头相 
     * @apiSuccess {String}          pageData.doctorName        主医生名字      
     * @apiSuccess {String}          pageData.doctorTitle        主医生职称      
     * @apiSuccess {String}          pageData.doctorHostpital     主医生医院      
     * @apiSuccess {String}          pageData.doctorDept          主医生科室  
     * @apiSuccess {List}            pageData.doctorList             会诊包医生列表
     * @apiSuccess {Integer}         pageData.doctorList.id             参与医生Id
     * @apiSuccess {String}          pageData.doctorList.pic             参与医生头像
     * @apiSuccess {String}          pageData.doctorList.name             参与医生名字
     * @apiAuthor  zhy
     * @date 2017年2月23
	 */
	@RequestMapping("getMyConsultationPackList")
	public JSONMessage getMyConsultationPackList(ConsultationPackParams consultationPackParams){
		if(consultationPackParams.getConsultationDoctor() == null){
			consultationPackParams.setConsultationDoctor(ReqUtil.instance.getUserId());
		}
		return JSONMessage.success(consultationPackServiceImpl.getMyConsultationPackList(consultationPackParams));
	}
}
