package com.dachen.health.pack.consult.timertask;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.service.IOrderDoctorService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.util.DateUtil;
import com.mobsms.sdk.MobSmsSdk;
import com.tencent.common.Util;

@Component("consultationPackTask")
public class ConsultationPackTask {

	@Autowired
	IOrderService orderService;
	
	@Autowired
	IOrderSessionService orderSessionService;
	
	@Autowired
	IOrderDoctorService orderDoctorService;
	
	@Autowired
	MobSmsSdk mobSmsSdk;
	
	@Autowired
	UserManager userManager;
	
	@Autowired
    IBaseDataService baseDataService;
	
	@Autowired
	IPatientService patientSevice;
	
	private static int day9Time = 0;
	
	private static int minute30Time = 1;
	
	//private static String[][] messageArr = new String[2][3];
										 /*{
											  {
												  "尊敬的{0}医生，{1}患者预约的会诊服务于{2}开始，请提前了解患者病历资料，做好咨询前准备！",
												  "尊敬的{0}医生，{1}患者预约的{2}医生会诊服务于{3}开始，请提前准备相关的病历资料，尽快告知患者会诊服务相关事宜，确保咨询开始前患者到达医院与医生一起进行会诊咨询服务",
												  "尊敬的{0}用户，您预约的{1}医生会诊服务于{2}开始，请提前准备相关的病历资料，确保咨询开始前到达医院与医生一起进行会诊咨询服务。"
											  },
											  {
												  "尊敬的{0}医生，{1}患者的会诊服务于30分钟后开始，请注意保持通话环境安静，保证信号畅通，提前做好准备！",
												  "尊敬的{0}医生，{1}患者预约{2}医生的会诊服务于30分钟后开始，请注意保持通话环境安静，保证信号畅通，提前做好准备！",
												  "尊敬的{0}用户，您与{1}医生的会诊服务于30分钟后开始，请提前做好准备！"
											  }
											};*/
	
	public void day9PointTask(){
		processTask(day9Time);
	}
	
	//已改成了Jesque定时任务
//	public void minute30PointTask(){
//		processTask(minute30Time);
//	}

	private void processTask(int intervalType) {
		List<Integer> orderIds = orderService.getAllPaidConsultationOrderIds();
		if(Util.isNullOrEmpty(orderIds)){
			return ;
		}
		List<OrderSession> oss = orderSessionService.getAllMoringBeginConsultationOrders(orderIds,intervalType);
		if(Util.isNullOrEmpty(oss)){
			return ;
		}
		for (OrderSession os : oss) {
			int orderId = os.getOrderId();
			remindConsultation(intervalType, os, orderId);
		}
	}

	public void remindConsultation(int intervalType, OrderSession os, int orderId) {
		long appointTime = os.getAppointTime();
		Order order = orderService.getOne(orderId);
		int assistantDocId = order.getDoctorId();
		int patientId = order.getPatientId();
		int specialistDocId = 0;
		List<OrderDoctor> ods = orderDoctorService.findOrderDoctors(orderId);
		if(!Util.isNullOrEmpty(ods)){
			for (OrderDoctor orderDoctor : ods) {
				if(orderDoctor.getDoctorId().intValue() != assistantDocId){
					specialistDocId = orderDoctor.getDoctorId();
				}
			}
		}
		User assistantDoctor = userManager.getUser(assistantDocId);
		Patient patient = patientSevice.findByPk(patientId);
		User specialistDoctor = userManager.getUser(specialistDocId);
		//根据patient里的userId查询该患者是属于哪个平台
		User p_user = userManager.getUser(patient.getUserId());
		String signature = mobSmsSdk.isBDJL(p_user)?BaseConstants.BD_SIGN:BaseConstants.XG_SIGN;
		if(intervalType == 0){
			String appointTimeStr = DateUtil.formatDate2Str2(new Date(appointTime));
			//会诊医生
			mobSmsSdk.send(specialistDoctor.getTelephone(),baseDataService.toContent("0064",  specialistDoctor.getName(),patient.getUserName(),appointTimeStr));
			//主诊医生
			mobSmsSdk.send(assistantDoctor.getTelephone(), baseDataService.toContent("0065",  assistantDoctor.getName(),patient.getUserName(),specialistDoctor.getName(),appointTimeStr));
			//患者
			mobSmsSdk.send(patient.getTelephone(), baseDataService.toContent("0066", patient.getUserName(),specialistDoctor.getName(),appointTimeStr),signature);
		}else if(intervalType == 1){
			//会诊医生
			mobSmsSdk.send(specialistDoctor.getTelephone(), baseDataService.toContent("0067", specialistDoctor.getName(),patient.getUserName()));
			//主诊医生
			mobSmsSdk.send(assistantDoctor.getTelephone(), baseDataService.toContent("0068", assistantDoctor.getName(),patient.getUserName(),specialistDoctor.getName()));
			//患者
			mobSmsSdk.send(patient.getTelephone(), baseDataService.toContent("0069", patient.getUserName(),specialistDoctor.getName()),signature);
		}
	}
}
