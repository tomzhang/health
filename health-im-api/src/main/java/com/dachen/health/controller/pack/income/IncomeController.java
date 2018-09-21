package com.dachen.health.controller.pack.income;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.income.entity.param.IncomeParam;
import com.dachen.health.pack.income.service.IIncomeDetailService;
import com.dachen.health.pack.income.service.IIncomeMonthService;
import com.dachen.health.pack.income.service.IIncomeService;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("/pack/income")
public class IncomeController {

    @Autowired
    private IIncomeService incomeService;

    @Autowired
    private IIncomeMonthService incomeMonthService;

    @Autowired
    private IIncomeDetailService incomeDetailService;

    /**
     * @api {get} /pack/income/getIncome 获取总收入
     * @apiVersion 1.0.0
     * @apiName getIncome
     * @apiGroup 收入
     * @apiDescription 获取总收入
     *
     * @apiParam  {String}    access_token          token
     * 
     * @apiSuccess {Long}     totalIncome           总收入，单位为分
     * @apiSuccess {Long}     outIncome             未结算收入，单位为分
     *
     * @apiAuthor  范鹏
     * @date 2015年8月17日
     */
    @Deprecated
    @RequestMapping("getIncome")
    public JSONMessage getIncome() {
//        return JSONMessage.success(null, incomeService.statisticsIncome(ReqUtil.instance.getUserId()));
        return JSONMessage.success(null);
    }

    /**
     * @api {get} /pack/income/getIncomeMonth 获取月收入
     * @apiVersion 1.0.0
     * @apiName getIncomeMonth
     * @apiGroup 收入
     * @apiDescription 获取月收入
     *
     * @apiParam  {String}    access_token          token
     * 
     * @apiSuccess {Integer}  month                 月份，格式201508
     * @apiSuccess {Long}     money                 金额，单位为分
     *
     * @apiAuthor  范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("getIncomeMonth")
    public JSONMessage getIncomeMonth() {
        return JSONMessage.success(null, incomeMonthService.getIncomeMonth(ReqUtil.instance.getUserId()));
    }

    /**
     * @api {get} /pack/income/getIncomeDetail 获取收入详细
     * @apiVersion 1.0.0
     * @apiName getIncomeDetail
     * @apiGroup 收入
     * @apiDescription 获取收入详细
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}   month                 月份,格式：201508，为空则查询所有
     * 
     * @apiSuccess {Long}     money                 金额，单位分
     * @apiSuccess {String}   remark                描述
     * @apiSuccess {Long}     incomeTime            时间，毫秒
     * @apiSuccess {String}   userName              用户名
     * @apiSuccess {String}   headPicFileName       头像
     *
     * @apiAuthor  范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("getIncomeDetail")
    public JSONMessage getIncomeDetail(IncomeParam param) {
        param.setDoctorId(ReqUtil.instance.getUserId());
        return JSONMessage.success(null, incomeDetailService.getIncomeDetail(param));
    }

}
