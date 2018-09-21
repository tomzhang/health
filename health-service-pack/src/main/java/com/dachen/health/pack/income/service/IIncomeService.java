package com.dachen.health.pack.income.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.income.entity.param.DoctorIncomeParam;
import com.dachen.health.pack.income.entity.param.IncomeDetailsParam;
import com.dachen.health.pack.income.entity.po.DoctorDivision;
import com.dachen.health.pack.income.entity.po.DoctorIncome;
import com.dachen.health.pack.income.entity.po.GroupDivision;
import com.dachen.health.pack.income.entity.vo.IncomeDetailsVO;
import com.dachen.health.pack.order.entity.po.Order;

/**
 * 
 * 收入相关服务类
 *@author wangqiao
 *@date 2016年1月23日
 *
 */
public interface IIncomeService {

	/**
	 * 订单付款时，生成 医生收入 记录
	 * @param order
	 * @return
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public void addDoctorIncomeForOrderPay(Order order);
    

	/**
	 * 订单完成时，更新 医生收入 记录
	 * @param doctorId
	 * @return
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public void updateDoctorIncomeForOrderComplete(Order order);
	
	

	/**
	 * 订单完成时，生成 上级医生分成 记录
	 * @param income  医生收入信息
	 * @param divisionDoctorId 上级医生id
	 * @param doctorIncome  上级分成收入
	 * @param divisionGroupId 分成集团id
	 * @return
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public void addDoctorDivisionForOrderComplete(DoctorIncome income,Integer divisionDoctorId,String divisionGroupId,double doctorIncome);
	

	/**
	 * 订单完成时，生成 集团分成 记录
	 * @param income
	 * @param groupIncome
	 * @param divisionGroupId 分成集团id
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public void addGroupDivisionForOrderComplete(DoctorIncome income ,String divisionGroupId,double groupIncome);
	
	/**
	 * 结算时， 批量更新 医生收入 记录
	 * @param docIncomeList
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public void updateDoctorIncomeForSettle(List<DoctorIncome> docIncomeList);
	
	
	/**
	 * 结算时， 批量更新 医生分成 记录
	 * @param docDivisionList
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public void updateDoctorDivisionForSettle(List<DoctorDivision>  docDivisionList);
	
	/**
	 * 结算时，批量更新 集团分成 记录
	 * @param groupDivisionList
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public void updateGroupDivisionForSettle(List<GroupDivision> groupDivisionList);
	
	/**
	 * 付款后，将settle对应的收入信息设置为 已付款
	 * @param settleId
	 *@author wangqiao
	 *@date 2016年1月25日
	 */
	public void updateIncomeForPay(Integer settleId);
	
	/**
	 * 统计集团中的 医生收入(分页list 数据)
	 * @param param groupId，pageIndex,pageSize
	 * @return
	 *@author wangqiao
	 *@date 2016年1月25日
	 */
	public List<IncomeDetailsVO> statDoctorIncomeByGroupId(DoctorIncomeParam param);
	
	/**
	 * 统计集团中的 医生收入(总记录数)
	 * @param param
	 * @return
	 *@author wangqiao
	 *@date 2016年1月25日
	 */
	public int countStatDoctorIncomeByGroupId(DoctorIncomeParam param);
	
	/**
	 * 查询 集团收入明细
	 * @param param
	 * @return
	 *@author wangqiao
	 *@date 2016年1月25日
	 */
	public List<IncomeDetailsVO> getGroupIncomeByGroupId(DoctorIncomeParam param);
	
	/**
	 * 查询集团收入 总数
	 * @param param
	 * @return
	 *@author wangqiao
	 *@date 2016年1月25日
	 */
	public int countGroupIncomeByGroupId(DoctorIncomeParam param);
	
	/**
	 * 查询 医生收入 列表
	 * @param doctorId 医生id
	 * @param orderStatus 订单状态 （可以为空）
	 * @param settleStatus 结算状态 （可以为空）
	 * @return
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public List<DoctorIncome> getDoctorIncomeByDoctorId(Integer doctorId,String orderStatus,String settleStatus);
	
	/**
	 * 查询 医生分成 列表
	 * @param doctorId 医生id
	 * @param settleStatus 结算状态 （可以为空）
	 * @return
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public List<DoctorDivision> getDoctorDivisionByDoctorId(Integer doctorId,String settleStatus);
	
	/**
	 * 查询 集团分成 列表
	 * @param groupId 集团id
	 * @param settleStatus 结算状态 （可以为空）
	 * @return
	 *@author wangqiao
	 *@date 2016年1月23日
	 */
	public List<GroupDivision> getGroupDivisionByDoctorId(String groupId,String settleStatus);
	
	
	/**
	 * 获取医生的账户余额、总收入、未完成订单金额
	 * @param param
	 * @return
	 */
	Map<String,Object> getDoctorBalances(IncomeDetailsParam param);
	
	
	/**
	 * 获取某种类型余额的订单的明细（账户余额、总收入、未完成订单）
	 * @param param
	 * @return
	 */
	PageVO getDoctorDetails(IncomeDetailsParam param);
	
}
