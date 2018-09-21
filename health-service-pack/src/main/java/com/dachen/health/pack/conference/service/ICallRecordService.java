package com.dachen.health.pack.conference.service;

import java.util.List;
import java.util.Map;

import com.dachen.health.pack.conference.entity.param.CallRecordParam;
import com.dachen.health.pack.conference.entity.vo.CallBusinessVO;
import com.dachen.health.pack.conference.entity.vo.CallRecordVO;
import com.dachen.health.pack.conference.entity.vo.ConfListVO;
import com.dachen.sdk.exception.HttpApiException;
import com.mongodb.DBObject;

public interface ICallRecordService {
	
	/**
	 * 创建会议。 会议在持续duration分钟后自动解散，在自动解散会议前5分钟会自动播放会议即将解散提示音，
	 * @param orderId 订单ID
	 * @return
	 */
	public Map<String,Object> createConference(Integer  orderId);
	
	/**
	 * 移除会议成员 ，如查是导医退出开始计算时长保证患者和医生有最多15分钟的通话时长。此时进需要计算会议剩下时长，来确定是否需要延长会议
	 * @param confId 会议标识ID
	 * @param callId 呼叫Id，由与会者接通电话后，第三方回调传过来的
	 * @return
	 */
	public Map<String,Object> removeConference(String recordId,String userId);
	/**
	 * 解散会议  停上录音并down下录音文件
	 * @param confId 会议标识ID
	 * @return
	 */
	public  Map<String,Object> dismissConference(String recordId);
	/**
	 * 邀请加入会议
	 * @param patientId 与会者ID，通过该ID去数据库查对应的用户名和电话号码
	 * @param confId 会议标识ID
	 * @return
	 */
	public Map<String,Object> inviteMember(String recordId,String userId);
	
	/**
	 * 禁听（不能听，不能说）
	 * @param confId 会议标识ID
	 * @param callId 呼叫Id，由与会者接通电话后，第三方回调传过来的
	 * @return
	 */
	public Map<String,Object> deafConference(String confId,String callId);
	
	/**
	 * 取消禁听
	 * @param confId
	 * @param callId
	 * @return
	 */
	public Map<String,Object> unDeafConference(String recordId,String userId);
	
	
	public Map<String,Object> queryConference(String recordId);
	
	/**
	 * 开始录音 ，导医作为会议主持人接通电话时开始录音
	 * @param confId
	 * @return
	 */
	public Map<String,Object> recordConference(String confId);
	
	/**
	 * 停止录音，
	 * @param confId
	 * @return
	 */
	public Map<String,Object> stopRecordConference(String confId);
	
	public Map<String,Object> getStatus(String recordId);
	
	public Map<String,Object> getCallRecordByParam(CallRecordParam param);
	
	public void endConfCreate(DBObject object);
	
	public void  endJoin(DBObject object) throws HttpApiException;
	public void endUnJoin(DBObject object);
	
	public void endMedia(DBObject object) throws HttpApiException;
	
	public void endStopRecord(DBObject object);
	
	public void endQuerry(DBObject object);
	
	public void endDismiss(DBObject object) throws HttpApiException;
	
	public String getMessage(String confId);
	
	public CallRecordVO getLastConference(CallRecordParam param);
	
	public Integer getStatusByOrderId(String orderId);
	
	public List<ConfListVO> getRecordStatus(CallRecordParam param);
	
	List<CallBusinessVO> getOrderInServiceByGuide(CallRecordParam param);
	
	public void sendNoConsultNotice() throws HttpApiException;
	
	/**
	 * 通过订单id查询录音信息
	 * @return
	 */
	List<CallRecordVO> getRecordByOrderId(Integer orderId);
	
}
