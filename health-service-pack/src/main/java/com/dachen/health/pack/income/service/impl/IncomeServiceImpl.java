package com.dachen.health.pack.income.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.GroupProfit;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupProfitService;
import com.dachen.health.pack.income.constant.IncomeEnum;
import com.dachen.health.pack.income.constant.IncomeEnum.IncomePayStatus;
import com.dachen.health.pack.income.constant.IncomeEnum.SettleStatus;
import com.dachen.health.pack.income.entity.param.DoctorIncomeParam;
import com.dachen.health.pack.income.entity.param.IncomeDetailsParam;
import com.dachen.health.pack.income.entity.po.DoctorDivision;
import com.dachen.health.pack.income.entity.po.DoctorDivisionExample;
import com.dachen.health.pack.income.entity.po.DoctorIncome;
import com.dachen.health.pack.income.entity.po.DoctorIncomeExample;
import com.dachen.health.pack.income.entity.po.GroupDivision;
import com.dachen.health.pack.income.entity.po.GroupDivisionExample;
import com.dachen.health.pack.income.entity.vo.DoctorIncomeVO;
import com.dachen.health.pack.income.entity.vo.IncomeDetailsVO;
import com.dachen.health.pack.income.entity.vo.IncomeVONew;
import com.dachen.health.pack.income.entity.vo.PageIncome;
import com.dachen.health.pack.income.entity.vo.PageIncomeDetails;
import com.dachen.health.pack.income.mapper.DoctorDivisionMapper;
import com.dachen.health.pack.income.mapper.DoctorIncomeMapper;
import com.dachen.health.pack.income.mapper.GroupDivisionMapper;
import com.dachen.health.pack.income.mapper.IncomeMapperNew;
import com.dachen.health.pack.income.service.IIncomeService;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.service.IOrderDoctorService;
import com.dachen.util.DateUtil;


/**
 * 
 * 收入相关服务类
 *@author wangqiao
 *@date 2016年1月23日
 *
 */
@Service
public class IncomeServiceImpl implements IIncomeService {


    
    @Autowired
    private DoctorDivisionMapper doctorDivisionMapper;
    
    @Autowired
    private DoctorIncomeMapper doctorIncomeMapper;
    
    @Autowired
    private GroupDivisionMapper groupDivisionMapper;
    
    @Autowired
    private IOrderDoctorService orderDoctorService;
    
    @Autowired
    private IncomeMapperNew incomeMapperNew;
    
    @Autowired
	private UserManager userService;
 
    @Autowired
    private   IGroupProfitService groupProfitService;
    
    @Autowired
    private IGroupDoctorService gdocService;
 

	@Override
	public void addDoctorIncomeForOrderPay(Order order) {
		//参数校验
		if(order == null ){
			throw new ServiceException("订单信息为空");
		}
		//参数校验  order.orderType，order.id, order.price, 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		long now = System.currentTimeMillis();
		
		//待新增的数据
		List<DoctorIncome> addIncomeList = new ArrayList<DoctorIncome>();
		
		//只有关怀计划 才有多个医生参与分成，需要生成多条记录
		if(order.getOrderType() == OrderEnum.OrderType.care.getIndex()){//关怀计划
			List<OrderDoctor>  odList  = orderDoctorService.findOrderDoctors(order.getId());//根据订单查找所有参与医生
			if(odList != null && odList.size()>0){
				for(OrderDoctor od :odList){
					
					//计算医生分成收入=订单金额*分成比例
					double orderPrice = order.getPrice();//订单总金额
					Integer ratio = od.getSplitRatio();//医生分成比例
					BigDecimal shareIncome = new BigDecimal(orderPrice*ratio).divide(new BigDecimal(100), 5, BigDecimal.ROUND_HALF_UP);
					
					DoctorIncome income = new DoctorIncome();
					income.setDoctorId(od.getDoctorId());//设置收入医生id
					income.setShareIncome(shareIncome.doubleValue());//设置 订单分成收入
					addIncomeList.add(income);
				}
			}
			
		}else{
			//其它类型的订单，分成收入=订单金额
			DoctorIncome income = new DoctorIncome();
			income.setDoctorId(order.getDoctorId());//设置收入医生id
			income.setShareIncome(order.getPrice().doubleValue());//设置 订单分成收入
			addIncomeList.add(income);
		}
		
		//初始化其它信息 并 持久化
		for(DoctorIncome income : addIncomeList){
			income.setOrderId(order.getId());
			income.setOrderIncome(order.getPrice().doubleValue());
			income.setSettleStatus(SettleStatus.未结算.getIndex()+"");

			income.setOrderStatus("3");//OrderStatusEnum 枚举读不到 FIXME
			income.setCreateTime(now);
			income.setExtend1(sdf.format(now));
			income.setExtend2(IncomePayStatus.未付款.getIndex());//设置为未付款
			
//			if(order.getGroupId() != null && !StringUtils.isEmpty(order.getGroupId())){
//				income.setDivisionGroupId(order.getGroupId());
//			}
			
			
			doctorIncomeMapper.insert(income);
		}
		
	}

	@Override
	public void updateDoctorIncomeForOrderComplete(Order order) {
		//参数校验
		if(order == null ){
			throw new ServiceException("订单信息为空");
		}
		//order.groupId , order.getPackType()
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		long now = System.currentTimeMillis();
		
		//查询 需要更新的 收入信息 (一个订单可能关联多个收入信息)
		DoctorIncomeExample example = new DoctorIncomeExample();
		example.createCriteria().andOrderIdEqualTo(order.getId());
		List<DoctorIncome> incomeList = doctorIncomeMapper.selectByExample(example);//FIXME 可以提取出来
		if(incomeList != null && incomeList.size() >0){
			for(DoctorIncome income: incomeList){
				//每条收入信息都要更新
				if(income == null){
					continue;
				}
				//抽成集团id
				String divisionGroupId = "";
				//抽成医生id
				Integer divisionDoctorId = null;
				
				//收入医生 是否在 订单集团中？
				List<GroupDoctor>  list =gdocService.findGroupDoctor(income.getDoctorId(), order.getGroupId(), GroupDoctorStatus.正在使用.getIndex());
				if(list != null && list.size()>0 ){
					//在订单集团中，则以订单集团来计算抽成
					divisionGroupId = order.getGroupId();
				}else{
					//不在订单集团中 则取主集团，没有主集团则取最早加入的一个集团
					List<String> groupIdList = gdocService.getGroupListByDoctorId(income.getDoctorId());
					if(groupIdList != null && groupIdList.size() >0){
						divisionGroupId = groupIdList.get(0);
					}
					
				}
								
				//读取 抽成比例信息
				int divisionDoctorProp = 0;
				int divisionGroupProp = 0;
				
				//没有抽成集团，则没有抽成比例和上级医生
				if(divisionGroupId != null &&  !StringUtils.isEmpty(divisionGroupId)){
					GroupProfit profit = groupProfitService.getGroupProfitById(income.getDoctorId(), divisionGroupId);
					if(profit != null && profit.getConfig() != null){
						//先判断是否是门诊  (用orderType=3判断) 
						if(OrderType.outPatient.getIndex() == order.getOrderType()){
							divisionDoctorProp = profit.getConfig().getClinicParentProfit()!=null?profit.getConfig().getClinicParentProfit():0;
							divisionGroupProp = profit.getConfig().getClinicGroupProfit()!=null?profit.getConfig().getClinicGroupProfit():0;
						}
						//再判断不同的套餐细分 用packType进行判断
						if(PackType.message.getIndex() ==  order.getPackType()){
							//图文资讯套餐
							divisionDoctorProp = profit.getConfig().getTextParentProfit()!=null?profit.getConfig().getTextParentProfit():0;
							divisionGroupProp = profit.getConfig().getTextGroupProfit()!=null?profit.getConfig().getTextGroupProfit():0;
							
						}else if(PackType.phone.getIndex() ==  order.getPackType()){
							//电话资讯套餐
							divisionDoctorProp = profit.getConfig().getPhoneParentProfit()!=null?profit.getConfig().getPhoneParentProfit():0;
							divisionGroupProp = profit.getConfig().getPhoneGroupProfit()!=null?profit.getConfig().getPhoneGroupProfit():0;
							
						}else if(PackType.careTemplate.getIndex() ==  order.getPackType()){
							//关怀计划套餐
							divisionDoctorProp = profit.getConfig().getCarePlanParentProfit()!=null?profit.getConfig().getCarePlanParentProfit():0;
							divisionGroupProp = profit.getConfig().getCarePlanGroupProfit()!=null?profit.getConfig().getCarePlanGroupProfit():0;
							
						}
						divisionDoctorId = profit.getParentId();
					}					
				}

				
				//计算上级提成收入，集团提成收入
				BigDecimal doctorIncome = new BigDecimal(0);
				BigDecimal groupIncome = new BigDecimal(0);
				if(divisionDoctorId != null && divisionDoctorId != 0 && divisionDoctorProp != 0){
					doctorIncome =  new BigDecimal(income.getShareIncome())
							.multiply( new BigDecimal( divisionDoctorProp ))
							.divide(new BigDecimal(100), 5, BigDecimal.ROUND_HALF_UP);
				}
				if(divisionGroupProp != 0){
					groupIncome =  new BigDecimal(income.getShareIncome())
							.multiply( new BigDecimal(divisionGroupProp))
							.divide(new BigDecimal(100), 5, BigDecimal.ROUND_HALF_UP);
				}
				//计算实际收入
				BigDecimal actualIncome = new BigDecimal(income.getShareIncome()).subtract(doctorIncome);
				actualIncome = actualIncome.subtract(groupIncome);
				
				//更新收入信息状态
				income.setActualIncome(actualIncome.doubleValue());
				income.setDivisionDoctorProp(divisionDoctorProp);
				income.setDivisionGroupProp(divisionGroupProp);
				income.setDivisionDoctorId(divisionDoctorId);
				income.setDivisionGroupId(divisionGroupId);
				income.setOrderStatus("4");//读不到 枚举值 FIXME
				income.setCompleteTime(now);
				income.setExtend1(sdf.format(now));
				income.setExtend2(IncomePayStatus.未付款.getIndex());
				doctorIncomeMapper.updateByPrimaryKey(income);
				
				//医生提成表 新增记录
				addDoctorDivisionForOrderComplete(income,divisionDoctorId,divisionGroupId,doctorIncome.doubleValue());
				//集团提成表 新增记录
				addGroupDivisionForOrderComplete(income,divisionGroupId,groupIncome.doubleValue());
			}
			
		}
		
	}
	
	

	/* (non-Javadoc)
	 * @see com.dachen.health.pack.income.service.IIncomeService#addDoctorDivisionForOrderComplete(com.dachen.health.pack.order.entity.po.Order, com.dachen.health.pack.income.entity.po.DoctorIncome, java.lang.Integer, double)
	 */
	@Override
	public void addDoctorDivisionForOrderComplete(DoctorIncome income,Integer divisionDoctorId,String divisionGroupId,double doctorIncome) {
		//参数校验
		if(income == null){
			throw new ServiceException("医生收入信息为空");
		}
		if(divisionDoctorId == null || divisionDoctorId == 0 || income.getDoctorId().intValue() == divisionDoctorId.intValue()){
			//找不到上级用户，或者上级用户就是自己  不需要提成
			return;
		}
		if(doctorIncome == 0){
			//提成收入为0，不需要记录提成
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		long now = System.currentTimeMillis();

		//新增医生提成信息
		DoctorDivision doctorDivision = new DoctorDivision();
		doctorDivision.setGroupId(divisionGroupId);
		doctorDivision.setDivisionDoctorId(divisionDoctorId);
		doctorDivision.setOrderId(income.getOrderId());
		doctorDivision.setIncomeId(income.getId());
		doctorDivision.setIncomeDoctorId(income.getDoctorId());
		doctorDivision.setDivisionIncome(doctorIncome);
		
		doctorDivision.setSettleStatus(SettleStatus.未结算.getIndex()+"");
		doctorDivision.setCreateTime(now);
		doctorDivision.setExtend1(sdf.format(now));
		doctorDivision.setExtend2(IncomePayStatus.未付款.getIndex());//设置为未付款
		//持久化
		doctorDivisionMapper.insert(doctorDivision);
	
		
	}

	@Override
	public void addGroupDivisionForOrderComplete(DoctorIncome income ,String divisionGroupId,double groupIncome) {
		//参数校验
		if(income == null){
			throw new ServiceException("医生收入信息为空");
		}
		if(groupIncome == 0){
			//提成收入为0，不需要提成
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		long now = System.currentTimeMillis();
		//新增 集团提成信息
		GroupDivision groupDivision = new GroupDivision();
		groupDivision.setGroupId(divisionGroupId);
		groupDivision.setOrderId(income.getOrderId());
		groupDivision.setIncomeId(income.getId());
		groupDivision.setIncomeDoctorId(income.getDoctorId());
		groupDivision.setDivisionIncome(groupIncome);
		groupDivision.setSettleStatus(SettleStatus.未结算.getIndex()+"");
		groupDivision.setCreateTime(now);
		groupDivision.setExtend1(sdf.format(now));
		groupDivision.setExtend2(IncomePayStatus.未付款.getIndex());//设置为未付款
		//持久化
		groupDivisionMapper.insert(groupDivision);
		
	}

	@Override
	public void updateDoctorIncomeForSettle(List<DoctorIncome> docIncomeList) {
		// TODO 以后优化
		for(DoctorIncome record : docIncomeList){
			record.setSettleStatus(SettleStatus.结算.getIndex() +"");
			record.setSettleTime(System.currentTimeMillis());
			
			doctorIncomeMapper.updateByPrimaryKey(record);
		}
	}

	@Override
	public void updateDoctorDivisionForSettle(List<DoctorDivision>  docDivisionList) {
		// TODO 以后优化
		for(DoctorDivision record : docDivisionList){
			record.setSettleStatus(SettleStatus.结算.getIndex() +"");
			record.setSettleTime(System.currentTimeMillis());
			
			doctorDivisionMapper.updateByPrimaryKey(record);
		}
	}

	@Override
	public void updateGroupDivisionForSettle(List<GroupDivision> groupDivisionList) {
		// TODO 以后优化
		for(GroupDivision record : groupDivisionList){
			record.setSettleStatus(SettleStatus.结算.getIndex() +"");
			record.setSettleTime(System.currentTimeMillis());
			
			groupDivisionMapper.updateByPrimaryKey(record);
		}
	}
	
	@Override
	public void updateIncomeForPay(Integer settleId){
		//参数校验
		if(settleId == null || settleId == 0){
			throw new ServiceException("结算id为空");
		}
		
		//查找settle对应的income 记录
		List<DoctorIncome> doctorIncomeList =  doctorIncomeMapper.selectBySettleId(settleId);
		
		List<DoctorDivision> doctorDivisionList =  doctorDivisionMapper.selectBySettleId(settleId);
		
		List<GroupDivision> groupDivisionList =  groupDivisionMapper.selectBySettleId(settleId);
		
		//将相关income记录 设置为已付款
		updateDoctorIncomeForPay(doctorIncomeList);
		updateDoctorDivisionForPay(doctorDivisionList);
		updateGroupDivisionForPay(groupDivisionList);
	}
	
	/**
	 * @param docIncomeList
	 *@author wangqiao
	 *@date 2016年1月25日
	 */
	private void updateDoctorIncomeForPay(List<DoctorIncome> docIncomeList) {
		//参数校验
		if(docIncomeList == null || docIncomeList.size()==0){
			return;
		}
		
		// TODO 以后优化
		for(DoctorIncome record : docIncomeList){
			record.setExtend2(IncomePayStatus.已付款.getIndex() );
//			record.setSettleTime(System.currentTimeMillis());
			
			doctorIncomeMapper.updateByPrimaryKey(record);
		}
	}

	
	/**
	 * @param docDivisionList
	 *@author wangqiao
	 *@date 2016年1月25日
	 */
	private void updateDoctorDivisionForPay(List<DoctorDivision>  docDivisionList) {
		//参数校验
		if(docDivisionList == null || docDivisionList.size()==0){
			return;
		}
		// TODO 以后优化
		for(DoctorDivision record : docDivisionList){
			record.setExtend2(IncomePayStatus.已付款.getIndex() );
//			record.setSettleTime(System.currentTimeMillis());
			
			doctorDivisionMapper.updateByPrimaryKey(record);
		}
	}

	
	/**
	 * @param groupDivisionList
	 *@author wangqiao
	 *@date 2016年1月25日
	 */
	private void updateGroupDivisionForPay(List<GroupDivision> groupDivisionList) {
		//参数校验
		if(groupDivisionList == null || groupDivisionList.size()==0){
			return;
		}
		// TODO 以后优化
		for(GroupDivision record : groupDivisionList){
			record.setExtend2(IncomePayStatus.已付款.getIndex() );
//			record.setSettleTime(System.currentTimeMillis());
			
			groupDivisionMapper.updateByPrimaryKey(record);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.pack.income.service.IIncomeService#statDoctorIncomeByGroupId(com.dachen.health.pack.income.entity.param.DoctorIncomeParam)
	 */
	@Override
	public List<IncomeDetailsVO> statDoctorIncomeByGroupId(DoctorIncomeParam param){
		//参数校验
		if(param == null ){
			throw new ServiceException("参数为空");
		}
		List<IncomeDetailsVO> retList = new ArrayList<IncomeDetailsVO>();
		
		//查询医生 订单实际收入 和 分成收入  的收入
		List<DoctorIncomeVO> incomeList = doctorIncomeMapper.getTotalDoctorIncomeByGroupId(param);
		
		//查询医生姓名和手机号
		if(incomeList != null){
			for(DoctorIncomeVO vo : incomeList){
				if(vo == null){
					continue;
				}
				Integer doctorId = vo.getDoctorId();
				//如果该医生只有提成收入，则vo.getDoctorId()是空，可以使用vo.getDoctorId1()
				if(doctorId == null || doctorId==0){
					doctorId = vo.getDoctorId1();
				}
				User user = userService.getUser(doctorId);
				IncomeDetailsVO retVO= new IncomeDetailsVO();
				retVO.setDoctorId(vo.getDoctorId());
				if(user != null){
					retVO.setDoctorName(user.getName());
					retVO.setTelephone(user.getTelephone());
				}
				//计算总收入
				BigDecimal totalIncome = new BigDecimal(vo.getOrderIncome()).add(new BigDecimal(vo.getDivisionIncome()));
				retVO.setFinishedMoney(totalIncome.doubleValue());
				
				retList.add(retVO);
			}
		}
		
		
		return retList;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.pack.income.service.IIncomeService#countDoctorIncomeByGroupId(com.dachen.health.pack.income.entity.param.DoctorIncomeParam)
	 */
	@Override
	public int countStatDoctorIncomeByGroupId(DoctorIncomeParam param){
		
		return doctorIncomeMapper.countTotalDoctorIncomeByGroupId(param);
//		return 0;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.pack.income.service.IIncomeService#getGroupIncomeByGroupId(com.dachen.health.pack.income.entity.param.DoctorIncomeParam)
	 */
	@Override
	public List<IncomeDetailsVO> getGroupIncomeByGroupId(DoctorIncomeParam param){
		//参数校验
		if(param == null ){
			throw new ServiceException("参数为空");
		}
		List<IncomeDetailsVO> retList = new ArrayList<IncomeDetailsVO>();
		
		//查询集团收入明细
		List<DoctorIncomeVO> incomeList = groupDivisionMapper.getGroupDivisionByGroupId(param);
		
		//查询医生姓名和手机号
		if(incomeList != null){
			for(DoctorIncomeVO vo : incomeList){
				if(vo == null){
					continue;
				}
				User user = userService.getUser(vo.getDoctorId());
				IncomeDetailsVO retVO= new IncomeDetailsVO();
				retVO.setDoctorId(vo.getDoctorId());
				retVO.setUpGroup(vo.getGroupId());
				retVO.setOrderNo(vo.getOrderNo());
				retVO.setOrderId(vo.getOrderId());
				
				
				//设置用户名，用户手机
				if(user != null){
					retVO.setDoctorName(user.getName());
					retVO.setTelephone(user.getTelephone());
				}
				//设置订单类型，订单号
				retVO.setOrderNo(vo.getOrderNo());
				retVO.setOrderId(vo.getOrderId());
				retVO.setOrderTypeName(IncomeDetailsVO.getTypeName(vo.getOrderType(), vo.getPackType()));
				
				//设置提成金额 ， 订单金额
				retVO.setGroupMoney(vo.getDivisionIncome());
				retVO.setMoney(vo.getOrderPrice());
				
				retList.add(retVO);
			}
		}
		
		
		return retList;
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.pack.income.service.IIncomeService#countGroupIncomeByGroupId(com.dachen.health.pack.income.entity.param.DoctorIncomeParam)
	 */
	@Override
	public int countGroupIncomeByGroupId(DoctorIncomeParam param){
		
		return groupDivisionMapper.countGroupDivisionByGroupId(param);
	}

	@Override
	public List<DoctorIncome> getDoctorIncomeByDoctorId(Integer doctorId, String orderStatus, String settleStatus) {
		
		DoctorIncomeExample example = new DoctorIncomeExample();
		DoctorIncomeExample.Criteria criteria = example.createCriteria();
		//查询条件
		if(doctorId != null && doctorId != 0){
			criteria.andDoctorIdEqualTo(doctorId);
		}
		if(!StringUtils.isEmpty(orderStatus)){
			criteria.andOrderStatusEqualTo(orderStatus);
		}
		if(!StringUtils.isEmpty(settleStatus)){
			criteria.andSettleStatusEqualTo(settleStatus);
		}

		List<DoctorIncome> incomeList = doctorIncomeMapper.selectByExample(example);
		
		return incomeList;
	}

	@Override
	public List<DoctorDivision> getDoctorDivisionByDoctorId(Integer doctorId, String settleStatus) {
		DoctorDivisionExample example = new DoctorDivisionExample();
		DoctorDivisionExample.Criteria criteria = example.createCriteria();
		//查询条件
		if(doctorId != null && doctorId != 0){
			criteria.andDivisionDoctorIdEqualTo(doctorId);
		}
		if(!StringUtils.isEmpty(settleStatus)){
			criteria.andSettleStatusEqualTo(settleStatus);
		}
		List<DoctorDivision> incomeList = doctorDivisionMapper.selectByExample(example);
		
		return incomeList;
		
	}

	@Override
	public List<GroupDivision> getGroupDivisionByDoctorId(String groupId, String settleStatus) {
		GroupDivisionExample example = new GroupDivisionExample();
		GroupDivisionExample.Criteria criteria = example.createCriteria();
		//查询条件
		if(!StringUtils.isEmpty(groupId)){
			criteria.andGroupIdEqualTo(groupId);
		}
		if(!StringUtils.isEmpty(settleStatus)){
			criteria.andSettleStatusEqualTo(settleStatus);
		}
		List<GroupDivision> incomeList = groupDivisionMapper.selectByExample(example);
		
		return incomeList;
		
	}

	/**
	 * 获取医生的账户余额、总收入、未完成订单金额
	 * 算法：
	 * 1、账户余额：从t_doctor_income中查询doctor_id=自己、order_status=已完成、settle_status=未结算的actual_income的合计
	 *           + 从t_doctor_division中查询division_doctor_id=自己、settle_status=未结算的division_income的合计
	 * 2、总收入：从t_doctor_income中查询doctor_id=自己、order_status=已完成的actual_income的合计
	 *           + 从t_doctor_division中查询division_doctor_id=自己的division_income的合计
	 * 2、未完成订单金额：从t_doctor_income中查询doctor_id=自己、order_status=未完成的share_income的合计
	 */
	@Override
	public Map<String, Object> getDoctorBalances(IncomeDetailsParam param) {
		Map<String, Object> map = new HashMap<String, Object>();
		//查询账户余额
		double ownBalance = getDoctorIncomeBalance(param.getDoctorId(),OrderStatus.已完成,IncomePayStatus.未付款);
		double divisionBalance = getDoctorDivisionBalance(param.getDoctorId(),IncomePayStatus.未付款);
		map.put("balance", ownBalance+divisionBalance);
		
		//查询总收入
		double totalBalance = getDoctorIncomeBalance(param.getDoctorId(),OrderStatus.已完成,null);
		double totalDivisionBalance = getDoctorDivisionBalance(param.getDoctorId(),null);
		map.put("totalIncome", totalBalance+totalDivisionBalance);
		
		//查询未完成收入
		double unbalance = getDoctorUnfinishBalance(param.getDoctorId(),OrderStatus.已支付);
		map.put("unbalance", unbalance);

		return map;
	}

	
	
	private double getDoctorIncomeBalance(Integer doctorId,OrderStatus orderStatus,IncomePayStatus incomePayStatus) {
		IncomeVONew incomeVONew=new IncomeVONew();
		incomeVONew.setDoctorId(doctorId);
		if(orderStatus!=null)
		{
		incomeVONew.setOrderStatus(orderStatus.getIndex());
		}
		if(incomePayStatus!=null)
		{
		incomeVONew.setExtend2(incomePayStatus.getIndex());
		}
		Integer ownBalance=incomeMapperNew.getDoctorIncomeBalance(incomeVONew);
		return ownBalance!=null?ownBalance.doubleValue():0;
	}
	
	private double getDoctorDivisionBalance(Integer doctorId,IncomePayStatus incomePayStatus) {
		IncomeVONew incomeVONew=new IncomeVONew();
		incomeVONew.setDoctorId(doctorId);
		if(incomePayStatus!=null)
		{
		incomeVONew.setExtend2(incomePayStatus.getIndex());
		}
		Integer ownBalance=incomeMapperNew.getDoctorDivisionBalance(incomeVONew);
		return ownBalance!=null?ownBalance.doubleValue():0;
	}

	private double getDoctorUnfinishBalance(Integer doctorId,OrderStatus orderStatus) {
		IncomeVONew incomeVONew=new IncomeVONew();
		incomeVONew.setDoctorId(doctorId);
		if(orderStatus!=null)
		{
		incomeVONew.setOrderStatus(orderStatus.getIndex());
		}
		Integer ownBalance=incomeMapperNew.getDoctorUnFinishBalance(incomeVONew);
		return ownBalance!=null?ownBalance.doubleValue():0;
	}
	
	/**
	 * 获取某种类型余额的订单的明细（账户余额明细、总收入明细、未完成订单明细）
	 * type(1：未完成；2：账户余额（已完成未结算）;3:总收入（所有已完成）)
	 */
	@Override
	public PageVO getDoctorDetails(IncomeDetailsParam param) {
		IncomeVONew incomeVONew=new IncomeVONew();
		incomeVONew.setPageSize(param.getPageSize());
		incomeVONew.setStart(param.getPageIndex() * param.getPageSize());
		incomeVONew.setDoctorId(param.getDoctorId());
		List<IncomeDetailsVO> voList=new ArrayList<IncomeDetailsVO>();
		 if(param.getType()!=null&&param.getType().intValue()==1)
		{
			incomeVONew.setOrderStatus(OrderStatus.已支付.getIndex());
			 voList = incomeMapperNew.getDoctorUnFinishDetail(incomeVONew);
//			 IncomelogParam paramNew = new IncomelogParam();
//			 paramNew.setDoctorId(param.getDoctorId());
//			 paramNew.setPageIndex(param.getPageIndex());
//			 paramNew.setPageSize(param.getPageSize());
//			 return incomelogService.getUnfinishedYMList(paramNew);
			 
		}
		 else if(param.getType()!=null&&param.getType().intValue()==2)
		{
			incomeVONew.setOrderStatus(OrderStatus.已完成.getIndex());
			incomeVONew.setExtend2(IncomePayStatus.未付款.getIndex());
			  voList = incomeMapperNew.getDoctorFinishDetail(incomeVONew);
		}
		else if(param.getType()!=null&&param.getType().intValue()==3)
		{
			incomeVONew.setOrderStatus(OrderStatus.已完成.getIndex());
			 voList = incomeMapperNew.getDoctorFinishDetail(incomeVONew);
		}
	
		PageVO pageVO = new PageVO();
		PageIncome page = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
		Map<String,PageIncome> map = new LinkedHashMap<String,PageIncome>();
		for(IncomeDetailsVO vo :voList){
			String key = DateUtil.formatDate2Str(vo.getCreateTime(), sdf);
			page = map.get(key);
			if(page == null){
				page = new PageIncome();
			}
			page.setKeyYM(key);
			List<PageIncomeDetails> pdList = page.getList();
			if(pdList == null){
				pdList = new ArrayList<PageIncomeDetails>();
			}
			PageIncomeDetails pd = new PageIncomeDetails();
			pd.setDay(String.valueOf(DateUtil.getDayOfLongTime(vo.getCreateTime())));
			pd.setOrderId(vo.getOrderId());
			pd.setOrderTypeName(IncomeDetailsVO.getTypeName(vo.getOrderType(), vo.getPackType()));
			if( vo.getIncomeType().intValue()==IncomeEnum.IncomeType.提成.getIndex()){//提成
				User user = userService.getUser(vo.getIncomeDoctorId());
				if(user != null){
					pd.setDoctorName(user.getName());
				}
				pd.setMoney(vo.getMoney());
				pd.setType(IncomeEnum.IncomeType.提成.getIndex());
			}else{//直接收益
				pd.setMoney(vo.getMoney());
				pd.setType(IncomeEnum.IncomeType.非提成.getIndex());
			}
			pdList.add(pd);//提成订单排在最后
			page.setList(pdList);
			map.put(key, page);
		}
		
		List<PageIncome> plist = new ArrayList<PageIncome>();
		
		Iterator<Entry<String, PageIncome>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, PageIncome> entry = iterator.next();
			PageIncome pi = entry.getValue();
			List<PageIncomeDetails> pl = pi.getList();
			pl = sortList(pl);
			pi.setList(pl);
			plist.add(pi);
		}
		pageVO.setPageData(plist);
		
		  Integer count=null;
		  if(param.getType()!=null&&param.getType().intValue()==1)
			{
			    count=incomeMapperNew.getDoctorUnFinishDetailCount(incomeVONew);
			}
			 else if(param.getType()!=null&&param.getType().intValue()==2)
			{
				  count=incomeMapperNew.getDoctorFinishDetailCount(incomeVONew);
			}
			else if(param.getType()!=null&&param.getType().intValue()==3)
			{
				  count=incomeMapperNew.getDoctorFinishDetailCount(incomeVONew);
			}
		  pageVO.setTotal(Long.valueOf(count!=null?count:0));
		pageVO.setPageIndex(param.getPageIndex());
		pageVO.setPageSize(param.getPageSize());
		return pageVO;
	
	}

	private List<PageIncomeDetails> sortList(List<PageIncomeDetails> list){
		if(list == null || list.isEmpty()){
			return list;
		}
		Collections.sort(list, new Comparator<PageIncomeDetails>() {
            public int compare(PageIncomeDetails arg0, PageIncomeDetails arg1) {
                return arg1.getType().compareTo(arg0.getType());
            }
        });
		return list;
	}

}
