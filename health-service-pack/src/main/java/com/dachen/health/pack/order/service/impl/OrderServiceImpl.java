package com.dachen.health.pack.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.careplan.api.entity.CCarePlan;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.lock.RedisLock;
import com.dachen.commons.lock.RedisLock.LockType;
import com.dachen.commons.logger.LoggerUtils;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.drug.api.client.DrugApiClientProxy;
import com.dachen.drug.api.entity.CGoodsView;
import com.dachen.drug.api.entity.CRecipeDetailView;
import com.dachen.drug.api.entity.CRecipeView;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.base.dao.IdxRepository.idxType;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.po.ServiceItem;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.helper.TemplateHelper;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.commons.constants.*;
import com.dachen.health.commons.constants.GroupEnum.GroupSkipStatus;
import com.dachen.health.commons.constants.GroupEnum.OnLineState;
import com.dachen.health.commons.constants.OrderEnum.*;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.utils.OrderNotify;
import com.dachen.health.commons.utils.PackUtil;
import com.dachen.health.commons.utils.ServiceItemUtil;
import com.dachen.health.commons.vo.CarePlanDoctorVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.doctor.service.ICommonGroupDoctorService;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.GroupServiceItem;
import com.dachen.health.group.group.entity.vo.OutpatientVO;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupSearchService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.group.service.IGroupServiceItemService;
import com.dachen.health.group.schedule.dao.IOfflineDao;
import com.dachen.health.group.schedule.entity.po.OfflineItem;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.msg.util.ImHelper;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.health.pack.account.entity.param.RechargeParam;
import com.dachen.health.pack.account.entity.po.Recharge;
import com.dachen.health.pack.account.entity.vo.RechargeVO;
import com.dachen.health.pack.account.service.IRechargeService;
import com.dachen.health.pack.consult.Service.ConsultationFriendService;
import com.dachen.health.pack.consult.Service.ConsultationPackService;
import com.dachen.health.pack.consult.Service.ElectronicIllCaseService;
import com.dachen.health.pack.consult.dao.ConsultationPackDao;
import com.dachen.health.pack.consult.dao.ElectronicIllCaseDao;
import com.dachen.health.pack.consult.entity.po.*;
import com.dachen.health.pack.consult.entity.vo.*;
import com.dachen.health.pack.evaluate.dao.IEvaluationDao;
import com.dachen.health.pack.evaluate.service.IEvaluationService;
import com.dachen.health.pack.guide.dao.IGuideDAO;
import com.dachen.health.pack.guide.entity.OrderCache;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.entity.po.OrderRelationPO;
import com.dachen.health.pack.guide.entity.vo.OrderDiseaseVO;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.guide.util.GuideMsgHelper;
import com.dachen.health.pack.illhistory.dao.IllHistoryInfoDao;
import com.dachen.health.pack.illhistory.dao.IllHistoryRecordDao;
import com.dachen.health.pack.illhistory.entity.po.*;
import com.dachen.health.pack.illhistory.service.IllHistoryInfoService;
import com.dachen.health.pack.messageGroup.IMessageGroupService;
import com.dachen.health.pack.messageGroup.MessageGroupEnum;
import com.dachen.health.pack.order.dao.IAssistantSessionRelationDao;
import com.dachen.health.pack.order.dao.IOrderSessionContainerDao;
import com.dachen.health.pack.order.dao.IPendingOrderStatusDao;
import com.dachen.health.pack.order.dao.OrderCancelInfoDao;
import com.dachen.health.pack.order.entity.param.CareOrderParam;
import com.dachen.health.pack.order.entity.param.CheckOrderParam;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.*;
import com.dachen.health.pack.order.entity.vo.*;
import com.dachen.health.pack.order.mapper.CheckInMapper;
import com.dachen.health.pack.order.mapper.OrderDoctorMapper;
import com.dachen.health.pack.order.mapper.OrderExtMapper;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.service.*;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.entity.po.PackDoctor;
import com.dachen.health.pack.pack.entity.vo.PackDoctorVO;
import com.dachen.health.pack.pack.entity.vo.PackVO;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackDoctorService;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.mapper.DiseaseMapper;
import com.dachen.health.pack.patient.mapper.OrderSessionMapper;
import com.dachen.health.pack.patient.mapper.OrderStatusLogMapper;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.*;
import com.dachen.health.pack.patient.service.ICureRecordService;
import com.dachen.health.pack.patient.service.IDiseaseService;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.pack.pay.entity.PaymentVO;
import com.dachen.health.pack.pay.service.IAlipayPayService;
import com.dachen.health.pack.pay.service.IPayHandleBusiness;
import com.dachen.health.pack.pay.service.IWechatPayService;
import com.dachen.health.pack.schedule.service.IScheduleService;
import com.dachen.health.recommand.service.IDiseaseLaberService;
import com.dachen.health.recommend.service.IIntegralDoctorService;
import com.dachen.health.user.dao.ITagDao;
import com.dachen.health.user.entity.param.DoctorParam;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.service.IDoctorService;
import com.dachen.health.user.service.IRelationService;
import com.dachen.im.server.constant.SysConstant.SysUserEnum;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.MsgDocument;
import com.dachen.im.server.data.MsgDocument.DocInfo;
import com.dachen.im.server.data.request.CreateGroupRequestMessage;
import com.dachen.im.server.data.request.GroupStateRequestMessage;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.enums.*;
import com.dachen.manager.ISMSManager;
import com.dachen.pub.service.PubGroupService;
import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mobsms.sdk.MobSmsSdk;
import com.tencent.common.Configure;
import com.tencent.common.Util;
import com.tencent.protocol.pay_protocol.AppPayReqData;
import com.tencent.protocol.pay_protocol.UnifiedOrderPayReqData;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


/**
 * ProjectName： health-service-pack<br>
 * ClassName： OrderServiceImpl<br>
 * Description：订单service实现类 <br>
 *
 * @author fanp
 * @version 1.0.0
 * @createTime 2015年8月10日
 */
@Service
public class OrderServiceImpl extends NoSqlRepository implements IOrderService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderExtMapper orderExtMapper;

    @Autowired
    private OrderSessionMapper orderSessionMapper;

    @Autowired
    private IOrderSessionService orderSessionService;

    @Autowired
    private IRechargeService rechargeService;

    @Autowired
    private IPackService packService;

    @Autowired
    private DiseaseMapper diseaseMapper;

    @Autowired
    private IAlipayPayService alipayPayService;

    @Autowired
    private IWechatPayService wechatPayService;

    @Autowired
    private IGuideDAO iGuideDAO;

    @Autowired
    private IDiseaseService diseaseService;

    @Autowired
    private UserRepository userManagerImpl;

    @Autowired
    private ICommonGroupDoctorService commongdService;

    @Autowired
    private ICureRecordService cureRecordService;

    @Autowired
    private IImageDataService imageDataService;

    @Autowired
    private IBaseUserService baseUserService;

    @Autowired
    private IBaseDataService baseDataService;

    @Autowired
    IBaseDataDao baseDataDao;

    @Autowired
    private PubGroupService pubGroupService;

    @Autowired
    private IMsgService imsgService;

    @Autowired
    private IBusinessServiceMsg businessServiceMsg;

    @Autowired
    private IPatientService patientService;

    @Resource
    private MobSmsSdk mobSmsSdk;

    @Resource
    OrderStatusLogMapper orderStatusMapper;

    @Autowired
    protected IPackDoctorService packDoctorService;

    @Autowired
    ConsultationPackService consultationPackService;


    @Autowired
    ConsultationPackDao consultationPackDao;

    @Autowired
    IOrderRefundService orderRefundService;

    @Resource
    OrderDoctorMapper orderDoctorMapper;

    @Resource
    IGuideService guideService;

    @Autowired
    private IdxRepository idxRepository;

    @Autowired
    private IGroupDoctorDao groupDoctorDao;

    @Autowired
    private IGroupDoctorService groupDoctorService;

    @Autowired
    private IGroupService groupService;

    @Autowired
    private IDoctorService doctorService;

    @Autowired
    private IScheduleService scheduleService;

    @Autowired
    private IRelationService relationService;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    IOrderDoctorService orderDoctorService;

    @Autowired
    private ElectronicIllCaseService illCaseServer;

    @Autowired
    private ElectronicIllCaseDao illCaseDao;

    @Resource
    protected CarePlanApiClientProxy carePlanApiClientProxy;

    @Autowired
    ConsultationFriendService consultationFriendService;

    @Autowired
    DiseaseTypeRepository diseaseTypeRepository;

    @Autowired
    IOfflineDao offlineDao;

    @Autowired
    IEvaluationService evaluationService;

    @Autowired
    private IMsgService msgService;

    @Autowired
    private UserManager userManager;

    @Autowired
    IEvaluationDao evaluationDao;

    @Autowired
    IOrderSessionContainerDao orderSessionContainerDao;

    @Autowired
    IllHistoryInfoService illHistoryInfoService;

    @Autowired
    private IllHistoryRecordDao illHistoryRecordDao;

    @Autowired
    private ICureRecordService cureRecordServiceImpl;

    @Autowired
    private IPayHandleBusiness payHandleBusiness;

    @Autowired
    private PackMapper packMapper;

    @Autowired
    private ISMSManager smsManager;

    @Autowired
    private ITagDao tagDao;

    @Autowired
    ICheckInService checkInService;

    @Autowired
    IIntegralDoctorService integralDoctorService;

    @Autowired
    private IDiseaseLaberService iDiseaseLaberService;

    @Autowired
    IllHistoryInfoDao illHistoryInfoDao;

    @Autowired
    private IPendingOrderStatusDao pendingOrderStatusDao;

    @Autowired
    private IAssistantSessionRelationDao assistantSessionRelationDao;

    @Resource
    private OrderCancelInfoDao orderCancelInfoDao;

    @Autowired
    private IMessageGroupService messageGroupService;

    public final static String MESSAGE_REPLY_ORDER_COUNT = KeyBuilder.MESSAGE_REPLY_COUNT + ":order";

    /**
     * </p>新建订单,返回客户端调用第三方接口参数</p>
     *
     * @author fanp
     * @date 2015年8月10日
     */

    @Override
    public Integer nextOrderNo() {
        // 生成订单号，规则：给定默认起始值（15080700+自增序列）*10+0，最后一位为用户id%10，现默认为0
        String payNo = idxRepository.nextOrderNoIdx(idxType.orderNo);
        if (payNo == null) {
            throw new ServiceException("订单id获取失败");
        }
        System.out.println(idxRepository.nextOrderNoIdx(idxType.orderNo));
        return (15080700 + Integer.valueOf(payNo)) * 10;
    }

    /**
     * 给导医端提供的生成待支付订单接口
     */
    @Override
    public PreOrderVO addPreCharge(OrderParam param, UserSession session) throws HttpApiException {
        param.setOrderStatus(OrderStatus.待支付.getIndex());
        Order order = createOrder(param, session);
        PreOrderVO vo = new PreOrderVO();
        vo.setOrderId(order.getId());
        vo.setOrderStatus(order.getOrderStatus());
        return vo;
    }

    /**
     * 给导医端提供的生成会话接口（支付成功之后调用）
     */
    @Override
    public OrderSessionVO addPreChargeSession(OrderParam param, CreateGroupRequestMessage createGroupParam) throws HttpApiException {

        GroupInfo groupInfo = (GroupInfo) imsgService.createGroup(createGroupParam);

        OrderSession orderSession = orderSessionService.findOneByOrderId(param.getOrderId());
        if (orderSession == null) {
            orderSession = new OrderSession();
        }
        orderSession.setOrderId(param.getOrderId());
        orderSession.setAppointTime(param.getOppointTime());
        orderSession.setPatientCanSend(true);
        orderSession.setMsgGroupId(groupInfo.getGid());
        orderSession.setLastModifyTime(System.currentTimeMillis());
        orderSession.setCreateTime(System.currentTimeMillis());
        orderSessionService.save(orderSession);

        // 更新日程信息
        scheduleService.createOrderSchedule(param.getOrderId(), param.getOppointTime());

//		orderSessionService.appointServiceTime(param.getOrderId(),param.getOppointTime());
        OrderSessionVO vo = new OrderSessionVO();
        vo.setOrderId(orderSession.getOrderId());
        vo.setMsgGroupId(orderSession.getMsgGroupId());
        vo.setSessionId(orderSession.getId());
        return vo;
    }

    @Override
    public PreOrderVO add(OrderParam param, UserSession session) throws HttpApiException {

        //保存导医订单
        Order order = createOrder(param, session);

        //创建会话，并保存ordersession
        OrderSession orderSession = new OrderSession();
        GroupInfo groupInfo = null;
        //如果是健康关怀订单生成多人会话组
        if (order.getOrderType() == OrderEnum.OrderType.care.getIndex()) {
            List<OrderDoctor> orderDoctorList = orderDoctorService.findOrderDoctors(order.getId());

            List<String> docIds = new ArrayList<String>(orderDoctorList.size() + 1);
            for (OrderDoctor orderDoctor : orderDoctorList) {
                docIds.add(orderDoctor.getDoctorId() + "");
            }
            docIds.add(order.getUserId() + "");
            orderSession.setToUserIds(OrderSession.appendStringUserId(docIds));
            groupInfo = createGroupMore(order, orderSession);
        } else {
            if (StringUtils.isNotBlank(param.getOfflineItemId())) {
                OfflineItem item = offlineDao.findOfflineItemById(param.getOfflineItemId());
                orderSession.setAppointTime(item.getStartTime());
                if (UserType.patient.getIndex() == ReqUtil.instance.getUser().getUserType()) {
                    JobTaskUtil.cancelNoAgreeOrNoPayAppointmentOrder(order.getId(), item.getStartTime());
                }
            }
            groupInfo = createGroup(order, orderSession);
        }

        PreOrderVO vo = new PreOrderVO();
        vo.setOrderId(order.getId());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setGid(groupInfo.getGid());
        vo.setOrderNo(order.getOrderNo());

        Map<String, Object> mapString = new HashMap<String, Object>();
        User user = userManagerImpl.getUser(order.getDoctorId());
        Patient patient = patientService.findByPk(param.getPatientId());
        mapString.put("title", StringUtil.returnPackTitle(order.getPackType(), order.getPrice()));
        mapString.put("patient", patient);
        mapString.put("docName", user.getName());
        mapString.put("diseaseDesc", order.getMainCase());
        mapString.put("orderType", order.getOrderType());
        mapString.put("price", order.getPrice());
        mapString.put("order", order);
        mapString.put("illCaseInfoId", order.getIllCaseInfoId());
        if (param.getTransferDoctorId() != null) {
            User transferDoctor = userManagerImpl.getUser(param.getTransferDoctorId());
            mapString.put("transferDoctorName", transferDoctor.getName());
        }

        //图文咨询流程改进，提交资料后直接未支付状态，省略中间阶段待预约
        if ((order.getPackType() != null) && (order.getPackType() == PackType.message.getIndex())) {
            orderSessionService.appointTime(order, System.currentTimeMillis(), null);
        }

        //来自服务服务号的下单，下单成功给患者发送短信
        if (ReqUtil.isFromWechat()) {
            //"1042"：尊敬的用户您好，您已成功提交订单，请登录玄关健康APP（http://t.cn/RqiNrGi）进一步完成支付，祝您早日康复。
            User patientUser = userManagerImpl.getUser(order.getUserId());
            mobSmsSdk.send(patientUser.getTelephone(), baseDataService.toContent("1042", BaseConstants.XG_PLATFORM,
                    shortUrlComponent.generateShortUrl(BaseConstants.XG_OPEN_PAT())));
        }

        //发送消息通知
        sendOrderNoitfy(String.valueOf(session.getUserId()), String.valueOf(param.getDoctorId()), groupInfo.getGid(), OrderNoitfyType.neworder, mapString);
        messageGroupService.updateGroupBizState(groupInfo.getGid(), MessageGroupEnum.NEW_ORDER.getIndex());

        return vo;
    }


    /**
     * 计划关怀医生下单 生成待支付订单
     * packId, userId, patiendId, telephone(如有), doctorId
     */
    @Override
    public Order addCareOrder(OrderParam param) throws HttpApiException {

        if (param.getDoctorId() == null) {
            throw new ServiceException("找不到医生！");
        }

        Pack pack = packService.getPack(param.getPackId());

        Order order = new Order();
        // 设置默认值
        order.setOrderStatus(OrderEnum.OrderStatus.待支付.getIndex());
        order.setRefundStatus(OrderEnum.OrderRefundStatus.refundWait.getIndex());
        order.setOrderType(OrderEnum.OrderType.care.getIndex());
        order.setCreateTime(System.currentTimeMillis());
        order.setFinishTime(0L);

        // 设置传过来的值
        order.setDoctorId(param.getDoctorId());
        order.setUserId(param.getUserId()); //设置病情信息，默认为患者端本人
        order.setPatientId(param.getPatientId());
        order.setDiseaseId(addDisease(param));

        // 设置pack相关值
        order.setPackId(pack.getId());
        order.setPackType(pack.getPackType());
        order.setPrice(pack.getPrice());
        order.setTimeLong(pack.getTimeLimit());
        order.setHelpTimes(pack.getHelpTimes());
        order.setGroupId(pack.getGroupId());

        if (0 == order.getPrice()) {
            order.setOrderStatus(OrderEnum.OrderStatus.已支付.getIndex());
        }

        // 重新copy一份计划，防止医生修改用药影响套餐
        CCarePlan carePlanCopy;
        try {
            carePlanCopy = carePlanApiClientProxy.copyByPack(pack.getCareTemplateId(), pack.getName(), pack.getDescription(), pack.getPrice().intValue());
            order.setCareTemplateId(carePlanCopy.getId());
        } catch (HttpApiException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("拷贝关怀计划失败，请稍候再试");
        }

        // 判断用户的状态
        User user = userManagerImpl.getUser(param.getUserId());
        if (user.getStatus() == UserStatus.inactive.getIndex()) {
            order.setAcStatus(0); // 0：未激活订单
        } else {
            order.setAcStatus(1);   // 1：已激活订单
        }

        order.setOrderNo(nextOrderNo());    // 前面没有报错，再插入数据之前生成订单号

        orderMapper.add(order);

        addJesQueTask(order);
        /*//创建会话
        createGroup(order,new OrderSession());*/

        //计算参与分成的医生和分成比例
        generateOrderDoctor(order);

        //医生集团和医生就诊人数++
        //医生就诊人数++
        doctorService.updateCureNum(order.getDoctorId());
        //根据医生查找医生集团 ,医生集团就论人数++
        if (StringUtils.isNotBlank(order.getGroupId())) {
            groupService.increaseCureNum(order.getGroupId());
        }

        return order;
    }

    @Override
    public Order addFeeBill(Order order) {
        Patient patient = patientService.findOne(order.getUserId(), SysConstants.ONESELF);
        order.setPatientId(patient.getId());
        order.setDiseaseId(addDisease(order));
        Long currTime = System.currentTimeMillis();
        order.setCreateTime(currTime);
        order.setFinishTime(0L);
        order.setOrderNo(nextOrderNo());
        order.setRefundStatus(OrderEnum.OrderRefundStatus.refundWait.getIndex());
        orderMapper.add(order);
        //计算参与分成的医生和分成比例
        generateOrderDoctor(order);
        return order;
    }

    private Order createOrder(Pack pack, Integer userId) {
        Order order = new Order();
        order.setOrderStatus(OrderEnum.OrderStatus.待支付.getIndex());
        order.setDoctorId(pack.getDoctorId());
        order.setOrderType(OrderEnum.OrderType.feeBill.getIndex());
        order.setPackId(pack.getId());
        order.setPackType(pack.getPackType());
        order.setPrice(pack.getPrice());
        order.setTimeLong(pack.getTimeLimit());
        order.setGroupId(pack.getGroupId());
        order.setHospitalId(pack.getHospitalId());
        order.setUserId(userId);
        return order;
    }

    @Override
    public void saveSendFeeBill(Pack pack, List<Integer> userIds) throws HttpApiException {
        if (StringUtil.isEmpty(pack.getServiceItemId())) {
            throw new ServiceException("服务项Id不能为空");
        }
        if (StringUtil.isEmpty(pack.getGroupId())) {
            throw new ServiceException("集团Id不能为空");
        }
        if (StringUtil.isEmpty(pack.getHospitalId())) {
            throw new ServiceException("医院Id不能Id为空");
        }

        pack = packService.addFeeBillPack(pack);

        for (Integer userId : userIds) {

            Order order = createOrder(pack, userId);

            order = this.addFeeBill(order);

            OrderSession orderSession = new OrderSession();
            // 创建会话
            this.createGroup(order, orderSession);

            String content = "您好，我已为您发送付费单，请及时支付。";
            businessServiceMsg.sendUserToUserMsg(order.getDoctorId() + "", orderSession.getMsgGroupId(), content);
        }
    }

    @Resource
    protected IGroupServiceItemService gserviceItemService;

    @Override
    public FeeBillDetail queryFeeBillByOrder(Integer orderId) {

        Order order = orderMapper.getOne(orderId);

        if (order == null) {
            throw new ServiceException("收费单不存在");
        }

        Pack pack = packService.getPack(order.getPackId());
//		Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
//		if (pack == null) {
//			throw new ServiceException("收费单关联套餐ID不存在");
//		}

        List<ServiceItemVO> vos = ServiceItemUtil.parseString(pack.getServiceItemId());
        for (ServiceItemVO vo : vos) {
            ServiceItem serviceItem = baseDataDao.getServiceItemByIds(vo.getId()).get(0);
            if (serviceItem == null)
                continue;
            vo.setName(serviceItem.getName());
            GroupServiceItem gserviceItem = gserviceItemService.getGroupServiceItem(order.getGroupId(),
                    order.getHospitalId(), serviceItem.getId());
            if (gserviceItem == null) {
                logger.error("获取集团服务项失败，集团Id：{}，医院Id：{}，服务项Id：{}", order.getGroupId(), order.getHospitalId(),
                        serviceItem.getId());
            } else {
                vo.setPrice(gserviceItem.getPrice());
            }
        }

        FeeBillDetail detail = new FeeBillDetail();
        detail.setOrderAmt(order.getPrice());
        detail.setServiceItem(vos);
        return detail;
    }


    //患者下单
    private Order createOrder(OrderParam param, UserSession session) throws HttpApiException {
        // 1.新建初始订单，2.新建初始充值记录
        if (session == null) {
            throw new ServiceException("请登录");
        }

        if (param.getDoctorId() == null) {
            throw new ServiceException("无效的医生");
        }

        // 1.新建初始订单
        Order order = new Order();
        // 默认为待预约订单
        order.setOrderStatus(OrderEnum.OrderStatus.待预约.getIndex());
        order.setDoctorId(param.getDoctorId());

        String groupId = param.getGroupId();
        GroupDoctor dg = null;
        if ((groupId == null) || groupId.trim().equals("")) {
            // 根据医生查找医生集团
            dg = groupDoctorService.findOneByUserIdAndStatus(order.getDoctorId(), "C");
            if (dg != null) {
                groupId = dg.getGroupId();
            }
        }
        order.setGroupId(groupId);
        Pack pack = null;
        // 图文和电话咨询类套订单
        if ((param.getOrderType() == null) || (param.getOrderType() == OrderEnum.OrderType.order.getIndex())) {
            // 只有套餐类型订单才需判断套餐Id不能为空
            if (param.getPackId() == null) {
                throw new ServiceException("请正确选择套餐");
            }
            // 查询套餐信息，判断该套餐是否属于该医生
            pack = packService.getPack(param.getPackId());
            if (pack == null) {
                throw new ServiceException("请正确选择套餐");
            }
            if (pack.getDoctorId().intValue() != param.getDoctorId().intValue()) {
                throw new ServiceException("套餐有误，请正确选择套餐");
            }
            // 套餐订单
            order.setOrderType(OrderEnum.OrderType.order.getIndex());
            order.setPackId(pack.getId());
            order.setPackType(pack.getPackType());
            order.setPrice(pack.getPrice());
            order.setTimeLong(pack.getTimeLimit());
        } else if ((param.getOrderType() == OrderEnum.OrderType.care.getIndex()) || (param.getOrderType() == OrderEnum.OrderType.followUp.getIndex())) {// 关怀计划类套餐订单
            // 只有套餐类型订单才需判断套餐Id不能为空
            if (param.getPackId() == null) {
                throw new ServiceException("请正确选择套餐");
            }
            // 查询套餐信息，判断该套餐是否属于该医生
            pack = packService.getPack(param.getPackId());
            if (pack == null) {
                throw new ServiceException("请正确选择套餐");
            }
//			if (pack.getDoctorId().intValue() != param.getDoctorId().intValue()) {
//				throw new ServiceException("套餐有误，请正确选择套餐");
//			}
            order.setOrderType(param.getOrderType());
            order.setPackId(pack.getId());
            order.setPackType(pack.getPackType());
            order.setPrice(pack.getPrice());
            order.setTimeLong(pack.getTimeLimit());

            //重新copy一份计划，防止医生修改用药影响套餐
            try {
                CCarePlan carePlan = carePlanApiClientProxy.copyByPack(pack.getCareTemplateId(), pack.getName(), pack.getDescription(), pack.getPrice().intValue());
                order.setCareTemplateId(carePlan.getId());
            } catch (HttpApiException e) {
                logger.error(e.getMessage(), e);
                throw new ServiceException("拷贝关怀计划失败，请稍候再试");
            }

            order.setRemarks("mark");//暂时用来区分患者主动购买还是医生发送给患者，支付时清空
            order.setHelpTimes(pack.getHelpTimes());
            if (order.getPrice() == 0) {
                order.setOrderStatus(OrderEnum.OrderStatus.已支付.getIndex());
            } else {
                order.setOrderStatus(OrderEnum.OrderStatus.待支付.getIndex());
            }
            order.setGroupId(pack.getGroupId());
        } else if (param.getOrderType() == OrderEnum.OrderType.appointment.getIndex()) {
            if (param.getPackId() == null) {
                throw new ServiceException("请正确选择套餐");
            }
            // 查询套餐信息，判断该套餐是否属于该医生
            pack = packService.getPack(param.getPackId());
            if (pack == null) {
                throw new ServiceException("请正确选择套餐");
            }

            order.setOrderType(param.getOrderType());
            order.setPackId(pack.getId());
            order.setPackType(pack.getPackType());
            if (param.getPrice() != null) {
                order.setPrice(param.getPrice());
            } else {
                order.setPrice(pack.getPrice());
            }
            order.setRemarks("mark");//暂时用来区分患者主动购买还是医生发送给患者
            order.setGroupId(pack.getGroupId());
            String offlineItemId = param.getOfflineItemId();
            if (StringUtils.isNotBlank(offlineItemId)) {
                OfflineItem item = offlineDao.findOfflineItemById(offlineItemId);
                if (item.getStatus() != ScheduleEnum.OfflineStatus.待预约.getIndex()) {
                    throw new ServiceException("医生已有预约，请选择其他时间");
                }
                if (item.getDoctorId().intValue() != param.getDoctorId().intValue()) {
                    logger.error("预约订单医生，数据错乱 order[doctorId]=" + param.getDoctorId() + "offlineItem[doctorId]=" + item.getDoctorId());
                    throw new ServiceException("预约医生有误，请重新选择");
                }
                synchronized (SysConstants.obj) {
                    //防止并发抢单
                    item = offlineDao.findOfflineItemById(offlineItemId);
                    if (item.getStatus() != ScheduleEnum.OfflineStatus.待预约.getIndex()) {
                        throw new ServiceException("医生已有预约，请选择其他时间");
                    }
                    order.setHospitalId(item.getHospitalId());
                    offlineDao.updateOfflineItemStatus(offlineItemId, ScheduleEnum.OfflineStatus.已预约.getIndex());
                }
            }

        } else {
            order.setOrderType(param.getOrderType());
            order.setPrice(0L);
            if (param.getOrderType() == OrderEnum.OrderType.outPatient.getIndex()) {//门诊套餐

                OutpatientVO outpatient = groupDoctorService.getOutpatientInfo(order.getDoctorId());

                if (OnLineState.offLine.getIndex().equals(outpatient.getOnLineState())) {
                    throw new ServiceException("该医生没有上线，不能提供在线门诊！");
                }

                //门诊时记录groupId
                order.setGroupId(outpatient.getGroupId());

                // 1、生成门诊订单的时候，根据医生的值班要求时长和在线时间比较，来确定订单价格，如果在线时长已超过值班时长，则取门诊价格，否则价格为0
                // 2、如果订单价格为0，则订单状态设为已支付，否则设为待支付。

                Long task = outpatient.getTaskDuration() == null ? 0L : outpatient.getTaskDuration(); // 在线时长(可设置的)(要完成这个任务才能收费)
                Long duty = 0L;
                if (outpatient.getDutyDuration() != null) {
                    duty = outpatient.getDutyDuration(); // 医生值班时长（定时器计算的在线值班时长）
                }

                boolean hasTaskTime = false;
                if (task != 0) {
                    hasTaskTime = true; // == true表示设置了要完成任务的值班时长
                }

                if ((hasTaskTime == false) || ((hasTaskTime == true) && (duty >= task))) {
                    // 没有设置值班时长 或者 设置了值班时长并且在线时长超过值班时长，才取门诊价格。
                    Long price = outpatient.getPrice() == null ? 0L : outpatient.getPrice().longValue();
                    order.setPrice(price);
                } else {
                    order.setPrice(0L);
                }

                if (order.getPrice() == 0) { // 如果价格为0，则订单状态变成已支付。
                    order.setOrderStatus(OrderEnum.OrderStatus.已支付.getIndex());
                } else {
                    order.setOrderStatus(OrderEnum.OrderStatus.待支付.getIndex());
                }
            }
            order.setPackId(0);
            order.setPackType(0);
            // 非套餐订单默认为15分钟
            order.setTimeLong(15);
        }

        Order preOrder = null;
        Integer diseaseId = null;
        if (param.getPreOrderId() != null) {
            preOrder = getOne(param.getPreOrderId());
            param.setPatientId(preOrder.getPatientId());
            diseaseId = preOrder.getDiseaseId();
        } else {
            diseaseId = addDisease(param);
        }
        order.setDiseaseId(diseaseId);
        order.setUserId(session.getUserId());

        order.setCreateTime(System.currentTimeMillis());
        order.setFinishTime(0L);
        order.setOrderNo(nextOrderNo());
        order.setPatientId(param.getPatientId());

        if (param.getOrderStatus() != null) {
            order.setOrderStatus(param.getOrderStatus());
        }

        order.setRefundStatus(OrderEnum.OrderRefundStatus.refundWait.getIndex());
        orderMapper.add(order);

        addJesQueTask(order);

        /**
         * update by wangl
         * 产品不确定添加医患关系 与 订单是否免费的关系
         * 暂时不管订单的价格 下单既是医患好友
         */
        DoctorPatient doctorPatient = null;
        try {
            //doctorPatient = (DoctorPatient)friendsManager.getFriend( order.getDoctorId(),order.getUserId());
            //2016-12-12医患关系的变化
            doctorPatient = tagDao.findByDoctorIdAndPatientId(order.getDoctorId(), order.getPatientId());
            if (doctorPatient == null) {
                //baseUserService.setDoctorPatient( order.getDoctorId(),order.getUserId());
                //2016-12-12医患关系的变化
                baseUserService.addDoctorPatient(order.getDoctorId(), param.getPatientId(), order.getUserId());
                // 关注医生公众号
                pubGroupService.addSubUser(param.getDoctorId(), order.getUserId());
                businessServiceMsg.sendEventFriendChange(EventEnum.ADD_FRIEND, order.getDoctorId().toString(), order.getUserId().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //计算参与分成的医生和分成比例
        generateOrderDoctor(order);

        // 判断订单类型如果是门诊订单则向断redis里是等待队列添加数据
        if ((order.getOrderType() == OrderEnum.OrderType.outPatient.getIndex())
                && (order.getOrderStatus() == OrderEnum.OrderStatus.已支付.getIndex())) {
            String wkey = RedisUtil.generateKey(RedisUtil.WAITING_QUEUE, String.valueOf(param.getDoctorId()));
            RedisUtil.rpush(wkey, order.getId().toString());
        }
        // 医生集团和医生就诊人数++
        // 医生就诊人数++
        doctorService.updateCureNum(order.getDoctorId());
        // 根据医生查找医生集团 ,医生集团就论人数++
        if ((order.getGroupId() != null) && !order.getGroupId().trim().equals("")) {
            groupService.increaseCureNum(order.getGroupId());
        }

        relationService.addRelationTag2(PackUtil.convert(order));

        /**
         * 选择电子病历 提交的订单 就直接返回 不需要重新生成电子病历
         */
        if (StringUtil.isNotEmpty(param.getIllCaseInfoId()) && param.getIsIllCaseCommit()) {
            /**
             * 同步更新offlineItem
             */
            if (StringUtils.isNotBlank(param.getOfflineItemId()) && (order.getOrderType() == 9)) {
                offlineDao.updateOfflineItemOrderInfo(param.getOfflineItemId(), order.getId(), order.getPatientId());
            }
            return order;
        }

        /**
         * 创建电子病历
         */
        if (preOrder != null) {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderId", order.getId());
            params.put("illCaseInfoId", preOrder.getIllCaseInfoId());
            orderMapper.updateOrderIllCaseInfoId(params);
            /**
             * 生成转诊记录
             */
            IllTransferRecord record = new IllTransferRecord();
            record.setIllCaseInfoId(preOrder.getIllCaseInfoId());
            record.setPreOrderId(preOrder.getId());
            record.setTargetOrderId(order.getId());
            record.setReceiveDoctorId(order.getDoctorId());
            record.setTransferDoctorId(param.getTransferDoctorId());
            record.setTransferTime(param.getTransferTime());
            record.setReceiveTime(System.currentTimeMillis());
            illCaseDao.saveIllRecord(record);
            IllCaseInfo info = illCaseDao.getIllCase(preOrder.getIllCaseInfoId());
            order.setMainCase(info.getMainCase());
            order.setIllCaseInfoId(info.getId());
        } else {
            int orderType = param.getOrderType() == null ? OrderType.order.getIndex() : param.getOrderType();
            Integer treateType = null;
            if (orderType == 1) {
                int packType = pack.getPackType();
                if (packType == 1) {
                    treateType = ConsultationEnum.IllCaseTreatType.text.getIndex();
                } else if (packType == 2) {
                    treateType = ConsultationEnum.IllCaseTreatType.phone.getIndex();
                }
            } else if (orderType == 3) {
                treateType = ConsultationEnum.IllCaseTreatType.outPatient.getIndex();
            } else if ((orderType == 4) && StringUtils.isNotBlank(param.getDiseaseDesc())) {
                treateType = ConsultationEnum.IllCaseTreatType.care.getIndex();
            } else if (orderType == 9) {
                treateType = ConsultationEnum.IllCaseTreatType.appointment.getIndex();
                /**
                 * 同步更新offlineItem
                 */
                if (StringUtils.isNotBlank(param.getOfflineItemId())) {
                    offlineDao.updateOfflineItemOrderInfo(param.getOfflineItemId(), order.getId(), order.getPatientId());
                }
            }
            if (treateType != null) {
                IllCaseInfo caseInfo = new IllCaseInfo();
                caseInfo.setCreateTime(System.currentTimeMillis());
                caseInfo.setDoctorId(param.getDoctorId());
                caseInfo.setUserId(param.getUserId());
                caseInfo.setPatientId(param.getPatientId());
                caseInfo.setTreateType(treateType);
                if ((param.getImagePaths() != null) && (param.getImagePaths().length > 0)) {
                    caseInfo.setImageUlrs(Arrays.asList(param.getImagePaths()));
                }
                caseInfo.setOrderId(order.getId());
                caseInfo.setMainCase(param.getDiseaseDesc());
                caseInfo.setSeeDoctorMsg(param.getSeeDoctorMsg());
                IllCaseInfo dbinfo = illCaseServer.createIllCaseInfo(caseInfo);
                if (dbinfo != null) {
                    String illCaseInfoId = dbinfo.getId();
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("orderId", order.getId());
                    params.put("illCaseInfoId", illCaseInfoId);
                    orderMapper.updateOrderIllCaseInfoId(params);
                    order.setIllCaseInfoId(illCaseInfoId);
                    /**
                     * 电话订单需要同步更多的数据
                     */
                    if (treateType == ConsultationEnum.IllCaseTreatType.phone.getIndex()) {
                        com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease disParam =
                                new com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease();
                        disParam.setIllCaseInfoId(illCaseInfoId);
                        disParam.setCureSituation(param.getCureSituation());
                        disParam.setDiseaseInfo_now(param.getDiseaseInfo_now());
                        disParam.setDiseaseInfo_old(param.getDiseaseInfo_old());
                        disParam.setMenstruationdiseaseInfo(param.getMenstruationdiseaseInfo());
                        disParam.setFamilydiseaseInfo(param.getFamilydiseaseInfo());
                        illCaseDao.syncPhoneOrderDiseaseToIllCase(disParam);
                    }
                }
            }
            order.setMainCase(param.getDiseaseDesc());
        }
        return order;
    }

    private Integer addDisease(Order param) {
        DiseaseParam diseaseParam = new DiseaseParam();
        diseaseParam.setPatientId(param.getPatientId());
        diseaseParam.setUserId(param.getUserId());
        Patient patient = patientMapper.selectByPrimaryKey(param.getPatientId());
        diseaseParam.setAge(DateUtil.calcAge(patient.getBirthday()));
        diseaseParam.setUserName(patient.getUserName());
        diseaseParam.setBirthday(patient.getBirthday());
        diseaseParam.setSex(patient.getSex() == null ? null : (int) patient.getSex());
        diseaseParam.setArea(patient.getArea());
        diseaseParam.setRelation(patient.getRelation());
        diseaseParam.setTelephone(patient.getTelephone());
        return diseaseService.addDisease(diseaseParam);
    }

    private Integer addDisease(OrderParam param) {
        DiseaseParam diseaseParam = new DiseaseParam();
        diseaseParam.setDiseaseDesc(param.getDiseaseDesc());
        diseaseParam.setVoice(param.getVoice());
        diseaseParam.setImagePaths(param.getImagePaths());
        diseaseParam.setPatientId(param.getPatientId());
        diseaseParam.setUserId(param.getUserId());
        diseaseParam.setTelephone(param.getTelephone());
        Patient patient = patientMapper.selectByPrimaryKey(param.getPatientId());
        diseaseParam.setAge(DateUtil.calcAge(patient.getBirthday()));
        diseaseParam.setUserName(patient.getUserName());
        diseaseParam.setBirthday(patient.getBirthday());
        diseaseParam.setSex(patient.getSex() == null ? null : (int) patient.getSex());
        diseaseParam.setArea(patient.getArea());
        diseaseParam.setRelation(patient.getRelation());

        /***begin add  by  liwei  2016年1月21日********/
        diseaseParam.setDiseaseInfo_now(param.getDiseaseInfo_now());
        diseaseParam.setDiseaseInfo_old(param.getDiseaseInfo_old());
        diseaseParam.setDiseaseInfo_now(param.getDiseaseInfo_now());
        diseaseParam.setFamilydiseaseInfo(param.getFamilydiseaseInfo());
        diseaseParam.setMenstruationdiseaseInfo(param.getMenstruationdiseaseInfo());
        diseaseParam.setSeeDoctorMsg(param.getSeeDoctorMsg());
        String isSee = StringUtils.trimToEmpty(param.getIsSee());
        if (StringUtils.equals(isSee, "true") || StringUtils.equals(isSee, "1")) {
            diseaseParam.setIsSeeDoctor(true);
        } else {
            diseaseParam.setIsSeeDoctor(false);
        }
        /***end add  by  liwei  2016年1月21日********/

        return diseaseService.addDisease(diseaseParam);
    }

    private Integer addDisease(IllCaseInfoPageVo caseInfo) {
        DiseaseParam diseaseParam = new DiseaseParam();
        diseaseParam.setPatientId(caseInfo.getPatientId());
        diseaseParam.setUserId(caseInfo.getUserId());
        diseaseParam.setTelephone(caseInfo.getTelephoneOk());
        diseaseParam.setAge(caseInfo.getAge());
        diseaseParam.setSex(StringUtils.isBlank(caseInfo.getSex()) ? null : Integer.valueOf(caseInfo.getSex()));
        diseaseParam.setUserName(caseInfo.getPatientName());
        diseaseParam.setArea(caseInfo.getArea());
        List<IllCaseTypeContentPageVo> contents = caseInfo.getBaseContentList();
        for (IllCaseTypeContentPageVo content : contents) {
            if (SysConstants.IllCASE_TYPE_NAME_1[0].equals(content.getTypeName())) {
                diseaseParam.setDiseaseDesc(content.getContentTxt());
                if ((content.getContentImages() == null) || (content.getContentImages().size() == 0)) {
                    continue;
                }
                List<String> contentImagesCopy = new ArrayList<String>(content.getContentImages().size());
                for (String imageUrl : content.getContentImages()) {
                    imageUrl = PropertiesUtil.removeUrlPrefix(imageUrl);
                    contentImagesCopy.add(imageUrl);
                }
                diseaseParam.setImagePaths(contentImagesCopy.toArray(new String[0]));
            } else if (SysConstants.IllCASE_TYPE_NAME_1[1].equals(content.getTypeName())) {
                diseaseParam.setDiseaseInfo_now(content.getContentTxt());
            } else if (SysConstants.IllCASE_TYPE_NAME_1[2].equals(content.getTypeName())) {
                diseaseParam.setDiseaseInfo_old(content.getContentTxt());
            } else if (SysConstants.IllCASE_TYPE_NAME_1[3].equals(content.getTypeName())) {
                diseaseParam.setFamilydiseaseInfo(content.getContentTxt());
            } else if (SysConstants.IllCASE_TYPE_NAME_1[4].equals(content.getTypeName())) {
                diseaseParam.setMenstruationdiseaseInfo(content.getContentTxt());
            } else if (SysConstants.IllCASE_TYPE_NAME_1[5].equals(content.getTypeName())) {
                if (StringUtils.isNotEmpty(content.getContentTxt())) {
                    diseaseParam.setVisitTime(Long.valueOf(content.getContentTxt()));
                }
            } else if (SysConstants.IllCASE_TYPE_NAME_1[6].equals(content.getTypeName())) {
                diseaseParam.setSeeDoctorMsg(content.getContentTxt());
            }
        }
        Patient patient = patientMapper.selectByPrimaryKey(caseInfo.getPatientId());
        diseaseParam.setBirthday(patient.getBirthday());
        diseaseParam.setRelation(patient.getRelation());
        return diseaseService.addDisease(diseaseParam);
    }

    /**
     * 添加直通车套餐订单
     */
    @Override
    public Order addThroughTrainOrder(OrderParam param) {
        Order order = new Order();
        order.setUserId(param.getUserId());
        order.setDoctorId(param.getDoctorId());
        order.setPatientId(param.getPatientId());
        order.setOrderType(OrderType.throughTrain.getIndex());
        order.setPackType(param.getPackType());
        order.setPrice(param.getPrice());
        order.setCreateTime(System.currentTimeMillis());
        order.setOrderStatus(OrderStatus.待支付.getIndex());
        order.setRefundStatus(OrderEnum.OrderRefundStatus.refundWait.getIndex());
        order.setOrderNo(nextOrderNo());
        order.setRemarks(param.getRemarks());
        order.setPackId(0);
        order.setFinishTime(0L);
        Integer diseaseId = addDisease(param);
        order.setDiseaseId(diseaseId);
        orderMapper.add(order);
        return order;
    }

    /**
     * 添加会诊订单
     *
     * @param conDoctorId
     * @return
     */
    @Override
    public Order addConsultation(String illCaseId, Integer conDoctorId) throws HttpApiException {

        Order order = createConsultation(illCaseId, conDoctorId);

        createOrderDoctor(order, conDoctorId);

        OrderSession orderSession = new OrderSession();
        orderSession.setToUserIds(conDoctorId.toString());
        GroupInfo groupInfo = createGroupMore(order, orderSession, userManagerImpl.getUser(conDoctorId).getName() + "会诊专家组");

        sendConsultNotify(conDoctorId, order, orderSession);

        /******************添加病程 start***********************/
        IllCaseInfo info = illCaseDao.getIllCase(illCaseId);
        OrderParam param = new OrderParam();
        param.setPicUrls(info.getImageUlrs());
        param.setDiseaseDesc(info.getMainCase());
        param.setConsultDoctorId(conDoctorId);
        param.setMainTreateDoctorId(info.getDoctorId());
        param.setGid(orderSession.getMsgGroupId());
        param.setOrderStatus(order.getOrderStatus());
        param.setPackType(order.getPackType());
        addHistoryRecord(param, order);
        /******************添加病程 end********************/

        GroupStateRequestMessage message = new GroupStateRequestMessage();
        message.setGid(groupInfo.getGid());
        message.setBizStatus(String.valueOf(order.getOrderStatus()));
        message.setParams(messageGroupService.getGroupParam(groupInfo.getGid()));
        MsgHelper.updateGroupBizState(message);

        return order;
    }

    public void updateTreatAdvise(Integer orderId, String treatAdvise) throws HttpApiException {
        Order order = orderMapper.getOne(orderId);
        if (order == null || order.getOrderType() != OrderType.consultation.getIndex()) {
            throw new ServiceException("不是会诊类型订单");
        }
        OrderSession orderSession = orderSessionService.findOneByOrderId(orderId);
        if (orderSession == null) {
            throw new ServiceException(30003, "订单还未绑定会话:" + orderId);
        }
        if (orderSession.getServiceBeginTime() == null) {
            throw new ServiceException("该订单尚未开始服务，不能填写会诊建议");
        }
        int doctor = ReqUtil.instance.getUserId();
        OrderExt ext = orderExtMapper.selectByDocAndOrderId(orderId, doctor);
        if (ext == null) {
            ext = new OrderExt();
            ext.setDoctorId(doctor);
            ext.setOrderId(orderId);
            ext.setTreatAdvise(treatAdvise);
            orderExtMapper.insert(ext);
            User u = userManager.getUser(doctor);
            String content = u.getName() + "医生已填写会诊建议";
            businessServiceMsg.sendNotifyMsgToAll(orderSession.getMsgGroupId(), content.toString());
        } else {
            ext.setTreatAdvise(treatAdvise);
            orderExtMapper.updateByPrimaryKey(ext);
        }
    }

    public String getTreatAdvise(Integer orderId) {
        OrderExt ext = orderExtMapper.selectByDocAndOrderId(orderId, ReqUtil.instance.getUserId());
        if (ext != null) {
            return ext.getTreatAdvise();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Object> getOrderTypeByGId(String gid) {
        List<OrderSessionContainer> orderSessionContainerLists = orderSessionContainerDao.findByMsgGroupId(gid);
        Map<String, Object> result = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(orderSessionContainerLists)) {
            List<Integer> orderIds = orderSessionContainerLists.stream()
                    .filter(orderSessionContainer -> Objects.nonNull(orderSessionContainer.getOrderId()))
                    .map(OrderSessionContainer::getOrderId)
                    .collect(Collectors.toList());
            List<Order> orders = orderMapper.findOrderByIds(orderIds);
            List<Integer> orderTypes = orders.stream().filter(order -> Objects.nonNull(order.getOrderType()))
                    .map(Order::getOrderType)
                    .collect(Collectors.toList());
            if (orderTypes.contains(OrderType.care.getIndex())) {
                result.put("orderType", OrderType.care.getIndex());
            } else {
                result.put("orderType", orderTypes.get(0));
            }
        } else {
            result.put("orderType", PackType.careTemplate.getIndex());
        }
        return result;
    }

    public List<Map> getTreatAdviseAndDocInfoList(Integer orderId) {
        Order order = getOne(orderId);
        if (order == null) {
            throw new ServiceException("未找到订单");
        }
        OrderSession session = orderSessionService.findOneByOrderId(orderId);
        if (session == null) {
            throw new ServiceException("未找到订单会话");
        }
        Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
        if (pack == null) {
            throw new ServiceException("数据异常");
        }
        List<OrderExt> list = getTreatAdviseList(orderId);
        Map<String, String> map = new HashMap<String, String>();
        for (OrderExt oe : list) {
            map.put(oe.getDoctorId().toString(), oe.getTreatAdvise());
        }
        //找到组员
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("gid", session.getMsgGroupId());
        JSON json = ImHelper.instance.postJson("inner/group", "groupUsers.action", paramMap);
        List<Map> result = JSON.parseArray(json.toString(), Map.class);
        result = new CopyOnWriteArrayList<Map>(result);
        for (Map<String, Object> m : result) {
            Integer userId = Integer.parseInt(m.get("id").toString());
            User u = userManager.getUser(userId);
            if (u.getUserType().intValue() == UserType.doctor.getIndex()) {
                if (u.getUserId().intValue() == pack.getDoctorId().intValue()) {
                    result.remove(m);//去掉主会诊医生
                }
                m.put("treatAdvise", map.get(m.get("id").toString()));
                m.put("title", u.getDoctor().getTitle());
                m.put("department", u.getDoctor().getDepartments());
                m.put("hospital", u.getDoctor().getHospital());
            } else {
                result.remove(m);//去掉非医生用户
            }
        }
        return result;
    }

    public List<OrderExt> getTreatAdviseList(Integer orderId) {
        return orderExtMapper.getOrderExtList(orderId);
    }

    public Order addNewConsultation(String illCaseId, String consultationId) throws HttpApiException {
        IllHistoryInfo caseInfo = null;
        ConsultationPackPageVo consulation = null;
        if (illCaseId == null) {
            throw new ServiceException("病历Id为空！");
        } else {
            caseInfo = illHistoryInfoDao.findById(illCaseId);
            if (caseInfo == null) {
                throw new ServiceException("未找到病历");
            }
        }
        if (consultationId == null) {
            throw new ServiceException("会诊包Id为空");
        } else {
            consulation = consultationPackService.getConsultPackDetail(consultationId);
            if (consulation == null) {
                throw new ServiceException("未找会诊包");
            }
        }
        Order order = createNewConsultation(caseInfo, consulation);
        createOrderDoctor(order, consulation);
        OrderSession orderSession = createOrderSession(order, consulation);
        order.setMsgId(orderSession.getMsgGroupId());
//    	IllHistoryInfo info = illHistoryInfoDao.findById(illCaseId);
        OrderParam param = new OrderParam();
        param.setConsultDoctorId(consulation.getConsultationDoctor());
        param.setConsultJoinDocs(new ArrayList<Integer>(consulation.getDoctorIds()));
        param.setGid(orderSession.getMsgGroupId());
        param.setOrderStatus(order.getOrderStatus());
        param.setPackType(order.getPackType());
        //新版电子病历id注入
        param.setIllHistoryInfoId(illCaseId);
        addHistoryRecord(param, order);
        /******************添加病程 end********************/
        /***发送通知***/
        sendNewConsultNotify(consulation.getConsultationDoctor(), order, orderSession);

        GroupStateRequestMessage message = new GroupStateRequestMessage();
        message.setGid(orderSession.getMsgGroupId());
        message.setBizStatus(String.valueOf(order.getOrderStatus()));
        message.setParams(messageGroupService.getGroupParam(orderSession.getMsgGroupId()));
        MsgHelper.updateGroupBizState(message);

        return order;
    }

    private String convertString(Set<Integer> list, String mainDoctId) {
        if (list == null) {
            return "";
        }
        String resString = mainDoctId + "|";
        for (Integer a : list) {
            resString += a + "|";
        }
        if (!(resString.indexOf("|") < 0)) {
            resString = resString.substring(0, resString.length() - 1);
        }
        return resString;
    }

    private OrderSession createOrderSession(Order order, ConsultationPackPageVo consulation) throws HttpApiException {
        String gName = userManagerImpl.getUser(consulation.getConsultationDoctor()).getName() + "会诊专家组";
        OrderSession orderSession = new OrderSession();
        orderSession.setToUserIds(convertString(consulation.getDoctorIds(), consulation.getConsultationDoctor() + ""));
        orderSession.setCreateTime(System.currentTimeMillis());
        orderSession.setOrderId(order.getId());
        CreateGroupRequestMessage createGroupParam = new CreateGroupRequestMessage();
        createGroupParam.setType(GroupTypeEnum.MULTI.getValue()); //多人组
        createGroupParam.setGtype(RelationTypeEnum.DOC_PATIENT.getValue());//
        createGroupParam.setFromUserId(String.valueOf(order.getDoctorId()));
        createGroupParam.setToUserId(orderSession.getToUserIds());
        createGroupParam.setGname(gName);
        createGroupParam.setSendRemind(false);
        createGroupParam.setCreateNew(true);
        createGroupParam.setBizStatus(String.valueOf(order.getOrderStatus()));
        Map<String, Object> param = new HashMap<>();
        int patientId = order.getPatientId();
        Patient p = patientService.findByPk(patientId);
        if (p != null) {
            param.put("patientName", p.getUserName());
            param.put("patientAge", p.getAgeStr());
            param.put("patientSex", p.getSex() == null ? null : BusinessUtil.getSexName(Integer.parseInt(p.getSex() + "")));
            param.put("patientArea", p.getArea());
        }
        param.put("orderId", order.getId());
        param.put("orderType", order.getOrderType());
        param.put("packType", order.getPackType());
        param.put("price", order.getPrice());
        param.put("mainDocId", consulation.getConsultationDoctor());
        param.put("groupId", consulation.getGroupId());
        Group group = groupService.getGroupById(consulation.getGroupId());
        String groupName = group == null ? "" : group.getName();
        param.put("groupName", groupName);

        createGroupParam.setParam(param);

        GroupInfo groupInfo = (GroupInfo) imsgService.createGroup(createGroupParam);
        orderSession.setMsgGroupId(groupInfo.getGid());
        orderSession.setLastModifyTime(System.currentTimeMillis());
        orderSessionService.save(orderSession);
        order.setMsgId(groupInfo.getGid());
        order.setImTitleName(gName);
        return orderSession;
    }

    private void createOrderDoctor(Order order, ConsultationPackPageVo consulation) {
        //添加OrderDoctor，记录会诊医生
        Integer orderId = order.getId();

        OrderDoctor orderDoctor = new OrderDoctor();
        orderDoctor.setOrderId(orderId);
        orderDoctor.setDoctorId(consulation.getConsultationDoctor());
        orderDoctor.setSplitRatio(consulation.getConsultationDoctorPercent());
        Double price = Double.valueOf(consulation.getConsultationPrice()) * consulation.getConsultationDoctorPercent();
        price = price / 100;
        orderDoctor.setSplitMoney(price);
        orderDoctorMapper.insert(orderDoctor);

        Map<String, Integer> map = consulation.getDoctorPercents();
        Set<Integer> doctorIds = consulation.getDoctorIds();
        for (Integer doc : doctorIds) {
            int percent = map.get(String.valueOf(doc));
            OrderDoctor od = new OrderDoctor();
            od.setOrderId(orderId);
            od.setDoctorId(doc);
            od.setSplitRatio(percent);
            price = Double.valueOf(consulation.getConsultationPrice()) * percent;
            price = price / 100;
            od.setSplitMoney(Double.valueOf(price));
            orderDoctorMapper.insert(od);
        }
    }

    private Order createNewConsultation(IllHistoryInfo caseInfo, ConsultationPackPageVo consulation) {
        Order order = new Order();
        order.setUserId(caseInfo.getUserId());
        order.setDoctorId(ReqUtil.instance.getUserId());
        order.setPatientId(caseInfo.getPatientId());
        order.setOrderType(OrderType.consultation.getIndex());
        order.setCreateTime(System.currentTimeMillis());
        order.setOrderStatus(OrderStatus.待预约.getIndex());
        order.setRefundStatus(OrderEnum.OrderRefundStatus.refundWait.getIndex());
        order.setOrderNo(nextOrderNo());

        Pack param = new Pack();
        param.setDoctorId(consulation.getConsultationDoctor());
        param.setPackType(PackType.consultation.getIndex());
        param.setStatus(PackEnum.PackStatus.open.getIndex());
        List<Pack> packs = packService.queryPack(param);
        if ((packs == null) || (packs.size() == 0)) {
            throw new ServiceException("该医生没有开通会诊套餐");
        }
        order.setPackId(packs.get(0).getId());
        order.setPackType(packs.get(0).getPackType());
        order.setPrice(Long.parseLong(consulation.getConsultationPrice() + ""));
        order.setFinishTime(0L);
        order.setConsultationPackId(consulation.getId());
        order.setGroupId(consulation.getGroupId());
        orderMapper.add(order);
        addJesQueTask(order);
        return order;
    }

    @Override
    public Order addNewConsultation(String illCaseId, Integer conDoctorId) throws HttpApiException {
        Order order = createNewConsultation(illCaseId, conDoctorId);

        createOrderDoctor(order, conDoctorId);

        OrderSession orderSession = new OrderSession();
        orderSession.setToUserIds(conDoctorId.toString());
        GroupInfo groupInfo = createGroupMore(order, orderSession, userManagerImpl.getUser(conDoctorId).getName() + "会诊专家组");
        /******************添加病程 start***********************/
        //IllCaseInfo info = illCaseDao.getIllCase(illCaseId);

        IllHistoryInfo info = illHistoryInfoDao.findById(illCaseId);

        OrderParam param = new OrderParam();
        param.setConsultDoctorId(conDoctorId);
        param.setMainTreateDoctorId(info.getDoctorId());
        param.setGid(orderSession.getMsgGroupId());
        param.setOrderStatus(order.getOrderStatus());
        param.setPackType(order.getPackType());
        //新版电子病历id注入
        param.setIllHistoryInfoId(illCaseId);
        addHistoryRecord(param, order);
        /******************添加病程 end********************/
        /***发送通知***/
        sendNewConsultNotify(conDoctorId, order, orderSession);

        GroupStateRequestMessage message = new GroupStateRequestMessage();
        message.setGid(groupInfo.getGid());
        message.setBizStatus(String.valueOf(order.getOrderStatus()));
        message.setParams(messageGroupService.getGroupParam(groupInfo.getGid()));
        MsgHelper.updateGroupBizState(message);

        return order;
    }


    public void addHistoryRecord(OrderParam param, Order order) {
        if (order.getOrderStatus() == OrderStatus.已支付.getIndex())
            param.setIsPay(true);
        else
            param.setIsPay(false);
        param.setPackType(order.getPackType());
        param.setOrderId(order.getId());
        if (param.getImagePaths() != null)
            param.setPicUrls(Arrays.asList(param.getImagePaths()));
        illHistoryInfoService.addHistoryRecordFromOrderParam(param);
        param.setPackType(order.getPackType());
    }

    public void sendNewOrderIllCard(OrderParam param) throws HttpApiException {
        String tag = "sendNewOrderIllCard";
        logger.info("{}. getDiseaseDesc={}, getIsSecondTreate={}, getOrderStatus={}", tag, param.getDiseaseDesc(), param.getIsSecondTreate(), param.getOrderStatus());

        if (StringUtils.isBlank(param.getDiseaseDesc()) && StringUtils.isBlank(param.getIsSecondTreate()))
            return;
        if (param.getOrderStatus() == OrderStatus.待支付.getIndex())
            return;

        logger.info("{}. starting...", tag);

        if (param.getPackType() == PackEnum.PackType.message.getIndex() ||
                param.getPackType() == PackType.phone.getIndex() ||
                param.getPackType() == PackType.integral.getIndex() ||
                param.getPackType() == PackType.careTemplate.getIndex() ||
                param.getPackType() == PackType.checkin.getIndex() ||
                param.getPackType() == PackType.online.getIndex()
                ) {
            MsgDocument msgDocument = new MsgDocument();

            Order order = orderMapper.getOne(param.getOrderId());
            String price = " ";
            if (order != null && order.getPackType() != PackType.integral.getIndex()) {
                if (order.getPrice() != null && order.getPrice() > 0)
                    price += order.getPrice() / 100 + "元";
                else
                    price += "免费";
            }
            msgDocument.setHeader((PackType.getTitle(param.getPackType().intValue()) + price));
            msgDocument.setHeaderIcon(getPackHeaderIcon(param.getPackType()));
            List<DocInfo> list = new ArrayList<>();

            Patient p = patientService.findByPk(param.getPatientId());
            DocInfo d1 = new DocInfo();
            d1.setTitle("患者");
            String content = p.getUserName();
            if (p.getSex() != null)
                content += " " + BusinessUtil.getSexName(Integer.valueOf(p.getSex()));
            if (StringUtils.isNotBlank(p.getAgeStr()))
                content += " " + p.getAgeStr();
            d1.setContent(content);
            d1.setType(0);
            list.add(d1);

            String diseaseID = param.getDiseaseID();
            if (StringUtils.isNotBlank(diseaseID)) {
                DocInfo d2 = new DocInfo();
                d2.setTitle("所患疾病");
                List<DiseaseType> dts = diseaseTypeRepository.findByIds(Arrays.asList(diseaseID.split(",")));
                if (dts != null) {
                    d2.setContent(dts.stream().map(o -> o.getName()).collect(Collectors.joining(" ")));
                }
                d2.setType(0);
                list.add(d2);
            }

            if (StringUtils.isNotBlank(param.getDiseaseDesc())) {
                DocInfo d3 = new DocInfo();
                d3.setTitle("病情描述");
                d3.setContent(param.getDiseaseDesc());
                d3.setType(0);
                list.add(d3);
            }

            if (StringUtils.isNotBlank(param.getHopeHelp())) {
                DocInfo d4 = new DocInfo();
                d4.setTitle("想获得的帮助");
                d4.setContent(param.getHopeHelp());
                d4.setType(0);
                list.add(d4);
            }

            if (StringUtils.isNotBlank(param.getDiseaseDuration())) {
                DocInfo d5 = new DocInfo();
                d5.setTitle("病症时长");
                d5.setContent(param.getDiseaseDuration());
                d5.setType(0);
                list.add(d5);
            }

            if (StringUtils.isNotBlank(param.getTreatCase())) {
                DocInfo d6 = new DocInfo();
                d6.setTitle("诊治情况");
                d6.setContent(param.getTreatCase());
                d6.setType(0);
                list.add(d6);
            }


            if (StringUtils.isNotBlank(param.getDrugCase()) ||
                    (param.getDrugPicUrls() != null && param.getDrugPicUrls().size() > 0)) {
                DocInfo d7 = new DocInfo();
                d7.setTitle("用药情况");
                if (StringUtils.isNotBlank(param.getDrugCase()))
                    d7.setContent(param.getDrugCase());
                d7.setType(0);
                if (param.getDrugPicUrls() != null && param.getDrugPicUrls().size() > 0) {
                    List<Map<String, Object>> d7List = new ArrayList<>();
                    for (String url : param.getDrugPicUrls()) {
                        Map<String, Object> d7map = new HashMap<>();
                        d7map.put("url", url);
                        d7List.add(d7map);
                    }
                    d7.setPic(d7List);
                    d7.setType(1);
                }
                list.add(d7);
            }

            if (param.getDrugInfoList() != null && param.getDrugInfoList().size() > 0) {
                String text = param.getDrugInfoList().stream().map(o -> o.getDrugName() + " ×" + o.getDrugCount()).collect(Collectors.joining(" ,"));
                DocInfo d8 = new DocInfo();
                d8.setTitle("选择药品");
                d8.setContent(text);
                d8.setType(0);
                list.add(d8);
            }

            if (param.getPicUrls() != null && param.getPicUrls().size() > 0) {
                DocInfo d9 = new DocInfo();
                d9.setTitle("影像资料");
                d9.setType(1);
                List<Map<String, Object>> d9List = new ArrayList<>();
                for (String url : param.getPicUrls()) {
                    Map<String, Object> d9map = new HashMap<>();
                    d9map.put("url", url);
                    d9List.add(d9map);
                }
                d9.setPic(d9List);
                list.add(d9);
            }

            if (StringUtils.isNotBlank(param.getExpectAppointmentIds())) {
                String str = baseDataDao.getExpectAppointmentsByIds(param.getExpectAppointmentIds());
                DocInfo d10 = new DocInfo();
                d10.setTitle("期望预约时间");
                d10.setType(0);
                d10.setContent(str);
                list.add(d10);
            }
            logger.info("{}. starting2...", tag);
            Map<String, Object> param_b = new HashMap<>();
            param_b.put("doctorId", param.getDoctorId());
            param_b.put("patientId", param.getPatientId());
            param_b.put("illHistoryInfoId", order.getIllHistoryInfoId());
            msgDocument.setBizParam(param_b);
            msgDocument.setList(list);
            logger.info("{}. end...", tag);
            GuideMsgHelper.getInstance().sendMsgDocument(param.getGid(), param.getUserId() + "", null, msgDocument, false);
        }
    }


    public String getPackHeaderIcon(Integer packType) {
        String imgPath = "";
        if (packType == PackType.message.getIndex()) {
            imgPath = "/pack/message.png";
        } else if (packType == PackType.phone.getIndex()) {
            imgPath = "/pack/phone.png";
        } else if (packType == PackType.careTemplate.getIndex()) {
            imgPath = "/pack/care.png";
        } else if (packType == PackType.integral.getIndex()) {
            imgPath = "/pack/integral.png";
        } else if (packType == PackType.checkin.getIndex()) {
            imgPath = "/pack/checkin.png";
        } else if (packType == PackType.online.getIndex()) {
            imgPath = "/pack/outPatient.png";
        } else if (packType == PackType.consultation.getIndex()) {
            imgPath = "/pack/consultation.png";
        }
        return MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.DEFAULT_BUCKET, imgPath);
    }

    @Override
    public void resubmitConsultation(Integer orderId) throws HttpApiException {
        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.待预约.getIndex());
        order = update(order);

        OrderSession orderSession = orderSessionService.findOneByOrderId(orderId);
        if (orderSession != null) {
            GroupStateRequestMessage message = new GroupStateRequestMessage();
            message.setBizStatus(String.valueOf(OrderStatus.待预约.getIndex()));
            message.setGid(orderSession.getMsgGroupId());
            MsgHelper.updateGroupBizState(message);
        }

        List<OrderDoctor> orderDoctors = orderDoctorService.findOrderDoctors(order.getId());
        OrderSession session = orderSessionService.findOneByOrderId(order.getId());
        // 目前t_order_doctor中只存储了大医生的Id，所以只有一条记录
        sendConsultNotify(orderDoctors.get(0).getDoctorId(), order, session);
    }

    /**
     * 发送会诊消息
     *
     * @param conDoctorId
     * @param order
     * @param orderSession
     */
    private void sendConsultNotify(Integer conDoctorId, Order order, OrderSession orderSession) throws HttpApiException {
        Order o = orderMapper.getOne(order.getId());
        if (o == null) {
            throw new ServiceException("订单ID为：" + order.getId() + "已经不存在！！！");
        }
        String illHistoryInfoId = o.getIllHistoryInfoId();
        IllHistoryInfo info = null;
        if (StringUtil.isNotEmpty(illHistoryInfoId)) {
            info = illHistoryInfoDao.findById(illHistoryInfoId);
        }
        //IllCaseInfo info = illCaseDao.getIllCase(illCaseInfoId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("patient", patientService.findByPk(order.getPatientId()));
        map.put("orderType", order.getOrderType());
        if (info != null && info.getIllContentInfo() != null && StringUtils.isNotBlank(info.getIllContentInfo().getIllDesc())) {
            map.put("diseaseDesc", info.getIllContentInfo().getIllDesc());
        } else
            map.put("diseaseDesc", "暂无");
        map.put("illHistoryInfoId", order.getIllHistoryInfoId());
        //map.put("diseaseDesc", info.getMainCase());
        map.put("illCaseInfoId", order.getIllCaseInfoId());
        sendOrderNoitfy(order.getDoctorId().toString(), conDoctorId.toString(), orderSession.getMsgGroupId(), OrderNoitfyType.neworder, map);
        messageGroupService.updateGroupBizState(orderSession.getMsgGroupId(), MessageGroupEnum.NEW_ORDER.getIndex());
    }

    private void sendNewConsultNotify(Integer conDoctorId, Order order, OrderSession orderSession) throws HttpApiException {
        Order o = orderMapper.getOne(order.getId());
        if (o == null) {
            throw new ServiceException("订单ID为：" + order.getId() + "已经不存在！！！");
        }
        String illHistoryInfoId = o.getIllHistoryInfoId();
        //IllCaseInfo info = illCaseDao.getIllCase(illCaseInfoId);
        IllHistoryInfo info = null;
        if (StringUtil.isNotEmpty(illHistoryInfoId)) {
            info = illHistoryInfoDao.findById(illHistoryInfoId);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("patient", patientService.findByPk(o.getPatientId()));
        map.put("orderType", o.getOrderType());
        if (info != null && info.getIllContentInfo() != null && StringUtils.isNotBlank(info.getIllContentInfo().getIllDesc())) {
            map.put("diseaseDesc", info.getIllContentInfo().getIllDesc());
        } else
            map.put("diseaseDesc", "暂无");
        map.put("illHistoryInfoId", o.getIllHistoryInfoId());
        sendOrderNoitfy(o.getDoctorId().toString(), conDoctorId.toString(), orderSession.getMsgGroupId(), OrderNoitfyType.neworder, map);
        messageGroupService.updateGroupBizState(orderSession.getMsgGroupId(), MessageGroupEnum.NEW_ORDER.getIndex());
    }

    private Order createConsultation(String illCaseId, Integer conDoctorId) throws HttpApiException {
        if (illCaseId == null) {
            throw new ServiceException("病历Id为空！");
        }
        if (conDoctorId == null) {
            throw new ServiceException("会诊医生Id为空");
        }
        IllCaseInfoPageVo caseInfo = illCaseServer.getIllCaseInfo(illCaseId);
        Order order = new Order();
        order.setUserId(caseInfo.getUserId());
        order.setDoctorId(caseInfo.getDoctorId());
        order.setPatientId(caseInfo.getPatientId());
        order.setOrderType(OrderType.consultation.getIndex());
        order.setCreateTime(System.currentTimeMillis());
        order.setOrderStatus(OrderStatus.待预约.getIndex());
        order.setRefundStatus(OrderEnum.OrderRefundStatus.refundWait.getIndex());
        order.setOrderNo(nextOrderNo());
        Pack param = new Pack();
        param.setDoctorId(conDoctorId);
        param.setPackType(PackType.consultation.getIndex());
        param.setStatus(PackEnum.PackStatus.open.getIndex());
        List<Pack> packs = packService.queryPack(param);
        if ((packs == null) || (packs.size() == 0)) {
            throw new ServiceException("该医生没有开通会诊套餐");
        }
        order.setPackId(packs.get(0).getId());
        order.setPackType(packs.get(0).getPackType());
        order.setPrice(packs.get(0).getPrice());
        order.setFinishTime(0L);
        Integer diseaseId = addDisease(caseInfo);
        order.setDiseaseId(diseaseId);
        order.setIllCaseInfoId(illCaseId);
        String groupId = consultationPackService.getConsultationOrderGroupByDoctorId(conDoctorId);
        /*if(StringUtils.isBlank(groupId)){
            groupId = GroupUtil.PLATFORM_ID;
		}*/
        GroupConsultationPack pack = consultationPackDao.getConsultationPackByGroupIdAndDoctorId(groupId, conDoctorId);
        if (pack != null) {
            order.setConsultationPackId(pack.getId());
        } else {
            throw new ServiceException("会诊医生找不到对应的会诊包");
        }
        order.setGroupId(groupId);
        orderMapper.add(order);
        addJesQueTask(order);

		/*更新电子病历的订单字段*/
        IllCaseInfo info = illCaseDao.getIllCase(illCaseId);
        if (info.getOrderId() == null) {
            illCaseDao.updateIllCaseOrderId(illCaseId, order.getId());
        }
        order.setMainCase(info.getMainCase());
        return order;
    }

    private Order createNewConsultation(String illCaseId, Integer conDoctorId) {
        if (illCaseId == null) {
            throw new ServiceException("病历Id为空！");
        }
        if (conDoctorId == null) {
            throw new ServiceException("会诊医生Id为空");
        }
        IllHistoryInfo caseInfo = illHistoryInfoDao.findById(illCaseId);
        Order order = new Order();
        order.setUserId(caseInfo.getUserId());
        order.setDoctorId(ReqUtil.instance.getUserId());
        order.setPatientId(caseInfo.getPatientId());
        order.setOrderType(OrderType.consultation.getIndex());
        order.setCreateTime(System.currentTimeMillis());
        order.setOrderStatus(OrderStatus.待预约.getIndex());
        order.setRefundStatus(OrderEnum.OrderRefundStatus.refundWait.getIndex());
        order.setOrderNo(nextOrderNo());
        Pack param = new Pack();
        param.setDoctorId(conDoctorId);
        param.setPackType(PackType.consultation.getIndex());
        param.setStatus(PackEnum.PackStatus.open.getIndex());
        List<Pack> packs = packService.queryPack(param);
        if ((packs == null) || (packs.size() == 0)) {
            throw new ServiceException("该医生没有开通会诊套餐");
        }
        order.setPackId(packs.get(0).getId());
        order.setPackType(packs.get(0).getPackType());
        order.setPrice(packs.get(0).getPrice());
        order.setFinishTime(0L);
        //Integer diseaseId = addDisease(caseInfo);
        //order.setDiseaseId(diseaseId);
        //order.setIllCaseInfoId(illCaseId);
        /**
         * 新版的电子病历
         */
        //order.setIllHistoryInfoId(illCaseId);
        String groupId = consultationPackService.getConsultationOrderGroupByDoctorId(conDoctorId);
        /*if(StringUtils.isBlank(groupId)){
            groupId = GroupUtil.PLATFORM_ID;
		}*/
        GroupConsultationPack pack = consultationPackDao.getConsultationPackByGroupIdAndDoctorId(groupId, conDoctorId);
        if (pack != null) {
            order.setConsultationPackId(pack.getId());
        } else {
            throw new ServiceException("会诊医生找不到对应的会诊包");
        }
        order.setGroupId(groupId);
        orderMapper.add(order);
        addJesQueTask(order);

        ///*更新电子病历的订单字段*/
        //IllCaseInfo info = illCaseDao.getIllCase(illCaseId);
        //if(info.getOrderId() == null){
        //    illCaseDao.updateIllCaseOrderId(illCaseId,order.getId());
        //}
        //order.setMainCase(info.getMainCase());
        return order;
    }

    private void createOrderDoctor(Order order, Integer conDoctorId) {
        //添加OrderDoctor，记录会诊医生
        String groupId = order.getGroupId();
        Map<String, String> map = consultationPackService.getConsultationPackByGroupIdAndDoctorId(groupId, conDoctorId, order.getPrice());
        OrderDoctor conOrderDoctor = new OrderDoctor();
        OrderDoctor uniOrderDoctor = new OrderDoctor();
        if (map != null) {
            Double uniPrice = Double.valueOf(map.get("uniPrice"));
            Double conPrice = Double.valueOf(map.get("conPrice"));
            Integer conPercent = Integer.valueOf(map.get("conPercent"));
            Integer uniPrecent = Integer.valueOf(map.get("uniPrecent"));
            conOrderDoctor.setSplitMoney(conPrice);
            conOrderDoctor.setSplitRatio(conPercent);
            uniOrderDoctor.setSplitMoney(uniPrice);
            uniOrderDoctor.setSplitRatio(uniPrecent);
        } else {
            throw new ServiceException("该会诊专家没有会诊资格");
        }
        conOrderDoctor.setDoctorId(conDoctorId);
        conOrderDoctor.setOrderId(order.getId());
        orderDoctorMapper.insert(conOrderDoctor);
        uniOrderDoctor.setDoctorId(order.getDoctorId());
        uniOrderDoctor.setOrderId(order.getId());
        orderDoctorMapper.insert(uniOrderDoctor);
    }


    private void updateConsoltationSplitMoney(Integer conDoctorId, Order order) {
        Map<String, String> map = consultationPackService.getConsultationPackById(order.getConsultationPackId(), order.getPrice());
        OrderDoctor conOrderDoctor = new OrderDoctor();
        OrderDoctor uniOrderDoctor = new OrderDoctor();
        if (map != null) {
            Double uniPrice = Double.valueOf(map.get("uniPrice"));
            Double conPrice = Double.valueOf(map.get("conPrice"));
            Integer conPercent = Integer.valueOf(map.get("conPercent"));
            Integer uniPrecent = Integer.valueOf(map.get("uniPrecent"));
            conOrderDoctor.setSplitMoney(conPrice);
            conOrderDoctor.setSplitRatio(conPercent);
            uniOrderDoctor.setSplitMoney(uniPrice);
            uniOrderDoctor.setSplitRatio(uniPrecent);
        } else {
            return;
        }
        conOrderDoctor.setDoctorId(conDoctorId);
        conOrderDoctor.setOrderId(order.getId());
        orderDoctorMapper.updateOrderDoctor(conOrderDoctor);
        uniOrderDoctor.setDoctorId(order.getDoctorId());
        uniOrderDoctor.setOrderId(order.getId());
        orderDoctorMapper.updateOrderDoctor(uniOrderDoctor);
    }

    public void patientExpectedTime(Integer orderId, String expectAppointmentIds) throws HttpApiException {
        int userId = ReqUtil.instance.getUserId();
        Order order = getOne(orderId);
        if (order == null || order.getOrderStatus() != OrderStatus.已支付.getIndex()) {
            throw new ServiceException("不是已支付状态不能添写期望时间");
        }
        if (order.getUserId().intValue() != userId) {
            throw new ServiceException("你不是该订单患者不能添加期望时间");
        }
        if (!StringUtil.isEmpty(expectAppointmentIds)) {
            Order upOrder = new Order();
            upOrder.setId(orderId);
            upOrder.setExpectAppointmentIds(expectAppointmentIds);
            order = update(upOrder);
        }
        OrderSession session = orderSessionService.findOneByOrderId(orderId);
        if (session != null) {
            Patient p = patientService.findByPk(order.getPatientId());
            if (p != null) {
                //发卡片
                MsgDocument msgDocument = new MsgDocument();
                String path = MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.DEFAULT_BUCKET, "/pack/checkin.png");
                msgDocument.setHeaderIcon(path);
                Map<String, Object> param_ = new HashMap<String, Object>();
                param_.put("illCaseInfoId", order.getIllCaseInfoId());
                param_.put("patientId", order.getPatientId());
                msgDocument.setBizParam(param_);
                msgDocument.setHeader("远程会诊  " + (order.getPrice() / 100) + "元");

                List<DocInfo> dlist = new ArrayList<DocInfo>();
                DocInfo docInfo = new DocInfo();
                docInfo.setTitle("患者");
                String name = p.getUserName() == null ? p.getTelephone() : p.getUserName();
                String sex = p.getSex() == null ? BusinessUtil.getSexName(null) : BusinessUtil.getSexName(Integer.valueOf(p.getSex()));
                String age = p.getAgeStr() == null ? "" : p.getAgeStr();
                docInfo.setContent(name + " " + sex + " " + age);
                docInfo.setType(0);
                dlist.add(docInfo);

                if (StringUtil.isNotEmpty(expectAppointmentIds)) {
                    docInfo = new DocInfo();
                    docInfo.setTitle("期望预约时间");
                    docInfo.setContent(baseDataDao.getExpectAppointmentsByIds(expectAppointmentIds));
                    docInfo.setType(0);
                    dlist.add(docInfo);
                }
                msgDocument.setList(dlist);
                GuideMsgHelper.getInstance().sendMsgDocument(session.getMsgGroupId(), "", null, msgDocument, false);
            }
        }
    }

    @Override
    public void confirmConsultation(OrderParam param, Integer conDoctorId) throws HttpApiException {
        if (param.getOrderId() == null) {
            throw new ServiceException("订单Id为空");
        }
        Order order = new Order();
        order.setId(param.getOrderId());
        order.setOrderStatus(OrderStatus.待支付.getIndex());
        orderMapper.update(order);
        

        order = getOne(param.getOrderId());
        //添加患者到会话组
        OrderSession session = orderSessionService.findOneByOrderId(order.getId());
        UpdateGroupRequestMessage request = new UpdateGroupRequestMessage();
        request.setGid(session.getMsgGroupId());
        request.setAct(1);
        request.setFromUserId(order.getDoctorId().toString());
        List<String> patientUserIds = Arrays.asList(order.getUserId().toString());
        request.setToUserId(OrderSession.appendStringUserId(patientUserIds));
        imsgService.updateGroup(request);

        messageGroupService.updateGroupBizState(session.getMsgGroupId(), MessageGroupEnum.CONFIRM_CONSULTATION.getIndex());
        sendSmsToPatient(order);
    }
    private void sendSmsToPatient(Order order){
//    	尊敬的{患者名}用户，{大医生名}医生已同意您的本次会诊咨询服务，请及时支付订单、并提前准备相关的病历资料及问题，等待与医生进行视频会诊。
    	Integer patientId = order.getPatientId();
    	Patient patient = patientMapper.selectByPrimaryKey(patientId);
    	if(patient == null || StringUtil.isEmpty(patient.getTelephone())){
    		logger.error("主会诊医生接受会诊发送短信给患者失败，订单ID：{}，患者ID", order.getId(), order.getPatientId());
    		return;
    	}
    	GroupConsultationPack pack = consultationPackDao.getById(order.getConsultationPackId());
    	if(pack != null){
    		pack = consultationPackDao.getConsultPackDetail(order.getConsultationPackId());
    		User u = userManager.getUser(pack.getConsultationDoctor());
    		final String content = "尊敬的"+patient.getUserName() + "用户，"+ u.getName() +"医生已同意您的本次会诊咨询服务，请及时支付订单、并提前准备相关的病历资料及问题，等待与医生进行视频会诊。";
        	mobSmsSdk.send(patient.getTelephone(), content);
    	}
    }

    @Override
    public void cancelConsultation(Integer orderId, Integer reason, Integer conDoctorId) throws HttpApiException {

        switch (reason) {

            case 1://待完善订单
                Order order2 = new Order();
                order2.setId(orderId);
                order2.setOrderStatus(OrderStatus.待完善.getIndex());
                order2 = update(order2);

                OrderSession session = orderSessionService.findOneByOrderId(orderId);
                session.setLastModifyTime(System.currentTimeMillis());
                orderSessionService.updateByPrimaryKeySelective(session);
                businessServiceMsg.sendNotifyMsgToUser(order2.getDoctorId().toString(), session.getMsgGroupId(), "请完善患者病历信息");
                //通知IM订单的状态
                GroupStateRequestMessage requestMsg = new GroupStateRequestMessage();
                requestMsg.setGid(session.getMsgGroupId());
                requestMsg.setBizStatus(String.valueOf(OrderStatus.待完善.getIndex()));
                MsgHelper.updateGroupBizState(requestMsg);
                break;
            case 2://取消订单
                RedisLock lock = new RedisLock();
                Order order = orderMapper.getOne(orderId);
                if (order == null) {
                    throw new ServiceException(30007, "Entity " + getClass() + "#" + orderId + " can't not found!");
                }
                verifyData(order);
                // lock
                boolean locked = lock.lock(orderId + "", LockType.order);
                if (!locked) {
                    throw new ServiceException(10007, "get order:" + orderId + " lock fail");
                }
                try {
                    cancelOrder(order, conDoctorId, orderCancelEnum.manual.getIndex());

                    OrderSession orderSession = orderSessionService.findOneByOrderId(order.getId());
                    orderSession.setLastModifyTime(System.currentTimeMillis());
                    orderSessionService.updateByPrimaryKeySelective(orderSession);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("orderType", order.getOrderType());
                    String specialistDoctorName = "";
                    Integer specialistDoctorId = null;
                    Integer assisantDoctorId = order.getDoctorId();
                    List<OrderDoctor> docs = orderDoctorService.findOrderDoctors(orderId);
                    if (!Util.isNullOrEmpty(docs)) {
                        for (OrderDoctor od : docs) {
                            int doctorId = od.getDoctorId();
                            assisantDoctorId = order.getDoctorId();
                            if (doctorId != assisantDoctorId) {
                                specialistDoctorId = doctorId;
                                User specialistDoctor = userManagerImpl.getUser(doctorId);
                                if (specialistDoctor != null) {
                                    specialistDoctorName = specialistDoctor.getName();
                                }
                            }
                        }
                    }
                    map.put("toAssistantMessage", specialistDoctorName + "医生取消本次会诊服务，本订单已取消");
                    sendOrderNoitfy(order.getDoctorId().toString(), assisantDoctorId + "," + specialistDoctorId, orderSession.getMsgGroupId(), OrderNoitfyType.cancelOrder, map);
                    messageGroupService.updateGroupBizState(orderSession.getMsgGroupId(), MessageGroupEnum.CANCEL_ORDER.getIndex());

                    lock.unlock(orderId + "", LockType.order);
                } catch (Exception e) {
                    lock.unlock(orderId + "", LockType.order);
                    throw e;
        }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void updateGroupId(Integer orderId, String groupId) {
        if (orderId == null) {
            throw new ServiceException("订单Id为空");
        }
        Order order = getOne(orderId);
        if (order == null) {
            throw new ServiceException("this order doesn't exists");
        }
        //门诊在创建订单时已保存了groupId；会诊在大医生接受会诊时确定集团，保存groupId
        if ((order.getOrderType() == OrderType.outPatient.getIndex())
                || (order.getOrderType() == OrderType.care.getIndex())
                || (order.getOrderType() == OrderType.consultation.getIndex())) {
            return;
        }
        if (groupId == null) {
            List<GroupDoctor> gdocs = groupDoctorDao.getByDoctorId(order.getDoctorId(), null, GroupDoctor.class);
            if (gdocs.isEmpty()) {
                groupId = GroupUtil.PLATFORM_ID;
            } else {
                boolean existNormalGroup = false;
                for (GroupDoctor gdoc : gdocs) {
                    Group g = groupService.getGroupById(gdoc.getGroupId());
                    if ((g != null) && (g.getSkip() != null) && GroupSkipStatus.normal.getIndex().equals(g.getSkip())) {
                        existNormalGroup = true;
                    }
                }
                if (existNormalGroup) {
                    throw new ServiceException("集团Id为空");
                }
                groupId = GroupUtil.PLATFORM_ID;
            }
        }
        Order param = new Order();
        param.setId(orderId);
        param.setGroupId(groupId);
        updateOrder(param);
    }

    /**
     * 修改订单状态
     *
     * @param param
     */
    @Override
    public void updateOrderStatus(OrderParam param) {
        Order order = new Order();
        order.setId(param.getOrderId());
        order.setOrderStatus(param.getOrderStatus());
        updateOrder(order);
    }

    @Override
    public Integer getOrderStatus(OrderParam param) {
        Order order = getOne(param.getOrderId());

        return order.getOrderStatus();
    }

    /**
     * 获取订单
     */
    @Override
    public List<OrderVO> getOrders(OrderParam param) {
        List<OrderVO> vos = orderMapper.findOrdersNoLimit(param);
        return vos;
    }

    /**
     * </p>追加订单</p>
     *
     * @param param
     * @author fanp
     * @date 2015年8月10日
     */
    @Override
    public void addOrder(OrderParam param) {

    }

    /**
     * </p>查询订单</p>
     *
     * @author eiX
     * @date 2015年8月17日
     */
    @Override
    public Order getOne(Integer orderId) {
        return orderMapper.getOne(orderId);
    }

    @Override
    public List<Order> findOrderByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return orderMapper.findOrderByIds(ids);
    }

    @Override
    public Order getOneByCarePlanId(String carePlanId) throws ServiceException {
        Order order = orderMapper.findOrderByCarePlanId(carePlanId);
        if (null == order) {
            throw new ServiceException("order不存在");
        }
        return order;
    }

    @Override
    public List<Order> findByCarePlanIds(List<String> carePlanIds) throws ServiceException {
        if (CollectionUtils.isEmpty(carePlanIds)) {
            return null;
        }
        List<Order> list = orderMapper.findByCarePlanIds(carePlanIds);
        return list;
    }


    /**
     * </p>查询订单</p>
     *
     * @author eiX
     * @date 2015年8月17日
     */
    @Override
    public Order findOrderBydiseaseId(Integer id) {
        return orderMapper.findOrderBydiseaseId(id);
    }

    /**
     * 修改状态
     */
    @Override
    public void updateOrder(Order order) {
        orderMapper.update(order);
    }

    private Order update(Order order) {
        orderMapper.update(order);
        return getOne(order.getId());
    }

    public Boolean checkOrderRefundStatus(Recharge recharge) {
        return null;
    }

    @Override
    public String checkOrderStatus(CheckOrderParam param, UserSession session) {


        if (session == null) {
            throw new ServiceException("请登录");
        }

        if ((session.getUserType() == null) || (session.getUserType() != UserEnum.UserType.patient.getIndex())) {
            throw new ServiceException("您不是患者，无法查询订单状态");
        }

        if (null == param.getPayNo()) {
            throw new ServiceException("订单号不能为空!");
        }

        RechargeParam rechargeParam = new RechargeParam();
        rechargeParam.setPayNo(param.getPayNo());
        RechargeVO rechargeVo = rechargeService.findRechargeByPayNo(rechargeParam);
        if (OrderEnum.OrderStatus.已支付.getIndex() == rechargeVo.getRechargeStatus()) {
            return "支付成功!";
        } else {
            //分别去微信或支付宝服务器去查
            if (AccountEnum.PayType.alipay.getIndex() == param.getPayType()) {
                //循环去查支付结果
                if (alipayPayService.singleTransactionQueryHandelFunction(String.valueOf(rechargeParam.getPayNo()), "")) {
                    //需要往个人账户加钱或扣钱,成功告知客户端
                    return "支付成功!";
                } else {
                    throw new ServiceException("等待支付");
                }
            } else {
                //微信支付接口查询
                if (wechatPayService.singleTransactionQueryHandelFunction(String.valueOf(rechargeParam.getPayNo()), "")) {
                    return "支付成功!";
                } else {
                    throw new ServiceException("等待支付");
                }
            }
        }
    }


    // 逻辑错误：0007：完全无法满足在线订单按照天分组的功能（获取医生值班订单(按照天分组)）
    @Override
    public PageVO findOrders(OrderParam param, UserSession session) {

        if (session == null) {
            throw new ServiceException("请登录");
        }

        PageVO pageVO = new PageVO();

        if (param.getDoctorId() != null) {
            List<Integer> list = orderDoctorMapper.findOrderIdByDoctorId(param.getDoctorId());
            if ((list != null) && (list.size() > 0)) {
                param.setOrderIds(list);
            }
        }

        if (session.getUserType() == UserEnum.UserType.patient.getIndex()) {// 针对患者不需要获取待预约的会诊订单
            param.setUserType(UserEnum.UserType.patient.getIndex());
        } else if (session.getUserType() == UserEnum.UserType.doctor.getIndex()) {// 针对医生不需要获取待支付的图文咨询
            param.setUserType(UserEnum.UserType.doctor.getIndex());
        }
        List<OrderVO> orders = orderMapper.findOrders(param);
        Integer countOrders = orderMapper.findOrdersCount(param);
        for (OrderVO orderVo : orders) {
            if (orderVo.getOrderType() == OrderEnum.OrderType.checkIn.getIndex()) {
                List<CheckInVO> checkInvos = checkInMapper.getCheckInByOrderId(orderVo.getOrderId());
                if ((checkInvos != null) && (checkInvos.size() > 0)) {
                    CheckInVO checkInvo = checkInvos.get(0);
                    orderVo.setBirthday(checkInvo.getBirthday());
                    orderVo.setPatientName(checkInvo.getPatientName());
                    orderVo.setSex(checkInvo.getSex());
                    orderVo.setArea(checkInvo.getArea());
                }
            }
            if (orderVo.getGroupId() != null) {
                Group group = groupService.getGroupById(orderVo.getGroupId());
                if (group != null) {
                    orderVo.setGroupName(group.getName());
                }
            }
        }

        if (session.getUserType() == UserEnum.UserType.patient.getIndex()) {
            List<OrderVO> patientOrderList = new ArrayList<OrderVO>();
            for (OrderVO orderVo : orders) {
                int orderType = orderVo.getOrderType();
                int assistantId = orderVo.getDoctorId();
                if (OrderEnum.OrderType.consultation.getIndex() == orderType) {
                    List<OrderDoctor> docs = orderDoctorService.findOrderDoctors(orderVo.getOrderId());
                    if (!Util.isNullOrEmpty(docs)) {
                        for (OrderDoctor od : docs) {
                            int specialistId = od.getDoctorId();
                            if (specialistId != assistantId) {
                                orderVo.setUserVo(findUserVO(specialistId));
                                // 设置医生信息
                                orderVo.setDoctorVo(this.findDoctorVo(specialistId));
                                orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));
                            }
                        }
                    }
                } else {
                    // 设置医生用户信息
                    orderVo.setUserVo(findUserVO(orderVo.getDoctorId()));
                    // 设置医生信息
                    orderVo.setDoctorVo(this.findDoctorVo(orderVo.getDoctorId()));

                    orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));

                    patientOrderList.add(orderVo);
                }
            }
        } else if (session.getUserType() == UserEnum.UserType.doctor.getIndex()) {
            for (OrderVO orderVo : orders) {
                orderVo.setUserVo(findUserVO(orderVo.getUserId()));
                orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));
            }
        } else if (session.getUserType() == UserEnum.UserType.customerService.getIndex()) {
            for (OrderVO orderVo : orders) {
                orderVo.setUserVo(findUserVO(orderVo.getUserId()));
                orderVo.setDoctorVo(this.findDoctorVo(orderVo.getDoctorId()));
                orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));
            }

        } else {
            for (OrderVO orderVo : orders) {
                orderVo.setUserVo(findUserVO(orderVo.getUserId()));
                orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));
            }
        }

        pageVO.setPageData(orders);
        pageVO.setTotal(Long.valueOf(countOrders));
        pageVO.setPageSize(param.getPageSize());
        pageVO.setPageIndex(param.getPageIndex());
        return pageVO;
    }

    @Override
    public PageVO findPaidOrders(OrderParam param) {

        if (StringUtils.isNotBlank(param.getUserName()) || StringUtils.isNotBlank(param.getTelephone())) {

            List<Integer> userIds = userManagerImpl.findUserBlurryByNameAndIphone(param.getUserName(),
                    param.getTelephone());
            if (userIds.isEmpty()) {
                userIds.add(Integer.MIN_VALUE);
            }
            param.setUserIds(userIds);

        }

        PageVO pageVO = new PageVO();

        List<OrderVO> orders = orderMapper.findPaidOrders(param);
        Integer countOrders = orderMapper.findPaidOrdersCount(param);

        for (OrderVO orderVo : orders) {
            if (null == orderVo.getPayTime()) {
                orderVo.setPayTime(orderVo.getCreateTime());
            }
            orderVo.setUserVo(findUserVO(orderVo.getUserId()));
            orderVo.setDoctorVo(this.findDoctorVo(orderVo.getDoctorId()));
            orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));

            //积分问诊支付类型为积分套餐名字
            if (orderVo.getPackType().intValue() == PackType.integral.getIndex()) {
                orderVo.setPayTypeName(orderVo.getPointName());
                orderVo.setMoney(Long.valueOf(orderVo.getPoint()) * 100);
                //免费套餐支付类型为无
            } else if (orderVo.getPayType() == 0) {
                orderVo.setPayTypeName("无");
            } else {
                orderVo.setPayTypeName(AccountEnum.PayType.getTitle(orderVo.getPayType()));
            }

        }

        pageVO.setPageData(orders);
        pageVO.setTotal(Long.valueOf(countOrders));
        pageVO.setPageSize(param.getPageSize());
        pageVO.setPageIndex(param.getPageIndex());
        return pageVO;
    }

    /**
     * </p>查看订单详情</p>
     *
     * @return
     * @author fanp
     * @date 2015年9月8日
     */
    @Override
    public OrderDetailVO detail(OrderParam param, UserSession session) throws HttpApiException {
        // 根据订单类型判断是订单还是报到
        User user = userManagerImpl.getUser(ReqUtil.instance.getUserId());
        // 返回订单病例信息
        OrderVO orderVo = orderMapper.detail(param);
        if (orderVo == null) {
            throw new ServiceException("订单详情信息不存在");
        }
        if (user.getUserType() != UserEnum.UserType.patient.getIndex()) {
            String tel = orderVo.getTelephone();
            orderVo.setYsTelphone(tel);
            if (!StringUtils.isEmpty(tel) && (tel.length() > 3)) {
                int end = 7;
                if (end > tel.length()) {
                    end = tel.length();
                }
                tel = tel.substring(0, 3) + "****" + tel.substring(end);
                orderVo.setTelephone(tel);
            }
        }
        if ((orderVo.getPackId() != null) && (orderVo.getPackId() != 0)) {
            orderVo.setPackName(packService.getPack(orderVo.getPackId()).getName());
        }
        orderVo.setOrderSessionStatus(orderVo.getOrderStatus());
        OrderSession orderSession = orderSessionService.findOneByOrderId(orderVo.getOrderId());
        if (orderSession != null) {
            orderVo.setMsgGroupId(orderSession.getMsgGroupId());
            if (OrderStatus.已支付.getIndex() == orderVo.getOrderStatus()) {
                if (orderSession.getServiceBeginTime() != null) {
                    orderVo.setOrderSessionStatus(17);
                }
            }
        }
        if (session.getUserType() == 1) {
            // 返回医生信息
            int orderType = orderVo.getOrderType();
            int d1Id = orderVo.getDoctorId();//发起医生
            if (OrderEnum.OrderType.consultation.getIndex() == orderType) {
                List<DoctorVO> doctorVos = new ArrayList<DoctorVO>();
                DoctorVO d1 = findDoctorVo(d1Id);
                d1.setDoctorRole(2);
                doctorVos.add(d1);
                
                Pack pack = packService.getPack(orderVo.getPackId());
                if(pack == null){
                	throw new ServiceException("未找到套餐");
                }
                DoctorVO d2 = findDoctorVo(pack.getDoctorId());
                d2.setDoctorRole(1);
                doctorVos.add(d2);
                orderVo.setDoctorVos(doctorVos);
            } else {
                orderVo.setDoctorVo(findDoctorVo(d1Id));
            }
        } else {
            // 返回 用户信息
            orderVo.setUserVo(findUserVO(orderVo.getUserId()));
        }
        // 根据套餐类型分别返回病情信息或病例ID
        OrderDetailVO detailVO = null;
        if (orderVo.getDiseaseId() != null) {
            detailVO = findOrderDetailVO(orderVo);
        } else
            detailVO = new OrderDetailVO();
        /**
         * 预约名医订单添加医院
         */
        if (StringUtils.isNotBlank(orderVo.getHospitalId())) {
            HospitalPO po = baseDataDao.getHospitalDetail(orderVo.getHospitalId());
            if (po != null) {
                orderVo.setHospitalName(po.getName());
            }
        }

        List<CureRecord> cureRecord = cureRecordService.findByOrderId(param.getOrderId());
        detailVO.setCureRecordList(cureRecord);
        Patient p = patientService.findByPk(orderVo.getPatientId());
        if (!Objects.isNull(p)) {
            orderVo.setPatientName(p.getUserName());
            orderVo.setSex(p.getSex());
            orderVo.setPatientAge(p.getAgeStr());
            orderVo.setBirthday(p.getBirthday());
        }
        detailVO.setOrderVo(orderVo);

        List<Pack> list = packService.findByDoctorIdAndPackType(orderVo.getDoctorId(), orderVo.getPackType());
        if (list != null && list.size() > 0)
            detailVO.setReplyCount(list.get(0).getReplyCount());
        return detailVO;
    }


    /**
     * 支付
     */
    @Override
    public PreOrderVO addPayOrder(OrderParam param, UserSession session) throws HttpApiException {
        return addPayOrder(param, session, false);
    }

    /**
     * 支付（博德嘉联）
     */
    @Override
    public PreOrderVO addPayOrder(OrderParam param, UserSession session, boolean isBDJL) throws HttpApiException {
        if (session == null) {
            throw new ServiceException("请登录");
        }
        if ((session.getUserType() == null) || (session.getUserType() != UserEnum.UserType.patient.getIndex())) {
            throw new ServiceException("您不是患者，无法购买服务");
        }

        if (param.getPayType() == AccountEnum.PayType.wechat.getIndex()) {
            param.setPayType(AccountEnum.PayType.wechat.getIndex());
        } else if (param.getPayType() == AccountEnum.PayType.alipay.getIndex()) {
            param.setPayType(AccountEnum.PayType.alipay.getIndex());
        } else if (param.getPayType() == AccountEnum.PayType.integral.getIndex()) {
            param.setPayType(AccountEnum.PayType.integral.getIndex());
        } else {
            throw new ServiceException("请选择一种支付方式");
        }

        if (null == param.getOrderId()) {
            throw new ServiceException("订单ID不能为空!");
        }

        Order order = this.getOne(param.getOrderId());

        if (order == null) {
            throw new ServiceException("订单不存在!");
        }

        if (order.getOrderStatus() != OrderEnum.OrderStatus.待支付.getIndex()) {
            throw new ServiceException("您的订单不支持支付或已支付!");
        }

        //2.查找是否存在，如果不存在新建
        RechargeParam reParam = new RechargeParam();
        reParam.setOrderId(param.getOrderId());
        reParam.setUserId(param.getUserId());
        Recharge recharge = rechargeService.findRechargeByOrderId(reParam);

        if (recharge == null) {
            RechargeParam rParam = new RechargeParam();
            rParam.setUserId(param.getUserId());
            rParam.setMoney(order.getPrice());
            rParam.setPayType(param.getPayType());
            rParam.setSourceType(AccountEnum.RechargeStatus.初始.getIndex());
            rParam.setSourceId(order.getId());
            recharge = rechargeService.addRecharge(rParam);
        } else if (recharge.getPayType().intValue() != param.getPayType().intValue()) {
            //用户更改“支付方式”时需要更新数据表
            RechargeVO rechargevo = new RechargeVO();
            rechargevo.setId(recharge.getId());
            rechargevo.setPayType(param.getPayType());
            rechargeService.updateRecharge(rechargevo);
        }
        String packName = OrderEnum.getOrderType(order.getOrderType());

        Pack pack = null;
        if ((order.getPackId() != null) && (order.getPackId() != 0)) {
            pack = packService.getPack(order.getPackId());
            if (pack != null) {
                packName = pack.getName();
            }
        }

        //积分问诊支付，服务套餐不能为空
        if (param.getPayType() == AccountEnum.PayType.integral.getIndex() && pack == null) {
            throw new ServiceException("订单中关联的服务套餐为空，不能支付");
        }

        PreOrderVO vo = new PreOrderVO();
        vo.setPayNo(recharge.getPayNo());

        if (PropertiesUtil.isNonProductionEnv()) {
            recharge.setRechargeMoney(1L);//非生产环境设置支付金额为1分
        }

        if (param.getPayType() == AccountEnum.PayType.wechat.getIndex()) {
            //统一下订单微信
            UnifiedOrderPayReqData unifiedOrderPayReqData
                    = new UnifiedOrderPayReqData(OrderEnum.getOrderType(order.getOrderType()),
                    packName,
                    String.valueOf(recharge.getPayNo()),
                    recharge.getRechargeMoney().intValue(),//金额写死
                    PropertiesUtil.getContextProperty(PackConstants.WXPAY_CALL_BACK),
                    "APP",
                    isBDJL);

            AppPayReqData appPayReqData = wechatPayService.generateAppPayParam(wechatPayService.unifiedOrder(unifiedOrderPayReqData), recharge.getPayNo());
            vo.setPayReq(BeanUtil.copy(appPayReqData, AppPayVO.class));
        } else if (param.getPayType() == AccountEnum.PayType.alipay.getIndex()) {
            //生成支付宝签名
            vo.setOrderInfo(alipayPayService.takeOrderSignatrue(String.valueOf(recharge.getPayNo()),
                    recharge.getRechargeMoney(),
                    recharge.getCreateTime(),
                    packName,
                    OrderEnum.getOrderType(order.getOrderType())));
        } else if (param.getPayType() == AccountEnum.PayType.integral.getIndex()) {
            //扣减患者积分
            boolean pointsDiagnose = false;

            if (pack != null && StringUtil.isNotEmpty(pack.getGoodsGroupId()) && pack.getPoint() != null && pack.getPoint() != 0
                    && order.getUserId() != null && order.getUserId() != 0
                    && order.getPatientId() != null && order.getPatientId() != 0
                    && order.getDoctorId() != null && order.getDoctorId() != 0) {
//                pointsDiagnose = drugManager.pointsDiagnose(pack.getGoodsGroupId(), order.getUserId(), order.getPatientId(), order.getDoctorId(), pack.getPoint());
                pointsDiagnose = drugApiClientProxy.pointsDiagnose(pack.getGoodsGroupId(), order.getUserId(), order.getPatientId(), order.getDoctorId(), pack.getPoint());
            }
            if (pointsDiagnose) {//积分扣减成功
                //订单支付成功，后续处理
                boolean endIntegralPay = endIntegralPay(order, recharge);
                if (endIntegralPay) {
                    vo.setIntegralOrder(Boolean.TRUE);
                    vo.setOrderStatus(OrderEnum.OrderStatus.已支付.getIndex());
                    return vo;
                } else {
                    throw new ServiceException("订单支付失败");
                }
            } else {
                throw new ServiceException("订单支付失败");
            }
        }
        //套餐价格不为0的情况下 支付完成需要将该医生加为好友 add by 姜宏杰 2016年1月27日15:18:50
        relationService.addRelationTag2(PackUtil.convert(order));
        /*//从会话订单中查询信息
        OrderSession orderSession=orderSessionService.findOneByOrderId(param.getOrderId());
        iDoctorTimeDAO.addCount(order.getDoctorId(), orderSession.getServiceBeginTime(), orderSession.getServiceEndTime());*/
        return vo;
    }

    private boolean endIntegralPay(Order order, Recharge recharge) {
        String payNo = recharge.getPayNo();
        if (StringUtil.isBlank(payNo)) {
            throw new ServiceException(300, "订单交易号为空!");
        }
        PaymentVO paymentVo = new PaymentVO();
        paymentVo.setPartner(Configure.getAppid());
        paymentVo.setPayNo(payNo);
        paymentVo.setPaymentMoney(recharge.getRechargeMoney().intValue());
        paymentVo.setPayType(AccountEnum.PayType.integral.getIndex());
        paymentVo.setTradeStatus(PackConstants.AIL_TRADE_SUCCESS);
        paymentVo.setPayAlftNo(null);
        paymentVo.setIsSuccess(true);

        RedisLock lock = new RedisLock();

        RechargeParam param = new RechargeParam();
        param.setPayNo(paymentVo.getPayNo());
        RechargeVO recharegVo = rechargeService.findRechargeByPayNo(param);
        if (recharegVo == null || recharegVo.getPayNo() == null) {
            throw new ServiceException(300, "找不到交易记录");
        }
        try {
            boolean locked = lock.lock(recharegVo.getUserId() + "_" + recharegVo.getPayNo(), LockType.orderpay);
            if (locked) {
                try {
                    payHandleBusiness.handlePayBusinessLogic(paymentVo);
                    lock.unlock(recharegVo.getUserId() + "_" + recharegVo.getPayNo(), LockType.orderpay);
                } catch (Exception e) {
                    lock.unlock(recharegVo.getUserId() + "_" + recharegVo.getPayNo(), LockType.orderpay);
                    throw e;
                }
            } else {
                throw new ServiceException(300, "未获取到订单交易号锁,订单交易号：" + paymentVo.getPayNo() + "处理失败!");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 此方法为原来医生处理订单的时候，调用，现在废弃掉
     */
    @Override
    public OrderSessionVO addSession(OrderParam param, UserSession userSession) {

        OrderSessionVO orderSessonVo = new OrderSessionVO();

        Order order = orderMapper.getOne(param.getOrderId());
        if (order == null) {
            throw new ServiceException("订单不存在!");
        }
        Integer orderStatus = order.getOrderStatus();
        orderSessonVo.setOrderStatus(orderStatus);

        List<OrderSession> orderSessions = orderSessionService.findByOrderId(param.getOrderId());

        if (orderSessions != null && orderSessions.size() > 0) {
            OrderSession orderSession = orderSessions.get(0);
            orderSessonVo.setMsgGroupId(orderSession.getMsgGroupId());
            orderSessonVo.setOrderId(orderSession.getOrderId());
            orderSessonVo.setSessionId(orderSession.getId());
            orderSessonVo.setCreateTime(orderSession.getCreateTime());
        }
        //先判断是否是好友，不是好友则添加好友并发送消息。如果是好友则不添加也不发送消息
        DoctorPatient doctorPatient = null;
        try {
            doctorPatient = tagDao.findByDoctorIdAndPatientId(order.getDoctorId(), order.getPatientId());
            if (doctorPatient == null) {
                //baseUserService.setDoctorPatient( order.getDoctorId(),order.getUserId());
                //2016-12-12医患关系的变化
                baseUserService.addDoctorPatient(order.getDoctorId(), order.getPatientId(), order.getUserId());
                // 关注医生公众号
                pubGroupService.addSubUser(param.getDoctorId(), order.getUserId());
                businessServiceMsg.sendEventFriendChange(EventEnum.ADD_FRIEND, order.getDoctorId().toString(), order.getUserId().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderSessonVo;
    }

    @Override
    public GroupInfo createGroup(Order order, OrderSession orderSession) throws HttpApiException {
        orderSession.setCreateTime(System.currentTimeMillis());
        orderSession.setOrderId(order.getId());
        CreateGroupRequestMessage createGroupParam = new CreateGroupRequestMessage();
        createGroupParam.setType(1);
        if (order.getOrderType() == OrderType.outPatient.getIndex()) {
            createGroupParam.setGtype("1_3_2");//门诊为1_3_2,其他为"1_3"
        } else {
            createGroupParam.setGtype("1_3");//门诊为1_3_2,其他为"1_3"
        }


        createGroupParam.setFromUserId(String.valueOf(order.getDoctorId()));
        createGroupParam.setToUserId(String.valueOf(order.getUserId()));

        Map<String, Object> param = new HashMap<String, Object>();
        if (order.getOrderType() == OrderEnum.OrderType.checkIn.getIndex()) {
            List<CheckInVO> checkInvos = checkInMapper.getCheckInByOrderId(order.getId());
            if ((checkInvos != null) && (checkInvos.size() > 0)) {
                CheckInVO checkInvo = checkInvos.get(0);
                param.put("patientName", checkInvo.getUserName());
//					if(checkInvo.getBirthday()!=null && checkInvo.getBirthday()>0) {
//						param.put("birthday", checkInvo.getBirthday());
//					}
                param.put("patientAge", checkInvo.getPatientAge());
                param.put("patientSex", checkInvo.getSex() == null ? null : BusinessUtil.getSexName((int) checkInvo.getSex()));
                param.put("patientArea", checkInvo.getArea());
            }
        } else {
            int diseaseId = order != null ? order.getDiseaseId() : 0;
            Disease disease = diseaseService.findByPk(diseaseId);
            if (disease != null) {
                param.put("patientName", disease.getUserName());
//					if(disease.getBirthday()!=null && disease.getBirthday()>0) {
//						param.put("birthday", disease.getBirthday());
//					}
                param.put("patientAge", disease.getPatientAge());
                param.put("patientSex", disease.getSex() == null ? null : BusinessUtil.getSexName(disease.getSex()));
                param.put("patientArea", disease.getArea());
            }
        }
        createGroupParam.setCreateNew(true);
        createGroupParam.setBizStatus(String.valueOf(order.getOrderStatus()));
        param.put("orderId", order.getId());
        param.put("orderType", order.getOrderType());
        param.put("packType", order.getPackType());
        param.put("price", order.getPrice());
        createGroupParam.setParam(param);
        GroupInfo groupInfo = (GroupInfo) imsgService.createGroup(createGroupParam);
        orderSession.setMsgGroupId(groupInfo.getGid());
        orderSession.setLastModifyTime(System.currentTimeMillis());
        orderSessionService.save(orderSession);
        return groupInfo;
    }

    @Override
    public GroupInfo createGroupMore(Order order, OrderSession orderSession) throws HttpApiException {
        return createGroupMore(order, orderSession, userManagerImpl.getUser(order.getDoctorId()).getName() + "专家组");
    }

    public GroupInfo createGroupMore(Order order, OrderSession orderSession, String gname) throws HttpApiException {
        orderSession.setCreateTime(System.currentTimeMillis());
        orderSession.setOrderId(order.getId());
        CreateGroupRequestMessage createGroupParam = new CreateGroupRequestMessage();
        createGroupParam.setType(2); //多人组
        createGroupParam.setGtype("1_3");//
        createGroupParam.setFromUserId(String.valueOf(order.getDoctorId()));
        createGroupParam.setToUserId(orderSession.getToUserIds());
        createGroupParam.setGname(gname);
        createGroupParam.setSendRemind(false);
        Map<String, Object> param = new HashMap<>();
        if (order.getOrderType() == OrderEnum.OrderType.checkIn.getIndex()) {
            List<CheckInVO> checkInvos = checkInMapper.getCheckInByOrderId(order.getId());
            if ((checkInvos != null) && (checkInvos.size() > 0)) {
                CheckInVO checkInvo = checkInvos.get(0);
                param.put("patientName", checkInvo.getUserName());
//				if(checkInvo.getBirthday()!=null && checkInvo.getBirthday()>0) {
//					param.put("birthday", checkInvo.getBirthday());
//				}
                param.put("patientAge", checkInvo.getPatientAge());
                param.put("patientSex", checkInvo.getSex() == null ? null : BusinessUtil.getSexName((int) checkInvo.getSex()));
                param.put("patientArea", checkInvo.getArea());
            }
        } else {

            int diseaseId = (order != null && order.getDiseaseId() != null) ? order.getDiseaseId() : 0;
            Disease disease = diseaseService.findByPk(diseaseId);
            if (disease != null) {
                param.put("patientName", disease.getUserName());
//				if(disease.getBirthday()!=null && disease.getBirthday()>0) {
//					param.put("birthday", disease.getBirthday());
//				}
                param.put("patientAge", disease.getPatientAge());
                param.put("patientSex", disease.getSex() == null ? null : BusinessUtil.getSexName(disease.getSex()));
                param.put("patientArea", disease.getArea());
            }
        }
        createGroupParam.setCreateNew(true);
        createGroupParam.setBizStatus(String.valueOf(order.getOrderStatus()));
        param.put("orderId", order.getId());
        param.put("orderType", order.getOrderType());
        param.put("packType", order.getPackType());
        param.put("price", order.getPrice());
        createGroupParam.setParam(param);
        createGroupParam.setSendRemind(false);
        GroupInfo groupInfo = (GroupInfo) imsgService.createGroup(createGroupParam);
        orderSession.setMsgGroupId(groupInfo.getGid());
        orderSession.setLastModifyTime(System.currentTimeMillis());
        orderSessionService.save(orderSession);
        order.setMsgId(groupInfo.getGid());
        order.setImTitleName(gname);

        //医生端将计划发送给患者后，患者页签没有显示姓名。需要点击到im在返回才会出现姓名
//		GroupStateRequestMessage request = new GroupStateRequestMessage();
//		request.setBizStatus(String.valueOf(OrderStatus.待支付.getIndex()));
//		request.setGid(orderSession.getMsgGroupId());
//		MsgHelper.updateGroupBizState(request);

        return groupInfo;
    }

    /**
     * 轮询统计订单状态数量
     *
     * @return
     */
    @Override
    public Integer countNewOrder(Integer doctorId) {

        return orderMapper.countNewOrder(doctorId);
    }

    @Override
    public void sendOrderNoitfy(String userId, String doctorId, String gid, OrderNoitfyType orderNoitfyType, Map<String, Object> paramMap) {
        this.sendOrderNoitfy(userId, doctorId, gid, orderNoitfyType, paramMap, null);
    }

    /**
     * 订单诊疗过程中发送通知或提醒方法
     * <p>
     * 1、患者提交订单后：患者发消息到im，内容为：“病情描述（姓名、性别、年龄）”；
     * 医生在im中自动回复消息，内容为：“您好，很高兴收到您的提问，我将尽快和你预约时间，请注意查收预约通知或短信。”
     * 2、患者修改病历后：患者发消息到im，内容为：“我修改了病情资料，请您再重新查看。”
     * 3、医生确定预约时间后：系统发消息到im，内容为：“套餐预约时间为2015-09-08 10:00。”
     * 4、医生修改预约时间后：系统发消息到im，内容为：“医生修改了预约时间，更改为2015-09-09 10:00” （暂没处理）
     * 5、医生开始服务后，系统发消息到im，内容为：“医生已开始了本次咨询”
     * 6、医生结束服务后，医生发消息到im，内容为：“我已填写了诊疗记录，本次结束咨询。”
     * 系统发消息到im，内容为：“本次咨询已结束，如需提问请再次购买服务”
     * 7、患者取消订单后，系统消息到im，内容为：“患者已经取消了订单”
     * 8、患者支付成功后，系统发消息到im，内容为：“患者已经成功支付”
     * 9、电话拨打开始后，系统发消息到im，内容为："电话拨打已开始”；
     * 10、电话拨打完成后，系统发消息到im，内容为：“电话拨打已完成”
     * 11、医生点击门诊接单后，医生发消息到im,内容为：“您好，现在可发开始您的咨询。”
     * 12、患者主动放弃咨询，系统发消息到im,内容为：“患者放弃咨询”
     */
    @Override
    public void sendOrderNoitfy(String userId, String doctorId, String gid, OrderNoitfyType orderNoitfyType, Map<String, Object> paramMap, Boolean isByPatient) {
        try {
            String tag = "sendOrderNoitfy";
            if (logger.isInfoEnabled()) {
                logger.info("{}. userId={}, gid={}, isByPatient={}", tag, userId, gid, isByPatient);
            }

            if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(gid)) {
                return;
            }

            if (OrderNoitfyType.neworder == orderNoitfyType) {//// 患者提交订单后
                sendNewNotify(userId, doctorId, gid, paramMap);
            } else if (OrderNoitfyType.editDisesase == orderNoitfyType) {//// 修改病历
                sendEditNotify(userId, gid);
            } else if (OrderNoitfyType.appointTime == orderNoitfyType) {//// 确定预约时间
                sendAppointNotify(gid, paramMap);
            } else if (OrderNoitfyType.prepareService == orderNoitfyType) {//// 医生开始接单（类似叫号）
                businessServiceMsg.sendUserToUserMsg(doctorId, gid, "您好，现在可开始您的咨询。");
            } else if (OrderNoitfyType.abandonService == orderNoitfyType) {//// 放弃咨询
                businessServiceMsg.sendNotifyMsgToAll(gid, "患者放弃咨询");
            } else if (OrderNoitfyType.beginService == orderNoitfyType) {//// 医生开始服务后
                sendBeginNotify(userId, doctorId, gid, paramMap);
            } else if (OrderNoitfyType.endService == orderNoitfyType) {//// 医生结束服务后
                sendEndNotify(userId, doctorId, gid, paramMap, isByPatient);
            } else if (OrderNoitfyType.cancelOrder == orderNoitfyType) {//// 患者取消订单后
                sendCancelNotify(userId, doctorId, gid, paramMap);
            } else if (OrderNoitfyType.payorder == orderNoitfyType) {//// 患者支付成功后
                sendPayNotify(userId, doctorId, gid, paramMap);
            } else if (OrderNoitfyType.beginCall == orderNoitfyType) {//// 电话拨打开始
            } else if (OrderNoitfyType.endCall == orderNoitfyType) {//// 电话拨打结束
            } else if (OrderNoitfyType.refuseorder == orderNoitfyType) {
                sendRefuseNotify(userId, doctorId, gid, paramMap);
            } else if (OrderNoitfyType.changeAppointmentTime == orderNoitfyType) {
                sendChangeAppointmentTimeNotify(userId, doctorId, gid, paramMap);
            }
        } catch (Exception e) {
            LoggerUtils.printExceptionLog("发送通知失败!", null);
        }

    }

    private void sendChangeAppointmentTimeNotify(String userId, String doctorId, String gid, Map<String, Object> mapString) throws HttpApiException {
        Order o = (Order) mapString.get("order");
        OfflineItem newItem = (OfflineItem) mapString.get("newOfflineItem");
        OfflineItem oldItem = (OfflineItem) mapString.get("oldOfflineItem");
        /*"短信内容：***医生您好，助手为
        您修改了***患者的面对面服务，
		预约时间已由06月11日11点30分
		改为06月14日11点30分，
		届时请您准时接诊"*/
        //{0}医生您好，助手为您修改了{1}患者的面对面服务，预约时间已由{2}改为{3}，届时请您准时接诊
        User d = userManagerImpl.getUser(o.getDoctorId());
        User up = userManagerImpl.getUser(o.getUserId());
        Patient p = patientService.findByPk(o.getPatientId());
        long oldAppointTime = oldItem.getStartTime();
        long newAppointTime = newItem.getStartTime();
        String signature = "";
        String oldDateStr = "";
        String newDateStr = "";
        if (ReqUtil.isBDJL()) {
            signature = BaseConstants.BD_SIGN;
        } else {
            signature = BaseConstants.BD_SIGN;
        }
        oldDateStr = DateUtil.formatDate2Str2(new Date(oldAppointTime)) + "-" +
                DateUtil.getMinuteTimeByLong(oldAppointTime + DateUtil.minute30millSeconds);
        newDateStr = DateUtil.formatDate2Str2(new Date(newAppointTime)) + "-" +
                DateUtil.getMinuteTimeByLong(newAppointTime + DateUtil.minute30millSeconds);


        /**
         * 系统推送
         */
        StringBuffer content = new StringBuffer("");
        content.append("医生预约时间为:" + newDateStr);
        businessServiceMsg.sendNotifyMsgToAll(gid, content.toString());

        /**
         * 短信发送
         */
        mobSmsSdk.send(d.getTelephone(), baseDataService.toContent("1033", d.getName(), p.getUserName(), oldDateStr, newDateStr), signature);

        /**
         * "短信内容：***患者您好，
         助手为您修改了***医生的
         面对面服务，预约时间已由
         06月11日11点30分改为
         06月14日11点30分，
         届时请您准时就诊。"
         {0}患者您好，助手为您修改了{1}医生的面对面服务，预约时间已由{2}改为{3}，届时请您准时就诊。
         */
        mobSmsSdk.send(p.getTelephone(), baseDataService.toContent("1032", p.getUserName(), d.getName(), oldDateStr, newDateStr), signature);
    }

    private void sendRefuseNotify(String userId, String doctorId, String gid, Map<String, Object> paramMap) throws HttpApiException {
        businessServiceMsg.sendNotifyMsgToUser(userId, gid, "医生不同意本次预约，订单已取消");
        businessServiceMsg.sendNotifyMsgToUser(doctorId, gid, "您不同意本次预约，订单已取消");
        String patientName = String.valueOf(paramMap.get("patientName"));
        String doctorName = String.valueOf(paramMap.get("doctorName"));
        String telephone = String.valueOf(paramMap.get("telephone"));
        String signature = "";
        if (ReqUtil.isBDJL()) {
            //{0}患者您好，{1}医生当前可能比较忙，拒绝了您的预约，请您重新咨询其他医生。
            signature = BaseConstants.BD_SIGN;
        } else {
            signature = BaseConstants.BD_SIGN;
        }
        mobSmsSdk.send(telephone, baseDataService.toContent("1034", patientName, doctorName), signature);
    }

    private void sendEditNotify(String userId, String gid) throws HttpApiException {
        StringBuffer content = new StringBuffer("");
        content.append("我修改了病情资料，请您再重新查看");
        businessServiceMsg.sendUserToUserMsg(userId, gid, content.toString());
    }

    private void sendAppointNotify(String gid, Map<String, Object> paramMap) throws HttpApiException {
        Order order = (Order) paramMap.get("order");
        if ((order != null) && (order.getOrderType() == OrderType.appointment.getIndex())) {//发送短信
            /*String patientName = String.valueOf(paramMap.get("patientName"));
            String doctorName = String.valueOf(paramMap.get("doctorName"));
			String telephone = String.valueOf(paramMap.get("telephone"));*/
            User pu = userManagerImpl.getUser(order.getUserId());
            User d = userManagerImpl.getUser(order.getDoctorId());
            Patient p = patientService.findByPk(order.getPatientId());
            String signature = "";
            String openURL = "";
            signature = BaseConstants.XG_SIGN;
            openURL = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, UserType.patient.getIndex()));
            OfflineItem item = offlineDao.findOfflineItemByOrderId(order.getId(), order.getDoctorId());
            if ((item.getDataForm() == null) || (item.getDataForm().intValue() == 1)) {
                mobSmsSdk.send(pu.getTelephone(), baseDataService.toContent("1036", p.getUserName(), d.getName(), openURL + "  "), signature);
            }
            businessServiceMsg.sendNotifyMsgToUser(order.getUserId() + "", gid, "医生同意预约，请及时付款");
            businessServiceMsg.sendNotifyMsgToUser(order.getDoctorId() + "", gid, "您同意本次预约");
        } else {
            long appointTime = (long) paramMap.get("appointTime");
            StringBuffer content = new StringBuffer("");
            String dateTimeBegin = DateUtil.formatDate2Str2(new Date(appointTime));
            String dateTimeEnd = DateUtil.formatDate2Str2(new Date(appointTime + (30 * 60 * 1000)));
            content.append("医生预约时间为:" + dateTimeBegin + "-" + dateTimeEnd.split(" ")[1]);
            businessServiceMsg.sendNotifyMsgToAll(gid, content.toString());
        }
    }

    private void sendPayNotify(String userId, String doctorId, String gid, Map<String, Object> paramMap) throws HttpApiException {
        Order order = (Order) paramMap.get("order");

        StringBuffer content = new StringBuffer("");
        StringBuffer title = new StringBuffer("");
        /**
         * 2016年11月3日18:27:55 李明 电子病历改为创建订单时候发送
         */
        if (order.getOrderType() == OrderType.outPatient.getIndex()) {

//			Disease disease = diseaseMapper.selectByPrimaryKey(order.getDiseaseId());
//			Patient patient = patientService.findByPk(disease.getPatientId());

//			StringBuffer content2 = new StringBuffer("");
//			content2.append(disease.getDiseaseInfo());
//			if (patient.getSex() != null) {
//				content2.append("(" + patient.getUserName() + "，" + BusinessUtil.getSexName(Integer.valueOf(patient.getSex())) + "，" + patient.getAgeStr() + ")");
//			} else {
//				content2.append("(" + patient.getUserName() + "，" + patient.getAgeStr() + ")");
//			}
//
//			businessServiceMsg.sendUserToUserMsg(userId, gid, content2.toString());


            sendNotitfyByOne("门诊咨询已成功支付", "门诊为在线咨询，请您耐心等待医生回复，并及时开始咨询，您可以先准备好问题，保持良好的网络环境，希望医生能完美解决您的问题。",
                    order.getUserId() + "", gid);
            sendNotitfyByOne("门诊订单患者已成功支付", "门诊订单患者已成功支付，请根据该患者情况，回复患者。", order.getDoctorId() + "", gid);
        } else if (order.getOrderType() == OrderType.care.getIndex()) {

            Pack pack = packService.getPack(order.getPackId());
            sendNotitfyByOne(pack.getName() + "支付成功",
                    "请等待医生确认，医生根据您的实际情况设置健康关怀计划的起始时间。医生设置完成后，计划立即生效，您只需根据系统提示回答问题即可。",
                    order.getUserId() + "", gid);

            List<OrderDoctor> orderDoctors = orderDoctorService.findOrderDoctors(order.getId());

            // 发短信给患者
            Disease disease = diseaseMapper.selectByPrimaryKey(order.getDiseaseId());
            Patient patient = patientService.findByPk(disease.getPatientId());
            // 尊敬的***用户，“***健康关怀计划”***元，已成功支付。

            User p_user = userManagerImpl.getUser(patient.getUserId());

            String signature = mobSmsSdk.isBDJL(p_user) ? BaseConstants.BD_SIGN : BaseConstants.XG_SIGN;

            String ms = baseDataService.toContent("0080", patient.getUserName(), pack.getName(), (order.getPrice() / 100) + "");
            mobSmsSdk.send(disease.getTelephone(), ms + shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, 1)), signature);

            // 以系统通知医生团队
//			User orderUser = userManagerImpl.getUser(order.getDoctorId());
            for (OrderDoctor orderDoctor : orderDoctors) {
                String title_doc = "健康关怀计划支付成功";
                User users = userManagerImpl.getUser(orderDoctor.getDoctorId());

                String content_doc = "尊敬的{0}医生，患者{1}购买了您的《{2}》健康关怀计划，请及时为患者提供服务";
                content_doc = MessageFormat.format(content_doc, users.getName(), p_user.getName(), pack.getName());

//				String content_doc = "尊敬的{0}医生，{1}发起“{2}健康关怀计划”已购买成功，请及时为患者提供服务";
//				if (order.getDoctorId().intValue() == orderDoctor.getDoctorId().intValue()) {
//					content_doc = MessageFormat.format(content_doc, users.getName(), "您", pack.getName());
//				} else {
//					content_doc = MessageFormat.format(content_doc, users.getName(), orderUser.getName(), pack.getName());
//				}

                sendNotitfy(title_doc, content_doc, orderDoctor.getDoctorId() + "");

                // 每个医生都发
                sendNotitfyByOne("患者已成功支付！", "请您根据该患者的病情，设置健康关怀计划开始时间，以及药品清单，并开始计划。", orderDoctor.getDoctorId() + "", gid);
                // 尊敬的***医生，***用户购买您的“***健康关怀计划”，***元已支付成功。请登录玄关健康APP为患者设置健康关怀计划并发送给患者。
                String appName = null;
                if (mobSmsSdk.isBDJL(users)) {
                    appName = BaseConstants.BD_DOC_APP;
                } else {
                    appName = BaseConstants.XG_DOC_APP;
                }
                ms = baseDataService.toContent("0081", users.getName(), patient.getUserName(), pack.getName(), (order.getPrice() / 100) + "", appName);
                mobSmsSdk.send(users.getTelephone(), ms + shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, 3)));
            }
        } else if (order.getOrderType() == OrderEnum.OrderType.consultation.getIndex()) {
            
            /*
             * 会诊订单患者支付成功自动开始服务
             */
            orderSessionService.beginService(order.getId());

            /**
             * 发送短信
             */
           
            
            String packId = order.getConsultationPackId();
            GroupConsultationPack consultPack = consultationPackDao.getById(packId);
            if(consultPack == null){
            	throw new ServiceException("未找到会诊包套参" + packId);
            }
            int patientId = order.getPatientId();
            Patient patient = patientMapper.selectByPrimaryKey(patientId);
            if (patient == null) {
                throw new ServiceException(10002, "错误的患者ID [" + patientId + "]");
            }
            
			/**
			 * 主会诊医生
			 * 尊敬的{0}医生，{1}患者已购买您的会诊咨询服务，您可与患者、其他医生进行视频会诊。了解病情后请及时填写会诊报告。
			 */
            int udocId = consultPack.getConsultationDoctor();//主会诊医生
            User uUser = userManagerImpl.getUser(udocId);
            String msg = baseDataService.toContent("0074", uUser.getName(), patient.getUserName());
            if (mobSmsSdk.isBDJL(uUser)) {
                mobSmsSdk.send(uUser.getTelephone(), msg, BaseConstants.BD_SIGN);
            } else {
                mobSmsSdk.send(uUser.getTelephone(), msg, BaseConstants.XG_SIGN);
            }
			/**
			 * 发起医生
			 * 尊敬的{0}医生，{1}患者已购买您推荐的专家会诊服务，您可与患者、其他医生进行视频会诊。了解病情后请及时填写会诊建议，以便会诊专家参考。
			*/
            int cdocId = order.getDoctorId();//发起会诊医生
            User cUser = userManagerImpl.getUser(cdocId);
            msg = baseDataService.toContent("0075", cUser.getName(), patient.getUserName());
            if (mobSmsSdk.isBDJL(cUser)) {
                mobSmsSdk.send(cUser.getTelephone(), msg, BaseConstants.BD_SIGN);
            } else {
                mobSmsSdk.send(cUser.getTelephone(), msg, BaseConstants.XG_SIGN);
            }
            
            /**
			 * 参与医生
			 * 尊敬的{小医生名}医生，{患者名}患者已购买您所在会诊包内的咨询服务，您可与患者、其他医生进行视频会诊。了解病情后请及时填写会诊建议，以便会诊专家参考。
			*/
            Set<Integer> docIds = consultPack.getDoctorIds();//参与医生
            for(int docId : docIds){
            	if(cdocId == docId){
            		continue;
            	}
            	User doc = userManagerImpl.getUser(docId);
            	msg = baseDataService.toContent("0089", doc.getName(), patient.getUserName());
            	if (mobSmsSdk.isBDJL(doc)) {
                    mobSmsSdk.send(doc.getTelephone(), msg, BaseConstants.BD_SIGN);
                } else {
                    mobSmsSdk.send(doc.getTelephone(), msg, BaseConstants.XG_SIGN);
                }
            }
            /**
             * 发送通知到会话组
             */
            title.append("会诊服务已支付成功");
            content.append("会诊咨询事项：\n");
            content.append("	1、请注意保持通话环境安静\n");
            content.append("	2、保持信号畅通\n");
            content.append("	3、提前做好准备");
            List<ImgTextMsg> msgs = new ArrayList<ImgTextMsg>();
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            imgTextMsg.setTitle(title.toString());
            imgTextMsg.setContent(content.toString());
            imgTextMsg.setTime(System.currentTimeMillis());
            imgTextMsg.setStyle(7);
            msgs.add(imgTextMsg);
            businessServiceMsg.sendTextMsgToGid(SysUserEnum.SYS_001.getUserId(), gid, msgs, null);
        } else if (order.getOrderType() == OrderEnum.OrderType.appointment.getIndex()) {
            Integer dId = order.getDoctorId();
            Integer patientId = order.getPatientId();
            OrderSession os = orderSessionService.findOneByOrderId(order.getId());
            User d = userManagerImpl.getUser(dId);
            Patient p = patientMapper.selectByPrimaryKey(patientId);


            title.append("预约名医已支付成功");
            content.append("预约时间前2小时我们会以短信提示。\n");
            content.append("预约咨询事项：\n");
            content.append("	1.请提前前往预约指定医院 \n");
            content.append("	2.请提前准备好病例资料");
            List<ImgTextMsg> msgs = new ArrayList<ImgTextMsg>();
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            imgTextMsg.setTitle(title.toString());
            imgTextMsg.setContent(content.toString());
            imgTextMsg.setTime(System.currentTimeMillis());
            imgTextMsg.setStyle(7);
            msgs.add(imgTextMsg);
            businessServiceMsg.sendTextMsgToGid(SysUserEnum.SYS_001.getUserId(), gid, msgs, null);
            String dateStr = "";
            String doctorName = "";
            String patientName = "";
            if (os != null) {
                long appointtime = os.getAppointTime();
                dateStr = DateUtil.formatDate2Str2(new Date(appointtime)) + "-" +
                        DateUtil.getMinuteTimeByLong(appointtime + DateUtil.minute30millSeconds);
            }
            if (p != null) {
                patientName = p.getUserName();
            }
            if (d != null) {
                doctorName = d.getName();
            }
            //String msg = "尊敬的"+doctorName+"医生，患者"+patientName+"购买了您的名医面对面服务，预约时间"+dataStr+"。预约时间有问题请您及时与患者协商修改" ;
            //***医生您好，***患者已支付您您2016-06-05于南山人民医院线下咨询，请您准时就诊。
            //{0}医生您好，{1}患者已支付您您{2}于{3}线下咨询，请您准时就诊。
            HospitalVO hospitalPo = baseDataDao.getHospital(order.getHospitalId());
            String hospitalName = "";
            if (hospitalPo != null) {
                hospitalName = hospitalPo.getName();
            }
            final String msg = baseDataService.toContent("1005", doctorName, patientName, dateStr, hospitalName);
            mobSmsSdk.send(d.getTelephone(), msg);
            //mobSmsSdk.send(d.getTelephone(), msg);
            Disease disease = diseaseMapper.selectByPrimaryKey(order.getDiseaseId());
            HospitalVO hospital = baseDataDao.getHospital(order.getHospitalId());
            //String msg2 = patientName+"您好，您购买了"+doctorName+"医生的名医面对面服务，预约时间："+dataStr+"，预约地址："+hospital.getName()+"、"+d.getDoctor().getDepartments()+"
            //              就诊，请提前半小时到达，凭证身份证号或手机号现场处理。" ;
			/*
			Patient patient = patientService.findByPk(disease.getPatientId());
			User p_user = userManagerImpl.getUser(patient.getUserId());*/
            String signature = mobSmsSdk.isBDJL(d) ? BaseConstants.BD_SIGN : BaseConstants.XG_SIGN;

            final String msg2 = baseDataService.toContent("1006", patientName, doctorName, dateStr, hospital.getName(), d.getDoctor().getDepartments());
            mobSmsSdk.send(disease.getTelephone(), msg2, signature);
            //mobSmsSdk.send(disease.getTelephone(), msg2);

        } else if (order.getOrderType() == OrderEnum.OrderType.feeBill.getIndex()) {
            Integer patientId = order.getPatientId();
            Integer dId = order.getDoctorId();
            Patient p = patientMapper.selectByPrimaryKey(patientId);
            User d = userManagerImpl.getUser(dId);
            String tel = "";
            if (d != null) {
                tel = d.getTelephone();
            }
            String pName = p == null ? "" : p.getUserName();
            title.append("付费单已支付成功");
            content.append(pName + "已成功支付本付费单，请及时知悉并安排其他事务。");
            List<ImgTextMsg> msgs = new ArrayList<ImgTextMsg>();
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            imgTextMsg.setTitle(title.toString());
            imgTextMsg.setContent(content.toString());
            imgTextMsg.setTime(System.currentTimeMillis());
            imgTextMsg.setStyle(7);
            msgs.add(imgTextMsg);
            businessServiceMsg.sendTextMsgToGid(SysUserEnum.SYS_001.getUserId(), gid, msgs, null);
            final String msg = baseDataService.toContent("1007", pName);
            mobSmsSdk.send(tel, msg);
            //mobSmsSdk.send(tel, pName+"已成功支付本付费单，请及时知悉并安排其他事务。");
        } else {
            String messageStr = "";
            User userDoc = userManagerImpl.getUser(order.getDoctorId());
//			User userpen = userManagerImpl.getUser(order.getUserId());
            Patient patient = patientService.findByPk(order.getPatientId());
            String dateTime = "";
            String endTime = "";
            OrderSession orderSession = orderSessionService.findOneByMsgGroupId(gid);
            if ((orderSession != null) && (orderSession.getAppointTime() != null)) {
                long appointTime = orderSession.getAppointTime();
                dateTime = DateUtil.getMinuteDateByLong(appointTime);
                endTime = DateUtil.getMinuteTimeByLong(appointTime + (30 * 60 * 1000));
            }
            if (order.getPackType() == PackEnum.PackType.message.getIndex()) {

                int total = 0;
                OrderSessionContainer osc = orderSessionContainerDao.findByOrderId(order.getId());
                if (osc != null) {
                    total = osc.getTotalReplyCount();
                } else {
                    total = orderSession.getTotalReplyCount();
                }
                title.append("图文套餐已支付成功");
                content.append("本次咨询含医生" + total + "次回复。");
                content.append("为节省沟通次数，提问时请尽量一次性描述病情与问题。\n");
                messageStr = baseDataService.toContent("1008",
                        userDoc.getName(), patient.getUserName(), PackType.message.getTitle(), shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, UserType.doctor.getIndex())));


            } else if (order.getPackType() == PackEnum.PackType.phone.getIndex()) {
                title.append("电话套餐已支付成功");
                content.append("电话咨询是以平台电话（座机）拨打，请您不要屏蔽此类电话。为了确保通话质量，请您届时务必保证通话环境安静和信号通畅。\n");
                content.append("有需要请点击右上角“联系助手”处理\n");
                String expectAppointment = baseDataDao.getExpectAppointmentsByIds(order.getExpectAppointmentIds());
                if (StringUtils.isNotBlank(expectAppointment)) {
                    expectAppointment = expectAppointment.substring(1);
                }
                long fen = order.getPrice();
                String price = (fen % 100) == 0 ? (fen / 100) + "" : (fen / 100) + "." + (fen % 100);
                String shortUrl = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", orderSession.getMsgGroupId(), UserType.doctor.getIndex()));
                messageStr = baseDataService.toContent("0072", userDoc.getName(),
                        patient.getUserName(),
                        expectAppointment,
                        price,
// 蹇晓枫修改需求去掉订单编号 2016-10-27 15:50:44                     order.getOrderNo()+"" ,
                        shortUrl);

            } else if (order.getPackType() == PackType.integral.getIndex()) {
                int total;
                OrderSessionContainer osc = orderSessionContainerDao.findByOrderId(order.getId());
                if (osc != null) {
                    total = osc.getTotalReplyCount();
                } else {
                    total = orderSession.getTotalReplyCount();
                }
                title.append("积分问诊已支付成功");
                content.append("本次积分咨询含医生" + total + "次回复。");
                content.append("为节省沟通次数，提问时请尽量一次性描述病情与问题。\n");
                messageStr = baseDataService.toContent("1008",
                        userDoc.getName(), patient.getUserName(), PackType.integral.getTitle(), shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, UserType.doctor.getIndex())));
            }
            List<ImgTextMsg> msgs = new ArrayList<>();
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            imgTextMsg.setTitle(title.toString());
            imgTextMsg.setContent(content.toString());
            imgTextMsg.setTime(System.currentTimeMillis());
            imgTextMsg.setStyle(7);
            msgs.add(imgTextMsg);
            businessServiceMsg.sendTextMsgToGid(SysUserEnum.SYS_001.getUserId(), gid, msgs, null);

            if ((order != null) && (order.getPackType() == PackType.message.getIndex())) {
//				businessServiceMsg.sendUserToUserMsg(doctorId, gid, "您好，很高兴收到您的提问，我将尽快开始服务，请注意查收通知或短信");
                String msg = "请您将您的病况、病史、过敏史、此前是否就医（如有，请说明医生对您的诊断情况）详细描述下发给我哦，这将有助于我给您诊治病情。";
                businessServiceMsg.sendUserToUserMsg(doctorId, gid, msg);
            }

            mobSmsSdk.send(userDoc.getTelephone(), messageStr);
        }

        messageGroupService.updateGroupBizState(gid, MessageGroupEnum.PAY_ORDER.getIndex());
     //   updateAssistantGroupState(order, "3");  统一通过MessageGroupServiceImpl 发送
    }

    private void sendCancelNotify(String userId, String doctorId, String gid, Map<String, Object> paramMap) throws HttpApiException {
        String cancelType = (String) paramMap.get("cancelType");
        Integer orderType = (Integer) paramMap.get("orderType");
        Integer userType = ReqUtil.instance.getUser().getUserType();
        Order order = (Order) paramMap.get("order");
        if ((order != null) &&
                (order.getPackType() != null) &&
                (order.getPackType() == PackType.message.getIndex() || order.getPackType() == PackType.integral.getIndex()) &&
                (order.getPreOrderStatus() == OrderStatus.已完成.getIndex()) &&
                (order.getPrice() != 0)) {
            User user = userManagerImpl.getUser(order.getUserId());
            String docName = userManagerImpl.getUser(order.getDoctorId()).getName();
            final String msg = baseDataService.toContent("1004", user.getName(), docName);
            mobSmsSdk.send(user.getTelephone(), msg);
            //mobSmsSdk.send(user.getTelephone(), MessageFormat.format(SMSUtil.SMS_48HOURS, user.getName(), docName));
        } else if ((cancelType != null) && cancelType.equals("cancelAfter12")) {
            businessServiceMsg.sendNotifyMsgToAll(gid, "门诊订单晚上12点后，系统自动取消");
        } else if ((cancelType != null) && cancelType.equals("cancelAfter3")) {
            businessServiceMsg.sendNotifyMsgToAll(gid, "门诊订单3分钟未开始，系统自动取消");
        } else if ((cancelType != null) && (cancelType.equals("noAppointTime") || cancelType.equals("noPay")
                || cancelType.equals("noStartService"))) {
            String contentToUser = (String) paramMap.get("contentToUser");
            String contentToDoctor = (String) paramMap.get("contentToDoctor");
            businessServiceMsg.sendNotifyMsgToUser(userId, gid, contentToUser);
            businessServiceMsg.sendNotifyMsgToUser(doctorId, gid, contentToDoctor);
        } else {
            if (orderType == OrderType.consultation.getIndex()) {
                Object toPatientMessage = paramMap.get("toPatientMessage");
                Object toSpecialistMessage = paramMap.get("toSpecialistMessage");
                Object toAssistantMessage = paramMap.get("toAssistantMessage");
                String specialDoctorId = null;
                String assistantDoctorId = null;
                if (StringUtil.isNotBlank(doctorId)) {
                    if (doctorId.contains(",")) {
                        assistantDoctorId = doctorId.split(",")[0];
                        specialDoctorId = doctorId.split(",")[1];
                    } else {
                        assistantDoctorId = doctorId;
                    }
                }
                if (toPatientMessage != null) {
                    businessServiceMsg.sendNotifyMsgToUser(userId, gid, String.valueOf(toPatientMessage));
                }
                if ((toSpecialistMessage != null) && (specialDoctorId != null)) {
                    businessServiceMsg.sendNotifyMsgToUser(specialDoctorId, gid, String.valueOf(toSpecialistMessage));
                }
                if ((toAssistantMessage != null) && (assistantDoctorId != null)) {
                    businessServiceMsg.sendNotifyMsgToUser(assistantDoctorId, gid, String.valueOf(toAssistantMessage));
                }
            } else if (orderType == OrderType.feeBill.getIndex()) {
                businessServiceMsg.sendNotifyMsgToAll(gid, "患者已取消本付费单");
            } else if ((orderType == OrderType.appointment.getIndex()) && (userType == UserType.DocGuide.getIndex())) {
                businessServiceMsg.sendNotifyMsgToAll(gid, "医生助手取消了订单");
            } else if (userType == UserType.patient.getIndex()) {
                businessServiceMsg.sendNotifyMsgToAll(gid, "患者已经取消了订单");
            }
        }
    }

    private void sendEndNotify(String userId, String doctorId, String gid, Map<String, Object> paramMap, Boolean isByPatient) throws HttpApiException {
        Integer orderType = (Integer) paramMap.get("orderType");
        Integer orderId = (Integer) paramMap.get("orderId");
        Order order = getOne(orderId);
        if (orderType == OrderType.care.getIndex()) {
            String packName = (String) paramMap.get("packName");
            User user = userManagerImpl.getUser(order.getDoctorId());
            User userPatient = userManagerImpl.getUser(order.getUserId());
            Disease disease = diseaseMapper.selectByPrimaryKey(order.getDiseaseId());
            Patient patient = patientService.findByPk(disease.getPatientId());
            String msgs = baseDataService.toContent("0013", patient.getUserName(), user.getName(), packName);
            // 在短信后添加跳APP链接
            msgs += shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, 1));
            //根据patient里的userId查询该患者是属于哪个平台
            User p_user = userManagerImpl.getUser(patient.getUserId());
            String signature = null;
            if (mobSmsSdk.isBDJL(p_user)) {
                signature = BaseConstants.BD_SIGN;
            } else {
                signature = BaseConstants.XG_SIGN;
            }
            if (userPatient.getTelephone().equals(disease.getTelephone())) {
                mobSmsSdk.send(disease.getTelephone(), msgs, signature);
            } else {
                mobSmsSdk.send(disease.getTelephone(), msgs, signature);
                mobSmsSdk.send(patient.getTelephone(), msgs, signature);
            }


            // 发IM通知
            if (StringUtil.isNotBlank(gid)) {
                if (null != isByPatient && isByPatient) {
                    businessServiceMsg.sendNotifytoMyMsg(userId, gid, "您结束了“" + packName + "”健康关怀计划");
                } else {
                    businessServiceMsg.sendNotifytoMyMsg(userId, gid, "“" + packName + "”健康关怀计划已经完成，感谢您的使用，祝您早日康复。");
                }

            }

//			OrderDoctorExample orderDoctorExample = new OrderDoctorExample();
//			orderDoctorExample.createCriteria().andOrderIdEqualTo(orderId);
//			List<OrderDoctor> orderDoctors = orderDoctorMapper.selectByExample(orderDoctorExample);
            List<OrderDoctor> orderDoctors = orderDoctorService.findOrderDoctors(orderId);
//			String docAppLink = PackConstants.greneartenURL("1", gid, 3);
            for (OrderDoctor orderDoctor : orderDoctors) {
                // 发送短信给医生
//				User users = userManagerImpl.getUser(orderDoctor.getDoctorId());
//				final String msg = baseDataService.toContent("0014", users.getName(), patient.getUserName(), packName);
//				mobSmsSdk.send(users.getTelephone(), msg + docAppLink);
                if (null != isByPatient && isByPatient) {
                    businessServiceMsg.sendNotifytoMyMsg(orderDoctor.getDoctorId() + "", gid, "患者结束了“" + packName + "”健康关怀计划");
                } else {
                    businessServiceMsg.sendNotifytoMyMsg(orderDoctor.getDoctorId() + "", gid, "“" + packName + "”健康关怀计划已经完成，感谢您提供的贴心服务。");
                }

            }

        } else if (orderType == OrderType.followUp.getIndex()) {

            // 尊敬的**用户，***（随访计划名称）已完成，感谢您的使用，祝您早日康复。
            String packName = (String) paramMap.get("packName");
            Integer autoClose = (Integer) paramMap.get("auto");
            // 发送短信提醒
            User userDoc = userManagerImpl.getUser(order.getDoctorId());
            User userPatient = userManagerImpl.getUser(order.getUserId());
            Disease disease = diseaseMapper.selectByPrimaryKey(order.getDiseaseId());
            Patient patient = patientService.findByPk(disease.getPatientId());

            //User p_user = userManagerImpl.getUser(patient.getUserId());//根据患者查询p
            String signature = mobSmsSdk.isBDJL(userDoc) ? BaseConstants.BD_SIGN : BaseConstants.XG_SIGN;

            if (autoClose == OrderEnum.OrderSessionOpenStatus.automatic.getIndex()) {
                // 发给患者：尊敬的**用户，***（随访计划名称）已完成，感谢您的使用，祝您早日康复。
                // 发给医生：尊敬的**医生，***患者的***（随访计划名称）已完成，感谢您提供的贴心服务。
                String contentPat = baseDataService.toContent("0077", patient.getUserName(), packName);
                String contentDoc = baseDataService.toContent("0078", userDoc.getName(), patient.getUserName(), packName);
                contentPat += shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, 1));
                contentDoc += shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, 3));
                mobSmsSdk.send(userDoc.getTelephone(), contentDoc);
                if (userPatient.getTelephone().equals(disease.getTelephone())) {
                    mobSmsSdk.send(userPatient.getTelephone(), contentPat);
                } else {
                    mobSmsSdk.send(userPatient.getTelephone(), contentPat);
                    mobSmsSdk.send(disease.getTelephone(), contentPat, signature);
                }

            } else {
//				String contentPat = "尊敬的{0}用户，{1}医生结束了{2}，感谢您的使用，祝您早日康复。";
                String contentPat = baseDataService.toContent("0079", patient.getUserName(), userDoc.getName(), packName);
                contentPat += shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, 1));
                if (userPatient.getTelephone().equals(disease.getTelephone())) {
                    mobSmsSdk.send(userPatient.getTelephone(), contentPat);
                } else {
                    mobSmsSdk.send(userPatient.getTelephone(), contentPat);
                    mobSmsSdk.send(disease.getTelephone(), contentPat, signature);
                }

            }
            businessServiceMsg.sendNotifyMsgToAll(gid, packName + "已经结束，祝您早日康复。");
        } else if (orderType == OrderType.appointment.getIndex()) {
            businessServiceMsg.sendNotifyMsgToAll(gid, "本次咨询已结束");
        } else {
            Patient p = patientService.findByPk(order.getPatientId());
            if (order.getPackType() == PackType.message.getIndex()) {
                if (evaluationDao.getByOrderId(order.getId()) == null) {
                    String docName = userManagerImpl.getUser(Integer.valueOf(doctorId)).getName();
                    final String msg = baseDataService.toContent("1003", p.getUserName(), docName, PackType.message.getTitle());
                    mobSmsSdk.send(p.getTelephone(), msg);
                }
            } else if (order.getPackType() == PackType.phone.getIndex()) {
                String docName = userManagerImpl.getUser(Integer.valueOf(doctorId)).getName();
                String openURL = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, UserType.patient.getIndex()));
                final String msg = baseDataService.toContent("1050", p.getUserName(), docName, openURL);
                mobSmsSdk.send(p.getTelephone(), msg);
            } else if (order.getPackType() == PackType.integral.getIndex()) {
                if (evaluationDao.getByOrderId(order.getId()) == null) {
                    String docName = userManagerImpl.getUser(Integer.valueOf(doctorId)).getName();
                    String msg = baseDataService.toContent("1003", p.getUserName(), docName, PackType.integral.getTitle());
                    mobSmsSdk.send(p.getTelephone(), msg);
                }
            } else if (order.getPackType() == PackType.consultation.getIndex()) {
                String content = paramMap.get("msg").toString();
                businessServiceMsg.sendNotifyMsgToAll(gid, content);
            } else {
                businessServiceMsg.sendUserToUserMsg(doctorId, gid, "您的服务已结束");
            }
            /**激活会话**/
//            orderSessionService.messageReplyCountChangeEvent(gid, order.getId());

            StringBuffer content = new StringBuffer("");
            content.append("本次咨询已结束，如需提问请再次购买服务。医生咨询内容仅作为对您病情的建议、不作为实际治疗的凭证。如有需要请您进一步去医院咨询确诊。");
            businessServiceMsg.sendNotifyMsgToUser(userId, gid, content.toString());
        }


//        updateAssistantGroupState(order, "17");  改由MessageGroupServiceImpl统一发送
    }

    private void updateAssistantGroupState(Order order, String bizStatus) {
        /**
         * 更新医助 、 助患 的会话状态
         */
        OrderSession os = orderSessionService.findOneByOrderId(order.getId());
        if ((order.getAssistantId() != null) &&
                StringUtils.isNotBlank(os.getAssistantDoctorGroupId()) &&
                StringUtils.isNotBlank(os.getAssistantPatientGroupId())) {
            GroupStateRequestMessage request = new GroupStateRequestMessage();
            request.setBizStatus(bizStatus);
            //更新订单助手医生会话状态
            request.setGid(os.getAssistantDoctorGroupId());
            MsgHelper.updateGroupBizState(request);
            //更新订单助手患者会话状态
            request.setGid(os.getAssistantPatientGroupId());
            MsgHelper.updateGroupBizState(request);
        }
    }

    private void sendBeginNotify(String userId, String doctorId, String gid, Map<String, Object> paramMap) throws HttpApiException {
        Integer orderType = (Integer) paramMap.get("orderType");

        StringBuffer content = new StringBuffer("");
        if (orderType == OrderType.outPatient.getIndex()) {
            content.append("患者已开始了本次咨询");
            businessServiceMsg.sendNotifyMsgToAll(gid, content.toString());
        } else if ((orderType == OrderType.care.getIndex()) || (orderType == OrderType.followUp.getIndex())) {
        } else if (orderType.intValue() == OrderType.consultation.getIndex()) {
            String dId[] = doctorId.split(",");
            Map<String, Object> msgReplaceMap = null;
            String remindContent = "您开始了本次咨询";
            if ((dId != null) && (dId.length > 1)) {
                String udid = dId[0];
                String cdid = dId[1];
                msgReplaceMap = new HashMap<>();
                msgReplaceMap.put(udid, "我已开始本次咨询");
                msgReplaceMap.put(cdid, "助手已开始本次咨询");
                msgReplaceMap.put(userId, "医生已开始了本次咨询");
            }
            GuideMsgHelper.getInstance().sendRemindMsg(gid, remindContent, false, msgReplaceMap);
        } else if (orderType == OrderType.order.getIndex()) {
        } else if (orderType == OrderType.integral.getIndex()) {
        } else {

            businessServiceMsg.sendNotifyMsgToAll(gid, "医生已开始了本次咨询");
            //蹇晓枫 修改需求 去掉该短信 2016-10-27 15:25:17
			/*Order order = (Order) paramMap.get("order");
			if ((order != null) && (order.getPackType() == PackType.message.getIndex())) {
				User user = userManagerImpl.getUser(Integer.valueOf(userId));
				String docName = userManagerImpl.getUser(Integer.valueOf(doctorId)).getName();

				String[] params=new String[]{user.getName(), docName};
				final String smsContent = baseDataService.toContent("1002", params);
				mobSmsSdk.send(user.getTelephone(), smsContent);
				//mobSmsSdk.send(user.getTelephone(), MessageFormat.format(SMSUtil.SMS_BEGIN_SERVICE, user.getName(), docName));
			}*/
        }
    }

    private void sendNewNotify(String userId, String doctorId, String gid, Map<String, Object> paramMap) throws HttpApiException {
        Patient patient = (Patient) paramMap.get("patient");
        String diseaseDesc = (String) paramMap.get("diseaseDesc");
        Integer orderType = (Integer) paramMap.get("orderType");
        //电子病历id
        String illCaseInfoId = (String) paramMap.get("illCaseInfoId");
        //新版电子病历id
        String illHistoryInfoId = (String) paramMap.get("illHistoryInfoId");
        //if(转诊订单) 转诊医生
        String transferDoctorName = (String) paramMap.get("transferDoctorName");
        Order o = (Order) paramMap.get("order");

        Long price = (Long) paramMap.get("price");
        String tag = "sendNewNotify";
        if (logger.isInfoEnabled()) {
            logger.info("{}. diseaseDesc={}, orderType={}, illCaseInfoId={}, transferDoctorName={}, price={}",
                    tag, diseaseDesc, orderType, illCaseInfoId, transferDoctorName, price);
        }

        String diseaseInfo = "";
        String username = patient.getUserName();
        Short sex = patient.getSex();
        String ageStr = patient.getAgeStr();
        String sexValue = sex == null ? "" : sex + "";
        String sexStr = "";
        if (sex == null) {
            diseaseInfo = StringUtil.isNullOrEmpty(ageStr) ? "(" + username + ")" : "(" + username + "，" + ageStr + ")";
        } else {
            sexStr = BusinessUtil.getSexName(Integer.valueOf(patient.getSex()));
            diseaseInfo = StringUtil.isNullOrEmpty(ageStr) ? "(" + username + "，" + sexStr + ")" : "(" + username + "，" + sexStr + "，" + ageStr + ")";

        }
        /**
         * 2016年11月3日16:46:22  李明 门诊订单价格不为0的电子病历修改
         */
        if (orderType == OrderType.outPatient.getIndex()) {
            if (price.longValue() != 0) {
                businessServiceMsg.sendNotifyMsgToUser(userId, gid, "门诊订单已提交，请尽快付款。");
            }
            //if (price.longValue() == 0) {
            if (StringUtils.isBlank(illCaseInfoId)) {
                StringBuffer content = new StringBuffer("");
                content.append(diseaseDesc);
                content.append(diseaseInfo);
                businessServiceMsg.sendUserToUserMsg(userId, gid, content.toString());
            } else {
                sendIllCaseCardInfo(userId, illCaseInfoId, gid, diseaseDesc, username, ageStr, sexValue);
                if (StringUtils.isNotBlank(transferDoctorName)) {
                    businessServiceMsg.sendNotifyMsgToUser(doctorId, gid, "该患者由" + transferDoctorName + "医生转诊推荐");
                    businessServiceMsg.sendNotifyMsgToUser(userId, gid, "您由" + transferDoctorName + "医生转诊推荐给该医生");
                }
            }
//			}else{
//
//			}

        } else if (orderType == OrderType.care.getIndex()) {
            IllHistoryRecord ihr = illHistoryInfoDao.findCareByOrderId(o.getId());
            //OrderSessionContainer osc = orderSessionContainerDao.findByMsgGroupIdAndType(gid,OrderSessionCategory.care.getIndex());
            if (ihr == null) {
                sendIllCaseCardInfo(userId, illCaseInfoId, gid, diseaseDesc, username, ageStr, sexValue);
                if (StringUtils.isNotBlank(transferDoctorName)) {
                    businessServiceMsg.sendNotifyMsgToUser(doctorId, gid, "该患者由" + transferDoctorName + "医生转诊推荐");
                    businessServiceMsg.sendNotifyMsgToUser(userId, gid, "您由" + transferDoctorName + "医生转诊推荐给该医生");
                }
            }
            if ((price != null) && (price.longValue() != 0)) {
                //businessServiceMsg.sendUserToUserMsg(doctorId, gid, "您好，很高兴收到您的提问，请及时支付。付款后我将尽快开通健康关怀计划服务。");
            }
        } else if (orderType == OrderType.followUp.getIndex()) {
            if (StringUtils.isBlank(illCaseInfoId)) {
                StringBuffer content = new StringBuffer("");
                content.append(diseaseDesc);
                content.append(diseaseInfo);
                businessServiceMsg.sendUserToUserMsg(userId, gid, content.toString());
            } else {
                sendIllCaseCardInfo(userId, illCaseInfoId, gid, diseaseDesc, username, ageStr, sexValue);
                if (StringUtils.isNotBlank(transferDoctorName)) {
                    businessServiceMsg.sendNotifyMsgToUser(doctorId, gid, "该患者由" + transferDoctorName + "医生转诊推荐");
                    businessServiceMsg.sendNotifyMsgToUser(userId, gid, "您由" + transferDoctorName + "医生转诊推荐给该医生");
                }
            }
            businessServiceMsg.sendUserToUserMsg(doctorId, gid, "您好，病情资料已收到，我将尽快开通随访计划服务。");
        } else if (orderType == OrderType.consultation.getIndex()) {
            String illCaseId = StringUtils.isNotBlank(illHistoryInfoId) ? illHistoryInfoId : illCaseInfoId;
            sendIllCaseCardInfo(userId, illCaseId, gid, diseaseDesc, username, ageStr, sexValue);
            businessServiceMsg.sendUserToUserMsg(doctorId, gid, "您好，很高兴收到您的会诊请求，我将尽快查看患者病历和你预约时间，请注意查收预约通知或短信。");
        } else if (orderType == OrderType.appointment.getIndex()) {
            if (StringUtils.isBlank(illCaseInfoId)) {
                StringBuffer content = new StringBuffer("");
                content.append(diseaseDesc);
                content.append(diseaseInfo);
                businessServiceMsg.sendUserToUserMsg(userId, gid, content.toString());
            } else {
                sendIllCaseCardInfo(userId, illCaseInfoId, gid, diseaseDesc, username, ageStr, sexValue);
            }
            OrderSession os = orderSessionService.findOneByOrderId(o.getId());
            User d = userManagerImpl.getUser(o.getDoctorId());
            String patientName = patient.getUserName();
            String doctorName = d.getName();
            String dateString = "";
            String hospitalName = "";
            HospitalVO hvo = baseDataDao.getHospital(o.getHospitalId());
            if (hvo != null) {
                hospitalName = hvo.getName();
            }
            Long appointtime = os.getAppointTime();
            dateString = DateUtil.formatDate2Str2(new Date(appointtime)) + "-" +
                    DateUtil.getMinuteTimeByLong(appointtime + DateUtil.minute30millSeconds);
            String signature = "";
            String openURL = "";
            signature = BaseConstants.XG_SIGN;
            openURL = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", gid, UserType.doctor.getIndex()));
            if (ReqUtil.instance.getUser().getUserType().intValue() == UserType.patient.getIndex()) {
                mobSmsSdk.send(d.getTelephone(), baseDataService.toContent("1035", doctorName, patientName, dateString, hospitalName, openURL + "  "), signature);
            }
        } else {
            Order order = (Order) paramMap.get("order");
            if (StringUtils.isBlank(illCaseInfoId)) {
                StringBuffer content = new StringBuffer("");
                content.append(diseaseDesc);
                content.append(diseaseInfo);
                businessServiceMsg.sendUserToUserMsg(userId, gid, content.toString());
            } else {
                sendIllCaseCardInfo(userId, illCaseInfoId, gid, diseaseDesc, username, ageStr, sexValue);
                if (StringUtils.isNotBlank(transferDoctorName)) {
                    if ((order != null) && (order.getPackType() == PackType.message.getIndex())) {
                    } else {
                        businessServiceMsg.sendNotifyMsgToUser(doctorId, gid, "该患者由" + transferDoctorName + "医生转诊推荐");
                    }
                    businessServiceMsg.sendNotifyMsgToUser(userId, gid, "您由" + transferDoctorName + "医生转诊推荐给该医生");
                }
            }
            if ((order != null) && (order.getPackType() == PackType.message.getIndex())) {//图文咨询流程改进，省略待预约状态
//				businessServiceMsg.sendUserToUserMsg(doctorId, gid, "您好，很高兴收到您的提问，我将尽快开始服务，请注意查收通知或短信。");
            } else {
                //businessServiceMsg.sendUserToUserMsg(doctorId, gid, "您好，很高兴收到您的提问，我将尽快和你预约时间，请注意查收预约通知或短信。");
            }
        }
    }


    //发送会话通知只显示一端
    private void sendNotitfyByOne(String title, String content, String toUserId, String gid) throws HttpApiException {

        List<ImgTextMsg> msgs = new ArrayList<ImgTextMsg>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle(title.toString());
        imgTextMsg.setContent(content.toString());
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setStyle(7);
        msgs.add(imgTextMsg);
        businessServiceMsg.sendTextMsgToGidOnToUserId(toUserId, gid, msgs, null);

    }

    //发送系统通知
    @Override
    public void sendNotitfy(String title, String content, String userId) throws HttpApiException {
        List<ImgTextMsg> msgs = new ArrayList<ImgTextMsg>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle(title.toString());
        imgTextMsg.setContent(content.toString());
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setStyle(7);
        msgs.add(imgTextMsg);
        businessServiceMsg.sendTextMsg(userId, SysGroupEnum.TODO_NOTIFY, msgs, null);
    }

    @Override
    public OrderDetailVO findOrderDisease(Integer orderId) throws HttpApiException {

        if (orderId == null) {
            throw new ServiceException("订单ID不能为空!");
        }

        OrderDetailVO orderDisease = orderMapper.findOrderDisease(orderId);

        if ((orderDisease != null) && (orderDisease.getOrderVo() != null)) {
            OrderVO orderVo = orderDisease.getOrderVo();

            orderDisease.setImgStringPath(imageDataService.findImgData(ImageDataEnum.dessImage.getIndex(), orderVo.getDiseaseId()));

            List<String> voices = imageDataService.findImgData(ImageDataEnum.cureVoice.getIndex(), orderVo.getDiseaseId());
            if ((voices != null) && !voices.isEmpty()) {
                orderDisease.setVoice(voices.get(0));
            }

            if (orderVo.getPackType() != 0) {
                Integer diseaseId = orderVo.getDiseaseId();
                Disease disease = diseaseService.findByPk(diseaseId);
                orderDisease.setDiseaseDesc(disease != null ? disease.getDiseaseInfo() : null);
                orderDisease.setImgStringPath(imageDataService.findImgData(ImageDataEnum.dessImage.getIndex(), diseaseId));
            } else {
                orderDisease.setImgStringPath(imageDataService.findImgData(ImageDataEnum.caseImage.getIndex(), orderVo.getCheckInVo().getCheckInId()));
            }
        }
        return orderDisease;
    }

    public DoctorVO findDoctorVo(Integer doctorId) {
        User user = userManagerImpl.getUser(doctorId);
        DoctorVO doctorVo = new DoctorVO();
        if (user != null) {
            doctorVo.setDoctorId(doctorId);
            doctorVo.setDoctorName(user.getName());
            doctorVo.setDoctorPath(user.getHeadPicFileName());
            Doctor doc = user.getDoctor();
            if (doc != null) {
                doctorVo.setDoctorSpecialty(doc.getDepartments());
                doctorVo.setTitle(doc.getTitle());
                doctorVo.setHospital(doc.getHospital());
            }

            doctorVo.setTelephone(user.getTelephone());
            user = commongdService.getGroupListByUserId(user);
            if ((user != null) && (user.getGroupList() != null) && (user.getGroupList().size() > 0)) {
                Map<String, Object> mapresult = user.getGroupList().get(0);
                doctorVo.setDoctorGroup(String.valueOf(mapresult.get("name")));
            }
            doctorVo.setSex(user.getSex());
            doctorVo.setCity(doctorVo.getCity());
        }
        return doctorVo;
    }

    private UserVO findUserVO(Integer userId) {
        UserVO userVo = new UserVO();
        User users = userManagerImpl.getUser(userId);
        if (users != null) {
            //设置用户信息
            userVo.setUserId(users.getUserId());
            userVo.setUserName(users.getName());
            userVo.setHeadPriPath(users.getHeadPicFileName());
            userVo.setArea(users.getArea());//订单中增加患者常住地
            userVo.setTelephone(users.getTelephone());
        }
        return userVo;
    }

    private OrderDetailVO findOrderDetailVO(OrderVO orderVo) throws HttpApiException {
        OrderDetailVO detailVO = new OrderDetailVO();
        Integer diseaseId = orderVo.getDiseaseId();
        Disease disease = diseaseService.findByPk(diseaseId);
        // 返回病情信息
        if ((orderVo.getOrderType() == OrderEnum.OrderType.order.getIndex())
                || (orderVo.getOrderType() == OrderEnum.OrderType.outPatient
                .getIndex())
                || (orderVo.getOrderType() == OrderEnum.OrderType.care
                .getIndex())
                || (orderVo.getOrderType() == OrderEnum.OrderType.followUp
                .getIndex())
                || (orderVo.getOrderType() == OrderEnum.OrderType.consultation
                .getIndex())
                || (orderVo.getOrderType() == OrderEnum.OrderType.appointment
                .getIndex())) {

            // 设置病情资资料
            dealDiseaseInfo(disease, detailVO);

            List<String> urls = imageDataService.findImgData(
                    ImageDataEnum.dessImage.getIndex(), diseaseId);
            detailVO.setImgStringPath(urls);

        } else if (orderVo.getOrderType() == OrderEnum.OrderType.checkIn
                .getIndex()) {
            // 设置病情资资料
            dealDiseaseInfoForCheckIn(orderVo);
            dealDiseaseInfo(disease, detailVO);
            List<String> urls = imageDataService.findImgData(
                    ImageDataEnum.caseImage.getIndex(), orderVo.getCheckInVo()
                            .getCaseId());
            detailVO.setImgStringPath(urls);

        }
        return detailVO;
    }

    private void dealDiseaseInfoForCheckIn(OrderVO diseaseParam) {
        Integer disesaseId = diseaseParam.getDiseaseId();
        if ((null == disesaseId) || (disesaseId.intValue() == 0)) {
            Patient patient = patientMapper.selectByPrimaryKey(diseaseParam.getPatientId());

            diseaseParam.setAge(DateUtil.calcAge(patient.getBirthday()));
            diseaseParam.setPatientName(patient.getUserName());
            diseaseParam.setBirthday(patient.getBirthday());
            diseaseParam.setSex(patient.getSex() == null ? null : (short) patient.getSex());
            diseaseParam.setArea(patient.getArea());
            diseaseParam.setRelation(patient.getRelation());
        }
    }

    private void dealDiseaseInfo(Disease disease, OrderDetailVO detailVO) {
        if (null != disease) {
            detailVO.setDiseaseId(disease.getId());
            // 现病史
            String diseaseInfo = disease.getDiseaseInfo();
            if (StringUtils.isNotEmpty(diseaseInfo)) {
                detailVO.setDiseaseDesc(diseaseInfo);
            }
            String diseaseInfoNow = disease.getDiseaseInfoNow();
            if (StringUtils.isNotEmpty(diseaseInfoNow)) {
                detailVO.setDiseaseInfoNow(diseaseInfoNow);
            }
            // 既往史
            String diseaseInfoOld = disease.getDiseaseInfoOld();
            if (StringUtils.isNotEmpty(diseaseInfoOld)) {
                detailVO.setDiseaseInfoOld(diseaseInfoOld);
            }
            // 家族史
            String familyDiseaseInfo = disease.getFamilyDiseaseInfo();
            if (StringUtils.isNotEmpty(familyDiseaseInfo)) {
                detailVO.setFamilyDiseaseInfo(familyDiseaseInfo);
            }
            // 月经史
            String menstruationdiseaseInfo = disease
                    .getMenstruationdiseaseInfo();
            if (StringUtils.isNotEmpty(menstruationdiseaseInfo)) {
                detailVO.setMenstruationdiseaseInfo(menstruationdiseaseInfo);
            }
            // 是否到医院就诊过
            Boolean isSeeDoctor = disease.getIsSeeDoctor();
            detailVO.setIsSeeDoctor(isSeeDoctor);
            ;
            // 就诊情况
            String seeDoctorMsg = disease.getSeeDoctorMsg();
            if (StringUtils.isNotEmpty(seeDoctorMsg)) {
                detailVO.setSeeDoctorMsg(seeDoctorMsg);
                detailVO.setIsSeeDoctor(true);
            }

            //就诊时间
            Long visitTime = disease.getVisitTime();
            if (null != visitTime) {
                detailVO.setVisitTime(visitTime);
            }

            String heigth = disease.getHeigth();
            if (StringUtils.isNotEmpty(heigth)) {
                detailVO.setHeigth(heigth);
            }

            // 诊治情况
            String cureSituation = disease.getCureSituation();
            if (StringUtils.isNotEmpty(seeDoctorMsg)) {
                detailVO.setCureSituation(cureSituation);
            }

            String weigth = disease.getWeigth();
            if (StringUtils.isNotEmpty(weigth)) {
                detailVO.setWeigth(weigth);
            }

            String marriage = disease.getMarriage();
            if (StringUtils.isNotEmpty(marriage)) {
                detailVO.setMarriage(marriage);
            }

            String profession = disease.getProfession();
            if (StringUtils.isNotEmpty(profession)) {
                detailVO.setProfession(profession);
            }
            detailVO.setTelephone(disease.getTelephone());
        }
    }


    @Override
    public void checkAndAutoClose() throws HttpApiException {
        logger.info("检查订单自动关闭-start");

        RedisLock lock = new RedisLock();
        boolean hasLock = lock.lock(LockType.ordercheckAndAutoClose.name(), LockType.ordercheckAndAutoClose);
        if (hasLock) {
            try {
                // 等待中队列订单，过了晚上0点到1点全部自动取消，并清除队列
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                if ((calendar.get(Calendar.HOUR_OF_DAY) == 0) || (calendar.get(Calendar.HOUR_OF_DAY) == 1)) {
                    // 模糊查询等待队列键
                    Set<String> set = RedisUtil.getKeysByPattern(RedisUtil.WAITING_QUEUE + "_*");
                    Iterator<String> Iterator = set.iterator();
                    while (Iterator.hasNext()) {
                        String key = Iterator.next();
                        // 取出该key对应的所有值
                        List<String> sList = RedisUtil.lrange(key, 0, -1);
                        // 清除该key对应队列
                        RedisUtil.del(key);
                        // 更新所有订单状为系统取消
                        int length = sList != null ? sList.size() : 0;
                        String val = null;
                        for (int i = 0; i < length; i++) {
                            val = sList.get(i);
                            if (val != null) {
                                Order order = getOne(Integer.parseInt(val));
                                cancelOrder(order, 0, orderCancelEnum.auto.getIndex());

                                Map<String, Object> mapString = new HashMap<String, Object>();
                                String cancelType = "cancelAfter12";
                                mapString.put("cancelType", cancelType);
                                OrderSession orderSession = orderSessionService.findOneByOrderId(order.getId());
                                if ((orderSession != null) && (orderSession.getMsgGroupId() != null)) {
                                    sendOrderNoitfy(order.getUserId().toString(), order.getDoctorId().toString(),
                                            orderSession.getMsgGroupId(), OrderNoitfyType.cancelOrder, mapString);

                                    messageGroupService.updateGroupBizState(orderSession.getMsgGroupId()
                                            , MessageGroupEnum.CANCEL_ORDER.getIndex());
                                }
                            }
                        }
                    }
                }
                lock.unlock(LockType.ordercheckAndAutoClose.name(), LockType.ordercheckAndAutoClose);
            } catch (Exception e) {
                lock.unlock(LockType.ordercheckAndAutoClose.name(), LockType.ordercheckAndAutoClose);
                throw e;
            }
        }

        // 定时任务的工作已迁移到careplan服务
//        //凌晨执行
//        careItemService.executeEveryDay();

        logger.info("检查订单自动关闭-end");

    }

    @Override
    public void cancelOrder(Integer orderId, Integer cancelType) throws HttpApiException {
        cancelOrder(orderId, ReqUtil.instance.getUserId(), cancelType);
    }

    @Override
    public void cancelOrder(OrderParam param, String pwd, Integer cancelType) throws HttpApiException {
        Integer currentUserId = ReqUtil.instance.getUserId();
        User currentUser = userManagerImpl.getUser(currentUserId);
        if (currentUser == null) {
            throw new ServiceException("未找到当前登录用户");
        }
        //博德端且是导医取消才需要填写取消原因
        if (ReqUtil.isBDJL() && (currentUser.getUserType().intValue() == UserEnum.UserType.DocGuide.getIndex())) {
            if (StringUtil.isEmpty(param.getCancelReason())) {
                throw new ServiceException("取消原因不能为空");
            }
            String password = Md5Util.md5Hex(pwd);
            if (!password.equals(currentUser.getPassword())) {
                throw new ServiceException("密码错误");
            }
        }
        cancelOrder(param, currentUserId, cancelType);
    }

    @Override
    public void cancelOrder(OrderParam param, Integer from, Integer cancelType) throws HttpApiException {
        logger.info("cancel order :" + param.getOrderId());
        Integer orderId = param.getOrderId();
        if (orderId == null) {
            throw new ServiceException(30006, "orderId is null");
        }
        Order order = orderMapper.getOne(orderId);
        if (order == null) {
            throw new ServiceException(30007, "Entity " + getClass() + "#" + orderId + " can't not found!");
        }

        order.setCancelReason(param.getCancelReason());

        verifyData(order);

        // 未激活用户的订单不参与48小时关闭机制
        User user = userManagerImpl.getUser(order.getUserId());
        if (user.getStatus() == UserStatus.inactive.getIndex()) {
            return;
        }

        // lock
        RedisLock lock = new RedisLock();
        boolean locked = lock.lock(orderId + "", LockType.order);
        if (!locked) {
            throw new ServiceException(10007, "get order:" + orderId + " lock fail");
        }

        try {
            sendCancelNotify(order, from);
            String serviceTime = "";
            OfflineItem item = offlineDao.findOfflineItemByOrderId(order.getId(), order.getDoctorId());
            if (item != null) {
                Long startTime = item.getStartTime();
                Long endTime = item.getEndTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                serviceTime = sdf.format(startTime);
                sdf = new SimpleDateFormat("HH:mm");
                serviceTime += "-" + sdf.format(endTime);
            }
            cancelOrder(order, from, cancelType);
            //接口兼容修改（医生助手），修改助手与医生，助手与患者的会话状态
            OrderSession orderSession = orderSessionService.findOneByOrderId(order.getId());
            /*if(orderSession != null&&(order.getAssistantId()!=null)&&(orderSession.getAssistantDoctorGroupId()!=null)&&(orderSession.getAssistantPatientGroupId()!=null)){
                GroupStateRequestMessage request = new GroupStateRequestMessage();
                request.setBizStatus(String.valueOf(17));
                //更新订单助手医生会话状态
                request.setGid(orderSession.getAssistantDoctorGroupId());
                MsgHelper.updateGroupBizState(request);
                //更新订单助手患者会话状态
                request.setGid(orderSession.getAssistantPatientGroupId());
                MsgHelper.updateGroupBizState(request);
            }*/ // 去掉和助手之间的会话更新
            if (ReqUtil.isBDJL() && (order.getOrderType().intValue() == OrderEnum.OrderType.appointment.getIndex())) {
                Integer currentUserId = ReqUtil.instance.getUserId();
                User currentUser = userManagerImpl.getUser(currentUserId);
                if (currentUser.getUserType() == UserEnum.UserType.DocGuide.getIndex()) {
                    sendSmsToDocAndPatient(order, serviceTime);
                }
            }
            lock.unlock(orderId + "", LockType.order);
        } catch (Exception e) {
            lock.unlock(orderId + "", LockType.order);
            throw e;
        }
    }

    /**
     * 只有博德端且是名医面对面类型订单且是医且助手在web端取消才发
     *
     * @param order
     */
    private void sendSmsToDocAndPatient(Order order, String serviceTime) {
        User doc = userManagerImpl.getUser(order.getDoctorId());
        Patient patient = patientMapper.selectByPrimaryKey(order.getPatientId());

        if ((doc != null) && (patient != null)) {
//			尊敬的医生您好，由于****（取消原因），助手帮您取消了预约订单
//			患者：***
//			服务时间：2016-07-02 14:00-14:30
//			执业地点：广东省深圳市罗湖区人民医院
//			订单编号：954616360
//			有问题请拨客服电话4009636386处理。
            HospitalVO hVO = baseDataDao.getHospital(order.getHospitalId());
            String hostpital = hVO == null ? "" : hVO.getName();
            String orderNo = order.getOrderNo() + "";
            final String contentDoc = baseDataService.toContent("1040", doc.getName(), order.getCancelReason(), patient.getUserName(), serviceTime, hostpital, orderNo);
            mobSmsSdk.send(doc.getTelephone(), contentDoc, BaseConstants.BD_SIGN);

//			***（患者）您好，由于****（取消原因），助手帮您取消了预约订单
//			医生：***
//			服务时间：2016-07-02 14:00-14:30
//			执业地点：广东省深圳市罗湖区人民医院
//			订单编号：954616360
//			有问题请拨客服电话4009636386处理，如果您已付款，我们会在15分工作日内退还到您的支付账户中。

            final String contentPat = baseDataService.toContent("1041", patient.getUserName(), order.getCancelReason(), doc.getName(), serviceTime, hostpital, orderNo);
            mobSmsSdk.send(patient.getTelephone(), contentPat, BaseConstants.BD_SIGN);
        }

    }

    @Override
    public void cancelOrder(Integer orderId, Integer from, Integer cancelType) throws HttpApiException {
        Order order = orderMapper.getOne(orderId);
        OrderParam param = new OrderParam();
        param.setOrderId(orderId);
        if(Objects.nonNull(order)) {
            param.setCancelReason(order.getCancelReason());
        }
        cancelOrder(param, from, cancelType);
    }


    @Override
    public void cancelOrder(Order order, Integer from, Integer cancelType) {
        Integer preOrderStatus = order.getOrderStatus();
        order.setPreOrderStatus(preOrderStatus);
        order.setOrderStatus(OrderStatus.已取消.getIndex());
        order.setCancelFrom(from);
        updateOrder(order);

        OrderStatusLog statusLog = new OrderStatusLog();
        statusLog.setCreateTime(new Date().getTime());
        statusLog.setOrderId(order.getId());
        statusLog.setSourceVal(preOrderStatus);
        statusLog.setVal(order.getOrderStatus());
        orderStatusMapper.insert(statusLog);

        OrderCancelInfo orderCancelInfo = new OrderCancelInfo();
        orderCancelInfo.setOrderId(order.getId());
        orderCancelInfo.setHistoryStatus(preOrderStatus);
        orderCancelInfo.setUserId(from);
        orderCancelInfo.setCancelType(cancelType);
        orderCancelInfoDao.save(orderCancelInfo);

        //预约订单取消之后空出医生对应的排班信息
        offlineDao.cancelOfflineItem(order.getId());

        // 增加退款单，支持未支付订单的取消，积分订单不做退款处理
        if (order.getOrderType() != OrderType.integral.getIndex()) {
            orderRefundService.addRefundOrder(order.getId());
        }

        // 结束关怀计划
        if (order.getOrderType() == OrderEnum.OrderType.care.getIndex()
                || order.getOrderType() == OrderEnum.OrderType.followUp.getIndex()) {
            this.finishCareOrderAsync(order);
        }
    }

    @Override
    public void finishCareOrderAsync(Order order) {
        String tag = "finishCareOrderAsync";
        logger.info("{}. order={}", tag, order);;
        if (OrderStatus.已完成.getIndex() != order.getOrderStatus().intValue()
                && OrderStatus.已取消.getIndex() != order.getOrderStatus().intValue()) {
            logger.info("{}. 订单未完成或未取消, return. orderId={}", tag, order.getId());
            return;
        }
        if (order.getOrderType() != OrderEnum.OrderType.care.getIndex()
                && order.getOrderType() != OrderEnum.OrderType.followUp.getIndex()) {
            logger.info("{}. 非健康关怀订单, return. orderId={}", tag, order.getId());
            return;
        }
        long finishAt = System.currentTimeMillis();
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    carePlanApiClientProxy.finishOrderCarePlan(order.getCareTemplateId(), order.getId(), finishAt);
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    private void sendCancelNotify(Order order, Integer from) throws HttpApiException {
        OrderSession orderSession = orderSessionService.findOneByOrderId(order.getId());
        if (orderSession == null)
            return;
        User operateUser = userManagerImpl.getUser(from);
        if ((operateUser != null) && (operateUser.getUserType() == UserType.customerService.getIndex())) {// 平台取消订单
            Patient patient = patientService.findByPk(order.getPatientId());
            businessServiceMsg.sendNotifyMsgToAll(orderSession.getMsgGroupId(), "订单已取消");
            sendNotitfy("系统通知",
                    MessageFormat.format("您与患者{0}的订单已取消，请知悉，谢谢你的支持", patient.getUserName()),
                    order.getDoctorId() + "");
            return;
        }
        String cancelType = null;
        // 消息内容
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Patient patient = patientService.findByPk(order.getPatientId());
        User doctorUser = userManagerImpl.getUser(order.getDoctorId());
        paramMap.put("title", StringUtil.returnPackTitle(order.getPackType(), order.getPrice()));
        paramMap.put("patient", patient);
        paramMap.put("docName", doctorUser.getName());

        /**
         * 针对会诊订单的提示
         */
        String toPatientMessage = "";
        String toSpecialistMessage = "";
        String toAssistantMessage = "";
        Integer assistantDoctorId = order.getDoctorId();
        Integer specialistDoctorId = getSpecialistDoctorId(order);

        if (0 == from) {
            // 系统自动取消
            String contentToUser = "";
            String contentToDoctor = "";
            if (OrderStatus.待预约.getIndex() == order.getOrderStatus()) {
                if (OrderType.consultation.getIndex() == order.getOrderType().intValue()) {
                    cancelType = "cancelConsultation";
                    User specialistDoctor = userManagerImpl.getUser(specialistDoctorId);
                    toAssistantMessage = specialistDoctor == null ? "" : specialistDoctor.getName() + "医生48小时未与您确定咨询时间，订单已自动取消";
                    toSpecialistMessage = "您48小时未与" + doctorUser.getName() + " 确定咨询时间，订单已自动取消。";
                } else {
                    cancelType = "noAppointTime";
                    contentToUser = paramMap.get("docName") + "医生48小时未与您确定咨询时间，订单已自动取消";
                    contentToDoctor = "您48小时未与患者" + patient.getUserName() + "确定咨询时间，订单已自动取消。";
                }
            } else {
                // 预约后未支付
                if (OrderStatus.已支付.getIndex() != order.getOrderStatus()) {
                    if (OrderType.consultation.getIndex() == order.getOrderType().intValue()) {
                        cancelType = "cancelConsultation";
                        toAssistantMessage = "患者" + patient.getUserName() + "48小时未支付订单，订单已自动取消。";
                        toSpecialistMessage = "患者" + patient.getUserName() + "48小时未支付订单，订单已自动取消。";
                        toPatientMessage = "您48小时未支付订单，订单已自动取消。";
                    } else if (OrderType.outPatient.getIndex() == order.getOrderType().intValue()) { //门诊两小时未支付自动取消订单
                        cancelType = "noPay";
                        contentToUser = "您2小时未支付订单，订单已自动取消。";
                        contentToDoctor = "患者" + patient.getUserName() + "2小时未支付订单，订单已自动取消。";
                    } else {
                        cancelType = "noPay";
                        contentToUser = "您48小时未支付订单，订单已自动取消。";
                        contentToDoctor = "患者" + patient.getUserName() + "48小时未支付订单，订单已自动取消。";
                    }
                }

                // 支付后 未开始服务
                if (OrderStatus.已支付.getIndex() == order.getOrderStatus()) {
                    if (OrderType.consultation.getIndex() == order.getOrderType().intValue()) {
                        cancelType = "cancelConsultation";
                        toAssistantMessage = patient.getUserName() + "患者与您的咨询服务超过48小时未开始，订单已自动取消。";
                        toSpecialistMessage = patient.getUserName() + "患者与您的咨询服务超过48小时未开始，订单已自动取消。";
                        toPatientMessage = doctorUser.getName() + "医生与您的咨询服务超过48小时未开始，订单已自动取消。退款将于3-10个工作日退还到您的付款账户。";
                    } else if (OrderType.outPatient.getIndex() == order.getOrderType().intValue()
                            && orderSession.getTreatBeginTime() == null) {
                        cancelType = "noStartService";
                        contentToUser = paramMap.get("docName") + "医生与您的订单已自动取消。退款将于3-10个工作日退还到您的付款账户。";
                        contentToDoctor = patient.getUserName() + "患者与您的订单已自动取消。";
                    } else {
                        cancelType = "noStartService";
                        contentToUser = paramMap.get("docName") + "医生与您的咨询服务超过48小时未开始，订单已自动取消。退款将于3-10个工作日退还到您的付款账户。";
                        contentToDoctor = patient.getUserName() + "患者与您的咨询服务超过48小时未开始，订单已自动取消。";
                    }
                }
            }
            Map<String, Object> mapString = new HashMap<String, Object>();
            mapString.put("cancelType", cancelType);
            mapString.put("contentToUser", contentToUser);
            mapString.put("contentToDoctor", contentToDoctor);
            mapString.put("orderType", order.getOrderType());
            mapString.put("order", order);

            mapString.put("toPatientMessage", toPatientMessage);
            mapString.put("toSpecialistMessage", toSpecialistMessage);
            mapString.put("toAssistantMessage", toAssistantMessage);

            String doctorIdString = null;
            if (OrderType.consultation.getIndex() == order.getOrderType().intValue()) {
                // 小医生，大医生
                doctorIdString = assistantDoctorId + "," + specialistDoctorId;
            } else {
                doctorIdString = order.getDoctorId().toString();
            }

            sendOrderNoitfy(order.getUserId().toString(), doctorIdString, orderSession.getMsgGroupId(),
                    OrderNoitfyType.cancelOrder, mapString);

        } else {
            cancelType = "customer";
            Map<String, Object> mapString = new HashMap<String, Object>();
            mapString.put("cancelType", cancelType);
            mapString.put("orderType", order.getOrderType());
            mapString.put("order", order);

            String doctorIdString = null;
            if (OrderType.consultation.getIndex() == order.getOrderType().intValue()) {
                cancelType = "cancelConsultation";
                mapString.put("cancelType", cancelType);
                mapString.put("toSpecialistMessage", "患者" + patient.getUserName() + " 已经取消了订单");
                mapString.put("toAssistantMessage", "患者" + patient.getUserName() + " 已经取消了订单");
                mapString.put("toPatientMessage", "您已经取消了订单");
                // 小医生，大医生
                doctorIdString = assistantDoctorId + "," + specialistDoctorId;
            } else {
                doctorIdString = order.getDoctorId().toString();
            }

            sendOrderNoitfy(order.getUserId().toString(), doctorIdString, orderSession.getMsgGroupId(),
                    OrderNoitfyType.cancelOrder, mapString);
        }

        messageGroupService.updateGroupBizState(orderSession.getMsgGroupId(), MessageGroupEnum.CANCEL_ORDER.getIndex());

        if (cancelType == null) {
            throw new ServiceException(40005, "非用户主动取消,状态受限");
        }
    }

    @Override
    public Integer getSpecialistDoctorId(Order order) {
        if (OrderType.consultation.getIndex() == order.getOrderType().intValue()) {
            List<OrderDoctor> docs = orderDoctorService.findOrderDoctors(order.getId());
            for (OrderDoctor doc : docs) {
                int doctorId = doc.getDoctorId();
                if (doctorId != order.getDoctorId()) {
                    return doctorId;
                }
            }
        }
        return order.getDoctorId();
    }

    private void verifyData(Order order) {
//		if (OrderStatus.已完成.getIndex() == order.getOrderStatus()) {
//
//			throw new ServiceException(30008, "订单已完成，不可取消");
//		}
        if (OrderStatus.已取消.getIndex() == order.getOrderStatus()) {
            throw new ServiceException(30009, "订单已取消");
        }
    }

    @Override
    public void addJesQueTask(Order order) {
        if (order == null) {
            return;
        }

        /**
         * 为了兼容以前的老版本
         * 从2016年7月28日16:28:34 之后除了门诊类型之外的订单都需要去掉自动取消订单功能
         * 做法：使用order中的assistantId做判断
         */
        Order temp = orderMapper.getOne(order.getId());
        Integer assistantId = temp.getAssistantId();

        //加入取消队列条件：1、待预约且咨询订单；2：待支付；3：已支付且咨询、会诊订单、价格为0的关怀订单（即随访）
        if ((OrderStatus.待预约.getIndex() == order.getOrderStatus()) && (OrderType.order.getIndex() == order.getOrderType()) && Objects.isNull(assistantId)) {
            JobTaskUtil.cancelNoAppointOrder(order.getId());
        }
        if ((OrderStatus.待支付.getIndex() == order.getOrderStatus()) && (OrderType.outPatient.getIndex() == order.getOrderType())) {
            JobTaskUtil.cancelNoPayOrder(order.getId());
        }
        if ((OrderStatus.已支付.getIndex() == order.getOrderStatus())
                && ((OrderType.order.getIndex() == order.getOrderType())
                || (OrderType.consultation.getIndex() == order.getOrderType())
                || ((order.getPrice() == 0) && (OrderType.care.getIndex() == order.getOrderType()))
                || (OrderType.outPatient.getIndex() == order.getOrderType()))) {
            if ((order.getPackType() != null) && (order.getPackType() == PackType.message.getIndex())) {
                //图文套餐支付完成后24小时未开始服务，取消订单
                if (Objects.isNull(assistantId)) {
                    JobTaskUtil.cancelPaidOrder(order.getId(), JobTaskUtil.paidTime / 2);
                }
                //图文套餐支付完成后2小时未开始服务，发送短信提醒
                JobTaskUtil.remindMessage(order.getId());
            } else if (OrderType.outPatient.getIndex() == order.getOrderType()) {
                JobTaskUtil.cancelTheNoPrepareOrder(order.getId());
            } else {
                //其他类型支付完成后48小时未开始服务，取消订单
                //陈洁说会诊订单去掉48小时结束服务的功能 2016年10月24日15:10:43
                if (Objects.isNull(assistantId) && OrderType.consultation.getIndex() != order.getOrderType()) {
                    JobTaskUtil.cancelPaidOrder(order.getId(), JobTaskUtil.paidTime);
                }
            }

            //会诊订单支付完成后，距预约时间30分钟前发送消息提醒
            if (OrderType.consultation.getIndex() == order.getOrderType()) {
                OrderSession session = orderSessionService.findOneByOrderId(order.getId());
                if (session.getAppointTime() != null) {
                    long delayTime = session.getAppointTime() - (30 * 60 * 1000) - System.currentTimeMillis();
                    if (delayTime >= 0) {
                        JobTaskUtil.remindConsultation(order.getId(), delayTime / 1000);
                    }
                }
            }
        }
    }

    @Override
    public void cancelThroughTrainOrder(Integer orderId) {
        RedisLock lock = new RedisLock();
        Order order = orderMapper.getOne(orderId);
        if (order == null) {
            throw new ServiceException(30007, "Entity " + getClass() + "#" + orderId + " can't not found!");
        }
        verifyData(order);
        // lock
        boolean locked = lock.lock(orderId + "", LockType.order);
        if (!locked) {
            throw new ServiceException(10007, "get order:" + orderId + " lock fail");
        }
        try {
            cancelOrder(order, ReqUtil.instance.getUserId(), orderCancelEnum.manual.getIndex());

            lock.unlock(orderId + "", LockType.order);
        } catch (Exception e) {
            lock.unlock(orderId + "", LockType.order);
            throw e;
        }
    }

    @Override
    public void add(Order order) {
        orderMapper.add(order);
    }


    @Override
    public List<Integer> getServingOrder(Integer doctorId) {
        return orderMapper.selectServingOrderByDocId(doctorId);
    }

    @Override
    public Object findOrdersGroupByDay(OrderParam param, UserSession session) {
        if (param.getStartCreateTime() == null) {
            Long currentTime = System.currentTimeMillis();
            param.setStartCreateTime(currentTime);
        }
        String minDay = orderMapper.findMinDay(param);
        Long endCreateTime = 0L;
        if (minDay != null) {
            endCreateTime = DateUtil.toTimestamp(minDay);
        }
        param.setEndCreateTime(endCreateTime);
        Map<String, List<OrderVO>> data = new LinkedHashMap<>();
        if (session == null) {
            throw new ServiceException("请登录");
        }

        List<OrderVO> orders = orderMapper.findOrdersNoLimit(param);

//		Integer countOrders = orderMapper.findOrdersCount(param);

        if (session.getUserType() == UserEnum.UserType.patient.getIndex()) {
            for (OrderVO orderVo : orders) {
                //设置医生用户信息
                orderVo.setUserVo(findUserVO(orderVo.getDoctorId()));
                //设置医生信息
                orderVo.setDoctorVo(this.findDoctorVo(orderVo.getDoctorId()));

                orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));
            }
        } else if (session.getUserType() == UserEnum.UserType.doctor.getIndex()) {
            for (OrderVO orderVo : orders) {
                UserVO user = findUserVO(orderVo.getUserId());
                orderVo.setUserVo(user);
                orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));
                orderVo.setTopPath(user.getHeadPriPath());
            }
        } else if (session.getUserType() == UserEnum.UserType.customerService.getIndex()) {
            for (OrderVO orderVo : orders) {
                orderVo.setUserVo(findUserVO(orderVo.getUserId()));
                orderVo.setDoctorVo(this.findDoctorVo(orderVo.getDoctorId()));
                orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));
            }
        } else {
            for (OrderVO orderVo : orders) {
                orderVo.setUserVo(findUserVO(orderVo.getUserId()));
                orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));
            }
        }

        for (OrderVO orderVO : orders) {
            Long createTime = orderVO.getCreateTime();
            String key = "undifined";
            if (createTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                key = sdf.format(createTime);
            }
            List<OrderVO> v = data.get(key);
            if (v == null) {
                v = new ArrayList<OrderVO>();
                data.put(key, v);
            }
            v.add(orderVO);

        }
        List<Map> ret_data = new ArrayList<Map>();
        for (Entry<String, List<OrderVO>> e : data.entrySet()) {
            Map row = new HashMap<>();
            row.put("date", e.getKey());
            row.put("orders", e.getValue());
            ret_data.add(row);
        }

        return ret_data;
    }


    public static void main(String[] args) {
    }

    @Override
    public List<OrderKeyInfoVO> getOrderKeyInfoByOrderId(String... ids) throws HttpApiException {
        if ((ids == null) || (ids.length == 0)) {
            throw new ServiceException("订单ID不能为空");
        }
        List<OrderKeyInfoVO> oList = new ArrayList<OrderKeyInfoVO>();
        //先查出订单
        OrderKeyInfoVO orderKeyInfoVO = null;
        for (String str : ids) {
            int id = Integer.parseInt(str);
            Order order = getOne(id);
            orderKeyInfoVO = new OrderKeyInfoVO();
            orderKeyInfoVO.setOrderId(id);
            int diseaseId = order != null ? order.getDiseaseId() : 0;
            //再根据订单查出患者信息
            if (order != null) {
                if (order.getOrderType() == OrderEnum.OrderType.checkIn.getIndex()) {
                    List<CheckInVO> checkInvos = checkInMapper.getCheckInByOrderId(order.getId());
                    if ((checkInvos != null) && (checkInvos.size() > 0)) {
                        CheckInVO checkInvo = checkInvos.get(0);
                        orderKeyInfoVO.setPatientName(checkInvo.getUserName());
                        if ((checkInvo.getBirthday() != null) && (checkInvo.getBirthday() > 0)) {
                            orderKeyInfoVO.setBirthday(checkInvo.getBirthday());
                        }
                        orderKeyInfoVO.setPatientSex(checkInvo.getSex() == null ? null : BusinessUtil.getSexName((int) checkInvo.getSex()));
                        orderKeyInfoVO.setPatientArea(checkInvo.getArea());
                    }
                } else {
                    Disease disease = diseaseService.findByPk(diseaseId);
                    if (disease != null) {
                        orderKeyInfoVO.setPatientName(disease.getUserName());
                        if ((disease.getBirthday() != null) && (disease.getBirthday() > 0)) {
                            orderKeyInfoVO.setBirthday(disease.getBirthday());
                            orderKeyInfoVO.setPatientAge(disease.getPatientAge());
                        }
                        orderKeyInfoVO.setPatientSex(disease.getSex() == null ? null : BusinessUtil.getSexName(disease.getSex()));
                        orderKeyInfoVO.setPatientArea(disease.getArea());

                    }
                }
            }
            oList.add(orderKeyInfoVO);
        }

        return oList;
    }

    @Override
    public void cancelAdvisoryOrderBySystem(Integer orderId) {
        if (orderId == null) {
            throw new ServiceException("订单ID不能为空");
        }
        cancelQueueOrder(RedisUtil.ADVISORY_QUEUE, orderId);
    }

    private void cancelQueueOrder(String type, Integer orderId) {
        RedisLock lock = new RedisLock();
        boolean hasLock = lock.lock(LockType.outPatientOrderCancel.name(), LockType.outPatientOrderCancel);
        if (hasLock) {
            try {
                Order order = orderMapper.getOne(orderId);
                if (order != null) {
                    cancelOrder(order, 1, orderCancelEnum.auto.getIndex());

                    if (order.getOrderType() == OrderEnum.OrderType.outPatient.getIndex()) {
                        String key = RedisUtil.generateKey(type, order.getDoctorId().toString());
                        RedisUtil.removeVal(key, order.getId().toString());
                    }

                    Map<String, Object> mapString = new HashMap<String, Object>();
                    String cancelType = "cancelAfter3";
                    mapString.put("cancelType", cancelType);
                    OrderSession orderSession = orderSessionService.findOneByOrderId(order.getId());
                    if ((orderSession != null) && (orderSession.getMsgGroupId() != null)) {
                        sendOrderNoitfy(order.getUserId().toString(), order.getDoctorId().toString(),
                                orderSession.getMsgGroupId(), OrderNoitfyType.cancelOrder, mapString);

                        messageGroupService.updateGroupBizState(orderSession.getMsgGroupId()
                                , MessageGroupEnum.CANCEL_ORDER.getIndex());
                    }
                }
                lock.unlock(LockType.outPatientOrderCancel.name(), LockType.outPatientOrderCancel);
            } catch (Exception e) {
                lock.unlock(LockType.outPatientOrderCancel.name(), LockType.outPatientOrderCancel);
                e.printStackTrace();
            }
        }

    }

    @Override
    public PreOrderVO getOrderByDocIdAndUserId(OrderParam param) {
        if ((param.getDoctorId() == null) || (param.getUserId() == null)) {
            throw new ServiceException("医生ID或患者ID为空");
        }
        if (param.getOrderType() == null) {
            throw new ServiceException("订单类型不能为空");
        }
        OrderVO ov = orderMapper.getOrderByDocIdAndUserId(param);
        PreOrderVO po = null;
        if (ov != null) {
            po = new PreOrderVO();
            OrderSession session = orderSessionService.findOneByOrderId(ov.getOrderId());
            if (session != null) {
                po.setOrderId(ov.getOrderId());
                po.setOrderStatus(ov.getOrderStatus());
                String gid = session.getMsgGroupId();
                po.setGid(gid);
            }
        }
        return po;
    }

    /**
     * 订单创建时，生成 p_order_doctor记录，记录医生分成比例
     *
     * @param order
     * @author wangqiao
     * @date 2016年3月3日
     */
    private void generateOrderDoctor(Order order) {
        if (order.getOrderType() == OrderEnum.OrderType.care.getIndex()) {
            //健康关怀 可能有多个医生参与分成
//			PackDoctorExample example = new PackDoctorExample();
//			example.createCriteria().andPackIdEqualTo(order.getPackId());
//			List<PackDoctor> packDoctors = packDoctorMapper.selectByExample(example);
            List<PackDoctor> packDoctors = packDoctorService.findByPackId(order.getPackId());
            for (PackDoctor packDoctor : packDoctors) {
                OrderDoctor record = new OrderDoctor();
                record.setOrderId(order.getId());
                record.setDoctorId(packDoctor.getDoctorId());
                record.setSplitRatio(packDoctor.getSplitRatio());
                record.setReceiveRemind(packDoctor.getReceiveRemind());
                BigDecimal money = new BigDecimal(order.getPrice()).multiply(new BigDecimal(packDoctor.getSplitRatio())).divide(new BigDecimal(100));
                record.setSplitMoney(money.doubleValue());
                orderDoctorMapper.insert(record);
            }
            return;

        }
        if (order.getOrderType() == OrderEnum.OrderType.consultation.getIndex()) {
            //会诊  有两个医生参与分成  由于大医生可以在付款前选择不同集团，导致分成比例可能发生变化
            return;
        }
        //其它类型的订单，只有一个医生参与分成
        OrderDoctor orderDoctor = new OrderDoctor();
        orderDoctor.setOrderId(order.getId());
        orderDoctor.setDoctorId(order.getDoctorId());
        orderDoctor.setSplitRatio(100);
        BigDecimal money = new BigDecimal(order.getPrice());
        orderDoctor.setSplitMoney(money.doubleValue());
        orderDoctorMapper.insert(orderDoctor);


    }


    @Override
    public List<OrderSession> getOverTimeOrderSession() {
        return orderMapper.getOverTimeOrderSession();
    }

    @Override
    public void updateRemarks(Integer orderId, String remarks) {
        Order order = new Order();
        order.setId(orderId);
        order.setRemarks(remarks);
        orderMapper.update(order);
    }

    @Override
    public String getRemarks(Integer orderId) {
        return getOne(orderId).getRemarks();
    }

    @Override
    public PageVO getOrderByRecordStatus(OrderParam param) {

        List<OrderVO> orders = orderMapper.getOrderByRecordStatus(param);
        Integer total = orderMapper.getOrderByRecordStatusCount(param);
        //TODO 转换成医生信息
        for (OrderVO order : orders) {
            User user = userManagerImpl.getUser(order.getDoctorId());
            if (user == null) {
                continue;
            }
            order.setDoctorName(user.getName());
            order.setTopPath(user.getHeadPicFileName());
            order.setDoctorId(user.getUserId());
            order.setTelephone(user.getTelephone());
        }
        return new PageVO(orders, total.longValue(), param.getPageIndex(), param.getPageSize());
    }

    @Override
    public void updateRecordStatus(Integer orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setRecordStatus(OrderRecordStatus.confirmed.getIndex());
        orderMapper.update(order);
    }

    @Override
    public List<OrderVO> findOrderSchedule(OrderParam param) {
        List<Integer> status = new ArrayList<Integer>();
        status.add(OrderEnum.OrderStatus.已支付.getIndex());
        status.add(OrderEnum.OrderStatus.待支付.getIndex());
        param.setMoreStatus(status);
        List<OrderVO> orders = orderMapper.findOrderExistByStatus(param);
        return orders;
    }

    @Override
    public PreOrderVO sumbitCarePlanOrder(CareOrderParam param) throws HttpApiException {

        PreOrderVO vo = new PreOrderVO();

        Pack pack = packService.getPack(param.getPackId());
        if (pack == null) {
            throw new ServiceException("关怀套餐已失效！");
        }

        // 新用户提交订单
        if (StringUtil.isNotEmpty(param.getVerifyCode())) {

            param.setUserId(ReqUtil.instance.getUserId());

            User user = userManager.getUser(param.getUserId());
            if (null == user) {
                throw new ServiceException("用户非法");
            }

            param.setTelephone(user.getTelephone());
            // 校验验证码
            Boolean ok = smsManager.isAvailable(user.getTelephone(), param.getVerifyCode(), "1");
            if (!ok) {
                throw new ServiceException("验证码非法或已过期。");
            }

            if (!StringUtil.isEmpty(param.getName())) {
                userManager.patientRename(param.getName());
            }

            List<Patient> patients = patientService.findByCreateUser(param.getUserId());
            if (CollectionUtils.isEmpty(patients)) {
                throw new ServiceException("患者异常");
            }
            param.setPatientId(patients.get(0).getId());

        } else {

            //短信分享
            if (StringUtil.isNotEmpty(param.getTelephone())) {

                String shareLink = PropertiesUtil.getContextProperty("health.server")
                        + PropertiesUtil.getContextProperty("care.share.link") + "?packId=" + param.getPackId();
                String shortLink = shortUrlComponent.generateShortUrl(shareLink);
                User doctor = userManager.getUser(pack.getDoctorId());

                final String smsContent = baseDataService.toContent("1051", doctor.getName(), pack.getName(), shortLink);
                smsManager.sendSMS(param.getTelephone(), smsContent);
                return null;

            }
            Patient patient = patientService.findByPk(param.getPatientId());
            if (null == patient) {
                throw new ServiceException("患者异常");
            }
            param.setTelephone(patient.getTelephone());
        }

        OrderParam orderParam = new OrderParam();
        orderParam.setPatientId(param.getPatientId());
        orderParam.setPackId(param.getPackId());
        orderParam.setOrderType(4);
        orderParam.setDoctorId(pack.getDoctorId());
        orderParam.setTelephone(param.getTelephone());
        orderParam.setDiseaseDesc(param.getDiseaseDesc());

        vo = this.processCreateOrder(orderParam);

        // 此实现移交到外层
//        if (null != pack.getPrice() && pack.getPrice().intValue() == 0) {
//            this.beginService4FreePlanImmediately(vo.getOrderId());
//        }

        if (vo != null && vo.getIfNewOrder() == true) {
            final String smsContent = baseDataService.toContent("1001", pack.getName(), BaseConstants.XG_PLATFORM,
                    shortUrlComponent.generateShortUrl(BaseConstants.XG_OPEN_PAT()));
            mobSmsSdk.send(param.getTelephone(), smsContent);
        }

        return vo;

/*		Map<String, Object> map = Maps.newHashMap();
		Order order = addOrderCareByShear(param, pack, OrderEnum.OrderActivateStatus.activate, smsContent, map);
		if (null == order) {
			return map;
		}
		addHistoryRecord(order, param);

		if (null != pack.getPrice() && pack.getPrice().intValue() == 0) {
			this.beginService4FreePlanImmediately(order.getId());
		}

		return map;*/
    }


    private Order addOrderCareByShear(CareOrderParam param, Pack pack,
                                      OrderEnum.OrderActivateStatus acStatus, String messageStr, Map<String, Object> map) throws HttpApiException {
        String tag = "addOrderCareByShear";
        if (logger.isInfoEnabled()) {
            logger.info("{}. userId={}, patientId={}, packId={}, packType={}", tag, param.getUserId(), param.getPatientId(), pack.getId(), pack.getPackType());
        }


        OrderVO orderVO = this.getOngoingCareOrderByPatient(param.getPatientId(), pack);
        if (null != orderVO) {
            logger.info("{}. exists ongoingCareOrder. orderVo.orderId={}, patientId={}, packId={}", tag, orderVO.getOrderId(), param.getPatientId(), pack.getId());
            map.put("ifNewOrder", false);
            return null;
        }

        String redyGoLink = "";

        OrderParam orderParam = new OrderParam();
        orderParam.setPackId(param.getPackId());
        orderParam.setUserId(param.getUserId());
        orderParam.setTelephone(param.getTelephone());
        orderParam.setPatientId(param.getPatientId());
        orderParam.setDoctorId(pack.getDoctorId());
        orderParam.setDiseaseDesc(param.getDiseaseDesc());
        orderParam.setActivateStatus(acStatus.getIndex());
        Order order = this.addCareOrder(orderParam);

        List<PackDoctor> packDoctors = packDoctorService.findByPackId(order.getPackId());
        List<String> docIds = new ArrayList<String>();

        for (PackDoctor packDoctor : packDoctors) {
            docIds.add(packDoctor.getDoctorId() + "");
        }
        docIds.add(order.getUserId() + "");
        OrderSession orderSession = new OrderSession();
        //创建会话
        orderSession.setToUserIds(OrderSession.appendStringUserId(docIds));
        GroupInfo groupInfo = createGroupMore(order, orderSession);
        if (groupInfo != null) {
            redyGoLink = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", groupInfo.getGid(), UserEnum.UserType.patient.getIndex()));
            mobSmsSdk.send(param.getTelephone(), messageStr + redyGoLink + " ");
        }

        User docUserInfo = userManagerImpl.getUser(pack.getDoctorId());
        User user = userManagerImpl.getUser(param.getUserId());

//		this.sendCareNotify(pack, order, orderSession, docUserInfo, user, false);

        map.put("link", redyGoLink);

        return order;
    }
    @Autowired
    protected ShortUrlComponent shortUrlComponent;

    private void addHistoryRecord(Order order, CareOrderParam careOrderParam) {
        OrderParam param = new OrderParam();
        param.setStartCreateTime(System.currentTimeMillis());
        param.setDiseaseDesc(careOrderParam.getDiseaseDesc());

        if (order.getOrderStatus() == OrderStatus.已支付.getIndex())
            param.setIsPay(true);
        else
            param.setIsPay(false);
        param.setPackType(order.getPackType());
        param.setOrderId(order.getId());
        illHistoryInfoService.addHistoryRecordFromOrderParam(param);
        param.setPackType(order.getPackType());
    }

    @Resource
    protected AsyncTaskPool asyncTaskPool;

    @Override
    public void sendCareNotify(Pack pack, Order order, OrderSession orderSession, User docUserInfo, User user) {
        sendCareSmsMessageAsync(pack, order, orderSession, docUserInfo, user, true);
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendCareIMMessage(pack, order, orderSession, docUserInfo, user, true);
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    private void sendCareSmsMessageAsync(Pack pack, Order order, OrderSession orderSession, User docUserInfo, User user, boolean pushSms) {
        if (pushSms) {
            this.asyncTaskPool.getPool().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendCareSmsMessage(pack, order, orderSession, docUserInfo, user, pushSms);
                    } catch (HttpApiException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            });
        }
    }

    private void sendCareSmsMessage(Pack pack, Order order, OrderSession orderSession, User docUserInfo, User user, boolean pushSms) throws HttpApiException {
        String tag = "sendCareSmsMessage";
        logger.info("{}. orderId={}, pushSms={}", tag, order.getId(), pushSms);
        if (pushSms) {
            //修改短信发送方式
            String openAppUrl = null;
            String platFrom = null;
            openAppUrl = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("1", orderSession.getMsgGroupId(), UserType.patient.getIndex()));
            platFrom = BaseConstants.XG_PLATFORM;
            //String msg = "尊敬的{0}用户，您好！{1}医生根据对您病情的了解，邀请您加入《{2}》健康计划，请登录玄关健康或直接点击{3} 链接打开查看。";
            // from: 尊敬的{0}用户，您好！{1}医生根据对您病情的了解，邀请您加入《{2}》健康计划，请登录{3}或直接点击{4} 查看。
            // to: 尊敬的{0}，您好！{1}医生根据对您病情的了解，邀请您加入《{2}》健康计划，请登录{3}在我-我的订单中查看或直接点击{4}查看。 070106
            final String msg = baseDataService.toContent("1000", user.getName(), docUserInfo.getName(), pack.getName(), platFrom, openAppUrl);
            mobSmsSdk.send(user.getTelephone(), msg);
        }

    }

    private void sendCareIMMessage(Pack pack, Order order, OrderSession orderSession, User docUserInfo, User user, boolean pushSms) throws HttpApiException {
        String tag = "sendCareIMMessage";
        logger.info("{}. orderId={}, pushSms={}", tag, order.getId(), pushSms);

        String content = "你好，根据专家组对您病情的了解，邀请您加入《{0}》健康计划。";
        businessServiceMsg.sendPushUserToUserMsg(order.getDoctorId() + "", orderSession.getMsgGroupId(), MessageFormat.format(content, pack.getName()), order.getUserId());
    }

    /**
     * 立刻开始免费的健康关怀计划
     *
     * @param orderId
     */
    @Override
    public boolean beginService4FreePlanImmediately(Integer orderId) throws HttpApiException {
        Order order = this.getOne(orderId);
        String tag = "beginService4FreePlanImmediately";
        if (logger.isInfoEnabled()) {
            logger.info("{}. orderId={}, orderType={}, orderPrice={}", tag, orderId, order.getOrderType(), order.getPrice());
        }
        if (order.getOrderType() != OrderType.care.getIndex()) {
            return false;
        }
        if (null != order.getPrice() && 0 != order.getPrice()) {
            return false;
        }

        if (logger.isInfoEnabled()) {
            logger.info("{}. 立刻开始计划. orderId={}", tag, orderId);
        }

        String startDate = DateUtil.formatDate2Str(new Date(), DateUtil.FORMAT_YYYY_MM_DD);
        orderSessionService.beginService4Plan(orderId, startDate);
        return true;
    }

    @Override
    public List<Order> findOrdersByUserId(Integer userId, Integer acStatus) {
        OrderParam orderParam = new OrderParam();
        orderParam.setUserId(userId);
        orderParam.setActivateStatus(acStatus);
        return orderMapper.getNoAcStatusByUserId(orderParam);
    }

    @Override
    public void updateOrderByUserId(Integer userId, Integer acStatus) {
        List<Order> orders = findOrdersByUserId(userId, acStatus);
        if ((orders != null) && (orders.size() > 0)) {
            for (Order order : orders) {
                order.setAcStatus(1);
                updateOrder(order);
            }
        }
    }

    /**
     * 目前只处理orderType=3的门诊订单
     * 只需从疾病表里查询患者名称，不需要去患者表里查询
     */
    @Override
    public List<OrderVO> findExpiredOrder(OrderParam param) throws HttpApiException {
        List<OrderVO> result = new ArrayList<OrderVO>();//待响应表的正常数据订单表
        List<OrderVO> orders = orderMapper.findOrderExpiredStatus(param);
        if ((orders == null) || (orders.size() == 0)) {
            return result;
        }
        String messageStr = "您与患者{0}的{1}已超时，请及时处理！";
        for (OrderVO order : orders) {

            //目前只处理orderType=3的门诊订单
            if (order.getPackType() == null || order.getOrderType() != OrderType.outPatient.getIndex()) {
                continue;
            }

/*			String packName ="";
			if(order.getOrderType()==OrderType.order.getIndex())
			{
				 packName= StringUtil.returnPackName(order.getPackType());
			}
			else if(order.getOrderType()==OrderType.outPatient.getIndex())
			{
				  packName = "门诊套餐";
			}

			if(StringUtil.isEmpty(packName)){
				continue;
			}*/
            String packName = "门诊套餐";

            String pName = "";
            Disease disease = diseaseService.findByPk(order.getDiseaseId());
            if (disease == null) {
                continue;
            } else {
                pName = disease.getUserName();
                if (StringUtil.isEmpty(pName)) {
//					Patient patient = patientService.findByPk(order.getPatientId());
//					if(patient==null) {
//						continue;
//					}else {
//						pName = patient.getUserName();
//					}
                    continue;
                }
            }
            TemplateHelper helper = new TemplateHelper(null, pName, packName);
            String messge = helper.formatText(messageStr);
            order.setRemarks(messge);
            result.add(order);
        }

        return result;
    }

    @Override
    public ConsultOrderPO modifyOrder(OrderParam param) {
        if (StringUtils.isBlank(param.getDiseaseDesc())) {
            throw new ServiceException("病情描述不能为空！！！");
        }
        OrderCache orderCache = iGuideDAO.getOrderCacheByGroup(param.getGid());
        String id = orderCache.getId();
        ConsultOrderPO consultOrderPO = iGuideDAO.getConsultOrderPO(id);
        Integer patientId = consultOrderPO.getDiseaseInfo().getPatientId();
        if (patientId == null) {
            throw new ServiceException(10001, "患者ID为空");
        } else {
            Patient patient = patientMapper.selectByPrimaryKey(patientId);
            if (patient == null) {
                throw new ServiceException(10002, "错误的患者ID [" + patientId + "]");
            }
        }
        OrderDiseaseVO dis = new OrderDiseaseVO();
        dis.setDiseaseDesc(param.getDiseaseDesc());
        dis.setCureSituation(param.getCureSituation());
        //图片也可以传空
        //if(StringUtils.isNoneBlank(param.getImagePaths())){
        dis.setImgStringPath(param.getImagePaths().length > 0 ? Arrays.asList(param.getImagePaths()) : new ArrayList<>());
        //}
        dis.setDiseaseInfo_now(param.getDiseaseInfo_now());
        dis.setDiseaseInfo_old(param.getDiseaseInfo_old());
        dis.setFamilydiseaseInfo(param.getFamilydiseaseInfo());
        dis.setMenstruationdiseaseInfo(param.getMenstruationdiseaseInfo());
        dis.setSeeDoctorMsg(param.getCureSituation());
        dis.setOrderId(id);
		/*if(null!=param.getImagePaths()&&param.getImagePaths().length>0){
			dis.setImgStringPath(Arrays.asList(param.getImagePaths()));
		}*/
        ConsultOrderPO po = iGuideDAO.updateOrderDisease(dis);
        List<OrderRelationPO> list = iGuideDAO.getOrderIdList(id);
        if (list.size() > 0) {
            Disease record = new Disease();
            Integer orderId = null;
            Integer basePatientId = null;//基础订单关联的病人id
            Order order = new Order();
            for (OrderRelationPO orderRelationPO : list) {
                //得到MySQL的基础订单id
                orderId = orderRelationPO.getOrderId();
                order = orderMapper.getOne(orderId);//基础订单信息
                if (null != order) {
                    basePatientId = order.getPatientId();
                    record.setDiseaseInfo(param.getDiseaseDesc());
                    record.setDiseaseInfoNow(param.getDiseaseInfo_now());
                    record.setDiseaseInfoOld(param.getDiseaseInfo_old());
                    record.setFamilyDiseaseInfo(param.getFamilydiseaseInfo());
                    record.setCureSituation(param.getCureSituation());
                    record.setSeeDoctorMsg(param.getCureSituation());
                    record.setMenstruationdiseaseInfo(param.getMenstruationdiseaseInfo());
                    record.setPatientId(basePatientId);
                    record.setId(order.getDiseaseId());
                    diseaseService.updateBaseDisease(record, po.getUserId());
                }
            }
        }
        return po;
    }

    @Override
    public List<Integer> getAllPaidConsultationOrderIds() {
        return orderMapper.getAllPaidConsultationOrderIds();
    }

    @Override
    public Integer getConsultationRoomNumber(Integer orderId) {
//		Order order = orderMapper.getOne(orderId);
//		if(order != null){
//			return order.getOrderNo();
//		}
//		return 0;
        return orderId;
    }

    @Override
    public Object getPatientsGid(Integer doctorId, String userIds, String patientIds) {
        List<Map> obj = Lists.newArrayList();
        String[] array = null;
        String[] patientIdArray = null;
        if (StringUtils.isNotBlank(patientIds)) {
            patientIdArray = patientIds.split(",");
        }
        if (StringUtils.isNotBlank(userIds)) {
            array = userIds.split(",");
        }

        if (patientIdArray != null && patientIdArray.length > 0) {
            for (String patientIdStr : patientIdArray) {
                Map<String, Object> map = Maps.newHashMap();
                Map<String, Object> params = Maps.newHashMap();
                int patientId = Integer.valueOf(patientIdStr);
                params.put("doctorId", doctorId);
                params.put("patientId", patientId);
                String gid = orderMapper.getLastGidByDoctorIdAndPatientId(params);
                Patient patient = patientMapper.selectByPrimaryKey(patientId);
                map.put("userId", patient.getUserId());
                map.put("gid", gid);
                obj.add(map);
            }
        } else if ((array != null) && (array.length > 0)) {
            for (String userIdString : array) {
                Map<String, Object> map = Maps.newHashMap();
                Map<String, Object> params = Maps.newHashMap();
                int userId = Integer.valueOf(userIdString);
                params.put("doctorId", doctorId);
                params.put("userId", userId);
                String gid = orderMapper.getLastGidByDoctorIdAndPatientId(params);
                map.put("userId", userId);
                map.put("gid", gid);
                obj.add(map);
            }
        }
        return obj;
    }

    @Override
    public Set<Integer> findOrderIdByIllCaseInfoId(String illCaseInfoId) {
        List<Integer> orderIds = orderMapper.findOrderIdByIllCaseInfoId(illCaseInfoId);
        Set<Integer> set = null;
        if ((orderIds != null) && (orderIds.size() > 0)) {
            set = new HashSet<Integer>(orderIds);
        }
        return set;
    }

    @Override
    public PageVO getConsultationRecordList(String enterType, Integer paramDoctorId, Integer pageIndex, Integer pageSize) {
        Integer currentDoctorId = ReqUtil.instance.getUserId();
        //（大厅："hall",全部"all",通过医生："byDoctorId"）
        PageVO pageVo = new PageVO();
		/*需求目前针对大小医生没有区分对待，后面以防万一*/
        List<Integer> specialistStatus = new ArrayList<Integer>();
        List<Integer> assistantStatus = new ArrayList<Integer>();
        if ("hall".equals(enterType)) {
            specialistStatus.add(OrderEnum.OrderStatus.待预约.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.待完善.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.待支付.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.已支付.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.进行中.getIndex());
        } else if ("all".equals(enterType)) {
            specialistStatus.add(OrderEnum.OrderStatus.待预约.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.待完善.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.待支付.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.已支付.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.进行中.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.已完成.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.已取消.getIndex());
        } else if ("byDoctorId".equals(enterType)) {
            specialistStatus.add(OrderEnum.OrderStatus.待支付.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.已支付.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.进行中.getIndex());
            specialistStatus.add(OrderEnum.OrderStatus.已完成.getIndex());
        }
        if (specialistStatus.size() < 1) {
            throw new ServiceException("enterType 入口参数错误");
        }
        List<Integer> orderIds = null;
        if (paramDoctorId != null) {
            //我作为小医生的所有会诊订单id
            Map<String, Object> orderMapParam = new HashMap<String, Object>();
            orderMapParam.put("doctorId", currentDoctorId);
            List<Integer> currentDoctorOrderIds = orderMapper.findOrderIdByDoctorId(orderMapParam);
            if ((currentDoctorOrderIds != null) && (currentDoctorOrderIds.size() > 0)) {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("orderIds", currentDoctorOrderIds);
                paramMap.put("doctorId", paramDoctorId);
                orderIds = orderDoctorMapper.findOrderIdByRelationDoctor(paramMap);
            }
        } else {
            orderIds = orderDoctorMapper.findOrderIdByDoctorId(currentDoctorId);
        }
        if ((orderIds == null) || (orderIds.size() < 1)) {
            pageVo.setTotal(0l);
            return pageVo;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("specialistStatus", specialistStatus);
        params.put("orderIds", orderIds);
        params.put("pageSize", pageSize);
        params.put("offset", pageIndex * pageSize);
        Long count = orderMapper.findConsultationOrderCount(params);
        List<Order> orders = orderMapper.findConsultationOrder(params);
        List<ConsultationOrderVo> list = new ArrayList<ConsultationOrderVo>();
        if ((orders != null) && (orders.size() > 0)) {
            for (Order order : orders) {
                Integer assistantId = order.getDoctorId();//发起医生
                String consultationPackId = order.getConsultationPackId();
                int consultDoctorId = -1;
                if(StringUtil.isNotEmpty(consultationPackId)){
                	 GroupConsultationPack consultPack = consultationPackDao.getById(consultationPackId);
                     if (consultPack != null && consultPack.getConsultationDoctor() != null) {
                         consultDoctorId = consultPack.getConsultationDoctor();
                     }
                }
               
                User user = null;
                Integer roleType = null;
                if (currentDoctorId.intValue() == assistantId.intValue()) {
                    roleType = 1;//发起的
                    user = userManagerImpl.getUser(consultDoctorId);
                } else if (currentDoctorId.intValue() == consultDoctorId) {//接收的
                    roleType = 2;
                    user = userManagerImpl.getUser(assistantId);
                } else {
                    roleType = 3;//参与的
                    user = userManagerImpl.getUser(assistantId);
                }
                
                ConsultationOrderVo vo = new ConsultationOrderVo();
                if (user != null) {
                    vo.setDoctorId(user.getUserId());
                    vo.setName(user.getName());
                    vo.setHeadPicFileName(user.getHeadPicFileName());
                    if (user.getDoctor() != null) {
                        vo.setDepartments(user.getDoctor().getDepartments());
                        vo.setHospital(user.getDoctor().getHospital());
                    }
                }
                vo.setIllCaseInfoId(order.getIllCaseInfoId());
                vo.setOrderId(order.getId());
                vo.setOrderStatus(order.getOrderStatus());
                Patient p = patientMapper.selectByPrimaryKey(order.getPatientId());
                if (p != null) {
                    vo.setPatientName(p.getUserName());
                }
                vo.setRoleType(roleType);
                OrderSession orderSession = orderSessionService.findOneByOrderId(order.getId());
                if (orderSession != null) {
                    vo.setMsgGroupId(orderSession.getMsgGroupId());
                }
                list.add(vo);
            }
        }
        pageVo.setTotal(count);
        pageVo.setPageData(list);
        return pageVo;
    }

    private Integer getSpecialistId(Integer orderId, int assistantId) {
        List<OrderDoctor> ods = orderDoctorService.findOrderDoctors(orderId);
        if ((ods != null) && (ods.size() > 0)) {
            for (OrderDoctor orderDoctor : ods) {
                int userId = orderDoctor.getDoctorId();
                if (assistantId != userId) {
                    return userId;
                }
            }
        }
        return null;
    }

    @Override
    public List<OrderVO> findByIdsMap(OrderParam param) {
        return orderMapper.findByIdsMap(param);
    }

    @Override
    public Integer findByIdsMapCount(OrderParam param) {
        return orderMapper.findByIdsMapCount(param);
    }

    @Override
    public OrderDetailVersion2 orderDetail(Integer orderId) {

        OrderDetailVersion2 vo = new OrderDetailVersion2();
        List<OrderDoctorDetail> odList = new ArrayList<>();
        Order order = orderMapper.getOne(orderId);
        OrderSession orderSession = orderSessionService.findOneByOrderId(orderId);
        if (orderSession != null) {
            vo.setMsgGroupId(orderSession.getMsgGroupId());
            if (orderSession.getAppointTime() != null) {
                vo.setAppointmentStart(orderSession.getAppointTime());
                vo.setAppointmentEnd(orderSession.getAppointTime() + 1800000);
            }
            vo.setServiceBeginTime(orderSession.getServiceBeginTime());
        }
        if (order != null) {
            vo.setOrderId(orderId);
            vo.setOrderType(order.getOrderType());
            vo.setIllCaseInfoId(order.getIllCaseInfoId());
            vo.setIllHistoryInfoId(order.getIllHistoryInfoId());
            vo.setOrderStatus(order.getOrderStatus());
            vo.setHospitalId(order.getHospitalId());
            if (StringUtils.isNotBlank(order.getHospitalId())) {
                vo.setHospitalName(baseDataDao.getHospital(order.getHospitalId()).getName());
            }
            vo.setPrice(order.getPrice());
            vo.setOrderNo(order.getOrderNo());
            vo.setPackType(order.getPackType());
            vo.setTimeLong(order.getTimeLong());
            vo.setRemarks(order.getRemarks());
            vo.setCreateTime(order.getCreateTime());
            vo.setFinishTime(order.getFinishTime());
            vo.setPayTime(order.getPayTime());
            vo.setPayType(order.getPayType());
            vo.setPackType(order.getPackType());
            vo.setPackId(order.getPackId());

            //积分问诊相关字段
            Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
            if (order.getPackId() != null && order.getPackId() != 0) {
                vo.setPoint(pack.getPoint());
                vo.setPointName(pack.getName());
            }
            if (StringUtils.isNotBlank(order.getCancelReason())) {
                vo.setCancelReason(order.getCancelReason());
            }
            if (order.getCancelFrom() != null) {
                User cancelUser = userManagerImpl.getUser(order.getCancelFrom());
                if (cancelUser != null) {
                    vo.setCancelFrom(cancelUser.getName());
                }
            }
            Integer patientId = order.getPatientId();
            Integer userId = order.getUserId();
            if (patientId != null) {
                Patient p = patientService.findByPk(patientId);
                User user = userManagerImpl.getUser(userId);
                if ((p != null) && (user != null)) {

                    vo.setPatientId(patientId);
                    vo.setPatientUserId(userId);
                    vo.setPatientUserName(user.getName());
                    vo.setAgeStr(p.getAgeStr());
                    vo.setArea(p.getArea());
                    vo.setHeadPicFileName(p.getTopPath());
                    vo.setSex(p.getSex());
                    vo.setPatientName(p.getUserName());
                    vo.setIdcard(p.getIdcard());
                    vo.setIdtype(p.getIdtype());
                    vo.setPatientTelephone(p.getTelephone());
                    vo.setPatientUserRelation(p.getRelation());
                    //患者新增字段
                    vo.setPatientHeight(p.getHeight());
                    vo.setPatientWeight(p.getWeight());
                    vo.setPatientMarriage(p.getMarriage());
                    vo.setPatientProfessional(p.getProfessional());
                }
            }
            Integer mainOrassistantId = order.getDoctorId();
            Integer orderType = order.getOrderType();
            List<OrderDoctor> orderDoctors = orderDoctorService.findOrderDoctors(orderId);
            if ((orderDoctors != null) && (orderDoctors.size() > 0)) {
                for (OrderDoctor od : orderDoctors) {
                    if (OrderEnum.OrderType.consultation.getIndex() == orderType.intValue()) {//会诊套餐
                        User u = userManagerImpl.getUser(od.getDoctorId());
                        int doctorRole = 2;
                        String groupName = "";
                        if (pack.getDoctorId().intValue() == od.getDoctorId().intValue()) {
                            //大医生
                            doctorRole = 1;
                            String groupId = order.getGroupId();
                            if (StringUtils.isNotBlank(groupId)) {
                                Group g = groupService.getGroupById(groupId);
                                if (g != null) {
                                    groupName = g.getName();
                                }
                            } else {
                                groupName = "玄关健康平台";
                            }
                        }
                        OrderDoctorDetail odd = getOdDetail(u, doctorRole);
                        odd.setGroupName(groupName);
                        odList.add(odd);
                    } else {
                        if (mainOrassistantId.intValue() == od.getDoctorId().intValue()) {
                            //主医生
                            User u = userManagerImpl.getUser(od.getDoctorId());
                            OrderDoctorDetail odd = getOdDetail(u, 1);
                            odList.add(odd);
                        } else {
                            User u = userManagerImpl.getUser(od.getDoctorId());
                            OrderDoctorDetail odd = getOdDetail(u, 2);
                            odList.add(odd);
                        }
                    }
                }
            } else {
                //主医生
                User u = userManagerImpl.getUser(mainOrassistantId);
                OrderDoctorDetail odd = getOdDetail(u, 1);
                odList.add(odd);
            }
        }
        vo.setDoctors(odList);
        //医生助手信息

        return vo;

    }

    private OrderDoctorDetail getOdDetail(User u, Integer doctorRole) {
        OrderDoctorDetail odDetail = new OrderDoctorDetail();
        if (u != null) {
            odDetail.setName(u.getName());
            odDetail.setDoctorRole(doctorRole);
            odDetail.setUserId(u.getUserId());
            odDetail.setTelephone(u.getTelephone());
            odDetail.setHeadPicFilleName(u.getHeadPicFileName());
            Doctor d = u.getDoctor();
            if (d != null) {
                odDetail.setDepartments(d.getDepartments());
                odDetail.setHospital(d.getHospital());
                odDetail.setTitle(d.getTitle());
            }
            /**
             *  不需要获取集团
             */
		   /*List<GroupDoctor> groups = groupDoctorDao.findMainGroupByDoctorId(u.getUserId());
		   if(!Util.isNullOrEmpty(groups)){
			  String groupId = groups.get(0).getGroupId();
			  Group group = groupService.getGroupById(groupId);
			  odDetail.setGroupName(group.getName());
		   }*/
        }
        return odDetail;
    }

    @Override
    public List<OrderVO> getGuideAlreadyServicedOrder(OrderParam param) {
        return orderMapper.getGuideAlreadyServicedOrder(param);
    }

    @Override
    public Integer getGuideAlreadyServicedOrderCount(OrderParam param) {
        return orderMapper.getGuideAlreadyServicedOrderCount(param);
    }

    @Override
    public Integer hasIllCase(Integer orderId) {
        Order o = orderMapper.getOne(orderId);
        int rtn = 0;
        if ((o != null) && StringUtils.isNotBlank(o.getIllCaseInfoId())) {
            String illcaseInfoId = o.getIllCaseInfoId();
            String illcaseTypeId = illCaseDao.getMustFillitemId();
            IllCaseTypeContent content = illCaseDao.getMainItemContentListByIllcaseId(illcaseInfoId, illcaseTypeId);
            if ((content != null) && StringUtils.isNotBlank(content.getContentTxt())) {
                rtn = 1;
            } else {
                //remove illcaseInfo
                illCaseServer.clearIllCaseAllById(illcaseInfoId);
            }
        }
        return rtn;
    }

    @Override
    public Map<String, Object> getIllCase(Integer orderId) {
        Map<String, Object> result = Maps.newHashMap();
        Order o = orderMapper.getOne(orderId);
        int rtn = 0;
        if ((o != null) && StringUtils.isNotBlank(o.getIllCaseInfoId())) {
            String illcaseInfoId = o.getIllCaseInfoId();
            String illcaseTypeId = illCaseDao.getMustFillitemId();
            IllCaseTypeContent content = illCaseDao.getMainItemContentListByIllcaseId(illcaseInfoId, illcaseTypeId);
            if ((content != null) && StringUtils.isNotBlank(content.getContentTxt())) {
                rtn = 1;
                result.put("IllCaseInfoId", illcaseInfoId);
            } else {
                //remove illcaseInfo
                illCaseServer.clearIllCaseAllById(illcaseInfoId);
            }
        }
        result.put("hasIllCase", rtn);

        return result;
    }

    @Override
    public List<Integer> getOrderDoctorIds(Integer orderId) {
        List<Integer> doctorIds = new ArrayList<Integer>();
        List<OrderDoctor> orderDoctors = orderDoctorService.findOrderDoctors(orderId);
        for (OrderDoctor orderDoctor : orderDoctors) {
            doctorIds.add(orderDoctor.getDoctorId());
        }
        return doctorIds;
    }

    @Override
    public void updateCareCorder(OrderParam param) {
        if (param.getOrderId() == null) {
            throw new ServiceException("订单id为空");
        }
        if (StringUtils.isBlank(param.getDiseaseDesc())) {
            throw new ServiceException("请填写病情描述");
        }
        Order o = orderMapper.getOne(param.getOrderId());
        if ((o == null) || (OrderEnum.OrderType.care.getIndex() != o.getOrderType())) {
            throw new ServiceException("找不到对应的健康关怀订单");
        }

        IllCaseInfo caseInfo = new IllCaseInfo();
        caseInfo.setCreateTime(System.currentTimeMillis());
        caseInfo.setDoctorId(o.getDoctorId());
        caseInfo.setUserId(o.getUserId());
        caseInfo.setPatientId(param.getPatientId());
        caseInfo.setTreateType(ConsultationEnum.IllCaseTreatType.care.getIndex());
        if ((param.getImagePaths() != null) && (param.getImagePaths().length > 0)) {
            caseInfo.setImageUlrs(Arrays.asList(param.getImagePaths()));
        }
        caseInfo.setOrderId(param.getOrderId());
        caseInfo.setMainCase(param.getDiseaseDesc());
        caseInfo.setSeeDoctorMsg(param.getSeeDoctorMsg());
        IllCaseInfo dbinfo = illCaseServer.createIllCaseInfo(caseInfo);
        if (dbinfo != null) {
            String illCaseInfoId = dbinfo.getId();
            Order order = new Order();
            order.setId(param.getOrderId());
            order.setPatientId(param.getPatientId());
            param.setUserId(o.getUserId());
            Integer diseaseId = addDisease(param);
            order.setDiseaseId(diseaseId);
            order.setIllCaseInfoId(illCaseInfoId);
            orderMapper.updateCareOrder(order);
        }
    }

    @Override
    public String getGroupNameByOrderId(Integer orderId) {
        Order o = orderMapper.getOne(orderId);
        if ((o != null) && StringUtils.isNotBlank(o.getGroupId())) {
            String groupId = o.getGroupId();
            Group g = groupService.getGroupById(groupId);
            if (g != null) {
                return g.getName();
            }
        }
        return null;
    }

    @Override
    public Object consultationMember(Integer orderId, Integer roleType) {
        Map<String, Object> map = new HashMap<String, Object>();
        OrderSession os = orderSessionService.findOneByOrderId(orderId);
        if (os != null) {
            map.put("time", os.getAppointTime());
            //找到组员
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("gid", os.getMsgGroupId());
            JSON json = ImHelper.instance.postJson("inner/group", "groupUsers.action", paramMap);
            List<Map> result = JSON.parseArray(json.toString(), Map.class);
            for (Map<String, Object> m : result) {
                Integer userId = Integer.parseInt(m.get("id").toString());
                User u = userManager.getUser(userId);
                if (u.getUserType().intValue() == UserType.doctor.getIndex()) {
                    m.put("title", u.getDoctor().getTitle());
                    m.put("department", u.getDoctor().getDepartments());
                    m.put("hospital", u.getDoctor().getHospital());
                    m.put("telephone", u.getTelephone());
                } else if (u.getUserType().intValue() == UserType.patient.getIndex()) {
                    m.put("area", u.getArea());
                    m.put("age", u.getAgeStr());
                    m.put("sex", u.getSex());
                    m.put("telephone", u.getTelephone());
                }
            }
            map.put("members", result);
        }
        return map;
    }

    @Override
    public PageVO getAppointmentOrders(Integer status, Integer pageIndex, Integer pageSize) {
        PageVO pageVo = new PageVO();
        if (status == null) {
            status = OrderEnum.OrderStatus.待预约.getIndex();
        }
        pageIndex = pageIndex == null ? pageVo.getPageIndex() : pageIndex;
        pageSize = pageSize == null ? pageVo.getPageSize() : pageSize;
        Map<String, Object> sqlparams = new HashMap<>();
        sqlparams.put("status", status);
        sqlparams.put("pageIndex", pageIndex);
        sqlparams.put("pageSize", pageSize);
        sqlparams.put("offset", pageSize * pageIndex);
        long count = orderMapper.getAppointmentOrdersCount(sqlparams);
        List<AppointOrderGuideVo> list = orderMapper.getAppointmentOrders(sqlparams);
        if ((list != null) && (list.size() > 0)) {
            for (AppointOrderGuideVo item : list) {
                User d = userManagerImpl.getUser(item.getDoctorId());
                if (d != null) {
                    item.setDoctorName(d.getName());
                    if (d.getDoctor() != null) {
                        item.setTitle(d.getDoctor().getTitle());
                        item.setDepartments(d.getDoctor().getDepartments());
                    }
                }
                Patient p = patientMapper.selectByPrimaryKey(item.getPatientId());
                if (p != null) {
                    item.setPatientName(p.getUserName());
                }
            }
        }
        pageVo.setPageData(list);
        pageVo.setTotal(count);
        return pageVo;
    }

    @Override
    public PageVO getOrderListByMC(OrderParam param) {
        //医生姓名 患者姓名 线下医院 订单类型 订单状态 起至时间
        List<OrderVO> orders = getOrderListToDown(param);
        Integer count = orderMapper.getOrderListByMoreCondCount(param);
        count = count == null ? 0 : count;
        PageVO pageVO = new PageVO();
        pageVO.setPageData(orders);
        pageVO.setTotal(Long.valueOf(count));
        pageVO.setPageSize(param.getPageSize());
        pageVO.setPageIndex(param.getPageIndex());
        return pageVO;
    }

    private List<Integer> getUserIdList(List<User> list, Map<Integer, User> dMap) {
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        List<Integer> ids = new ArrayList<Integer>();
        for (User u : list) {
            ids.add(u.getUserId());
            dMap.put(u.getUserId(), u);
        }
        return ids;
    }

    private List<Integer> getPatientIdList(List<Patient> list, Map<Integer, Patient> pMap) {
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        List<Integer> ids = new ArrayList<Integer>();
        for (Patient u : list) {
            ids.add(u.getId());
            pMap.put(u.getId(), u);
        }
        return ids;
    }

    private List<String> getHostpitalIds(List<Object> list, Map<String, Map<String, Object>> hmap) {
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        List<String> ids = new ArrayList<String>();
        for (Object o : list) {
            Map<String, Object> map = (Map<String, Object>) o;
            ids.add(map.get("id").toString());
            hmap.put(map.get("id").toString(), map);
        }
        return ids;
    }

    @Override
    public List<OrderVO> getOrderListToDown(OrderParam param) {
        List<Integer> doctorIdList = null;
        Map<Integer, User> dMap = new HashMap<Integer, User>();
        if (StringUtil.isNoneBlank(param.getDoctorName())) {
            List<User> list = userManagerImpl.getUserListByTypeAndFuzzyName(UserEnum.UserType.doctor.getIndex(), param.getDoctorName());
            doctorIdList = getUserIdList(list, dMap);
            if (doctorIdList == null) {
                return new ArrayList<OrderVO>();
            }
            param.setUserIds(doctorIdList);
        }
        List<Integer> patientList = null;
        Map<Integer, Patient> pMap = new HashMap<Integer, Patient>();
        if (StringUtil.isNoneBlank(param.getPatientName())) {
            List<Patient> list = patientService.getPatientsByFuzzyName(param.getPatientName());
            patientList = getPatientIdList(list, pMap);
            if (patientList == null) {
                return new ArrayList<OrderVO>();
            }
            param.setPatientIds(patientList);
        }
        List<String> hostpitalList = null;
        Map<String, Map<String, Object>> hMap = new HashMap<String, Map<String, Object>>();
        if (StringUtil.isNoneBlank(param.getHostpitalName())) {
            DoctorParam dparam = new DoctorParam();
            dparam.setKeyWord(param.getHostpitalName());
            dparam.setPageIndex(0);
            dparam.setPageSize(Integer.MAX_VALUE);
            PageVO page = userManagerImpl.findHospitalByCondition(dparam);
            List<Map<String, Object>> list = (List<Map<String, Object>>) page.getPageData();
            if (page.getPageData().isEmpty()) {
                return new ArrayList<OrderVO>();
            }
            hostpitalList = getHostpitalIds(new ArrayList<Object>(list), hMap);
            if ((hostpitalList == null) || (hostpitalList.size() == 0)) {
                return new ArrayList<OrderVO>();
            }
            param.setHospitalIds(hostpitalList);
        }
        List<OrderVO> orders = orderMapper.getOrderListByMoreCond(param);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (OrderVO vo : orders) {
            vo.setFormateTime(sdf.format(vo.getCreateTime()));
            setOrderStatus(vo);
            setOrderType(vo);
            String docName = dMap.get(vo.getDoctorId()) == null ? "" : dMap.get(vo.getDoctorId()).getName();
            if (StringUtil.isEmpty(docName)) {
                User u = userManagerImpl.getUser(vo.getDoctorId());
                docName = u == null ? "" : u.getName();
            }
            vo.setDoctorName(docName);

            String pName = pMap.get(vo.getPatientId()) == null ? "" : pMap.get(vo.getPatientId()).getUserName();
            if (StringUtil.isEmpty(pName)) {
                Patient p = patientService.findByPk(vo.getPatientId());
                pName = p == null ? "" : p.getUserName();
            }
            vo.setPatientName(pName);

            Map<String, Object> temp = hMap.get(vo.getHospitalId());
            String hName = temp == null ? "" : temp.get("name").toString();
            if (StringUtil.isEmpty(hName)) {
                HospitalPO h = userManagerImpl.getHostpitalByPK(vo.getHospitalId());
                hName = h == null ? "" : h.getName();
            }
            vo.setHospitalName(hName);
        }
        return orders;
    }

    private void setOrderType(OrderVO vo) {
        vo.setOrderTypeStr(OrderEnum.getOrderType(vo.getOrderType()));
    }

    private void setOrderStatus(OrderVO vo) {
        vo.setOrderStatusStr(OrderEnum.getOrderStatus(vo.getOrderStatus()));
    }


    @Override
    public void sendDoctorCardInfo(Integer doctorId, String msgId) throws HttpApiException {
        User u = userManagerImpl.getUser(doctorId);
        businessServiceMsg.sendUserToUserMsg(ReqUtil.instance.getUserId() + "", msgId, "根据您的病情，为您推荐转诊给" + u.getName() + "医生。您可以与该医生保持沟通交流。");
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        String groupName = consultationFriendService.getMainGroupName(doctorId);
        imgTextMsg.setContent(groupName);
        imgTextMsg.setTime(System.currentTimeMillis());
        StringBuffer remark = new StringBuffer();
        if ((u != null) && (u.getDoctor() != null)) {
            imgTextMsg.setTitle(u.getName());
            Doctor d = u.getDoctor();
            if (StringUtils.isBlank(groupName)) {
                imgTextMsg.setContent(d.getHospital());
            }
            remark.append(d.getTitle())
                    .append("/")
                    .append(d.getDepartments())
                    .append("|");
            remark.append("擅长 :");
            StringBuilder goodAt = new StringBuilder();
            if ((d.getExpertise() != null) && (d.getExpertise().size() > 0)) {
                List<DiseaseType> diseaseTypes = diseaseTypeRepository.findByIds(d.getExpertise());
                if ((diseaseTypes != null) && (diseaseTypes.size() > 0)) {
                    for (DiseaseType dt : diseaseTypes) {
                        goodAt.append(dt.getName())
                                .append(" ,");
                    }
                    goodAt.deleteCharAt(goodAt.length() - 1);
                }
            }
            if (goodAt.length() > 0) {
                remark.append(goodAt);
            } else {
                remark.append("暂无");
            }
        }
        imgTextMsg.setRemark(remark.toString());
        imgTextMsg.setPic(u.getHeadPicFileName());
        imgTextMsg.setStyle(10);
        Map<String, Object> param_b = new HashMap<String, Object>();
        param_b.put("bizType", 25);
        param_b.put("bizId", doctorId);
        imgTextMsg.setParam(param_b);
        //imgTextMsg.setFooter("查看详情");
        GuideMsgHelper.getInstance().sendImgTextMsg(
                msgId, ReqUtil.instance.getUserId() + "",
                null, imgTextMsg, false);
    }

    @Override
    public void sendIllCaseCardInfo(Integer orderId, String illCaseInfoId, String msgId, String fromUserId) throws HttpApiException {
        if (StringUtils.isBlank(illCaseInfoId)) {
            Order o = orderMapper.getOne(orderId);
            if (o != null) {
                illCaseInfoId = o.getIllCaseInfoId();
            }
        }
        if (StringUtils.isBlank(illCaseInfoId)) {
            throw new ServiceException("非法参数   orderId = " + orderId + " , illCaseInfoId = " + illCaseInfoId);
        }
        IllCaseInfo info = illCaseDao.getIllCase(illCaseInfoId);
        if (info != null) {
            IllCasePatientInfo patientInfo = illCaseDao.getIllCasePatient(illCaseInfoId);
            String mainCase = info.getMainCase();
            String name = patientInfo.getPatientName();
            String ageStr = patientInfo.getAgeStr();
            String sex = patientInfo.getSex();
            //发送系统消息
            if (StringUtils.isNotBlank(fromUserId)) {
                businessServiceMsg.sendUserToUserMsg(fromUserId, msgId, "我发送了一个典型的病情案例，请及时查看交流。");
            } else {
                fromUserId = info.getUserId() + "";
            }
            sendIllCaseCardInfo(fromUserId, illCaseInfoId, msgId, mainCase, name, ageStr, sex);
        }
    }

    @Override
    public void sendIllCaseCardInfo(String userId, String illCaseInfoId, String msgId, String mainCase, String name, String ageStr, String sex) throws HttpApiException {
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle(name);
        imgTextMsg.setContent("主诉：" + mainCase);
        Map<String, Object> param_b = new HashMap<String, Object>();
        param_b.put("bizType", 26);
        param_b.put("bizId", illCaseInfoId);
        param_b.put("sex", sex);
        param_b.put("age", ageStr);
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setStyle(8);
        imgTextMsg.setParam(param_b);
        imgTextMsg.setFooter("查看患者病情资料");

        GuideMsgHelper.getInstance().sendImgTextMsg(msgId, userId + "", null, imgTextMsg, false);
    }


    @Override
    public PageVO transferRecords(Integer transferRecordType, Integer pageIndex, Integer pageSize) {
        PageVO pageVo = new PageVO();
        OrderParam param = new OrderParam();
        List<IllTransferRecord> recordList = illCaseDao.findTransferRecords(transferRecordType);
        if ((recordList == null) || (recordList.size() < 1)) {
            return new PageVO();
        }
        List<Integer> orderIds = new ArrayList<Integer>();
        Map<Integer, IllTransferRecord> cacheMap = new HashMap<>();
        for (IllTransferRecord record : recordList) {
            orderIds.add(record.getTargetOrderId());
            cacheMap.put(record.getTargetOrderId(), record);
        }
        pageIndex = pageIndex == null ? pageVo.getPageIndex() : pageIndex;
        pageSize = pageSize == null ? pageVo.getPageSize() : pageSize;
        param.setPageIndex(pageIndex);
        param.setPageSize(pageSize);
        param.setDoctorId(Integer.MIN_VALUE);//满足sql查询
        param.setOrderIds(orderIds);
        pageVo = this.findOrders(param, ReqUtil.instance.getUser());
        if ((pageVo != null) && (pageVo.getPageData() != null) && (pageVo.getPageData().size() > 0)) {
            @SuppressWarnings("unchecked")
            List<OrderVO> orders = (List<OrderVO>) pageVo.getPageData();
            for (OrderVO orderVO : orders) {
                IllTransferRecord record = cacheMap.get(orderVO.getOrderId());
                orderVO.setIllCaseInfoId(record.getIllCaseInfoId());
                User u = null;
                if (transferRecordType == 1) {
                    u = userManagerImpl.getUser(record.getReceiveDoctorId());
                    if (u != null) {
                        orderVO.setReceiveDoctorName(u.getName());
                    }
                } else if (transferRecordType == 2) {
                    u = userManagerImpl.getUser(record.getTransferDoctorId());
                    if (u != null) {
                        orderVO.setTransferDoctorName(u.getName());
                    }
                }
            }
        }
        return pageVo;
    }


    @Override
    public List<OrderVO> getAppointmentListByCondition(OrderParam param) {
        List<OrderVO> orderList = orderMapper.getAppointmentListByCondition(param);
        return orderList;
    }

    @Override
    public List<OrderVO> getAppointmentListByConditionByMongo(OrderParam param) {
        //List<OrderVO> orderList= orderMapper.getAppointmentListByCondition(param);
        List<OrderVO> orderList = new ArrayList<OrderVO>();
        Query<OfflineItem> query = dsForRW.createQuery(OfflineItem.class);
        query.field("doctorId").equal(param.getDoctorId());
        query.field("hospitalId").equal(param.getHospitalId());
        query.field("startTime").greaterThanOrEq(param.getOppointTime());
        query.field("startTime").lessThanOrEq(param.getOppointTime() + 86400000);
        query.field("period").equal(param.getPeriod());
        //query.field("status").notEqual(1);
        Pack pack = packService.getDoctorPackDBData(param.getDoctorId(), PackType.appointment.getIndex());
        Long price = 0L;
        if (pack != null) {
            price = pack.getPrice();
        }
        for (OfflineItem oi : query.asList()) {
            OrderVO orderVo = new OrderVO();
            if ((oi.getOrderId() != null) && (oi.getPatientId() != null)) {

                orderVo.setOrderId(oi.getOrderId());
                Order order = orderMapper.getOne(oi.getOrderId());
                orderVo.setOrderStatus(order.getOrderStatus());
                if (order.getOrderStatus() == OrderStatus.已支付.getIndex()) {//
                    OrderSession os = orderSessionService.findOneByOrderId(order.getId());
                    orderVo.setServiceBeginTime(os.getServiceBeginTime());
                }
                orderVo.setRemarks(order.getRemarks());
                orderVo.setPatientId(oi.getPatientId());
                Patient patient = patientMapper.selectByPrimaryKey(oi.getPatientId());
                orderVo.setPatientName(patient.getUserName());
                orderVo.setTopPath(patient.getTopPath());
                orderVo.setTelephone(patient.getTelephone());
            }
            orderVo.setDoctorId(oi.getDoctorId());
            orderVo.setHospitalId(oi.getHospitalId());
            orderVo.setAppointTime(oi.getStartTime());
            orderVo.setOfflineItemId(oi.getId());
            orderVo.setMoney(price);
            orderList.add(orderVo);
        }
        //对预约时间进行排序
        orderList.sort(new Comparator<OrderVO>() {
            @Override
            public int compare(OrderVO vo1, OrderVO vo2) {
                return (int) (vo1.getAppointTime() - vo2.getAppointTime());
            }
        });
        return orderList;
    }


    @Override
    public Integer getHaveAppointmentListByDate(OrderParam param) {

        return orderMapper.getHaveAppointmentListByDate(param);
    }

    @Override
    public Long getHaveAppointmentListByDateByMongo(OrderParam param) {
        Query<OfflineItem> query = dsForRW.createQuery(OfflineItem.class);
        query.field("hospitalId").equal(param.getHospitalId());
        if (param.getDoctorId() != null) {
            query.field("doctorId").equal(param.getDoctorId());
            /**
             * 根据医生过滤只获取没有被预约的数据
             */
            //query.field("status").equal(OfflineStatus.待预约.getIndex());
        }
        query.field("startTime").greaterThanOrEq(param.getOppointTime());
        query.field("startTime").lessThan(param.getOppointTime() + 86400000);

        return query.countAll();
    }

    @Override
    public Object getAppointmentPaidOrders(String groupId, String hospitalId, Long start, Long end) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> sqlmap = new HashMap<String, Object>();
        List<Integer> orderIds = orderSessionService.getTimeAreaOrderIds(start, end);
        if ((orderIds != null) && (orderIds.size() > 0)) {
            sqlmap.put("groupId", groupId);
            sqlmap.put("hospitalId", hospitalId);
            sqlmap.put("orderIds", orderIds);
            List<Map<String, Object>> countMap = orderMapper.getAppointmentPaidOrdersGroupByDoctorId(sqlmap);
            List<Integer> doctorIds = new ArrayList<Integer>();
            Map<Integer, Integer> doctorCountMap = new HashMap<Integer, Integer>();
            if ((countMap != null) && (countMap.size() > 0)) {
                for (Map<String, Object> item : countMap) {
                    Integer doctorId = Integer.valueOf(item.get("doctorId").toString());
                    Integer count = Integer.valueOf(item.get("count").toString());
                    doctorIds.add(doctorId);
                    doctorCountMap.put(doctorId, count);
                }

                /**
                 * 获取医生数据
                 */
                List<User> users = userManagerImpl.findDoctorsInIds(doctorIds, 0, Integer.MAX_VALUE);
                for (User d : users) {
                    Map<String, Object> resultMap = new HashMap<String, Object>();
                    resultMap.put("doctorId", d.getUserId());
                    resultMap.put("name", d.getName());
                    resultMap.put("headPicFileName", d.getHeadPicFileName());
                    resultMap.put("count", doctorCountMap.get(d.getUserId()));
                    if (d.getDoctor() != null) {
                        resultMap.put("departments", d.getDoctor().getDepartments());
                        resultMap.put("title", d.getDoctor().getTitle());
                    }
                    result.add(resultMap);
                }
            }
        }
        return result;
    }

    @Override
    public PageVO searchAppointmentOrder4Guide(List<Integer> patientIds, Integer pageIndex, Integer pageSize) {
        PageVO pageVo = new PageVO();
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        Map<String, Object> sqlParam = new HashMap<String, Object>();
        pageIndex = pageIndex == null ? 0 : pageIndex;
        pageSize = pageSize == null ? pageVo.getPageSize() : pageSize;
        sqlParam.put("patientIds", patientIds);
        sqlParam.put("pageIndex", pageIndex);
        sqlParam.put("pageSize", pageSize);
        sqlParam.put("offset", pageSize * pageIndex);
        Long count = orderMapper.searchAppointmentOrder4GuideCount(sqlParam);
        if ((count != null) && (count > 0)) {
            List<OrderVO> ods = orderMapper.searchAppointmentOrder4Guide(sqlParam);
            for (OrderVO ov : ods) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("serviceBeginTime", ov.getServiceBeginTime());
                if (ov.getAppointTime() != null) {
                    map.put("appointmentStart", ov.getAppointTime());
                    map.put("appointmentEnd", ov.getAppointTime() + 1800000);
                }
                Patient p = patientMapper.selectByPrimaryKey(ov.getPatientId());
                if (p != null) {
                    map.put("patientName", p.getUserName());
                    map.put("patientTelephone", p.getTelephone());
                }
                User d = userManagerImpl.getUser(ov.getDoctorId());
                map.put("doctorName", d.getName());
                map.put("orderType", ov.getOrderType());
                map.put("orderId", ov.getOrderId());
                map.put("orderStatus", ov.getOrderStatus());
                dataList.add(map);
            }
            pageVo.setPageData(dataList);
            pageVo.setTotal(count);
        }
        return pageVo;
    }

    @Override
    public void refuseAppointOrder(Integer orderId) throws HttpApiException {
        Order order = orderMapper.getOne(orderId);
        if (order == null) {
            throw new ServiceException("订单[orderId = " + orderId + "]不存在");
        }
        Order o = new Order();
        o.setOrderStatus(OrderStatus.已拒绝.getIndex());
        o.setId(orderId);
        orderMapper.update(o);
        offlineDao.cancelOfflineItem(orderId);

        Integer patientId = order.getPatientId();
        Patient p = patientService.findByPk(patientId);
        Integer doctorId = order.getDoctorId();
        User d = userManagerImpl.getUser(doctorId);
        OrderSession os = orderSessionService.findOneByOrderId(orderId);
        Map<String, Object> mapString = new HashMap<String, Object>();
        mapString.put("patientName", p.getUserName());
        mapString.put("doctorName", d.getName());
        mapString.put("telephone", p.getTelephone());

        sendOrderNoitfy(order.getUserId() + "", doctorId + "", os.getMsgGroupId(), OrderNoitfyType.refuseorder, mapString);
        messageGroupService.updateGroupBizState(os.getMsgGroupId(), MessageGroupEnum.REFUSE_ORDER.getIndex());
    }

    @Override
    public void updateRemark(Integer orderId, String remark, String isSend) {
        Order order = new Order();
        order.setId(orderId);
        order.setRemarks(remark);
        orderMapper.update(order);
        //如果勾选了给患者发送短信
        if ("true".equals(isSend)) {
            Order orderinfo = orderMapper.getOne(orderId);
            if (orderinfo == null) {
                throw new ServiceException("订单已经不存在");
            }
            Patient patient = patientService.findByPk(orderinfo.getPatientId());
            if (patient == null) {
                throw new ServiceException("患者不存在！！！");
            }
            //根据医生查询此订单属于哪个平台发发出
            User user = userManagerImpl.getUser(orderinfo.getDoctorId());
            mobSmsSdk.send(patient.getTelephone(), remark, mobSmsSdk.isBDJL(user) ? BaseConstants.BD_SIGN : BaseConstants.XG_SIGN);
        }
    }

    @Override
    public Object getAppointmentOrder4H5(Integer orderId) {
        Map<String, Object> map = new HashMap<>();
        Order o = orderMapper.getOne(orderId);
        if (o != null) {
            OfflineItem oi = offlineDao.findOfflineItemByOrderId(orderId, o.getDoctorId());
            if (oi != null) {
                Integer patientId = oi.getPatientId();
                Integer doctorId = oi.getDoctorId();
                Patient p = patientMapper.selectByPrimaryKey(patientId);
                User d = userManagerImpl.getUser(doctorId);
                Map<String, Object> pInfo = new HashMap<>();
                pInfo.put("name", p.getUserName());
                pInfo.put("ageStr", p.getAgeStr());
                pInfo.put("sex", p.getSex());
                pInfo.put("area", p.getArea());
                pInfo.put("topPath", p.getTopPath());
                pInfo.put("id", p.getId());
                pInfo.put("telephone", p.getTelephone());

                Map<String, Object> dInfo = new HashMap<>();
                dInfo.put("userId", d.getUserId());
                dInfo.put("name", d.getName());
                dInfo.put("headPicFileName", d.getHeadPicFileName());
                Doctor doctor = d.getDoctor();
                dInfo.put("departments", doctor.getDepartments());
                dInfo.put("cureNum", doctor.getCureNum());
                dInfo.put("title", doctor.getTitle());
                String goodRate = evaluationService.getGoodRate(d.getUserId());
                dInfo.put("goodRate", "暂无评价".equals(goodRate) ? "暂无" : goodRate);
                dInfo.put("hospital", doctor.getHospital());
                String dateStr = DateUtil.formatDate2Str(oi.getCreateTime()) +
                        " - " +
                        DateUtil.getMinuteDateByLong(oi.getCreateTime() + DateUtil.minute30millSeconds);
                dInfo.put("dateStr", dateStr);

                List<String> expIds = doctor.getExpertise();
                StringBuilder sb = new StringBuilder();
                if (!Util.isNullOrEmpty(expIds)) {
                    List<DiseaseTypeVO> retData = baseDataService.getDiseaseType(expIds);
                    if (!Util.isNullOrEmpty(retData)) {
                        for (DiseaseTypeVO diseaseTypeVO : retData) {
                            sb.append(diseaseTypeVO.getName()).append(",");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                    }
                }
                dInfo.put("skill", sb.toString());
                map.put("patient", pInfo);
                map.put("doctor", dInfo);
            }
            map.put("price", o.getPrice());
        }
        return map;
    }

    @Override
    public PreOrderVO processCreateOrder(OrderParam param) throws HttpApiException {
        String tag = "processCreateOrder";
        /**
         * 1、生成订单
         * 2、生成会话
         * 3、发送通知
         */
        /********************* 待废弃 ************************/
        //如果传入了illCaseInfoId,则病情描述、患者id，患者电话都从数据库中取。
        if (StringUtils.isNotBlank(param.getIllCaseInfoId())) {
            //从t_ill_case_info表取医生id和患者id
            IllCaseInfo illCaseInfo = illCaseDao.getIllCase(param.getIllCaseInfoId());
            param.setPatientId(illCaseInfo.getPatientId());
            if (StringUtils.equals(param.getDiseaseDesc(), illCaseInfo.getMainCase())) {
                param.setDiseaseDesc(illCaseInfo.getMainCase());
            }
            IllCasePatientInfo illCasePatientInfo = illCaseDao.getIllCasePatient(param.getIllCaseInfoId());
            param.setTelephone(illCasePatientInfo.getTelephone());
        }
        /********************* 待废弃 ************************/

        // 检查是否有正在进行的订单，如有，直接返回
        PreOrderVO vo = this.getOngoingCareOrderByPatient(param);
        logger.info("{}. vo={}", tag, vo);
        if (null != vo) {
            return vo;
        }

        /**处理用药**/
        setParamDrug(param);
        Order o = createOrderVersion2(param);
        logger.info("{}. o={}", tag, o);

        OrderSession os = createOrderSession(param, o);
        logger.info("{}. os={}", tag, os);

        /********************订单创建插入病程 start*************************/
        param.setGid(os.getMsgGroupId());
        param.setOrderStatus(o.getOrderStatus());
        param.setPackType(o.getPackType());
        addHistoryRecord(param, o);
        /********************订单创建插入病程 end*************************/
        if (o.getPackType() == PackType.careTemplate.getIndex() &&
                o.getOrderStatus() == OrderStatus.已支付.getIndex() &&
                StringUtils.isNotBlank(param.getDiseaseDesc())) {
            Pack carePack = packService.getPack(param.getPackId());
            if (carePack.getReplyCount() != null && carePack.getReplyCount() > 0)
                sendFreeMessage(os.getMsgGroupId());
        }
        /**
         * 患者添加疾病标签
         */
        Integer userId = ReqUtil.instance.getUserId();
        if (StringUtil.isNotEmpty(param.getDiseaseID())) {
            iDiseaseLaberService.addLaberByTreat(userId, param.getDiseaseID());
        }
        if (null != param.getDiseaseId()) {
            iDiseaseLaberService.addLaberByTreat(userId, param.getDiseaseId() + "");
        }
        if (!CollectionUtils.isEmpty(param.getDiseaseIds())) {
            for (String diseaseId : param.getDiseaseIds()) {
                iDiseaseLaberService.addLaberByTreat(userId, diseaseId);
            }
        }

        /**返回值处理**/
        vo = createPreOrderVO(o, os, true);
        logger.info("{}. 创建订单后的异步任务 ... vo={}", tag, vo);
        // 创建订单后的异步任务
        OrderNotify.sendMessageAndNotify(vo.getOrderId(), ReqUtil.isFromWechat());

        //生成对应待处理状态数据PendingOrderStatus
        createPendingOrderStatus(o);
        return vo;
    }

    private PreOrderVO createPreOrderVO(Order o, OrderSession os, boolean ifNewOrder) {
        PreOrderVO vo = new PreOrderVO();
        vo.setOrderId(o.getId());
        vo.setOrderNo(o.getOrderNo());
        vo.setOrderStatus(o.getOrderStatus());
        if (os != null)
            vo.setGid(os.getMsgGroupId());
        vo.setIfNewOrder(ifNewOrder);
        vo.setPatientId(o.getPatientId());
        return vo;
    }

    private PreOrderVO createPreOrderVO(OrderVO o, OrderSession os, boolean ifNewOrder) {
        PreOrderVO vo = new PreOrderVO();
        vo.setOrderId(o.getOrderId());
        vo.setOrderNo(o.getOrderNo());
        vo.setOrderStatus(o.getOrderStatus());
        if (os != null)
            vo.setGid(os.getMsgGroupId());
        vo.setIfNewOrder(ifNewOrder);
        vo.setPatientId(o.getPatientId());
        return vo;
    }

    @Override
    public void wrapPatients(List<PreOrderVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        Set<Integer> patientIdSet = new HashSet<Integer>(list.size());
        for (PreOrderVO vo : list) {
            if (null != vo.getPatientId()) {
                patientIdSet.add(vo.getPatientId());
            }
        }

        if (CollectionUtils.isEmpty(patientIdSet)) {
            return;
        }

        List<Patient> patients = this.patientService.findByIds(new ArrayList<Integer>(patientIdSet));
        if (CollectionUtils.isEmpty(patients)) {
            return;
        }

        for (PreOrderVO vo : list) {
            for (Patient patient : patients) {
                if (patient.getId().equals(vo.getPatientId())) {
                    vo.setPatient(patient);
                    break;
                }
            }
        }
    }

    public PreOrderVO getOngoingCareOrderByPatient(OrderParam param) {
        return getOngoingCareOrderByPatient(param.getPatientId(), param.getPackId());
    }

    public PreOrderVO getOngoingCareOrderByPatient(Integer patientId, Integer packId) {
        String tag = "getOngoingCareOrderByPatient";
        if (logger.isInfoEnabled()) {
            logger.info("{}. patientId={}, packId={}", tag, patientId, packId);
        }

        if (null == patientId || null == packId) {
            if (logger.isInfoEnabled()) {
                logger.info("{}. 参数有误，返回.", tag);
            }
            return null;
        }

        Pack pack = packService.getPack(packId);
        OrderVO orderVO = this.getOngoingCareOrderByPatient(patientId, pack);
        if (logger.isInfoEnabled()) {
            logger.info("{}. orderVO={}", tag, orderVO);
        }

//        if (OrderType.care.getIndex() != orderVO.getOrderType()) {
//            if (logger.isInfoEnabled()) {
//                logger.info("{}. 非关怀项订单类型，返回. {}", tag, orderVO.getOrderType());
//            }
//            return null;
//        }

        if (null != orderVO) {
            OrderSession orderSession = orderSessionService.findOneByOrderId(orderVO.getOrderId());

            PreOrderVO vo = createPreOrderVO(orderVO, orderSession, false);
            return vo;
        }

        return null;
    }

    /**
     * 查看患者是否有正在进行的某个关怀计划套餐
     *
     * @param patientId
     * @param pack
     * @return
     */
    private OrderVO getOngoingCareOrderByPatient(Integer patientId, Pack pack) {
        String tag = "getOngoingCareOrderByPatient";

        if (pack.getPackType().intValue() != PackEnum.PackType.careTemplate.getIndex()) {
            if (logger.isInfoEnabled()) {
                logger.info("{}. 非关怀项套餐类型，返回. {}", tag, pack.getPackType());
            }
            return null;
        }

        Map<String, Object> sqlParam = new HashMap<String, Object>();
        sqlParam.put("patientId", patientId);
        sqlParam.put("packId", pack.getId());
        sqlParam.put("packType", pack.getPackType());
        OrderVO orderVO = this.orderMapper.getOngoingCareOrderByPatient(sqlParam);
        return orderVO;
    }


    private OrderSession createOrderSession(OrderParam param, Order order) throws HttpApiException {
        /**
         * 生成 医患 、 医助 、 患助 三个会话
         * 去掉 医助、患助 会话生成（不在此生成）
         */
        OrderSession orderSession = new OrderSession();

        CreateGroupRequestMessage createGroupParam = new CreateGroupRequestMessage();
        Map<String, Object> groupParam = new HashMap<String, Object>();
        Patient patient = patientMapper.selectByPrimaryKey(order.getPatientId());
        groupParam.put("patientName", patient.getUserName());
        groupParam.put("patientAge", patient.getAgeStr());
        groupParam.put("patientSex", patient.getSex() != null ? BusinessUtil.getSexName(Integer.valueOf(patient.getSex())) : "");
        groupParam.put("patientArea", patient.getArea());
        groupParam.put("orderId", order.getId());
        groupParam.put("orderType", order.getOrderType());
        if (PackType.message.getIndex() == order.getPackType()) {
            groupParam.put("packType", 2);
        } else if (PackType.phone.getIndex() == order.getPackType()) {
            groupParam.put("packType", 3);
        } else {
            groupParam.put("packType", order.getPackType());
        }
        groupParam.put("price", order.getPrice());
        createGroupParam.setParam(groupParam);
        createGroupParam.setCreateNew(true);
        createGroupParam.setBizStatus(String.valueOf(order.getOrderStatus()));
        createGroupParam.setSendRemind(false);

        Map<String, Object> noStatusGroupParam = new HashMap<String, Object>();
        noStatusGroupParam.put("orderId", order.getId());


        int oneOrMore;
        String gtype, gname, fromUserId, toUserId, toUserIds1 = null, toUserIds2 = null;
        if (order.getOrderType() == OrderEnum.OrderType.care.getIndex()) {
            OrderDoctorExample orderDoctorExample = new OrderDoctorExample();
            orderDoctorExample.createCriteria().andOrderIdEqualTo(order.getId());
            List<String> docIds = new ArrayList<>();
            for (OrderDoctor orderDoctor : orderDoctorMapper.selectByExample(orderDoctorExample)) {
                docIds.add(orderDoctor.getDoctorId() + "");
            }
            String userIdTemp = order.getUserId() + "";
            docIds.add(userIdTemp);
            toUserIds1 = OrderSession.appendStringUserId(docIds);
            docIds.remove(userIdTemp);
            docIds.add(order.getAssistantId() + "");
            toUserIds2 = OrderSession.appendStringUserId(docIds);

        }

        {

            //医患会话组
            if (order.getOrderType() == OrderEnum.OrderType.outPatient.getIndex()) {
                gtype = RelationTypeEnum.DOC_OUTPATIENT.getValue();
            } else {
                gtype = RelationTypeEnum.DOC_PATIENT.getValue();
            }

            fromUserId = String.valueOf(order.getDoctorId());
            if (order.getOrderType() == OrderEnum.OrderType.care.getIndex()) {
                User user = userManagerImpl.getUser(order.getDoctorId());
                oneOrMore = 2;
                gname = user.getName() + "专家组";
                toUserId = toUserIds1;
            } else {
                oneOrMore = 1;
                gname = null;
                toUserId = String.valueOf(order.getUserId());
            }
            GroupInfo groupInfo = invokeIMCreateSession(createGroupParam, oneOrMore, gtype, gname, fromUserId, toUserId);
            orderSession.setMsgGroupId(groupInfo.getGid());
        }
        /**
         * 健康关怀临时决定不需要医助 ， 助患会话
         */

        /**
         * 去掉 2017-1-12 11:33:12
         */
        /*if(order.getOrderType() == OrderEnum.OrderType.order.getIndex()) {
            {
                //医助会话组
                gtype = RelationTypeEnum.DOC_ASSISTANT.getValue();
                fromUserId = String.valueOf(order.getDoctorId());
                if(order.getOrderType()==OrderEnum.OrderType.care.getIndex()) {
                    User user = userManagerImpl.getUser(order.getDoctorId());
                    oneOrMore = 2 ; gname = user.getName()+"专家组";
                    toUserId = toUserIds2;
                } else {
                    oneOrMore = 1 ; gname = null;
                    toUserId = String.valueOf(order.getAssistantId());
                }
                GroupInfo groupInfo = invokeIMCreateSession(createGroupParam , oneOrMore,gtype ,  gname , fromUserId , toUserId);
                orderSession.setAssistantDoctorGroupId(groupInfo.getGid());
            }

            //患助没有业务状态
            createGroupParam.setParam(noStatusGroupParam);
            createGroupParam.setBizStatus(null);
            {
                //助患会话组
                gtype = RelationTypeEnum.PATIENT_ASST.getValue();
                fromUserId = String.valueOf(order.getAssistantId());
                oneOrMore = 1 ; gname = null;
                toUserId = String.valueOf(order.getUserId());
                GroupInfo groupInfo = invokeIMCreateSession(createGroupParam , oneOrMore,gtype ,  gname , fromUserId , toUserId);
                orderSession.setAssistantPatientGroupId(groupInfo.getGid());
            }
        }*/

        orderSession.setCreateTime(System.currentTimeMillis());
        orderSession.setOrderId(order.getId());

        orderSession.setLastModifyTime(System.currentTimeMillis());
        /**
         * 图文咨询消息条数限制
         */
        if (order.getPackType() == PackType.message.getIndex()) {
//            Pack p = packService.getPack(param.getPackId());
            orderSession.setTotalReplyCount(SysConstants.DEFAULT_MESSAGE_REPLY_COUNT);
            orderSession.setReplidCount(0);
        }
        orderSessionService.save(orderSession);
        if (order.getPackType() == PackType.careTemplate.getIndex() &&
                order.getOrderStatus() == OrderStatus.已支付.getIndex()) {
            /**
             * add at 2016年12月8日10:24:20
             * 只是针对创建健康关怀
             * 创建orderSessionContainer po
             */
            Pack p = packService.getPack(param.getPackId());
            OrderSessionContainer po = new OrderSessionContainer();
            po.setDoctorId(order.getDoctorId());
            po.setUserId(order.getUserId());
            po.setPatientId(order.getPatientId());
            po.setOrderSessionId(orderSession.getId());
            po.setOrderId(order.getId());
            po.setPackType(order.getPackType());
            po.setSessionType(OrderSessionCategory.care.getIndex());
            po.setStatus(OrderStatus.已支付.getIndex());
            po.setMsgGroupId(orderSession.getMsgGroupId());
            int replyCount = p.getReplyCount() == null ? 0 : p.getReplyCount();
            po.setTotalReplyCount(replyCount);
            po.setReplidCount(0);
            orderSessionContainerDao.insert(po);
            param.setOrderStatus(order.getOrderStatus());
            param.setPackType(order.getPackType());
            param.setGid(orderSession.getMsgGroupId());
            param.setUserId(ReqUtil.instance.getUserId());
            param.setOrderId(order.getId());
            sendNewOrderIllCard(param);
        }
        return orderSession;
    }

    private GroupInfo invokeIMCreateSession(CreateGroupRequestMessage createGroupParam,
                                            int oneOrMore, String gtype, String gname, String fromUserId, String toUserIds) {
        createGroupParam.setType(oneOrMore); //1:单人，2：多人
        createGroupParam.setGtype(gtype);//
        createGroupParam.setFromUserId(fromUserId);
        createGroupParam.setToUserId(toUserIds);
        createGroupParam.setGname(gname);
        GroupInfo groupInfo = (GroupInfo) imsgService.createGroup(createGroupParam);
        return groupInfo;
    }

    private Order createOrderVersion2(OrderParam param) {
        /**
         * 1、验证参数
         * 2、初始化订单数据，插入order到mysql
         * 3、 多线程执行 病历病情 、 医患关系 、 分成 ，主线程等待
         */
        Pack p = null;
        if (Objects.nonNull(param.getPackId())) {
            p = packService.getPack(param.getPackId());
        }
        validateCreateOrderParams(param, p);

        Order o = setOrderAttribute(param, p);

        setOrderOtherData(param, p, o);

        return o;
    }

    private void setOrderOtherData(OrderParam param, Pack pack, Order order) {

        class Task implements Runnable {

            private int type;
            private CountDownLatch threadsSignal;

            public Task(CountDownLatch threadsSignal, int type) {
                this.type = type;
                this.threadsSignal = threadsSignal;
            }

            @Override
            public void run() {
                logger.info(Thread.currentThread().getName() + " , 类型 type =" + type + "开始...");
                switch (this.type) {
                    case 1:
                        /**
                         *  异步定时任务 待修改
                         */
                        try {
//                          addJesQueTask(order);
                            if ((OrderType.outPatient.getIndex() == order.getOrderType())) {
                                if (OrderStatus.待支付.getIndex() == order.getOrderStatus()) {
                                    JobTaskUtil.cancelNoPayOrder(order.getId());
                                } else if (OrderStatus.已支付.getIndex() == order.getOrderStatus()) {
                                    JobTaskUtil.cancelTheNoPrepareOrder(order.getId());
                                }
                            } else if ((OrderType.care.getIndex() == order.getOrderType()) && (OrderStatus.已支付.getIndex() == order.getOrderStatus())) {
                                // 患者新增已支付健康关怀订单创建48小时取消的定时任务
    //                            JobTaskUtil.cancelPaidOrder(order.getId(), JobTaskUtil.paidTime);
                            }

                            /**
                             * 门诊订单处理
                             */
                            if ((order.getOrderType() == OrderEnum.OrderType.outPatient.getIndex())
                                    && (order.getOrderStatus() == OrderEnum.OrderStatus.已支付.getIndex())) {
                                String wkey = RedisUtil.generateKey(RedisUtil.WAITING_QUEUE, String.valueOf(param.getDoctorId()));
                                RedisUtil.rpush(wkey, order.getId().toString());
                            }

                            /**
                             * 医生就诊量++
                             * 集团就诊量++
                             */
                            doctorService.updateCureNum(order.getDoctorId());
                        // 根据医生查找医生集团 ,医生集团就论人数++
                            if (StringUtils.isNoneBlank(order.getGroupId())) {
                                groupService.increaseCureNum(order.getGroupId());
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                        break;
                    case 2:
                        /**
                         * 医患好友关系
                         */
                        DoctorPatient doctorPatient = null;
                        //doctorPatient = (DoctorPatient)friendsManager.getFriend( order.getDoctorId(),order.getUserId());
                        doctorPatient = tagDao.findByDoctorIdAndPatientId(order.getDoctorId(), order.getPatientId());
                        if (doctorPatient == null) {
                            //baseUserService.setDoctorPatient( order.getDoctorId(),order.getUserId());
                            //2016-12-12医患关系的变化
                            baseUserService.addDoctorPatient(order.getDoctorId(), order.getPatientId(), order.getUserId());
                            // 关注医生公众号
                            try {
                                pubGroupService.addSubUser(param.getDoctorId(), order.getUserId());
                            } catch (HttpApiException e) {
                                logger.error(e.getMessage(), e);
                            }
                            businessServiceMsg.sendEventFriendChange(EventEnum.ADD_FRIEND, order.getDoctorId().toString(), order.getUserId().toString());
                        }
                        /**
                         * 医患好友关系打标签
                         */
                        relationService.addRelationTag2(PackUtil.convert(order));
                        break;
                    case 3:
                        /**
                         * 电子病历处理
                         */
                        if (param.getPreOrderId() != null) {
                            /**
                             * 生成转诊记录
                             */
                            Order preOrder = orderMapper.getOne(param.getPreOrderId());
                            IllTransferRecord record = new IllTransferRecord();
                            record.setIllCaseInfoId(preOrder.getIllCaseInfoId());
                            record.setPreOrderId(preOrder.getId());
                            record.setTargetOrderId(order.getId());
                            record.setReceiveDoctorId(order.getDoctorId());
                            record.setTransferDoctorId(param.getTransferDoctorId());
                            record.setTransferTime(param.getTransferTime());
                            record.setReceiveTime(System.currentTimeMillis());
                            illCaseDao.saveIllRecord(record);
                            break;
                        }

                        if (StringUtil.isNotEmpty(param.getIllCaseInfoId()) && param.getIsIllCaseCommit()) {
                            break;
                        }

                        //从新生成电子病历
                        int orderType = param.getOrderType() == null ? OrderType.order.getIndex() : param.getOrderType();
                        Integer treateType = null;
                        if (orderType == 1) {
                            int packType = pack.getPackType();
                            if (packType == 1) {
                                treateType = ConsultationEnum.IllCaseTreatType.text.getIndex();
                            } else if (packType == 2) {
                                treateType = ConsultationEnum.IllCaseTreatType.phone.getIndex();
                            }
                        } else if (orderType == 3) {
                            treateType = ConsultationEnum.IllCaseTreatType.outPatient.getIndex();
                        } else if ((orderType == 4) && StringUtils.isNotBlank(param.getDiseaseDesc())) {
                            treateType = ConsultationEnum.IllCaseTreatType.care.getIndex();
                        }
                        if (treateType != null) {
                            IllCaseInfo caseInfo = new IllCaseInfo();
                            caseInfo.setCreateTime(System.currentTimeMillis());
                            caseInfo.setDoctorId(param.getDoctorId());
                            caseInfo.setUserId(param.getUserId());
                            caseInfo.setPatientId(param.getPatientId());
                            caseInfo.setTreateType(treateType);
                            if ((param.getImagePaths() != null) && (param.getImagePaths().length > 0)) {
                                caseInfo.setImageUlrs(Arrays.asList(param.getImagePaths()));
                            }
                            caseInfo.setOrderId(order.getId());
                            caseInfo.setMainCase(param.getDiseaseDesc());
                            caseInfo.setSeeDoctorMsg(param.getSeeDoctorMsg());
                            IllCaseInfo dbinfo = illCaseServer.createIllCaseInfo(caseInfo);
                            if (dbinfo != null) {
                                String illCaseInfoId = dbinfo.getId();
                                order.setIllCaseInfoId(illCaseInfoId);
                                /**
                                 * 电话订单需要同步更多的数据
                                 */
                                if (treateType == ConsultationEnum.IllCaseTreatType.phone.getIndex()) {
                                    com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease disParam =
                                            new com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease();
                                    disParam.setIllCaseInfoId(illCaseInfoId);
                                    disParam.setCureSituation(param.getCureSituation());
                                    disParam.setDiseaseInfo_now(param.getDiseaseInfo_now());
                                    disParam.setDiseaseInfo_old(param.getDiseaseInfo_old());
                                    disParam.setMenstruationdiseaseInfo(param.getMenstruationdiseaseInfo());
                                    disParam.setFamilydiseaseInfo(param.getFamilydiseaseInfo());
                                    illCaseDao.syncPhoneOrderDiseaseToIllCase(disParam);
                                }
                            }
                        }
                        order.setMainCase(param.getDiseaseDesc());
                        break;
                    default:
                        break;
                }
                threadsSignal.countDown();
                logger.info(Thread.currentThread().getName() + " , 类型 type =" + type + "结束.还有" + threadsSignal.getCount() + " 个线程");
            }
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        final CountDownLatch threadSignal = new CountDownLatch(3);
        Runnable task1 = new Task(threadSignal, 1);
        Runnable task2 = new Task(threadSignal, 2);
        Runnable task3 = new Task(threadSignal, 3);

        exec.submit(task1);
        exec.submit(task2);
        exec.submit(task3);
        try {

            /**
             * 计算参与分成的医生和分成比例
             * 该函数会插入orderDoctor数据
             * 主线程在生成会话的时候会从DB中读取该会话数据
             */
            generateOrderDoctor(order);

            //等待所有子线程执行完
            threadSignal.await();
            // 关闭线程池
            exec.shutdown();
            /**
             * 更新订单中的illCaseInfoId
             */
            if (StringUtil.isNotBlank(order.getIllCaseInfoId())) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("orderId", order.getId());
                params.put("illCaseInfoId", order.getIllCaseInfoId());
                orderMapper.updateOrderIllCaseInfoId(params);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(exec) && !exec.isShutdown()) {
                exec.shutdown();
            }
        }

    }

    private void validateCreateOrderParams(OrderParam param, Pack pack) {
        /**非空验证*/
        if (Objects.isNull(ReqUtil.instance.getUser())) {
            throw new ServiceException("请先登录");
        }
        if (Objects.isNull(param.getDoctorId())) {
            throw new ServiceException("请选择医生");
        }
        if (pack != null &&
                pack.getPackType() != PackType.careTemplate.getIndex() &&
                StringUtils.isBlank(param.getDiseaseDesc())
                && !param.getIsIllCaseCommit()
                && param.getPreOrderId() == null) {
            throw new ServiceException("请输入症状描述或选择历史病情");
        }
        if (param.getIsIllCaseCommit()
                && StringUtils.isBlank(param.getIllCaseInfoId())) {
            throw new ServiceException("请输入症状描述");
        }
        if (Objects.isNull(param.getOrderType())) {
            throw new ServiceException("订单类型为空");
        }

        /**套餐验证*/
        if (param.getOrderType().intValue() != OrderEnum.OrderType.outPatient.getIndex()) {
            if (Objects.isNull(param.getPackId())) {
                throw new ServiceException("请选择套餐");
            }
            if (Objects.isNull(pack) ||
                    Objects.isNull(pack.getDoctorId()) ||
                    (pack.getDoctorId().intValue() != param.getDoctorId().intValue())) {
                throw new ServiceException("该医生套餐不存在");
            }
        }
    }

    private Order setOrderAttribute(OrderParam param, Pack pack) {
        Order order = new Order();

        String groupId = param.getGroupId();
        GroupDoctor dg = null;
        if (StringUtils.isBlank(groupId)) {
            // 根据医生查找医生集团
            dg = groupDoctorService.findOneByUserIdAndStatus(param.getDoctorId(), "C");
            if (dg != null) {
                groupId = dg.getGroupId();
            }
        }
        order.setGroupId(groupId);
        order.setOrderStatus(OrderEnum.OrderStatus.待支付.getIndex());
        order.setDoctorId(param.getDoctorId());
        User user = userManagerImpl.getUser(param.getDoctorId());
        order.setAssistantId(user.getDoctor().getAssistantId());
        order.setOrderType(param.getOrderType());
        order.setUserId(ReqUtil.instance.getUserId());
        param.setUserId(order.getUserId());

        if (Objects.nonNull(pack)) {
            order.setPackId(pack.getId());
            order.setPackType(pack.getPackType());
            order.setPrice(pack.getPrice());
            order.setTimeLong(pack.getTimeLimit());

            //健康关怀或随访
            if ((pack.getPackType().intValue() == PackEnum.PackType.careTemplate.getIndex()) ||
                    (pack.getPackType().intValue() == PackEnum.PackType.followTemplate.getIndex())) {
                try {
                    CCarePlan carePlan = carePlanApiClientProxy.copyByPack(pack.getCareTemplateId(), pack.getName(), pack.getDescription(), pack.getPrice().intValue());
                    order.setCareTemplateId(carePlan.getId());

                    order.setRemarks("mark");//暂时用来区分患者主动购买还是医生发送给患者，支付时清空
                    order.setHelpTimes(pack.getHelpTimes());
                    order.setGroupId(pack.getGroupId());
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                    throw new ServiceException("拷贝关怀计划失败，请稍候再试");
                }
            }

            if (Objects.isNull(pack.getPrice()) || (pack.getPrice().intValue() == 0)) {
                order.setOrderStatus(OrderEnum.OrderStatus.已支付.getIndex());
            }

        }

        //门诊
        if (param.getOrderType().intValue() == OrderEnum.OrderType.outPatient.getIndex()) {
            order.setPrice(0L);
            OutpatientVO outpatient = groupDoctorService.getOutpatientInfo(order.getDoctorId());
            if (OnLineState.offLine.getIndex().equals(outpatient.getOnLineState())) {
                throw new ServiceException("该医生没有上线，不能提供在线门诊！");
            }
            //门诊时记录groupId
            order.setGroupId(outpatient.getGroupId());

            // 1、生成门诊订单的时候，根据医生的值班要求时长和在线时间比较，来确定订单价格，如果在线时长已超过值班时长，则取门诊价格，否则价格为0
            // 2、如果订单价格为0，则订单状态设为已支付，否则设为待支付。
            Long task = outpatient.getTaskDuration() == null ? 0L : outpatient.getTaskDuration(); // 在线时长(可设置的)(要完成这个任务才能收费)
            Long duty = 0L;
            if (outpatient.getDutyDuration() != null) {
                duty = outpatient.getDutyDuration(); // 医生值班时长（定时器计算的在线值班时长）
            }

            boolean hasTaskTime = false;
            if (task != 0) {
                hasTaskTime = true; // == true表示设置了要完成任务的值班时长
            }

            if ((hasTaskTime == false) || ((hasTaskTime == true) && (duty >= task))) {
                // 没有设置值班时长 或者 设置了值班时长并且在线时长超过值班时长，才取门诊价格。
                Long price = outpatient.getPrice() == null ? 0L : outpatient.getPrice().longValue();
                order.setPrice(price);
            } else {
                order.setPrice(0L);
            }

            if (order.getPrice() == 0) {
                order.setOrderStatus(OrderEnum.OrderStatus.已支付.getIndex());
            }
            order.setPackId(0);
            order.setPackType(PackType.online.getIndex());
            // 非套餐订单默认为15分钟
            order.setTimeLong(15);
        }

        Order preOrder = null;

        Integer diseaseId = null;
        if (param.getPreOrderId() != null) {
            preOrder = getOne(param.getPreOrderId());
            if (preOrder == null)
                throw new ServiceException("找不到原订单");
            param.setPatientId(preOrder.getPatientId());
            diseaseId = preOrder.getDiseaseId();
        } else {
            diseaseId = addDisease(param);
        }
        order.setDiseaseId(diseaseId);

        order.setCreateTime(System.currentTimeMillis());
        order.setFinishTime(0L);
        order.setOrderNo(nextOrderNo());
        order.setPatientId(param.getPatientId());

        if (param.getOrderStatus() != null) {
            order.setOrderStatus(param.getOrderStatus());
        }

        order.setRefundStatus(OrderEnum.OrderRefundStatus.refundWait.getIndex());

        order.setIllCaseInfoId(param.getIllCaseInfoId());
        if (preOrder != null)
            order.setIllCaseInfoId(preOrder.getIllCaseInfoId());

        if (Objects.nonNull(order.getPackType())
                && (order.getPackType().intValue() == PackType.phone.getIndex())) {
            if (StringUtils.isBlank(param.getExpectAppointmentIds())) {
                order.setExpectAppointmentIds(baseDataDao.getExpectAppointments().get(0).getId());
            } else {
                order.setExpectAppointmentIds(param.getExpectAppointmentIds());
            }
        }

        orderMapper.add(order);
        return order;
    }


    @Override
    public void sendDoctorAssistantOrderNotify(String assistantId, String doctorId, String assistantDoctorGroupId,
                                               OrderNoitfyType orderNoitfyType, Map<String, Object> paramMap) throws HttpApiException {

        if (OrderNoitfyType.neworder == orderNoitfyType) {
            businessServiceMsg.sendNotifyMsgToUser(assistantId, assistantDoctorGroupId, "您有一个新订单，可以联系医生");
            businessServiceMsg.sendNotifyMsgToUser(doctorId, assistantDoctorGroupId, "您有一个新订单，可以联系助手处理");
        }
    }

    @Override
    public void sendPatientAssistantOrderNotify(String userId, String assistantId, String assistantPatientGroupId,
                                                OrderNoitfyType orderNoitfyType, Map<String, Object> paramMap) throws HttpApiException {

        if (OrderNoitfyType.neworder == orderNoitfyType) {
            businessServiceMsg.sendNotifyMsgToUser(userId, assistantPatientGroupId, "您有一个新订单，可以联系医生助手处理");
            businessServiceMsg.sendNotifyMsgToUser(assistantId, assistantPatientGroupId, "您有一个新订单，请及时联系患者");
        }
    }


    //------------------------------------------------医生助手订单功能修改2.0---------------------------------------------
    @Override
    public List<User> queryDoctors(Integer userId) {
        if (userId == null) {
            throw new ServiceException("请登录");
        }
        OrderParam param = new OrderParam();
        param.setAssistantId(userId);
        List<OrderVO> orderList = orderMapper.findOrdersForAssistantAll(param);
        List<Integer> doctorIds = new ArrayList<>();
        for (OrderVO ov : orderList) {
            if (ov.getDoctorId() != null) {
                doctorIds.add(ov.getDoctorId());
            }
        }
        return userManagerImpl.findUsers(doctorIds);
    }

    @Override
    public PageVO queryOrderByConditions(OrderParam param) {
        if (param.getAssistantId() == null) {
            throw new ServiceException("请登录");
        }
        PageVO pageVO = new PageVO();
        if (param.getPendingOrderStatus() == 1) {
            List<Integer> orderIds = pendingOrderStatusDao.queryAllOrderIds();
            param.setOrderIds(orderIds);
        }
        List<OrderVO> orders = orderMapper.findOrdersForAssistant(param);
        Integer countOrders = orderMapper.findOrdersForAssistantCount(param);
        for (OrderVO orderVo : orders) {
            orderVo.setUserVo(findUserVO(orderVo.getUserId()));
            orderVo.setDoctorVo(this.findDoctorVo(orderVo.getDoctorId()));
            orderVo.setOrderSession(orderSessionService.findOneByOrderId(orderVo.getOrderId()));
            PendingOrderStatus pos = pendingOrderStatusDao.queryByOrderId(orderVo.getOrderId());
            if (pos != null) {
                orderVo.setPendingOrderStatus(pos.getOrderStatus());
                orderVo.setPendingOrderWaitType(pos.getOrderWaitType());
                if (pos.getFlagTime() != null) {
                    orderVo.setWaitTime(System.currentTimeMillis() - pos.getFlagTime());
                }
            }
        }

        pageVO.setPageData(orders);
        pageVO.setTotal(Long.valueOf(countOrders));
        pageVO.setPageSize(param.getPageSize());
        pageVO.setPageIndex(param.getPageIndex());
        return pageVO;
    }

    @Override
    public PageVO queryOrderByConditionsForPatient(OrderParam param) {
        if (param.getAssistantId() == null) {
            throw new ServiceException("请登录");
        }
        if (param.getDoctorId() == null) {
            List<User> doctors = this.queryDoctors(param.getAssistantId());
            List<Integer> doctorIds = new ArrayList<>();
            for (User user : doctors) {
                doctorIds.add(user.getUserId());
            }
            param.setDoctorIds(doctorIds);
        }
        List<OrderVO> orders = orderMapper.findOrdersForAssistant(param);
        Integer countOrders = orderMapper.findOrdersForAssistantCount(param);
        for (OrderVO orderVo : orders) {
            orderVo.setDoctorVo(this.findDoctorVo(orderVo.getDoctorId()));
        }

        PageVO pageVO = new PageVO();
        pageVO.setPageData(orders);
        pageVO.setTotal(Long.valueOf(countOrders));
        pageVO.setPageSize(param.getPageSize());
        pageVO.setPageIndex(param.getPageIndex());
        return pageVO;
    }

    @Override
    public PageVO queryOrderByConditionsForDoctor(OrderParam param) {
        if (param.getAssistantId() == null) {
            throw new ServiceException("请登录");
        }
        PageVO pageVO = new PageVO();
        List<OrderVO> orders = orderMapper.findOrdersForAssistant(param);
        Integer countOrders = orderMapper.findOrdersForAssistantCount(param);
        pageVO.setPageData(orders);
        pageVO.setTotal(Long.valueOf(countOrders));
        pageVO.setPageSize(param.getPageSize());
        pageVO.setPageIndex(param.getPageIndex());
        return pageVO;
    }

    @Override
    public OrderVO doctorAssistantQueryOrderById(Integer orderId) {
        OrderVO orderVO = orderMapper.doctorAssistantQueryOrderById(orderId);
        //设置订单待处理状态
        PendingOrderStatus pos = pendingOrderStatusDao.queryByOrderId(orderId);
        if (pos != null) {
            orderVO.setPendingOrderStatus(pos.getOrderStatus());
            orderVO.setPendingOrderWaitType(pos.getOrderWaitType());
            if (pos.getFlagTime() != null) {
                orderVO.setWaitTime(System.currentTimeMillis() - pos.getFlagTime());
            }
        }
        if (orderVO.getOrderType().intValue() == OrderEnum.OrderType.order.getIndex() && orderVO.getPackType().intValue() == PackEnum.PackType.phone.getIndex()) {
            orderVO.setExpectAppointment(baseDataDao.getExpectAppointmentsByIds(orderVO.getExpectAppointment()));
        }
        orderVO.setUserVo(findUserVO(orderVO.getUserId()));
        orderVO.setDoctorVo(this.findDoctorVo(orderVO.getDoctorId()));
        return orderVO;
    }

    @Override
    public void updateOrderAssistantComment(Integer orderId, String assistantComment) {
        //数据库限制的字符长度是500，这里最好用宏定义
        if (StringUtils.isNotBlank(assistantComment) && assistantComment.length() > 500) {
            throw new ServiceException("备注不能超过500字");
        }

        Map<String, Object> sqlParam = new HashMap<String, Object>();
        sqlParam.put("orderId", orderId);
        sqlParam.put("assistantComment", assistantComment);

        orderMapper.updateAssistantComment(sqlParam);
    }

    @Override
    public void setConsultationTime(Integer orderId, Long appointTime) throws HttpApiException {
        Order order = getOne(orderId);
        OrderSession orderSession = orderSessionService.findOneByOrderId(orderId);
        OrderSession record = new OrderSession();
        //record.setOrderId(orderId);
        record.setAppointTime(appointTime);
        record.setId(orderSession.getId());
        orderSessionMapper.updateByPrimaryKeySelective(record);//设置咨询时间
        //系统给医生IM
        MessageVO mv1 = new MessageVO();
        mv1.setType(MsgTypeEnum.TEXT.getValue());
        mv1.setFromUserId(order.getAssistantId().toString());
        mv1.setGid(orderSession.getAssistantDoctorGroupId());
        mv1.setContent("我为您设置了咨询时间：" + DateUtil.formatDate2Str(appointTime) + "，请您尽量在此时间内开始咨询。");
        mv1.setToUserId(order.getDoctorId().toString() + "|" + order.getAssistantId());
        msgService.baseSendMsg(mv1);
        //系统患者发送IM
        MessageVO mv2 = new MessageVO();
        mv2.setType(MsgTypeEnum.TEXT.getValue());
        mv2.setFromUserId(order.getAssistantId().toString());
        mv2.setGid(orderSession.getAssistantPatientGroupId());
        mv2.setContent("我为您设置了咨询时间：" + DateUtil.formatDate2Str(appointTime) + "，医生会在此时间内开始咨询。");
        mv2.setToUserId(order.getUserId().toString() + "|" + order.getAssistantId());
        msgService.baseSendMsg(mv2);
        //系统到了咨询时间给医生助手发送短信
        if (order.getAssistantId() != null) {
            User assistant = userManager.getUser(order.getAssistantId());
            User doctor = userManager.getUser(order.getDoctorId());
            //拉起APP的URL获取
            String url = shortUrlComponent.generateShortUrl(PackConstants.greneartenURL("2", order.getId().toString(), UserEnum.UserType.assistant.getIndex()));
			/*String msg=doctor.getName()+"医生的"+ PackType.getTitle(order.getPackType()) +
					"已到咨询时间还未开始，请情快联系医生开始服务！点击"+url+" 联系处理.";*/
            String msg = baseDataService.toContent("1044", doctor.getName(), PackType.getTitle(order.getPackType()), url);
            JobTaskUtil.doctorAssistantSetAppointTimeSendMsg(assistant.getTelephone(), msg, appointTime);
        }

    }

    @Override
    public void cancelOrder(String msg, Integer orderId, Integer cancelType) throws HttpApiException {
        Order order = getOne(orderId);
        if (order == null) {
            return;
        }
        OrderSession orderSession = orderSessionService.findOneByOrderId(orderId);
        User doctor = userManager.getUser(order.getDoctorId());
        User patient = userManager.getUser(order.getUserId());

        Integer currentUserId = ReqUtil.instance.getUserId();

        if (cancelType == 1) {
            //1.超时患者取消
            //更新order状态和取消原因
            order.setCancelReason(msg);
            order.setCancelFrom(ReqUtil.instance.getUserId());

            cancelOrder(order, currentUserId, orderCancelEnum.manual.getIndex());

            if (orderSession != null) {
                //系统给患者发送通知
                businessServiceMsg.sendNotifyMsgToUser(order.getUserId().toString(), orderSession.getMsgGroupId(), "订单已取消");

                //系统给医生发送IM
                MessageVO mv2 = new MessageVO();
                mv2.setType(MsgTypeEnum.TEXT_IMG.getValue());
                mv2.setFromUserId(SysUserEnum.SYS_001.getUserId());
                mv2.setGid(orderSession.getMsgGroupId());
                mv2.setContent(msg);
                mv2.setToUserId(order.getDoctorId().toString());
                msgService.baseSendMsg(mv2);
            }

            //系统给医生发送短信
            String msgContent = baseDataService.toContent("1043", doctor.getName(), patient.getName(), PackType.getTitle(order.getPackType()), msg, (order.getPrice() / 100) + "", order.getOrderNo().toString());
            mobSmsSdk.send(doctor.getTelephone(), msgContent);

            //http://192.168.3.3:8090/pages/viewpage.action?pageId=1605681 状态的定义 17代表人工取消
            //更新订单助手医生会话状态
//            GroupStateRequestMessage request = new GroupStateRequestMessage();
//            request.setBizStatus(String.valueOf(17));
//            request.setGid(orderSession.getAssistantDoctorGroupId());
//            MsgHelper.updateGroupBizState(request);
        } else if (cancelType == 2) {
            //2.联系医生助手取消
            //患者给医生助手发送IM
            if (order.getAssistantId() != null) {
                AssistantSessionRelation session = getSession(order, doctor, 2);
                if (session != null) {
                    MessageVO mv1 = new MessageVO();
                    mv1.setType(MsgTypeEnum.TEXT_IMG.getValue());
                    mv1.setFromUserId(order.getUserId().toString());
                    mv1.setGid(session.getMsgGroupId());
                    mv1.setContent("非常抱歉我需要取消订单，原因：" + msg);
                    mv1.setToUserId(order.getAssistantId().toString());
                    msgService.baseSendMsg(mv1);
                }
            }
            //患者给医生发送IM
            MessageVO mv2 = new MessageVO();
            mv2.setType(MsgTypeEnum.TEXT_IMG.getValue());
            mv2.setFromUserId(order.getUserId().toString());
            mv2.setGid(orderSession.getMsgGroupId());
            mv2.setContent("非常抱歉我需要取消订单，原因：" + msg);
            mv2.setToUserId(order.getDoctorId().toString());
            msgService.baseSendMsg(mv2);
        } else if (cancelType == 3) {//3.医生助手取消订单
            //更新order状态和取消原因
            order.setCancelReason(msg);
            order.setCancelFrom(ReqUtil.instance.getUserId());

            OrderParam param = new OrderParam();
            param.setOrderId(orderId);
            param.setCancelReason(msg);
            cancelOrder(param, currentUserId, orderCancelEnum.manual.getIndex());

            //医患会话
            if (orderSession != null) {
                businessServiceMsg.sendNotifyMsgToUser(order.getUserId().toString(), orderSession.getMsgGroupId(), "订单已取消");

                //发消息给医生
                List<ImgTextMsg> msgs = new ArrayList<ImgTextMsg>();
                ImgTextMsg imgTextMsg = new ImgTextMsg();
                imgTextMsg.setTitle("助手已取消订单");
                imgTextMsg.setContent("原因：" + msg);
                imgTextMsg.setTime(System.currentTimeMillis());
                imgTextMsg.setStyle(7);
                msgs.add(imgTextMsg);
                businessServiceMsg.sendTextMsgToGidOnToUserId(order.getDoctorId().toString(), orderSession.getMsgGroupId(), msgs, null);
            }
            //发送短信给医生
            String msgContent = baseDataService.toContent("1043", doctor.getName(), patient.getName(), PackType.getTitle(order.getPackType()), msg, (order.getPrice() / 100) + "", order.getOrderNo().toString());
            mobSmsSdk.send(doctor.getTelephone(), msgContent);

            //助手医生会话
            AssistantSessionRelation session = getSession(order, doctor, 1);
            if (session != null) {
                businessServiceMsg.sendNotifyMsgToUser(order.getAssistantId().toString(), session.getMsgGroupId(), "订单已取消");
            }
            //助手患者会话
            session = getSession(order, doctor, 2);
            if (session != null) {
                businessServiceMsg.sendNotifyMsgToUser(order.getAssistantId().toString(), session.getMsgGroupId(), "订单已取消");
            }

        } else {
            throw new ServiceException("参数不正确");
        }
    }

    private AssistantSessionRelation getSession(Order order, User doctor, Integer type) {
        AssistantSessionRelation param = new AssistantSessionRelation();
        param.setType(type);
        if (type == 1) {
            param.setAssistantId(doctor.getDoctor().getAssistantId());
            param.setDoctorId(order.getDoctorId());
        } else if (type == 2) {
            param.setAssistantId(doctor.getDoctor().getAssistantId());
            param.setPatientId(order.getPatientId());
            param.setUserId(order.getUserId());
            param.setDoctorId(order.getDoctorId());
        } else {
            return null;
        }
        List<AssistantSessionRelation> list = assistantSessionRelationDao.queryByConditions(param);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    //生成对应待处理状态数据PendingOrderStatus,,免费的订单
    public void createPendingOrderStatus(Order order) {
        if (order.getOrderStatus() != OrderStatus.已支付.getIndex()) {
            return;
        }

        //电话咨询订单
        if (order.getPackType() == PackType.phone.getIndex()) {
            PendingOrderStatus pendingOrderStatus = new PendingOrderStatus();
            pendingOrderStatus.setOrderId(order.getId());
            pendingOrderStatus.setOrderStatus(1);//订单一创建就是待处理状态
            pendingOrderStatus.setFlagTime(System.currentTimeMillis());//设置开始等待时间
            pendingOrderStatus.setOrderWaitType(2);//2.电话订单-医生未开始
            pendingOrderStatusDao.add(pendingOrderStatus);
        }

        //图文咨询订单
        if (order.getPackType() == PackType.message.getIndex()) {
            PendingOrderStatus pendingOrderStatus = new PendingOrderStatus();
            pendingOrderStatus.setOrderId(order.getId());
            pendingOrderStatus.setOrderStatus(0);//订单不是待处理状态，患者发消息置为待处理状态
            pendingOrderStatus.setOrderWaitType(1);//1.图文订单-医生未回复
            pendingOrderStatusDao.add(pendingOrderStatus);
        }

        //健康关怀订单
        if (order.getPackType() == PackType.careTemplate.getIndex()) {
            PendingOrderStatus pendingOrderStatus = new PendingOrderStatus();
            pendingOrderStatus.setOrderId(order.getId());
            pendingOrderStatus.setOrderStatus(0);//订单不是待处理状态，有最新待办事项则置为待处理状态
            pendingOrderStatus.setOrderWaitType(4);//4.健康关怀-患者未答题
            pendingOrderStatusDao.add(pendingOrderStatus);
        }
    }


    //------------------------------------------------医生助手订单功能修改2.0---------------------------------------------


    @Override
    public PageVO queryOrderByConditions(OrderParam param, UserSession session) {

        if (session == null) {
            throw new ServiceException("请登录");
        }
        PageVO pageVO = new PageVO();

        if (param.getTelephone() != null) {//查询条件为电话号码
            List<Integer> userIdList = new ArrayList<>();
            List<String> telephoneList = new ArrayList<>();
            telephoneList.add(param.getTelephone());
            List<User> userList = userManagerImpl.findByTelephone(telephoneList);
            for (User user : userList) {
                userIdList.add(user.getUserId());
            }
            param.setUserIds(userIdList);
        }

        if (param.getDoctorName() != null) {//查询条件为医生姓名
            List<Integer> userIdList = new ArrayList<>();
            List<User> userList = userManagerImpl.findByNameKeyWordAndUserType(param.getDoctorName(), UserEnum.UserType.doctor.getIndex());
            for (User user : userList) {
                userIdList.add(user.getUserId());
            }
            param.setUserIds(userIdList);
        }

        if (param.getUserName() != null) {//查询条件为用户姓名
            List<Integer> userIdList = new ArrayList<>();
            List<User> userList = userManagerImpl.findByNameKeyWordAndUserType(param.getUserName(), UserEnum.UserType.patient.getIndex());
            for (User user : userList) {
                userIdList.add(user.getUserId());
            }
            param.setUserIds(userIdList);
        }

        List<OrderVO> orderList = null;
        Integer count = 0;
        if ((param.getUserIds() == null) || (param.getUserIds().size() > 0)) {
            orderList = orderMapper.queryOrders(param);
            count = orderMapper.queryOrdersCount(param);

            for (OrderVO orderVo : orderList) {
                if (orderVo.getGroupId() != null) {
                    Group group = groupService.getGroupById(orderVo.getGroupId());
                    if (group != null) {
                        orderVo.setGroupName(group.getName());
                    }
                }
                UserVO uv = findUserVO(orderVo.getUserId());
                orderVo.setUserName(uv.getUserName());
                DoctorVO dv = findDoctorVo(orderVo.getDoctorId());
                orderVo.setDoctorName(dv.getDoctorName());
            }
        }
        pageVO.setPageData(orderList);
        pageVO.setTotal(count.longValue());
        pageVO.setPageSize(param.getPageSize());
        pageVO.setPageIndex(param.getPageIndex());
        return pageVO;

    }

    @Override
    public List<PackDoctorVO> getPackDoctorListByOrder(Integer orderId) {
        List<OrderDoctor> orderDoctorList = orderDoctorService.findOrderDoctors(orderId);

        List<PackDoctorVO> packDoctorList = new ArrayList<PackDoctorVO>(orderDoctorList.size());
        for (OrderDoctor orderDoctor : orderDoctorList) {
            Integer doctorId = orderDoctor.getDoctorId();
            Pack pack = new Pack();
            pack.setDoctorId(doctorId);
            pack.setStatus(PackEnum.PackStatus.open.getIndex());
            List<PackVO> packList = packService.queryPack12(pack);
            if (CollectionUtils.isEmpty(packList)) {
                continue;
            }

            User doctorUser = userManager.getUser(doctorId);

            PackDoctorVO packDoctor = new PackDoctorVO(doctorUser, packList);
            packDoctorList.add(packDoctor);
        }
        return packDoctorList;
    }

    @Override
    public Map<String, Object> getOldSession(Integer doctorId, Integer userId, Integer patientId, Integer packId, Integer careOrderId, Integer packTypeTemp, Integer orderId) {
        if (careOrderId != null) {
            Order o = orderMapper.getOne(careOrderId);
            if (o == null || o.getPackType() != PackType.careTemplate.getIndex())
                throw new ServiceException("careOrderId=" + careOrderId + "不是健康关怀订单");
        }
        Map<String, Object> map = new HashMap<>();
        Pack p = new Pack();
        //判断套餐的状态
        if (packTypeTemp == null || packTypeTemp != PackType.online.getIndex()) {
            p = packService.getPack(packId);
            if (orderId == null) {
                if (orderId == null) {
                    if (p == null || (p.getStatus() == PackEnum.PackStatus.close.getIndex()))
                        throw new ServiceException("套餐id{" + packId + "}错误，或未开通");
                }
            }
        } else if (packTypeTemp == PackType.online.getIndex()) {
            p.setPackType(PackType.online.getIndex());
        }

        List<IllHistoryInfo> illHistoryInfos = illHistoryInfoDao.getInfosByDoctorIdAndPatientId(null, patientId);
        List<IllHistoryInfo> illHistoryInfosMy = illHistoryInfoDao.getInfosByDoctorIdAndPatientId(doctorId, patientId);
        if (illHistoryInfos == null || illHistoryInfos.size() == 0) {
            map.put("status", 1);
        } else if (illHistoryInfosMy != null && illHistoryInfosMy.size() == 1) {
            map.put("status", 2);
            map.put("illHistoryInfoId", illHistoryInfosMy.get(0).getId());
        } else {
            map.put("status", 3);
        }
        //判断时候可以新建病历
        if (illHistoryInfosMy == null || illHistoryInfosMy.size() == 0) {
            map.put("newIllHistory", 1);
        } else {
            map.put("newIllHistory", 0);
        }

        if (orderId != null) {
            Order order = orderMapper.getOne(orderId);
            if (order == null) {
                throw new ServiceException("订单异常");
            }
            if (order.getOldSession() == null) {
                map.put("hasSession", 0);
            } else if (order.getOldSession()) {
                map.put("hasSession", 1);
            } else {
                map.put("hasSession", 2);
            }

        }

        //List<OrderSessionContainer> osList = orderSessionContainerDao.findByUser(doctorId,userId,patientId);
        //if(osList != null && osList.size() > 0)
        //    //表示已经有图文/电话/健康关怀订单
        //    map.put("hasOrder",true);
        //else{
        //	map.put("hasOrder",false);
        //	return map;
        //}
        Integer packType = p.getPackType();
        if (packType != PackType.careTemplate.getIndex()) {
            //如果当前下的是图文/电话 需要进一步判断是否能下订单
            int sessionCategory = OrderSessionCategory.text_tel_checkIn_integral.getIndex();
            if (careOrderId != null) {
                sessionCategory = OrderSessionCategory.care_in_text_tel_integral.getIndex();
            }
            //门诊订单
            if (packType == PackType.online.getIndex()) {
                sessionCategory = OrderSessionCategory.online.getIndex();
            }
            OrderSessionContainer osc = orderSessionContainerDao.findTextPhone(doctorId, userId, patientId, careOrderId, sessionCategory);
            if (osc == null)
                return map;
            Order o = orderMapper.getOne(osc.getOrderId());
            if (o == null)
                return map;//防止错误数据
            if (o.getOrderStatus() != OrderStatus.已支付.getIndex()) {
                orderSessionContainerDao.updateStatus(osc.getId(), o.getOrderStatus());
                return map;
            }
            int dbPackType = osc.getPackType();
            if (packType == PackType.phone.getIndex() && dbPackType == PackType.phone.getIndex() ||
                    packType == PackType.message.getIndex() && dbPackType == PackType.message.getIndex() ||
                    packType == PackType.integral.getIndex() && dbPackType == PackType.integral.getIndex() ||
                    packType == PackType.message.getIndex() && dbPackType == PackType.phone.getIndex() ||
                    packType == PackType.integral.getIndex() && dbPackType == PackType.phone.getIndex() ||
                    packType == PackType.integral.getIndex() && dbPackType == PackType.message.getIndex() ||
                    packType == PackType.message.getIndex() && dbPackType == PackType.integral.getIndex() ||
                    packType == PackType.online.getIndex() && dbPackType == PackType.online.getIndex()
                    ) {
                OrderSession os = orderSessionMapper.selectByPrimaryKey(osc.getOrderSessionId());
                map.put("msgGroupId", os.getMsgGroupId());
                map.put("packType", packType);
            }
        }
        return map;
    }

    @Autowired
    protected DrugApiClientProxy drugApiClientProxy;

    @Override
    public OrderSimpleInfoVO getOrderSimpleInfo(Integer orderId) throws HttpApiException {

        logger.info(ReqUtil.instance.getUserId() + " get order detail info. OrderId:" + orderId);

        if (null == orderId) {
            throw new ServiceException("订单Id不能为空");
        }

        OrderSimpleInfoVO vo = new OrderSimpleInfoVO();
        vo.setId(orderId);

        //获取套餐类型

        Order order = orderMapper.getOne(orderId);

        if (null == order) {
            logger.info("Can't find orderId:" + orderId);
            throw new ServiceException("订单号无效");
        }

        vo.setOrderType(order.getOrderType());
        vo.setOrderStatus(order.getOrderStatus());
        Integer packType = order.getPackType();

        //查找病程信息
        IllHistoryRecord illHistoryRecord = null;

        //图文 电话 门诊 积分 查询IllHistoryRecord.RecordOrder
        if (packType == PackEnum.PackType.message.getIndex() || packType == PackEnum.PackType.phone.getIndex() ||
                packType == PackEnum.PackType.online.getIndex() || packType == PackEnum.PackType.integral.getIndex()) {

            illHistoryRecord = illHistoryRecordDao.findByOrderIdAndType(orderId, IllHistoryEnum.IllHistoryRecordType.order.getIndex());
            if (illHistoryRecord == null) {
                logger.info("Can't find illHistoryRecord for orderId:" + orderId);
                throw new ServiceException("找不到病程信息");
            }

            RecordOrder recordOrder = illHistoryRecord.getRecordOrder();
            //所患疾病
            String diseaseId = recordOrder.getDiseaseId();
            if (StringUtil.isNotEmpty(diseaseId)) {
                DiseaseType diseaseType = diseaseTypeRepository.findByDiseaseId(diseaseId);
                if (null != diseaseType) {
                    vo.setDisease(diseaseType.getName());
                }
            }

            List<String> pics = new ArrayList<>();
            pics = recordOrder.getPics();
            vo.setPics(pics);

            vo.setDiseaseDuration(recordOrder.getDiseaseDuration());
            vo.setDiseaseDesc(recordOrder.getDiseaseDesc());
            vo.setHopeHelp(recordOrder.getHopeHelp());

            List<String> drugNames = Lists.newArrayList();
            List<String> drugGoodsIds = recordOrder.getDrugGoodsIds();
            if (!CollectionUtils.isEmpty(drugGoodsIds)) {
                for (String drug : drugGoodsIds) {
                    CGoodsView goodsView = drugApiClientProxy.getDrugUsage(drug);

                    if (goodsView != null) {
                        drugNames.add(goodsView.getTitle());
                    }
                }
            }
            vo.setDrugNames(drugNames);
            vo.setDrugPics(recordOrder.getDrugPicUrls());
            vo.setDrugInfo(recordOrder.getDrugCase());

        } else if (packType == PackEnum.PackType.careTemplate.getIndex()) {

            illHistoryRecord = illHistoryRecordDao.findByOrderIdAndType(orderId, IllHistoryEnum.IllHistoryRecordType.care.getIndex());
            if (illHistoryRecord == null) {
                logger.info("Can't find illHistoryRecord for orderId:" + orderId);
                throw new ServiceException("找不到病程信息");
            }

            RecordCare care = illHistoryRecord.getRecordCare();
            vo.setDiseaseDuration(care.getDiseaseDuration());
            vo.setDiseaseDesc(care.getDiseaseDesc());
            vo.setHopeHelp(care.getHopeHelp());
            vo.setPics(care.getPics());

            //所患疾病
            String diseaseId = care.getDiseaseId();
            if (StringUtil.isNotEmpty(diseaseId)) {
                DiseaseType diseaseType = diseaseTypeRepository.findByDiseaseId(diseaseId);
                if (null != diseaseType) {
                    vo.setDisease(diseaseType.getName());
                }
            }

            List<String> drugNames = Lists.newArrayList();
            List<String> drugGoodsIds = care.getDrugGoodsIds();
            if (!CollectionUtils.isEmpty(drugGoodsIds)) {
                for (String drug : drugGoodsIds) {
                    CGoodsView goodsView = drugApiClientProxy.getDrugUsage(drug);
                    if (goodsView != null) {
                        drugNames.add(goodsView.getTitle());
                    }
                }
            }
            vo.setDrugNames(drugNames);
            vo.setDrugPics(care.getDrugPicUrls());
            vo.setDrugInfo(care.getDrugCase());

        } else if (packType == PackEnum.PackType.consultation.getIndex()) {

            illHistoryRecord = illHistoryRecordDao.findByOrderIdAndType(orderId, IllHistoryEnum.IllHistoryRecordType.consultation.getIndex());
            if (illHistoryRecord == null) {
                logger.info("Can't find illHistoryRecord for orderId:" + orderId);
                throw new ServiceException("找不到病程信息");
            }
            RecordConsultiation consultiation = illHistoryRecord.getRecordConsultiation();
            vo.setPics(consultiation.getPics());
        }

        //用药建议跟检查建议
        List<CureRecord> cureRecords = cureRecordServiceImpl.findByOrderId(orderId);
        List<CRecipeDetailView> views = Lists.newArrayList();
        List<String> checkSuggestNames = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(cureRecords)) {
            for (CureRecord cureRecord : cureRecords) {
                CRecipeView view = cureRecord.getRecipeView();
                if (null != view) {
                    views.addAll(view.getRecipeDetailList());
                }

                List<CheckSuggest> checkSuggests = cureRecord.getCheckSuggestList();
                if (!CollectionUtils.isEmpty(checkSuggests)) {
                    for (CheckSuggest checkSuggest : checkSuggests) {
                        checkSuggestNames.add(checkSuggest.getName());
                    }
                }

            }
        }

        vo.setRecipeDetailViews(views);
        vo.setCheckSuggestNames(checkSuggestNames);
        vo.setEvaluation(evaluationService.getEvaluationNamesByOrderId(orderId));

        OrderSession session = orderSessionService.findOneByOrderId(orderId);
        if (null != session) {
            vo.setSessionStatus(null != session.getServiceBeginTime() ? 1 : 0);
        }

        return vo;

    }


    @Override
    public PreOrderVO processNewOrder(OrderParam param) {

        String lockString = "" + param.getDoctorId() + param.getPatientId();
        RedisLock lock = new RedisLock();
        boolean locked = lock.lock(lockString, LockType.neworder);
        PreOrderVO vo = new PreOrderVO();
        try {
            if (locked) {
                Map<String, Object> map = new HashMap<>();
                if (param.getOrderType() == OrderType.outPatient.getIndex()) {
                    map = getOldSession(param.getDoctorId(), ReqUtil.instance.getUserId(), param.getPatientId(), param.getPackId(), param.getCareOrderId(), PackType.online.getIndex(), null);

                } else {
                    map = getOldSession(param.getDoctorId(), ReqUtil.instance.getUserId(), param.getPatientId(), param.getPackId(), param.getCareOrderId(), null, null);
                }
                if (map != null && map.get("msgGroupId") != null) {
                    vo.setGid(map.get("msgGroupId") + "");
                    return vo;
                }

                /******************参数验证 start******************/
                /**非空验证*/
                if (Objects.isNull(ReqUtil.instance.getUser()))
                    throw new ServiceException("请先登录");
                if (Objects.isNull(param.getDoctorId()))
                    throw new ServiceException("请选择医生");
                if (Objects.isNull(param.getOrderType()))
                    throw new ServiceException("订单类型为空");
                if (param.getCareOrderId() != null) {
                    Order o = orderMapper.getOne(param.getCareOrderId());
                    if (o == null || o.getPackType() != PackType.careTemplate.getIndex())
                        throw new ServiceException("careOrderId=" + param.getCareOrderId() + "不是健康关怀订单,客户端参数错误");
                }
                //在线门诊没有套餐id
                //if(Objects.isNull(param.getPackId()))
                //	throw new ServiceException("套餐id为空");
                Pack pack = new Pack();
                /**套餐验证*/
                if (param.getOrderType() != OrderType.outPatient.getIndex()) {
                    pack = packService.getPack(param.getPackId());
                    if (Objects.isNull(pack) ||
                            Objects.isNull(pack.getDoctorId()) ||
                            (pack.getDoctorId().intValue() != param.getDoctorId().intValue())) {
                        throw new ServiceException("该医生套餐不存在");
                    }
                    if (pack.getPackType() == PackType.integral.getIndex()) {
                        boolean integralFlag = integralDoctorService.checkPatientPoint(pack.getId(), ReqUtil.instance.getUserId());
                        if (!integralFlag)
                            throw new ServiceException("您的积分不够");
                    }
                } else {
                    pack.setPackType(PackType.online.getIndex());
                }

                /******************参数验证 end******************/
                param.setUserId(ReqUtil.instance.getUserId());
                String groupId = param.getGroupId();
                GroupDoctor dg = null;
                if (StringUtils.isBlank(groupId)) {
                    // 根据医生查找医生集团
                    dg = groupDoctorService.findOneByUserIdAndStatus(param.getDoctorId(), "C");
                    if (dg != null) {
                        groupId = dg.getGroupId();
                    }
                }
                User user = userManagerImpl.getUser(param.getDoctorId());
                /*************设置订单属性  start****************/
                Order order = new Order();
                setParamDrug(param);
                order.setGroupId(groupId);
                order.setDoctorId(param.getDoctorId());
                order.setAssistantId(user.getDoctor().getAssistantId());
                order.setOrderType(param.getOrderType());
                order.setUserId(ReqUtil.instance.getUserId());
                order.setPackId(pack.getId());
                order.setPackType(pack.getPackType());
                order.setPrice(pack.getPrice());
                order.setPoint(pack.getPoint());//积分问诊订单使用
                order.setTimeLong(pack.getTimeLimit());

                if (param.getOrderType() == OrderType.outPatient.getIndex()) {
                    order.setPrice(0L);
                    OutpatientVO outpatient = groupDoctorService.getOutpatientInfo(order.getDoctorId());
                    if (OnLineState.offLine.getIndex().equals(outpatient.getOnLineState())) {
                        throw new ServiceException("该医生没有上线，不能提供在线门诊！");
                    }
                    //门诊时记录groupId
                    order.setGroupId(outpatient.getGroupId());

                    // 1、生成门诊订单的时候，根据医生的值班要求时长和在线时间比较，来确定订单价格，如果在线时长已超过值班时长，则取门诊价格，否则价格为0
                    // 2、如果订单价格为0，则订单状态设为已支付，否则设为待支付。
                    Long task = outpatient.getTaskDuration() == null ? 0L : outpatient.getTaskDuration(); // 在线时长(可设置的)(要完成这个任务才能收费)
                    Long duty = 0L;
                    if (outpatient.getDutyDuration() != null) {
                        duty = outpatient.getDutyDuration(); // 医生值班时长（定时器计算的在线值班时长）
                    }

                    boolean hasTaskTime = false;
                    if (task != 0) {
                        hasTaskTime = true; // == true表示设置了要完成任务的值班时长
                    }

                    if ((hasTaskTime == false) || ((hasTaskTime == true) && (duty >= task))) {
                        // 没有设置值班时长 或者 设置了值班时长并且在线时长超过值班时长，才取门诊价格。
                        Long price = outpatient.getPrice() == null ? 0L : outpatient.getPrice().longValue();
                        order.setPrice(price);
                    } else {
                        order.setPrice(0L);
                    }

                    if (order.getPrice() == 0) {
                        order.setOrderStatus(OrderEnum.OrderStatus.已支付.getIndex());
                    } else {
                        order.setOrderStatus(OrderStatus.待支付.getIndex());
                    }
                    order.setPackId(0);
                    order.setPackType(PackType.online.getIndex());
                    // 非套餐订单默认为15分钟
                    order.setTimeLong(15);
                } else if (pack.getPackType() != PackType.integral.getIndex()) {

                    if (Objects.isNull(pack.getPrice()) || (pack.getPrice().intValue() == 0)) {
                        order.setOrderStatus(OrderStatus.已支付.getIndex());
                    } else {
                        order.setOrderStatus(OrderStatus.待支付.getIndex());
                    }
                } else {//积分问诊订单没有免费订单
                    order.setOrderStatus(OrderStatus.待支付.getIndex());
                }


                order.setCreateTime(System.currentTimeMillis());
                order.setFinishTime(0L);
                order.setOrderNo(nextOrderNo());
                order.setPatientId(param.getPatientId());
                order.setRefundStatus(OrderEnum.OrderRefundStatus.refundWait.getIndex());
                if (order.getPackType() == PackType.phone.getIndex()) {
                    if (StringUtils.isBlank(param.getExpectAppointmentIds())) {
                        order.setExpectAppointmentIds(baseDataDao.getExpectAppointments().get(0).getId());
                    } else {
                        order.setExpectAppointmentIds(param.getExpectAppointmentIds());
                    }
                }
                order.setCareOrderId(param.getCareOrderId());
                orderMapper.add(order);

                if (order.getOrderType().intValue() == OrderType.outPatient.getIndex()
                        && order.getOrderStatus().intValue() == OrderStatus.已支付.getIndex()) {
                    String wkey = RedisUtil.generateKey(RedisUtil.WAITING_QUEUE, String.valueOf(order.getDoctorId()));
                    RedisUtil.rpush(wkey, order.getId().toString());
                    addJesQueTask(order);
                }

                /*************订单附加信息 start ****************/
                /**
                 * 医生就诊量++
                 * 集团就诊量++
                 */
                doctorService.updateCureNum(order.getDoctorId());
                // 根据医生查找医生集团 ,医生集团就论人数++
                if (StringUtils.isNoneBlank(order.getGroupId())) {
                    groupService.increaseCureNum(order.getGroupId());
                }
                /**
                 * 医患好友关系
                 */
                DoctorPatient doctorPatient;
                //2016-12-12医患关系的变化
                doctorPatient = tagDao.findByDoctorIdAndPatientId(order.getDoctorId(), order.getPatientId());
                if (doctorPatient == null) {
                    //2016-12-12医患关系的变化
                    baseUserService.addDoctorPatient(order.getDoctorId(), param.getPatientId(), order.getUserId());
                    // 关注医生公众号
                    pubGroupService.addSubUser(param.getDoctorId(), order.getUserId());
                    businessServiceMsg.sendEventFriendChange(EventEnum.ADD_FRIEND, order.getDoctorId().toString(), order.getUserId().toString());
                }
                /**
                 * 医患好友关系打标签
                 */
                relationService.addRelationTag2(PackUtil.convert(order));
                generateOrderDoctor(order);

                /*************订单附加信息  end****************/

                /*************订单会话信息 start****************/
                if (order.getOrderStatus() == OrderStatus.已支付.getIndex()) {

                    /**
                     * 2016年12月26日11:05:31
                     * 门诊订单会话和其他类别不共享
                     */
                    /** 会话创建 **/
                    OrderSessionContainer osc = processOrderSession(order, pack, param.getCareOrderId());

                    /**发送初始化消息**/
                    String msgGroupId = osc.getMsgGroupId();
                    param.setGid(msgGroupId);
                    vo.setGid(msgGroupId);

                    /**发送卡片**/
                    if (StringUtils.isNotBlank(param.getDiseaseDesc())) {
                        param.setOrderStatus(order.getOrderStatus());
                        param.setPackType(order.getPackType());
                        param.setGid(osc.getMsgGroupId());
                        param.setOrderId(order.getId());
                        sendNewOrderIllCard(param);
                    }

                    if (order.getOrderType().intValue() == OrderType.outPatient.getIndex()) {
                        messageGroupService.updateGroupBizState(osc.getMsgGroupId(), MessageGroupEnum.NEW_ORDER.getIndex());
                    }
                }
                /*************订单会话信息 end****************/
                /**
                 * 病程信息
                 */
                /**
                 * 这里可以选择传入病历信息
                 */
                param.setOrderStatus(order.getOrderStatus());
                param.setPackType(order.getPackType());
                addHistoryRecord(param, order);

                /**
                 * 患者添加疾病标签
                 */
                Integer userId = ReqUtil.instance.getUserId();
                if (StringUtil.isNotEmpty(param.getDiseaseID())) {
                    iDiseaseLaberService.addLaberByTreat(userId, param.getDiseaseID());
                }
                if (null != param.getDiseaseId()) {
                    iDiseaseLaberService.addLaberByTreat(userId, param.getDiseaseId() + "");
                }
                if (!CollectionUtils.isEmpty(param.getDiseaseIds())) {
                    for (String diseaseId : param.getDiseaseIds()) {
                        iDiseaseLaberService.addLaberByTreat(userId, diseaseId);
                    }
                }

                /*******************积分问诊订单支付 start ***********/
                if (pack.getPackType() == PackType.integral.getIndex()) {
                    param.setOrderId(order.getId());
                    param.setPayType(AccountEnum.PayType.integral.getIndex());
                    PreOrderVO payVo = addPayOrder(param, ReqUtil.instance.getUser());
                    if (payVo == null || payVo.getIntegralOrder() == null || !payVo.getIntegralOrder()) {
                        throw new ServiceException("积分问诊订单支付失败");
                    }
                    OrderSessionContainer osc = orderSessionContainerDao.findByOrderId(order.getId());
                    vo.setGid(osc.getMsgGroupId());
                    order.setOrderStatus(OrderStatus.已支付.getIndex());
                }
                /******************积分问诊订单支付 end **********/

                vo.setIfNewOrder(true);
                vo.setOrderId(order.getId());
                vo.setOrderNo(order.getOrderNo());
                vo.setOrderStatus(order.getOrderStatus());

                //生成对应待处理状态数据PendingOrderStatus
                createPendingOrderStatus(order);

                if (order.getOrderStatus() == OrderStatus.已支付.getIndex() &&
                        order.getPackType() != PackType.integral.getIndex()) {
                    OrderNotify.sendMessageAndNotify(vo.getOrderId(), ReqUtil.isFromWechat());
                }
            } else {
                throw new ServiceException("请不要重复下单");
            }
        } catch (HttpApiException e) {
            e.printStackTrace();
        } finally {
            lock.unlock(lockString, LockType.neworder);
        }
        return vo;
    }


    /**
     * 2016-12-07 14:42:44
     * 会话创建  （图文/电话/报道）
     *
     * @param order
     * @param pack
     * @param careOrderId
     */
    public OrderSessionContainer processOrderSession(Order order, Pack pack, Integer careOrderId) throws HttpApiException {


        int sessionCategory = OrderSessionCategory.text_tel_checkIn_integral.getIndex();
        if (careOrderId != null) {
            sessionCategory = OrderSessionCategory.care_in_text_tel_integral.getIndex();
        } else if (pack.getPackType() == PackType.online.getIndex()) {
            sessionCategory = OrderSessionCategory.online.getIndex();
        }

        OrderSessionContainer osc =
                orderSessionContainerDao.findTextPhone(order.getDoctorId(), order.getUserId(), order.getPatientId(), careOrderId, sessionCategory);
        if (osc != null) {
            //完善错误数据
            //由于mongodb 没有事物控制导致数据不一致
            Order o = orderMapper.getOne(osc.getOrderId());
            OrderSession os = orderSessionMapper.selectByPrimaryKey(osc.getOrderSessionId());
            if (o == null || os == null) {
                orderSessionContainerDao.deleteById(osc.getId());
                osc = null;
            }
        }
        if (osc != null) {
            /**Id
             * 获取oldSession
             * insert new Session
             * 更新 container
             */
            OrderSession newSession = new OrderSession();
            newSession.setMsgGroupId(osc.getMsgGroupId());
            newSession.setAssistantDoctorGroupId(osc.getAssistantDoctorGroupId());
            newSession.setAssistantPatientGroupId(osc.getAssistantPatientGroupId());
            newSession.setCreateTime(System.currentTimeMillis());
            newSession.setOrderId(order.getId());
            newSession.setLastModifyTime(System.currentTimeMillis());

            orderSessionService.save(newSession);
            Order o = orderMapper.getOne(osc.getOrderId());
            if (pack.getPackType() == PackType.checkin.getIndex()) {
                if (o.getOrderStatus() == OrderStatus.已支付.getIndex() ||
                        osc.getTotalReplyCount() > osc.getReplidCount()) {
                } else {
                    Integer openFlag = checkInService.getCheckInGiveTimes(o.getDoctorId());
                    int count = 0;
                    if (openFlag == 1)
                        count = SysConstants.DEFAULT_MESSAGE_REPLY_COUNT;
                    orderSessionContainerDao.updateById(osc.getId(), null, null, null, count, 0, o.getOrderStatus());
                    //处理报道的待回复数据
                    jedisTemplate.hdel(MESSAGE_REPLY_ORDER_COUNT, osc.getMsgGroupId());
                }
            } else {
                if (pack.getPackType() == PackType.phone.getIndex()) {
                    if ((o.getPackType() == PackType.message.getIndex() && !o.ifFinished()) ||
                            (o.getPackType() == PackType.integral.getIndex() && !o.ifFinished())) {
                        orderSessionService.finishService(o.getId(), 1);
                    }
                    orderSessionContainerDao.updateById(osc.getId(), order.getId(), pack.getPackType(), newSession.getId(), 0, 0, OrderStatus.已支付.getIndex());
                } else if (pack.getPackType() == PackType.message.getIndex() ||
                        pack.getPackType() == PackType.integral.getIndex()) {
                    orderSessionContainerDao.updateById(osc.getId(), order.getId(), pack.getPackType(), newSession.getId(), SysConstants.DEFAULT_MESSAGE_REPLY_COUNT, 0, OrderStatus.已支付.getIndex());
                } else if (pack.getPackType() == PackType.online.getIndex()) {
                    orderSessionContainerDao.updateById(osc.getId(), order.getId(), pack.getPackType(), newSession.getId(), 0, 0, OrderStatus.已支付.getIndex());
                }
            }
        } else {

            if (sessionCategory == OrderSessionCategory.text_tel_checkIn_integral.getIndex()) {
                /**
                 * invoke im 生成会话  # 生成 医患 、 医助 、 患助 三个会话
                 * insert new Session
                 * 创建容器  container
                 */
                OrderSession orderSession = new OrderSession();
                CreateGroupRequestMessage createGroupParam = new CreateGroupRequestMessage();
                Map<String, Object> groupParam = new HashMap<String, Object>();
                Patient patient = patientMapper.selectByPrimaryKey(order.getPatientId());
                groupParam.put("patientName", patient.getUserName());
                groupParam.put("patientAge", patient.getAgeStr());
                groupParam.put("patientSex", patient.getSex() != null ? BusinessUtil.getSexName(Integer.valueOf(patient.getSex())) : "");
                groupParam.put("patientArea", patient.getArea());
                groupParam.put("orderId", order.getId());
                groupParam.put("orderType", order.getOrderType());
                if (PackType.message.getIndex() == order.getPackType())
                    groupParam.put("packType", 2);
                else if (PackType.phone.getIndex() == order.getPackType())
                    groupParam.put("packType", 3);
                else
                    groupParam.put("packType", order.getPackType());
                groupParam.put("price", order.getPrice());
                groupParam.put("point", order.getPoint());
                createGroupParam.setParam(groupParam);
                createGroupParam.setCreateNew(true);
                createGroupParam.setBizStatus(String.valueOf(order.getOrderStatus()));
                createGroupParam.setSendRemind(false);

                Map<String, Object> noStatusGroupParam = new HashMap<String, Object>();
                noStatusGroupParam.put("orderId", order.getId());
                int oneOrMore = 1;
                String gtype, gname = null, fromUserId, toUserId;
                {
                    //医患会话组
                    gtype = RelationTypeEnum.DOC_PATIENT.getValue();
                    fromUserId = String.valueOf(order.getDoctorId());
                    toUserId = String.valueOf(order.getUserId());
                    GroupInfo groupInfo = invokeIMCreateSession(createGroupParam, oneOrMore, gtype, gname, fromUserId, toUserId);
                    orderSession.setMsgGroupId(groupInfo.getGid());
                }
                orderSession.setCreateTime(System.currentTimeMillis());
                orderSession.setOrderId(order.getId());
                orderSession.setLastModifyTime(System.currentTimeMillis());
                orderSessionService.save(orderSession);

                OrderSessionContainer po = new OrderSessionContainer();
                po.setDoctorId(order.getDoctorId());
                po.setUserId(order.getUserId());
                po.setPatientId(order.getPatientId());
                po.setOrderSessionId(orderSession.getId());
                po.setOrderId(order.getId());
                po.setPackType(order.getPackType());
                po.setSessionType(sessionCategory);
                po.setAssistantDoctorGroupId(orderSession.getAssistantDoctorGroupId());
                po.setAssistantPatientGroupId(orderSession.getAssistantPatientGroupId());
                if (order.getPackType() == PackType.checkin.getIndex())
                    po.setStatus(OrderStatus.已完成.getIndex());
                else
                    po.setStatus(OrderStatus.已支付.getIndex());
                if (order.getPackType() == PackType.message.getIndex() ||
                        order.getPackType() == PackType.integral.getIndex()
                        ) {
                    po.setTotalReplyCount(SysConstants.DEFAULT_MESSAGE_REPLY_COUNT);
                    po.setReplidCount(0);
                } else if (order.getPackType() == PackType.checkin.getIndex()) {
                    Integer openFlag = checkInService.getCheckInGiveTimes(order.getDoctorId());
                    int count = 0;
                    if (openFlag.intValue() == 1)
                        count = SysConstants.DEFAULT_MESSAGE_REPLY_COUNT;
                    po.setTotalReplyCount(count);
                    po.setReplidCount(0);
                } else {
                    po.setTotalReplyCount(0);
                    po.setReplidCount(0);
                }
                po.setMsgGroupId(orderSession.getMsgGroupId());
                orderSessionContainerDao.insert(po);
                osc = po;
            } else if (sessionCategory == OrderSessionCategory.online.getIndex()) {
                /**
                 * invoke im 生成会话  # 生成 医患 、 医助 、 患助 三个会话
                 * insert new Session
                 * 创建容器  container
                 */
                OrderSession orderSession = new OrderSession();
                CreateGroupRequestMessage createGroupParam = new CreateGroupRequestMessage();
                Map<String, Object> groupParam = new HashMap<String, Object>();
                Patient patient = patientMapper.selectByPrimaryKey(order.getPatientId());
                groupParam.put("patientName", patient.getUserName());
                groupParam.put("patientAge", patient.getAgeStr());
                groupParam.put("patientSex", patient.getSex() != null ? BusinessUtil.getSexName(Integer.valueOf(patient.getSex())) : "");
                groupParam.put("patientArea", patient.getArea());
                groupParam.put("orderId", order.getId());
                groupParam.put("orderType", order.getOrderType());
                if (PackType.message.getIndex() == order.getPackType())
                    groupParam.put("packType", 2);
                else if (PackType.phone.getIndex() == order.getPackType())
                    groupParam.put("packType", 3);
                else
                    groupParam.put("packType", order.getPackType());
                groupParam.put("price", order.getPrice());
                groupParam.put("point", order.getPoint());
                createGroupParam.setParam(groupParam);
                createGroupParam.setCreateNew(true);
                createGroupParam.setBizStatus(String.valueOf(order.getOrderStatus()));
                createGroupParam.setSendRemind(false);

                Map<String, Object> noStatusGroupParam = new HashMap<String, Object>();
                noStatusGroupParam.put("orderId", order.getId());
                int oneOrMore = 1;
                String gtype, gname = null, fromUserId, toUserId;
                {
                    //医患会话组
                    gtype = RelationTypeEnum.DOC_OUTPATIENT.getValue();
                    fromUserId = String.valueOf(order.getDoctorId());
                    toUserId = String.valueOf(order.getUserId());
                    GroupInfo groupInfo = invokeIMCreateSession(createGroupParam, oneOrMore, gtype, gname, fromUserId, toUserId);
                    orderSession.setMsgGroupId(groupInfo.getGid());
                }
                orderSession.setCreateTime(System.currentTimeMillis());
                orderSession.setOrderId(order.getId());
                orderSession.setLastModifyTime(System.currentTimeMillis());
                int save = orderSessionService.save(orderSession);

                OrderSessionContainer po = new OrderSessionContainer();
                po.setDoctorId(order.getDoctorId());
                po.setUserId(order.getUserId());
                po.setPatientId(order.getPatientId());
                po.setOrderSessionId(orderSession.getId());
                po.setOrderId(order.getId());
                po.setPackType(order.getPackType());
                po.setSessionType(sessionCategory);
                po.setAssistantDoctorGroupId(orderSession.getAssistantDoctorGroupId());
                po.setAssistantPatientGroupId(orderSession.getAssistantPatientGroupId());
                po.setTotalReplyCount(0);
                po.setReplidCount(0);
                po.setMsgGroupId(orderSession.getMsgGroupId());
                po.setStatus(OrderStatus.已支付.getIndex());
                orderSessionContainerDao.insert(po);
                osc = po;

            } else {
                /**
                 * 健康关怀下的 图文/电话
                 * 根据健康关怀获取old orderSession
                 * insert new Session
                 *  创建容器  container
                 */
                OrderSession oldSession = orderSessionService.findOneByOrderId(careOrderId);
                OrderSession newSession = new OrderSession();

                newSession.setMsgGroupId(oldSession.getMsgGroupId());
                newSession.setAssistantDoctorGroupId(oldSession.getAssistantDoctorGroupId());
                newSession.setAssistantPatientGroupId(oldSession.getAssistantPatientGroupId());
                newSession.setCreateTime(System.currentTimeMillis());
                newSession.setOrderId(order.getId());
                newSession.setLastModifyTime(System.currentTimeMillis());
                orderSessionService.save(newSession);

                OrderSessionContainer po = new OrderSessionContainer();
                po.setDoctorId(order.getDoctorId());
                po.setUserId(order.getUserId());
                po.setPatientId(order.getPatientId());
                po.setOrderSessionId(newSession.getId());
                po.setOrderId(order.getId());
                po.setPackType(order.getPackType());
                po.setSessionType(sessionCategory);
                po.setStatus(OrderStatus.已支付.getIndex());
                if (order.getPackType() == PackType.message.getIndex() ||
                        order.getPackType() == PackType.integral.getIndex()
                        ) {
                    po.setTotalReplyCount(SysConstants.DEFAULT_MESSAGE_REPLY_COUNT);
                    po.setReplidCount(0);
                } else {
                    po.setTotalReplyCount(0);
                    po.setReplidCount(0);
                }
                po.setMsgGroupId(newSession.getMsgGroupId());
                po.setCareOrderId(careOrderId);
                orderSessionContainerDao.insert(po);
                osc = po;
            }
        }
        OrderParam param = new OrderParam();
        param.setDoctorId(osc.getDoctorId());
        param.setPatientId(osc.getPatientId());
        param.setUserId(osc.getUserId());
        param.setPackType(osc.getPackType());
        osc = orderSessionContainerDao.findById(osc.getId());
        if (order.getPackType() != PackType.checkin.getIndex())
            jedisTemplate.hdel(MESSAGE_REPLY_ORDER_COUNT, osc.getMsgGroupId());
        return osc;
    }

    @Override
    public PreOrderVO updateNewOrder(OrderParam param) throws HttpApiException {
        Order o = orderMapper.getOne(param.getOrderId());
        setParamDrug(param);
        if (o.getPackType() == PackType.phone.getIndex() &&
                StringUtils.isBlank(param.getExpectAppointmentIds()))
            param.setExpectAppointmentIds(baseDataDao.getExpectAppointments().get(0).getId());
        Map<String, Object> sqlmap = new HashMap<>();
        sqlmap.put("orderId", param.getOrderId());
        sqlmap.put("expectAppointmentIds", param.getExpectAppointmentIds());
        orderMapper.updateExpectAppointmentIds(sqlmap);
        illHistoryRecordDao.updateFromOrder(param.getOrderId(), param.getDrugCase(), param.getDrugGoodsIds(), param.getDrugPicUrls(), param.getHopeHelp(), param.getPicUrls());
        OrderSessionContainer osc = orderSessionContainerDao.findByOrderId(param.getOrderId());
        PreOrderVO vo = new PreOrderVO();
        vo.setGid(osc.getMsgGroupId());
        vo.setOrderId(o.getId());
        vo.setOrderStatus(o.getOrderStatus());
        param.setPackType(o.getPackType());
        param.setGid(osc.getMsgGroupId());
        param.setIsSecondTreate("yes");
        param.setOrderStatus(o.getOrderStatus());
        param.setPatientId(o.getPatientId());
        param.setDoctorId(o.getDoctorId());
        param.setUserId(o.getUserId());
        param.setPackType(o.getPackType());
        param.setOrderStatus(o.getOrderStatus());
        param.setOrderType(o.getOrderType());
        param.setUserId(o.getUserId());

        sendNewOrderIllCard(param);

        messageGroupService.updateGroupBizState(osc.getMsgGroupId(), MessageGroupEnum.NEW_ORDER.getIndex());

        if (o.getPackType() == PackType.careTemplate.getIndex() && (o.getPrice() == null || o.getPrice() < 1) && osc.getTotalReplyCount() != null && osc.getTotalReplyCount() > 0)
            sendFreeMessage(osc.getMsgGroupId());
        return vo;
    }

    private void sendFreeMessage(String gid) throws HttpApiException {
        List<ImgTextMsg> msgs = new ArrayList<>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTitle("医生已赠送沟通次数");
        imgTextMsg.setContent("本次咨询含医生3次回复。为节省沟通次数，提问时请尽量一次性描述病情与问题。");
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setStyle(7);
        msgs.add(imgTextMsg);
        businessServiceMsg.sendTextMsgToGid(SysUserEnum.SYS_001.getUserId(), gid, msgs, null);
    }

    private void setParamDrug(OrderParam param) {
        if (StringUtils.isNotBlank(param.getDrugInfos())) {
            List<DrugInfo> drugInfoList = JSONMessage.parseArray(param.getDrugInfos(), DrugInfo.class);
            param.setDrugInfoList(drugInfoList);
            List<String> drugGoodsIds = new ArrayList<>();
            drugInfoList.forEach(o -> drugGoodsIds.add(o.getDrugId()));
            param.setDrugGoodsIds(drugGoodsIds);
        }
    }

    private void initOrderParam3(OrderParam orderParam, Integer packId, Integer doctorId, Integer patientId) {
        Patient patient = patientMapper.selectByPrimaryKey(patientId);

        orderParam.setUserId(patient.getUserId());
        orderParam.setPatientId(patient.getId());
        orderParam.setPackId(packId);
        orderParam.setDoctorId(doctorId);
    }

    @Override
    public PreOrderVO saveSendPayOrderSingleByNotice3(Integer userType, Integer doctorUserId, Integer patientId, Integer packId) throws HttpApiException {
        String tag = "saveSendPayOrderSingleByNotice3";
        if (logger.isInfoEnabled()) {
            logger.info("{}. userType={}, doctorUserId={}, patientId={}, packId={}", tag,
                    userType, doctorUserId, patientId, packId);
        }

        // 只处理一名患者
        if (userType != UserEnum.UserType.doctor.getIndex()) {
            throw new ServiceException("您不是医生身份！");
        }
        if (patientId == null) {
            throw new ServiceException("请选择需要发送的患者");
        }
        if (packId == null) {
            throw new ServiceException("套餐ID不能为空！");
        }

        Pack pack = packService.getPack(packId);
        if (pack == null) {
            throw new ServiceException("套餐不存在！");
        }

        // 检查是否有正在进行的订单，如有，直接返回
        PreOrderVO vo = this.getOngoingCareOrderByPatient(patientId, pack.getId());
        if (null != vo) {
            return vo;
        }

        logger.info("{}. vo={}", tag, vo);

        User docUserInfo = userManager.getUser(doctorUserId);

        OrderParam orderParam = new OrderParam();
        initOrderParam3(orderParam, packId, doctorUserId, patientId);

        User user = userManager.getUser(orderParam.getUserId());
        orderParam.setTelephone(user.getTelephone());
        logger.info("{}. user.getTelephone()={}", tag, user.getTelephone());
        // 设置患者病情信息
        Order order = this.addCareOrder(orderParam);

        List<PackDoctor> packDoctors = packDoctorService.findByPackId(order.getPackId());
        List<String> docIds = new ArrayList<String>(packDoctors.size() + 1);
        for (PackDoctor packDoctor : packDoctors) {
            docIds.add(packDoctor.getDoctorId() + "");
        }
        docIds.add(order.getUserId() + "");
        logger.info("{}. docIds={}", tag, docIds);
        // 创建会话
        OrderSession orderSession = new OrderSession();
        orderSession.setToUserIds(OrderSession.appendStringUserId(docIds));
        this.createGroupMore(order, orderSession);
        logger.info("{}. orderSession={}", tag, orderSession);
        /****创建订单会话 start***/
        if (order.getPackType() == PackEnum.PackType.careTemplate.getIndex() &&
                order.getOrderStatus() == OrderEnum.OrderStatus.已支付.getIndex()) {
            Pack p = packService.getPack(packId);
            OrderSessionContainer po = new OrderSessionContainer();
            po.setDoctorId(order.getDoctorId());
            po.setUserId(order.getUserId());
            po.setPatientId(order.getPatientId());
            po.setOrderSessionId(orderSession.getId());
            po.setOrderId(order.getId());
            po.setPackType(order.getPackType());
            po.setSessionType(OrderEnum.OrderSessionCategory.care.getIndex());
            po.setStatus(OrderEnum.OrderStatus.已支付.getIndex());
            po.setMsgGroupId(orderSession.getMsgGroupId());
            int replyCount = p.getReplyCount() == null ? 0 : p.getReplyCount();
            po.setTotalReplyCount(replyCount);
            po.setReplidCount(0);
            orderSessionContainerDao.insert(po);
        }
        /****创建订单会话 end***/
        logger.info("{}. order.getPackType()={}", tag, order.getPackType());
        /****创建病程 start*****/
        orderParam.setPackType(pack.getPackType());
        orderParam.setGid(orderSession.getMsgGroupId());
        orderParam.setMainTreateDoctorId(order.getDoctorId());
        orderParam.setOrderId(order.getId());
        this.addHistoryRecord(orderParam, order);
        /****创建病程 end*****/
        logger.info("{}. addHistoryRecord={}", tag, true);
        // 发送短信和消息, fix XGSF-13296，开启通知 
        this.sendCareNotify(pack, order, orderSession, docUserInfo, user);
        logger.info("{}. sendCareNotify={}", tag, true);

        return this.createPreOrderVO(order, orderSession, true);
    }

    @Override
    public void saveRecommendCarePack(Integer orderId, Integer followPackId) {
        Order order = new Order();
        order.setId(orderId);
        order.setFollowTemplateId(followPackId + "");
        this.updateOrder(order);
    }

    @Resource
    protected UserRepository userRepository;

    @Resource
    protected IGroupSearchService groupSearchService;

    @Override
    public List<CarePlanDoctorVO> findDoctorInfoGroup(Integer orderId, List<Integer> docIds) {
        Order order = this.getOne(orderId);
        List<CarePlanDoctorVO> list = this.userRepository.findDoctorInfoGroup(docIds, order.getDoctorId());

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        groupSearchService.wrapGroupNames(list);

        Collections.sort(list, new Comparator<CarePlanDoctorVO>() {
            @Override
            public int compare(CarePlanDoctorVO o1, CarePlanDoctorVO o2) {
                if (1 == o1.getGroupType()) {
                    return -1;
                } else if (1 == o2.getGroupType()) {
                    return 1;
                }
                return 0;
            }
        });

        return list;
    }

    @Override
    public void sendBeginIMMsg(Integer orderId, String patientMsg, String doctorMsg) throws HttpApiException {
        Order order = this.getOne(orderId);
        // 患者端
        OrderSession session = orderSessionService.findOneByOrderId(order.getId());
        String content = patientMsg;
        businessServiceMsg.sendNotifyMsgToUser(order.getUserId() + "", session.getMsgGroupId(), content);

        // 医生端
        List<OrderDoctor> orderDoctors = orderDoctorService.findOrderDoctors(order.getId());
        String content_ = doctorMsg;
        for (OrderDoctor orderDoctor : orderDoctors) {
            businessServiceMsg.sendNotifyMsgToUser(orderDoctor.getDoctorId() + "", session.getMsgGroupId(), content_);
        }
    }


}
