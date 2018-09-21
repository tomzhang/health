package com.dachen.health.controller.schedule;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.UserSession;
import com.dachen.health.group.group.entity.vo.HospitalInfo;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.schedule.entity.param.OfflineParam;
import com.dachen.health.group.schedule.entity.po.Offline;
import com.dachen.health.group.schedule.entity.vo.DoctorOfflineVO;
import com.dachen.health.group.schedule.entity.vo.DoctorVO;
import com.dachen.health.group.schedule.service.IOfflineService;
import com.dachen.util.ReqUtil;

/**
 * ProjectName： health-im-api<br>
 * ClassName： DoctorScheduleController<br>
 * Description： 医生排班controller<br>
 * 
 * @author CQY
 * @createTime 2016年6月22日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/doctorSchedule")
public class DoctorScheduleController {

    @Autowired
    private IOfflineService offlineService;
    
    /**
     * @api {post} /doctorSchedule/addOffline 增加医生排班信息
     * @apiVersion 1.0.0
     * @apiName addOffline
     * @apiGroup 排班（博德嘉联）
     * @apiDescription 添加医生排班信息
     *
     * @apiParam  {String}    access_token 	token
     * @apiParam  {String}	doctorId      	医生ID，必传
     * @apiParam  {String}	hospitalId      坐诊医院ID，必传
     * @apiParam  {String}	hospital      	坐诊医院名称，必传
     * @apiParam  {Integer}  clinicType      门诊类型--1=普通;2=专家;3=特需，可选
     * @apiParam  {Long}     price             	价格，单位分，可选
     * @apiParam  {Integer}  week       		星期，格式为1=星期一，2=星期二，以此类推，必传
     * @apiParam  {Integer}  	period     		时间段--1=上午;2=下午;3=晚上，必传
     * @apiParam  {Long}		startTime     	医生坐诊开始时间，必传
     * @apiParam  {Long}		endTime    	医生坐诊结束时间，必传
     * @apiParam  {Long}		dateTime    	医生排班日期，必传
     * @apiParam  {String}	startTimeString    	医生坐诊开始时间(字符串形式)
     * @apiParam  {String}	endTimeString    	医生排班结束结束(字符串形式)
     * @apiParam	{String}		dateTimeString		医生排班日期(字符串形式)
     * 
     * @apiSuccess {Number=1} resultCode    返回状态吗
     *
     * @apiAuthor  CQY
     * @date 2016年6月22日
     */
    @RequestMapping(value = "/addOffline" , method = RequestMethod.POST)
    public JSONMessage addOffline(OfflineParam param){
    	if(param.getDoctorId()==null){
    		param.setDoctorId(ReqUtil.instance.getUserId());
    	}
    	Boolean flag=offlineService.hasOffline(param);
    	if(flag){
        	return JSONMessage.failure("该医生在该时间段已有排班！");
    	}else{
    		offlineService.addOffline(param);
        	return JSONMessage.success();
    	}
    }
    
    
    /**
     * @api {get} /doctorSchedule/getOfflinesForWeb 按照条件查询医生排班信息(Web端)
     * @apiVersion 1.0.0
     * @apiName getOfflinesForWeb
     * @apiGroup 排班（博德嘉联）
     * @apiDescription 按照条件查询医生排班信息，返回结果按照医生分组，且需分页
     *
     * @apiParam  {String}    	access_token          token
     * @apiParam  {String}   		hospitalId   			医院id，必传
     * @apiParam  {Long}   		startTime        		起始日期，必传
     * @apiParam  {Long}   		endTime         		截止日期，必传
     * 
     * 
     * @apiSuccess {String}   			doctor   						医生唯一标记
     * @apiSuccess {List}  				offlineList  						一周的排班信息
     * @apiSuccess {Object}     			offline.id         				排班ID
     * @apiSuccess {Integer}     			offline.doctorId         		排班医生ID
     * @apiSuccess {String}     			offline.hospital         		排班医院名称
     * @apiSuccess {String}     			offline.hospitalId         	排班医院ID
     * @apiSuccess {Integer}     			offline.clinicType         	门诊类型
     * @apiSuccess {Long}     			offline.price         			价格，单位分
     * @apiSuccess {Integer}  			offline.week          			星期，格式为1=星期一，2=星期二，以此类推
     * @apiSuccess {Integer}  			offline.period        			时间段--1=上午;2=下午;3=晚上
     * @apiSuccess {Long}  				offline.startTime     		坐诊开始时间戳
     * @apiSuccess {Long}  				offline.endTime      		坐诊结束时间戳
     * @apiSuccess {Long}  				offline.dateTime     		排班日期时间戳
     * 
     * @apiAuthor  CQY
     * @date 2016年6月22日
     */
    @RequestMapping("/getOfflinesForWeb")
    public JSONMessage getOfflinesForWeb(OfflineParam param){
    	List<Map<String,Object>> map=offlineService.queryByConditionsForWeb(param);
    	return JSONMessage.success(null,map);
    }
    
    
    /**
     * @api {get} /doctorSchedule/getOfflinesForClient 按照条件查询医生排班信息(客户端)
     * @apiVersion 1.0.0
     * @apiName getOfflinesForClient
     * @apiGroup 排班（博德嘉联）
     * @apiDescription 按照条件查询医生排班信息，返回结果按照医生分组，且需分页
     *
     * @apiParam  {String}    	access_token          token
     * @apiParam  {String}   	hospitalId   			医院id，必传
     * @apiParam  {Integer}   	doctorId              	医生id，必传
     * 
     * 
     * @apiSuccess {List}   		DoctorOfflineVO    			排班数据
     * @apiSuccess {Integer}   		DoctorOfflineVO.index    第几周
     * @apiSuccess {Integer[]}  		DoctorOfflineVO.days    	一周的日期列表
     * @apiSuccess {Map}  			DoctorOfflineVO.offlineMap  一周的排班信息
     * @apiSuccess {String}  				offlineMap.key  				weekperiod,用作定位21个小方格
     * @apiSuccess {List}     				offlineMap.value         	每个小方格的排班数据
     * @apiSuccess {Object}     				offline.id         				排班ID
     * @apiSuccess {Integer}     				offline.doctorId         		排班医生ID
     * @apiSuccess {String}     				offline.hospital         		排班医院名称
     * @apiSuccess {String}     				offline.hospitalId         	排班医院ID
     * @apiSuccess {Integer}     				offline.clinicType         	门诊类型
     * @apiSuccess {Long}     				offline.price         			价格，单位分
     * @apiSuccess {Integer}  				offline.week          			星期，格式为1=星期一，2=星期二，以此类推
     * @apiSuccess {Integer}  				offline.period        			时间段--1=上午;2=下午;3=晚上
     * @apiSuccess {Long}  					offline.startTime     		坐诊开始时间戳
     * @apiSuccess {Long}  					offline.endTime      		坐诊结束时间戳
     * @apiSuccess {Long}  					offline.dateTime     		排班日期时间戳
     * 
     * @apiAuthor  CQY
     * @date 2016年6月24日
     */
    @RequestMapping("/getOfflinesForClient")
    public JSONMessage getOfflinesForClient(OfflineParam param){
    	List<DoctorOfflineVO> list=offlineService.queryByConditionsForClient(param);
    	return JSONMessage.success(null,list);
    }
    
    /**
     * @api {get} /doctorSchedule/queryDoctorPeriodOfflines 查询某个医生在哪家医院某个时间段内的所有排班信息
     * @apiVersion 1.0.0
     * @apiName queryDoctorPeriodOfflines
     * @apiGroup 排班（博德嘉联）
     * @apiDescription 查询某个医生在哪家医院某个时间段内的所有排班信息
     *
     * @apiParam  {String}    	access_token          token
     * @apiParam  {Integer}   	hospitalId   			医院id，必传
     * @apiParam  {Integer}   	doctorId              	医生id，必选
     * @apiParam  {Integer}   	period        			时间短，client必传，web可选，1=上午;2=下午;3=晚上
     * @apiParam  {Long}   		dateTime         		排班日期
     * @apiParam  {String}   		dateTimeString      	排班日期（字符串形式）
     * 
     * 
     * @apiSuccess {List}  			offlineList  				某个时间段内的排班信息列表
     * @apiSuccess {Object}     		offline.id         				排班ID
     * @apiSuccess {Integer}     		offline.doctorId         		排班医生ID
     * @apiSuccess {String}     		offline.hospital         		排班医院名称
     * @apiSuccess {String}     		offline.hospitalId         	排班医院ID
     * @apiSuccess {Integer}     		offline.clinicType         	门诊类型
     * @apiSuccess {Long}     		offline.price         			价格，单位分
     * @apiSuccess {Integer}  		offline.week          			星期，格式为1=星期一，2=星期二，以此类推
     * @apiSuccess {Integer}  		offline.period        			时间段--1=上午;2=下午;3=晚上
     * @apiSuccess {Long}  			offline.startTime     		坐诊开始时间戳
     * @apiSuccess {Long}  			offline.endTime      		坐诊结束时间戳
     * @apiSuccess {Long}  			offline.dateTime     		排班日期时间戳
     * 
     * @apiAuthor  CQY
     * @date 2016年6月22日
     */
    @RequestMapping("/queryDoctorPeriodOfflines")
    public JSONMessage queryDoctorPeriodOfflines(OfflineParam param){
    	return JSONMessage.success(null,offlineService.queryDoctorPeriodOfflines(param));
    }
    
    
    /**
     * @api {post} /doctorSchedule/hasAppointment 判断医生该时间段是否有被患者预约
     * @apiVersion 1.0.0
     * @apiName hasAppointment
     * @apiGroup 排班（博德嘉联）
     * @apiDescription 判断医生该时间段是否有被患者预约
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    id                    		id， 医生排班表ID

     * @apiSuccess {Boolean} data    该时间段是否有被患者预约
     * @apiAuthor  CQY
     *
     */
    @RequestMapping(value="/hasAppointment")
    public JSONMessage hasAppointment(OfflineParam param){
    	Boolean flag=false;
    	//判断该时间段内是否有被患者预约
    	flag=offlineService.hasAppointment(param);
    	return JSONMessage.success(null, flag);
    }
    
    
    /**
     * @api {get} /doctorSchedule/deleteOffline 删除医生排班信息
     * @apiVersion 1.0.0
     * @apiName deleteOffline
     * @apiGroup 排班（博德嘉联）
     * @apiDescription 删除医生排班信息
     *
     * @apiParam	{String}    	access_token          token
     * @apiParam	{String}   	id                    		必传，id
     * @apiParam 	{Integer}  	doctorId            	可选， 医生ID
     * @apiParam	{String}   	hospitalId            	可选， 医院ID
     * @apiParam	{Integer}   week                  	可选，星期，格式为1=星期一，2=星期二，以此类推；与period不为空时不可为空
     * @apiParam	{Integer}   period                	可选，时间段--1=上午;2=下午;3=晚上；与week不为空时不可为空
     * @apiParam	{Long}  	startTime           	可选，医生坐诊开始时间
     * @apiParam	{Long}  	endTime         		可选，医生坐诊结束时间
     * 
     * @apiSuccess	{Number=1} resultCode    返回状态码
     *
     * @apiAuthor  CQY
     * @date 2016年6月22日
     */
    @RequestMapping(value="/deleteOffline")
    public JSONMessage deleteOffline(OfflineParam param) {
        //param.setDoctorId(ReqUtil.instance.getUserId());
        offlineService.delete(param);
        return JSONMessage.success();
    }
    
    /**
     * @api {get} /doctorSchedule/getDoctorList 获取医生列表
     * @apiVersion 1.0.0
     * @apiName getDoctorList
     * @apiGroup 排班（博德嘉联）
     * @apiDescription 获取医生列表
     *
     * @apiParam	{String}    			access_token 	token
     * 
     * @apiSuccess {Number=1} 	resultCode    						返回状态码
     *	@apiSuccess {List} 				dcotorList    						医生列表
     *	@apiSuccess {Integer} 			DoctorVO.doctorId 				医生ID
     *	@apiSuccess {String} 			DoctorVO.doctorName		医生名称
     * 	@apiSuccess {String} 			DoctorVO.hospitalName		医院名称
     *	@apiSuccess {String} 			DoctorVO.departments		科室名称
     *	@apiSuccess {String} 			DoctorVO.title					职称
     *
     * @apiAuthor  CQY
     * @date 2016年6月22日
     */
    @RequestMapping(value="/getDoctorList")
    public JSONMessage getDoctorList(){
    	UserSession us =ReqUtil.instance.getUser();
    	List<DoctorVO> dcotorList=offlineService.getDoctorList(us);
    	return JSONMessage.success(null,dcotorList);
    }
    
    /**
     * @api {get} /doctorSchedule/getHospitalList 获取医院列表
     * @apiVersion 1.0.0
     * @apiName getHospitalList
     * @apiGroup 排班（博德嘉联）
     * @apiDescription 获取医院列表
     *
     * @apiParam  {String}    		access_token		token
     * 
     * @apiSuccess {Number=1} 	resultCode				返回状态码
     *	@apiSuccess {List} 				hospitalList    			医院列表
     *	@apiSuccess {String} 			HospitalInfo.id  		医院ID
     *	@apiSuccess {String} 			HospitalInfo.name	医院名称
     *
     * @apiAuthor  CQY
     * @date 2016年6月22日
     */
    @RequestMapping(value="/getHospitalList")
    public JSONMessage getHospitalList(){
    	UserSession us =ReqUtil.instance.getUser();
    	List<HospitalInfo> hospitalList=offlineService.getHospitalList(us);
    	return JSONMessage.success(null,hospitalList);
    }
}
