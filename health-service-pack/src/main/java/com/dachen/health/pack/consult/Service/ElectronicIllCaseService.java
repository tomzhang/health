package com.dachen.health.pack.consult.Service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.consult.entity.po.IllCaseInfo;
import com.dachen.health.pack.consult.entity.po.IllCasePatientInfo;
import com.dachen.health.pack.consult.entity.po.IllCaseType;
import com.dachen.health.pack.consult.entity.po.IllCaseTypeContent;
import com.dachen.health.pack.consult.entity.vo.IllCaseInfoPageVo;
import com.dachen.health.pack.consult.entity.vo.IllHistoryInfoItem;
import com.dachen.sdk.exception.HttpApiException;

import java.util.List;
import java.util.Map;

public interface ElectronicIllCaseService {

	IllCaseInfo createIllCaseInfo(IllCaseInfo illCaseInfo);

	IllCasePatientInfo getIllCasePatient(String illCaseInfoId, Integer patientId);

	IllCasePatientInfo updateIllCasePatient(IllCasePatientInfo illCasePatientInfo);

	IllCaseInfoPageVo getIllCaseInfo(Integer patientId, Integer userId, Integer doctorId, Integer enterType) throws HttpApiException;
	
	IllCaseInfoPageVo getIllCaseInfo(String illCaseId) throws HttpApiException;

	IllCaseTypeContent saveIllCaseTypeContent(IllCaseTypeContent illCaseTypeContent);

	int batchSaveIllCaseTypeContent(String illCaseTypeContentString);

	Object insertBaseData();

	List<IllCaseType> getSeekIllInit();

	Object isFinished(Integer orderId);

	PageVO getPatientIllcaseList(Integer userId, Integer patientId, Integer pageIndex, Integer pageSize);

	IllCaseInfoPageVo getIllCaseByOrderId(Integer orderId) throws HttpApiException;
	
	/***begin add  by  liwei  2016年1月26日********/
	//医生结束服务  给患者生成电子病历
	//public  void createElctriExperience(Order order);
	/***end add  by  liwei  2016年1月26日********/

	Object updateIllCasetoSaved(String illcaseInfoId);

	Map<String,Object> createAndGet(Integer patientId, Integer userId, Integer doctorId,Integer orderId , Integer treateType);

	Object getIllRecordList(Integer userId, Integer doctorId);
	
	Object getIllRecords(Integer userId, Integer doctorId);

	Map<String,Object> getIllcaseBaseContentById(String illcaseInfoId);

	Object createOrGetByOrderId(Integer orderId);
	
	void clearIllCaseAllById(String illCaseInfoId) ;

	void setDiseaseInfo(String illCaseInfoId, com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease d);

	Object getIllcase4CreateOrder(String illcaseInfoId);

	List<IllHistoryInfoItem> getIllRecordsByPatientId(Integer userId,Integer patientId,Integer doctorId);

	List<IllHistoryInfoItem> getIllRecordsByDiseaseId(Integer patientId,String diseaseId);

}
