package com.dachen.health.pack.order.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.entity.SingleRefundReqData;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.logger.LoggerUtils;
import com.dachen.commons.page.PageVO;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.base.dao.IdxRepository.idxType;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.commons.constants.AccountEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderRefundStatus;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.account.entity.vo.RechargeVO;
import com.dachen.health.pack.account.service.IRechargeService;
import com.dachen.health.pack.incomeNew.constant.IncomeEnumNew.LogType;
import com.dachen.health.pack.incomeNew.entity.po.Incomelog;
import com.dachen.health.pack.incomeNew.entity.po.RefundOrder;
import com.dachen.health.pack.incomeNew.entity.po.RefundOrderExample;
import com.dachen.health.pack.incomeNew.entity.vo.RefundOrderVO;
import com.dachen.health.pack.incomeNew.mapper.RefundOrderMapper;
import com.dachen.health.pack.incomeNew.service.IncomelogService;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.Refund;
import com.dachen.health.pack.order.entity.po.RefundExample;
import com.dachen.health.pack.order.entity.vo.OrderVO;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.mapper.RefundMapper;
import com.dachen.health.pack.order.service.IOrderRefundService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.pay.service.IAlipayPayService;
import com.dachen.health.pack.pay.service.IWechatPayService;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.UUIDGenerator;
import com.tencent.common.Configure;
import com.tencent.common.Util;
import com.tencent.protocol.pay_protocol.NoitfyPayResData;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;

@Service
public class OrderRefundServiceImpl implements IOrderRefundService{
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	OrderMapper orderMapper;
	
	@Autowired
	IOrderService orderService;
	
	@Autowired
	IOrderSessionService orderSessionService;
	
	@Autowired
	IBusinessServiceMsg businessServiceMsg;
	
	@Autowired
	IRechargeService rechargeService;
	
	@Autowired
	private RefundOrderMapper refundOrderMapper;
	
	@Autowired
	private UserRepository userManager;
	
	@Autowired
	private RefundMapper refundMapper; 
	
	@Autowired
	private IdxRepository idxRepository;
	
	@Autowired
	IAlipayPayService alipayPayService;
	
	@Autowired
	IWechatPayService wechatPayService;
	
	@Autowired
	IncomelogService incomeLogService;
	
	@Autowired
	IGroupDao groupDao;
	
	/**
	 * 自增增长批次好，退款批次号支付宝规则:(yyyyMMdd+自增号)
	 */
	public String nextRefundNo() {
		Integer refundNo = idxRepository.nextIdx(idxType.refundNo);
		if (refundNo == null) {
			throw new ServiceException("退款自增No获取失败");
		}
		String currDay = DateUtil.getYMDString();
		String serialNumber = UUIDGenerator.getIP() + "" + refundNo;// IP+流水号
		return currDay + serialNumber.replaceAll("-", "");

	}
	
	/**
	 * 生成退款单（取消已支付的订单）
	 * @param orderId
	 */
	public Map<Integer, Object> addRefundOrder(Integer... orderIds) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		for (Integer orderId : orderIds) {
			Order order = orderService.getOne(orderId);
			
			// 判断是否已经支付成功
			if (order == null) {
				LoggerUtils.printCommonLog("订单ID为：[" + orderId + "]订单不存在,或未支付..");
				map.put(orderId, "该订单不存");
				continue;
			}
			if (order.getPayType() == 0) {
				LoggerUtils.printCommonLog("订单ID为：[" + orderId + "]或未支付..");
				map.put(orderId, "该订单未支付");
				continue;
			}
			RechargeVO rechargevo = rechargeService.findOneByOrderId(order.getId());
			
			if (rechargevo == null || order.getPayType() == 0) {
				LoggerUtils.printCommonLog("订单ID为：[" + orderId + "]未找到支付信息，或在退款中,或未支付..");
				map.put(orderId, "该订单未支付");
				continue;
			}
			
			if (getByOrderId(orderId) != null) {
				LoggerUtils.printCommonLog("订单ID为：[" + orderId + "]已有退款单，不能重复退款..");
				map.put(orderId, "该订单已有退款单，不能重复退款");
				continue;
			}
			
			//增加退款单
			incomeLogService.addRefundOrder(order, rechargevo, null);
			
			//更新状态为待退款
			order.setRefundStatus(OrderRefundStatus.refundApply.getIndex());
			orderService.updateOrder(order);
		}
		return map;
	}
	
	/**
	 * 1.产生退款记录到t_refund表中
	 * 2.封装请求参数，生成请求链接
	 * 3.通过支付宝官网向支付宝发送请求
	 * 4.等待处理结果
	 */
	public String addRefund(Integer orderId) {
		
		Order order = orderService.getOne(orderId);
		
		// 判断是否已经支付成功
		if (order == null) {
			LoggerUtils.printCommonLog("订单ID为：[" + orderId + "]订单不存在,或未支付..");
			return "订单不存在";
		}
		if (order.getPayType() == 0) {
			LoggerUtils.printCommonLog("订单ID为：[" + orderId + "]或未支付..");
			return "订单未支付";
		}
		RechargeVO rechargevo = rechargeService.findOneByOrderId(order.getId());
		
		if (rechargevo == null || order.getPayType() == 0) {
			LoggerUtils.printCommonLog("订单ID为：[" + orderId + "]未找到支付信息，或在退款中,或未支付..");
			return "订单未支付";
		}
		
		RefundOrder refundOrder = getByOrderId(orderId);
		if (refundOrder == null) {
//			LoggerUtils.printCommonLog("订单ID为：[" + orderId + "]尚未生成退款单，不能退款..");
//			return "尚未生成退款单，不能退款";
			refundOrder = incomeLogService.addRefundOrder(order, rechargevo, ReqUtil.instance.getUserId());
			LoggerUtils.printCommonLog("订单ID为：[" + orderId + "]尚未生成退款单");
		} 
		
		// 记录第三方（支付宝、微信）退款信息
		RefundExample example = new RefundExample();
		example.createCriteria().andOrderIdEqualTo(orderId).andRefundOrderIdEqualTo(refundOrder.getId());
		List<Refund> refunds = refundMapper.selectByExample(example);
		Refund refund = null;
		if (refunds == null || refunds.isEmpty()) {
			refund = new Refund();
			refund.setCreateDate(System.currentTimeMillis());
			refund.setCreateUserId(ReqUtil.instance.getUserId());
			refund.setMoney(order.getPrice());
			refund.setPayType(rechargevo.getPayType());
			refund.setStatus(OrderRefundStatus.refundApply.getIndex());
			refund.setOrderId(orderId);//订单Id
			refund.setRefundOrderId(refundOrder.getId());//退款单Id
			refund.setMoney(order.getPrice());
			refund.setPayNo(rechargevo.getPayNo());
			refund.setRefundNo(nextRefundNo());
			refund.setTransId(rechargevo.getAlipayNo());
			refund.setRefundReason("协商退款");
			refundMapper.insertSelective(refund);
		} else {
			refund = refunds.get(0);
			refund.setRefundNo(nextRefundNo());
			refundMapper.updateByPrimaryKeySelective(refund);
		}
		
		String formHTML = handleRefund(refund, rechargevo.getPartner() == null ? false : rechargevo.getPartner().equals(Configure.getAppid_BDJL()));
		
		if (refund.getPayType() == AccountEnum.PayType.wechat.getIndex()) {
			try {
				//1秒之后查询一次
				Thread.sleep(1000);
				
				String resultXml = getWechatRefundResult(refund.getTransId(), refund.getPayNo());
				
				NoitfyPayResData noitfyPayResDataParam = (NoitfyPayResData)Util.getObjectFromXML(resultXml,NoitfyPayResData.class);
				
				//成功了更新订单“退款状态”，否则由调度更新@see autoQueryWechat
				if (noitfyPayResDataParam.getReturn_code().equals("SUCCESS") && noitfyPayResDataParam.getResult_code().equals("SUCCESS")) {
					refundSuccess(refund.getId(), orderId, resultXml);
					formHTML = "退款成功";
				} else {
//					formHTML = "未知";
					JobTaskUtil.queryWXRefundResult(refund.getId());
				}
			} catch (InterruptedException e) {
//				formHTML = "未知";
				JobTaskUtil.queryWXRefundResult(refund.getId());
			}
		}

		return formHTML;
	}
	
	public List<RefundOrderVO> getRefundDetail(Integer orderId) {
		RefundExample example = new RefundExample();
		example.createCriteria().andOrderIdEqualTo(orderId);
		List<Refund> refundList = refundMapper.selectByExample(example);
		Refund refund = refundList.get(0);
		if (refund == null) {
			throw new ServiceException("无退款记录");
		}
		List<RefundOrderVO> vos = new ArrayList<RefundOrderVO>();
		List<Incomelog> list = incomeLogService.getIncomeLog(orderId);
		for (Incomelog info : list) {
			if (info.getLogType().intValue() == LogType.orderRefund.getIndex()) {
				RefundOrderVO doctor = new RefundOrderVO();
				doctor.setRefundTime(DateUtil.formatDate2Str(refund.getCompleteDate()));
				doctor.setRefundStatus(refund.getStatus());
				doctor.setRefundAmt(refund.getMoney());
				doctor.setSerialNumber(refund.getTransId());
				doctor.setName(userManager.getUser(info.getDoctorId()).getName());
				doctor.setAmount(info.getMoney());
				Incomelog parent = getByDoctorIdAndType(list, info.getDoctorId(), 1);
				if (parent != null) {
					RefundOrderVO parentDoctorInfo = new RefundOrderVO();
					parentDoctorInfo.setName(userManager.getUser(parent.getDoctorId()).getName());
					parentDoctorInfo.setAmount(parent.getMoney());
					doctor.setParentDoctorInfo(parentDoctorInfo);
				}
				Incomelog group = getByDoctorIdAndType(list, info.getDoctorId(), 2);
				if (group != null) {
					RefundOrderVO groupInfo = new RefundOrderVO();
					groupInfo.setName(groupDao.getById(group.getGroupId()).getName());
					groupInfo.setAmount(group.getMoney());
					doctor.setGroupInfo(groupInfo);
				}
				vos.add(doctor);
			}
		}
		if (vos.isEmpty()) {
			RefundOrderVO refundvo = new RefundOrderVO();
			refundvo.setRefundTime(DateUtil.formatDate2Str(refund.getCompleteDate()));
			refundvo.setRefundStatus(refund.getStatus());
			refundvo.setRefundAmt(refund.getMoney());
			refundvo.setSerialNumber(refund.getTransId());
			Order order = orderService.getOne(orderId);
			refundvo.setName(userManager.getUser(order.getDoctorId()).getName());
			refundvo.setAmount(refund.getMoney().doubleValue());
			vos.add(refundvo);
		}
		return vos;
	}
	
	public Incomelog getByDoctorIdAndType(List<Incomelog> list, int doctorId, int type) {
		for (Incomelog info : list) {
			if (doctorId == 0 || info.getChildDoctorId() == null)
				continue;
			if (doctorId == info.getChildDoctorId().intValue() && type == info.getType().intValue())
				return info;
		}
		return null;
	} 
	
	
	public RefundOrder getByOrderId(Integer orderId) {
		RefundOrderExample example = new RefundOrderExample();
		example.createCriteria().andOrderIdEqualTo(orderId);
		List<RefundOrder> refundOrders = refundOrderMapper.selectByExample(example);
		if (refundOrders.isEmpty())
			return null;
		return refundOrders.get(0);
	}

	private String appendDetailData(Refund... refunds) {
		StringBuffer bufferStr = new StringBuffer("");
		for (Refund orderRefund : refunds) {
			bufferStr.append(StringUtil.atValue(orderRefund.getTransId(), ((float)orderRefund.getMoney())/100+"", orderRefund.getRefundReason()))
					.append("#");
		}
		if (bufferStr.length() > 0) {
			return bufferStr.substring(0, bufferStr.length() - 1);
		} else {
			return bufferStr.toString();
		}
	}
	
	/**
	 * @param orderRefund
	 * @param refund
	 */
	public String handleRefund(Refund refund, boolean isBDJL) {
		if (PropertiesUtil.isNonProductionEnv()) {
			refund.setMoney(1L);//非生产环境设置支付金额为1分
        }
		if (refund.getPayType() == AccountEnum.PayType.alipay.getIndex()) {
			// https://doc.open.alipay.com/doc2/apiDetail.htm?spm=a219a.7395905.0.0.JF19M9&docType=4&apiId=850#s5
			SingleRefundReqData singleRefundReqData = new SingleRefundReqData(
					refund.getRefundNo(), 
					"1",
					appendDetailData(refund));
			return alipayPayService.takeRefundOrderSignatrue(singleRefundReqData);
		} else if (refund.getPayType() == AccountEnum.PayType.wechat.getIndex()) {
			// TODO 微信退款,需要去主动查询退款，才知道退款结果
			RefundReqData refundReqData = new RefundReqData(
					refund.getTransId(), 
					refund.getPayNo(), 
					null,
					refund.getRefundNo(), 
					refund.getMoney().intValue(), 
					refund.getMoney().intValue(), 
					Configure.getMchid(), 
					"CNY",
					isBDJL);

			return wechatPayService.refundOrder(refundReqData);
		} else {
			throw new ServiceException("退款类型错误");
		}
	}
	
	/**
	 * 针对微信退款是异步接口，退款结果需主动去微信查询退款结果
	 */
	@Override
	public void autoQueryWechat(Integer refundId) {
		//查询“微信”退款中的数据
		Refund refund = refundMapper.selectByPrimaryKey(refundId);
		String resultXml = getWechatRefundResult(refund.getTransId(), refund.getPayNo());
		
		NoitfyPayResData noitfyPayResDataParam = (NoitfyPayResData)Util.getObjectFromXML(resultXml,NoitfyPayResData.class);
		
		if (noitfyPayResDataParam != null) {
			if (noitfyPayResDataParam.getReturn_code().equals("SUCCESS")) {
				if (noitfyPayResDataParam.getResult_code().equals("SUCCESS")) {
					// 退款成功
					if (noitfyPayResDataParam.getCash_fee().equals(refund.getMoney())) {
						refundSuccess(refund.getId(), refund.getOrderId(), resultXml);
					}
				} else {
					// 退款失败
					refundFail(refund.getId(), refund.getOrderId(), resultXml);
				}
			} else {
				// 退款失败
				refundFail(refund.getId(), refund.getOrderId(), resultXml);
			}
		}
	}

	private String getWechatRefundResult(String transactionID, String outTradeNo) {
		RefundQueryReqData refundQueryReqData = new RefundQueryReqData(transactionID, outTradeNo, null, null, null);
		String result = wechatPayService.singleRefundqueryHandelFunction(refundQueryReqData);
		return result;
	}
	
	/**
	 * 退款成功
	 * @param refundOrderId
	 * @param refundId
	 * @param orderId
	 * @param log
	 */
	public void refundSuccess(Integer refundId,Integer orderId,String log){
		//更新患者退款纪录
		Refund updateRefund = new Refund();
		updateRefund.setId(refundId);
		updateRefund.setStatus(OrderRefundStatus.refundSuccess.getIndex());
		updateRefund.setCompleteDate(System.currentTimeMillis());
		updateRefund.setRemark(log);//TODO 用作备份
		refundMapper.updateByPrimaryKeySelective(updateRefund);
		
		Order order = new Order();
		order.setId(orderId);
		order.setRefundStatus(OrderRefundStatus.refundSuccess.getIndex());
		orderService.updateOrder(order);
		
		sendRefundNotify(orderId);
	}

	private void sendRefundNotify(Integer orderId) {
		try {
			Order order = orderService.getOne(orderId);
			User userDoc = userManager.getUser(order.getDoctorId());
			User user = userManager.getUser(order.getUserId());
			if(userDoc!=null && user !=null) {
				//推送消息给用户
				String content = "您与{0}医生的{1}订单已取消并退款，金额将退回到您的原支付渠道。";
				orderService.sendNotitfy("系统通知",
						MessageFormat.format(content, userDoc.getName(), PackEnum.getPackType(order.getPackType())),
						order.getUserId() + "");
			}
		} catch (Exception e) {
			logger.error("退款通知发送异常："+orderId, e);
		} 
	}
	
	public void refundFail(Integer refundId, Integer orderId, String log) {
		// 更新患者退款纪录
		Refund updateRefund = new Refund();
		updateRefund.setId(refundId);
		updateRefund.setStatus(OrderRefundStatus.refundFailure.getIndex());
		updateRefund.setCompleteDate(System.currentTimeMillis());
		updateRefund.setRemark(log);// TODO 用作备份
		refundMapper.updateByPrimaryKeySelective(updateRefund);

		Order order = new Order();
		order.setId(orderId);
		order.setRefundStatus(OrderRefundStatus.refundFailure.getIndex());
		orderService.updateOrder(order);
		
	}
	
	public PageVO getRefundOrders(OrderParam param) {
		if (StringUtil.isNotBlank(param.getUserName()) || StringUtil.isNotBlank(param.getTelephone())) {

			List<Integer> userIds = userManager.findUserBlurryByNameAndIphone(param.getUserName(), param.getTelephone());
			if (userIds.isEmpty()) {
				userIds.add(Integer.MIN_VALUE);
			}
			param.setUserIds(userIds);
		}
		
		List<OrderVO> orders = orderMapper.getRefundOrders(param);
		Integer count = orderMapper.getRefundOrdersCount(param);
		for (OrderVO order : orders) {
			User user = userManager.getUser(order.getDoctorId());
			if (user != null) {
				order.setDoctorName(user.getName());
			}
			user = userManager.getUser(order.getUserId());
			if (user != null) {
				order.setUserName(user.getName());
			}
		}
		
		return new PageVO(orders, Long.valueOf(count), param.getPageIndex(), param.getPageSize());
	}

}
