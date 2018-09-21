package com.dachen.health.pack.conference.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.constant.CallEnum;
import com.dachen.health.pack.conference.dao.CallRecordRepository;
import com.dachen.health.pack.conference.entity.param.CallRecordParam;
import com.dachen.health.pack.conference.entity.param.ConfDetailParam;
import com.dachen.health.pack.conference.entity.po.CallRecord;
import com.dachen.health.pack.conference.entity.po.ConfCall;
import com.dachen.health.pack.conference.entity.vo.CallRecordVO;
import com.dachen.health.pack.conference.entity.vo.ConfDetailVO;
import com.dachen.util.BeanUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository
public class CallRecordRepositoryImpl extends NoSqlRepository implements CallRecordRepository {

	@Override
	public CallRecord saveCallRecord(CallRecord param) {
//		DBObject saveDate = new BasicDBObject();
//		saveDate.put("callType", param.getCallType());
//		saveDate.put("orderId", param.getOrderId());
//		saveDate.put("createTime", param.getCreateTime());
//		
//		if(param.getCallType() == CallEnum.CallType.conference.getIndex()){
//			Map<String,Object> confCall = new HashMap<String,Object>();
//			confCall.put("confId", param.getConfCall().getConfId());
//			confCall.put("creater", param.getConfCall().getCreater());
//			confCall.put("playTone", param.getConfCall().getPlayTone());
//			confCall.put("maxMember", param.getConfCall().getMaxMember());
//			confCall.put("mediaType", param.getConfCall().getMediaType());
//			confCall.put("duration", param.getConfCall().getDuration());
//			confCall.put("result", param.getConfCall().getResult());
//			saveDate.put("confCall", confCall);
//		}else{
//			saveDate.put("from", param.getFrom());
//			saveDate.put("to", param.getTo());
//			saveDate.put("callid", param.getCallid());
//		}
//		dsForRW.getDB().getCollection("t_call_record").save(saveDate);
////		DBObject obj = dsForRW.getDB().getCollection("t_call_record").findOne(saveDate);
//		Query<CallRecord> query = dsForRW.createQuery(CallRecord.class).field("confCall.confId")
//				.equal(param.getConfCall().getConfId()).field("createTime").equal(param.getCreateTime());
//		CallRecord cr = query.get();
//		CallRecordVO crVO = null;
//		if(cr != null){
//			crVO = BeanUtil.copy(cr, CallRecordVO.class);
//		}
//		if(crVO != null){
//			crVO.setId(cr.getId().toString());
//		}
//		return crVO;
//		return setCRField(obj);
		String id = dsForRW.insert(param).getId().toString();
		return dsForRW.createQuery(CallRecord.class).field("_id").equal(new ObjectId(id)).get();
	}

	@Override
	public CallRecordVO getCallRecordById(String id) {
//		DBObject query = new  BasicDBObject();
//		query.put("_id", new ObjectId(id));
//		DBObject obj = dsForRW.getDB().getCollection("t_call_record").findOne(query);
		Query<CallRecord> query = dsForRW.createQuery(CallRecord.class).field("_id")
				.equal(new ObjectId(id));
//		return setCRField(obj);
		CallRecord cr = query.get();
		CallRecordVO crVO = null;
		if(cr != null){
			crVO = BeanUtil.copy(cr, CallRecordVO.class);
		}
		if(crVO != null){
			crVO.setId(cr.getId().toString());
		}
		return crVO;
	}
	@Override
	public CallRecordVO getCallRecordByConfId(String confId) {
//		DBObject query = new  BasicDBObject();
//		query.put("confCall.confId", confId);
//		DBObject obj = dsForRW.getDB().getCollection("t_call_record").findOne(query);
		
		Query<CallRecord> query = dsForRW.createQuery(CallRecord.class).field("confCall.confId")
				.equal(confId);
		CallRecord cr = query.get();
		CallRecordVO crVO = null;
		if(cr != null){
			crVO = BeanUtil.copy(cr, CallRecordVO.class);
		}
		if(crVO != null){
			crVO.setId(cr.getId().toString());
		}
		return crVO;
	}
	
	@Override
	public void updateCallRecordByConfId(CallRecordParam param) {
		DBObject update = new BasicDBObject();
		if(param.getEndTime() != 0){
			update.put("endTime", param.getEndTime());
		}
		if(param.getConfCall().getResult() != null){
			update.put("confCall.result", param.getConfCall().getResult());
		}
		if(param.getRecordStatus()!=null)
		{
			update.put("recordStatus", param.getRecordStatus());	
		}
		dsForRW.getDB().getCollection("t_call_record").update(new BasicDBObject("confCall.confId", param.getConfCall().getConfId()),new BasicDBObject("$set",update),false,false);
	}
	
	@Override
	public void updateRecordStatusByConfId(CallRecordParam param) {
		DBObject update = new BasicDBObject("confCall.result",param.getConfCall().getStatus());
		if(!StringUtil.isEmpty(param.getRecordUrl())){
			update.put("recordUrl", param.getRecordUrl());
			update.put("recordId", param.getRecordId());
		}
		dsForRW.getDB().getCollection("t_call_record").update(new BasicDBObject("confCall.confId", param.getConfCall().getConfId()),new BasicDBObject("$set",update),false,false);
	}
	
	@Override
	public void saveConfDetail(ConfDetailParam param) {
		DBObject saveDate = new BasicDBObject();
		saveDate.put("crId", param.getCrId());
		saveDate.put("memberId", param.getMemberId());
		saveDate.put("telephone", param.getTelephone());
		saveDate.put("callId", param.getCallId());
		saveDate.put("role", param.getRole());
		saveDate.put("status", param.getStatus());
		saveDate.put("isNow", true);//每次拨打后，就会何存一条纪录是当前最新纪录
		saveDate.put("joinTime", param.getJoinTime());
		dsForRW.getDB().getCollection("t_conf_detail").save(saveDate);
	}
	
	@Override
	public boolean updateConfDetailIsNow(ConfDetailParam param){
		boolean result = false;
		DBObject query = new  BasicDBObject();
		query.put("crId", param.getCrId());
		query.put("memberId", param.getMemberId());
		
		WriteResult wr = dsForRW.getDB().getCollection("t_conf_detail").update(query,  new BasicDBObject("$set", new BasicDBObject("isNow", param.getIsNow())),false,true);
		if(wr.getN()>0){
			result = true;
		}
		return result;
	}
	@Override
	public void endConfDetailUpdate(ConfDetailParam param){
		DBObject query = new  BasicDBObject();
		query.put("crId", param.getCrId());
		if(!StringUtil.isEmpty(param.getMemberId())){
			query.put("memberId", param.getMemberId());
		}
		if(!StringUtil.isEmpty(param.getTelephone())){
			query.put("telephone", param.getTelephone());
		}
		query.put("isNow", true);
		DBObject update = new BasicDBObject();
		update.put("status", param.getStatus());
		if(!StringUtil.isEmpty(param.getCallId())){
			update.put("callId", param.getCallId());
		}
		if(param.getUnJoinTime() != 0 ){
			update.put("unJoinTime", param.getUnJoinTime());
		}
		dsForRW.getDB().getCollection("t_conf_detail").update(query, new BasicDBObject("$set", update),false,true);
	}
	@Override
	public void endUnJoinUpdate(ConfDetailParam param){
		DBObject query = new  BasicDBObject();
		query.put("crId", param.getCrId());
		if(!StringUtil.isEmpty(param.getMemberId())){
			query.put("memberId", param.getMemberId());
		}
		if(!StringUtil.isEmpty(param.getCallId())){
			query.put("callId", param.getCallId());
		}
		query.put("isNow", true);
		DBObject update = new BasicDBObject();
		update.put("status", param.getStatus());
		if(!StringUtil.isEmpty(param.getCallId())){
			update.put("callId", param.getCallId());
		}
		if(param.getUnJoinTime() != 0 ){
			update.put("unJoinTime", param.getUnJoinTime());
		}
		dsForRW.getDB().getCollection("t_conf_detail").update(query, new BasicDBObject("$set", update),false,true);
	}
	
	@Override
	public ConfDetailVO getConfDetailNow(ConfDetailParam param){
		DBObject query = new  BasicDBObject();
		query.put("crId", param.getCrId());
		if(!StringUtil.isEmpty(param.getMemberId())){
			query.put("memberId", param.getMemberId());
		}
		if(!StringUtil.isEmpty(param.getTelephone())){
			query.put("telephone", param.getTelephone());
		}
		if(!StringUtil.isEmpty(param.getCallId())){
			query.put("callId", param.getCallId());
		}
		query.put("isNow", true);
		DBObject obj = dsForRW.getDB().getCollection("t_conf_detail").findOne(query);
		return setCDField(obj);
	}
	@Override
	public ConfDetailVO getConfDtailNowByRole(ConfDetailParam param){
		ConfDetailVO vo = null;
		if(param.getRole() == null){
			return vo;
		}
		DBObject query = new  BasicDBObject();
		query.put("crId", param.getCrId());
		query.put("isNow", true);
		query.put("role", param.getRole());
		DBObject obj = dsForRW.getDB().getCollection("t_conf_detail").findOne(query);
		vo = setCDField(obj);
		return vo;
	}
	@Override
	public List<ConfDetailVO> getConfDetailsByParam(ConfDetailParam param){
		List<ConfDetailVO> list = new ArrayList<ConfDetailVO>();
		DBObject query = new  BasicDBObject();
		query.put("crId", param.getCrId());
		if(param.getIsNow() != null){
			query.put("isNow", param.getIsNow());
		}
		DBCursor cursor = dsForRW.getDB().getCollection("t_conf_detail").find(query);
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			list.add(setCDField(obj));
		}
		cursor.close();
		return list;
	}
	@Override
	public List<CallRecordVO> getCallRecordByOrderId(CallRecordParam param){
		List<CallRecordVO> list = new ArrayList<CallRecordVO>();
		if(param.getOrderId() == null){
			return list;
		}
		DBObject query = new  BasicDBObject();
		query.put("orderId", param.getOrderId());
		if(param.getRecordStatus()!=null)
		{
			query.put("recordStatus",param.getRecordStatus());
		}
		DBCursor cursor = dsForRW.getDB().getCollection("t_call_record").find(query).sort(new BasicDBObject("createTime", -1));
		CallRecordVO vo = null;
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			vo = setCRField(obj);
			list.add(vo);
		}
		cursor.close();
		return list;
	}
	@Override
	public List<CallRecordVO> getConferenceInServiceByGuide(CallRecordParam param) {
		List<CallRecordVO> list = new ArrayList<CallRecordVO>();
		DBObject query = new BasicDBObject();
		if(param.getDuration() != null){
			long time = new Date().getTime();
			time = time - param.getDuration()*60*1000;
			query.put("createTime", new BasicDBObject("$gt", time));
		}
		query.put("confCall.result", new BasicDBObject("$lt", CallEnum.ConferenceStatus.overBydefault.getIndex()));
		
		if(!StringUtil.isEmpty(param.getConfCall().getCreater())){
			query.put("confCall.creater", param.getConfCall().getCreater());
		}
		DBCursor cursor = dsForRW.getDB().getCollection("t_call_record").find(query).sort(new BasicDBObject("createTime", -1));
		CallRecordVO vo = null;
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			vo = setCRField(obj);
			if(vo != null){
				list.add(vo);
			}
		}
		cursor.close();
		return list;
	}
	@Override
	public Map<String ,List<ConfDetailVO>> getConfDetailByCrId(CallRecordParam param){
		Map<String ,List<ConfDetailVO>> map = new LinkedHashMap<String ,List<ConfDetailVO>>();
		if(param == null  ){
			return map;
		}
		DBObject query = new  BasicDBObject();
		if(!StringUtil.isEmpty(param.getId())){
			query.put("crId", param.getId());
		}
		if(param.getEndTime() != 0){
			query.put("unJoinTime", new BasicDBObject("$gte", param.getEndTime()));
		}
		DBCursor cursor = dsForRW.getDB().getCollection("t_conf_detail").find(query).sort(new BasicDBObject("joinTime", -1));
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			ConfDetailVO vo = setCDField(obj);
			List<ConfDetailVO> cdList = map.get(vo.getCrId());
			if(cdList == null){
				cdList = new ArrayList<ConfDetailVO>();
			}
			cdList.add(vo);
			map.put(vo.getCrId(), cdList);
		}
		cursor.close();
		return map;
	}
	private CallRecordVO setCRField(DBObject obj ){
		if(obj == null){
			return null;
		}
		CallRecordVO vo = new CallRecordVO();
		vo.setId(MongodbUtil.getString(obj, "_id"));
		int  type = MongodbUtil.getInteger(obj, "callType");
		vo.setCallType(type);
		vo.setOrderId(MongodbUtil.getInteger(obj, "orderId"));
		vo.setCreateTime(MongodbUtil.getLong(obj, "createTime"));
		vo.setRecordUrl(MongodbUtil.getString(obj, "recordUrl"));
		vo.setRecordId(MongodbUtil.getString(obj, "recordId"));
		vo.setVideoUrl(MongodbUtil.getString(obj, "videoUrl"));
		vo.setRecordStatus(MongodbUtil.getInteger(obj, "recordStatus"));
		if(MongodbUtil.getLong(obj, "endTime") != null){
			vo.setEndTime(MongodbUtil.getLong(obj, "endTime"));
		}else{
			vo.setEndTime(-1);
		}
		if(type == CallEnum.CallType.conference.getIndex()){
			ConfCall cc = new ConfCall();
			Map<String,Object> map = (Map<String, Object>) obj.get("confCall");
			cc.setConfId(map.get("confId").toString());
			cc.setCreater(map.get("creater").toString());
			cc.setDuration(Integer.parseInt(map.get("duration").toString()));
			cc.setMaxMember(Integer.parseInt(map.get("maxMember").toString()));
			cc.setMediaType(Integer.parseInt(map.get("mediaType").toString()));
			cc.setPlayTone(Integer.parseInt(map.get("playTone").toString()));
			cc.setResult(Integer.parseInt(map.get("result").toString()));
			vo.setConfCall(cc);
		}else{
			vo.setFrom(MongodbUtil.getString(obj, "from"));
			vo.setTo(MongodbUtil.getString(obj, "to"));
		}
		return vo;
	}
	
	public ConfDetailVO setCDField(DBObject obj ){
		if(obj == null){
			return null;
		}
		ConfDetailVO vo = new ConfDetailVO();
		vo.setCallId(MongodbUtil.getString(obj, "callId"));
		vo.setCrId(MongodbUtil.getString(obj, "crId"));
		vo.setId(MongodbUtil.getString(obj, "_id"));
		String now = MongodbUtil.getString(obj, "isNow");
		Boolean isNow = now == null?false : Boolean.valueOf(now);
		vo.setIsNow(isNow);
		vo.setJoinTime(MongodbUtil.getLong(obj, "joinTime"));
		vo.setMemberId(MongodbUtil.getString(obj, "memberId"));
		vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
		vo.setRole(MongodbUtil.getInteger(obj, "role"));
		vo.setStatus(MongodbUtil.getInteger(obj, "status"));
		if(MongodbUtil.getLong(obj, "unJoinTime") !=null){
			vo.setUnJoinTime(MongodbUtil.getLong(obj, "unJoinTime"));
		}
		return vo;
	}

	@Override
	public CallRecordVO getConference(CallRecordParam param) {
		DBObject query = new BasicDBObject();
		if(param.getOrderId() !=null){
			query.put("orderId", param.getOrderId());
		}
		if(param.getDuration() != null){
			long time = new Date().getTime();
			time = time - param.getDuration()*60*1000;
			query.put("createTime", new BasicDBObject("$gt", time));
		}
		if(param.getConfCall() != null ){
			if( !StringUtil.isEmpty(param.getConfCall().getCreater())){
				query.put("confCall.creater", param.getConfCall().getCreater());
			}
			if(param.getConfCall().getResult() != null){
				query.put("confCall.result", new BasicDBObject("$lt", param.getConfCall().getResult()));
			}
		}
		
		DBCursor cursor = dsForRW.getDB().getCollection("t_call_record").find(query).sort(new BasicDBObject("createTime", -1));
		CallRecordVO vo = new CallRecordVO();
		while(cursor.hasNext()){
			DBObject obj = cursor.next();	
			vo = setCRField(obj);
			break;
		}
		cursor.close();
		return vo;
	}

	@Override
	public List<CallRecordVO> getCallToSendNotice(CallRecordParam param) {
		List<CallRecordVO> list = new ArrayList<CallRecordVO>();
		DBObject query = new BasicDBObject();
		query.put("confCall.result", new BasicDBObject("$gte", param.getConfCall().getResult()));
		query.put("endTime", new BasicDBObject("$gte", param.getEndTime()));
		CallRecordVO vo = null;
		DBCursor cursor = dsForRW.getDB().getCollection("t_call_record").find(query).sort(new BasicDBObject("createTime", -1));
		while(cursor.hasNext()){
			DBObject object = cursor.next();
			vo = setCRField(object);
			if(vo != null){
				list.add(vo);
			}
		}
		cursor.close();
		return list;
	}

	@Override
	public List<Integer> get8HourOrder() {
		List<Integer> list = new ArrayList<Integer>();
		long currentTime = System.currentTimeMillis();
		//首先过滤出所有的通话结束时间超出8小时的记录
		//Query<CallRecord> uq=dsForRW.createQuery(CallRecord.class).field("endTime").greaterThanOrEq(currentTime -8*60*60*1000L).order("-endTime");
        Query<CallRecord> uq=dsForRW.createQuery(CallRecord.class).field("endTime").lessThanOrEq(currentTime -10*60*1000L).order("-endTime");
        for (CallRecord record : uq.asList()) {
        	if(!list.contains(record.getOrderId())){
        		list.add(record.getOrderId());
        	}
		}
        return list;
	}

	@Override
	public List<CallRecordVO> getVoiceByOrderId(Integer orderId) {
		 Query<CallRecord> uq=dsForRW.createQuery(CallRecord.class).filter("orderId", orderId).order("-endTime");
		 List<CallRecordVO> V  =new ArrayList<CallRecordVO>();
		 for (CallRecord callRecord : uq) {
			 CallRecordVO vo = new CallRecordVO();
			 vo.setCallType(callRecord.getCallType());
			 vo.setConfCall(callRecord.getConfCall());
			 vo.setCreateTime(callRecord.getCreateTime());
			 vo.setEndTime(callRecord.getEndTime());
			 vo.setRecordUrl(callRecord.getRecordUrl());
			 vo.setId(callRecord.getId().toString());
			 V.add(vo);
		}
		return V;
	}

	@Override
	public boolean updateCallRecordByCallid(CallRecord param) {
		if(StringUtil.isEmpty(param.getCallid())){
			return false;
		}
		Query<CallRecord> query = dsForRW.createQuery(CallRecord.class).filter("callid", param.getCallid());
		UpdateOperations<CallRecord> ops = dsForRW.createUpdateOperations(CallRecord.class);
		if(param.getOrderId() != null){
			ops.set("orderId", param.getOrderId());
		}
		if(param.getCallType() != null){
			ops.set("callType", param.getCallType());
		}
		if(param.getCreateTime() != 0){
			ops.set("createTime", param.getCreateTime());
		}	
		if(param.getEndTime() != 0){
			ops.set("endTime", param.getEndTime());
		}
		if(!StringUtil.isEmpty(param.getFrom())){
			ops.set("from", param.getFrom());
		}
		if(!StringUtil.isEmpty(param.getTo())){
			ops.set("to", param.getTo());
		}
		if(!StringUtil.isEmpty(param.getRecordUrl())){
			ops.set("recordUrl", param.getRecordUrl());
		}
		if(!StringUtil.isEmpty(param.getVideoUrl())){
			ops.set("videoUrl", param.getVideoUrl());
		}
		if(!StringUtil.isEmpty(param.getRecordId())){
			ops.set("recordId", param.getRecordId());
		}
		CallRecord record = dsForRW.findAndModify(query, ops, false, true);
		
		return true;
	}

	@Override
	public CallRecord getCallVOByCallid(String callid) {
		if(StringUtil.isEmpty(callid)){
			return null;
		}
		return  dsForRW.createQuery(CallRecord.class).filter("callid", callid).get();
	}

	@Override
	public List<ConfDetailVO> getDetailList(ConfDetailParam param) {
		List<ConfDetailVO> list = new ArrayList<ConfDetailVO>();
		DBObject query = new  BasicDBObject();
		query.put("crId", param.getCrId());
		query.put("memberId", param.getMemberId());
		DBCursor cursor = dsForRW.getDB().getCollection("t_conf_detail").find(query);
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			list.add(setCDField(obj));
		}
		cursor.close();
		return list;
	}
	
	
}
