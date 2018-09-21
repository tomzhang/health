package com.dachen.health.pack.order.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.group.schedule.service.IOfflineService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.pack.conference.entity.param.CallRecordParam;
import com.dachen.health.pack.conference.entity.vo.ConfListVO;
import com.dachen.health.pack.conference.service.ICallRecordService;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.vo.OrderDetailVO;
import com.dachen.health.pack.order.mapper.OrderDoctorMapper;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.health.pack.order.service.IOrderExpandService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.mapper.OrderStatusLogMapper;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.pack.schedule.entity.po.Schedule;
import com.dachen.health.pack.schedule.entity.vo.DocScheduleVO;
import com.dachen.health.pack.schedule.entity.vo.ScheduleParam;
import com.dachen.health.pack.schedule.entity.vo.ScheduleRecordVO;
import com.dachen.health.pack.schedule.entity.vo.ScheduleVO;
import com.dachen.health.pack.schedule.service.IScheduleService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.DateUtil;
import com.dachen.util.ReqUtil;
import com.mobsms.sdk.MobSmsSdk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderExpandServiceImpl implements IOrderExpandService {
    
	@Autowired
	IOrderService orderService;
	
    @Autowired
	IImageDataService imageDataService;
    
    @Autowired
    IMsgService imsgService;
    
    @Autowired
    IBusinessServiceMsg businessServiceMsg;
    
    @Autowired
    IPatientService patientService;
    
    @Resource
    MobSmsSdk mobSmsSdk;
    
    @Resource
    IOrderSessionService orderSessionSevice;
    
    @Resource
    OrderStatusLogMapper orderStatusMapper;
    
//    @Resource
//    PackDoctorMapper packDoctorMapper;
    
    @Resource
    OrderDoctorMapper orderDoctorMapper;

    @Autowired
    private IOfflineService offlineService;
    
    @Autowired
	private IScheduleService scheduleService;
       
    @Autowired
    private ICallRecordService callRecordService;
	
	/**
	 * 医生订单日程查询
	 */
	public DocScheduleVO getDocSchedule(ScheduleParam param) throws HttpApiException {
		
		verify(param);
		
		param = coventDateMillis(param);
		
		DocScheduleVO docSc = new DocScheduleVO();

		docSc.setOrderScheduleVo(scheduleService.getScheduleVOs(param));
		
		//MongoDB查询医生值班----modify by xieping 注释线下门诊
		/*Map<String,Object> offline = offlineService.getAll(param.getUserId(),param.getWeek());
		
		if(offline!=null) {
			@SuppressWarnings("unchecked")
			List<OfflineVO> listOfflineVOs =(List<OfflineVO>) offline.get("offline");
			List<ScheduleVO> onDutyScheduleVos = new ArrayList<ScheduleVO>();
			for(OfflineVO offlineVO :listOfflineVOs) {
				ScheduleVO scheduleVo = new ScheduleVO();
				scheduleVo.setHospital(offlineVO.getHospital());
				scheduleVo.setScheduleTime(ScheduleEnum.Period.getTitle(offlineVO.getPeriod()));
				scheduleVo.setClinicType(ScheduleEnum.ClinicType.getTitle(offlineVO.getClinicType()));
				scheduleVo.setOfflineName("线下门诊");
				onDutyScheduleVos.add(scheduleVo);
			}
			
			docSc.setOnDutyScheduleVo(onDutyScheduleVos);
		}*/
		return docSc;
	}
	
	/**
	 * 医生订单日程查询
	 */
	public List<ScheduleVO> scheduleDetail(ScheduleParam param) throws HttpApiException {
		
		verify(param);
		
		param = coventDateMillis(param);
		
		List<ScheduleVO> vos = scheduleService.getScheduleVOs(param);
		
		Iterator<ScheduleVO> iter = vos.iterator();
		while (iter.hasNext()) {
			Order order = orderService.getOne(iter.next().getOrderId());
			if(null==order) continue;
			if(order.getOrderStatus()==OrderEnum.OrderStatus.已完成.getIndex()){
				iter.remove();
			}
		}
		for (ScheduleVO vo : vos) {
			Integer orderId = vo.getOrderId();
			if (orderId == null)
				continue;
			OrderParam orderParam = new OrderParam();
			orderParam.setOrderId(orderId);
			OrderDetailVO orderDetail = orderService.detail(orderParam,ReqUtil.instance.getUser());
			vo.setOrderDetail(orderDetail);
			CallRecordParam callParam = new CallRecordParam();
			callParam.setOrderId(orderId);
			List<ConfListVO> confList = callRecordService.getRecordStatus(callParam);
			vo.setCallRecordList(confList);
		}
		return vos;
		
	}
	
	
	/**
	 * 患者、导医日程查询
	 */
	public PageVO getPatSchedule(ScheduleParam param) throws HttpApiException {
		
		verify(param);
		
		param.setStartDate(DateUtil.getDayBegin(param.getStartDate()));
		
		PageVO page = scheduleService.getScheduleVOsPage(param, false);
		
		return page;
	}
	
	
	/**
	 * 数据校验
	 * @param param
	 */
	private void verify(ScheduleParam param) {
		if (param.getUserId() == null) {
			throw new ServiceException("用户Id不能为空！");
		}
		if (param.getUserType() == null) {
			throw new ServiceException("用户类型不能为空！");
		}
	}
	
	
	private ScheduleParam coventDateMillis(ScheduleParam param) {

		Calendar c = Calendar.getInstance();
		try {
			Date date = DateUtil.FORMAT_YYYY_MM_DD.parse(param.getSearchDate());
			c.setTime(date);
			param.setStartDate(c.getTimeInMillis());
			param.setEndDate(c.getTimeInMillis() + (24 * 60 * 1000 * 60));
			// DateUtil.getWeekDays(date);
			int week = 0;
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				week = 7;
			} else {
				week = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
			param.setWeek(week);
		} catch (ParseException e) {
			throw new ServiceException("请输入正确的日期格式！");
		}
		return param;
	}
	
	
	public PageVO getSchedules(ScheduleParam param) throws HttpApiException {
		param = coventDateMillis(param);
		//查询searchDate之后的所有日程记录，设置endDate为空
		param.setEndDate(null);
		return scheduleService.getScheduleVOsPage(param, true);
	}
	
	/**
	 * 医生日程记录列表
	 */
	public List<ScheduleRecordVO> getDocScheduleRecord(ScheduleParam param) {

		verify(param);

		List<ScheduleRecordVO> recordVos = buildScheduleRecord(param);

		if (param.getUserType() == UserType.patient.getIndex() || param.getUserType() == UserType.DocGuide.getIndex()) {
			return recordVos;
		}

		// MogoDb查询医生值班----modify by xieping 注释线下门诊
		/*Map<String, Object> offline = offlineService.getAll(param.getUserId(), 0);
		if (offline != null) {
			List<OfflineVO> listOfflineVOs = (List<OfflineVO>) offline.get("offline");
			for (OfflineVO offlineVO : listOfflineVOs) {
				for (ScheduleRecordVO vo : recordVos) {
					if (vo.getWeek() == offlineVO.getWeek()) {
						vo.setIsTrue(1);
					}
				}
			}
		}*/

		return recordVos;
	}
	

	/**
	 * 创建本月日程记录
	 * @param param
	 * @return
	 */
	private List<ScheduleRecordVO> buildScheduleRecord(ScheduleParam param) {
		List<ScheduleRecordVO> recordVos = new ArrayList<ScheduleRecordVO>();
		Calendar c = Calendar.getInstance();
		try {
			Date startDate = DateUtil.getFirstDayOfMonth(DateUtil.FORMAT_YYYY_MM_DD.parse(param.getSearchDate()));
			Date endDate = DateUtil.getLastDayOfMonth(DateUtil.FORMAT_YYYY_MM_DD.parse(param.getSearchDate()));
			buildMonthRecord(recordVos, c, startDate, endDate);
			//构造参数值
			c.setTime(startDate);
			param.setStartDate(c.getTimeInMillis());
			c.setTime(endDate);
			param.setEndDate((c.getTimeInMillis() + (24 * 60 * 1000 * 60)));
		} catch (ParseException e) {
			throw new ServiceException("请输入正确的日期格式！");
		}

		List<Schedule> slist = scheduleService.getSchedules(param);
		
		Iterator<Schedule> iter = slist.iterator();
		while (iter.hasNext()) {
			Order order = orderService.getOne(Integer.valueOf(iter.next().getRelationId()));
			if(null==order) continue;
			if(order.getOrderStatus()==OrderEnum.OrderStatus.已完成.getIndex()){
				iter.remove();
			}
		}
		for (Schedule s : slist) {
			Long appointTime = s.getScheduleTime();
			Date date = new Date(appointTime);
			c.setTime(date);

			Integer days = c.get(Calendar.DAY_OF_MONTH);
			for (ScheduleRecordVO recordvo : recordVos) {
				if (recordvo.getDayNum() == days && recordvo.getIsTrue() == 0) {
					recordvo.setIsTrue(1);
					break;
				}
			}
		}
		return recordVos;
	}
	
	/**
	 * 创建本月日程记录，默认为空
	 * @param recordVos
	 * @param c
	 * @param startDate
	 * @param endDate
	 */
	private void buildMonthRecord(List<ScheduleRecordVO> recordVos, Calendar c, Date startDate, Date endDate) {
		c.setTime(endDate);
		Integer dayss = c.get(Calendar.DAY_OF_MONTH);
		c.setTime(startDate);
		for (int i = 1; i <= dayss; i++) {
			ScheduleRecordVO recorde = new ScheduleRecordVO();
			recorde.setDayNum(i);
			recorde.setIsTrue(0);
			int week = 0;
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				week = 7;
			} else {
				week = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
			recorde.setWeek(week);
			recordVos.add(recorde);
			c.add(Calendar.DATE, 1);
		}
	}

	@Override
	public int getNoServiceCount(ScheduleParam param) throws HttpApiException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        long times = System.currentTimeMillis();  
        Date date = new Date(times);  
        String tim = sdf.format(date);  
        try {
			param.setStartDate(sdf.parse(tim).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//查询searchDate之后的所有日程记录，设置endDate为空
         
		param.setEndDate(null);
	    int count = scheduleService.getNoServiceCount(param, true);
		return count;
	}
}
