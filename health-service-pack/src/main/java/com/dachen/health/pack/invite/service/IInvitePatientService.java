package com.dachen.health.pack.invite.service;

import java.util.Map;

import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.sdk.exception.HttpApiException;

public interface IInvitePatientService {

	/**
	 * 邀请患者，支持批量
	 * 
	 * @param doctorId
	 * @param telephones
	 * @return
	 */
	Map<String, Object> invitePatient(Integer doctorId, String... telephones);

	
	/**
	 * 未激活用户注册成功后给邀请医生发送系统通知
	 * @param user
	 */
	void sendNotice(Integer userId) throws HttpApiException;
	void sendNotice(User user) throws HttpApiException;

	void sendSmsAsync(Integer userId, Integer toUserId, Integer smsType, String text);

	public Map<String, Object> addOnePatient(Integer doctorId, String  tel) ;


	Map<String,Object> addPatientByGuide(Patient patient) throws HttpApiException;

}
