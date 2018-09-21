package com.dachen.health.pack.patient.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.exception.ServiceException;
import com.dachen.drug.api.client.DrugApiClientProxy;
import com.dachen.drug.api.entity.CGoodsView;
import com.dachen.drug.api.entity.CRecipeView;
import com.dachen.health.base.constant.CallEnum.CallType;
import com.dachen.health.base.constant.DownTaskEnum;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.checkbill.entity.po.CheckBill;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.checkbill.service.CheckBillService;
import com.dachen.health.commons.constants.*;
import com.dachen.health.commons.constants.OrderEnum.OrderRecordStatus;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.utils.PackUtil;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.health.pack.conference.dao.CallRecordRepository;
import com.dachen.health.pack.conference.entity.param.CallRecordParam;
import com.dachen.health.pack.conference.entity.vo.CallRecordVO;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.util.GuideMsgHelper;
import com.dachen.health.pack.illhistory.dao.IllHistoryInfoDao;
import com.dachen.health.pack.illhistory.entity.po.Diagnosis;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryInfo;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.health.pack.order.service.IOrderDoctorService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.mapper.CureRecordMapper;
import com.dachen.health.pack.patient.mapper.DiseaseMapper;
import com.dachen.health.pack.patient.mapper.PatientDiseaseMapper;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.*;
import com.dachen.health.pack.patient.model.CureRecordExample.Criteria;
import com.dachen.health.pack.patient.service.ICureRecordService;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.recommand.service.IDiseaseLaberService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MsgDocument;
import com.dachen.im.server.data.request.GroupStateRequestMessage;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.*;
import com.mobsms.sdk.MobSmsSdk;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.ucpaas.restsdk.UcPaasRestSdk;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.AdvancedDatastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 
 * ProjectName： health-service-pack<br>
 * ClassName： DiseaseServiceImpl<br>
 * Description： <br>
 * 
 * @author 李淼淼
 * @createTime 2015年8月12日
 * @version 1.0.0
 */
@Service
public class CureRecordServiceImpl extends BaseServiceImpl<CureRecord, Integer>
		implements ICureRecordService {

	@Resource(name = "dsForRW")
	protected AdvancedDatastore dsForRW;
	
	@Resource
	CureRecordMapper mapper;

	@Resource
	PatientMapper patientMapper;

	@Resource
	IImageDataService imageDataService;

	@Resource
	DiseaseMapper diseaseMapper;

	@Resource
	OrderMapper orderMapper;
	
	@Resource
	private CallRecordRepository callRecordRepository;

	@Resource
	PatientDiseaseMapper patientDiseaseMapper;

	@Resource
	DiseaseTypeRepository diseaseTypeRepository;

	@Autowired
	IBaseDataService baseDataService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IBusinessServiceMsg sendMsgService;
	
	@Autowired
	private IGroupDao groupDao;

	@Autowired
	private IGuideDAO iGuideDAO;

	@Autowired
	CheckBillService checkBillService;

	@Autowired
	IBaseDataService baseDataServiceImpl;
	
	@Resource
	private IOrderService orderService;
	
	@Autowired
	IOrderSessionService orderSessionService;
	
	@Resource
	private UserManager userManager;
	
	@Autowired
	private MobSmsSdk mobSmsSdk;

	@Autowired
	private IDiseaseLaberService diseaseLaberService;
	
	@Autowired
	private IllHistoryInfoDao illHistoryInfoDao;

    @Autowired
    private IOrderDoctorService orderDoctorService;
	
	Logger logger = LoggerFactory.getLogger(CureRecordServiceImpl.class);

	@Override
	public int save(CureRecord intance) throws HttpApiException {
		Order order = orderMapper.getOne(intance.getOrderId());
		if (order == null) {
			throw new ServiceException(30003, "找不到对应订单");
		}
		intance.setPatientId(order.getPatientId());
		intance.setUserId(order.getUserId());
		// 保存患者病种关系(此时不对病种进行判空，否则将无法将患者信息保存于集团病种库中2016-8-6傅永德)
		//if (StringUtil.isNotEmpty(intance.getConsultAdviseDiseases())) {
			savePatientDisease(order, intance.getConsultAdviseDiseases());
		//}
		if (StringUtil.isNotEmpty(intance.getDrugAdviseJson())) {
			String drugAdviseId = saveDrugRecipe(ReqUtil.instance.getToken(),
					order.getDoctorId(), order.getUserId(),
					order.getPatientId(), order.getGroupId(), order.getId(),
					intance.getDrugAdviseJson());
			intance.setDrugAdvise(drugAdviseId);
			logger.info("############save: " + drugAdviseId);
		}
		intance.setCreateTime(new Date().getTime());
		int ret = mapper.insert(intance);
		//update  by 姜宏杰 2016年2月24日17:36:13 如果选了让导医帮忙则将order更新为待确认
		if(order.getPackType() != null && PackEnum.PackType.phone.getIndex() == order.getPackType().intValue())
		{
			if("0".equals(intance.getIsNeedHelp())){
				updateOrderRecordStatus(order.getId(), OrderRecordStatus.confirming);
			}
		}
		
		//会诊订单
		if(order.getOrderType()==OrderType.consultation.getIndex())
		{
			if("0".equals(intance.getIsNeedHelp())){
				updateOrderRecordStatus(order.getId(), OrderRecordStatus.confirming);
				if(OrderEnum.OrderType.consultation.getIndex() == order.getOrderType().intValue()){
					//是会诊订单的话就通知小医生确认
					OrderSession orderSession = orderSessionService.findOneByOrderId(intance.getOrderId());
					//通知IM订单的状态
					GroupStateRequestMessage requestMsg = new GroupStateRequestMessage();
					requestMsg.setGid(orderSession.getMsgGroupId());
					requestMsg.setBizStatus(String.valueOf(order.getOrderStatus()));
					MsgHelper.updateGroupBizState(requestMsg);
				}
			}
		}
		return ret;
	}

	private void savePatientDisease(Order order, String consultAdviseDiseases) {
		if (StringUtils.isEmpty(consultAdviseDiseases)) {
			PatientDisease record = new PatientDisease();
			record.setUserId(order.getUserId());
			record.setPatientId(order.getPatientId());
			record.setDoctorId(order.getDoctorId());
			record.setOrderId(order.getId());
			record.setGroupId(order.getGroupId());
			patientDiseaseMapper.insert(record);
		} else {
			String[] diseaseTypeIds = consultAdviseDiseases.split(",");
			List<String> diseaseIds = new ArrayList<String>();
			Map<String, String> diseaseMap = new HashMap<String, String>();
			for (String diseaseTypeId : diseaseTypeIds) {
				diseaseIds.add(diseaseTypeId);
			}
			List<DiseaseType> diseaseTypes = diseaseTypeRepository
					.findByIds(diseaseIds);
			for (DiseaseType diseaseType : diseaseTypes) {
				diseaseMap.put(diseaseType.getId(), diseaseType.getName());
			}
			for (String diseaseTypeId : diseaseTypeIds) {
				PatientDisease record = new PatientDisease();
				record.setUserId(order.getUserId());
				record.setPatientId(order.getPatientId());
				record.setDoctorId(order.getDoctorId());
				record.setOrderId(order.getId());
				record.setGroupId(order.getGroupId());
				record.setDiseaseTypeId(diseaseTypeId);
				record.setDiseaseTypeName(diseaseMap.get(diseaseTypeId));
				patientDiseaseMapper.insert(record);
			}
		}
	}

	private void deletePatientDisease(Order order) {
		PatientDiseaseExample example = new PatientDiseaseExample();
		example.createCriteria().andOrderIdEqualTo(order.getId());
		patientDiseaseMapper.deleteByExample(example);

	}

	private String saveDrugRecipe(String token, Integer doctor, Integer user_id, Integer patient, String group,
			Integer orderId, String drugAdviseJson) throws HttpApiException {
		return drugApiClientProxy.saveDrugRecipe(token, doctor, user_id, patient, group, orderId, 2, drugAdviseJson);
	}

	private void updateDrugRecipe(String drugAdviseId, Integer doctor, Integer user_id, Integer patient,
			String group, Integer orderId, String drugAdviseJson) throws HttpApiException {
		drugApiClientProxy.updateDrugRecipe(drugAdviseId, doctor, user_id, patient, group, orderId, 2, drugAdviseJson);
	}
	
	private String deleteDrugRecipe(String token, String drugAdviseId) throws HttpApiException {
		return drugApiClientProxy.deleteDrugRecipe(drugAdviseId);
	}

	@Override
	public int update(CureRecord intance) throws HttpApiException {
		Order order = orderMapper.getOne(intance.getOrderId());
		if (order == null) {
			throw new ServiceException(30003, "找不到对应订单");
		}
		intance.setPatientId(order.getPatientId());
		intance.setUserId(order.getUserId());

		CureRecord oldCureRecord = mapper.selectByPrimaryKey(intance.getId());
		
		if (oldCureRecord != null) {
			//新旧不为空，更新药方
			handleDrugRecipe(intance, order, oldCureRecord);
		}
		// if(oldCureRecord!=null&&StringUtil.isNotEmpty(oldCureRecord.getDrugAdviseId()))
		// {
		// deleteDrugRecipe(ReqUtil.getToken(), oldCureRecord.getDrugAdviseId());
		// }
		// if(StringUtil.isNotEmpty(intance.getDrugAdviseJson()))
		// {
		// String
		// drugAdviseId=saveDrugRecipe(ReqUtil.getToken(),order.getDoctorId(),order.getUserId(),order.getPatientId(),order.getGroupId(),intance.getDrugAdviseJson());
		// intance.setDrugAdviseId(drugAdviseId);
		// }
		// 调用医药平台提供的update方法，保证delete和save在同一事务中
/*		if (!(StringUtil.isEmpty(oldCureRecord.getDrugAdvise()) && StringUtil
				.isEmpty(intance.getDrugAdviseJson()))) {
			String drugAdviseId = updateDrugRecipe(ReqUtil.getToken(),
					oldCureRecord.getDrugAdvise(), order.getDoctorId(),
					order.getUserId(), order.getPatientId(),
					order.getGroupId(), order.getId(), intance.getDrugAdviseJson());
			intance.setDrugAdvise(drugAdviseId);
		}*/
		// 保存患者病种关系(不进行判空操作，但凡更新诊疗记录，都将之前的患者病种删除并重新保存2016-8-6傅永德)
		//if (StringUtil.isNotEmpty(oldCureRecord.getConsultAdviseDiseases())) {
			deletePatientDisease(order);
		//}
		//保存患者病种关系不用判空，判空则会导致该患者统计不到集团患者库（2016-7-7傅永德）
		//if (StringUtil.isNotEmpty(intance.getConsultAdviseDiseases())) {
			savePatientDisease(order, intance.getConsultAdviseDiseases());
		//}
		return mapper.updateByPrimaryKeySelective(intance);

	}

	@Override
	public int deleteByPK(Integer pk) {
		CureRecord cureRecord = mapper.selectByPrimaryKey(pk);
		updateOrderRecordStatus(cureRecord.getOrderId(),
				OrderRecordStatus.blank);
		return mapper.deleteByPrimaryKey(pk);

	}

	private void updateOrderRecordStatus(Integer orderId,
			OrderEnum.OrderRecordStatus recordStatus) {
		Order order = new Order();
		order.setId(orderId);
		order.setRecordStatus(recordStatus.getIndex());
		orderMapper.update(order);
	}

	/**
	 * 填充数据
	 * 
	 * 因mysql数据库的表CureRecord(p_cure_record)字段attention里储存的是id数组
	 * 所以要返回给客户端之前，要将类CureRecord字段attention转成 private List<CheckSuggest>
	 * checkSuggestList;
	 * 
	 * @param cureRecord
	 * @return
	 */
	private CureRecord fillCheckSuggestList(CureRecord cureRecord) throws HttpApiException {
		if (cureRecord != null) {
			String attention = cureRecord.getAttention();
			if (attention != null && attention.isEmpty() == false) {
				// 分解
				String[] attentionIds = attention.split(",");
				if (attentionIds != null && attentionIds.length > 0) {
					List<CheckSuggest> checkSuggestList = baseDataService
							.getCheckSuggestByIds(attentionIds);
					// set
					cureRecord.setCheckSuggestList(checkSuggestList);
				}
			}
			String consultAdviseDiseases = cureRecord.getConsultAdviseDiseases();
			if (consultAdviseDiseases != null && consultAdviseDiseases.isEmpty() == false) {
				// 分解
				String[] diseaseIds = consultAdviseDiseases.split(",");
				if (diseaseIds != null && diseaseIds.length > 0) {
					List<String> idList = new ArrayList<String>();
					for (String id : diseaseIds) {
						idList.add(id);
					}
					List<DiseaseTypeVO> diseaseList = baseDataService.getDiseaseType(idList);
					// set
					cureRecord.setConsultAdviseDiseaseList(diseaseList);
				}
			}

			String recipeId = cureRecord.getDrugAdvise();
			if (!StringUtil.isEmpty(recipeId)) {
				logger.info("############fillCheckSuggestList: " + recipeId);
				CRecipeView recipeView = drugApiClientProxy.getDrugRecipe(recipeId);
				cureRecord.setRecipeView(recipeView);
			}
		}
		return cureRecord;
	}

	private static String  DEFAULT_BUCKET= PropertiesUtil.getContextProperty("qiniu.call.bucket");
	private static String Default_FORMAT = "mp3";
	private static String DEFALUT_DOMAIN = PropertiesUtil.getContextProperty("qiniu.call.domain");
	@Override
	public CureRecord findByPk(Integer pk) throws HttpApiException {
		CureRecord cureRecord = mapper.selectByPrimaryKey(pk);
		if (cureRecord != null) {
			// TODO
			// 填充数据 start
			fillCheckSuggestList(cureRecord);
			// 填充数据 end
			List<String> images = imageDataService.findImgData(
					ImageDataEnum.cureImage.getIndex(), pk);
			List<String> voices = imageDataService.findImgData(
					ImageDataEnum.cureVoice.getIndex(), pk);
			
			if (images != null) {
				cureRecord.setImages(images.toArray(new String[images.size()]));
			}
			if (voices != null) {
				cureRecord.setVoices(voices.toArray(new String[voices.size()]));
			}
			//update by 姜宏杰 返回医生信息 以及患者信息
			Order order = orderService.getOne(cureRecord.getOrderId());
			User doc = userManager.getUser(order.getDoctorId());//医生信息
			Patient patitent = patientMapper.selectByPrimaryKey(order.getPatientId());//患者信息
			cureRecord.setPatient(patitent);
			cureRecord.setUser(doc);
			cureRecord.setRecordStatus(order.getRecordStatus());
			return cureRecord;
		} else {
			throw new ServiceException(30001, "can't found CureRecord#" + pk);
		}
	}

	@Override
	public int save(CureRecord intance, String[] images, String[] voices) throws HttpApiException {
		save(intance);
		int cureRecordId = intance.getId();
		if (cureRecordId > 0) {
			if (images != null) {
				for (String k : images) {
					saveAnnex(cureRecordId, k, ImageDataEnum.cureImage);
				}
			}
			if (voices != null) {
				for (String k : voices) {
					saveAnnex(cureRecordId, k, ImageDataEnum.cureVoice);
				}
			}
			/**
			 * 保存检查单记录
			 */
			if (StringUtil.isNotBlank(intance.getAttention())) {
				boolean checkBillSaveFlag = saveCheckBill(intance.getOrderId(),
						intance.getAttention());
				if (!checkBillSaveFlag) {
					throw new ServiceException(30001, "诊疗记录创建成功，保存检查单失败");
				}
			}
		} else {
			throw new ServiceException(30001, "诊疗记录创建失败");
		}
		return cureRecordId;
	}


	private boolean saveCheckBill(Integer orderId, String attention) {
		Order order = orderMapper.getOne(orderId);
		if (order == null) {
			throw new ServiceException("找不到对应订单记录 orderId={" + orderId + "}");
		}
		long createTime = System.currentTimeMillis();
		CheckBill checkBill = new CheckBill();
		checkBill.setCheckBillStatus(1);
		checkBill.setCreateTime(createTime);
		checkBill.setOrderId(orderId);
		checkBill.setPatientId(order.getPatientId());
		CheckBill rtnCheckBill = checkBillService.addCheckBill(checkBill);
		String checkBillId = rtnCheckBill.getId();

		List<String> checkItemIds = addCheckItem(attention, createTime, checkBillId);
		rtnCheckBill.setCheckItemIds(checkItemIds);
		int rtn = checkBillService.updateCheckbill(rtnCheckBill);
		return rtn == 1;
	}

	private List<String> addCheckItem(String attention, long createTime, String checkBillId) {
		List<String> checkItemIds = new ArrayList<String>();
		List<CheckSuggest> css = baseDataServiceImpl
				.getCheckSuggestByIds(attention.split(","));

		if (css != null && css.size() > 0) {
			for (CheckSuggest cs : css) {
				CheckItem item = new CheckItem();
				item.setCreateTime(createTime);
				item.setFrom(1);
				item.setCheckUpId(cs.getId());
				item.setFromId(checkBillId);
				item.setItemName(cs.getName());
				item.setUpdateTime(createTime);
				CheckItem rtnCheckItem = checkBillService.addCheckItem(item);
				checkItemIds.add(rtnCheckItem.getId());
			}
		}
		return checkItemIds;
	}
	
	private void updateCheckBill(Integer orderId, String attention) {
		if(orderId == null || StringUtils.isBlank(attention)){
			return ;
		}
		CheckBill checkbill = checkBillService.getCheckBillByOrderId(orderId);
		if(checkbill == null){
			//插入
			boolean checkBillSaveFlag = saveCheckBill(orderId,attention);
			if (!checkBillSaveFlag) {
				throw new ServiceException(30001, "检查单添加失败");
			}
		}else{
			//修改
			String checkbillId = checkbill.getId();
			long createTime = System.currentTimeMillis();
			checkBillService.removeCheckItemByCheckBillId(checkbillId);
			List<String> checkItemIds = addCheckItem(attention,createTime,checkbillId);
			CheckBill param = new CheckBill();
			param.setId(checkbillId);
			param.setCheckItemIds(checkItemIds);
			checkBillService.updateCheckbill(param);
		}
	}

	// 保存附件
	private void saveAnnex(int cureRecordId, String k, ImageDataEnum imageType) {
		ImageData imageData = PackUtil.newImageData(cureRecordId,
				ReqUtil.instance.getUserId(), k, imageType);
		imageDataService.add(imageData);
	}

    /**
     * 获取病程中需要需要的医生的id
     * @param order
     * @return
     */

    private Integer getBigDoctorId(Order order) {
        // 返回医生信息
        Integer orderType = order.getOrderType();
        Integer d1Id = order.getDoctorId();
        if (orderType != null && OrderEnum.OrderType.consultation.getIndex() == orderType.intValue()) {
            Integer d2Id = null;
            List<OrderDoctor> ods = orderDoctorService.findOrderDoctors(order.getId());
            if ((ods != null) && (ods.size() > 0)) {
                for (OrderDoctor orderDoctor : ods) {
                    if (orderDoctor.getDoctorId().intValue() != d1Id) {
                        d2Id = orderDoctor.getDoctorId();
                        break;
                    }
                }
            }
            return d2Id;
        } else {
            return d1Id;
        }
    }

	@Autowired
	protected ShortUrlComponent shortUrlComponent;

	@Override
	public int update(CureRecord intance, String[] images, String[] voices) throws HttpApiException {
		if (intance == null) {
			throw new ServiceException(30007, "parameter all  null!");
		}
		if (intance.getDoctorId() == null) {
			Integer userId = ReqUtil.instance.getUserId();
			intance.setDoctorId(userId);
		}
		Order order = orderMapper.getOne(intance.getOrderId());

		Integer doctorId = getBigDoctorId(order);

        /**添加初步诊断**/
		IllHistoryInfo info = illHistoryInfoDao.getById(order.getIllHistoryInfoId());
		if(info != null){
			if (info.getDiagnosis() !=null) {
				//是否可以添加初步诊断
				boolean canAdd = true;
				for (Diagnosis diagnosis : info.getDiagnosis()) {
					if (diagnosis.getOrderId() != null && diagnosis.getOrderId().intValue() == order.getId().intValue()
							&& diagnosis.getFromDoctorId() != null) {
						canAdd = false;
						break;
					}
				}
				if (canAdd){
					illHistoryInfoDao.addDiagnosis(info.getId(), intance.getConsultAdvise(), intance.getConsultAdviseDiseases(), doctorId, order.getId());
				} else {
                    illHistoryInfoDao.updateDiagnosis(info.getId(), intance.getConsultAdvise(), intance.getConsultAdviseDiseases(), order.getId());
				}
			} else {
				illHistoryInfoDao.addDiagnosis(info.getId(), intance.getConsultAdvise(), intance.getConsultAdviseDiseases(), doctorId, order.getId());
			}
		}

		if (intance.getId() == null || intance.getId() == 0) {
			this.save(intance, images, voices);
			if(StringUtils.equals(intance.getSubmitState(), "0")){
				if(intance != null)
					intance.setImages(images);
				sendCureRecordCard(intance,order.getPackType());
			}
			return -1;
		}
		int ret = 0;
		if (order.getPackType() != null && PackEnum.PackType.phone.getIndex() == order.getPackType().intValue()) {// 电话订单
			ret = updateRecord(intance);
		} else {
			ret = update(intance);
		}
		int cureRecordId = intance.getId();
		imageDataService.deleteImgData(ImageDataEnum.cureImage.getIndex(), cureRecordId);
		imageDataService.deleteImgData(ImageDataEnum.cureVoice.getIndex(), cureRecordId);
		if (ret > 0) {
			if (images != null) {
				for (String k : images) {
					saveAnnex(cureRecordId, k, ImageDataEnum.cureImage);
				}
			}
			if (voices != null) {
				for (String k : voices) {
					saveAnnex(cureRecordId, k, ImageDataEnum.cureVoice);
				}
			}
		} else {
			throw new ServiceException(30001, "诊疗记录更新失败");
		}

		// 电话订单处理
		if (order.getPackType() != null && PackEnum.PackType.phone.getIndex() == order.getPackType().intValue()) {
			if ("1".equals(intance.getSubmitState())) {// 导医点击提交之后再给医生发送短信
				updateOrderRecordStatus(intance.getOrderId(), OrderRecordStatus.confirmed);// 更新订单表的咨询状态为：导医已确认
				if (ReqUtil.instance.getUser().getUserType() == UserType.DocGuide.getIndex()) {
					// 通知IM订单的状态
					OrderSession orderSession = orderSessionService.findOneByOrderId(intance.getOrderId());
					GroupStateRequestMessage requestMsg = new GroupStateRequestMessage();
					requestMsg.setGid(orderSession.getMsgGroupId());
					requestMsg.setBizStatus(String.valueOf(order.getOrderStatus()));
					MsgHelper.updateGroupBizState(requestMsg);
					Patient patient = patientMapper.selectByPrimaryKey(intance.getPatientId());
					User user = userRepository.getUser(intance.getDoctorId());
					// 发给医生
					//{0}医生您好，导医已为患者{1}({2}岁,{3})填写了咨询记录，请您登录App确认。您确认结束服务后，系统将会为您结算收入。{4}
					final String msg = baseDataService.toContent("1030", user.getName(), patient.getUserName(),
							patient.getAge(), patient.getSex() == 1 ? "男" : "女", shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("3",
									order.getId().toString(), Integer.valueOf(UserEnum.UserType.doctor.getIndex()))));
					mobSmsSdk.send(user.getTelephone(), msg);
					intance.setUpdateTime(System.currentTimeMillis());
					mapper.updateByPrimaryKeySelective(intance);
				}
			}
			if ("0".equals(intance.getIsNeedHelp())) {
				updateOrderRecordStatus(order.getId(), OrderRecordStatus.confirming);
			}
		}
		// 会诊订单
		if (order.getOrderType() == OrderType.consultation.getIndex()) {
			// 会诊医生点保存，则不更改咨询记录状态

			// 会诊医生点提交，修改状态为待主诊医生确认咨询记录
			if ("0".equals(intance.getIsNeedHelp())) {
				updateOrderRecordStatus(order.getId(), OrderRecordStatus.confirming);
			}

			// 主诊医生点击确定，同时会调结束服务接口，更改咨询记录为已确定
		}

		/**
		 * 当检查项被修改之后，同时更新检查单中的检查项目
		 */
		if (StringUtil.isNotBlank(intance.getAttention()) && !"null".equals(intance.getAttention())) {
			updateCheckBill(intance.getOrderId(), intance.getAttention());
		}
		
		/**
		 * 添加疾病标签
		 */
		diseaseLaberService.addLaberByTreat(order.getUserId(), intance.getConsultAdviseDiseases());
		
		/**
		 * 发送咨询结果卡片
		 * at 2016-12-16 19:02:26
		 */
		if(StringUtils.equals(intance.getSubmitState(), "0")){
			if(intance != null)
				intance.setImages(images);
			sendCureRecordCard(intance,order.getPackType());
		}
		return ret;
	}

	private void sendCureRecordCard(CureRecord cr, Integer packType) throws HttpApiException {
		MsgDocument msgDocument = new MsgDocument();
		List<MsgDocument.DocInfo> list = new ArrayList<>();

		if(StringUtils.isNotBlank(cr.getConsultAdvise())){
			MsgDocument.DocInfo d1 = new MsgDocument.DocInfo();
			d1.setTitle("咨询结果");
			d1.setContent(cr.getConsultAdvise());
			d1.setType(0);
			list.add(d1);
		}

		if(StringUtils.isNotBlank(cr.getConsultAdviseDiseases())){
			MsgDocument.DocInfo d2 = new MsgDocument.DocInfo();
			d2.setTitle("疑似疾病");
			List<DiseaseType> dts = diseaseTypeRepository.findByIds(Arrays.asList(cr.getConsultAdviseDiseases().split(",")));
			if(dts != null){
				d2.setContent(dts.stream().map(o -> o.getName()).collect(Collectors.joining(" ")));
			}
			d2.setType(0);
			list.add(d2);
		}

		if(StringUtils.isNotBlank(cr.getAttention())){
			MsgDocument.DocInfo d3 = new MsgDocument.DocInfo();
			d3.setTitle("检查项目");
			List<CheckSuggest> css = baseDataServiceImpl.getCheckSuggestByIds(cr.getAttention().split(","));
			if(css != null)
				d3.setContent(css.stream().map(o -> o.getName()).collect(Collectors.joining(" ")));
			d3.setType(0);
			list.add(d3);
		}

		if(StringUtils.isNotBlank(cr.getDrugAdviseJson())){
			List drugs = JSONUtil.parseObject(List.class,cr.getDrugAdviseJson());
			String content = drugs.stream().map( o -> { Map m = (Map)o; return m.get("goodsName")+" ×"+m.get("goodsNumber");}).collect(Collectors.joining(" ,"))+"";
			MsgDocument.DocInfo d4 = new MsgDocument.DocInfo();
			d4.setTitle("用药建议");
			d4.setContent(content);
			d4.setType(0);
			list.add(d4);
		}

		if(cr.getImages() != null && cr.getImages().length > 0){
			MsgDocument.DocInfo d5 = new MsgDocument.DocInfo();
			d5.setTitle("影像资料");
			d5.setType(1);
			List<Map<String,Object>> pics = new ArrayList<>();
			for(String url : cr.getImages()){
				Map<String,Object> pic = new HashMap<>();
				pic.put("url",url);
				pics.add(pic);
			}
			d5.setPic(pics);
			list.add(d5);
		}

		Order o = orderMapper.getOne(cr.getOrderId());
		OrderSession os = orderSessionService.findOneByOrderId(o.getId());
		Map<String, Object> param_b = new HashMap<>();
		param_b.put("doctorId", o.getDoctorId());
		param_b.put("patientId", o.getPatientId());
		param_b.put("illHistoryInfoId", o.getIllHistoryInfoId());
		msgDocument.setBizParam(param_b);
		msgDocument.setBizParam(param_b);
		msgDocument.setList(list);
		msgDocument.setHeader(PackEnum.PackType.getTitle(packType));
		msgDocument.setHeaderIcon(getPackHeaderIcon(packType));
		GuideMsgHelper.getInstance().sendMsgDocument(os.getMsgGroupId(), o.getDoctorId() + "", null, msgDocument, false);
	}

	public String getPackHeaderIcon(Integer packType) {
		String imgPath="";
		if(packType == PackEnum.PackType.message.getIndex()){
			imgPath = "/pack/message.png";
		}else if (packType == PackEnum.PackType.phone.getIndex()){
			imgPath = "/pack/phone.png";
		}else if (packType == PackEnum.PackType.careTemplate.getIndex()){
			imgPath = "/pack/care.png";
		}else if (packType == PackEnum.PackType.integral.getIndex()){
			imgPath = "/pack/integral.png";
		}else if (packType == PackEnum.PackType.checkin.getIndex()){
			imgPath = "/pack/checkin.png";
		}else if (packType == PackEnum.PackType.online.getIndex()){
			imgPath = "/pack/outPatient.png";
		}else if (packType == PackEnum.PackType.consultation.getIndex()){
			imgPath = "/pack/consultation.png";
		}
		return MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.DEFAULT_BUCKET, imgPath);
	}

	@Override
	public List<CureRecord> findByOrderId(int orderId) throws HttpApiException {
		CureRecordExample example = new CureRecordExample();
		example.createCriteria().andOrderIdEqualTo(orderId);
		List<CureRecord> datas = mapper.selectByExample(example);

		List<CureRecord> adata = new ArrayList<CureRecord>();
		for (CureRecord cureRecord : datas) {
			adata.add(findByPk(cureRecord.getId()));
		}
		return adata;
	}

	@Override
	public List<CureRecord> findByPatientAndDoctor(Integer patientId,
			Integer doctorId) throws HttpApiException {
		if (patientId == null && doctorId == null) {
			throw new ServiceException(40001,
					"parameter [patientId,doctorId] all null ");
		}

		CureRecordExample example = new CureRecordExample();
		Criteria criteria = example.createCriteria();
		if (patientId != null) {
			criteria.andPatientIdEqualTo(patientId);
		}
		if (doctorId != null) {
			criteria.andDoctorIdEqualTo(doctorId);
		}
		List<CureRecord> datas = mapper.selectByExample(example);

		List<CureRecord> adata = new ArrayList<CureRecord>();
		for (CureRecord cureRecord : datas) {
			adata.add(findByPk(cureRecord.getId()));
		}
		return adata;

	}

	@Autowired
	protected DrugApiClientProxy drugApiClientProxy;

    @Override
	public CGoodsView getUsageByDrugId(String drugId) throws HttpApiException {
		CGoodsView drugUsageList = drugApiClientProxy.getDrugUsage(drugId);
		return drugUsageList;
	}

	@Override
	public void sendNotice(CureRecord intance) throws HttpApiException {
		if (intance.getDoctorId() == null) {
			return;
		}
		User uu = userRepository.getUser(intance.getDoctorId());
		if (uu != null
				&& uu.getUserType() == UserEnum.UserType.doctor.getIndex()) {
			// 获取导医信息
			ConsultOrderPO po = iGuideDAO.getObjectByOrderId(intance
					.getOrderId());
			if (po == null || po.getGuideId() == null) {
				return;
			}
			// ***医生已填写电话咨询记录，请您尽快确认。
			//
			// 1、推送显示：***医生填写了电话咨询记录，请确认。（现在的错误显示：简小风医生给您发了一条新消息）
			// 2、导医的通知显示：标题：确认咨询记录
			// 内容：***医生已填写电话咨询记录，请您尽快确认。
			List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
			ImgTextMsg imgTextMsg = new ImgTextMsg();
			imgTextMsg.setTime(System.currentTimeMillis());
			imgTextMsg.setStyle(7);
			imgTextMsg.setTitle(UserChangeTypeEnum.DOCTOR_WRTIE_CONSULT
					.getAlias());
			imgTextMsg.setContent(uu.getUsername() + "医生已填写电话咨询记录，请您尽快确认。");
			Map<String, Object> imParam = new HashMap<String, Object>();
			imParam.put("bizType", 15);
			imParam.put("bizId", intance.getOrderId());
			imgTextMsg.setParam(imParam);
			mpt.add(imgTextMsg);
			Map<String, Object> msg = new HashMap<String, Object>();
			msg.put("pushTip", uu.getUsername() + "医生填写了电话咨询记录，请确认。");
			sendMsgService.sendTextMsg(String.valueOf(po.getGuideId()), SysGroupEnum.TODO_NOTIFY, mpt, msg);
		}
	}
	
	@Override
	public List<DiseaseTypeVO> getCommonDiseasesByDocId(Integer doctorId){
		if(doctorId == null){
			throw new ServiceException("医生ID不能为空");
		}
		List<DiseaseTypeVO> list = new ArrayList<DiseaseTypeVO>();
		List<CureRecord> crList = mapper.getCommonDiseasesByDocId(doctorId);
		return getDiseaseTypeVO(crList,list);
	}
	private List<DiseaseTypeVO> getDiseaseTypeVO(List<CureRecord> crlist,List<DiseaseTypeVO> diseaseList){
		if(crlist == null || crlist.size() == 0){
			return diseaseList; 
		}
		if(diseaseList == null){
			diseaseList = new ArrayList<DiseaseTypeVO>();
		}
		Map<String,Integer> map = new HashMap<String,Integer>();
		for(CureRecord cr : crlist){
			String diseases = cr.getConsultAdviseDiseases();
			if(!StringUtil.isEmpty(diseases)){
				String[] array = diseases.trim().split(",");
				for(String str :array){
					Integer num = map.get(str);
					if(num == null){
						num = 1;
					}else{
						num = num +1;
					}
					map.put(str, num);
				}
			}
		}
		map = sortMapByValue(map);
		int i = 0;
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext() && i<15){
			DiseaseTypeVO vo =diseaseTypeRepository.getDiseaseTypeTreeById(it.next());
			if(vo != null){
				diseaseList.add(vo);
				i++;
			}
		}
		return diseaseList; 
	}
	
    public static Map<String, Integer> sortMapByValue(Map<String, Integer> oriMap) {  
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();  
        if (oriMap != null && !oriMap.isEmpty()) {  
            List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(oriMap.entrySet());  
            Collections.sort(entryList,  
                    new Comparator<Map.Entry<String, Integer>>() {  
                        public int compare(Entry<String, Integer> entry1,  
                                Entry<String, Integer> entry2) {  
                            int value1 = 0, value2 = 0;  
                            try {  
                                value1 = entry1.getValue();  
                                value2 = entry2.getValue();  
                            } catch (NumberFormatException e) {  
                                value1 = 0;  
                                value2 = 0;  
                            }  
                            return value2 - value1;  
                        }  
                    });  
            Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();  
            Map.Entry<String, Integer> tmpEntry = null;  
            while (iter.hasNext()) {  
                tmpEntry = iter.next();  
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());  
            }  
        }  
        return sortedMap;  
    }  
    public static void main(String[] args) {
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("a", 1);
		map.put("b", 10);
		map.put("c", 5);
		map.put("d", 3);
		map.put("e", 7);
		map = sortMapByValue(map);
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			System.err.println(key+"----"+map.get(key));;
		}
	}

	@Override
	public List<CureRecord> getListByCondition() {
		List<CureRecord> list = mapper.getListByCondition();
		return list;
	}

	@Override
	public Map<String, Object> pushMessageToDoctor(Integer doctorId) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CureRecord> list = mapper.pushMessageToDoctor(doctorId);
		//因为每次只给医生看最早的那一条所以……
		if(list.size()>0){
			CureRecord crue = list.get(0);
			Order order = orderMapper.getOne(crue.getOrderId());
			if(order!=null&&OrderEnum.OrderStatus.已支付.getIndex()!=order.getOrderStatus()){
				throw new ServiceException("订单状态不正确");
			}
			OrderSession session = orderSessionService.findOneByOrderId(crue.getOrderId());
			//得到订单id 然后去会话订单里查询会话id 订单id
			map.put("message", "导医已经帮您填写了"+list.size()+"条咨询记录，请您确认并结束服务，系统将为您结算收入。");
			map.put("session_id", session.getMsgGroupId());
			map.put("orderId", session.getOrderId());
			map.put("size", list.size());
			map.put("groupId", order.getGroupId());//集团id
			String groupId = order.getGroupId();
			Group group = groupDao.getById(groupId);
			map.put("groupName", group.getName());//集团名称
		}
		return map;
	}

	public int updateRecord(CureRecord intance) throws HttpApiException {
		Order order = orderMapper.getOne(intance.getOrderId());
		if (order == null) {
			throw new ServiceException(30003, "找不到对应订单");
		}
		intance.setPatientId(order.getPatientId());
		intance.setUserId(order.getUserId());
		CureRecord oldCureRecord = mapper.selectByPrimaryKey(intance.getId());
		
		if (oldCureRecord != null) {
			handleDrugRecipe(intance, order, oldCureRecord);
		}
		
		// 调用医药平台提供的update方法，保证delete和save在同一事务中
//		if (StringUtil.isNotEmpty(oldCureRecord.getDrugAdvise()) && StringUtil.isNotEmpty(intance.getDrugAdviseJson())) {
//			String drugAdviseId = updateDrugRecipe(ReqUtil.getToken(),
//					oldCureRecord.getDrugAdvise(), order.getDoctorId(),
//					order.getUserId(), order.getPatientId(),
//					order.getGroupId(), order.getId(), intance.getDrugAdviseJson());
//			intance.setDrugAdvise(drugAdviseId);
//		}
		// 保存患者病种关系(不进行判空操作，但凡更新诊疗记录，都将之前的患者病种删除并重新保存2016-8-6傅永德)
		//if (StringUtil.isNotEmpty(oldCureRecord.getConsultAdviseDiseases())) {
			deletePatientDisease(order);
		//}
		//保存患者病种关系不用判空，判空则会导致该患者统计不到集团患者库（2016-7-7傅永德）
		//if (StringUtil.isNotEmpty(intance.getConsultAdviseDiseases())) {
			savePatientDisease(order, intance.getConsultAdviseDiseases());
		//}
		if("0".equals(intance.getIsNeedHelp())){//需要导医帮助的时候再去更新订单的状态
			OrderSession orderSession = orderSessionService.findOneByOrderId(intance.getOrderId());
			//通知IM订单的状态
			GroupStateRequestMessage requestMsg = new GroupStateRequestMessage();
			requestMsg.setGid(orderSession.getMsgGroupId());
			requestMsg.setBizStatus(String.valueOf(order.getOrderStatus()));
			MsgHelper.updateGroupBizState(requestMsg);
			updateOrderRecordStatus(order.getId(), OrderRecordStatus.confirming);
		}
		if(ReqUtil.instance.getUser().getUserType() == UserType.DocGuide.getIndex()){//专门记录导医的更新时间
			intance.setUpdateTime(new Date().getTime());
		}
		return mapper.updateByPrimaryKeySelective(intance);
	}

	private void handleDrugRecipe(CureRecord intance, Order order, CureRecord oldCureRecord) throws HttpApiException {
		//新旧不为空，更新药方
		if (StringUtil.isNotEmpty(oldCureRecord.getDrugAdvise()) && StringUtil.isNotEmpty(intance.getDrugAdviseJson())) {
            updateDrugRecipe(
                    oldCureRecord.getDrugAdvise(), order.getDoctorId(),
                    order.getUserId(), order.getPatientId(),
                    order.getGroupId(), order.getId(), intance.getDrugAdviseJson());
            intance.setDrugAdvise(oldCureRecord.getDrugAdvise());
        //新数据为空,删除药方
        }else if (StringUtil.isNotEmpty(oldCureRecord.getDrugAdvise()) && StringUtil.isEmpty(intance.getDrugAdviseJson())) {
            deleteDrugRecipe(ReqUtil.instance.getToken(), oldCureRecord.getDrugAdvise());
            intance.setDrugAdvise(null);
        //新增药方
        }else if (StringUtil.isEmpty(oldCureRecord.getDrugAdvise()) && StringUtil.isNotEmpty(intance.getDrugAdviseJson())) {
            String drugAdviseId = saveDrugRecipe(ReqUtil.instance.getToken(), order.getDoctorId(),
                    order.getUserId(), order.getPatientId(),
                    order.getGroupId(), order.getId(), intance.getDrugAdviseJson());
            intance.setDrugAdvise(drugAdviseId);
        }
	}

	@Override
    public List<CureRecord> getVoiceUrlByOrderId(Integer orderId) {
		List<CureRecord> list_obj = new ArrayList<CureRecord>();
            CallRecordParam param = new CallRecordParam();
            int status = DownTaskEnum.DownStatus.recordDownFail.getIndex();
            String filePath = null;
            Boolean downStatus  = false;
            Map<String,Object> map  = null;
            String dest = null;
            String recordUrl = "";//
            String videoUrl="";
            param.setOrderId(orderId);
            param.setRecordStatus(1);
            List<CallRecordVO> list = callRecordRepository.getCallRecordByOrderId(param);//通过订单id找出所有的咨询记录信息
            if(list.size()>0){
                for (CallRecordVO callRecordVO : list) {
                	CureRecord re = new CureRecord();
                	videoUrl=callRecordVO.getVideoUrl();
                	if(StringUtil.isNotEmpty(videoUrl))
                	{
                		 re.setVideoUrl(videoUrl);
                         re.setVideoStopTime(callRecordVO.getEndTime());
                         list_obj.add(re);
                	}
                	else
                	{
                		   recordUrl = callRecordVO.getRecordUrl();
                           if(null!=recordUrl){
                           map = UcPaasRestSdk.downTask(recordUrl, callRecordVO.getRecordId());
                           downStatus = Boolean.parseBoolean(map.get("result").toString());
                           if(downStatus){
                               status = DownTaskEnum.DownStatus.recordDownSuccess.getIndex();
                               filePath = map.get("path").toString();
                               dest = map.get("uuid").toString();
                               
                               String toUrl = QiniuUtil.upload(filePath, dest, DEFAULT_BUCKET);//上传
                               
                               if(callRecordVO.getCallType().intValue() == CallType.doctorToPatient.getIndex()){
                            	   JSON json = QiniuUtil.converterToFormat(toUrl,Default_FORMAT, DEFAULT_BUCKET);//转MP3
          						   toUrl = "http://" + DEFALUT_DOMAIN +"/" + toUrl;
          						   HashMap jsonMap = JSON.parseObject(json.toJSONString(), HashMap.class);
          						   if(jsonMap!=null){
          							  toUrl = "http://" + DEFALUT_DOMAIN +"/" + jsonMap.get("key").toString();
          						   }
                               }
                               
                               if(toUrl!=null){
                                   toUrl = "http://" + DEFALUT_DOMAIN +"/" + toUrl;
                                   status = DownTaskEnum.DownStatus.recordUploadSuccess.getIndex();
                                   re.setVideoUrl(toUrl);
                                   re.setVideoStopTime(callRecordVO.getEndTime());
                                   list_obj.add(re);
                                   
                                   DBObject update = new BasicDBObject("videoUrl",toUrl);
                           		   dsForRW.getDB().getCollection("t_call_record").update(new BasicDBObject("recordId", callRecordVO.getRecordId()),new BasicDBObject("$set",update),false,false);
                           		   
                           		  DBObject update2 = new BasicDBObject("status",69);
            		         	  dsForRW.getDB().getCollection("DownTask").update(new BasicDBObject("recordId", callRecordVO.getRecordId()),new BasicDBObject("$set",update2),false,false);
                               }
                           }
                	}
                 
                  }
                }
            }
            //双向电话
//            List<CallResult> result = callResultService.getAllCallResultByOrderId(orderId);
//            if(result.size()>0){
//                for (CallResult callResult : result) {
//                	CureRecord re = new CureRecord();
//                	String stoptime = "";
//                    recordUrl = callResult.getRecordurl();
//                    map = UcPaasRestSdk.downTask(recordUrl, String.valueOf(callResult.getId()));
//                    downStatus = Boolean.parseBoolean(map.get("result").toString());
//                    if(downStatus){
//                        status = DownTaskEnum.DownStatus.recordDownSuccess.getIndex();
//                        filePath = map.get("path").toString();
//                        dest = map.get("uuid").toString();
//                        //上传
//                        String toUrl = QiniuUtil.upload(filePath, dest, DEFAULT_BUCKET);
//                            //转mp3
//                        JSON json = QiniuUtil.converterToFormat(toUrl,Default_FORMAT, DEFAULT_BUCKET);
//                        status = DownTaskEnum.DownStatus.recordUploadFail.getIndex();
//                        HashMap<String,Object> jsonMap = JSON.parseObject(json.toJSONString(), HashMap.class); 
//                        if(jsonMap!=null){
//                            toUrl = "http://" + DEFALUT_DOMAIN +"/" + jsonMap.get("key").toString();
//                            status = DownTaskEnum.DownStatus.recordUploadSuccess.getIndex();
//                            re.setVideoUrl(toUrl);
//                            re.setVideoStopTime(Long.valueOf(callResult.getStoptime()));
//                            list_obj.add(re);
//                        }
//                    }
//                }
//            }
        return list_obj;
    }
}
