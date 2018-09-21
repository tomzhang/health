package com.dachen.line.stat.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.checkbill.entity.po.CheckBill;
import com.dachen.health.checkbill.service.CheckBillService;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.line.stat.comon.constant.ExceptionEnum;
import com.dachen.line.stat.comon.constant.LineServiceTypeEnum;
import com.dachen.line.stat.comon.constant.VServiceProcessFromEnum;
import com.dachen.line.stat.comon.constant.VServiceProcessStatusEnum;
import com.dachen.line.stat.dao.ILineServiceDao;
import com.dachen.line.stat.dao.ILineServiceProductDao;
import com.dachen.line.stat.dao.INurseOrderConditionDao;
import com.dachen.line.stat.dao.INurseOrderDao;
import com.dachen.line.stat.dao.INurseUserDao;
import com.dachen.line.stat.dao.IUserHospitalDao;
import com.dachen.line.stat.dao.IVSPTrackingDao;
import com.dachen.line.stat.dao.IVServiceProcessDao;
import com.dachen.line.stat.entity.param.NurseOrderParm;
import com.dachen.line.stat.entity.param.PalceOrderParam;
import com.dachen.line.stat.entity.vo.LineService;
import com.dachen.line.stat.entity.vo.LineServiceProduct;
import com.dachen.line.stat.entity.vo.NurseDutyTime;
import com.dachen.line.stat.entity.vo.NurseOrderDetail;
import com.dachen.line.stat.entity.vo.PatientOrder;
import com.dachen.line.stat.entity.vo.UserHospital;
import com.dachen.line.stat.entity.vo.UserLineService;
import com.dachen.line.stat.entity.vo.VSPTracking;
import com.dachen.line.stat.entity.vo.VServiceProcess;
import com.dachen.line.stat.service.ICheckResultsService;
import com.dachen.line.stat.service.IOrderService;
import com.dachen.line.stat.service.IUserLineService;
import com.dachen.line.stat.service.IUserServiceTimeService;
import com.dachen.line.stat.util.ConfigUtil;
import com.dachen.line.stat.util.Constant;
import com.dachen.line.stat.util.DateUtils;
import com.dachen.line.stat.util.OutServiceHelper;
import com.dachen.util.ReqUtil;
import com.mobsms.sdk.MobSmsSdk;

@Service(OrderServiceImpl.BEAN_ID)
public class OrderServiceImpl implements IOrderService {
	Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	public static final String BEAN_ID = "NurseOrderServiceImpl";
	@Autowired
	public IUserServiceTimeService userServiceTimeService;

	@Autowired
	public IUserLineService userLineService;

	@Autowired
	public INurseOrderConditionDao nurseOrderConditionDao;

	@Autowired
	public INurseOrderDao nurseOrderDao;

	@Autowired
	public ILineServiceProductDao lineServiceProductDao;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private IPatientService patientService;

	@Autowired
	private IVServiceProcessDao vServiceProcessDao;
	
	@Autowired
	private ICheckResultsService checkResultsService;

	@Autowired
	private ILineServiceDao lineServiceDao;

	@Autowired
	private IBaseDataDao baseDataDao;

	@Autowired
	private IUserHospitalDao userHospitalDao;

	@Autowired
	private INurseUserDao nurseUserDao;

	@Autowired
	public IVSPTrackingDao vspTrackingDao;

	@Autowired
	CheckBillService checkBillService;

	@Autowired
	UserManager userManagerImpl;

	@Autowired
	PatientMapper patientMapper;
	
	@Autowired
	private MobSmsSdk mobSmsSdk;

	/**
	 * 2.定义护士获取不到订单的状态，对应首页不同的状态 100：用户没有设置服务套餐（判断依据用户没有关联套餐）
	 * 101：用户没有设置时间（判断依据用户从未设置过服务时间，或是当前时间超过最大预约时间）
	 * 102：用户需要扩大设置时间范围（判断依据用户设置的最大的时间，距离当前的时间小于2天，则提醒设置扩大服务时间）
	 * 103：暂时没有订单（以上逻辑全部走通，根据第二步的基础信息匹配不到订单，或是系统中没有订单）
	 * 104：关闭了所有的服务，也没有时间（判断依据，所有的关联表的关闭状态）
	 * 
	 * 对应的文案： 100：恭喜您认证通过，您还需要设置服务才能接单哦 101：您需要设置服务时间，患者才能会找到您哦
	 * 102：您设置的服务时间只有N（非固定）天喽，快去再设置吧 103：订单来了我第一时间通知您 104：您的服务尚未开启
	 */
	public List<PatientOrder> getUserOrder(Integer userId) {

		return null;
	}

	private List<String> getNurseServiceTime(List<NurseDutyTime> timeList) {
		List<String> hospitalList = new ArrayList<String>();
		if (null != timeList && timeList.size() > 0) {
			for (NurseDutyTime time : timeList) {
				hospitalList.add(time.getTime());
			}
		}

		return hospitalList;
	}

	// 这里只有都打开的服务才可以作为查询条件
	private List<String> getNurseService(List<UserLineService> timeList) {
		List<String> serviceList = new ArrayList<String>();
		if (null != timeList && timeList.size() > 0) {
			for (UserLineService time : timeList) {
				serviceList.add(time.getLineServiceProduct().getId());
			}
		}

		return serviceList;
	}

	/**
	 * 
	 100：用户没有设置服务套餐（判断依据用户没有关联套餐） 101：用户没有设置时间（判断依据用户从未设置过服务时间，或是当前时间超过最大预约时间）
	 * 102：用户需要扩大设置时间范围（判断依据用户设置的最大的时间，距离当前的时间小于2天，则提醒设置扩大服务时间）
	 * 103：暂时没有订单（以上逻辑全部走通，根据第二步的基础信息匹配不到订单，或是系统中没有订单）
	 * 104：关闭了所有的服务，也没有时间（判断依据，所有的关联表的关闭状态） 105: 认证未通过
	 * 
	 * 对应的文案： 100：恭喜您认证通过，您还需要设置服务才能接单哦 101：您需要设置服务时间，患者才能会找到您哦
	 * 102：您设置的服务时间只有N（非固定）天喽，快去再设置吧 103：订单来了我第一时间通知您 104：您的服务尚未开启 105: 认证未通过
	 * 
	 * 获取护士可以接的订单 resultCode data PageVO
	 * 
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getNurseOrder(Integer userId, Integer pageNo,
			Integer pageSize) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int resultCode = 0;
		List<String> serviceList = null;
		List<String> timeList = null;
		List<String> hospitalList = null;
		List<String> departList = null;
		try {
			// 服务查询
			// Map<String, Object> serviceMap = userLineService
			// .checkUserServiceSet(userId);
			// resultCode = Integer.parseInt(serviceMap.get("resultCode")
			// .toString());
			// if (resultCode == NurseServiceOrderSetEnum.set_1.getIndex()) {
			// serviceList = getNurseService((List<UserLineService>) serviceMap
			// .get("data"));
			// } else {
			// resultMap.put("resultCode", serviceMap.get("resultCode"));
			// resultMap.put("message", serviceMap.get("message"));
			// return resultMap;
			// }
			// // 服务时间查询
			// Map<String, Object> timeMap = userServiceTimeService
			// .checkUserServiceTimeSet(userId);
			// resultCode =
			// Integer.parseInt(timeMap.get("resultCode").toString());
			//
			// if (resultCode == NurseServiceOrderSetEnum.set_1.getIndex()) {
			// timeList = getNurseServiceTime((List<NurseDutyTime>) timeMap
			// .get("data"));
			// } else {
			// resultMap.put("resultCode", timeMap.get("resultCode"));
			// resultMap.put("message", timeMap.get("message"));
			//
			// return resultMap;
			// }
			// 校验护士的状态
			boolean certFalg = nurseUserDao.checkUserCertStatus(userId);

			if (!certFalg) {
				resultMap.put("data", resultMap);
				resultMap.put("resultCode", 103);
				resultMap.put("message", "暂时没有订单!");

				return resultMap;
			}

			hospitalList = nurseOrderConditionDao
					.getNurseBelongHospital(userId);

			// 调用查询接口
			NurseOrderParm param = new NurseOrderParm();
			param.setDepartList(departList);
			param.setHospitalList(hospitalList);
			param.setServiceList(serviceList);
			param.setTimeList(timeList);
			param.setPageNo(pageNo);
			param.setPageSize(pageSize);
			List<PatientOrder> patientOrder = nurseOrderDao.getUserOrder(
					hospitalList, departList, timeList, serviceList);

			List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
			if (null != patientOrder && patientOrder.size() > 0) {
				setpatientOrderData(patientOrder, listMap, userId);
				resultMap.put("data", listMap);
				resultMap.put("resultCode", 1);
				resultMap.put("message", "查询订单成功！");
			} else {
				resultMap.put("data", listMap);
				resultMap.put("resultCode", 103);
				resultMap.put("message", "暂时没有订单!");
			}
			// filterNurseOrder(userId,patientOrder);//过滤订单 可以根据用户的一些状态进行过滤

			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 有新订单的时候 给时候的护士发送短信
	 * 
	 * @param hospitalList
	 */
	public void sendOrder(List<String> hospitalList) throws HttpApiException {
		List<User> users = nurseUserDao.getUserList(hospitalList);
		if (ConfigUtil.checkCollectionIsEmpty(users)) {
			String tel = null;
			for (User user : users) {
				tel = user.getTelephone();
				if (StringUtils.isNotEmpty(tel)) {
					String messageTemplate = dealPlaceOrderMessageTemplate(Constant.MESSAGE_PATIENT_PLACE_ORDER_SUCCESS());
					mobSmsSdk.send(tel, messageTemplate);
				}
			}
		}
		// 发送
	}

	@Autowired
	protected AsyncTaskPool asyncTaskPool;

	public void sendOrderAsync(List<String> hospitalList) {
		asyncTaskPool.getPool().submit(new Runnable() {
			@Override
			public void run() {
				try {
					sendOrder(hospitalList);
				} catch (HttpApiException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	public void setpatientOrderData(List<PatientOrder> patientOrders,
			List<Map<String, Object>> listMap, Integer userId) {
		if (ConfigUtil.checkCollectionIsEmpty(patientOrders)) {

			for (PatientOrder order : patientOrders) {
				String productId = order.getProductId();

				Map<String, Object> result = new HashMap<String, Object>();
				result.put("hospital", userRepository.getUser(userId)
						.getNurse().getHospital());// 这里是去护士的医院
				result.put("nurseTel", userRepository.getUser(userId)
						.getTelephone());// 这里是去护士的医院
				result.put("orderId", order.getId());
				result.put("doctorName", order.getDoctorName());// 查询医生的名称
				List<String> depart = order.getDepartList();
				if (ConfigUtil.checkCollectionIsEmpty(depart)) {

					result.put("doctorDepart",
							getDepartStr(getDepartNameList(depart)));// 医生的科室
				}
				if (StringUtils.isNotEmpty(productId)) {
					LineServiceProduct product = order.getProduct();
					if (null != product) {
						result.put("productTitle", product.getTitle());
						result.put("price", product.getPrice());
					}

				}
				String orderTime = order.getAppointmentTime();
				result.put("appointmentTime", orderTime);
				Map<String, Object> orderTimeMap = new HashMap<String, Object>();

				orderTimeMap.put("orderDate",
						order.getDay() + " " + order.getWeek());
				orderTimeMap.put("orderHours", order.getHours());
				orderTimeMap.put("timeAgo", order.getTimeAgo());
				orderTimeMap.put("orderCreateTime", DateUtils.formatDate2Str(
						order.getTime(), DateUtils.FORMAT_YYYY_MM_DD_HH_MM_SS));
				result.put("showTime", orderTimeMap);
				// result.put("patientName", order.getPatientName());
				// result.put("patientTel", order.getPatientTel());
				// User patient = userRepository.getUser(order.getPatientId());
				Patient patient = patientService.findByPk(order.getPatientId());
				if (null != patient) {
					result.put("patientHeadPicFileName", patient.getTopPath());
					result.put("patientName", patient.getUserName());
					result.put("patientTel", patient.getTelephone());
				}
				List<Map<String, Object>> checkItem = order.getLineServiceMap();
				result.put("checkItem", checkItem);
				listMap.add(result);
			}

		}

	}

	/**
	 * 查询医院的名称
	 * 
	 * @return
	 */
	private String getDepartStr(List<String> items) {
		StringBuffer buffer = new StringBuffer();
		if (ConfigUtil.checkCollectionIsEmpty(items)) {
			for (int i = 0; i < items.size(); i++) {
				String line = items.get(i);
				if (null != line) {
					if (i < items.size() - 1) {
						buffer.append(line).append(",");
					} else {
						buffer.append(line);
					}
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * 抢单
	 */
	@Override
	public Map<String, Object> getTheOrder(Integer userId, String orderId) {

		boolean isFirst = true;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			PatientOrder order = nurseOrderDao.getPatientOrderById(orderId);
			if (null == order) {
				throw new ServiceException("订单不存在！");
			}

			Integer jsonStatus = OutServiceHelper.getOrderStatus(
					ReqUtil.instance.getToken(), order.getBasicId());

			// 订单状态必须是 已经支付才可以 给护士看 进行抢单
			if (jsonStatus == OrderEnum.OrderStatus.已支付.getIndex()) {

				// 更新基础订单 为 预约成功
				OutServiceHelper.updateOrderStatus(ReqUtil.instance.getToken(),
						order.getBasicId(), OrderStatus.预约成功.getIndex());
				// 更新订单状态
				// nurseOrderDao.updatePatientOrderStatus(orderId,
				// OrderStatusEnum.toUploadResult.getIndex());

				boolean result = vServiceProcessDao.checkVServiceProcessBean(
						orderId, userId);
				if (!result) {
					VServiceProcess process = new VServiceProcess();
					process.setOrderId(orderId);
					process.setFrom(VServiceProcessFromEnum.nurse_type
							.getIndex());
					process.setStatus(VServiceProcessStatusEnum.toStartOrder
							.getIndex());
					process.setTime(new Date().getTime());
					process.setUserId(userId);
					// 插入护士流程日志
					vServiceProcessDao.insertVServiceProcess(process);
					// 如果记录大于1 就标示鄙视第一次接单
					List<VServiceProcess> processList = vServiceProcessDao
							.getVServiceProcessListById(userId);
					if (null != processList && processList.size() >= 2) {
						isFirst = false;
						resultMap.put("isFirst", isFirst);
					}
					String messageTemplate =null;
					try {
						messageTemplate = dealMessageTemplate(
								Constant.MESSAGE_NURSE_GRAP_ORDER_SUCCESS(),
								userId);
						// 护士抢单成功之后，给患者推送的短信末班
						mobSmsSdk.send(order.getPatientTel(), messageTemplate);
						List<String> patientIds = new ArrayList<String>();
						patientIds.add(String.valueOf(order.getUserId()));
						// Helper.push(messageTemplate, patientIds);// 推送消息
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("护士抢单成功，给患者发短信失败！订单id=" + orderId);
					}

					resultMap.put("resultCode", 1);
					resultMap.put("message", "恭喜你抢单成功");

					// 修改检查单结果
					String checkId = order.getCheckId();
					if (StringUtils.isNotEmpty(checkId)) {
						OutServiceHelper.updateCheckbill(ReqUtil.instance.getToken(),
								checkId, "3");
					}

					// VServiceProcess vsp =
					// vServiceProcessDao.getVServiceInfoByOrderId(order.getId());
					VSPTracking trac = new VSPTracking();
					trac.setCreateTime(new Date().getTime());
					trac.setAppointmentTime(order.getAppointmentTime());
					trac.setOrderId(orderId);
					trac.setCode(ExceptionEnum.Business_code_200.getIndex());
					trac.setPatientId(Integer.toString(order.getPatientId()));
					trac.setSms_content(messageTemplate);
					// trac.setServiceId(vsp.getId());
					trac.setPatientTel(order.getPatientTel());
					vspTrackingDao.insertVSPTracking(trac);
					//

				} else {
					resultMap.put("resultCode", 0);
					resultMap.put("message", "你已经抢到此单，无须再重复抢了！");
				}

			} else {
				resultMap.put("resultCode", 0);
				resultMap.put("message", "这个订单被抢走啦，下次手快点哦");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("resultCode", 0);
			resultMap.put("message", "这个订单被抢走啦，下次手快点哦");
			throw new ServiceException("对不起，服务器出现异常了！");
		}

		return resultMap;
	}

	@Override
	public void filterNurseOrder(Integer userId, List<PatientOrder> patientOrder) {
		// TODO Auto-generated method stub

	}

	/**
	 * 替换短信末班的相关内容
	 * 
	 * @param template
	 * @param userId
	 * @return
	 */
	private String dealMessageTemplate(String template, Integer userId) {
		User user = userRepository.getUser(userId);
		String hospital = user.getNurse().getHospital();
		String nurseTel = user.getTelephone();
		String nurseName = user.getName();
		String messageTemplate = template.replace("HL", hospital)
				.replace("NURSE", nurseName).replace("NE_TL", nurseTel)
				.replace("SER_TL", Constant.CUSTOMER_SERVICE_TELEPHONE());

		return messageTemplate;
	}

	@Override
	public Map<String, Object> insertUserOrder(PalceOrderParam param) {
		Map<String, Object> map = new HashMap<String, Object>();
		long price = 0;
		String basicId = null;
		String productTitle = null;
		String checkId = param.getCheckId();
		PatientOrder order = new PatientOrder();
		order.setAppointmentTime(param.getAppointmentTime());
		order.setDoctorName(param.getDoctorName());
		order.setDoctorId(param.getDoctorId());

		Integer patientId = param.getPatientId();
		if (null == patientId) {
			throw new ServiceException("就诊人Id不能够为空！");
		}
		Patient user = patientService.findByPk(patientId);
		if (null != user) {
			order.setPatientName(user.getUserName());
		}
		else
		{	
			throw new ServiceException("就诊人信息不存在！");
		}
		order.setUserId(ReqUtil.instance.getUserId());
		String patientTel = param.getPatientTel();
		if (StringUtils.isEmpty(patientTel)) {
			throw new ServiceException("就诊人电话不能够为空！");
		}
		order.setPatientTel(patientTel);
		order.setPatientId(param.getPatientId());
		order.setCheckId(checkId);
		LineServiceProduct product = null;

		String productId = param.getProductId();
		if (StringUtils.isNotEmpty(productId)) {
			order.setProductId(param.getProductId());
			product = lineServiceProductDao.getSystemLineServiceBean(productId);
			if (null == product) {
				product = lineServiceProductDao
						.getSystemLineServiceBean(Constant.CHECK_UP_PRODUCT_ID());
				order.setProductId(Constant.CHECK_UP_PRODUCT_ID());
			}
			if (null != product) {
				order.setPrice(product.getPrice());
				productTitle = product.getTitle();
				order.setProduct(product);// 保存产品套餐
			}
		} else {
			throw new ServiceException("套餐id不能为空！");
		}
		order.setBasicId("1100001");
		order.setTime(new Date().getTime());
		order.setRemark(param.getRemark());
		order.setStatus(10);
		// 保存检查项id
		List<String> checkIds = param.getCheckItem();
		List<LineService> lines = new ArrayList<LineService>();
		if (ConfigUtil.checkCollectionIsEmpty(checkIds)) {

			for (String checkItemId : checkIds) {
				LineService line = new LineService();
				CheckSuggest checkSuggest = lineServiceDao
						.getCheckSuggestById(checkItemId);
				line.setBasicId(checkItemId);
				line.setTitle(checkSuggest.getName());
				line.setType(LineServiceTypeEnum.Check.getIndex());
				lines.add(line);
			}
			order.setLineList(lines);
		} else {
			LineService line = new LineService();
			line.setBasicId(String.valueOf(System.currentTimeMillis()));
			line.setTitle(productTitle == null ? "加号" : productTitle);
			line.setType(LineServiceTypeEnum.jiaHao.getIndex());
			lines.add(line);
			order.setLineList(lines);
		}
		// 保存科室
		List<String> departs = param.getDepartment();
		if (ConfigUtil.checkCollectionIsEmpty(departs)) {
			order.setDepartList(departs);
		}
		String remarks = null;
		// 保存医院
		List<String> hospitals = param.getHospital();

		if (ConfigUtil.checkCollectionIsEmpty(hospitals)) {
			order.setHospitalList(hospitals);
			remarks = getHospitalStr(hospitals);
		}
		Object id = nurseOrderDao.insertOrder(order);
		if (null != id) {

			List<UserHospital> hospitalList = new ArrayList<UserHospital>();
			setHospitalStr(hospitals, hospitalList, id.toString());// 保存医院订单关系
			userHospitalDao.insertBatchUserHospital(hospitalList);// 保存医院订单关系
			price = (long) product.getPrice();

			// OutServiceHelper.throughTrainOrder( param.getDoctorId(),
			// param.getPatientId(), product.getProductType(), price,
			// remarks, order.getPatientTel());

			// 生成基础订单
			JSONMessage response = OutServiceHelper.throughTrainOrder(
					ReqUtil.instance.getToken(), param.getDoctorId(),
					param.getPatientId(), product.getProductType(), price,
					remarks, order.getPatientTel());
			if (null != response) {
				Integer resultCode = (Integer) response.get("resultCode");
				if (resultCode == 1) {
					if (null != response.getData()) {
						JSONObject object = (JSONObject) response.getData();
						Object idObject = object.get("id");
						if (null != idObject) {
							basicId = idObject.toString();
						}
					}
				} else {
					throw new ServiceException("生成基础订单失败！");
				}
			}
			// 讲基础订单id 回写到业务订单中
			nurseOrderDao.updatePatientOrderBasicId(id.toString(), basicId);
		}
		map.put("id", basicId);
		map.put("price", price);
		map.put("productTitle", productTitle);

		return map;
	}

	private void setHospitalStr(List<String> hospitals,
			List<UserHospital> hospitalList, String orderId) {
		for (int i = 0; i < hospitals.size(); i++) {
			UserHospital hos = new UserHospital();

			String hospitalId = hospitals.get(i);

			if (StringUtils.isNotEmpty(hospitalId)) {
				hos.setHospitalId(hospitalId);
				hos.setSourceId(orderId);
				hospitalList.add(hos);
			}
		}
	}

	/**
	 * 查询医院的名称
	 * 
	 * @param hospitals
	 * @return
	 */
	private String getHospitalStr(List<String> hospitals) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < hospitals.size(); i++) {
			String hospitalId = hospitals.get(i);
			if (StringUtils.isNotEmpty(hospitalId)) {
				HospitalVO bo = baseDataDao.getHospital(hospitalId);
				if (null != bo) {
					if (i < hospitals.size() - 1) {
						buffer.append(bo.getName()).append(",");
					} else {
						buffer.append(bo.getName());
					}
				}

			}
		}
		return buffer.toString();
	}

	/**
	 * 查询医院的名称
	 * 
	 * @param hospitals
	 * @return
	 */
	private List<String> getHospitalNameList(List<String> hospitals) {
		List<String> hospitalNames = new ArrayList<String>();
		for (int i = 0; i < hospitals.size(); i++) {
			String hospitalId = hospitals.get(i);
			if (StringUtils.isNotEmpty(hospitalId)) {
				HospitalVO bo = baseDataDao.getHospital(hospitalId);
				if (null != bo) {
					hospitalNames.add(bo.getName());
				}

			}
		}
		return hospitalNames;
	}

	/**
	 * 查询医院的名称
	 * 
	 * @return
	 */
	private List<String> getDepartNameList(List<String> departs) {

		List<String> deptNames = new ArrayList<String>();

		List<DeptVO> deptVoList = baseDataDao.getDeptByIds(departs);
		if (null != deptVoList && deptVoList.size() > 0) {
			for (int i = 0; i < deptVoList.size(); i++) {
				DeptVO deptVO = deptVoList.get(i);
				if (null != deptVO) {
					deptNames.add(deptVO.getName());
				}
			}
		}

		return deptNames;
	}

	/**
	 * 
	 * @param userId
	 *            true false
	 */
	@Override
	public void cancleUserOrder(String orderId, Integer userId) {
		if (StringUtils.isEmpty(orderId)) {
			throw new ServiceException("订单id不能够为空！");
		}
		PatientOrder order = nurseOrderDao.getPatientOrderById(orderId);
		if (null == order) {
			throw new ServiceException("订单不存在！");
		}

		// 获取订单状态 进行业务判断
		Integer status = OutServiceHelper.getOrderStatus(ReqUtil.instance.getToken(),
				order.getBasicId());
		if (null != status) {
			if (status == OrderEnum.OrderStatus.已完成.getIndex()) {
				throw new ServiceException("订单已完成，不可以取消");
			}
			if (status == OrderEnum.OrderStatus.已取消.getIndex()) {
				throw new ServiceException("取消订单失败，订单已经取消");
			}
		} else {
			throw new ServiceException("获取订单状态错误");
		}
		/**
		 * 取消订单 调用基础订单接口 取消订单
		 */
		JSONMessage json = OutServiceHelper.cancelThroughTrainOrder(
				ReqUtil.instance.getToken(), order.getBasicId());
		Object resultCode = json.getResultCode();

		if (null != resultCode) {
			Integer code = Integer.parseInt(resultCode.toString());
			if (code != 1) {
				throw new ServiceException("取消订单失败");
			}
		}
		String messageTemplate = "";
		// 取消订单 发送对应的短信和 推送消息
		VServiceProcess isInService = vServiceProcessDao
				.getVServiceProcessBeanByOrderId(order.getId());
		if (null != isInService) {
			Integer nurseId = isInService.getUserId();
			User user = userRepository.getUser(nurseId);
			if (null != user) {
				try {
					// 发送
					 messageTemplate = dealCancleOrderMessageTemplate(
							Constant.MESSAGE_PATIENT_CANCLE_ORDER_SUCCESS(),
							order);
					 mobSmsSdk.send(user.getTelephone(), messageTemplate);
					// 结束护士服务流程
					vServiceProcessDao.updateVServiceProcess(
							isInService.getId(),
							VServiceProcessStatusEnum.toClosd.getIndex());//VServiceProcessStatusEnum.toClosd.getIndex() 这里直接关闭
					// 推送消息
					List<String> userIds = new ArrayList<String>();
					userIds.add(String.valueOf(nurseId));
					// Helper.push(messageTemplate, userIds);// 推送消息
				} catch (Exception e) {
					logger.error("The cancleUserOrder errors  =" + e.toString());
				}
				
			}
		}

		try {
			VSPTracking trac = new VSPTracking();
			trac.setCreateTime(new Date().getTime());
			trac.setAppointmentTime(order.getAppointmentTime());
			trac.setOrderId(order.getId());
			trac.setCode(ExceptionEnum.Business_code_900.getIndex());
			trac.setPatientId(Integer.toString(order.getPatientId()));
			if (null != isInService) {
				trac.setServiceId(isInService.getId());
			}
			trac.setPatientTel(order.getPatientTel());
			trac.setSms_content(messageTemplate);
			vspTrackingDao.insertVSPTracking(trac);

			// 取消订单 更新 检查状态 为 1 检查单可以重新下单
			String checkId = order.getCheckId();
			if (StringUtils.isNotEmpty(checkId)) {
				CheckBill checkBill = new CheckBill();
				checkBill.setCheckBillStatus(1);
				checkBill.setId(checkId);
				checkBillService.updateCheckbill(checkBill);
			}
		} catch (Exception e) {
			logger.error("The cancleUserOrder errors  ="
					+ e.toString());
		}

	}

	/**
	 * 1 可以取消 2.可以取消 不够钱    3.可以取消 但是会扣50%  4不能够取消
	 */
	@Override
	public Map<String, Object> checkCancleUserOrder(String orderId) {
		Map<String, Object> map = new HashMap<String, Object>();
		int reuslt = 1;// 可以取消
		if (StringUtils.isEmpty(orderId)) {
			throw new ServiceException("订单id不能够为空！");
		}
		PatientOrder order = nurseOrderDao.getPatientOrderById(orderId);

		if (null == order) {
			throw new ServiceException("订单不存在！");
		}
		double price = order.getPrice();
		// 获取订单状态 进行业务判断
		Integer status = OutServiceHelper.getOrderStatus(ReqUtil.instance.getToken(),
				order.getBasicId());
		if (null != status) {
			if (status == OrderEnum.OrderStatus.已完成.getIndex()) {
				 throw new ServiceException(4,"订单已完成，不可以取消");
//				map.put("code", 4);
//				map.put("price", 0);
//				return map;
			}
			if (status == OrderEnum.OrderStatus.已取消.getIndex()) {
				throw new ServiceException(4,"取消订单失败，订单已经取消");
//				map.put("code", 4);
//				map.put("price", 0);
//				return map;
			}
		} else {
			throw new ServiceException(3,"获取订单状态错误");
//			map.put("code", 4);
//			map.put("price", 0);
//			return map;
		}

		
		VServiceProcess isInService = vServiceProcessDao
				.getVServiceProcessBeanByOrderId(order.getId());
		String orderTime = order.getAppointmentTime();
		boolean checkTime = false;
		try {
			checkTime = checkOrderTime(orderTime);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("预约时间格式错误!");
		}
		// 订单已经被抢的就需要判断预当前时间是不是大于预约时间的前天的10点
		if (null == isInService) {
			reuslt = 1;
		} else {// 没有被抢
			// 护士已经抢单 这里就需要判断 是不是 十点以前 如果是 全额退款 如果不是 50% 退款
			if (isInService.getStatus() == VServiceProcessStatusEnum.toStartOrder
					.getIndex()) {
				// 如果在十点以前 是可以取消订单
				if (!checkTime) {
					reuslt = 3;
				} else {
					reuslt = 2;
				}
			} else// 已经点击开始服务 不允许 取消（订单在进行中）
			{
				reuslt = 4;
			}

		}
		map.put("code", reuslt);
		map.put("price", price);
		return map;
	}

	/**
	 * true 可以取消 否则 不可以取消
	 * 
	 * @param orderTimeStr
	 * @return
	 */
	private boolean checkOrderTime(String orderTimeStr) {
		if (StringUtils.isNotEmpty(orderTimeStr)) {
			Date d = new Date(DateUtils.toDate(orderTimeStr).getTime() - 1000
					* 60 * 60 * 24);
			String dayStr = DateUtils.formatDate2Str(d,
					DateUtils.FORMAT_YYYY_MM_DD);
			dayStr = dayStr + " 22:00:00";

			long orderTimes = DateUtils.toTimestamp(dayStr);
			if (orderTimes > new Date().getTime()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public void callBackBasicOrder(String basicOrderId) {

		logger.debug("Entry The method callBackBasicOrder ,the param basicOrderId="
				+ basicOrderId);
		PatientOrder order = null;
		if (StringUtils.isEmpty(basicOrderId)) {
			logger.info("订单id为空！");
			return;
		}
		order = nurseOrderDao.getPatientBasicOrderById(basicOrderId);
		if (null == order) {
			logger.info("订单不存在");
			return;
		}
		LineServiceProduct product = lineServiceProductDao
				.getSystemLineServiceBean(order.getProductId());
		if (null == product) {
			logger.info("患者服务套餐不存在！");
			return;
		}
		String messageTemplate ="";
		if (StringUtils.isNotEmpty(order.getPatientTel())) {
			// 下单给患者发短信
			 messageTemplate = dealPayOrderMessageTemplate(
					Constant.MESSAGE_PATIENT_PAY_ORDER_SUCCESS(), product);
			logger.info("messageTemplate=" + messageTemplate);
			mobSmsSdk.send(order.getPatientTel(), messageTemplate);
		} else {
			logger.info("就诊人电话为空");
		}
		VSPTracking trac = new VSPTracking();
		trac.setCreateTime(new Date().getTime());
		trac.setAppointmentTime(order.getAppointmentTime());
		trac.setOrderId(order.getId());
		trac.setSms_content(messageTemplate);//记录发送的短信
		trac.setCode(ExceptionEnum.Business_code_100.getIndex());
		trac.setPatientId(Integer.toString(order.getPatientId()));
		trac.setPatientTel(order.getPatientTel());
		vspTrackingDao.insertVSPTracking(trac);
		// 给护士发短信
		sendOrderAsync(order.getHospitalList());// 派单

		String checkId = order.getCheckId();

		if (StringUtils.isNotEmpty(checkId)) {
			CheckBill checkBill = new CheckBill();
			checkBill.setCheckBillStatus(2);
			checkBill.setId(checkId);
			checkBillService.updateCheckbill(checkBill);
			// OutServiceHelper.updateCheckbill(checkId, "2");
		}
		logger.debug("Exit The method callBackBasicOrder ,the param basicOrderId=");
	}

	/**
	 * 更新订单状态
	 */
	@Override
	public void endAppraise(String orderId, Integer status) {
		if (StringUtils.isEmpty(orderId)) {
			throw new ServiceException("订单id不能够为空！");
		}
		PatientOrder order = nurseOrderDao.getPatientOrderById(orderId);
		if (null == order) {
			throw new ServiceException("订单不能够存在！");
		}
		VServiceProcess isInService = null;
		Integer jsonStatus = OutServiceHelper.getOrderStatus(
				ReqUtil.instance.getToken(), order.getBasicId());
		if (null != jsonStatus) {
			if (jsonStatus != OrderEnum.OrderStatus.进行中.getIndex()) {
				throw new ServiceException("服务未完成，无法确认!");
			} else {
				isInService = vServiceProcessDao
						.getVServiceProcessBeanByOrderId(order.getId());
				if (null == isInService) {
					throw new ServiceException("护士服务不存在！");
				} else {
					// 判断护士 是不是已经上传了 检查结果 如果没有 就不给患者 确认服务
					if (isInService.getStatus() != VServiceProcessStatusEnum.end
							.getIndex()) {
						throw new ServiceException("检查结果还没有上传,无法进行订单确认！");
					}
				}
			}
		}
		if (null != isInService) {
			Integer nurseId = isInService.getUserId();
			User user = userRepository.getUser(nurseId);
			if (null != user) {
				try {
					// 发送
					String messageTemplate = dealendAppraiseMessageTemplate(
							Constant.MESSAGE_PATIENT_END_APPRAISE_THE_SERVICE(),
							order);
					mobSmsSdk.send(user.getTelephone(), messageTemplate);
					VServiceProcess vsp = vServiceProcessDao
							.getVServiceInfoByOrderId(order.getId());
					VSPTracking trac = new VSPTracking();
					trac.setCreateTime(new Date().getTime());
					trac.setSms_content(messageTemplate);
					trac.setAppointmentTime(order.getAppointmentTime());
					trac.setOrderId(order.getId());
					trac.setCode(ExceptionEnum.Business_code_800.getIndex());
					trac.setPatientId(Integer.toString(order.getPatientId()));
					if (vsp != null) {
						trac.setServiceId(vsp.getId());
					}
					trac.setPatientTel(order.getPatientTel());
					vspTrackingDao.insertVSPTracking(trac);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		// 更新基础订单状态
		OutServiceHelper.updateOrderStatus(ReqUtil.instance.getToken(),
				order.getBasicId(), OrderStatus.已完成.getIndex());
	}

	/**
	 * 结束评价模板
	 * 
	 * @param template
	 * @return
	 */
	private String dealendAppraiseMessageTemplate(String template,
			PatientOrder order) throws HttpApiException {
		LineServiceProduct product = lineServiceProductDao
				.getSystemLineServiceBean(order.getProductId());
		String productTitle = product.getTitle();
		String patientName = order.getPatientName();

		if (StringUtils.isEmpty(patientName)) {
			Integer patientId = order.getPatientId();
			Patient patient = patientService.findByPk(patientId);

			if (null != patient) {
				patientName = patient.getUserName();
			}
		}

		double price = order.getPrice();

		String messageTemplate = template.replace("Name", patientName)
				.replace("Service", productTitle)
				.replace("Money", String.valueOf(price))
				.replace("APP_LINK", APP_NURSE_CLIENT_LINK());

		return messageTemplate;
	}

	/**
	 * 取消订单模板
	 * 
	 * @param template
	 * @return
	 */
	private String dealCancleOrderMessageTemplate(String template,
			PatientOrder order) throws HttpApiException {
		LineServiceProduct product = lineServiceProductDao
				.getSystemLineServiceBean(order.getProductId());
		String productTitle = product.getTitle();

		String patientName = order.getPatientName();

		if (StringUtils.isEmpty(patientName)) {
			Patient user = patientService.findByPk(order.getPatientId());

			if (null != user) {
				patientName = user.getUserName();
			}
		}

		String messageTemplate = template.replace("Name", patientName)
				.replace("Service", productTitle)
				.replace("APP_LINK", APP_NURSE_CLIENT_LINK());

		return messageTemplate;
	}

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	public String APP_NURSE_CLIENT_LINK () throws HttpApiException {
		String nurseLink = PropertiesUtil
				.getContextProperty("app.nurse.client.link");
		String shorUrl = shortUrlComponent.generateShortUrl(nurseLink);
		return shorUrl;
	}

	/**
	 * 下单模板
	 * 
	 * @param template
	 * @return
	 */
	private String dealPlaceOrderMessageTemplate(String template) throws HttpApiException {
		String messageTemplate = template.replace("APP_LINK",
				APP_NURSE_CLIENT_LINK());
		return messageTemplate;
	}

	/**
	 * 下单模板
	 * 
	 * @param template
	 * @return
	 */
	private String dealPayOrderMessageTemplate(String template,
			LineServiceProduct product) {
		String messageTemplate = template
				.replace("Service", product.getTitle());
		return messageTemplate;
	}

	/*
	 * @Override public void getOderOvertopHour() { logger.debug("任务开始");
	 * List<VSPTracking> list = nurseOrderDao.getOderOvertopHour();
	 * logger.debug("任务结束"); }
	 */

	@Override
	public Map<String, Object> getOrderDetail(String orderId) {
		if (StringUtils.isEmpty(orderId)) {
			throw new ServiceException("订单id不能够为空！");
		}
		Map<String, Object> result = new HashMap<String, Object>();

		PatientOrder order = nurseOrderDao.getPatientBasicOrderById(orderId);
		if (null == order) {
			throw new ServiceException("订单不存在");
		}
		LineServiceProduct product = null;
		product = order.getProduct();
		if (null != product) {
			result.put("productTitle", product.getTitle());
			result.put("productPrice", product.getPrice());
			result.put("productType", product.getProductType());//
			// 5就医直通车、6专家直通车、7检查直通车
		}

		result.put("serviceOrderId", order.getId());// 业务订单id
		result.put("orderId", order.getBasicId());// 基础订单id

		result.put("patientId", order.getPatientId());

		JSONMessage json = OutServiceHelper.detail(ReqUtil.instance.getToken(),
				order.getBasicId());
		Object status = null;
		Object orderNo = null;
		Object age = null;
		Object preOrderStatus = null;
		if (null != json) {
			int resultCode = json.getInteger("resultCode");
			if (resultCode == 1) {
				status = json.getData();
				if (null != status) {
					JSONObject statusObject = (JSONObject) status;
					Object orderVo = statusObject.get("orderVo");
					if (null != orderVo) {
						JSONObject orderVoObject = (JSONObject) orderVo;
						status = orderVoObject.get("orderStatus");
						orderNo = orderVoObject.get("orderNo");
						preOrderStatus = orderVoObject.get("preOrderStatus");

					}
				}

			} else {
				result.put("orderStatus", null);
				throw new ServiceException("查询订单状态失败！");
			}
		}
		result.put("doctorName", order.getDoctorName());// 专家名称
		result.put("orderNo", orderNo);
		result.put("orderStatus", status);
		result.put("preOrderStatus", preOrderStatus);
		List<Map<String, Object>> checkResultMap = checkResultsService
				.getCheckResultsServiceMapList(order.getId());
		result.put("result", checkResultMap);
		List<String> names = getHospitalNameList(order.getHospitalList());
		result.put("hospitalList", names);
		result.put("departList", getDepartNameList(order.getDepartList()));
		String orderTime = order.getAppointmentTime();
		result.put("orderTime", orderTime);
		List<LineService> lines = order.getLineList();
		result.put("checkItem", getCheckItemStr(lines));
		result.put("remark", order.getRemark());
		Integer patientId = order.getPatientId();
		Patient user = patientService.findByPk(patientId);
		// User user = userRepository.getUser(patientId);
		if (null != user) {
			result.put("name", user.getUserName());
			result.put("sex", user.getSex());// 性别1男，2女 3 保密
			result.put("age", user.getAge());
		}

		return result;
	}

	/**
	 * 查询医院的名称
	 * 
	 * @return
	 */
	private String getCheckItemStr(List<LineService> items) {
		StringBuffer buffer = new StringBuffer();
		if (ConfigUtil.checkCollectionIsEmpty(items)) {
			for (int i = 0; i < items.size(); i++) {
				LineService line = items.get(i);
				if (null != line) {
					if (i < items.size() - 1) {
						buffer.append(line.getTitle()).append(",");
					} else {
						buffer.append(line.getTitle());
					}
				}
			}
		}
		return buffer.toString();
	}

	private Integer getOrderStatus(JSONMessage jsonStatus) {
		Integer status = null;
		if (null != jsonStatus) {
			int resultCode = jsonStatus.getInteger("resultCode");
			if (resultCode == 1) {
				status = Integer.parseInt(jsonStatus.getData().toString());

			} else {
				throw new ServiceException("查询订单状态失败！");
			}
		}
		return status;
	}

	@Override
	public NurseOrderDetail getThroughTrainOrderDetail(String orderId) {
		NurseOrderDetail nod = new NurseOrderDetail();
		Map<String, Object> map = getOrderDetail(orderId + "");
		if (map == null) {
			throw new ServiceException("查询订单失败！");
		}
		VServiceProcess vp = vServiceProcessDao
				.getVServiceProcessBeanByOrderId(String.valueOf(map
						.get("serviceOrderId")));
		if (vp != null) {
			Integer userId = vp.getUserId();
			User user = userManagerImpl.getUser(userId);
			String nurseName = user.getName();
			String telephone = user.getTelephone();
			nod.setNurseName(nurseName);
			nod.setNurseTelephone(telephone);
		}
		nod.setOrderNo(String.valueOf(map.get("orderNo")));
		nod.setOrderType(String.valueOf(OrderEnum.OrderType.throughTrain
				.getIndex()));
		nod.setAttachmentDoctorName(String.valueOf(map.get("doctorName")));

		if (map.get("patientId") != null) {
			int id = Integer.valueOf(map.get("patientId").toString());
			Patient patient = patientMapper.selectByPrimaryKey(id);
			nod.setPatientName(patient.getUserName());
			nod.setPatientTelephone(patient.getTelephone());
		}

		nod.setVisitingTime(String.valueOf(map.get("orderTime")));
		nod.setCheckItems(String.valueOf(map.get("checkItem")));
		nod.setMessage(String.valueOf(map.get("remark")));

		nod.setHospitalList(map.get("hospitalList"));
		nod.setDepartList(map.get("departList"));

		return nod;
	}
}
