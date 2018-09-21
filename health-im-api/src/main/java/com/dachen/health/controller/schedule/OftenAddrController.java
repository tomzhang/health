package com.dachen.health.controller.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.group.schedule.entity.po.OftenAddr;
import com.dachen.health.group.schedule.service.IOftenAddrService;
import com.dachen.util.ReqUtil;

/**
 * ProjectName： health-im-api<br>
 * ClassName： OftenAddrController<br>
 * Description： 医生常用地址controller<br>
 * 
 * @author fanp
 * @createTime 2015年8月12日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/schedule")
public class OftenAddrController {

    @Autowired
    private IOftenAddrService oftenAddrService;
    
    /**
     * @api {get} /schedule/getOftenAddrs 常用地址获取
     * @apiVersion 1.0.0
     * @apiName getOftenAddrs
     * @apiGroup 排班
     * @apiDescription 获取常用地址
     *
     * @apiParam  {String}    access_token          token
     * 
     * @apiSuccess {String}   id                    id
     * @apiSuccess {String}   hospital              医院
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/getOftenAddrs")
    public JSONMessage getOftenAddrs() {
        return JSONMessage.success(null, oftenAddrService.getAll(ReqUtil.instance.getUserId()));
    }

    
    /**
     * @api {get} /schedule/deleteOftenAddr 常用地址删除
     * @apiVersion 1.0.0
     * @apiName deleteOftenAddr
     * @apiGroup 排班
     * @apiDescription 删除常用地址
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    id                    id
     * 
     * @apiSuccess {Number=1} resultCode            返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年7月2日
     */
    @RequestMapping("/deleteOftenAddr")
    public JSONMessage getOffline(OftenAddr po) {
        po.setDoctorId(ReqUtil.instance.getUserId());
        oftenAddrService.delete(po);
        return JSONMessage.success();
    }
    
   
    
}
