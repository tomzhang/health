package com.dachen.health.pack.schedule.service.impl;

import static com.dachen.util.DateUtil.FORMAT_YYYY_MM_DD;
import static com.dachen.util.DateUtil.FORMAT_YYYY_MM_DD_HH_MM;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.api.client.schedule.entity.CCareItemSchedule;
import com.dachen.health.api.client.schedule.entity.CCareItemScheduleUpdate;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.conference.service.ICallRecordService;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.service.GuideScheduleService;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.patient.mapper.OrderSessionMapper;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IDiseaseService;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.pack.schedule.dao.IScheduleDao;
import com.dachen.health.pack.schedule.entity.po.Schedule;
import com.dachen.health.pack.schedule.entity.po.Schedule.ScheduleType;
import com.dachen.health.pack.schedule.entity.vo.ScheduleGroupVO;
import com.dachen.health.pack.schedule.entity.vo.ScheduleParam;
import com.dachen.health.pack.schedule.entity.vo.ScheduleVO;
import com.dachen.health.pack.schedule.service.IScheduleService;
import com.dachen.util.DateUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;

/**
 * 日程服务接口实现
 * @author 谢平
 */
@Service
public class ScheduleServiceImpl implements IScheduleService {

	@Resource
	private IOrderSessionService orderSessionService;
	@Resource
	private IScheduleDao scheduleDao;
	
	@Resource
	private OrderMapper orderMapper;
	
	@Resource
	private OrderSessionMapper orderSessionMapper;
	
	@Resource
	private PackMapper packMapper;
	
	@Resource
	private UserRepository userDao;
	
	@Resource	
	private MobSmsSdk mobSmsSdk;
	
	@Resource
	private IGuideDAO guideDao;
	
	@Resource
	private IDiseaseService diseaseService;
	
	@Resource
	private ICallRecordService callRecord;
	
	@Resource
	private IBaseDataService baseDataService;
	
	@Autowired
	private GuideScheduleService guideScheduleService;
	
	@Autowired
	private IPatientService patientService;
	
	@Override
	public void deleteByCareItems(List<String> careItemIds, Long deadline) {
		if (CollectionUtils.isEmpty(careItemIds)) {
			return;
		}
		
		for (String careItemId:careItemIds) {
			Schedule schedule = scheduleDao.getByCareItemId(careItemId, deadline);
			if (schedule != null) {
				scheduleDao.delete(schedule.getId().toString());
			}
		}
	}
	
	
	
	@Override
	public void createOrderSchedule(Integer orderId, Long appointTime) {
		String relationId = orderId + "";
		Schedule schedule = scheduleDao.getByRelationId(relationId);
		Order order = orderMapper.getOne(Integer.valueOf(relationId));
		if (schedule != null) {
			//获取当前医生与患者
			User doctor = userDao.getUser(order.getDoctorId());
			Patient patient =patientService.findByPk(order.getPatientId()); 
			//获取之前预约时间和当前预约时间
			Long beforTime = schedule.getScheduleTime();
			SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH点mm分");
			
			Object[] params = new Object[]{doctor.getName(),patient.getUserName(),sdf.format(beforTime),sdf.format(appointTime)};
			final String content1 = baseDataService.toContent("0022", params);
			mobSmsSdk.send(doctor.getTelephone(), content1);
			
			params = new String[]{patient.getUserName(),doctor.getName(),sdf.format(beforTime),sdf.format(appointTime)};
			final String content2 = baseDataService.toContent("0023", params);
			//根据patient里的userId查询该患者是属于哪个平台
			User p_user = userDao.getUser(patient.getUserId());
			String signature = null;
			if(mobSmsSdk.isBDJL(p_user)){
				signature = BaseConstants.BD_SIGN;
			}else{
				signature = BaseConstants.XG_SIGN;
			}
			mobSmsSdk.send(patient.getTelephone(), content2,signature);
			
			scheduleDao.updateByRelationId(relationId, appointTime);
			return;
		}
		
		//医生日程
		createOrderSchedule(order, appointTime, order.getDoctorId());
		//患者日程
		createOrderSchedule(order, appointTime, order.getUserId());
	}
	
	
	public void createGuiderSchedule(Order order, Long appointTime) {
		//导医日程，只有电话咨询才添加导医日程
		if (order.getPackType() != PackType.phone.getIndex()) {
			return;
		}
		ConsultOrderPO consultOrder = guideDao.getObjectByOrderId(order.getId());
		createOrderSchedule(order, appointTime, consultOrder.getGuideId());
	}
	
	/**
	 * 创建订单类型日程
	 * @param order
	 * @param appointTime
	 * @param userId
	 * @return
	 */
	private Schedule createOrderSchedule(Order order, Long appointTime, Integer userId) {
		Schedule schedule = new Schedule();
		schedule.setType(ScheduleType.order.getIndex());
		schedule.setRelationId(order.getId().toString());
		schedule.setScheduleTime(appointTime);
		schedule.setUserId(userId);
		schedule.setTitle(StringUtil.returnPackTitle(order.getPackType(), order.getPrice()));
		schedule.setCreateTime(System.currentTimeMillis());
		return scheduleDao.save(schedule);
	}
	
	@Override
	public void scheduleRemind() {
		//订单日程提醒
		orderScheduleRemind();
		
		//导医日程提醒
		guideScheduleService.scheduleRemind();
	}
	
	@Override
	public void scheduleAppointment() {
		//预约名医日程提醒
		orderScheduleappointment();	
	}
	
	private void orderScheduleRemind() {
		long currTime = System.currentTimeMillis();
		List<Schedule> schedules = scheduleDao.getSendSchedule(currTime, currTime + 20 * 60 * 1000);
		
		for (Schedule schedule : schedules) {
			if (schedule.getUserId() == null)
				continue;
			User user = userDao.getUser(schedule.getUserId());
			if (user == null)
				continue;
			if (schedule.getType() == Schedule.ScheduleType.order.getIndex()) {
				String orderIdStr = schedule.getRelationId();
				try {
					int orderId = Integer.parseInt(orderIdStr);
					Order order = orderMapper.getOne(orderId);
					List<OrderSession> orderSessions = orderSessionService.findByOrderId(orderId);
					boolean isServiceBegin = false;
					if (orderSessions.size() > 0) {
						isServiceBegin = orderSessions.get(0).getServiceBeginTime() != null ? true : false;
					}
					// YHPT-7537 任务改进 判断支付过的订单才发此短信+且未开始服务的  
					//预约名医信息提醒方式改变
					if (order != null && order.getPackType()!=OrderEnum.OrderType.appointment.getIndex()&& order.getOrderStatus() == OrderEnum.OrderStatus.已支付.getIndex()
							&& !isServiceBegin) {
						// String message =
						// "{0}您好，距离预约的{1}已不足10分钟，请尽快登录玄关健康APP处理。";
						String content = baseDataService.toContent("0051", user.getName(), schedule.getTitle());
						mobSmsSdk.send(user.getTelephone(), content);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			schedule.setSendTime(currTime);
			scheduleDao.update(schedule);
		}
	}
	/**
	 * 预约名医短信提醒
	 */
	private void orderScheduleappointment() {
		long currTime = System.currentTimeMillis();
		List<Schedule> schedules = scheduleDao.getSendSchedule(currTime+2*60*60*1000-3*60*1000,currTime+2*60*60*1000+3*60*1000,"预约名医");
		
		for (Schedule schedule : schedules) {
			if (schedule.getUserId() == null)
				continue;
			User user = userDao.getUser(schedule.getUserId());
			if (user == null)
				continue;
			if (schedule.getType() == Schedule.ScheduleType.order.getIndex()) {
				String orderIdStr = schedule.getRelationId();
				try {
					int orderId = Integer.parseInt(orderIdStr);
					Order order = orderMapper.getOne(orderId);
					List<OrderSession> orderSessions = orderSessionService.findByOrderId(orderId);
					boolean isServiceBegin = false;
					if (orderSessions.size() > 0) {
						isServiceBegin = orderSessions.get(0).getServiceBeginTime() != null ? true : false;
					}
					// YHPT-7537 任务改进 判断支付过的订单才发此短信+且未开始服务的  
					//预约名医信息提醒方式改变
					if (order != null && order.getPackType()==OrderEnum.OrderType.appointment.getIndex()&& order.getOrderStatus() == OrderEnum.OrderStatus.已支付.getIndex()
							&& !isServiceBegin) {
						// String message =
						// "{0}您好，距离预约的{1}已不足10分钟，请尽快登录玄关健康APP处理。";
						String content = baseDataService.toContent("0051", user.getName(), schedule.getTitle());
						mobSmsSdk.send(user.getTelephone(), content);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			schedule.setSendTime(currTime);
			scheduleDao.update(schedule);
		}
	}
	
	public List<Schedule> getSchedules(ScheduleParam param) {
		verify(param);
		
		Query<Schedule> q = scheduleDao.getSchedules(param);
		
		return q.asList();
	}
	
	public List<ScheduleVO> getScheduleVOs(ScheduleParam param) throws HttpApiException {
		
		return convert(getSchedules(param));
	}
	
	public PageVO getScheduleVOsPage(ScheduleParam param, boolean grouping) throws HttpApiException {
		verify(param);
		
		Query<Schedule> q = scheduleDao.getSchedules(param);
		
		List<Schedule> schedules = q.offset(param.getStart()).limit(param.getPageSize()).order("scheduleTime").asList();
		
		List<?> list = null;
		if (grouping) {
			list = groupByDate(schedules);
		} else {
			list = convert(schedules);
		}
		
		return new PageVO(list, q.countAll(), param.getPageIndex(), param.getPageSize());
	}
	
	
	
	
	/**
	 * 数据校验
	 */
	private void verify(ScheduleParam param) {
		if (param.getUserId() == null) {
			throw new ServiceException("用户Id为空");
		}
	}
	
	/**
	 * 数据分组
	 * [2015-11-22 08:00, ...]
	 * [2015-11-22 09:00, ...]
	 * [2015-11-23 09:00, ...]
	 * --->
	 * dateStr: 2015-11-22 voList: {[2015-11-22 08:00, ...], [2015-11-22 09:00, ...]}
	 * dateStr: 2015-11-23 voList: {[2015-11-23 09:00, ...]}
	 * @param schedules
	 * @return
	 */
	private List<ScheduleGroupVO> groupByDate(List<Schedule> schedules) throws HttpApiException {
		List<ScheduleGroupVO> list = new ArrayList<ScheduleGroupVO>();
		
		Set<String> set = new HashSet<String>();
		ScheduleGroupVO group = null;
		List<ScheduleVO> voList = null;
		String key = null;
		for (Schedule schedule : schedules) {
			key = DateUtil.formatDate2Str(schedule.getScheduleTime(), FORMAT_YYYY_MM_DD);
			if (!set.contains(key)) {
				group = new ScheduleGroupVO();
				voList = new ArrayList<ScheduleVO>();
				list.add(group);
				set.add(key);
			}
			voList.add(convert(schedule));
			group.setDateStr(key);
			group.setVoList(voList);
		}
		return list;
	}

	/**
	 * schedules集合->ScheduleVO集合
	 * @param schedules
	 * @return
	 */
	private List<ScheduleVO> convert(List<Schedule> schedules) throws HttpApiException {
		List<ScheduleVO> scheduleVOs = new ArrayList<ScheduleVO>();
		
		for (Schedule schedule : schedules) {
			ScheduleVO vo = convert(schedule);//抛异常返回空
			if(vo==null){
				continue;
			}
			scheduleVOs.add(vo);
		}
		return scheduleVOs;
	}
	
	/**
	 * schedules->ScheduleVO
	 * @param schedule
	 * @return
	 */
	private ScheduleVO convert(Schedule schedule) throws HttpApiException {
		ScheduleVO vo = new ScheduleVO();
		vo.setRelationId(schedule.getRelationId());
		if (schedule.getTitle().contains("#")) {
			vo.setTitle(schedule.getTitle().split("#")[0]);
			vo.setCarePlanName(schedule.getTitle().split("#")[1]);
		} else {
			vo.setTitle(schedule.getTitle());
		}
		vo.setScheduleTime(DateUtil.formatDate2Str(schedule.getScheduleTime(), FORMAT_YYYY_MM_DD_HH_MM));
		vo.setAppointTime(schedule.getScheduleTime());
		vo.setScheduleType(schedule.getType());	
		vo.setCareItemId(schedule.getCareItemId());
		Order order = orderMapper.getOne(Integer.valueOf(schedule.getRelationId()));
		if (order == null)  return vo;
		vo.setOrderId(order.getId());
		vo.setFlag(callRecord.getStatusByOrderId(order.getId().toString()));
		User doctor = userDao.getUser(order.getDoctorId());
		if (doctor == null) return vo;
		vo.setDoctorId(order.getDoctorId());
		vo.setDoctorName(doctor.getName());
		vo.setDoctorHeadIcon(doctor.getHeadPicFileName());
		vo.setDoctorTele(doctor.getTelephone());
		vo.setTroubleFree(doctor.getDoctor() != null ? doctor.getDoctor().getTroubleFree() : "1");
		User patient = userDao.getUser(order.getUserId());
		if (patient == null) return vo;
		vo.setPatientId(order.getUserId());
		vo.setPatientName(patient.getName());
		vo.setPatientHeadIcon(patient.getHeadPicFileName());
		vo.setPatientTele(diseaseService.findByPk(order.getDiseaseId()).getTelephone());
		if (order.getPackType() == PackType.message.getIndex() || order.getPackType() == PackType.phone.getIndex()) {
			vo.setPrice(((order.getPrice()==null?0:order.getPrice())/100)+"元/"+(order.getTimeLong()==null?"":order.getTimeLong())+"分钟");
		} else if (order.getPackType() == PackType.careTemplate.getIndex()) {
			vo.setPrice(((order.getPrice()==null?0:order.getPrice())/100)+"元/次");
		} else {
			vo.setPrice(order.getPrice() == 0 ? "免费" : ((order.getPrice()/100) + "元"));
		}
		return vo;
	}

	@Override
	public int getNoServiceCount(ScheduleParam param, boolean grouping) throws HttpApiException {
		List<ScheduleVO> listSchedule = new ArrayList<ScheduleVO>();
		Query<Schedule> q = scheduleDao.getSchedulesCount(param);
		List<Schedule> schedules = q.asList();
		List<ScheduleVO> list = null;
		list = convert(schedules);
		for (ScheduleVO scheduleVO : list) {
			Order order = orderMapper.getOne(Integer.valueOf(scheduleVO.getRelationId()));
			if(null!=order){
				if(order.getOrderStatus()==OrderEnum.OrderStatus.已支付.getIndex()||order.getOrderStatus()==OrderEnum.OrderStatus.已取消.getIndex()){
					if(0==scheduleVO.getFlag()||2==scheduleVO.getFlag()){
						listSchedule.add(scheduleVO);
					}
				}
			}
		}
		return listSchedule.size();
	}

	@Override
	public Schedule createCareItemSchedule(CCareItemSchedule careItemSchedule) {
		Schedule schedule = this.construct(careItemSchedule);
		schedule = this.scheduleDao.save(schedule);
		return schedule;
	}
	
	@Override
	public void createCareItemSchedules(List<CCareItemSchedule> careItemSchedules) {
		if (CollectionUtils.isEmpty(careItemSchedules)) {
			return;
		}
		
		List<Schedule> schedules = new ArrayList<Schedule>(careItemSchedules.size());
		for (CCareItemSchedule careItemSchedule:careItemSchedules) {
			Schedule schedule = this.construct(careItemSchedule);
			schedules.add(schedule);
		}
		
		for (Schedule schedule:schedules) {
			this.scheduleDao.save(schedule);	
		}
	}

	@Override
	public int updateCareItemSchedules(List<CCareItemScheduleUpdate> updates) {
		if (CollectionUtils.isEmpty(updates)) {
			return 0;
		}
	
		checkData(updates);
		
		for (CCareItemScheduleUpdate update:updates) {
			scheduleDao.updateByCareItemId(update.getCareItemId(), update.getFullSendTime());
		}
		return updates.size();
	}
	

	private void checkData(List<CCareItemScheduleUpdate> updates) {
		if (CollectionUtils.isEmpty(updates)) {
			return;
		}
		for (CCareItemScheduleUpdate update:updates) {
			if (null == update.getCareItemId() || null == update.getFullSendTime()) {
				throw new ServiceException("参数有误");
			}
		}
	}

	private Schedule construct(CCareItemSchedule careItemSchedule) {
		Schedule schedule = new Schedule();
		if (null == careItemSchedule.getCareItemType()) {
			throw new ServiceException("数据有误：careItemType为空");
		}
		schedule.setType(convertType(careItemSchedule.getCareItemType()));
		if (null == schedule.getType()) {
			throw new ServiceException("数据有误：type为空");
		}
		if (null == careItemSchedule.getOrderId()) {
			throw new ServiceException("数据有误：orderId为空");
		}
		if (null == careItemSchedule.getCareItemId()) {
			throw new ServiceException("数据有误：careItemId为空");
		}
		if (null == careItemSchedule.getFullSendTime()) {
			throw new ServiceException("数据有误：fullSendTime为空");
		}
		if (null == careItemSchedule.getUserId()) {
			throw new ServiceException("数据有误：userId为空");
		}
		if (null == careItemSchedule.getCareItemTitle()) {
			throw new ServiceException("数据有误：careItemTitle为空");
		}
		if (null == careItemSchedule.getCarePlanName()) {
			throw new ServiceException("数据有误：carePlanName为空");
		}
		if (null == careItemSchedule.getCreateTime()) {
			throw new ServiceException("数据有误：createTime为空");
		}
		schedule.setRelationId(""+careItemSchedule.getOrderId());
		schedule.setCareItemId(careItemSchedule.getCareItemId());
		schedule.setScheduleTime(careItemSchedule.getFullSendTime());
		schedule.setUserId(careItemSchedule.getUserId());
		schedule.setTitle(String.format("%s#%s", careItemSchedule.getCareItemTitle(), careItemSchedule.getCarePlanName()));
		schedule.setCreateTime(careItemSchedule.getCreateTime());
		return schedule;
	}
	
	private Integer convertType(Integer careItemType) {
		switch (careItemType) {
		case 10:// 病情跟踪
			return 2;
		case 30: // 指数与量表
		case 40: // 调查表
		case 50: // 检查检验项
		case 60: // 其他提醒
		case 70: // 患教资料
			return careItemType / 10;
		}
		return null;
	}


}
