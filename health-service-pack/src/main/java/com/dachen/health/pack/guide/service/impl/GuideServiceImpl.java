package com.dachen.health.pack.guide.service.impl;

import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.careplan.api.entity.CHelpRecord;
import com.dachen.careplan.api.entity.CIllnessAnswerSheet;
import com.dachen.careplan.api.entity.CWarningRecord;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.lock.RedisLock;
import com.dachen.commons.lock.RedisLock.LockType;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.dao.IBaseUserDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.commons.constants.*;
import com.dachen.health.commons.constants.OrderEnum.OrderNoitfyType;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.constants.OrderEnum.OrderType;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.constants.ScheduleEnum.OfflineDateFrom;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.fee.dao.IFeeDao;
import com.dachen.health.group.fee.entity.param.FeeParam;
import com.dachen.health.group.fee.entity.vo.FeeVO;
import com.dachen.health.group.fee.service.IFeeService;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IGroupProfitDao;
import com.dachen.health.group.group.entity.param.GroupConfigAndFeeParam;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupConfig;
import com.dachen.health.group.group.entity.vo.GroupDoctorVO;
import com.dachen.health.group.group.entity.vo.HospitalInfo;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.schedule.dao.IOfflineDao;
import com.dachen.health.group.schedule.entity.param.OfflineParam;
import com.dachen.health.group.schedule.entity.po.Offline;
import com.dachen.health.group.schedule.entity.po.OfflineItem;
import com.dachen.health.group.schedule.service.IOfflineService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.pack.conference.dao.CallRecordRepository;
import com.dachen.health.pack.conference.entity.vo.CallRecordVO;
import com.dachen.health.pack.conference.service.ICallRecordService;
import com.dachen.health.pack.consult.Service.ElectronicIllCaseService;
import com.dachen.health.pack.consult.dao.ElectronicIllCaseDao;
import com.dachen.health.pack.consult.entity.po.IllCaseInfo;
import com.dachen.health.pack.evaluate.service.IEvaluationService;
import com.dachen.health.pack.guide.dao.*;
import com.dachen.health.pack.guide.entity.OrderCache;
import com.dachen.health.pack.guide.entity.ServiceStateEnum;
import com.dachen.health.pack.guide.entity.param.ConsultOrderParam;
import com.dachen.health.pack.guide.entity.po.*;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease;
import com.dachen.health.pack.guide.entity.po.DoctorTimePO.Remark;
import com.dachen.health.pack.guide.entity.po.DoctorTimePO.Time;
import com.dachen.health.pack.guide.entity.vo.*;
import com.dachen.health.pack.guide.entity.vo.OrderDiseaseVO.PatientVO;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.guide.util.GuideMsgHelper;
import com.dachen.health.pack.guide.util.GuideUtils;
import com.dachen.health.pack.invite.service.IInvitePatientService;
import com.dachen.health.pack.messageGroup.Impl.MessageGroupServiceImpl;
import com.dachen.health.pack.messageGroup.MessageGroupEnum;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.entity.po.OrderDoctorExample;
import com.dachen.health.pack.order.entity.vo.AppointmentOrderWebParams;
import com.dachen.health.pack.order.entity.vo.OrderVO;
import com.dachen.health.pack.order.entity.vo.PreOrderVO;
import com.dachen.health.pack.order.mapper.OrderDoctorMapper;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.mapper.CureRecordMapper;
import com.dachen.health.pack.patient.mapper.OrderSessionMapper;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.*;
import com.dachen.health.pack.patient.service.*;
import com.dachen.health.pack.schedule.entity.vo.ScheduleRecordVO;
import com.dachen.health.pack.schedule.service.IScheduleService;
import com.dachen.health.user.service.IDoctorService;
import com.dachen.im.server.constant.SysConstant;
import com.dachen.im.server.constant.SysConstant.SysUserEnum;
import com.dachen.im.server.data.EventVO;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.request.CreateGroupRequestMessage;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.data.response.SendMsgResult;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.im.server.enums.GroupTypeEnum;
import com.dachen.im.server.enums.MsgTypeEnum;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.*;
import com.google.common.collect.Maps;
import com.mobsms.sdk.MobSmsSdk;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;

@Service
public class GuideServiceImpl extends NoSqlRepository implements IGuideService {
    private final static Logger logger = LoggerFactory.getLogger(GuideServiceImpl.class);

    @Autowired
    private IPatientService patientService;

    @Autowired
    private CallRecordRepository callRecordRepository;

    @Autowired
    MobSmsSdk mobSmsSdk;

    @Autowired
    IGroupService groupService;

    @Autowired
    IBaseDataService baseDataService;

    @Resource
    PatientMapper patientMapper;

    @Autowired
    private IConsultOrderDoctorDAO consultOrderDoctorDAO;

    @Autowired
    private IDoctorService doctorService;

    @Autowired
    private PackMapper packMapper;

    @Autowired
    private IBusinessServiceMsg businessServiceMsg;

    @Autowired
    private IPackService packService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderSessionService orderSessionService;

    @Autowired
    private UserManager userManager;

    @Autowired
    private ICallResultService callResultService;
    @Autowired
    private ICureRecordService cureRecordService;

    @Autowired
    private ElectronicIllCaseDao illCaseDao;

    @Autowired
    IFeeDao feeDao;

    @Autowired
    IGroupDoctorService gdService;

    @Autowired
    private IGroupDoctorDao gdocDao;

    @Autowired
    private IGuideDAO iGuideDAO;

    @Autowired
    private IDoctorTimeDAO iDoctorTimeDAO;

    @Autowired
    private IBaseDataDao baseDataDao;

    @Resource
    CureRecordMapper mapper;

    @Autowired
    private IBaseUserDao baseUserDao;

    @Autowired
    private IMsgService msgService;

    @Autowired
    private IDiseaseService diseaseService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IGroupDao groupDao;

    @Resource(name = "jedisTemplate")
    protected JedisTemplate jedisTemplate;

    @Autowired
    private ICallRecordService callRecordService;

    @Resource
    IOrderSessionService orderSessionSevice;

    @Resource
    OrderSessionMapper orderSessionMapper;

    @Resource
    IConsultOrderOtherDao consultOrderOtherDao;

    @Autowired
    ElectronicIllCaseService electronicIllCaseService;

    @Autowired
    OrderDoctorMapper orderDoctorMapper;

    @Autowired
    IGroupProfitDao groupProfitDao;

    @Autowired
    IImageDataService imageDataService;

    @Autowired
    IDiseaseService mysqlDiseaseService;
    @Autowired
    private IOfflineDao offlineDao;

    @Autowired
    IOfflineService offlineService;

    @Autowired
    private IEvaluationService evaluationService;

    @Autowired
    IInvitePatientService invitePatientService;

    @Autowired
    private IFeeService feeService;

    @Autowired
    IScheduleService scheduleService;

    @Autowired
    private MessageGroupServiceImpl messageGroupService;

    /**
     * 被服务的患者池
     */
    public final static String PATIENT_MESSAGE_TO_GUIDE_POOL_BE_SERVICED = KeyBuilder.Z_PATIENT_MESSAGE_TO_GUIDE_POOL + ":beServiced";

    /**
     * 已接单池
     */
    public final static String PATIENT_MESSAGE_TO_GUIDE_POOL_SERVICING = KeyBuilder.Z_PATIENT_MESSAGE_TO_GUIDE_POOL + ":guide_";

    public Object existServingOrder(UserSession session) {
        if (session == null) {
            throw new ServiceException("请登录");
        }
        if (session.getUserType() == null
                || session.getUserType() != UserEnum.UserType.patient
                .getIndex()) {
            throw new ServiceException("您不是患者，无法购买服务");
        }
        // 1、检查该患者是否存在服务中或者未开始服务的订单
        if (iGuideDAO.exist(session.getUserId())) {
            String gid = SysConstant.GUIDE_DOCTOR_GROUP_PREFIX
                    + session.getUserId();
            GroupInfoRequestMessage request = new GroupInfoRequestMessage();
            request.setGid(gid);
            request.setUserId("" + session.getUserId());
            Object groupInfo = null;
            try {
                groupInfo = msgService.getGroupInfo(request);
            } catch (ServiceException e) {
                ConsultOrderPO order = iGuideDAO.getOrderByGroup(gid);
                String guideId = null;
                if (order.getGuideId() != null) {
                    guideId = String.valueOf(order.getGuideId());
                }
                Map<String, Object> groupParam = new HashMap<String, Object>();
                groupParam.put("orderId", order.getId());
                groupParam.put("orderType", OrderEnum.OrderType.order.getIndex());
                groupParam.put("packType", "3");
                groupInfo = msgService.createGroup("" + session.getUserId(), guideId, groupParam);
            } catch (HttpApiException e) {
                e.printStackTrace();
            }

            return groupInfo;
        }
        return null;
    }

    /**
     * 患者下单（创建咨询订单）
     *
     * @param session 患者用户信息
     */
    public GroupInfo createConsultOrder(ConsultOrderParam param,
                                        UserSession session) throws HttpApiException {
        if (session == null) {
            throw new ServiceException("请登录");
        }
        if (session.getUserType() == null
                || session.getUserType() != UserEnum.UserType.patient
                .getIndex()) {
            throw new ServiceException("您不是患者，无法购买服务");
        }

        if (param.getDoctorId() == null) {
            throw new ServiceException("参数错误：未选择医生");
        }
        if (param.getPackId() == null) {
            throw new ServiceException("请正确选择套餐");
        }

        // 查询套餐信息，判断该套餐是否属于该医生
        Pack pack = packService.getPack(param.getPackId());
        if (pack == null) {
            throw new ServiceException("请正确选择套餐");
        }
        if (pack.getDoctorId().intValue() != param.getDoctorId().intValue()) {
            throw new ServiceException("套餐有误，请正确选择套餐");
        }
        // 1、检查该患者是否存在服务中或者未开始服务的订单
        if (iGuideDAO.exist(session.getUserId())) {
            throw new ServiceException(60101, "你还有未结束服务的咨询订单。");
        }

        String id = com.dachen.util.StringUtils.getRandomString2(12);
        // 2、创建会话（患者的客户组）:系统发送消息
        Map<String, Object> groupParam = new HashMap<String, Object>();
        groupParam.put("orderId", id);
        groupParam.put("orderType", OrderEnum.OrderType.order.getIndex());
        groupParam.put("packType", "3");
        GroupInfo groupInfo = (GroupInfo) msgService.createGroup(
                String.valueOf(session.getUserId()), null, groupParam);
        if (groupInfo == null) {
            throw new ServiceException("会话组创建失败");
        }
        // 3、增加ConsultOrder（状态：服务未开始）
        ConsultOrderPO orderPO = new ConsultOrderPO();
        orderPO.setId(id);
        orderPO.setDoctorId(param.getDoctorId());
        orderPO.setPackId(param.getPackId());
        orderPO.setUserId(session.getUserId());
        orderPO.setState(ServiceStateEnum.NO_START.getValue());
        orderPO.setGroupId(groupInfo.getGid());

        /**
         * 博德嘉联版本专用
         */
        String bdjlGroupId = groupService.isOpenSelfGuideAndGetGroupId(param.getDoctorId(), UserType.doctor.getIndex());
        if (StringUtils.isNotBlank(bdjlGroupId)) {
            orderPO.setGroupUnionId(bdjlGroupId);
        }

        Disease diseaseInfo = null;
        if (param.getPreOrderId() != null) {
            /**
             * 生成新纪录的时候已同步
             * 当再次修改的时候注意同步
             */
            diseaseInfo = getDiseaseByPreOrderOrIllCase(param);
            orderPO.setPreOrderId(param.getPreOrderId());
            orderPO.setTransferDoctorId(param.getTransferDoctorId());
            orderPO.setTransferTime(param.getTransferTime());
        } else if (StringUtil.isNotEmpty(param.getIllCaseInfoId())) {
            IllCaseInfo caseInfo = illCaseDao.getIllCase(param.getIllCaseInfoId());
            if (caseInfo == null) {
                throw new ServiceException("caseInfo is null");
            }
            diseaseInfo = getDiseaseByPreOrderOrIllCase(param);
        } else {
            diseaseInfo = new Disease();
            diseaseInfo.setPatientId(param.getPatientId());
            diseaseInfo.setDiseaseDesc(param.getDiseaseDesc());
            /*** begin add by liwei 2016年1月21日 ********/
            if (StringUtils.isNotEmpty(param.getSeeDoctorMsg())) {
                diseaseInfo.setSeeDoctorMsg(param.getSeeDoctorMsg());
            }
            diseaseInfo.setIsSeeDoctor(param.getIsSeeDoctor());
            /*** end add by liwei 2016年1月21日 ********/

            // 存储全路径，先注释
            // diseaseInfo.setVoice(param.getVoice());
            diseaseInfo.setTelephone(param.getTelephone());
            if (param.getImagePaths() != null) {
                List<String> diseaseImgs = new ArrayList<String>();
                for (String imgPath : param.getImagePaths()) {
                    diseaseImgs.add(imgPath);
                }
                this.addDialogueImg("", diseaseImgs.toArray((new String[diseaseImgs.size()])), id);
                diseaseInfo.setDiseaseImgs(diseaseImgs);
            }
        }
        orderPO.setDiseaseInfo(diseaseInfo);

        iGuideDAO.addConsultOrder(orderPO);
        logger.info("咨询订单创建成功。订单Id={0}", id);
        // 记录预约医生信息
        // 添加信息的时候的先去记录表里查询看当前这个医生有没有在
        ConsultOrderDoctorPO po = consultOrderDoctorDAO.getOrderByUserById(
                param.getDoctorId(), id);
        if (po == null) {
            ConsultOrderDoctorPO orderDoctorPO = new ConsultOrderDoctorPO();
            orderDoctorPO.setDoctorId(String.valueOf(param.getDoctorId()));
            orderDoctorPO.setComsultOrderId(id);
            orderDoctorPO.setCreateTime(System.currentTimeMillis());
            orderDoctorPO.setStatus(8);
            consultOrderDoctorDAO.addConsultOrder(orderDoctorPO);
        }
        // 3.1、系统代替患者发送消息
        // User user = userManager.getUser(session.getUserId());
        SendMsgResult result = sendCreateConsultMessage(param.getPatientId(), session, groupInfo, diseaseInfo);

        ////modify by 姜宏杰 患者自己也要看到
        sendConsultOrderCard(param, pack, groupInfo);

        /**
         * 更新该订单对应的会话中的首条消息，用于客户端定位
         */
        Map<String, Object> updateValue = new HashMap<String, Object>();
        updateValue.put("msgId", result.getMid());
        orderPO = iGuideDAO.updateConsultOrder(id, updateValue);
        logger.info("更新ConsultOrderPO成功:msgId={0}", result.getMid());
        return groupInfo;
    }

    private void sendConsultOrderCard(ConsultOrderParam param, Pack pack, GroupInfo groupInfo) throws HttpApiException {
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle(PackEnum.PackType.getTitle(pack.getPackType()));
        User docUser = userManager.getUser(param.getDoctorId());
        imgTextMsg.setContent(docUser.getName());
        StringBuffer remark = new StringBuffer();
        String groupName = getDocGroupName(param.getDoctorId());
//		groupName="默认集团";
        //groupName为空则将医院信息传入
        if (StringUtil.isNotEmpty(groupName)) {
            remark.append(groupName).append("|");//
        } else {
            //将医院名称传递
            HospitalVO hospitalVO = baseDataDao.getHospital(docUser.getDoctor().getHospitalId());
            remark.append(hospitalVO.getName()).append("|");//
        }
        remark.append(docUser.getDoctor().getTitle() + "-"
                + docUser.getDoctor().getDepartments());
        imgTextMsg.setRemark(remark.toString());
        imgTextMsg.setPic(docUser.getHeadPicFileName());
        imgTextMsg.setAction("详情");
        imgTextMsg.setPrice("￥"
                + (pack.getPrice() == null ? 0 : pack.getPrice() / 100) + "/"
                + pack.getTimeLimit() + "分钟");
        imgTextMsg.setContent(docUser.getName() + "|" + docUser.getDoctor().getTitle());
        imgTextMsg.setFooter("查看详情");
        imgTextMsg.setStyle(10);
        // bizType =22表示预约医生， bizId表示医生Id
        Map<String, Object> param_b = new HashMap<String, Object>();
        param_b.put("bizType", 22);
        param_b.put("bizId", param.getDoctorId());
        imgTextMsg.setParam(param_b);
        GuideMsgHelper.getInstance().sendImgTextMsg(
                groupInfo.getGid(), String.valueOf(param.getUserId()),
                String.valueOf(param.getPatientId()), imgTextMsg, false);
    }

    private SendMsgResult sendCreateConsultMessage(int patientId, UserSession session, GroupInfo groupInfo,
                                                   Disease diseaseInfo) throws HttpApiException {
        Patient patient = patientService.findByPk(patientId);
        StringBuffer content = new StringBuffer();
        content.append(diseaseInfo.getDiseaseDesc());// (性别，年龄，电话)
        content.append("(");
        content.append(patient.getUserName()).append("，");
        if (patient.getSex() != null) {
            if (patient.getSex() == 1) {
                content.append("男，");
            } else if (patient.getSex() == 2) {
                content.append("女，");
            }
        }
        String ageStr = patient.getAgeStr();
        if (StringUtils.isNotEmpty(ageStr)) {
            content.append(ageStr).append("，");
        }
        content.append(diseaseInfo.getTelephone());
        content.append(")");
        SendMsgResult result = GuideMsgHelper.getInstance().sendMsg(
                MsgTypeEnum.TEXT, groupInfo.getGid(), session.getUserId(),
                null, content.toString(), false);
        logger.info("消息发送成功。msgContent={0}", content.toString());
        return result;
    }

    /**
     * 设置病情
     *
     * @param param
     * @return
     */
    private Disease getDiseaseByPreOrderOrIllCase(ConsultOrderParam param) throws HttpApiException {
        /**
         * 1、根据原订单的病情 重新生成电话订单病情
         * 2、根据illcaseInfoId 从新生成电话订单病情
         */
        Disease d = new Disease();
        Integer orderId = param.getPreOrderId();
        String illcaseInfoId = param.getIllCaseInfoId();
        com.dachen.health.pack.patient.model.Disease dis = null;
        if (orderId != null) {
            Order o = orderMapper.getOne(orderId);
            Integer diseaseId = o.getDiseaseId();
            illcaseInfoId = o.getIllCaseInfoId();
            param.setPatientId(o.getPatientId());

            dis = mysqlDiseaseService.findByPk(diseaseId);
            d.setVoice(dis.getVoice());
        }
        d.setPatientId(param.getPatientId());
        d.setIllCaseInfoId(illcaseInfoId);
        electronicIllCaseService.setDiseaseInfo(illcaseInfoId, d);
        if (dis != null) d.setTelephone(dis.getTelephone());
        return d;
    }


    /**
     * 导医接单
     *
     * @param id      :咨询订单的id
     * @param session
     */
    public Object receivingOrder(String id, UserSession session) throws HttpApiException {
        if (session.getUserType() != UserEnum.UserType.DocGuide.getIndex()) {
            throw new ServiceException("接单失败：您不是导医，不能接单。");
        }
        Integer guideId = session.getUserId();
        // 检查导医当前正在服务的订单数量
        /*
         * long count = iGuideDAO.count(guideId); if(count>=3) { throw new
		 * ServiceException("接单失败：当前正在咨询的订单不能超过3个"); }
		 */
        // 1、导医加入会话组:系统发送消息

        RedisLock lock = new RedisLock();
        boolean locked = lock.lock(id, LockType.order);
        if (!locked) {
            throw new ServiceException("接单失败：该订单已被锁定。");
        }
        ConsultOrderPO order = iGuideDAO.getConsultOrderPO(id);
        if (order.getState() == ServiceStateEnum.SERVING.getValue()) {
            throw new ServiceException("接单失败：该订单已经被接过了。");
        }
        String userId = String.valueOf(order.getUserId());
        String groupId = null;
        GroupInfo groupInfo = null;
        try {
            // 1、创建会话（导医加入会话组）
            Map<String, Object> groupParam = new HashMap<String, Object>();
            groupParam.put("orderId", order.getId());
            groupParam.put("orderType", OrderEnum.OrderType.order.getIndex());
            groupParam.put("packType", "3");
            groupInfo = (GroupInfo) msgService.createGroup(userId, String.valueOf(guideId), groupParam);
            if (groupInfo == null) {
                throw new ServiceException("导医加入会话组失败");
            }
            groupId = groupInfo.getGid();
            logger.info("导医接单，成功加入会话组，gid={0}", groupId);
            // 2、更改订单状态，开始服务时间---服务中;反写导医字段
            Map<String, Object> updateValue = new HashMap<String, Object>();
            updateValue.put("state", ServiceStateEnum.SERVING.getValue());
            updateValue.put("guideId", guideId);
            updateValue.put("startTime", System.currentTimeMillis());
            order = iGuideDAO.updateConsultOrder(id, updateValue);

            // 24小时未结束服务则自动结束
            JobTaskUtil.endService4Guide(order.getId());

            logger.info("导医接单，ConsultOrderPO信息更新成功，orderId={0}", order.getId());

            jedisTemplate.zrem(GuideDAOImpl.GUIDE_ORDER_POOL, id);

            lock.unlock(id, LockType.order);

            logger.info("导医接单成功，订单Id={0},导医Id={1},患者Id={2}", id, guideId,
                    userId);
        } catch (Exception e) {
            lock.unlock(id, LockType.order);
            throw new ServiceException("导医接单失败:订单Id" + id + ",导医Id=" + guideId
                    + ",患者Id=" + userId);
        }
        // 3.1、系统发送消息:
        GuideMsgHelper.getInstance().sendRemindMsg(order.getGroupId(), "咨询开始",
                false, null);
        logger.info("导医接单消息提醒发送成功：咨询开始。");

        // 3.2、系统发送消息:由系统发送给导医医生名片信息
        int doctorId = order.getDoctorId();
        User docUser = userManager.getUser(doctorId);
        Pack pack = packService.getPack(order.getPackId());
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle(PackEnum.PackType.getTitle(pack.getPackType()));
        imgTextMsg.setPrice("￥"
                + (pack.getPrice() == null ? 0 : pack.getPrice() / 100) + "/"
                + pack.getTimeLimit() + "分钟");
        imgTextMsg.setContent(docUser.getName());
        StringBuffer remark = new StringBuffer();
        String groupName = getDocGroupName(doctorId);
        if (groupName != null) {
            remark.append(groupName).append("|");//
        }
        remark.append(docUser.getDoctor().getTitle() + "-"
                + docUser.getDoctor().getDepartments());
        imgTextMsg.setRemark(remark.toString());
        imgTextMsg.setPic(docUser.getHeadPicFileName());
        imgTextMsg.setFooter("查看详情");
        // imgTextMsg.setFooter("电话咨询 ￥200/10分钟");
        imgTextMsg.setStyle(10);
        // bizType =5表示预约医生， bizId表示医生Id
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("bizType", 5);
        param.put("bizId", doctorId);
        imgTextMsg.setParam(param);
        SendMsgResult result = GuideMsgHelper.getInstance().sendImgTextMsg(
                groupId, SysUserEnum.SYS_001.getUserId(),
                String.valueOf(guideId), imgTextMsg, false);
        logger.info("导医接单：系统发送给导医医生名片成功，msgId={0}", result.getMid());
        // 3.3、系统代替导医发送消息:回复
        String content = "您好，很高兴为您服务，接下来我需要尽可能多的帮助医生收集您的病情信息，以便医生更好的为您服务。谢谢！";
        result = GuideMsgHelper.getInstance().sendMsg(MsgTypeEnum.TEXT,
                groupId, guideId, null, content, true);
        logger.info("导医接单：系统给患者自动回复，msgId={0}", result.getMid());

        // 对于导医来说，该会话的组名称和组图片显示患者的名称和头像
        if (groupInfo != null) {
            UserSession user = ReqUtil.instance.getUser(order.getUserId());
            if (user != null) {
                groupInfo.setGname(user.getName());
                groupInfo.setGpic(user.getHeadImgPath());
            }
        }
        return groupInfo;
    }

    public Object endService(String id) throws HttpApiException {
        ConsultOrderPO order = iGuideDAO.getConsultOrderPO(id);
        if (order.getState() == ServiceStateEnum.SERVCE_END.getValue()) {
            throw new ServiceException("该订单服务已经结束。");
        }
        // String id = orderCache.getId();
        Integer currentUserId = ReqUtil.instance.getUserId();
        // 1、结束服务
        Map<String, Object> updateValue = new HashMap<String, Object>();
        updateValue.put("state", ServiceStateEnum.SERVCE_END.getValue());
        updateValue.put("closedTime", System.currentTimeMillis());
        updateValue.put("closedUserId", currentUserId);
        order = iGuideDAO.updateConsultOrder(id, updateValue);
        logger.info("结束服务，ConsultOrderPO信息更新成功，orderId={0}", order.getId());
        try {
            jedisTemplate.zrem(GuideDAOImpl.GUIDE_ORDER_POOL, id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Integer guideId = order.getGuideId();
        String content = null;
        if (guideId == null) {

            content = "您放弃了本次咨询";//
        } else {
            Map<String, String> msgReplaceMap = new HashMap<>();
            ;
            String remindContent = "您结束了本次咨询";
            if (currentUserId != null
                    && currentUserId.intValue() == order.getUserId()) {
                msgReplaceMap.put("" + guideId, "患者结束了本次咨询");
            } else {
                msgReplaceMap.put("" + order.getUserId(), "医生助手结束了本次咨询");
            }
            content = "以上是由导医" + guideId + "服务";// 需要改为导医编号 TODO
            this.sendEvent(guideId, order.getGroupId(), id);
            Map<String, Object> param = new HashMap<>();
            if (msgReplaceMap != null) {
                param.put(SysConstant.PARAM_MSG_REPLACE, msgReplaceMap);
            }
            GuideMsgHelper.getInstance().sendRemindMsg(order.getGroupId(),
                    remindContent, false, param);
        }

        // 2 发送提醒消息
        logger.info("{0}结束服务，提醒消息成功--本次咨询结束。", order.getId());
        // 3 发送(分割线)
        GuideMsgHelper.getInstance().sendLineMsg(order.getGroupId(), content,
                false, null);
        return null;
    }

    /**
     * 订单超时，自动结束
     *
     * @return
     */
    public Object endServiceAuto(String id) throws HttpApiException {
        ConsultOrderPO order = iGuideDAO.getConsultOrderPO(id);
        return endService(order, "timeout");
    }

    /**
     * 订单超时，自动结束
     *
     * @param order
     * @return
     */
    public Object endService(ConsultOrderPO order, String type) throws HttpApiException {
        if (order.getState() == ServiceStateEnum.SERVCE_END.getValue()) {
            throw new ServiceException("该订单服务已经结束。");
        }
        String id = order.getId();
        // 1、结束服务
        Map<String, Object> updateValue = new HashMap<String, Object>();
        updateValue.put("state", ServiceStateEnum.SERVCE_END.getValue());
        order = iGuideDAO.updateConsultOrder(id, updateValue);
        logger.info("订单自动结束服务，ConsultOrderPO信息更新成功，orderId={0}", order.getId());
        try {
            jedisTemplate.zrem(GuideDAOImpl.GUIDE_ORDER_POOL, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (order.getGuideId() != null) {
            this.sendEvent(order.getGuideId(), order.getGroupId(), id);
        }
        //购买后结束服务
        if ("buy".equals(type)) {
            GuideMsgHelper.getInstance().sendLineMsg(order.getGroupId(), "您的电话套餐购买成功，医生助手的服务已结束。", false, null);
        } else if ("timeout".equals(type)) {//超时结束服务
            GuideMsgHelper.getInstance().sendLineMsg(order.getGroupId(), "订单超时，自动结束服务", false, null);
        }
        return null;
    }

    /**
     * 支付完成
     *
     */
    public Object endPay(Integer orderId) throws HttpApiException {
        ConsultOrderPO order = iGuideDAO.getObjectByOrderId(orderId);
        if (order == null || order.getId() == null) {
            return null;
        }
        // String id = order.getId();
        /**
         * 1、修改状态 TODO
         */
        // Map<String,Object>updateValue = new HashMap<String,Object>();
        // updateValue.put("payState", PayStateEnum.HAS_PAY.getValue());
        // order = iGuideDAO.updateConsultOrder(id,updateValue);
        logger.info("订单{0}支付完成，支付状态修改成功", order.getId());
        /**
         * 2、创建医生与患者的会话
         */
        CreateGroupRequestMessage createGroupParam = new CreateGroupRequestMessage();
        createGroupParam.setType(GroupTypeEnum.DOUBLE.getValue());
        createGroupParam.setGtype("1_3");//
        createGroupParam.setFromUserId(String.valueOf(order.getDoctorId()));
        createGroupParam.setToUserId(String.valueOf(order.getUserId()));
        createGroupParam.setCreateNew(true);
        createGroupParam.setBizStatus(String.valueOf(OrderEnum.OrderStatus.已支付.getIndex()));
        Map<String, Object> groupParam = new HashMap<String, Object>();
        groupParam.put("orderId", orderId);
        groupParam.put("orderType", OrderEnum.OrderType.order.getIndex());
        groupParam.put("packType", "3");
//		groupParam.put("price", order.getPrice());
        createGroupParam.setParam(groupParam);

        OrderParam param = new OrderParam();
        param.setOrderId(orderId);
        param.setOppointTime(order.getAppointStartTime());
        // OrderSessionVO orderSession = orderService.addPreChargeSession(param,
        // createGroupParam);
        orderService.addPreChargeSession(param, createGroupParam);
        logger.info("订单{0}支付完成，成功创建医生和患者会话组", order.getId());

        // 从会话订单中查询信息
        OrderSession orderSession = orderSessionSevice
                .findOneByOrderId(orderId);
        iDoctorTimeDAO
                .addCount(order.getDoctorId(), orderSession.getAppointTime(),
                        orderSession.getServiceEndTime());
		/*
		 * 增加缓存数据，用户发送提醒推送（给导医）
		 */
        // jedisTemplate.zadd(GuideDAOImpl.Z_GUIDE_SCHEDULE,order.getAppointTime(),order.getGuideId()+":"+order.getId());

        /**
         * 3.1、发送支付完成的提醒消息
         */
        GuideMsgHelper.getInstance().sendRemindMsg(order.getGroupId(),
                "订单支付成功", false, null);
        /**
         * 3.2、(给导医)发送系统通知 这里不需要给患者发送（）。
         */
        String toUserIds = String.valueOf(order.getGuideId());
        String content = GuideUtils.buildNotifyContent(order,
                "订单支付成功，该事项已进入日程，请直接查看。");

        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle("支付通知");
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setContent(content);
        imgTextMsg.setStyle(7);
        GuideMsgHelper.getInstance().sendNotifyMsg(toUserIds, imgTextMsg);
        // 支付完成之后将导医订单关闭 modifyBy 姜宏杰 只关闭电话订单
        Integer packId = order.getPackId();
        Pack pack = packMapper.selectByPrimaryKey(packId);
        if (PackEnum.PackType.phone.getIndex() == pack.getPackType()
                && PackEnum.PackStatus.open.getIndex() == pack.getStatus()) {
            order.setState(2);
            this.endService(order, "buy");
        }
        logger.info("订单{0}支付完成，给导医发送系统通知成功。", order.getId());
        // (返回值需要确认)

        /**
         * 发送患者的卡片信息
         */
        orderService.sendIllCaseCardInfo(orderId, null, orderSession.getMsgGroupId(), null);
        return null;
    }

    /**
     * 导医给患者发送预约时间，每次都会生成一张新的医生患者订单
     */
    public Object appointTime(String gid, Integer packId, Long startTime,
                              Long endTime, String type) throws HttpApiException {
        OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(gid);
        String id = orderCache.getId();
        Map<String, Object> updateValue = new HashMap<String, Object>();
        ConsultOrderPO consultOrderPO = iGuideDAO.getConsultOrderPO(id);

        if (consultOrderPO.getGuideId() == null) {
            updateValue.put("guideId", consultOrderPO.getGuideId());
            consultOrderPO = iGuideDAO.updateConsultOrder(id, updateValue);
        }
        if (packId != null && packId > 0) {
            consultOrderPO.setPackId(packId);
            Pack pack = packService.getPack(packId);
            if (pack != null) {
                consultOrderPO.setDoctorId(pack.getDoctorId());
            }
        }
        //创建标准订单
        Integer orderId = createOrder(consultOrderPO);

        OrderRelationPO relation = new OrderRelationPO();
        relation.setAppointStartTime(startTime);
        relation.setAppointEndTime(endTime);
        relation.setDoctorId(consultOrderPO.getDoctorId());
        relation.setOrderId(orderId);
        relation.setGuideOrderId(id);
        iGuideDAO.addOrderRelation(relation);
        logger.info("导医修改预约时间，consultOrderPO信息更新成功。orderId={0}", orderId);

        // 修改预约时间发送通知和提醒
        int doctorId = consultOrderPO.getDoctorId();
        User docUser = userManager.getUser(doctorId);
        // 3.1、导医向患者发送消息(医生名片)
        Pack pack = packService.getPack(consultOrderPO.getPackId());
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle(PackEnum.PackType.getTitle(pack.getPackType()));
        imgTextMsg.setContent(docUser.getName());

        StringBuffer remark = new StringBuffer();
        String groupName = getDocGroupName(doctorId);
        //集团为空则去取医院名称
        if (StringUtil.isNotEmpty(groupName)) {
            remark.append(groupName).append("|");//
        } else {
            //将医院名称传递
            HospitalVO hospitalVO = baseDataDao.getHospital(docUser.getDoctor().getHospitalId());
            remark.append(hospitalVO.getName()).append("|");//
        }
        remark.append(docUser.getDoctor().getTitle() + "-"
                + docUser.getDoctor().getDepartments());
        imgTextMsg.setRemark(remark.toString());
        imgTextMsg.setPic(docUser.getHeadPicFileName());
        imgTextMsg.setPrice("￥"
                + (pack.getPrice() == null ? 0 : pack.getPrice() / 100) + "/"
                + pack.getTimeLimit() + "分钟");
        // 如果预约的时间为空的 预约按钮都得屏蔽
        Map<String, Object> param = new HashMap<String, Object>();
        String content = "";
        if (startTime != null) {// modify by 姜宏杰 给患者发送不带时间以及预约按钮的卡片
            imgTextMsg.setAction("去付款");
            imgTextMsg
                    .setFooter(GuideUtils.convertDate2Str(startTime, endTime));
            param.put("bizType", 6);
            param.put("bizId", orderId);
            content = "预约时间已经以卡片方式发送给您，请您在2小时内付款，到预约时间医生会以电话方式联系您，请保持电话通畅！";
        } else {
            imgTextMsg.setAction("详情");
            param.put("bizType", 20);
            param.put("bizId", doctorId);
            content = "已经将医生的卡片发送给您，请查看医生详情！";
        }
        imgTextMsg.setStyle(10);
        // bizType =6表示进入支付， bizId表示订单Id
        // param.put("appointTime", startTime);
        imgTextMsg.setParam(param);
        GuideMsgHelper.getInstance().sendImgTextMsg(gid,
                String.valueOf(consultOrderPO.getGuideId()), null, imgTextMsg,
                true);
        logger.info("导医预约时间，订单{0}状态:未支付。", orderId);
        MessageVO msg = new MessageVO();
        msg.setGid(gid);
        msg.setType(MsgTypeEnum.TEXT.getValue());
        msg.setContent(content);
        msg.setFromUserId(String.valueOf(consultOrderPO.getGuideId()));
        msgService.baseSendMsg(msg);
        // 记录预约医生信息
        // 添加信息的时候的先去记录表里查询看当前这个医生有没有在
        ConsultOrderDoctorPO po = consultOrderDoctorDAO.getOrderByUserById(
                doctorId, id);
        if (po == null) {
            // 如果预约时间不为空那么直接状态待支付
            ConsultOrderDoctorPO orderDoctorPO = new ConsultOrderDoctorPO();
            if (startTime != null) {
                orderDoctorPO.setDoctorId(String.valueOf(doctorId));
                orderDoctorPO.setComsultOrderId(id);
                orderDoctorPO.setCreateTime(System.currentTimeMillis());
                orderDoctorPO.setStatus(OrderEnum.OrderStatus.待支付.getIndex());
                consultOrderDoctorDAO.addConsultOrder(orderDoctorPO);

                //TODO 2小时未支付则自动结束
                JobTaskUtil.endService4NoPay(orderDoctorPO.getId());

            } else {
                orderDoctorPO.setDoctorId(String.valueOf(doctorId));
                orderDoctorPO.setComsultOrderId(id);
                orderDoctorPO.setCreateTime(System.currentTimeMillis());
                orderDoctorPO.setStatus(8);
                consultOrderDoctorDAO.addConsultOrder(orderDoctorPO);
            }
        } else if (startTime != null && po != null) {// 如果预约时间不为空
            // 并且破不为空说明当前这个医生是符合患者的要求
            // 可以进行预约
            // 那么就要当state更新为待支付
            boolean updateSuccess = consultOrderDoctorDAO.updateOrderState(po.getId(), id);
            if (updateSuccess) {
                JobTaskUtil.endService4NoPay(po.getId());
            }
        }
        // 卡片发送给患者
        businessServiceMsg.sendEventForGuide(id, consultOrderPO.getUserId()
                .toString(), type, docUser.getName());
        //发送短信给患者
        //GuideUtils.convertDate2Str(startTime, endTime)
        //不带预约时间的时候 不需要发送短信
        if (startTime != null) {
            Disease disease = consultOrderPO.getDiseaseInfo();
            Patient patient = patientService.findByPk(disease.getPatientId());
            User user = userManager.getUser(patient.getUserId());
            if (user == null) {
                throw new ServiceException("用户不存在！！！");
            }
            //将要跳转的页面链接地址
            String openURL = null;
            String signature = null;
            openURL = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, 1));
            signature = BaseConstants.XG_SIGN;
            String[] params = new String[]{patient.getUserName(), docUser.getName(), GuideUtils.convertDate2Str(startTime, endTime), openURL};
            final String smsContent = baseDataService.toContent("0900", params);
            mobSmsSdk.send(patient.getTelephone(), smsContent, signature);
        }
        return null;
    }


    @Override
    public void sendDoctorCard2Patient(String gid, Integer packId) throws HttpApiException {
        // 3.1、导医向患者发送消息(医生名片)
        User docUser;
        Pack pack;
        if (Objects.isNull(packId)
                || Objects.isNull(pack = packService.getPack(packId))
                || Objects.isNull(docUser = userManager.getUser(pack.getDoctorId()))) {
            throw new ServiceException("套餐id = [" + packId + "] 错误");
        }
        Integer doctorId = docUser.getUserId();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle(PackEnum.PackType.getTitle(pack.getPackType()));
        imgTextMsg.setContent(docUser.getName());

        StringBuffer remark = new StringBuffer();
        String groupName = getDocGroupName(doctorId);
        //集团为空则去取医院名称
        if (StringUtil.isNotEmpty(groupName)) {
            remark.append(groupName).append("|");//
        } else {
            //将医院名称传递
            HospitalVO hospitalVO = baseDataDao.getHospital(docUser.getDoctor().getHospitalId());
            remark.append(hospitalVO.getName()).append("|");//
        }
        remark.append(docUser.getDoctor().getTitle() + "-"
                + docUser.getDoctor().getDepartments());
        imgTextMsg.setRemark(remark.toString());
        imgTextMsg.setPic(docUser.getHeadPicFileName());
        imgTextMsg.setPrice("￥"
                + (pack.getPrice() == null ? 0 : pack.getPrice() / 100) + "/"
                + pack.getTimeLimit() + "分钟");
        Map<String, Object> param = new HashMap<String, Object>();
        imgTextMsg.setAction("详情");
        param.put("bizType", 20);
        param.put("bizId", doctorId);

        imgTextMsg.setStyle(10);
        imgTextMsg.setParam(param);
        GuideMsgHelper.getInstance().sendImgTextMsg(gid,
                String.valueOf(ReqUtil.instance.getUserId()),
                null,
                imgTextMsg,
                true);
    }

    /**
     * 医生修改预约时间（支付成功后）:调用此接口给导医发送系统通知
     */
    public Object updateAppointTime(Integer orderId, Long appointTime) throws HttpApiException {
        ConsultOrderPO order = iGuideDAO.getObjectByOrderId(orderId);
        if (order == null || order.getId() == null) {
            return null;
        }
        /**
         * 1、修改预约时间
         */
        Long endTime = appointTime + 30 * 60 * 1000L;
        order.setAppointStartTime(appointTime);
        order.setAppointEndTime(endTime);
        // logger.info("医生修改预约时间，consultOrderPO信息更新成功。orderId={0}",orderId);
        /**
         * 给导医发送系统通知
         */
        String toUserIds = String.valueOf(order.getGuideId());
        String content = GuideUtils.buildNotifyContent(order,
                "医生修改了预约时间，您的日程改变，请注意查看。");

        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle("医生修改预约时间");
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setContent(content.toString());
        imgTextMsg.setStyle(7);
        GuideMsgHelper.getInstance().sendNotifyMsg(toUserIds, imgTextMsg);

        logger.info("医生修改预约时间，给导医发送系统通知成功。orderId={0}", orderId);
        return null;
    }

    /**
     * 获取等待接单的咨询订单
     *
     * @param count      返回最大记录数
     * @param startScore 订单的创建时间大于此时间
     * @param endScore   订单的创建时间小于此时间
     * @return
     */
    public List<ConsultOrderVO> waitOrderList(int count, Long startScore,
                                              Long endScore) {
        // 1、获取订单：未开始服务状态的咨询订单(每次最多获取20条)
        if (count <= 0) {
            count = 20;
        }
        if (startScore == null) {
            startScore = 0L;
        }
        if (endScore == null) {
            endScore = 0L;
        }
        List<ConsultOrderVO> list = new ArrayList<ConsultOrderVO>(count);
        String key = GuideUtils.genKey(null, GuideDAOImpl.GUIDE_ORDER_POOL);
        Set<String> idSet = jedisTemplate.zrangeByScore(key,
                (startScore <= 0 ? "-inf" : "(" + startScore),
                (endScore <= 0 ? "+inf" : "(" + endScore), 0, count);

        /**
         * 博德嘉联导医需要处理自己的订单
         */
        String bdjlGroupId = groupService.isOpenSelfGuideAndGetGroupId(ReqUtil.instance.getUserId(), UserType.DocGuide.getIndex());

        List<ConsultOrderPO> orderList = iGuideDAO.getOrderList(idSet, bdjlGroupId);
        if (orderList != null) {
            for (ConsultOrderPO orderPO : orderList) {
                ConsultOrderVO vo = GuideUtils.convertP2V(orderPO);
                list.add(vo);
            }

            // 按照下单时间正序排序
            list.sort(new Comparator<ConsultOrderVO>() {
                @Override
                public int compare(ConsultOrderVO o1, ConsultOrderVO o2) {
                    return o1.getCreateTime().compareTo(o2.getCreateTime());
                }

            });
        }
        return list;

    }

    /**
     * 获取导医接单记录
     *
     * @param userId
     * @param startTime （服务开始时间小于此时间）
     * @param count     ：默认20天
     * @return
     */
    public Object orderList(Integer userId, Long startTime, Integer count) {
        Date startDate = null, endDate = null;
        if (startTime == null) {
            startTime = iGuideDAO.getLastServiceTime(userId);
            if (startTime == null) {
                return null;
            }
            startDate = new Date(startTime);
        } else {
            startDate = DateUtil.getAddDate(new Date(startTime), -1);
        }
        // 截掉时分秒
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        startDate = new Date(calendar.getTimeInMillis());
        if (count == null || count == 0) {
            count = 20;
        }
        endDate = DateUtil.getAddDate(startDate, (1 - count));
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        endDate = new Date(calendar.getTimeInMillis());

        List<ConsultOrderPO> orderList = iGuideDAO.getOrderListByGuide(userId,
                startDate, endDate);
        if (orderList != null) {
            Map<String, List<ConsultOrderVO>> map = new HashMap<String, List<ConsultOrderVO>>();
            List<ConsultOrderVO> list = null;
            String key = null;
            for (ConsultOrderPO orderPO : orderList) {
                if (orderPO.getStartTime() == null) {
                    continue;
                }
                ConsultOrderVO vo = GuideUtils.convertP2V(orderPO);
                key = DateUtil.formatDate2Str(vo.getStartTime(),
                        DateUtil.FORMAT_YYYY_MM_DD);
                if (map.containsKey(key)) {
                    list = map.get(key);
                } else {
                    list = new ArrayList<ConsultOrderVO>(orderList.size());
                    map.put(key, list);
                }
                list.add(vo);
            }
            List<OrderListVO> listVO = new ArrayList<OrderListVO>();
            for (Entry<String, List<ConsultOrderVO>> e : map.entrySet()) {
                OrderListVO eachDate = new OrderListVO();
                eachDate.setDay(e.getKey());
                eachDate.setOrderList(e.getValue());
                if (eachDate.getOrderList() != null) {
                    eachDate.setCount(eachDate.getOrderList().size());
                }
                listVO.add(eachDate);
            }
            // 按照日期倒叙排序
            listVO.sort(new Comparator<OrderListVO>() {
                @Override
                public int compare(OrderListVO o1, OrderListVO o2) {
                    return o2.getDay().compareTo(o1.getDay());
                }

            });
            return listVO;
        }
        return null;
    }

    @Override
    public Object doctorInfo(String gid, Integer doctorId) {
        if (doctorId == null || doctorId == 0) {
            if (gid == null) {
                throw new ServiceException("参数错误：医生Id和gid不能同时为空");
            }

            OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(gid);
            if (orderCache == null) {
                throw new ServiceException("数据错误：该会话(" + gid + ")没有对应订单");
            }
            String id = orderCache.getId();

            ConsultOrderPO consultOrderPO = iGuideDAO.getConsultOrderPO(id);
            doctorId = consultOrderPO.getDoctorId();
            if (doctorId == null) {
                throw new ServiceException("数据错误：该订单中没有选择医生。");
            }
        }

        User user = userManager.getUser(doctorId);
        if (user == null) {
            throw new ServiceException("数据错误：医生不存在");
        }
        DoctorTimeVO doctor = new DoctorTimeVO();
        doctor.setUserId(doctorId);
        doctor.setName(user.getName());
        doctor.setSex(user.getSex());
        doctor.setTelephone(user.getTelephone());
        doctor.setHeadImg(user.getHeadPicFileName());
        if (user.getDoctor() != null) {
            doctor.setTitle(user.getDoctor().getTitle());
            doctor.setHospital(user.getDoctor().getHospital());
            doctor.setDoctorNum(user.getDoctor().getDoctorNum());
            doctor.setDepartments(user.getDoctor().getDepartments());
            // TODO 消息免打扰在这里取.
            String troubleFree = null;
            troubleFree = user.getDoctor().getTroubleFree();
            if (troubleFree == null || troubleFree.isEmpty()) {
                troubleFree = "1"; // 默认为1，DB不存储默认值，所以在这里判断，取出为空时，设置为1
            }
            // 设置免打扰到DoctorTimeVO里，准备返回DoctorTimeVO
            doctor.setTroubleFree(troubleFree);
        }

        // 获取医生对应的所有电话套餐
        Pack condition = new Pack();
        condition.setDoctorId(doctorId);
        condition.setStatus(PackEnum.PackStatus.open.getIndex());
        /**
         * 导医web查看医生详情页面需要查看医生的所有套餐
         */
//		condition.setPackType(PackEnum.PackType.phone.getIndex());
        List<Pack> packList = packService.queryPack(condition);
        if (packList != null) {
            for (Pack pack : packList) {
                if (pack != null && pack.getPrice() != null) {
                    pack.setPrice(pack.getPrice() / 100);
                }
            }
        }
        doctor.setPackList(packList);

        DoctorTimePO doctorTime = iDoctorTimeDAO.getDoctorTime(doctorId);
        if (doctorTime != null) {
            doctor.setTimeList(doctorTime.getTimeList());
        } else {
            doctor.setTimeList(new ArrayList<Time>());
        }
        DoctorTimePO remark = iDoctorTimeDAO.getDoctorRemark(doctorId);
        if (remark != null) {

            doctor.setRemarkList(remark.getRemarkList());
        } else {
            doctor.setRemarkList(new ArrayList<Remark>());
        }
        return doctor;
    }

    @Override
    public Object addDocTime(Integer doctorId, Long startTime, Long endTime) {
        DoctorTimePO doctorTime = iDoctorTimeDAO.addDoctorTime(doctorId,
                startTime, endTime);
        return doctorTime;
    }

    @Override
    public Object removeDocTime(Integer doctorId, Long startTime, Long endTime) {
        DoctorTimePO doctorTime = iDoctorTimeDAO.removeDoctorTime(doctorId,
                startTime, endTime);
        return doctorTime;
    }

    /**
     * @param id 订单Id
     * @return
     */
    public OrderDiseaseVO findOrderDisease(String id) {
        ConsultOrderPO order = iGuideDAO.getConsultOrderPO(id);

        OrderDiseaseVO orderDiseaseVO = new OrderDiseaseVO();
        orderDiseaseVO.setOrderId(order.getId());
        orderDiseaseVO.setDiseaseDesc(order.getDiseaseInfo().getDiseaseDesc());
        orderDiseaseVO
                .setImgStringPath(order.getDiseaseInfo().getDiseaseImgs());
        if (orderDiseaseVO.getImgStringPath() == null) {
            orderDiseaseVO.setImgStringPath(new ArrayList<String>());
        }
        /*** begin add by liwei 2016年1月21日 ********/
        String seeDoctorMsg = order.getDiseaseInfo().getSeeDoctorMsg();
        if (StringUtils.isNotEmpty(seeDoctorMsg)) {
            orderDiseaseVO.setSeeDoctorMsg(seeDoctorMsg);
        }
        Boolean isSeeDoctor = order.getDiseaseInfo().getIsSeeDoctor();

        if (null != isSeeDoctor) {
            orderDiseaseVO.setIsSeeDoctor(isSeeDoctor.toString());
        }
        /*** end add by liwei 2016年1月21日 ********/
        PatientVO patientVO = new PatientVO();
        Integer patientId = order.getDiseaseInfo().getPatientId();
        Patient patient = patientService.findByPk(patientId);
        patientVO.setTelephone(order.getDiseaseInfo().getTelephone());
        patientVO.setPatientId(patientId);
        patientVO.setAge(patient.getAge());
        patientVO.setPatientName(patient.getUserName());
        patientVO.setRelation(patient.getRelation());
        patientVO.setSex(patient.getSex());
        patientVO.setUserId(patient.getUserId());
        patientVO.setOrderType(OrderType.order.getIndex());
        patientVO.setPackType(PackType.phone.getIndex());
        orderDiseaseVO.setOrderVo(patientVO);
        return orderDiseaseVO;
    }

    /**
     * 导医会话中修改病情详情的时候，同步修改未支付订单中的病情信息
     */
    public Object updateOrderDisease(OrderDiseaseVO param) throws HttpApiException {
        if (param.getOrderId() == null) {
            throw new ServiceException("数据错误：Id不能为空");
        }
        ConsultOrderPO orderPO = iGuideDAO.updateOrderDisease(param);
        List<OrderRelationPO> orderList = iGuideDAO.getOrderIdList(orderPO
                .getId());
        if (orderList != null && orderList.size() > 0) {
            DiseaseParam intance = new DiseaseParam();
            intance.setDiseaseDesc(param.getDiseaseDesc());
            if (param.getImgStringPath() != null) {
                String[] diseaseImgs = new String[param.getImgStringPath()
                        .size()];
                intance.setImagePaths(param.getImgStringPath().toArray(
                        diseaseImgs));
            }
            intance.setTelephone(param.getTelephone());
            for (OrderRelationPO relation : orderList) {
                // 修改Order订单的病情信息
                Order order = orderService.getOne(relation.getOrderId());
                if (order.getOrderStatus() == OrderStatus.待支付.getIndex())// 只有待支付才同步更新病情信息；
                {
                    diseaseService.updateDisease(intance, order.getDiseaseId());
                }
            }
        }
        String content = "我修改了病情资料，请您再重新查看。";
        GuideMsgHelper.getInstance().sendMsg(MsgTypeEnum.TEXT,
                orderPO.getGroupId(), orderPO.getUserId(), null, content, true);
        return orderPO;
    }

    /**
     * 创建待支付订单
     *
     * @param order
     * @return
     */
    private Integer createOrder(ConsultOrderPO order) throws HttpApiException {
        OrderParam param = new OrderParam();
        param.setDoctorId(order.getDoctorId());
        param.setUserId(order.getUserId());
        param.setPackId(order.getPackId());
        param.setOrderType(OrderEnum.OrderType.order.getIndex());
        param.setOrderStatus(OrderEnum.OrderStatus.待支付.getIndex());
        param.setPreOrderId(order.getPreOrderId());
        param.setTransferDoctorId(order.getTransferDoctorId());
        param.setTransferTime(order.getTransferTime());

        Disease diseaseInfo = order.getDiseaseInfo();
        if (diseaseInfo != null) {
            param.setPatientId(diseaseInfo.getPatientId());
            param.setDiseaseDesc(diseaseInfo.getDiseaseDesc());
            param.setDiseaseInfo_now(diseaseInfo.getDiseaseInfo_now());
            param.setDiseaseInfo_old(diseaseInfo.getDiseaseInfo_old());
            param.setMenstruationdiseaseInfo(diseaseInfo.getMenstruationdiseaseInfo());
            param.setFamilydiseaseInfo(diseaseInfo.getFamilydiseaseInfo());
            param.setSeeDoctorMsg(diseaseInfo.getSeeDoctorMsg());
            param.setTelephone(diseaseInfo.getTelephone());
            param.setSeeDoctorMsg(diseaseInfo.getSeeDoctorMsg());
            param.setIllCaseInfoId(diseaseInfo.getIllCaseInfoId());
            if (diseaseInfo.getDiseaseImgs() != null) {
                String[] imagePaths = new String[diseaseInfo.getDiseaseImgs()
                        .size()];
                param.setImagePaths(diseaseInfo.getDiseaseImgs().toArray(
                        imagePaths));
            }
        }
        UserSession session = new UserSession();
        session.setUserId(order.getUserId());
        session.setUserType(UserEnum.UserType.patient.getIndex());
        /**
         * 生成订单，不创建会话
         */
        PreOrderVO preOrderVO = orderService.addPreCharge(param, session);

        // 预约时间
        // orderSessionService.appointServiceTime(preOrderVO.getOrderId(),
        // startTime);
        return preOrderVO.getOrderId();
    }

    /**
     * 最大服务时长
     *
     * @return
     */
    public long getMaxServiceTime() {
        return 15 * 60 * 1000L;
    }

    public String getOrderIdByGid(String gid) {
        OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(gid);
        return orderCache.getId();
    }

    /**
     * </p>通过用户id查找医生集团名称</p>
     *
     * @return
     * @author fanp
     * @date 2015年9月15日
     */
    private String getDocGroupName(Integer doctorId) {
        BaseUserVO baseUserVO = baseUserDao.getGroupByUserId(doctorId);
        if (baseUserVO != null) {
            return baseUserVO.getGroupName();
        }
        // return "医生集团";
        return null;
    }

    public Long getAppointTime(Integer orderId) {
        ConsultOrderPO orderPO = iGuideDAO.getObjectByOrderId(orderId);
        if (orderPO != null) {
            return orderPO.getAppointStartTime();
        }
        return null;
    }

    /**
     * 获取所有超时订单
     *
     * @return
     */
    public List<ConsultOrderPO> getTimeOutOrderList() {
        return iGuideDAO.getTimeOutOrderList();
    }

    private void sendEvent(Integer guideId, String groupId, String orderId) {
        EventVO eventVO = new EventVO();
        eventVO.setUserId(guideId + "");
        eventVO.setEventType(EventEnum.ORDER_CHANGE_STATUS.getValue());
        eventVO.setTs(System.currentTimeMillis());
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("groupId", groupId);
        param.put("orderId", orderId);
        param.put("status", "" + ServiceStateEnum.SERVCE_END.getValue());
        eventVO.setParam(param);
        msgService.sendEvent(eventVO);
    }

    @Override
    public PageVO getOrders(OrderParam param) {
        List<ConsultOrderPO> consultOrder = iGuideDAO.getOrderByGuide(
                param.getUserId(), null, null);

        List<Integer> orderIds = new ArrayList<Integer>();
        for (ConsultOrderPO conOrder : consultOrder) {
            List<OrderRelationPO> orderList = iGuideDAO.getOrderIdList(conOrder
                    .getId());
            if (orderList != null && orderList.size() > 0) {
                for (OrderRelationPO relation : orderList) {
                    orderIds.add(relation.getOrderId());
                }
            }
        }

        if (orderIds.isEmpty())
            return new PageVO();

        param.setOrderIds(orderIds);
        if (param.getRecordStatus() == 0) {
            param.setRecordStatus(null);
        }
        return orderService.getOrderByRecordStatus(param);
    }

    @Override
    public void handleConfirm(Integer orderId) throws HttpApiException {
        // 修改成确定状态
        orderService.updateRecordStatus(orderId);
        // 结束订单服务
        if (orderService.getOne(orderId).getOrderStatus() == OrderStatus.已支付
                .getIndex()) {
            orderSessionService.finishService(orderId, 2);
        }
    }

    public void dataUpgrade() {
        iGuideDAO.dataUpgrade();
    }

    @Override
    public OrderDiseaseVO findOrderDiseaseAndRemarks(String id, Integer userId) {
        ConsultOrderPO order = iGuideDAO.getConsultOrderPO(id);
        OrderDiseaseVO orderDiseaseVO = new OrderDiseaseVO();
        orderDiseaseVO.setOrderId(order.getId());
        orderDiseaseVO.setDiseaseDesc(order.getDiseaseInfo().getDiseaseDesc());
        orderDiseaseVO.setDiseaseInfo_now(order.getDiseaseInfo()
                .getDiseaseInfo_now());
        orderDiseaseVO.setDiseaseInfo_old(order.getDiseaseInfo()
                .getDiseaseInfo_old());
        orderDiseaseVO
                .setSeeDoctorMsg(order.getDiseaseInfo().getSeeDoctorMsg());
        orderDiseaseVO.setFamilydiseaseInfo(order.getDiseaseInfo()
                .getFamilydiseaseInfo());
        orderDiseaseVO.setMenstruationdiseaseInfo(order.getDiseaseInfo()
                .getMenstruationdiseaseInfo());
        orderDiseaseVO.setCureSituation(order.getDiseaseInfo()
                .getSeeDoctorMsg());
        orderDiseaseVO
                .setImgStringPath(order.getDiseaseInfo().getDiseaseImgs());
        if (orderDiseaseVO.getImgStringPath() == null) {
            orderDiseaseVO.setImgStringPath(new ArrayList<String>());
        }
        PatientVO patientVO = new PatientVO();
        Integer patientId = order.getDiseaseInfo().getPatientId();
        Patient patient = patientService.findByPk(patientId);
        patientVO.setTelephone(order.getDiseaseInfo().getTelephone());
        patientVO.setPatientId(patientId);
        patientVO.setAge(patient.getAge());
        patientVO.setPatientName(patient.getUserName());
        patientVO.setRelation(patient.getRelation());
        patientVO.setSex(patient.getSex());
        patientVO.setUserId(patient.getUserId());
        patientVO.setOrderType(OrderType.order.getIndex());
        patientVO.setPackType(PackType.phone.getIndex());

        User user = userManager.getRemindVoice(order.getUserId());
        String remark = userManager.getRemarks(order.getUserId());
        patientVO.setRemark(remark == null ? "" : remark);
        orderDiseaseVO.setUser(user);
        orderDiseaseVO.setOrderVo(patientVO);
        orderDiseaseVO.setYsTelephone(order.getDiseaseInfo().getTelephone());
        // 获取患者的备注信息

        return orderDiseaseVO;
    }

    @Override
    public Object addDocRemark(Integer doctorId, String remark, String guideId,
                               String guideName) {
        DoctorTimePO doctorTime = iDoctorTimeDAO.addDoctorRemark(doctorId,
                remark, guideId, guideName);
        return doctorTime;
    }

    /**
     * 约不到医生，继续等待接口
     */
    @Override
    public void sendOnWaiterMsg(String groupId, String orderId) throws HttpApiException {
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("会话组id不能为空！");
        }
        if (StringUtils.isEmpty(orderId)) {
            throw new ServiceException("订单id不能够为空！");
        }
        ConsultOrderPO consultOrderPO = iGuideDAO.getConsultOrderPO(orderId);
        String content = "继续等待";
        Integer patientUserId = consultOrderPO.getUserId();

        // 系统模拟患者发送一条 继续等待的接口
        SendMsgResult result = GuideMsgHelper.getInstance().sendMsg(
                MsgTypeEnum.TEXT, groupId, patientUserId, null, content, false);
        logger.info("消息发送成功。msgContent={0}", content.toString());

        Integer guideId = consultOrderPO.getGuideId();
        content = "请您保持电话通畅，24小时内答复您结果。";
        // 系统模拟导医 恢复患者 一条消息
        result = GuideMsgHelper.getInstance().sendMsg(MsgTypeEnum.TEXT,
                groupId, guideId, null, content, false);
        logger.info("消息发送成功。msgContent={0}", content.toString());

        /**
         * 更新该订单对应的会话中的首条消息，用于客户端定位
         */
        Map<String, Object> updateValue = new HashMap<String, Object>();
        updateValue.put("msgId", result.getMid());
        iGuideDAO.updateConsultOrder(orderId, updateValue);
        logger.info("更新ConsultOrderPO成功:msgId={0}", result.getMid());

    }

    /**
     *
     */
    @Override
    public void sendCommendMsg(String groupId, String orderId) throws HttpApiException {

        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("会话组id不能为空！");
        }
        if (StringUtils.isEmpty(orderId)) {
            throw new ServiceException("订单id不能够为空！");
        }
        ConsultOrderPO consultOrderPO = iGuideDAO.getConsultOrderPO(orderId);
        String content = "请帮我推荐医生";
        Integer patientUserId = consultOrderPO.getUserId();

        // 系统模拟患者发送一条 继续等待的接口
        SendMsgResult result = GuideMsgHelper.getInstance().sendMsg(
                MsgTypeEnum.TEXT, groupId, patientUserId, null, content, false);
        logger.info("消息发送成功。msgContent={0}", content.toString());


        content = "好的，我可以为您推荐同城、同医院或同职称的医生，您可以把要求告诉我。";
        Integer guideId = consultOrderPO.getGuideId();
        result = GuideMsgHelper.getInstance().sendMsg(
                MsgTypeEnum.TEXT, groupId, guideId, null, content, false);
        logger.info("消息发送成功。msgContent={0}", content.toString());
        /**
         * 更新该订单对应的会话中的首条消息，用于客户端定位
         */
        Map<String, Object> updateValue = new HashMap<String, Object>();
        updateValue.put("msgId", result.getMid());
        iGuideDAO.updateConsultOrder(orderId, updateValue);
        logger.info("更新ConsultOrderPO成功:msgId={0}", result.getMid());
    }

    @Override
    public PageVO fingDoctors(String orderId, boolean isCity,
                              boolean isHospital, boolean isTitle, Integer pageIndex,
                              Integer pageSize) {
        if (StringUtils.isEmpty(orderId)) {
            throw new ServiceException("订单id不能够为空！");
        }
        PageVO pageVo = new PageVO();

        ConsultOrderPO consultOrderPO = iGuideDAO.getConsultOrderPO(orderId);
        Integer doctorId = consultOrderPO.getDoctorId();
        User user = userManager.getUser(doctorId);
        Integer cityId = null;
        String hospitalId = null;
        String title = null;
        String deptId = "";
        if (null != user) {
            cityId = user.getDoctor().getCityId();
            hospitalId = user.getDoctor().getHospitalId();
            title = user.getDoctor().getTitle();
            deptId = user.getDoctor().getDeptId();
            deptId = null;
            int flag = -1;// 0 全选 1 全不选 2 其他情况

            if (isCity && isHospital && isTitle) {
                flag = 0;
            } else if (!isCity && !isHospital && !isTitle) {
                flag = 1;
            } else {
                flag = 2;
                if (!isCity) {
                    cityId = null;
                }
                if (!isHospital) {
                    hospitalId = null;
                }
                if (!isTitle) {
                    title = null;
                }
            }
            String groupId = groupService.isOpenSelfGuideAndGetGroupId(ReqUtil.instance.getUserId(), UserType.DocGuide.getIndex());
            Map<String, Object> map = getDoctorIds(groupId, null);
            @SuppressWarnings("unchecked")
            List<Integer> doctorList = (List<Integer>) map.get("userIds");
            pageVo = userManager.fingDoctors(flag, doctorList, cityId,
                    hospitalId, title, deptId, pageIndex, pageSize);
        }
        return pageVo;
    }

    private Map<String, Object> getDoctorIds(String groupId, Integer queryType) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Integer> userIds = null;
        List<Integer> doctorList = new ArrayList<Integer>();
        Map<Integer, Long> doctorPrice = new HashMap<Integer, Long>();
        List<Pack> packList = packService.selectPackDortorList(queryType);
        if (null != packList && packList.size() > 0) {
            for (Pack pack : packList) {
                doctorList.add(pack.getDoctorId());
                doctorPrice.put(pack.getDoctorId(), pack.getPrice());
            }
        }

        if (StringUtils.isNotBlank(groupId)) {
            userIds = gdocDao.filterByGroupId(doctorList, groupId);
        } else {
            userIds = doctorList;
        }
        map.put("userIds", userIds);
        map.put("doctorPriceMap", doctorPrice);

        return map;
    }

    /**
     * 发送卡片
     */
    @Override
    public Object sendDoctorCard(String groupId, String orderId,
                                 Integer doctorId) throws HttpApiException {

        if (StringUtils.isEmpty(orderId)) {
            throw new ServiceException("订单id不能够为空！");
        }

        if (StringUtils.isEmpty(orderId)) {
            throw new ServiceException("订单id不能够为空！");
        }

        ConsultOrderPO consultOrderPO = iGuideDAO.getConsultOrderPO(orderId);
        if (null == consultOrderPO) {
            throw new ServiceException("订单不存在");
        }
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGid(groupId);
        User docUser = userManager.getUser(doctorId);
        // Pack pack = packService.getPack(consultOrderPO.getPackId());

        // 查询医生对应的电话套餐
        Pack paramPack = new Pack();
        paramPack.setDoctorId(doctorId);
        paramPack.setPackType(PackEnum.PackType.phone.getIndex());
        paramPack.setStatus(PackEnum.PackStatus.open.getIndex());
        List<Pack> packList = packService.queryPack(paramPack);
        Pack pack = null;
        if (null == packList || packList.size() == 0) {
            throw new ServiceException("该医生还没有开通电话套餐！");
        } else {
            pack = packList.get(0);
        }
        // 发送名片
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle(PackEnum.PackType.getTitle(PackEnum.PackType.phone
                .getIndex()));
        imgTextMsg.setContent(docUser.getName());

        StringBuffer remark = new StringBuffer();
        String groupName = getDocGroupName(doctorId);
        // groupName="默认集团";
        if (groupName != null) {
            remark.append(groupName).append("|");//
        }
        remark.append(docUser.getDoctor().getTitle() + "-"
                + docUser.getDoctor().getDepartments());
        imgTextMsg.setRemark(remark.toString());
        imgTextMsg.setPic(docUser.getHeadPicFileName());
        imgTextMsg.setPrice("￥"
                + (pack.getPrice() == null ? 0 : pack.getPrice() / 100) + "/"
                + pack.getTimeLimit() + "分钟");
        imgTextMsg.setContent(docUser.getName() + "|"
                + docUser.getDoctor().getTitle());
        imgTextMsg.setFooter("查看详情");
        // imgTextMsg.setFooter("电话咨询 ￥200/10分钟");
        imgTextMsg.setStyle(10);
        // bizType =5表示预约医生， bizId表示医生Id
        // modify By 姜宏杰 2016年1月26日14:23:53
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("bizType", 22);
        param.put("bizId", doctorId);
        imgTextMsg.setParam(param);
        SendMsgResult result = GuideMsgHelper.getInstance().sendImgTextMsg(
                // modify By 姜宏杰 2016年1月26日14:23:53
				/*
				 * groupId, SysUserEnum.SYS_001.getUserId(),
				 * String.valueOf(guideId), imgTextMsg, false);
				 */
                groupId, String.valueOf(consultOrderPO.getUserId()), null,
                imgTextMsg, false);
        logger.info("导医接单：系统发送给导医医生名片成功，msgId={0}", result.getMid());

        // 对于导医来说，该会话的组名称和组图片显示患者的名称和头像
        if (groupInfo != null) {
            UserSession user = ReqUtil.instance.getUser(docUser.getUserId());
            if (user != null) {
                groupInfo.setGname(user.getName());
                groupInfo.setGpic(user.getHeadImgPath());
            }
        }
        Map<String, Object> updateValue = new HashMap<String, Object>();
        updateValue.put("doctorId", doctorId);
        // 更新导医订单中的医生
        iGuideDAO.updateConsultOrder(orderId, updateValue);

        try {
            insertDoctorList(doctorId, orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return groupInfo;
    }

    // 插入医生信息到医生列表
    private void insertDoctorList(Integer doctorId, String orderId) {
        ConsultOrderDoctorPO po = consultOrderDoctorDAO.getOrderByUserById(
                doctorId, orderId);
        if (po == null) {
            ConsultOrderDoctorPO order = new ConsultOrderDoctorPO();
            order.setDoctorId(String.valueOf(doctorId));
            order.setComsultOrderId(orderId);
            order.setStatus(8);
            order.setCreateTime(new Date().getTime());
            consultOrderDoctorDAO.addConsultOrder(order);
        }
    }

    @Override
    public Map<String, Object> getConsultOrderDoctor(int doctorId,
                                                     String orerId, int userId) {
        Map<String, Object> data = doctorService.getIntro(doctorId);
        List<ConsultOrderDoctorPO> po = consultOrderDoctorDAO.getOrderByUser(
                doctorId, orerId);
        List<Object> doctorlist = new ArrayList<Object>();
        String groupName = "";
        if (null != po && po.size() > 0) {
            for (ConsultOrderDoctorPO consultOrderDoctorPO : po) {
                User user = userManager.getUser(Integer
                        .valueOf(consultOrderDoctorPO.getDoctorId()));
                if (user == null) {
                    throw new ServiceException("数据错误：医生不存在");
                }
                DoctorTimeVO doctor = new DoctorTimeVO();
                doctor.setUserId(Integer.valueOf(consultOrderDoctorPO
                        .getDoctorId()));
                groupName = getDocGroupName(doctorId);
                doctor.setGroupName(groupName);
                doctor.setName(user.getName());
                doctor.setSex(user.getSex());
                doctor.setState(consultOrderDoctorPO.getStatus());
                doctor.setTelephone(user.getTelephone());
                doctor.setCreateTime(consultOrderDoctorPO.getCreateTime());
                doctor.setHeadImg(user.getHeadPicFileName());
                if (user.getDoctor() != null) {
                    doctor.setTitle(user.getDoctor().getTitle());
                    doctor.setHospital(user.getDoctor().getHospital());
                    doctor.setDoctorNum(user.getDoctor().getDoctorNum());
                    doctor.setDepartments(user.getDoctor().getDepartments());
                    String troubleFree = null;
                    troubleFree = user.getDoctor().getTroubleFree();
                    if (troubleFree == null || troubleFree.isEmpty()) {
                        troubleFree = "1"; // 默认为1，DB不存储默认值，所以在这里判断，取出为空时，设置为1
                    }
                }
                doctorlist.add(doctor);
                // list.add(po);
            }
            data.put("doctorlist", doctorlist);
            // data.put("info", list);
        }
        return data;
    }


    @SuppressWarnings("unchecked")
    @Override
    public PageVO findDoctorByKeyWord(String keyWord, Integer queryType, Integer pageIndex, Integer pageSize) {
        PageVO pageVo = new PageVO();
        String groupId = groupService.isOpenSelfGuideAndGetGroupId(ReqUtil.instance.getUserId(), UserType.DocGuide.getIndex());
        Map<String, Object> map = getDoctorIds(groupId, queryType);
        List<Integer> doctorList = (List<Integer>) map.get("userIds");
        if (null == doctorList) {
            return pageVo;
        }
        Map<Integer, Long> doctorPriceMap = (Map<Integer, Long>) map.get("doctorPriceMap");
        if (queryType != null) {
            /**
             * 1、获取有排班的医生id
             */
            List<Integer> keywordUserIds = new ArrayList<>();
            if (StringUtils.isNotBlank(keyWord)) {
                List<String> hospitalIds = groupDao.findOfflineHospitalIdByKeyword(groupId, keyWord);
                if (hospitalIds.size() > 0) {
                    keywordUserIds = offlineDao.getDoctorIdsInHospitalIds(hospitalIds);
                }
            }
            pageVo = userManager.searchAppointmentDoctor(keyWord, pageIndex, pageSize, doctorList, keywordUserIds);
            List<?> dataList = pageVo.getPageData();
            if (dataList != null && dataList.size() > 0) {
                for (Object obj : dataList) {
                    Map<String, Object> item = (Map<String, Object>) obj;
                    Integer doctorId = item.get("doctorId") != null ? Integer.valueOf(item.get("doctorId") + "") : null;
                    item.put("appointmentPrice", doctorPriceMap.get(doctorId));
                }
            }
        } else {
            pageVo = userManager.fingDoctorByKeyWord(keyWord, pageIndex, pageSize, doctorList);
        }
        return pageVo;
    }

    @Override
    public void addDialogueImg(String guideId, String[] imgs, String orderId) {
        if (StringUtil.isEmpty(orderId)) {
            throw new ServiceException("订单id不能为空");
        }
        for (int i = 0; i < imgs.length; i++) {
            DialogueImgPO po = new DialogueImgPO();
            po.setOrderId(orderId);
            po.setGuideId(guideId);
            long currentTime = System.currentTimeMillis();
            po.setCreateTime(currentTime);
            if (imgs[i].indexOf("[") > -1 || imgs[i].indexOf("]") > -1) {
                po.setImgs(imgs[i].substring(1, imgs[i].length() - 1));
            } else {
                po.setImgs(imgs[i]);
            }
            dsForRW.save(po);
        }
    }

    @Override
    public List<DialogueImgPO> getDialogueImg(String orderId) {
        Query<DialogueImgPO> uq = dsForRW.createQuery(DialogueImgPO.class)
                .filter("orderId", String.valueOf(orderId)).order("-createTime");
        return uq.asList();
    }

    @Override
    public PageVO get8HourOrder(OrderParam param) {
        List<Integer> listOrderId = new ArrayList<Integer>();//所有的订单id
        //**********2方通话记录以及订单开始***************//
        List<Integer> ids = callRecordRepository.get8HourOrder();
        if (ids.size() > 0) {
            for (int i = 0; i < ids.size(); i++) {
                listOrderId.add(ids.get(i));
            }
        }
        //List<Order> po =  orderService.findOrderIdByIds(listOrderId);
        //**********2方通话记录以及订单结束***************//
        //3方通话时存储在MySQL里面的 开发过程中 在SQL里控制了时间（比如8小时：8*3600*1000）请知悉 而且订单的信息需要去MySQL里面的p_order表里面取
        List<CallResult> listCall = callResultService.get8HourCallResultList();
        //listOrderId.clear();
        if (listCall.size() > 0) {
            for (CallResult callResult : listCall) {
                listOrderId.add(Integer.valueOf(callResult.getUserData()));
            }
        }
        //以上结果为医生和患者产生通话（两方或三方通话）8小时后，医生仍没有填写咨询记录的 结果列
        //一下数据为医生在咨询记录中录了语音，让导医帮助转成文字的，但订单是没有结束服务。
        List<CureRecord> listCure = cureRecordService.getListByCondition();
        if (listCure.size() > 0) {
            for (CureRecord cureRecord : listCure) {
                listOrderId.add(cureRecord.getOrderId());
            }
        }
        //根据订单集合id查询出订单
        //List<OrderVO> listOrder= orderService.findByIdsMap(listOrderId);
        if (listOrderId.size() == 0) {
            //要是没有满足的订单那么直接返回一个空
            return null;
        }
        param.setOrderIds(listOrderId);
        List<OrderVO> orders = orderService.findByIdsMap(param);
        //查询出所有的订单后 在根据订单id去查找导医信息 排除其他导医的订单（只接自己的）
        Iterator<OrderVO> iter = orders.iterator();
        while (iter.hasNext()) {
            //过滤掉不是当前登录导医的订单
            ConsultOrderPO po = iGuideDAO.getObjectByOrderId(iter.next().getOrderId());
            if (po == null) {
                logger.info("导医订单已经不存在，可能被删除了:" + iter.next().getOrderId());
                continue;
                //throw new ServiceException("导医订单已经不存在，可能被删除了");
            }
            Integer guideId = po.getGuideId();
            if (ReqUtil.instance.getUserId() != guideId) {
                iter.remove();//与当前登录的导医不是一个人的数据全部干掉
            }
            ;
        }
        //Integer total = orderService.findByIdsMapCount(param);
        if (orders.size() > 0) {
            for (OrderVO orderVO : orders) {
                //现在通话完成之后 没有在对CallResult进行修改(具体可见/voip/endCall)
                List<CallRecordVO> list3 = callRecordService.getRecordByOrderId(orderVO.getOrderId());//三方通话录音
                //List<CallResult> list2 = callRecordRepository.getAllCallResultByOrderId(orderVO.getOrderId());//双向通话录音
				/*if(null!=list2&&list2.size()>0){
					if(null!=list2.get(0).getStoptime()){
						orderVO.setConferenceStopTime(list2.get(0).getStoptime());
					}
				}*/
                if (null != list3 && list3.size() > 0) {
                    orderVO.setConferenceStopTime(String.valueOf(list3.get(0).getEndTime()));
                }
            }
        }
        //TODO 转换成医生信息
        for (OrderVO order : orders) {
            User user = userManager.getUser(order.getDoctorId());
            if (user == null)
                continue;
            order.setDoctorName(user.getName());
            order.setTopPath(user.getHeadPicFileName());
            order.setDoctorId(user.getUserId());
            order.setTelephone(user.getTelephone());
        }
        return new PageVO(orders, 0l, param.getPageIndex(), param.getPageSize());
    }

    @Override
    public PageVO getGuideAlreadyServicedOrder(OrderParam param) {
        List<OrderVO> orders = new ArrayList<OrderVO>();
        orders = orderService.getGuideAlreadyServicedOrder(param);
        Iterator<OrderVO> iter = orders.iterator();
        while (iter.hasNext()) {
            //过滤掉不是当前登录导医的订单
            ConsultOrderPO po = iGuideDAO.getObjectByOrderId(iter.next().getOrderId());
            if (po == null) {
                logger.error("导医订单已经不存在，可能被删除了:");
                continue;
                //throw new ServiceException("导医订单已经不存在，可能被删除了");
            }
            Integer guideId = po.getGuideId();
            if (ReqUtil.instance.getUserId() != guideId) {
                iter.remove();//与当前登录的导医不是一个人的数据全部干掉
            }
            ;
        }
        Integer total = 0;
        if (orders.size() > 0) {//大于0 再去查询
            for (OrderVO orderVO : orders) {
                User user = userManager.getUser(orderVO.getDoctorId());
                CureRecord re = new CureRecord();
                re.setOrderId(orderVO.getOrderId());
                List<CureRecord> list = mapper.getEndTime(re);//一个订单会被处理多次
                Long endTime = 0l;
                if (list.size() > 0) {
                    if (null != list.get(0).getUpdateTime() && list.get(0).getUpdateTime() > 0) {//取最后一次的数据作为结束时间
                        endTime = list.get(0).getUpdateTime();
                    }
                }
                orderVO.setConferenceStopTime(endTime.toString());
                orderVO.setDoctorName(user.getName());
                orderVO.setTopPath(user.getHeadPicFileName());
                orderVO.setDoctorId(user.getUserId());
                orderVO.setTelephone(user.getTelephone());
            }
            total = orderService.getGuideAlreadyServicedOrderCount(param);
        }
        return new PageVO(orders, Long.valueOf(total), param.getPageIndex(), param.getPageSize());
    }

    @Resource
    protected CarePlanApiClientProxy carePlanApiClientProxy;

    @Override
    public List<HelpVO> heathWaitOrderList() throws HttpApiException {
        List<HelpVO> list_vo = new ArrayList<HelpVO>();
        //求助订单
        try {
            List<CHelpRecord> list = carePlanApiClientProxy.getHealthCarePlanByCondition();

            if (list.size() > 0) {
                for (CHelpRecord helpRecord : list) {
                    //返回true说明可以满足条件   因为导医相对于一个订单只能有一个正在进行中的服务 不能有多个
                    if (!consultOrderOtherDao.checkByOrderId(helpRecord.getOrderId())) {
                        continue;
                    }
                    ;
                    HelpVO vo = new HelpVO();
                    Order order = orderService.getOne(helpRecord.getOrderId());
                    Patient patient = patientService.findByPk(order.getPatientId());
                    vo.setPatient(patient);//患者信息
                    vo.setOrderId(helpRecord.getOrderId());
                    vo.setCreateTime(helpRecord.getCallTime());
                    vo.setWaitTime(System.currentTimeMillis() - helpRecord.getCallTime());
                    String careTemplateId = order.getCareTemplateId();//健康关怀id
                    //HealthCarePlan plan = healthCarePlanDao.getPlanById(careTemplateId);
                    Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
                    vo.setCareName(pack.getName());
                    vo.setFromId(helpRecord.getId() + "");
                    vo.setCareTemplateId(careTemplateId);
                    vo.setCareType("help");
                    list_vo.add(vo);
                }
            }
        } catch (HttpApiException e) {
            logger.error(e.getMessage(), e);
        }


        //告警订单
//        List<WarningRecord> getWarningOrder = callHelpDao.getWarningOrder();//求助订单
        List<CWarningRecord> getWarningOrder = carePlanApiClientProxy.getRecentWarningRecords();//求助订单
        if (getWarningOrder.size() > 0) {
            for (CWarningRecord warningRecord : getWarningOrder) {
                //返回true说明可以满足条件
                if (!consultOrderOtherDao.checkByOrderId(warningRecord.getOrderId())) {
                    continue;
                }
                ;
                HelpVO vo = new HelpVO();
                Order order = orderService.getOne(warningRecord.getOrderId());
                Patient patient = patientService.findByPk(order.getPatientId());
                vo.setPatient(patient);//患者信息
                vo.setCreateTime(warningRecord.getCreateTime());
                vo.setWaitTime(System.currentTimeMillis() - warningRecord.getCreateTime());
                vo.setOrderId(warningRecord.getOrderId());
                vo.setCareType("warning");
                String careTemplateId = order.getCareTemplateId();//告警id
                vo.setCareTemplateId(careTemplateId);
                vo.setFromId(warningRecord.getId() + "");
                Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
                vo.setCareName(pack.getName());
                list_vo.add(vo);
            }
        }
        list_vo.sort(new Comparator<HelpVO>() {
            @Override
            public int compare(HelpVO o1, HelpVO o2) {
                return o2.getWaitTime().compareTo(o1.getWaitTime());
            }

        });
        return list_vo;
    }

    @Override
    public String receiveCareOrder(HelpVO vo) {
        //首先判断当前的订单有没有被抢过 通过订单id 以及关怀id查询
        boolean fg = consultOrderOtherDao.checkCareOrder(vo);
        if (fg) {
            ConsultOrderOtherPO po = new ConsultOrderOtherPO();
            po.setGuideId(ReqUtil.instance.getUserId());
            po.setCreateTime(System.currentTimeMillis());
            po.setOrderId(vo.getOrderId());
            po.setType(vo.getCareType());
            po.setFromId(vo.getFromId());
            po.setSourceId(vo.getCareTemplateId());
            po.setState(0);
            String id = consultOrderOtherDao.receiveCareOrder(po);
            return id;
        } else {
            throw new ServiceException("该订单已经被抢");
        }
    }

    @Override
    public List<User> getDoctorTeamByOrderId(Integer orderId) {
        List<User> user_list = new ArrayList<User>();
        OrderDoctorExample orderDoctorExample = new OrderDoctorExample();
        orderDoctorExample.createCriteria().andOrderIdEqualTo(orderId);
        //接受的
        List<OrderDoctor> list = orderDoctorMapper.selectByExample(orderDoctorExample);
        if (list.size() > 0) {
            for (OrderDoctor orderDoctor : list) {
                User user = new User();
                user = userManager.getUser(orderDoctor.getDoctorId());
                user.setReceiveRemind(orderDoctor.getReceiveRemind());
                user_list.add(user);

            }
        }
        //不接受的

        return user_list;
    }

    @Override
    public HelpVO getCareOrderDetail(Integer orderId, String careType, String sourceId) throws HttpApiException {
        HelpVO vo = new HelpVO();
        DoctorTimePO remark = iDoctorTimeDAO.getDoctorRemark(orderId);
        vo.setRemark(remark);
        if ("help".equals(careType)) {
            CHelpRecord help = carePlanApiClientProxy.findHelpRecordById(sourceId);
            Order order = orderService.getOne(help.getOrderId());
            Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
            vo.setCareName(pack.getName());
            Patient patient = patientService.findByPk(order.getPatientId());
            vo.setWaitTime(System.currentTimeMillis() - help.getCallTime());
            vo.setPatient(patient);
            vo.setCareType("help");
            vo.setHelpMsg(help.getHelpMsg());
            return vo;
        } else {
            CWarningRecord warin = carePlanApiClientProxy.findWarningRecordById(sourceId);
            Order order = orderService.getOne(warin.getOrderId());
            Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
            vo.setCareName(pack.getName());
            Patient patient = patientService.findByPk(order.getPatientId());
            vo.setPatient(patient);
            vo.setWaitTime(System.currentTimeMillis() - warin.getCreateTime());
            vo.setCareType("warning");
            vo.setCreateTime(warin.getCreateTime());

            try {
                CIllnessAnswerSheet illnessSheet = carePlanApiClientProxy.getIllnessAnswerByCareItem(warin.getCareItemId());
                vo.setAnswer(illnessSheet);
            } catch (HttpApiException e) {
                logger.error(e.getMessage(), e);
            }
            return vo;
        }
    }

    @Override
    public Object updateCareOrder(Integer orderId, String careType, String id) throws HttpApiException {
        if ("help".equals(careType)) {
            return consultOrderOtherDao.updateHelpInfoByOrderId(id);
        } else {
            return consultOrderOtherDao.updateWarningInfoByOrderId(id);
        }
    }

    @Override
    public List<HelpVO> getHandleCareOrder(Integer userId) throws HttpApiException {
        List<HelpVO> list = new ArrayList<HelpVO>();
        List<ConsultOrderOtherPO> poList = consultOrderOtherDao.getHandleCareOrder(userId);
        for (ConsultOrderOtherPO consultOrderOtherPO : poList) {
            HelpVO vo = new HelpVO();
            Order order = orderService.getOne(consultOrderOtherPO.getOrderId());
            String careTemplateId = order.getCareTemplateId();
            vo.setCareTemplateId(careTemplateId);
            Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
            vo.setCareName(pack.getName());
            Patient patient = patientService.findByPk(order.getPatientId());
            vo.setPatient(patient);
            vo.setCareType(consultOrderOtherPO.getType());

            if ("help".equals(consultOrderOtherPO.getType())) {
//                HelpRecord help = callHelpDao.getHelpInfoById(consultOrderOtherPO.getFromId());
                CHelpRecord help = carePlanApiClientProxy.findHelpRecordById(consultOrderOtherPO.getFromId());
                vo.setCreateTime(help.getCallTime());
                vo.setWaitTime(System.currentTimeMillis() - help.getCallTime());
                vo.setFromId(help.getId() + "");
            } else {
//                WarningRecord warin = callHelpDao.getWarningInfoById(consultOrderOtherPO.getFromId());
                CWarningRecord warin = carePlanApiClientProxy.findWarningRecordById(consultOrderOtherPO.getFromId());
                vo.setCreateTime(warin.getCreateTime());
                vo.setWaitTime(System.currentTimeMillis() - warin.getCreateTime());
                vo.setFromId(warin.getId() + "");
            }
            vo.setId(consultOrderOtherPO.getId().toString());
            vo.setOrderId(consultOrderOtherPO.getOrderId());
            list.add(vo);
            list.sort(new Comparator<HelpVO>() {
                @Override
                public int compare(HelpVO o1, HelpVO o2) {
                    return o2.getWaitTime().compareTo(o1.getWaitTime());
                }

            });
        }
        return list;
    }

    @Override
    public PageVO getAppointmentOrders(Integer status, Integer pageIndex, Integer pageSize) {
        return orderService.getAppointmentOrders(status, pageIndex, pageSize);
    }

    @Override
    public AppointmentGuideOrderDetail getAppointmentDetail(Integer orderId) throws HttpApiException {
        AppointmentGuideOrderDetail detail = new AppointmentGuideOrderDetail();
        Order o = orderService.getOne(orderId);
        if (o != null) {
            detail.setOrderId(o.getId());
            detail.setDoctorId(o.getDoctorId());
            detail.setOrderNo(o.getOrderNo() + "");
            detail.setPatientId(o.getPatientId());
            User d = userManager.getUser(o.getDoctorId());
            if (d != null) {
                detail.setDoctorName(d.getName());
                detail.setDoctorTel(d.getTelephone());
                detail.setHeadPicFileName(d.getHeadPicFileName());
                if (d.getDoctor() != null) {
                    detail.setDepartments(d.getDoctor().getDepartments());
                    detail.setTitle(d.getDoctor().getTitle());
                }
            }

            detail.setPatientId(o.getPatientId());
            Patient p = patientMapper.selectByPrimaryKey(o.getPatientId());
            if (p != null) {
                detail.setPatientAgeStr(p.getAgeStr());
                detail.setPatientArea(p.getArea());
                detail.setPatientName(p.getUserName());
                detail.setPatientTel(p.getTelephone());
                detail.setPatientSex(p.getSex() != null ? p.getSex() + "" : null);
            }

            com.dachen.health.pack.patient.model.Disease disease = diseaseService.findByPk(o.getDiseaseId());
            detail.setDisease(disease);


            ImageData imageData = new ImageData();
            imageData.setRelationId(disease.getId());
            List<ImageData> imageDatas = imageDataService.findImgDataByRelationId(imageData);
            if (imageDatas != null && imageDatas.size() > 0) {
                List<String> images = new ArrayList<String>();
                for (ImageData data : imageDatas) {
                    images.add(data.getImageUrl());
                }
                disease.setDiseaseImgs(images);
            }
            DoctorTimePO remark = iDoctorTimeDAO.getDoctorRemark(orderId);
            detail.setRemark(remark);
        }
        return detail;
    }
    @Autowired
    protected ShortUrlComponent shortUrlComponent;

    @Override
    public void submitAppointmentOrder(Integer orderId, String hospitalId, Long appointTime) throws HttpApiException {
        /**
         * 调用通用逻辑
         */
        orderSessionSevice.appointServiceTime(orderId, appointTime, false, hospitalId);
        OrderSession os = orderSessionSevice.findOneByOrderId(orderId);

        /**
         * 发送短信
         */
        //【玄关健康】提醒您：{0}您好，已为您成功预约博德嘉联的{1}医生，订单号为{2}，{3}，{4}，预约时间为{5}，就诊人：{6}。请您登录app尽快完成付款。{7}
        Order o = orderMapper.getOne(orderId);
        Integer userId = o.getUserId();
        User u = userManager.getUser(userId);
        User d = userManager.getUser(o.getDoctorId());
        HospitalVO hospital = baseDataDao.getHospital(hospitalId);
        Patient p = patientMapper.selectByPrimaryKey(o.getPatientId());
        String dateTime = DateUtil.getMinuteDateByLong(appointTime);
        String endTime = DateUtil.getMinuteTimeByLong(appointTime + 30 * 60 * 1000);

		/*final TemplateHelper helper = new TemplateHelper("1",u.getName(),
					d.getName(),
					o.getOrderNo()+"",
					hospital.getName(),
					d.getDoctor().getDepartments(),
					dateTime + "-" +endTime,
					p.getUserName(),
					PackConstants.greneartenURL("1", os.getMsgGroupId(), 1)
				);*/
		/*final String txt = "{0}您好，已为您成功预约博德嘉联的{1}医生，订单号为{2}，{3}，{4}，预约时间为{5}，就诊人：{6}。请您登录app尽快完成付款，{7}，祝早日康复。";
		mobSmsSdk.send(p.getTelephone(),helper.formatText(txt));*/
        if (StringUtils.isNotBlank(p.getTelephone())) {
            String openAppUrl = null;
            //这个比较特殊 根据医生属于哪个平台来确认此医生的患者所属平台
            String platForm = null;
            String signature = null;
            openAppUrl = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", os.getMsgGroupId(), 1));
            platForm = BaseConstants.XG_DOC_APP;
            signature = BaseConstants.XG_SIGN;
            final String msg = baseDataService.toContent("1010", u.getName(), platForm,
                    d.getName(),
                    o.getOrderNo() + "",
                    hospital.getName(),
                    d.getDoctor().getDepartments(),
                    dateTime + "-" + endTime,
                    p.getUserName(), openAppUrl);
            mobSmsSdk.send(p.getTelephone(), msg, signature);
            //发通知
            StringBuffer content = new StringBuffer("");
            String dateTimeBegin = DateUtil.formatDate2Str2(new Date(appointTime));
            content.append("医生预约时间为:" + dateTimeBegin);
            businessServiceMsg.sendNotifyMsgToAll(os.getMsgGroupId(), content.toString());

        }
    }

    @Override
    public List<HospitalInfo> getGroupHospital(String groupId) {
        Group g = groupDao.getById(groupId);

        /**
         * 对之前遗留的数据进行处理
         * 之前是存放的hospitalIds，现在改为hospialInfo对象
         * 第一次取读取的是否，判断新记录是否存在，不存在就按照之前的数据结构进行读取
         */
        if (g != null && g.getConfig() != null) {
            List<String> hospitalIds = g.getConfig().getHospitalIds();
            List<HospitalInfo> hospitalInfo = g.getConfig().getHospitalInfo();
            if (hospitalInfo != null && hospitalInfo.size() > 0) {
                return hospitalInfo;
            } else if (hospitalIds != null && hospitalIds.size() > 0) {
                hospitalInfo = new ArrayList<>();
                List<HospitalVO> list = new ArrayList<HospitalVO>();
                if (hospitalIds != null && hospitalIds.size() > 0) {
                    list = baseDataDao.getHospitals(hospitalIds);
                }
                for (HospitalVO vo : list) {
                    HospitalInfo info = new HospitalInfo();
                    info.setId(vo.getId());
                    info.setName(vo.getName());
                    hospitalInfo.add(info);
                    vo = null;
                }

                return hospitalInfo;


            } else {
                return null;
            }


        }
        return null;

		/*
		if(g != null && g.getConfig() != null){
			List<String> hospitalIds = g.getConfig().getHospitalIds();
			if(hospitalIds != null && hospitalIds.size() > 0){
				return baseDataDao.getHospitals(hospitalIds);
			}
			g.getConfig().getHospitalInfo();

		}*/
        //return null;
    }

    @Override
    public void setAppointmentInfo(String groupId, Boolean openAppointment, Integer appointmentGroupProfit,
                                   Integer appointmentParentProfit, Integer appointmentMin, Integer appointmentMax,
                                   Integer appointmentDefault) throws HttpApiException {
        //修改集团
        groupDao.updateAppointment(groupId, openAppointment, appointmentGroupProfit, appointmentParentProfit);
        FeeParam param = new FeeParam();
        param.setGroupId(groupId);
        param.setAppointmentMin(appointmentMin);
        param.setAppointmentMax(appointmentMax);
        param.setAppointmentDefault(appointmentDefault);
        if (appointmentDefault == null) {
            throw new ServiceException("默认价格不能为空");
        }
        if (appointmentDefault.intValue() < 0) {
            throw new ServiceException("默认价格不能小于零");
        }
        if (appointmentDefault != null && appointmentMin != null && appointmentMax != null) {
            if (appointmentDefault.intValue() > appointmentMax.intValue() || appointmentDefault.intValue() < appointmentMin.intValue()) {
                throw new ServiceException("默认价格，必须在价格范围之间");
            }
        }
        feeDao.save(param);
        //根据 openAppointment 修改集团医生的预约包，以及医生提成
        List<GroupDoctorVO> gds = gdService.getGroupDoctorListByGroupId(groupId);
        for (GroupDoctorVO gd : gds) {
            Pack pack = packService.getDoctorPackDBData(gd.getDoctorId(), PackEnum.PackType.appointment.getIndex());
            if (openAppointment) {
                if (pack != null) {
                    Map<String, Object> sqlParam = Maps.newHashMap();
//					sqlParam.put("status", PackEnum.PackStatus.close.getIndex());
                    sqlParam.put("price", pack.getPrice() == null ? appointmentDefault : pack.getPrice());
                    sqlParam.put("doctorId", gd.getDoctorId());
                    packMapper.updateDoctorAppointmentPack(sqlParam);
                } else {
                    //新增
                    Pack doctorPack = new Pack();
                    doctorPack.setName(PackEnum.PackType.appointment.getTitle());
                    doctorPack.setIsSearched(0);
                    doctorPack.setPrice(Long.valueOf(appointmentDefault));
                    doctorPack.setPackType(PackEnum.PackType.appointment.getIndex());
                    doctorPack.setGroupId(groupId);
                    doctorPack.setDoctorId(gd.getDoctorId());
                    doctorPack.setStatus(PackEnum.PackStatus.close.getIndex());
                    packService.addPack(doctorPack);
                }
            } else {
                if (pack != null) {
                    //更新
                    Pack doctorPack = new Pack();
                    doctorPack.setId(pack.getId());
                    doctorPack.setPrice(pack.getPrice() == null ? appointmentDefault : pack.getPrice());
                    packService.updatePack(doctorPack);
                }
            }
            groupProfitDao.updateAppointment(groupId, gd.getDoctorId(), appointmentGroupProfit, appointmentParentProfit);
        }
    }

    @Override
    public Object getAppointmentInfo(String groupId) {
        Map<String, Object> map = new HashMap<>();
        Group g = groupDao.getById(groupId);
        FeeVO feeVo = feeDao.get(groupId);
        if (feeVo != null) {
            map.put("appointmentMin", feeVo.getAppointmentMin());// 收费价格范围
            map.put("appointmentMax", feeVo.getAppointmentMax());
            map.put("appointmentDefault", feeVo.getAppointmentDefault());// 默认价格
        }
        if (g != null && g.getConfig() != null) {
            GroupConfig gc = g.getConfig();
            map.put("openAppointment", gc.isOpenAppointment());
            map.put("appointmentGroupProfit", gc.getAppointmentGroupProfit());
            map.put("appointmentParentProfit", gc.getAppointmentParentProfit());
        }
        return map;
    }

    @Override
    public void setGroupHospital(GroupParam param) throws HttpApiException {
        /**
         * 保存之前，先对传入的医院list和之前的list作一个对比
         * 删除操作的，需要先去判断是否有对应的医生将其设置为了值班
         * 有的话，需要有对应的通知去通知医生，并删除对应的医生值班表
         */
        if (param.getId() == null) {
            throw new ServiceException("请传入集团id");
        }
        //新增的医院列表
        List<HospitalInfo> newHospital = param.getConfig().getHospitalInfo();
        //原来的医院列表
        List<HospitalInfo> hospitalInfos = groupDao.getById(param.getId()).getConfig().getHospitalInfo();
        //删除的医院列表
        List<HospitalInfo> deleteHospital = new ArrayList<>();
        if (hospitalInfos != null && hospitalInfos.size() > 0) {
            if (newHospital != null && newHospital.size() > 0) {
                for (HospitalInfo hos : hospitalInfos) {

                    boolean is_del = true;
                    for (HospitalInfo newHos : newHospital) {
                        /**
                         * 由于之前有些数据没有保存id，判断id是否为空，为空的用name来进行判断是否为相同医院
                         */
                        if (newHos.getId() == null || hos.getId() == null) {
                            if (newHos.getName().equals(hos.getName())) {
                                is_del = false;
                                break;
                            }
                        } else {
                            if (newHos.getId().equals(hos.getId())) {
                                is_del = false;
                                break;
                            }
                        }
                    }
                    if (is_del) {
                        deleteHospital.add(hos);
                    }

                }

            } else {
                deleteHospital = hospitalInfos;
            }

        }


        if (deleteHospital != null && deleteHospital.size() > 0) {
            List<String> list = new ArrayList<>();
            for (HospitalInfo temp : deleteHospital) {
                list.add(temp.getName());
            }
            List<Integer> doctorIds = null;
            List<String> gIds = new ArrayList<String>();
            gIds.add(param.getId());
            //获取集团所有医生
            doctorIds = gdocDao.findAllDoctorIdsInGroupIds(gIds);
            //获取删除信息的列表
            Map<String, List<OfflineParam>> deloffline = offlineDao.getDoctor(doctorIds, list);

            if (deloffline != null && deloffline.size() > 0) {
                for (String key : deloffline.keySet()) {
                    List<OfflineParam> pa = deloffline.get(key);
                    //删除医院，发起相应的通知给用户
                    for (OfflineParam offparam : pa) {
                        evaluationService.sendSystem(offparam.getDoctorId(), offparam.getHospital());

                    }

                }


            }


        }


        //保存对应的信息
        groupDao.setGroupHospital(param);
    }

    @Override
    public Object getHaveAppointmentListByDate(OrderParam param) {
        List<ScheduleRecordVO> recordVos = new ArrayList<ScheduleRecordVO>();
        Calendar c = Calendar.getInstance();
        try {
            Date startDate = DateUtil.getFirstDayOfMonth(new Date(param.getOppointTime()));
            Date endDate = DateUtil.getLastDayOfMonth(new Date(param.getOppointTime()));
            //构造参数值
            buildMonthRecord(recordVos, c, startDate, endDate);

            c.setTime(startDate);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            Long oppointTime = c.getTimeInMillis();
            for (ScheduleRecordVO srvo : recordVos) {
                param.setOppointTime(oppointTime);
				/*List<OrderVO> orderList=orderService.getAppointmentListByCondition(param);
				if(orderList!=null&&orderList.size()>0){
					srvo.setIsTrue(1);
				}*/
                long count = orderService.getHaveAppointmentListByDateByMongo(param);
                if (count > 0) {
                    srvo.setIsTrue(1);
                }
                oppointTime += 86400000;
            }
        } catch (Exception e) {
            throw new ServiceException("请输入正确的日期格式！");
        }
        return recordVos;
    }

    public Object getAppointmentPaidOrders(String groupId, String hospitalId, Long date) {
        Map<String, Long> timeAreaMap = DateUtil.getDayStartAndEndTime(date);
        /**
         * 最终是从user数据表中获取集合数据
         * 1、根据时间 groupId hospitalId 从p_order中获取doctorId 和数量
         * 2、迭代集合获取userId 再从user中获取医生数据并进行排序
         */
        return orderService.getAppointmentPaidOrders(groupId, hospitalId, timeAreaMap.get("start"), timeAreaMap.get("end"));
    }

    private Map<Integer, Patient> setPatientData(List<Patient> list) {
        Map<Integer, Patient> map = new HashMap<Integer, Patient>();
        for (Patient p : list) {
            map.put(p.getId(), p);
        }
        return map;
    }

    private void setOrderStatus(List<OrderVO> orderList, List<Map<String, Object>> dataList) {
        for (Map<String, Object> map : dataList) {
            Object orderIdStr = map.get("orderId");
            if (orderIdStr == null) {
                continue;
            }
            Integer orderId = Integer.parseInt(orderIdStr.toString());
            for (OrderVO vo : orderList) {
                if (vo.getOrderId().intValue() == orderId.intValue()) {
                    map.put("orderStatus", vo.getOrderStatus());
                }
            }
        }

    }

    @Override
    public PageVO searchAppointmentOrderByKeyword(String keyword, Integer pageIndex, Integer pageSize) {
        String groupId = groupService.isOpenSelfGuideAndGetGroupId(ReqUtil.instance.getUserId(), UserType.DocGuide.getIndex());
        if (StringUtils.isNotBlank(groupId)) {
            List<Patient> patients = patientMapper.findPatientsByKeyword(keyword);
            Map<Integer, Patient> pMap = setPatientData(patients);
            List<Integer> patientIds = new ArrayList<Integer>(pMap.keySet());

            if (patientIds != null && patientIds.size() > 0) {
                List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
                Long count = offlineDao.searchAppointmentOrder4GuideCount(patientIds);
                if (count != null && count > 0) {
                    List<Integer> orderIdList = new ArrayList<Integer>();
                    List<OfflineItem> itemList = offlineDao.searchAppointmentOrder4Guide(patientIds, pageIndex, pageSize);
                    for (OfflineItem item : itemList) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("appointmentStart", item.getStartTime());
                        map.put("appointmentEnd", item.getEndTime());
                        Patient p = pMap.get(item.getPatientId());
                        if (p != null) {
                            map.put("patientName", p.getUserName());
                            map.put("patientTelephone", p.getTelephone());
                        }
                        User d = userManager.getUser(item.getDoctorId());
                        map.put("doctorName", d.getName());
                        map.put("orderId", item.getOrderId());
                        if (item.getOrderId() != null) {
                            orderIdList.add(item.getOrderId());
                        }
//						map.put("orderStatus", item.getStatus());
                        dataList.add(map);
                    }
                    OrderParam param = new OrderParam();
                    param.setOrderIds(orderIdList);
                    List<OrderVO> orderList = orderMapper.findByIds(param);
                    setOrderStatus(orderList, dataList);
                }
                PageVO pageVo = new PageVO();
                pageVo.setPageData(dataList);
                pageVo.setTotal(count);
                return pageVo;
            }
        }
        return new PageVO();
    }

    /**
     * 创建本月日程记录，默认为空
     *
     * @param recordVos
     * @param c
     * @param startDate
     * @param endDate
     */
    private void buildMonthRecord(List<ScheduleRecordVO> recordVos, Calendar c, Date startDate, Date endDate) {
        c.setTime(endDate);
        Integer dayss = c.get(Calendar.DAY_OF_MONTH);
        c.setTime(startDate);
        for (int i = 1; i <= dayss; i++) {
            ScheduleRecordVO recorde = new ScheduleRecordVO();
            recorde.setDayNum(i);
            recorde.setIsTrue(0);
            int week = 0;
            if (c.get(Calendar.DAY_OF_WEEK) == 1) {
                week = 7;
            } else {
                week = c.get(Calendar.DAY_OF_WEEK) - 1;
            }
            recorde.setWeek(week);
            recordVos.add(recorde);
            c.add(Calendar.DATE, 1);
        }
    }

    @Override
    public Object getAppointmentListByCondition(OrderParam param) {
        Map<String, Object> map = new HashMap<String, Object>();
        Date date = new Date(param.getOppointTime());
        //构造参数值
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        param.setOppointTime(c.getTimeInMillis());
        List<OrderVO> orderList = orderService.getAppointmentListByConditionByMongo(param);

        //Map<String,List<OrderVO>> orderMap= orderService.getAppointmentListByConditionByMongo(param);

        map.put("orderList", orderList);
        map.put("count", orderList != null ? orderList.size() : 0);
        return map;
    }

    @Override
    public PageVO doctorOfflinesByDate(String groupId, String hospitalId, Long date, Integer period, Integer pageSize, Integer pageIndex) {
        PageVO pageVO = new PageVO();
        Long dateTime = DateUtil.getDayBegin(date);
        Collection<BasicDBObject> offlineCounts = offlineService.getDoctorOfflineAndCount(hospitalId, dateTime, period);
        if (offlineCounts != null && offlineCounts.size() > 0) {
            Iterator<BasicDBObject> ite = offlineCounts.iterator();
            List<Integer> doctorIds = new ArrayList<Integer>();
            Map<Integer, Map<String, Object>> map = new HashMap<Integer, Map<String, Object>>();
            while (ite.hasNext()) {
                DBObject obj = ite.next();
                Double tempDouble = MongodbUtil.getDouble(obj, "doctorId");
                Integer doctorId = tempDouble != null ? tempDouble.intValue() : null;
                doctorIds.add(doctorId);
                Map<String, Object> innerMap = new HashMap<String, Object>();
                innerMap.put("doctorId", doctorId);
/*				tempDouble = MongodbUtil.getDouble(obj, "totalCount");
				innerMap.put("totalCount", tempDouble != null ? tempDouble.intValue() : 0);
				innerMap.put("beginTime", MongodbUtil.getLong(obj, "beginTime"));
				innerMap.put("overTime", MongodbUtil.getLong(obj, "overTime"));*/
                tempDouble = MongodbUtil.getDouble(obj, "realCount");
                innerMap.put("realCount", tempDouble != null ? tempDouble.intValue() : 0);

                tempDouble = MongodbUtil.getDouble(obj, "week");
                Integer week = tempDouble != null ? tempDouble.intValue() : null;
                List<Offline> offlinePos = offlineDao.findOfflineByDoctorPeriod(hospitalId, doctorId, week, period);

                if (offlinePos != null && offlinePos.size() > 0) {
                    long totalCount = 0;
                    List<Map<String, Long>> timeSegment = new ArrayList<Map<String, Long>>();
                    for (Offline offlinePo : offlinePos) {
                        Map<String, Long> segmentMap = new HashMap<String, Long>();
                        long start = offlinePo.getStartTime();
                        long end = offlinePo.getEndTime();
                        totalCount += (end - start) / DateUtil.minute30millSeconds;
                        segmentMap.put("beginTime", start);
                        segmentMap.put("overTime", end);
                        timeSegment.add(segmentMap);
                    }
                    innerMap.put("totalCount", totalCount);
                    innerMap.put("timeSegment", timeSegment);
                } else {
                    innerMap.put("totalCount", 0);
                    innerMap.put("timeSegment", "");
                }
                map.put(doctorId, innerMap);
            }

            pageSize = pageSize == null ? pageVO.getPageSize() : pageSize;
            pageIndex = pageIndex == null ? pageVO.getPageIndex() : pageIndex;
            List<User> users = userManager.findDoctorsInIds(doctorIds, pageIndex, pageSize);
            for (User d : users) {
                Map<String, Object> innerMap = map.get(d.getUserId());
                innerMap.put("name", d.getName());
                innerMap.put("headPicFileName", d.getHeadPicFileName());
                if (d.getDoctor() != null) {
                    innerMap.put("departments", d.getDoctor().getDepartments());
                    innerMap.put("title", d.getDoctor().getTitle());
                }
            }
            pageVO.setPageData(new ArrayList<Map<String, Object>>(map.values()));
            pageVO.setPageSize(doctorIds.size());
        }
        return pageVO;
    }

    @Override
    public Map<String, List<OfflineItem>> getPatientAppointmentByCondition(OrderParam param) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(param.getOppointTime());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        param.setOppointTime(c.getTimeInMillis());
        Map<String, List<OfflineItem>> map = new HashMap<String, List<OfflineItem>>();
        map.put("1", new ArrayList<OfflineItem>());
        map.put("2", new ArrayList<OfflineItem>());
        map.put("3", new ArrayList<OfflineItem>());

        List<OfflineItem> offlineItemList = offlineDao.getPatientAppointmentByCondition(param.getHospitalId(), param.getDoctorId(), param.getOppointTime());
        for (OfflineItem oi : offlineItemList) {
            if (oi.getPeriod() == 1) {
                map.get("1").add(oi);
            } else if (oi.getPeriod() == 2) {
                map.get("2").add(oi);
            } else if (oi.getPeriod() == 3) {
                map.get("3").add(oi);
            }
        }
        return map;
    }

    @Override
    public void changeAppointmentTime(String offlineItemId, Integer orderId) throws HttpApiException {
        Order o = orderMapper.getOne(orderId);
        if (o != null) {
            OfflineItem oldItem = offlineDao.findOfflineItemByOrderId(orderId, o.getDoctorId());
            OfflineItem item = offlineDao.findOfflineItemById(offlineItemId);
            if (item.getStatus() != ScheduleEnum.OfflineStatus.待预约.getIndex()) {
                throw new ServiceException("医生已有预约，请选择其他时间");
            }
            if (item.getDoctorId().intValue() != o.getDoctorId().intValue()) {
                logger.error("预约订单医生，数据错乱 order[doctorId]=" + o.getDoctorId() + "offlineItem[doctorId]=" + item.getDoctorId());
                throw new ServiceException("预约医生有误，请重新选择");
            }
            synchronized (SysConstants.obj) {
                item = offlineDao.findOfflineItemById(offlineItemId);
                if (item.getStatus() != ScheduleEnum.OfflineStatus.待预约.getIndex()) {
                    throw new ServiceException("医生已有预约，请选择其他时间");
                }
                offlineDao.updateOfflineItemStatus(offlineItemId, ScheduleEnum.OfflineStatus.已预约.getIndex());
                offlineDao.cancelOfflineItem(orderId);
                offlineDao.updateOfflineItemOrderInfo(offlineItemId, orderId, o.getPatientId());
                Map<String, Object> sqlmap = new HashMap<>();
                sqlmap.put("orderId", orderId);
                sqlmap.put("appointmentTime", item.getStartTime());
                orderSessionMapper.changeAppointmentTime(sqlmap);
            }
            OrderSession os = orderSessionService.findOneByOrderId(orderId);
            Map<String, Object> mapString = new HashMap<String, Object>();
            mapString.put("order", o);
            mapString.put("newOfflineItem", item);
            mapString.put("oldOfflineItem", oldItem);
            orderService.sendOrderNoitfy(o.getUserId() + "", o.getDoctorId() + "", os.getMsgGroupId(), OrderNoitfyType.changeAppointmentTime, mapString);
            messageGroupService.updateGroupBizState(os.getMsgGroupId(), MessageGroupEnum.CHANGE_APPOINTMENT_TIME.getIndex());

        }
    }

    @Override
    public Map<String, List<Map<String, Object>>> getDoctorOneDayOffline(String hospitalId, Long date, Integer doctorId) {
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        long dateTime = DateUtil.getDayBegin(date);
        List<OfflineItem> itemList = offlineDao.getDoctorOneDayOffline(hospitalId, dateTime, doctorId);
        if (itemList != null && itemList.size() > 0) {
            for (OfflineItem item : itemList) {
                String period = item.getPeriod() + "";
                List<Map<String, Object>> list = map.get(period);
                if (list == null) {
                    list = new ArrayList<>();
                }
                Map<String, Object> innerMap = new HashMap<>();
                innerMap.put("startTime", item.getStartTime());
                innerMap.put("endTime", item.getEndTime());
                innerMap.put("id", item.getId());
                innerMap.put("status", item.getStatus());
                list.add(innerMap);
                map.put(period, list);
            }
        }
        return map;
    }

    @Override
    public int isTimeToAppointment(Long startTime, Integer doctorId) {
        OfflineItem item = offlineDao.getOfflineItem(startTime, doctorId);
        return item == null ? 1 : 2;
    }

    @Override
    public Object takeAppointmentOrder(AppointmentOrderWebParams webParams) throws HttpApiException {

        validateTakeAppointmentOrderParam(webParams);
        OfflineItem offlineItem = webParams.getOfflineItem();
        Patient patient = webParams.getPatient();
        OrderParam orderParam = webParams.getOrderParam();


        /**
         * 1、添加患者信息
         */
        Integer patientId = patient.getId();
        Integer userId = null;
        //boolean noRegister = false;
        String tel = patient.getTelephone();
        if (patientId == null) {
            Map<String, Object> map = invitePatientService.addPatientByGuide(patient);
            Patient p = (Patient) map.get("patient");
            map.put("patient", patient);
            //noRegister = (boolean) map.get("noRegister");
            patientId = p.getId();
            userId = p.getUserId();
        } else {
            Patient p = patientService.findByPk(patientId);
            if (p != null) {
                userId = p.getUserId();
                tel = p.getTelephone();
            } else {
                throw new ServiceException("根据患者id = " + patientId + " 找不到对应的患者");
            }
        }


        /**
         * 2、添加医生预约信息
         */
        String offlineItemId = offlineItem.getId();
        Integer doctorId = null;
        if (StringUtils.isBlank(offlineItemId)) {
            offlineItemId = addOfflineItem(offlineItem);
            doctorId = offlineItem.getDoctorId();
        } else {
            offlineItem = offlineDao.findOfflineItemById(offlineItemId);
            doctorId = offlineItem.getDoctorId();
        }

        /**
         * 3、下订单
         */
        UserSession session = ReqUtil.instance.getUser(userId);
        orderParam.setDoctorId(doctorId);
        orderParam.setUserId(userId);
        orderParam.setPatientId(patientId);
        orderParam.setOrderType(OrderType.appointment.getIndex());
        orderParam.setPackType(PackType.appointment.getIndex());
        Pack pack = packService.getDoctorPackDBData(doctorId, PackType.appointment.getIndex());
        orderParam.setPackId(pack.getId());
        orderParam.setTelephone(tel);
        orderParam.setOfflineItemId(offlineItemId);
        orderParam.setCreateUserType(UserType.DocGuide.getIndex());
        orderParam.setOrderStatus(OrderStatus.待支付.getIndex());
        PreOrderVO info = orderService.add(orderParam, session);

        /**
         * 4、更新日程
         */
        scheduleService.createOrderSchedule(info.getOrderId(), offlineItem.getStartTime());

        /**
         * 5、发短信
         */
        final String shortMessage;
        String shortUrl = shortUrlComponent.generateShortUrl(
                PropertiesUtil.getContextProperty("application.rootUrl")
                        + PropertiesUtil.getContextProperty("group.web.prefix")
                        + "/bd_h5/attachments/offlineOrder/offlineOrder.html?orderId=" + info.getOrderId()
        );
        //助手已帮您预约了\n服务名称：名医面对面\n服务医生：{0}\n服务时间：{1}\n执业地点：{2}\n订单编号：{3}\n为确保能准确就医，请您及时付款，点击{4}付款。
        String doctorName = userManager.getUser(doctorId).getName();
        String dateStr = DateUtil.formatDate2Str2(new Date(offlineItem.getStartTime())) + "-" +
                DateUtil.getMinuteTimeByLong(offlineItem.getStartTime() + DateUtil.minute30millSeconds);
        String hospitalName = baseDataDao.getHospital(offlineItem.getHospitalId()).getName();
        Integer orderNo = info.getOrderNo();
        shortMessage = baseDataService.toContent("1037", doctorName, dateStr, hospitalName, orderNo + "", shortUrl);
        mobSmsSdk.send(tel, shortMessage);
        return info;
    }

    private String addOfflineItem(OfflineItem offlineItem) {
        if (StringUtils.isNoneBlank(offlineItem.getId())) {
            return offlineItem.getId();
        }
        long startTime = offlineItem.getStartTime();
        long dayBegin = DateUtil.getDayBegin(startTime);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        int nowWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (nowWeek == 0) {
            nowWeek = 7;
        }
        long _12PointTime = dayBegin + 12 * 60 * 60 * 1000;
        long _18PointTime = dayBegin + 18 * 60 * 60 * 1000;
        int period = 0;
        if (startTime < _12PointTime) {
            period = 1;
        } else if (startTime < _18PointTime) {
            period = 2;
        } else {
            period = 3;
        }
        offlineItem.setWeek(nowWeek);
        offlineItem.setPeriod(period);
        offlineItem.setCreateTime(System.currentTimeMillis());
        offlineItem.setStatus(ScheduleEnum.OfflineStatus.待预约.getIndex());
        offlineItem.setDateTime(dayBegin);
        offlineItem.setDataForm(OfflineDateFrom.导医添加.getIndex());
        return offlineDao.insertOfflineItem(offlineItem).getId();
    }

    private void validateTakeAppointmentOrderParam(AppointmentOrderWebParams webParams) {
        OfflineItem offlineItem = webParams.getOfflineItem();
        Patient patient = webParams.getPatient();
        OrderParam orderParam = webParams.getOrderParam();
        if (offlineItem == null ||
                patient == null ||
                orderParam == null) {
            throw new ServiceException("缺少必要的参数");
        }
        if (StringUtils.isBlank(offlineItem.getId())) {
            if (offlineItem.getStartTime() == null ||
                    offlineItem.getEndTime() == null ||
                    offlineItem.getDoctorId() == null ||
                    StringUtils.isBlank(offlineItem.getHospitalId())
                    ) {
                throw new ServiceException("缺少必要的医生排班参数");
            }
        }

        if (patient.getId() == null) {
            if (StringUtils.isBlank(patient.getUserName()) ||
                    patient.getSex() == null ||
                    patient.getBirthday() == null ||
                    StringUtils.isBlank(patient.getIdtype()) ||
                    StringUtils.isBlank(patient.getIdcard()) ||
                    patient.getBirthday() == null ||
                    StringUtils.isBlank(patient.getArea()) ||
                    StringUtils.isBlank(patient.getRelation()) ||
                    !CheckUtils.checkMobile(patient.getTelephone())) {
                throw new ServiceException("缺少必要的患者参数");
            }
        }

        if (StringUtils.isBlank(orderParam.getDiseaseDesc())) {
            throw new ServiceException("缺少必要的订单病情参数");
        }
    }

    @Override
    public void updateGroupConfigAndFee(GroupConfigAndFeeParam param) throws HttpApiException {
        /**
         * 更新|插入 类型 1、 名医面对面 2 、 图文咨询 3 、电话咨询 4、健康关怀 5、收费项
         */
        switch (param.getType()) {
            case 1: // 名医面对面
                updateAppointment(param);
                // 处理收费设置修改后集团里医生的图文套餐、电话套餐、关怀计划、名医面对面
//				packService.executeFeeUpdate(param.getGroupId(),null);
                break;
            case 2: //  图文咨询
                updateText(param);
                // 处理收费设置修改后集团里医生的图文套餐、电话套餐、关怀计划、名医面对面
//				packService.executeFeeUpdate(param.getGroupId(),null);
                break;
            case 3://电话咨询
                updatePhone(param);
                // 处理收费设置修改后集团里医生的图文套餐、电话套餐、关怀计划、名医面对面
//				packService.executeFeeUpdate(param.getGroupId(),null);
                break;
            case 4: // 健康关怀
                updateCarePlan(param);
                // 处理收费设置修改后集团里医生的图文套餐、电话套餐、关怀计划、名医面对面
//				packService.executeFeeUpdate(param.getGroupId(),null);
                break;
            case 5: // 收费项
                updateChargeItem(param);
                break;
            case 6: // 在线门诊
                updateClinic(param);
                break;
            case 7: // 会诊
                updateConsultation(param);
                break;
            default:
                throw new ServiceException("未知type=" + param.getType() + "参数值");
        }

    }

    /**
     * 在线门诊
     */
    private void updateConsultation(GroupConfigAndFeeParam param) {
        String groupId = param.getGroupId();// 集团Id
        Integer consultationGroupProfit = param.getConsultationGroupProfit();// 集团抽成比例
        Integer consultationParentProfit = param.getConsultationParentProfit();// 上级抽成比例

        Group group = new Group();
        group.setId(groupId);
        GroupConfig config = new GroupConfig();
        config.setConsultationGroupProfit(consultationGroupProfit);
        config.setConsultationParentProfit(consultationParentProfit);
        ;
        group.setConfig(config);
        groupService.updateGroupProfit(group);
    }

    /**
     * 在线门诊
     */
    private void updateClinic(GroupConfigAndFeeParam param) {
        String groupId = param.getGroupId();// 集团Id
        Integer clinicGroupProfit = param.getClinicGroupProfit();// 集团抽成比例
        Integer clinicParentProfit = param.getClinicParentProfit();// 上级抽成比例

        Group group = new Group();
        group.setId(groupId);
        GroupConfig config = new GroupConfig();
        config.setClinicGroupProfit(clinicGroupProfit);
        config.setClinicParentProfit(clinicParentProfit);
        ;
        group.setConfig(config);
        groupService.updateGroupProfit(group);
    }

    /**
     * 收费项
     */
    private void updateChargeItem(GroupConfigAndFeeParam param) {
        String groupId = param.getGroupId();// 集团Id
        Integer chargeItemGroupProfit = param.getChargeItemGroupProfit();// 集团抽成比例
        Integer chargeItemParentProfit = param.getChargeItemParentProfit();// 上级抽成比例

        Group group = new Group();
        group.setId(groupId);
        GroupConfig config = new GroupConfig();
        config.setChargeItemGroupProfit(chargeItemGroupProfit);
        config.setChargeItemParentProfit(chargeItemParentProfit);
        ;
        group.setConfig(config);
        groupService.updateGroupProfit(group);
    }

    private void updateCarePlan(GroupConfigAndFeeParam param) {
        String groupId = param.getGroupId();// 集团Id
        Integer carePlanMax = param.getCarePlanMax();  // 价格范围  大
        Integer carePlanMin = param.getCarePlanMin();  // 价格范围  小

        FeeParam feeParam = new FeeParam();
        feeParam.setCarePlanMax(carePlanMax);
        feeParam.setCarePlanMin(carePlanMin);
        feeParam.setGroupId(groupId);
        feeService.save(feeParam);

        Integer carePlanGroupProfit = param.getCarePlanGroupProfit();// 集团抽成比例
        Integer carePlanParentProfit = param.getCarePlanParentProfit();// 上级抽成比例

        Group group = new Group();
        group.setId(groupId);
        GroupConfig config = new GroupConfig();
        config.setCarePlanGroupProfit(carePlanGroupProfit);
        ;
        config.setCarePlanParentProfit(carePlanParentProfit);
        group.setConfig(config);
        groupService.updateGroupProfit(group);
    }

    /**
     * 电话咨询
     */
    private void updatePhone(GroupConfigAndFeeParam param) {
        String groupId = param.getGroupId();// 集团Id
        Integer phoneMax = param.getPhoneMax();  // 价格范围  大
        Integer phoneMin = param.getPhoneMin();  // 价格范围  小

        FeeParam feeParam = new FeeParam();
        feeParam.setPhoneMax(phoneMax);
        feeParam.setPhoneMin(phoneMin);
        feeParam.setGroupId(groupId);
        feeService.save(feeParam);

        Integer phoneGroupProfit = param.getPhoneGroupProfit();// 集团抽成比例
        Integer phoneParentProfit = param.getPhoneParentProfit();// 上级抽成比例

        Group group = new Group();
        group.setId(groupId);
        GroupConfig config = new GroupConfig();
        config.setPhoneGroupProfit(phoneGroupProfit);
        config.setPhoneParentProfit(phoneParentProfit);
        group.setConfig(config);
        groupService.updateGroupProfit(group);
    }

    /**
     * 图文咨询
     */
    private void updateText(GroupConfigAndFeeParam param) {
        String groupId = param.getGroupId();// 集团Id
        Integer textMax = param.getTextMax();  // 价格范围  大
        Integer textMin = param.getTextMin();  // 价格范围  小

        FeeParam feeParam = new FeeParam();
        feeParam.setTextMax(textMax);
        feeParam.setTextMin(textMin);
        feeParam.setGroupId(groupId);
        feeService.save(feeParam);

        Integer textGroupProfit = param.getTextGroupProfit();// 集团抽成比例
        Integer textParentProfit = param.getTextParentProfit();// 上级抽成比例

        Group group = new Group();
        group.setId(groupId);
        GroupConfig config = new GroupConfig();
        config.setTextGroupProfit(textGroupProfit);
        config.setTextParentProfit(textParentProfit);
        group.setConfig(config);
        groupService.updateGroupProfit(group);
    }

    /**
     * 名医面对面
     *
     * @throws HttpApiException
     */
    private void updateAppointment(GroupConfigAndFeeParam param) throws HttpApiException {
        String groupId = param.getGroupId();// 集团Id
        Boolean openAppointment = param.getOpenAppointment(); // 是否开启
        Integer appointmentMax = param.getAppointmentMax();  // 价格范围  大
        Integer appointmentMin = param.getAppointmentMin();  // 价格范围  小
        Integer appointmentDefault = param.getAppointmentDefault();

//		FeeParam feeParam = new FeeParam();
//		feeParam.setAppointmentMax(appointmentMax);
//		feeParam.setAppointmentMin(appointmentMin);
//		feeParam.setAppointmentDefault(appointmentDefault);
//		feeParam.setGroupId(groupId);
//		Group group = groupService.getGroupById(groupId);
//		List<Integer> doctorIds= baseUserService.getDoctorIdByGroup(groupId);
//		feeService.save(feeParam);

        Integer appointmentGroupProfit = param.getAppointmentGroupProfit();// 集团抽成比例
        Integer appointmentParentProfit = param.getAppointmentParentProfit();// 上级抽成比例

//		Group group =  new Group();
//		group.setId(groupId);
//		GroupConfig config = new GroupConfig();
//		config.setAppointmentGroupProfit(appointmentGroupProfit);
//		config.setAppointmentParentProfit(appointmentParentProfit);
//		config.setOpenAppointment(openAppointment);
//		group.setConfig(config);
        this.setAppointmentInfo(groupId, openAppointment, appointmentGroupProfit, appointmentParentProfit, appointmentMin, appointmentMax, appointmentDefault);
//		groupService.updateGroupProfit(group);
    }

    /**
     * @param pageIndex
     * @param pageSize
     * @return
     * @apiSuccess {Integer}        pageData.userId               患者的用户id
     * @apiSuccess {Integer}        pageData.msgGroupId           客服会话id
     * @apiSuccess {String}         pageData.userName			  患者用户名
     * @apiSuccess {String}         pageData.headPicFileName      患者头像
     * @apiSuccess {Integer}         pageData.sex                  性别
     * @apiSuccess {String}         pageData.ageStr             年龄
     */
    @Override
    public PageVO getBeServicedPatients(Integer pageIndex, Integer pageSize) {
        PageVO pageVo = new PageVO();
        pageIndex = pageIndex == null ? pageVo.getPageIndex() : pageIndex;
        pageSize = pageSize == null ? pageVo.getPageSize() : pageSize;
        Set<String> userIds = jedisTemplate.zrangeByScore(PATIENT_MESSAGE_TO_GUIDE_POOL_BE_SERVICED, "-inf", "+inf", pageIndex * pageSize, pageSize);
        final List<Map<String, Object>> list = new ArrayList<>(userIds.size());
        long count = jedisTemplate.zcard(PATIENT_MESSAGE_TO_GUIDE_POOL_BE_SERVICED);
        if (count > 0) {
            userIds.stream().map(Integer::valueOf).forEach(id -> {
                User u = userManager.getUser(id);
                Map<String, Object> item = new HashMap<>();
                item.put("userId", u.getUserId());
                item.put("msgGroupId", SysConstant.getGuideDocGroupId(u.getUserId() + ""));
                item.put("userName", u.getName());
                item.put("headPicFileName", u.getHeadPicFileName());
                item.put("sex", u.getSex());
                item.put("ageStr", u.getAgeStr());
                list.add(item);
            });
        }
        pageVo.setPageData(list);
        pageVo.setTotal(count);
        return pageVo;
    }

    @Override
    public void replyPatientMessage(Integer userId) {
        /**
         * 1 . 从被服务的患者池中移除当前用户id （PATIENT_MESSAGE_TO_GUIDE_POOL_BE_SERVICED）
         * 2 . 添加用户id 该导医的已服务患者池
         */
        String member = userId + "";
        RedisLock lock = new RedisLock();
        boolean locked = lock.lock(member, LockType.customPatientMessage);
        if (!locked) {
            throw new ServiceException("接单失败：该订单已被锁定。");
        }
        try {
            boolean removeOk = jedisTemplate.zrem(PATIENT_MESSAGE_TO_GUIDE_POOL_BE_SERVICED, member);
            if (removeOk) {
                String guideId = ReqUtil.instance.getUserId() + "";
                jedisTemplate.zadd(PATIENT_MESSAGE_TO_GUIDE_POOL_SERVICING + guideId, System.currentTimeMillis(), member);
                GroupInfo groupInfo = (GroupInfo) msgService.createGroup(member, guideId, null);
                businessServiceMsg.sendUserToUserMsg(guideId, groupInfo.getGid(), "您好，导医[" + guideId + "]很高兴为您服务，有任何问题或补充其他请在对话框中输入");
                CustomerPatientRecord record = new CustomerPatientRecord();
                long now = System.currentTimeMillis();
                record.setCustomerId(ReqUtil.instance.getUserId());
                record.setPatientUserId(userId);
                record.setStartTime(now);
                record.setStatus(CustomerPatientRecord.SERVICE_ING);
                record.setDateTime(DateUtil.getDayBegin(now));
                iGuideDAO.addCustomerPatientRecord(record);
            }
        } catch (HttpApiException e) {
            e.printStackTrace();
        } finally {
            lock.unlock(member, LockType.customPatientMessage);
        }
    }

    @Override
    public void finish(String gid) throws HttpApiException {
        /**
         * 结束服务指令:推送前端页面刷新
         */
        this.sendEvent(ReqUtil.instance.getUserId(), gid, null);
        String userId = GuideUtils.getUserIdByGuideDocGroupId(gid);
        /**
         * 删除会话组中的导医
         */
        GroupInfo gi = (GroupInfo) msgService.createGroup(userId, null, null);
        if (gi != null && gi.getUserList() != null) {
            logger.info("会话组[" + gi.getGid() + "]的成员个数 = " + gi.getUserList().size());
        }
        /**
         * 发送系统消息到会话组的患者
         */
        businessServiceMsg.sendNotifyMsgToUser(userId, gid, "以上是导医[" + ReqUtil.instance.getUserId() + "] 服务");
        /**
         * 更新服务记录时间
         */
        CustomerPatientRecord record = new CustomerPatientRecord();
        record.setCustomerId(ReqUtil.instance.getUserId());
        record.setPatientUserId(Integer.valueOf(userId));
        record.setFinishTime(System.currentTimeMillis());
        record.setStatus(CustomerPatientRecord.SERVICE_END);
        iGuideDAO.updateCustomerPatientRecord(record);
        jedisTemplate.zrem(PATIENT_MESSAGE_TO_GUIDE_POOL_SERVICING + ReqUtil.instance.getUserId(), userId);
    }

    @Override
    public PageVO getCustomerWorkDate(Long dateTime, Integer pageIndex, Integer pageSize) {
        PageVO pageVo = new PageVO();
        pageIndex = pageIndex == null ? pageVo.getPageIndex() : pageIndex;
        pageSize = pageVo.getPageSize();
        /**
         * 默认显示15条数据
         * 1、获取总条数
         * 2、计算当前分页是否超出最大分页
         * 3、每次获取15天内的数据 ，如果不够15条则获取下一个15天的数据直至累计15条数据为止
         */
        Long total = iGuideDAO.getCustomerWorkDateTotal();
        if (total < 1)
            return pageVo;

        int mod = Long.valueOf(total % pageSize).intValue();
        int maxPageIndex = mod == 0 ? total.intValue() / pageSize - 1 : total.intValue() / pageSize;
        if (pageIndex > maxPageIndex)
            return pageVo;

        if (Objects.isNull(dateTime))
            dateTime = DateUtil.getDayBegin(System.currentTimeMillis() + DateUtil.daymillSeconds);

        List<Object> list = iGuideDAO.getCustomerWorkDate(dateTime);

        if (pageIndex == maxPageIndex || list.size() == pageSize)
            pageVo.setPageData(list);
        else {
            //获取当前医生的最早数据时间做判断
            long minTime = iGuideDAO.getFirstWorkDataTime();
            //需要重复获取
            while (list.size() < pageSize && dateTime >= minTime) {
                dateTime -= DateUtil.halfmonthmillSeconds;
                list.addAll(iGuideDAO.getCustomerWorkDate(dateTime));
            }
            if (list.size() > pageSize)
                list = list.subList(0, pageSize);
            pageVo.setPageData(list);
        }
        pageVo.setTotal(total);
        return pageVo;
    }

    @Override
    public PageVO getDayRecords(Long dateTime, Integer pageIndex, Integer pageSize) {
        PageVO pageVO = new PageVO();
        pageSize = pageSize == null ? pageVO.getPageSize() : pageSize;
        pageIndex = pageIndex == null ? pageVO.getPageIndex() : pageIndex;
        long count = iGuideDAO.getDayRecordsCount(dateTime);
        List<CustomerPatientRecord> list = iGuideDAO.getDayRecords(dateTime, pageIndex, pageSize);
        List<Map<String, Object>> dataList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            list.stream().forEach(o -> {
                Map<String, Object> map = new HashMap<>();
                User u = userManager.getUser(o.getPatientUserId());
                map.put("patientUserId", u.getUserId());
                map.put("name", u.getName());
                map.put("ageStr", u.getAgeStr());
                map.put("sex", u.getSex());
                map.put("headPicFileName", u.getHeadPicFileName());
                map.put("startTime", DateUtil.getMinuteTimeByLong(o.getStartTime()));
                map.put("gid", SysConstant.getGuideDocGroupId(u.getUserId() + ""));
                dataList.add(map);
            });
        }
        pageVO.setTotal(count);
        pageVO.setPageData(dataList);
        return pageVO;
    }

    @Override
    public String clearAllGuideSession() {
        Set<String> userIdset = iGuideDAO.findAll();
        int size = userIdset.size();
        userIdset.forEach(o -> {
            msgService.createGroup(o, null, null);
            logger.info("成功清除了 【gid = guide_" + o + " 】的会话组导医");
        });
        return "成功清除了" + size + "个导医会话中的导医，详情请看info日志";
    }


    @Override
    public Object getUserInfo(Integer userId) {
        User u = userManager.getUser(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", u.getUserId());
        map.put("name", u.getName());
        map.put("headPicFileName", u.getHeadPicFileName());
        map.put("remarks", u.getRemarks());
        map.put("sex", u.getSex());
        map.put("birthday", u.getBirthday());
        map.put("ageStr", u.getAgeStr());
        map.put("area", u.getArea());
        map.put("telephone", u.getTelephone());
        return map;
    }

    @Override
    public boolean hasService(Integer userId) {
        List<CustomerPatientRecord> records = iGuideDAO.getRecordsByUserId(userId);
        if (CollectionUtils.isEmpty(records)) {
            return false;
        }
        return true;
    }
}
