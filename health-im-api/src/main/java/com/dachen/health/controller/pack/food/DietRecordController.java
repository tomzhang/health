package com.dachen.health.controller.pack.food;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.food.service.DietRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by fuyongde on 2017/2/23.
 */
@RestController
@RequestMapping("/pack/dietRecord")
public class DietRecordController {

    @Autowired
    private DietRecordService dietRecordService;

    /**
     * @api {get} /pack/dietRecord/addDietRecord	添加一条饮食记录
     * @apiVersion 1.0.0
     * @apiName addDietRecord
     * @apiGroup 饮食记录
     * @apiDescription 添加一条饮食记录
     * @apiParam {String} 	access_token 		    token
     * @apiParam {String} 	foodName                食物名称
     * @apiParam {String} 	reactions               不良反应
     * @apiParam {Long} 	dietTime                进食时间（时间戳，精确到毫秒，如：1487828771000）
     * @apiParam {Integer} 	patientId               患者id
     * @apiSuccess {Integer} 	resultCode			1 成功
     * @apiAuthor 傅永德
     * @date 2017年2月23日
     */
    @RequestMapping("/addDietRecord")
    public JSONMessage addDietRecord(
            @RequestParam(name = "foodName") String foodName,
            @RequestParam(name = "reactions") String reactions,
            @RequestParam(name = "dietTime") Long dietTime,
            @RequestParam(name = "patientId") Integer patientId
    ) {
        dietRecordService.save(foodName, reactions, dietTime, patientId);
        return JSONMessage.success();
    }

    /**
     * @api {get} /pack/dietRecord/getDietRecords	获取饮食记录列表
     * @apiVersion 1.0.0
     * @apiName addDietRecord
     * @apiGroup 饮食记录
     * @apiDescription 获取饮食记录列表
     * @apiParam   {String} 	access_token 		        token
     * @apiParam   {Integer} 	patientId                   患者id
     * @apiParam   {Integer} 	pageIndex                   起始页
     * @apiParam   {Integer} 	pageSize                    页面大小
     * @apiSuccess {Integer} 	resultCode			        1 成功
     * @apiSuccess {Long} 	    data.pageData.createTime    创建时间
     * @apiSuccess {Long} 	    data.pageData.dietTime      进食时间
     * @apiSuccess {String} 	data.pageData.foodName      食物名称
     * @apiSuccess {String} 	data.pageData.reactions     不良反应
     * @apiAuthor 傅永德
     * @date 2017年2月23日
     */
    @RequestMapping("/getDietRecords")
    public JSONMessage getDietRecords(
            @RequestParam(name = "patientId") Integer patientId,
            @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return JSONMessage.success(dietRecordService.findByPatientId(patientId, pageIndex, pageSize));
    }
}
