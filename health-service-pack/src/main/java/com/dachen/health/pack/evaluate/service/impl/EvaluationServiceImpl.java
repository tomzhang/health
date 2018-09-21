package com.dachen.health.pack.evaluate.service.impl;

import java.text.DecimalFormat;
import java.util.*;

import javax.annotation.Resource;

import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.sdk.exception.HttpApiException;
import org.mongodb.morphia.AdvancedDatastore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.entity.po.EvaluationItem;
import com.dachen.health.base.entity.vo.EvaluationItemPO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.EvaluationItemEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.evaluate.dao.IEvaluationDao;
import com.dachen.health.pack.evaluate.entity.Evaluation;
import com.dachen.health.pack.evaluate.entity.vo.EvaluationDetailVO;
import com.dachen.health.pack.evaluate.entity.vo.EvaluationStatVO;
import com.dachen.health.pack.evaluate.entity.vo.EvaluationVO;
import com.dachen.health.pack.evaluate.entity.vo.TopSixEvaluationVO;
import com.dachen.health.pack.evaluate.service.IEvaluationService;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.util.DateUtil;

@Service
public class EvaluationServiceImpl implements IEvaluationService {
	
	@Resource(name = "dsForRW")
	protected AdvancedDatastore dsForRW;
	
	@Resource
	private IOrderService orderService;
	
	@Resource
	private IOrderSessionService orderSessionService;
	
	@Resource
	private IEvaluationDao evaluationDao;
	
	@Resource
	private UserManager userManager;
	
	@Resource
	private IBaseDataService baseDataService;
	
	@Resource
    private IBusinessServiceMsg businessMsgService;

	@Autowired
	IPatientService patientService;

	
	
	private static Map<String, EvaluationItem> map = new HashMap<String, EvaluationItem>();
	public void initEvaluationItem() {
		if (map.isEmpty()) {
			List<EvaluationItem> items = dsForRW.createQuery(EvaluationItem.class).asList();
			for (EvaluationItem item : items) {
				map.put(item.getId().toString(), item);
			}
		}
	}
	
	public EvaluationItemPO getEvaluationItem(Integer orderId) {
		if (orderId == null) {
			throw new ServiceException("订单ID为空");
		}
		Order order = orderService.getOne(orderId);
		if (order == null) {
			throw new ServiceException("订单不存在");
		}
		return baseDataService.getEvaluationItem(100, order.getPackType());
	}
	
	public Evaluation add(Integer orderId, String... itemIds) throws HttpApiException {
		if (orderId == null) {
			throw new ServiceException("订单ID为空");
		}
		if (itemIds.length == 0 || itemIds[0].length() != 24) {
			throw new ServiceException("参数错误：itemIds为空或类型错误");
		}
		if (evaluationDao.getByOrderId(orderId) != null) {
			throw new ServiceException("该订单已评价");
		}
		Order order = orderService.getOne(orderId);
		if (order == null) {
			throw new ServiceException("订单不存在");
		}
		
		Evaluation eva = new Evaluation();	
		eva.setDoctorId(order.getDoctorId());
		eva.setUserId(order.getUserId());
		eva.setItemIds(itemIds);
		eva.setOrderId(orderId);
		eva.setCreateTime(System.currentTimeMillis());
		eva = evaluationDao.add(eva);

		//TODO 发送通知
		sendImInfo(order);
		sendNotify(order);
		return eva;
	}
	
	private void sendImInfo(Order order) throws HttpApiException {
		OrderSession session = orderSessionService.findOneByOrderId(order.getId());
//		String userName = userManager.getUser(order.getUserId()).getName();
		Patient p = patientService.findByPk(order.getPatientId());
		businessMsgService.sendNotifytoMyMsg(order.getDoctorId().toString(), session.getMsgGroupId(), "患者"+p.getUserName()+"已为您的咨询服务发起评价");
	}
	
	private void sendNotify(Order order) throws HttpApiException {
//		String userName = userManager.getUser(order.getUserId()).getName();
		Patient patient = patientService.findByPk(order.getPatientId());
		String userName = "";
		if(patient != null)userName = patient.getUserName();
		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
    	ImgTextMsg textMsg = new ImgTextMsg();
    	textMsg.setStyle(6);
    	textMsg.setTitle("评价通知");
    	textMsg.setTime(System.currentTimeMillis());
    	textMsg.setFooter("立即查看");
    	textMsg.setPic(GroupUtil.getPingImage());
    	textMsg.setContent("患者"+userName+"已对您的服务进行评价，点击查看");
    	Map<String, Object> param = new HashMap<String, Object>();
    	param.put("bizType", 16);
    	textMsg.setParam(param);
    	mpt.add(textMsg);
    	businessMsgService.sendTextMsg(order.getDoctorId()+"", SysGroupEnum.TODO_NOTIFY, mpt, null);
	}
	
	public void sendSystem(Integer doctorId,String name) throws HttpApiException {
		 //gdocDao
		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
    	ImgTextMsg textMsg = new ImgTextMsg();
    	textMsg.setStyle(7);
    	textMsg.setTitle("系统通知");
    	textMsg.setTime(System.currentTimeMillis());
    	textMsg.setContent("您设置的执业医院："+name+"已经被集团管理员删除，如有疑问，请联系集团管理员");
    	mpt.add(textMsg);
    	businessMsgService.sendTextMsg(doctorId+"", SysGroupEnum.TODO_NOTIFY, mpt, null);
    	businessMsgService.sendTextMsg(doctorId+"", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
	}

	@Override
	public List<String> getEvaluationNamesByOrderId(Integer orderId) {
		if (orderId == null) {
			throw new ServiceException("订单ID为空");
		}

		List<String> result = new ArrayList<>();
		Evaluation evaluation = evaluationDao.getByOrderId(orderId);
		if (null != evaluation) {
			String[] items = evaluation.getItemIds();
            List<String> itemList = Arrays.asList(items);
            for (String itemId : itemList) {
                result.add(map.get(itemId).getName());
            }
		}

		return result;
	}


	public Map<String, Object> isEvaluated(Integer orderId) {
		if (orderId == null) {
			throw new ServiceException("订单ID为空");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isEvaluated", false);
		if (evaluationDao.getByOrderId(orderId) != null) {
			map.put("isEvaluated", true);
		}
		return map;
	}
	
	public TopSixEvaluationVO getTopSix(Integer doctorId) {
		if (doctorId == null) {
			throw new ServiceException("医生ID为空");
		}
		
		TopSixEvaluationVO result = new TopSixEvaluationVO();
		
		// 用户数量
		List<Evaluation> evaList = evaluationDao.getEvaluations(doctorId);
		result.setUserNum(evaList.size());
		
		// 好评率
		List<EvaluationStatVO> vos = evaluationDao.getEvaluationStatVO(doctorId);
		result.setGoodRate(getGoodRate(vos));
		
		// topSix
		List<EvaluationStatVO> topSix = new ArrayList<EvaluationStatVO>();
		for (int i = 0; i < (vos.size() > 6 ? 6 : vos.size()); i++) {
			topSix.add(vos.get(i));
		}
		result.setEvaluateStatList(topSix);
		return result;
	}
	
	public String getGoodRate(Integer doctorId) {
		List<EvaluationStatVO> vos = evaluationDao.getEvaluationStatVO(doctorId);
		return getGoodRate(vos);
	}
	
	public String getGoodRate(List<EvaluationStatVO> vos) {
		initEvaluationItem();
		int goodCount = 0;
		int sum = 0;
		for (EvaluationStatVO vo : vos) {
			if (map.get(vo.getId()).getLevel() == EvaluationItemEnum.good.getIndex()) {
				goodCount = goodCount + vo.getCount();
			}
			sum = sum + vo.getCount();
		}
		if (sum == 0) {
			return "暂无评价";
		}
		float num = ((float)goodCount)/((float)sum)*100;
		DecimalFormat df = new DecimalFormat("0.0");//格式化小数
		
		//好评率规则：好评率0~50%都为9.0分 好评率每+5%，评价+0.1分，直到10分。
		float result = 9.0f;
		String resultStr = "9";
		
		if (num <= 50.0f) {
			result = 9.0f;
		} else {
			float poor = num - 50.0f;
			float count = poor/5f;
			result += 0.1f * count;
			if (result > 10.0f) {
				result = 10.0f;
			}
		}
		
		//若为整数则不保留小数点，否则保留一位小数点
		float f = Float.parseFloat(df.format(result));
		if (f % 1.0 == 0) {
			resultStr = String.valueOf((int)f);
		} else {
			resultStr = String.valueOf(df.format(f));
		}
		
		return resultStr;
		//return df.format(num) + "%";//返回的是String类型
	}
	
	public EvaluationDetailVO getEvaluationDetail(Integer doctorId) {
		initEvaluationItem();
		EvaluationDetailVO detailVO = new EvaluationDetailVO();
		
		detailVO.setEvaluateStatList(evaluationDao.getEvaluationStatVO(doctorId));
		
		List<EvaluationVO> vos = new ArrayList<EvaluationVO>();
		for (Evaluation eva : evaluationDao.getEvaluations(doctorId)) {
			EvaluationVO vo = new EvaluationVO();
			StringBuilder userName = new StringBuilder();
			Order o = orderService.getOne(eva.getOrderId());
			Patient p = patientService.findByPk(o.getPatientId());
//			userName.append(userManager.getUser(eva.getUserId()).getName());
			userName.append(p.getUserName());
			userName.replace(1, userName.length(), "**");
			vo.setUserName(userName.toString());
			vo.setCreateTime(DateUtil.formatDate2Str(eva.getCreateTime(), DateUtil.FORMAT_YYYY_MM_DD));
			StringBuilder description = new StringBuilder();
			for (String evaId : eva.getItemIds()) {
				description.append(map.get(evaId).getName()).append("，");
			}
			description.replace(description.length()-1, description.length(), "。");
			vo.setDescription(description.toString());
			vos.add(vo);
		}
		detailVO.setEvaluateVOList(vos);
		return detailVO;
	}
}
