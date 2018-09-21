package com.dachen.health.pack.stat.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.pack.stat.entity.param.PatientStatParam;
import com.dachen.health.pack.stat.entity.vo.PatientStatVO;
import com.dachen.util.tree.ExtTreeNode;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IPatientStatService<br>
 * Description：患者统计service <br>
 * 
 * @author fanp
 * @createTime 2015年9月22日
 * @version 1.0.0
 */
public interface IPatientStatService {

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
    PageVO getUserByDiseaseAndDoctor(PatientStatParam param);
    
    /**
     * </p>获取诊疗记录,通过患者和医生获取</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月22日
     */
    List<PatientStatVO> getCureRecordByDoctor(PatientStatParam param);
    
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
    List<String> getDiseaseByDoctorAndGroup(List<Integer> doctorIds, String groupId);
    
    /**
     * 统计  集团 患者库
     * @param param
     * @param id
     * @return
     *@author wangqiao
     *@date 2015年12月30日
     */
    PageVO statGroupPatient(StatParam param, String id);
    
    List<Map<String, Object>> patientRegions(String groupId);
    
    List<DiseaseTypeVO> patientDiseases(String groupId);
    
    PageVO patientInfos(String groupId, String province, String city, String[] diseaseTypeId, Integer pageIndex, Integer pageSize);
    
    Integer patientCount(String groupId);

    /**
     * 统计集团患者列表
     * @param param
     * @return
     * @author qinyuan.chen
     * @date 2016年12月28日
     */
    PageVO queryGroupPatient(StatParam param, String id);
}
