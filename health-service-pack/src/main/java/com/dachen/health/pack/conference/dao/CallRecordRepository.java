package com.dachen.health.pack.conference.dao;

import java.util.List;
import java.util.Map;

import com.dachen.health.pack.conference.entity.param.CallRecordParam;
import com.dachen.health.pack.conference.entity.param.ConfDetailParam;
import com.dachen.health.pack.conference.entity.po.CallRecord;
import com.dachen.health.pack.conference.entity.vo.CallRecordVO;
import com.dachen.health.pack.conference.entity.vo.ConfDetailVO;

public interface CallRecordRepository {

	CallRecord saveCallRecord(CallRecord param);
	
	CallRecordVO getCallRecordById(String id);
	
	CallRecordVO getCallRecordByConfId(String confId);
	
	CallRecordVO getConference(CallRecordParam param);
	
	void updateCallRecordByConfId(CallRecordParam param);
	
	void updateRecordStatusByConfId(CallRecordParam param);
	
	void saveConfDetail(ConfDetailParam param);
	
	boolean updateConfDetailIsNow(ConfDetailParam param);
	
	void endConfDetailUpdate(ConfDetailParam param);
	
	void endUnJoinUpdate(ConfDetailParam param);
	
	ConfDetailVO getConfDetailNow(ConfDetailParam param);
	
	ConfDetailVO getConfDtailNowByRole(ConfDetailParam param);
	
	List<ConfDetailVO> getConfDetailsByParam(ConfDetailParam param);
	
	List<CallRecordVO> getCallRecordByOrderId(CallRecordParam param);
	
	List<CallRecordVO> getConferenceInServiceByGuide(CallRecordParam param);
	
	Map<String ,List<ConfDetailVO>> getConfDetailByCrId(CallRecordParam param);
	
	List<CallRecordVO> getCallToSendNotice(CallRecordParam param);
	
	List<CallRecordVO> getVoiceByOrderId(Integer orderId);
	
	/**
	 * 获取通话结束超过8小时的所有订单
	 * @return
	 */
	List<Integer> get8HourOrder();
	
	/**
	 * 根据callid
	 * 更新双向回拨
	 * @param param callid
	 * @return
	 */
	boolean updateCallRecordByCallid(CallRecord param);
	
	CallRecord getCallVOByCallid(String callid);
	
	/**
	 * 根据用户获取所有通话纪录
	 * @param param
	 * @return
	 */
	List<ConfDetailVO> getDetailList(ConfDetailParam param);
	
}
