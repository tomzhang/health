package com.dachen.health.pack.guide.dao;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.KeyBuilder;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.lock.RedisLock;
import com.dachen.commons.lock.RedisLock.LockType;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.pack.consult.dao.ElectronicIllCaseDao;
import com.dachen.health.pack.guide.entity.OrderCache;
import com.dachen.health.pack.guide.entity.PayStateEnum;
import com.dachen.health.pack.guide.entity.ServiceStateEnum;
import com.dachen.health.pack.guide.entity.po.ConsultOrderDoctorPO;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease;
import com.dachen.health.pack.guide.entity.po.CustomerPatientRecord;
import com.dachen.health.pack.guide.entity.po.OrderRelationPO;
import com.dachen.health.pack.guide.entity.vo.OrderDiseaseVO;
import com.dachen.health.pack.guide.util.GuideUtils;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.RedisUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class GuideDAOImpl extends NoSqlRepository implements IGuideDAO {
	
	/**
	 * 接单池(缓存所有的待接单数据：state = 未开始服务)
	 */
	public final static String GUIDE_ORDER_POOL =KeyBuilder.Z_ORDER_POOL+":guide"; 
	
	@Autowired
	ElectronicIllCaseDao electronicIllCaseDao;
	
	/**
	 * 缓存订单预约时间
	 * 1、当患者支付成功的时候，往此缓存增加数据：key：z_schedule_pool:guide；score：预约时间；member：guideId+":"+订单Id
	 * 2、删除对应的数据：key：z_schedule_pool:guide；score：预约时间；member：guideId+":"+订单Id(定时任务中删除)
	 */
//	public final static String Z_GUIDE_SCHEDULE =KeyBuilder.Z_SCHEDULE_POOL+":guide";
	
	public String addConsultOrder(ConsultOrderPO order)
	{
		if(order.getId()==null)
		{
			order.setId(StringUtils.getRandomString2(12));
		}
		String id = order.getId();
		String groupId = order.getGroupId();
		order.setCreateTime(System.currentTimeMillis());
		RedisLock lock=new RedisLock();
		boolean locked=lock.lock(groupId, LockType.order);
		if(!locked)
		{
			throw new ServiceException("下单失败：您正在其他途径下单。");
		}
		try{
			dsForRW.save(order);
			jedisTemplate.zadd(GUIDE_ORDER_POOL, order.getCreateTime(), id);
			this.updateGroupOrderCache(groupId, id);
		}
		catch(Exception e)
		{
			throw new ServiceException("下单失败");
		}
		finally
		{
			lock.unlock(groupId, LockType.order);
		}
		this.updateOrderCache(order);
		return id;
	}
	/**
	 * 修改病情资料
	 * @param param
	 * @return
	 */
	public ConsultOrderPO updateOrderDisease(OrderDiseaseVO param)
	{
		String id = param.getOrderId(); 
		Map<String,Object>updateValue = new HashMap<String,Object>();
		if(!com.dachen.util.StringUtil.isEmpty(param.getDiseaseDesc()))
		{
			updateValue.put("diseaseInfo.diseaseDesc", param.getDiseaseDesc());
		}
		if(param.getImgStringPath()!=null)//目前的要求是图片也可以全部删除 （图片可以传空）
		{
			updateValue.put("diseaseInfo.diseaseImgs", param.getImgStringPath());	
		}
		if(!com.dachen.util.StringUtil.isEmpty(param.getDiseaseInfo_now()))
		{
			updateValue.put("diseaseInfo.diseaseInfo_now", param.getDiseaseInfo_now());
		}
		if(!com.dachen.util.StringUtil.isEmpty(param.getDiseaseInfo_old()))
		{
			updateValue.put("diseaseInfo.diseaseInfo_old", param.getDiseaseInfo_old());
		}
		if(!com.dachen.util.StringUtil.isEmpty(param.getCureSituation()))
		{
			updateValue.put("diseaseInfo.cureSituation", param.getCureSituation());
		}
		if(!com.dachen.util.StringUtil.isEmpty(param.getMenstruationdiseaseInfo()))
		{
			updateValue.put("diseaseInfo.menstruationdiseaseInfo", param.getMenstruationdiseaseInfo());
		}
		if(!com.dachen.util.StringUtil.isEmpty(param.getFamilydiseaseInfo()))
		{
			updateValue.put("diseaseInfo.familydiseaseInfo", param.getFamilydiseaseInfo());
		}
		if(!com.dachen.util.StringUtil.isEmpty(param.getTelephone()))
		{
			updateValue.put("diseaseInfo.telephone", param.getTelephone());	
		}
		
		if(!com.dachen.util.StringUtil.isEmpty(param.getSeeDoctorMsg()))
		{
			updateValue.put("diseaseInfo.seeDoctorMsg", param.getCureSituation());	
		}
		if(!com.dachen.util.StringUtil.isEmpty(param.getIsSeeDoctor()))
		{
			String IsSee=param.getIsSeeDoctor();
			if("true".equals(IsSee)||"false".equals(IsSee))
			{	
				updateValue.put("diseaseInfo.isSeeDoctor", Boolean.valueOf(IsSee));	
			}
		}
		
		ConsultOrderPO po = updateConsultOrder(id,updateValue);
		
		/**
		 * 如果当前电话订单是转诊生成的订单 则修改病情时需要同步修改对应的电子病历（illcase） 
		 * 相对应修改电子病历也会同步修改对应的电话订单中的disease 
		 * 对应函数 com.dachen.health.pack.consult.Service.impl.ElectronicIllCaseServiceImpl.updatePhoneOrderDisease(String, String, String, List<String>)
		 */
		ConsultOrderPO potemp = dsForRW.createQuery(ConsultOrderPO.class).field("_id").equal(id).get();
		if(potemp != null && 
				potemp.getDiseaseInfo() != null && 
				org.apache.commons.lang3.StringUtils.isNotBlank(potemp.getDiseaseInfo().getIllCaseInfoId())){
			electronicIllCaseDao.syncPhoneOrderDiseaseToIllCase(potemp.getDiseaseInfo());
		}
		return po;
	}
	/**
	 * @param id
	 * @param newValue（key:propery,value:value）
	 */
	public ConsultOrderPO updateConsultOrder(String id,Map<String,Object>updateValue)
	{
		Query<ConsultOrderPO> q = dsForRW.createQuery(ConsultOrderPO.class).field("_id").equal(id);
//		ConsultOrderPO oldObj=q.get();
		UpdateOperations<ConsultOrderPO> ops = dsForRW.createUpdateOperations(ConsultOrderPO.class);
		for( Entry<String, Object> eachObj:updateValue.entrySet())
		{
			ops.set(eachObj.getKey(), eachObj.getValue());
		}
		ConsultOrderPO order = dsForRW.findAndModify(q, ops);
		if(updateValue.containsKey("state") 
				|| updateValue.containsKey("startTime") 
				|| updateValue.containsKey("guideId"))
		{
			updateOrderCache(order);
		}
		return order;
	}
	
	/**
	 * 获取会话对应的最新订单的状态
	 * @param groupId
	 * @return
	 */
	/* (non-Javadoc)
	 * @see com.dachen.health.pack.guide.dao.IGuideDAO#getOrderCache(java.lang.String)
	 */
	public OrderCache getOrderCache(String id){
		String orderKey = GuideUtils.genKey(KeyBuilder.H_ORDER_CACHE,id);
		Map<String, String> map = RedisUtil.hgetAll(orderKey);
		 
		if (map!=null  && !map.isEmpty()) {
			 OrderCache session = new OrderCache();
			 session.setId(map.get("id"));
			 String startTime = map.get("startTime");
			 if(startTime!=null && startTime.trim().length()>0)
			 {
				 session.setStartTime(Long.valueOf(startTime));
			 }
			 String state = map.get("state");
			 if(state!=null && state.trim().length()>0)
			 {
				 session.setState(ServiceStateEnum.getEnum(Integer.valueOf(state)));
			 }
			 String guide = map.get("guideId");
			 if(guide!=null && guide.trim().length()>0)
			 {
				 session.setGuideId(Integer.valueOf(guide));
			 }
			 return session;
		 }
		else
		{
			 ConsultOrderPO order = getConsultOrderPO(id);
			 if(order!=null)
			 {
				 updateOrderCache(order);
				 OrderCache session = new OrderCache();
				 session.setId(order.getId());
				 session.setStartTime(order.getStartTime());
				 session.setState(ServiceStateEnum.getEnum(order.getState()));
				 session.setGuideId(order.getGuideId());
				 return session;
			 }
		}
		return null;
	}
	
	public OrderCache getOrderCacheByGroup(String groupId)
	{
		OrderCache session = null;
		/**
		 * 首先获取会话对应的当前的订单Id
		 */
		 String groupKey = GuideUtils.genKey(KeyBuilder.S_ORDER_GROUP,groupId);
		 String id = jedisTemplate.get(groupKey);
		 
		 if(!StringUtils.isEmpty(id))
		 {
			 session = getOrderCache(id);
		 }
		 if(session==null)
		 {
			 //根据会话获取对应的订单，
			 ConsultOrderPO order = getOrderByGroup(groupId);
			 if(order!=null)
			 {
				 updateOrderCache(order);
				 session = new OrderCache();
				 session.setId(order.getId());
				 session.setStartTime(order.getStartTime());
				 session.setState(ServiceStateEnum.getEnum(order.getState()));
				 session.setGuideId(order.getGuideId());
			 }
		 }
		 return session;
	}
	
	private void updateGroupOrderCache(String groupId,String id)
	{
		if(groupId!=null && groupId.trim().length()>0){
			String groupKey = GuideUtils.genKey(KeyBuilder.S_ORDER_GROUP,groupId);
			jedisTemplate.set(groupKey,id);
		}
	}
	/**
	 * 缓存订单当前状态
	 * @param groupId
	 * @param state
	 * @return
	 */
	private void updateOrderCache(ConsultOrderPO order)
	{
		String id = order.getId();
		String orderKey = GuideUtils.genKey(KeyBuilder.H_ORDER_CACHE,id);
		Map<String,String> map = new HashMap<String,String>();
		map.put("id", id);
		if(order.getState()!=null)
		{
			ServiceStateEnum e = ServiceStateEnum.getEnum(order.getState());
			if(e!=null)
			{
				 map.put("state", String.valueOf(order.getState()));
			}
		}
	    if(order.getGuideId()!=null)
	    {
	    	map.put("guideId", String.valueOf(order.getGuideId()));
	    }
        if(order.getStartTime()!=null)
        {
        	map.put("startTime", String.valueOf(order.getStartTime()));
        }
        this.jedisTemplate.hmset(orderKey, map);
	}
	
	/**
	 * 更新咨询订单缓存
	 * @param id
	 * @param key
	 * @param value
	 */
	/*private void updateOrderCache(String id,String key,String value)
	{
		try {
			
			String orderKey = GuideUtils.genKey(ORDER_CACHE,id);
            Map<String,String> map = this.jedisTemplate.hgetAll(orderKey);
            if(map!=null && value!=null)
            {
            	map.put(key, value);
            	jedisTemplate.hmset(orderKey, map);
            }
		} finally {
		}
	}*/
	/**
	 * 获取患者所有咨询订单
	 */
	public List<ConsultOrderPO> getOrderByUser(int userId)
	{
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.field("userId").equal(userId)
				.order("-createTime");
		return uq.asList();
	}
	/**
	 * 获取导医接单记录(按接单时间倒排)
	 */
	public List<ConsultOrderPO> getOrderByGuide(Integer userId,Long time,Integer count)
	{
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.field("guideId").equal(userId);
		if(time!=null && time>0)
		{
			uq.field("startTime").lessThan(time);
		}
		uq.order("-startTime");
		if(count!=null && count>0)
		{
			uq.limit(count);
		}
		return uq.asList();
	}
	
	/**
	 * 获取预约时间（appointTime）在startTime和endTime之间的已支付订单
	 * @param userId 导医Id
	 * @param startTime 
	 * @param endTime
	 * @return
	 */
	public List<ConsultOrderPO> getHasPayOrderByGuide(Integer userId,long startTime,long endTime)
	{
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.field("payState").equal(PayStateEnum.HAS_PAY.getValue())//已支付
				.field("guideId").equal(userId);
		uq.field("appointTime").greaterThanOrEq(startTime);
		uq.field("appointTime").lessThanOrEq(endTime);
		//按照预约时间排序（升序）
		uq.order("appointTime");
		return uq.asList();
	}
	
	/**
	 * 查询患者是否存在未开始服务以及服务中的订单
	 * @param userId
	 * @return
	 */
	public boolean exist(int userId)
	{
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.filter("userId", userId)
				.filter("state in", new Integer[]{ServiceStateEnum.NO_START.getValue(),ServiceStateEnum.SERVING.getValue()});
		long count = uq.countAll();
		return count>0;
	}
	/**
	 * 根据会话获取患者所有未开始服务和服务中的咨询订单
	 */
	public ConsultOrderPO getOrderByGroup(String groupId)
	{
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.filter("groupId", groupId)
				.filter("state in", new Integer[]{ServiceStateEnum.NO_START.getValue(),ServiceStateEnum.SERVING.getValue()})
				.order("-createTime");
		 List<ConsultOrderPO> list = uq.asList();
		 if(list!=null && list.size()>0)
		 {
			 return list.get(0);
		 }
		return null;
	}
	
	public ConsultOrderPO getConsultOrderPO(String id)
	{
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.filter("_id", id);
		return uq.get();
	}
	public ConsultOrderPO getObjectByOrderId(Integer orderId)
	{
		/*Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.filter("orderId", orderId);
//				.filter("state in", new Integer[]{ServiceStateEnum.NO_START.getValue(),ServiceStateEnum.SERVING.getValue()})
//				.order("-createTime");
		return uq.get();*/
		
		Query<OrderRelationPO> uq=dsForRW.createQuery(OrderRelationPO.class)
				.filter("orderId", orderId);
		OrderRelationPO order = uq.get();
		if(order==null)
			return null;
		String guideOrderId = order.getGuideOrderId();
		
		Query<ConsultOrderPO> query=dsForRW.createQuery(ConsultOrderPO.class)
				.filter("_id", guideOrderId);
		
		ConsultOrderPO guideOrder = query.get();
		guideOrder.setAppointStartTime(order.getAppointStartTime());
		guideOrder.setAppointEndTime(order.getAppointEndTime());
		guideOrder.setDoctorId(order.getDoctorId());
		return guideOrder;
	}
	/**
	 * 获取导医正在服务中的订单数量
	 * @param guideId
	 * @return
	 */
	public long count(String guideId)
	{
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.filter("guideId", guideId)
				.filter("state", ServiceStateEnum.SERVING.getValue());
		
		return uq.countAll();
	}
	
	/**
	 * @param 
	 * @return
	 */
	public List<ConsultOrderPO> getOrderList(Set<String>ids,String groupId)
	{
		if(ids!=null && ids.size()>0)
		{
			Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class);
			uq.field("_id").in(ids);
			if(StringUtils.isNotEmpty(groupId)){
				uq.field("groupUnionId").equal(groupId);
			}
			else
			{
				uq.field("groupUnionId").equal(null);
			}
			return uq.asList();
		}
		return null;
	}
	
	public List<ConsultOrderPO> getTimeOutOrderList()
	{
		long currentTime = System.currentTimeMillis();
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.field("state").equal(ServiceStateEnum.SERVING.getValue()).order("createTime");//服务中
		uq.field("startTime").lessThanOrEq(currentTime - 24*60*60*1000L);
		//按照预约时间排序（升序）
		return uq.asList();
	}
	
	public List<ConsultOrderPO> getNotSendTimeOutOrderList()
	{
		long currentTime = System.currentTimeMillis();
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.field("state").equal(ServiceStateEnum.SERVING.getValue());//服务中
		uq.field("startTime").lessThanOrEq(currentTime - 15*60*1000L);
		uq.field("isSendOverTime").notEqual(true);
		//按照预约时间排序（升序）
		return uq.asList();
	}
	public List<ConsultOrderPO> getOrderListByGuide(Integer userId,Date startDate,Date endDate)
	{
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.field("guideId").equal(userId);
		if(startDate!=null)
		{
			uq.field("startTime").lessThanOrEq(startDate.getTime());
		}
		if(endDate!=null)
		{
			uq.field("startTime").greaterThanOrEq(endDate.getTime());
		}
		uq.order("-startTime");
		return uq.asList();
	}
	
	public Long getLastServiceTime(Integer userId)
	{
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class).field("guideId").equal(userId);
		uq.order("-startTime");
		uq.limit(1);
		
		List<ConsultOrderPO> list = uq.asList();
		if(list!=null && list.size()>0)
		{
			return list.get(0).getStartTime();
		}
		return null;
	}
	
	public void dataUpgrade()
	{
		dsForRW.ensureIndexes(OrderRelationPO.class);
		DBObject query = new BasicDBObject();
		query.put("orderId", new BasicDBObject("$gt",0));
		DBCollection collection = dsForRW.getDB().getCollection("t_consult_order");
		DBCursor cursor = collection.find(query);
		
		Integer orderId = null;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			orderId = MongodbUtil.getInteger(obj, "orderId");
			if(orderId==null)
			{
				continue;
			}
			 OrderRelationPO orderRelation = new OrderRelationPO();
			 orderRelation.setDoctorId(MongodbUtil.getInteger(obj, "doctorId"));
			 orderRelation.setOrderId(orderId);
			 orderRelation.setGuideOrderId(MongodbUtil.getString(obj, "_id"));
			 orderRelation.setAppointStartTime(MongodbUtil.getLong(obj, "appointTime"));
			 orderRelation.setAppointEndTime(MongodbUtil.getLong(obj, "endTime"));
			 dsForRW.save(orderRelation);
			 
			 DBObject values = new BasicDBObject();
			 values.put("orderId", orderId);
			 values.put("appointTime", MongodbUtil.getLong(obj, "appointTime"));
			 values.put("endTime", MongodbUtil.getLong(obj, "endTime"));
			 values.put("payState", MongodbUtil.getInteger(obj, "payState"));
			 values.put("price", MongodbUtil.getLong(obj, "price"));

			 DBObject q = new BasicDBObject("_id", MongodbUtil.getString(obj, "_id"));
			 DBObject o = new BasicDBObject("$unset", values);
			 dsForRW.getCollection(ConsultOrderPO.class).update(q, o);
		}
	}
	
	public void addOrderRelation(OrderRelationPO orderRelation)
	{
		dsForRW.save(orderRelation);
	}
	
	/**
	 * 根据导医订单会话，获取对应的所有订单
	 * @param guideOrderId
	 * @return
	 */
	public List<OrderRelationPO> getOrderIdList(String guideOrderId)
	{
		Query<OrderRelationPO> uq=dsForRW.createQuery(OrderRelationPO.class).field("guideOrderId").equal(guideOrderId);
		List<OrderRelationPO> list = uq.asList();
		return list;
	}
	@Override
	public ConsultOrderDoctorPO get2HourNoPay() {
		long currentTime = System.currentTimeMillis();
		Query<ConsultOrderDoctorPO> uq=dsForRW.createQuery(ConsultOrderDoctorPO.class)
				.field("status").equal(OrderEnum.OrderStatus.待支付.getIndex());//未支付
		uq.field("createTime").lessThanOrEq(currentTime -2*60*60*1000L).order("createTime");
		return uq.get();
	}
	@Override
	public ConsultOrderPO getConsultOrderPOAndState(String id) {
		Query<ConsultOrderPO> uq=dsForRW.createQuery(ConsultOrderPO.class)
				.filter("groupId", id).filter("state", 2);
		return uq.get();
	}
	@Override
	public OrderRelationPO getGuideIdByOrderId(Integer orderId) {
		Query<OrderRelationPO> uq=dsForRW.createQuery(OrderRelationPO.class).field("orderId").equal(orderId);
		return uq.get();
	}

	@Override
	public void syncDiseaseFromIllCase(Disease dis) {
		//Query<ConsultOrderPO> q = dsForRW.createQuery(ConsultOrderPO.class);
		DBObject q = new BasicDBObject("diseaseInfo.illCaseInfoId", dis.getIllCaseInfoId());
		//q.field("diseaseInfo.illCaseInfoId").equal(dis.getIllCaseInfoId());
		
		DBObject ops = new BasicDBObject();
		
		if(dis.getDiseaseDesc() != null){
			ops.put("diseaseInfo.diseaseDesc", dis.getDiseaseDesc());
		}
		if(dis.getDiseaseImgs() != null){
			ops.put("diseaseInfo.diseaseImgs", dis.getDiseaseImgs());
		}
		if(dis.getDiseaseInfo_now() != null){
			ops.put("diseaseInfo.diseaseInfo_now", dis.getDiseaseInfo_now());
		}
		if(dis.getDiseaseInfo_old() != null){
			ops.put("diseaseInfo.diseaseInfo_old", dis.getDiseaseInfo_old());
		}
		if(dis.getCureSituation() != null){
			ops.put("diseaseInfo.cureSituation", dis.getCureSituation());
		}
		if(dis.getMenstruationdiseaseInfo() != null){
			ops.put("diseaseInfo.menstruationdiseaseInfo", dis.getMenstruationdiseaseInfo());
		}
		if(dis.getFamilydiseaseInfo() != null){
			ops.put("diseaseInfo.familydiseaseInfo", dis.getFamilydiseaseInfo());
		}
		dsForRW.getDB().getCollection("t_consult_order").updateMulti(q , new BasicDBObject("$set", ops));
		
	}
	@Override
	public CustomerPatientRecord addCustomerPatientRecord(CustomerPatientRecord record) {
		dsForRW.insert(record);
		return record;
	}

	@Override
	public void updateCustomerPatientRecord(CustomerPatientRecord record) {
		Query<CustomerPatientRecord> q = dsForRW.createQuery(CustomerPatientRecord.class)
				.field("customerId").equal(record.getCustomerId())
				.field("patientUserId").equal(record.getPatientUserId())
				.field("status").equal(CustomerPatientRecord.SERVICE_ING);
		UpdateOperations<CustomerPatientRecord> ops = dsForRW.createUpdateOperations(CustomerPatientRecord.class);
		ops.set("finishTime", System.currentTimeMillis());
		ops.set("status", CustomerPatientRecord.SERVICE_END);
		dsForRW.updateFirst(q, ops);
	}
	@Override
	public List<Object> getCustomerWorkDate(Long dateTime) {
		Query<CustomerPatientRecord> q = dsForRW.createQuery(CustomerPatientRecord.class);
			long before = 0;
			long after = 0;
			q.field("customerId").equal(ReqUtil.instance.getUserId());
			after = dateTime;
			before = after - DateUtil.halfmonthmillSeconds;
			q.field("dateTime").greaterThanOrEq(before);
			q.field("dateTime").lessThan(after);
			
			q.field("status").equal(CustomerPatientRecord.SERVICE_END);
			q.order("-dateTime");
			
		String reduce = "function (doc,aggr){"
				 +	      "aggr.dayCount += 1 ;"
				 +      "}";
		DBObject keys = new BasicDBObject("dateTime", 1);
		
		DBObject group = new BasicDBObject();
		group.put("dayCount", 0);
		
		DBObject result = dsForRW.getCollection(CustomerPatientRecord.class)
						.group(keys,
								q.getQueryObject(),
								group,
								reduce);
		Map<String,BasicDBObject> map = result.toMap();
		if(map.size() > 0){
			return map.values().stream().sorted((o1,o2) -> {
				Long l1 = MongodbUtil.getLong(o1, "dateTime");
				Long l2 = MongodbUtil.getLong(o2, "dateTime");
				if(Objects.isNull(l1) || Objects.isNull(l2))
					return 1;
				else
					return Long.valueOf(l2 - l1).intValue();
			}).collect(Collectors.toList());
		}
		return Collections.EMPTY_LIST;
	}
	
	@Override
	public long getDayRecordsCount(Long dateTime) {
		Query<CustomerPatientRecord> q = dsForRW.createQuery(CustomerPatientRecord.class);
			q.field("customerId").equal(ReqUtil.instance.getUserId());
			q.field("status").equal(CustomerPatientRecord.SERVICE_END);
			q.field("dateTime").equal(dateTime);
		return q.countAll();
	}
	
	@Override
	public List<CustomerPatientRecord> getDayRecords(Long dateTime, Integer pageIndex, Integer pageSize) {
		Query<CustomerPatientRecord> q = dsForRW.createQuery(CustomerPatientRecord.class);
		q.field("customerId").equal(ReqUtil.instance.getUserId());
		q.field("status").equal(CustomerPatientRecord.SERVICE_END);
		q.field("dateTime").equal(dateTime);
		q.order("-startTime");
		q.offset(pageIndex * pageSize);
		q.limit(pageSize);
		return q.asList();
	}
	
	@Override
	public long getCustomerWorkDateTotal() {
		Query<CustomerPatientRecord> q = dsForRW.createQuery(CustomerPatientRecord.class);
		q.field("customerId").equal(ReqUtil.instance.getUserId());
		q.field("status").equal(CustomerPatientRecord.SERVICE_END);
		q.order("-dateTime");
		/**
		 * 如果mongodb中正常插入的数据
		 * dateTime 肯定是从小到大排列的数据
		 * 一个客服同一天的数据在mongodb中是连续排列的 (默认逻辑)
		 * 所以该处选择dateTime 作为临时变量来判断：
		 * (当dateTime 变化的时候同一天的数据已经完全统计完成)
		 */
		String reduce = "function (doc,aggr){"
				 +	      "if(aggr.dateTime+'' != doc.dateTime+''){"
				 +	      	  "aggr.total += 1 ;"
				 +	      	  "aggr.dateTime = doc.dateTime ;"
				 +         "}"
				 +      "}";
		DBObject keys = new BasicDBObject();
		
		DBObject group = new BasicDBObject();
		group.put("total", 0);
		group.put("dateTime", 0);
		
		DBObject result = dsForRW.getCollection(CustomerPatientRecord.class)
						.group(keys,
								q.getQueryObject(),
								group,
								reduce);
		Map<String,BasicDBObject> map = result.toMap();
		if(map.size() > 0){
			Long l = MongodbUtil.getDouble(map.values().iterator().next(), "total").longValue(); 
			return l == null ? 0 : l; 
		}
		return 0;
	}

	@Override
	public long getFirstWorkDataTime() {
		Query<CustomerPatientRecord> q = dsForRW.createQuery(CustomerPatientRecord.class);
		q.field("customerId").equal(ReqUtil.instance.getUserId());
		q.field("status").equal(CustomerPatientRecord.SERVICE_END);
		q.order("dateTime");
		q.retrievedFields(true, "dateTime");
		CustomerPatientRecord record = q.get();
		if(Objects.isNull(record))
			return Long.MAX_VALUE;
		return record.getDateTime();
	}
	
	@Override
	public Set<String> findAll() {
		Query<ConsultOrderPO> q = dsForRW.createQuery(ConsultOrderPO.class);
		q.retrievedFields(true, "userId");
		if(q.countAll() < 1)
			return Collections.EMPTY_SET;
		return q.asList().stream().map(o -> String.valueOf(o.getUserId())).collect(Collectors.toSet());
	}

	@Override
	public List<CustomerPatientRecord> getRecordsByUserId(Integer userId) {
		Query<CustomerPatientRecord> q = dsForRW.createQuery(CustomerPatientRecord.class);
		q.field("customerId").equal(userId);
		q.field("status").equal(CustomerPatientRecord.SERVICE_ING);
		return q.asList();
	}

}
