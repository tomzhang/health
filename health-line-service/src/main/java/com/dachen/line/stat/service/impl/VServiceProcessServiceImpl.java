package com.dachen.line.stat.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.line.stat.comon.constant.ExceptionEnum;
import com.dachen.line.stat.comon.constant.VServiceProcessStatusEnum;
import com.dachen.line.stat.dao.ILineServiceProductDao;
import com.dachen.line.stat.dao.INurseOrderDao;
import com.dachen.line.stat.dao.INurseUserDao;
import com.dachen.line.stat.dao.IVSPTrackingDao;
import com.dachen.line.stat.dao.IVServiceProcessDao;
import com.dachen.line.stat.entity.param.ServiceProcessParm;
import com.dachen.line.stat.entity.vo.LineServiceProduct;
import com.dachen.line.stat.entity.vo.NurseServiceOrder;
import com.dachen.line.stat.entity.vo.PatientOrder;
import com.dachen.line.stat.entity.vo.VSPTracking;
import com.dachen.line.stat.entity.vo.VServiceProcess;
import com.dachen.line.stat.service.IVServiceProcessService;
import com.dachen.line.stat.util.ConfigUtil;
import com.dachen.line.stat.util.Constant;
import com.dachen.line.stat.util.DateUtils;
import com.dachen.line.stat.util.OutServiceHelper;
import com.dachen.util.ReqUtil;
import com.mobsms.sdk.MobSmsSdk;

/**
 * 护士订单服务
 * 
 * @author liwei
 * @date 2015/8/19
 */
@Service
public class VServiceProcessServiceImpl implements IVServiceProcessService {

	Logger logger = LoggerFactory.getLogger(VServiceProcessServiceImpl.class);
	@Autowired
	private IVServiceProcessDao vServiceProcessDao;
	
	@Autowired
	public INurseOrderDao nurseOrderDao;

	@Autowired
	public ILineServiceProductDao lineServiceProductDao;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IVSPTrackingDao vspTrackingDao;

	@Autowired
	private IPatientService patientService;

	@Autowired
	private IBaseDataDao baseDataDao;
	
	@Autowired
	private INurseUserDao nurseUserDao;
	
	@Autowired
	private MobSmsSdk mobSmsSdk;

	@Override
	public void updateVServiceProcess(String processServiceId, Integer status) {
			VServiceProcess process = vServiceProcessDao
					.getVServiceProcessBean(processServiceId);

			if (null == process) {
				throw new ServiceException("护士服务不存在！");
			}

			PatientOrder patientOrder = nurseOrderDao
					.getPatientOrderById(process.getOrderId());
			if (null == patientOrder) {
				throw new ServiceException("订单不存在！");
			}
			
			/**
			 * 开始服务  需要对订单进行状态判断  已经取消 就不能够在进行取消
			 */
			Integer  orderStatus = OutServiceHelper.getOrderStatus(ReqUtil.instance.getToken(), patientOrder.getBasicId());
			
			if(null==orderStatus || orderStatus==OrderEnum.OrderStatus.已取消.getIndex())
			{	
				throw new ServiceException(101,"订单已经取消");
			}
			// 点击开始服务 这里已经进行了更新
			vServiceProcessDao.updateVServiceProcess(processServiceId, status);
			VServiceProcess processDb = vServiceProcessDao
					.getVServiceProcessBean(processServiceId);

			if (VServiceProcessStatusEnum.toUploadResult.getIndex() == processDb
					.getStatus()) {
				// 更新基础订单状态
				OutServiceHelper.updateOrderStatus(ReqUtil.instance.getToken(),
						patientOrder.getBasicId(), OrderStatus.进行中.getIndex());
				PatientOrder patient = nurseOrderDao
						.getPatientOrderById(process.getOrderId());
				if (null != patient) {
					// 护士点击了开始服务之后，就会给患者发送短信
					if (status == VServiceProcessStatusEnum.toUploadResult
							.getIndex()) {
						// 护士抢单成功之后，给患者推送的短信末班
						String messageTemplate = dealMessageTemplate(
								Constant.MESSAGE_NURSE_START_THE_SERVICE(),
								ReqUtil.instance.getUserId());
						mobSmsSdk.send(patient.getPatientTel(),messageTemplate);//给患者发短信
						// 推送消息给患者
						// List<String> patientIds = new ArrayList<String>();
						// patientIds.add(String.valueOf(patient.getUserId()));
						// Helper.push(messageTemplate, patientIds);// 推送消息给患者
					}

				}
				VSPTracking trac = new VSPTracking();
				trac.setCreateTime(new Date().getTime());
				trac.setAppointmentTime(patientOrder.getAppointmentTime());
				trac.setOrderId(patientOrder.getId());
				trac.setCode(ExceptionEnum.Business_code_500.getIndex());
				trac.setPatientId(Integer.toString(patientOrder.getPatientId()));
				trac.setServiceId(patientOrder.getProductId());
				trac.setPatientTel(patientOrder.getPatientTel());
				vspTrackingDao.insertVSPTracking(trac);
			} else {
				throw new ServiceException("更新护士服务状态失败！");
			}
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
		String nurseName = user.getName();
		String messageTemplate = template.replace("HL", hospital)
				.replace("NURSE", nurseName)
				.replace("SER_TL", Constant.CUSTOMER_SERVICE_TELEPHONE());

		return messageTemplate;
	}

	@Override
	public List<Map<String, Object>> getHistoryVServiceProcessList(
			Integer userId) {
		// 这里查询出来 关闭之外的所有的服务记录
		Integer[] statusList = new Integer[] {
				VServiceProcessStatusEnum.toStartOrder.getIndex(),
				VServiceProcessStatusEnum.toUploadResult.getIndex(),
				VServiceProcessStatusEnum.end.getIndex(),
				VServiceProcessStatusEnum.closed.getIndex() };
		List<Map<String, Object>> listMap = getBaseServiceProcessList(userId,
				statusList);

		return listMap;
	}

	@Override
	public List<Map<String, Object>> getVServiceProcessList(Integer userId) {

		// 开始 服务 等待上传 结果 申请关闭
		Integer[] statusList = new Integer[] {
				VServiceProcessStatusEnum.toStartOrder.getIndex(),
				VServiceProcessStatusEnum.toUploadResult.getIndex(),
				VServiceProcessStatusEnum.toClosd.getIndex() };

		List<Map<String, Object>> listMap = getBaseServiceProcessList(userId,
				statusList);

		return listMap;
	}

	private List<Map<String, Object>> getBaseServiceProcessList(Integer userId,
			Integer[] statusList) {

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<NurseServiceOrder> orderList = new ArrayList<NurseServiceOrder>();
		// 这里查询出来 关闭之外的所有的服务记录
		List<VServiceProcess> processList = vServiceProcessDao
				.getVServiceProcessList("status in", statusList, userId);

		if (null == processList || processList.size() == 0) {
			return listMap;
		}

		// 倒叙排列
		Collections.sort(processList, new Comparator<VServiceProcess>() {

			@Override
			public int compare(VServiceProcess arg0, VServiceProcess arg1) {
				long timeOne = arg0.getTime();
				long timeTwo = arg1.getTime();

				if (timeOne < timeTwo) {
					return 1;
				} else if (timeOne > timeTwo) {
					return -1;
				}
				return 0;
			}
		});

		if (null != processList && processList.size() > 0) {

			for (VServiceProcess p : processList) {
				String orderId = p.getOrderId();
				if (StringUtils.isNotEmpty(orderId)) {
					NurseServiceOrder serviceOrder = new NurseServiceOrder();
					PatientOrder order = nurseOrderDao
							.getPatientOrderById(orderId);
					if (null != order) {
						Integer orderStatus = OutServiceHelper.getOrderStatus(
								ReqUtil.instance.getToken(), order.getBasicId());
						serviceOrder.setOrderStatus(orderStatus);
						serviceOrder.setOrder(order);
						serviceOrder.setServiceId(p.getId());
						serviceOrder.setStatus(p.getStatus());
						serviceOrder.setFrom(p.getFrom());
						serviceOrder.setTime(p.getTime());
						orderList.add(serviceOrder);
					}
				}
			}
			setpatientOrderData(orderList, listMap, userId);
		}

		return listMap;
	}

	public void setpatientOrderData(List<NurseServiceOrder> patientOrders,
			List<Map<String, Object>> listMap, Integer userId) {
		if (ConfigUtil.checkCollectionIsEmpty(patientOrders)) {

			for (NurseServiceOrder serviceOrder : patientOrders) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("hospital", userRepository.getUser(userId)
						.getNurse().getHospital());// 这里是去护士的医院
				result.put("nurseTel", userRepository.getUser(userId)
						.getTelephone());// 这里是去护士的医院
				result.put("serviceId", serviceOrder.getServiceId());
				result.put("serviceStatus", serviceOrder.getStatus());
				result.put("orderStatus", serviceOrder.getOrderStatus());
				result.put("doctorName", serviceOrder.getOrder()
						.getDoctorName());// 医生名称
				PatientOrder order = serviceOrder.getOrder();
				String productId = order.getProductId();
				List<String> depart = order.getDepartList();
				if (null != depart && depart.size() > 0) {
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

	@Override
	public VServiceProcess getHistoryByOrderId(String orderId) {
		VServiceProcess uq_ = vServiceProcessDao
				.getVServiceInfoByOrderId(orderId);
		return uq_;
	}

	@Override
	public void updateVServiceById(String serverId, Integer status) {
		// TODO Auto-generated method stub
		vServiceProcessDao.updateVServiceProcess(serverId, status);
	}

	// updateVServiceProcess
	@Override
	public List<VSPTracking> getExceptionNoCallPhone() {
		List<VSPTracking> list = vServiceProcessDao.getVServiceInfoByState();
		return list;
	}

	/**
	 * { "data": { "productTitle": "检查直通车", "orderTime": "2016-01-31 07:00",
	 * "orderId": "3268", "checkBusDesc":
	 * "您的检查直通车服务已购买成功，正在安排南山区人民医院的护士为您服务。预约成功后，我们会第一时间短信通知您。",
	 * "productPrice":125 }, "resultCode": 1 }
	 */
	@Override
	public Map<String, Object> getCheckBillService(String checkId,
			Integer checkbillStatus) {

		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isEmpty(checkId)) {
			throw new ServiceException("检查单id为空");
		}
		if (null == checkbillStatus) {
			throw new ServiceException("检查单状态为空");
		}
		if (!(2 == checkbillStatus || 3 == checkbillStatus || 4 == checkbillStatus)) {
			throw new ServiceException("检查单状态取值错误");
		}

		PatientOrder order = nurseOrderDao.getPatientOrderByCheckId(checkId);
		if (null == order) {
			throw new ServiceException("检查单对应的订单不存在！");
		}
		String orderId = order.getBasicId();
		result.put("orderId", orderId);
		String orderTime = order.getAppointmentTime();
		result.put("orderTime", orderTime);
		LineServiceProduct product = order.getProduct();
		String productTitle = null;
		Integer price = null;
		String checkBusDesc = null;
		if (null != product) {
			productTitle = product.getTitle();
			price = (int) product.getPrice();

		}
		if (2 == checkbillStatus) {
			checkBusDesc = "您的" + productTitle
					+ "服务已购买成功，正在安排护士为您服务。预约成功后，我们会第一时间短信通知您。";
		}
		if (3 == checkbillStatus) {

			VServiceProcess process = vServiceProcessDao
					.getVServiceProcessBeanByOrderId(order.getId());
			if (null != process) {
				Integer nurseId = process.getUserId();
				User user = userRepository.getUser(nurseId);
				if (null != user) {
					String nurseHospital = user.getNurse().getHospital();
					String nurseName = user.getName();
					checkBusDesc = "已安排" + nurseHospital + "的" + nurseName
							+ "护士为您服务,稍后" + nurseName
							+ "护士会通过平台电话联系您,请您留意400开头的电话";
				}
			}

		}

		if (4 == checkbillStatus) {
			checkBusDesc = "";
		}

		result.put("productTitle", productTitle);
		result.put("productPrice", price);
		result.put("checkBusDesc", checkBusDesc);
		return result;
	}

	@Override
	public User updateUserGuide(Integer userId) {

		User user = nurseUserDao.updateUserGuide(userId);

		return user;
	}

	@Override
	public void endNurseServiceProcess(String processServiceId) {

		VServiceProcess process = vServiceProcessDao
				.getVServiceProcessBean(processServiceId);

		if (null == process) {
			throw new ServiceException("护士服务不存在！");
		}
		Integer status = process.getStatus();

		if (VServiceProcessStatusEnum.closed.getIndex() == status) {
			throw new ServiceException("服务已经确认，无须重复确认！");
		}

		if (VServiceProcessStatusEnum.toClosd.getIndex() == status) {
			vServiceProcessDao.updateVServiceProcess(processServiceId,
					VServiceProcessStatusEnum.closed.getIndex());
		}
	}

	@Override
	public Map<String,Object> getHistoryVServiceProcessList(
			Integer userId, Integer pageIndex, Integer pageSize) {
		
		Map<String, Object> result = new  HashMap<String, Object>();
		Integer[] statusList = new Integer[] {
				VServiceProcessStatusEnum.toStartOrder.getIndex(),
				VServiceProcessStatusEnum.toUploadResult.getIndex(),
				VServiceProcessStatusEnum.end.getIndex(),
				VServiceProcessStatusEnum.closed.getIndex() };
		
		
		ServiceProcessParm scp = new ServiceProcessParm();
		scp.setUserId(userId);
		scp.setStatusList(statusList);
		scp.setPageIndex(pageIndex);
		scp.setPageSize(pageSize);
		PageVO pageVo=	vServiceProcessDao.getServiceProcessList(scp);
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		
		result.put("pageIndex", pageVo.getPageIndex());
		result.put("pageSize", pageVo.getPageSize());
		result.put("totalCount", pageVo.getTotal());
		if(null!=pageVo.getPageData()&&pageVo.getPageData().size()>0)
		{	
			getBaseServiceProcessListForPage(pageVo,result,userId,listMap) ;
		}
		result.put("list", listMap);
		return result;
	}
	
	/**
	 * 分页基础类
	 * @param userId
	 * @return
	 */
	private void getBaseServiceProcessListForPage(PageVO pageVo,Map<String, Object> result,Integer userId,List<Map<String, Object>> listMap) {
		
		
		List<NurseServiceOrder> orderList = new ArrayList<NurseServiceOrder>();
		// 这里查询出来 关闭之外的所有的服务记录
		List<VServiceProcess> processList=(List<VServiceProcess> )pageVo.getPageData();

		

		if (null != processList && processList.size() > 0) {

			for (VServiceProcess p : processList) {
				String orderId = p.getOrderId();
				if (StringUtils.isNotEmpty(orderId)) {
					NurseServiceOrder serviceOrder = new NurseServiceOrder();
					PatientOrder order = nurseOrderDao
							.getPatientOrderById(orderId);
					if (null != order) {
						Integer orderStatus = OutServiceHelper.getOrderStatus(
								ReqUtil.instance.getToken(), order.getBasicId());
						serviceOrder.setOrderStatus(orderStatus);
						serviceOrder.setOrder(order);
						serviceOrder.setServiceId(p.getId());
						serviceOrder.setStatus(p.getStatus());
						serviceOrder.setFrom(p.getFrom());
						serviceOrder.setTime(p.getTime());
						orderList.add(serviceOrder);
					}
				}
			}
			setpatientOrderData(orderList, listMap, userId);
		}

	}
}
