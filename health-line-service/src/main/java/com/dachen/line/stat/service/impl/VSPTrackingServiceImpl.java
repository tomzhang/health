package com.dachen.line.stat.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.entity.TelephoneAccount;
import com.dachen.health.commons.service.TelephoneService;
import com.dachen.health.commons.vo.User;
import com.dachen.line.stat.comon.constant.ExceptionEnum;
import com.dachen.line.stat.dao.IMessageDao;
import com.dachen.line.stat.dao.INurseOrderDao;
import com.dachen.line.stat.dao.IVSPTrackingDao;
import com.dachen.line.stat.dao.IVServiceProcessDao;
import com.dachen.line.stat.entity.vo.Message;
import com.dachen.line.stat.entity.vo.VSPTracking;
import com.dachen.line.stat.entity.vo.VServiceProcess;
import com.dachen.line.stat.service.IVSPTrackingService;
import com.dachen.line.stat.util.Constant;
import com.mobsms.sdk.CMSClient;
import com.mobsms.sdk.MobSmsSdk;
import com.ucpaas.restsdk.UcPaasRestSdk;

/**
 * 护士订单服务
 * 
 * @author liwei
 * @date 2015/8/19
 */
@Service
public class VSPTrackingServiceImpl implements IVSPTrackingService {

	@Autowired
	TelephoneService telephoneService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private IMessageDao messageDao;
	@Autowired
	private IVSPTrackingDao vspTrackingDao;
	@Autowired
	private IVServiceProcessDao vServiceProcessDao;

	@Autowired
	private INurseOrderDao nurseOrderDao;

	@Autowired
	private MobSmsSdk mobSmsSdk;

	@Override
	public Map<String, Object> callByTel(Integer nurseId, String vspId) {
		Map<String, Object> map = new HashMap<String, Object>();

		/**
		 * 通过护士服务id获取患者电话
		 */
		String toTel = getPatientTelByServiceId(vspId);

		if (StringUtils.isEmpty(toTel)) {
			throw new ServiceException("患者电话为空");
		}

		String fromTel = userRepository.getUser(nurseId).getTelephone();
		TelephoneAccount toTelAcount = telephoneService.findByTelePhone(toTel);
		TelephoneAccount fromTelAcount = telephoneService
				.findByTelePhone(fromTel);
		if (toTelAcount == null || fromTelAcount == null) {
			map.put("resultCode", "40010");
			map.put("message", "第三方接口调用失败");
		} else {
			JSONObject ret = UcPaasRestSdk.callback(
					fromTelAcount.getClientNumber(),
					toTelAcount.getTelephone(), toTelAcount.getTelephone(),
					fromTelAcount.getTelephone(), "");
			if ("000000".equals(((JSONObject) ret.get("resp")).get("respCode"))) {

				// 记录流程日志
				JSONObject callback = (JSONObject) (((JSONObject) ret
						.get("resp")).get("callback"));
				VSPTracking trace = new VSPTracking();
				if (null != callback) {
					trace.setPhoneId(callback.getString("callId"));
				}
				;// {"resp":{"callback":{"callId":"525c09da1951b61c93b849cb50c8dd8d","createDate":"20151212152440"},"respCode":"000000"}}
				trace.setCreateTime(new Date().getTime());
				trace.setCode(ExceptionEnum.Business_code_300.getIndex());
				trace.setServiceId(vspId);
				vspTrackingDao.insertVSPTracking(trace);
				map.put("resultCode", "1");
				map.put("message", "拨打成功");
				map.put("data", ret);
			} else {
				map.put("resultCode", "0");
				map.put("message", "拨打失败");
				map.put("data", ret);
			}
		}

		return map;
	}

	/**
	 * 通过护士服务id获取患者电话
	 * 
	 * @param vspId
	 * @return
	 */
	private String getPatientTelByServiceId(String vspId) {

		VServiceProcess process = vServiceProcessDao
				.getVServiceProcessBean(vspId);

		if (null == process) {
			throw new ServiceException("获取患者电话失败！");
		}
		String orderId = process.getOrderId();

		if (StringUtils.isEmpty(orderId)) {
			throw new ServiceException("获取患者电话失败！");
		}

		String toTel = nurseOrderDao.getPatientTelById(orderId);

		return toTel;
	}

	@Override
	public Map<String, Object> sendMessage(String messageId, String content,
			String vspId, Integer userId) {
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 通过护士服务id获取患者电话
		 */
		String phone = getPatientTelByServiceId(vspId);

		if (StringUtils.isEmpty(phone)) {
			throw new ServiceException("患者电话为空，发送短信失败！");
		}

		User nurse = userRepository.getUser(userId);
		String nurseName = "";//护士姓名
		if (null != nurse) {
			nurseName = nurse.getName();
		}
		String  sendMesageContent =nurseName+ Constant.MESSAGE_NURSE_TO_PATIENT() + content;
		try {
			if (StringUtils.isNotEmpty(messageId)) {
				Message message = messageDao.getMessageById(messageId);
				if (null != message) {
					mobSmsSdk.send(phone, sendMesageContent);// 发送短信
					VSPTracking trace = new VSPTracking();
					trace.setCreateTime(new Date().getTime());
					trace.setSms_content(content);
					trace.setCode(ExceptionEnum.Business_code_400.getIndex());
					trace.setServiceId(vspId);
					vspTrackingDao.insertVSPTracking(trace);

					result.put("resultCode", "1");
					result.put("message", "短信发送成功");
				}
			} else {
				mobSmsSdk.send(phone, sendMesageContent);// 这里是一个数字串

				Message mess = new Message();
				mess.setContent(content);
				mess.setRemarks("");
				mess.setTime(new Date().getTime());
				mess.setUserId(userId);
				mess.setType(0);
				messageDao.insertUserMessage(mess);// 插入记录

				VSPTracking trace = new VSPTracking();
				trace.setCreateTime(new Date().getTime());
				trace.setCode(ExceptionEnum.Business_code_400.getIndex());
				trace.setServiceId(vspId);
				trace.setPatientTel(phone);
				trace.setSms_content(content);
				vspTrackingDao.insertVSPTracking(trace);
				// result.put("result", messageResult);
				result.put("resultCode", "1");
				result.put("message", "短信发送成功");
			}
		} catch (Exception e) {
			result.put("resultCode", "40010");
			result.put("message", "发送短信失败！");
		}
		return result;
	}

	@Override
	public Map<String, Object> getTrackingServiceByOrderId(String orderId) {
		// TODO Auto-generated method stub
		return null;
	}

}
