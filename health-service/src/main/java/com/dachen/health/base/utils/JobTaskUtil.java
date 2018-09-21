package com.dachen.health.base.utils;

import java.util.Calendar;
import java.util.Date;

import com.dachen.commons.schedule.JobExecutors;
import com.dachen.commons.schedule.data.JobType;
import com.dachen.commons.schedule.data.JobVO;
import com.dachen.health.base.constant.JobQueueName;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.util.DateUtil;

/**
 * Jesque定时任务工具类
 * @author xieping
 */
public class JobTaskUtil {
	
	/**
	 * 预约预留时间
	 */
	public static long appointTime = 48 * 60 * 60;
	
	/**
	 * 支付预留时间
	 */
	public static long payTime = 48 * 60 * 60;
	
	/**
	 * 支付完成-开始服务预留时间
	 */
	public static long paidTime = 48 * 60 * 60;
	
	/**
	 * 门诊订单加入队列
	 * @param orderId
	 */
	public static void cancelOutpatient(Integer orderId) {
		JobVO job = new JobVO(JobType.DELAYJOB, 3*60);
		job.setBeanName("JobTask");
		job.setMethod("cancelOutpatient");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		
		JobExecutors.createJob(JobQueueName.CANCEL_OUTPATIENT.name(), job);
	} 
	
	
	/**
	 * 用药提醒定时任务加入队列 （20160818, added by xiaowei）
	 * 
	 * @param schedulePlanId
	 */
//	public static void sendMedicalSchedulePlanRemind(String schedulePlanId, int delay) {
//		System.out.println("sendMedicalSchedulePlanRemind. schedulePlanId="+ schedulePlanId + ",delay="+delay);
//		if (0 == delay) {
//			delay=1;
//		}
//		JobVO job = new JobVO(JobType.DELAYJOB, delay);
//		job.setBeanName("JobTask");
//		job.setMethod("sendMedicalSchedulePlanRemind");
//		job.addParamTypes(String.class);
//		job.addParamValue(schedulePlanId);
//
//		JobExecutors.createJob(JobQueueName.REMIND_MEDICAL_SCHEDULE_PLAN_MESSAGE.name(), job);
//	}
	
//	public static void sendCareItemImmediately(String schedulePlanId) {
//		JobVO job = new JobVO(JobType.DELAYJOB, 3);
//		job.setBeanName("JobTask");
//		job.setMethod("sendCareItemImmediately");
//		job.addParamTypes(String.class);
//		job.addParamValue(schedulePlanId);
//
//		JobExecutors.createJob(JobQueueName.CARE_ITEM_IMMEDIATELY.name(), job);
//	}
	
	/**
	 * 待预约订单加入队列
	 * @param orderId
	 */
	public static void cancelNoAppointOrder(Integer orderId) {
		JobVO job = new JobVO(JobType.DELAYJOB, appointTime);
		job.setBeanName("JobTask");
		job.setMethod("cancelNoAppointOrder");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);

		JobExecutors.createJob(JobQueueName.CANCEL_NOAPPOINT_ORDER.name(), job);
	} 
	
	/**
	 * 待支付订单加入队列
	 * @param orderId
	 */
	public static void cancelNoPayOrder(Integer orderId) {
		JobVO job = new JobVO(JobType.DELAYJOB, 2 * 60 * 60);
		job.setBeanName("JobTask");
		job.setMethod("cancelNoPayOrder");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);

		JobExecutors.createJob(JobQueueName.CANCEL_NOPAY_ORDER.name(), job);
	} 
	
	/**
	 * 已支付订单加入队列
	 * @param orderId
	 */
	public static void cancelPaidOrder(Integer orderId, long delay) {
		JobVO job = new JobVO(JobType.DELAYJOB, delay);
		job.setBeanName("JobTask");
		job.setMethod("cancelPaidOrder");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);

		JobExecutors.createJob(JobQueueName.CANCEL_PAID_ORDER.name(), job);
	}
	
	/**
	 * 已支付图文订单加入队列（2小时未开始服务短信提醒医生）
	 * @param orderId
	 */
	public static void remindMessage(Integer orderId) {
		JobVO job = new JobVO(JobType.DELAYJOB, 2*60*60);
		job.setBeanName("JobTask");
		job.setMethod("remindMessage");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);

		JobExecutors.createJob(JobQueueName.REMIND_MESSAGE.name(), job);
	}
	
	/**
	 * 已支付会诊订单加入队列（预约时间前半小时提醒）
	 * @param orderId
	 * @param delaySecond 延迟时间(?秒)
	 */
	public static void remindConsultation(Integer orderId, long delaySecond) {
		JobVO job = new JobVO(JobType.DELAYJOB, delaySecond);
		job.setBeanName("JobTask");
		job.setMethod("remindConsultation");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);

		JobExecutors.createJob(JobQueueName.REMIND_CONSULTATION.name(), job);
	}
	
	/**
	 * 医生上线2小时后系统强制下线
	 * @param gdocId 集团医生Id
	 * @param isGdoc 是否集团医生  false:平台医生
	 */
	public static void doctorOffline(String gdocId, boolean isGdoc) {
		JobVO job = new JobVO(JobType.DELAYJOB, 2 * 60 * 60);
		job.setBeanName("JobTask");
		job.setMethod(isGdoc ? "groupOffline" : "platformOffline");
		job.addParamTypes(String.class);
		job.addParamValue(gdocId);

		JobExecutors.createJob(JobQueueName.DOCTOR_OFFLINE_TWO_HOURS.name(), job);
	}
	
	/**
	 * 不在值班时间段系统强制下线
	 */
	public static void offlineTimeout(String groupId) {
		JobVO job = new JobVO(JobType.RECURRINGJOB, 5 * 60, 5 * 60);//5分钟执行一次
		job.setBeanName("JobTask");
		job.setMethod("offlineTimeout");
		job.addParamTypes(String.class);
		job.addParamValue(groupId);

		JobExecutors.createJob(JobQueueName.DOCTOR_OFFLINE_FORCE.name(), job);
	}
	
	/**
	 * 1分钟后再次查询微信退款结果
	 */
	public static void queryWXRefundResult(Integer refundId) {
		JobVO job = new JobVO(JobType.DELAYJOB, 60);
		job.setBeanName("JobTask");
		job.setMethod("queryWXRefundResult");
		job.addParamTypes(Integer.class);
		job.addParamValue(refundId);

		JobExecutors.createJob(JobQueueName.WX_REFUDN_QUERY.name(), job);
	}
	
	/**
	 * 患者电话咨询订单超过24小时未关闭-自动结束服务
	 * @param consultOrderId
	 */
	public static void endService4Guide(String consultOrderId) {
		JobVO job = new JobVO(JobType.DELAYJOB, 24 * 60 * 60);
		job.setBeanName("JobTask");
		job.setMethod("endService4Guide");
		job.addParamTypes(String.class);
		job.addParamValue(consultOrderId);

		JobExecutors.createJob(JobQueueName.GUIDE_END_SERVICE.name(), job);
	}
	
	/**
	 * 预约到医生2小时未支付-自动结束服务
	 * @param consultOrderDoctorId
	 */
	public static void endService4NoPay(String consultOrderDoctorId) {
		JobVO job = new JobVO(JobType.DELAYJOB, 2 * 60 * 60);
		job.setBeanName("JobTask");
		job.setMethod("endService4NoPay");
		job.addParamTypes(String.class);
		job.addParamValue(consultOrderDoctorId);

		JobExecutors.createJob(JobQueueName.GUIDE_NO_PAY_END_SERVICE.name(), job);
	}
	
	public static void endStopRecord(String recordId){
		JobVO job = new JobVO(JobType.DELAYJOB, 30);
		job.setBeanName("JobTask");
		job.setMethod("endStopRecord");
		job.addParamTypes(String.class);
		job.addParamValue(recordId);
		job.addParamTypes(Integer.class);
		job.addParamValue(2);
		
		JobExecutors.createJob(JobQueueName.DOWN_FROM_YZX2QN.name(), job);
	}
	/**
	 * 预约名医发送短信
	 * @param userName
	 * @param telPhone
	 * @param appTime
	 */
	public static void sendAppointment(String userName,String telPhone,Long appTime){
		Long sysTime=(appTime-System.currentTimeMillis()-2*60*60*1000)/1000;
		if(sysTime>0){
			JobVO job = new JobVO(JobType.DELAYJOB, sysTime);
			job.setBeanName("JobTask");
			job.setMethod("sendAppointment");
			job.addParamTypes(String.class);
			job.addParamValue(userName);
			job.addParamTypes(String.class);
			job.addParamValue(telPhone);
			
			JobExecutors.createJob(JobQueueName.SEND_APPOINTMENT.name(), job);
		}
	}
	
	/**
	 * 用户未评价，15天后自动评价
	 * @param orderId
	 */
	public static void autoEvaluation(Integer orderId) {
		JobVO job = new JobVO(JobType.DELAYJOB, 15 * 24 * 60 * 60);
		job.setBeanName("JobTask");
		job.setMethod("autoEvaluation");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);

		JobExecutors.createJob(JobQueueName.AUTO_EVALUATION.name(), job);
	}
	
	/**
	 * 已支付电话咨询订单预约时间前10分钟向导医发送短信通知
	 * @param orderId
	 */
	public static void autoSendMsgToGuider(Integer orderId,long delaySecond) {
		JobVO job = new JobVO(JobType.DELAYJOB, delaySecond);
		job.setBeanName("JobTask");
		job.setMethod("autoSendMsgToGuider");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		JobExecutors.createJob(JobQueueName.SEND_MSGTOGUIDER.name(), job);
	}
	
	/**
	 * 已支付电话咨询订单超过预约时间半小时
	 * @param orderId
	 */
	public static void autoSendMsgToGuiderOfHalfHour(Integer orderId,long delaySecond) {
		JobVO job = new JobVO(JobType.DELAYJOB, delaySecond);
		job.setBeanName("JobTask");
		job.setMethod("autoSendMsgToGuiderOfHalfHour");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		JobExecutors.createJob(JobQueueName.SEND_MSGTOGUIDEROF_HALF_HOUR.name(), job);
	}
	
	
	/**
	 * 已开始服务订单 在${delaySecond}秒之后结束订单
	 * @param orderId
	 */
	public static void autoFinishAfterBeginService(Integer orderId,long delaySecond) {
		JobVO job = new JobVO(JobType.DELAYJOB, delaySecond);
		job.setBeanName("JobTask");
		job.setMethod("autoFinishAfterBeginService");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		JobExecutors.createJob(JobQueueName.AUTO_FINISH_AFTER_BEGINSERVICE.name(), job);
	}
	
	/**
	 * 每天更新医生的预约排班
	 */
	private static void updateDoctorOfflineItemEveryDay() {
		long now = System.currentTimeMillis();
		long delaySecond = (DateUtil.getDayBegin(now+DateUtil.daymillSeconds) - now )/ 1000;
		JobVO job = new JobVO(JobType.RECURRINGJOB, delaySecond,DateUtil.daymillSeconds/1000);
		job.setBeanName("JobTask");
		job.setMethod("updateDoctorOfflineItemEveryDay");
		JobExecutors.createJob(JobQueueName.UPDATE_DOCTOR_OFFLINEITEM_RECURRING.name(), job);
	}

	
	/**
	 * 名医面对面服务在开启之后两小时自动结束
	 * @param orderId
	 */
	public static void autoFinishAppointmentOrder(Integer orderId,long delaySecond) {
		JobVO job = new JobVO(JobType.DELAYJOB, delaySecond);
		job.setBeanName("JobTask");
		job.setMethod("autoFinishAppointmentOrder");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		JobExecutors.createJob(JobQueueName.AUTO_FINISH_APPOINTMENT_AFTER_BEGINSERVICE.name(), job);
	}

	public static void cancelNoAgreeOrNoPayAppointmentOrder(Integer orderId, Long appointmentTime) {
		/**
		 * 1 、定时取消医生没有同意的订单
		 */
		long now = System.currentTimeMillis();
		long noDoctorProcessTime = (DateUtil.getDayBegin(appointmentTime) - now)/1000;
		JobVO job = new JobVO(JobType.DELAYJOB, noDoctorProcessTime);
		job.setBeanName("JobTask");
		job.setMethod("cancelNoAgreeOrNoPayAppointmentOrder");
		job.addParamTypes(Integer.class);
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		job.addParamValue(UserType.doctor.getIndex());
		JobExecutors.createJob(JobQueueName.AUTO_CANCEL_APPOINTMENT_NO_PROCESS.name(), job);
		/**
		 * 2、定时取消患者没有支付的订单
		 */
		long noPatientProcessTime = ((appointmentTime - 4 * 60 * 60 * 1000) - now)/1000;
		job = new JobVO(JobType.DELAYJOB, noPatientProcessTime);
		job.setBeanName("JobTask");
		job.setMethod("cancelNoAgreeOrNoPayAppointmentOrder");
		job.addParamTypes(Integer.class);
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		job.addParamValue(UserType.patient.getIndex());
		JobExecutors.createJob(JobQueueName.AUTO_CANCEL_APPOINTMENT_NO_PROCESS.name(), job);
	}

	public static void doctorAssistantSetAppointTimeSendMsg(String phone, String content, Long appointTime){
		JobVO job = new JobVO(JobType.DELAYJOB, (appointTime-System.currentTimeMillis())/1000);
		job.setBeanName("JobTask");
		job.setMethod("doctorAssistantSetAppointTimeSendMsg");
		job.addParamTypes(String.class);
		job.addParamValue(phone);
		job.addParamTypes(String.class);
		job.addParamValue(content);
		JobExecutors.createJob(JobQueueName.REMIND_ASSISTANT_APPOINTMENT_MESSAGE.name(), job);
	}
	
	public static void syncAuthData(Integer i) {
		JobVO job = new JobVO(JobType.DELAYJOB, 60);
		job.setBeanName("UserManagerImpl");
		job.setMethod("syncAuthData");
		job.addParamTypes(Integer.class);
		job.addParamValue(i);
		JobExecutors.createJob("SYNC_AUTH_DATA", job);
	}

	public static int calcTodayTaskDelay(Long fullSendTime) {

		Calendar fullSendDate = Calendar.getInstance();
		fullSendDate.setTimeInMillis(fullSendTime);

		Calendar nowDate = Calendar.getInstance();
		long now = nowDate.getTimeInMillis();
		nowDate.set(Calendar.HOUR_OF_DAY, fullSendDate.get(Calendar.HOUR_OF_DAY));
		nowDate.set(Calendar.MINUTE, fullSendDate.get(Calendar.MINUTE));
		nowDate.set(Calendar.SECOND, fullSendDate.get(Calendar.SECOND));

		long sendTime = nowDate.getTimeInMillis();

		if (sendTime<=now) {	// 发送时间比当前时间还小，则即刻发送(传1即可，传0会报错)
			return 1;
		}

		// 发送时间与当前时间的时间差
		long diff = sendTime-now;

		return (int)(diff/1000)+1;
	}

	public static int calcTaskDelay(Long fullSendTime, int dayRepeated) {
		// 计算第N天的发送时间（根据fullSendTime和dayRepeated计算出）
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(fullSendTime);
		if (0 != dayRepeated) {
			cal.add(Calendar.DATE, dayRepeated);
		}

		long sendTime = cal.getTimeInMillis();
		
		long now = System.currentTimeMillis();
		if (sendTime<now) {	// 发送时间比当前时间还小，则即刻发送(传1即可，传0会报错)
			return 1;
		}
		
		// 发送时间与当前时间的时间差
		long diff = sendTime-now;
		
		return (int)(diff/1000)+1;
	}


	public static int calcTodayRemainSeconds() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.DATE, 1);

		long diff = cal.getTime().getTime()-System.currentTimeMillis();
		return (int)(diff/1000)+1;
	}

	public static int calcDayRepeated(Long fullSendTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(fullSendTime);

		Date date1 = cal.getTime();
		Date date2 = new Date();
		long diff = date2.getTime() - date1.getTime();
		if (diff<0) {
			return -1; // 未来的时间，不确定
		}

		int daysDiff = (int)(diff/(1000*3600*24));

		return daysDiff;	// 第daysDiff天
	}
	
	public static int calcTaskDelayTest(Long fullSendTime, int dayRepeated) {
		// 每隔30秒发一次，用于测试
		if (1 == dayRepeated) {
			return 1;
		}
		int delay = 60;
		return delay;
		
	}


	public static void notifyWhenReplyCountEqZero(Integer orderId) {
		// 2 hours 
		JobVO job = new JobVO(JobType.DELAYJOB, 2*60*60);
		job.setBeanName("JobTask");
		job.setMethod("notifyWhenReplyCountEqZero");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		JobExecutors.createJob(JobQueueName.NOTIFY_WHEN_REPLY_COUNT_EQ_ZERO.name(), job);
	}

	/**
	 * 取消{CANCEL_WHEN_REPLY_COUNT_EQ_ZERO}任务
	 * 取消{FINISH_WHEN_REPLY_COUNT_EQ_ZERO}任务
	 * @param orderId
	 */
	public static void cancelReplyCountEqZeroJob(Integer orderId) {
		//CANCEL_WHEN_REPLY_COUNT_EQ_ZERO
		JobVO job = new JobVO(JobType.DELAYJOB, 2*60*60);
		job.setBeanName("JobTask");
		job.setMethod("notifyWhenReplyCountEqZero");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		JobExecutors.cancelDelayJob(JobQueueName.NOTIFY_WHEN_REPLY_COUNT_EQ_ZERO.name(), job);
		//FINISH_WHEN_REPLY_COUNT_EQ_ZERO
		job = new JobVO(JobType.DELAYJOB, 5*60*60);
		job.setBeanName("JobTask");
		job.setMethod("finishWhenReplyCountEqZero");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		JobExecutors.createJob(JobQueueName.FINISH_WHEN_REPLY_COUNT_EQ_ZERO.name(), job);
	}

	public static void finishWhenReplyCountEqZero(Integer orderId) {
		// 5 hours 
		JobVO job = new JobVO(JobType.DELAYJOB, 5*60*60);
		job.setBeanName("JobTask");
		job.setMethod("finishWhenReplyCountEqZero");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		JobExecutors.createJob(JobQueueName.FINISH_WHEN_REPLY_COUNT_EQ_ZERO.name(), job);
	}


	public static void cancelTheNoPrepareOrder(Integer orderId) {
		long now = System.currentTimeMillis();
		long intervel = DateUtil.getDayBegin(now+DateUtil.daymillSeconds) - now;
		JobVO job = new JobVO(JobType.DELAYJOB, intervel / 1000); //晚上凌晨时间的秒数
		job.setBeanName("JobTask");
		job.setMethod("cancelOutpatientNoParpare");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);
		JobExecutors.createJob(JobQueueName.CANCEL_OUTPATIENT_NOPARPARE.name(), job);
	}

	public static void reSendImagesToXG(String xGCheckItemReqId) {
		long now = System.currentTimeMillis();
		//获取0点
		long intervel = DateUtil.getDayBegin(now+DateUtil.daymillSeconds) - now;
		JobVO job = new JobVO(JobType.DELAYJOB, intervel / 1000); //晚上凌晨时间的秒数
		job.setBeanName("JobTask");
		job.setMethod("reSendImagesToXG");
		job.addParamTypes(String.class);
		job.addParamValue(xGCheckItemReqId);
		JobExecutors.createJob(JobQueueName.RE_SEND_IMAGES_TO_XG.name(), job);
	}
	
	

	/**
	 * 更新医生助手看到的订单状态
	 * @param orderId
	 */
	public static void updatePendingOrderStatus(Integer orderId, long delay) {
		JobVO job = new JobVO(JobType.DELAYJOB, delay);
		job.setBeanName("JobTask");
		job.setMethod("updatePendingOrderStatus");
		job.addParamTypes(Integer.class);
		job.addParamValue(orderId);

		JobExecutors.createJob(JobQueueName.CANCEL_PAID_ORDER.name(), job);
	}
}
