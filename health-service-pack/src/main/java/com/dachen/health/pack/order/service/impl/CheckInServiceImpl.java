package com.dachen.health.pack.order.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.commons.constants.GroupEnum.GroupType;
import com.dachen.health.commons.constants.ImageDataEnum;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.OrderEnum.CheckInStatus;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.constants.SysConstants;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.utils.PackUtil;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.guide.util.GuideMsgHelper;
import com.dachen.health.pack.illhistory.service.IllHistoryInfoService;
import com.dachen.health.pack.order.entity.param.CheckInParam;
import com.dachen.health.pack.order.entity.param.CheckInParam.CheckInFrom;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Case;
import com.dachen.health.pack.order.entity.po.CheckIn;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.OrderSessionContainer;
import com.dachen.health.pack.order.entity.vo.CheckInVO;
import com.dachen.health.pack.order.mapper.CaseMapper;
import com.dachen.health.pack.order.mapper.CheckInMapper;
import com.dachen.health.pack.order.service.ICheckInService;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.patient.mapper.PatientDiseaseMapper;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.DiseaseParam;
import com.dachen.health.pack.patient.model.ImageData;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.model.PatientDisease;
import com.dachen.health.pack.patient.service.IDiseaseService;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.user.dao.ITagDao;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.service.IRelationService;
import com.dachen.health.wx.remote.WXRemoteManager;
import com.dachen.im.server.constant.SysConstant.SysUserEnum;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MsgDocument;
import com.dachen.im.server.data.MsgDocument.DocInfo;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.pub.service.PubGroupService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.BeanUtil;
import com.dachen.util.BusinessUtil;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;


/**
 * ProjectName： health-service-pack<br>
 * ClassName： CheckInServiceImpl<br>
 * Description：报到 <br>
 *
 * @author fanp
 * @version 1.0.0
 * @createTime 2015年9月7日
 */
@Service
public class CheckInServiceImpl implements ICheckInService {

    @Autowired
    private CaseMapper caseMapper;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private IBaseUserService baseUserService;

    @Autowired
    private UserManager userManager;

    @Autowired
    private PubGroupService pubGroupService;

    @Autowired
    private IGroupDoctorService groupDoctorService;

    @Autowired
    private IGroupDoctorDao gdocDao;

    @Autowired
    private IPatientService patientService;

    @Autowired
    private IBusinessServiceMsg businessMsgService;

    @Resource
    private IImageDataService imageDataService;

    @Autowired
    private IRelationService relationService;

    @Autowired
    private PatientDiseaseMapper patientDiseaseMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private IDiseaseService diseaseService;

    @Autowired
    private WXRemoteManager wxManager;

    @Autowired
    private IllHistoryInfoService illHistoryInfoService;

    @Autowired
    private IBaseDataDao baseDataDao;

    @Autowired
    private ITagDao tagDao;


    private static final String APP_DOWN_PATIENT = "/health/web/app/downPatient.html";

    private static final Integer giveTimes = 1;


    @Override
    public Integer getCheckInStatus(CheckInParam param) {
        if (param.getUserId() == null) {
            throw new ServiceException("用户ID为空");
        }
        BaseUserVO vo = baseUserService.getUser(param.getUserId());

        if (vo != null) {
            if (vo.getUserType().intValue() != UserEnum.UserType.doctor.getIndex()) {
                throw new ServiceException("不是医生类型用户");
            }
            return vo.getCheckInGive();
        } else {
            throw new ServiceException("该医生不存在");
        }
    }

    @Override
    public Integer getCheckInGiveTimes(Integer docId) {
        CheckInParam param = new CheckInParam();
        param.setUserId(docId);
        if (getCheckInStatus(param) == 1) {//开通
            return giveTimes;
        }
        return 0;
    }

    private CheckInVO getCheckInPatient(Integer patientId, List<CheckInVO> vlist) {
        if (vlist != null && !vlist.isEmpty()) {
            for (CheckInVO v : vlist) {
                if (v.getPatientId().intValue() == patientId.intValue()) {
                    return v;
                }
            }
        }
        return null;
    }

    /**
     * </p>添加报到信息</p>
     *
     * @param param
     * @return 报到id
     * @author fanp
     * @date 2015年9月7日
     */
    public int add(CheckInParam param) throws HttpApiException {
        // 验证参数
        if (param.getCheckInFrom() == null || !CheckInFrom.isCheckInFromEffective(param.getCheckInFrom())) {
            throw new ServiceException("非法调用");
        }

        if (param.getPatientId() == null) {
            throw new ServiceException("请选择患者");
        }
        if (param.getDoctorId() == null) {
            throw new ServiceException("请选择医生");
        }

        if (StringUtil.isBlank(param.getPhone())) {
            throw new ServiceException("请填写手机号码");
        }

//		if (EmojiUtil.containsEmoji(param.getMessage())) {
//			throw new ServiceException("报道失败，不支持表情字符");
//		}

        // 如果未处理则更新数据
        // 取消状态则更新数据以及修状态为未处理
        // 如果已确定则抛出异常

        // 验证当前用户类型是否为患者
//		BaseUserVO user = baseUserService.getUser(param.getUserId());
        User uu = userManager.getUser(param.getUserId());
        if (uu == null) {
            throw new ServiceException("用户不存在，请重新登录");
        } else if (uu.getUserType() != UserEnum.UserType.patient.getIndex()) {
            throw new ServiceException("您不是患者，无法报到");
        }

        // 验证患者是否属于当前用户
        Patient patient = patientService.findByPk(param.getPatientId());
        if (patient == null || !patient.getUserId().equals(param.getUserId())) {
            throw new ServiceException("患者不存在");
        }

        // 验证医生是否为医生
        BaseUserVO doctor = baseUserService.getUser(param.getDoctorId());
        if (doctor == null || doctor.getUserType() != UserEnum.UserType.doctor.getIndex()) {
            throw new ServiceException("医生不存在，请重新查找");
        }

        // 先查找患者和医生存在一个非确定状态的患者报到，则再次报到的时候是更新上次的所有报到信息

        // 添加报到
        Long nowTime = System.currentTimeMillis();
        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(param.getUserId());
        checkIn.setPatientId(param.getPatientId());
        checkIn.setDoctorId(param.getDoctorId());
        checkIn.setCreateTime(nowTime);
        checkIn.setLastUpdateTime(nowTime);
        checkIn.setCheckInFrom(param.getCheckInFrom());
        checkIn.setFreePack(doctor.getCheckInGive() == null ? 1 : doctor.getCheckInGive());
        List<CheckInVO> vlist = checkInMapper.getCheckInByParam(checkIn);
        //获取报到的患者
        CheckInVO vo = getCheckInPatient(param.getPatientId(), vlist);

        Case casePo = new Case();
        casePo.setUserId(param.getUserId());
        casePo.setPatientId(param.getPatientId());
        casePo.setPatientName(patient.getUserName());
        casePo.setSex(patient.getSex());
        casePo.setBirthday(patient.getBirthday());
        casePo.setPhone(param.getPhone());
        casePo.setHospital(param.getHospital());
        casePo.setRecordNum(param.getRecordNum());
        casePo.setLastCureTime(param.getLastCureTime());
        if (param.getDiseaseIds() != null && param.getDiseaseIds().size() > 0) {
            String dis = param.getDiseaseIds().toString();
            casePo.setDiseaseId(dis.substring(1, dis.length() - 1));
        }
        casePo.setMessage(param.getMessage());

        if (vo == null) {// 新增
            checkIn.setStatus(OrderEnum.CheckInStatus.init.getIndex());
            checkInMapper.add(checkIn);
            // 添加病例信息
            casePo.setCheckInId(checkIn.getId());
            caseMapper.add(casePo);

            // 添加病例图像信息
            List<String> imageUrls = param.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String imageUrl : imageUrls) {
                    ImageData imageData = new ImageData();
                    imageData.setUserId(param.getUserId());
                    imageData.setRelationId(casePo.getId());

                    /***end add  by  liwei  2016年1月25日********/
                    imageData.setImageUrl(imageUrl);
                    imageData.setImageType(ImageDataEnum.caseImage.getIndex());
                    imageDataService.add(imageData);
                }
            }
        } else if (vo.getStatus() == OrderEnum.CheckInStatus.cancel.getIndex()) {
            checkIn.setId(vo.getCheckInId());
            checkIn.setStatus(OrderEnum.CheckInStatus.init.getIndex());
            param.setLastUpdateTime(nowTime);
            param.setCheckInId(vo.getCheckInId());
            checkInMapper.updateLastConfirmTime(param);
            CheckInParam checkInParm = BeanUtil.copy(checkIn, CheckInParam.class);
            checkInParm.setCheckInId(vo.getCheckInId());
            if (vo.getOrderId() == null || vo.getOrderId() == 0) {
                checkInParm.setOrderId(0);
            }
            if (vo.getStatus() == OrderEnum.CheckInStatus.cancel.getIndex()) {
                checkInMapper.update(checkInParm);
            }
            casePo.setCheckInId(checkIn.getId());
            CheckInVO cVO = caseMapper.getByCheckIn(checkInParm);
            if (cVO == null) {
                // 添加病例信息
                caseMapper.add(casePo);
            } else {
                casePo.setCheckInId(cVO.getCheckInId());
                casePo.setId(cVO.getCaseId()); // 更新病例信息
                caseMapper.updateAllCase(casePo);
            }
            ImageData imgParam = new ImageData();
            imgParam.setRelationId(casePo.getId());
            imageDataService.deleteByExampleByRelationId(imgParam);// 删除
            // 添加病例图像信息
            List<String> imageUrls = param.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String imageUrl : imageUrls) {
                    ImageData imageData = new ImageData();
                    imageData.setUserId(param.getUserId());
                    imageData.setRelationId(casePo.getId());
                    imageData.setImageUrl(imageUrl);
                    imageData.setImageType(ImageDataEnum.caseImage.getIndex());
                    imageDataService.add(imageData);
                }
            }
        } else if (vo.getStatus().intValue() == CheckInStatus.init.getIndex()) {
            throw new ServiceException("您已向" + doctor.getName() + "医生发起了患者报到、请耐心等待医生回复");
        } else if (vo.getStatus() == OrderEnum.CheckInStatus.confirm.getIndex()) {
            throw new ServiceException("您已向" + doctor.getName() + "医生报到成功、如需咨询医生请下载玄关健康咨询沟通");
        }
        //  发送通知
        List<ImgTextMsg> tlist = new ArrayList<ImgTextMsg>();
        ImgTextMsg msg = new ImgTextMsg();
        msg.setStyle(7);
        msg.setTitle("患者报到");
        msg.setTime(nowTime);
        msg.setContent("您的病历资料已提交，等待" + doctor.getName() + "医生的确认。");
        tlist.add(msg);
        businessMsgService.sendTextMsg(String.valueOf(param.getUserId()), SysGroupEnum.TODO_NOTIFY, tlist, null);

        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setStyle(7);
        textMsg.setTitle("患者报到");
        textMsg.setTime(nowTime);
        textMsg.setContent("您有" + casePo.getPatientName() + "患者来报到");
        mpt.add(textMsg);

        businessMsgService.sendTextMsg(String.valueOf(param.getDoctorId()), SysGroupEnum.TODO_NOTIFY, mpt, null);

        businessMsgService.sendEventDoctorDisturb(EventEnum.NEW_CHECKIN, String.valueOf(param.getDoctorId()), String.valueOf(param.getUserId()));

        String url = PropertiesUtil.getContextProperty("health.server") + APP_DOWN_PATIENT;
//		如果是微信端需要发报到微信卡片
        if (param.getCheckInFrom().intValue() == CheckInFrom.WX.getIndex()) {
        	wxManager.checkInResultNotify(uu.getWeInfo().getMpOpenid(), url, patient.getUserName(), doctor.getName(), checkIn.getStatus());
        }

        return checkIn.getId();
    }

    /**
     * </p>报到列表</p>
     *
     * @param param
     * @author fanp
     * @date 2015年9月9日
     */
    public List<CheckInVO> list(CheckInParam param) {

        Integer currentUserId = ReqUtil.instance.getUserId();
        User currentUser = userRepository.getUser(currentUserId);
        if (Objects.isNull(currentUser)) {
            throw new ServiceException("当前用户不存在");
        }

        List<CheckInVO> list = checkInMapper.list(param);
        if (!CollectionUtils.isEmpty(list)) {
            for (CheckInVO vo : list) {
                Long birthday = vo.getBirthday();
                if (birthday != null && birthday != 0) {
                    vo.setAge(DateUtil.calcAge(birthday));
                }
                vo.setBirthday(birthday);
                BaseUserVO user = baseUserService.getUser(vo.getUserId());
                if (user != null) {
                    vo.setUserName(user.getName());
                    if (StringUtil.isNotBlank(user.getHeadPicFileName())) {
                        vo.setTopPath(PropertiesUtil.addUrlPrefix(user.getHeadPicFileName()));
                    }
                }
            }
        }

        List<DoctorPatient> doctorPatients = tagDao.getAllMyPatient(currentUserId);
        if (!CollectionUtils.isEmpty(doctorPatients)) {
            for (CheckInVO checkInVO : list) {
                for (DoctorPatient doctorPatient : doctorPatients) {
                    if (Objects.nonNull(checkInVO.getPatientId()) && Objects.nonNull(doctorPatient.getPatientId()) &&
                            checkInVO.getPatientId().intValue() == doctorPatient.getPatientId().intValue()) {
                        checkInVO.setRemarkName(doctorPatient.getRemarkName());
                        checkInVO.setRemark(doctorPatient.getRemark());
                    }
                }
            }
        }

        return list;
    }

    /**
     * </p>查看报到详情</p>
     *
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月7日
     */
    public CheckInVO detail(CheckInParam param) {
        if (param.getCheckInId() == null) {
            throw new ServiceException("请选择报到");
        }

        CheckInVO vo = caseMapper.getByCheckIn(param);
        if (vo == null) {
            return null;
        }
        Long birthday = vo.getBirthday();
        vo.setBirthday(birthday);
        setDiseaseInfo(vo);

        List<String> urls = imageDataService.findImgData(ImageDataEnum.caseImage.getIndex(), vo.getCaseId());

        vo.setImageUrls(urls);
        BaseUserVO user = baseUserService.getUser(vo.getUserId());
        if (user != null) {
            vo.setUserName(user.getName());
            if (StringUtil.isNotBlank(user.getHeadPicFileName())) {
                vo.setTopPath(PropertiesUtil.addUrlPrefix(user.getHeadPicFileName()));
            }
        }

        if (Objects.nonNull(param.getDoctorId()) && Objects.nonNull(vo.getPatientId())) {
            DoctorPatient doctorPatient = tagDao.findByDoctorIdAndPatientId(param.getDoctorId(), vo.getPatientId());
            if (Objects.nonNull(doctorPatient)) {
                vo.setRemarkName(doctorPatient.getRemarkName());
                vo.setRemark(doctorPatient.getRemark());
            }
        }

        return vo;
    }

    private void setDiseaseInfo(CheckInVO vo) {
        if (vo == null || StringUtil.isEmpty(vo.getDiseaseId())) {
            return;
        }
        String[] diseaseIds = vo.getDiseaseId().split(",");
        List<DiseaseTypeVO> list = baseDataDao.getDiseaseType(Arrays.asList(diseaseIds));
        if (list == null || list.isEmpty()) {
            return;
        }
        String description = "";
        for (DiseaseTypeVO dv : list) {
            description += "," + dv.getName();
        }
        vo.setDescription(description.substring(1));
    }

    @Override
    public List<DiseaseTypeVO> getRecommendDisease(Integer docId) {
        /**
         *  1、用户选择系统推荐疾病只能选第三级疾病，如果该医生设置擅长是第二级疾病系统时，把该疾病系统的第三级疾病展示出来，
         最多展示20个。
         2、用户搜索时可以搜索整个平台的第三级疾病。
         3、如果没有设置，推荐为空，唤起键盘
         4、如果已选3个疾病，回到所患疾病界面，再点击所患疾病进入搜索界面。底部显示已选疾病，推荐上有所选疾病，则显示已勾选状态，再次搜索疾病时，搜索列表中有已选择的疾病则显示勾选状态。
         */
        if (docId == null) {
            throw new ServiceException("医生ID不能为空");
        }
        Doctor doc = userManager.getUser(docId).getDoctor();
        List<DiseaseTypeVO> result = new ArrayList<DiseaseTypeVO>();
        if (doc != null) {
            List<String> eList = doc.getExpertise();
            eList = eList == null ? new ArrayList<String>() : eList;
            List<DiseaseTypeVO> list = baseDataDao.getDiseaseType(eList);//主要是为了二级排序
            if (list == null || list.isEmpty()) {
                return result;
            }
            //先添加三级病种
            for (DiseaseTypeVO vo : list) {
                if (result.size() < 20) {
                    if (vo.isLeaf()) {
                        result.add(vo);
                    }
                } else {
                    return result;
                }
            }
            //再添加二级病种下的三级病种
            for (DiseaseTypeVO vo : list) {
                if (result.size() < 20) {
                    if (!vo.isLeaf()) {
                        List<DiseaseTypeVO> dList = baseDataDao.getDiseaseByParent(vo.getId());
                        int add = 20 - result.size();
                        if (dList.size() <= add) {
                            result.addAll(dList);
                        } else {
                            result.addAll(dList.subList(0, add));
                        }
                    }
                } else {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * </p>修改报到状态</p>
     *
     * @param param
     * @return 用户id
     * @author fanp
     * @date 2015年9月10日
     */
    public String updateStatus(CheckInParam param) throws HttpApiException {
        if (param.getCheckInId() == null) {
            throw new ServiceException("报到有误");
        }
        if (param.getDoctorId() == null) {
            throw new ServiceException("报到有误");
        }
        if (param.getStatus() == null) {
            throw new ServiceException("请选择处理方式");
        }

        CheckIn checkIn = checkInMapper.getById(param.getCheckInId());
        if (checkIn == null) {
            // 报到不存在
            return null;
        }
        if (!checkIn.getDoctorId().equals(param.getDoctorId())) {
            // 订单错误,不属于当前医生
            return null;
        }
        if (!checkIn.getStatus().equals(OrderEnum.CheckInStatus.init.getIndex())) {
            throw new ServiceException("报到已处理");
        }

        String docName = null;
        String orderId = null;
        String url = null;
        if (param.getStatus() == OrderEnum.CheckInStatus.confirm.getIndex()) {
            // 添加订单
            Order order = new Order();
            order.setUserId(checkIn.getUserId());
            order.setDoctorId(param.getDoctorId());
            if (order.getDoctorId() != null) {
                //如果没有加入集团，则忽略
                GroupDoctor gdoc = new GroupDoctor();
                gdoc = groupDoctorService.findOneByUserIdAndStatus(order.getDoctorId(), "C");
                order.setGroupId(gdoc == null ? null : gdoc.getGroupId());
            }
            order.setPackId(0);
            order.setPackType(0);
            order.setPrice(0L);
            order.setCreateTime(System.currentTimeMillis());
            order.setFinishTime(System.currentTimeMillis());
            order.setPatientId(checkIn.getPatientId());
            if (param.getDiseaseIds() != null && param.getDiseaseIds().size() > 0) {
                String dis = param.getDiseaseIds().toString();
                order.setDiseaseID(dis.substring(1, dis.length() - 1));
            }
            order.setOrderNo(orderService.nextOrderNo());
            order.setTimeLong(0);
            order.setOrderType(OrderEnum.OrderType.checkIn.getIndex());
            order.setOrderStatus(OrderEnum.OrderStatus.已完成.getIndex());
            order.setRefundStatus(OrderEnum.OrderRefundStatus.refundWait.getIndex());
            orderService.add(order);
            // 确定报到，关联订单
            param.setOrderId(order.getId());
            checkInMapper.update(param);

            // 添加患者信息到t_patient_disease
            addPatientDisease(order);
            //创建会话
            Pack pack = new Pack();
            pack.setPackType(PackType.checkin.getIndex());
            OrderSessionContainer container = orderService.processOrderSession(order, pack, null);
            //添加病程
            addHistoryRecord(order, checkIn.getId(), container.getMsgGroupId());
            // 添加好友关系
            //baseUserService.setDoctorPatient(param.getDoctorId(), checkIn.getUserId());
            //2016-12-12医患关系的变化
            baseUserService.addDoctorPatient(param.getDoctorId(), checkIn.getPatientId(), checkIn.getUserId());
            //添加好友通知
            businessMsgService.sendEventFriendChange(EventEnum.ADD_FRIEND, param.getDoctorId().toString(), checkIn.getUserId().toString());
            //生成订单时，自动附上患者标签
            relationService.addRelationTag2(PackUtil.convert(order));
            // 关注医生公众号
            pubGroupService.addSubUser(param.getDoctorId(), checkIn.getUserId());
            // 关注医生集团公众号
            GroupDoctor groupDoctor = groupDoctorService.findOneByUserId(param.getDoctorId());
            if (groupDoctor != null) {
                pubGroupService.addSubUser(groupDoctor.getGroupId(), String.valueOf(checkIn.getUserId()), 1);
            }
            // 发送通知
            BaseUserVO doctor = baseUserService.getUser(param.getDoctorId());
            docName = doctor.getName();
            List<ImgTextMsg> list = new ArrayList<ImgTextMsg>();
            ImgTextMsg msg = new ImgTextMsg();
            msg.setStyle(7);
            msg.setTitle("患者报到");
            msg.setTime(System.currentTimeMillis());
            msg.setContent(docName + "医生同意了您的报到，等待医生答复。\n您也可以通过购买医生套餐，直接与医生沟通。");
            list.add(msg);
            businessMsgService.sendTextMsg(String.valueOf(order.getUserId()), SysGroupEnum.TODO_NOTIFY, list, null);
            businessMsgService.sendEventDoctorDisturb(EventEnum.NEW_CHECKIN, String.valueOf(param.getDoctorId()), String.valueOf(param.getUserId()));

//            if(checkIn.getCheckInFrom() != null && checkIn.getCheckInFrom().intValue() == CheckInParam.CheckInFrom.App.getIndex()){
            Patient p = patientService.findByPk(checkIn.getPatientId());
            if (p != null) {
                MsgDocument msgDocument = new MsgDocument();
                String path = MessageFormat.format(QiniuUtil.QINIU_URL(), QiniuUtil.DEFAULT_BUCKET, "/pack/checkin.png");
                msgDocument.setHeaderIcon(path);
                Map<String, Object> param_ = new HashMap<String, Object>();
                param_.put("doctorId", checkIn.getDoctorId());
                param_.put("patientId", checkIn.getPatientId());
                msgDocument.setBizParam(param_);
                msgDocument.setHeader("患者报到");

                List<DocInfo> dlist = new ArrayList<DocInfo>();
                DocInfo docInfo = new DocInfo();
                docInfo.setTitle("患者");
                String name = p.getUserName() == null ? p.getTelephone() : p.getUserName();
                String sex = p.getSex() == null ? BusinessUtil.getSexName(null) : BusinessUtil.getSexName(Integer.valueOf(p.getSex()));
                String age = p.getAgeStr() == null ? "" : p.getAgeStr();
                docInfo.setContent(name + " " + sex + " " + age);
                docInfo.setType(0);
                dlist.add(docInfo);

                CheckInParam cParam = new CheckInParam();
                cParam.setCheckInId(checkIn.getId());
                CheckInVO casePo = caseMapper.getByCheckIn(cParam);
                if (casePo != null) {
                    if (StringUtil.isNotEmpty(casePo.getMessage())) {
                        docInfo = new DocInfo();
                        docInfo.setTitle("病情描述");
                        docInfo.setContent(casePo.getMessage());
                        docInfo.setType(0);
                        dlist.add(docInfo);
                    }

                    List<String> urls = imageDataService.findImgData(ImageDataEnum.caseImage.getIndex(), casePo.getCaseId());
                    if (urls != null && urls.size() > 0) {
                        List<Map<String, Object>> d9List = new ArrayList<>();
                        for (String u : urls) {
                            Map<String, Object> d9map = new HashMap<>();
                            d9map.put("url", u);
                            d9List.add(d9map);
                        }

                        docInfo = new DocInfo();
                        docInfo.setTitle("影像资料");
                        docInfo.setType(1);
                        docInfo.setPic(d9List);
                        dlist.add(docInfo);
                    }
                }
                msgDocument.setList(dlist);
                GuideMsgHelper.getInstance().sendMsgDocument(container.getMsgGroupId(), "", null, msgDocument, false);
            }

            List<ImgTextMsg> msgs = new ArrayList<ImgTextMsg>();
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            imgTextMsg.setTitle("医生同意了您的患者报到");
            imgTextMsg.setContent("医生已经对您的病历进行管理。在后期诊治中，医生会参考您提交的病历，对您的病情作出更准确的判断。");
            imgTextMsg.setTime(System.currentTimeMillis());
            imgTextMsg.setStyle(7);
            msgs.add(imgTextMsg);
            businessMsgService.sendTextMsgToGid(SysUserEnum.SYS_001.getUserId(), container.getMsgGroupId(), msgs, null);

            Integer openFlag = getCheckInGiveTimes(order.getDoctorId());
            if (openFlag.intValue() == 1) {
                msgs.clear();
                imgTextMsg.setTitle("医生已赠送沟通次数");
                imgTextMsg.setContent("本次咨询含医生3次回复。为节省沟通次数，提问时请尽量一次性描述病情与问题。");
                imgTextMsg.setTime(System.currentTimeMillis());
                imgTextMsg.setStyle(7);
                msgs.add(imgTextMsg);
                businessMsgService.sendTextMsgToGid(SysUserEnum.SYS_001.getUserId(), container.getMsgGroupId(), msgs, null);
            }
//            }

            url = PropertiesUtil.getContextProperty("health.server") + APP_DOWN_PATIENT;//指向App下载
            orderId = order.getId() + "";
        } else if (param.getStatus() == OrderEnum.CheckInStatus.cancel.getIndex()) {
            // 取消报到
            param.setOrderId(0);
            checkInMapper.update(param);
            businessMsgService.sendEventDoctorDisturb(EventEnum.NEW_CHECKIN, String.valueOf(param.getDoctorId()), String.valueOf(param.getUserId()));
            url = PropertiesUtil.getContextProperty("health.server") + "/wechat/web/#/doctorDetails/" + param.getDoctorId() + "/0";//指向医生H5主页

            // 发送通知
            BaseUserVO doctor = baseUserService.getUser(param.getDoctorId());
            List<ImgTextMsg> list = new ArrayList<ImgTextMsg>();
            ImgTextMsg msg = new ImgTextMsg();
            msg.setStyle(7);
            msg.setTitle("患者报到");
            msg.setTime(System.currentTimeMillis());
            msg.setContent(doctor.getName() + "医生拒绝了您的患者报到");
            list.add(msg);
            businessMsgService.sendTextMsg(String.valueOf(checkIn.getUserId()), SysGroupEnum.TODO_NOTIFY, list, null);
        } else {
            throw new ServiceException("请选择处理方式");
        }
        //如果是  微信端 则需要发送对应的微信报道结果卡片,同时还需要下载微信图片到本地然后上传到七牛服务器，再更新或者添加图片的地址
        if (checkIn.getCheckInFrom() != null && checkIn.getCheckInFrom().intValue() == CheckInParam.CheckInFrom.WX.getIndex()) {
            if (StringUtil.isEmpty(docName)) {
                BaseUserVO user = baseUserService.getUser(param.getDoctorId());
                docName = user == null ? "" : user.getName();
            }
            User user = userManager.getUser(checkIn.getUserId());

            if (user != null) {
                Patient patient = patientService.findByPk(checkIn.getPatientId());
                String patientName = "";
                if (patient != null) {
                    patientName = patient.getUserName();
                }
                wxManager.checkInResultNotify(user.getWeInfo().getMpOpenid(), url, patientName, docName, param.getStatus());
            }
        }
        return orderId;
    }

    private void addHistoryRecord(Order order, Integer checkInId, String msgGroupId) {
        CheckInParam checkParam = new CheckInParam();
        checkParam.setCheckInId(checkInId);
        CheckInVO checkInVO = caseMapper.getByCheckIn(checkParam);
        if (checkInVO == null) {
            return;
        }
        OrderParam param = new OrderParam();
        param.setOrderId(order.getId());
        param.setMessage(checkInVO.getMessage());
        param.setCheckInTime(System.currentTimeMillis());
        param.setHospital(checkInVO.getHospital());
        param.setIllHistoryInfoNo(checkInVO.getRecordNum());
        param.setLastTime(checkInVO.getLastCureTime());
        param.setPackType(order.getPackType());
        param.setMsgGroupId(msgGroupId);
        String diseaseIds = checkInVO.getDiseaseId();
        if (StringUtil.isNotEmpty(diseaseIds)) {
            param.setDiseaseIds(Arrays.asList(diseaseIds.split(",")));
        }
        //获取图片
        List<String> urls = imageDataService.findImgData(ImageDataEnum.caseImage.getIndex(), checkInVO.getCaseId());
        param.setPicUrls(urls);
        illHistoryInfoService.addHistoryRecordFromOrderParam(param);
    }

    /**
     * 添加定情资料到 t_disease表
     *
     * @param param
     * @return
     */
    private Integer addDisease(CheckInParam param) {
        Integer result = 0;
        CheckInVO caseObject = caseMapper.selectCaseByCheckInId(param);
        if (null != caseObject) {
            DiseaseParam diseaseParam = new DiseaseParam();
            diseaseParam.setDiseaseDesc(caseObject.getMessage());

            List<String> urls = imageDataService.findImgData(ImageDataEnum.caseImage.getIndex(), caseObject.getCaseId());
            if (null != urls && urls.size() > 0) {
                diseaseParam.setImagePaths(getImagesArrays(urls));
            }
            diseaseParam.setPatientId(caseObject.getPatientId());
            diseaseParam.setUserId(caseObject.getUserId());
            diseaseParam.setTelephone(caseObject.getPhone());
            Patient patient = patientMapper.selectByPrimaryKey(caseObject.getPatientId());
            diseaseParam.setAge(DateUtil.calcAge(patient.getBirthday()));
            diseaseParam.setUserName(patient.getUserName());
            diseaseParam.setBirthday(patient.getBirthday());
            diseaseParam.setSex(patient.getSex() == null ? null : (int) patient.getSex());
            diseaseParam.setArea(patient.getArea());
            diseaseParam.setRelation(patient.getRelation());
            result = diseaseService.addDisease(diseaseParam);
        }
        return result;
    }

    private String[] getImagesArrays(List<String> urls) {
        String[] images = null;

        if (null != urls && urls.size() > 0) {
            images = new String[urls.size()];
            for (int i = 0; i < urls.size(); i++) {
                images[i] = urls.get(i);
            }
        } else {
            images = new String[1];
        }
        return images;
    }

    /**
     * 添加"患者报到"于t_patient_disease
     *
     * @param order
     */
    private void addPatientDisease(Order order) {
        if (order.getDoctorId() == null)
            return;

        List<GroupDoctor> gdocs = gdocDao.getByDoctorId(order.getDoctorId(), GroupType.group.getIndex());
        for (GroupDoctor gdoc : gdocs) {
            PatientDisease record = new PatientDisease();
            record.setUserId(order.getUserId());
            record.setDoctorId(order.getDoctorId());
            record.setOrderId(order.getId());
            record.setPatientId(order.getPatientId());
            record.setGroupId(gdoc.getGroupId());
            patientDiseaseMapper.insertSelective(record);
        }
    }

    public void updateCase(Case ca) {

        if (ca == null) {
            throw new ServiceException("病例信息不存在");
        }

        if (ca.getCaseImgs() != null) {
            imageDataService.deleteImgData(ImageDataEnum.caseImage.getIndex(), ca.getId());
            for (String imgPath : ca.getCaseImgs()) {
                ImageData imageData = new ImageData();

                /***begin add  by  liwei  2016年1月25日    不再去除图片********/

//				if(StringUtil.isNotBlank(imgPath) && imgPath.indexOf(PropertiesUtil.getHeaderPrefix())>-1){
//					 imgPath = imgPath.replace(PropertiesUtil.getHeaderPrefix(), "");
//	            }else{
//	                continue;
//	            }
                /***end add  by  liwei  2016年1月25日********/


                imageData.setImageUrl(imgPath);
                imageData.setRelationId(ca.getId());
                imageData.setUserId(ca.getUserId());
                imageData.setImageType(ImageDataEnum.caseImage.getIndex());
                imageDataService.add(imageData);
            }
        }

        caseMapper.updateCase(ca);
    }

    /**
     * 获取新报道的个数
     *
     * @param doctorId
     * @return
     */
    public int getNewCheckInCount(Integer doctorId) {
        return checkInMapper.getNewCheckInCount(doctorId);
    }

    public boolean isCheckIn(Integer doctorId, Integer userId) {
        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        checkIn.setDoctorId(doctorId);

        List<CheckInVO> vlist = checkInMapper.getCheckInByParam(checkIn);
        if (vlist != null && !vlist.isEmpty() && vlist.get(0).getStatus() != CheckInStatus.cancel.getIndex()) {
            return true;
        }
        return false;
    }

    @Override
    public List<Patient> getPatientsWithStatusByDocAndCreater(Integer docId, Integer creator) {
        if (creator == null || creator == 0) {
            return new ArrayList<Patient>();
        }
        if (docId == null) {
            docId = 0;
        }
        List<Patient> patients = patientService.findByCreateUser(creator);
        if (patients == null || patients.isEmpty()) {
            return new ArrayList<Patient>();
        }

        CheckIn po = new CheckIn();
        po.setDoctorId(docId);
        po.setUserId(creator);
        List<CheckInVO> CheckInVOs = checkInMapper.getCheckInByParam(po);
        return setCheckInStatus(patients, CheckInVOs);
    }

    private List<Patient> setCheckInStatus(List<Patient> pList, List<CheckInVO> cList) {
        if (cList == null) {
            cList = new ArrayList<CheckInVO>();
        }
        Patient selt = null;
        for (int i = 0; i < pList.size(); i++) {
            Patient p = pList.get(i);
            if (p.getRelation().equals(SysConstants.ONESELF)) {
                selt = p;
            }
            int status = 0;
            for (CheckInVO c : cList) {
                if (c.getPatientId().intValue() == p.getId().intValue()) {
                    status = c.getStatus();
                    break;
                }
            }
            p.setCheckInStatus(status);
        }
        if (selt != null) {
            pList.remove(selt);
            pList.add(0, selt);
        }
        return pList;
    }

    @Override
    public Map<String, Object> updateCheckInGiveStatus(Integer docId, Integer status) {
        if (docId == null || status == null) {
            throw new ServiceException("参数非空");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        userManager.updateDoctorCheckInGiveStatus(docId, status);
        map.put("result", true);
        return map;
    }
}
