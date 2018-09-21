package com.dachen.health.pack.illhistory.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.illhistory.entity.po.DrugInfo;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryRecord;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.sdk.exception.HttpApiException;

import java.util.List;
import java.util.Map;

/**
 * Created by fuyongde on 2016/12/2.
 */
public interface IllHistoryInfoService {

    void fixOldData();

    /**
     * 获取病程的类型
     *
     * @return
     */
    Map<String, Object> getIllHistoryRecordTypes(String parentId);

    /**
     * 手动添加一条病程
     *
     * @param illHistoryInfoId      病历Id
     * @param doctorId              医生Id
     * @param illRecordTypeId       病程的类型id
     * @param illRecordTypeParentId 病程类型的父级id
     * @param illRecordTypeSource   病程类型的来源
     * @param illRecordTypeName     病程类型的名称
     * @param remark                备注信息
     * @param isSecret              是否允许患者查看(true保密不允许患者查看，false允许患者查看)
     * @param pics                  图片信息
     * @param time                  病程时间（区别于病程记录的创建时间）
     */
    void addIllHistoryRecord(String illHistoryInfoId, Integer doctorId, String illRecordTypeId,
                             String illRecordTypeParentId, Integer illRecordTypeSource, String illRecordTypeName,
                             String remark, Boolean isSecret, String[] pics, Long time);

    /**
     * 根据订单添加一个病程
     *
     * @param orderParam
     * @return
     */
    IllHistoryRecord addHistoryRecordFromOrderParam(OrderParam orderParam);

    /**
     * 医生编辑病历中的患者信息
     *
     * @param illHistoryInfoId 病历id
     * @param doctorId         医生id
     * @param patientId        患者id
     * @param remarkName       备注姓名
     * @param tags             标签
     * @param remark           备注
     * @param height           身高
     * @param weight           体重
     * @param marriage         婚姻
     * @param job              职业
     */
    void editPationInfo(String illHistoryInfoId, Integer doctorId, Integer patientId, String remarkName, String[] tags, String remark, String height,
                        String weight, String marriage, String job);

    /**
     * 编辑病情资料
     *
     * @param illHistoryInfoId 病历id
     * @param illDesc          主诉
     * @param pics             病情描述的图片
     * @param treatment        诊治情况
     */
    void editIllContentInfo(String illHistoryInfoId, String illDesc, List<String> pics, String treatment);

    /**
     * 添加初诊
     *
     * @param illHistoryInfoId 病历id
     * @param content          初步诊断内容
     */
    void addDiagnosis(String illHistoryInfoId, String content, String diseaseId);

    /**
     * 获取初步诊断列表
     *
     * @param illHistoryInfoId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageVO getDiagnosis(String illHistoryInfoId, Integer pageIndex, Integer pageSize);

    /**
     * 获取全部我的患者
     *
     * @return
     */
    Map<String, Object> getAllMyPatients();

    /**
     * 修复医患关系表
     */
    void fixDoctorPatient();

    /**
     * 获取病历信息
     *
     * @param doctorId         医生id
     * @param patientId        患者id
     * @param illHistoryInfoId 病历id
     * @return
     */
    Map<String, Object> getillHistoryInfo(Integer doctorId, Integer patientId, String illHistoryInfoId);

    /**
     * 分页获取病程信息
     *
     * @param doctorId         医生id
     * @param patientId        患者id
     * @param illHistoryInfoId 病历id
     * @param pageIndex        页码
     * @param pageSize         页面大小
     * @return
     */
    PageVO getillHistoryRecords(Integer doctorId, Integer patientId, String illHistoryInfoId, Integer pageIndex, Integer pageSize) throws HttpApiException;

    /**
     * 添加用药的病程
     *
     * @param illHistoryInfoId 病历的id
     * @param drugInfos        用药的信息（需要反序列化为对象）
     * @param pics             图片信息
     * @param drugCase         用药情况
     * @param time             用药时间
     */
    void addDrugCase(String illHistoryInfoId, String drugInfos, List<String> pics, String drugCase, Long time);

    /**
     * 添加用药的病程
     *
     * @param illHistoryInfoId 病历的id
     * @param drugInfos        用药信息
     * @param pics             图片信息
     * @param drugCase         用药情况
     * @param time
     */
    void addDrugCase(String illHistoryInfoId, List<DrugInfo> drugInfos, List<String> pics, String drugCase, Long time);

    /**
     * 往病程中添加一个用药信息，并发送IM消息
     *
     * @param drugInfos 用药信息
     * @param pics      图片信息
     * @param drugCase  用药情况
     * @param orderId   订单id
     * @param gid       会话组id
     */
    void addDrugCaseAndSendMsg(String drugInfos, List<String> pics, String drugCase, Integer orderId, String gid) throws HttpApiException;

    /**
     * 医生在IM中，给患者发送检查单
     *
     * @param gid              会话组id
     * @param orderId          订单id
     * @param checkupId        检查项id
     * @param checkupName      检查项名称
     * @param indicatorIds     指标的id
     * @param suggestCheckTime 建议检查时间
     * @param attention        注意事项
     */
    void sendCheckItem(String gid, Integer orderId, String checkupId, String checkupName, String[] indicatorIds, Long suggestCheckTime, String attention) throws HttpApiException;

    /**
     * 患者端添加检查项
     *
     * @param gid         会话组id
     * @param orderId     订单id
     * @param checkupId   检查项id
     * @param checkupName 检查项名称
     * @param checkTime   检查时间
     * @param result      检查结果
     * @param pics        图片信息
     * @param checkItemId 检查单的id
     */
    void addCheckItem(String gid, Integer orderId, String checkupId, String checkupName, Long checkTime, String result, String[] pics, String checkItemId) throws HttpApiException;

    /**
     * 患者通过IM右上角上传检查项
     *
     * @param gid         会话组id
     * @param orderId     订单id
     * @param checkupId   检查单id
     * @param checkupName 检查项名称
     * @param checkTime   检查时间
     * @param result      检查结果
     * @param pics        图片信息
     */
    void addCheckItemBySelf(String gid, Integer orderId, String checkupId, String checkupName, Long checkTime, String result, String[] pics) throws HttpApiException;

    /**
     * 更新简要病史
     *
     * @param history
     */
    void updateBriefHistroy(String id, String history);

    Map<String, Object> analysisIllHistoryInfo(String illHistoryInfoId);
}
