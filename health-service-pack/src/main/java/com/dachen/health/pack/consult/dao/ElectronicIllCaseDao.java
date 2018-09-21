package com.dachen.health.pack.consult.dao;

import java.util.List;

import com.dachen.health.pack.consult.entity.po.IllCaseInfo;
import com.dachen.health.pack.consult.entity.po.IllCasePatientInfo;
import com.dachen.health.pack.consult.entity.po.IllCaseType;
import com.dachen.health.pack.consult.entity.po.IllCaseTypeContent;
import com.dachen.health.pack.consult.entity.po.IllTransferRecord;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease;

public interface ElectronicIllCaseDao {

	IllCaseInfo insertIllCase(IllCaseInfo illCaseInfo);

	void insertIllCasePatient(IllCasePatientInfo icpi);

	IllCasePatientInfo getIllCasePatient(String illCaseInfoId);

	IllCasePatientInfo updateIllCasePatient(IllCasePatientInfo illCasePatientInfo);

	IllCaseTypeContent saveIllCaseTypeContent(IllCaseTypeContent illCaseTypeContent);

	IllCaseInfo getIllCase(String id);
	
	IllCaseInfo getIllCaseBase(Integer patientId, int doctorId);

	List<IllCaseType> getInitIllCaseTypeByCategoryId(int categoryId);

	List<IllCaseTypeContent> getIllCaseTypeContentListByIllcaseId(String illCaseInfoId);

	void insertBaseData(IllCaseType ict);

	List<IllCaseType> getContentType();

	String getMustFillitemId();

	IllCaseTypeContent getCaseContent(String illCaseInfoId, String mfId);

	long getPatientIllcaseListTotal(Integer doctorId);

	List<IllCaseInfo> getPatientIllcaseList(Integer doctorId, Integer pageIndex, Integer pageSize);

	List<IllCaseInfo> getIllCaseListByUserIdAndPatientId(Integer userId, Integer patientId);

	IllCaseTypeContent getMainItemContentListByIllcaseId(String illcaseInfoId, String illcaseTypeId);

	long getMainItemContentListCountByIllcaseIds(List<String> illcaseInfoIds, String illCaseTypeId);

	List<IllCaseTypeContent> getMainItemContentListByIllcaseIds(List<String> illcaseInfoIds, String illCaseTypeId, Integer pageIndex, Integer pageSize);

	void clearContentData(String illCaseInfoId);

	void updateIllCasetoSaved(String illcaseInfoId);

	void updateIllCaseTreateType(String illCaseInfoId, String contentTxt);

	List<IllCaseInfo> findIllCase(Integer patientId, Integer userId, Integer doctorId);

	void updateillCaseUpdateTime(String illCaseInfoId);

	void clearIllPatientData(String illCaseInfoId);

	void clearIllCaseInfo(String illCaseInfoId);

	void updateIllCaseOrderId(String illCaseId, Integer orderId);

	void updateIllCaseMainCase(String illCaseInfoId, String contentTxt, List<String> contentImages);

	IllCaseType getIllCaseTypeById(String illCaseTypeId);

	IllCaseTypeContent getContentByInfoIdAndTypeId(String illCaseInfoId, String illCaseTypeId);

	void saveIllRecord(IllTransferRecord record);

	List<IllTransferRecord> getIllTransferRecordByIllCaseId(String illCaseInfoId);

	void syncPhoneOrderDiseaseToIllCase(Disease diseaseInfo);

	List<IllTransferRecord> findTransferRecords(Integer transferRecordType);

	IllCaseType getInitIllCaseTypeByName(String typeName);

	IllCaseInfo getByOrderId(Integer orderId);

	/**病历改造，查询医生可以看到的病程中检查项的类型**/
	List<IllCaseType> forDoctor();
	/**病历改造，查询医生可以看到的病程中检查项的类型**/
	List<IllCaseType> forPatient();

	List<IllCasePatientInfo> getIllCasePatients(List<String> oldIllCaseId);

	List<IllCaseType> getAllIllCaseTypes();

	List<IllCaseTypeContent> getallIllCaseTypeContents();

	void setIllCaseTypeContentDeal(String id);
}
