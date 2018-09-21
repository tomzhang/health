package com.dachen.health.controller.schedule;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.group.schedule.entity.param.OfflineParam;
import com.dachen.health.group.schedule.service.IOfflineService;
import com.dachen.util.DateUtil;
import com.dachen.util.JSONUtil;
import com.dachen.util.ReqUtil;

/**
 * ProjectName： health-im-api<br>
 * ClassName： OfflineController<br>
 * Description： 医生线下门诊controller<br>
 * 
 * @author fanp
 * @createTime 2015年8月12日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/schedule")
public class OfflineController {

    @Autowired
    private IOfflineService offlineService;

    /**
     * @api {post} /schedule/addOffline 医生添加门诊信息
     * @apiVersion 1.0.0
     * @apiName addOffline
     * @apiGroup 排班
     * @apiDescription 医生添加门诊信息，该接口提交数据为json字符串
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}   hospital              坐诊医院名称
     * @apiParam  {Integer}   hospitalId              坐诊医院Id
     * @apiParam  {Integer}   clinicType            门诊类型--1=普通;2=专家;3=特需
     * @apiParam  {Long}      price                 价格，单位分
     * @apiParam  {Object[]}  clinicDate            坐诊时间
     * @apiParam  {Integer}   clinicDate.week       星期，格式为1=星期一，2=星期二，以此类推
     * @apiParam  {Integer[]} clinicDate.period     时间段--1=上午;2=下午;3=晚上
     * @apiParam  {Long} clinicDate.startTime     医生坐诊开始时间
     * @apiParam  {Long} clinicDate.endTime     医生坐诊结束时间
     * 
     * @apiExample {javascript} Example usage:
     *      var data = {"hospital":"北医三院","hospitalId":"XXXX","clinicType":1,"price":500,
     *                  "clinicDate":[
     *                                {"week":1,"period":[1],"startTimeString":"2:4","endTimeString":"05:00"},
     *                                {"week":2,"period":[2],"startTimeString":"02:4","endTimeString":"5:30"},
     *                                {"week":3,"period":[3],"startTimeString":"2:04","endTimeString":"15:00"}
     *                               ]
     *                 };
     *      $.ajax({
     *          url: "http://192.168.3.7:8091/schedule/addOffline",
     *          dataType: "json",
     *          data: {"data":JSON.stringify(data),"access_token":"fafef7fe7be84bfeba3e54eef157a8a3"},
     *          type: "POST",
     *          success: function(){
     *              console.log(1);
     *          }
     *      });
     * 
     * @apiSuccess {Number=1} resultCode    返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping(value = "/addOffline" , method = RequestMethod.POST)
    public JSONMessage addOffline(String data) throws CloneNotSupportedException {
        OfflineParam param = JSONUtil.parseObject(OfflineParam.class, data);
        //param.setDoctorId(793);
        param.setDoctorId(ReqUtil.instance.getUserId());
        offlineService.add(param);
        return JSONMessage.success();
    }
    
    /**
     * @api {get} /schedule/getOfflines 获取医生所有门诊信息
     * @apiVersion 1.0.0
     * @apiName getOfflines
     * @apiGroup 排班
     * @apiDescription 获取医生所有门诊信息
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}   doctorId              医生id，如果为空则查询当前登录用户的门诊信息
     * @apiParam  {Integer}   is_hospital_group     出诊信息是否按照医院分组(0-否  1-是  默认为0)
     * @apiParam  {String}    lat                   用户当前经度(非必填)
     * @apiParam  {String}    lng 					用户当前纬度(非必填)
     * 
     * @apiSuccess {String[]} hospital              医院数组
     * @apiSuccess {Integer[]}days                  日期数组
     * @apiSuccess {Object[]} offline               排班数据
     * @apiSuccess {String}   offline.id            id
     * @apiSuccess {Number}   offline.clinicType    门诊类型--1=普通;2=专家;3=特需
     * @apiSuccess {String}   offline.hospital      医院
     * @apiSuccess {Integer}  offline.hospitalId    坐诊医院Id
     * @apiSuccess {Long}     offline.price         价格，单位分
     * @apiSuccess {Integer}  offline.week          星期，格式为1=星期一，2=星期二，以此类推
     * @apiSuccess {Integer}  offline.period        时间段--1=上午;2=下午;3=晚上
     * @apiSuccess {Long}  offline.startTime     开始时间戳
     * @apiSuccess {Long}  offline.endTime       结束时间戳
     * @apiSuccess {String}  offline.startTimeString     开始时间字符串
     * @apiSuccess {String}  offline.endTimeString       结束时间字符串
     * @apiSuccess {String}  hospital.distance      距离当前用户的距离km
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getOfflines")
    public JSONMessage getOfflines(Integer doctorId,@RequestParam(defaultValue="0") Integer is_hospital_group,String lat,String lng) {
        Integer[] days = DateUtil.getWeekDays(null);
        
        if(doctorId == null){
            doctorId = ReqUtil.instance.getUserId();
        }
        
        Map<String,Object> map = offlineService.getAll(doctorId,0, lat, lng,is_hospital_group);
        map.put("days", days);
        
        return JSONMessage.success(null, map);
    }

    
    /**
     * @api {get} /schedule/getOffline 获取医生单个门诊信息
     * @apiVersion 1.0.0
     * @apiName getOffline
     * @apiGroup 排班
     * @apiDescription 获取医生单个门诊信息
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    id                    id
     * 
     * @apiSuccess {String}   id                    id
     * @apiSuccess {Number}   clinicType            门诊类型--1=普通;2=专家;3=特需
     * @apiSuccess {String}   hospital              医院
     * @apiSuccess {Integer}   hospitalId             医院ID
     * @apiSuccess {Long}     price                 价格，单位分
     * @apiSuccess {Integer}  week                  星期，格式为1=星期一，2=星期二，以此类推
     * @apiSuccess {Integer}  period                时间段--1=上午;2=下午;3=晚上
     * @apiSuccess {Long}  startTime     开始时间戳
     * @apiSuccess {Long}  endTime       结束时间戳
     * @apiSuccess {String}  startTimeString     开始时间字符串
     * @apiSuccess {String}  endTimeString       结束时间字符串

     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getOffline")
    public JSONMessage getOffline(OfflineParam param) {
        param.setDoctorId(ReqUtil.instance.getUserId());
        return JSONMessage.success(null, offlineService.getOne(param));
    }
    
    
    /**
     * @api {post} /schedule/updateOffline 修改医生门诊信息
     * @apiVersion 1.0.0
     * @apiName updateOffline
     * @apiGroup 排班
     * @apiDescription 修改医生门诊信息
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    id                    id
     * @apiParam  {Integer}   clinicType            可选，门诊类型--1=普通;2=专家;3=特需
     * @apiParam  {String}    hospital              医院
     * @apiParam {Integer}   hospitalId            医院ID
     * @apiParam  {Long}      price                 价格，单位分
     * @apiParam  {Integer}   week                  格式为1=星期一，2=星期二，以此类推；与period不为空时不可为空
     * @apiParam  {Integer}   period                时间段--1=上午;2=下午;3=晚上；与week不为空时不可为空
     * @apiParam  {Long}  startTime          		 医生坐诊开始时间
     * @apiParam  {String}  startTimeString           医生坐诊开始时间（字符串形式）
     * @apiParam  {Long}  endTime         			医生坐诊结束时间
     * @apiParam  {String}  endTimeString           医生坐诊结束时间（字符串形式）
     * 
     * @apiSuccess {Number=1} resultCode    返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/updateOffline")
    public JSONMessage updateOffline(OfflineParam param) {
        param.setDoctorId(ReqUtil.instance.getUserId());
        offlineService.update(param);
        return JSONMessage.success();
    }
    
    /**
     * @api {get} /schedule/deleteOffline 删除医生门诊信息
     * @apiVersion 1.0.0
     * @apiName deleteOffline
     * @apiGroup 排班
     * @apiDescription 删除医生门诊信息
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    id                    可选，id
     * @apiParam {Integer}   doctorId            可选， 医生ID
     * @apiParam {Integer}   hospitalId            可选， 医院ID
     * @apiParam  {Integer}   week                  可选，星期，格式为1=星期一，2=星期二，以此类推；与period不为空时不可为空
     * @apiParam  {Integer}   period                可选，时间段--1=上午;2=下午;3=晚上；与week不为空时不可为空
     * @apiParam  {Long}  startTime           可选，医生坐诊开始时间
     * @apiParam  {Long}  endTime         可选，医生坐诊结束时间
     * @apiSuccess {Number=1} resultCode    返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping(value="/deleteOffline")
    public JSONMessage deleteOffline(OfflineParam param) {
        param.setDoctorId(ReqUtil.instance.getUserId());
        offlineService.delete(param);
        return JSONMessage.success();
    }
    
    
    /**
     * @api {post} /schedule/hasAppointment 判断医生该时间段是否有被患者预约
     * @apiVersion 1.0.0
     * @apiName hasAppointment
     * @apiGroup 排班
     * @apiDescription 判断医生该时间段是否有被患者预约
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    id                    id， 医生预约排班表ID

     * 
     * @apiSuccess {Boolean} data    该时间段是否有被患者预约
     *
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
     * @api {get/post} /schedule/getDoctorOfflineItems 患者获取医生的一周的预约信息
     * @apiVersion 1.0.0
     * @apiName getDoctorOfflineItems
     * @apiGroup 排班
     * @apiDescription 患者获取医生的一周的预约信息
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}    doctorId              医生id
     * @apiParam  {String}    hospitalId            医院id
     * @apiParam  {Long}    startTime           	起始日期时间戳
     * 
     * @apiSuccess {Number=1} resultCode    返回状态吗
     * @apiSuccess {Long}      key    日期时间戳
     * @apiSuccess {Integer}   period    1:早，2：下午，3：晚上
     * @apiSuccess {Boolean}   isAppoint true: 已约满 ， false：没约满  null：没排班
     * 
	 * @apiExample {javascript} Example usage:
     *          {   1465747200000={1=false, 2=true, 3=null}, 
	 *              1465833600000={1=null, 2=null, 3=null}, 
	 *              1465920000000={1=null, 2=null, 3=null}, 
	 *  			1466006400000={1=null, 2=null, 3=null}, 
	 *  			1466092800000={1=null, 2=null, 3=null}, 
	 *  			1466179200000={1=null, 2=null, 3=null}, 
	 *  			1466265600000={1=null, 2=null, 3=null}
	 *         }
	 *  
     *      
     * @apiAuthor  wangl
     * @date 2016年6月13日13:08:51
     */
    @RequestMapping(value="/getDoctorOfflineItems")
    public JSONMessage getDoctorOfflineItems(@RequestParam(required=true) Integer doctorId,
    										 @RequestParam(required=true) String hospitalId,
    										 @RequestParam(required=true) Long  startTime) {
        return JSONMessage.success(offlineService.getDoctor7DaysOfflineItems(doctorId,hospitalId,startTime));
    }
    
    
    /**
     * @api {get/post} /schedule/offlineItemDetail 患者获取医生的某一时间段的预约详情
     * @apiVersion 1.0.0
     * @apiName offlineItemDetail
     * @apiGroup 排班
     * @apiDescription 患者获取医生的某一时间段的预约详情
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}    doctorId              医生id
     * @apiParam  {String}    hospitalId            医院id
     * @apiParam  {Integer}    period            1：早，2：午，3：晚
     * @apiParam  {Long}    dateTime            日期时间戳
     * 
     * @apiSuccess {Number=1} resultCode    返回状态吗
     * @apiSuccess {Long}   startTime    起始时间
     * @apiSuccess {Long}   endTime      结束时间
     * @apiSuccess {Integer}   status     预约状态  1: 待预约 ， 2：已预约 ， 3：已开始，4: 已完成
     * 
     * @apiAuthor  wangl
     * @date 2016年6月13日13:08:51
     */
    @RequestMapping(value="/offlineItemDetail")
    public JSONMessage offlineItemDetail(@RequestParam(required=true) Integer doctorId,
    										 @RequestParam(required=true) String hospitalId,
    										 @RequestParam(required=true) Integer period,
    										 @RequestParam(required=true) Long dateTime) {
        return JSONMessage.success(offlineService.offlineItemDetail(doctorId,hospitalId,period,dateTime));
    }
    
}
