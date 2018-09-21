package com.dachen.health.pack.income.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.income.constant.IncomeEnum;
import com.dachen.health.pack.income.entity.param.IncomeDetailsParam;
import com.dachen.health.pack.income.entity.vo.IncomeDetailsVO;
import com.dachen.health.pack.income.entity.vo.IncomeVONew;
import com.dachen.health.pack.income.entity.vo.PageIncome;
import com.dachen.health.pack.income.entity.vo.PageIncomeDetails;
import com.dachen.health.pack.income.mapper.IncomeDetailsMapper;
import com.dachen.health.pack.income.mapper.IncomeMapperNew;
import com.dachen.health.pack.income.service.IIncomDetailsService;
import com.dachen.util.DateUtil;
import com.dachen.util.StringUtil;

@Service
public class IncomDetailsServiceImpl implements IIncomDetailsService {
	
	@Autowired
	private IncomeDetailsMapper mapper;
	
	@Autowired
	private UserManager userService;
	
	@Autowired
    private IncomeMapperNew incomeMapperNew;

	@Override
	public void insertIncomeDetails(IncomeDetailsParam param) {
		mapper.insert(param);
	}
	
	public List<IncomeDetailsVO> getListByDoctorID(IncomeDetailsParam param){
		if(param.getDoctorId() == null){
			return null;
		}
		return mapper.getListByDoctorID(param);
	}

	@Override
	public Map<String, Object> getDocIncomeIndex(IncomeDetailsParam param) {
		if(param.getDoctorId() == null){
			throw new ServiceException("医生ID不能为空");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		param.setType(OrderEnum.OrderStatus.已完成.getIndex());
		param.setStatus(IncomeEnum.SettleStatus.未结算.getIndex());
		List<IncomeDetailsVO> voList = mapper.getDocIncomeList(param);
		double finished = 0;
		double unFinished = 0;
		for(IncomeDetailsVO vo :voList){
			if(vo.getDoctorId().intValue() ==param.getDoctorId().intValue()){
				finished += vo.getShareMoney();
			}else if(vo.getUpDoc() !=null && vo.getUpDoc() != null && vo.getUpDoc().intValue() == param.getDoctorId().intValue()){
				double result = IncomeDetailsVO.getValue(vo.getMoney(), vo.getselfProportion(), '*').doubleValue();
				result = IncomeDetailsVO.getValue(result,vo.getDocProportion(),'*').doubleValue();
				result = IncomeDetailsVO.getValue(result,10000,'/').doubleValue();
				finished += result;
			}
		}
		voList = null;
		param.setType(OrderEnum.OrderStatus.已支付.getIndex());
		voList = mapper.getDocIncomeList(param);
		for(IncomeDetailsVO vo :voList){
			if(vo.getDoctorId().intValue() == param.getDoctorId().intValue()){
				unFinished += vo.getShareMoney();
			}else if(vo.getUpDoc() !=null && vo.getUpDoc() != null && vo.getUpDoc().intValue() == param.getDoctorId().intValue()){
				double result = IncomeDetailsVO.getValue(vo.getMoney(), vo.getselfProportion(), '*').doubleValue();
				result = IncomeDetailsVO.getValue(result,vo.getDocProportion(),'*').doubleValue();
				result = IncomeDetailsVO.getValue(result,10000,'/').doubleValue();
				unFinished += result;
			}
		}
		map.put("balance", finished);
//		unFinished=mapper.getUnFinishBalance(param.getDoctorId())!=null?mapper.getUnFinishBalance(param.getDoctorId()):0;
		map.put("unbalance", unFinished);
		return map;
	}

	@Override
	public PageVO getDocIncomeDetails(IncomeDetailsParam param) {
		if(param.getDoctorId() == null || param.getType() == null){
			throw new ServiceException("医生ID或类型有误");
		}
		PageVO pageVO = new PageVO();
		if(param.getType().intValue() == 2){
			param.setType(OrderEnum.OrderStatus.已完成.getIndex());
			param.setStatus(IncomeEnum.SettleStatus.未结算.getIndex());
		}else if(param.getType().intValue() == 1){
			param.setType(OrderEnum.OrderStatus.已支付.getIndex());
			param.setStatus(IncomeEnum.SettleStatus.未结算.getIndex());
		}
		param.setStart(param.getPageIndex() * param.getPageSize());
		List<IncomeDetailsVO> voList = mapper.getDocIncomeList(param);
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
			if( vo.getUpDoc() != null && vo.getUpDoc().intValue() == param.getDoctorId().intValue()){//提成
				User user = userService.getUser(vo.getDoctorId());
				if(user != null){
					pd.setDoctorName(user.getName());
				}
				double result = IncomeDetailsVO.getValue(vo.getMoney(), vo.getselfProportion(), '*').doubleValue();
				result = IncomeDetailsVO.getValue(result,vo.getDocProportion(),'*').doubleValue();
				result = IncomeDetailsVO.getValue(result,10000,'/').doubleValue();
				pd.setMoney(result);
				pd.setType(IncomeEnum.IncomeType.提成.getIndex());
			}else if(vo.getDoctorId().intValue() == param.getDoctorId().intValue()){//直接收益
				pd.setMoney(vo.getShareMoney());
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
		pageVO.setTotal(Long.valueOf(mapper.getDocIncomeCount(param)));
		pageVO.setPageIndex(param.getPageIndex());
		pageVO.setPageSize(param.getPageSize());
		return pageVO;
	}
	
	@Override
	public PageVO getDocHistoryIncome(IncomeDetailsParam param) {
		if(param.getDoctorId() == null ){
			throw new ServiceException("医生ID有误");
		}
		param.setStart(param.getPageIndex() * param.getPageSize());
//		List<IncomeDetailsVO> voList = mapper.getDocHisIncomeList(param);
		
		//基于分页的统计收入 当数据量超过1页时，统计结果必然只是当前页的累加，导致统计错误  add by wangqiao 2016-2-15
		IncomeVONew incomeVONew=new IncomeVONew();
		//为了统计结果的正确，暂时关闭分页，后续统计效率出现问题再改造 add by wangqiao 2016-2-15  FIXME
		incomeVONew.setPageSize(Integer.MAX_VALUE);
		incomeVONew.setStart(0);
//		incomeVONew.setPageSize(param.getPageSize());
//		incomeVONew.setStart(param.getPageIndex() * param.getPageSize());
		incomeVONew.setDoctorId(param.getDoctorId());
		incomeVONew.setOrderStatus(OrderStatus.已完成.getIndex());
		List<IncomeDetailsVO> voList = incomeMapperNew.getDoctorFinishDetail(incomeVONew);
		
		
		SimpleDateFormat sdfY = new SimpleDateFormat("yyyy年");
		SimpleDateFormat sdfM = new SimpleDateFormat("MM月");
		LinkedHashSet<PageIncome> yList = new LinkedHashSet<PageIncome>();//年列表
		for(IncomeDetailsVO vo : voList){
			if(vo.getOrderStatus() == OrderEnum.OrderStatus.已支付.getIndex()){
				continue;
			}
			String yKey = DateUtil.formatDate2Str(vo.getCreateTime(), sdfY);
			String mKey = DateUtil.formatDate2Str(vo.getCreateTime(), sdfM);
			PageIncome pyIncome = null;
			for(PageIncome temp : yList){
				if(temp.getKeyY().equals(yKey)){
					pyIncome = temp;//获取年对象
					break;
				}
			}
			if(pyIncome == null){
				pyIncome = new PageIncome();
			}
			pyIncome.setKeyY(yKey);
			
			LinkedHashSet<PageIncome> mList = pyIncome.getmList();//获取月列表
			if(mList == null){
				mList = new LinkedHashSet<PageIncome>();
			}
			PageIncome pmIncome = null;//获取月对象
			for(PageIncome temp : mList){
				if(temp.getKeyM().equals(mKey)){
					pmIncome = temp;
					break;
				}
			}
			if(pmIncome == null){
				pmIncome = new PageIncome();
			}
			
			pmIncome.setKeyM(mKey);
			pmIncome.setTotalNum(pmIncome.getTotalNum()+1);
			if( vo.getIncomeType().intValue()==IncomeEnum.IncomeType.提成.getIndex()){//提成
				pmIncome.setTotalMoney(pmIncome.getTotalMoney() + vo.getMoney());
			}else {//提成收益
				pmIncome.setTotalMoney(pmIncome.getTotalMoney() + vo.getMoney());
			}
			mList.add(pmIncome);//添加月列表中
			pyIncome.setmList(mList);//添加到年对象中
			yList.add(pyIncome);//添加到年列表中
		}
		PageVO pageVO = new PageVO();
		pageVO.setPageData(new ArrayList<PageIncome>(yList));
		pageVO.setPageIndex(param.getPageIndex());
		pageVO.setPageSize(param.getPageSize());
		return pageVO;
	}

	@Override
	public PageIncome getDocMonthImcomeDetail(IncomeDetailsParam param) {
		if(param.getDoctorId() == null || StringUtil.isEmpty(param.getTime())){
			throw new ServiceException("参数有误 ");
		}
		Long beginTime = null;;
		Long endTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Date date = null;
		try {
			date = sdf.parse(param.getTime());
			beginTime = date.getTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, 1);
			endTime =calendar.getTimeInMillis();
		} catch (Exception e) {
			throw new ServiceException("格式应为yyyy-MM");
		}
	
//		List<IncomeDetailsVO> list = mapper.getDocHisIncomeList(param);
		
		IncomeVONew incomeVONew=new IncomeVONew();
		incomeVONew.setPageSize(param.getPageSize());
		incomeVONew.setStart(param.getPageIndex() * param.getPageSize());
		incomeVONew.setDoctorId(param.getDoctorId());
		incomeVONew.setCreateTime(beginTime);
		incomeVONew.setFinishTime(endTime);
		incomeVONew.setOrderStatus(OrderStatus.已完成.getIndex());
		List<IncomeDetailsVO> list = incomeMapperNew.getDoctorFinishDetail(incomeVONew);
		
		//计算当月总金客额 ，月收入明细
		sdf = new SimpleDateFormat("yyyy年MM月");
		PageIncome result = new PageIncome();
		result.setKeyYM(sdf.format(date));
		List<PageIncomeDetails> share = new ArrayList<PageIncomeDetails>();//订单收益
		List<PageIncomeDetails> unShare = new ArrayList<PageIncomeDetails>();//提成收益
//		double totalMoney = 0;
		for(IncomeDetailsVO vo : list){
			PageIncomeDetails pd = new PageIncomeDetails();
			pd.setDay(String.valueOf(DateUtil.getDayOfLongTime(vo.getCreateTime())));
			pd.setOrderTypeName(IncomeDetailsVO.getTypeName(vo.getOrderType(), vo.getPackType()));
			pd.setOrderId(vo.getOrderId());
			if( vo.getIncomeType().intValue()==IncomeEnum.IncomeType.非提成.getIndex()){
				//订单收益
				pd.setMoney(vo.getMoney());
				pd.setType(IncomeEnum.IncomeType.非提成.getIndex());
				share.add(pd);
//				totalMoney += vo.getMoney();
			}else {
				//提成收益
				User user = userService.getUser(vo.getIncomeDoctorId());
				if(user != null){
					pd.setDoctorName(user.getName());
				}
				pd.setMoney(vo.getMoney());
				pd.setType(IncomeEnum.IncomeType.提成.getIndex());
				unShare.add(pd);
//				totalMoney += vo.getMoney();
			}
		}
		//计算医生当月的收入汇总
		double totalMoney = statDoctorTotalMoney(param.getDoctorId(),beginTime,endTime);
		result.setPageIndex(param.getPageIndex());
		result.setPageSize(param.getPageSize());
		result.setTotalMoney(totalMoney);
		result.setShareList(share);
		result.setUnshareList(unShare);
		return result;
	}
	
	/**
	 * 计算医生在某个时间段 已完成订单的收入汇总
	 * @param doctorId
	 * @param beginTime
	 * @param endTime
	 * @return
	 *@author wangqiao
	 *@date 2016年2月16日
	 */
	private double statDoctorTotalMoney(Integer doctorId,long beginTime ,long endTime){
		//参数校验
		if(doctorId == null || doctorId == 0){
			return 0;
		}
		
		double totalMoney = 0;
		
		//查询医生在某个时间段中的所有 收入明细信息
		IncomeVONew incomeVONew=new IncomeVONew();
		incomeVONew.setPageSize(Integer.MAX_VALUE);
		incomeVONew.setStart(0);
		incomeVONew.setDoctorId(doctorId);
		incomeVONew.setCreateTime(beginTime);
		incomeVONew.setFinishTime(endTime);
		incomeVONew.setOrderStatus(OrderStatus.已完成.getIndex());
		List<IncomeDetailsVO> list = incomeMapperNew.getDoctorFinishDetail(incomeVONew);
		//将医生收入累加起来
		for(IncomeDetailsVO vo : list){
			totalMoney += vo.getMoney();
		}
		
		return totalMoney;
	}
	
	@Override
	public PageVO getGroupIncomeList(IncomeDetailsParam param) {
		if(StringUtil.isEmpty(param.getUpGroup()) ){
			throw new ServiceException("集团ID不能为空");
		}
		param.setStart(param.getPageIndex()*param.getPageSize());
		List<PageIncome> list = mapper.getGroupIncomeByYM(param);
		
		PageVO pageVO = new PageVO();
		pageVO.setPageData(list);
		Integer count = mapper.getGroupIncomeByYMCount(param);
		pageVO.setTotal(Long.valueOf(count==null ? 0:count  ));
		pageVO.setPageIndex(param.getPageIndex());
		pageVO.setPageSize(param.getPageSize());
		return pageVO;
		
	}
	
//	@Override
	public PageVO getGroupDoctorIncomeList(IncomeDetailsParam param) {
		if(StringUtil.isEmpty(param.getUpGroup())){
			throw new ServiceException("集团ID不能为空");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		if(StringUtil.isEmpty(param.getTime())){
			param.setTime(sdf.format(System.currentTimeMillis()));
		}
		try {
			sdf.parse(param.getTime());
		} catch (Exception e) {
			throw new ServiceException("格式应为yyyy-MM");
		}
		List<IncomeDetailsVO> list = new ArrayList<IncomeDetailsVO>(); 
		Integer count = 0;
		param.setStart(param.getPageIndex()*param.getPageSize());
		if(param.getUserType() == IncomeEnum.SettleUserType.doctor.getIndex()){
			//集团某年某月对应医生对应的订单相关信息 ，按已完成订单金额倒序排序
			User user = null;
			if(!StringUtil.isEmpty(param.getTelephone())){
				user = userService.getUser(param.getTelephone().trim(), UserEnum.UserType.doctor.getIndex());
				if(user == null){
					throw new ServiceException("没有找到应的用户");
				}
				param.setDoctorId(user.getUserId());
			}else if(!StringUtil.isEmpty(param.getName())){
				List<User> userList = null;
//						userService.getUserByNameAndType(param.getName().trim(), UserEnum.UserType.doctor.getIndex());
				if(user == null){
					throw new ServiceException("没有找到应的用户");
				}
				param.setDoctorId(user.getUserId());
			}
				
			list = mapper.getGroupDoctorIncomeList(param);
			count = mapper.getGroupDoctorIncomeListCount(param);
		}else if(param.getUserType() == IncomeEnum.SettleUserType.group.getIndex()){
			//集团某年某月对应里对所有订单对应的相关信息，按时间倒取排列
			list = mapper.getGroupIncomeList(param);
			count = mapper.getGroupIncomeListCount(param);
		}
		User user = null;
		for(IncomeDetailsVO vo : list){
			user = userService.getUser(vo.getDoctorId());
			if(user != null){
				vo.setDoctorName(user.getName());
				vo.setTelephone(user.getTelephone());
			}
			if(param.getUserType() == IncomeEnum.SettleUserType.doctor.getIndex()){
				//获取对应的提成金额
				if(vo.getDoctorId() != null){
					IncomeDetailsParam upDocParam = new IncomeDetailsParam();
					upDocParam.setUpDoc(vo.getDoctorId());
					upDocParam.setTime(param.getTime());
					List<IncomeDetailsVO> upDocList = mapper.getUpDocIncome(upDocParam);
					if(upDocList.size() != 0){
						vo.setProportionMoney(upDocList.get(0).getProportionMoney());
					}
				}
			}else if(param.getUserType() == IncomeEnum.SettleUserType.group.getIndex()){
				vo.setOrderTypeName(IncomeDetailsVO.getTypeName(vo.getOrderType(), vo.getPackType()));
//				double money = IncomeDetailsVO.getValue(vo.getMoney(), vo.getselfProportion(), '*').doubleValue();
//				money = IncomeDetailsVO.getValue(money,vo.getGroupProportion(),'*').doubleValue();
//				money = IncomeDetailsVO.getValue(money,10000,'/').doubleValue();
//				vo.setGroupMoney(money);
//				pageList.add(vo);
			}
			
		}
		count = count == null? 0:count;
		PageVO pageVO = new PageVO();
		pageVO.setPageData(list);
		pageVO.setPageIndex(param.getPageIndex());
		pageVO.setTotal(Long.valueOf(count));
		pageVO.setPageSize(param.getPageSize());
		return pageVO;
	}
	
	private IncomeDetailsVO getVO(LinkedHashSet<IncomeDetailsVO> list, User user ){
		if(list == null || user == null){
			return null;
		}
		for(IncomeDetailsVO vo : list){
			 if(user.getUserId().equals(vo.getDoctorId())){
				 return vo;
			 }
		}
		return null;
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
