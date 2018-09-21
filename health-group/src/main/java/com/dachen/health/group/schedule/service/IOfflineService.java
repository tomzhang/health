package com.dachen.health.group.schedule.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.dachen.commons.constants.UserSession;
import com.dachen.health.group.group.entity.vo.HospitalInfo;
import com.dachen.health.group.schedule.entity.param.OfflineParam;
import com.dachen.health.group.schedule.entity.po.Offline;
import com.dachen.health.group.schedule.entity.po.OfflineItem;
import com.dachen.health.group.schedule.entity.vo.DoctorOfflineVO;
import com.dachen.health.group.schedule.entity.vo.DoctorVO;
import com.dachen.health.group.schedule.entity.vo.OfflineVO;
import com.mongodb.BasicDBObject;

/**
 * ProjectName： health-group<br>
 * ClassName： IOfflineService<br>
 * Description：线下门诊service <br>
 * 
 * @author fanp
 * @createTime 2015年8月11日
 * @version 1.0.0
 */
public interface IOfflineService {

    /**
     * </p>添加门诊信息</p>
     * 
     * @param param
     * @author fanp
     * @throws CloneNotSupportedException
     * @date 2015年8月11日
     */
    void add(OfflineParam param) throws CloneNotSupportedException;

    /**
     * </p>查找医生线下坐诊时间表</p>
     * 
     * @param doctorId
     * @return List<OfflineVO> Set<hospital>
     * @author fanp
     * @date 2015年8月11日
     */
    Map<String, Object> getAll(Integer doctorId,Integer week,String lat,String lng,Integer is_hospital_group);

    /**
     * </p>查找医生线下坐诊时间表</p>
     * 
     * @param param
     * @return OfflineVO
     * @author fanp
     * @date 2015年8月11日
     */
    OfflineVO getOne(OfflineParam param);
    
    /**
     * </p>修改门诊坐诊信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月13日
     */
    void update(OfflineParam param);

    /**
     * </p>删除门诊坐诊信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月13日
     */
    void delete(OfflineParam param);

    /**
     * 患者获取医生7天的排班信息
     * @param doctorId
     * @param hospitalId
     * @param startDate 
     * @return
     */
    Map<String,Map<String,Boolean>> getDoctor7DaysOfflineItems(Integer doctorId, String hospitalId, Long startTime);

    /**
     * 
     * @Title: hasAppointment   
     * @Description: 判断医生该时间段是否有被患者预约
     * @param: @param param
     * @param: @return      
     * @return: Boolean      
     * @author: qinyuan.chen 
     * @date:   2016年6月13日 下午8:25:25 
     * @throws
     */
	Boolean hasAppointment(OfflineParam param);

	void addOneDayOfflineItem(Offline o, long startTime, int size);

	List<OfflineItem> offlineItemDetail(Integer doctorId, String hospitalId, Integer period, Long dateTime);

	Collection<BasicDBObject> getDoctorOfflineAndCount(String hospitalId, Long dateTime, Integer period);

	/**
	 * 
	 * @Title: addOffline   
	 * @Description: 添加排班信息 
	 * @param: @param param      
	 * @return: void      
	 * @author: qinyuan.chen 
	 * @date:   2016年6月22日 下午6:20:21 
	 * @throws
	 */
	void addOffline(OfflineParam param);

	/**
	 * 
	 * @Title: queryByConditions   
	 * @Description: 根据条件查询医生排班信息 
	 * @param: @param param
	 * @param: @return      
	 * @return: Map<String,Object>      
	 * @author: qinyuan.chen 
	 * @date:   2016年6月23日 上午11:27:49 
	 * @throws
	 */
	/*Map<Object, Object> queryByConditions(OfflineParam param);*/

	/**
	 * 
	 * @Title: getHospitalList   
	 * @Description: 根据登录用户获取集团医院列表   
	 * @param: @param us
	 * @param: @return      
	 * @return: List<HospitalInfo>      
	 * @author: qinyuan.chen 
	 * @date:   2016年6月23日 下午4:47:15 
	 * @throws
	 */
	List<HospitalInfo> getHospitalList(UserSession us);

	/**
	 * 
	 * @Title: getDoctorList   
	 * @Description: 根据登录用户获取集团医生列表
	 * @param: @param us
	 * @param: @return      
	 * @return: List<GroupDoctorVO>      
	 * @author: qinyuan.chen 
	 * @date:   2016年6月23日 下午5:08:06 
	 * @throws
	 */
	List<DoctorVO> getDoctorList(UserSession us);

	/**
	 * 
	 * @Title: queryDoctorPeriodOfflines   
	 * @Description: 查询某个医生在哪家医院某个时间段内的所有排班信息 
	 * @param: @param param
	 * @param: @return      
	 * @return: List<Offline>      
	 * @author: qinyuan.chen 
	 * @date:   2016年6月24日 上午10:21:03 
	 * @throws
	 */
	List<Offline> queryDoctorPeriodOfflines(OfflineParam param);

	/**
	 * 
	 * @Title: queryByConditionsForClient   
	 * @Description: 根据条件查询医生排班信息(客户端)
	 * @param: @param param
	 * @param: @return      
	 * @return: List<DoctorOfflineVO>      
	 * @author: qinyuan.chen 
	 * @date:   2016年6月24日 下午4:17:11 
	 * @throws
	 */
	List<DoctorOfflineVO> queryByConditionsForClient(OfflineParam param);

	/**
	 * 
	 * @Title: queryByConditionsForWeb   
	 * @Description: 根据条件查询医生排班信息(Web端) 
	 * @param: @param param
	 * @param: @return      
	 * @return: Map<String, List<Offline>>      
	 * @author: qinyuan.chen 
	 * @date:   2016年6月24日 下午4:17:48 
	 * @throws
	 */
	//Map<String, List<Offline>> queryByConditionsForWeb(OfflineParam param);
	List<Map<String,Object>> queryByConditionsForWeb(OfflineParam param);

	/**
	 * 
	 * @Title: hasOffline   
	 * @Description: 判断该医生在该时间段是否已有排班
	 * @param: @param param
	 * @param: @return      
	 * @return: Boolean      
	 * @author: qinyuan.chen 
	 * @date:   2016年6月24日 下午5:52:57 
	 * @throws
	 */
	Boolean hasOffline(OfflineParam param);

    
}
