package com.dachen.health.pack.incomeNew.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderRefundStatus;
import com.dachen.health.commons.constants.OrderEnum.OrderRefundType;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.GroupProfit;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupProfitService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.account.entity.param.BankCardParam;
import com.dachen.health.pack.account.entity.vo.BankCardVO;
import com.dachen.health.pack.account.entity.vo.RechargeVO;
import com.dachen.health.pack.account.service.IBankCardService;
import com.dachen.health.pack.incomeNew.constant.IncomeEnumNew;
import com.dachen.health.pack.incomeNew.constant.IncomeEnumNew.*;
import com.dachen.health.pack.incomeNew.entity.param.IncomelogParam;
import com.dachen.health.pack.incomeNew.entity.param.SettleNewParam;
import com.dachen.health.pack.incomeNew.entity.po.*;
import com.dachen.health.pack.incomeNew.entity.vo.*;
import com.dachen.health.pack.incomeNew.mapper.*;
import com.dachen.health.pack.incomeNew.service.IncomelogService;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.entity.po.OrderDoctorExample;
import com.dachen.health.pack.order.mapper.OrderDoctorMapper;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@Service
public class IncomelogServiceImpl implements IncomelogService{
	
	@Autowired
	protected IncomelogMapper incomelogMapper;
	@Autowired
    protected SettleMapperNew settleMapperNew;
	@Autowired
    protected CashMapper cashMapper;
	@Autowired
    protected ExpendMapper expendMapper;
	@Autowired
    protected RefundOrderMapper refundOrderMapper;
	@Autowired
    protected UserManager userManager;
	@Autowired
    protected OrderDoctorMapper orderDoctorMapper;
	@Autowired
    protected IGroupProfitService groupProfitService;
	@Autowired
    protected IGroupDoctorService gdocService;
	@Autowired
    protected IGroupService groupService;
	@Autowired
    protected IBankCardService bankCardService;
	@Resource
    protected MobSmsSdk mobSmsSdk;
	@Autowired
    protected IBusinessServiceMsg sendMsgService;
	@Autowired
    protected IBaseDataService baseDataService;
	@Autowired
    protected IOrderSessionService orderSessionService;

	private static Double money() {
        return Double.valueOf(PropertiesUtil.getContextProperty("settle.Money"));
    }

	/**
	 * yyyy年MM月
	 */
	private static SimpleDateFormat ym= new SimpleDateFormat("yyyy年MM月");
	private static SimpleDateFormat yy = new SimpleDateFormat("yyyy");

	@Override
	public Map<String,Double> getDoctorIncomeIndex(IncomelogParam param) {
		if(param.getDoctorId() == null){
			throw new ServiceException("医生Id不能为空");
		}
		//总收入
		param.setType(ObjectType.doctor.getIndex());
		DoctorMoneyVO totalMoney =  incomelogMapper.getTotalMoneyAndCountIncomByParam(param);
		double tMoney = totalMoney==null?0:totalMoney.getTotalMoney();
		//余额
		DoctorMoneyVO balanceMoney =  incomelogMapper.getBalanceMoneyAndCountIncomByParam(param);
		double  bMoney = balanceMoney==null?0:balanceMoney.getBalance();
		//未完成订单金额
		DoctorMoneyVO unFinshedMoney = incomelogMapper.getUnFinishedMoneyAndCountByParam(param);
		double uMoney = unFinshedMoney==null?0:unFinshedMoney.getUnFinshedMoney();
		
		Map<String,Double> result= new HashMap<String,Double>();
		result.put("totalIncome", tMoney);
		result.put("balance", bMoney);
		result.put("unbalance", uMoney);
		return result;
	}

	@Override
	public PageVO getBalanceDetail(IncomelogParam param) {
		if(param.getDoctorId() == null){
			throw new ServiceException("医生ID不能为空");
		}
		
		List<BalanceVO> resultlist = new ArrayList<BalanceVO>();
		List<BaseDetailVO> list = incomelogMapper.getBalanceDetail(param);
		if(list != null){
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
			BalanceVO childVO = null;
			String preYM="";
			for(BaseDetailVO temp : list){
				String time = ym.format(temp.getCreateDate());
				setTypeName(temp);
				//通过childId，读取 childName
				if(temp.getChildId() >0 ){
					User childUser = userManager.getUser(temp.getChildId());
					temp.setChildName(childUser.getName());
				}
				
				if(preYM.equals(time)){//相同年月只更新
					childVO.getList().add(temp);
				}else{//不同年月，做新建
					preYM=time;
					childVO = new BalanceVO();
					childVO.setYM(time);
					childVO.getList().add(temp);
					resultlist.add(childVO);
				}
			}
		}
		PageVO page = new PageVO();
		page.setPageIndex(param.getPageIndex());
		page.setPageData(resultlist);
		int count=incomelogMapper.getBalanceDetailCount(param);
		page.setTotal(Long.valueOf(count));
		return page;
	}
	
	
	
	@Override
	public PageVO getTotalIncomeYMList(IncomelogParam param) {
		if(param.getType() == null ){
			throw new ServiceException("查询类型不能为空");
		}else{
			if(param.getDoctorId() == null && StringUtil.isEmpty(param.getGroupId())){
				throw new ServiceException("医生Id 或者集团 ID为空");
			}
		}
		boolean doctor = false;
		if(param.getType() == ObjectType.doctor.getIndex()){
			doctor = true;
		}
		//总收入
		PageVO page = new PageVO();
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		List<TotalIncomeVO> resultlist = new ArrayList<TotalIncomeVO>();
		List<DoctorMoneyVO> list =  incomelogMapper.getTotalMoneyYMList(param);
		if(list != null && doctor){//医生端需要分组
			int preYY =0;
			TotalIncomeVO childVO = null;
			for(DoctorMoneyVO vo : list){
				if(vo.getYear() == preYY){
					childVO.getList().add(vo);
				}else{
					preYY = vo.getYear();
					childVO = new TotalIncomeVO();
					childVO.setYear(vo.getYear());
					childVO.getList().add(vo);
					resultlist.add(childVO);
				}
			}
		}
		if(doctor){
			page.setPageData(resultlist);
		}else{
			page.setPageData(list);
		}
		page.setTotal(Long.valueOf(incomelogMapper.getTotalMoneyYMListCount(param)));
		return page;
	}

	@Override
	public PageVO getTotalIncomeYMDetail(IncomelogParam param) {
		boolean doctor = false;
		if(param.getType() == ObjectType.doctor.getIndex()){// 医生
			doctor = true;
			if(param.getDoctorId() == null){
				throw new ServiceException("医生Id不能为空");
			}
		}else if(param.getType() == ObjectType.group.getIndex()){// 集团
			if(StringUtil.isEmpty(param.getGroupId())){
				throw new ServiceException("集团Id不能为空");
			}
		}
		if(StringUtil.isEmpty(param.getMonth())){
			throw new ServiceException("时间不能为空");
		}
		List<TotalIncomeDVO> resultlist = new ArrayList<TotalIncomeDVO>();
		List<BaseDetailVO> list = incomelogMapper.getTotalMoneyYMDetail(param);
		if(list != null){
			for(BaseDetailVO vo : list){
				setTypeName(vo);//设置类型
					//查询下级医生的电话与用户名
//						User user = userManager.getUser(vo.getChildId());
					User user = getUser(vo.getChildId());
					if(user != null){
						vo.setChildName(user.getName());
						vo.setTelephone(user.getTelephone());
					}
			}
			//分组
			if(doctor){
				listToGroup(list,resultlist);
			}
		}
		
		PageVO page = new PageVO();
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		if(doctor){
			page.setPageData(resultlist);
		}else{
			page.setPageData(list);
		}
		
		page.setTotal(Long.valueOf(incomelogMapper.getTotalMoneyYMDetailCount(param)));
		return page;
	}

	/**
	 * 订单完成时的操作
	 * 1.从订单表t_order取出订单所在集团
	 * 2.去医生订单表t_order_doctor 表查出订单所参与医生及分比例
	 * 3.先判断医生是否在订单集团中，否则去医生集团表c_group_doctor查主集团，若没有主集团则取最早加入的集团
	 * 4.去c_group_profit表里查出集团和上级医生对应的分成比例
	 * 5.把对应收入主体以及分成金额上入级关系保存到收入表t_incomelog，
	 * 6.更新或插入结算表t_settle表，每个月每个结算主体最多只有一条数据
	 * 
	 */
	@Override
	public void addOrderIncomelog(Order order) {
		if(order == null || order.getPrice() == null || order.getPrice() == 0){
			return;
		}
		OrderDoctorExample example = new OrderDoctorExample();
		example.createCriteria().andOrderIdEqualTo(order.getId());
		List<OrderDoctor> list = orderDoctorMapper.selectByExample(example);
		
		if(list != null && list.size() >0){//获取参与订单医生以及分成比例
			
			OrderSession session = orderSessionService.findOneByOrderId(order.getId());
			Long time = null;
			if(session != null){
				time = session.getServiceEndTime();
			}
			if(time == null){
				return;
			}
			
			String orderGroupId = order.getGroupId();
			//会诊订单，对应订单集团拿的分成比例
			int consulationProp = 100;
			
			for(OrderDoctor od : list){
				consulationProp = consulationProp - od.getSplitRatio();
				
				//获取对应上级抽成集团ID和上级抽成医生ID
				String upGroupId = getDoctorGroupID(od,orderGroupId);
				Integer upDoctorId = null;
				
				//根据医生ID和上级集团ID获取上级医生和集团抽成比例
				GroupProfit profit = getGroupProfit(upGroupId , od.getDoctorId());
				
				//读取 抽成比例信息
				int divisionDoctorProp = 0;
				int divisionGroupProp = 0;
				if(profit != null && profit.getConfig() != null){
					Map<String , Integer> map = getUpProfit(order,profit);
					divisionDoctorProp = map.get("doctor");
					divisionGroupProp = map.get("group");
					
					upDoctorId = profit.getParentId();//如果没有上级医生就不抽成，抽成比例为0
					if(upDoctorId == null || upDoctorId == 0 || upDoctorId.intValue() == od.getDoctorId().intValue()  ){
						divisionDoctorProp = 0;
					}
				}
				Long self= order.getPrice()*od.getSplitRatio()/100;
				Long upDoctor = self * divisionDoctorProp /100;
				Long upGroup = self * divisionGroupProp /100;
				
				//还要减去抽成的金额
				self = self - upDoctor - upGroup;
				if(self > 0){//有金额才添加到收入中
					Incomelog selfRecord = new Incomelog();
					selfRecord.setOrderId(order.getId());
					selfRecord.setDoctorId(od.getDoctorId());
					selfRecord.setType(ObjectType.doctor.getIndex());
					selfRecord.setMoney(Double.valueOf(self));
					selfRecord.setLogType(LogType.order.getIndex());
					selfRecord.setMonth(ym.format(time));
					selfRecord.setYear(Integer.parseInt(yy.format(time)));
					selfRecord.setCreateDate(time);
					incomelogMapper.insertSelective(selfRecord);//保存收入
					
					SettleNew settle = new SettleNew();
					settle.setMonth(ym.format(time));
					settle.setYear(Integer.parseInt(yy.format(time)));
					settle.setCreateDate(time);
					settle.setStatus(SettleStatus.forbidden.getIndex());
					settle.setDoctorId(od.getDoctorId());
					settle.setObjectType(ObjectType.doctor.getIndex());
					settle.setMonthMoney(Double.valueOf(self));
					settle.setTotalMoney(Double.valueOf(self));
					saveOrUpadateSettle(settle);//更新自己结算
				}
				
				
				if(upDoctor> 0  && upDoctorId != null && upDoctorId !=0){//有金额才添加到收入中
					Incomelog upDRecord = new Incomelog();
					upDRecord.setOrderId(order.getId());
					upDRecord.setDoctorId(upDoctorId);
					upDRecord.setChildDoctorId(od.getDoctorId());
					upDRecord.setType(ObjectType.doctor.getIndex());
//					upDRecord.setMoney(updoctorIncome.doubleValue());
					upDRecord.setMoney(Double.valueOf(upDoctor));
					upDRecord.setLogType(LogType.doctorCommission.getIndex());
					upDRecord.setMonth(ym.format(time));
					upDRecord.setYear(Integer.parseInt(yy.format(time)));
					upDRecord.setCreateDate(time);
					incomelogMapper.insertSelective(upDRecord);
					
					SettleNew settle = new SettleNew();
					settle.setMonth(ym.format(time));
					settle.setYear(Integer.parseInt(yy.format(time)));
					settle.setCreateDate(time);
					settle.setStatus(SettleStatus.forbidden.getIndex());
					settle.setDoctorId(upDoctorId);
					settle.setObjectType(ObjectType.doctor.getIndex());
					settle.setMonthMoney(Double.valueOf(upDoctor));
					settle.setTotalMoney(Double.valueOf(upDoctor));
					saveOrUpadateSettle(settle);//更新上级结算
				}
				
				if(upGroup > 0 && StringUtil.isNotEmpty(upGroupId)){//有金额才添加到收入中
					Incomelog upGRecord = new Incomelog();
					upGRecord.setOrderId(order.getId());
					upGRecord.setGroupId(upGroupId);
					upGRecord.setChildDoctorId(od.getDoctorId());
					upGRecord.setType(ObjectType.group.getIndex());
					upGRecord.setMoney(Double.valueOf(upGroup));
					upGRecord.setLogType(LogType.groupCommission.getIndex());
					upGRecord.setMonth(ym.format(time));
					upGRecord.setYear(Integer.parseInt(yy.format(time)));
					upGRecord.setCreateDate(time);
					incomelogMapper.insertSelective(upGRecord);
					
					SettleNew settle = new SettleNew();
					settle.setMonth(ym.format(time));
					settle.setYear(Integer.parseInt(yy.format(time)));
					settle.setCreateDate(time);
					settle.setStatus(SettleStatus.forbidden.getIndex());
					settle.setGroupId(upGroupId);
					settle.setObjectType(ObjectType.group.getIndex());
					settle.setMonthMoney(Double.valueOf(upGroup));
					settle.setTotalMoney(Double.valueOf(upGroup));
					saveOrUpadateSettle(settle);//更新集团结算
				}
			}
			
			//如果是会诊订单还需要保存对应集团的分成收入,更新结算
			if(order.getOrderType() == OrderEnum.OrderType.consultation.getIndex() && consulationProp > 0&&StringUtil.isNotEmpty(orderGroupId)){
				
				Incomelog group = new Incomelog();
				group.setOrderId(order.getId());
				group.setGroupId(orderGroupId);
//				upGRecord.setChildDoctorId(od.getDoctorId());
//				double money = calculationInCome(Double.valueOf(order.getPrice()), consulationProp).doubleValue();
				Long money = order.getPrice() * consulationProp / 100 ;
				group.setMoney(Double.valueOf(money));
				group.setType(ObjectType.group.getIndex());
				group.setLogType(LogType.groupOrderCommission.getIndex());
				group.setMonth(ym.format(time));
				group.setYear(Integer.parseInt(yy.format(time)));
				group.setCreateDate(time);
				incomelogMapper.insertSelective(group);//插入一条结算纪录
				
				SettleNew settle = new SettleNew();
				settle.setMonth(ym.format(time));
				settle.setYear(Integer.parseInt(yy.format(time)));
				settle.setCreateDate(time);
				settle.setStatus(SettleStatus.forbidden.getIndex());
				settle.setGroupId(orderGroupId);
				settle.setObjectType(ObjectType.group.getIndex());
				settle.setMonthMoney(Double.valueOf(money));
				saveOrUpadateSettle(settle);//保存或更新集团结算
			}
			
		}
	}
	
	@Override
	public CashVO getCashRecordById(Integer id) {
		if(id == null ){
			throw new ServiceException("打款不能为空");
		}
		CashVO vo  = cashMapper.getCashDetailByID(id);
		if(vo != null && !StringUtil.isEmpty(vo.getBankID())){
			String bankId = vo.getBankID();
			char[] s = bankId.toCharArray();
			for(int i= 0; i< s.length-4;i++){
				s[i] = '*';
			}
			vo.setBankID(new String(s));
		}
		return vo;
	}

	@Override
	public PageVO getGroupIncomeDetailByMore(IncomelogParam param) {
		//姓名 手机号 类型  起止时间（当前月份）
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException("集团Id不能为空");
		}
		if(StringUtil.isEmpty(param.getMonth())){
			throw new ServiceException("月份不能为空");
		}
		//设置类型
		setOrderTypeAndPack(param);
		PageVO page = new PageVO();
		if(StringUtil.isEmpty(param.getChildName()) && StringUtil.isEmpty(param.getTelephone())){
			param.setuIdList(null);
		}else {
			User user = new User();
			user.setUserType(UserEnum.UserType.doctor.getIndex());
			if(!StringUtil.isEmpty(param.getChildName())){
				user.setName(param.getChildName());
			}
			if(!StringUtil.isEmpty(param.getTelephone())){
				user.setTelephone(param.getTelephone());
			}
			List<User> userList = userManager.getUserByNameAndTelephoneAndType(user);
			if(userList == null || userList.size() == 0){
				page.setPageIndex(param.getPageIndex());
				page.setPageSize(param.getPageSize());
				page.setTotal(Long.valueOf(0));
				page.setPageData(new ArrayList<BaseDetailVO>());
				return page;
			}else{
				List<Integer> list = new ArrayList<Integer>();
				for(User u :userList){
					list.add(u.getUserId());
				}
				param.setuIdList(list);
			}
			
		}
		
		
		List<BaseDetailVO> resultlist = incomelogMapper.getMoreConditionMMList(param);
		if(resultlist !=null && resultlist.size() > 0){
			for(BaseDetailVO vo : resultlist){
				setTypeName(vo);//设置类型
//					User user = userManager.getUser(vo.getChildId());
					User user = getUser(vo.getChildId());
					if(user != null){
						vo.setChildName(user.getName());
						vo.setTelephone(user.getTelephone());
					}
			}
		}
		
		page.setPageData(resultlist);
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		page.setTotal(Long.valueOf(incomelogMapper.getMoreConditionMMCount(param)));
		return page;
	}
	
	private  void setOrderTypeAndPack(IncomelogParam param){
		if(StringUtil.isEmpty(param.getoType())){
			return;
		}
		if(param.getoType().equalsIgnoreCase("HZ")){
			param.setOrderType(OrderType.consultation.getIndex());
		}else if(param.getoType().equalsIgnoreCase("MZ")){
			param.setOrderType(OrderType.outPatient.getIndex());
		}else if(param.getoType().equalsIgnoreCase("TW")){
			param.setOrderType(OrderType.order.getIndex());
			param.setPackType(PackEnum.PackType.message.getIndex());
		}else if(param.getoType().equalsIgnoreCase("DH")){
			param.setOrderType(OrderType.order.getIndex());
			param.setPackType(PackEnum.PackType.phone.getIndex());
		}else if(param.getoType().equalsIgnoreCase("JK")){
			param.setOrderType(OrderType.care.getIndex());
		}else if(param.getoType().equalsIgnoreCase("KJ")){
			param.setKj(0);
		}else if(param.getoType().equalsIgnoreCase("TK")){
			param.setLogType(13);
		}else if(param.getoType().equalsIgnoreCase("MY")){
			param.setOrderType(OrderType.appointment.getIndex());
		}
	}

	@Override
	public PageVO getSettleYMList(SettleNewParam param){
		if(param.getObjectType() == null){
			throw new ServiceException("查询类型不能为空");
		}
		//当前月份     待结算金额指当月发生金额,已结算金额为空
		//历史月份    待结算金额就是待结算金额，有结算的就是待结算金额，没有结算的就是0
		List<SysSettleVO> resultlist = settleMapperNew.selectSettleYMListByType(param);
		long time = System.currentTimeMillis();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
		String timeStr = ym.format(time);
		for(SysSettleVO vo :resultlist){
			if(vo.getMonth().equals(timeStr)){
				vo.setStatus(SettleStatus.forbidden.getIndex());
			}else if(vo.getMonth().equals(getMonthSumNum(time,-1))){
				vo.setStatus(SettleStatus.unsettle.getIndex());
			}else{
				vo.setStatus(SettleStatus.expired.getIndex());
			}
		}
		PageVO page = new PageVO();
		page.setPageData(resultlist);
		page.setTotal(Long.valueOf(settleMapperNew.selectSettleYMCountByType(param)));
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		return page;
	}
	
	
	
	@Override
	public PageVO getUnfinishedYMList(IncomelogParam param) {
		if(param.getDoctorId() == null){
			throw new ServiceException("医生ID不能为空");
		}
		
		List<BaseDetailVO> list = incomelogMapper.getUnFinishedMoneyList(param);
		List<UnfinishedVO> resultlist = new ArrayList<UnfinishedVO>();
		if(list != null){
			UnfinishedVO child = null;
			String timeStr = "";
			for(BaseDetailVO vo :list){
				setOrderName(vo);
				if(timeStr.equals(ym.format(vo.getCreateDate()))){
					child.getList().add(vo);
				}else{
					timeStr = ym.format(vo.getCreateDate());
					child = new UnfinishedVO();
					child.setKeyYM(timeStr);
					child.getList().add(vo);
					resultlist.add(child);
				}
			}
		}
		PageVO page = new PageVO();
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		page.setPageData(resultlist);
		page.setTotal(Long.valueOf(incomelogMapper.getUnFinishedMoneyCount(param)));
		return page;
	}

	@Override
	public PageVO getSettleList(SettleNewParam param){
		if(StringUtil.isEmpty(param.getMonth())){
			throw new ServiceException("month不能为空");
		}
		if(param.getObjectType() == null){
			throw new ServiceException("objectType不能为空");
		}
		//如果是当月待结算金额是就是当月实际发生金额（monthMoney或totalMoney）,如果是历史月份已结算则是totalMoney未结算则是0
		List<SettleDetailVO> resultList = settleMapperNew.selectListByMonth(param);
		if(resultList != null && resultList.size() > 0){
			for(SettleDetailVO vo: resultList){
				if(vo.getStatus().intValue() == SettleStatus.settled.getIndex()){
					vo.setActualMoney(vo.getNoSettleMoney());
				}else{
					vo.setActualMoney(Double.valueOf(0));
				}
				
				if(vo.getObjectType() == ObjectType.doctor.getIndex()){
//					User user = userManager.getUser(vo.getDoctorId());
					User user = getUser(vo.getDoctorId());
					if(user != null){
						vo.setUserName(user.getName());
						vo.setTelephone(user.getTelephone());
					}
				}else if(vo.getObjectType() == ObjectType.group.getIndex()){
					Group group =groupService.getGroupById(vo.getGroupId());
					if(group != null ){
						vo.setUserName(group.getName());
					}
				}
				judgeInfo(vo);
			}
		}
		PageVO page = new PageVO();
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		page.setPageData(resultList);
		page.setTotal(Long.valueOf(settleMapperNew.selectListByMonthCount(param)));
		return page;
	}
	
	
	private void judgeInfo(SettleDetailVO vo){
		//姓名、手机号、开户名称、身份证号、开户银行、支行、银行账号
		vo.setInofOK(false);
		if(!StringUtil.isEmpty(vo.getUserName()) && !StringUtil.isEmpty(vo.getBankNo()) 
				&& !StringUtil.isEmpty(vo.getUserRealName()) && !StringUtil.isEmpty(vo.getPersonNo())
				&& !StringUtil.isEmpty(vo.getBankName()) && !StringUtil.isEmpty(vo.getSubBankName())){
			if(vo.getObjectType() == ObjectType.group.getIndex()){
				vo.setInofOK(true);
			}else if(vo.getObjectType() == ObjectType.doctor.getIndex()  && !StringUtil.isEmpty(vo.getTelephone())){
				vo.setInofOK(true);
			}
			
		}
	}
	
	@Override
	public Map<String,Object> settle(SettleNewParam param) throws HttpApiException {
		//参数校验
		if(param.getId() == null){
			throw new ServiceException("ID不能为空");
		}
		if(param.getSettleMoney()== null || param.getExpandMoney() == null){
			throw new ServiceException("扣减金额或者结算金额不正确");
		}
		SettleNew settle = settleMapperNew.selectByPrimaryKey(param.getId());
		if(settle == null){
			throw new ServiceException("ID 有误");
		}
		
		//业务校验
		Map<String,Object> map = new HashMap<String,Object>();
		if(settle.getStatus().intValue() != SettleStatus.unsettle.getIndex()){
			map.put("msg", "当前纪录状态" + settle.getStatus() + "不可结算");
			map.put("result", false);
			return map;
		}
		Integer bankId = settle.getBankcardId();
		if(bankId == null){
			bankId = getBankId(settle);
			if(bankId == null){
				map.put("msg", "没有绑定银行卡不能结算");
				map.put("result", false);
				return map;
			}
		}
		
		double money = param.getSettleMoney() + param.getExpandMoney();
		if(money != (settle.getTotalMoney()==null?Double.valueOf(0):settle.getTotalMoney().doubleValue())){
			map.put("msg", "金额不正确");
			map.put("result", false);
			return map;
		}
		
		settle.setSettleUserId(ReqUtil.instance.getUserId());
		settle.setStatus(SettleStatus.settled.getIndex());
		settle.setSettleDate(System.currentTimeMillis());
		settle.setBankcardId(bankId);
		int count  = settleMapperNew.updateByPrimaryKey(settle);
		long time = System.currentTimeMillis();
		
		if(count > 0){
			//查询本月的结算纪录
			SettleNew nowSettle = new SettleNew();
			nowSettle.setMonth(ym.format(time));
			nowSettle.setCreateDate(time);
			nowSettle.setYear(Integer.parseInt(yy.format(time)));
			nowSettle.setObjectType(settle.getObjectType());
			nowSettle.setDoctorId(settle.getDoctorId());
			nowSettle.setGroupId(settle.getGroupId());
			nowSettle.setStatus(SettleStatus.forbidden.getIndex());
			//打款纪录 --> 插入收入表
			Cash cash = addCashRecord(settle, param.getSettleMoney(),time);
			//扣减项纪录 --> 插入收入表 --> 更新或者插入本月结算纪录
			if(param.getExpandMoney() > 0){
				Expend expend = addExpendRecord(settle,nowSettle, param.getExpandMoney(),time);
			}
			
			if(param.getObjectType() == ObjectType.doctor.getIndex()){
				// 推送app消息  发送短信息  
				User user = getUser(settle.getDoctorId());
				String doc = null;
				if(user != null){
					String telephone = user.getTelephone();
					if (mobSmsSdk.isBDJL(telephone)) {
						doc = BaseConstants.BD_DOC_APP;	
					} else {
						doc = BaseConstants.XG_PLATFORM;
					}
					String[]params=new String[]{user.getName(),doc,settle.getMonth(),money/100 +""};
					final String content = baseDataService.toContent("0062", params);
					mobSmsSdk.send(telephone, content);
				}
				
				String str = StringUtils.isNotBlank(doc) ? doc : BaseConstants.XG_PLATFORM;
				String notice = "您于" + str  + settle.getMonth() + "的" + money/100
						+ "元账户收入，已成功转入到您的账户！一分耕耘，一分收获，感谢您的付出！";
				// 发通知
				List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
				ImgTextMsg imgTextMsg = new ImgTextMsg();
				imgTextMsg.setStyle(7);
				imgTextMsg.setTitle(UserChangeTypeEnum.AFTER_SETTLE_NOTICE.getAlias());
				imgTextMsg.setContent(notice);
				imgTextMsg.setTime(System.currentTimeMillis());
				mpt.add(imgTextMsg);
				sendMsgService.sendTextMsg(settle.getDoctorId()+"", SysGroupEnum.TODO_NOTIFY, mpt, null);
			}
			
			 
			map.put("msg", "结算成功");
			map.put("result", true);
			return map;
		}
		map.put("msg", "结算失败");
		map.put("result", false);
		return map;
	}
	
	private void listToGroup(List<BaseDetailVO> list,List<TotalIncomeDVO> resultlist){
		if(list == null){
			return ;
		}
		List<Integer> order = Arrays.asList(1);
		List<Integer> split = Arrays.asList(2,3,4);
		List<Integer> expend = Arrays.asList(11,12,13,15,16);
		Map<Integer,String> map = new HashMap<Integer,String>();
		
		map.put(1, "我的订单收入");
		map.put(2, "我的提成收入");
		map.put(3, "扣减项");
		
////		if(param.getType() == ObjectType.doctor.getIndex())
////		订单收入：门诊、图文、电话、健康关怀、会诊
////		提成收入：从下级医生的提成收入
////		扣减项：订单取消扣款、扣减金额（提现手续费、代缴税费）
////		 业务类型（收入类型（1=订单收入，2=医生提成收入，3=集团在医生中的提成收入，4=集团在会诊订单中的分成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费））
//		if(preType == vo.getLogType()){
//			child.getList().add(vo);
//		}else{
//			preType = vo.getLogType();
//			child = new TotalIncomeDVO();
//			child.setBussnessType(preType);
//			child.getList().add(vo);
//			resultlist.add(child);
//		}
//	}
		
		TotalIncomeDVO child = new TotalIncomeDVO();
		int nowType = 0;
		
		for(BaseDetailVO vo : list){
			if(order.contains(vo.getLogType())){
				nowType = 1;
			}else if(split.contains(vo.getLogType())){
				nowType = 2;
			}else if(expend.contains(vo.getLogType())){
				nowType = 3;
			}else {
				throw new ServiceException(vo.getLogType() + " 暂不支技");
			}
//			if(child == null){
//				child = new TotalIncomeDVO();
//				child.setBussnessType(nowType);
//				child.setBussnessName(map.get(nowType));
//				resultlist.add(child);
//			}
			
			if(child.getBussnessType() == nowType){
				child.getList().add(vo);
			}else{
				child = new TotalIncomeDVO();
				child.setBussnessName(map.get(nowType));
				child.setBussnessType(nowType);
				child.getList().add(vo);
				resultlist.add(child);
			}
		}
	}
	
	@Override
	public RefundOrder addRefundOrder(Order order,RechargeVO rechargevo,Integer userId){
		if(order == null){
			return null;
		}
		
		//添加退款单
		userId = userId == null ? ReqUtil.instance.getUserId() : userId;
		long time = System.currentTimeMillis();
		RefundOrder refundOrder = new RefundOrder();
		refundOrder.setOrderId(order.getId());
		refundOrder.setCreaterDate(time);
		refundOrder.setCreateUserId(ReqUtil.instance.getUserId());
		refundOrder.setMoney(Double.valueOf(order.getPrice()));
		refundOrder.setUserId(order.getUserId());
		//没有多种退款渠道，只有web端退款，统一为“申请退款”
		refundOrder.setType(OrderRefundType.applyRefund.getIndex());
		//默认为退款成功，取消已支付的订单，不管有没有退款（钱原路返回），收入部分默认已退款
		refundOrder.setStatus(OrderRefundStatus.refundSuccess.getIndex());
		refundOrder.setOperator(ReqUtil.instance.getUserId());
		refundOrder.setThrid_refund_id(rechargevo.getAlipayNo()+"");
		refundOrderMapper.insertSelective(refundOrder);
		
		//添加退款类型收入流水
		addIncomeLogByRefundOrder(order,refundOrder,time);
		return refundOrder;
	}
	
	private void addIncomeLogByRefundOrder(Order order , RefundOrder refundOrder,Long time){
		
		//根据订单去查找incomeLog表里查找对应的收益对象
		IncomelogExample example = new IncomelogExample();
		Integer[] logArray = {1,2,3,4};//只查正收入
		example.createCriteria().andOrderIdEqualTo(order.getId()).andLogTypeIn(Arrays.asList(logArray));
		List<Incomelog> list = incomelogMapper.selectByExample(example);
		if(list != null){
			for(Incomelog log : list){
				
				Incomelog refundOrderRecord = new Incomelog();
				refundOrderRecord.setOrderId(order.getId());
				refundOrderRecord.setType(log.getType());
				refundOrderRecord.setMoney(-log.getMoney());
				refundOrderRecord.setRefundOrderId(refundOrder.getId());
				
				if(log.getType() == ObjectType.doctor.getIndex()){
					refundOrderRecord.setDoctorId(log.getDoctorId());
				}else if(log.getType() == ObjectType.group.getIndex()){
					refundOrderRecord.setGroupId(log.getGroupId());
				}
				
				if(log.getLogType().intValue() == LogType.order.getIndex()){//订单直接收益
					refundOrderRecord.setLogType(LogType.orderRefund.getIndex());
				}else if(log.getLogType().intValue() == LogType.doctorCommission.getIndex()){//订单提成收入
					refundOrderRecord.setLogType(LogType.doctorCommiessionRefund.getIndex());
					refundOrderRecord.setChildDoctorId(log.getChildDoctorId());
				}else if(log.getLogType().intValue() == LogType.groupOrderCommission.getIndex()){//集团分成收入
					refundOrderRecord.setLogType(LogType.groupOrderCommissionRefund.getIndex());
				}else if(log.getLogType().intValue() == LogType.groupCommission.getIndex()){//集团提成收入
					refundOrderRecord.setLogType(LogType.groupCommiessionRefund.getIndex());
					refundOrderRecord.setChildDoctorId(log.getChildDoctorId());
				}
				
				refundOrderRecord.setMonth(ym.format(time));
				refundOrderRecord.setYear(Integer.parseInt(yy.format(time)));
				refundOrderRecord.setCreateDate(time);
				
				incomelogMapper.insertSelective(refundOrderRecord);
				
				//结算
				SettleNew settleParam = new SettleNew();
				settleParam.setCreateDate(time);
				settleParam.setMonth(ym.format(time));
				settleParam.setYear(Integer.parseInt(yy.format(time)));
				settleParam.setMonthMoney(-log.getMoney());
				settleParam.setTotalMoney(-log.getMoney());
				settleParam.setObjectType(log.getType());
				settleParam.setStatus(SettleStatus.forbidden.getIndex());
				if(log.getType() == ObjectType.doctor.getIndex()){
					settleParam.setDoctorId(log.getDoctorId());
				}else if(log.getType() == ObjectType.group.getIndex()){
					settleParam.setGroupId(log.getGroupId());
				}
				saveOrUpadateSettle(settleParam);
			}
		}
	}
	
	public List<Incomelog> getIncomeLog(Integer orderId) {
		IncomelogExample example = new IncomelogExample();
		Integer[] logArray = {11,12,13};//退款
		example.createCriteria().andOrderIdEqualTo(orderId).andLogTypeIn(Arrays.asList(logArray));
		List<Incomelog> list = incomelogMapper.selectByExample(example);
		return list;
	}
	
	/**
	 * 向支出表插入纪录
	 * 向收入流水表新增支出类型流水纪录
	 * 更新或插入本月结算纪录
	 * @param settle
	 * @param expendMoney
	 * @param time
	 * @return
	 */
	private Expend addExpendRecord(SettleNew settle,SettleNew nowSettle,Double expendMoney,Long time){
		expendMoney = expendMoney==null?Double.valueOf(0):expendMoney;
		Expend expend = new Expend();
		expend.setCreateDate(time);
		expend.setMoney(expendMoney);
		expend.setCreateUserId(ReqUtil.instance.getUserId());
		expend.setObjectType(settle.getObjectType());
		expend.setDoctorId(settle.getDoctorId());
		expend.setGroupId(settle.getGroupId());
		expend.setExpendType(ExpendType.carryFee.getIndex());
		expend.setStatus(ExpendStatus.finished.getIndex());
		int count = expendMapper.insertSelective(expend);
		
		if(count > 0){
			//插入incomeLog 提现纪录
			Incomelog cashLog = new Incomelog();
			cashLog.setExpendId(expend.getId());
			cashLog.setLogType(LogType.carryFee.getIndex());
			cashLog.setDoctorId(settle.getDoctorId());
			cashLog.setGroupId(settle.getGroupId());
			cashLog.setType(settle.getObjectType());
			cashLog.setMoney(-expend.getMoney());
			cashLog.setCreateDate(time);
			cashLog.setMonth(ym.format(time));
			cashLog.setYear(Integer.parseInt(ym.format(time)));
			int logNum = incomelogMapper.insertSelective(cashLog);
			if(logNum > 0){
				nowSettle.setMonthMoney(-expend.getMoney());
				nowSettle.setTotalMoney(-expend.getMoney());
				saveOrUpadateSettle(nowSettle);
			}
		}
		
		return expend;
	}
	
	/**
	 *   向打款纪录表，新增打款纪录
	 *   向收入流水表，新增打款类型纪录
	 * @param settle
	 * @param cashMoney
	 * @param time
	 * @return
	 */
	private Cash addCashRecord(SettleNew settle,Double cashMoney,Long time){
		cashMoney = cashMoney==null? 0:cashMoney;
		Cash cash = new Cash();
		cash.setCreateDate(System.currentTimeMillis());
		cash.setCreateUserId(ReqUtil.instance.getUserId());
		cash.setObjectType(settle.getObjectType().intValue());
		cash.setMoney(cashMoney);
		cash.setSettleId(settle.getId());
//		if(settle.getObjectType().intValue() == ObjectType.doctor.getIndex()){
			cash.setDoctorId(settle.getDoctorId());
//		}else if(settle.getObjectType().intValue() == ObjectType.group.getIndex()){
			cash.setGroupId(settle.getGroupId());
//		}else{
//			throw new ServiceException("暂不支持类型");
//		}
		int cashNum  = cashMapper.insertSelective(cash);
		if(cashNum > 0){
			//插入incomeLog 提现纪录
			Incomelog cashLog = new Incomelog();
			cashLog.setCashId(cash.getId());
			cashLog.setLogType(LogType.carryCash.getIndex());
			cashLog.setDoctorId(settle.getDoctorId());
			cashLog.setGroupId(settle.getGroupId());
			cashLog.setType(settle.getObjectType());
			cashLog.setMoney(-cash.getMoney());
			cashLog.setCreateDate(time);
			cashLog.setMonth(ym.format(time));
			cashLog.setYear(Integer.parseInt(yy.format(time)));
			int logNum = incomelogMapper.insertSelective(cashLog);
		}
		
		return cash;
	}
	
	private Integer getBankId(SettleNew settle){
		Integer bankId = null;
		if(settle == null ){
			return bankId;
		}
		BankCardParam param = new BankCardParam();
		if(settle.getObjectType().intValue() == ObjectType.doctor.getIndex()){
			param.setUserId(settle.getDoctorId());
			BankCardVO vo = bankCardService.getDocDefaultCard(param);
			if(vo != null){
				bankId = vo.getId() ;
			}
		}else if(settle.getObjectType().intValue() == ObjectType.group.getIndex()){
			param.setGroupId(settle.getGroupId());
			BankCardVO vo = bankCardService.getGroupDefaultCard(param);
			if(vo != null){
				bankId = vo.getId() ;
			}
		}
		return bankId;
	}
	
	public static String getMonthSumNum(long time,int num) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		calendar.add(Calendar.MONTH, num);
		return ym.format(calendar.getTime());
	}
	
	/**
	 * 先判断是否存在当前月份，指定医生或者集团  结算数据：
	 * 有则更新待结算金额和当月实际发生金额，状态为不可结算,否则就插入数据
	 * @param settleParam
	 * @return
	 */
	private SettleNew  saveOrUpadateSettle(SettleNew settleParam){
		boolean doctor = false;
		boolean update = false;
		SettleNew settle = null;
		SettleExample settleExample = new SettleExample();
		if(settleParam.getObjectType() == ObjectType.doctor.getIndex()){
			doctor = true;
			settleExample.createCriteria()
			.andObjectTypeEqualTo(settleParam.getObjectType())
			.andMonthEqualTo(settleParam.getMonth()).
			andDoctorIdEqualTo(settleParam.getDoctorId());
		}else if(settleParam.getObjectType() == ObjectType.group.getIndex()){
			settleExample.createCriteria()
			.andObjectTypeEqualTo(settleParam.getObjectType())
			.andMonthEqualTo(settleParam.getMonth())
			.andGroupIdEqualTo(settleParam.getGroupId());
		}else{
			throw new ServiceException("暂不是支持该对象类型");
		}
		List<SettleNew> settleList = settleMapperNew.selectByExample(settleExample);
		if(settleList!=null && settleList.size() > 0){
			settle = settleList.get(0);
			update = true;//更新
		}else{
			update = false;//插入
		}
		
		int count = 0;
		if(update && settle != null){
			//更新
			double monthMoney = settle.getMonthMoney()== null? 0:settle.getMonthMoney();
			double totalMoney = settle.getTotalMoney()== null? 0:settle.getTotalMoney();
			monthMoney = monthMoney + (settleParam.getMonthMoney()==null?0:settleParam.getMonthMoney());
			totalMoney = totalMoney + (settleParam.getTotalMoney()==null?0:settleParam.getTotalMoney());
			settle.setMonthMoney(monthMoney);
			settle.setTotalMoney(totalMoney);
			settle.setStatus(SettleStatus.forbidden.getIndex());
			count = settleMapperNew.updateByPrimaryKeySelective(settle);
		}else{
			//插入
			if(settleParam.getMonthMoney() > 0){
				settle =  new SettleNew();
				settle.setCreateDate(settleParam.getCreateDate());
				settle.setYear(settleParam.getYear());
				settle.setMonth(settleParam.getMonth());
				settle.setStatus(settleParam.getStatus());
				settle.setObjectType(settleParam.getObjectType());
				settle.setMonthMoney(settleParam.getMonthMoney());
				settle.setTotalMoney(settleParam.getTotalMoney());
				if(doctor){
					settle.setDoctorId(settleParam.getDoctorId());
				}else{
					settle.setGroupId(settleParam.getGroupId());
				}
				count = settleMapperNew.insertSelective(settle);
			}
		}
		
		return settle;
	}
	
	private BigDecimal calculationInCome(Double price,Integer splitRatio){
		return new BigDecimal(price).multiply(new BigDecimal(splitRatio)).divide(new BigDecimal(100), 5, BigDecimal.ROUND_HALF_UP);
	}
	
	private Map<String,Integer> getUpProfit(Order order,GroupProfit profit){
		Map<String,Integer> map = new HashMap<String,Integer>();
		int divisionDoctorProp = 0;
		int divisionGroupProp = 0;
		if(OrderType.outPatient.getIndex() == order.getOrderType()){
			//先判断是否是门诊  (用orderType=3判断) 
			divisionDoctorProp = profit.getConfig().getClinicParentProfit()!=null?profit.getConfig().getClinicParentProfit():0;
			divisionGroupProp = profit.getConfig().getClinicGroupProfit()!=null?profit.getConfig().getClinicGroupProfit():0;
		}else if(OrderType.consultation.getIndex() == order.getOrderType()){
			//会诊订单 
			divisionDoctorProp = profit.getConfig().getConsultationParentProfit()!=null?profit.getConfig().getConsultationParentProfit():0;
			divisionGroupProp = profit.getConfig().getConsultationGroupProfit()!=null?profit.getConfig().getConsultationGroupProfit():0;
		}else if(OrderType.feeBill.getIndex() == order.getOrderType()){
			//收费单
			divisionDoctorProp = profit.getConfig().getChargeItemParentProfit()!=null?profit.getConfig().getChargeItemParentProfit():0;
			divisionGroupProp = profit.getConfig().getChargeItemGroupProfit()!=null?profit.getConfig().getChargeItemGroupProfit():0;
		}else if(OrderType.appointment.getIndex() == order.getOrderType()){
			//收费单
			divisionDoctorProp = profit.getConfig().getAppointmentParentProfit()!=null?profit.getConfig().getAppointmentParentProfit():0;
			divisionGroupProp = profit.getConfig().getAppointmentGroupProfit()!=null?profit.getConfig().getAppointmentGroupProfit():0;
		}
		
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
			if(StringUtil.isEmpty(order.getGroupId()) || order.getGroupId().equals(GroupUtil.PLATFORM_ID)){
//				BUG BDJL-392 属于平台上级医生和集团没有提成
				divisionDoctorProp = 0;
				divisionGroupProp = 0;
			}else{
//				BUG XGSF-4704 属于集团上级医生和集团有提成
				divisionDoctorProp = profit.getConfig().getCarePlanParentProfit()!=null?profit.getConfig().getCarePlanParentProfit():0;
				divisionGroupProp = profit.getConfig().getCarePlanGroupProfit()!=null?profit.getConfig().getCarePlanGroupProfit():0;
			}
			
		}
//		else if(PackType.appointment.getIndex() ==  order.getPackType()){
//			//关怀计划套餐 add by wangl
//			divisionDoctorProp = profit.getConfig().getAppointmentParentProfit()!=null?profit.getConfig().getAppointmentParentProfit():0;
//			divisionGroupProp = profit.getConfig().getAppointmentGroupProfit()!=null?profit.getConfig().getAppointmentGroupProfit():0;
//		}
		map.put("doctor", divisionDoctorProp);
		map.put("group", divisionGroupProp);
		return map;
	}
	
	private GroupProfit getGroupProfit(String orderGroupId,int doctorId){
		return groupProfitService.getGroupProfitById(doctorId,orderGroupId );
	}
	
	private String getDoctorGroupID(OrderDoctor od,String orderGroupId){
		String divisionGroupId = "";
		List<GroupDoctor>  list =gdocService.findGroupDoctor(od.getDoctorId(), orderGroupId, GroupDoctorStatus.正在使用.getIndex());
		if(list != null && list.size()>0 ){
			//在订单集团中，则以订单集团来计算抽成
			divisionGroupId = orderGroupId;
		}else{
			//不在订单集团中 则取主集团，没有主集团则取最早加入的一个集团
			List<String> groupIdList = gdocService.getGroupListByDoctorId(od.getDoctorId());
			if(groupIdList != null && groupIdList.size() >0){
				divisionGroupId = groupIdList.get(0);
			}
		}
		return divisionGroupId;
	}
	
	public void setOrderName(BaseDetailVO temp){
		if(temp == null){
			return;
		}
		temp.setDay(DateUtil.getDayOfLongTime(temp.getCreateDate())+"");
		if(temp.getOrderType() == OrderEnum.OrderType.order.getIndex()){
			if(temp.getPackType() == PackEnum.PackType.checkin.getIndex()){
				temp.setTypeName(PackEnum.PackType.checkin.getTitle());
			}else if(temp.getPackType() == PackEnum.PackType.phone.getIndex()){
				temp.setTypeName(PackEnum.PackType.phone.getTitle());
			}else if(temp.getPackType() == PackEnum.PackType.message.getIndex()){
				temp.setTypeName(PackEnum.PackType.message.getTitle());
			}
		}else {
			temp.setTypeName(OrderEnum.getOrderType(temp.getOrderType()));
		}
	}
	
	private void setTypeName(BaseDetailVO temp){
		if(temp == null){
			return;
		}
		temp.setDay(DateUtil.getDayOfLongTime(temp.getCreateDate())+"");
		if(temp.getLogType() == IncomeEnumNew.LogType.carryCash.getIndex()){
			temp.setTypeName(IncomeEnumNew.LogType.carryCash.getTitle());
		}else if(temp.getLogType() == IncomeEnumNew.LogType.carryFee.getIndex()){
			temp.setTypeName(IncomeEnumNew.LogType.carryFee.getTitle());
		}else if(temp.getLogType() == IncomeEnumNew.LogType.orderRefund.getIndex()){
			setOrderName(temp);
			temp.setTypeName(temp.getTypeName()+IncomeEnumNew.LogType.orderRefund.getTitle());
		}else if(temp.getLogType() == IncomeEnumNew.LogType.doctorCommiessionRefund.getIndex()
				|| temp.getLogType() == IncomeEnumNew.LogType.groupCommiessionRefund.getIndex()){
			setOrderName(temp);
			temp.setTypeName(temp.getTypeName()+"提成退款");
		}else if(temp.getLogType() == IncomeEnumNew.LogType.sysCommiession.getIndex()){
			temp.setTypeName(IncomeEnumNew.LogType.sysCommiession.getTitle());
		}else if(temp.getLogType() == IncomeEnumNew.LogType.order.getIndex()
				|| temp.getLogType() == IncomeEnumNew.LogType.doctorCommission.getIndex()
				|| temp.getLogType() == IncomeEnumNew.LogType.groupCommission.getIndex()){
			//提成类型订单需要获取对应下级医生名字
			if(temp.getChildId() != 0){
//				User user = userManager.getUser(temp.getChildId());
				User user = getUser(temp.getChildId());
				if(user != null){
					temp.setChildName(user.getName());
				}
			}
//			if(temp.getOrderType() == OrderEnum.OrderType.care.getIndex()){
//				temp.setTypeName(OrderEnum.OrderType.care.getTitle());
//			}else if(temp.getOrderType() == OrderEnum.OrderType.checkIn.getIndex()){
//				temp.setTypeName(OrderEnum.OrderType.checkIn.getTitle());
//			}else if(temp.getOrderType() == OrderEnum.OrderType.followUp.getIndex()){
//				temp.setTypeName(OrderEnum.OrderType.followUp.getTitle());
//			}else if(temp.getOrderType() == OrderEnum.OrderType.order.getIndex()){
//				if(temp.getPackType() == PackEnum.PackType.checkin.getIndex()){
//					temp.setTypeName(PackEnum.PackType.checkin.getTitle());
//				}else if(temp.getPackType() == PackEnum.PackType.message.getIndex()){
//					temp.setTypeName(PackEnum.PackType.message.getTitle());
//				}else if(temp.getPackType() == PackEnum.PackType.phone.getIndex()){
//					temp.setTypeName(PackEnum.PackType.phone.getTitle());
//				}else {
//					temp.setTypeName(OrderEnum.OrderType.order.getTitle());
//				}
//			}else if(temp.getOrderType() == OrderEnum.OrderType.outPatient.getIndex()){
//				temp.setTypeName(OrderEnum.OrderType.outPatient.getTitle());
//			}else if(temp.getOrderType() == OrderEnum.OrderType.throughTrain.getIndex()){
//				temp.setTypeName(OrderEnum.OrderType.throughTrain.getTitle());
//			}else if(temp.getOrderType() == OrderEnum.OrderType.consultation.getIndex()){
//				temp.setTypeName(OrderEnum.OrderType.consultation.getTitle());
//			}
			setOrderName(temp);
		}else if(temp.getLogType() == IncomeEnumNew.LogType.groupOrderCommission.getIndex()){
			temp.setTypeName(IncomeEnumNew.LogType.groupOrderCommission.getTitle());
		}else{
			throw new ServiceException( temp.getOrderType() + "暂不支持该业务类型");
		}
	}
	
	
	@Override
	public void autorConvertToNext(Long time) {

//		1、每月1号0点自动运行
//		2、新增当前月的“结算”记录，状态为不允许结算
//		3、将上个月的“结算”记录状态改为未结算，将上上个月的待结算金额和本月的发生金额累加作为本月的待结算金额
//		4、如果上上个月的结算记录未结算，状态改为已过期
		if(time  == null){
			time = System.currentTimeMillis();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd日");
		if(!sdf.format(time).equals("01日")){
			//TODO 暂时注释掉
//			return;
		}
		String month = ym.format(time);//
		
		String lastAndLastMonth = getMonthSumNum(time, -2);//上上个月，如存在则更新状态为过期
		
		/**
		 * key   医生或者集团ID
		 * value 医生或者集团 的对应待结算金额 
		 */
		Map<String,SettleNew> map = new HashMap<String,SettleNew>();
		
		//获取“上上月”所有“未结算”的列表,把 "医生/集团"  作为Key,
		SettleExample example = new SettleExample();
		example.createCriteria().andMonthEqualTo(lastAndLastMonth).andStatusEqualTo(SettleStatus.unsettle.getIndex());
		List<SettleNew> lastAndLastList = settleMapperNew.selectByExample(example);
		if(lastAndLastList != null){
			for(SettleNew vo :lastAndLastList){
				if(vo.getObjectType() == ObjectType.doctor.getIndex()){
					map.put(vo.getDoctorId() + "", vo);
				}else{
					map.put(vo.getGroupId(), vo);
				}
				//更新状态
				SettleNew record = new SettleNew();
				record.setStatus(SettleStatus.expired.getIndex());
				record.setId(vo.getId());
				settleMapperNew.updateByPrimaryKeySelective(record);
			}
		}
		
		String lastMonth = getMonthSumNum(time,-1);//上个月 更新状态为可结算，待结算金额为当月待结算金额加上上个月待结算金额,或者新增
		example = new SettleExample();
		example.createCriteria().andMonthEqualTo(lastMonth).andStatusLessThan(SettleStatus.settled.getIndex());
		List<SettleNew> lastList = settleMapperNew.selectByExample(example);
		if(lastList != null){
			for(SettleNew vo :lastList){
				String key = "";
				if(vo.getObjectType() == ObjectType.doctor.getIndex()){
					key = vo.getDoctorId() + "";
				}else{
					key = vo.getGroupId();
				}
				
				SettleNew record = new SettleNew();
				
				SettleNew last = map.get(key);
				if(last != null){
					//更新金额
					record.setTotalMoney(addDouble(vo.getTotalMoney(), last.getTotalMoney()));
					map.remove(key);
				}else{
					record.setTotalMoney(vo.getTotalMoney());
				}
				
				if(record.getTotalMoney() < money()){
					record.setStatus(SettleStatus.forbidden.getIndex());//更新状态
				}else{
					record.setStatus(SettleStatus.unsettle.getIndex());//更新状态
				}
				
				record.setId(vo.getId());
				settleMapperNew.updateByPrimaryKeySelective(record);
			}
		}
		
		Iterator<Entry<String,SettleNew>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String,SettleNew> entry = iterator.next();
			SettleNew entrySettle = entry.getValue();
			SettleNew  settle = new SettleNew();
			
			if(entrySettle.getTotalMoney() < money()){
				settle.setStatus(SettleStatus.forbidden.getIndex());
			}else{
				settle.setStatus(SettleStatus.unsettle.getIndex());
			}
			settle.setTotalMoney(entrySettle.getTotalMoney());
			settle.setDoctorId(entrySettle.getDoctorId());
			settle.setGroupId(entrySettle.getGroupId());
			settle.setObjectType(entrySettle.getObjectType());
			settle.setCreateDate(time);
			settle.setMonth(month);
			settle.setYear(Integer.parseInt(yy.format(time)));
			settleMapperNew.insertSelective(settle);
		}
	
	}

	@Override
	public void autoSettleNew(){
		autorConvertToNext(null);
	}
	private double addDouble(Double a,Double b){
		if(a == null ){
			a = Double.valueOf(0);
		}
		if(b == null){
			b = Double.valueOf(0);
		}
		return new BigDecimal(a).add(new BigDecimal(b)).doubleValue();
	}
	
	/**
	 * 避免userManager抛出的异常导至程序中止
	 */
	private User getUser(Integer id){
		User user = null;
		if(id == null){
			return null;
		}
		try{
			user = userManager.getUser(id);
		}catch(Exception e){
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public List<Incomelog> getIncomer(Integer orderId) {
		if(orderId == null){
			return new ArrayList<Incomelog>();
		}
		IncomelogExample example = new IncomelogExample();
		Integer[] logArray = {1,2,3,4};//正收入
		example.createCriteria().andOrderIdEqualTo(orderId).andLogTypeIn(Arrays.asList(logArray));
		return incomelogMapper.selectByExample(example);
	}

	@Override
	public List<Incomelog> getLogListByDoc(Integer doctorId) {
		if(doctorId == null){
			return new ArrayList<Incomelog>();
		}
		IncomelogExample example = new IncomelogExample();
		Integer[] logArray = {1,2,3,4};//正收入
		example.createCriteria().andDoctorIdEqualTo(doctorId).andLogTypeIn(Arrays.asList(logArray));
		return incomelogMapper.selectByExample(example);
	}
}
