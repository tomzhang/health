package com.dachen.health.pack.pack.service;

import com.dachen.drug.api.entity.CGoodsView;
import com.dachen.health.commons.vo.CarePlanDoctorVO;
import com.dachen.health.group.group.entity.vo.DoctorInfoDetailsVO;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.entity.po.PackDoctor;
import com.dachen.health.pack.pack.entity.vo.PackVO;
import com.dachen.sdk.exception.HttpApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IPackService {

	/**
	 * </p>
	 * 根据id查找套餐
	 * </p>
	 * 
	 * @param id
	 * @return
	 * @author fanp
	 * @date 2015年8月11日
	 */
	Pack getPack(Integer id);

	void addPackIfMessageNull(Integer userId) throws HttpApiException;

	Map<String, Object> addPack(Pack pack) throws HttpApiException;
	
	public Pack addFeeBillPack(Pack pack);

	void deletePack(Integer id);
	
	Map updatePack(Pack pack);

	List<Pack> queryPack(Pack pack);

	boolean isValidCareTemplateForPatient(Pack pack);

	List<PackVO> queryPack12(Pack pack);
	
	Integer queryPack12Point(Pack pack);
	
	public boolean isValidCare(Pack pack);

	void savePackDoctor(Integer packId, List<PackDoctor> packDoctorList) throws HttpApiException;

	public List<Pack> findPackByType(Integer docId, String groupId, Integer packType);
	List<Pack> findByDoctorIdAndPackType(Integer doctorId, Integer packType);
	boolean ifAdded(Integer doctorId, String carePlanId, String carePlanSourceId);

	List<Pack> queryByUserIds(List<Integer> doctorUserIds);

	public Map getPack012Doctors(List<Integer> doctorUserIds);

	public Boolean checkPackBy(Integer doctorUserId, String followUpTemplateId);

	public void executeFeeUpdate(String groupId, List<Integer> doctorIds) throws HttpApiException;

	/**
	 * 删除套餐药品
	 * 
	 * @param packDrugId
	 */
	public void deletePackDrug(Integer packDrugId);

	/**
	 * 添加套餐药品
	 * 
	 * @param packDrugId
	 */
	public void addPackDrug(Integer packId, List<String> drugIds);

	/**
	 * 查询套餐药品数据
	 * 
	 * @param packId
	 * @param access_tonke
	 * @return
	 */
	public List<CGoodsView> findPackDrugView(Integer packId, String access_tonke) throws HttpApiException;

	Pack getDoctorPackByType(Integer doctorId, int packType);

	void deleteConsultationPackByDoctorId(Integer doctorId);
/***begin add  by  liwei  2016年1月22日
 * @param queryType ********/
	public 	 List<Pack> selectPackDortorList(Integer queryType);



	/***end add  by  liwei  2016年1月22日********/
	
	
	Set<Integer> getAllBeSearchDoctorIds(ArrayList<Integer> arrayList);

	void updateConsultationPackPrice(Integer doctorId, long price);

	List<Integer> getConsultationDoctorId(List<Integer> friendIds);

	List<Integer> getConsultationDoctorIdNotInIds(List<Integer> friendIds);

	Pack getDoctorPackDBData(Integer doctorId, int packType);

	Set<Integer> getAllConsultationDoctorIds();

	Object getDoctorAppointment(String groupId, Integer doctorId) throws HttpApiException;
	
	List<Integer> getDocIdsByServiceType(String groupId, String packId);
	
	void setPackForDoctorVO(DoctorInfoDetailsVO vo);
	
	/**
	 * 根据用户的Id获取该用户的套餐
	 * @param doctorId
	 * @return
	 */
	List<Pack> getByDoctorIdAndPacktype(Integer doctorId, Integer packType);
	
	List<Pack> withRemoveDisablePack(List<Pack> packList);
	
	void saveIntegralPack(Integer doctorId, String goodsGroupId, String goodsGroupName, Integer point, Integer status);
	
	Pack getIntegralPack(Integer doctorId, String goodsGroupId);

	Set<String> getPackNameByDoctorId(Integer doctorId);

	List<CarePlanDoctorVO> findUserInfoByPack(Integer packId);

	List<String> findCarePlanIdListByDoctor(Integer doctorId);
	
	void deletePackByConsultationId(String consultationId);
}
