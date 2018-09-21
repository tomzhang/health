package com.dachen.health.commons.utils;

import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventProducer;
import com.dachen.commons.asyn.event.EventType;

import java.util.List;

public class OrderNotify {

	/**
	 * 支付成功异步处理相关业务
	 * @param orderId
	 */
	public static void paySuccessNotify(Integer orderId) {
		EcEvent event = EcEvent.build(EventType.OrderPaySuccess)
				.param("orderId", orderId);
		EventProducer.fireEvent(event);
	}
	
	/**
	 * 订单创建完成异步发送短信和通知
	 * @param orderId
	 * @param isFromWeChat
	 */
	public static void sendMessageAndNotify(Integer orderId, Boolean isFromWeChat) {
		EcEvent event = EcEvent.build(EventType.SendMessageAndNotify)
				.param("orderId", orderId).param("isFromWeChat", isFromWeChat);
		EventProducer.fireEvent(event);
	}
	
	
	/**
	 * 健康关怀留言异步发送至IM
	 */
	public static void sendCareMessage(String fromUserId, String gid, String jsonMsg) {
		EcEvent event = EcEvent.build(EventType.SendCareMessage)
				.param("fromUserId", fromUserId).param("gid", gid).param("jsonMsg", jsonMsg);
		EventProducer.fireEvent(event);
	}
	
	/**
	 * 答题完成时发送关怀至IM
	 * @param careItemId
	 */
	public static void sendCareAnswerCard(String careItemId, String answerSheetId) {
		EcEvent event = EcEvent.build(EventType.SendCareAnswerCard).param("careItemId", careItemId)
				.param("answerSheetId", answerSheetId);
		EventProducer.fireEvent(event);
	}
	
	/**
	 * 咨询小节上传后更新音频格式
	 * @param key
	 */
	public static void armCovertToMP3(String key) {
		EcEvent event = EcEvent.build(EventType.ARM_TO_MP3).param("key", key);
		EventProducer.fireEvent(event);
	}

	public static void sendCheckItemToXG(Integer patientId, Integer sex, String patientName, String hospitalId, List<String> images,
										 String medicalHistoryUrl, String checkUpId, String checkItemId, String patientPhone, String patientAddress) {
		EcEvent event = EcEvent.build(EventType.SEND_CHECKITEM_TO_XG)
				.param("patientId", patientId)
				.param("sex", sex)
				.param("patientName", patientName)
				.param("hospitalId", hospitalId)
				.param("images", images)
				.param("medicalHistoryUrl", medicalHistoryUrl)
				.param("checkUpId", checkUpId)
                .param("checkItemId", checkItemId)
				.param("phone", patientPhone)
				.param("address", patientAddress);
		EventProducer.fireEvent(event);
	}
	
	/**
	 * @param orderId
	 */
	public static void fireOrdersessionSendmsg(Integer orderId,String messageBit ) {
		EcEvent event = EcEvent.build(EventType.OrderSessionSendMsg)
				.param("orderId", orderId).param("messageBit", messageBit);
		EventProducer.fireEvent(event);
	}
	
	/**
	 * @param orderId
	 */
	public static void fireOrdersessionSendFirstmsg(Integer orderId,String messageGroupId ) {
		EcEvent event = EcEvent.build(EventType.OrderSessionSendFirstMsg)
				.param("orderId", orderId).param("messageGroupId", messageGroupId);
		EventProducer.fireEvent(event);
	}
}
