package com.dachen.health.pack.patient.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.lock.RedisLock;
import com.dachen.commons.lock.RedisLock.LockType;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.commons.constants.*;
import com.dachen.health.commons.constants.OrderEnum.OrderNoitfyType;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.utils.OrderNotify;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.friend.service.FriendsManager;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.schedule.dao.IOfflineDao;
import com.dachen.health.group.schedule.entity.po.OfflineItem;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.health.pack.account.entity.vo.BankCardVO;
import com.dachen.health.pack.account.service.IBankCardService;
import com.dachen.health.pack.consult.dao.ConsultationPackDao;
import com.dachen.health.pack.consult.entity.po.GroupConsultationPack;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.income.service.IIncomeService;
import com.dachen.health.pack.incomeNew.constant.IncomeEnumNew;
import com.dachen.health.pack.incomeNew.entity.po.Incomelog;
import com.dachen.health.pack.incomeNew.service.IncomelogService;
import com.dachen.health.pack.messageGroup.IMessageGroupService;
import com.dachen.health.pack.messageGroup.MessageGroupEnum;
import com.dachen.health.pack.order.dao.IOrderSessionContainerDao;
import com.dachen.health.pack.order.dao.IReplyCountRecordDao;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.entity.po.OrderFreeReplyCountRecord;
import com.dachen.health.pack.order.entity.po.OrderSessionContainer;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.service.IOrderDoctorService;
import com.dachen.health.pack.order.service.IOrderRefundService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.mapper.OrderSessionMapper;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.OrderSessionExample;
import com.dachen.health.pack.patient.model.OrderSessionExample.Criteria;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.pack.schedule.service.IScheduleService;
import com.dachen.im.server.constant.SysConstant;
import com.dachen.im.server.constant.SysConstant.SysUserEnum;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.request.GroupStateRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.pub.service.PubGroupService;
import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.DateUtil;
import com.dachen.util.RedisUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Maps;
import com.mobsms.sdk.MobSmsSdk;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @author vincent
 *
 */
@Service
public class OrderSessionServiceImpl extends BaseServiceImpl<OrderSession, Integer> implements IOrderSessionService {

	@Resource
	OrderSessionMapper mapper;

	@Resource
	PackMapper packMapper;
	
	@Autowired
	protected IPackService packService;

	@Resource
	OrderMapper orderMapper;

	@Resource
	IOrderService orderService;
	
	@Resource
	IOrderRefundService orderRefundService;

	@Autowired
	IBusinessServiceMsg businessServiceMsg;

	@Autowired
	private IGuideService guideService;

	@Autowired
	private FriendsManager friendsManager;

	@Autowired
	private IBaseUserService baseUserService;

	@Autowired
	private IScheduleService scheduleService;
	
	@Autowired
	private PubGroupService pubGroupService;

	@Autowired
	IOrderDoctorService orderDoctorService;

	@Autowired
	private IncomelogService incomelogService;

	@Autowired
	IIncomeService incomeService;

	@Resource
	protected CarePlanApiClientProxy carePlanApiClientProxy;
	
	@Autowired
    private IBankCardService bankCardService;
	
	@Autowired
	IGroupService groupService;
	
	@Autowired
	IOfflineDao offlineDao;
	
	@Resource
    private MobSmsSdk mobSmsSdk;
	
	@Autowired
	IMsgService msgService;
	
	@Autowired
	JedisTemplate jedisTemplate;

	@Autowired
	IReplyCountRecordDao replyCountRecordDao;
	
	@Autowired
	UserManager userManager;
	
	@Autowired
    IBaseDataService baseDataService;
	
	@Autowired
	IOrderSessionContainerDao orderSessionContainerDao;

	@Autowired
	IPatientService patientService;

	@Autowired
	private ConsultationPackDao consultationPackDao;

    @Autowired
    private IMessageGroupService messageGroupService;

	/**
	 * 被服务的患者池
	 */
	public final static String PATIENT_MESSAGE_TO_GUIDE_POOL_BE_SERVICED = KeyBuilder.Z_PATIENT_MESSAGE_TO_GUIDE_POOL+":beServiced";
	
	public final static String MESSAGE_REPLY_ORDER_COUNT = KeyBuilder.MESSAGE_REPLY_COUNT+":order"; 

	@Override
	public int save(OrderSession intance) {
		OrderSessionExample example = new OrderSessionExample();
		Criteria criteria = example.createCriteria();
		criteria.andOrderIdEqualTo(intance.getOrderId());
		int count = mapper.countByExample(example);
		if (count > 0) {
			throw new ServiceException(50005, "订单已经绑定会话");
		}
		return mapper.insert(intance);

	}

	@Override
	public int update(OrderSession intance) {
		return mapper.updateByPrimaryKey(intance);
	}

	@Override
	public int updateByPrimaryKeySelective(OrderSession record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int deleteByPK(Integer pk) {
		return mapper.deleteByPrimaryKey(pk);
	}

	@Override
	public OrderSession findByPk(Integer pk) {
		return mapper.selectByPrimaryKey(pk);
	}

	@Override
	public void appointServiceTime(Integer orderId, Long appointTime, boolean sendNotify,String hospitalId) throws HttpApiException {
		if (appointTime == null) {
			throw new ServiceException(30007, "parameter [appointTime]  is null");
		}
		long current = new Date().getTime();
		if (appointTime < (current + 60000)) {
			throw new ServiceException(30008, "预约的开始时间已经过时。");
		}
		Order order = orderService.getOne(orderId);
//		Order order = orderMapper.getOne(orderId);
		
		if (order == null) {
			throw new ServiceException(30004, "can't found Order#:" + orderId);
		}

		OrderSession orderSession = appointTime(order, appointTime, hospitalId);

		// 更新日程信息
		scheduleService.createOrderSchedule(orderId, appointTime);

		if (sendNotify) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("appointTime", appointTime);
			map.put("hospitalId", hospitalId);
			map.put("order", order);
			//修改预约时间
			guideService.updateAppointTime(orderId, appointTime);
			//发送消息通知
			orderService.sendOrderNoitfy(order.getUserId().toString(), order.getDoctorId().toString(),
					orderSession.getMsgGroupId(), OrderNoitfyType.appointTime, map);
            messageGroupService.updateGroupBizState(orderSession.getMsgGroupId()
                    , MessageGroupEnum.CHANGE_APPOINTMENT_TIME.getIndex());
		}

	}

	public OrderSession appointTime(Order order, Long appointTime, String hospitalId) {
		if (order.getPrice() == 0) {
			order.setOrderStatus(OrderStatus.已支付.getIndex());
		} else if (order.getOrderStatus() != OrderStatus.已支付.getIndex()) {
			order.setOrderStatus(OrderStatus.待支付.getIndex());
		}
		if (StringUtil.isNotEmpty(hospitalId)) {
			order.setHospitalId(hospitalId);
		}
		orderService.updateOrder(order);

		OrderSession orderSession = findOneByOrderId(order.getId());
		if (orderSession == null) {
			throw new ServiceException(30003, "订单还未绑定会话:" + order.getId());
		}
		orderSession.setPatientCanSend(false);
		orderSession.setAppointTime(appointTime);
		orderSession.setLastModifyTime(System.currentTimeMillis());
		update(orderSession);
		if(order.getPackType().intValue() != PackType.appointment.getIndex()){
			orderService.addJesQueTask(order);
		}

        GroupStateRequestMessage message = new GroupStateRequestMessage();
        message.setGid(orderSession.getMsgGroupId());
        message.setBizStatus(String.valueOf(order.getOrderStatus()));
        Map<String, Object> map = Maps.newHashMap();
        map.put("appointTime", appointTime);
        message.setParams(map);
        MsgHelper.updateGroupBizState(message);

		return orderSession;

	}

	@Override
	public void beginService(Integer orderId) throws HttpApiException {
		String tag = "beginService";
		if (logger.isInfoEnabled()) {
			logger.info("{}. orderId={}", tag, orderId);
		}
		
		Order order = orderService.getOne(orderId);
//		Order order = orderMapper.getOne(orderId);
		if (order == null) {
			throw new ServiceException(30004, "can't found Order#" + orderId);
		}

		OrderSession orderSession = findOneByOrderId(orderId);
		if (orderSession == null) {
			throw new ServiceException(30003, "订单还未绑定会话:" + orderId);
		}
		if (orderSession.getServiceBeginTime() != null) {
			if (logger.isInfoEnabled()) {
				logger.info("{}. 订单已经开始, return. orderId={}", tag, orderId);
			}
			return;
		}

		if(OrderType.appointment.getIndex() != order.getOrderType() && 
				OrderStatus.已支付.getIndex() != order.getOrderStatus()){
			throw new ServiceException(30005, "非已支付订单不可作此操作！");
		}

		// 更新orderSession的serviceBeginTime
		orderSession.setServiceBeginTime(new Date().getTime());
		this.update(orderSession);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderType", order.getOrderType());
		paramMap.put("order", order);
		if (order.getPackId() != null && order.getPackId() != 0) {
			Pack pack = packService.getPack(order.getPackId());
			paramMap.put("packName", pack.getName());
		}
		
		String doctorId = order.getDoctorId() + "";
		if (order.getOrderType().intValue() == OrderEnum.OrderType.consultation.getIndex()) {
			List<OrderDoctor> ods = orderDoctorService.findOrderDoctors(orderId);
			for (OrderDoctor od : ods) {
				if (od.getDoctorId().intValue() != order.getDoctorId().intValue()) {
					doctorId += "," + od.getDoctorId();
				}
			}
		}
		
		/**
		 * 针对博德
		 */
		if(order.getPackType().intValue() == PackType.phone.getIndex()){
			String groupId = groupService.isOpenSelfGuideAndGetGroupId(order.getDoctorId(), UserType.doctor.getIndex());
			if(StringUtils.isNotBlank(groupId)){
				JobTaskUtil.autoFinishAfterBeginService(order.getId(), 2 * 60 * 60);
			}
		}
		
		if(order.getPackType().intValue() == PackType.appointment.getIndex()){
			//offlineDao.updateOfflineItemStatusByOrderId(order.getId(), order.getDoctorId(), ScheduleEnum.OfflineStatus.已开始.getIndex());
			//2小时之后自动结束订单
			JobTaskUtil.autoFinishAppointmentOrder(orderId, 2 * 60 * 60);
		}
		
		orderService.sendOrderNoitfy(order.getUserId().toString(), doctorId,
				orderSession.getMsgGroupId(), OrderNoitfyType.beginService, paramMap);
        messageGroupService.updateGroupBizState(orderSession.getMsgGroupId()
                , MessageGroupEnum.START_SERVICE.getIndex());

	}

	@Override
	public void beginService4Plan(Integer orderId, String startDate) throws HttpApiException {
		String tag = "beginService4Plan";
		if (logger.isInfoEnabled()) {
			logger.info("{}. orderId={}, startDate={}", tag, orderId, startDate);
		}
		this.beginService(orderId);
		
		Order order = orderService.getOne(orderId);
		
		if (order.getOrderType() != OrderType.care.getIndex()) {
			throw new ServiceException("非关怀计划不可作此操作！！");
		}
		
		if (null == startDate) {
			startDate = DateUtil.formatDate2Str(new Date(), DateUtil.FORMAT_YYYY_MM_DD);
		}
		
		try {
			carePlanApiClientProxy.beginCarePlan(order.getCareTemplateId(), startDate, order.getId());
		} catch (HttpApiException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void finishServiceByPatient(Integer orderId,Integer patientId) throws HttpApiException {
		String tag = "finishServiceByPatient";
		if (logger.isInfoEnabled()) {
			logger.info("{}. orderId={}, patientId={}", tag, orderId, patientId);
		}
		this.finishService(orderId, 2, true);
	}
	
	public void updateSplit(String splitJson,Integer orderId,boolean finishService) throws HttpApiException {
		if(StringUtil.isEmpty(splitJson)){
			throw new ServiceException("分成比例不能为空");
		}
		Order order = orderMapper.getOne(orderId);
		Map<String,Integer> map = (Map<String, Integer>)JSON.parse(splitJson);		
		List<OrderDoctor> list = orderDoctorService.findOrderDoctors(orderId);
		List<Integer> deleteIds = new ArrayList<Integer>();
		int i=0;
		for(OrderDoctor od : list){
			if(map.containsKey(od.getDoctorId()+"")){
				int splitRatio = map.get(od.getDoctorId()+"");
				if(od.getSplitRatio().intValue() != splitRatio){
					OrderDoctor update = new OrderDoctor();
					update.setId(od.getId());
					update.setSplitRatio(splitRatio);
		        	Double price = Double.valueOf(order.getPrice())*splitRatio;
		            price = price/100;
		            update.setSplitMoney(Double.valueOf(price));
					orderDoctorService.updateSelective(update);
				}
				map.remove(od.getDoctorId()+"");
				i += splitRatio;
			}else{
				deleteIds.add(od.getDoctorId());
			}
		}
		if(!deleteIds.isEmpty()){
			//删掉需要删除的分成医生
			orderDoctorService.deleteByIdList(deleteIds);
		}
		
		//增加诊断过程中的新增医生
		Iterator<Entry<String, Integer>>  iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Integer> entry = iterator.next();
			User u = userManager.getUser(Integer.parseInt(entry.getKey()));
			if(u == null || u.getUserType() != UserEnum.UserType.doctor.getIndex()){
				continue;
			}
			OrderDoctor orderDoctor = new OrderDoctor();
	        orderDoctor.setOrderId(orderId);
        	orderDoctor.setDoctorId(u.getUserId());
        	int percent = entry.getValue();
        	orderDoctor.setSplitRatio(percent);
        	Double price  = Double.valueOf(order.getPrice())*percent;
            price = price/100;
            orderDoctor.setSplitMoney(Double.valueOf(price));
            orderDoctorService.add(orderDoctor);
            i += percent;
		}
		if(i != 100){
			throw new ServiceException("分成比例有误");
		}
		if(finishService){
			finishService(orderId, 2);
			if(order.getOrderType() == OrderType.consultation.getIndex()){
				List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
				ImgTextMsg textMsg = new ImgTextMsg();
				textMsg.setStyle(7);
				textMsg.setTitle("会诊结束");
				textMsg.setTime(System.currentTimeMillis());
				textMsg.setFooter("查看详情");
				OrderSession orderSession = findOneByOrderId(orderId);
				if (orderSession == null) {
					throw new ServiceException(30003, "订单还未绑定会话:" + orderId);
				}
				//发给小医生
				GroupConsultationPack pack = consultationPackDao.getById(order.getConsultationPackId());
				String expertName = "";
				if(pack != null){
					User u = userManager.getUser(pack.getConsultationDoctor());
					if(u == null)
						return;
					expertName = u.getName();
					String patientName = patientService.getPatientNameByPatientId(order.getPatientId());
					String content = "您为患者"+patientName +" 发起的会诊咨询已结束，专家"+ expertName+ "医生已填写会诊报告，请及时知悉并查看、查看详情!";
					textMsg.setContent(content);
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("bizType", 35);
					param.put("bizId", orderSession.getMsgGroupId());
					textMsg.setParam(param);
					mpt.add(textMsg);
					businessServiceMsg.sendTextMsg(order.getDoctorId()+"", SysGroupEnum.TODO_NOTIFY, mpt, null);
				}
				//发给患者
				User u = userManager.getUser(order.getDoctorId());
				if(u == null){
					return;
				}
				String patientContent = u.getName() + "医生为您发起的会诊咨询已结束，专家"+ expertName +"医生已填写会诊报告，请及时知悉并查看、查看详情!";
				textMsg.setContent(patientContent);
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("bizType", 35);
				param.put("bizId", orderSession.getMsgGroupId());
				textMsg.setParam(param);
				mpt.add(textMsg);
				businessServiceMsg.sendTextMsg(order.getUserId()+"", SysGroupEnum.TODO_NOTIFY, mpt, null);
			}
		}
	}
	
	@Override
	public synchronized void finishService(Integer orderId, Integer aotoStatus) throws HttpApiException {
		this.finishService(orderId, aotoStatus, false);
	}
	
	private synchronized void finishService(Integer orderId, Integer aotoStatus, boolean isByPatient) throws HttpApiException {
		//Order order = orderService.getOne(orderId);
		Order order = orderMapper.getOne(orderId);
		if (order == null) {
			throw new ServiceException(30004, "can't found Order#" + orderId);
		}

		if (OrderStatus.已完成.getIndex() == order.getOrderStatus().intValue())
			return;

		if (OrderStatus.已支付.getIndex() == order.getOrderStatus().intValue()) {
			order.setOrderStatus(OrderStatus.已完成.getIndex());
		} else {
			throw new ServiceException(30005, "非已支付订单不可作此操作！");
		}
		if(order.getPackType().intValue() ==PackType.phone.getIndex()){
			order.setRecordStatus(OrderEnum.OrderRecordStatus.doc_confirmed.getIndex());
		}
		if(order.getOrderType().intValue() ==OrderType.consultation.getIndex()){
			order.setRecordStatus(OrderEnum.OrderRecordStatus.doc_confirmed.getIndex());
		}
		orderService.updateOrder(order);

		OrderSession orderSession = findOneByOrderId(orderId);
		if (orderSession == null) {
			throw new ServiceException(30003, "订单还未绑定会话:" + orderId);
		}
		orderSession.setServiceEndTime(new Date().getTime());
		update(orderSession);

		// 门诊订单
		if (order.getOrderType() == OrderEnum.OrderType.outPatient.getIndex()) {
			String key = RedisUtil.generateKey(RedisUtil.ADVISORY_QUEUE, order
					.getDoctorId().toString());
			// 从除咨询中的队列删除
			RedisUtil.removeVal(key, orderId.toString());
		} else if (order.getOrderType() == OrderEnum.OrderType.care.getIndex()
				|| order.getOrderType() == OrderEnum.OrderType.followUp.getIndex()) {
			// 删除计划表记录
//			careNewService.updateStatus(order.getCareTemplateId(), 0);

            orderService.finishCareOrderAsync(order);

			
			// 如果是手动的删除当天后的记录
//			if (aotoStatus == OrderEnum.OrderSessionOpenStatus.manually.getIndex()) {
//				scheduleService.deleteCareRecord(order.getCareTemplateId(), System.currentTimeMillis());
//			}
		}

		/*if(order.getPackType().intValue() == PackType.appointment.getIndex()){
			offlineDao.updateOfflineItemStatusByOrderId(order.getId(), order.getDoctorId(), ScheduleEnum.OfflineStatus.已完成.getIndex());
		}*/
		
		// 计算收入
		calcIncome(orderId);
		
		//发送消息通知
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderType", order.getOrderType());
		
		Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
		if (order.getPackId() != null && order.getPackId() != 0) {
			try {
//				Pack pack = packService.getPack(order.getPackId());
				paramMap.put("packName", pack.getName());	
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		paramMap.put("orderId", orderId);
		paramMap.put("auto", aotoStatus);// 该参数是只在随访计划的时候处理医生或系统自动结束不同情况下给医生或患者发送不同短信提醒
		
		if(order.getPackType() == PackEnum.PackType.consultation.getIndex()){
			GroupConsultationPack consultPack = consultationPackDao.getById(order.getConsultationPackId());
			if(consultPack != null){
				User u = userManager.getUser(consultPack.getConsultationDoctor());
				if(u != null){
					paramMap.put("msg", u.getName() + "医生已填写会诊报告，本次会诊结束");
				}
			}
			
		}
		// 医生或患者结束取消发送IM通知及短信通知
		orderService.sendOrderNoitfy(order.getUserId().toString(), order.getDoctorId() + "",
				orderSession.getMsgGroupId(), OrderNoitfyType.endService, paramMap, isByPatient);

        messageGroupService.updateGroupBizState(orderSession.getMsgGroupId(), MessageGroupEnum.FINISH_SERVICE.getIndex());

		/**
		 * add at 2016年12月8日14:37:23
		 * 订单结束的时候如果有orderSessionContainer po 
		 * 则修改改数据状态
		 */
		List<OrderSessionContainer> oss = orderSessionContainerDao.findByMsgGroupId(orderSession.getMsgGroupId());
		if(oss != null){
			for(OrderSessionContainer osc : oss){
				orderSessionContainerDao.updateStatus(osc.getId(), OrderStatus.已完成.getIndex());
			}
		}


	}

	public void finishServiceWhenPaySuccess(Integer orderId) throws HttpApiException {
		OrderSession orderSession = findOneByOrderId(orderId);
		if (orderSession == null) {
			throw new ServiceException(30003, "订单还未绑定会话:" + orderId);
		}
		orderSession.setServiceBeginTime(System.currentTimeMillis());
		orderSession.setServiceEndTime(System.currentTimeMillis());
		mapper.updateByPrimaryKey(orderSession);
		
		calcIncome(orderId);
	}


	private void calcIncome(Integer orderId) throws HttpApiException {
		Order order = orderService.getOne(orderId);
//		Order order = orderMapper.getOne(orderId);
		// 订单结束时，需要将订单金额转化为医生收入
		incomelogService.addOrderIncomelog(order);

		// 订单完成时向所有有收入的医生发送绑定银行卡的通知
		List<Incomelog> list = incomelogService.getIncomer(order.getId());
		for (Incomelog log : list) {
			if (log.getType() == IncomeEnumNew.ObjectType.doctor.getIndex()) {
				List<BankCardVO> bList = bankCardService.getAll(log.getDoctorId());
				List<Incomelog> dlist = incomelogService.getLogListByDoc(log.getDoctorId());
				if (dlist.size() == 1 && bList.isEmpty()) {
					sendNotify(log.getDoctorId());
				}
			}
		}
	}
	
	private void sendNotify(Integer doctorId) throws HttpApiException {
		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
		ImgTextMsg textMsg = new ImgTextMsg();
		textMsg.setStyle(7);
		textMsg.setTitle("绑定银行卡");
		textMsg.setTime(System.currentTimeMillis());
		textMsg.setContent("恭喜您获得了第一笔收入，快去绑定银行卡，我们会在指定的日期将收入打款到您的账户上");
		textMsg.setFooter("立即绑定");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("bizType", 18);
		textMsg.setParam(param);
		mpt.add(textMsg);
		businessServiceMsg.sendTextMsg(doctorId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
	}

	@Override
	public List<OrderSession> findByOrderId(Integer orderId) {
		OrderSessionExample example = new OrderSessionExample();
		example.createCriteria().andOrderIdEqualTo(orderId);
		List<OrderSession> orderSessions = mapper.selectByExample(example);

		return orderSessions;
	}

	@Override
	public OrderSession findOneByOrderId(Integer orderId) {
		List<OrderSession> orderSessions = findByOrderId(orderId);
		if (orderSessions != null && !orderSessions.isEmpty()) {
			return orderSessions.get(0);
		}
		return null;
	}
	
	@Override
	public List<OrderSession> findByOrderIds(List<Integer> orderIds) {
		if (CollectionUtils.isEmpty(orderIds)) {
			return null;
		}
		
		List<OrderSession> osList = new ArrayList<OrderSession>(orderIds.size());
		for (Integer orderId:orderIds) {
			OrderSession os = this.findOneByOrderId(orderId);
			osList.add(os);
		}
		return osList;
	}
	

	@Override
	public List<OrderSession> findByMsgGroupId(String msgGroupId) {
		OrderSessionExample example = new OrderSessionExample();
		example.createCriteria().andMsgGroupIdEqualTo(msgGroupId);
		List<OrderSession> orderSessions = mapper.selectByExample(example);
		return orderSessions;
	}

	@Override
	public OrderSession findOneByMsgGroupId(String msgGroupId) {
		List<OrderSession> orderSessions = findByMsgGroupId(msgGroupId);
		if (orderSessions != null && !orderSessions.isEmpty()) {
			return orderSessions.get(0);
		}
		return null;
	}

	@Override
	public void abandonService(Integer orderId) throws HttpApiException {
		Order order = orderService.getOne(orderId);
		if (order == null) {
			throw new ServiceException(40005, "找不到对应订单");
		}
		orderService.cancelOrder(order, ReqUtil.instance.getUserId(), OrderEnum.orderCancelEnum.manual.getIndex());
		// 从当前等待队列里移除这当前订单
		String key = RedisUtil.generateKey(RedisUtil.WAITING_QUEUE, order.getDoctorId().toString());
		OrderSession orderSession = findOneByOrderId(orderId);
		if (orderSession == null) {
			throw new ServiceException(30003, "订单还未绑定会话:" + orderId);
		}
		
		orderService.sendOrderNoitfy(order.getUserId().toString(), order.getDoctorId().toString(),
				orderSession.getMsgGroupId(), OrderNoitfyType.abandonService, null);
		RedisUtil.removeVal(key, orderId.toString());

        messageGroupService.updateGroupBizState(orderSession.getMsgGroupId()
                , MessageGroupEnum.ABANDON_SERVICE.getIndex());

	}

	@Override
	public void prepareService(Integer orderId) throws HttpApiException {
		Order order = orderService.getOne(orderId);
		if (order == null) {
			throw new ServiceException(40005, "找不到对应订单");
		}
		if (OrderStatus.已支付.getIndex() != order.getOrderStatus()) {
			throw new ServiceException(30005, "非已支付订单不可作此操作！");
		}
		OrderSession orderSession = findOneByOrderId(orderId);
		if (orderSession == null) {
			throw new ServiceException(30003, "订单还未绑定会话:" + orderId);
		}
		// 门诊订单
		// if(order.getOrderType() ==
		// OrderEnum.OrderType.outPatient.getIndex()){
		// 保存开始叫号时间点
		orderSession.setTreatBeginTime(new Date().getTime());
		update(orderSession);

		DoctorPatient doctorPatient = null;
		try {
			doctorPatient = (DoctorPatient) friendsManager.getFriend(
					order.getUserId(), order.getDoctorId());
			//doctorPatient = tagDao.findByDoctorIdAndPatientId(order.getDoctorId(), order.getPatientId());
			if (doctorPatient == null) {
				//baseUserService.setDoctorPatient(order.getDoctorId(), order.getUserId());
				//2016-12-12医患关系的变化
				baseUserService.addDoctorPatient( order.getDoctorId(), order.getPatientId(),order.getUserId());
				// 关注医生公众号
				pubGroupService.addSubUser(order.getDoctorId(),
						order.getUserId());
				businessServiceMsg.sendEventFriendChange(EventEnum.ADD_FRIEND,
						order.getDoctorId().toString(), order.getUserId().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 向咨询队列里压入元素
		String key = RedisUtil.generateKey(RedisUtil.ADVISORY_QUEUE, order
				.getDoctorId().toString());
		RedisUtil.rpush(key, orderId.toString());
		// 再把等待队列里元素弹出
		key = RedisUtil.generateKey(RedisUtil.WAITING_QUEUE, order
				.getDoctorId().toString());
		RedisUtil.removeVal(key, orderId.toString());
		
		if(order.getOrderType() == OrderType.outPatient.getIndex()){
			JobTaskUtil.cancelOutpatient(orderId);
		}
		
		orderService.sendOrderNoitfy(order.getUserId().toString(), order
				.getDoctorId().toString(), orderSession.getMsgGroupId(),
				OrderNoitfyType.prepareService, null);
        messageGroupService.updateGroupBizState(orderSession.getMsgGroupId()
                , MessageGroupEnum.PREPARE_SERVICE.getIndex());
	}
	
	@Override
	public List<OrderSession> getAllMoringBeginConsultationOrders(
			List<Integer> orderIds, int intervalType) {
		Map<String, Object> params = getParamsByIntervalType(intervalType);
		params.put("orderIds", orderIds);
		return mapper.getAllMoringBeginConsultationOrders(params);
	}

	private static Map<String, Object> getParamsByIntervalType(int intervalType) {
		Map<String, Object> params = new HashMap<String, Object>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if (intervalType == 0) {
			int beginDayVal = cal.get(Calendar.DAY_OF_YEAR) + 1;
			int endDayVal = cal.get(Calendar.DAY_OF_YEAR) + 2;
			cal.set(Calendar.DAY_OF_YEAR, beginDayVal);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			long begin = cal.getTime().getTime();
			cal.set(Calendar.DAY_OF_YEAR, endDayVal);
			long end = cal.getTime().getTime();
			params.put("begin", begin);
			params.put("end", end);
		} else if (intervalType == 1) {
			int beginMinuteVal = cal.get(Calendar.MINUTE) + 30;
			cal.set(Calendar.MINUTE, beginMinuteVal);
			cal.set(Calendar.SECOND, 0);
			long begin = cal.getTime().getTime();
			long end = begin + 1 * 60 * 1000;
			params.put("begin", begin);
			params.put("end", end);
		}
		return params;
	}

	@Override
	public List<Integer> getTimeAreaOrderIds(Long start, Long end) {
		Map<String,Long> map = new HashMap<String,Long>();
		map.put("start", start);
		map.put("end", end);
		return mapper.getTimeAreaOrderIds(map);
	}

	@Override
	public void agreeAppointmentOrder(Integer orderId) throws HttpApiException {
		OfflineItem item = offlineDao.findOfflineItemByOrderId(orderId,ReqUtil.instance.getUserId());
		if(item == null || 
				StringUtils.isBlank(item.getHospitalId()) || 
				item.getStartTime() == null){
			throw new ServiceException("订单[orderId]="+orderId+"预约数据错误");
		}
		appointServiceTime(orderId,item.getStartTime(),true,item.getHospitalId());
		//我是医生，我同意了患者这个预约时间
		//发送推送信息：您同意预约（医生）；医生同意预约，请及时付款（患者）

		//发送短信：***患者您好，你预约的***医生接受您的预约，请马上点击http：****打开app支付，以免预约不到该医生。
		
	}

	@Override
	public OrderSession findOneByGroupId(String msgGroupId) {
		List<OrderSession> osList = mapper.selectByGroupId(msgGroupId);
		if(osList != null && osList.size() > 0)
			return osList.get(0);
		return null;
	}

	@Override
	public Object getOrCreatePatientCustomerSession() {
		
		/**
		 * 1、检验会话是否存在
		 * 2、是否去创建新会话  还是 返回旧会话
		 */
		String userId = ReqUtil.instance.getUserId()+"";
		GroupInfo groupInfo = null;
		synchronized (userId) {
			String gid = SysConstant.getGuideDocGroupId(userId);
			GroupInfoRequestMessage request = new GroupInfoRequestMessage();
			request.setGid(gid);			
			request.setUserId(userId);
			try {
				groupInfo = (GroupInfo) msgService.getGroupInfo(request);
			} catch (ServiceException e) {
				groupInfo = (GroupInfo) msgService.createGroup(String.valueOf(ReqUtil.instance.getUserId()), null,null);
			} catch (HttpApiException e) {
				e.printStackTrace();
			}
			if(Objects.isNull(groupInfo))
				groupInfo = (GroupInfo) msgService.createGroup(String.valueOf(ReqUtil.instance.getUserId()), null,null);
		}
		Map<String,Object> map = new HashMap<>();
		map.put("patientUserId", String.valueOf(ReqUtil.instance.getUserId()));
		map.put("gid", groupInfo.getGid());
		map.put("gname", groupInfo.getGname());
		map.put("gpic", groupInfo.getGpic());
		return map;
	}

	@Override
	public void cacheBeServicedUser() {
		/**
		 * zrevrangeByScore 坑逼直接获取不到值
		 * 所以把score存成负数实倒序的效果
		 */
		jedisTemplate.zadd(PATIENT_MESSAGE_TO_GUIDE_POOL_BE_SERVICED, -System.currentTimeMillis(), ReqUtil.instance.getUserId()+"");
	}
	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	@Autowired
	protected AsyncTaskPool asyncTaskPool;


	@Override
	public void cacheSendMessageRecord(String messageGroupId) {
		char bit = '1';//医生
		if(ReqUtil.instance.getUser().getUserType().intValue() == UserType.patient.getIndex())
			bit = '0';//患者
		RedisLock lock = new RedisLock();
		boolean locked = lock.lock(messageGroupId, LockType.messageReplyCount);
		try{
			if(locked){
				String messageSerial = jedisTemplate.hget(MESSAGE_REPLY_ORDER_COUNT, messageGroupId);
				if(StringUtils.isBlank(messageSerial))
					jedisTemplate.hset(MESSAGE_REPLY_ORDER_COUNT, messageGroupId, bit+"");
				else{
					char last = messageSerial.charAt(messageSerial.length()-1);
					if(last == '0' && bit == '1'){
						List<OrderSession> osList = mapper.selectByGroupId(messageGroupId);
						OrderSession os = osList.get(0);
						if(bit == '1'){
							Order o = orderService.getOne(os.getOrderId());
							if(o.getDoctorId() != ReqUtil.instance.getUserId())
								return;
						}
						if(os.getTotalReplyCount() == null){
							/**兼容更老的数据**/
							os.setTotalReplyCount(SysConstants.DEFAULT_MESSAGE_REPLY_COUNT);
							os.setReplidCount(1);
						}else{
							Integer replyCount = os.getReplidCount();
							if(replyCount == null)
								replyCount = 0;
							os.setReplidCount(++replyCount);
						}
						os.setLastModifyTime(System.currentTimeMillis());
						mapper.updateByPrimaryKey(os);
						int beUseCount = os.getTotalReplyCount() - os.getReplidCount();
						/**发送指令**/
						messageReplyCountChangeEvent(messageGroupId, os.getOrderId(), beUseCount);
						/**生成定时任务**/
						if(beUseCount == 0){
							//短信提醒
							JobTaskUtil.notifyWhenReplyCountEqZero(os.getOrderId());
							//结束服务
							JobTaskUtil.finishWhenReplyCountEqZero(os.getOrderId());
						}
						logger.info("messageGroupId = "+messageGroupId +" has reamain count = "+beUseCount);
					}
					jedisTemplate.hset(MESSAGE_REPLY_ORDER_COUNT, messageGroupId, messageSerial+bit);
				}

				if(bit == '1' && !StringUtils.contains(messageSerial, "1")){
					List<OrderSession> osList = mapper.selectByGroupId(messageGroupId);
					OrderSession os = osList.get(0);
					Order o = orderMapper.getOne(os.getOrderId());
					User p = userManager.getUser(o.getUserId());
					User d = userManager.getUser(o.getDoctorId());
					//亲，XX医生已经回复您了，快去看看医生说了什么吧
					String url = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", messageGroupId, UserType.patient.getIndex()));
					mobSmsSdk.send(p.getTelephone(), baseDataService.toContent("1049", d.getName(),url));
				}
			}
		} catch (HttpApiException e) {
			logger.error(e.getMessage(), e);
		} finally{
			lock.unlock(messageGroupId, LockType.messageReplyCount);
		}
	}

	@Override
	public void messageReplyCountChangeEvent(String messageGroupId, Integer orderId, Integer beUseCount) {
		Order o = orderService.getOne(orderId);
		/*患者报道完成后，通知IM更改会话状态*/
		GroupStateRequestMessage request = new GroupStateRequestMessage();
		request.setGid(messageGroupId);
        Map<String,Object> params = new HashMap<>();
        params.put("beUseCount", beUseCount);
        request.setParams(params);
		MsgHelper.updateGroupBizState(request);
	}

	@Override
	public void addFreeReplyCount(String messageGroupId, Integer count) throws HttpApiException {
		if(count == null) count = SysConstants.DEFAULT_MESSAGE_REPLY_COUNT;
		List<OrderSession> osList = mapper.selectByGroupId(messageGroupId);
		OrderSession os = osList.get(0);
		if(os == null)
			throw new ServiceException("不能根据gid="+messageGroupId+"找到会话");
		/***新版本可以赠送**/
		/*if(os.getServiceEndTime() != null)
			throw new ServiceException("订单已经结束，不能再赠送");*/
		Integer total = os.getTotalReplyCount();
		if(total == null)
			total = 0;
		if(os.getReplidCount() == null)
			os.setReplidCount(0);
		//目前是赠送之前表示可用次数已经耗尽
		if(total != os.getReplidCount())
			os.setReplidCount(total);
		total += count;
		os.setTotalReplyCount(total);
		os.setLastModifyTime(System.currentTimeMillis());
		mapper.updateByPrimaryKey(os);
		messageReplyCountChangeEvent(messageGroupId,os.getOrderId(), os.getTotalReplyCount() - os.getReplidCount());
		//记录赠送次数
		OrderFreeReplyCountRecord record = new OrderFreeReplyCountRecord();
		record.setCreateTime(System.currentTimeMillis());
		record.setFreeCount(count);
		record.setOrderId(os.getOrderId());
		record.setMessageGroupId(messageGroupId);
		replyCountRecordDao.insert(record);
		/**取消定时任务**/
		JobTaskUtil.cancelReplyCountEqZeroJob(os.getOrderId());
		
		List<ImgTextMsg> msgs = new ArrayList<ImgTextMsg>();
		ImgTextMsg imgTextMsg = new ImgTextMsg();
		imgTextMsg.setTitle("医生已赠送沟通次数");
		imgTextMsg.setContent("本次咨询含医生3次回复。为节省沟通次数，提问时请尽量一次性描述病情与问题。");
		imgTextMsg.setTime(System.currentTimeMillis());
		imgTextMsg.setStyle(7);
		msgs.add(imgTextMsg);
		businessServiceMsg.sendTextMsgToGid(SysUserEnum.SYS_001.getUserId(), messageGroupId, msgs, null);
	}

    private void cacheSendMessageRecordTemp(String messageGroupId) throws HttpApiException {
		char bit = '1';//医生
		if(ReqUtil.instance.getUser().getUserType().intValue() == UserType.patient.getIndex())
			bit = '0';//患者
		String messageSerial = jedisTemplate.hget(MESSAGE_REPLY_ORDER_COUNT, messageGroupId);
		if(StringUtils.isBlank(messageSerial))
			jedisTemplate.hset(MESSAGE_REPLY_ORDER_COUNT, messageGroupId, bit+"");
		else{
			char last = messageSerial.charAt(messageSerial.length()-1);
			List<OrderSession> osList = mapper.selectByGroupId(messageGroupId);
			OrderSession os = osList.get(0);
			if(bit == '1'){
				Order o = orderService.getOne(os.getOrderId());
				if(o.getDoctorId() != ReqUtil.instance.getUserId() && o.getPackType() != PackType.consultation.getIndex())
					return;
			}
			if(last == '0' && bit == '1'){
				if(os.getTotalReplyCount() == null){
					/**兼容更老的数据**/
					os.setTotalReplyCount(SysConstants.DEFAULT_MESSAGE_REPLY_COUNT);
					os.setReplidCount(1);
				}else{
					Integer replyCount = os.getReplidCount();
					if(replyCount == null)
						replyCount = 0;
					os.setReplidCount(++replyCount);
				}
				os.setLastModifyTime(System.currentTimeMillis());
				mapper.updateByPrimaryKey(os);
				int beUseCount = os.getTotalReplyCount() - os.getReplidCount();
				/**发送指令**/
				messageReplyCountChangeEvent(messageGroupId, os.getOrderId(), beUseCount);
				/**生成定时任务**/
				if(beUseCount == 0){
					//短信提醒
					JobTaskUtil.notifyWhenReplyCountEqZero(os.getOrderId());
					//结束服务
					//JobTaskUtil.finishWhenReplyCountEqZero(os.getOrderId());
					Order o = orderMapper.getOne(os.getOrderId());
					if(o.getOrderStatus() != OrderStatus.已完成.getIndex() &&
							(o.getPackType() == PackType.message.getIndex() ||
									o.getPackType() == PackType.integral.getIndex())){
						this.finishService(os.getOrderId(),1);
					}
				}
				logger.info("messageGroupId = "+messageGroupId +" has reamain count = "+beUseCount);
			}
			jedisTemplate.hset(MESSAGE_REPLY_ORDER_COUNT, messageGroupId, messageSerial+bit);
		}

		if(bit == '1' && !StringUtils.contains(messageSerial, "1")){
			List<OrderSession> osList = mapper.selectByGroupId(messageGroupId);
			OrderSession os = osList.get(0);
			Order o = orderMapper.getOne(os.getOrderId());
			User p = userManager.getUser(o.getUserId());
			User d = userManager.getUser(o.getDoctorId());
			//亲，XX医生已经回复您了，快去看看医生说了什么吧
			String url = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", messageGroupId, UserType.patient.getIndex()));
			mobSmsSdk.send(p.getTelephone(), baseDataService.toContent("1049", d.getName(),url));
		}
	}

	@Override
	public void cacheSessionMessageRecord(String messageGroupId , String messageId) throws HttpApiException {
		char bit = '1';//医生
		if(ReqUtil.instance.getUser().getUserType().intValue() == UserType.patient.getIndex())
			bit = '0';//患者
		String messageSerial = jedisTemplate.hget(MESSAGE_REPLY_ORDER_COUNT, messageGroupId);
		OrderSessionContainer os = getSessionContainer(messageGroupId);
		if (os == null) {
			cacheSendMessageRecordTemp(messageGroupId);
			return;
		} else {
//			session = findOneByOrderId(os.getOrderId());
		}
		if (bit == '1') {
			if (os.getDoctorId() != ReqUtil.instance.getUserId() && os.getPackType()!= PackType.consultation.getIndex())
				return;
		}
		/**
		 * 更新会话中的第一发送的消息
		 */
//		if (session.getFirstMessageId() == null) {
//			Map<String, Object> param = new HashMap<>();
//			param.put("firstMessageId", messageId);
//			param.put("orderId", session.getOrderId());
//			mapper.updateFirstMessage(param);
//		}
		if (StringUtils.isBlank(messageSerial)) {
			jedisTemplate.hset(MESSAGE_REPLY_ORDER_COUNT, messageGroupId, bit + "");
			OrderNotify.fireOrdersessionSendmsg( os.getOrderId(),bit + "");  //根据医生患者会话消息来更改订单待处理状态
		} else {
			char last = messageSerial.charAt(messageSerial.length() - 1);
			if (last == '0' && bit == '1' && os != null && os.getTotalReplyCount() != null && os.getTotalReplyCount().intValue() != 0) {
				Integer replyCount = os.getReplidCount();
				if (replyCount == null)
					replyCount = 0;
				os.setReplidCount(++replyCount);
				orderSessionContainerDao.updateById(os.getId(), null, null, null, null, os.getReplidCount(), null);

				int beUseCount = os.getTotalReplyCount() - os.getReplidCount();
				/**发送指令**/
				messageReplyCountChangeEvent(messageGroupId, os.getOrderId(), beUseCount);
				/**生成定时任务**/
				if (beUseCount == 0) {
					//短信提醒
					JobTaskUtil.notifyWhenReplyCountEqZero(os.getOrderId());
					//结束服务
					//第3次取订单，聊完次数结束订单
					Order o = orderMapper.getOne(os.getOrderId());
					if (o.getOrderStatus() != OrderStatus.已完成.getIndex() &&
							(o.getPackType() == PackType.message.getIndex() ||
									o.getPackType() == PackType.integral.getIndex())) {
						this.finishService(os.getOrderId(), 1);
					}
				}
				logger.info("messageGroupId = " + messageGroupId + " has reamain count = " + beUseCount);
			}
			jedisTemplate.hset(MESSAGE_REPLY_ORDER_COUNT, messageGroupId, messageSerial + bit);
			OrderNotify.fireOrdersessionSendmsg( os.getOrderId(), messageSerial + bit); //根据医生患者会话消息来更改订单待处理状态
		}

		if (bit == '1' && !StringUtils.contains(messageSerial, "1")) {
			OrderNotify.fireOrdersessionSendFirstmsg(os.getOrderId(), messageGroupId);
		}
	}

	 



	@Override
	public void addFreeMessageCount(String messageGroupId, Integer count) throws HttpApiException {
		if(count == null) count = SysConstants.DEFAULT_MESSAGE_REPLY_COUNT;
		OrderSessionContainer os = getSessionContainer(messageGroupId);
		if(os == null){
			/**老数据做兼容**/
			addFreeReplyCount(messageGroupId,count);
			return ;
		}
		Integer total = os.getTotalReplyCount();
		if(total == null)
			total = 0;
		if(os.getReplidCount() == null)
			os.setReplidCount(0);
		//恢复次数到平衡
		if(total != os.getReplidCount())
			os.setReplidCount(total);
		total += count;
		os.setTotalReplyCount(total);
		orderSessionContainerDao.updateById(os.getId(), null, null, null, total, os.getReplidCount(), null);
		messageReplyCountChangeEvent(messageGroupId,os.getOrderId(), os.getTotalReplyCount() - os.getReplidCount());
		//记录赠送次数
		OrderFreeReplyCountRecord record = new OrderFreeReplyCountRecord();
		record.setCreateTime(System.currentTimeMillis());
		record.setFreeCount(count);
		record.setOrderId(os.getOrderId());
		record.setMessageGroupId(messageGroupId);
		replyCountRecordDao.insert(record);
		/**取消定时任务**/
		JobTaskUtil.cancelReplyCountEqZeroJob(os.getOrderId());
		List<ImgTextMsg> msgs = new ArrayList<>();
		ImgTextMsg imgTextMsg = new ImgTextMsg();
		imgTextMsg.setTitle("医生已赠送沟通次数");
		imgTextMsg.setContent("本次咨询含医生3次回复。为节省沟通次数，提问时请尽量一次性描述病情与问题。");
		imgTextMsg.setTime(System.currentTimeMillis());
		imgTextMsg.setStyle(7);
		msgs.add(imgTextMsg);
		businessServiceMsg.sendTextMsgToGid(SysUserEnum.SYS_001.getUserId(), messageGroupId, msgs, null);
	}

	@Override
	public OrderSessionContainer getOrderSessionWhenPaysuccess(Integer orderId) {
		int i=7;
		OrderSessionContainer osc = null;
		do{
			osc = orderSessionContainerDao.findByOrderId(orderId);
			if(osc == null)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
		}while (i-- > 0 && osc == null);
		if(osc == null)
			throw new ServiceException("不能根据订单id=("+orderId+")获取到会话");
		return osc;
	}

	@Override
	public void repairOldCareOrderSession() {
		List<Order> list = orderMapper.findAllCareOrder();
		for(Order order : list){
			OrderSessionContainer osc = orderSessionContainerDao.findByOrderId(order.getId());
			if(osc == null){
				OrderSession os = mapper.findByOrderId(order.getId());
				osc = new OrderSessionContainer();
				osc.setDoctorId(order.getDoctorId());
				osc.setUserId(order.getUserId());
				osc.setPatientId(order.getPatientId());
				osc.setOrderSessionId(os.getId());
				osc.setOrderId(order.getId());
				osc.setPackType(order.getPackType());
				osc.setSessionType(OrderEnum.OrderSessionCategory.care.getIndex());
				if(order.getOrderStatus() == 6)
					osc.setStatus(OrderStatus.已支付.getIndex());
				else
					osc.setStatus(order.getOrderStatus());
				osc.setMsgGroupId(os.getMsgGroupId());
				Pack p = packMapper.selectByPrimaryKey(order.getPackId());
				if(p != null){
					int totalReplyCount = p.getReplyCount() == null ? 0 : p.getReplyCount();
					osc.setTotalReplyCount(totalReplyCount);
				}else
					osc.setTotalReplyCount(0);
				osc.setReplidCount(0);
				orderSessionContainerDao.insert(osc);
			}
		}
	}

	public OrderSessionContainer getSessionContainer(String messageGroupId) {
		List<OrderSessionContainer> oss = orderSessionContainerDao.findByMsgGroupId(messageGroupId);
		if(oss == null)
			return null;
		if(oss.size() > 1){
			for(OrderSessionContainer os : oss){
				if(os.getPackType() != PackType.careTemplate.getIndex()){
					/**
					 * 表示如果是健康关怀会话里有图文/电话/积分订单
					 * 则在赠送的话次数要计算在图文/电话/积分订单 上面
					 */
					return os;
				}
			}
		}
		if(oss.size() == 1)
			return oss.get(0);
		return null;
	}
	
}
