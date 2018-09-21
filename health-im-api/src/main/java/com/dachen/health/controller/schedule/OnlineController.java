package com.dachen.health.controller.schedule;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.group.schedule.service.IOnlineService;
import com.dachen.util.DateUtil;
import com.dachen.util.ReqUtil;


/**
 * ProjectName： health-im-api<br>
 * ClassName： OnlineController<br>
 * Description： 医生线上值班controller<br>
 * @author fanp
 * @createTime 2015年8月17日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/schedule")
public class OnlineController {

    @Autowired
    private IOnlineService onlineService;

    
    
    /**
     * @api {get} /schedule/getOnlines 获取医生在线值班信息
     * @apiVersion 1.0.0
     * @apiName getOnlines-app
     * @apiGroup 排班
     * @apiDescription 获取医生在线值班信息
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}   doctorId              医生id，如果为空则查询当前登录用户的值班信息
     * 
     * @apiSuccess {String[]} group                 集团数组
     * @apiSuccess {Integer[]}days                  日期数组
     * @apiSuccess {Object[]} online                排班数据
     * @apiSuccess {String}   online.department     科室
     * @apiSuccess {Long}     online.price          价格，单位分
     * @apiSuccess {Integer}  online.week           星期，格式为1=星期一，2=星期二，以此类推
     * @apiSuccess {Integer}  online.period         时间段--1=上午;2=下午;3=晚上
     * @apiSuccess {String}   online.startTime      开始时间，格式0930
     * @apiSuccess {String}   online.endTime        结束时间，格式1200
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getOnlines")
    public JSONMessage getOfflines(Integer doctorId) {
        Integer[] days = DateUtil.getWeekDays(null);
        
        if(doctorId == null){
            doctorId = ReqUtil.instance.getUserId();
        }
        
        Map<String,Object> map = onlineService.getAllByDoctor(doctorId);
        if(map==null){
            map = new HashMap<String,Object>();
        }
        map.put("days", days);
        
        return JSONMessage.success(null, map);
    }
    
}
