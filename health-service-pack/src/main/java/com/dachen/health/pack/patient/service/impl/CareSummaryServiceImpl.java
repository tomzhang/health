package com.dachen.health.pack.patient.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.drug.api.client.DrugApiClientProxy;
import com.dachen.drug.api.entity.CRecipeView;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.checkbill.entity.po.CheckBill;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.checkbill.service.CheckBillService;
import com.dachen.health.commons.constants.ImageDataEnum;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.utils.PackUtil;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.guide.util.GuideMsgHelper;
import com.dachen.health.pack.illhistory.dao.IllHistoryInfoDao;
import com.dachen.health.pack.illhistory.entity.po.Diagnosis;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryInfo;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.mapper.CareSummaryMapper;
import com.dachen.health.pack.patient.mapper.PatientDiseaseMapper;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.*;
import com.dachen.health.pack.patient.service.ICareSummaryService;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.im.server.data.MsgDocument;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.JSONUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CareSummaryServiceImpl extends BaseServiceImpl<CareSummary, Integer>
implements ICareSummaryService{
	
	@Resource
	private CareSummaryMapper careSummaryMapper;
	
	@Resource
	private OrderMapper orderMapper;

	@Resource
	private PatientDiseaseMapper patientDiseaseMapper;
	
	@Resource
	private DiseaseTypeRepository diseaseTypeRepository;
	
	@Autowired
	private IOrderSessionService orderSessionService;
	
	@Resource
	private IOrderService orderService;
	
	@Resource
	private UserManager userManager;
	
	@Autowired
	private MobSmsSdk mobSmsSdk;
	
	@Resource
	PatientMapper patientMapper;

	@Resource
	IImageDataService imageDataService;
	
	@Autowired
	IBaseDataService baseDataService;
	
	@Autowired
	CheckBillService checkBillService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private IGuideDAO iGuideDAO;
	
	@Autowired
	private IBusinessServiceMsg sendMsgService;
	
	@Autowired
	IBaseDataService baseDataServiceImpl;
	
	@Autowired
	private IllHistoryInfoDao illHistoryInfoDao;
	
	
	@Override
	public int save(CareSummary intance) throws HttpApiException {
		Order order = orderMapper.getOne(intance.getOrderId());
		if (order == null) {
			throw new ServiceException(30003, "找不到对应订单");
		}
		intance.setPatientId(order.getPatientId());
		intance.setUserId(order.getUserId());
		// 保存患者病种关系
		savePatientDisease(order, intance.getConsultAdviseDiseases());
		if (StringUtil.isNotEmpty(intance.getDrugAdviseJson())) {
			String drugAdviseId = saveDrugRecipe(ReqUtil.instance.getToken(),
					order.getDoctorId(), order.getUserId(),
					order.getPatientId(), order.getGroupId(), order.getId(),
					intance.getDrugAdviseJson());
			intance.setDrugAdvise(drugAdviseId);
			logger.info("CareSummaryServiceImpl save: " + drugAdviseId);
		}
		intance.setCreateTime(new Date().getTime());
		intance.setUpdateTime(System.currentTimeMillis());
		if(intance.getAttention().trim().equals("")){
			intance.setAttention(null);
		}
		if(intance.getConsultAdviseDiseases().trim().equals("")){
			intance.setConsultAdviseDiseases(null);
		}
		int ret = careSummaryMapper.insert(intance);
		return ret;
	}

	@Override
	public int update(CareSummary intance) throws HttpApiException {
		Order order = orderMapper.getOne(intance.getOrderId());
		if (order == null) {
			throw new ServiceException(30003, "找不到对应订单");
		}
		intance.setPatientId(order.getPatientId());
		intance.setUserId(order.getUserId());
		CareSummary oldCareSummary=careSummaryMapper.selectByPrimaryKey(intance.getId());
		if (!(StringUtil.isEmpty(oldCareSummary.getDrugAdvise()) && StringUtil
				.isEmpty(intance.getDrugAdviseJson()))) {
			updateDrugRecipe(
					oldCareSummary.getDrugAdvise(), order.getDoctorId(),
					order.getUserId(), order.getPatientId(),
					order.getGroupId(), order.getId(), intance.getDrugAdviseJson());
			intance.setDrugAdvise(oldCareSummary.getDrugAdvise());
		}
		deletePatientDisease(order);
		savePatientDisease(order, intance.getConsultAdviseDiseases());
		intance.setUpdateTime(System.currentTimeMillis());
		return careSummaryMapper.updateByPrimaryKeySelective(intance);
	}

	@Override
	public int deleteByPK(Integer pk) {
		return careSummaryMapper.deleteByPrimaryKey(pk);
	}

	@Override
	public CareSummary findByPk(Integer pk) throws HttpApiException {
		CareSummary careSummary = careSummaryMapper.selectByPrimaryKey(pk);
		if (careSummary != null) {
			// 填充数据 start
			fillCheckSuggestList(careSummary);
			// 填充数据 end
			List<String> images = imageDataService.findImgData(
					ImageDataEnum.careImage.getIndex(), pk);
			List<String> voices = imageDataService.findImgData(
					ImageDataEnum.careVoice.getIndex(), pk);
			
			if (images != null) {
				careSummary.setImages(images.toArray(new String[images.size()]));
			}
			if (voices != null) {
				careSummary.setVoices(voices.toArray(new String[voices.size()]));
			}
			// 返回医生信息 以及患者信息
			Order order = orderService.getOne(careSummary.getOrderId());
			User doc = userManager.getUser(order.getDoctorId());//医生信息
			Patient patitent = patientMapper.selectByPrimaryKey(order.getPatientId());//患者信息
			careSummary.setPatient(patitent);
			careSummary.setUser(doc);
			return careSummary;
		} else {
			throw new ServiceException(30001, "can't found CureRecord#" + pk);
		}
	}
	
	@Override
	public List<CareSummary> findByOrderId(int orderId) throws HttpApiException {
		List<CareSummary> datas = careSummaryMapper.selectByOrderId(orderId);
		if(datas==null||datas.isEmpty()){
			return Collections.emptyList();
		}
		List<CareSummary> adata = new ArrayList<CareSummary>();
		for (CareSummary cureRecord : datas) {
			adata.add(findByPk(cureRecord.getId()));
		}
		return adata;
	}

	@Autowired
	protected DrugApiClientProxy drugApiClientProxy;
	
	private String saveDrugRecipe(String token, Integer doctor, Integer user_id, Integer patient, String group,
			Integer orderId, String drugAdviseJson) throws HttpApiException {
		return drugApiClientProxy.saveDrugRecipe(token, doctor, user_id, patient, group, orderId, 2, drugAdviseJson);
	}

	
	private void updateDrugRecipe(String drugAdviseId, Integer doctor, Integer user_id, Integer patient,
			String group, Integer orderId, String drugAdviseJson) throws HttpApiException {
		drugApiClientProxy.updateDrugRecipe(drugAdviseId, doctor, user_id, patient, group, orderId, 2, drugAdviseJson);
	}
	
	
	private void deletePatientDisease(Order order) {
		PatientDiseaseExample example = new PatientDiseaseExample();
		example.createCriteria().andOrderIdEqualTo(order.getId());
		patientDiseaseMapper.deleteByExample(example);

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
	
	/**
	 * 填充数据
	 */
	private CareSummary fillCheckSuggestList(CareSummary careSummary) throws HttpApiException {
		if (careSummary != null) {
			String attention = careSummary.getAttention();
			if (attention != null && attention.isEmpty() == false) {
				// 分解
				String[] attentionIds = attention.split(",");
				if (attentionIds != null && attentionIds.length > 0) {
					List<CheckSuggest> checkSuggestList = baseDataService
							.getCheckSuggestByIds(attentionIds);
					// set
					careSummary.setCheckSuggestList(checkSuggestList);
				}
			}
			String consultAdviseDiseases = careSummary.getConsultAdviseDiseases();
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
					careSummary.setConsultAdviseDiseaseList(diseaseList);
				}
			}

			String recipeId = careSummary.getDrugAdvise();
			if (!StringUtil.isEmpty(recipeId)) {
				logger.info("############fillCheckSuggestList: " + recipeId);
				CRecipeView recipeView = drugApiClientProxy.getDrugRecipe(recipeId);
				careSummary.setRecipeView(recipeView);
			}
		}
		return careSummary;
	}
	
	

	@Override
	public int save(CareSummary intance, String[] images, String[] voices) throws HttpApiException {
		save(intance);
		int cureRecordId = intance.getId();
		if (cureRecordId > 0) {
			if (images != null) {
				for (String k : images) {
					saveAnnex(cureRecordId, k, ImageDataEnum.careImage);
				}
			}
			if (voices != null) {
				for (String k : voices) {
					saveAnnex(cureRecordId, k, ImageDataEnum.careVoice);
				}
			}
			/**
			 * 保存检查单记录
			 */
			if (StringUtil.isNotBlank(intance.getAttention())) {
				boolean checkBillSaveFlag = saveCheckBill(intance.getOrderId(),
						intance.getAttention());
				if (!checkBillSaveFlag) {
					throw new ServiceException(30001, "咨询小结创建成功，保存检查单失败");
				}
			}
			/**添加到初步诊断**/
			Order order = orderMapper.getOne(intance.getOrderId());
			if(order != null && StringUtil.isNotEmpty(order.getIllHistoryInfoId())){
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
							illHistoryInfoDao.addDiagnosis(info.getId(), intance.getConsultAdvise(), intance.getConsultAdviseDiseases(), intance.getDoctorId(), order.getId());
						}
					} else {
						illHistoryInfoDao.addDiagnosis(info.getId(), intance.getConsultAdvise(), intance.getConsultAdviseDiseases(), intance.getDoctorId(), order.getId());
					}
				}
			}
			
		} else {
			throw new ServiceException(30001, "咨询小结创建失败");
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
		List<CheckSuggest> css = baseDataService
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
	

	// 保存附件
	private void saveAnnex(int cureRecordId, String k, ImageDataEnum imageType) {
		ImageData imageData = PackUtil.newImageData(cureRecordId,
				ReqUtil.instance.getUserId(), k, imageType);
		imageDataService.add(imageData);
	}

	@Override
	public void sendNotice(CareSummary intance) throws HttpApiException {
		if (intance.getDoctorId() == null) {
			return;
		}
		User uu = userRepository.getUser(intance.getDoctorId());
		Order order = orderMapper.getOne(intance.getOrderId());
		OrderSession os = orderSessionService.findOneByOrderId(order.getId());
		if (uu != null&& uu.getUserType() == UserEnum.UserType.doctor.getIndex()) {
			//系统通知：***医生已为您填写咨询小结。医生回复仅为建议，进一步治疗请到线下医院。
		 	sendMsgService.sendNotifyMsgToUser(String.valueOf(intance.getUserId()),os.getMsgGroupId(), uu.getUsername() + "医生已为您填写咨询小结。医生回复仅为建议，进一步治疗请到线下医院");
		 	//系统通知：您已为患者填写了咨询小结
			sendMsgService.sendNotifyMsgToUser(String.valueOf(intance.getDoctorId()),os.getMsgGroupId(), "您已为患者填写了咨询小结");
			MsgDocument msgDocument = new MsgDocument();
			msgDocument.setHeaderIcon("url");
			List<MsgDocument.DocInfo> list = new ArrayList<>();
			//咨询小结
			if(StringUtils.isNotBlank(intance.getConsultAdvise())){
				MsgDocument.DocInfo d1 = new MsgDocument.DocInfo();
				d1.setTitle("咨询小结");
				d1.setContent(intance.getConsultAdvise());
				d1.setType(0);
				list.add(d1);
			}
			//疑似疾病
			if(StringUtils.isNotBlank(intance.getConsultAdviseDiseases())){
				MsgDocument.DocInfo d2 = new MsgDocument.DocInfo();
				d2.setTitle("疑似疾病");
				List<DiseaseType> dts = diseaseTypeRepository.findByIds(Arrays.asList(intance.getConsultAdviseDiseases().split(",")));
				if(dts != null){
					d2.setContent(dts.stream().map(o -> o.getName()).collect(Collectors.joining(" ")));
				}
				d2.setType(0);
				list.add(d2);
			}
			//检查项目
			if(StringUtils.isNotBlank(intance.getAttention())){
				MsgDocument.DocInfo d3 = new MsgDocument.DocInfo();
				d3.setTitle("检查项目");
				List<CheckSuggest> css = baseDataServiceImpl.getCheckSuggestByIds(intance.getAttention().split(","));
				if(css != null)
					d3.setContent(css.stream().map(o -> o.getName()).collect(Collectors.joining(" ")));
				d3.setType(0);
				list.add(d3);
			}
			//用药建议
			if(StringUtils.isNotBlank(intance.getDrugAdviseJson())){
				List drugs = JSONUtil.parseObject(List.class,intance.getDrugAdviseJson());
				String content = drugs.stream().map( o -> { Map m = (Map)o; return m.get("goodsName");}).collect(Collectors.joining(","))+"";
				MsgDocument.DocInfo d4 = new MsgDocument.DocInfo();
				d4.setTitle("用药建议");
				d4.setContent(content);
				d4.setType(0);
				list.add(d4);
			}
			//影像资料
			if(intance.getImages() != null && intance.getImages().length > 0){
				MsgDocument.DocInfo d5 = new MsgDocument.DocInfo();
				d5.setTitle("影像资料");
				d5.setType(1);
				List<Map<String,Object>> pics = new ArrayList<>();
				for(String url : intance.getImages()){
					Map<String,Object> pic = new HashMap<>();
					pic.put("url",url);
					pics.add(pic);
				}
				d5.setPic(pics);
				list.add(d5);
			}

			Map<String, Object> param_b = new HashMap<>();
			param_b.put("doctorId",order.getDoctorId());
			param_b.put("userId",order.getUserId());
			param_b.put("patientId",order.getPatientId());
			param_b.put("diseaseId",order.getDiseaseID());
            param_b.put("illHistoryInfoId",order.getIllHistoryInfoId());
			msgDocument.setBizParam(param_b);
			msgDocument.setList(list);
			msgDocument.setHeader(PackEnum.PackType.getTitle(order.getPackType()));
			msgDocument.setHeaderIcon(getPackHeaderIcon(order.getPackType()));
			GuideMsgHelper.getInstance().sendMsgDocument(os.getMsgGroupId(), order.getDoctorId() + "", null, msgDocument, false);
		}
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
		}
		return MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.DEFAULT_BUCKET, imgPath);
	}

}
