package com.dachen.health.pack.consult.Service.impl;

import static com.dachen.health.commons.constants.UserEnum.UserType.patient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.checkbill.entity.vo.CheckBillPageVo;
import com.dachen.health.checkbill.service.CheckBillService;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.SysConstants;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.consult.Service.ElectronicIllCaseService;
import com.dachen.health.pack.consult.dao.ElectronicIllCaseDao;
import com.dachen.health.pack.consult.entity.po.IllCaseInfo;
import com.dachen.health.pack.consult.entity.po.IllCasePatientInfo;
import com.dachen.health.pack.consult.entity.po.IllCaseType;
import com.dachen.health.pack.consult.entity.po.IllCaseTypeContent;
import com.dachen.health.pack.consult.entity.po.IllTransferRecord;
import com.dachen.health.pack.consult.entity.vo.ConsultationEnum;
import com.dachen.health.pack.consult.entity.vo.CureRecordAndDiseaseVo;
import com.dachen.health.pack.consult.entity.vo.IllCaseEnum;
import com.dachen.health.pack.consult.entity.vo.IllCaseInfoPageVo;
import com.dachen.health.pack.consult.entity.vo.IllCaseTypeContentPageVo;
import com.dachen.health.pack.consult.entity.vo.IllHistoryInfoItem;
import com.dachen.health.pack.consult.entity.vo.IllRecordItem;
import com.dachen.health.pack.consult.entity.vo.PatientIllCaseItemVo;
import com.dachen.health.pack.consult.entity.vo.PatientIllRecordItemVo;
import com.dachen.health.pack.consult.entity.vo.PatientIllRecordListVo;
import com.dachen.health.pack.consult.entity.vo.SeekIllInfoPageVo;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.illhistory.dao.IllHistoryInfoDao;
import com.dachen.health.pack.illhistory.dao.IllHistoryRecordDao;
import com.dachen.health.pack.illhistory.entity.po.Diagnosis;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryInfo;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryRecord;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.service.IOrderDoctorService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.model.CareSummary;
import com.dachen.health.pack.patient.model.CureRecord;
import com.dachen.health.pack.patient.model.Disease;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.ICareSummaryService;
import com.dachen.health.pack.patient.service.ICureRecordService;
import com.dachen.health.pack.patient.service.IDiseaseService;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.user.service.IRelationService;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.StringUtils;
import com.tencent.common.Util;

@Service("electronicIllCaseService")
public class ElectronicIllCaseServiceImpl implements ElectronicIllCaseService{
	
	private static final Logger logger = LoggerFactory.getLogger(ElectronicIllCaseServiceImpl.class);

	@Autowired
	ElectronicIllCaseDao electronicIllCaseDao;
	
	@Autowired
	IPatientService patientServiceImpl;
	
	@Autowired
	ICureRecordService cureRecordServiceImpl;
	
	@Autowired
	CheckBillService checkBillServiceImpl;
	
	@Autowired
	IOrderService orderServiceImpl;
	
	@Autowired
	IOrderDoctorService orderDoctorService;
	
	@Autowired
	OrderMapper orderMapper;
	
	@Autowired
	UserManager userManager;
	
	@Autowired
	private IDiseaseService diseaseService;
	
	@Autowired
	IRelationService	relationService;
	
	@Autowired
	IOrderSessionService orderSessionService;
	
	@Autowired
	IGuideDAO guideDao;
	
	@Resource
	ICareSummaryService careSummaryservice;
	
	@Autowired
	private IllHistoryInfoDao illHistoryInfoDao;
	
	@Autowired
	private IllHistoryRecordDao recordDao;
	
	/**
	 * 保存数据
	 */
	private Order commOrder;

	public synchronized Order getCommOrder() {
		return commOrder;
	}

	public synchronized void setCommOrder(Order commOrder) {
		this.commOrder = commOrder;
	}
	
	public synchronized void clearCommOrder(){
		commOrder = null;
	}

	@Override
	public IllCaseInfo createIllCaseInfo(IllCaseInfo illCaseInfo) {
		int patientId = illCaseInfo.getPatientId();
		int doctorId = illCaseInfo.getDoctorId();
		Patient patient = patientServiceImpl.findByPk(patientId);
		IllCaseInfo rtnici = null;
		if(patient != null){
			long time = System.currentTimeMillis();
			illCaseInfo.setDoctorId(doctorId);
			illCaseInfo.setCreateTime(time);
			illCaseInfo.setUpdateTime(time);
			rtnici = electronicIllCaseDao.insertIllCase(illCaseInfo);
			String illCaseInfoId = rtnici.getId();
			IllCasePatientInfo icpi = new IllCasePatientInfo();
			icpi.setArea(patient.getArea());
			icpi.setPatientId(patientId);
			icpi.setAge(patient.getAge());
			icpi.setAgeStr(patient.getAgeStr());
			icpi.setCreateTime(time);
			icpi.setSex(patient.getSex() == null ? "" : + patient.getSex()+"");
			icpi.setDoctorId(doctorId);
			icpi.setCreateTime(time);
			icpi.setIllCaseInfoId(illCaseInfoId);
			icpi.setTelephone(patient.getTelephone());
			icpi.setPatientName(patient.getUserName());
			electronicIllCaseDao.insertIllCasePatient(icpi);
			
			/**
			 * update
			 * at 2016年3月1日16:08:59
			 * version 2
			 */
			if(StringUtil.isNotBlank(illCaseInfo.getMainCase())){
				String mainCaseTypeId = electronicIllCaseDao.getMustFillitemId();
				IllCaseTypeContent illCaseTypeContent = new IllCaseTypeContent();
				illCaseTypeContent.setContentImages(illCaseInfo.getImageUlrs());
				illCaseTypeContent.setContentTxt(illCaseInfo.getMainCase());
				illCaseTypeContent.setCreateTime(illCaseInfo.getCreateTime());
				illCaseTypeContent.setIllCaseInfoId(illCaseInfoId);
				illCaseTypeContent.setIllCaseTypeId(mainCaseTypeId);
				illCaseTypeContent.setUpdateTime(time);
				this.saveIllCaseTypeContent(illCaseTypeContent);
			}
			/**
			 * 添加诊治情况
			 */
			if(StringUtil.isNotBlank(illCaseInfo.getSeeDoctorMsg())){
				String seeDoctorMsgTypeId = getSeeDoctorMsgTypeId();
				IllCaseTypeContent illCaseTypeContent = new IllCaseTypeContent();
				illCaseTypeContent.setContentTxt(illCaseInfo.getSeeDoctorMsg());
				illCaseTypeContent.setCreateTime(illCaseInfo.getCreateTime());
				illCaseTypeContent.setIllCaseInfoId(illCaseInfoId);
				illCaseTypeContent.setIllCaseTypeId(seeDoctorMsgTypeId);
				illCaseTypeContent.setUpdateTime(time);
				this.saveIllCaseTypeContent(illCaseTypeContent);
			}
		}
		return rtnici;
	}

	private String getSeeDoctorMsgTypeId() {
		List<IllCaseType> list = electronicIllCaseDao.getInitIllCaseTypeByCategoryId(1);
		if(list != null && list.size() > 0){
			for (IllCaseType type : list) {
				String typeName = type.getTypeName();
				if("诊治情况".equals(typeName)){
					return type.getId();
				}
			}
		}
		return null;
	}

	@Override
	public IllCasePatientInfo getIllCasePatient(String illCaseInfoId,Integer patientId) {
		IllCasePatientInfo ip = electronicIllCaseDao.getIllCasePatient(illCaseInfoId);
		if(patientId != null){
			Patient p = patientServiceImpl.findByPk(patientId);
			ip.setAgeStr(p.getAgeStr());
			ip.setSex(p.getSex() == null ? "" : p.getSex() + "");
			ip.setPatientName(p.getUserName());
		}
		User user = userManager.getUser(ReqUtil.instance.getUserId());
		if(user.getUserType().intValue() != patient.getIndex()){
			String tel=ip.getTelephone();
			if(!StringUtils.isEmpty(tel) && tel.length() > 3){
				int end = 7;
				if(end > tel.length()){
					end = tel.length();
				}
				//15122222223
				tel = tel.substring(0,3) + "****" +tel.substring(end);
				ip.setTelephone(tel);
			}
		}
		return ip;
	}

	@Override
	public IllCasePatientInfo updateIllCasePatient(IllCasePatientInfo illCasePatientInfo) {
		if(StringUtil.isNullOrEmpty(illCasePatientInfo.getIllCaseInfoId())){
			throw new ServiceException("电子病历id为空");
		}
		return electronicIllCaseDao.updateIllCasePatient(illCasePatientInfo);
	}

	@Override
	public IllCaseInfoPageVo getIllCaseInfo(Integer patientId,Integer userId,Integer doctorId,Integer enterType) throws HttpApiException {
		//根据医生和患者来查询当前病历是否存在
		IllCaseInfo info = electronicIllCaseDao.getIllCaseBase(patientId, doctorId);
		if(info == null){
			info = new IllCaseInfo();
			info.setPatientId(patientId);
			info.setUserId(userId);
			info.setDoctorId(doctorId);
			this.createIllCaseInfo(info);
		}
		if(enterType != null && enterType.intValue() == 2){
			electronicIllCaseDao.updateIllCasetoSaved(info.getId());
		}
		return getIllCaseInfo(info);
	}
	
	@Override
	public IllCaseInfoPageVo getIllCaseInfo(String illCaseId) throws HttpApiException {
		//根据医生和患者来查询当前病历是否存在
		IllCaseInfo info = electronicIllCaseDao.getIllCase(illCaseId);
		if (info == null) {
			throw new ServiceException("病历不存在");
		}
		return getIllCaseInfo(info);
	}

/*	private IllCaseInfoPageVo getIllCaseInfo(IllCaseInfo info) {
		//清空电子病历中冗余的数据
		clearContentData(info.getId());

		//获取所有基础病历类型
		List<IllCaseType> illCaseTypeList =  electronicIllCaseDao.getInitIllCaseTypeByCategoryId(0);
		
		IllCasePatientInfo icpi = electronicIllCaseDao.getIllCasePatient(info.getId());
		
		List<IllCaseTypeContent> illCaseTypeContentList = new ArrayList<IllCaseTypeContent>();
		if(icpi != null){
			String illCaseInfoId = icpi.getIllCaseInfoId();
			//获取各种比病历史和就医资料内容
			illCaseTypeContentList = electronicIllCaseDao.getIllCaseTypeContentListByIllcaseId(illCaseInfoId);
		}else{
			throw new ServiceException("根据参数 {patientId="+info.getPatientId()+",doctorId="+info.getDoctorId()+"}获取不到对应的病历或病历患者信息");
		}
		//组装基础病史和就医资料的集合
		List<IllCaseTypeContentPageVo> baseContentList = new ArrayList<IllCaseTypeContentPageVo>();
		
		//护士添加的就医资料列表
		List<SeekIllInfoPageVo> seekInfoList = new ArrayList<SeekIllInfoPageVo>();
		
		{1: 基础病史分类，2：就医资料分类}
		for (IllCaseType illCaseType : illCaseTypeList) {
			int categoryId = illCaseType.getCategoryId();
			if(categoryId == 1){
				//基础病史数据
				IllCaseTypeContentPageVo item = (IllCaseTypeContentPageVo) setItemPageVo(illCaseTypeContentList, illCaseType,categoryId);
				baseContentList.add(item);
			}else if(categoryId == 2){
				@SuppressWarnings("unchecked")
				List<SeekIllInfoPageVo> itemList = (List<SeekIllInfoPageVo>) setItemPageVo(illCaseTypeContentList, illCaseType,categoryId);
				if(!Util.isNullOrEmpty(itemList)){
					seekInfoList.addAll(itemList);
				}
			}
		}
		//获取咨询建议
		List<CureRecord> cureRecords = cureRecordServiceImpl.findByPatientAndDoctor(info.getPatientId(), info.getDoctorId());
		Set<Integer> orderIds = new HashSet<Integer>();
		if(cureRecords != null && cureRecords.size() > 0){
			for (CureRecord cureRecord : cureRecords) {
				int orderId = cureRecord.getOrderId();
				SeekIllInfoPageVo item = new SeekIllInfoPageVo();
				CureRecordAndDiseaseVo cradVo = new CureRecordAndDiseaseVo();
				cradVo.setCureRecord(cureRecord);
				Order order = orderServiceImpl.getOne(orderId);
				if (null != order) {
					Integer disesaseId = order.getDiseaseId();
					Disease dis = diseaseService.findByPk(disesaseId);
					cradVo.setDisease(dis);
				}
				item.setCreateTime(cureRecord.getCreateTime());
				item.setType(IllCaseEnum.SeekIllType.cureRecord.getIndex());
				item.setCureRecordAndDiseaseVo(cradVo);
				seekInfoList.add(item);
				orderIds.add(cureRecord.getOrderId());
			}
		}
		*//***begin add  by  liwei  2016年2月18日********//*
		else
           {
			Order order = getCommOrder();
			if (null != order) {
				Integer disesaseId = order.getDiseaseId();
				Disease dis = diseaseService.findByPk(disesaseId);
				List<String> urls = imageDataService.findImgData(
						ImageDataEnum.dessImage.getIndex(), disesaseId);
				dis.setDiseaseImgs(urls);
				if (null != urls && urls.size() > 0) {
					dis.setDiseaseImgs(urls);
				}
				buildCaseDisease(baseContentList, dis);
				
				clearCommOrder();//清除订单对象
			
			}

		}
		*//***end add  by  liwei  2016年2月18日********//*
		//获取检查建议
		if(orderIds.size() > 0){
			List<CheckBillPageVo> checkBillPageVos = checkBillServiceImpl.getCheckBillList(orderIds,info.getPatientId());
			if(checkBillPageVos != null && checkBillPageVos.size() > 0){
				for (CheckBillPageVo cbpv : checkBillPageVos) {
					SeekIllInfoPageVo item = new SeekIllInfoPageVo();
					item.setCreateTime(cbpv.getCreateTime());
					item.setCheckBillPageVo(cbpv);
					item.setType(IllCaseEnum.SeekIllType.checkItem.getIndex());
					seekInfoList.add(item);
				}
			}
		}
		
		Collections.sort(seekInfoList, new Comparator<SeekIllInfoPageVo>() {
			public int compare(SeekIllInfoPageVo o1, SeekIllInfoPageVo o2) {
				long decrease = o2.getCreateTime() - o1.getCreateTime();
				return decrease > 0 ? 1 : -1;
			};
		});
		
		IllCaseInfoPageVo icipv = new IllCaseInfoPageVo();
		Patient patient = patientServiceImpl.findByPk(info.getPatientId());
		if(patient != null){
			icipv.setAgeStr(patient.getAgeStr());
			icipv.setAge(patient.getAge());
			icipv.setSex(patient.getSex());
			icipv.setPatientName(patient.getUserName());
			icipv.setArea(patient.getArea());
		}
		icipv.setPatientId(info.getPatientId());
		icipv.setDoctorId(info.getDoctorId());
		icipv.setUserId(info.getUserId());
		icipv.setIllCaseInfoId(icpi.getIllCaseInfoId());
		icipv.setTelephone(icpi.getTelephone());
		icipv.setBaseContentList(baseContentList);
		icipv.setSeekInfoList(seekInfoList);
		return icipv;
	}*/
    
	private IllCaseInfoPageVo getIllCaseInfo(IllCaseInfo info) throws HttpApiException {
		//获取所有基础病历类型
			List<IllCaseType> illCaseTypeList =  electronicIllCaseDao.getInitIllCaseTypeByCategoryId(0);
			
			IllCasePatientInfo icpi = electronicIllCaseDao.getIllCasePatient(info.getId());
			
			List<IllCaseTypeContent> illCaseTypeContentList = new ArrayList<IllCaseTypeContent>();
			if(icpi != null){
				String illCaseInfoId = icpi.getIllCaseInfoId();
				//获取各种比病历史和就医资料内容
				illCaseTypeContentList = electronicIllCaseDao.getIllCaseTypeContentListByIllcaseId(illCaseInfoId);
			}else{
				throw new ServiceException("找不到对应的患者");
			}
			//组装基础病史集合
			List<IllCaseTypeContentPageVo> baseContentList = new ArrayList<IllCaseTypeContentPageVo>();
/*			IllCaseTypeContentPageVo treateType = new IllCaseTypeContentPageVo();
			treateType.setIllCaseInfoId(info.getId());
			treateType.setIllCaseTypeId("treateType");
			treateType.setTypeName("就诊类型");
			treateType.setContentTxt(info.getTreateType() == null ? "" : info.getTreateType()+"");
			baseContentList.add(treateType);*/
			//护士添加的就医资料列表
			List<SeekIllInfoPageVo> seekInfoList = new ArrayList<SeekIllInfoPageVo>();
			
			//{1: 基础病史分类，2：就医资料分类}
			for (IllCaseType illCaseType : illCaseTypeList) {
				int categoryId = illCaseType.getCategoryId();
				if(categoryId == 1){
					//基础病史数据
					IllCaseTypeContentPageVo item = (IllCaseTypeContentPageVo) setItemPageVo(illCaseTypeContentList, illCaseType,categoryId);
					if(item != null){
						baseContentList.add(item);
					}
				}else if(categoryId == 2){
					@SuppressWarnings("unchecked")
					List<SeekIllInfoPageVo> itemList = (List<SeekIllInfoPageVo>) setItemPageVo(illCaseTypeContentList, illCaseType,categoryId);
					if(!Util.isNullOrEmpty(itemList)){
						seekInfoList.addAll(itemList);
					}
				}
			}
			String illCaseInfoId = info.getId();
			Set<Integer> orderIds = orderServiceImpl.findOrderIdByIllCaseInfoId(illCaseInfoId);
			
			if(orderIds != null && orderIds.size() > 0){
				for (Integer orderId : orderIds) {
					OrderSession os = orderSessionService.findOneByOrderId(orderId);
					//生成咨询记录卡片
					if(os != null && os.getServiceBeginTime() != null){
						Order order = orderServiceImpl.getOne(orderId);
						SeekIllInfoPageVo item = new SeekIllInfoPageVo();
						CureRecordAndDiseaseVo cradVo = new CureRecordAndDiseaseVo();
						
						List<CureRecord> cureRecords = cureRecordServiceImpl.findByOrderId(orderId);
						if(cureRecords != null && cureRecords.size() > 0){
							for (CureRecord cureRecord : cureRecords) {
								Integer packType = order.getPackType();
								Integer recordStatus = order.getRecordStatus();
								if(packType != null && 
										(packType.intValue() == PackEnum.PackType.phone.getIndex() 
											|| packType.intValue() == PackEnum.PackType.consultation.getIndex() )){
									if(recordStatus == null || 
											(recordStatus.intValue() != OrderEnum.OrderRecordStatus.confirmed.getIndex() 
												&& recordStatus.intValue() != OrderEnum.OrderRecordStatus.doc_confirmed.getIndex() )){
										continue;
									}
								}
								cradVo.setCureRecord(cureRecord);
								if (null != order) {
									Integer disesaseId = order.getDiseaseId();
									Disease dis = diseaseService.findByPk(disesaseId);
									cradVo.setDisease(dis);
								}
							}
						}
						
						/**
						 * mainDoctorName
						 * secondaryDoctorName
						 * orderType
						 * beginTime
						 * endTime
						 */
						Map<String,Object> orderInfo = new HashMap<String,Object>();
						setDoctorName(order, orderInfo);
						orderInfo.put("cardType", getIllCaseType(order));
						orderInfo.put("beginTime", os.getServiceBeginTime());
						orderInfo.put("endTime", os.getServiceEndTime());
						cradVo.setOrderInfo(orderInfo);
						item.setCreateTime(os.getServiceBeginTime());
						item.setType(IllCaseEnum.SeekIllType.cureRecord.getIndex());
						item.setCureRecordAndDiseaseVo(cradVo);
						seekInfoList.add(item);
					}
				  }
					
				//获取检查建议
				List<CheckBillPageVo> checkBillPageVos = checkBillServiceImpl.getCheckBillList(orderIds,info.getPatientId());
				if(checkBillPageVos != null && checkBillPageVos.size() > 0){
					for (CheckBillPageVo cbpv : checkBillPageVos) {
						SeekIllInfoPageVo item = new SeekIllInfoPageVo();
						item.setCreateTime(cbpv.getCreateTime());
						item.setCheckBillPageVo(cbpv);
						item.setType(IllCaseEnum.SeekIllType.checkItem.getIndex());
						seekInfoList.add(item);
					}
				}
			}
			
			/**
			 * 添加转诊记录
			 */
			List<IllTransferRecord> transferRecordList = electronicIllCaseDao.getIllTransferRecordByIllCaseId(illCaseInfoId);
			if(transferRecordList != null && transferRecordList.size() > 0){
				for (IllTransferRecord illTransferRecord : transferRecordList) {
					SeekIllInfoPageVo item = new SeekIllInfoPageVo();
					User ut = userManager.getUser(illTransferRecord.getTransferDoctorId());
					illTransferRecord.setTransferDoctorName(ut.getName());
					User ur = userManager.getUser(illTransferRecord.getReceiveDoctorId());
					illTransferRecord.setReceiveDoctorName(ur.getName());
					item.setCreateTime(illTransferRecord.getTransferTime());
					item.setIllTransferRecord(illTransferRecord);
					item.setType(IllCaseEnum.SeekIllType.transferRecord.getIndex());
					seekInfoList.add(item);
				}
			}

			Collections.sort(seekInfoList, new Comparator<SeekIllInfoPageVo>() {
				public int compare(SeekIllInfoPageVo o1, SeekIllInfoPageVo o2) {
					long decrease = o2.getCreateTime() - o1.getCreateTime();
					return decrease > 0 ? 1 : -1;
				};
			});
			
			IllCaseInfoPageVo icipv = new IllCaseInfoPageVo();
			icipv.setAgeStr(icpi.getAgeStr());
			icipv.setSex(icpi.getSex());
			icipv.setPatientName(icpi.getPatientName());
			icipv.setArea(icpi.getArea());
			icipv.setPatientId(info.getPatientId());
			icipv.setHeight(icpi.getHeight()+"");
			icipv.setWeight(icpi.getWeight()+"");
			icipv.setDoctorId(info.getDoctorId());
			icipv.setUserId(info.getUserId());
			icipv.setIllCaseInfoId(icpi.getIllCaseInfoId());
			String tel=icpi.getTelephone();
			if(!StringUtils.isEmpty(tel) && tel.length() > 3){
				icipv.setTelephoneOk(new String(tel));
				int end = 7;
				if(end > tel.length()){
					end = tel.length();
				}
				tel = tel.substring(0,3) + "****" +tel.substring(end);
				icipv.setTelephone(tel);
			}
			icipv.setBaseContentList(baseContentList);
			icipv.setSeekInfoList(seekInfoList);
			icipv.setTreateType(info.getTreateType());

			return icipv;
	}

	/**
	 * 		 	 text(1, "图文咨询"), 
		 	 phone(2, "电话咨询"),
			 consultation(3, "会诊咨询"),
			 outPatient(4, "门诊咨询"),
			 care(5, "健康关怀"),
			 appointment(6, "预约名医");
	 * @param order
	 * @return
	 */
	private Integer getIllCaseType(Order order) {
		Integer result = null;
		Integer orderType = order.getOrderType();
		Integer packType = order.getPackType();
		if(orderType == null){
			return result;
		}
		if(orderType.intValue() == OrderEnum.OrderType.order.getIndex()){
			if(packType == null){
				return result;
			}
			if(packType.intValue() == PackEnum.PackType.message.getIndex()){
				result = ConsultationEnum.IllCaseTreatType.text.getIndex();
			}else if(packType.intValue() == PackEnum.PackType.phone.getIndex()){
				result = ConsultationEnum.IllCaseTreatType.phone.getIndex();
			}
		}else if(orderType.intValue() == OrderEnum.OrderType.outPatient.getIndex()){
			result = ConsultationEnum.IllCaseTreatType.outPatient.getIndex();
		}else if(orderType.intValue() == OrderEnum.OrderType.consultation.getIndex()){
			result = ConsultationEnum.IllCaseTreatType.consultation.getIndex();
		}else if(orderType.intValue() == OrderEnum.OrderType.care.getIndex()){
			result = ConsultationEnum.IllCaseTreatType.care.getIndex();
		}else if(orderType.intValue() == OrderEnum.OrderType.appointment.getIndex()){
			result = ConsultationEnum.IllCaseTreatType.appointment.getIndex();
		}
		return result;
	}

	private void setDoctorName(Order order, Map<String, Object> orderInfo) {
		Integer orderId = order.getId();
		Integer mainOrassistantId = order.getDoctorId();
		Integer orderType = order.getOrderType();
		List<OrderDoctor> orderDoctors = orderDoctorService.findOrderDoctors(orderId);
		if(orderDoctors != null && orderDoctors.size() > 0){
			for (OrderDoctor od : orderDoctors) {
				if(OrderEnum.OrderType.consultation.getIndex() == orderType.intValue()){
					if(mainOrassistantId.intValue() == od.getDoctorId().intValue()){
						//小医生
						User u = userManager.getUser(od.getDoctorId());
						orderInfo.put("secondaryDoctorName", u.getName());
					}else{
						//大医生
						User u = userManager.getUser(od.getDoctorId());
						orderInfo.put("mainDoctorName", u.getName());
					}
				}else{
					if(mainOrassistantId.intValue() == od.getDoctorId().intValue()){
						//主医生
						User u = userManager.getUser(od.getDoctorId());
						orderInfo.put("mainDoctorName", u.getName());
					}else{
						User u = userManager.getUser(od.getDoctorId());
						orderInfo.put("secondaryDoctorName", u.getName());
					}
				}
			}
		}else{
			//主医生
			User u = userManager.getUser(mainOrassistantId);
			orderInfo.put("mainDoctorName", u.getName());
		}
	}


/*	private  void  buildCaseDisease(List<IllCaseTypeContentPageVo> baseContentList,Disease disease)
	{
		if(null!=baseContentList&& baseContentList.size()>0)
		{	
			for(IllCaseTypeContentPageVo page:baseContentList)
			{	

				//"",   现病史  既往史  家族史  月经生育史  就诊时间   首诊  seeDoctorMsg
				String  typeName = page.getTypeName();
				
				if(!"现病史, 既往史, 家族史 ,月经生育史,就诊时间,首诊,主诉,诊治情况".contains(typeName))
				{	
					continue;
				}
				
				String diseaseInfo = disease.getDiseaseInfo();
				if(StringUtils.isNotEmpty(diseaseInfo))
				{	
					if("主诉".equals(typeName))
					{	
						if(null!=disease.getDiseaseImgs() && disease.getDiseaseImgs().size()>0)
						{	
							page.setContentImages(disease.getDiseaseImgs());
						}
						
						page.setContentTxt(diseaseInfo);
					}
				}
				// 现病史
				String diseaseInfoNow = disease.getDiseaseInfoNow();
				
				if(StringUtils.isNotEmpty(diseaseInfoNow))
				{	
					if("现病史".equals(typeName))
					{	
						page.setContentTxt(diseaseInfoNow);
					}
				}
				// 既往史
				String diseaseInfoOld = disease.getDiseaseInfoOld();
				if(StringUtils.isNotEmpty(diseaseInfoOld))
				{	
					if("既往史".equals(typeName))
					{	
						page.setContentTxt(diseaseInfoOld);
					}
				}
				// 家族史
				String familyDiseaseInfo = disease.getFamilyDiseaseInfo();
				if(StringUtils.isNotEmpty(familyDiseaseInfo))
				{	
					if("家族史".equals(typeName))
					{	
						page.setContentTxt(familyDiseaseInfo);
					}
				}
				// 月经史
				String menstruationdiseaseInfo = disease.getMenstruationdiseaseInfo();
				
				if(StringUtils.isNotEmpty(menstruationdiseaseInfo))
				{	
					if("月经生育史".equals(typeName))
					{	
						page.setContentTxt(menstruationdiseaseInfo);
					}
				}
				// 就诊情况
				String seeDoctorMsg = disease.getSeeDoctorMsg();
				
				if(StringUtils.isNotEmpty(seeDoctorMsg))
				{	
					if("诊治情况".equals(typeName))
					{	
						page.setContentTxt(seeDoctorMsg);
					}
				}
				// 就诊情况
				Long visitTime = disease.getVisitTime();

				if (null != visitTime) {
					if ("就诊时间".equals(typeName)) {
						page.setContentTxt(String.valueOf(visitTime));
					}
				}
			
			}
		}
	}*/
	
	
	
	private void clearContentData(String illCaseInfoId) {
		IllCaseInfo info = electronicIllCaseDao.getIllCase(illCaseInfoId);
		if(info != null && !info.isSaved()){
			//清空该电子病历中所有填写的各种病史以及各种就医资料内容
			electronicIllCaseDao.clearContentData(illCaseInfoId);
		}
	}

	private Object setItemPageVo(List<IllCaseTypeContent> illCaseTypeContentList, IllCaseType illCaseType,int categoryId) {
		if(categoryId == 1){
			IllCaseTypeContentPageVo item = null;
			if(illCaseTypeContentList != null){
				for(IllCaseTypeContent ictc : illCaseTypeContentList){
					if(ictc.getIllCaseTypeId().equals(illCaseType.getId())){
						item = new IllCaseTypeContentPageVo();
						item.setTypeName(illCaseType.getTypeName());
						item.setIllCaseTypeId(illCaseType.getId());
						item.setContentTxt(ictc.getContentTxt());
						item.setContentImages(ictc.getContentImages());
						item.setIllCaseInfoId(ictc.getIllCaseInfoId());
						item.setId(ictc.getId());
						return item;
					}
				}
			}
			return item;
		}else if(categoryId == 2){
			List<SeekIllInfoPageVo> list = new ArrayList<SeekIllInfoPageVo>();
			if(illCaseTypeContentList != null){
				for (IllCaseTypeContent ictc : illCaseTypeContentList) {
					if(ictc.getIllCaseTypeId().equals(illCaseType.getId())){
						SeekIllInfoPageVo item = new SeekIllInfoPageVo();
						item.setType(IllCaseEnum.SeekIllType.manualInput.getIndex());
						item.setCreateTime(ictc.getCreateTime());
						IllCaseTypeContentPageVo pageVo = new IllCaseTypeContentPageVo();
						pageVo.setTypeName(illCaseType.getTypeName());
						pageVo.setIllCaseTypeId(illCaseType.getId());
						pageVo.setContentTxt(ictc.getContentTxt());
						pageVo.setContentImages(ictc.getContentImages());
						pageVo.setIllCaseInfoId(ictc.getIllCaseInfoId());
						pageVo.setId(ictc.getId());
						item.setIllCaseTypeContentPageVo(pageVo);
						list.add(item);
					}
				}
			}
			return list;
		}
		return null;
	}

	@Override
	public IllCaseTypeContent saveIllCaseTypeContent(IllCaseTypeContent illCaseTypeContent) {
		String illCaseInfoId = illCaseTypeContent.getIllCaseInfoId();
		String illCaseTypeId = illCaseTypeContent.getIllCaseTypeId();
		if(StringUtil.isNullOrEmpty(illCaseInfoId) || StringUtil.isNullOrEmpty(illCaseTypeId)){
			throw new ServiceException("电子病历或病历类型id为空");
		}
		String mainCaseTypeId =  electronicIllCaseDao.getMustFillitemId();
		if(illCaseTypeId.equals("treateType")){
			//保存就诊类型
			if(StringUtil.isNotBlank(illCaseTypeContent.getContentTxt())){
				electronicIllCaseDao.updateIllCaseTreateType(illCaseInfoId,illCaseTypeContent.getContentTxt());
			}
			return null;
		}else{
			IllCaseType type = electronicIllCaseDao.getIllCaseTypeById(illCaseTypeId);
			IllCaseTypeContent content = electronicIllCaseDao.saveIllCaseTypeContent(illCaseTypeContent);
			electronicIllCaseDao.updateillCaseUpdateTime(illCaseInfoId);
			if(illCaseTypeId.equals(mainCaseTypeId)){
				electronicIllCaseDao.updateIllCaseMainCase(illCaseInfoId,illCaseTypeContent.getContentTxt(),illCaseTypeContent.getContentImages());
			}
			if(type != null && "主诉".equals(org.apache.commons.lang3.StringUtils.trim(type.getTypeName())) || 
					type != null && "诊治情况".equals(org.apache.commons.lang3.StringUtils.trim(type.getTypeName()))){
				List<Integer> diseaseIds = orderMapper.findDiseaseIdByIllCaseInfoId(illCaseInfoId);
				if(!Util.isNullOrEmpty(diseaseIds)){
					Map<String,Object> params = new HashMap<>();
					params.put("diseaseIds", diseaseIds);
					if(type != null && "主诉".equals(org.apache.commons.lang3.StringUtils.trim(type.getTypeName()))){
						/**
						 * 如果当前电子病历关联了  病情资料 就同步主诉到 病情资料
						 */
						List<String> imageUrls = 
								illCaseTypeContent.getContentImages() == null ? new ArrayList<String>() :illCaseTypeContent.getContentImages();
						params.put("diseaseInfo", illCaseTypeContent.getContentTxt());
						params.put("imageUrls", imageUrls);
					}
					if(type != null && "诊治情况".equals(org.apache.commons.lang3.StringUtils.trim(type.getTypeName()))){
						/**
						 * 如果当前电子病历关联了  病情 就同步诊治情况到 病情的 诊治情况
						 */
						params.put("seeDoctorMsg", illCaseTypeContent.getContentTxt());
						if(org.apache.commons.lang3.StringUtils.isNotBlank(illCaseTypeContent.getContentTxt())){
							params.put("isSeeDoctor", true);
						}else{
							params.put("isSeeDoctor", false);
						}
					}
					if(params.size() > 1){
						diseaseService.updateRelationIllCase(params);
					}
				}
			}
			
			/**
			 * 转诊成电话订单的时候需要保证数据一致 
			 * 电话订单在没有生成基础订单之前
			 * 会将数据存储在mongodb中的t_consult_order数据表
			 * 此处在修改的时候保持对应电话订单数据一致
			 */
			updatePhoneOrderDisease(illCaseInfoId,type.getTypeName(),content.getContentTxt(),content.getContentImages());
			return content;
		}
	}

	private void updatePhoneOrderDisease(String illCaseInfoId, String typeName, String contentTxt,List<String> contentImages) {
		if(!"现病史, 既往史, 家族史 ,月经生育史,主诉,诊治情况".contains(typeName))
		{	
			return;
		}
		com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease dis = 
				new com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease();
		dis.setIllCaseInfoId(illCaseInfoId);
		if("主诉".equals(typeName)){
			dis.setDiseaseImgs(contentImages);
			dis.setDiseaseDesc(contentTxt);
		}else if("现病史".equals(typeName)){
			dis.setDiseaseInfo_now(contentTxt);
		}else if("既往史".equals(typeName)){
			dis.setDiseaseInfo_old(contentTxt);
		}else if("家族史".equals(typeName)){
			dis.setFamilydiseaseInfo(contentTxt);
		}else if("月经生育史".equals(typeName)){
			dis.setMenstruationdiseaseInfo(contentTxt);
		}else if("诊治情况".equals(typeName)){
			dis.setCureSituation(contentTxt);
		}
		guideDao.syncDiseaseFromIllCase(dis);
	}

	@Override
	public int batchSaveIllCaseTypeContent(String illCaseTypeContentString) {
		List<IllCaseTypeContent> list = null;
		try{
			list = JSONArray.parseArray(illCaseTypeContentString, IllCaseTypeContent.class);
		}catch(Exception e){
			throw new ServiceException("检查项列表序列化参数错误");
		}
		if(list != null && list.size() > 0){
			for (IllCaseTypeContent illCaseTypeContent : list) {
				saveIllCaseTypeContent(illCaseTypeContent);
			}
		}
		return 1;
	}

	public Object insertBaseData() {
		double i = 1.0;
		for (int t=0;t<SysConstants.IllCASE_TYPE_NAME_1.length;t++) {
			String name = SysConstants.IllCASE_TYPE_NAME_1[t];
			IllCaseType ict = new IllCaseType();
			ict.setCategoryId(1);
			ict.setDataType(1);
			ict.setTypeOrder(i++);
			ict.setTypeName(name);
			if(t == 0){
				ict.setRequired(true);
			}
			electronicIllCaseDao.insertBaseData(ict);
		}
		i = 1.0;
		for (String name : SysConstants.IllCASE_TYPE_NAME_2) {
			IllCaseType ict = new IllCaseType();
			ict.setCategoryId(2);
			ict.setDataType(1);
			ict.setTypeOrder(i++);
			ict.setTypeName(name);
			electronicIllCaseDao.insertBaseData(ict);
		}
		return 1;
	}

	@Override
	public List<IllCaseType> getSeekIllInit() {
		return electronicIllCaseDao.getContentType();
	}

	@Override
	public Object isFinished(Integer orderId) {
		Map<String,Object> rtnObj = new HashMap<>();
		Order order = orderServiceImpl.getOne(orderId);
		if(order == null){
			return rtnObj;
		}
		int patientId = order.getPatientId();
		int doctorId = order.getDoctorId();
		IllCaseInfo info = electronicIllCaseDao.getIllCaseBase(patientId, doctorId);
		boolean isFinished = false;
		if(info != null){
			String illcaseInfoId = info.getId();
			String illCaseTypeId = electronicIllCaseDao.getMustFillitemId();
			IllCaseTypeContent content = electronicIllCaseDao.getMainItemContentListByIllcaseId(illcaseInfoId, illCaseTypeId);
			if(content != null && StringUtil.isNotEmpty(content.getContentTxt())){
				isFinished = true;
			}
		}
		rtnObj.put("isFinished", isFinished);
		rtnObj.put("patientId", patientId);
		return rtnObj;
	}
	/***begin add  by  liwei  2016年2月19日********/
	/**
	 * 根据订单id去查询对应的患者的病情中的主诉 是否为空 来判断返回 true或者 false
	 */
	/*public Object isFinished(Integer orderId) {
		Map<String, Object> rtnObj = new HashMap<>();
		Order order = orderServiceImpl.getOne(orderId);
		if (order == null) {
			return rtnObj;
		}
		int patientId = order.getPatientId();
		int diseaseId = order.getDiseaseId();
		boolean isFinished = false;
		Disease disease = diseaseService.findByPk(diseaseId);
		if (null != disease) {
			// 主诉信息
			String diseaseInfo = disease.getDiseaseInfo();
			if (StringUtils.isNotEmpty(diseaseInfo)) {
				isFinished = true;
			} else {
				isFinished = false;
			}
		}
		rtnObj.put("isFinished", isFinished);
		rtnObj.put("patientId", patientId);
		return rtnObj;
	}*/
	/*** end add by liwei 2016年2月19日 ********/

	
	@Override
	public PageVO getPatientIllcaseList(Integer userId, Integer patientId,Integer pageIndex, Integer pageSize){
		PageVO vo = new PageVO();
		List<PatientIllCaseItemVo> list = new ArrayList<PatientIllCaseItemVo>();
		List<IllCaseInfo> illcaseInfos = electronicIllCaseDao.getIllCaseListByUserIdAndPatientId(userId,patientId);
		if(Util.isNullOrEmpty(illcaseInfos)){
			return vo;
		}
		List<String> illcaseInfoIds = new ArrayList<String>();
		String illCaseTypeId = electronicIllCaseDao.getMustFillitemId();
		Map<String,IllCaseInfo> map = new HashMap<String,IllCaseInfo>();
		for (IllCaseInfo illCaseInfo : illcaseInfos) {
			map.put(illCaseInfo.getId(), illCaseInfo);
			illcaseInfoIds.add(illCaseInfo.getId());
		}
		long count = electronicIllCaseDao.getMainItemContentListCountByIllcaseIds(illcaseInfoIds,illCaseTypeId);
		if(count < 1){
			return vo;
		}
		pageIndex = pageIndex == null ? vo.getPageIndex() : pageIndex;
		pageSize = pageSize == null ? vo.getPageSize() : pageIndex;
		List<IllCaseTypeContent> contentList = electronicIllCaseDao.getMainItemContentListByIllcaseIds(illcaseInfoIds,illCaseTypeId,pageIndex,pageSize);
		for (IllCaseTypeContent content : contentList) {
			IllCaseInfo illCaseInfo = map.get(content.getIllCaseInfoId());
			PatientIllCaseItemVo item = new PatientIllCaseItemVo();
			item.setDoctorId(illCaseInfo.getDoctorId());
			User user = userManager.getUser(illCaseInfo.getDoctorId());
			if(user != null){
				item.setDoctorName(user.getName());
			}
			item.setIllcaseInfoId(illCaseInfo.getId());
			item.setMainCondition(content.getContentTxt());
			item.setUpdateTime(content.getUpdateTime());
			list.add(item);
		}
		vo.setTotal(count);
		vo.setPageData(list);
		return vo;
	}

	@Override
	public IllCaseInfoPageVo getIllCaseByOrderId(Integer orderId) throws HttpApiException {
		
		Order order = orderServiceImpl.getOne(orderId);
		if(order == null){
			throw new ServiceException("该订单不存在");
		}
		//setCommOrder(order);
		IllCaseInfo info =null;
		if(order.getIllCaseInfoId()!=null){
			info=electronicIllCaseDao.getIllCase(order.getIllCaseInfoId());
		}
		if(info == null){
			throw new ServiceException("该订单没有关联电子病历");
		}
		IllCaseInfoPageVo pageVo = getIllCaseInfo(info);
		pageVo.setOrderNo(order.getOrderNo()+"");
		pageVo.setOrderStatus(order.getOrderStatus());
		//根据订单查询病历详情时，加入关怀小结数据
		List<CareSummary> careSummaryList=careSummaryservice.findByOrderId(orderId);
		pageVo.setCareSummaryList(careSummaryList);
		return pageVo;
	}
   /**
	 * 批量构建电子病历数据
	 * @param illCaseTypeContent
	 * @param disease
	 * @param illCaseInfoId
	 */
	private void buildIllCaseTypeContent(
			List<IllCaseTypeContent> illCaseTypeContent, Disease disease,String  illCaseInfoId) {
		// 获取所有基础病历类型
		List<IllCaseType> illCaseTypeList = electronicIllCaseDao
				.getInitIllCaseTypeByCategoryId(0);
		for (IllCaseType illCaseType : illCaseTypeList) {
			//"",   现病史  既往史  家族史  月经生育史  就诊时间   首诊  seeDoctorMsg
			String  typeName = illCaseType.getTypeName();
			String illCaseTypeId = illCaseType.getId();
			
			if(!"现病史, 既往史, 家族史 ,月经生育史,就诊时间,主诉,诊治情况".contains(typeName))
			{	
				continue;
			}
			
			String diseaseInfo = disease.getDiseaseInfo();
			if(StringUtils.isNotEmpty(diseaseInfo))
			{	
				if("主诉".equals(typeName))
				{	
					
					IllCaseTypeContent content = new IllCaseTypeContent();
					buildIllCaseTypeContent(content, diseaseInfo,illCaseInfoId,illCaseTypeId);
					content.setContentImages(disease.getDiseaseImgs());
					illCaseTypeContent.add(content);
				}
			}
			// 现病史
			String diseaseInfoNow = disease.getDiseaseInfoNow();
			
			if(StringUtils.isNotEmpty(diseaseInfoNow))
			{	
				if("现病史".equals(typeName))
				{	
					IllCaseTypeContent content = new IllCaseTypeContent();
					buildIllCaseTypeContent(content, diseaseInfoNow,illCaseInfoId,illCaseTypeId);
					illCaseTypeContent.add(content);
				}
			}
			
			
			// 既往史
			String diseaseInfoOld = disease.getDiseaseInfoOld();
			if(StringUtils.isNotEmpty(diseaseInfoOld))
			{	
				if("既往史".equals(typeName))
				{	
					IllCaseTypeContent content = new IllCaseTypeContent();
					buildIllCaseTypeContent(content, diseaseInfoOld,illCaseInfoId,illCaseTypeId);
					illCaseTypeContent.add(content);
				}
			}
			// 家族史
			String familyDiseaseInfo = disease.getFamilyDiseaseInfo();
			if(StringUtils.isNotEmpty(familyDiseaseInfo))
			{	
				if("家族史".equals(typeName))
				{	
					IllCaseTypeContent content = new IllCaseTypeContent();
					buildIllCaseTypeContent(content, familyDiseaseInfo,illCaseInfoId,illCaseTypeId);
					illCaseTypeContent.add(content);
				}
			}
			// 月经史
			String menstruationdiseaseInfo = disease.getMenstruationdiseaseInfo();
			
			if(StringUtils.isNotEmpty(menstruationdiseaseInfo))
			{	
				if("月经生育史".equals(typeName))
				{	
					IllCaseTypeContent content = new IllCaseTypeContent();
					buildIllCaseTypeContent(content, menstruationdiseaseInfo,illCaseInfoId,illCaseTypeId);
					illCaseTypeContent.add(content);
				}
			}
			// 就诊情况
			String seeDoctorMsg = disease.getSeeDoctorMsg();
			
			if(StringUtils.isNotEmpty(seeDoctorMsg))
			{	
				if("诊治情况".equals(typeName))
				{	
					IllCaseTypeContent content = new IllCaseTypeContent();
					buildIllCaseTypeContent(content, seeDoctorMsg,illCaseInfoId,illCaseTypeId);
					illCaseTypeContent.add(content);
				}
			}
			// 就诊情况
			Long visitTime = disease.getVisitTime();

			if (null != visitTime) {
				if ("就诊时间".equals(typeName)) {
					IllCaseTypeContent content = new IllCaseTypeContent();
					buildIllCaseTypeContent(content, visitTime.toString(),
							illCaseInfoId, illCaseTypeId);
					illCaseTypeContent.add(content);
				}
			}
		}
	}
	
	private  void  buildIllCaseTypeContent(IllCaseTypeContent content,String  object,String illCaseInfoId,String illCaseTypeId)
	{
		content.setContentTxt(object);
		content.setCreateTime(new Date().getTime());
		content.setIllCaseInfoId(illCaseInfoId);
		content.setIllCaseTypeId(illCaseTypeId);
		content.setUpdateTime(new Date().getTime());
	}
	
	
    /**
     * 医生结束服务  给患者生成电子病历
     */
	//@Override
	public void createElctriExperience(Order order) throws HttpApiException {
		Integer doctorId = order.getDoctorId();
		Integer userId = order.getUserId();
		Integer patientId = order.getPatientId();
		Integer diseaseId = order.getDiseaseId();
		Disease disease = diseaseService.findByPk(diseaseId);
		if (null == disease) {
			return;
		}
		IllCaseInfo info = electronicIllCaseDao.getIllCaseBase(patientId,
				doctorId);
		if (null != info&&!info.isSaved()) {
			// 主诉不存在就清理下数据
			this.clearContentData(info.getId());
		}
		IllCaseInfo illCaseInfo = null;
		String  caseId =null;
		if (null == info) {
			// 创建电子病历，并且插入患者信息
		    illCaseInfo = new IllCaseInfo();
			illCaseInfo.setPatientId(patientId);
			illCaseInfo.setUserId(userId);
			illCaseInfo.setDoctorId(doctorId);
			illCaseInfo.setSaved(true);
			IllCaseInfo caseInfo = this.createIllCaseInfo(illCaseInfo);
			caseId =caseInfo.getId();
		}
		else
		{	
			caseId =info.getId();
		}
		

		List<IllCaseTypeContent> conentList = new ArrayList<IllCaseTypeContent>();
		// 构建数据
		buildIllCaseTypeContent(conentList, disease, caseId);
		this.batchSaveIllCaseTypeContent(JSON.toJSONString(conentList));
	}

	@Override
	public Object updateIllCasetoSaved(String illcaseInfoId) {
		electronicIllCaseDao.updateIllCasetoSaved(illcaseInfoId);
		return 1;
	}

	@Override
	public Map<String,Object> createAndGet(Integer patientId, Integer userId, Integer doctorId,Integer orderId,Integer treateType) {
		IllCaseInfo info = new IllCaseInfo();
		info.setPatientId(patientId);
		info.setUserId(userId);
		info.setDoctorId(doctorId);
		info.setTreateType(treateType);
		info.setOrderId(orderId);
		String illcaseInfoId = this.createIllCaseInfo(info).getId();
		List<IllCaseType> caseTypeList = electronicIllCaseDao.getInitIllCaseTypeByCategoryId(1);
		List<IllCaseTypeContentPageVo> dataList = new ArrayList<IllCaseTypeContentPageVo>();
		/*IllCaseTypeContentPageVo teateType = new IllCaseTypeContentPageVo();
		teateType.setIllCaseInfoId(illcaseInfoId);
		teateType.setIllCaseTypeId("treateType");
		teateType.setTypeName("就诊类型");
		teateType.setContentTxt(treateType+"");
		dataList.add(teateType);*/
		for (IllCaseType illCaseType : caseTypeList) {
			IllCaseTypeContentPageVo vo = new IllCaseTypeContentPageVo();
			vo.setIllCaseInfoId(illcaseInfoId);
			vo.setIllCaseTypeId(illCaseType.getId());
			vo.setTypeName(illCaseType.getTypeName());
			dataList.add(vo);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("illcaseInfoId", illcaseInfoId);
		map.put("dataList", dataList);
		IllCasePatientInfo patient = electronicIllCaseDao.getIllCasePatient(illcaseInfoId);
		map.put("patient", patient);
		return map;
	}
	
	private String getAgeStr(Long birthday){
		if(birthday!=null) {
    		int ages=DateUtil.calcAge(birthday);
    		if (ages == 0 || ages == -1) {
    			return DateUtil.calcMonth(birthday)<=0?"1个月":DateUtil.calcMonth(birthday)+"个月";
    		}
    		return ages + "岁";
    	}else {
    		return null;
    	}
	}

	@Override
	public PatientIllRecordListVo getIllRecordList(Integer userId, Integer doctorId) {
		PatientIllRecordListVo dataVo = new PatientIllRecordListVo();
		User user = userManager.getUser(userId);
		if(user != null){
			dataVo.setUserId(userId);
			dataVo.setUserName(user.getName());
			dataVo.setAgeStr(getAgeStr(user.getBirthday()));
			dataVo.setArea(user.getArea());
			dataVo.setHeadPicFilleName(user.getHeadPicFileName());
			if(user.getSex() != null){
				dataVo.setSex(Short.valueOf(user.getSex()+""));
			}
			if(doctorId != null){
				List<String> tagNames = relationService.getDoctorPatientSelfTag(doctorId,userId);
				dataVo.setTags(tagNames);
			}
		}
		List<PatientIllRecordItemVo> patientIllRecordList = new ArrayList<PatientIllRecordItemVo>();
		List<Patient> patients = patientServiceImpl.findByCreateUser(userId);
		if(!Util.isNullOrEmpty(patients)){
			for (Patient patient : patients) {
				PatientIllRecordItemVo vo = new PatientIllRecordItemVo();
				vo.setAgeStr(patient.getAgeStr());
				vo.setPatientId(patient.getId());
				vo.setPatientName(patient.getUserName());
				vo.setSex(patient.getSex());
				List<IllRecordItem> illRecordList = getIllRecordListByPatient(patient.getId(),userId,doctorId);
				vo.setIllRecordList(illRecordList);
				patientIllRecordList.add(vo);
			}
		}
		dataVo.setPatientIllRecordList(patientIllRecordList);
		return dataVo;
	}

	private List<IllRecordItem> getIllRecordListByPatient(Integer patientId, Integer userId, Integer doctorId) {
		List<IllRecordItem> list = new ArrayList<IllRecordItem>();
		List<IllCaseInfo> illCaseInfoList = electronicIllCaseDao.findIllCase(patientId,userId,doctorId);
		for (IllCaseInfo info : illCaseInfoList) {
			String mainCase = info.getMainCase();
			Integer treateType = info.getTreateType();
			if(StringUtil.isNotBlank(mainCase) && treateType != null){
				IllRecordItem item = new IllRecordItem();
				item.setIllCaseInfoId(info.getId());
				item.setImageUrls(info.getImageUlrs());
				item.setMainCase(info.getMainCase());
				item.setTreatType(info.getTreateType());
				item.setUpdateTime(info.getUpdateTime());
				User user = userManager.getUser(info.getDoctorId());
				if(user != null){
					item.setDoctorName(user.getName());
				}
				list.add(item);
			}else{
				this.clearIllCaseAllById(info.getId());
			}
		}
		return list;
	}

	@Override
	public void clearIllCaseAllById(String illCaseInfoId) {
		//清除所有内容项
		electronicIllCaseDao.clearContentData(illCaseInfoId);
		//清除电子病历的患者数据
		electronicIllCaseDao.clearIllPatientData(illCaseInfoId);
		//清除电子病历数据
		electronicIllCaseDao.clearIllCaseInfo(illCaseInfoId);
		//清空订单中的 illcaseinfo 字段
		Map<String,String> sqlParam = new HashMap<String,String>();
		sqlParam.put("illCaseInfoId", illCaseInfoId);
		orderMapper.clearOrderIllCaseInfo(sqlParam);
	}

	@Override
	public Map<String,Object> getIllcaseBaseContentById(String illcaseInfoId) {
		//IllCaseInfo info = electronicIllCaseDao.getIllCase(illcaseInfoId);
		List<IllCaseType> caseTypeList = electronicIllCaseDao.getInitIllCaseTypeByCategoryId(1);
		List<IllCaseTypeContentPageVo> dataList = new ArrayList<IllCaseTypeContentPageVo>();
/*		IllCaseTypeContentPageVo teateType = new IllCaseTypeContentPageVo();
		teateType.setIllCaseInfoId(illcaseInfoId);
		teateType.setIllCaseTypeId("treateType");
		teateType.setTypeName("就诊类型");
		teateType.setContentTxt(info.getTreateType()+"");
		dataList.add(teateType);*/
		for (IllCaseType illCaseType : caseTypeList) {
			IllCaseTypeContentPageVo vo = new IllCaseTypeContentPageVo();
			vo.setIllCaseInfoId(illcaseInfoId);
			vo.setIllCaseTypeId(illCaseType.getId());
			vo.setTypeName(illCaseType.getTypeName());
			IllCaseTypeContent content = electronicIllCaseDao.getCaseContent(illcaseInfoId,illCaseType.getId());
			if(content != null){
				List<String> paths = content.getContentImages();
				List<String> urls = null;
				if(paths != null && paths.size() > 0){
					urls = new ArrayList<String>();
					for (String path : paths) {
						urls.add(PropertiesUtil.addUrlPrefix(path));
					}
				}
				vo.setContentImages(urls);
				vo.setContentTxt(content.getContentTxt());
			}
			dataList.add(vo);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("illcaseInfoId", illcaseInfoId);
		map.put("dataList", dataList);
		IllCasePatientInfo patient = electronicIllCaseDao.getIllCasePatient(illcaseInfoId);
		map.put("patient", patient);
		return map;
	}

	@Override
	public Object createOrGetByOrderId(Integer orderId) {
		Order o = orderMapper.getOne(orderId);
		if(o == null || o.getOrderType() != OrderEnum.OrderType.care.getIndex()){
			throw new ServiceException("找不到对应的健康关怀订单");
		}
		String infoId = o.getIllCaseInfoId();
		if(StringUtil.isEmpty(infoId)){
			Map<String,Object> map = 
					createAndGet(o.getPatientId(), o.getUserId(), o.getDoctorId(),orderId, ConsultationEnum.IllCaseTreatType.care.getIndex());
			String illCaseInfoId = String.valueOf(map.get("illcaseInfoId"));
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("orderId", orderId);
			params.put("illCaseInfoId", illCaseInfoId);
			orderMapper.updateOrderIllCaseInfoId(params);
			map.remove("illCaseInfoId");
			return map;
		}else{
			return getIllcaseBaseContentById(infoId);
		}
		
	}

	@Override
	public void setDiseaseInfo(String illCaseInfoId,com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease d) {
		List<IllCaseType> typeList = electronicIllCaseDao.getInitIllCaseTypeByCategoryId(1);
		for (IllCaseType type : typeList) {
			String  typeName = type.getTypeName();
			String illCaseTypeId = type.getId();
			if(!"现病史, 既往史, 家族史 ,月经生育史,主诉,诊治情况".contains(typeName))
			{	
				continue;
			}
			IllCaseTypeContent c = electronicIllCaseDao.getContentByInfoIdAndTypeId(illCaseInfoId,illCaseTypeId);
			if(c == null){
				continue;
			}
			if("主诉".equals(typeName)){
				d.setDiseaseDesc(c.getContentTxt());
				d.setDiseaseImgs(c.getContentImages());
			}else if("现病史".equals(typeName)){
				d.setDiseaseInfo_now(c.getContentTxt());
			}else if("既往史".equals(typeName)){
				d.setDiseaseInfo_old(c.getContentTxt());
			}else if("家族史".equals(typeName)){
				d.setFamilydiseaseInfo(c.getContentTxt());
			}else if("月经生育史".equals(typeName)){
				d.setMenstruationdiseaseInfo(c.getContentTxt());
			}else if("诊治情况".equals(typeName)){
				d.setIsSeeDoctor(true);
				d.setSeeDoctorMsg(c.getContentTxt());
			}
		}
		IllCasePatientInfo pinfo = electronicIllCaseDao.getIllCasePatient(illCaseInfoId);
		d.setTelephone(pinfo.getTelephone());
	}

	@Override
	public Object getIllcase4CreateOrder(String illcaseInfoId) {
		Map<String,Object> map = new HashMap<>();
		IllCaseType type = electronicIllCaseDao.getInitIllCaseTypeByName("诊治情况");
		if(Objects.nonNull(type)){
			IllCaseTypeContent c = electronicIllCaseDao.getContentByInfoIdAndTypeId(illcaseInfoId,type.getId());
			if(Objects.nonNull(c)){
				map.put("seeDoctorMsg", c.getContentTxt());
				map.put("isSeeDoctor", true);
			}else{
				map.put("isSeeDoctor", false);
			}
		}
		IllCaseInfo info = electronicIllCaseDao.getIllCase(illcaseInfoId);
		if(Objects.nonNull(info)){
			map.put("mainCase", info.getMainCase());
			map.put("imageUlrs", info.getImageUlrs());
		}
		
		IllCasePatientInfo pinfo = electronicIllCaseDao.getIllCasePatient(illcaseInfoId);
		if(Objects.nonNull(pinfo)){
			map.put("patientId", pinfo.getPatientId());
			map.put("patientName", pinfo.getPatientName());
			map.put("telephone", pinfo.getTelephone());
		}
		return map;
	}

	@Override
	public Object getIllRecords(Integer userId, Integer doctorId) {
		
		if (userId == null) {
			userId = ReqUtil.instance.getUserId();
		}
		
		logger.info("userId: " + userId + " and doctorId:" + doctorId + " getIllRecords");
		
		PatientIllRecordListVo dataVo = new PatientIllRecordListVo();
		User user = userManager.getUser(userId);
		if(user != null){
			dataVo.setUserId(userId);
			dataVo.setUserName(user.getName());
			dataVo.setAgeStr(getAgeStr(user.getBirthday()));
			dataVo.setArea(user.getArea());
			dataVo.setHeadPicFilleName(user.getHeadPicFileName());
			if(user.getSex() != null){
				dataVo.setSex(Short.valueOf(user.getSex()+""));
			}
/*			if(doctorId != null){
				List<String> tagNames = relationService.getDoctorPatientSelfTag(doctorId,userId);
				dataVo.setTags(tagNames);
			}*/
		}
		List<PatientIllRecordItemVo> patientIllRecordList = new ArrayList<PatientIllRecordItemVo>();
		List<Patient> patients = patientServiceImpl.findByCreateUser(userId);
		if(!Util.isNullOrEmpty(patients)){
			for (Patient patient : patients) {
				PatientIllRecordItemVo vo = new PatientIllRecordItemVo();
				vo.setAgeStr(patient.getAgeStr());
				vo.setPatientId(patient.getId());
				vo.setPatientName(patient.getUserName());
				vo.setSex(patient.getSex());
				List<IllHistoryInfoItem> illHistoryInfoItems = getIllRecordsByPatient(patient.getId(), doctorId);
				vo.setIllHistoryInfoItems(illHistoryInfoItems);
				patientIllRecordList.add(vo);
			}
		}
		dataVo.setPatientIllRecordList(patientIllRecordList);
		return dataVo;
	}

	@Override
	public List<IllHistoryInfoItem> getIllRecordsByPatientId(Integer userId, Integer patientId, Integer doctorId) {
		//获取患者的电子病历
		List<IllHistoryInfoItem> illHistoryInfoItems = getIllRecordsByPatient(patientId, doctorId);
		return illHistoryInfoItems;
	}

	@Override
	public List<IllHistoryInfoItem> getIllRecordsByDiseaseId(Integer patientId, String diseaseId) {
		if(null==patientId){
			throw new ServiceException("患者id不能为空");
		}
		if(StringUtil.isEmpty(diseaseId)){
			throw new ServiceException("疾病id不能为空");
		}

		List<IllHistoryInfoItem> list = new ArrayList<IllHistoryInfoItem>();
		//根据病历和患者找到对应的病历，判断时候有重复病历
		List<IllHistoryInfo> infos = illHistoryInfoDao.getInfosByDoctorIdAndPatientId(null,patientId);
		for(IllHistoryInfo info : infos) {
			IllHistoryInfoItem item = new IllHistoryInfoItem();
			item.setInfoId(info.getId());

			if (null != info.getIllContentInfo()) {
				item.setIllDesc(info.getIllContentInfo().getIllDesc());
			}
			
			User user = userManager.getUser(info.getDoctorId());
			item.setDoctorId(info.getDoctorId());
			if(user != null){
				item.setDoctorName(user.getName());
			}

			item.setUpdateTime(info.getUpdateTime());
			item.setCreateTime(info.getCreateTime());

			List<Diagnosis> diagnosiss = info.getDiagnosis();
			if (!CollectionUtils.isEmpty(diagnosiss)) {
				//排序，取最新的一条初步诊断
				Collections.sort(diagnosiss, new Comparator<Diagnosis>() {

					@Override
					public int compare(Diagnosis o1, Diagnosis o2) {
						return o2.getCreateTime().compareTo(o1.getCreateTime());
					}
				});

				Diagnosis diagnosis = diagnosiss.get(0);
				//匹配最新的一条初步诊断
				if(diagnosis.getDiseaseId()==null||!diagnosis.getDiseaseId().equals(diseaseId)){
					continue;
				}
				if (StringUtil.isNotEmpty(diagnosis.getDiseaseName())) {
					item.setDiseaseName(diagnosis.getDiseaseName());
				}else if (StringUtil.isNotEmpty(diagnosis.getContent())) {
					item.setDiseaseName(diagnosis.getContent());
				}
			}else{
				continue;
			}

			list.add(item);
		}

		return list;
	}

	private List<IllHistoryInfoItem> getIllRecordsByPatient(Integer patientId, Integer doctorId) {
		List<IllHistoryInfoItem> list = new ArrayList<IllHistoryInfoItem>();
		List<IllHistoryInfo> infos = illHistoryInfoDao.getInfosByDoctorIdAndPatientId(doctorId, patientId);
		for(IllHistoryInfo info : infos) {	
			IllHistoryInfoItem item = new IllHistoryInfoItem();
			item.setInfoId(info.getId());

			if (null != info.getIllContentInfo()) {
				item.setIllDesc(info.getIllContentInfo().getIllDesc());
			}
			if (Objects.isNull(info.getDoctorId())) {
				illHistoryInfoDao.removeNullDoctorId(info.getId());
				continue;
			}
			User user = userManager.getUser(info.getDoctorId());
			item.setDoctorId(info.getDoctorId());
			if(user != null){
				item.setDoctorName(user.getName());
			}
			
			item.setUpdateTime(info.getUpdateTime());
			item.setCreateTime(info.getCreateTime());
			
			List<Diagnosis> diagnosiss = info.getDiagnosis();
			if (!CollectionUtils.isEmpty(diagnosiss)) {
				//排序，取最新的一条初步诊断
				Collections.sort(diagnosiss, new Comparator<Diagnosis>() {

					@Override
					public int compare(Diagnosis o1, Diagnosis o2) {
						if(o1 == null || o2 == null || o1.getCreateTime() == null || o2.getCreateTime() == null)
							return 0;
						return o2.getCreateTime().compareTo(o1.getCreateTime());
					}
				});
				
				Diagnosis diagnosis = diagnosiss.get(0);
				if (StringUtil.isNotEmpty(diagnosis.getDiseaseName())) {
					item.setDiseaseName(diagnosis.getDiseaseName());
				}else if (StringUtil.isNotEmpty(diagnosis.getContent())) {
					item.setDiseaseName(diagnosis.getContent());
				}else{
					item.setDiseaseName("");
				}
			}
			
			list.add(item);
		}
		
		return list;
	}
}

