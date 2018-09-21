package com.dachen.line.stat.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.commons.constants.PackConstants;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.line.stat.comon.constant.ExceptionEnum;
import com.dachen.line.stat.comon.constant.VServiceProcessStatusEnum;
import com.dachen.line.stat.dao.ICheckResultsDao;
import com.dachen.line.stat.dao.ILineServiceDao;
import com.dachen.line.stat.dao.ILineServiceProductDao;
import com.dachen.line.stat.dao.INurseOrderDao;
import com.dachen.line.stat.dao.IVSPTrackingDao;
import com.dachen.line.stat.dao.IVServiceProcessDao;
import com.dachen.line.stat.entity.param.CheckResultLineServiceParm;
import com.dachen.line.stat.entity.param.CheckResultsParm;
import com.dachen.line.stat.entity.vo.CheckResults;
import com.dachen.line.stat.entity.vo.LineService;
import com.dachen.line.stat.entity.vo.PatientOrder;
import com.dachen.line.stat.entity.vo.ServiceImage;
import com.dachen.line.stat.entity.vo.VSPTracking;
import com.dachen.line.stat.entity.vo.VServiceProcess;
import com.dachen.line.stat.service.ICheckResultsService;
import com.dachen.line.stat.util.ConfigUtil;
import com.dachen.line.stat.util.Constant;
import com.dachen.line.stat.util.DateUtils;
import com.dachen.line.stat.util.OutServiceHelper;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;

/**
 * 短信
 * 
 * @author liwei
 * @date 2015/12/14
 */
@Service
public class CheckResultsServiceImpl implements ICheckResultsService {

	Logger logger = LoggerFactory.getLogger(CheckResultsServiceImpl.class);

	@Autowired
	private ICheckResultsDao checkResultsDao;

	@Autowired
	private ILineServiceProductDao lineServiceProductDao;

	@Autowired
	private IVServiceProcessDao vServiceProcessDao;

	@Autowired
	private INurseOrderDao nurseOrderDao;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IVSPTrackingDao vspTrackingDao;

	@Autowired
	private ILineServiceDao lineServiceDao;

	@Autowired
	private MobSmsSdk mobSmsSdk;

	public List<CheckResults> getCheckResultsServiceList(String orderId) {

		List<CheckResults> checkLists = checkResultsDao.getCheckResultsList(
				"orderId", orderId);
		if (ConfigUtil.checkCollectionIsEmpty(checkLists)) {
			for (CheckResults result : checkLists) {
				String lsId = result.getLsIds();
				if (StringUtils.isNotEmpty(lsId)) {
					LineService line = lineServiceProductDao
							.getLineServiceItem(lsId);
					if (null != line) {
						result.setLineService(line);
					}
				}
			}
		}
		return checkLists;
	}

	@Override
	public void insertCheckResults(CheckResultsParm param) {

		String serviceId = param.getServiceId();
		if (StringUtil.isEmpty(serviceId)) {
			throw new ServiceException("服务id为空！");
		}
		VServiceProcess process = vServiceProcessDao
				.getVServiceProcessBean(param.getServiceId());
		if (null == process) {
			throw new ServiceException("护士对应的服务不存在！");
		}
		String orderId = null;
		String checkId = null;
		PatientOrder order = null;
		List<CheckResults> list = new ArrayList<CheckResults>();
		List<CheckResultLineServiceParm> checkItemList = param.getItemList();
		if (ConfigUtil.checkCollectionIsEmpty(checkItemList)) {
			if (process != null) {
				orderId = process.getOrderId();
				checkId = nurseOrderDao.getCheckIdIdById(orderId);
				int from = 2;
				if (StringUtils.isNotEmpty(checkId)) {
					from = 1;
				}
				order = nurseOrderDao.getPatientOrderById(orderId);
				if (null == order) {
					throw new ServiceException("服务订单不存在！");
				}
				for (CheckResultLineServiceParm check : checkItemList) {
					long time = new Date().getTime();
					CheckResults checkRsults = new CheckResults();
					checkRsults.setFrom(from);// 存在检查项id 就是1 不存在 就是2
					checkRsults.setOrderId(orderId);
					checkRsults.setLsIds(check.getLsIds());
					checkRsults.setResults(check.getResults());
					checkRsults.setTime(time);
					List<ServiceImage> paramList = new ArrayList<ServiceImage>();
					setUserImageList(check.getImageList().split(","), paramList);
					checkRsults.setImageList(paramList);
					list.add(checkRsults);
				}
			}
		}
		if (list.size() > 0) {
			List<CheckItem> checkList = new ArrayList<CheckItem>();
			String orderTime = order.getAppointmentTime();
			convertResultToItem(list, checkList, orderId, serviceId, orderTime);
			OutServiceHelper.batchAddCheckItem(ReqUtil.instance.getToken(), checkList);// 批量插入检查结果
			vServiceProcessDao.updateVServiceProcess(param.getServiceId(),
					VServiceProcessStatusEnum.end.getIndex());// 更新服务流程状态
			// 修改检查单结果
			if (StringUtils.isNotEmpty(checkId)) {
				OutServiceHelper.updateCheckbill(ReqUtil.instance.getToken(), checkId,
						"4");
			}
			try {
				// 发送短信
				String messageTemplate = dealUploadMessageTemplate(
						Constant.MESSAGE_NURSE_UPLOAD_CHECK_RESULT(),
						ReqUtil.instance.getUserId(),order.getBasicId());
				mobSmsSdk.send(order.getPatientTel(), messageTemplate);
				// 推送消息
//				List<String> userIds = new ArrayList<String>();
//				userIds.add(String.valueOf(order.getUserId()));
//				Helper.push(messageTemplate, userIds);// 推送消息

				VSPTracking trac = new VSPTracking();
				trac.setCreateTime(new Date().getTime());
				trac.setAppointmentTime(order.getAppointmentTime());
				trac.setOrderId(order.getId());
				trac.setCode(ExceptionEnum.Business_code_600.getIndex());
				trac.setPatientId(Integer.toString(order.getPatientId()));
				trac.setServiceId(order.getProductId());
				trac.setPatientTel(order.getPatientTel());
				vspTrackingDao.insertVSPTracking(trac);

				VSPTracking tracT = new VSPTracking();
				tracT.setCreateTime(new Date().getTime());
				tracT.setAppointmentTime(order.getAppointmentTime());
				tracT.setOrderId(order.getId());
				tracT.setCode(ExceptionEnum.Business_code_700.getIndex());
				tracT.setPatientId(Integer.toString(order.getPatientId()));
				tracT.setServiceId(order.getProductId());
				tracT.setPatientTel(order.getPatientTel());
				vspTrackingDao.insertVSPTracking(tracT);

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("给患者发短信失败！订单id=" + orderId);
			}
		}

	}

	private List<String> getItemImageList(List<ServiceImage> ims) {
		List<String> images = new ArrayList<String>();
		for (ServiceImage str : ims) {
			images.add(str.getImageId());
		}

		return images;
	}

	/**
	 * 
	 * @param checkLists
	 * @param checkList
	 * @param orderId
	 */
	private void convertResultToItem(List<CheckResults> checkLists,
			List<CheckItem> checkList, String orderId, String serviceId,
			String orderTime) {
		PatientOrder order = nurseOrderDao.getPatientOrderById(orderId);
		if (null != order) {
			for (CheckResults check : checkLists) {
				if (null != check) {
					CheckItem item = new CheckItem();
					item.setCreateTime(new Date().getTime());

					item.setImageList(getItemImageList(check.getImageList()));
					item.setResults(check.getResults());
					String checkId = order.getCheckId();
					if (StringUtils.isNotEmpty(checkId)) {
						item.setFromId(checkId);
						item.setFrom(1);
					} else {
						item.setFromId(serviceId);
						item.setFrom(2);
					}
					item.setItemName(check.getTitle());
					CheckSuggest suggest = lineServiceDao
							.getCheckSuggestById(check.getLsIds());
					if (null != suggest) {
						item.setItemName(suggest.getName());
					} else {
						item.setItemName(order.getProduct().getTitle());
					}
					item.setCheckUpId(check.getLsIds());
					/**
					 * 上传结果 新增预约时间
					 */
					if (StringUtils.isNotEmpty(orderTime)) {
						int first=orderTime.indexOf(":");
						int last =orderTime.indexOf(":");
						if(first==last)
						{	
							orderTime = orderTime + ":00";
						}
						Long time = DateUtils.toDate(orderTime).getTime();
						item.setVisitingTime(time);
					}
					checkList.add(item);
				}
			}
		}

	}

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	/**
	 * 替换短信末班的相关内容
	 * 
	 * @param template
	 * @param userId
	 * @return
	 */
	private String dealUploadMessageTemplate(String template, Integer userId,String orderId) throws HttpApiException {
		User user = userRepository.getUser(userId);
		String hospital = user.getNurse().getHospital();
		String nurseName = user.getName();
		String  appLink= shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("2", orderId, UserEnum.UserType.patient.getIndex()));
		String messageTemplate = template.replace("HL", hospital)
				.replace("NURSE", nurseName)
				.replace("SER_TL", Constant.CUSTOMER_SERVICE_TELEPHONE())
				.replace("APP_LINK", appLink);

		return messageTemplate;
	}

	/**
	 * 转换图片对象
	 * 
	 * @param strs
	 * @param paramList
	 */
	private void setUserImageList(String[] strs, List<ServiceImage> paramList) {
		if (null != strs && strs.length > 0) {
			for (String str : strs) {
				if (StringUtils.isNotEmpty(str)) {
					ServiceImage image = new ServiceImage();
					image.setImageId(str);
					paramList.add(image);
				}
			}
		}
	}

	/**
	 * { "itemName": "特殊细菌涂片检查", "createTime": 1451003289131, "from": 2, "id":
	 * "567c8d994203f326a290d1c5", "fromId": "567a1428f7aaa10900fa79e3",
	 * "imageList": [ "http://192.168.3.7:8081http://121323123",
	 * "http://192.168.3.7:8081http://66666666" ], "results": "检查结果OK" }
	 */
	@Override
	public List<Map<String, Object>> getCheckResultsServiceMapList(
			String orderId) {

		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
		PatientOrder order = nurseOrderDao.getPatientOrderById(orderId);
		if (null != order) {
			Integer code = -1;
			JSONMessage json = null;
			String checkId = order.getCheckId();
			Object page = null;
			if (StringUtils.isNotEmpty(checkId)) {
				json = OutServiceHelper.getCheckItemList(ReqUtil.instance.getToken(),
						checkId);
				code = json.getInteger("resultCode");
				if (code == 1) {
					page = json.getData();
				}
			} else {
				VServiceProcess service = vServiceProcessDao
						.getVServiceInfoByOrderId(orderId);
				String serviceId = null;
				if (null != service) {
					serviceId = service.getId();
				}
				json = OutServiceHelper.getCheckItemList(ReqUtil.instance.getToken(),
						serviceId);
				code = json.getInteger("resultCode");
				if (code == 1) {
					page = json.getData();
				}
			}
			if (null != page) {
				JSONObject pageObject = (JSONObject) page;
				JSONArray pageArray = (JSONArray) pageObject.get("pageData");
				if (null != pageArray && pageArray.size() > 0) {
					for (int i = 0; i < pageArray.size(); i++) {

						JSONObject object = pageArray.getJSONObject(i);
						Object imageObt = object.get("imageList");
						if (null != imageObt) {
							Map<String, Object> map = new HashMap<String, Object>();
							JSONArray imageList = (JSONArray) imageObt;
							List<String> images = new ArrayList<String>();
							if (null != imageList && imageList.size() > 0) {
								for (int j = 0; j < imageList.size(); j++) {
									Object image = imageList.get(j);
									if (null != image) {
										images.add(image.toString());
									}
								}
								map.put("imageList", images);
								String checkUpId = object
										.getString("checkUpId");// checkUpId
								map.put("id", checkUpId);
								map.put("title", object.getString("itemName"));
								map.put("results", object.getString("results"));
								resultMap.add(map);
							}
						}

					}
				}
			}
		}
		return resultMap;
	}

	public List<Map<String, Object>> getCheckResultsServiceMapListByCheckId(
			String checkId) {
		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
		Integer code = -1;
		JSONMessage json = null;
		Object page = null;
		if (StringUtils.isNotEmpty(checkId)) {
			json = OutServiceHelper.getCheckItemList(ReqUtil.instance.getToken(),
					checkId);
			code = json.getInteger("resultCode");
			if (code == 1) {
				page = json.getData();
			}
		}
		if (null != page) {
			JSONObject pageObject = (JSONObject) page;
			JSONArray pageArray = (JSONArray) pageObject.get("pageData");
			if (null != pageArray && pageArray.size() > 0) {
				for (int i = 0; i < pageArray.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					JSONObject object = pageArray.getJSONObject(i);
					String checkUpId = object.getString("checkUpId");// checkUpId
					map.put("id", checkUpId);
					map.put("title", object.getString("itemName"));

					resultMap.add(map);
				}
			}
		}
		return resultMap;
	}
}
