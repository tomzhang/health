package com.dachen.health.pack.order.service;

import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.OrderEnum.OrderNoitfyType;
import com.dachen.health.commons.vo.CarePlanDoctorVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.order.entity.param.CareOrderParam;
import com.dachen.health.pack.order.entity.param.CheckOrderParam;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderExt;
import com.dachen.health.pack.order.entity.po.OrderSessionContainer;
import com.dachen.health.pack.order.entity.vo.*;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.entity.vo.PackDoctorVO;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.im.server.data.request.CreateGroupRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.sdk.exception.HttpApiException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IOrderService<br>
 * Description： 订单service<br>
 *
 * @author fanp
 * @version 1.0.0
 * @createTime 2015年8月10日
 */
public interface IOrderService {

    /**
     * </p>新建订单,返回客户端调用第三方接口参数</p>
     *
     * @param param
     * @param session
     * @author fanp
     * @date 2015年8月10日
     */
    PreOrderVO add(OrderParam param, UserSession session) throws HttpApiException;

    /**
     * </p>追加订单</p>
     *
     * @param param
     * @author fanp
     * @date 2015年8月10日
     */
    void addOrder(OrderParam param);


    /**
     * </p>查询订单</p>
     *
     * @param param
     * @author peiX
     * @date 2015年8月17日
     */
    Order getOne(Integer orderId);

    List<Order> findOrderByIds(List<Integer> ids);

    Order getOneByCarePlanId(String carePlanId) throws ServiceException;

    List<Order> findByCarePlanIds(List<String> carePlanIds) throws ServiceException;


    /**
     * </p>查询订单</p>
     *
     * @param param
     * @author peiX
     * @date 2015年8月17日
     */
    Order findOrderBydiseaseId(Integer id);

    /**
     * </p>修改订单</p>
     *
     * @param param
     * @author peiX
     * @date 2015年8月17日
     */
    void updateOrder(Order order);

    /**
     * 填写咨询记录时修改集团Id
     *
     * @param orderId
     * @param groupId
     */
    void updateGroupId(Integer orderId, String groupId);

    /**
     * 修改订单状态
     *
     * @param param
     */
    void updateOrderStatus(OrderParam param);

    Integer getOrderStatus(OrderParam param);


    List<OrderVO> getOrders(OrderParam param);

    /**
     * </p>查询订单状态,返回成功</p>
     *
     * @param param
     * @author peiX
     * @date 2015年8月19日
     */
    String checkOrderStatus(CheckOrderParam param, UserSession session);

    /**
     * </p>查询订单状态数据</p>
     *
     * @param param
     * @param session
     * @return
     */
    PageVO findOrders(OrderParam param, UserSession session);

    PageVO findPaidOrders(OrderParam param);

    /**
     * </p>查看订单详情</p>
     *
     * @param orderId
     * @param userId
     * @return
     * @author fanp
     * @date 2015年9月8日
     */
    OrderDetailVO detail(OrderParam param, UserSession session) throws HttpApiException;

    /**
     * </p>支付订单,返回客户端调用第三方接口参数</p>
     *
     * @param param
     * @param session
     * @author xiepei
     * @date 2015年8月10日
     */
    PreOrderVO addPayOrder(OrderParam param, UserSession session) throws HttpApiException;

    PreOrderVO addPayOrder(OrderParam param, UserSession session, boolean isBDJL) throws HttpApiException;

    /**
     * </p>reate session</p>
     *
     * @param param
     * @return
     */
    OrderSessionVO addSession(OrderParam param, UserSession userSession);

    /**
     * </p>count new order record by userId and orderStatus</p>
     *
     * @param param
     * @return
     */
    Integer countNewOrder(Integer doctorId);

    void sendOrderNoitfy(String userIds, String doctorId, String gid, OrderNoitfyType orderNoitfyType, Map<String, Object> paramMap);

    void sendOrderNoitfy(String userIds, String doctorId, String gid, OrderNoitfyType orderNoitfyType, Map<String, Object> paramMap, Boolean isByPatient);

    void sendNotitfy(String title, String content, String userId) throws HttpApiException;

    void sendCareNotify(Pack pack, Order order, OrderSession orderSession, User docUserInfo, User user);

    OrderDetailVO findOrderDisease(Integer orderId) throws HttpApiException;

    DoctorVO findDoctorVo(Integer doctorId);

    /**
     * 检查并自动关闭订单
     *
     * @date 2015年9月16日
     */
    void checkAndAutoClose() throws HttpApiException;

    void cancelOrder(Integer orderId, Integer cancelType) throws HttpApiException;

    void cancelOrder(OrderParam param, String pwd, Integer cancelType) throws HttpApiException;

    void cancelOrder(OrderParam param, Integer from, Integer cancelType) throws HttpApiException;

    void cancelOrder(Integer orderId, Integer from, Integer cancelType) throws HttpApiException;

    void cancelOrder(Order order, Integer from, Integer cancelType);

    void addJesQueTask(Order order);

    void cancelThroughTrainOrder(Integer orderId);

    Integer nextOrderNo();

    void add(Order order);


    /**
     * 获取医生服务中的订单
     *
     * @param doctorId 医生ID
     * @return 返回订单ID列表
     */
    List<Integer> getServingOrder(Integer doctorId);

    /**
     * </p>查询订单状态数据</p>
     *
     * @param param
     * @param session
     * @return
     */
    Object findOrdersGroupByDay(OrderParam param, UserSession session);


    OrderSessionVO addPreChargeSession(OrderParam param, CreateGroupRequestMessage createGroupParam) throws HttpApiException;

    PreOrderVO addPreCharge(OrderParam param, UserSession session) throws HttpApiException;


    List<OrderKeyInfoVO> getOrderKeyInfoByOrderId(String... ids) throws HttpApiException;

    void cancelAdvisoryOrderBySystem(Integer orderId);

    PreOrderVO getOrderByDocIdAndUserId(OrderParam param);

    GroupInfo createGroup(Order order, OrderSession orderSession) throws HttpApiException;

    List<OrderSession> getOverTimeOrderSession();

    GroupInfo createGroupMore(Order order, OrderSession orderSession) throws HttpApiException;

    /**
     * 医生添加关怀计划待支付订单
     *
     * @param param
     * @return
     */
    Order addCareOrder(OrderParam param) throws HttpApiException;

    Order addFeeBill(Order order);

    Order addThroughTrainOrder(OrderParam param);

    /**
     * 生成会诊订单
     *
     * @param illCaseId
     * @param conDoctorId
     * @return
     */
    Order addConsultation(String illCaseId, Integer conDoctorId) throws HttpApiException;

    /**
     * 生成会诊订单
     *
     * @param illCaseId
     * @param consultationId
     * @return
     */
    Order addNewConsultation(String illCaseId, String consultationId) throws HttpApiException;

    /**
     * 生成会诊新订单
     *
     * @param illCaseId
     * @param conDoctorId
     * @return
     */
    Order addNewConsultation(String illCaseId, Integer conDoctorId) throws HttpApiException;

    /**
     * 完善病历重新提交会诊订单
     *
     * @param orderId
     */
    void resubmitConsultation(Integer orderId) throws HttpApiException;

    /**
     * 大医生接受会诊
     *
     * @param param
     */
    void confirmConsultation(OrderParam param, Integer conDoctorId) throws HttpApiException;

    /**
     * 患者填写预约时间
     *
     * @param orderId
     * @param expectAppointmentIds
     */
    void patientExpectedTime(Integer orderId, String expectAppointmentIds) throws HttpApiException;

    /**
     * 大医生取消会诊
     *
     * @param orderId
     */
    void cancelConsultation(Integer orderId, Integer reason, Integer conDoctorId) throws HttpApiException;

    void updateRemarks(Integer orderId, String remarks);

    String getRemarks(Integer orderId);

    PageVO getOrderByRecordStatus(OrderParam param);

    void updateRecordStatus(Integer orderId);

    List<OrderVO> findOrderSchedule(OrderParam param);

    PreOrderVO sumbitCarePlanOrder(CareOrderParam param) throws HttpApiException;

    List<Order> findOrdersByUserId(Integer userId, Integer acStatus);

    void updateOrderByUserId(Integer userId, Integer acStatus);

    /**
     * 获取图文，电话，报道过期订单
     *
     * @param param
     * @return
     */
    List<OrderVO> findExpiredOrder(OrderParam param) throws HttpApiException;

    /**
     * 修改订单信息
     *
     * @param param 2016年1月19日17:02:21
     *              作者：姜宏杰
     * @return
     */
    ConsultOrderPO modifyOrder(OrderParam param);

    List<Integer> getAllPaidConsultationOrderIds();

    Integer getConsultationRoomNumber(Integer orderId);

    Object getPatientsGid(Integer doctorId, String userIds, String patientIds);

    Set<Integer> findOrderIdByIllCaseInfoId(String illCaseInfoId);

    PageVO getConsultationRecordList(String enterType, Integer doctorId, Integer pageIndex, Integer pageSize);


    /**
     * 根据传入的订单id集合去查询记录
     *
     * @param param
     * @return
     */
    List<OrderVO> findByIdsMap(OrderParam param);

    /**
     * 根据传入的订单id集合去查询记录条数
     *
     * @param param
     * @return
     */
    Integer findByIdsMapCount(OrderParam param);

    /**
     * 查询到已处理过的订单
     *
     * @param param
     * @return
     */
    List<OrderVO> getGuideAlreadyServicedOrder(OrderParam param);

    /**
     * 查询到已处理过的订单数
     *
     * @param param
     * @return
     */
    Integer getGuideAlreadyServicedOrderCount(OrderParam param);


    OrderDetailVersion2 orderDetail(Integer orderId);

    Integer hasIllCase(Integer orderId);

    List<Integer> getOrderDoctorIds(Integer orderId);

    void updateCareCorder(OrderParam param);

    String getGroupNameByOrderId(Integer orderId);

    Object consultationMember(Integer orderId, Integer roleType);

    void finishCareOrderAsync(Order order);

    Integer getSpecialistDoctorId(Order order);

    PageVO getAppointmentOrders(Integer status, Integer pageIndex, Integer pageSize);

    /**
     * 多条件查询
     *
     * @param param
     * @return
     */
    PageVO getOrderListByMC(OrderParam param);

    List<OrderVO> getOrderListToDown(OrderParam param);

    void sendDoctorCardInfo(Integer doctorId, String msgId) throws HttpApiException;

    void sendIllCaseCardInfo(Integer orderId, String illCaseInfoId, String msgId, String fromUserId) throws HttpApiException;

    /**
     * 暴露出来给电话订单使用
     *
     * @param userId
     * @param illCaseInfoId
     * @param msgId
     * @param mainCase
     * @param name
     * @param ageStr
     * @param sex
     */
    void sendIllCaseCardInfo(String userId, String illCaseInfoId, String msgId, String mainCase, String name,
                             String ageStr, String sex) throws HttpApiException;

    PageVO transferRecords(Integer transferRecordType, Integer pageIndex, Integer pageSize);

    List<OrderVO> getAppointmentListByCondition(OrderParam param);

    Object getAppointmentPaidOrders(String groupId, String hospitalId, Long start, Long end);

    PageVO searchAppointmentOrder4Guide(List<Integer> patientIds, Integer pageIndex, Integer pageSize);

    Integer getHaveAppointmentListByDate(OrderParam param);

    Long getHaveAppointmentListByDateByMongo(OrderParam param);

    List<OrderVO> getAppointmentListByConditionByMongo(OrderParam param);

    void refuseAppointOrder(Integer orderId) throws HttpApiException;

    void updateRemark(Integer orderId, String remark, String isSend);

    Object getAppointmentOrder4H5(Integer orderId);

    PreOrderVO processCreateOrder(OrderParam param) throws HttpApiException;

    void sendDoctorAssistantOrderNotify(String assistantId, String doctorId, String assistantDoctorGroupId,
                                        OrderNoitfyType orderNoitfyType, Map<String, Object> paramMap) throws HttpApiException;

    void sendPatientAssistantOrderNotify(String userId, String assistantId, String assistantPatientGroupId,
                                         OrderNoitfyType orderNoitfyType, Map<String, Object> paramMap) throws HttpApiException;

    //------------------------------------------------医生助手电话订单功能修改---------------------------------------------

    /**
     * @throws
     * @Title: queryDoctors
     * @Description: 根据医生助手ID查询对应医生
     * @param: @param userId
     * @param: @return
     * @return: List<Doctor>
     * @author: qinyuan.chen
     * @date: 2016年7月26日 下午3:19:00
     */
    List<User> queryDoctors(Integer userId);

    /**
     * @throws
     * @Title: queryOrderByConditions
     * @Description: 医生助手根据条件查询订单
     * @param: @param param
     * @param: @return
     * @return: PageVO
     * @author: qinyuan.chen
     * @date: 2016年7月26日 下午3:58:51
     */
    PageVO queryOrderByConditions(OrderParam param);

    /**
     * @throws
     * @Title: setConsultationTime
     * @Description: 填写患者咨询时间
     * @param: @param orderId
     * @param: @param appointTime
     * @return: void
     * @author: qinyuan.chen
     * @date: 2016年7月27日 下午2:18:24
     */
    void setConsultationTime(Integer orderId, Long appointTime) throws HttpApiException;

    /**
     * 更新订单助手备注
     *
     * @param orderId
     * @param assistantComment
     */
    void updateOrderAssistantComment(Integer orderId, String assistantComment);

    /**
     * @throws
     * @Title: cancelOrder
     * @Description: 取消订单
     * @param: @param msg
     * @param: @param orderId
     * @param: @param cancelType
     * @return: void
     * @author: qinyuan.chen
     * @date: 2016年7月27日 下午2:19:19
     */
    void cancelOrder(String msg, Integer orderId, Integer cancelType) throws HttpApiException;

    /**
     * @throws
     * @Title: queryOrderByConditions
     * @Description: 集团平台与运营后台，多条件查询订单列表
     * @param: @param param
     * @param: @param user
     * @param: @return
     * @return: String
     * @author: qinyuan.chen
     * @date: 2016年8月18日 下午3:26:39
     */
    PageVO queryOrderByConditions(OrderParam param, UserSession user);

    /**
     * 根据订单id查询是否有病情，有病情则返回病情id
     *
     * @param orderId
     * @return
     */
    Map<String, Object> getIllCase(Integer orderId);

    boolean beginService4FreePlanImmediately(Integer orderId) throws HttpApiException;

    List<PackDoctorVO> getPackDoctorListByOrder(Integer orderId);

    Object getOldSession(Integer doctorId, Integer userId, Integer patientId, Integer packId, Integer careOrderId, Integer pakcType, Integer orderId);

    /**
     * 运营平台或管理后台查看订单详情
     *
     * @param orderId
     * @return
     */
    OrderSimpleInfoVO getOrderSimpleInfo(Integer orderId) throws HttpApiException;

    PreOrderVO processNewOrder(OrderParam param);

    OrderSessionContainer processOrderSession(Order order, Pack pack, Integer careOrderId) throws HttpApiException;

    PreOrderVO updateNewOrder(OrderParam param) throws HttpApiException;

    void sendNewOrderIllCard(OrderParam param) throws HttpApiException;

    void addHistoryRecord(OrderParam param, Order order);

    PreOrderVO saveSendPayOrderSingleByNotice3(Integer userType, Integer doctorUserId, Integer patientId, Integer packId) throws HttpApiException;

    void saveRecommendCarePack(Integer orderId, Integer followPackId);

    void saveSendFeeBill(Pack pack, List<Integer> userIds) throws HttpApiException;

    FeeBillDetail queryFeeBillByOrder(Integer orderId);

    OrderVO doctorAssistantQueryOrderById(Integer orderId);

    PageVO queryOrderByConditionsForPatient(OrderParam param);


    PageVO queryOrderByConditionsForDoctor(OrderParam param);

    void wrapPatients(List<PreOrderVO> list);

    List<CarePlanDoctorVO> findDoctorInfoGroup(Integer orderId, List<Integer> docIds);

    void sendBeginIMMsg(Integer orderId, String patientMsg, String doctorMsg) throws HttpApiException;


    List<OrderExt> getTreatAdviseList(Integer orderId);

    List<Map> getTreatAdviseAndDocInfoList(Integer orderId);

    void updateTreatAdvise(Integer orderId, String treatAdvise) throws HttpApiException;

    String getTreatAdvise(Integer orderId);

    Map<String, Object> getOrderTypeByGId(String gid);
}
