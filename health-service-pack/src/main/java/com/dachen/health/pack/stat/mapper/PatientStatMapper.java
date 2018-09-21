package com.dachen.health.pack.stat.mapper;

import java.util.List;

import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.pack.stat.entity.param.PatientStatParam;
import com.dachen.health.pack.stat.entity.vo.DiseaseInfoVo;
import com.dachen.health.pack.stat.entity.vo.PatientStatVO;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IPatientStatService<br>
 * Description：患者统计service <br>
 * 
 * @author fanp
 * @createTime 2015年9月22日
 * @version 1.0.0
 */
public interface PatientStatMapper {

    /**
     * </p>统计用户被医生治疗过的患者</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    List<PatientStatVO> getByUserAndDoctor(PatientStatParam param);

    /**
     * </p>按用户和病种查找患者</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    List<PatientStatVO> getByUserAndDisease(PatientStatParam param);
    
    /**
     * </p>按病种统计出被医生治疗过用户</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月25日
     */
    List<Integer> getUserByDiseaseAndDoctor(PatientStatParam param);
    
    /**
     * </p>按病种统计出被医生治疗过用户</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月25日
     */
    Long getUserByDiseaseAndDoctorCount(PatientStatParam param);
    
    /**
     * </p>获取诊疗记录,通过患者和医生获取</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    List<PatientStatVO> getCureRecordByDoctor(PatientStatParam param);
    
    /**
     * </p>获取诊疗记录,通过患者和医生获取</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    List<PatientStatVO> getCureRecordByGroupDoctor(PatientStatParam param);
    
    List<PatientStatVO> getDieaseByGroupDoctor(PatientStatParam param);
    
    /**
     * </p>获取诊疗记录,通过患者和病种获取</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    List<PatientStatVO> getCureRecordByDisease(PatientStatParam param);
    
    /**
     * </p>获取医生给患者打的病种</p>
     * @param doctorIds
     * @return
     * @author fanp
     * @date 2015年9月29日
     */
    List<String> getDiseaseByDoctor(List<Integer> doctorIds);
    
    /**
     * </p>获取医生给患者打的病种</p>
     * @param doctorIds
     * @return
     * @author fanp
     * @date 2015年9月29日
     */
    List<String> getDiseaseByDoctorAndGroup(StatParam param);
    
    /**
     * 统计 集团所有患者信息
     * @param groupId
     * @return
     *@author wangqiao
     *@date 2015年12月31日
     */
    List<Integer> statGroupPatient(StatParam param);
    
    /**
     * 计算 集团所有患者总数
     * @param groupId
     * @return
     *@author wangqiao
     *@date 2015年12月31日
     */
    Integer countGroupPatient(StatParam param);
    
    /**
     * 统计 集团中某个医生 的患者信息
     * @param groupId
     * @param doctorId
     * @return
     *@author wangqiao
     *@date 2015年12月31日
     */
    List<Integer> statDoctorPatient(StatParam param);
    
    /**
     * 计算 集团中某个医生的患者总数
     * @param groupId
     * @param doctorId
     * @return
     *@author wangqiao
     *@date 2015年12月31日
     */
    Integer countDoctorPatient(StatParam param);
    /**
     * 统计 集团中多个医生的患者信息
     * @param groupId
     * @param doctorIds
     * @return
     *@author wangqiao
     *@date 2015年12月31日
     */
    List<Integer> statDoctorsPatient(StatParam param);
    
    /**
     * 计算 集团中多个医生的患者总数
     * @param groupId
     * @param doctorIds
     * @return
     *@author wangqiao
     *@date 2015年12月31日
     */
    Integer countDoctorsPatient(StatParam param);
    
    
    /**
     * 统计患者对应的集团列表
     * @param userId
     * @return
     *@author liwei
     *@date 2015年12月31日
     */
    List<String> statPatietGroupList(PatientStatParam param);
    
    /**
     * 统计患者对应的医生列表
     * @param userId
     * @return
     *@author liwei
     *@date 2015年12月31日
     */
    List<Integer> statPatietDoctorList(PatientStatParam param);
    
    /**
     * 统计集团的患者的地区信息
     * @param groupId 集团id
     * @author fuyongde
     * @date 2016年11月8日
     * @return
     */
    List<String> getGroupPatientDiseases(String groupId);
    
    
    List<String> getGroupPatientRegions(String groupId);
    
    List<PatientStatVO> getGroupPatientInfos(PatientStatParam param);
    Integer getGroupPatientInfosCount(PatientStatParam param);
    Integer getGroupPatientCount(String groupId);
    List<DiseaseInfoVo> getDiseaseNames(PatientStatParam param);


    /**
     * 按条件查询用户总数
     * @param param
     * @return
     * @author qinyuan.chen
     * @date 2016年12月28日
     */
    Integer countGroupUserByCondition(StatParam param);


    /**
     * 按条件查询用户ID集合
     * @param param
     * @return
     * @author qinyuan.chen
     * @date 2016年12月28日
     */
    List<Integer> queryGroupUserByCondition(StatParam param);

    /**
     * 按条件查询用户总数
     * @param param
     * @return
     * @author qinyuan.chen
     * @date 2016年12月28日
     */
    Integer countGroupUserByDisease(StatParam param);


    /**
     * 按条件查询用户ID集合
     * @param param
     * @return
     * @author qinyuan.chen
     * @date 2016年12月28日
     */
    List<Integer> queryGroupUserByDisease(StatParam param);
}
