package com.dachen.health.pack.patient.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.ImageDataEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderNoitfyType;
import com.dachen.health.commons.utils.PackUtil;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.mapper.DiseaseMapper;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.Disease;
import com.dachen.health.pack.patient.model.DiseaseExample;
import com.dachen.health.pack.patient.model.DiseaseParam;
import com.dachen.health.pack.patient.model.ImageData;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IDiseaseService;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.util.DateUtil;
import com.dachen.util.ReqUtil;

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
public class DiseaseServiceImpl extends BaseServiceImpl<Disease, Integer> implements IDiseaseService {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	DiseaseMapper mapper;
	
	@Resource 
	PatientMapper patientMapper;
	
	@Autowired
	IImageDataService imageDataService;
	
	@Autowired
	IOrderService orderService;
	
	@Autowired
    private IOrderSessionService orderSessionService;

	@Autowired
	IBusinessServiceMsg businessServiceMsg;
	
	@Override
	public int save(Disease intance) {
		int ret=0;
		try {
			 ret= mapper.insert(intance);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		return ret;
	}
	
	
	@Override
	public int update(Disease intance) {
		return mapper.updateByPrimaryKeySelective(intance);

	}

	@Override
	public int deleteByPK(Integer pk) {
		return mapper.deleteByPrimaryKey(pk);

	}

	@Override
	public Disease findByPk(Integer pk) {
		return mapper.selectByPrimaryKey(pk);
	}

	@Override
	public List<Disease> findByPatient(int patientId) {
		DiseaseExample example=new DiseaseExample();
		example.createCriteria().andPatientIdEqualTo(patientId);
		return mapper.selectByExampleWithBLOBs(example);
	}

	@Override
	public List<Disease> findByCreateUser(int createUserId) {
		DiseaseExample example=new DiseaseExample();
		example.createCriteria().andCreateUserIdEqualTo(createUserId);
		return mapper.selectByExampleWithBLOBs(example);
	}
	
	public void updateDisease(Disease intance,Integer userId) {
		Integer id = intance.getId();
		Disease disease = this.findByPk(id);
		if(disease == null) {
			throw new ServiceException("找不到病情记录!");
		}
		if (intance.getPatientId() != null) {
			Patient patient = patientMapper.selectByPrimaryKey(intance.getPatientId());
			intance.setAge(DateUtil.calcAge(patient.getBirthday()));
			intance.setUserName(patient.getUserName());
			intance.setBirthday(patient.getBirthday());
			intance.setSex(patient.getSex()==null?null:(int)patient.getSex());
			intance.setArea(patient.getArea());
			intance.setRelation(patient.getRelation());
		}
		if(intance.getDiseaseImgs()!=null) {
			String[]diseaseImgs = new String[intance.getDiseaseImgs().size()];
			intance.getDiseaseImgs().toArray(diseaseImgs);
			saveDiseaseImage(id,userId,diseaseImgs,false);
		}
		update(intance);
		
		Order order=orderService.findOrderBydiseaseId(id);
		order.setPatientId(intance.getPatientId());
		OrderSession orderSession=orderSessionService.findOneByOrderId(order.getId());
		orderService.updateOrder(order);
		orderService.sendOrderNoitfy(order.getUserId().toString(), order.getDoctorId().toString()
                , orderSession.getMsgGroupId(), OrderNoitfyType.editDisesase, null);
	}
	
	public void updateBaseDisease(Disease intance,Integer userId) {
		Integer id = intance.getId();
		Disease disease = this.findByPk(id);
		if(disease == null) {
			throw new ServiceException("找不到病情记录!");
		}
		if (intance.getPatientId() != null) {
			Patient patient = patientMapper.selectByPrimaryKey(intance.getPatientId());
			intance.setAge(DateUtil.calcAge(patient.getBirthday()));
			intance.setUserName(patient.getUserName());
			intance.setBirthday(patient.getBirthday());
			intance.setSex(patient.getSex()==null?null:(int)patient.getSex());
			intance.setArea(patient.getArea());
			intance.setRelation(patient.getRelation());
		}
		if(intance.getDiseaseImgs()!=null) {
			String[]diseaseImgs = new String[intance.getDiseaseImgs().size()];
			intance.getDiseaseImgs().toArray(diseaseImgs);
			saveDiseaseImage(id,userId,diseaseImgs,false);
		}
		update(intance);
	}
	

	public Integer addDisease(DiseaseParam param) throws ServiceException {
		
		Integer patientId=param.getPatientId();
		if(patientId==null){
			throw new ServiceException(10001, "患者ID为空");
		}else{
			Patient patient= patientMapper.selectByPrimaryKey(patientId);
			if(patient==null){
				throw new ServiceException(10002, "错误的患者ID ["+patientId+"]");
			}
		}
		
		Disease intance = new Disease();
		intance.setCreatedTime(System.currentTimeMillis());
		intance.setCreateUserId(ReqUtil.instance.getUserId());
		intance.setDiseaseInfo(param.getDiseaseDesc());
		intance.setTelephone(param.getTelephone());
		intance.setPatientId(patientId);
		intance.setAge(param.getAge());
		intance.setArea(param.getArea());
		intance.setBirthday(param.getBirthday());
		intance.setRelation(param.getRelation());
		intance.setSex(param.getSex());
		intance.setUserName(param.getUserName());
		/***begin add  by  liwei  2016年1月21日********/
		intance.setDiseaseInfoNow(param.getDiseaseInfo_now());
		intance.setDiseaseInfoOld(param.getDiseaseInfo_old());
		intance.setDiseaseInfoNow(param.getDiseaseInfo_now());
		intance.setFamilyDiseaseInfo(param.getFamilydiseaseInfo());
		intance.setMenstruationdiseaseInfo(param.getMenstruationdiseaseInfo());
		intance.setSeeDoctorMsg(param.getSeeDoctorMsg());
		intance.setIsSeeDoctor(param.getIsSeeDoctor());
		intance.setVisitTime(param.getVisitTime());
		/***end add  by  liwei  2016年1月21日********/
		//非电话订单 保存信息到mysql中
		save(intance);
		Integer disId = intance.getId();
		if(param.getImagePaths()!=null) {
			saveDiseaseImage(disId,param.getUserId(),param.getImagePaths(),true);
		}
		if (param.getVoice() != null) {
			saveVoice(disId, param.getUserId(), param.getVoice());
		}
		return disId;
	}
	
	public void updateDisease(DiseaseParam intance, Integer id) throws ServiceException {
		Disease disease = this.findByPk(id);
		if (disease == null) {
			throw new ServiceException("找不到病情记录!");
		}

		if (intance.getImagePaths() != null) {
			saveDiseaseImage(id, disease.getCreateUserId(), intance.getImagePaths(), false);
		}
		if (intance.getDiseaseDesc() != null) {
			disease.setDiseaseInfo(intance.getDiseaseDesc());
		}
		update(disease);

		Order order = orderService.findOrderBydiseaseId(id);
		order.setPatientId(disease.getPatientId());

	}
	
	private void saveDiseaseImage(Integer diseaseId, Integer createUserId, String[] imagePaths, boolean isNew) {
		if (imagePaths != null && imagePaths.length > 0) {
			if (!isNew) {
				imageDataService.deleteImgData(ImageDataEnum.dessImage.getIndex(), diseaseId);
			}
			for (String imgPath : imagePaths) {
				ImageData imageData = new ImageData();
				
				/***begin add  by  liwei  2016年1月25日********/
				//上传图片的时候 不再去除前缀
//				if (StringUtil.isNotBlank(imgPath) && imgPath.indexOf(PropertiesUtil.getHeaderPrefix()) > -1) {
//					imgPath = imgPath.replace(PropertiesUtil.getHeaderPrefix(), "");
//				} else {
//					continue;
//				}
				/***end add  by  liwei  2016年1月25日********/


				imageData.setImageUrl(imgPath);
				imageData.setRelationId(diseaseId);
				imageData.setUserId(createUserId);
				imageData.setImageType(ImageDataEnum.dessImage.getIndex());
				imageDataService.add(imageData);
			}
		}
	}
	
	
	private void saveOrDeleteDiseaseImage(Integer diseaseId, Integer createUserId, String[] imagePaths, boolean isNew) {
		if (imagePaths != null) {
			if (isNew) {
				imageDataService.deleteImgData(ImageDataEnum.dessImage.getIndex(), diseaseId);
			}
			
			if(imagePaths.length > 0){
				for (String imgPath : imagePaths) {
					ImageData imageData = new ImageData();
					imageData.setImageUrl(imgPath);
					imageData.setRelationId(diseaseId);
					imageData.setUserId(createUserId);
					imageData.setImageType(ImageDataEnum.dessImage.getIndex());
					imageDataService.add(imageData);
				}
			}
			
		}
	}
	
	
	private void saveVoice(Integer diseaseId, Integer userId, String voicePath) {
		if (voicePath == null)
			return;
		
		ImageData imageData = PackUtil.newImageData(diseaseId, userId, voicePath, ImageDataEnum.cureVoice);
		imageDataService.add(imageData);
	}
	@Override
	public void updateDiseaseMsg(Disease intance) throws ServiceException {
		
		update(intance);
		if (intance.getImagePaths() != null) {
			saveDiseaseImage(intance.getId(), ReqUtil.instance.getUserId(), intance.getImagePaths(), false);
		}
	}


	@Override
	public void updateRelationIllCase(Map<String, Object> params) {
		List<Integer> diseaseIds =  (List<Integer>) params.get("diseaseIds");
		List<String> imageUrls =  (List<String>) params.get("imageUrls");
		if(diseaseIds != null && diseaseIds.size() > 0){
			if(params.get("diseaseInfo") != null 
					&& StringUtils.isNotBlank(params.get("diseaseInfo").toString())){
				for (Integer disId : diseaseIds) {
					saveOrDeleteDiseaseImage(disId,ReqUtil.instance.getUserId(),imageUrls.toArray(new String[]{}),true);
				}
			}
			mapper.updateRelationIllCase(params);
		}
	}
}
