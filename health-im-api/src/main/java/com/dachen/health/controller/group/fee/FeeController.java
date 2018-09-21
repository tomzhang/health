package com.dachen.health.controller.group.fee;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.group.fee.entity.param.FeeParam;
import com.dachen.health.group.fee.entity.vo.FeeVO;
import com.dachen.health.group.fee.service.IFeeService;
import com.dachen.health.pack.consult.Service.ConsultationPackService;
import com.dachen.util.ReqUtil;

/**
 * ProjectName： health-im-api<br>
 * ClassName： FeeController<br>
 * Description：医生集团收费设置 <br>
 * @author fanp
 * @createTime 2015年9月18日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/group/fee")
public class FeeController {

    @Autowired
    private IFeeService feeService;
    
    @Autowired
    ConsultationPackService consultationPackServiceImpl;
    /**
     * @api {post} /group/fee/setting 收费设置
     * @apiVersion 1.0.0
     * @apiName setting
     * @apiGroup 收费
     * @apiDescription 医生集团收费设置
     *
     * @apiParam  {String}      access_token                token
     * @apiParam  {String}      groupId                     集团id
     * @apiParam  {Integer}     textMin                     图文咨询最低价
     * @apiParam  {Integer}     textMax                     图文咨询最高价
     * @apiParam  {Integer}     phoneMin                    电话咨询最低价
     * @apiParam  {Integer}     phoneMax                    电话咨询最高价
     * @apiParam  {Integer}     clinicMin                   门诊最低价
     * @apiParam  {Integer}     clinicMax                   门诊最高价
     * @apiParam  {Integer}     carePlanMin                 计划关怀最低价
     * @apiParam  {Integer}     carePlanMax                 计划关怀最高价
     * 
     * @apiSuccess {Integer}    resultCode                  状态
     *
     * @apiAuthor  范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("/setting")
    public JSONMessage setting(FeeParam param) {
        feeService.save(param);
        //处理收费设置修改后集团里医生的图文套餐、电话套餐、关怀计划
        //add by wangqiao 暂时关闭对集团下所有医生的收费调整
//        packService.executeFeeUpdate(param.getGroupId(),null);
        
        return JSONMessage.success();
    }
    
    /**
     * @api {post} /group/fee/get 获取登录用户所在多集团的收费范围
     * @apiVersion 1.0.1
     * @apiName get
     * @apiGroup 收费
     * @apiDescription 获取登录用户所在多集团的 收费设置总和（取多集团的交集）
     *
     * @apiParam    {String}      access_token                token
     * @apiParam    {String}      groupId                     集团id
     *
     * @apiSuccess  {Integer}     textMin                     图文咨询最低价
     * @apiSuccess  {Integer}     textMax                     图文咨询最高价
     * @apiSuccess  {Integer}     phoneMin                    电话咨询最低价
     * @apiSuccess  {Integer}     phoneMax                    电话咨询最高价
     * @apiSuccess  {Integer}     clinicMin                   门诊最低价
     * @apiSuccess  {Integer}     clinicMax                   门诊最高价
     * @apiSuccess  {Integer}     carePlanMin                 计划关怀最低价
     * @apiSuccess  {Integer}     carePlanMax                 计划关怀最高价
     * @apiSuccess  {Integer}     appointmentMin                 预约最低价
     * @apiSuccess  {Integer}     appointmentMax                 预约最高价
     * 
     * @apiAuthor  王峭
     * @date 2015年12月28日
     */
    @RequestMapping("/get")
    public JSONMessage get(String groupId) {
    	FeeVO feeVo = feeService.get(ReqUtil.instance.getUserId(), groupId);
    	if(StringUtils.isBlank(groupId)){
    		//查询会诊的最低最高价格
        	Map<String,Integer> map = consultationPackServiceImpl.getConsultationPrice(ReqUtil.instance.getUserId());
        	feeVo = feeVo == null ? new FeeVO() : feeVo;
        	feeVo.setConsultationMax(map.get("consultationMax"));
        	feeVo.setConsultationMin(map.get("consultationMin"));
    	}
        return JSONMessage.success(null,feeVo);
    }
    
    /**
     * @api {post} /group/fee/getGroupFee  获取单个集团的收费设置
     * @apiVersion 1.0.0
     * @apiName getGroupFee
     * @apiGroup 收费
     * @apiDescription 获取单个集团的收费设置
     *
     * @apiParam    {String}      access_token                token
     * @apiParam    {String}      groupId                     集团id
     *
     * @apiSuccess  {Integer}     textMin                     图文咨询最低价
     * @apiSuccess  {Integer}     textMax                     图文咨询最高价
     * @apiSuccess  {Integer}     phoneMin                    电话咨询最低价
     * @apiSuccess  {Integer}     phoneMax                    电话咨询最高价
     * @apiSuccess  {Integer}     clinicMin                   门诊最低价
     * @apiSuccess  {Integer}     clinicMax                   门诊最高价
     * @apiSuccess  {Integer}     carePlanMin                 计划关怀最低价
     * @apiSuccess  {Integer}     carePlanMax                 计划关怀最高价
     * @apiSuccess  {Integer}     appointmentMin              预约（名医面对面）最低价
     * @apiSuccess  {Integer}     appointmentMax              预约（名医面对面）最高价
     * @apiSuccess  {Integer}     appointmentDefault          预约（名医面对面）默认价
     * 
     * @apiAuthor  王峭
     * @date 2016年1月12日
     */
    @RequestMapping("/getGroupFee")
    public JSONMessage getGroupFee(String groupId) {
        return JSONMessage.success(null,feeService.get(groupId));
    }
    
    
}
