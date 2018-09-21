package com.dachen.health.commons.utils;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.constant.DownTaskEnum;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.checkbill.dao.XGCheckItemReqDao;
import com.dachen.health.checkbill.entity.po.ImageData;
import com.dachen.health.checkbill.entity.po.XGCheckItemReq;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.OnlineRecordRepository;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.entity.DownTask;
import com.dachen.health.commons.entity.OnlineRecord;
import com.dachen.health.commons.service.DownTaskService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.PlatformDoctor;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.group.service.IPlatformDoctorService;
import com.dachen.health.group.schedule.dao.IOfflineDao;
import com.dachen.health.group.schedule.entity.po.Offline;
import com.dachen.health.group.schedule.service.IOfflineService;
import com.dachen.health.pack.conference.entity.vo.CallRecordVO;
import com.dachen.health.pack.conference.service.ICallRecordService;
import com.dachen.health.pack.consult.timertask.ConsultationPackTask;
import com.dachen.health.pack.guide.dao.IConsultOrderDoctorDAO;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.guide.entity.ServiceStateEnum;
import com.dachen.health.pack.guide.entity.po.ConsultOrderDoctorPO;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.entity.po.OrderRelationPO;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.guide.util.GuideUtils;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.service.IOrderRefundService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mobsms.sdk.MobSmsSdk;
import com.squareup.okhttp.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Jesque定时任务执行业务处理类
 *
 * @author xieping
 */
public class JobTask {

    private static final Logger log = LoggerFactory.getLogger(JobTask.class);

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//    public static String XG_SERVER = PropertiesUtil.getContextProperty("xg.server"); //玄关接口访问地址

    @Resource
    protected IPackService packService;

    @Resource
    protected IOrderService orderService;

    @Resource
    protected ICallRecordService callRecordService;

    @Resource
    protected IOrderSessionService orderSessionService;

    @Resource
    protected IGroupService groupService;

    @Resource
    protected IGroupDoctorService gdocService;

    @Resource
    protected IPlatformDoctorService pdocService;

    @Resource
    protected IOrderRefundService orderRefundService;

    @Autowired
    protected IGuideDAO guideDao;

    @Autowired
    protected IGuideService guideService;

    @Autowired
    protected IConsultOrderDoctorDAO consultOrderDoctorDao;

    @Autowired
    protected DownTaskService downTaskService;

    @Autowired
    protected ConsultationPackTask consultationTask;

    @Autowired
    protected MobSmsSdk mobSmsSdk;

    @Autowired
    protected IBaseDataService baseDataService;

    @Autowired
    protected UserManager userManager;

    @Autowired
    protected IPatientService patientService;

    @Autowired
    protected IGuideDAO iGuideDAO;

    @Autowired
    protected IOfflineDao offlineDao;

    @Autowired
    protected IOfflineService offlineService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected XGCheckItemReqDao xgCheckItemReqDao;

    @Resource
    protected OnlineRecordRepository onlineRecordRepository;

    public void cancelOutpatient(Integer orderId) {
        Order order = orderService.getOne(orderId);
        if (order.getOrderType() != OrderType.outPatient.getIndex())
            return;
        OrderSession session = orderSessionService.findOneByOrderId(orderId);
        if (session.getServiceBeginTime() == null) {//三分钟后未开始服务，自动取消
            orderService.cancelAdvisoryOrderBySystem(orderId);
        }
        log.info("###############门诊订单3分钟后未开始服务，自动取消。订单ID：{}", orderId);
    }

    public void cancelNoAppointOrder(Integer orderId) throws HttpApiException {
        Order order = orderService.getOne(orderId);
        if (order.getOrderStatus() == OrderStatus.待预约.getIndex()) {
            orderService.cancelOrder(orderId, 0);
        }
        log.info("###############待预约订单48小时后未预约自动取消。订单ID：{}", orderId);
    }

    public void cancelNoPayOrder(Integer orderId) throws HttpApiException {
        Order order = orderService.getOne(orderId);
        if (order.getOrderStatus() == OrderStatus.待支付.getIndex()) {
            orderService.cancelOrder(orderId, 0);
        }
        log.info("###############待支付订单2小时后未支付自动取消。订单ID：{}", orderId);
    }

    public void cancelPaidOrder(Integer orderId) throws HttpApiException {
        OrderSession session = orderSessionService.findOneByOrderId(orderId);
        if (session.getServiceBeginTime() == null) {
            orderService.cancelOrder(orderId, 0);
        }
        log.info("###############已支付订单48小时后未开始服务自动取消。订单ID：{}", orderId);
    }

    public void remindConsultation(Integer orderId) {
        consultationTask.remindConsultation(1, orderSessionService.findOneByOrderId(orderId), orderId);
        log.info("###############会诊提前30分钟发送短信提醒。订单ID：{}", orderId);
    }

    public void remindMessage(Integer orderId) {
        OrderSession session = orderSessionService.findOneByOrderId(orderId);
        if (session.getServiceBeginTime() == null) {
            Order order = orderService.getOne(orderId);
            String patName = userManager.getUser(order.getUserId()).getName();
            User docUser = userManager.getUser(order.getDoctorId());
            //mobSmsSdk.send(docUser.getTelephone(), MessageFormat.format(SMSUtil.SMS_24HOURS, docUser.getName(), patName));
            final String msg = baseDataService.toContent("1013", docUser.getName(), patName);
            mobSmsSdk.send(docUser.getTelephone(), msg);
        }
        log.info("###############图文2小时后未开始服务发送短信提醒。订单ID：{}", orderId);
    }

    public void groupOffline(String gdocId) throws HttpApiException {
        GroupDoctor gdoc = new GroupDoctor();
        gdoc.setId(gdocId);
        gdocService.doctorOffline(gdoc, EventEnum.DOCTOR_OFFLINE_SYSTEM_FORCE);
        log.info("###############医生上线2小时后系统强制下线。集团医生ID：{}", gdocId);
    }

    public void platformOffline(String pdocId) throws HttpApiException {
        PlatformDoctor pdoc = new PlatformDoctor();
        pdoc.setId(new ObjectId(pdocId));
        pdocService.doctorOffline(pdoc, EventEnum.DOCTOR_OFFLINE_SYSTEM_FORCE);
        log.info("###############医生上线2小时后系统强制下线。平台医生ID：{}", pdocId);
    }

    public void offlineTimeout(String groupId) {
        String tag = "offlineTimeout";
        Group group = groupService.getGroupById(groupId);
        if (null == group) {
            log.error("{}. group不存在，应该取消此循环定时任务. groupId={}", tag, groupId);
            return;
        }

        try {
            if (groupService.isWithinDutyTime(groupId)) {
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }

        List<GroupDoctor> gdocList = gdocService.findGroupDoctor(null, groupId, GroupDoctorStatus.正在使用.getIndex());
        for (GroupDoctor gdoc : gdocList) {
            try {
                OnlineRecord record = onlineRecordRepository.findLastOneByGroupDoctor(gdoc.getId());
                if (record == null) {
                    continue;
                }
                if (gdoc.getOnLineState() != null && !"1".equals(gdoc.getOnLineState())) {
                    continue;
                }
                gdocService.doctorOffline(gdoc, EventEnum.DOCTOR_OFFLINE_SYSTEM_FORCE);
            } catch (ServiceException e) {
                log.error(e.getMessage() + "###集团医生ID：" + gdoc.getId());
                continue;
            } catch (Exception e) {
                log.error("###############医生上线超出集团设置的值班时间。集团医生ID：" + gdoc.getId(), e);
            }
        }
//        log.info("###############医生上线超出集团设置的值班时间。集团ID：{}", groupId);
    }

    public void queryWXRefundResult(Integer refundId) {
        orderRefundService.autoQueryWechat(refundId);
        log.info("###############1分钟后再次查询微信退款结果。退款记录ID：{}", refundId);
    }

    public void endService4Guide(String consultOrderId) throws HttpApiException {
        ConsultOrderPO order = guideDao.getConsultOrderPO(consultOrderId);
        Pack pack = packService.getPack(order.getPackId());
        if (order.getState() == ServiceStateEnum.SERVING.getValue() && PackType.phone.getIndex() == pack.getPackType()
                && PackEnum.PackStatus.open.getIndex() == pack.getStatus()) {
            guideService.endServiceAuto(consultOrderId);
        }
        log.info("###############患者电话咨询订单超过24小时未关闭-自动结束服务。导医订单ID：{}", consultOrderId);
    }

    public void endService4NoPay(String consultOrderDoctorId) throws HttpApiException {
        ConsultOrderDoctorPO doctorPO = consultOrderDoctorDao.getConsultOrderDoctor(consultOrderDoctorId);
        ConsultOrderPO order = guideDao.getConsultOrderPO(doctorPO.getComsultOrderId());
        Pack pack = packService.getPack(order.getPackId());
        if (OrderStatus.待支付.getIndex() == doctorPO.getStatus() && PackType.phone.getIndex() == pack.getPackType()
                && PackEnum.PackStatus.open.getIndex() == pack.getStatus()) {
            guideService.endServiceAuto(order.getId());
        }
        log.info("###############预约到医生2小时未支付-自动结束服务。导医订单ID：{}", order.getId());
    }

    /**
     * @param recordId
     * @param loopCount 循环次数
     */
    public void endStopRecord(String recordId, Integer loopCount) {
        DownTask task = downTaskService.getDownTaskByRecordId(recordId);
        downTaskService.downAndUpdate(task);
        if (task.getStatus() < DownTaskEnum.DownStatus.recordDownSuccess.getIndex() && loopCount > 0) {
            endStopRecord(recordId, loopCount--);
        }
        log.info("###############下载更新录音：{}", recordId);
    }

    /**
     * 预约名医患者短信提醒
     *
     * @param userName
     * @param telPhone
     */
    public void sendAppointment(String userName, String telPhone) {
        String content = baseDataService.toContent("0051", userName, OrderEnum.OrderType.appointment.getTitle());
        mobSmsSdk.send(telPhone, content);
        log.info("###############预约名医患者提前两个小时短信通知。患者电话号码：{}", telPhone);

    }

    /**
     * @param orderId 订单id
     */
    public void autoSendMsgToGuider(Integer orderId) {
        /*OrderSession session= orderSessionService.findOneByOrderId(orderId);
        Integer orderid = session.getOrderId();*/
        Order order = orderService.getOne(orderId);
        Patient patient = patientService.findByPk(order.getPatientId());
        if (patient == null) {
            throw new ServiceException("找不到患者！");
        }
        OrderRelationPO po = iGuideDAO.getGuideIdByOrderId(order.getId());
        User doctor = userManager.getUser(order.getDoctorId());
        String content = baseDataService.toContent("0902", doctor.getName(), patient.getUserName(), GuideUtils.convertDate2Str(po.getAppointStartTime(), po.getAppointEndTime()));
        ConsultOrderPO poOrder = iGuideDAO.getConsultOrderPO(po.getGuideOrderId());
        User guideInfo = userManager.getUser(poOrder.getGuideId());
        String signature = null;
        if (mobSmsSdk.isBDJL(doctor)) {
            signature = BaseConstants.BD_SIGN;
        } else {
            signature = BaseConstants.XG_SIGN;
        }
        mobSmsSdk.send(guideInfo.getTelephone(), content, signature);
    }

    /**
     * @param orderId 订单id
     */
    public void autoSendMsgToGuiderOfHalfHour(Integer orderId) {
        /*OrderSession session= orderSessionService.findOneByOrderId(orderId);
        Integer orderid = session.getOrderId();*/

        List<CallRecordVO> list = callRecordService.getRecordByOrderId(orderId);
        //只给没有拨打电话的导医发送短信
        if (list == null || list.size() == 0) {
            Order order = orderService.getOne(orderId);
            OrderRelationPO po = iGuideDAO.getGuideIdByOrderId(order.getId());
            String content = baseDataService.toContent("0903", GuideUtils.convertDate2Str(po.getAppointStartTime(), po.getAppointEndTime()));
            ConsultOrderPO poOrder = iGuideDAO.getConsultOrderPO(po.getGuideOrderId());
            User user = userManager.getUser(poOrder.getGuideId());
            //根据Order信息去反查doctor，然后在去判断该导医属于哪个平台的（博德嘉联、玄关健康）
            User doctor = userManager.getUser(order.getDoctorId());
            String signature = null;
            if (mobSmsSdk.isBDJL(doctor)) {
                signature = BaseConstants.BD_SIGN;
            } else {
                signature = BaseConstants.XG_SIGN;
            }
            mobSmsSdk.send(user.getTelephone(), content, signature);
        }
    }

    /**
     * @param orderId
     */
    public void autoFinishAfterBeginService(Integer orderId) throws HttpApiException {
        orderSessionService.finishService(orderId, 2);
    }

    /**
     *
     */
    public void updateDoctorOfflineItemEveryDay() {
        long last = System.currentTimeMillis() - DateUtil.daymillSeconds;
        long week12After = last + DateUtil.week12millSeconds;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(week12After);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week == 0) {
            week = 7;
        }
        List<Offline> dataList = offlineDao.getByWeek(week);
        Calendar calStart = Calendar.getInstance();
        for (Offline o : dataList) {
            Long start = o.getStartTime();
            Long end = o.getEndTime();
            if (end != null && start != null) {
                Long itemSize = (end - start) / DateUtil.minute30millSeconds;
                calStart.setTimeInMillis(start);
                calStart.set(Calendar.YEAR, cal.get(Calendar.YEAR));
                calStart.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                calStart.set(Calendar.DATE, cal.get(Calendar.DATE));
                offlineService.addOneDayOfflineItem(o, calStart.getTimeInMillis(), itemSize.intValue());
            }
        }
    }

    /**
     * @param orderId
     */
    public void autoFinishAppointmentOrder(Integer orderId) throws HttpApiException {
        orderSessionService.finishService(orderId, 1);
    }


    public void cancelNoAgreeOrNoPayAppointmentOrder(Integer orderId, Integer userType) throws HttpApiException {
        Order o = orderService.getOne(orderId);
        Integer orderStatus = o.getOrderStatus();
        if (userType == UserType.doctor.getIndex()) {
            if (orderStatus == OrderStatus.待预约.getIndex()) {
                orderService.cancelOrder(orderId, 0);
            }
        } else if (userType == UserType.patient.getIndex()) {
            if (orderStatus == OrderStatus.待支付.getIndex()) {
                orderService.cancelOrder(orderId, 0);
            }
        }
    }

    public void doctorAssistantSetAppointTimeSendMsg(String phone, String content) {
        mobSmsSdk.send(phone, content);
    }


    public void notifyWhenReplyCountEqZero(Integer orderId) {
        Order o = orderService.getOne(orderId);
        OrderSession os = orderSessionService.findOneByOrderId(orderId);
        Integer status = o.getOrderStatus();
        Integer count = os.getTotalReplyCount() - os.getReplidCount();
        Long time = os.getLastModifyTime();
        if (OrderStatus.已完成.getIndex() != status
                && OrderStatus.已取消.getIndex() != status
                && count == 0
                && System.currentTimeMillis() - time >= 2 * 60 * 60 * 1000) {
            //发送短信给医生
            User doctor = userManager.getUser(o.getDoctorId());
            String signature = null;
            if (mobSmsSdk.isBDJL(doctor)) {
                signature = BaseConstants.BD_SIGN;
            } else {
                signature = BaseConstants.XG_SIGN;
            }
            User d = userManager.getUser(o.getDoctorId());
            User u = userManager.getUser(o.getUserId());
            String content = baseDataService.toContent("1046", d.getName(), u.getName());
            mobSmsSdk.send(d.getTelephone(), content, signature);
        }
    }

    public void finishWhenReplyCountEqZero(Integer orderId) throws HttpApiException {
        Order o = orderService.getOne(orderId);
        OrderSession os = orderSessionService.findOneByOrderId(orderId);
        Integer status = o.getOrderStatus();
        Integer count = os.getTotalReplyCount() - os.getReplidCount();
        Long time = os.getLastModifyTime();
        if (OrderStatus.已完成.getIndex() != status
                && OrderStatus.已取消.getIndex() != status
                && count == 0
                && System.currentTimeMillis() - time >= 5 * 60 * 60 * 1000) {
            orderSessionService.finishService(orderId, 2);
        }
    }

    public void cancelOutpatientNoParpare(Integer orderId) throws HttpApiException {
        Order order = orderService.getOne(orderId);
        if (order.getOrderType() != OrderType.outPatient.getIndex())
            return;
        OrderSession session = orderSessionService.findOneByOrderId(orderId);
        if (session.getTreatBeginTime() == null &&
                order.getOrderStatus() == OrderStatus.已支付.getIndex()) {//凌晨医生还没有接单
            orderService.cancelOrder(orderId, 1);
            log.info("###############晚上凌晨医生还没有开始接单，订单自动取消。订单ID：{}", orderId);
        }
    }

    public void reSendImagesToXG(String xGCheckItemReqId) {
        XGCheckItemReq xgCheckItemReq = xgCheckItemReqDao.findById(xGCheckItemReqId);
        Gson gson = new Gson();

        Integer patientId = xgCheckItemReq.getPatientId();
        String patientName = xgCheckItemReq.getPatientName();
        Integer sex = xgCheckItemReq.getSex();
        String hospitalId = xgCheckItemReq.getHospitalId();
        String medicalHistoryUrl = xgCheckItemReq.getMedicalHistoryUrl();
        String checkUpId = xgCheckItemReq.getCheckUpId();
        String phone = xgCheckItemReq.getPhone();
        String address = xgCheckItemReq.getAddress();

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("PatientID", patientId);
        paramMap.put("Sex", (sex != null && sex.intValue() == 1) ? "M" : "F");
        paramMap.put("Name", patientName);
        paramMap.put("HospitalID", hospitalId);
        paramMap.put("Phone", phone);
        paramMap.put("Address", address);
        paramMap.put("MedicalHistoryUrl", medicalHistoryUrl);

        List<String> images = xgCheckItemReq.getImages();

        List<ImageData> imageDatas = Lists.newArrayList();
        images.forEach(a -> {
            ImageData imageData = new ImageData();
            imageData.setInspection(checkUpId);
            imageData.setImageUrl(a);
            imageDatas.add(imageData);
        });
        paramMap.put("Images", imageDatas);

        String requestParam = gson.toJson(paramMap);

        OkHttpClient client = new OkHttpClient();

        String url = PropertiesUtil.getContextProperty("xg.server") + "/Service/api/AppSync/UploadAppData";
        RequestBody body = RequestBody.create(JSON, requestParam);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            Map<String, Object> result = JSONMessage.parseObject(response.body().string(), Map.class);

            String message = (String) result.get("Message");
            String code = (String) result.get("Code");
            Boolean success = false;
            if (StringUtils.equals("1", code)) {
                success = true;
            }

            xgCheckItemReq.setSuccess(success);
            xgCheckItemReq.setResponseMessage(message);
            xgCheckItemReqDao.update(xgCheckItemReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
