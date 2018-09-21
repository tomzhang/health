package com.dachen.health.controller.income;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.pack.income.constant.IncomeEnum;
import com.dachen.health.pack.income.entity.param.DoctorIncomeParam;
import com.dachen.health.pack.income.entity.param.DownParam;
import com.dachen.health.pack.income.entity.param.IncomeDetailsParam;
import com.dachen.health.pack.income.entity.vo.IncomeDetailsVO;
import com.dachen.health.pack.income.service.IIncomeService;
import com.dachen.health.pack.income.util.ExcelUtil;
import com.dachen.health.pack.incomeNew.constant.IncomeEnumNew;
import com.dachen.health.pack.incomeNew.constant.IncomeEnumNew.ObjectType;
import com.dachen.health.pack.incomeNew.entity.param.IncomelogParam;
import com.dachen.health.pack.incomeNew.entity.param.SettleNewParam;
import com.dachen.health.pack.incomeNew.entity.vo.BaseDetailVO;
import com.dachen.health.pack.incomeNew.entity.vo.SettleDetailVO;
import com.dachen.health.pack.incomeNew.service.IncomelogService;
import com.dachen.util.StringUtil; 

@RestController
@RequestMapping("income")
public class IncomesController {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日，HH时mm分");
	
	@Autowired
	private IncomelogService incomelogService;
	
	@Autowired
	private IIncomeService incomeServiceNew;
	
	@Autowired
	private IGroupService groupService;
	/**
     * @api {get} /income/info 获取收入基本信息
     * @apiVersion 1.0.0
     * @apiName income
     * @apiGroup 收入
     * @apiDescription 包含账户余额（已完成未结算）和未完成订单金额和总收入，
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    doctorId               医生用户ID
     * 
     * @apiSuccess {double}      balance                  账户余额（订单已完成&&未结算的收入明细的实际收入的合计）
     * @apiSuccess {double}      unbalance                未完成订单的期望收入 （订单未完成的收入明细的实际收入的合计）
     * @apiSuccess {double}      totalIncome              总收入（该医生已完成订单的所有实际收入的累积总和）
     *    
     * 
     * @apiAuthor  张垠
     * @date 2016年1月8日
     */
    @RequestMapping("/info")
	public JSONMessage getIncomeInfo(IncomelogParam param){
		return JSONMessage.success(null, incomelogService.getDoctorIncomeIndex(param));
	}
    
    /**
     * @api {get} /income/balanceDetail 账户余额明细
     * @apiVersion 1.0.0
     * @apiName balanceDetail
     * @apiGroup 收入
     * @apiDescription 查询医生账户余额明细
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    doctorId               医生用户ID
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * @apiSuccess {List}      resultlist                               结果列表
     * @apiSuccess {String}    resultlist.YM             		        yyyy年MM月
     * @apiSuccess {List}      resultlist.list             		    yyyy年MM月
     * @apiSuccess {Integer}   resultlist.list.orderId             		订单ID
     * @apiSuccess {Integer}   resultlist.list.logType             		收入类型（1=订单收入，2=医生提成收入，3=集团在医生中的提成收入，4=集团在会诊订单中的分成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费）
     * @apiSuccess {Integer}   resultlist.list.cashId             		打款ID（可选字段）
     * @apiSuccess {Integer}   resultlist.list.refundId             		退款ID（可选字段）
     * @apiSuccess {String}    resultlist.list.typeName             		订单类型名称（可选字段）
     * @apiSuccess {String}    resultlist.list.childName             	下级医生名称
     * @apiSuccess {String}    resultlist.list.day             		            日
     * @apiSuccess {double}    resultlist.list.money             		金额（分）
     * 
     * @apiAuthor  张垠
     * @date 2016年3月1日
     */
    @RequestMapping("/balanceDetail")
    public JSONMessage getBalanceDetail(IncomelogParam param){
    	return JSONMessage.success(null, incomelogService.getBalanceDetail(param));
    }
    
    /**
     * @api {get} /income/unbalanceDetail 医生账户未完成订单列表
     * @apiVersion 1.0.0
     * @apiName unbalanceDetail 
     * @apiGroup 收入
     * @apiDescription 医生账户未完成订单列表
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    doctorId               医生用户ID
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * @apiSuccess {List}      resultlist                           结果列表
     * @apiSuccess {String}    resultlist.keyYM                		yyyy年MM月
     * @apiSuccess {List}      resultlist.List                		
     * @apiSuccess {String}    resultlist.List.typeName            	订单类型名称    		
     * @apiSuccess {double}    resultlist.List.money            	订单收益金额   		
     * @apiSuccess {Integer}    resultlist.List.orderId            	订单ID 		
     * @apiSuccess {Integer}    resultlist.List.day            		对应日期Day 
     * 
     * @apiAuthor  张垠
     * @date 2016年3月1日
     */
    @RequestMapping("/unbalanceDetail")
    public JSONMessage getunBalanceDetail(IncomelogParam param){
    	return JSONMessage.success(null, incomelogService.getUnfinishedYMList(param));
    }
    
    
    /**
     * @api {get} /income/incomeList 医生收入列表
     * @apiVersion 1.0.0
     * @apiName incomeList
     * @apiGroup 收入
     * @apiDescription 按年月查询医生列表
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    doctorId               医生用户ID
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * @apiSuccess {List}      resultlist                           结果列表
     * @apiSuccess {Integer}   resultlist.year                      年
     * @apiSuccess {List}      resultlist.list                  	列表
     * @apiSuccess {String}    resultlist.list.month             	yyyy年MM月
     * @apiSuccess {double}    resultlist.list.totalMoney           总金额
     * @apiSuccess {Integer}   resultlist.list.totalNum             总笔数
     * 
     * @apiAuthor  张垠
     * @date 2016年3月1日
     */
    @RequestMapping("/incomeList")
    public JSONMessage getTotalIncomeList(IncomelogParam param){
    	param.setType(IncomeEnumNew.ObjectType.doctor.getIndex());
    	return JSONMessage.success(null, incomelogService.getTotalIncomeYMList(param));
    }
    
    
    /**
     * @api {get} /income/incomeDetail 医生收入明细
     * @apiVersion 1.0.0
     * @apiName incomeDetail
     * @apiGroup 收入
     * @apiDescription 查询医生指定年月收入明细
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    doctorId               医生用户ID
     * @apiParam  {String}     month               	  yyyy年MM月
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * @apiSuccess {List}      resultlist                            结果列表
     * @apiSuccess {Integer}   resultlist.bussnessType               业务类型（1，收入类型，2:提成3：扣减
     * @apiSuccess {String}    resultlist.bussnessName               业务类型名称
     * @apiSuccess {List}      resultlist.list                       收入列表
     * @apiSuccess {Integer}   resultlist.list.logType             		收入类型（1=订单收入，2=医生提成收入，3=集团在医生中的提成收入，4=集团在会诊订单中的分成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费）
     * @apiSuccess {Integer}   resultlist.list.orderId             		订单ID（可选字段）
     * @apiSuccess {Integer}   resultlist.list.cashId             		打款ID（可选字段）
     * @apiSuccess {Integer}   resultlist.list.refundId             	退款ID（可选字段）
     * @apiSuccess {String}    resultlist.list.typeName             	订单类型名称
     * @apiSuccess {String}    resultlist.list.childName             	下级医生名称（可选字段）
     * @apiSuccess {String}    resultlist.list.day             		            日
     * @apiSuccess {double}    resultlist.list.money             		金额（分）
     * 
     * 
     * @apiAuthor  张垠
     * @date 2016年3月1日
     */
    @RequestMapping("/incomeDetail")
    public JSONMessage getTotalIncomeDetail(IncomelogParam param){
    	param.setType(IncomeEnumNew.ObjectType.doctor.getIndex());
    	return JSONMessage.success(null, incomelogService.getTotalIncomeYMDetail(param));
    } 
    
    /**
     * @api {get} /income/cashDetail 打款详情
     * @apiVersion 1.0.0
     * @apiName cashDetail
     * @apiGroup 收入
     * @apiDescription 打款详情
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    id              		     打款ID
     * 
     * 
     * private String bankName;
		private String userRealName;
		private String bankID;
     * 
     * @apiSuccess {String}      bankName                           银行名称
     * @apiSuccess {String}    userRealName             		           户名
     * @apiSuccess {String}   bankID             		                                  银行卡号
     * @apiSuccess {Long}   	createDate             			打款日期
     * @apiSuccess {double}    money             				金额（分）
     * 
     * @apiAuthor  张垠
     * @date 2016年3月1日
     */
    @RequestMapping("/cashDetail")
    public JSONMessage getCashDetail(Integer id){
    	return JSONMessage.success(null, incomelogService.getCashRecordById(id));
    } 
    
    
    
    /**
     * @api {get} /income/gIncomeListNew 集团收入列表
     * @apiVersion 1.0.0
     * @apiName gIncomeListNew
     * @apiGroup 收入
     * @apiDescription 按年月查询集团收入列表
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     groupId                集团ID
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * @apiSuccess {List}      resultlist                           结果列表
     * @apiSuccess {String}    resultlist.month             		yyyy年MM月
     * @apiSuccess {double}    resultlist.totalMoney             	总金额
     * @apiSuccess {Integer}   resultlist.totalNum             		总笔数
     * 
     * @apiAuthor  张垠
     * @date 2016年3月2日
     */
    @RequestMapping("/gIncomeListNew")
    public JSONMessage getGoupIncomeListNew(IncomelogParam param){
    	param.setType(IncomeEnumNew.ObjectType.group.getIndex());
    	return JSONMessage.success(null, incomelogService.getTotalIncomeYMList(param));
    }
    
    
    /**
     * @api {get} /income/gIncomeDetail  集团收入明细
     * @apiVersion 1.0.0
     * @apiName gIncomeDetail
     * @apiGroup 收入
     * @apiDescription 查询指定 集团年月收入明细
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     groupId               集团ID
     * @apiParam  {String}     month               	  yyyy年MM月
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * @apiSuccess {List}      resultlist                               结果列表
     * @apiSuccess {String}    resultlist.childName                医生名称
     * @apiSuccess {String}    resultlist.telephone             	手机号
     * @apiSuccess {Integer}   resultlist.logType             		收入类型（1=订单收入，2=医生提成收入，3=集团在医生中的提成收入，4=集团在会诊订单中的分成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费）
     * @apiSuccess {Integer}   resultlist.cashId             		打款ID（可选字段）
     * @apiSuccess {Integer}   resultlist.refundId             	退款ID（可选字段）
     * @apiSuccess {Integer}   resultlist.orderNO             		订单号（（可选字段））
     * @apiSuccess {String}    resultlist.typeName             	订单类型名称
     * @apiSuccess {long}      resultlist.createDate             	收入时间
     * @apiSuccess {double}    resultlist.orderMoney             	订单金额（分）
     * @apiSuccess {double}    resultlist.money             	           提成金额（分）
     * 
     * @apiAuthor  张垠
     * @date 2016年3月2日
     */
    @RequestMapping("/gIncomeDetail")
    public JSONMessage getGroupIncomeDetail(IncomelogParam param){
    	param.setType(IncomeEnumNew.ObjectType.group.getIndex());
    	return JSONMessage.success(null, incomelogService.getTotalIncomeYMDetail(param));
    }
    
    
    /**
     * @api {get} /income/gMIncomeDetail 按条件查询集团收入列表
     * @apiVersion 1.0.0
     * @apiName gMIncomeDetail
     * @apiGroup 收入
     * @apiDescription 按条件查询集团收入列表
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     groupId                集团ID
     * @apiParam  {String}     childName              医生名称
     * @apiParam  {String}     telephone              医生手机号
     * @apiParam  {String}     oType                  类型（HZ：会诊 ，MZ：门诊 ， TW：图文咨询 ，  DH：电话咨询  ， JK:健康关怀，KJ：扣减，TK:提成退款,MY:名医面对面）
     * @apiParam  {String}     month                  yyyy年MM月
     * @apiParam  {Long}     	startTime             开始如间
     * @apiParam  {Long}     	endTime               结束如间
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * @apiSuccess {List}      resultlist                           结果列表
     * @apiSuccess {String}    resultlist.childName                 医生名称
     * @apiSuccess {String}    resultlist.telephone             	手机号
     * @apiSuccess {Integer}   resultlist.logType             		收入类型（1=订单收入，2=医生提成收入，3=集团在医生中的提成收入，4=集团在会诊订单中的分成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费）
     * @apiSuccess {Integer}   resultlist.cashId             		打款ID（可选字段）
     * @apiSuccess {Integer}   resultlist.refundId             		退款ID（可选字段）
     * @apiSuccess {Integer}   resultlist.orderNO             		订单号（（可选字段））
     * @apiSuccess {String}    resultlist.typeName             		订单类型名称
     * @apiSuccess {long}      resultlist.createDate             	收入时间
     * @apiSuccess {double}    resultlist.orderMoney             	订单金额（分）
     * @apiSuccess {double}    resultlist.money             	           提成金额（分）
     * 
     * @apiAuthor  张垠
     * @date 2016年3月2日
     */
    @RequestMapping("/gMIncomeDetail")
    public JSONMessage getGoupMoreIncomeList(IncomelogParam param){
    	param.setType(IncomeEnumNew.ObjectType.group.getIndex());
    	return JSONMessage.success(null, incomelogService.getGroupIncomeDetailByMore(param));
    }
    
    /**
     * @api {get} /income/gSettleYMList 按年月查询集团结算列表
     * @apiVersion 1.0.0
     * @apiName gSettleYMList
     * @apiGroup 收入
     * @apiDescription 按年月查询集团结算列表
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * @apiSuccess {List}      resultlist                           结果列表
     * @apiSuccess {String}    resultlist.month                		报表名称
     * @apiSuccess {double}    resultlist.noSettleMoney             待结算金额
     * @apiSuccess {double}   resultlist.settledMoney             	已结算金额
     * @apiSuccess {Integer}   resultlist.status             		结算状态（1=不允许结算，2=未结算，3=已结算，4=已过期）
     * 
     * @apiAuthor  张垠
     * @date 2016年3月2日
     */
    @RequestMapping("/gSettleYMList")
    public JSONMessage getGoupYMSettleList(SettleNewParam param){
    	param.setObjectType(IncomeEnumNew.ObjectType.group.getIndex());
    	return JSONMessage.success(null, incomelogService.getSettleYMList(param));
    }
    
    /**
     * @api {get} /income/dSettleYMList 按年月查询医生结算列表
     * @apiVersion 1.0.0
     * @apiName dSettleYMList
     * @apiGroup 收入
     * @apiDescription 按年月查询医生结算列表
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * @apiSuccess {List}      resultlist                           结果列表
     * @apiSuccess {String}    resultlist.month                		报表名称
     * @apiSuccess {double}    resultlist.noSettleMoney             待结算金额
     * @apiSuccess {double}   resultlist.settledMoney             	已结算金额
     * @apiSuccess {Integer}   resultlist.status             		结算状态（1=不允许结算，2=未结算，3=已结算，4=已过期）
     * 
     * @apiAuthor  张垠
     * @date 2016年3月2日
     */
    @RequestMapping("/dSettleYMList")
    public JSONMessage getDoctorYMSettleList(SettleNewParam param){
    	param.setObjectType(IncomeEnumNew.ObjectType.doctor.getIndex());
    	return JSONMessage.success(null, incomelogService.getSettleYMList(param));
    }
    
    /**
     * @api {get} /income/dSettleMList 按年月查询医生算列表
     * @apiVersion 1.0.0
     * @apiName dSettleMList
     * @apiGroup 收入
     * @apiDescription 按年月查询医生结算列表
     *
     *
     *
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     month                  yyyy年MM月
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * 
     * 
     * @apiSuccess {List}      resultlist                           结果列表
     * @apiSuccess {Integer}   resultlist.id                        纪录ID
     * @apiSuccess {String}    resultlist.userName                	医生/集团名称
     * @apiSuccess {String}    resultlist.telephone             	医生电话（可选字段）
     * @apiSuccess {double}    resultlist.settledMoney             	待结算金额
     * @apiSuccess {double}    resultlist.actualMoney             	实际结算金额
     * @apiSuccess {Integer}   resultlist.status             		结算状态（1=不允许结算，2=未结算，3=已结算，4=已过期）
     * @apiSuccess {String}    resultlist.userRealName             	开户名称
     * @apiSuccess {String}    resultlist.personNo             		身份证号
     * @apiSuccess {String}    resultlist.bankName             		开户银行
     * @apiSuccess {String}    resultlist.subBankName             	支行
     * @apiSuccess {String}    resultlist.bankNo             		账号
     * @apiSuccess {Boolean}   resultlist.inofOK             		信息是否完善（true：信息完善，false:信息不完善）
     * 
     * @apiAuthor  张垠
     * @date 2016年3月3日
     */
    @RequestMapping("/dSettleMList")
    public JSONMessage getDoctorMSettleList(SettleNewParam param){
    	param.setObjectType(IncomeEnumNew.ObjectType.doctor.getIndex());
    	return JSONMessage.success(null, incomelogService.getSettleList(param));
    }
    
    /**
     * @api {get} /income/gSettleMList 按年月查询集团结算列表
     * @apiVersion 1.0.0
     * @apiName gSettleMList
     * @apiGroup 收入
     * @apiDescription 按年月查询集团结算列表
     *
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     month                  yyyy年MM月
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * 
     * @apiSuccess {List}      resultlist                           结果列表
     * @apiSuccess {String}    resultlist.userName                	医生/集团名称
     * @apiSuccess {String}    resultlist.telephone             	医生电话（可选字段）
     * @apiSuccess {double}    resultlist.settledMoney             	待结算金额
     * @apiSuccess {double}    resultlist.actualMoney             	实际结算金额
     * @apiSuccess {Integer}   resultlist.status             		结算状态（1=不允许结算，2=未结算，3=已结算，4=已过期）
     * @apiSuccess {String}    resultlist.userRealName             	开户名称
     * @apiSuccess {String}    resultlist.personNo             		身份证号
     * @apiSuccess {String}    resultlist.bankName             		开户银行
     * @apiSuccess {String}    resultlist.subBankName             	支行
     * @apiSuccess {String}    resultlist.bankNo             		账号
     * @apiSuccess {Boolean}   resultlist.inofOK             		信息是否完善（true：信息完善，false:信息不完善）
     * 
     * @apiAuthor  张垠
     * @date 2016年3月3日
     */
    @RequestMapping("/gSettleMList")
    public JSONMessage getGroupMSettleList(SettleNewParam param){
    	param.setObjectType(IncomeEnumNew.ObjectType.group.getIndex());
    	return JSONMessage.success(null, incomelogService.getSettleList(param));
    }
    
    
    /**
     * @api {get} /income/groupSettle 集团结算
     * @apiVersion 1.0.0
     * @apiName groupSettle
     * @apiGroup 收入
     * @apiDescription 集团结算
     *
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}     id                    结算ID
     * @apiParam  {double}     settleMoney             实际结算金额
     * @apiParam  {double}     expandMoney             扣减金额
     * 
     * @apiSuccess {Map}       map                           		结果集合
     * @apiSuccess {String}    map.msg               				提示信息
     * @apiSuccess {boolean}   map.status             				执行结果true：成功；false:失败
     * 
     * 
     * @apiAuthor  张垠
     * @date 2016年3月3日
     */
    @RequestMapping("/groupSettle")
    public JSONMessage settleGroup(SettleNewParam param) throws HttpApiException {
    	param.setObjectType(IncomeEnumNew.ObjectType.group.getIndex());
    	return JSONMessage.success(null, incomelogService.settle(param));
    }
    
    
    
    /**
     * @api {get} /income/doctorSettle 医生结算
     * @apiVersion 1.0.0
     * @apiName doctorSettle
     * @apiGroup 收入
     * @apiDescription 医生结算
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}     id                    结算ID
     * @apiParam  {double}    settleMoney             实际结算金额
     * @apiParam  {double}    expandMoney             扣减金额
     * 
     * @apiSuccess {Map}       map                           		结果集合
     * @apiSuccess {String}    map.msg               				提示信息
     * @apiSuccess {boolean}   map.status             				执行结果true：成功；false:失败
     * 
     * @apiAuthor  张垠
     * @date 2016年3月3日
     */
    @RequestMapping("/doctorSettle")
    public JSONMessage settleDoctor(SettleNewParam param) throws HttpApiException {
    	param.setObjectType(IncomeEnumNew.ObjectType.doctor.getIndex());
    	return JSONMessage.success(null, incomelogService.settle(param));
    }
    
    /**
     * @api {get} /income/autoSettle 医生结算
     * @apiVersion 1.0.0
     * @apiName autoSettle
     * @apiGroup 收入
     * @apiDescription 自动结算
     * 
     * @apiAuthor  张垠
     * @date 2016年3月18日
     */
    @RequestMapping("/autoSettle")
    public JSONMessage autoSettle(){
    	incomelogService.autoSettleNew();
    	return JSONMessage.success(null, null);
    }
    
    
    
    
    
    /**
     * @api {get} /income/details 获取收入明细
     * @apiVersion 1.0.0
     * @apiName details
     * @apiGroup 收入
     * @apiDescription 查询账户余额明细或者未完成订单余额明细
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}    doctorId               医生用户ID
     * @apiParam  {Integer}    type                   type(1：未完成；2：账户余额;3:总收入)
     * @apiParam  {Integer}    pageSize               页面大小
     * @apiParam  {Integer}    pageIndex              页码
     * 
     * 
     * @apiAuthor  张垠
     * @date 2016年1月8日
     */
    @RequestMapping("/details")
    public JSONMessage getIncomeDetails(IncomeDetailsParam param){
    	return JSONMessage.success(null, incomeServiceNew.getDoctorDetails(param));
    }
    
    
    /**
     * @api {get} /income/gIncomeDetails 获取集团某年某月收入明细
     * @apiVersion 1.0.0
     * @apiName gIncomeDetails
     * @apiGroup 收入
     * @apiDescription 获取集团某年某月收入明细
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     upGroup                集团ID
     * @apiParam  {String}     time                   年月份(格式：yyyy-MM)
     * 
     * 
     * @apiAuthor  张垠
     * @date 2016年1月8日
     */
    @RequestMapping("gIncomeDetails")
    public JSONMessage getGoupIncomeDetails(DoctorIncomeParam param,String upGroup){
    	
    	param.setUserType(IncomeEnum.SettleUserType.group.getIndex());
    	param.setGroupId(upGroup);
    	
    	List<IncomeDetailsVO>  list = incomeServiceNew.getGroupIncomeByGroupId(param);
    	int count = incomeServiceNew.countGroupIncomeByGroupId(param);
    	
		PageVO pageVO = new PageVO();
		pageVO.setPageData(list);
		pageVO.setPageIndex(param.getPageIndex());
		pageVO.setTotal(Long.valueOf(count));
		pageVO.setPageSize(param.getPageSize());
    	
    	return JSONMessage.success(null,pageVO);
    	
    }
    
    
    /**
     * @api {get} /income/gdIncomeList 获取集团内医生收入列表
     * @apiVersion 1.0.0
     * @apiName gdIncomeList
     * @apiGroup 收入
     * @apiDescription 根据条件获取集团里医生收入列表
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     upGroup                集团ID
     * @apiParam  {String}     telephone              医生电话号码(可不传)
     * @apiParam  {String}     name                   医生名称（可不传）
     * 
     * 
     * @apiAuthor  张垠
     * @date 2016年1月8日
     */
    @RequestMapping("gdIncomeList")
    public JSONMessage getGroupDoctorIncomList(DoctorIncomeParam param,String upGroup){
    	
    	param.setUserType(IncomeEnum.SettleUserType.doctor.getIndex());
    	param.setGroupId(upGroup);
    	
    	List<IncomeDetailsVO>  list = incomeServiceNew.statDoctorIncomeByGroupId(param);
    	
    	
    	int count = incomeServiceNew.countStatDoctorIncomeByGroupId(param);
    	
		PageVO pageVO = new PageVO();
		pageVO.setPageData(list);
		pageVO.setPageIndex(param.getPageIndex());
		pageVO.setTotal(Long.valueOf(count));
		pageVO.setPageSize(param.getPageSize());
    	
    	return JSONMessage.success(null,pageVO);
    }
    
    
   
    
    /**
     * @api {get} /income/downExcel 下载报表
     * @apiVersion 1.0.0
     * @apiName downExcel
     * @apiGroup 收入
     * @apiDescription 下载已结算纪录报表
     *
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {String}     groupId               集团ID(可选)
     * @apiParam  {String}     childName             医生姓名
     * @apiParam  {String}     telephone             医生电话
     * @apiParam  {Long}       startTime             开始时间
     * @apiParam  {Long}        endTime              结束时间
     * @apiParam  {Integer}     type              	    报表类型（11：医生结算，12：集团结算;21：收入）
     * @apiParam  {Integer}     logType              收入类型（会诊,门诊,图文咨询,电话咨询,健康关怀,扣减项,订单取消退款）
     * @apiParam  {String}     oType                类型（HZ：会诊 ，MZ：门诊 ， TW：图文咨询 ，  DH：电话咨询  ， JK:健康关怀，KJ：扣减，TK:提成退款）
     * @apiParam  {String}     month           		 yyyy年MM月
     * @apiParam  {Integer}    pageSize              下载第几页的数据
     * @apiParam  {Integer}    pageIndex             页码
     * 
     * 
     * @apiAuthor  张垠
     * @date 2016年1月8日
     */
    @RequestMapping("downExcel")
    public String download(DownParam param,HttpServletRequest request,HttpServletResponse response) throws IOException{
    	List<?> dataList = new ArrayList<>();
    	String filenName = "";
    	String[] columnNames = null;
    	String[] keys = null;
//    	if(param.getType() == 11 || param.getType() == 12){//结算
//    		SettleNewParam settleParam = new SettleNewParam();
//    		if(param.getType() ==12){
//    			settleParam.setObjectType(ObjectType.group.getIndex());
//    		}else if(param.getType() ==11){
//    			settleParam.setObjectType(ObjectType.doctor.getIndex());
//    		}else{
//    			return "暂不支持该类型 ";
//    		}
//    		
//    		settleParam.setPageIndex(param.getPageIndex());
//    		settleParam.setPageSize(param.getPageSize());
//    		settleParam.setPageIndex(param.getPageIndex());
//    		settleParam.setMonth(param.getMonth());
//    		PageVO vo = incomelogService.getSettleList(settleParam);
//    		dataList = vo.getPageData();
////    		集团名称	开户名称	身份证号	开户银行	支行	银行账号	待结算金额（元）	实际结算金额（元）
//    		columnNames = new String[]{"集团名称","开户名称","身份证号","开户银行","支行","银行账号","待结算金额（元）","实际结算金额（元）","打款状态"};//列名
//    		keys =  new String[]{"userName","userRealName","personNo","bankName","subBankName","bankNo","noSettleMoney","actualMoney","statusName"};
//    		filenName = "结算纪录报表";
//    	}else 
    		
		if(param.getType() == 21){//收入
			if(StringUtil.isEmpty(param.getGroupId())){
				throw new ServiceException("集团ID不能为空");
			}
    		IncomelogParam logParam = new IncomelogParam();
    		logParam.setChildName(param.getChildName());
    		logParam.setPageIndex(param.getPageIndex());
    		logParam.setPageSize(param.getPageSize());
    		logParam.setMonth(param.getMonth());
    		logParam.setTelephone(param.getTelephone());
    		logParam.setStartTime(param.getStartTime());
    		logParam.setEndTime(param.getEndTime());
    		logParam.setLogType(param.getLogType());
    		logParam.setType(ObjectType.group.getIndex());
    		logParam.setoType(param.getoType());
    		logParam.setGroupId(param.getGroupId());
    		PageVO vo = incomelogService.getGroupIncomeDetailByMore(logParam);
    		dataList = vo.getPageData();
//    		医生名称	手机号	订单类型	订单号	订单时间	订单金额（元）	提成金额（元）
    		columnNames= new String[]{"医生名称","手机号","订单类型","订单号","订单时间","订单金额（元）","提成金额（元）"};//列名
    		keys =  new String[]{"childName","telephone","typeName","orderNO","createDate","orderMoney","money"};
    		Group group= groupService.getGroupById(param.getGroupId());
    		String groupName=group==null?"":group.getName();
    		filenName = param.getMonth()+groupName+"提成记录报表";
    	}else{
    		
    		SettleNewParam settleParam = new SettleNewParam();
    		if(param.getType() ==12){
    			filenName=param.getMonth()+"集团结算详情报表";
    			settleParam.setObjectType(ObjectType.group.getIndex());
    			columnNames = new String[]{"集团名称","开户名称","身份证号","开户银行","支行","银行账号","待结算金额（元）","实际结算金额（元）","打款状态"};//列名
    			keys =  new String[]{"userName","userRealName","personNo","bankName","subBankName","bankNo","noSettleMoney","actualMoney","statusName"};
    		}else if(param.getType() ==11){
    			filenName=param.getMonth()+"医生结算详情报表";
    			settleParam.setObjectType(ObjectType.doctor.getIndex());
    			columnNames = new String[]{"医生名称","手机号","开户名称","身份证号","开户银行","支行","银行账号","待结算金额（元）","实际结算金额（元）","打款状态"};//列名
    			keys =  new String[]{"userName","telephone","userRealName","personNo","bankName","subBankName","bankNo","noSettleMoney","actualMoney","statusName"};
    		}else{
    			return "暂不支持该类型 ";
    		}
    		
    		settleParam.setPageIndex(param.getPageIndex());
    		settleParam.setPageSize(param.getPageSize());
    		settleParam.setPageIndex(param.getPageIndex());
    		settleParam.setMonth(param.getMonth());
    		PageVO vo = incomelogService.getSettleList(settleParam);
    		dataList = vo.getPageData();
//    		集团名称	开户名称	身份证号	开户银行	支行	银行账号	待结算金额（元）	实际结算金额（元）
    	}
    	if(dataList.size() == 0){
    		return "没有找到下载数据";
    	}
    	
    	List<Map<String,Object>> list=createExcelRecord(dataList,param.getType());
       
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ExcelUtil.createWorkBook(list,keys,columnNames).write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename="+ new String((filenName+".xls").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        return null;
    }
    private List<Map<String, Object>> createExcelRecord(List<?> projects,int userType) {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sheetName", "sheet1");
        listmap.add(map);
        for (int j = 0; j < projects.size(); j++) {
        	 Map<String, Object> mapValue = new HashMap<String, Object>();
    		if(userType == 21){//收入
        		BaseDetailVO vo = (BaseDetailVO)projects.get(j);
//        		"childName","telephone","typeName","orderNO","createDate","orderMoney","money"
        		mapValue.put("childName", vo.getChildName());
        		mapValue.put("telephone", vo.getTelephone());
        		mapValue.put("typeName", vo.getTypeName());
        		mapValue.put("orderNO", vo.getOrderNO());
        		mapValue.put("createDate", sdf.format(vo.getCreateDate()));
        		mapValue.put("orderMoney", calculateMoney(vo.getOrderMoney(), Double.valueOf(100)));
        		mapValue.put("money", calculateMoney(vo.getMoney(), Double.valueOf(100)));
        	}else{
        		 //结算
        		SettleDetailVO vo = (SettleDetailVO)projects.get(j);
        		if(userType ==12){
//        			keys =  new String[]{"userName","userRealName","personNo","bankName","subBankName","bankNo","noSettleMoney","actualMoney","statusName"};
        		}else if(userType ==11){
//        			keys =  new String[]{"userName","telephone","userRealName","personNo","bankName","subBankName","bankNo","noSettleMoney","actualMoney","statusName"};
        			mapValue.put("telephone", vo.getTelephone());
        		}else{
        			throw new ServiceException("useType不支持");
        		}
        		
        		mapValue.put("userName", vo.getUserName());
        		mapValue.put("userRealName", vo.getUserRealName());
        		mapValue.put("personNo", vo.getPersonNo());
        		mapValue.put("bankName", vo.getBankName());
        		mapValue.put("subBankName", vo.getSubBankName());
        		mapValue.put("bankNo", vo.getBankNo());
        		
        		mapValue.put("noSettleMoney", calculateMoney(vo.getNoSettleMoney(), Double.valueOf(100)));
        		mapValue.put("actualMoney", calculateMoney(vo.getActualMoney(), Double.valueOf(100)));
        		
        		if(vo.getStatus() == IncomeEnumNew.SettleStatus.settled.getIndex()){
        			mapValue.put("statusName", IncomeEnumNew.SettleStatus.settled.getTitle());
        		}else if(vo.getStatus() == IncomeEnumNew.SettleStatus.expired.getIndex()){
        			mapValue.put("statusName", IncomeEnumNew.SettleStatus.expired.getTitle());
        		}else if(vo.getStatus() == IncomeEnumNew.SettleStatus.forbidden.getIndex()){
        			mapValue.put("statusName", IncomeEnumNew.SettleStatus.forbidden.getTitle());
        		}else if(vo.getStatus() == IncomeEnumNew.SettleStatus.unsettle.getIndex()){
        			mapValue.put("statusName", IncomeEnumNew.SettleStatus.unsettle.getTitle());
        		}
        	}
        	
        	listmap.add(mapValue);
        }
        return listmap;
    }
    
    private double calculateMoney(Double a,Double b){
    	if(a == null){
    		a = Double.valueOf(0);
    	}
    	if(b == null){
    		b = Double.valueOf(0);
    	}
    	return new BigDecimal(a).divide(new BigDecimal(b)).doubleValue();
    }
}
