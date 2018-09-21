package com.dachen.health.pack.incomeNew.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.account.entity.vo.RechargeVO;
import com.dachen.health.pack.incomeNew.entity.param.IncomelogParam;
import com.dachen.health.pack.incomeNew.entity.param.SettleNewParam;
import com.dachen.health.pack.incomeNew.entity.po.Incomelog;
import com.dachen.health.pack.incomeNew.entity.po.RefundOrder;
import com.dachen.health.pack.incomeNew.entity.vo.CashVO;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.sdk.exception.HttpApiException;

public interface IncomelogService {
	
	/**
	 * 医生APP首页 （余额数，总收入数，未完成订单数）
	 * 
	 */
	public Map<String,Double> getDoctorIncomeIndex(IncomelogParam param);
	
	/**
	 * 医生App首页 账户余额详情
	 * @param param
	 * @return
	 */
	public PageVO getBalanceDetail(IncomelogParam param);
	/**
	 * 获取按年月分组的收入列表
	 * @param param
	 * @return
	 */
	public PageVO getTotalIncomeYMList(IncomelogParam param);
	/**
	 * 获取按年月分组对应明细
	 * @param param
	 * @return
	 */
	public PageVO getTotalIncomeYMDetail(IncomelogParam param);
	
	/**
	 * 多条件查询集团收入流水
	 * @param param
	 * @return
	 */
	public PageVO getGroupIncomeDetailByMore(IncomelogParam param);
	
	/**
	 * 订单完成时新增
	 * @param param
	 */
	public void addOrderIncomelog(Order param);
	
	/**
	 * 根据打款ID获取打款纪录
	 */
	public CashVO getCashRecordById(Integer cashId);
	
	/**
	 * 按年月获取指定ObjectType的待结算金额和已结算金额
	 * @param param
	 * @return
	 */
	PageVO getSettleYMList(SettleNewParam param);
	/**
	 * 获取指定月份的结算纪录
	 * @param param
	 * @return
	 */
	PageVO getSettleList(SettleNewParam param);
	
	/**
	 * 可结算则更新状态为结算并把银行卡更新到结算纪录里
	         新增一条打款纪录到打款表 新增打款纪录到流水表（负值）
	        新增一条扣减纪录到扣减表
	        新增一条扣减项到流水表（负值），并更新结算表里本月的结算实际金额（负值）
	 * @param param
	 * @return
	 */
	Map<String,Object> settle(SettleNewParam param) throws HttpApiException;
	
	/**
	 * 获取医生未完成订单统计
	 * @param param
	 * @return
	 */
	PageVO getUnfinishedYMList(IncomelogParam param);
	
	/**
	 * 每个月1号0点 自动转存（自动转存期间若发生结算相关业务就会导致数据不正确）
	 * 更新 "上上个月" 的所有 "未结算" 状态为 "过期",并把 "待结算的金额" 累加到 "上个月" 的待结算金额，
	 * 并更新 "上个月的" 的结算状态为 "未结算"
	 * 
	 */
	void autoSettleNew();
	
	void autorConvertToNext(Long time);
	
	/**
	 *  手动退款 添加退款单 患者退款纪 添加incomelog纪录 settle更新
	 * @param order
	 * @param time
	 * @param userId
	 * @return
	 */
	RefundOrder addRefundOrder(Order order,RechargeVO rechargevo,Integer userId);
	
	
	/**
	 * 根据订单获取对应退款收入
	 * @param orderId
	 * @return
	 */
	List<Incomelog> getIncomeLog(Integer orderId);
	
	/**
	 * 获取对应的收入主体
	 * @param orderId
	 * @return
	 */
	List<Incomelog> getIncomer(Integer orderId);
	
	List<Incomelog> getLogListByDoc(Integer doctorId);
	
}
