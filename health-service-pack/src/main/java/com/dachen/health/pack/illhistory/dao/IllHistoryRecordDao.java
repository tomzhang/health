package com.dachen.health.pack.illhistory.dao;

import com.dachen.health.pack.illhistory.entity.po.IllHistoryRecord;
import com.dachen.health.pack.illhistory.entity.po.IllRecordTypeUsed;

import java.util.List;

/**
 * Created by wangl on 2016/12/5.
 */
public interface IllHistoryRecordDao {


    IllHistoryRecord insert(IllHistoryRecord hr);

    IllHistoryRecord insertWithOutSetTime(IllHistoryRecord illHistoryRecord);

    IllHistoryRecord findByOrderIdAndType(Integer orderId, Integer type);

    void updateFromOrder(Integer orderId, String drugCase, List<String> drugGoodsIds, List<String> drugPicUrls, String hopeHelp, List<String> picUrls);

    /**
     * 更新病程的支付状态或者更新病程关联的IM的消息组
     *
     * @param orderId    订单id
     * @param isPay      是否支付
     * @param msgGroupId 会话组id
     */
    void updateRecordPayOrSessionGroup(Integer orderId, Boolean isPay, String msgGroupId);

    /**
     * 医生端分页获取病程
     *
     * @param illHistoryInfoId 病历id
     * @param doctorId         医生id
     * @param pageIndex        页码
     * @param pageSize         页面大小
     * @return
     */
    List<IllHistoryRecord> findByIllHistoryInfoIdForDoctor(String illHistoryInfoId, Integer doctorId, Integer pageIndex, Integer pageSize);

    /**
     * 获取医生端获取病程总数
     *
     * @param illHistoryInfoId 病历id
     * @param doctorId         医生id
     * @return
     */
    Long findByIllHistoryInfoIdForDoctorCount(String illHistoryInfoId, Integer doctorId);

    /**
     * 患者端分页获取病程
     *
     * @param illHistoryInfoId 病历id
     * @param pageIndex        页码
     * @param pageSize         页面大小
     * @return
     */
    List<IllHistoryRecord> findByIllHistoryInfoIdForPatient(String illHistoryInfoId, Integer pageIndex, Integer pageSize);

    /**
     * 患者端获取病程总数
     *
     * @param illHistoryInfoId
     * @return
     */
    Long findByIllHistoryInfoIdForPatient(String illHistoryInfoId);

    /**
     * 通过病历id查找最近健康关怀、订单病程列表
     *
     * @param illHistoryInfoId
     * @return
     */
    IllHistoryRecord findLastRecordByIllhistoryInfoId(String illHistoryInfoId);

    /**
     * 修复没有病历id的病程
     *
     * @param illHistoryRecordId 病程id
     * @param illHistoryInfoId   病历id
     */
    void updateIllHistoryInfoId(String illHistoryRecordId, String illHistoryInfoId);

    void fixCheckIn(String illHistoryRecordId, IllHistoryRecord illHistoryRecord);

    /**
     * 删除患者报道的脏数据
     */
    void removeDirtyCheckInData();

    /**
     * 删除健康关怀的脏数据
     */
    void removeDirtyCareData();

    void fixCareGroupDoctors(String illHistoryRecordId, List<Integer> groupDoctors);

    /**
     * 删除订单状态为已取消的订单的病程
     * @param illHistoryRecordId
     */
    void removeDirtyOrderStatus(String illHistoryRecordId);

    /**
     * 修复健康关怀类型的病程的诊治情况
     * @param illHistoryRecordId
     * @param treatCase
     */
    void fixRecordCareTreatCase(String illHistoryRecordId, String treatCase);

    /**
     * 修复订单类型的病程的诊治情况
     * @param illHistoryRecordId
     * @param treatCase
     */
    void fixRecordOrderTreatCase(String illHistoryRecordId, String treatCase);
}
