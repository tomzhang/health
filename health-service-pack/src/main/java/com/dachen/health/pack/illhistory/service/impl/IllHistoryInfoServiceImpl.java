package com.dachen.health.pack.illhistory.service.impl;

import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.drug.api.client.DrugApiClientProxy;
import com.dachen.drug.api.entity.CGoods;
import com.dachen.drug.api.entity.CRecipeVO;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.base.utils.SortByChina;
import com.dachen.health.checkbill.dao.CheckBillDao;
import com.dachen.health.checkbill.entity.po.CheckBill;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.common.listener.OrderBusinessListener;
import com.dachen.health.commons.constants.*;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.utils.OrderNotify;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.pack.consult.dao.ConsultationPackDao;
import com.dachen.health.pack.consult.dao.ElectronicIllCaseDao;
import com.dachen.health.pack.consult.entity.po.*;
import com.dachen.health.pack.illhistory.dao.IllHistoryInfoDao;
import com.dachen.health.pack.illhistory.dao.IllHistoryRecordDao;
import com.dachen.health.pack.illhistory.dao.IllRecordTypeUsedDao;
import com.dachen.health.pack.illhistory.dao.PatientInfoDao;
import com.dachen.health.pack.illhistory.entity.po.*;
import com.dachen.health.pack.illhistory.entity.vo.*;
import com.dachen.health.pack.illhistory.service.IllHistoryInfoService;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.vo.CheckInVO;
import com.dachen.health.pack.order.mapper.CheckInMapper;
import com.dachen.health.pack.order.mapper.OrderDoctorMapper;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.patient.mapper.*;
import com.dachen.health.pack.patient.model.*;
import com.dachen.health.pack.patient.model.vo.ImageDataParam;
import com.dachen.health.user.dao.IRelationDao;
import com.dachen.health.user.dao.ITagDao;
import com.dachen.health.user.entity.po.Tag;
import com.dachen.health.user.entity.po.TagUtil;
import com.dachen.health.user.entity.vo.RelationVO;
import com.dachen.health.user.service.IRelationService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.data.response.GroupUserInfo;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.util.SdkBeanUtils;
import com.dachen.util.ParserHtmlUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 病历相关的Service层 Created by fuyongde on 2016/12/5.
 */
@Service
public class IllHistoryInfoServiceImpl implements IllHistoryInfoService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IllHistoryInfoDao illHistoryInfoDao;

    @Autowired
    private PatientInfoDao patientInfoDao;

    @Autowired
    private IllHistoryRecordDao illHistoryRecordDao;

    @Autowired
    private UserRepository userRepository;

    @Resource
    private PatientMapper patientMapper;


    @Resource
    private DiseaseMapper diseaseMapper;

    @Resource
    private PackMapper packMapper;

    @Autowired
    private IBaseDataDao baseDataDao;

    @Autowired
    private IllRecordTypeUsedDao illRecordTypeUsedDao;

    @Autowired
    private ITagDao tagDao;

    @Autowired
    private IRelationDao relationDao;

    @Autowired
    private IRelationService relationService;

    @Autowired
    private IBaseUserService baseUserService;

    @Autowired
    private OrderDoctorMapper orderDoctorMapper;

    @Autowired
    private CheckBillDao checkBillDao;

    @Resource
    private IBusinessServiceMsg businessServiceMsg;

    @Autowired
    private OrderSessionMapper orderSessionMapper;

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private CureRecordMapper cureRecordMapper;

    @Autowired
    private CareSummaryMapper careSummaryMapper;

    @Resource
    private ImageDataMapper imageDataMapper;

    @Resource
    protected CarePlanApiClientProxy carePlanApiClientProxy;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IMsgService msgService;

    @Autowired
    private ElectronicIllCaseDao electronicIllCaseDao;
    
    @Autowired
    private ConsultationPackDao consultationPackDao;

    @Override
    public void fixOldData() {
        // 1、先查询当前所有的订单
        List<Order> allOrders = orderMapper.findAllWithNoActive();

        //查询出所有的旧病历中的项
        List<IllCaseType> allIllCaseTypes = electronicIllCaseDao.getAllIllCaseTypes();
        Map<String, IllCaseType> illCaseTypeMap = Maps.newHashMap();
        if (allIllCaseTypes != null && allIllCaseTypes.size() > 0) {
            for (IllCaseType illCaseType : allIllCaseTypes) {
                illCaseTypeMap.put(illCaseType.getId(), illCaseType);
            }
        }

        // 删除患者报道的脏数据
        illHistoryRecordDao.removeDirtyCheckInData();

        // 删除健康关怀的脏数据
        illHistoryRecordDao.removeDirtyCareData();

        List<String> oldIllCaseIds = Lists.newArrayList();

        if (allOrders != null && allOrders.size() > 0) {
            int i = 0;
            for (Order order : allOrders) {
                i++;
                logger.info("--->>>处理第{}条订单，订单id为：{}", i, order.getId());
                System.out.println("--->>>处理第" + i + "条订单，订单id为：" + order.getId());

                Integer patientId = order.getPatientId();
                Integer userId = order.getUserId();
                Integer doctorId = order.getDoctorId();

                if (StringUtils.isNotBlank(order.getIllCaseInfoId())) {
                    oldIllCaseIds.add(order.getIllCaseInfoId());
                }

                // 3、根据patientId，查询该患者的病例的列表
                List<IllHistoryInfo> illHistoryInfos = illHistoryInfoDao.getByPatientId(patientId);

                IllHistoryInfo illHistoryInfo = null;

                boolean oldSession = false;

                if (illHistoryInfos != null && illHistoryInfos.size() > 0) {
                    illHistoryInfo = illHistoryInfos.get(0);
                } else {
                    //4、新建病历
                    PatientInfo patientInfo = patientInfoDao.findByDoctorIdAndPatientId(doctorId, patientId);
                    if (patientInfo == null) {
                        Patient patient = patientMapper.selectByPrimaryKey(patientId);
                        if (patient == null) {
                            logger.info("--->>>第{}条订单，由于找不到该患者，无法处理，订单id为{}", i, order.getId());
                            System.out.println("--->>>第"+ i +"条订单，由于找不到该患者，无法处理，订单id为" + order.getId());
                            continue;
                        }
                        patientInfo = new PatientInfo();
                        patientInfo.setDoctorId(doctorId);
                        patientInfo.setPatientId(patientId);
                        patientInfoDao.insert(patientInfo);
                    }
                    illHistoryInfo = illHistoryInfoDao.createIllHistoryInfo(doctorId, userId, patientId, patientInfo.getId(), false);
                    //创建病历时为初诊
                    oldSession = true;
                }

                Integer type = null;

                if (order.getPackType() == null) {
                    logger.info("--->>>第{}条订单，由于套餐类型为空，无法处理，订单id为{}", i, order.getId());
                    System.out.println("--->>>第"+ i +"条订单，由于套餐类型为空，无法处理，订单id为" + order.getId());
                    continue;
                }

                if (order.getPackType().intValue() == 0) {
                    type = IllHistoryEnum.IllHistoryRecordType.checkIn.getIndex();
                } else if (order.getPackType().intValue() == 1 || order.getPackType().intValue() == 2 ||
                        order.getPackType().intValue() == 11 || order.getPackType().intValue() == 12
                        ) {
                    type = IllHistoryEnum.IllHistoryRecordType.order.getIndex();
                } else if (order.getPackType().intValue() == 3) {
                    type = IllHistoryEnum.IllHistoryRecordType.care.getIndex();
                } else if (order.getPackType().intValue() == 8) {
                    type = IllHistoryEnum.IllHistoryRecordType.consultation.getIndex();
                } else {
                    logger.info("--->>>第{}条订单，由于订单类型不是所需类型，无法处理，订单id为{}", i, order.getId());
                    System.out.println("--->>>第" + i + "条订单，由于订单类型不是所需类型，无法处理，订单id为" + order.getId());
                    continue;
                }

                //查询当前订单是否创建病程，若存在该病程，则循环下一条记录
                IllHistoryRecord illHistoryRecordTemp = illHistoryRecordDao.findByOrderIdAndType(order.getId(), type);

                if (illHistoryRecordTemp != null) {

                    boolean needFix = false;

                    if (StringUtils.isBlank(illHistoryRecordTemp.getIllHistoryInfoId())) {
                        //若病程没有病历id，则需要将该病程关联到病历，并将医生加入到病历中
                        illHistoryRecordDao.updateIllHistoryInfoId(illHistoryRecordTemp.getId(), illHistoryInfo.getId());
                        //当前订单不存在病程，则把当前订单的医生加入到病历中
                        List<Integer> doctorIds = illHistoryInfo.getDoctorIds();
                        if (doctorIds == null) {
                            doctorIds = new ArrayList<>();
                            doctorIds.add(order.getDoctorId());
                        }
                        if (!doctorIds.contains(order.getDoctorId())) {
                            doctorIds.add(order.getDoctorId());
                        }
                        illHistoryInfo.setDoctorIds(doctorIds);
                        //更新病历中的医生
                        illHistoryInfoDao.updateIllHistoryInfo(illHistoryInfo);
                        needFix = true;
                    }

                    if (StringUtils.isBlank(order.getIllHistoryInfoId())) {
                        //订单中，没有存储新的病历id，针对这种数据，也要修复病历id
                        order.setIllHistoryInfoId(illHistoryInfo.getId());
                        order.setOldSession(oldSession);
                        orderMapper.update(order);
                        needFix = true;
                    }


                    if (order.getOrderStatus() == null || order.getOrderStatus().intValue() == OrderEnum.OrderStatus.已取消.getIndex()) {
                        //删除这样的病程
                        illHistoryRecordDao.removeDirtyOrderStatus(illHistoryRecordTemp.getId());
                    }

                    //修复健康关怀病程中专家组包含主医生的数据
                    if (illHistoryRecordTemp.getType() != null && illHistoryRecordTemp.getType().intValue() == IllHistoryEnum.IllHistoryRecordType.care.getIndex()
                            && illHistoryRecordTemp.getRecordCare() != null && illHistoryRecordTemp.getRecordCare().getGroupDoctors() != null
                            && illHistoryRecordTemp.getRecordCare().getMainDoctor() != null
                            && illHistoryRecordTemp.getRecordCare().getGroupDoctors().contains(illHistoryRecordTemp.getRecordCare().getMainDoctor())) {
                        illHistoryRecordTemp.getRecordCare().getGroupDoctors().remove(illHistoryRecordTemp.getRecordCare().getMainDoctor());
                        illHistoryRecordDao.fixCareGroupDoctors(illHistoryRecordTemp.getId(), illHistoryRecordTemp.getRecordCare().getGroupDoctors());
                    }

                    //修复这种数据的诊治情况
                    if (illHistoryRecordTemp.getType() != null && (illHistoryRecordTemp.getType().intValue() == IllHistoryEnum.IllHistoryRecordType.care.getIndex()
                            || illHistoryRecordTemp.getType().intValue() == IllHistoryEnum.IllHistoryRecordType.order.getIndex()
                        )) {
                        IllCaseInfo illCaseInfo = electronicIllCaseDao.getByOrderId(order.getId());
                        if (illCaseInfo != null) {
                            // 健康关怀需要修复诊治情况
                            List<IllCaseTypeContent> illCaseTypeContents = electronicIllCaseDao.getIllCaseTypeContentListByIllcaseId(illCaseInfo.getId());
                            if (illCaseTypeContents != null && illCaseTypeContents.size() > 0) {
                                for(IllCaseTypeContent illCaseTypeContent : illCaseTypeContents) {
                                    if (illCaseTypeContent.getIllCaseTypeId() != null && illCaseTypeMap.get(illCaseTypeContent.getIllCaseTypeId()) != null) {
                                        IllCaseType illCaseType = illCaseTypeMap.get(illCaseTypeContent.getIllCaseTypeId());
                                        if (illCaseType != null && StringUtils.equals("诊治情况", illCaseType.getTypeName())) {
                                            if (illHistoryRecordTemp.getType().intValue() == IllHistoryEnum.IllHistoryRecordType.care.getIndex()) {
                                                illHistoryRecordDao.fixRecordCareTreatCase(illHistoryRecordTemp.getId(), illCaseTypeContent.getContentTxt());
                                            } else if (illHistoryRecordTemp.getType().intValue() == IllHistoryEnum.IllHistoryRecordType.order.getIndex()) {
                                                illHistoryRecordDao.fixRecordOrderTreatCase(illHistoryRecordTemp.getId(), illCaseTypeContent.getContentTxt());
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (needFix == false) {
                        logger.info("--->>>第{}条订单，已存在病程，不做处理，订单id为{}", i, order.getId());
                        System.out.println("--->>>第" + i + "条订单，已存在病程，不做处理，订单id为" + order.getId());
                    } else {
                        logger.info("--->>>第{}条订单，处理完毕，订单id为{}", i, order.getId());
                        System.out.println("--->>>第" + i + "条订单，处理完毕，订单id为" + order.getId());
                    }

                    continue;
                }

                if (order.getOrderStatus() == null) {
                    //订单状态为空的数据，不进行修复
                    continue;
                }

                if (order.getOrderStatus().intValue() == OrderEnum.OrderStatus.已取消.getIndex()) {
                    //订单状态为已取消的，不进病程，但是要把病历id关联上
                    order.setIllHistoryInfoId(illHistoryInfo.getId());
                    order.setOldSession(oldSession);
                    orderMapper.update(order);

                    List<Integer> doctorIds = illHistoryInfo.getDoctorIds();
                    if (doctorIds == null) {
                        doctorIds = new ArrayList<>();
                        doctorIds.add(order.getDoctorId());
                    }
                    if (!doctorIds.contains(order.getDoctorId())) {
                        doctorIds.add(order.getDoctorId());
                    }
                    illHistoryInfo.setDoctorIds(doctorIds);
                    //更新病历
                    illHistoryInfoDao.updateIllHistoryInfo(illHistoryInfo);

                    continue;
                }

                //当前订单不存在病程，则把当前订单的医生加入到病历中
                List<Integer> doctorIds = illHistoryInfo.getDoctorIds();
                if (doctorIds == null) {
                    doctorIds = new ArrayList<>();
                    doctorIds.add(order.getDoctorId());
                }
                if (!doctorIds.contains(order.getDoctorId())) {
                    doctorIds.add(order.getDoctorId());
                }
                illHistoryInfo.setDoctorIds(doctorIds);
                //更新病历
                illHistoryInfoDao.updateIllHistoryInfo(illHistoryInfo);

                // 5、根据当前订单创建病程
                IllHistoryRecord illHistoryRecord = new IllHistoryRecord();
                illHistoryRecord.setIllHistoryInfoId(illHistoryInfo.getId());
                illHistoryRecord.setCreateTime(order.getCreateTime());
                illHistoryRecord.setUpdateTime(order.getCreateTime());

                Integer packType = order.getPackType();
                if (packType.intValue() == PackEnum.PackType.careTemplate.getIndex()) {
                    // 健康关怀
                    illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.care.getIndex());
                    illHistoryRecord.setName("健康关怀");
                    RecordCare recordCare = new RecordCare();
                    recordCare.setOrderId(order.getId());
                    recordCare.setStartTime(order.getCreateTime());

                    // 查询原来的illCase，获取其中的图片
                    IllCaseInfo illCaseInfo = electronicIllCaseDao.getByOrderId(order.getId());
                    if (illCaseInfo != null) {
                        recordCare.setPics(illCaseInfo.getImageUlrs());
                        recordCare.setDiseaseDesc(illCaseInfo.getMainCase());

                        // 健康关怀需要修复诊治情况
                        List<IllCaseTypeContent> illCaseTypeContents = electronicIllCaseDao.getIllCaseTypeContentListByIllcaseId(illCaseInfo.getId());
                        if (illCaseTypeContents != null && illCaseTypeContents.size() > 0) {
                            for(IllCaseTypeContent illCaseTypeContent : illCaseTypeContents) {
                                if (illCaseTypeContent.getIllCaseTypeId() != null && illCaseTypeMap.get(illCaseTypeContent.getIllCaseTypeId()) != null) {
                                    IllCaseType illCaseType = illCaseTypeMap.get(illCaseTypeContent.getIllCaseTypeId());
                                    if (illCaseType != null && StringUtils.equals("诊治情况", illCaseType.getTypeName())) {
                                        recordCare.setTreatCase(illCaseTypeContent.getContentTxt());
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    OrderSession orderSession = orderSessionMapper.findByOrderId(order.getId());
                    if (orderSession != null) {
                        recordCare.setMessageGroupId(orderSession.getMsgGroupId());
                    }

                    Disease disease = diseaseMapper.selectByPrimaryKey(order.getDiseaseId());
                    if (disease != null) {
                        recordCare.setSeeDoctor(disease.getIsSeeDoctor());
                    }

                    Integer orderStatus = order.getOrderStatus();
                    if (orderStatus.intValue() == OrderEnum.OrderStatus.已支付.getIndex() || orderStatus.intValue() == OrderEnum.OrderStatus.已完成.getIndex()) {
                        recordCare.setPay(true);
                    } else {
                        recordCare.setPay(false);
                    }

                    // 设置健康关怀的医生、专家组、健康关怀的名称
                    recordCare.setMainDoctor(order.getDoctorId());
                    List<Integer> groupDoctors = orderDoctorMapper.findDoctorByOrderId(order.getId());
                    if (groupDoctors != null && groupDoctors.size() > 0 && groupDoctors.contains(order.getDoctorId())) {
                        groupDoctors.remove(order.getDoctorId());
                    }
                    recordCare.setGroupDoctors(groupDoctors);

                    Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
                    if (pack != null) {
                        recordCare.setName(pack.getName());
                    }
                    recordCare.setDoctorId(order.getDoctorId());
                    illHistoryRecord.setRecordCare(recordCare);

                } else if (packType.intValue() == PackEnum.PackType.message.getIndex()
                        || packType.intValue() == PackEnum.PackType.phone.getIndex()
                        || packType.intValue() == PackEnum.PackType.integral.getIndex()
                        || packType.intValue() == PackEnum.PackType.online.getIndex()) {
                    // 图文、电话、积分问诊、门诊
                    illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.order.getIndex());
                    RecordOrder recordOrder = new RecordOrder();
                    recordOrder.setOrderId(order.getId());
                    OrderSession orderSession = orderSessionMapper.findByOrderId(order.getId());
                    if (orderSession != null) {
                        recordOrder.setMessageGroupId(orderSession.getMsgGroupId());
                    }
                    recordOrder.setStartTime(order.getCreateTime());
                    IllCaseInfo illCaseInfo = electronicIllCaseDao.getByOrderId(order.getId());
                    if (illCaseInfo != null) {
                        recordOrder.setPics(illCaseInfo.getImageUlrs());
                        recordOrder.setDiseaseDesc(illCaseInfo.getMainCase());

                        // 订单类型的，需要修复诊治情况
                        List<IllCaseTypeContent> illCaseTypeContents = electronicIllCaseDao.getIllCaseTypeContentListByIllcaseId(illCaseInfo.getId());
                        if (illCaseTypeContents != null && illCaseTypeContents.size() > 0) {
                            for(IllCaseTypeContent illCaseTypeContent : illCaseTypeContents) {
                                if (illCaseTypeContent.getIllCaseTypeId() != null && illCaseTypeMap.get(illCaseTypeContent.getIllCaseTypeId()) != null) {
                                    IllCaseType illCaseType = illCaseTypeMap.get(illCaseTypeContent.getIllCaseTypeId());
                                    if (illCaseType != null && StringUtils.equals("诊治情况", illCaseType.getTypeName())) {
                                        recordOrder.setTreatCase(illCaseTypeContent.getContentTxt());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    Disease disease = diseaseMapper.selectByPrimaryKey(order.getDiseaseId());
                    if (disease != null) {
                        recordOrder.setSeeDoctor(disease.getIsSeeDoctor());
                    }
                    Integer orderStatus = order.getOrderStatus();
                    if (orderStatus.intValue() == OrderEnum.OrderStatus.已支付.getIndex() || orderStatus.intValue() == OrderEnum.OrderStatus.已完成.getIndex()) {
                        recordOrder.setPay(true);
                    } else {
                        recordOrder.setPay(false);
                    }

                    if (packType.intValue() == PackEnum.PackType.message.getIndex()) {
                        illHistoryRecord.setName("图文咨询");
                    }
                    if (packType.intValue() == PackEnum.PackType.phone.getIndex()) {
                        illHistoryRecord.setName("电话咨询");
                    }
                    if (packType.intValue() == PackEnum.PackType.integral.getIndex()) {
                        illHistoryRecord.setName("积分问诊");
                    }
                    if (packType.intValue() == PackEnum.PackType.online.getIndex()) {
                        illHistoryRecord.setName("门诊");
                    }
                    recordOrder.setDoctorId(order.getDoctorId());
                    illHistoryRecord.setRecordOrder(recordOrder);

                } else if (packType.intValue() == PackEnum.PackType.checkin.getIndex()) {
                    // 患者报道
                    illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.checkIn.getIndex());
                    illHistoryRecord.setName("患者报到");
                    // 设置患者报道的信息
                    RecordCheckIn recordCheckIn = new RecordCheckIn();
                    recordCheckIn.setCheckInTime(order.getCreateTime());
                    recordCheckIn.setOrderId(order.getId());
                    recordCheckIn.setDoctorId(order.getDoctorId());
                    List<CheckInVO> checkIns = checkInMapper.getCheckInByOrderId(order.getId());
                    if (checkIns != null && checkIns.size() > 0) {
                        CheckInVO checkInVO = checkIns.get(0);
                        recordCheckIn.setMessage(checkInVO.getMessage());
                        recordCheckIn.setHospital(checkInVO.getHospital());
                        recordCheckIn.setIllHistoryInfoNo(checkInVO.getRecordNum());
                        recordCheckIn.setPics(checkInVO.getImageUrls());
                        List<String> diseaseIds = Lists.newArrayList();
                        if (checkInVO.getDiseaseId() != null) {
                            diseaseIds.add(checkInVO.getDiseaseId());
                        }
                        if (diseaseIds != null && diseaseIds.size() > 0) {
                            recordCheckIn.setDiseaseIds(diseaseIds);
                        }
                        recordCheckIn.setLastTime(checkInVO.getLastCureTime());
                    }
                    illHistoryRecord.setRecordCheckIn(recordCheckIn);

                } else if (packType.intValue() == PackEnum.PackType.consultation.getIndex()) {
                    // 会诊
                    illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.consultation.getIndex());
                    illHistoryRecord.setName("远程会诊记录");
                    // 设置会诊的信息
                    RecordConsultiation recordConsultiation = new RecordConsultiation();
                    recordConsultiation.setOrderId(order.getId());
                    Integer orderStatus = order.getOrderStatus();
                    if (orderStatus.intValue() == OrderEnum.OrderStatus.已支付.getIndex() || orderStatus.intValue() == OrderEnum.OrderStatus.已完成.getIndex()) {
                        recordConsultiation.setPay(true);
                    } else {
                        recordConsultiation.setPay(false);
                    }
                   
//                    List<Integer> orderDoctorIds = orderDoctorMapper.findDoctorByOrderId(order.getId());
//                    if (orderDoctorIds != null && orderDoctorIds.size() > 0) {
//                        if (orderDoctorIds.contains(order.getDoctorId())) {
//                            orderDoctorIds.remove(order.getDoctorId());
//                            if (orderDoctorIds.size() > 0) {
//                                recordConsultiation.setConsultationDoctor(orderDoctorIds.get(0));
//                            }
//                        }
//                    }
//                    recordConsultiation.setMainDoctor(order.getDoctorId());
//                    recordConsultiation.setConsultationDoctor(consultationDoctor);
                    
                    GroupConsultationPack pack = consultationPackDao.getById(order.getConsultationPackId());
                    if(pack != null){
                    	 recordConsultiation.setConsultationDoctor(pack.getConsultationDoctor());
                         recordConsultiation.setConsultJoinDocs(new ArrayList<>(pack.getDoctorIds()));
                    }
                    recordConsultiation.setStartTime(order.getCreateTime());
                    if (StringUtils.isNotBlank(order.getIllCaseInfoId())) {
                        IllCaseInfo illCaseInfo = electronicIllCaseDao.getIllCase(order.getIllCaseInfoId());
                        if (illCaseInfo != null) {
                            recordConsultiation.setPics(illCaseInfo.getImageUlrs());
                        }
                    }
                    recordConsultiation.setEndTime(order.getFinishTime());
                    recordConsultiation.setDoctorId(order.getDoctorId());
                    illHistoryRecord.setRecordConsultiation(recordConsultiation);

                } else {
                    // 其他类型的暂时不添加病程
                }

                illHistoryRecordDao.insertWithOutSetTime(illHistoryRecord);

                //将病历id和是否初诊写入到订单表中
                order.setIllHistoryInfoId(illHistoryInfo.getId());
                order.setOldSession(oldSession);
                orderMapper.update(order);
                logger.info("--->>>第{}条订单，处理完毕，订单id为{}", i, order.getId());
                System.out.println("--->>>第" + i + "条订单，处理完毕，订单id为" + order.getId());
            }
        }

        //根据旧的病历，获取患者的信息
        String[] illCaseTypeArray = {"出院", "住院", "手术", "复诊", "首诊", "实验室（影像资料）", "实验室（病历图片）"};
        List<String> illCaseTypeNameList = Arrays.asList(illCaseTypeArray);
        if (oldIllCaseIds != null && oldIllCaseIds.size() > 0) {
            logger.info("--->>>旧病历相关数据共{}条", oldIllCaseIds.size());
            System.out.println("--->>>旧病历相关数据共" + oldIllCaseIds.size() + "条");
            List<IllCasePatientInfo> illCasePatientInfos = electronicIllCaseDao.getIllCasePatients(oldIllCaseIds);
            List<IllCasePatientInfo> illCasePatientInfosTargetList = Lists.newArrayList();
            Map<Integer, IllCasePatientInfo> tempMap = Maps.newHashMap();
            if (illCasePatientInfos != null && illCasePatientInfos.size() > 0) {
                for (IllCasePatientInfo target : illCasePatientInfos) {
                    IllCasePatientInfo temp = tempMap.get(target.getPatientId());
                    if (temp != null) {
                        if (temp.getCreateTime() < target.getCreateTime()) {
                            illCasePatientInfosTargetList.remove(temp);
                            tempMap.remove(temp.getPatientId());

                            illCasePatientInfosTargetList.add(target);
                            tempMap.put(target.getPatientId(), target);
                        }
                    } else {
                        illCasePatientInfosTargetList.add(target);
                        tempMap.put(target.getPatientId(), target);
                    }
                }
            }

            //更新患者的身高、体重、婚姻、职业
            if (illCasePatientInfosTargetList != null && illCasePatientInfosTargetList.size() > 0) {
                logger.info("旧病历共{}个相关的患者数据", illCasePatientInfosTargetList.size());
                System.out.println("旧病历共" + illCasePatientInfosTargetList.size() + "个相关的患者数据");


                int j = 0;

                for (IllCasePatientInfo illCasePatientInfo : illCasePatientInfosTargetList) {
                    j++;
                    logger.info("--->>>处理第{}条患者信息", j);
                    System.out.println("--->>>处理第" + j + "条患者信息");
                    List<IllHistoryInfo> illHistoryInfos = illHistoryInfoDao.getByPatientId(illCasePatientInfo.getPatientId());

                    IllHistoryInfo illHistoryInfo = null;

                    if (illHistoryInfos != null && illHistoryInfos.size() > 0) {
                        illHistoryInfo = illHistoryInfos.get(0);
                    } else {
                        continue;
                    }

                    String patientInfoId = illHistoryInfo.getPatientInfoId();
                    PatientInfo patientInfo = patientInfoDao.findById(patientInfoId);
                    if (patientInfo != null) {
                        // 婚姻状况存在疑问
                        if (StringUtils.equals("true", illCasePatientInfo.getIsMarried()) || StringUtils.equals("1", illCasePatientInfo.getIsMarried())) {
                            patientInfo.setMarriage("已婚");
                        } else {
                            patientInfo.setMarriage("未婚");
                        }
                        patientInfo.setJob(illCasePatientInfo.getJob());
                        patientInfo.setHeight(illCasePatientInfo.getHeight());
                        patientInfo.setWeight(illCasePatientInfo.getWeight());

                        patientInfoDao.update(patientInfoId, patientInfo);
                        logger.info("第{}条患者数据修复完成", j);
                    }
                }
            }

            List<IllCaseTypeContent> illCaseTypeContents = electronicIllCaseDao.getallIllCaseTypeContents();
            logger.info("旧病历共{}个相关的病程数据", illCaseTypeContents.size());
            System.out.println("旧病历共" + illCaseTypeContents.size() + "个相关的病程数据");
            if (illCaseTypeContents != null && illCaseTypeContents.size() > 0) {
                int k = 0;
                for (IllCaseTypeContent illCaseTypeContent : illCaseTypeContents) {
                    k++;
                    for(Order order : allOrders) {
                        if (illCaseTypeContent.getDeal() == null || illCaseTypeContent.getDeal() == false) {
                            if (StringUtils.isNotBlank(order.getIllHistoryInfoId()) && StringUtils.equals(illCaseTypeContent.getIllCaseInfoId(), order.getIllCaseInfoId())) {
                                //说明要加入到当前病历
                                IllHistoryInfo illHistoryInfo = illHistoryInfoDao.findById(order.getIllHistoryInfoId());
                                if(illHistoryInfo != null) {
                                    IllCaseType illCaseType = illCaseTypeMap.get(illCaseTypeContent.getIllCaseTypeId());
                                    if (illCaseType.getTypeName() != null && illCaseType.getTypeName().contains("史")) {
                                        //要插入到简要病史里面
                                        String str = null;
                                        if (StringUtils.isBlank(illHistoryInfo.getBriefHistroy())) {
                                            str = illCaseType.getTypeName() + ":" + illCaseTypeContent.getContentTxt() + ";\r\n";
                                        } else {
                                            str = illHistoryInfo.getBriefHistroy() + illCaseType.getTypeName() + ":" + illCaseTypeContent.getContentTxt() + ";\r\n";
                                        }
                                        illHistoryInfo.setBriefHistroy(str);
                                        illHistoryInfoDao.updateIllHistoryInfo(illHistoryInfo);
                                    } else if (illCaseType.getTypeName() != null && illCaseTypeNameList.contains(illCaseType.getTypeName())) {
                                        //要插入到病程里面
                                        IllHistoryRecord illHistoryRecord = new IllHistoryRecord();
                                        illHistoryRecord.setIllHistoryInfoId(illHistoryInfo.getId());
                                        illHistoryRecord.setName(illCaseType.getTypeName());
                                        illHistoryRecord.setSecret(false);

                                        // 正常添加的病程
                                        RecordNormal recordNormal = new RecordNormal();
                                        recordNormal.setInfo(illCaseTypeContent.getContentTxt());
                                        recordNormal.setRecordType(illCaseType.getId());
                                        recordNormal.setRecordName(illCaseType.getTypeName());
                                        recordNormal.setTime(illCaseTypeContent.getCreateTime());
                                        illHistoryRecord.setRecordNormal(recordNormal);
                                        illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.normal.getIndex());
                                        illHistoryRecordDao.insert(illHistoryRecord);
                                    }

                                    electronicIllCaseDao.setIllCaseTypeContentDeal(illCaseTypeContent.getId());
                                    logger.info("--->>>处理第{}条病程信息", k);
                                    System.out.println("--->>>处理第" + k + "条病程信息");
                                }
                            }
                        }
                    }
                }
            }
        }

        List<IllHistoryInfo> allIllHistoryInfos = illHistoryInfoDao.findAll();
        if (allIllHistoryInfos != null && allIllHistoryInfos.size() > 0) {
            for (IllHistoryInfo illHistoryInfo : allIllHistoryInfos) {
                if (StringUtils.isNotBlank(illHistoryInfo.getBriefHistroy()) && illHistoryInfo.getBriefHistroy().startsWith("null")) {
                    String briefHistoryTemp = illHistoryInfo.getBriefHistroy();
                    String birefHistory = briefHistoryTemp.replaceFirst("null", "");
                    illHistoryInfoDao.fixStartWithNullData(illHistoryInfo.getId(), birefHistory);
                }

                if (Objects.isNull(illHistoryInfo.getDoctorId())) {
                    illHistoryInfoDao.removeNullDoctorId(illHistoryInfo.getId());
                }
            }
        }
    }

    @Override
    public Map<String, Object> getIllHistoryRecordTypes(String parentId) {

        // 1、先获取调用者的身份信息，是医生还是患者
        // 2、若是患者，则从b_checkup表取出患者可以选择项
        // 3、若是医生，则从b_checkup表取出医生可以选择的项，并从t_ill_case_type表中取出来数据，再转换成一致的结构

        Integer userId = ReqUtil.instance.getUserId();
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        Map<String, Object> result = Maps.newHashMap();

        if (user.getUserType() != null && (user.getUserType().intValue() == UserEnum.UserType.doctor.getIndex()
                || user.getUserType().intValue() == UserType.assistant.getIndex())) {
            // 医生
            List<RecordTypeVo> recordTypeVoList = Lists.newArrayList();
            if (StringUtils.equals(parentId, "0")) {
                List<IllCaseType> illCaseTypes = electronicIllCaseDao.forDoctor();
                if (illCaseTypes != null && illCaseTypes.size() > 0) {
                    for (IllCaseType illCaseType : illCaseTypes) {
                        RecordTypeVo recordTypeVo = RecordTypeVo.formIllCaseType(illCaseType);
                        recordTypeVoList.add(recordTypeVo);
                    }
                }
            }

            List<CheckSuggest> checkSuggests = baseDataDao.getCheckSuggestByParentId(parentId);
            if (checkSuggests != null && checkSuggests.size() > 0) {
                for (CheckSuggest checkSuggest : checkSuggests) {
                    RecordTypeVo recordTypeVo = RecordTypeVo.formCheckSuggest(checkSuggest);
                    recordTypeVoList.add(recordTypeVo);
                }
            }
            // 设置全部的可以查看的
            result.put("all", recordTypeVoList);

            IllRecordTypeUsed illRecordTypeUsed = illRecordTypeUsedDao.findByDoctorId(userId);
            if (illRecordTypeUsed != null) {
                List<RecordTypeVo> usedRecordVos = illRecordTypeUsed.getRecordTypeVos();
                Collections.reverse(usedRecordVos);
                result.put("used", usedRecordVos);
            }

        } else {
            // 患者
            List<RecordTypeVo> recordTypeVoList = Lists.newArrayList();
            List<IllCaseType> illCaseTypes = electronicIllCaseDao.forPatient();
            if (illCaseTypes != null && illCaseTypes.size() > 0) {
                for (IllCaseType illCaseType : illCaseTypes) {
                    RecordTypeVo recordTypeVo = RecordTypeVo.formIllCaseType(illCaseType);
                    recordTypeVoList.add(recordTypeVo);
                }
            }

            result.put("all", recordTypeVoList);
        }

        return result;
    }

    @Override
    @Transactional
    public void addIllHistoryRecord(String illHistoryInfoId, Integer doctorId, String illRecordTypeId,
                                    String illRecordTypeParentId, Integer illRecordTypeSource, String illRecordTypeName, String info,
                                    Boolean isSecret, String[] pics, Long time) {
        // 1、先根据doctorId和patientId查出病历的id
        IllHistoryInfo illHistoryInfo = illHistoryInfoDao.getById(illHistoryInfoId);

        if (Objects.isNull(illHistoryInfo)) {
            throw new ServiceException("病历为空");
        }

        // 根据patientId查询userId
        Patient patient = patientMapper.selectByPrimaryKey(illHistoryInfo.getPatientId());
        if (patient == null) {
            throw new ServiceException("患者为空");
        }

        User doctor = userRepository.getUser(illHistoryInfo.getDoctorId());
        if (doctor == null) {
            throw new ServiceException("医生为空");
        }

        Integer currentUserId = ReqUtil.instance.getUserId();

        System.out.println("当前登录用户id：" + currentUserId);

        User createUser = userRepository.getUser(currentUserId);

        System.out.println("当前登录用户姓名：" + createUser.getName() + "，用户类型：" + createUser.getUserType());

        // 2、创建IllHistoryRecord对象
        IllHistoryRecord illHistoryRecord = new IllHistoryRecord();
        illHistoryRecord.setIllHistoryInfoId(illHistoryInfo.getId());
        illHistoryRecord.setName(illRecordTypeName);
        //若当前登录用户为医生或者医生助手，则病程创建者为当前用户，否则为患者
        if (createUser != null && createUser.getUserType() != null &&
                (createUser.getUserType().intValue() == UserType.doctor.getIndex() || createUser.getUserType().intValue() == UserType.assistant.getIndex())) {
            illHistoryRecord.setCreater(currentUserId);
        } else {
            illHistoryRecord.setCreater(illHistoryInfo.getPatientId());
        }
        illHistoryRecord.setSecret(isSecret);
        Long now = System.currentTimeMillis();

        // 3、添加IllHistoryRecord对象
        if (illRecordTypeSource.intValue() == 1) {
            // 正常添加的病程
            RecordNormal recordNormal = new RecordNormal();
            recordNormal.setInfo(info);
            recordNormal.setRecordType(illRecordTypeId);
            recordNormal.setRecordName(illRecordTypeName);
            recordNormal.setTime(time);
            if (pics != null && pics.length > 0) {
                recordNormal.setPics(Arrays.asList(pics));
            }
            illHistoryRecord.setRecordNormal(recordNormal);
            illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.normal.getIndex());

        } else {
            // 添加的检查项
            RecordCheckItem recordCheckItem = new RecordCheckItem();

            // 添加bill,关联patient
            CheckBill checkBill = new CheckBill();
            checkBill.setPatientId(illHistoryInfo.getPatientId());
            checkBill.setCreateTime(now);
            checkBill.setUpdateTime(now);
            checkBill = checkBillDao.insertCheckBill(checkBill);

            // 调用添加检查单
            CheckItem checkItem = new CheckItem();
            checkItem.setCheckUpId(illRecordTypeId);
            checkItem.setItemName(illRecordTypeName);
            checkItem.setFrom(1);
            checkItem.setFromId(checkBill.getId());
            checkItem.setCreateTime(now);
            checkItem.setUpdateTime(now);
            checkItem.setVisitingTime(time);
            List<String> images = Lists.newArrayList();
            if (pics != null) {
                images = Arrays.asList(pics);
                checkItem.setImageList(images);
            }
            checkItem.setResults(info);
            checkBillDao.addCheckItem(checkItem);

            // checkItem的id关联回bill
            checkBill.setCheckItemIds(Lists.newArrayList(checkItem.getId()));
            checkBill.setCheckBillStatus(4);
            checkBillDao.updateCheckbill(checkBill);

            recordCheckItem.setCheckItemId(checkItem.getId());

            illHistoryRecord.setRecordCheckItem(recordCheckItem);
            illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.checkItem.getIndex());

            sendCheckItemToXG(illHistoryInfo.getDoctorId(), patient.getId(), illHistoryInfoId, images, illRecordTypeId, checkItem.getId(), patient.getTelephone(), patient.getArea());
        }

        if (!Objects.isNull(doctorId)) {
            //医生助手添加的，才有这个字段
            illHistoryRecord.setForDoctorId(doctorId);
        }

        illHistoryRecordDao.insert(illHistoryRecord);

        // 4、若当前添加者为医生，则需要向 t_ill_record_type_userd 添加记录或更新记录
        if (createUser != null && createUser.getUserType() != null &&
                (createUser.getUserType().intValue() == UserType.doctor.getIndex() || createUser.getUserType().intValue() == UserType.assistant.getIndex())) {
            // 查询t_ill_record_type_userd表是否存在对应的记录
            IllRecordTypeUsed illRecordTypeUsed = illRecordTypeUsedDao.findByDoctorId(currentUserId);
            RecordTypeVo recordTypeVo = new RecordTypeVo();
            recordTypeVo.setId(illRecordTypeId);
            recordTypeVo.setParentId(illRecordTypeParentId);
            recordTypeVo.setSource(illRecordTypeSource);
            recordTypeVo.setName(illRecordTypeName);
            if (illRecordTypeUsed != null) {
                // 存在常用的记录，则更新记录
                List<RecordTypeVo> recordTypeVos = illRecordTypeUsed.getRecordTypeVos();
                if (recordTypeVos == null) {
                    // 新建一个list对象存储RecordTypeVo
                    recordTypeVos = Lists.newArrayList();
                    recordTypeVos.add(recordTypeVo);
                } else if (recordTypeVos.size() < 5 && !recordTypeVos.contains(recordTypeVo)) {
                    recordTypeVos.add(recordTypeVo);
                } else if (recordTypeVos.size() < 5 && recordTypeVos.contains(recordTypeVo)) {
                    recordTypeVos.remove(recordTypeVo);
                    recordTypeVos.add(recordTypeVo);
                } else if (recordTypeVos.contains(recordTypeVo)) {
                    recordTypeVos.remove(recordTypeVo);
                    recordTypeVos.add(recordTypeVo);
                } else {
                    RecordTypeVo recordTypeVoTemp = recordTypeVos.get(0);
                    recordTypeVos.remove(recordTypeVoTemp);
                    recordTypeVos.add(recordTypeVo);
                }
                illRecordTypeUsed.setRecordTypeVos(recordTypeVos);
                // 调用更新的方法
                illRecordTypeUsedDao.update(currentUserId, illRecordTypeUsed);
            } else {
                // 不存在常用的记录，则插入一条记录
                illRecordTypeUsed = new IllRecordTypeUsed();
                List<RecordTypeVo> recordTypeVos = Lists.newArrayList();
                recordTypeVos.add(recordTypeVo);
                illRecordTypeUsed.setRecordTypeVos(recordTypeVos);
                illRecordTypeUsed.setDoctorId(currentUserId);
                illRecordTypeUsedDao.insert(illRecordTypeUsed);
            }
        }

    }

    @Override
    public IllHistoryRecord addHistoryRecordFromOrderParam(OrderParam orderParam) {
        IllHistoryInfo illHistoryInfo = null;
        boolean oldSession = false;
        //判断是否传入了医生id
        if (!StringUtil.isEmpty(orderParam.getIllHistoryInfoId())) {
            illHistoryInfo = illHistoryInfoDao.findById(orderParam.getIllHistoryInfoId());
            //将对应的医生
            if (illHistoryInfo != null) {
                List<Integer> doctorIds = illHistoryInfo.getDoctorIds();
                if (doctorIds == null) {
                    doctorIds = new ArrayList<>();
                    doctorIds.add(orderParam.getDoctorId());
                }
                if (!doctorIds.contains(orderParam.getDoctorId()) && orderParam.getDoctorId() != null) {
                    doctorIds.add(orderParam.getDoctorId());
                }
                if(orderParam.getConsultDoctorId() != null && !doctorIds.contains(orderParam.getConsultDoctorId().intValue())){
                	doctorIds.add(orderParam.getConsultDoctorId());
                }
                if(orderParam.getConsultJoinDocs() != null){
                	List<Integer> list =  new ArrayList<>(orderParam.getConsultJoinDocs());
                	list.removeAll(doctorIds);
                	doctorIds.addAll(list);
                }
                illHistoryInfo.setDoctorIds(doctorIds);
                //更新病历
                illHistoryInfoDao.updateIllHistoryInfo(illHistoryInfo);
            } else {
                throw new ServiceException("请检查病历id");
            }

        }

        IllHistoryRecord illHistoryRecord = new IllHistoryRecord();
        illHistoryRecord.setSecret(false);
        Order order = orderMapper.getOne(orderParam.getOrderId());

        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        Integer packType = order.getPackType();
        if (packType.intValue() == PackEnum.PackType.careTemplate.getIndex()) {
            // 健康关怀
            illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.care.getIndex());
            illHistoryRecord.setName("健康关怀");

            RecordCare recordCare = new RecordCare();
            recordCare.setOrderId(orderParam.getOrderId());
            recordCare.setStartTime(order.getCreateTime());
            recordCare.setPics(orderParam.getPicUrls());
            recordCare.setDiseaseId(orderParam.getDiseaseID());
            recordCare.setDiseaseDesc(orderParam.getDiseaseDesc());
            recordCare.setDiseaseDuration(orderParam.getDiseaseDuration());
            recordCare.setSeeDoctor(orderParam.getIsSeeDoctor());
            recordCare.setDrugGoodsIds(orderParam.getDrugGoodsIds());
            recordCare.setDrugPicUrls(orderParam.getDrugPicUrls());
            recordCare.setHopeHelp(orderParam.getHopeHelp());
            recordCare.setPay(orderParam.getPay());
            recordCare.setDrugCase(orderParam.getDrugCase());
            recordCare.setTreatCase(orderParam.getTreatCase());
            recordCare.setMessageGroupId(orderParam.getGid());

            // 设置健康关怀的医生、专家组、健康关怀的名称
            recordCare.setMainDoctor(order.getDoctorId());
            List<Integer> groupDoctors = orderDoctorMapper.findDoctorByOrderId(orderParam.getOrderId());
            if (groupDoctors != null && groupDoctors.size() > 0 && groupDoctors.contains(order.getDoctorId())) {
                groupDoctors.remove(order.getDoctorId());
            }
            recordCare.setGroupDoctors(groupDoctors);

            Pack pack = packMapper.selectByPrimaryKey(order.getPackId());
            if (pack != null) {
                recordCare.setName(pack.getName());
            }
            recordCare.setDoctorId(order.getDoctorId());
            recordCare.setDrugInfos(orderParam.getDrugInfos());
            illHistoryRecord.setRecordCare(recordCare);

        } else if (packType.intValue() == PackEnum.PackType.message.getIndex()
                || packType.intValue() == PackEnum.PackType.phone.getIndex()
                || packType.intValue() == PackEnum.PackType.integral.getIndex()
                || packType.intValue() == PackEnum.PackType.online.getIndex()) {
            // 图文、电话、积分问诊、门诊
            illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.order.getIndex());
            RecordOrder recordOrder = new RecordOrder();
            recordOrder.setOrderId(orderParam.getOrderId());
            recordOrder.setMessageGroupId(orderParam.getGid());
            recordOrder.setStartTime(order.getCreateTime());
            recordOrder.setPics(orderParam.getPicUrls());
            recordOrder.setDiseaseId(orderParam.getDiseaseID());
            recordOrder.setDiseaseDesc(orderParam.getDiseaseDesc());
            recordOrder.setDiseaseDuration(orderParam.getDiseaseDuration());
            recordOrder.setSeeDoctor(orderParam.getIsSeeDoctor());
            recordOrder.setDrugGoodsIds(orderParam.getDrugGoodsIds());
            recordOrder.setDrugPicUrls(orderParam.getDrugPicUrls());
            recordOrder.setHopeHelp(orderParam.getHopeHelp());
            recordOrder.setPay(orderParam.getPay());
            recordOrder.setDrugCase(orderParam.getDrugCase());
            recordOrder.setTreatCase(orderParam.getTreatCase());
            if (packType.intValue() == PackEnum.PackType.message.getIndex()) {
                illHistoryRecord.setName("图文咨询");
            }
            if (packType.intValue() == PackEnum.PackType.phone.getIndex()) {
                illHistoryRecord.setName("电话咨询");
            }
            if (packType.intValue() == PackEnum.PackType.integral.getIndex()) {
                illHistoryRecord.setName("积分问诊");
            }
            if (packType.intValue() == PackEnum.PackType.online.getIndex()) {
                illHistoryRecord.setName("门诊");
            }
            recordOrder.setDoctorId(order.getDoctorId());
            recordOrder.setDrugInfos(orderParam.getDrugInfos());
            illHistoryRecord.setRecordOrder(recordOrder);

        } else if (packType.intValue() == PackEnum.PackType.checkin.getIndex()) {
            // 患者报道
            illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.checkIn.getIndex());
            // 设置患者报道的信息
            RecordCheckIn recordCheckIn = new RecordCheckIn();
            recordCheckIn.setMessage(orderParam.getMessage());
            recordCheckIn.setCheckInTime(orderParam.getCheckInTime());
            recordCheckIn.setHospital(orderParam.getHospital());
            recordCheckIn.setIllHistoryInfoNo(orderParam.getIllHistoryInfoNo());
            recordCheckIn.setPics(orderParam.getPicUrls());
            recordCheckIn.setDiseaseIds(orderParam.getDiseaseIds());
            recordCheckIn.setLastTime(orderParam.getLastTime());

            recordCheckIn.setOrderId(orderParam.getOrderId());

            recordCheckIn.setMessageGroupId(orderParam.getMsgGroupId());

            illHistoryRecord.setName("患者报到");
            recordCheckIn.setDoctorId(order.getDoctorId());
            illHistoryRecord.setRecordCheckIn(recordCheckIn);

        } else if (packType.intValue() == PackEnum.PackType.consultation.getIndex()) {
            // 会诊
            illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.consultation.getIndex());
            illHistoryRecord.setName("远程会诊记录");

            // 设置会诊的信息
            RecordConsultiation recordConsultiation = new RecordConsultiation();
            recordConsultiation.setOrderId(orderParam.getOrderId());
            recordConsultiation.setConsultationDoctor(orderParam.getConsultDoctorId());
            recordConsultiation.setConsultJoinDocs(orderParam.getConsultJoinDocs());
            recordConsultiation.setStartTime(order.getCreateTime());
            recordConsultiation.setPics(orderParam.getPicUrls());
            recordConsultiation.setEndTime(orderParam.getEndCreateTime());
            recordConsultiation.setMessageGroupId(orderParam.getGid());
            recordConsultiation.setDoctorId(order.getDoctorId());
            recordConsultiation.setDrugInfos(orderParam.getDrugInfos());
            recordConsultiation.setPay(orderParam.getPay());
            illHistoryRecord.setRecordConsultiation(recordConsultiation);

        } else {
            // 其他类型的暂时不添加病程
        }

        // 设置病历的id
        if (illHistoryInfo == null) {
            illHistoryInfo = illHistoryInfoDao.getByDoctorIdAndPatientId(order.getDoctorId(), order.getPatientId());
        }
        if (illHistoryInfo == null) {
            PatientInfo patientInfo = patientInfoDao.findByDoctorIdAndPatientId(order.getDoctorId(), order.getPatientId());
            if (patientInfo == null) {
                Patient patient = patientMapper.selectByPrimaryKey(order.getPatientId());
                patientInfo = new PatientInfo();
                patientInfo.setDoctorId(order.getDoctorId());
                patientInfo.setPatientId(order.getPatientId());
                patientInfo.setHeight(patient.getHeight());
                patientInfo.setWeight(patient.getWeight());
                patientInfo.setMarriage(patient.getMarriage());
                patientInfo.setJob(patient.getProfessional());
                patientInfoDao.insert(patientInfo);
            }

            illHistoryInfo = illHistoryInfoDao.createIllHistoryInfo(order.getDoctorId(), order.getUserId(), order.getPatientId(), patientInfo.getId(), false);

            /**添加病情资料**/
            illHistoryInfoDao.addIllContentInfo(illHistoryInfo, orderParam.getDiseaseDesc(), orderParam.getTreatCase(), orderParam.getImagePaths() == null ? null : Arrays.asList(orderParam.getImagePaths()));

            //初诊标示
            oldSession = true;
        }

        /**第一次添加初步诊断**/
        /**患者报道无疾病时不添加初步诊断**/
        if (illHistoryRecord.getType() == IllHistoryEnum.IllHistoryRecordType.checkIn.getIndex() && !CollectionUtils.isEmpty(orderParam.getDiseaseIds())) {
            illHistoryInfoDao.addDiagnosis(illHistoryInfo.getId(), null, orderParam.getDiseaseIds().get(0), null, order.getId());
        } else if (illHistoryRecord.getType() != IllHistoryEnum.IllHistoryRecordType.checkIn.getIndex()) {
            illHistoryInfoDao.addDiagnosis(illHistoryInfo.getId(), null, orderParam.getDiseaseID(), null, order.getId());
        }

        illHistoryRecord.setIllHistoryInfoId(illHistoryInfo.getId());
        illHistoryRecordDao.insert(illHistoryRecord);
        //将病历id和是否初诊写入到订单表中
        order.setIllHistoryInfoId(illHistoryInfo.getId());
        order.setOldSession(oldSession);
        orderMapper.update(order);

        return illHistoryRecord;
    }

    @Override
    public void editPationInfo(String illHistoryInfoId, Integer doctorId, Integer patientId, String remarkName, String[] tags, String remark,
                               String height, String weight, String marriage, String job) {

        Integer currentUserId = ReqUtil.instance.getUserId();
        if (Objects.isNull(currentUserId) || currentUserId.intValue() == 0) {
            throw new ServiceException("无此权限");
        }

        IllHistoryInfo illHistoryInfo = null;
        if (StringUtils.isNotBlank(illHistoryInfoId)) {
            illHistoryInfo = illHistoryInfoDao.findById(illHistoryInfoId);
        }
        if (Objects.isNull(illHistoryInfo)) {
            if (!Objects.isNull(doctorId) && !Objects.isNull(patientId)) {
                illHistoryInfo = illHistoryInfoDao.getByDoctorIdAndPatientId(doctorId, patientId);
            }

            if (Objects.isNull(illHistoryInfo)) {
                throw new ServiceException("未找到病历，无法编辑。");
            }
        }

        String patientInfoId = illHistoryInfo.getPatientInfoId();

        if (patientInfoId == null) {
            // 若病例中没有patientInfoId，则更新病例
            PatientInfo patientInfo = new PatientInfo();
            patientInfo.setDoctorId(illHistoryInfo.getDoctorId());
            patientInfo.setPatientId(illHistoryInfo.getPatientId());
            patientInfo.setHeight(height);
            patientInfo.setWeight(weight);
            patientInfo.setJob(job);
            patientInfo.setMarriage(marriage);
            patientInfoDao.insert(patientInfo);
            patientInfoId = patientInfo.getId();
            illHistoryInfoDao.updatePatientInfoId(illHistoryInfo.getId(), patientInfoId);
        }
        PatientInfo patientInfo = patientInfoDao.findById(patientInfoId);
        patientInfo.setHeight(height);
        patientInfo.setWeight(weight);
        patientInfo.setJob(job);
        patientInfo.setMarriage(marriage);
        patientInfoDao.update(patientInfo.getId(), patientInfo);

        User currentUser = userRepository.getUser(currentUserId);
        // 根据当前用户id和患者id查询u_doctor_patient表，只有医生端才能更新患者标签、备注等信息
        if (currentUser != null && currentUser.getUserType() != null && currentUser.getUserType().intValue() == UserType.doctor.getIndex()) {
            DoctorPatient doctorPatient = tagDao.findByDoctorIdAndPatientId(currentUserId, illHistoryInfo.getPatientId());
            if (doctorPatient == null) {
                // 新增一条医患关系(双向的，所以要添加两条)
                Patient patient = patientMapper.selectByPrimaryKey(illHistoryInfo.getPatientId());
                baseUserService.addDoctorPatient(currentUserId, patientId, patient.getUserId(), remarkName, remark);
                doctorPatient = tagDao.findByDoctorIdAndPatientId(currentUserId, patientId);
            }
            // 更新标签
            tagDao.updateDoctorPatient(doctorPatient.getId(), tags, remarkName, remark);

            if (tags != null && tags.length > 0) {
                for (String tag : tags) {
                    Tag tempTag = new Tag();
                    tempTag.setUserId(currentUserId);
                    tempTag.setTagName(tag);
                    tempTag.setTagType(UserEnum.TagType.doctorPatient.getIndex());
                    tempTag.setSys(false);
                    if (!tagDao.existTag(tempTag)) {
                        tagDao.addTag(tempTag);
                    }
                }
            }
        }


        // TagParam tagParam = new TagParam();
        // tagParam.setUserId(doctorId);
        // tagParam.setTagType(UserEnum.TagType.doctorPatient.getIndex());
        // tagParam.setTagNames(tags);
        // relationService.updateUserTag(tagParam);

    }

    @Override
    public void editIllContentInfo(String illHistoryInfoId, String illDesc, List<String> pics, String treatment) {

        Integer userId = ReqUtil.instance.getUserId();
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new ServiceException("用户为空");
        }

        IllContentInfo illContentInfo = new IllContentInfo();
        illContentInfo.setIllDesc(illDesc);
        illContentInfo.setPics(pics);
        illContentInfo.setTreatment(treatment);
        illHistoryInfoDao.updateIllContentInfo(illHistoryInfoId, illContentInfo);

    }

    @Override
    public void addDiagnosis(String illHistoryInfoId, String content, String diseaseId) {
        Integer userId = ReqUtil.instance.getUserId();
        User user = userRepository.getUser(userId);
        if (null == user) {
            throw new ServiceException("非法用户");
        }
        if (user.getUserType().intValue() == UserType.doctor.getIndex()) {
            illHistoryInfoDao.addDiagnosis(illHistoryInfoId, content, diseaseId, userId, null);
        } else {
            illHistoryInfoDao.addDiagnosis(illHistoryInfoId, content, diseaseId, null, null);
        }

    }

    @Override
    public PageVO getDiagnosis(String illHistoryInfoId, Integer pageIndex, Integer pageSize) {
        PageVO page = new PageVO();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);

        if (StringUtil.isEmpty(illHistoryInfoId)) {
            throw new ServiceException("病历id不能为空");
        }

        IllHistoryInfo info = illHistoryInfoDao.findById(illHistoryInfoId);
        if (null == info) {
            throw new ServiceException("找不到该病历信息");
        }

        List<Diagnosis> diagnosiss = info.getDiagnosis();
        if (CollectionUtils.isEmpty(diagnosiss)) {
            page.setTotal(0L);
            return page;
        }

        List<DiagnosisVO> vos = Lists.newArrayList();
        for (Diagnosis diagnosis : diagnosiss) {
            DiagnosisVO vo = covertDiagnosis(diagnosis, info.getPatientId());
            vos.add(vo);
        }

        //根据创建时间倒序排序
        Collections.sort(vos, new Comparator<DiagnosisVO>() {

            @Override
            public int compare(DiagnosisVO o1, DiagnosisVO o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });

        page.setTotal(CollectionUtils.isEmpty(vos) ? 0L : Long.valueOf(vos.size()));

        //内存分页
        if (vos.size() < page.getStart()) {
            vos = Lists.newArrayList();
        } else {
            if ((page.getPageIndex() + 1) * page.getPageSize() < vos.size()) {
                vos = vos.subList(page.getStart(), (page.getPageIndex() + 1) * page.getPageSize());
            } else {
                vos = vos.subList(page.getStart(), vos.size());
            }
        }

        page.setPageData(vos);
        return page;
    }

    @Override
    public Map<String, Object> getAllMyPatients() {
        // 1、先获取当前用户
        Integer currentUserId = ReqUtil.instance.getUserId();
        User user = userRepository.getUser(currentUserId);
        if (user == null) {
            throw new ServiceException("当前用户不存在");
        }

        if (user.getUserType() != null && user.getUserType().intValue() == UserEnum.UserType.doctor.getIndex()) {

            Map<String, Object> result = Maps.newHashMap();
            // 1、查询u_doctor_patient表，查出所有的患者的id
            Integer doctorId = currentUserId;
            List<Integer> patientIds = relationDao.getAllMyPatientIds(doctorId);
            // 2、查询患者表，获取地区，生日、性别信息
            if (patientIds != null && patientIds.size() > 0) {
                PatientExample patientExample = new PatientExample();
                patientExample.setIds(patientIds);
                List<Patient> patients = patientMapper.getByIds(patientExample);
                List<PatientInfoVo> patientInfoVos = Lists.newArrayList();

                if (patients != null && patients.size() > 0) {

                    List<Integer> userIds = Lists.newArrayList();

                    for (Patient patient : patients) {
                        PatientInfoVo patientInfoVo = new PatientInfoVo();
                        patientInfoVo.setPatientId(patient.getId());
                        patientInfoVo.setName(patient.getUserName());
                        if (patient.getBirthday() != null) {
                            patientInfoVo.setBirthday(patient.getBirthday());
                            patientInfoVo.setAgeStr(patient.getAgeStr());
                        }
                        patientInfoVo.setHeadPicFileName(patient.getTopPath());
                        patientInfoVo.setSex(patient.getSex());
                        patientInfoVo.setArea(patient.getArea());
                        patientInfoVo.setUserId(patient.getUserId());
                        if (Objects.nonNull(patient.getUserId())) {
                            userIds.add(patient.getUserId());
                        }
                        patientInfoVo.setPhone(patient.getTelephone());
                        patientInfoVos.add(patientInfoVo);
                    }

                    if (!CollectionUtils.isEmpty(userIds)) {
                        List<User> users = userRepository.findUsersWithOutStatus(userIds);
                        if (!CollectionUtils.isEmpty(users) && !CollectionUtils.isEmpty(patientInfoVos)) {
                            users.forEach(u -> {
                                patientInfoVos.forEach(p -> {
                                    if (Objects.nonNull(p.getUserId()) && u.getUserId().intValue() == p.getUserId()) {
                                        p.setStatus(u.getStatus());
                                    }
                                });
                            });
                        }
                    }

                    // 4、查询t_patient_info表，获取备注名
                    List<DoctorPatient> doctorPatients = tagDao.getAllMyPatient(doctorId);
                    if (doctorPatients != null && doctorPatients.size() > 0) {
                        for (DoctorPatient doctorPatient : doctorPatients) {
                            for (PatientInfoVo patientInfoVo : patientInfoVos) {
                                if (patientInfoVo.getPatientId() != null && doctorPatient.getPatientId() != null &&
                                        patientInfoVo.getPatientId().intValue() == doctorPatient.getPatientId().intValue()) {
                                    patientInfoVo.setRemarkName(doctorPatient.getRemarkName());
                                    patientInfoVo.setRemark(doctorPatient.getRemark());
                                }
                            }
                        }
                    }
                }

                Collections.sort(patientInfoVos, new SortByChina<PatientInfoVo>("name"));
                // 标签分组
                List<RelationVO> relaList = relationDao.getGroupTag2(UserEnum.RelationType.doctorPatient, doctorId, patientIds);
                // 添加数量为0的标签
                relationService.addNoneTag2(relaList, doctorId, UserEnum.RelationType.doctorPatient);
                // 添加系统标签
                this.addSysTag(relaList, doctorId, patientIds);
                // 填充属性
                relationService.fillProperty(relaList, doctorId);
                // 去重
                this.removeDuplicate(relaList);
                result.put("users", patientInfoVos);
                result.put("tags", relaList);
                return result;
            } else {
                return null;
            }

        } else {
            throw new ServiceException("当前用户不为医生");
        }
    }

    private void removeDuplicate(List<RelationVO> relaList) {
        if (relaList != null && relaList.size() > 0) {
            for (RelationVO relationVO : relaList) {
                List<Integer> tempPatientId = relationVO.getPatientIds();
                if (CollectionUtils.isEmpty(tempPatientId)) {
                    continue;
                }
                Set<Integer> set = new HashSet<Integer>(tempPatientId);
                relationVO.setPatientIds(new ArrayList<Integer>(set));
            }
        }
    }

    private void addSysTag(List<RelationVO> list, Integer doctorId, List<Integer> patientIds) {
        list.add(patientInfoDao.getInactiveTag(doctorId, patientIds));
        List<String> names = new ArrayList<String>();
        for (RelationVO vo : list) {
            names.add(vo.getTagName());
        }
        for (String sysTag : TagUtil.SYS_TAG) {
            if (!names.contains(sysTag)) {
                RelationVO vo = new RelationVO();
                vo.setTagName(sysTag);
                vo.setNum(0);
                vo.setPatientIds(Lists.newArrayList());
                list.add(vo);
            }
        }
    }

    /**
     * 修复医患关系数据
     */
    @Override
    public void fixDoctorPatient() {
        // 1、先查询所有的u_doctor_patient表
        List<DoctorPatient> doctorPatients = relationDao.getAll();
        // 2、取出所有的用户id，并用用户id查询MySQL patient表中患者的id
        if (doctorPatients != null && doctorPatients.size() > 0) {
            for (DoctorPatient doctorPatient : doctorPatients) {
                // 3、更新patientid到u_doctor_patient表
                Integer userId = doctorPatient.getToUserId();
                User user = userRepository.getUser(userId);
                if (user != null && user.getUserType() != null
                        && user.getUserType().intValue() == UserEnum.UserType.patient.getIndex()) {
                    // 说明该用户为患者，查询MySQL中Patient表
                    Patient patient = patientMapper.findSelfByUserId(userId);
                    if (patient != null) {
                        relationDao.fixDoctorPatient(doctorPatient.getId(), patient.getId());
                    } else {
                        // TODO 没有Patient记录的怎么办？
                    }
                } else {
                    Integer pUserId = doctorPatient.getUserId();
                    User pUser = userRepository.getUser(pUserId);
                    if (pUser != null && pUser.getUserType() != null
                            && pUser.getUserType().intValue() == UserEnum.UserType.patient.getIndex()) {
                        Patient patient = patientMapper.findSelfByUserId(pUserId);
                        if (patient != null) {
                            relationDao.fixDoctorPatient(doctorPatient.getId(), patient.getId());
                        } else {
                            // TODO 没有Patient记录的怎么办？
                        }
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Object> getillHistoryInfo(Integer doctorId, Integer patientId, String illHistoryInfoId) {
        // 根据医生id和患者id获取病历
        IllHistoryInfo illHistoryInfo = null;

        if (StringUtils.isNotBlank(illHistoryInfoId)) {
            illHistoryInfo = illHistoryInfoDao.getById(illHistoryInfoId);
            if (Objects.isNull(illHistoryInfo)) {
                //若还未空，则可能是旧病历id，
                List<Order> orders = orderMapper.findByIllCaseInfoId(illHistoryInfoId);
                if (orders != null && orders.size() > 0) {
                    Order order = orders.get(0);
                    if (StringUtils.isNotBlank(order.getIllHistoryInfoId())) {
                        illHistoryInfo = illHistoryInfoDao.getById(order.getIllHistoryInfoId());
                    }
                }
            }
        } else {
            illHistoryInfo = illHistoryInfoDao.getByDoctorIdAndPatientId(doctorId, patientId);
        }

        if (illHistoryInfo == null) {
            throw new ServiceException("病历信息为空");
        }
        // 根据医生id和患者id获取患者信息
        PatientInfo patientInfo = patientInfoDao.findById(illHistoryInfo.getPatientInfoId());
        if (patientInfo == null) {
            throw new ServiceException("患者信息为空");
        }

        Integer currentUserId = ReqUtil.instance.getUserId();
        User user = userRepository.getUser(currentUserId);
        if (user == null) {
            throw new ServiceException("当前用户为空");
        }

        User doctor = userRepository.getUser(illHistoryInfo.getDoctorId());
        if (doctor == null) {
            throw new ServiceException("该医生不存在");
        }

        Patient patient = patientMapper.selectByPrimaryKey(illHistoryInfo.getPatientId());
        if (patient == null) {
            throw new ServiceException("患者信息不存在");
        }

        // 获取前端所需的患者信息
        DoctorPatient doctorPatient = null;
        if (user.getUserType() != null && user.getUserType().intValue() == UserType.doctor.getIndex()) {
            doctorPatient = tagDao.findByDoctorIdAndPatientId(currentUserId, illHistoryInfo.getPatientId());
        } else {
            doctorPatient = tagDao.findByDoctorIdAndPatientId(illHistoryInfo.getDoctorId(), illHistoryInfo.getPatientId());
        }

        PatientInfoVo patientInfoVo = fromPatientAndPatientInfo(patient, patientInfo);

        if (doctorPatient != null) {
            //自获取用户自定义的标签
            List<String> list = Lists.newArrayList();
            String[] tags = doctorPatient.getTags();
            if (tags != null && tags.length > 0) {
                for (String tag : tags) {
                    if (!TagUtil.SYS_TAG.contains(StringUtils.trim(tag))) {
                        list.add(tag);
                    }
                }
            }
            patientInfoVo.setTags(list);
            patientInfoVo.setRemarkName(doctorPatient.getRemarkName());
            patientInfoVo.setRemark(doctorPatient.getRemark());
        }

        // 获取前端所需的病情资料
        IllContentInfo illContentInfo = illHistoryInfo.getIllContentInfo();
        if (illContentInfo != null && illContentInfo.getPics() != null) {
            List<String> pics = Lists.newArrayList();
            for (String pic : illContentInfo.getPics()) {
                if (StringUtils.isNotBlank(pic)) {
                    pics.add(pic);
                }
            }
            illContentInfo.setPics(pics);
        }

        // 获取前端所需的初步诊断
        List<Diagnosis> diagnoses = illHistoryInfo.getDiagnosis();
        // 按时间顺序排序
        if (diagnoses != null) {
            Collections.sort(diagnoses, new Comparator<Diagnosis>() {
                @Override
                public int compare(Diagnosis o1, Diagnosis o2) {
                    return o2.getCreateTime().compareTo(o1.getUpdateTime());
                }
            });
        }

        List<DiagnosisVO> diagnosisVOs = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(diagnoses)) {
            for (Diagnosis diag : diagnoses) {
                DiagnosisVO diagnosisVO = covertDiagnosis(diag, illHistoryInfo.getPatientId());
                diagnosisVOs.add(diagnosisVO);
            }
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("illHistoryInfoId", illHistoryInfo.getId());
        result.put("patient", patientInfoVo);
        result.put("illContentInfo", illContentInfo);
        result.put("diagnosesVO", diagnosisVOs);
        result.put("briefHistroy", illHistoryInfo.getBriefHistroy());

        return result;
    }

    public PageVO getillHistoryRecords(Integer doctorId, Integer patientId, String illHistoryInfoId, Integer pageIndex, Integer pageSize) throws HttpApiException {
        // 根据医生id和患者id获取病历
        IllHistoryInfo illHistoryInfo = null;
        if (StringUtils.isNotBlank(illHistoryInfoId)) {
            illHistoryInfo = illHistoryInfoDao.getById(illHistoryInfoId);
            if (Objects.isNull(illHistoryInfo)) {
                //若还未空，则可能是旧病历id，
                List<Order> orders = orderMapper.findByIllCaseInfoId(illHistoryInfoId);
                if (orders != null && orders.size() > 0) {
                    Order order = orders.get(0);
                    if (StringUtils.isNotBlank(order.getIllHistoryInfoId())) {
                        illHistoryInfo = illHistoryInfoDao.getById(order.getIllHistoryInfoId());
                    }
                }
            }
        } else {
            illHistoryInfo = illHistoryInfoDao.getByDoctorIdAndPatientId(doctorId, patientId);
        }

        if (illHistoryInfo == null) {
            throw new ServiceException("病历信息为空");
        }

        Integer currentUserId = ReqUtil.instance.getUserId();
        User user = userRepository.getUser(currentUserId);
        if (user == null) {
            throw new ServiceException("当前用户为空");
        }

        User doctor = userRepository.getUser(illHistoryInfo.getDoctorId());
        if (doctor == null) {
            throw new ServiceException("该医生不存在");
        }

        Patient patient = patientMapper.selectByPrimaryKey(illHistoryInfo.getPatientId());
        if (patient == null) {
            throw new ServiceException("患者信息不存在");
        }

        // 分页获取病程信息
        List<IllHistoryRecord> illHistoryRecords = null;
        List<IllHistoryRecordVo> illHistoryRecordVos = Lists.newArrayList();
        Long count = 0l;
        if (user.getUserType() != null & user.getUserType().intValue() == UserEnum.UserType.doctor.getIndex()) {
            illHistoryRecords = illHistoryRecordDao.findByIllHistoryInfoIdForDoctor(illHistoryInfo.getId(), currentUserId, pageIndex, pageSize);
            count = illHistoryRecordDao.findByIllHistoryInfoIdForDoctorCount(illHistoryInfo.getId(), currentUserId);
        } else if (user.getUserType() != null & user.getUserType().intValue() == UserType.assistant.getIndex()) {
            //医生助手看到的病程和所负责的医生是一致的。
            illHistoryRecords = illHistoryRecordDao.findByIllHistoryInfoIdForDoctor(illHistoryInfo.getId(), doctorId, pageIndex, pageSize);
            count = illHistoryRecordDao.findByIllHistoryInfoIdForDoctorCount(illHistoryInfo.getId(), doctorId);
        } else {
            illHistoryRecords = illHistoryRecordDao.findByIllHistoryInfoIdForPatient(illHistoryInfo.getId(), pageIndex, pageSize);
            count = illHistoryRecordDao.findByIllHistoryInfoIdForPatient(illHistoryInfo.getId());
        }

        //查询出全部的疾病
        List<DiseaseTypeVO> diseaseTypes = baseDataDao.getAllDiseaseType();
        Map<String, String> diseaseTypeMap = Maps.newHashMap();
        if (diseaseTypes != null && diseaseTypes.size() > 0) {
            for (DiseaseTypeVO diseaseTypeVo : diseaseTypes) {
                diseaseTypeMap.put(diseaseTypeVo.getId(), diseaseTypeVo.getName());
            }
        }

        //查询出全部的检查项
        List<CheckSuggest> allCheckSuggests = baseDataDao.getAllLeaf();
        Map<String, String> checkSuggestMap = Maps.newHashMap();
        if (allCheckSuggests != null && allCheckSuggests.size() > 0) {
            for (CheckSuggest checkSuggest : allCheckSuggests) {
                checkSuggestMap.put(checkSuggest.getId(), checkSuggest.getName());
            }
        }

        if (illHistoryRecords != null && illHistoryRecords.size() > 0) {

            List<Integer> orderIds = Lists.newArrayList();
            List<Integer> careOrderIds = Lists.newArrayList();

            // 健康关怀专家组的医生的id
            List<Integer> careGroupDoctorIds = Lists.newArrayList();
            // 会诊的转诊医生的id
            List<Integer> consultationDoctorIds = Lists.newArrayList();
            // 检查项的id
            List<String> checkItemIds = Lists.newArrayList();

            List<String> drugGoodsIds = Lists.newArrayList();

            List<Integer> doctorIds = Lists.newArrayList();

            List<Integer> createrIds = Lists.newArrayList();

            for (IllHistoryRecord illHistoryRecord : illHistoryRecords) {
                // 1、先获取病程的类型，除健康关怀、电话咨询、门诊，则都组装为对应的Vo
                IllHistoryRecordVo illHistoryRecordVo = new IllHistoryRecordVo();
                illHistoryRecordVo.setPatientName(patient.getUserName());

                // 处理健康关怀的卡片
                if (illHistoryRecord.getRecordCare() != null) {
                    orderIds.add(illHistoryRecord.getRecordCare().getOrderId());
                    careOrderIds.add(illHistoryRecord.getRecordCare().getOrderId());

                    illHistoryRecordVo.setOrderId(illHistoryRecord.getRecordCare().getOrderId());

                    RecordCareVo recordCareVo = RecordCareVo.formRecordCare(illHistoryRecord.getRecordCare());
                    if (StringUtils.isBlank(recordCareVo.getDiseaseDesc())) {
                        recordCareVo.setDiseaseDesc(null);
                    }
                    if (StringUtils.isBlank(recordCareVo.getDiseaseDuration())) {
                        recordCareVo.setDiseaseDuration(null);
                    }
                    if (StringUtils.isBlank(recordCareVo.getDiseaseId())) {
                        recordCareVo.setDiseaseId(null);
                    }
                    if (StringUtils.isBlank(recordCareVo.getDrugCase())) {
                        recordCareVo.setDrugCase(null);
                    }
                    if (StringUtils.isBlank(recordCareVo.getHopeHelp())) {
                        recordCareVo.setHopeHelp(null);
                    }

                    if (!Objects.isNull(illHistoryRecord.getRecordCare().getMainDoctor())) {
                        doctorIds.add(illHistoryRecord.getRecordCare().getMainDoctor());
                    }

                    if (illHistoryRecord.getRecordCare().getGroupDoctors() != null) {
                        careGroupDoctorIds.addAll(illHistoryRecord.getRecordCare().getGroupDoctors());
                    }

                    if (StringUtils.isNotEmpty(recordCareVo.getDiseaseId())) {
                        String disease = diseaseTypeMap.get(recordCareVo.getDiseaseId());
                        recordCareVo.setDisease(disease);
                    }

                    if (illHistoryRecord.getRecordCare().getDrugGoodsIds() != null && illHistoryRecord.getRecordCare().getDrugGoodsIds().size() > 0) {
                        drugGoodsIds.addAll(illHistoryRecord.getRecordCare().getDrugGoodsIds());
                    }

                    boolean canJoinIM = canJoinIM(illHistoryRecord.getRecordCare().getMessageGroupId(), String.valueOf(currentUserId));
                    recordCareVo.setCanJoinIM(canJoinIM);
                    recordCareVo.setPatientName(patient.getUserName());
                    illHistoryRecordVo.setRecordCare(recordCareVo);
                }

                // 处理图文咨询、电话咨询、门诊、积分问诊类型的的卡片
                if (illHistoryRecord.getRecordOrder() != null) {
                    orderIds.add(illHistoryRecord.getRecordOrder().getOrderId());
                    illHistoryRecordVo.setOrderId(illHistoryRecord.getRecordOrder().getOrderId());
                    RecordOrderVo recordOrderVo = RecordOrderVo.fromRecordOrder(illHistoryRecord.getRecordOrder());
                    if (!Objects.isNull(illHistoryRecord.getRecordOrder().getDoctorId())) {
                        doctorIds.add(illHistoryRecord.getRecordOrder().getDoctorId());
                    }
                    if (StringUtils.isNotEmpty(recordOrderVo.getDiseaseId())) {
                        String disease = diseaseTypeMap.get(recordOrderVo.getDiseaseId());
                        recordOrderVo.setDisease(disease);
                    }
                    if (illHistoryRecord.getRecordOrder().getDrugGoodsIds() != null && illHistoryRecord.getRecordOrder().getDrugGoodsIds().size() > 0) {
                        drugGoodsIds.addAll(illHistoryRecord.getRecordOrder().getDrugGoodsIds());
                    }
                    if (StringUtils.isBlank(recordOrderVo.getDiseaseDesc())) {
                        recordOrderVo.setDiseaseDesc(null);
                    }
                    if (StringUtils.isBlank(recordOrderVo.getDiseaseDuration())) {
                        recordOrderVo.setDiseaseDuration(null);
                    }
                    if (StringUtils.isBlank(recordOrderVo.getDiseaseId())) {
                        recordOrderVo.setDiseaseId(null);
                    }
                    if (StringUtils.isBlank(recordOrderVo.getDrugCase())) {
                        recordOrderVo.setDrugCase(null);
                    }
                    if (StringUtils.isBlank(recordOrderVo.getHopeHelp())) {
                        recordOrderVo.setHopeHelp(null);
                    }

                    boolean canJoinIM = canJoinIM(illHistoryRecord.getRecordOrder().getMessageGroupId(), String.valueOf(currentUserId));
                    recordOrderVo.setCanJoinIM(canJoinIM);

                    illHistoryRecordVo.setRecordOrder(recordOrderVo);
                }

                // 处理会诊类型的卡片
                if (illHistoryRecord.getRecordConsultiation() != null) {
                    orderIds.add(illHistoryRecord.getRecordConsultiation().getOrderId());
                    illHistoryRecordVo.setOrderId(illHistoryRecord.getRecordConsultiation().getOrderId());
                    RecordConsultiationVo recordConsultiationVo = RecordConsultiationVo.fromRecornConsultiation(illHistoryRecord.getRecordConsultiation());
                    if (!Objects.isNull(illHistoryRecord.getRecordConsultiation().getConsultJoinDocs())) {
                    	List<Integer> docs =new ArrayList<>( illHistoryRecord.getRecordConsultiation().getConsultJoinDocs());
                    	recordConsultiationVo.setConsultJoinDocs(docs);
                    	
                    	illHistoryRecord.getRecordConsultiation().getConsultJoinDocs().removeAll(doctorIds);
                        doctorIds.addAll(illHistoryRecord.getRecordConsultiation().getConsultJoinDocs());
                    }
                    boolean canJoinIM = canJoinIM(illHistoryRecord.getRecordConsultiation().getMessageGroupId(), String.valueOf(currentUserId));
                    recordConsultiationVo.setCanJoinIM(canJoinIM);
                    consultationDoctorIds.add(illHistoryRecord.getRecordConsultiation().getConsultationDoctor());
                    illHistoryRecordVo.setRecordConsultiation(recordConsultiationVo);
                }

                // 处理患者报道类型的卡片
                if (illHistoryRecord.getRecordCheckIn() != null) {
                    RecordCheckInVo recordCheckInVo = RecordCheckInVo.fromRecordCheckIn(illHistoryRecord.getRecordCheckIn());
                    if (recordCheckInVo.getDiseaseIds() != null && recordCheckInVo.getDiseaseIds().size() > 0) {
                        List<String> diseases = Lists.newArrayList();
                        for (String diseaseId : recordCheckInVo.getDiseaseIds()) {
                            String disease = diseaseTypeMap.get(diseaseId);
                            if (StringUtils.isNotBlank(disease)) {
                                diseases.add(disease);
                            }
                        }
                        recordCheckInVo.setDiseases(diseases);
                    }
                    boolean canJoinIM = canJoinIM(illHistoryRecord.getRecordCheckIn().getMessageGroupId(), String.valueOf(currentUserId));
                    recordCheckInVo.setCanJoinIM(canJoinIM);
                    illHistoryRecordVo.setRecordCheckIn(recordCheckInVo);
                }

                // 处理检查单类型的卡片
                if (illHistoryRecord.getRecordCheckItem() != null) {
                    RecordCheckItemVo recordCheckItemVo = RecordCheckItemVo.fromRecordCheckItem(illHistoryRecord.getRecordCheckItem());
                    checkItemIds.add(illHistoryRecord.getRecordCheckItem().getCheckItemId());
                    createrIds.add(illHistoryRecord.getCreater());
                    illHistoryRecordVo.setRecordCheckItem(recordCheckItemVo);
                }

                // 处理手动添加类型的卡片
                if (illHistoryRecord.getRecordNormal() != null) {
                    RecordNormalVo recordNormalVo = RecordNormalVo.fromRecordNormal(illHistoryRecord.getRecordNormal());
                    createrIds.add(illHistoryRecord.getCreater());
                    illHistoryRecordVo.setRecordNormal(recordNormalVo);
                }

                // 处理用药类型的卡片
                if (illHistoryRecord.getRecordDrug() != null) {
                    RecordDrugVo recordDrugVo = RecordDrugVo.fromRecordDrug(illHistoryRecord.getRecordDrug());
                    illHistoryRecordVo.setRecordDrug(recordDrugVo);
                }

                illHistoryRecordVo.setId(illHistoryRecord.getId());
                illHistoryRecordVo.setName(illHistoryRecord.getName());
                illHistoryRecordVo.setIllHistoryInfoId(illHistoryRecord.getIllHistoryInfoId());
                illHistoryRecordVo.setType(illHistoryRecord.getType());
                illHistoryRecordVo.setCreateTime(illHistoryRecord.getCreateTime());
                illHistoryRecordVo.setUpdateTime(illHistoryRecord.getUpdateTime());
                illHistoryRecordVo.setCreater(illHistoryRecord.getCreater());

                illHistoryRecordVos.add(illHistoryRecordVo);
            }

            if (orderIds != null && orderIds.size() > 0) {
                // 获取上一步中健康关怀、图文咨询、电话咨询、门诊、远程会诊的订单id，查询p_cure_record表，组装对应的咨询结果
                assemblyOrderResult(illHistoryRecordVos, orderIds, checkSuggestMap, diseaseTypeMap);
            }

            if (careGroupDoctorIds != null && careGroupDoctorIds.size() > 0) {
                // 组装健康关怀专家组的医生
                assemblyCareGroupDoctors(illHistoryRecordVos, careGroupDoctorIds);
            }

            if (consultationDoctorIds != null && consultationDoctorIds.size() > 0) {
                // 组装会诊的转诊医生
                assemblyConsultationDoctor(illHistoryRecordVos, consultationDoctorIds);
            }

            if (checkItemIds != null && checkItemIds.size() > 0) {
                // 查询检查项，组装检查项的病程
                assemblyCheckItem(illHistoryRecordVos, checkItemIds);
            }

            if (careOrderIds != null && careOrderIds.size() > 0) {
                // 获取健康关怀的id，组装关怀小节
                assemblyCareTips(illHistoryRecordVos, careOrderIds, checkSuggestMap, diseaseTypeMap);
            }


            if (doctorIds != null && doctorIds.size() > 0) {
                //处理医生姓名
                assemblyDoctorName(illHistoryRecordVos, doctorIds);
            }

            if (createrIds != null && createrIds.size() > 0) {
                //处理创建人姓名
                assemblyCreaterName(illHistoryRecordVos, createrIds, patient);
            }

            List<DrugInfoVo> drugInfoVos = null;
            if (drugGoodsIds != null && drugGoodsIds.size() > 0) {
                //从药那边获取药品信息
//                drugInfoVos = drugManager.getGoodsByIds(drugGoodsIds);
                List<CGoods> ret = drugApiClientProxy.getGoodsByIds(drugGoodsIds);
                if (!CollectionUtils.isEmpty(ret)) {
                    drugInfoVos = SdkBeanUtils.copyList(ret, DrugInfoVo.class);
                }
            }
            if (drugInfoVos != null && drugInfoVos.size() > 0) {
                //组装患者下单时填写的用药信息
                assemblyDrugInfo(illHistoryRecordVos, drugInfoVos);
            }

        }

        PageVO pageVO = new PageVO();
        pageVO.setPageData(illHistoryRecordVos);
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        pageVO.setTotal(count);

        return pageVO;
    }


    @Override
    public void addDrugCase(String illHistoryInfoId, String drugInfos, List<String> pics, String drugCase, Long time) {
        List<DrugInfo> drugInfoList = null;
        if (StringUtils.isNotEmpty(drugInfos)) {
            drugInfoList = JSONMessage.parseArray(drugInfos, DrugInfo.class);
        }
        addDrugCase(illHistoryInfoId, drugInfoList, pics, drugCase, time);
    }

    @Override
    public void addDrugCase(String illHistoryInfoId, List<DrugInfo> drugInfos, List<String> pics, String drugCase, Long time) {
        IllHistoryRecord illHistoryRecord = new IllHistoryRecord();
        illHistoryRecord.setIllHistoryInfoId(illHistoryInfoId);
        illHistoryRecord.setName("用药");

        illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.drug.getIndex());

        RecordDrug recordDrug = new RecordDrug();
        recordDrug.setDrugCase(drugCase);
        recordDrug.setDrugInfos(drugInfos);
        recordDrug.setPics(pics);
        recordDrug.setTime(time);

        illHistoryRecord.setRecordDrug(recordDrug);

        illHistoryRecordDao.insert(illHistoryRecord);
    }

    @Override
    public void addDrugCaseAndSendMsg(String drugInfos, List<String> pics, String drugCase, Integer orderId,
                                      String gid) throws HttpApiException {
        List<DrugInfo> drugInfoList = null;
        if (StringUtils.isNotEmpty(drugInfos)) {
            drugInfoList = JSONMessage.parseArray(drugInfos, DrugInfo.class);
        }
        Order order = orderMapper.getOne(orderId);
        if (order == null) {
            // 订单不存在
            throw new ServiceException("订单不存在");
        }

        if (StringUtils.isBlank(drugCase) && (Objects.isNull(pics) || pics.size() < 1) && StringUtils.isBlank(drugInfos)) {
            throw new ServiceException("请填写正确的用药信息");
        }

        Integer doctorId = order.getDoctorId();
        Integer patientId = order.getPatientId();
        Integer userId = order.getUserId();
        String illHistoryInfoId = order.getIllHistoryInfoId();
        IllHistoryInfo illHistoryInfo = null;
        if (StringUtils.isNotEmpty(illHistoryInfoId)) {
            illHistoryInfo = illHistoryInfoDao.getById(illHistoryInfoId);
        } else {
            illHistoryInfo = illHistoryInfoDao.getByDoctorIdAndPatientId(doctorId, patientId);
        }

        if (illHistoryInfo == null) {
            throw new ServiceException("病历信息为空");
        }

        addDrugCase(illHistoryInfo.getId(), drugInfoList, pics, drugCase, null);

        // 发送IM消息
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(6);
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setTitle("用药情况");
        imgTextMsg.setPic(QiniuUtil.DEFAULT_IM_DRUG_IMG());
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(drugCase)) {
            sb.append(drugCase);
        }
        if (drugInfoList != null && drugInfoList.size() > 0) {
            if (sb != null && sb.length() > 0) {
                sb.append(";");
            }
            for (DrugInfo drugInfo : drugInfoList) {
                if (StringUtils.isNotBlank(drugInfo.getDrugName())) {
                    sb.append(drugInfo.getDrugName()).append("，");
                }
            }
            if (sb != null && sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
        }
        if (pics != null && pics.size() > 0) {
            if (sb != null && sb.length() > 0) {
                sb.append(";");
            }
            sb.append("我上传了药品照片。");
        }
        imgTextMsg.setContent(ParserHtmlUtil.delHTMLTag(sb.toString()));
        imgTextMsg.setUrl("");

        Map<String, Object> bizParam = Maps.newHashMap();
        Map<String, Object> param = Maps.newHashMap();
        // 61表示用药卡片
        param.put("bizType", 61);
        // 病历id
        param.put("bizId", illHistoryInfo.getId());

        bizParam.put("bizParam", param);

        List<ImgTextMsg> mpt = Lists.newArrayList();
        mpt.add(imgTextMsg);

        businessServiceMsg.sendTextMsgToGid(String.valueOf(userId), gid, mpt, bizParam, false);
    }

    @Override
    public void sendCheckItem(String gid, Integer orderId, String checkupId, String checkupName, String[] indicatorIds,
                              Long suggestCheckTime, String attention) throws HttpApiException {

        Order order = orderMapper.getOne(orderId);
        if (order == null) {
            throw new ServiceException("未查询到订单");
        }

        // 1、向checkBill表插入记录
        Long now = System.currentTimeMillis();
        CheckBill checkBill = new CheckBill();
        checkBill.setOrderId(orderId);
        checkBill.setPatientId(order.getPatientId());
        checkBill.setCheckBillStatus(2);
        checkBill.setAttention(attention);

        checkBill.setSuggestCheckTime(suggestCheckTime);
        checkBill.setCreateTime(now);
        checkBill.setUpdateTime(now);
        List<String> checkupIds = Lists.newArrayList();
        checkupIds.add(checkupId);
        checkBill.setCheckupIds(checkupIds);
        checkBillDao.insertCheckBill(checkBill);

        // 调用添加检查单
        CheckItem checkItem = new CheckItem();
        checkItem.setCheckUpId(checkupId);
        checkItem.setItemName(checkupName);
        checkItem.setFrom(1);
        checkItem.setFromId(checkBill.getId());
        checkItem.setCreateTime(now);
        checkItem.setUpdateTime(now);
        if (indicatorIds != null && indicatorIds.length > 0) {
            checkItem.setIndicatorIds(Arrays.asList(indicatorIds));
        }
        checkBillDao.addCheckItem(checkItem);

        List<String> checkItemIds = Lists.newArrayList();
        checkItemIds.add(checkItem.getId());
        checkBill.setCheckItemIds(checkItemIds);
        checkBill.setCheckBillStatus(2);
        checkBillDao.updateCheckbill(checkBill);

        List<String> concernedItemIds = null;
        if (indicatorIds != null && indicatorIds.length > 0) {
            concernedItemIds = Arrays.asList(indicatorIds);
        }

        // 2、调用肖伟的接口发送代办事项
        if (order.getPackType() != null && order.getPackType().intValue() == PackEnum.PackType.careTemplate.getIndex()) {

            String careItemId = null;
            try {
                careItemId = carePlanApiClientProxy.createCareCheckItemByCheckBill(order.getCareTemplateId(), checkBill.getId(), attention, suggestCheckTime, checkItem.getId(), checkupId, checkupName, concernedItemIds);
            } catch (HttpApiException e) {
                logger.error(e.getMessage(), e);
            }
            if (StringUtils.isNotBlank(careItemId)) {
                checkBill.setCareItemId(careItemId);
                checkBillDao.updateCareItemId(checkBill.getId(), checkBill.getCareItemId()); // 更新冗余的careItemId
            }
        }

        // 3、发送IM图文消息
        StringBuffer msgContent = new StringBuffer();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String suggestDate = simpleDateFormat.format(new Date(suggestCheckTime));
        msgContent.append("建议您做检查：").append(checkupName);

        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(6);
        imgTextMsg.setTime(now);
        imgTextMsg.setTitle("检查检验选项");
        imgTextMsg.setPic(QiniuUtil.DEFAULT_IM_CHECKITEM_IMG());
        if (StringUtils.isNotEmpty(attention)) {
            imgTextMsg.setContent(ParserHtmlUtil.delHTMLTag(msgContent.toString()));
        }
        imgTextMsg.setUrl("");

        Map<String, Object> param = Maps.newHashMap();
        Map<String, Object> bizParam = Maps.newHashMap();
        // 62表示检查项卡片
        param.put("bizType", 62);
        // 检查单的id
        param.put("bizId", checkItem.getId());
        bizParam.put("bizParam", param);

        List<ImgTextMsg> mpt = Lists.newArrayList();
        mpt.add(imgTextMsg);

        businessServiceMsg.sendTextMsgToGid(String.valueOf(order.getDoctorId()), gid, mpt, bizParam, false);
    }

    @Override
    public void addCheckItem(String gid, Integer orderId, String checkupId, String checkupName, Long checkTime,
                             String result, String[] pics, String checkItemId) throws HttpApiException {
        // 1、先根据doctorId和patientId查出病历的id
        Order order = orderMapper.getOne(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        CheckItem checkItem = checkBillDao.getCheckItemById(checkItemId);
        if (checkItem == null) {
            throw new ServiceException("检查项不存在");
        }

        // checkItem的id关联回bill
        CheckBill checkBill = checkBillDao.getCheckBillById(checkItem.getFromId());
        List<String> checkItemIds = Lists.newArrayList();
        checkItemIds.add(checkItemId);
        if (!CollectionUtils.isEmpty(checkBill.getCheckItemIds())) {
            checkBill.getCheckItemIds().addAll(checkItemIds);
        } else {
            checkBill.setCheckItemIds(checkItemIds);
        }
        checkBill.setCheckBillStatus(4);

        //更新检查单
        checkBillDao.updateCheckbill(checkBill);
        //由于健康关怀只上传一个检查项，所以只getIndex(0)即可
        checkItem.setVisitingTime(checkTime);
        checkItem.setResults(result);
        List<String> images = null;
        if (pics != null && pics.length > 0) {
            images = Arrays.asList(pics);
            checkItem.setImageList(images);
        }
        checkBillDao.updateCheckItem(checkItem);

        // 更新对应的关怀项状态
        boolean finished = updateCareCheckItemStatus(checkBill);

        IllHistoryInfo illHistoryInfo = illHistoryInfoDao.getById(order.getIllHistoryInfoId());

        // 2、创建IllHistoryRecord对象
        IllHistoryRecord illHistoryRecord = new IllHistoryRecord();
        illHistoryRecord.setIllHistoryInfoId(illHistoryInfo.getId());
        illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.checkItem.getIndex());
        illHistoryRecord.setName(checkupName);
        illHistoryRecord.setCreater(order.getPatientId());
        illHistoryRecord.setSecret(false);

        RecordCheckItem recordCheckItem = new RecordCheckItem();
        recordCheckItem.setCheckItemId(checkItem.getId());

        illHistoryRecord.setRecordCheckItem(recordCheckItem);
        illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.checkItem.getIndex());

        illHistoryRecordDao.insert(illHistoryRecord);

        if (StringUtils.isNotBlank(gid)) {
            sendCheckItemIM(checkupName, checkupId, order, gid);
        }
        
        // 更新状态
        if (finished) {
        	 try {
                 carePlanApiClientProxy.checkItemSubmitted(checkBill.getCareItemId(), checkBill.getId());
             } catch (HttpApiException e) {
                 logger.error(e.getMessage(), e);
             }
        }

        Patient patient = patientMapper.selectByPrimaryKey(order.getPatientId());
        //向玄关发送图片
        sendCheckItemToXG(order, images, checkupId, checkItemId, patient.getTelephone(), patient.getArea());
    }

    private void sendCheckItemIM(String checkupName, String checkupId, Order order, String gid) throws HttpApiException {
        // 发送im
        String content = "医生您好，我已上传" + checkupName + "检查，请您及时查看。";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("checkupId", checkupId);
        param.put("patientId", order.getPatientId());
        //60表示检查项，客户端会根据这个值跳转到患者该检查项的列表页。

        param.put("type", "60");
        businessServiceMsg.sendUserToUserParam(String.valueOf(order.getUserId()), gid, content, param, false);
    }

    private boolean updateCareCheckItemStatus(CheckBill checkBill) {
        if (StringUtils.isBlank(checkBill.getCareItemId())) {
            return false;
        }
        // 检查状态
        boolean finished = true;
        List<CheckItem> checkItems = checkBillDao.getItemList(checkBill.getId());
        for (com.dachen.health.checkbill.entity.po.CheckItem item : checkItems) {
            if (CollectionUtils.isEmpty(item.getImageList())) {
                finished = false;
                break;
            }
        }
        
        return finished;
    }

    @Override
    public void addCheckItemBySelf(String gid, Integer orderId, String checkupId, String checkupName, Long checkTime, String result, String[] pics) throws HttpApiException {
        // 1、先根据doctorId和patientId查出病历的id
        Order order = orderMapper.getOne(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        IllHistoryInfo illHistoryInfo = null;

        String illHistoryInfoId = order.getIllHistoryInfoId();
        if (StringUtils.isNotBlank(illHistoryInfoId)) {
            illHistoryInfo = illHistoryInfoDao.getById(illHistoryInfoId);
        } else {
            illHistoryInfo = illHistoryInfoDao.getByDoctorIdAndPatientId(order.getDoctorId(), order.getPatientId());
        }

        if (illHistoryInfo == null) {
            throw new ServiceException("为找到相关病历");
        }

        // 2、创建IllHistoryRecord对象
        IllHistoryRecord illHistoryRecord = new IllHistoryRecord();
        illHistoryRecord.setIllHistoryInfoId(illHistoryInfo.getId());
        illHistoryRecord.setName(checkupName);

        Long now = System.currentTimeMillis();

        // 添加的检查项
        RecordCheckItem recordCheckItem = new RecordCheckItem();
        illHistoryRecord.setSecret(false);
        //创建人是患者本人
        illHistoryRecord.setCreater(order.getPatientId());

        // 添加bill,关联patient
        CheckBill checkBill = new CheckBill();
        checkBill.setPatientId(order.getPatientId());
        checkBill.setOrderId(orderId);
        checkBill.setCreateTime(now);
        checkBill.setUpdateTime(now);
        checkBill = checkBillDao.insertCheckBill(checkBill);

        // 调用添加检查单
        CheckItem checkItem = new CheckItem();
        checkItem.setCheckUpId(checkupId);
        checkItem.setItemName(checkupName);
        checkItem.setFrom(1);
        checkItem.setFromId(checkBill.getId());
        checkItem.setCreateTime(now);
        checkItem.setUpdateTime(now);
        checkItem.setVisitingTime(checkTime);
        List<String> images = null;
        if (pics != null) {
            images = Arrays.asList(pics);
            checkItem.setImageList(images);
        }
        checkItem.setResults(result);
        checkBillDao.addCheckItem(checkItem);

        // checkItem的id关联回bill
        checkBill.setCheckItemIds(Lists.newArrayList(checkItem.getId()));
        checkBill.setCheckBillStatus(4);
        checkBillDao.updateCheckbill(checkBill);

        recordCheckItem.setCheckItemId(checkItem.getId());

        illHistoryRecord.setRecordCheckItem(recordCheckItem);
        illHistoryRecord.setType(IllHistoryEnum.IllHistoryRecordType.checkItem.getIndex());

        illHistoryRecordDao.insert(illHistoryRecord);

        if (StringUtils.isNotBlank(gid)) {
            sendCheckItemIM(checkupName, checkupId, order, gid);
        }
        Patient patient = patientMapper.selectByPrimaryKey(order.getPatientId());
        sendCheckItemToXG(order, images, checkupId, checkItem.getId(), patient.getTelephone(), patient.getArea());
    }

    /**
     * 组装会诊医生
     */
    void assemblyConsultationDoctor(List<IllHistoryRecordVo> illHistoryRecordVos, List<Integer> consultationDoctorIds) {
        List<User> users = userRepository.findUsers(consultationDoctorIds);
        if (users != null && users.size() > 0) {
            for (User tempUser : users) {
                for (IllHistoryRecordVo illHistoryRecordVo : illHistoryRecordVos) {
                    if (illHistoryRecordVo.getRecordConsultiation() != null
                            && illHistoryRecordVo.getRecordConsultiation().getConsultationDoctor() != null
                            && illHistoryRecordVo.getRecordConsultiation().getConsultationDoctor().intValue() == tempUser.getUserId().intValue()) {

                        illHistoryRecordVo.getRecordConsultiation().setConsultationDoctorName(tempUser.getName());
                    }
                }
            }
        }
    }

    /**
     * 组装咨询结果
     *
     * @param illHistoryRecordVos 返回给前端的病程对象
     * @param orderIds            订单id
     * @param checkSuggestMap     所有检查项的Map
     * @param diseaseTypeMap      所有疾病的Map
     */
    void assemblyOrderResult(List<IllHistoryRecordVo> illHistoryRecordVos, List<Integer> orderIds, Map<String, String> checkSuggestMap, Map<String, String> diseaseTypeMap) throws HttpApiException {
        List<CureRecord> cureRecords = cureRecordMapper.getByOrderIds(orderIds);

        if (cureRecords != null && cureRecords.size() > 0) {
            List<Integer> orderResultImgIds = Lists.newArrayList();
            List<String> drugAdviseIds = Lists.newArrayList();
            for (CureRecord cureRecord : cureRecords) {
                orderResultImgIds.add(cureRecord.getId());
                if (StringUtils.isNotBlank(cureRecord.getDrugAdvise())) {
                    drugAdviseIds.add(cureRecord.getDrugAdvise());
                }
            }

            List<DrugAdviseVo> drugAdviseVos = null;
            if (drugAdviseIds != null && drugAdviseIds.size() > 0) {
                // 调用药的接口
//                drugAdviseVos = drugManager.getRecipeDetail(drugAdviseIds);
                List<CRecipeVO> ret = drugApiClientProxy.getRecipeDetail(drugAdviseIds);
                if (!CollectionUtils.isEmpty(ret)) {
                    drugAdviseVos = SdkBeanUtils.copyList(ret, DrugAdviseVo.class);
                }
            }

            List<ImageData> imageDatas = null;
            if (orderResultImgIds != null && orderResultImgIds.size() > 0) {
                ImageDataParam imageDataParam = new ImageDataParam();
                imageDataParam.setType(ImageDataEnum.cureImage.getIndex());
                imageDataParam.setCureRecordIds(orderResultImgIds);
                imageDatas = imageDataMapper.findByCureRecordIds(imageDataParam);
            }

            List<ImageData> amrDates = null;
            if (orderResultImgIds != null && orderResultImgIds.size() > 0) {
                ImageDataParam imageDataParam = new ImageDataParam();
                imageDataParam.setType(ImageDataEnum.cureVoice.getIndex());
                imageDataParam.setCureRecordIds(orderResultImgIds);
                amrDates = imageDataMapper.findByCureRecordIds(imageDataParam);
            }

            for (CureRecord cureRecord : cureRecords) {
                OrderResult orderResult = new OrderResult();
                orderResult.setOrderId(cureRecord.getOrderId());

                //返回医生名字
                Integer doctroId = cureRecord.getDoctorId();
                if (null != doctroId) {
                    User doctor = userRepository.getUser(doctroId);
                    if (null != doctor) {
                        orderResult.setDoctorName(doctor.getName());
                    }
                }

                if (cureRecord.getUpdateTime() == null) {
                    orderResult.setEndTime(cureRecord.getCreateTime());
                } else {
                    orderResult.setEndTime(cureRecord.getUpdateTime());
                }
                orderResult.setResult(cureRecord.getConsultAdvise());

                //组装检查项
                String checkSuggestStr = cureRecord.getAttention();
                if (StringUtils.isNotEmpty(checkSuggestStr) && checkSuggestStr.contains(",")) {
                    String[] checkSuggests = checkSuggestStr.split(",");
                    StringBuffer checkSuggestBuffer = new StringBuffer();
                    for (String str : checkSuggests) {
                        checkSuggestBuffer.append(checkSuggestMap.get(str)).append(",");
                    }
                    if (checkSuggestBuffer != null && checkSuggestBuffer.length() > 0) {
                        checkSuggestBuffer.setLength(checkSuggestBuffer.length() - 1);
                    }
                    orderResult.setCheckSuggestName(checkSuggestBuffer.toString());
                } else if (StringUtils.isNotEmpty(checkSuggestStr)) {
                    orderResult.setCheckSuggestName(checkSuggestMap.get(checkSuggestStr));
                }

                // 用药建议需要让药店提供批量查询的接口
                if (StringUtils.isNotBlank(cureRecord.getDrugAdvise())) {
                    orderResult.setDrugAdviseId(cureRecord.getDrugAdvise());
                    if (drugAdviseVos != null && drugAdviseVos.size() > 0) {
                        for (DrugAdviseVo drugAdviseVo : drugAdviseVos) {
                            if (StringUtils.equals(drugAdviseVo.getId(), cureRecord.getDrugAdvise())) {
                                orderResult.setDrugAdviseVo(drugAdviseVo);
                                break;
                            }
                        }
                    }
                }

                if (imageDatas != null && imageDatas.size() > 0) {
                    List<String> pics = Lists.newArrayList();
                    for (ImageData imageData : imageDatas) {
                        if (imageData.getRelationId().intValue() == cureRecord.getId().intValue()) {
                            if (StringUtils.isNotBlank(imageData.getImageUrl())) {
                                pics.add(imageData.getImageUrl());
                            }
                        }
                    }
                    orderResult.setPics(pics);
                }

                if (amrDates != null && amrDates.size() > 0) {
                    List<String> amrs = Lists.newArrayList();
                    for (ImageData imageData : amrDates) {
                        if (imageData.getRelationId().intValue() == cureRecord.getId().intValue()) {
                            if (StringUtils.isNotBlank(imageData.getImageUrl())) {
                                amrs.add(imageData.getImageUrl());
                            }
                        }
                    }
                    orderResult.setVoices(amrs);
                }

                //初步诊断
                String diseaseStr = cureRecord.getConsultAdviseDiseases();
                if (StringUtils.isNotEmpty(diseaseStr) && diseaseStr.contains(",")) {
                    String[] diseases = diseaseStr.split(",");
                    StringBuffer diagnosis = new StringBuffer();
                    for (String str : diseases) {
                        diagnosis.append(diseaseTypeMap.get(str)).append(",");
                    }
                    if (diagnosis != null && diagnosis.length() > 0) {
                        diagnosis.setLength(diagnosis.length() - 1);
                    }
                    orderResult.setDiagnosis(diagnosis.toString());
                } else if (StringUtils.isNotEmpty(diseaseStr)) {
                    orderResult.setDiagnosis(diseaseTypeMap.get(diseaseStr));
                }

                for (IllHistoryRecordVo illHistoryRecordVo : illHistoryRecordVos) {
                    if (illHistoryRecordVo.getOrderId() != null && illHistoryRecordVo.getOrderId().intValue() == cureRecord.getOrderId().intValue()) {
                        if (illHistoryRecordVo.getType().intValue() == IllHistoryEnum.IllHistoryRecordType.order.getIndex()) {
                            illHistoryRecordVo.getRecordOrder().setOrderResult(orderResult);
                        }
                        if (illHistoryRecordVo.getType().intValue() == IllHistoryEnum.IllHistoryRecordType.care.getIndex()) {
                            illHistoryRecordVo.getRecordCare().setOrderResult(orderResult);
                        }
                        if (illHistoryRecordVo.getType().intValue() == IllHistoryEnum.IllHistoryRecordType.consultation.getIndex()) {
                            illHistoryRecordVo.getRecordConsultiation().setOrderResult(orderResult);
                        }
                    }
                }
            }
        }
    }

    @Autowired
    protected DrugApiClientProxy drugApiClientProxy;

    /**
     * 组装关怀小节
     *
     * @param illHistoryRecordVos 返回给前端的病程实体对象
     * @param careOrderIds        健康关怀的订单id
     * @param checkSuggestMap     所有检查项的Map
     * @param diseaseTypeMap      所有疾病的Map
     */
    void assemblyCareTips(List<IllHistoryRecordVo> illHistoryRecordVos, List<Integer> careOrderIds, Map<String, String> checkSuggestMap, Map<String, String> diseaseTypeMap) throws HttpApiException {
        List<CareSummary> careSummaries = careSummaryMapper.selectByOrderIds(careOrderIds);
        if (careSummaries != null && careSummaries.size() > 0) {
            List<Integer> tipResultIds = Lists.newArrayList();
            List<String> drugAdviseIds = Lists.newArrayList();
            for (CareSummary careSummary : careSummaries) {
                if (StringUtils.isNotBlank(careSummary.getDrugAdvise())) {
                    drugAdviseIds.add(careSummary.getDrugAdvise());
                }
                tipResultIds.add(careSummary.getId());
            }

            //调用药企的接口
            List<DrugAdviseVo> drugAdviseVos = null;
            if (drugAdviseIds != null && drugAdviseIds.size() > 0) {
//                drugAdviseVos = drugManager.getRecipeDetail(drugAdviseIds);
                List<CRecipeVO> ret = drugApiClientProxy.getRecipeDetail(drugAdviseIds);
                if (!CollectionUtils.isEmpty(ret)) {
                    drugAdviseVos = SdkBeanUtils.copyList(ret, DrugAdviseVo.class);
                }
            }

            List<ImageData> imageDatas = null;
            if (tipResultIds != null && tipResultIds.size() > 0) {
                ImageDataParam imageDataParam = new ImageDataParam();
                imageDataParam.setCureRecordIds(tipResultIds);
                imageDataParam.setType(ImageDataEnum.careImage.getIndex());
                imageDatas = imageDataMapper.findByCureRecordIds(imageDataParam);
            }

            List<ImageData> voiceDatas = null;
            if (tipResultIds != null && tipResultIds.size() > 0) {
                ImageDataParam imageDataParam = new ImageDataParam();
                imageDataParam.setCureRecordIds(tipResultIds);
                imageDataParam.setType(ImageDataEnum.careVoice.getIndex());
                voiceDatas = imageDataMapper.findByCureRecordIds(imageDataParam);
            }

            for (CareSummary careSummary : careSummaries) {
                for (IllHistoryRecordVo illHistoryRecordVo : illHistoryRecordVos) {
                    if (illHistoryRecordVo.getRecordCare() != null) {
                        if (illHistoryRecordVo.getRecordCare().getOrderId() != null
                                && illHistoryRecordVo.getRecordCare().getOrderId().intValue() == careSummary
                                .getOrderId().intValue()) {

                            CareTip careTip = new CareTip();
                            careTip.setOrderId(careSummary.getOrderId());

                            Integer doctorId = careSummary.getDoctorId();
                            if (null != doctorId) {
                                User user = userRepository.getUser(doctorId);
                                if (null != user) {
                                    careTip.setDoctorName(user.getName());
                                }
                            }

                            if (careSummary.getUpdateTime() == null) {
                                careTip.setEndTime(careSummary.getCreateTime());
                            } else {
                                careTip.setEndTime(careSummary.getUpdateTime());
                            }
                            careTip.setResult(careSummary.getConsultAdvise());

                            String checkSuggestStr = careSummary.getAttention();
                            if (StringUtils.isNotEmpty(checkSuggestStr) && checkSuggestStr.contains(",")) {
                                String[] checkSuggests = checkSuggestStr.split(",");
                                StringBuffer checkSuggestBuffer = new StringBuffer();
                                for (String str : checkSuggests) {
                                    checkSuggestBuffer.append(checkSuggestMap.get(str)).append(",");
                                }
                                if (checkSuggestBuffer != null && checkSuggestBuffer.length() > 0) {
                                    checkSuggestBuffer.setLength(checkSuggestBuffer.length() - 1);
                                }
                                careTip.setCheckSuggestName(checkSuggestBuffer.toString());
                            } else if (StringUtils.isNotEmpty(checkSuggestStr)) {
                                careTip.setCheckSuggestName(checkSuggestMap.get(checkSuggestStr));
                            }

                            // 用药建议需要让药店提供批量查询的接口
                            if (StringUtils.isNotBlank(careSummary.getDrugAdvise())) {
                                careTip.setDrugAdviseId(careSummary.getDrugAdvise());
                                if (drugAdviseVos != null && drugAdviseVos.size() > 0) {
                                    for (DrugAdviseVo drugAdviseVo : drugAdviseVos) {
                                        if (StringUtils.equals(drugAdviseVo.getId(), careSummary.getDrugAdvise())) {
                                            careTip.setDrugAdviseVo(drugAdviseVo);
                                            break;
                                        }
                                    }
                                }
                            }

                            if (imageDatas != null && imageDatas.size() > 0) {
                                List<String> pics = Lists.newArrayList();
                                for (ImageData imageData : imageDatas) {
                                    if (imageData.getRelationId().intValue() == careSummary.getId().intValue()) {
                                        if (StringUtils.isNotBlank(imageData.getImageUrl())) {
                                            pics.add(imageData.getImageUrl());
                                        }
                                    }
                                }
                                careTip.setPics(pics);
                            }

                            if (voiceDatas != null && voiceDatas.size() > 0) {
                                List<String> voices = Lists.newArrayList();
                                for (ImageData imageData : voiceDatas) {
                                    if (imageData.getRelationId().intValue() == careSummary.getId().intValue()) {
                                        if (StringUtils.isNotBlank(imageData.getImageUrl())) {
                                            voices.add(imageData.getImageUrl());
                                        }
                                    }
                                }
                                careTip.setVoices(voices);
                            }

                            //初步诊断
                            String diseaseStr = careSummary.getConsultAdviseDiseases();
                            if (StringUtils.isNotEmpty(diseaseStr) && diseaseStr.contains(",")) {
                                String[] diseases = diseaseStr.split(",");
                                StringBuffer diagnosis = new StringBuffer();
                                for (String str : diseases) {
                                    diagnosis.append(diseaseTypeMap.get(str)).append(",");
                                }
                                if (diagnosis != null && diagnosis.length() > 0) {
                                    diagnosis.setLength(diagnosis.length() - 1);
                                }
                                careTip.setDiagnosis(diagnosis.toString());
                            } else if (StringUtils.isNotEmpty(diseaseStr)) {
                                careTip.setDiagnosis(diseaseTypeMap.get(diseaseStr));
                            }

                            if (illHistoryRecordVo.getRecordCare().getCareTips() != null) {
                                illHistoryRecordVo.getRecordCare().getCareTips().add(careTip);
                            } else {
                                List<CareTip> careTips = Lists.newArrayList();
                                careTips.add(careTip);
                                illHistoryRecordVo.getRecordCare().setCareTips(careTips);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 组装检查项
     *
     * @param illHistoryRecordVos 返回给前端的病程实体
     * @param checkItemIds        检查项的id列表
     */
    void assemblyCheckItem(List<IllHistoryRecordVo> illHistoryRecordVos, List<String> checkItemIds) {
        List<CheckItem> checkItems = checkBillDao.findByIds(checkItemIds);
        if (checkItems != null && checkItems.size() > 0) {
            for (CheckItem checkItem : checkItems) {
                for (IllHistoryRecordVo illHistoryRecordVo : illHistoryRecordVos) {
                    if (illHistoryRecordVo.getRecordCheckItem() != null) {
                        if (StringUtils.equals(illHistoryRecordVo.getRecordCheckItem().getCheckItemId(), checkItem.getId())) {
                            illHistoryRecordVo.getRecordCheckItem().setCheckupId(checkItem.getCheckUpId());
                            illHistoryRecordVo.getRecordCheckItem().setPics(checkItem.getImageList());
                            illHistoryRecordVo.getRecordCheckItem().setInfo(checkItem.getResults());
                            illHistoryRecordVo.getRecordCheckItem().setTime(checkItem.getVisitingTime());
                        }
                    }
                }
            }
        }
    }

    /**
     * 组装健康关怀专家组
     *
     * @param illHistoryRecordVos 返回给前端的病程对象
     * @param careGroupDoctorIds  健康关怀专家组医生id列表
     */
    void assemblyCareGroupDoctors(List<IllHistoryRecordVo> illHistoryRecordVos, List<Integer> careGroupDoctorIds) {
        List<User> users = userRepository.findUsers(careGroupDoctorIds);
        if (users != null && users.size() > 0) {
            for (User tempUser : users) {
                for (IllHistoryRecordVo illHistoryRecordVo : illHistoryRecordVos) {
                    if (illHistoryRecordVo.getRecordCare() != null) {
                        if (illHistoryRecordVo.getRecordCare().getGroupDoctors()!= null && illHistoryRecordVo.getRecordCare().getGroupDoctors().contains(tempUser.getUserId())) {
                            if (illHistoryRecordVo.getRecordCare().getGroupDoctorNames() != null) {
                                illHistoryRecordVo.getRecordCare().getGroupDoctorNames().add(tempUser.getName());
                            } else {
                                List<String> groupDoctorNames = Lists.newArrayList();
                                groupDoctorNames.add(tempUser.getName());
                                illHistoryRecordVo.getRecordCare().setGroupDoctorNames(groupDoctorNames);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 组装患者下单时所填写的用药信息
     *
     * @param illHistoryRecordVos 返回给前端的病程信息
     * @param drugInfoVos         药品信息
     */
    void assemblyDrugInfo(List<IllHistoryRecordVo> illHistoryRecordVos, List<DrugInfoVo> drugInfoVos) {
        for (DrugInfoVo drugInfoVo : drugInfoVos) {
            for (IllHistoryRecordVo illHistoryRecordVo : illHistoryRecordVos) {
                if (illHistoryRecordVo.getRecordOrder() != null && illHistoryRecordVo.getRecordOrder().getDrugGoodsIds() != null && illHistoryRecordVo.getRecordOrder().getDrugGoodsIds().size() > 0) {
                    if (illHistoryRecordVo.getRecordOrder().getDrugGoodsIds().contains(drugInfoVo.getId())) {
                        if (illHistoryRecordVo.getRecordOrder().getDrugInfoVos() == null) {
                            List<DrugInfoVo> list = Lists.newArrayList();
                            list.add(drugInfoVo);
                            illHistoryRecordVo.getRecordOrder().setDrugInfoVos(list);
                        } else {
                            illHistoryRecordVo.getRecordOrder().getDrugInfoVos().add(drugInfoVo);
                        }
                    }
                }

                if (illHistoryRecordVo.getRecordCare() != null && illHistoryRecordVo.getRecordCare().getDrugGoodsIds() != null && illHistoryRecordVo.getRecordCare().getDrugGoodsIds().size() > 0) {
                    if (illHistoryRecordVo.getRecordCare().getDrugGoodsIds().contains(drugInfoVo.getId())) {
                        if (illHistoryRecordVo.getRecordCare().getDrugInfoVos() == null) {
                            List<DrugInfoVo> list = Lists.newArrayList();
                            list.add(drugInfoVo);
                            illHistoryRecordVo.getRecordCare().setDrugInfoVos(list);
                        } else {
                            illHistoryRecordVo.getRecordCare().getDrugInfoVos().add(drugInfoVo);
                        }
                    }
                }
            }
        }
    }

    /**
     * 组装前端所需的patientInfovo对象
     *
     * @param patient
     * @param patientInfo
     * @return
     */
    PatientInfoVo fromPatientAndPatientInfo(Patient patient, PatientInfo patientInfo) {
        PatientInfoVo patientInfoVo = new PatientInfoVo();
        patientInfoVo.setPatientId(patient.getId());
        patientInfoVo.setUserId(patient.getUserId());
        patientInfoVo.setName(patient.getUserName());
        patientInfoVo.setSex(patient.getSex());
        patientInfoVo.setBirthday(patient.getBirthday());
        patientInfoVo.setAgeStr(patient.getAgeStr());
        patientInfoVo.setArea(patient.getArea());
        patientInfoVo.setPhone(patient.getTelephone());

        if (StringUtils.isBlank(patient.getTopPath())) {
            patientInfoVo.setHeadPicFileName(QiniuUtil.DEFAULT_AVATAR());
        } else {
            patientInfoVo.setHeadPicFileName(patient.getTopPath());
        }
        patientInfoVo.setHeight(patientInfo.getHeight());
        patientInfoVo.setWeight(patientInfo.getWeight());
        patientInfoVo.setMarriage(patientInfo.getMarriage());
        patientInfoVo.setJob(patientInfo.getJob());

        return patientInfoVo;
    }

    @Override
    public void updateBriefHistroy(String id, String history) {

        if (StringUtil.isEmpty(id)) {
            throw new ServiceException("病历ID不能为空");
        }

        IllHistoryInfo info = new IllHistoryInfo();
        info.setId(id);
        info.setBriefHistroy(history);

        illHistoryInfoDao.updateIllHistoryInfo(info);

    }

    @Override
    public Map<String, Object> analysisIllHistoryInfo(String illHistoryInfoId) {
        IllHistoryInfo illHistoryInfo = illHistoryInfoDao.findById(illHistoryInfoId);

        Map<String, Object> result = Maps.newHashMap();

        if (Objects.isNull(illHistoryInfo)) {
            result.put("display", false);
            return result;
        }

        Integer doctorId = illHistoryInfo.getDoctorId();
        if (Objects.isNull(doctorId)) {
            result.put("display", false);
            return result;
        }

        User doctor = userRepository.getUser(doctorId);
        if (doctor != null && doctor.getDoctor() != null && StringUtils.isNotBlank(doctor.getDoctor().getHospitalId())) {
            String h = doctor.getDoctor().getHospitalId();
            String p = String.valueOf(illHistoryInfo.getPatientId());
            result.put("display", true);
            String url = OrderBusinessListener.XG_SERVER_ANALYSIS + "?h=" + h +"&p=" + p;
            result.put("url", url);
            result.put("infoUrl", OrderBusinessListener.ILLHISTORYINFO_INFO);
            return result;
        } else {
            result.put("display", false);
            return result;
        }

    }

    private DiagnosisVO covertDiagnosis(Diagnosis diagnosis, Integer patientId) {
        DiagnosisVO vo = new DiagnosisVO();
        vo.setTime(diagnosis.getCreateTime());

        //获取创建者信息，区分医生和患者
        if (null != diagnosis.getFromDoctorId()) {
            User doctor = userRepository.getUser(diagnosis.getFromDoctorId());
            if (null != doctor) {
                vo.setUserName(doctor.getName());
                vo.setUserType(3);
            }
        } else {
            Patient patient = patientMapper.selectByPrimaryKey(patientId);
            if (null != patient) {
                vo.setUserName(patient.getUserName());
                vo.setUserType(1);
            }
        }

        //构建疾病诊断内容
        vo.setDiseaseName(diagnosis.getDiseaseName());
        vo.setContent(diagnosis.getContent());

        return vo;
    }

    private boolean canJoinIM(String gid, String currentUserId) throws HttpApiException {

        if (StringUtils.isBlank(gid) || StringUtils.isBlank(currentUserId)) {
            return false;
        }

        GroupInfoRequestMessage requestMessage = new GroupInfoRequestMessage();
        requestMessage.setGid(gid);
        requestMessage.setUserId(String.valueOf(currentUserId));
        GroupInfo groupInfo = (GroupInfo) msgService.getGroupInfo(requestMessage);
        if (Objects.isNull(groupInfo)) {
            return false;
        }

        List<GroupUserInfo> groupUserInfos = groupInfo.getUserList();

        boolean canJoinIM = false;

        if (groupUserInfos != null && groupUserInfos.size() > 0) {
            List<String> userIds = Lists.newArrayList();
            for (GroupUserInfo groupUserInfo : groupUserInfos) {
                userIds.add(groupUserInfo.getId());
            }
            if (userIds.contains(String.valueOf(currentUserId))) {
                canJoinIM = true;
            }
        }
        return canJoinIM;
    }

    private void assemblyDoctorName(List<IllHistoryRecordVo> illHistoryRecordVos, List<Integer> doctorIds) {
        List<User> users = userRepository.findUsers(doctorIds);
        if (users != null && users.size() > 0) {
            for (IllHistoryRecordVo illHistoryRecordVo : illHistoryRecordVos) {
                for (User user : users) {
                    if (illHistoryRecordVo.getRecordOrder() != null) {
                        if (illHistoryRecordVo.getRecordOrder().getDoctorId() != null && illHistoryRecordVo.getRecordOrder().getDoctorId().intValue() == user.getUserId().intValue()) {
                            illHistoryRecordVo.getRecordOrder().setDoctorName(user.getName());
                        }

                    }
                    if (illHistoryRecordVo.getRecordCare() != null) {
                        if (illHistoryRecordVo.getRecordCare().getMainDoctor() != null && illHistoryRecordVo.getRecordCare().getMainDoctor().intValue() == user.getUserId().intValue()) {
                            illHistoryRecordVo.getRecordCare().setMainDoctorName(user.getName());
                        }
                    }
                    if (illHistoryRecordVo.getRecordCheckIn() != null) {
                        if (illHistoryRecordVo.getRecordCheckIn().getDoctorId() != null && illHistoryRecordVo.getRecordCheckIn().getDoctorId().intValue() == user.getUserId().intValue()) {
                            illHistoryRecordVo.getRecordCheckIn().setDoctorName(user.getName());
                        }
                    }
                    if (illHistoryRecordVo.getRecordConsultiation() != null) {
                    	if(illHistoryRecordVo.getRecordConsultiation().getConsultJoinDocs() != null && illHistoryRecordVo.getRecordConsultiation().getConsultJoinDocs().contains(user.getUserId().intValue())){
                    		List<String> names = illHistoryRecordVo.getRecordConsultiation().getConsultJoinDocNames();
                        	names = names== null?new ArrayList<String>(): names;
                        	if(!names.contains(user.getName())){
                        		names.add(user.getName());
                        	}
                        	illHistoryRecordVo.getRecordConsultiation().setConsultJoinDocNames(names);
                    	}
                    }

                }
            }
        }

    }

    private void assemblyCreaterName(List<IllHistoryRecordVo> illHistoryRecordVos, List<Integer> createrIds, Patient patient) {
        List<User> users = userRepository.findUsers(createrIds);
        if (users != null && users.size() > 0) {
            for (User user : users) {
                for (IllHistoryRecordVo illHistoryRecordVo : illHistoryRecordVos) {
                    if (illHistoryRecordVo.getCreater() != null && illHistoryRecordVo.getCreater().intValue() == user.getUserId().intValue()) {
                        Integer createrType = user.getUserType();
                        if (createrType != null && (createrType.intValue() == UserType.doctor.getIndex() || createrType.intValue() == UserType.assistant.getIndex())) {
                            if (illHistoryRecordVo.getRecordCheckItem() != null) {
                                illHistoryRecordVo.getRecordCheckItem().setCreaterType(createrType);
                                illHistoryRecordVo.getRecordCheckItem().setCreaterName(user.getName());
                            }
                            if (illHistoryRecordVo.getRecordNormal() != null) {
                                illHistoryRecordVo.getRecordNormal().setCreaterType(createrType);
                                illHistoryRecordVo.getRecordNormal().setCreaterName(user.getName());
                            }
                        } else {
                            if (illHistoryRecordVo.getRecordCheckItem() != null) {
                                illHistoryRecordVo.getRecordCheckItem().setCreaterType(createrType);
                                illHistoryRecordVo.getRecordCheckItem().setCreaterName(patient.getUserName());
                            }
                            if (illHistoryRecordVo.getRecordNormal() != null) {
                                illHistoryRecordVo.getRecordNormal().setCreaterType(createrType);
                                illHistoryRecordVo.getRecordNormal().setCreaterName(patient.getUserName());
                            }
                        }

                    }
                }
            }
        }
    }

    private void sendCheckItemToXG(Order order, List<String> images, String checkupId, String checkItemId, String patientPhone, String patientAddress) {
        Patient patient = patientMapper.selectByPrimaryKey(order.getPatientId());
        User doctor = userRepository.getUser(order.getDoctorId());
        if (patient != null && doctor != null && doctor.getDoctor() != null && doctor.getDoctor().getCheck() != null) {
            Integer patientId = patient.getId();
            Integer sex = 1;
            if (patient.getSex() != null) {
                sex = Integer.valueOf(patient.getSex());
            }
            String patientName = "";
            if (StringUtils.isNotBlank(patient.getUserName())){
                patientName = patient.getUserName();
            }
            String hospitalId = doctor.getDoctor().getCheck().getHospitalId();
            String medicalHistoryUrl = OrderBusinessListener.ILLHISTORYINFO_H5 + "?illHistoryInfoId=" + order.getIllHistoryInfoId() + "&doctorId=" + order.getDoctorId() + "&patientId=" + order.getPatientId();
            String checkUpId = checkupId;
            OrderNotify.sendCheckItemToXG(patientId, sex, patientName, hospitalId, images, medicalHistoryUrl, checkUpId, checkItemId, patientPhone, patientAddress);

        }
    }

    private void sendCheckItemToXG(Integer doctorId, Integer patientId, String illHistoryInfoId, List<String> images, String checkupId, String checkItemId, String patientPhone, String patientAddress) {
        Patient patient = patientMapper.selectByPrimaryKey(patientId);
        User doctor = userRepository.getUser(doctorId);
        if (patient != null && doctor != null && doctor.getDoctor() != null && doctor.getDoctor().getCheck() != null) {
            Integer sex = 1;
            if (patient.getSex() != null) {
                sex = Integer.valueOf(patient.getSex());
            }
            String patientName = "";
            if (StringUtils.isNotBlank(patient.getUserName())){
                patientName = patient.getUserName();
            }
            String hospitalId = doctor.getDoctor().getCheck().getHospitalId();
            String medicalHistoryUrl = OrderBusinessListener.ILLHISTORYINFO_H5 + "?illHistoryInfoId=" + illHistoryInfoId + "&doctorId=" + doctorId + "&patientId=" + patientId;
            String checkUpId = checkupId;
            OrderNotify.sendCheckItemToXG(patientId, sex, patientName, hospitalId, images, medicalHistoryUrl, checkUpId, checkItemId, patientPhone, patientAddress);

        }
    }
}
