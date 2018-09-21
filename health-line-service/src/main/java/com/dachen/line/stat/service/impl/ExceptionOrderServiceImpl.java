package com.dachen.line.stat.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.line.stat.comon.constant.ExceptionEnum;
import com.dachen.line.stat.comon.constant.OrderStatusEnum;
import com.dachen.line.stat.dao.IMonitorLogDao;
import com.dachen.line.stat.dao.INurseOrderDao;
import com.dachen.line.stat.entity.vo.MonitorLog;
import com.dachen.line.stat.entity.vo.PatientOrder;
import com.dachen.line.stat.entity.vo.VSPTracking;
import com.dachen.line.stat.service.IExceptionOrderService;
import com.dachen.line.stat.service.ILineServiceProductService;
import com.dachen.line.stat.service.IOrderService;
import com.dachen.line.stat.service.IVServiceProcessService;
import com.dachen.line.stat.util.Constant;
import com.mobsms.sdk.MobSmsSdk;

@Service
public class ExceptionOrderServiceImpl implements IExceptionOrderService{
	
	Logger logger=LoggerFactory.getLogger(getClass());
	@Autowired
	public INurseOrderDao nurseOrderDao;
	@Autowired
	public ILineServiceProductService lineServiceProductService;
	
	@Autowired
	public IMonitorLogDao monitorLogDao;
	@Autowired
	public IVServiceProcessService vServiceProcessService;
	
	@Autowired
	private IPatientService patientService;
	
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private MobSmsSdk mobSmsSdk;
	
	@Override
	public void getOderOvertopHour() throws HttpApiException {
		logger.info("检查2小时无人接单任务开始");
		//所有的符合2小时无人接单的记录
		List<VSPTracking> list = nurseOrderDao.getOderOvertopHour();
		String content = Constant.MESSAGE_PATIENT_TIME_OVERTOP();
		if (list.size()>0) {
			String phone = "";
			List<String> list_u_id = new ArrayList<String>();
			MonitorLog log = new MonitorLog();
			log.setTime(new Date().getTime()/1000);
			log.setType(1);
			log.setServiceCode(ExceptionEnum.Exception100.getIndex());
			for (VSPTracking vp : list) {
				MonitorLog m_log = monitorLogDao.getMonitorLogList(vp.getId(), ExceptionEnum.Exception100.getIndex(), 1);
				//发送之前先去短信记录表查询是否相同的检查服务、异常类型已经发送
				if(m_log==null){
					PatientOrder order = nurseOrderDao.getPatientOrderById(vp.getOrderId());
					phone = order.getPatientTel();
					content = content.replace("**", order.getProduct().getTitle())+patient_order_exception_url();
					log.setContent(content);
					log.setTel(phone);
					mobSmsSdk.send(phone,content);//发送短消息
					list_u_id.add(String.valueOf(order.getUserId()));
					log.setServiceId(vp.getId());
					//完成之后往短信日志表插入一跳记录
					monitorLogDao.insertUserMonitorLog(log);
					//Helper.push(content,list_u_id);//通过APP 推送消息
				}
			}
		}
		logger.info("检查2小时无人接单任务结束");
	}

	public String patient_order_exception_url() throws HttpApiException {
		String url = PropertiesUtil.getContextProperty("patient.order.exception.url");
		String shortUrl = shortUrlComponent.generateShortUrl(url);
		return shortUrl;

	}

	@Override
	public void getBeforeExceptionOrder() throws ParseException, HttpApiException {
		logger.info("检查兜底时间无人接单任务开始");
		List<VSPTracking> list = nurseOrderDao.getBeforeExceptionOrder();
		String content =  Constant.PATIENT_ORDER_EXCEPTION_CANCEL();
		if(list.size()>0){
			String phone = "";
			List<String> list_u_id = new ArrayList<String>();
			MonitorLog log = new MonitorLog();
			log.setTime(new Date().getTime()/1000);
			log.setType(1);
			log.setServiceCode(ExceptionEnum.Exception101.getIndex());
			for (VSPTracking vsp : list) {
				MonitorLog m_log = monitorLogDao.getMonitorLogList(vsp.getId(), ExceptionEnum.Exception101.getIndex(), 1);
				//发送之前先去短信记录表查询是否相同的检查服务、异常类型已经发送
				if(m_log==null){
					PatientOrder order = nurseOrderDao.getPatientOrderById(vsp.getOrderId());
					phone = order.getPatientTel();
					//根据服务id查询患者所选择检查套餐
					content = content.replace("**", order.getProduct().getTitle())+patient_order_exception_url();
					mobSmsSdk.send(phone,content);//发送短消息
					list_u_id.add(String.valueOf(order.getUserId()));
					log.setServiceId(vsp.getId());
					log.setContent(content);
					log.setTel(phone);
					//完成之后往短信日志表插入一跳记录
					monitorLogDao.insertUserMonitorLog(log);
					nurseOrderDao.updatePatientOrderStatus(order.getId(), 2);
					//Helper.push(content,list_u_id);//通过APP 推送消息
				}
			}
		}
		logger.info("检查兜底时间无人接单任务结束");
	}

	@Override
	public void getExceptionOrderNoEvaluate() throws ParseException {
		logger.info(" 超过3天患者没有做出评价--开始");
		List<VSPTracking> list = nurseOrderDao.getExceptionOrderNoEvaluate();
		if(list.size()>0){
			MonitorLog log = new MonitorLog();
			log.setTime(new Date().getTime());
			log.setType(1);
			log.setServiceCode(ExceptionEnum.Exception102.getIndex());
			for (VSPTracking vp : list) {
				MonitorLog m_log = monitorLogDao.getMonitorLogList(vp.getId(), ExceptionEnum.Exception102.getIndex(), 1);
				if(m_log==null){
					//关闭操作
					orderService.endAppraise(vp.getOrderId(), OrderStatusEnum.close.getIndex());
					//智行的过程要是没有抛出异常则会智行插入操作完成之后往短信日志表插入一跳记录
					monitorLogDao.insertUserMonitorLog(log);
					//接下来就得给护士打钱 目前 接口没有 先预留
				}
			}
		}
		logger.info(" 超过3天患者没有做出评价--结束");
	}

	@Override
	public void getExceptionNoCallPhone() throws HttpApiException {
		logger.info("护士接单成功 但是过半小时仍然没有与患者进行联络--开始");
		String phone="";
		String content = Constant.NURSE_ORDER_NO_SENDMESSAGE();
		List<VSPTracking> list = vServiceProcessService.getExceptionNoCallPhone();
		if(list.size()>0){
			MonitorLog log = new MonitorLog();
			log.setTime(new Date().getTime()/1000);
			log.setType(1);
			log.setServiceCode(ExceptionEnum.Exception200.getIndex());
			for (VSPTracking vp : list) {
				PatientOrder order = nurseOrderDao.getPatientOrderById(vp.getOrderId());
				Patient p = findByPk(order.getPatientId());
				phone = order.getPatientTel();
				//目前所有的短信均只发一次
				MonitorLog m_log = monitorLogDao.getMonitorLogList(vp.getId(), ExceptionEnum.Exception200.getIndex(),0);
				if(m_log==null){
					content = content.replace("**", order.getProduct().getTitle()).replace("##", null==p.getUserName()?"":p.getUserName())+ APP_NURSE_CLIENT_LINK();
					mobSmsSdk.send(phone,content);//发送短消息
					log.setType(0);
					log.setContent(content);
					log.setTel(phone);
					log.setServiceId(vp.getId());
					monitorLogDao.insertUserMonitorLog(log);
				}
			}
		}
		logger.info("护士接单成功 但是过半小时仍然没有与患者进行联络--结束");
	}

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	public String APP_NURSE_CLIENT_LINK () throws HttpApiException {
		String nurseLink = PropertiesUtil
				.getContextProperty("app.nurse.client.link");

		String shorUrl = shortUrlComponent.generateShortUrl(nurseLink);
		return shorUrl;
	}

	@Override
	public void getExceptionOfNurseService() throws Exception{
		logger.info("护士在预约时间开始之前点击了开始服务按钮--开始");
		MonitorLog log = new MonitorLog();
		log.setTime(new Date().getTime()/1000);
		log.setType(1);
		List<VSPTracking> vsp = nurseOrderDao.getExceptionOfNurseService();
		if(vsp.size()>0){
			for (VSPTracking vp : vsp) {
				MonitorLog m_log = monitorLogDao.getMonitorLogList(vp.getId(), ExceptionEnum.Exception202.getIndex(), 1);
				if(m_log==null){
					log.setServiceCode(ExceptionEnum.Exception202.getIndex());
					log.setType(1);
					log.setServiceId(vp.getId());
					monitorLogDao.insertUserMonitorLog(log);
				}
			}
		}
		logger.info("护士在预约时间开始之前点击了开始服务按钮--结束");
	}

	@Override
	public void getExceptionOfNurseServiceNoClick() throws Exception {
		logger.info("护士在预约时间开始之前点击了开始服务按钮--开始");
		List<VSPTracking> vsp = nurseOrderDao.getExceptionOfNurseServiceNoClick();
		MonitorLog log = new MonitorLog();
		log.setTime(new Date().getTime()/1000);
		log.setType(1);
		log.setServiceCode(ExceptionEnum.Exception203.getIndex());
		if (vsp.size()>0) {
			for (VSPTracking vp : vsp) {
				MonitorLog m_log = monitorLogDao.getMonitorLogList(vp.getId(),ExceptionEnum.Exception203.getIndex(), 1);
				if(m_log==null){
					log.setType(1);
					log.setServiceId(vp.getId());
					monitorLogDao.insertUserMonitorLog(log);
				}
			}
		}
		logger.info("护士在预约时间开始之前点击了开始服务按钮--结束");
	}
	/**
	 * 根据患者id查询患者信息
	 * @param pk
	 * @return
	 */
	public Patient findByPk(Integer pk){
		Patient p = new Patient();
		p = patientService.findByPk(pk);
		return p;
	}
}
