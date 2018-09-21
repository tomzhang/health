package com.dachen.health.pack.guide.util;

import java.text.SimpleDateFormat;

import com.dachen.commons.constants.UserSession;
import com.dachen.commons.support.spring.SpringBeansUtils;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.entity.vo.ConsultOrderVO;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.util.DateUtil;
import com.dachen.util.ReqUtil;


public class GuideUtils {
	
	
	public static final String GUIDE_DOCTOR_GROUP_PREFIX="guide_";
	
	public static String genKey(Object... keys) {
        if (keys==null){
        	return null;
        }
        StringBuilder buf = new StringBuilder();
        for(Object obj:keys)
        {
        	if(obj==null)
        	{
        		continue;
        	}
        	if(buf.length()>0)
        	{
        		buf.append(":");
        	}
        	buf.append(obj);
        }
        return buf.toString();
    }
	
	
	public static ConsultOrderVO convertP2V(ConsultOrderPO orderPO) {
		ConsultOrderVO vo = new ConsultOrderVO();
		vo.setId(orderPO.getId());
		
		//用户信息
		int userId = orderPO.getUserId();
		UserSession patientUser = ReqUtil.instance.getUser(userId);
		vo.setUserId(userId);
		vo.setName(patientUser.getName());
		vo.setHeadImg(patientUser.getHeadImgPath());
		
		//订单信息
		vo.setMsgId(orderPO.getMsgId());
		vo.setCreateTime(orderPO.getCreateTime());
//		vo.setAppointTime(orderPO.getAppointTime());
//		vo.setEndTime(orderPO.getEndTime());
		if(orderPO.getTalkState()!=null)
		{
			vo.setTalkState(orderPO.getTalkState());
		}
		vo.setGroupId(orderPO.getGroupId());
		vo.setStartTime(orderPO.getStartTime());
		if(orderPO.getDiseaseInfo()!=null){
			//患者信息
//			IPatientService patientService = SpringBeansUtils.getBean("PatientServiceImpl");
			IPatientService patientService = SpringBeansUtils.getBeane(IPatientService.class);
			if(null!=orderPO.getDiseaseInfo().getPatientId()){
				Patient patient = patientService.findByPk(orderPO.getDiseaseInfo().getPatientId()); 
				if(patient!=null){
					if(patient.getSex()!=null){
						vo.setSex(patient.getSex());
					}
					vo.setAge(patient.getAge());
					vo.setAgeStr(patient.getAgeStr());
					vo.setPatientName(patient.getUserName());
				}
			}
			vo.setTelephone(orderPO.getDiseaseInfo().getTelephone());
			vo.setDiseaseDesc(orderPO.getDiseaseInfo().getDiseaseDesc());
		}
		return vo;
	}
	
	public static String convertDate2Str(Long startTime,Long endTime)
	{
		String start = DateUtil.formatDate2Str(startTime, DateUtil.FORMAT_YYYY_MM_DD_HH_MM);
		String end =DateUtil.formatDate2Str(endTime, new SimpleDateFormat("HH:mm"));
//		StringBuffer dateStr = new StringBuffer();
//		dateStr.append(DateUtil.formatDate2Str(startTime, DateUtil.FORMAT_YYYY_MM_DD_HH_MM));
		
		return start+"-"+end;
	}
	
	public static String buildNotifyContent(ConsultOrderPO order,String endContent)
	{
		if(order==null)
		{
			return "";
		}
		UserSession docUser = ReqUtil.instance.getUser(order.getDoctorId());
		UserSession patientUser = ReqUtil.instance.getUser(order.getUserId());
		StringBuffer content=new StringBuffer();
		content.append("用户：").append(patientUser.getName()).append("；患者：").append(getPatientName(order)).append("\n");
		content.append("预约医生：").append(docUser.getName()).append("\n");
		content.append("预约时间：").append(convertDate2Str(order.getAppointStartTime(),order.getAppointEndTime())).append("\n");
		content.append(endContent);
		return content.toString();
	}
	
	private static String getPatientName(ConsultOrderPO order)
	{
		if(order==null)
		{
			return "";
		}
		try
		{
//			IPatientService patientService = SpringBeansUtils.getBean("PatientServiceImpl");
			IPatientService patientService = SpringBeansUtils.getBeane(IPatientService.class);
			Integer patientId = order.getDiseaseInfo().getPatientId();
			Patient patient = patientService.findByPk(patientId); 
			return patient.getUserName();
		}
		catch(Exception e)
		{
			
		}
		UserSession patientUser = ReqUtil.instance.getUser(order.getUserId());
		return patientUser.getName();
	}
	
	public static String getUserIdByGuideDocGroupId(String groupId)
	{
		String temp = new String(groupId);
		return temp.replace(GUIDE_DOCTOR_GROUP_PREFIX, "");
	}
}
