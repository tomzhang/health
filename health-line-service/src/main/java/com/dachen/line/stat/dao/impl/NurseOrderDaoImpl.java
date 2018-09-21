package com.dachen.line.stat.dao.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.line.stat.comon.constant.ExceptionEnum;
import com.dachen.line.stat.dao.INurseOrderDao;
import com.dachen.line.stat.dao.IUserHospitalDao;
import com.dachen.line.stat.dao.IVSPTrackingDao;
import com.dachen.line.stat.dao.IVServiceProcessDao;
import com.dachen.line.stat.entity.vo.PatientOrder;
import com.dachen.line.stat.entity.vo.VSPTracking;
import com.dachen.line.stat.entity.vo.VServiceProcess;
import com.dachen.line.stat.util.ConfigUtil;
import com.dachen.line.stat.util.Constant;
import com.dachen.line.stat.util.DateUtils;
import com.dachen.line.stat.util.OutServiceHelper;
import com.dachen.util.MongodbUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class NurseOrderDaoImpl extends NoSqlRepository implements
		INurseOrderDao {

	@Autowired
	private IUserHospitalDao userHospitalDao;
	@Autowired
	private IVServiceProcessDao vServiceProcessDao;

	@Autowired
	private IVSPTrackingDao vspdao;
	@Override
	public List<PatientOrder> getUserOrder(List<String> hospitalList,
			List<String> departList, List<String> timeList,
			List<String> serviceList) {
		List<PatientOrder> result = null;
		List<ObjectId> orderIds = userHospitalDao.getUserHospitalStringList(
				"hospitalId in", hospitalList);
		if (null != orderIds && orderIds.size() > 0) {
			result = getPatientServiceOrderList(orderIds);//获取线下服务业务订单列表  
			result=  getRightServiceOrderList(result) ;//过滤基础订单状态必须是已经支付的订单
		}
		if(ConfigUtil.checkCollectionIsEmpty(result))
		{	
			//倒叙排列
			Collections.sort(result, new Comparator<PatientOrder>() {

				@Override
				public int compare(PatientOrder arg0, PatientOrder arg1) {
					long timeOne = arg0.getTime();
					long timeTwo = arg1.getTime();
					
					if(timeOne <timeTwo)
					{	
						return 1;
					}
					else if(timeOne >timeTwo)
					{	
						return -1;
					}
					return 0;
				}
			});
		}
		return result;
	}
	/**
	 * 获取业务订单
	 * @param orderIds
	 * @return
	 */
	@Override
	public List<PatientOrder> getPatientServiceOrderList(List<ObjectId> orderIds) {
		List<PatientOrder> result = new ArrayList<PatientOrder>();
		Query<PatientOrder> uq = dsForRW.createQuery(PatientOrder.class)
				.filter("id in ", orderIds);// 查询所有的正常的订单
		result = uq.asList();
		return result;
	}
	
	
	/**
	 * 这里
	 * @param serviceOrderList
	 * @return
	 */
	public List<PatientOrder>  getRightServiceOrderList(List<PatientOrder> serviceOrderList) {
		List<PatientOrder> result = new ArrayList<PatientOrder>();
		String  basicId=null;
		if(ConfigUtil.checkCollectionIsEmpty(serviceOrderList))
		{	
			Integer status=null;
			for(PatientOrder order:serviceOrderList)
			{
				if(null!=order)
				{
					basicId= order.getBasicId();
					if(StringUtils.isNotEmpty(basicId))
					{	
						try {
							status=	 OutServiceHelper.getOrderStatus(ReqUtil.instance.getToken(),basicId);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(null!=status&&OrderEnum.OrderStatus.已支付.getIndex()==status)
						{	
							result.add(order);
						}
					}
					
				}	
			}	
			
		}
		return result;
	}
	


	@Override
	public List<PatientOrder> getPatientOrderList(Integer status, Integer type,
			Integer userId) {
		List<PatientOrder> result = new ArrayList<PatientOrder>();
		Query<PatientOrder> uq = dsForRW.createQuery(PatientOrder.class);
		if (null != status) {
			uq = uq.filter("status", status);
		}
		if (null != type) {
			uq = uq.filter("type", type);
		}
		if (null != userId) {
			uq = uq.filter("userId", userId);
		}
		result = uq.asList();
		return result;
	}

	/**
	 * 查询制定字段条件下的下面的医院
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	@Override
	public PatientOrder getPatientOrderById(String orderId) {
		Query<PatientOrder> query = dsForRW.createQuery(PatientOrder.class)
				.field("_id").equal(new ObjectId(orderId));
		return query.get();

	}
	
	/**
	 * 查询制定字段条件下的下面的医院
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	@Override
	public PatientOrder getPatientOrderByCheckId(String checkId) {
		PatientOrder order =null;
		List<PatientOrder> orderList  = dsForRW.createQuery(PatientOrder.class).filter("checkId", checkId).asList();
		// 倒叙排列
		if(ConfigUtil.checkCollectionIsEmpty(orderList))
		{	
			Collections.sort(orderList, new Comparator<PatientOrder>() {
				@Override
				public int compare(PatientOrder arg0, PatientOrder arg1) {
					Long timeOne = arg0.getTime();
					Long timeTwo = arg1.getTime();

					if (timeOne.longValue() < timeTwo.longValue()) {
						return 1;
					} else if (timeOne.longValue() > timeTwo.longValue()) {
						return -1;
					}
					return 0;
				}
			});
			
			order =orderList.get(0);
		}
				
		return order;

	}

	/**
	 * 根据基础订单id查询对应的id
	 * 
	 * @param column
	 * @param sourceId
	 * @return
	 */
	@Override
	public PatientOrder getPatientBasicOrderById(String basicOrderId) {
		PatientOrder order = null;
		if(StringUtil.isNotEmpty(basicOrderId))
		{	
			Query<PatientOrder> query = dsForRW.createQuery(PatientOrder.class)
					.field("basicId").equal(basicOrderId);
			order = query.get();
		}
		
		return order;

	}

	/**
	 * 修改状态
	 */
	@Override
	public void updatePatientOrderBasicId(String orderId, String basicId) {
		Query<PatientOrder> result = dsForRW.createQuery(PatientOrder.class)
				.field("_id").equal(new ObjectId(orderId));
		PatientOrder process = result.get();
		if (null != process) {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", new ObjectId(orderId));

			BasicDBObject update = new BasicDBObject();
			update.put("basicId", basicId);
			if (!update.isEmpty()) {
				dsForRW.getDB().getCollection("v_order")
						.update(query, new BasicDBObject("$set", update));
			}
		} else {
			throw new ServiceException("订单不存在");
		}
	}

	/**
	 * 修改状态
	 */
	@Override
	public void updatePatientOrderStatus(String orderId, Integer status) {
		Query<PatientOrder> result = dsForRW.createQuery(PatientOrder.class)
				.field("_id").equal(new ObjectId(orderId));
		PatientOrder process = result.get();
		if (null != process) {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", new ObjectId(orderId));

			BasicDBObject update = new BasicDBObject();
			update.put("status", status);
			update.put("updTime", new Date().getTime());
			if (!update.isEmpty()) {
				dsForRW.getDB().getCollection("v_order")
						.update(query, new BasicDBObject("$set", update));
			}
		} else {
			throw new ServiceException("订单不存在");
		}
	}

	/**
	 * 修改状态
	 */
	@Override
	public void canclePatientOrderStatus(String orderId, Integer type) {
		Query<PatientOrder> result = dsForRW.createQuery(PatientOrder.class)
				.field("_id").equal(new ObjectId(orderId));
		PatientOrder process = result.get();
		if (null != process) {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", new ObjectId(orderId));

			BasicDBObject update = new BasicDBObject();
			update.put("type", type);
			update.put("updTime", new Date().getTime());
			if (!update.isEmpty()) {
				dsForRW.getDB().getCollection("v_order")
						.update(query, new BasicDBObject("$set", update));
			}
		} else {
			throw new ServiceException("订单不存在");
		}
	}

	@Override
	public void sendOrder(String orderId) {
		PatientOrder order = getPatientOrderById(orderId);
		if (null != order) {
			List<ObjectId> userIds = userHospitalDao.getUserHospitalStringList(
					"orderId ", orderId);
		}

	}

	@Override
	public Object insertOrder(PatientOrder order) {
		Key<PatientOrder> f = dsForRW.insert(order);
		Object id = f.getId();
		return id;
	}

	@Override
	public List<VSPTracking> getOderOvertopHour() {
		int time = Constant.MESSAGE_PATIENT_TIME*3600;
		List<VSPTracking> result = new ArrayList<VSPTracking>();
		Long value = 0l;//系统时间与日志创建时间只差
		boolean flag = true;
		//找出所有患者下单日志
		Query<VSPTracking> uq = dsForRW.createQuery(VSPTracking.class).filter("code", ExceptionEnum.Business_code_100.getIndex());
		if (uq.asList().size()>0) {
			for (VSPTracking vsp : uq) {
				//查询出所有的患者订单记录之后 还得去看患者此时有没有取消订单 所以才是得根据订单id来查一下所有与此订单有关的记录里有没有取消（900）订单的记录
				List<Integer> list = vspdao.getTrackListByOrderId(vsp.getOrderId());
				if(!list.contains(ExceptionEnum.Business_code_900.getIndex())){
					value = new Date().getTime() - vsp.getCreateTime();
					flag =value-time>0?true:false;
					Query<VSPTracking> uq1 = dsForRW.createQuery(VSPTracking.class).filter("code", ExceptionEnum.Business_code_100.getIndex()).where("function(){ if("
							+flag+"){" + "return true" + "}else{"
							+ "return false}}");
					if(uq1.get()!=null){
						result.add(uq1.get());
					}
				}
			}
		}
		return result;
	}

	@Override
	public List<VSPTracking> getBeforeExceptionOrder() throws ParseException {
		Long value = 0l;//系统时间与日志创建时间只差
		boolean flag = true;
		String t = Constant.PATIENT_ORDER_EXCEPTION_TIME();// 订单兜底时间距离凌晨的时间秒数
		List<VSPTracking> result = new ArrayList<VSPTracking>();
		String appointmentTime = "";
		Long seconds = null;
		//找出所有下单日志---流程记录还是以100为准
		Query<VSPTracking> uq = dsForRW.createQuery(VSPTracking.class).filter("code", ExceptionEnum.Business_code_100.getIndex());
		if (uq.asList().size()>0) {
			for (VSPTracking vsp : uq) {
				List<Integer> list = vspdao.getTrackListByOrderId(vsp.getOrderId());
				//得看看现在轮询的订单中有没有取消订单的记录
				if(!list.contains(ExceptionEnum.Business_code_900.getIndex())){
					appointmentTime = DateUtils.getSeconds2(vsp.getAppointmentTime());// 预约服务时间对应的秒数
					seconds = DateUtils.getAfterSecond(appointmentTime, t);//兜底时间对应的秒数
					value = Long.parseLong(appointmentTime)-new Date().getTime();
					//预约时间减去系统时间-减去设置的兜底值大于0说明已经过了兜底值
					flag =seconds-(new Date().getTime())>0?true:false;//兜底时间减去系统时间   为了测试 先改外大于 时间上是小于
					Query<VSPTracking> uq1 = dsForRW.createQuery(VSPTracking.class).filter("code", ExceptionEnum.Business_code_100.getIndex()).where("function(){ if("
							+flag+"){" + "return true" + "}else{"
							+ "return false}}");
					if(uq1.get()!=null){
						result.add(uq1.get());
					}
				}
			}
		}
		return result;
	}

	@Override
	public List<VSPTracking> getExceptionOrderNoEvaluate()
			throws ParseException {
		List<VSPTracking> result = new ArrayList<VSPTracking>();
		Long createTime = null;
		Long difference = null;//系统时间与创建时间的差值
		boolean flag= true;
		// 查询出所有护士已经结束服务的记录
		Query<VSPTracking> uq = dsForRW.createQuery(VSPTracking.class).filter("code", ExceptionEnum.Business_code_700.getIndex());
		if (uq.asList().size() > 0) {
			for (VSPTracking vp : uq) {
				createTime = vp.getCreateTime();// 订单服务结束时间
				difference = new Date().getTime()-createTime;
				//259200  3天对应的秒数
				flag = difference>259200?true:false;
				//查找出所有的已经结束服务但是没有进行评价的订单
				Query<VSPTracking> uq_ = dsForRW
						.createQuery(VSPTracking.class)
						.filter("code", ExceptionEnum.Business_code_700.getIndex())
						.where("function(){ if(" +flag+"){" + "return true" + "}else{"
								+ "return false}}");
				if (uq_.asList().size() > 0) {
					result.add(uq_.get());
				}
			}
		}
		return result;
	}

	@Override
	public String getPatientTelById(String orderId) {
		String patientTel = null;
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(orderId));

		DBObject field = new BasicDBObject();
		field.put("patientTel", 1);
		DBObject obj = dsForRW.getDB().getCollection("v_order")
				.findOne(query, field);
		if (obj != null) {
			patientTel = MongodbUtil.getString(obj, "patientTel");
		}
		return patientTel;
	}

	/**
	 * 根据订单id查询患者id
	 */
	public Integer getPatientIdById(String orderId) {

		Integer patientTel = null;
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(orderId));

		DBObject field = new BasicDBObject();
		field.put("patientId", 1);
		DBObject obj = dsForRW.getDB().getCollection("v_order")
				.findOne(query, field);
		if (obj != null) {
			patientTel = MongodbUtil.getInteger(obj, "patientId");
		}
		return patientTel;

	}
	
	/**
	 * 根据订单id查询检查单id
	 */
	public String getCheckIdIdById(String orderId) {

		String checkId = null;
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(orderId));

		DBObject field = new BasicDBObject();
		field.put("checkId", 1);
		DBObject obj = dsForRW.getDB().getCollection("v_order")
				.findOne(query, field);
		if (obj != null) {
			checkId = MongodbUtil.getString(obj, "checkId");
		}
		return checkId;
	}

	@Override
	public void updatePatientOrderTypeByBasicId(String basicOrderId,
			Integer type) {

		Query<PatientOrder> result = dsForRW.createQuery(PatientOrder.class)
				.field("basicId").equal(basicOrderId);
		PatientOrder process = result.get();
		if (null != process) {
			BasicDBObject query = new BasicDBObject();
			query.put("basicId", basicOrderId);

			BasicDBObject update = new BasicDBObject();
			update.put("type", type);
			update.put("updTime", new Date().getTime());
			if (!update.isEmpty()) {
				dsForRW.getDB().getCollection("v_order")
						.update(query, new BasicDBObject("$set", update));
			}
		} else {
			throw new ServiceException("订单不存在");
		}

	}

	@Override
	public List<VSPTracking> getExceptionOfNurseService()
			throws ParseException {
		List<VSPTracking> list = new ArrayList<VSPTracking>();
		Query<VSPTracking> uq = dsForRW.createQuery(VSPTracking.class).filter("code", ExceptionEnum.Business_code_500.getIndex());
		String appointmentTime = "";//预约时间
		boolean flag = true;
		if(uq.asList().size()>0){
			for (VSPTracking vp : uq) {
				appointmentTime = DateUtils.getSeconds2(vp.getAppointmentTime());
				flag = Long.parseLong(appointmentTime)-(vp.getCreateTime())>0?true:false;//说明创建时间在预约时间之前发生前
				//查找在预约时间之前就已经状态变为500的日志信息   预约时间-记录创建时间>说明就是在预约之前就已经点击了开始服务按钮
				Query<VSPTracking> uq_ = dsForRW.createQuery(VSPTracking.class)
						.filter("code", ExceptionEnum.Business_code_500.getIndex())
						.filter("_id", new ObjectId(vp.getId()))
						.where("function(){ if(" +flag+ "){" + "return true" + "}else{"
								+ "return false}}");
				list.add(uq_.get());
			}
		}
		return list;
	}

	@Override
	public List<VSPTracking> getExceptionOfNurseServiceNoClick() throws ParseException {
		List<VSPTracking> list = new ArrayList<VSPTracking>();
		List<String> id = new ArrayList<String>();
		
		return list;
	}
}
