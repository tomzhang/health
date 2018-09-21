package com.dachen.health.group.schedule.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.dachen.health.group.group.entity.vo.HospitalInfo;
import com.dachen.health.group.schedule.entity.param.OfflineParam;
import com.dachen.health.group.schedule.entity.po.Offline;
import com.dachen.health.group.schedule.entity.po.OfflineItem;
import com.dachen.health.group.schedule.entity.vo.OfflineVO;
import com.mongodb.BasicDBObject;

/**
 * ProjectName： health-group<br>
 * ClassName： IOfflineDao<br>
 * Description： 下线门诊dao<br>
 * 
 * @author fanp
 * @createTime 2015年8月11日
 * @version 1.0.0
 */
public interface IOfflineDao {

    /**
     * </p>添加门诊坐诊信息,通过更新来排队存不存在</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年8月11日
     */
    void add(Offline po);

    /**
     * </p>获取门诊坐诊信息</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年8月12日
     */
    OfflineVO getOne(OfflineParam param);

    /**
     * </p>删除门诊坐诊信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月11日
     */
    void delete(OfflineParam param);

    /**
     * </p>更新门诊坐诊信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月12日
     */
    void update(OfflineParam param);

    /**
     * </p>查找医生线下坐诊时间表</p>
     * 
     * @param doctorId
     * @return List<OfflineVO> Set<hospital>
     * @author fanp
     * @date 2015年8月11日
     */
    Map<String,Object> getAll(Integer doctorId,Integer week);
   
    /**
     * 查找医生坐诊医院列表
     * @param doctorId
     * @param week
     * @return
     */
    List<OfflineVO> getAllhospital(Integer doctorId,Integer week,String lat,String lng,List<HospitalInfo> list);
    
    /**
     * 查找医生线下坐诊时间表（按照医院进行分组）
     * @param doctorId
     * @param week
     * @return
     */
    Map<String,Object> getAllHostialGroup(Integer doctorId,Integer week,String lat,String lng,List<HospitalInfo> hospitals);
    /**
     * 
     * @param doctorIds
     * @param hospital
     * @return
     */
    Map<String,List<OfflineParam>> getDoctor(List<Integer> doctorIds,List<String> hospital);

	Collection<BasicDBObject> getDoctor7DaysOfflineItems(Integer doctorId, String hospitalId, Long startTime);

	OfflineItem insertOfflineItem(OfflineItem item);

	List<OfflineItem> queryByCondition(OfflineParam param);

	List<Offline> getByWeek(int week);

	List<OfflineItem> offlineItemDetail(Integer doctorId, String hospitalId,Integer period,
			Long dateTime);

	Collection<BasicDBObject> getDoctorOfflineAndCount(String hospitalId, Long dateTime, Integer period);

	Long searchAppointmentOrder4GuideCount(List<Integer> patientIds);
	
	void removeOfflineItemList(OfflineVO oldOffline);
	
	public List<Long> getHasAppointmentOfflineItemList(OfflineVO oldOffline);

	List<OfflineItem> searchAppointmentOrder4Guide(List<Integer> patientIds, Integer pageIndex, Integer pageSize);

	OfflineItem findOfflineItemById(String offlineItemId);

	void updateOfflineItemStatus(String offlineItemId, int status);

	void updateOfflineItemOrderInfo(String offlineItemId, Integer orderId, Integer patientId);

	void updateOfflineItemStatusByOrderId(Integer orderId,Integer doctorId , Integer status);

	OfflineItem findOfflineItemByOrderId(Integer orderId, Integer doctorId);

	void cancelOfflineItem(Integer orderId);

	List<OfflineItem> getPatientAppointmentByCondition(String hospitalId, Integer doctorId, Long oppointTime);

	List<Offline> findOfflineByDoctorPeriod(String hospitalId, Integer doctorId, Integer week, Integer period);

	List<OfflineItem> getDoctorOneDayOffline(String hospitalId, long dateTime, Integer doctorId);

	OfflineItem getOfflineItem(Long startTime, Integer doctorId);

	List<Offline> queryByConditions(OfflineParam param);

	List<Integer> getDoctorIdsInHospitalIds(List<String> hospitalIds);

	Long queryhasOffline(OfflineParam param);

	void addNew(Offline offline);

	List<Offline> queryByConditionsForWeb(OfflineParam param);


}
