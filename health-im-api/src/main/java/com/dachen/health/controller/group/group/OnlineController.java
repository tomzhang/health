package com.dachen.health.controller.group.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.group.schedule.entity.param.OnlineParam;
import com.dachen.health.group.schedule.service.IOnlineService;
import com.dachen.util.JSONUtil;

/**
 * ProjectName： health-group-webapi<br>
 * ClassName： OnlineController<br>
 * Description： 医生在线值班controller<br>
 * 
 * @author fanp
 * @createTime 2015年8月14日
 * @version 1.0.0
 */
@RestController("group.online")
@RequestMapping("/group/schedule")
public class OnlineController {

    @Autowired
    private IOnlineService onlineService;
    
    /**
     * @api {post} /group/schedule/addOnline 添加线上值班信息
     * @apiVersion 1.0.0
     * @apiName addOnline
     * @apiGroup 排班
     * @apiDescription 集团添加线上值班信息，该接口提交数据为json字符串
     *
     * @apiParam  {String}    access_token                  token
     * @apiParam  {Integer}   departmentId                  科室id
     * @apiParam  {Object[]}  clinicDate                    坐诊时间
     * @apiParam  {Integer}   clinicDate.week               星期，格式为1=星期一，2=星期二，以此类推
     * @apiParam  {Integer}   clinicDate.period             时间段--1=上午;2=下午;3=晚上
     * @apiParam  {Object[]}  clinicDate.doctor             医生信息
     * @apiParam  {Integer}   clinicDate.doctor.doctorId    医生id
     * @apiParam  {String}    clinicDate.doctor.doctorName  医生姓名
     * @apiParam  {String}    clinicDate.doctor.startTime   开始时间，格式0930
     * @apiParam  {String}    clinicDate.doctor.endTime     结束时间，格式1230
     * 
     * @apiExample {javascript} Example usage:
     *      var data = {"departmentId":"55c950d8eb2ef113a0c8e9ac",
     *                  "clinicDate":[{"week":1,"period":1,
     *                      "doctors":[
     *                                {"doctorId":1,"doctorName":"fan","startTime":"0930","endTime":"1230"},
     *                                {"doctorId":2,"doctorName":"bowl","startTime":"0930","endTime":"1230"}
     *                              ]
     *                    }]
     *                  };
     *       $.ajax({
     *         url: "http://localhost/group/schedule/addOnline",
     *         dataType: "json",
     *         data: {"data":JSON.stringify(data),"access_token":"fafef7fe7be84bfeba3e54eef157a8a3"},
     *         type: "POST",
     *         success: function(){
     *           console.log(1);
     *         }
     *       });
     * 
     * @apiSuccess {Number=1} resultCode    返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping(value = "/addOnline" , method = RequestMethod.POST)
    public JSONMessage addOffline(String data) throws CloneNotSupportedException {
        OnlineParam param = JSONUtil.parseObject(OnlineParam.class, data);
        onlineService.add(param);
        return JSONMessage.success();
    }
    
    /**
     * @api {post} /group/schedule/getOnlines 获取组织架构线上值班信息
     * @apiVersion 1.0.0
     * @apiName getOnlines-web
     * @apiGroup 排班
     * @apiDescription 获取组织架构线上值班信息
     *
     * @apiParam    {String}    access_token                  token
     * @apiParam    {Integer}   departmentId                  科室id
     *
     * @apiSuccess  {Integer}   departmentId                  科室id
     * @apiSuccess  {Object[]}  clinicDate                    值班时间数组
     * @apiSuccess  {String}    clinicDate.onlineId           值班id
     * @apiSuccess  {Integer}   clinicDate.week               星期，格式为1=星期一，2=星期二，以此类推
     * @apiSuccess  {Integer}   clinicDate.period             时间段--1=上午;2=下午;3=晚上
     * @apiSuccess  {Object[]}  clinicDate.doctor             医生数组
     * @apiSuccess  {Integer}   clinicDate.doctor.doctorId    医生id
     * @apiSuccess  {String}    clinicDate.doctor.doctorName  医生姓名
     * @apiSuccess  {String}    clinicDate.doctor.startTime   开始时间,格式0930
     * @apiSuccess  {String}    clinicDate.doctor.endTime     结束时间,格式1230
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping(value = "/getOnlines")
    public JSONMessage getOnlines(String departmentId){
        return JSONMessage.success(null,onlineService.getAllByDept(departmentId));
    }
    
    
    /**
     * @api {get} /group/schedule/deleteOnline 删除医生在线值班信息
     * @apiVersion 1.0.0
     * @apiName deleteOnline
     * @apiGroup 排班
     * @apiDescription 删除医生在线值班信息
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}   doctorId              医生id
     * @apiParam  {String}    departmentId          组织架构id
     * @apiParam  {Integer}   week                  星期，格式为1=星期一，2=星期二，以此类推
     * @apiParam  {Integer}   period                时间段--1=上午;2=下午;3=晚上
     * 
     * 
     * @apiSuccess {Number=1} resultCode            返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping(value="/deleteOnline")
    public JSONMessage deleteOffline(OnlineParam param) {
        onlineService.delete(param);
        return JSONMessage.success();
    }
    
}
