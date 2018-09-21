package com.dachen.health.pack.consult.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.pack.consult.dao.ElectronicIllCaseDao;
import com.dachen.health.pack.consult.entity.po.IllCaseInfo;
import com.dachen.health.pack.consult.entity.po.IllCasePatientInfo;
import com.dachen.health.pack.consult.entity.po.IllCaseType;
import com.dachen.health.pack.consult.entity.po.IllCaseTypeContent;
import com.dachen.health.pack.consult.entity.po.IllTransferRecord;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.tencent.common.Util;

@Repository("electronicIllCaseDao")
public class ElectronicIllCaseDaoImpl extends NoSqlRepository implements ElectronicIllCaseDao {

	@Override
	public IllCaseInfo insertIllCase(IllCaseInfo illCaseInfo) {
		if(illCaseInfo.getDoctorId() == null || illCaseInfo.getUserId() == null || illCaseInfo.getPatientId() == null){
			throw new ServiceException("插入电子病历时，必要参数为空");
		}
		String id = dsForRW.insert(illCaseInfo).getId().toString();
		return dsForRW.createQuery(IllCaseInfo.class).field("_id").equal(new ObjectId(id)).get();
	}

	@Override
	public void insertIllCasePatient(IllCasePatientInfo icpi) {
		dsForRW.insert(icpi);
	}

	@Override
	public IllCasePatientInfo getIllCasePatient(String illCaseInfoId) {
		return dsForRW.createQuery(IllCasePatientInfo.class).field("illCaseInfoId").equal(illCaseInfoId).get();
	}

	@Override
	public IllCasePatientInfo updateIllCasePatient(IllCasePatientInfo illCasePatientInfo) {
		UpdateOperations<IllCasePatientInfo> ops = dsForRW.createUpdateOperations(IllCasePatientInfo.class);
		Query<IllCasePatientInfo> query = dsForRW.createQuery(IllCasePatientInfo.class).
				field("illCaseInfoId").equal(illCasePatientInfo.getIllCaseInfoId());
		if(illCasePatientInfo.getIsMarried() != null){
			ops.set("isMarried", illCasePatientInfo.getIsMarried());
		}
		if(illCasePatientInfo.getAge() != null){
			ops.set("age", illCasePatientInfo.getAge());
		}
		if(illCasePatientInfo.getArea() != null){
			ops.set("area", illCasePatientInfo.getArea());
		}
		if(illCasePatientInfo.getHeight() != null){
			ops.set("height", illCasePatientInfo.getHeight());
		}
		if(illCasePatientInfo.getJob() != null){
			ops.set("job", illCasePatientInfo.getJob());
		}
		if(illCasePatientInfo.getPatientName() != null){
			ops.set("patientName", illCasePatientInfo.getPatientName());
		}
		if(illCasePatientInfo.getSex() != null){
			ops.set("sex", illCasePatientInfo.getSex());
		}
		if(illCasePatientInfo.getWeight() != null){
			ops.set("weight", illCasePatientInfo.getWeight());
		}
		if(illCasePatientInfo.getTelephone() != null){
			ops.set("telephone", illCasePatientInfo.getTelephone());
		}
		dsForRW.updateFirst(query, ops);
		return query.get();
	}

	
	private List<String> setImageUrlToPath(List<String> imageUrls) {
		List<String> imagePaths = new ArrayList<String>();
		if(imageUrls != null && imageUrls.size() > 0){
			for (String url : imageUrls) {
				String path = PropertiesUtil.removeUrlPrefix(url);
				imagePaths.add(path);
			}
		}
		return imagePaths;
	}
	
	
	private List<String> setImagePathToUrl(List<String> imagePaths) {
		List<String> imageUrls = new ArrayList<String>();
		if(imagePaths != null && imagePaths.size() > 0){
			for (String path : imagePaths) {
				String url = PropertiesUtil.addUrlPrefix(path);
				imageUrls.add(url);
			}
		}
		return imageUrls;
	}
	
	
	@Override
	public IllCaseTypeContent saveIllCaseTypeContent(IllCaseTypeContent illCaseTypeContent) {
		String illCaseInfoId = illCaseTypeContent.getIllCaseInfoId();
		String illCaseTypeId = illCaseTypeContent.getIllCaseTypeId();
		IllCaseType illcaseType = dsForRW.createQuery(IllCaseType.class).field("_id").equal(new ObjectId(illCaseTypeId)).get();
		if(illcaseType == null){
			return null;
		}
		int categoryId = illcaseType.getCategoryId();
		Query<IllCaseTypeContent> query = dsForRW.createQuery(IllCaseTypeContent.class)
					.field("illCaseInfoId").equal(illCaseInfoId)
					.field("illCaseTypeId").equal(illCaseTypeId);
		IllCaseTypeContent ictc = query.get();
		if(ictc != null && categoryId == 1){
			/**
			 * 2016年2月16日16:12:50
			 * update by wangl
			 * 就医资料中同一类型可以多次添加数据
			 */
			UpdateOperations<IllCaseTypeContent> ops = dsForRW.createUpdateOperations(IllCaseTypeContent.class);
			ops.set("contentTxt", illCaseTypeContent.getContentTxt());
			ops.set("contentImages", setImageUrlToPath(illCaseTypeContent.getContentImages()));
			ops.set("updateTime", System.currentTimeMillis());
			dsForRW.updateFirst(query, ops);
		}else{
			long time = System.currentTimeMillis();
			illCaseTypeContent.setCreateTime(time);
			illCaseTypeContent.setUpdateTime(time);
			illCaseTypeContent.setContentImages(setImageUrlToPath(illCaseTypeContent.getContentImages()));
			dsForRW.insert(illCaseTypeContent);
		}
		return query.get();
	}
	
	@Override
	public IllCaseInfo getIllCase(String id) {
		return dsForRW.createQuery(IllCaseInfo.class).field("_id").equal(new ObjectId(id)).get();
	}

	@Override
	public IllCaseInfo getIllCaseBase(Integer patientId, int doctorId) {
		return dsForRW.createQuery(IllCaseInfo.class).field("doctorId").equal(doctorId).
			field("patientId").equal(patientId).get();
	}

	@Override
	public List<IllCaseType> getInitIllCaseTypeByCategoryId(int categoryId) {
		Query<IllCaseType> query = dsForRW.createQuery(IllCaseType.class).field("dataType").equal(1);
		if(categoryId == 1 || categoryId == 2){
			//查询所有基础类型
			query.field("categoryId").equal(categoryId);
		}
		query.order("typeOrder");
		List<IllCaseType> list = query.asList();
		if(!Util.isNullOrEmpty(list)){
			IllCaseType item = list.get(list.size() - 1);
			if(item.getCategoryId().intValue() == 1 
					&& "就诊时间".equals(item.getTypeName())){
				list.remove(item);
			}else{
				Iterator<IllCaseType> ite = list.iterator();
				while(ite.hasNext()){
					IllCaseType type = ite.next();
					if(type.getCategoryId().intValue() == 1 
							&& "就诊时间".equals(type.getTypeName())){
						ite.remove();
					}
				}
			}
		}
		return list;
	}

	@Override
	public List<IllCaseTypeContent> getIllCaseTypeContentListByIllcaseId(String illCaseInfoId) {
		List<IllCaseTypeContent> list = 
				dsForRW.createQuery(IllCaseTypeContent.class).field("illCaseInfoId").equal(illCaseInfoId).asList();
		if(list != null && list.size() > 0){
			for (IllCaseTypeContent illCaseTypeContent : list) {
				illCaseTypeContent.setContentImages(setImagePathToUrl(illCaseTypeContent.getContentImages()));
			}
		}
		return list;
	}

	@Override
	public void insertBaseData(IllCaseType ict) {
		dsForRW.insert(ict);
	}

	@Override
	public List<IllCaseType> getContentType() {
		return dsForRW.createQuery(IllCaseType.class).field("categoryId").equal(2)
				.field("dataType").equal(1).order("typeOrder").asList();
	}

	/**
	 *  "typeOrder" : 2.0, 
    "categoryId" : NumberInt(1), 
    "dataType" : NumberInt(1)
	 * 
	 * 
	 * 
	 */
	@Override
	public String getMustFillitemId() {
		return dsForRW.createQuery(IllCaseType.class).field("required").equal(true).get().getId();
	}

	@Override
	public IllCaseTypeContent getCaseContent(String illCaseInfoId, String mfId) {
			return dsForRW.createQuery(IllCaseTypeContent.class).field("illCaseInfoId").equal(illCaseInfoId)
						.field("illCaseTypeId").equal(mfId).get();
	}

	@Override
	public long getPatientIllcaseListTotal(Integer doctorId) {
		return dsForRW.createQuery(IllCaseInfo.class).field("doctorId").equal(doctorId).countAll();
	}

	@Override
	public List<IllCaseInfo> getPatientIllcaseList(Integer doctorId, Integer pageIndex, Integer pageSize) {
		return dsForRW.createQuery(IllCaseInfo.class).field("doctorId")
			.equal(doctorId).order("-createTime").offset(pageIndex * pageSize).limit(pageSize).asList();
	}

	@Override
	public List<IllCaseInfo> getIllCaseListByUserIdAndPatientId(Integer userId, Integer patientId) {
		return dsForRW.createQuery(IllCaseInfo.class).field("userId").equal(userId)
							.field("patientId").equal(patientId).asList();
	}

	@Override
	public IllCaseTypeContent getMainItemContentListByIllcaseId(String illcaseInfoId, String illcaseTypeId) {
		return dsForRW.createQuery(IllCaseTypeContent.class).field("illCaseInfoId").equal(illcaseInfoId)
				.field("illCaseTypeId").equal(illcaseTypeId).get();
	}

	@Override
	public long getMainItemContentListCountByIllcaseIds(List<String> illcaseInfoIds, String illCaseTypeId) {
		return dsForRW.createQuery(IllCaseTypeContent.class).field("illCaseInfoId").in(illcaseInfoIds)
				.field("illCaseTypeId").equal(illCaseTypeId).countAll();
	}

	@Override
	public List<IllCaseTypeContent> getMainItemContentListByIllcaseIds(List<String> illcaseInfoIds,String illCaseTypeId,Integer pageIndex, Integer pageSize){
		return dsForRW.createQuery(IllCaseTypeContent.class).field("illCaseInfoId").in(illcaseInfoIds)
				.field("illCaseTypeId").equal(illCaseTypeId)
				.order("-updateTime")
				.offset(pageIndex * pageSize)
				.limit(pageSize)
				.asList();
	}

	@Override
	public void clearContentData(String illCaseInfoId) {
		Query<IllCaseTypeContent> q = 
				dsForRW.createQuery(IllCaseTypeContent.class).field("illCaseInfoId").equal(illCaseInfoId);
		dsForRW.delete(q);
	}

	@Override
	public void updateIllCasetoSaved(String illcaseInfoId) {
		UpdateOperations<IllCaseInfo> ops = dsForRW.createUpdateOperations(IllCaseInfo.class);
		Query<IllCaseInfo> query = dsForRW.createQuery(IllCaseInfo.class).field("_id").equal(new ObjectId(illcaseInfoId));
		ops.set("isSaved", true);
		dsForRW.updateFirst(query, ops);
	}

	@Override
	public void updateIllCaseTreateType(String illCaseInfoId, String contentTxt) {
		UpdateOperations<IllCaseInfo> ops = dsForRW.createUpdateOperations(IllCaseInfo.class);
		Query<IllCaseInfo> query = dsForRW.createQuery(IllCaseInfo.class).field("_id").equal(new ObjectId(illCaseInfoId));
		ops.set("treateType", Integer.valueOf(contentTxt));
		ops.set("updateTime", System.currentTimeMillis());
		dsForRW.updateFirst(query, ops);
	}

	@Override
	public List<IllCaseInfo> findIllCase(Integer patientId, Integer userId, Integer doctorId) {
		Query<IllCaseInfo> q = dsForRW.createQuery(IllCaseInfo.class);
		q.field("patientId").equal(patientId);
		q.field("userId").equal(userId);
		if(doctorId != null){
			q.field("doctorId").equal(doctorId);
		}
		q.order("-updateTime");
		return q.asList();
	}

	@Override
	public void updateillCaseUpdateTime(String illCaseInfoId) {
		UpdateOperations<IllCaseInfo> ops = dsForRW.createUpdateOperations(IllCaseInfo.class);
		Query<IllCaseInfo> query = dsForRW.createQuery(IllCaseInfo.class).field("_id").equal(new ObjectId(illCaseInfoId));
		ops.set("updateTime", System.currentTimeMillis());
		dsForRW.updateFirst(query, ops);
	}

	@Override
	public void clearIllPatientData(String illCaseInfoId) {
		Query<IllCasePatientInfo> q = 
				dsForRW.createQuery(IllCasePatientInfo.class).field("illCaseInfoId").equal(illCaseInfoId);
		dsForRW.delete(q);
	}

	@Override
	public void clearIllCaseInfo(String illCaseInfoId) {
		Query<IllCaseInfo> q = 
				dsForRW.createQuery(IllCaseInfo.class).field("_id").equal(new ObjectId(illCaseInfoId));
		dsForRW.delete(q);
	}

	@Override
	public void updateIllCaseOrderId(String illCaseId, Integer orderId) {
		UpdateOperations<IllCaseInfo> ops = dsForRW.createUpdateOperations(IllCaseInfo.class);
		Query<IllCaseInfo> q = 
				dsForRW.createQuery(IllCaseInfo.class).field("_id").equal(new ObjectId(illCaseId));
		ops.set("orderId",orderId);
		dsForRW.updateFirst(q, ops);
	}

	@Override
	public void updateIllCaseMainCase(String illCaseInfoId, String mainCase, List<String> contentImages) {
		UpdateOperations<IllCaseInfo> ops = dsForRW.createUpdateOperations(IllCaseInfo.class);
		Query<IllCaseInfo> q = 
				dsForRW.createQuery(IllCaseInfo.class).field("_id").equal(new ObjectId(illCaseInfoId));
		ops.set("mainCase",mainCase);
		ops.set("imageUlrs",setImageUrlToPath(contentImages));
		dsForRW.updateFirst(q, ops);
	}

	@Override
	public IllCaseType getIllCaseTypeById(String illCaseTypeId) {
		return dsForRW.createQuery(IllCaseType.class).field("_id").equal(new ObjectId(illCaseTypeId)).get();
	}

	@Override
	public IllCaseTypeContent getContentByInfoIdAndTypeId(String illCaseInfoId, String illCaseTypeId) {
		return dsForRW.createQuery(IllCaseTypeContent.class).field("illCaseInfoId").equal(illCaseInfoId)
				.field("illCaseTypeId").equal(illCaseTypeId).get();
	}

	@Override
	public void saveIllRecord(IllTransferRecord record) {
		dsForRW.insert(record);
	}

	@Override
	public List<IllTransferRecord> getIllTransferRecordByIllCaseId(String illCaseInfoId) {
		return dsForRW.createQuery(IllTransferRecord.class).field("illCaseInfoId").equal(illCaseInfoId).asList();
	}

	@Override
	public void syncPhoneOrderDiseaseToIllCase(Disease diseaseInfo) {
		String illCaseInfoId = diseaseInfo.getIllCaseInfoId();
		List<IllCaseType> types = getInitIllCaseTypeByCategoryId(1);
		for (IllCaseType type : types) {
			String typeName = type.getTypeName();
			IllCaseTypeContent content = new IllCaseTypeContent();
			content.setIllCaseInfoId(illCaseInfoId);
			content.setIllCaseTypeId(type.getId());
			if(StringUtils.isNotBlank(diseaseInfo.getDiseaseDesc()) 
					&& typeName.equals("主诉")){
				content.setContentTxt(diseaseInfo.getDiseaseDesc());
				if(diseaseInfo.getDiseaseImgs() != null){
					content.setContentImages(diseaseInfo.getDiseaseImgs());
				}
				saveIllCaseTypeContent(content);
			}else if(StringUtils.isNotBlank(diseaseInfo.getCureSituation()) 
					&& typeName.equals("诊治情况")){
				content.setContentTxt(diseaseInfo.getCureSituation());
				saveIllCaseTypeContent(content);
			}else if(StringUtils.isNotBlank(diseaseInfo.getDiseaseInfo_now()) 
					&& typeName.equals("现病史")){
				content.setContentTxt(diseaseInfo.getDiseaseInfo_now());
				saveIllCaseTypeContent(content);
			}else if(StringUtils.isNotBlank(diseaseInfo.getDiseaseInfo_old()) 
					&& typeName.equals("既往史")){
				content.setContentTxt(diseaseInfo.getDiseaseInfo_old());
				saveIllCaseTypeContent(content);
			}else if(StringUtils.isNotBlank(diseaseInfo.getFamilydiseaseInfo()) 
					&& typeName.equals("家族史")){
				content.setContentTxt(diseaseInfo.getFamilydiseaseInfo());
				saveIllCaseTypeContent(content);
			}else if(StringUtils.isNotBlank(diseaseInfo.getMenstruationdiseaseInfo()) 
					&& typeName.equals("月经生育史")){
				content.setContentTxt(diseaseInfo.getMenstruationdiseaseInfo());
				saveIllCaseTypeContent(content);
			}
		}
	}

	@Override
	public List<IllTransferRecord> findTransferRecords(Integer transferRecordType) {
		Query<IllTransferRecord> q = dsForRW.createQuery(IllTransferRecord.class);
		Integer userId = ReqUtil.instance.getUserId();
		if(transferRecordType.intValue() == 1){
			q.field("transferDoctorId").equal(userId);
		}else if(transferRecordType.intValue() == 2){
			q.field("receiveDoctorId").equal(userId);
		}
		return q.asList();
	}

	@Override
	public IllCaseType getInitIllCaseTypeByName(String typeName) {
		return dsForRW.createQuery(IllCaseType.class)
					.field("typeName").equal(typeName)
					.get();
	}

	@Override
	public IllCaseInfo getByOrderId(Integer orderId) {
		return dsForRW.createQuery(IllCaseInfo.class).field("orderId").equal(orderId).get();
	}

	@Override
	public List<IllCaseType> forDoctor() {
		return dsForRW.createQuery(IllCaseType.class).field("forDoctor").equal(true).order("typeOrder").asList();
	}

	@Override
	public List<IllCaseType> forPatient() {
		return dsForRW.createQuery(IllCaseType.class).field("forPatient").equal(true).order("typeOrder").asList();
	}

	@Override
	public List<IllCasePatientInfo> getIllCasePatients(List<String> oldIllCaseId) {
		return dsForRW.createQuery(IllCasePatientInfo.class).field("illCaseInfoId").in(oldIllCaseId).asList();
	}

    @Override
    public List<IllCaseType> getAllIllCaseTypes() {
        return dsForRW.createQuery(IllCaseType.class).asList();
    }

    @Override
    public List<IllCaseTypeContent> getallIllCaseTypeContents() {
        return dsForRW.createQuery(IllCaseTypeContent.class).asList();
    }

	@Override
	public void setIllCaseTypeContentDeal(String id) {
		Query<IllCaseTypeContent> query = dsForRW.createQuery(IllCaseTypeContent.class).field("_id").equal(new ObjectId(id));
		UpdateOperations<IllCaseTypeContent> ops = dsForRW.createUpdateOperations(IllCaseTypeContent.class);
		ops.set("deal", true);
		dsForRW.update(query, ops);
	}

}
