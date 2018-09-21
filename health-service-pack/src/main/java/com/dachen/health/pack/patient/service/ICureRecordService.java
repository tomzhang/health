package com.dachen.health.pack.patient.service;

import com.dachen.drug.api.entity.CGoodsView;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.pack.patient.model.CureRecord;
import com.dachen.sdk.exception.HttpApiException;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author vincent
 *
 */
public interface ICureRecordService extends IBaseService<CureRecord, Integer>{
	
	int save(CureRecord intance,String[] images,String[] voices) throws HttpApiException;
	
	int update(CureRecord intance,String[] images,String[] voices) throws HttpApiException;
	
	List<CureRecord> findByOrderId(int orderId) throws HttpApiException;
	
	List<CureRecord> findByPatientAndDoctor(Integer patientId,Integer doctorId) throws HttpApiException;
	
	CGoodsView getUsageByDrugId(String drugId) throws HttpApiException;
	
	/**
	 * 医生填写完诊疗纪录后向导医推送系统通知
	 * @param intance
	 */
	void sendNotice(CureRecord intance) throws HttpApiException;
	
	/**
	 * 获取医生常用疾病（按医生使用次数取前15位）
	 * @param doctorId
	 * @return
	 */
	List<DiseaseTypeVO> getCommonDiseasesByDocId(Integer doctorId);
	
	//查询需要让导医帮忙的订单
	List<CureRecord> getListByCondition();

	Map<String, Object> pushMessageToDoctor(Integer doctorId);
	
	List<CureRecord> getVoiceUrlByOrderId(Integer orderId);

}
