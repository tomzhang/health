package com.dachen.health.pack.messageGroup.Impl;

import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.careplan.api.entity.CCarePlan;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.health.pack.consult.Service.ConsultationFriendService;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.guide.entity.OrderCache;
import com.dachen.health.pack.guide.entity.ServiceStateEnum;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.illhistory.dao.PatientInfoDao;
import com.dachen.health.pack.messageGroup.IMessageGroupService;
import com.dachen.health.pack.messageGroup.MessageGroupEnum;
import com.dachen.health.pack.order.dao.IOrderSessionContainerDao;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderExt;
import com.dachen.health.pack.order.entity.po.OrderSessionContainer;
import com.dachen.health.pack.order.mapper.OrderExtMapper;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.mapper.CureRecordMapper;
import com.dachen.health.pack.patient.model.CureRecord;
import com.dachen.health.pack.patient.model.CureRecordExample;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.user.dao.ITagDao;
import com.dachen.im.server.constant.SysConstant;
import com.dachen.im.server.data.request.GroupStateRequestMessage;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.RedisUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2017/3/1.
 */
@Service
public class MessageGroupServiceImpl implements IMessageGroupService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IOrderSessionService orderSessionService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IGuideDAO iGuideDAO;

    @Autowired
    private IGuideService guideService;

    @Autowired
    UserManager userManager;

    @Autowired
    ConsultationFriendService consultationFriendService;

    @Autowired
    IBaseDataDao baseDataDao;

    @Autowired
    IOrderSessionContainerDao orderSessionContainerDao;

    @Autowired
    PatientInfoDao patientInfoDao;

    @Autowired
    JedisTemplate jedisTemplate;

    @Autowired
    IPatientService patientService;

    @Autowired
    ITagDao tagDao;

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    private IPackService packService;

    @Autowired
    private IGroupService groupService;
    @Autowired
    OrderExtMapper orderExtMapper;

    @Autowired
    CureRecordMapper cureRecordMapper;

    @Resource
    protected CarePlanApiClientProxy carePlanApiClientProxy;

    public final static String MESSAGE_REPLY_ORDER_COUNT = KeyBuilder.MESSAGE_REPLY_COUNT + ":order";

    @Override
    public void updateGroupBizState(String gid, Integer type) throws HttpApiException {
        GroupStateRequestMessage message = new GroupStateRequestMessage();

        OrderSession orderSession = orderSessionService.findOneByGroupId(gid);
        if (orderSession == null) {
            return;
        }
        Order order = orderService.getOne(orderSession.getOrderId());
        if (order == null) {
            return;
        }

        if (Objects.equals(type, MessageGroupEnum.FINISH_SERVICE.getIndex())) {

            OrderSession os = orderSessionService.findOneByOrderId(order.getId());
            if ((order.getAssistantId() != null) && StringUtils.isNotBlank(os.getAssistantDoctorGroupId()) &&
                    StringUtils.isNotBlank(os.getAssistantPatientGroupId())) {
                message.setBizStatus("17");
                //更新订单助手医生会话状态
                message.setGid(os.getAssistantDoctorGroupId());
                MsgHelper.updateGroupBizState(message);
                //更新订单助手患者会话状态
                message.setGid(os.getAssistantPatientGroupId());
                MsgHelper.updateGroupBizState(message);
            }

        } else if (Objects.equals(type, MessageGroupEnum.PAY_ORDER.getIndex())) {

            OrderSession os = orderSessionService.findOneByOrderId(order.getId());
            if ((order.getAssistantId() != null) && StringUtils.isNotBlank(os.getAssistantDoctorGroupId()) &&
                    StringUtils.isNotBlank(os.getAssistantPatientGroupId())) {
                message.setBizStatus("3");
                //更新订单助手医生会话状态
                message.setGid(os.getAssistantDoctorGroupId());
                MsgHelper.updateGroupBizState(message);
                //更新订单助手患者会话状态
                message.setGid(os.getAssistantPatientGroupId());
                MsgHelper.updateGroupBizState(message);
            }

        }

        Map<String, Object> bussiness = new HashMap<>();
        bussiness = getGroupParam(gid);

        message.setGid(gid);

        if (bussiness.containsKey("sessionStatus")) {
            String bizStatus = bussiness.get("sessionStatus").toString();
            message.setBizStatus(bizStatus);
            bussiness.remove("sessionStatus");
        } else {
            message.setBizStatus(order.getOrderStatus().toString());
        }

        message.setParams(bussiness);
        MsgHelper.updateGroupBizState(message);
    }

    @Override
    public Map<String, Object> getGroupParam(String gid) throws HttpApiException {

        Map<String, Object> bussiness = new HashMap<>();

        Map<String, Object> childOrder = new HashMap<>();
        /**
         * 2016-12-22 13:10:41
         * 会话容器判断
         */
        List<OrderSessionContainer> oss = orderSessionContainerDao.findByMsgGroupId(gid);
        boolean isCareFlag = false;
        OrderSessionContainer osc = null;
        OrderSessionContainer careOsc = null;
        if (oss != null) {
            /**针对健康关怀中的图文电话积分  客户端需要特殊处理**/
            if (oss.size() > 1) {
                for (OrderSessionContainer os : oss) {
                    if (os.getPackType() == PackEnum.PackType.careTemplate.getIndex()) {
                        isCareFlag = true;
                        careOsc = os;
                    } else
                        osc = os;
                }
            }
            if (oss.size() == 1)
                osc = oss.get(0);
        }
        if (isCareFlag) {
            getOrderRelevantNew(gid, bussiness, careOsc);
            getOrderRelevantNew(gid, childOrder, osc);
            if (childOrder.containsKey("needReplyRole"))
                bussiness.put("needReplyRole", childOrder.get("needReplyRole"));
        } else
            getOrderRelevantNew(gid, bussiness, osc);

        Map<String, Object> result = new HashMap<>();
        if (!bussiness.isEmpty()) {
            result.put("groupId", gid);
            if (bussiness.containsKey("sessionStatus")) {
                String bizStatus = bussiness.get("sessionStatus").toString();
                result.put("bizStatus", bizStatus);
            }
            result.put("param", bussiness);
        }
        if (!childOrder.isEmpty()) {
            bussiness.put("childOrder", childOrder);
        }
        return bussiness;
    }

    /**
     * 状态值	       状态定义	       		对应的订单状态
     * 15    	        服务中				3（已支付）
     * 16    	        服务超时				3（已支付）
     * 17    	        人工取消				5（已取消）
     * 18    	        后台自动取消			5（已取消）
     * 19    	        等待队列中			    3（已支付）--门诊订单
     * 20    	        咨询队列中			     3（已支付）--门诊订单
     * 21    	        等待接单				导医端使用 --电话咨询
     * 22    	        服务结束				导医端使用--电话咨询
     * 23	                未激活			    2、3（待支付或者已支付
     *
     * @param gid
     * @param bussiness
     * @param osc
     * @return
     */
    private Order getOrderRelevantNew(String gid, Map<String, Object> bussiness, OrderSessionContainer osc) throws HttpApiException {
        if (gid.startsWith(SysConstant.GUIDE_DOCTOR_GROUP_PREFIX)) {
            setGuideOrderRelevant(gid, bussiness);
            return null;
        }

        OrderSession orderSession;
        if (osc != null) {
            orderSession = orderSessionService.findByPk(osc.getOrderSessionId());
        } else {
            orderSession = orderSessionService.findOneByGroupId(gid);
        }

        if (orderSession == null) {
            return null;
        }
        Order order = orderService.getOne(orderSession.getOrderId());
        if (order == null) {
            return null;
        }
        bussiness.put("illHistoryInfoId", order.getIllHistoryInfoId());
//		Long waitNum=null;
        Integer sessionStatus = null;//用来标志是在等待队列中还是咨询队列中
        Integer timeLong = order.getTimeLong();
        Long serviceBeginTime = orderSession.getServiceBeginTime();
        long currentTime = System.currentTimeMillis();
        Long price = order.getPrice();
        Integer orderType = order.getOrderType();

        Patient p = patientService.findByPk(order.getPatientId());
        bussiness.put("patientName", p.getUserName());
        bussiness.put("patientId", p.getId());

        bussiness.put("orderId", order.getId());
        bussiness.put("orderType", orderType);
        bussiness.put("recordStatus", order.getRecordStatus());
        bussiness.put("price", price);
        if (order.getOrderType() == OrderEnum.OrderType.consultation.getIndex()) {
            boolean wrote = false;
            Pack pack = packService.getPack(order.getPackId());
            if (pack != null) {
                if (pack.getDoctorId().intValue() == ReqUtil.instance.getUserId()) {
                    CureRecordExample example = new CureRecordExample();
                    example.createCriteria().andOrderIdEqualTo(order.getId()).andDoctorIdEqualTo(ReqUtil.instance.getUserId());
                    List<CureRecord> cList = cureRecordMapper.selectByExample(example);
                    if (cList != null && cList.size() > 0) {
                        wrote = true;
                    }
                } else {
                    OrderExt orderExt = orderExtMapper.selectByDocAndOrderId(order.getId(), ReqUtil.instance.getUserId());
                    if (orderExt != null) {
                        wrote = true;
                    }
                }

                bussiness.put("mainDocId", pack.getDoctorId());
                bussiness.put("groupId", pack.getGroupId());
                if (StringUtil.isNoneBlank(pack.getGroupId())) {
                    Group group = groupService.getGroupById(pack.getGroupId());
                    if (group != null) {
                        bussiness.put("groupName", group.getName());
                    }
                }
            } else {
                OrderExt orderExt = orderExtMapper.selectByDocAndOrderId(order.getId(), ReqUtil.instance.getUserId());
                if (orderExt != null) {
                    wrote = true;
                }
            }
            bussiness.put("treatAdvise", wrote);
        }

        if (PackEnum.PackType.message.getIndex() == order.getPackType()) {
            bussiness.put("packType", "2");
        } else if (PackEnum.PackType.phone.getIndex() == order.getPackType()) {
            bussiness.put("packType", "3");
        } else if (PackEnum.PackType.integral.getIndex() == order.getPackType()) {
            bussiness.put("packType", order.getPackType() + "");
        }
        bussiness.put("orderCreatorId", order.getDoctorId());
        bussiness.put("remarks", order.getRemarks());
        bussiness.put("waitCount", 1);
        //
        bussiness.put("sessionStatus", order.getOrderStatus());

        if (StringUtils.isNotBlank(order.getHospitalId())) {
            HospitalVO vo = baseDataDao.getHospital(order.getHospitalId());
            bussiness.put("hospitalId", order.getHospitalId());
            if (vo != null) {
                bussiness.put("hospitalName", vo.getName());
            }
        }

        bussiness.put("timeLong", timeLong);//套餐时长,单位：分钟
        if (orderSession.getAppointTime() != null) {
            bussiness.put("appointTime", orderSession.getAppointTime());// 预约时间
        }
        if (serviceBeginTime != null) {
            bussiness.put("serviceBeginTime", serviceBeginTime);// 服务开始时间
        }
        //判断订单是否已支付超过48小时未处理
        if (order.getPayTime() != null) {
            if (order.getOrderStatus() == OrderEnum.OrderStatus.已支付.getIndex() && System.currentTimeMillis() >= order.getPayTime() + 48 * 60 * 60 * 1000) {
                bussiness.put("isOverPayTime48Hours", true);
            } else {
                bussiness.put("isOverPayTime48Hours", false);
            }
        }
        //服务中：15，服务超时：16，人工取消：17；医生助手取消：172;后台自动取消：18，等待队列中：19，咨询队列中：20，可以继续咨询 : 24
        if (order.getOrderStatus() == OrderEnum.OrderStatus.已取消.getIndex()) {
            Integer cancleFrom = order.getCancelFrom();
            bussiness.put("cancelFrom", cancleFrom);// 取消人
            if (cancleFrom != null && cancleFrom == 0) {
                sessionStatus = 18;
            } else {
                sessionStatus = 17;
                if (cancleFrom != null) {
                    try {
                        User user = userManager.getUser(cancleFrom);
                        if (user.getUserType() == UserEnum.UserType.DocGuide.getIndex()) {
                            sessionStatus = 172;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (StringUtil.isNotEmpty(order.getCancelReason())) {
                bussiness.put("cancelReason", order.getCancelReason());
            }
        } else if (order.getOrderStatus() == OrderEnum.OrderStatus.已完成.getIndex()) {
            if (serviceBeginTime != null) {
                long durationTime = orderSession.getServiceEndTime() - serviceBeginTime;
                bussiness.put("duraTime", durationTime / 60000);// 服务时长
            }
            sessionStatus = order.getOrderStatus();
            if (order.getOrderType() == OrderEnum.OrderType.care.getIndex()) {
                bussiness.put("nextCarePackId", order.getFollowTemplateId());
            }
        } else if (order.getOrderStatus() == OrderEnum.OrderStatus.已支付.getIndex()) {// 已经支付

            if (orderType == OrderEnum.OrderType.outPatient.getIndex()) {
                if (orderSession.getTreatBeginTime() != null) {
                    // 叫号开始时间
                    bussiness.put("treatBeginTime", orderSession.getTreatBeginTime());
                }
            }
            if (serviceBeginTime != null) {
                /**
                 * 健康关怀套餐,随访套餐
                 */
                if (order.getOrderType() == OrderEnum.OrderType.care.getIndex()
                        || order.getOrderType() == OrderEnum.OrderType.followUp.getIndex()) {
                    sessionStatus = 15;
                    if (order.getCareTemplateId() != null && order.getCareTemplateId().length() == 24) {
                        try {
                            CCarePlan carePlan = carePlanApiClientProxy.findById(order.getCareTemplateId());
                            if (carePlan != null && carePlan.getStatus() == 0) {
                                sessionStatus = 16;
                            }
                        } catch (HttpApiException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                } else if (order.getOrderType() == OrderEnum.OrderType.consultation.getIndex())
                    sessionStatus = 15;
                else if (order.getOrderType() == OrderEnum.OrderType.appointment.getIndex())
                    sessionStatus = 15;
                else if (order.getPackType() == PackEnum.PackType.message.getIndex())
                    sessionStatus = 15;
                else {
                    long duration = currentTime - serviceBeginTime;
                    long leftTime = (timeLong != null ? timeLong : 0) - duration / 60000;
                    if (leftTime == 0) {
                        leftTime = leftTime + 1;
                    }
                    sessionStatus = (leftTime < 0 ? 16 : 15);
                }
            }
            // 记录门诊订单时sessionStatus(19为等待中，20为咨询中)
            else if (orderType == OrderEnum.OrderType.outPatient.getIndex()) {
                long len = 0;
                // 查询需要得到当前等待人数，同时可查看出是否存等待队中
                String key = RedisUtil.generateKey(RedisUtil.WAITING_QUEUE, order.getDoctorId().toString());
                List<String> list = RedisUtil.lrange(key, 0, -1);
                if (list != null) {
                    len = list.size();
                    for (int i = 0; i < len; i++) {
                        String str = list.get(i);
                        if (str != null && order.getId() == Integer.parseInt(str)) {
                            // waitNum = (long)i;
                            // 记录sesssionStatus为等待中即19
                            sessionStatus = 19;
                            break;
                        }
                    }
                }
                // 当前订单不在等待中就去咨询队列中查找
                if (sessionStatus == null) {
                    key = RedisUtil.generateKey(RedisUtil.ADVISORY_QUEUE, order.getDoctorId().toString());
                    list = RedisUtil.lrange(key, 0, -1);
                    if (list != null) {
                        len = list.size();
                        for (int i = 0; i < len; i++) {
                            String str = list.get(i);
                            if (str != null && order.getId() == Integer.parseInt(str)) {
                                // 记录sesssionStatus为等待中即20
                                sessionStatus = 20;
                                break;
                            }
                        }
                    }
                }
            }
        }
        // 订单未激活，则置未激活状态
        if (order.getAcStatus() != 1) {
            sessionStatus = 23;
        }

        /****兼容老版本保留的代码****/
        if (order.getPackType() == PackEnum.PackType.message.getIndex() &&
                orderSession.getTotalReplyCount() != null &&
                orderSession.getReplidCount() != null) {
            int beUseCount = orderSession.getTotalReplyCount() - orderSession.getReplidCount();
            bussiness.put("beUseCount", beUseCount < 0 ? 0 : beUseCount);
            if (order.getOrderStatus() != OrderEnum.OrderStatus.已完成.getIndex()
                    && beUseCount <= 0)
                sessionStatus = 24;
            if (sessionStatus != null && sessionStatus == 16)
                sessionStatus = 15;
        }

        /**
         * add at 2016-12-12 15:25:39
         * 医生给患者的备注
         * 该谁来回复
         */
        if (osc != null &&
                osc.getTotalReplyCount() != null &&
                osc.getReplidCount() != null) {
            int beUseCount = osc.getTotalReplyCount() - osc.getReplidCount();
            bussiness.put("beUseCount", beUseCount < 0 ? 0 : beUseCount);
            if (sessionStatus != null && sessionStatus == 16 &&
                    order.getPackType() == PackEnum.PackType.message.getIndex())
                sessionStatus = 15;
        }
        /**医生给患者的备注**/
        DoctorPatient doctorPatient = tagDao.findByDoctorIdAndPatientId(order.getDoctorId(), order.getPatientId());
        if (doctorPatient != null) {
            bussiness.put("remarkName", doctorPatient.getRemarkName());
        }

        if (sessionStatus != null) {
            bussiness.put("sessionStatus", sessionStatus);
        }

        Integer statusFlag = (Integer) bussiness.get("sessionStatus");
        /**该医生或患者回复**/
        if (statusFlag != null &&
                statusFlag != 17 && statusFlag != 18 && statusFlag != 5) {
            if (order.getPackType() == PackEnum.PackType.integral.getIndex() ||
                    order.getPackType() == PackEnum.PackType.message.getIndex() ||
                    order.getPackType() == PackEnum.PackType.phone.getIndex() ||
                    order.getPackType() == PackEnum.PackType.checkin.getIndex() ||
                    order.getPackType() == PackEnum.PackType.careTemplate.getIndex()
                    ) {
                String messageSerial = jedisTemplate.hget(MESSAGE_REPLY_ORDER_COUNT, gid);
                if (StringUtils.isNotBlank(messageSerial)) {
                    char last = messageSerial.charAt(messageSerial.length() - 1);
                    // 0:患者，1：医生
                    // 患者回复之后，就该医生回复
                    if (last == '0')
                        bussiness.put("needReplyRole", 1);
                    else if (last == '1')
                        bussiness.put("needReplyRole", 0);
                } else {
                    if (order.getPackType() != PackEnum.PackType.checkin.getIndex() &&
                            order.getPackType() != PackEnum.PackType.careTemplate.getIndex())
                        bussiness.put("needReplyRole", 1);
                }
            }
        }

        /**
         * 会诊的待回复
         */
        if (order.getPackType() == PackEnum.PackType.consultation.getIndex()) {
            UserSession us = ReqUtil.instance.getUser();
            Integer userType = us.getUserType();
            Integer currUserId = us.getUserId();
            Integer orderStatus = order.getOrderStatus();
            Integer doctorId = order.getDoctorId();//小医生id
            if (orderStatus == OrderEnum.OrderStatus.待预约.getIndex() &&
                    doctorId.intValue() != currUserId &&
                    userType == UserEnum.UserType.doctor.getIndex())
                bussiness.put("needReplyRole", 1);

            if (orderStatus == OrderEnum.OrderStatus.待完善.getIndex() &&
                    doctorId.intValue() == currUserId)
                bussiness.put("needReplyRole", 1);

            if (orderStatus == OrderEnum.OrderStatus.已支付.getIndex()) {
                String messageSerial = jedisTemplate.hget(MESSAGE_REPLY_ORDER_COUNT, gid);
                if (StringUtils.isBlank(messageSerial)) {
                    if (userType == UserEnum.UserType.doctor.getIndex())
                        bussiness.put("needReplyRole", 1);
                } else {
                    char last = messageSerial.charAt(messageSerial.length() - 1);
                    // 0:患者，1：医生
                    // 患者回复之后，就该医生回复
                    if (last == '0')
                        bussiness.put("needReplyRole", 1);
                    else if (last == '1')
                        bussiness.put("needReplyRole", 0);
                }
            }
        }

        fillPatientInfo(order, bussiness);
        fillDoctorInfo(order.getDoctorId(), bussiness);

        if (StringUtils.isNotBlank(order.getExpectAppointmentIds())) {
            String str = baseDataDao.getExpectAppointmentsByIds(order.getExpectAppointmentIds());
            bussiness.put("expectAppointmentInfo", str);
        }
        return order;
    }

    //导医组
    private void setGuideOrderRelevant(String gid, Map<String, Object> bussiness) {
        OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(gid);
        if (orderCache == null) {
            return;
        }
        bussiness.put("orderId", orderCache.getId());
        UserSession currentUser = ReqUtil.instance.getUser();
        //判断当前用户是否是该订单对应的导医(如果不是，则返回状态：服务已经结束)
        boolean isPatient = false;
        if (currentUser != null) {
            int currentUserId = currentUser.getUserId();
            ConsultOrderPO order = iGuideDAO.getConsultOrderPO(orderCache.getId());
            int userId = order.getUserId();//患者Id
            if (currentUserId != userId) {
                Integer guideId = order.getGuideId();//导医Id
                //当前用户不是患者
                //导医Id为空，表示还未接单;当前用户是其他导医（非接单的导医）；这两种情况都显示服务结束；
                if (guideId == null || currentUserId != guideId.intValue()) {
                    //服务结束(导医会话)
                    bussiness.put("sessionStatus", 22);
                    return;
                }
            } else {
                isPatient = true;
            }
        }
        ServiceStateEnum state = orderCache.getState();
        if (state == null) {
            state = ServiceStateEnum.NO_START;
        }
        if (state.getValue() == ServiceStateEnum.NO_START.getValue()) {
            //等待接单(导医会话)
            bussiness.put("sessionStatus", 21);
        } else if (state.getValue() == ServiceStateEnum.SERVING.getValue()) {
            long currentTime = System.currentTimeMillis();
            long maxTime = guideService.getMaxServiceTime();//毫秒
            Long startTime = orderCache.getStartTime();
            long serviceTime = currentTime - startTime; //服务时间
//			long leftTime = 0;
            if (startTime != null && serviceTime > maxTime) {
                //服务中---服务超时
                bussiness.put("sessionStatus", 16);
//				leftTime = (serviceTime - maxTime)/60000;
            } else {
                //服务中
                bussiness.put("sessionStatus", 15);
//				leftTime = (maxTime - serviceTime)/60000;
            }
            bussiness.put("serviceBeginTime", startTime);
            bussiness.put("timeLong", maxTime / 60000);
        } else {
            //服务结束(导医会话)
            bussiness.put("sessionStatus", 22);
        }
    }

    private void fillPatientInfo(Order order, Map<String, Object> bussiness) {
        if (order == null)
            return;

        Patient p = patientService.findByPk(order.getPatientId());
        if (p == null)
            return;

        bussiness.put("patientName", p.getUserName());
        bussiness.put("patientAge", p.getAgeStr());
        bussiness.put("patientSex", p.getSex());
        bussiness.put("patientArea", p.getArea());

        User u = userManager.getUser(order.getUserId());
        if (u != null) {
            bussiness.put("userName", u.getName());
        }
    }

    private void fillDoctorInfo(Integer doctorId, Map<String, Object> bussiness) {
        if (doctorId == null)
            return;
        User u = userManager.getUser(doctorId);
        if (u == null)
            return;
        bussiness.put("doctorName", u.getName());
        bussiness.put("headPicFileName", u.getHeadPicFileName());
        if (u.getDoctor() != null) {
            bussiness.put("title", u.getDoctor().getTitle());
            bussiness.put("departments", u.getDoctor().getDepartments());
            String groupName = consultationFriendService.getMainGroupName(doctorId);
            bussiness.put("groupName", groupName);
        }
    }
}
