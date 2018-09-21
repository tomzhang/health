package com.dachen.health.pack.illhistory.dao;

import com.dachen.health.pack.illhistory.entity.po.IllContentInfo;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryInfo;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryRecord;

import java.util.List;

public interface IllHistoryInfoDao {

    /**
     * 创建一个电子病历
     */
    IllHistoryInfo createIllHistoryInfo(Integer doctorId, Integer userId, Integer patientId, String patientInfoId, Boolean isFirstTreat);

    /**
     * 添加一条电子病历
     *
     * @param illHistoryInfo
     * @return
     */
    IllHistoryInfo insert(IllHistoryInfo illHistoryInfo);

    /**
     * 为病历添加一个病情资料
     */
    void addIllContentInfo(IllHistoryInfo illHistoryInfo, String illDesc, String treatCase, List<String> pics);

    /**
     * 为病历添加一个初步诊断
     *
     * @param illHistoryInfoId 病历id
     * @param content          初步诊断的内容
     * @param diseaseId        疾病id
     * @param doctorId         医生id
     * @param orderId          订单id
     */
    void addDiagnosis(String illHistoryInfoId, String content, String diseaseId, Integer doctorId, Integer orderId);

    void updateDiagnosis(String illHistoryInfoId, String content, String diseaseId, Integer orderId);

    /**
     * 根据医生id和患者id取出一个病例
     *
     * @param doctorId  医生id
     * @param patientId 患者id
     * @return 病历
     */
    IllHistoryInfo getByDoctorIdAndPatientId(Integer doctorId, Integer patientId);

    /**
     * 根据患者id，获取患者病历列表
     * @param patientId
     * @return
     */
    List<IllHistoryInfo> getByPatientId(Integer patientId);

    /**
     * 根据医生id和患者id取出所有符合病例
     *
     * @param doctorId  医生id
     * @param patientId 患者id
     * @return 病历
     */
    List<IllHistoryInfo> getInfosByDoctorIdAndPatientId(Integer doctorId, Integer patientId);


    /**
     * 根据病历id查询病历
     *
     * @param illHistoryInfoId 病历id
     * @return 病历
     */
    IllHistoryInfo findById(String illHistoryInfoId);

    /**
     * 更新更新病历中的病情资料
     *
     * @param illHistoryInfoId 病历id
     * @param illContentInfo   病情资料
     */
    void updateIllContentInfo(String illHistoryInfoId, IllContentInfo illContentInfo);

    /**
     * 获取全部我的患者的病历
     *
     * @param doctorId   医生id
     * @param patientIds 患者ids
     * @return
     */
    List<IllHistoryInfo> getByMyPatientIds(Integer doctorId, List<Integer> patientIds);

    /**
     * 更新病历中的患者信息id
     *
     * @param illHistoryInfoId 病历id
     * @param patientInfoId    患者信息id
     */
    void updatePatientInfoId(String illHistoryInfoId, String patientInfoId);

    /**
     * 更新
     *
     * @param illHistoryInfo
     */
    void updateIllHistoryInfo(IllHistoryInfo illHistoryInfo);

    IllHistoryRecord findCareByOrderId(Integer orderId);

    IllHistoryInfo getById(String id);

    List<IllHistoryInfo> findAll();

    /**
     * 修复以null开头的简要病史
     * @param illHistoryInfoId
     * @param birefHistory
     */
    void fixStartWithNullData(String illHistoryInfoId, String birefHistory);

    /**
     * 删除没有医生id的数据
     * @param illHistoryInfoId
     */
    void removeNullDoctorId(String illHistoryInfoId);
}
