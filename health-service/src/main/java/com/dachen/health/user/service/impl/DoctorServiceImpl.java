package com.dachen.health.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventProducer;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.elasticsearch.model.EsDiseaseType;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.commons.constants.DoctorInfoChangeEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.impl.UserOperationLogService;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.doctor.service.ICommonGroupDoctorService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.user.dao.IDoctorDao;
import com.dachen.health.user.entity.param.DoctorParam;
import com.dachen.health.user.entity.param.GetRecheckInfo;
import com.dachen.health.user.entity.param.ResetDoctorInfo;
import com.dachen.health.user.entity.po.DoctorCheckInfoChange;
import com.dachen.health.user.entity.vo.*;
import com.dachen.health.user.service.IDoctorService;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.mq.producer.BasicProducer;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.BeanUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DoctorServiceImpl implements IDoctorService {

    private Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);

    @Autowired
    private IBusinessServiceMsg businessMsgService;
    @Autowired
    private IDoctorDao doctorDao;

    @Autowired
    private ICommonGroupDoctorService commonGroupDoctorService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiseaseTypeRepository diseaseTypeRepository;

    @Autowired
    private UserOperationLogService userOperationLogService;

    @Autowired
    private Auth2Helper auth2Helper;

    /**
     * </p>
     * 获取个人介绍和擅长领域
     * </p>
     * 
     * @param userId
     * @return
     * @author fanp
     * @date 2015年7月7日
     */
    public Map<String, Object> getIntro(Integer userId) {
        return doctorDao.getIntro(userId);
    }

    /**
     * </p>
     * 设置医生个人介绍
     * </p>
     * 
     * @param introduction 个人介绍
     * @author fanp
     * @date 2015年7月7日
     */
    public void updateIntro(String introduction) throws HttpApiException {
        UserSession session = ReqUtil.instance.getUser();

        if (session == null) {
            throw new ServiceException("未登陆，无法设置我的介绍");
        } else if (session.getUserType() != UserEnum.UserType.doctor.getIndex()) {
            throw new ServiceException("不是医生，无法设置我的介绍");
        }
        if (StringUtil.isBlank(introduction)) {
            throw new ServiceException("请填写我的介绍");
        } else if (introduction.length() > 4000) {
            throw new ServiceException("我的介绍最多4000字符");
        }

        doctorDao.updateIntro(session.getUserId(), introduction);

        businessMsgService.userChangeNotify(UserChangeTypeEnum.PROFILE_CHANGE,
                ReqUtil.instance.getUser().getUserId(), null);
    }


    private List<EsDiseaseType> getEsDiseaseTypeList(List<String> diseaseIds) {
        if (diseaseIds == null || diseaseIds.size() == 0) {
            return null;
        }
        List<DiseaseType> diseases = diseaseTypeRepository.findByIds(diseaseIds);
        // 医生集团擅长病种
        List<EsDiseaseType> diseaseTypeList = new ArrayList<EsDiseaseType>(diseaseIds.size());
        for (DiseaseType type : diseases) {
            diseaseTypeList.add(new EsDiseaseType(type.getName(),
                    type.getAlias(), type.getRemark()));
        }
        return diseaseTypeList;
    }


    /**
     * </p>
     * 设置医生擅长领域
     * </p>
     * 
     * @param skill 擅长
     * @author fanp
     * @date 2015年7月7日
     */
    public void updateSkill(String skill) throws HttpApiException {
        UserSession session = ReqUtil.instance.getUser();

        if (Objects.isNull(session)) {
            throw new ServiceException("用户没有登录。");
        }
        if (session.getUserType() != UserEnum.UserType.doctor.getIndex()) {
            throw new ServiceException("用户不是医生。");
        }

        if (Objects.isNull(skill)) {
            skill = "";
        }
        if (skill.length() > 4000) {
            throw new ServiceException("信息超过4000字符");
        }
        skill = skill.trim();

        doctorDao.updateSkill(session.getUserId(), skill);
        businessMsgService.userChangeNotify(UserChangeTypeEnum.PROFILE_CHANGE, 
                session.getUserId(), null);
        
        // 完成之后调用接口更新ES服务器数据(采用异步调用的方式更新数据 防止等待超时)
        User user = userRepository.getUser(session.getUserId());
        if (Objects.isNull(user))
            throw new ServiceException("用户不存在！！");
        // 将需要更新的数据放入消息队列中
        EcEvent event = EcEvent.build(EventType.DoctorInfoUpdateForEs)
                .param("departments", user.getDoctor().getDepartments())
                .param("name", user.getName())
                .param("skill", skill)
                .param("userId", user.getUserId())
                .param("expertise", getEsDiseaseTypeList(user.getDoctor().getExpertise()));
        EventProducer.fireEvent(event);
    }

    @Override
    public void updateScholarship(String scholarship) throws HttpApiException {
        UserSession session = ReqUtil.instance.getUser();

        if (Objects.isNull(session)) {
            throw new ServiceException("用户没有登录。");
        }
        if (session.getUserType() != UserEnum.UserType.doctor.getIndex()) {
            throw new ServiceException("用户不是医生。");
        }

        if (Objects.isNull(scholarship)) {
            scholarship = "";
        }
        if (scholarship.length() > 4000) {
            throw new ServiceException("信息超过4000字符");
        }
        scholarship.trim();

        doctorDao.updateScholarship(session.getUserId(), scholarship);
        businessMsgService.userChangeNotify(UserChangeTypeEnum.PROFILE_CHANGE, 
                session.getUserId(), null);

        // 调用接口更新ES服务器数据(采用异步调用的方式更新数据 防止等待超时)
        User user = userRepository.getUser(session.getUserId());
        if (Objects.isNull(user)) {
            throw new ServiceException("用户不存在！！");
        }
        // 将需要更新的数据放入消息队列中
        EcEvent event = EcEvent.build(EventType.DoctorInfoUpdateForEs)
                .param("departments", user.getDoctor().getDepartments())
                .param("name", user.getName())
                .param("scholarship", scholarship)
                .param("userId", user.getUserId())
                .param("expertise", getEsDiseaseTypeList(user.getDoctor().getExpertise()));
        EventProducer.fireEvent(event);
    }

    @Override
    public void updateExperience(String experience) throws HttpApiException {
        UserSession session = ReqUtil.instance.getUser();

        if (Objects.isNull(session)) {
            throw new ServiceException("用户没有登录。");
        }
        if (session.getUserType() != UserEnum.UserType.doctor.getIndex()) {
            throw new ServiceException("用户不是医生。");
        }

        if (Objects.isNull(experience)) {
            experience = "";
        }
        if (experience.length() > 4000) {
            throw new ServiceException("信息超过4000字符");
        }
        experience.trim();

        doctorDao.updateExperience(session.getUserId(), experience);
        businessMsgService.userChangeNotify(UserChangeTypeEnum.PROFILE_CHANGE, 
                session.getUserId(), null);

        // 调用接口更新ES服务器数据(采用异步调用的方式更新数据 防止等待超时)
        User user = userRepository.getUser(session.getUserId());
        if (Objects.isNull(user)) {
            throw new ServiceException("用户不存在！！");
        }
        // 将需要更新的数据放入消息队列中
        EcEvent event = EcEvent.build(EventType.DoctorInfoUpdateForEs)
                .param("departments", user.getDoctor().getDepartments())
                .param("name", user.getName())
                .param("experience", experience)
                .param("userId", user.getUserId())
                .param("expertise", getEsDiseaseTypeList(user.getDoctor().getExpertise()));
        EventProducer.fireEvent(event);
    }

    /**
     * </p>
     * 修改职业信息
     * </p>
     * 
     * @author fanp
     * @date 2015年7月7日
     */
    public DoctorVO getWork(Integer userId) {
        return doctorDao.getWork(userId);
    }

    /**
     * </p>
     * 修改职业信息
     * </p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月7日
     */
    public void updateWork(DoctorParam param) throws HttpApiException {
        doctorDao.updateWork(param);
        commonGroupDoctorService.updateGroupDoctor(ReqUtil.instance.getUser().getUserId(),
                param.getDepartments());
        businessMsgService.userChangeNotify(UserChangeTypeEnum.PROFILE_CHANGE,
                ReqUtil.instance.getUser().getUserId(), null);
    }

    /**
     * </p>
     * 医生获取认证信息
     * </p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月15日
     */
    public DoctorVO getCheckInfo(DoctorParam param) {
        DoctorVO checkInfo = doctorDao.getCheckInfo(param);
        /* 获取医生是否有未处理的认证资料修改申请(0:没有;1:有) */
        Integer checkInfoStatus = this.getCheckInfoStatus(param.getUserId());
        checkInfo.setInfoStatus(checkInfoStatus);
        return checkInfo;
    }

    /**
     * </p>
     * 医生认证失败后修改认证信息
     * </p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月13日
     */
    public void updateCheckInfo(DoctorParam param) {
        param.setStatuses(new Integer[] {UserEnum.UserStatus.uncheck.getIndex(),
                UserEnum.UserStatus.fail.getIndex()});
        param.setStatus(UserEnum.UserStatus.uncheck.getIndex());
        doctorDao.updateWork(param);
    }


    @Override
    public Object search(DoctorParam param) {
        return doctorDao.search(param);
    }

    @Override
    public Object searchs(DoctorParam param) {
        return doctorDao.searchs(param);
    }

    @Override
    public void updateExpertise(Integer userId, String[] diseaseTypeId) {
        doctorDao.updateExpertise(userId, diseaseTypeId);
        // 完成之后调用接口更新ES服务器数据
        // 将需要更新的数据放入消息队列中 这里只负责推--UserBusinessListener监听器里面负责取（fireDoctorExpertiseSuccess）
        User user = userRepository.getUser(userId);
        if (user == null)
            throw new ServiceException("用户不存在！！");
        EcEvent event = EcEvent.build(EventType.DoctorInfoUpdateForEs)
                .param("departments", user.getDoctor().getDepartments())
                .param("name", user.getName()).param("skill", user.getDoctor().getSkill())
                .param("userId", user.getUserId())
                .param("expertise", getEsDiseaseTypeList(user.getDoctor().getExpertise()));
        EventProducer.fireEvent(event);
        /*
         * Thread thread = new Thread(new Runnable() { public void run() { User user =
         * userManagerImpl.getUser(userId); if(user==null)throw new ServiceException("用户不存在！！");
         * EsDoctor doctor = new EsDoctor(String.valueOf(userId));
         * doctor.setDepartments(user.getDoctor().getDepartments()); doctor.setName(user.getName());
         * doctor.setSkill(user.getDoctor().getSkill());
         * doctor.setExpertise(getEsDiseaseTypeList(user.getDoctor().getExpertise()));
         * ElasticSearchFactory.getInstance().updateDocument(doctor); } }); thread.start();
         */
    }

    @Override
    public void deleteExpertise(Integer userId, String[] diseaseTypeId) {
        doctorDao.deleteExpertise(userId, diseaseTypeId);
    }

    @Override
    public Object getExpertise(Integer userId) {
        return doctorDao.getExpertise(userId);

    }

    @Override
    public void updateCureNum(Integer dcoID) {
        Integer cureNum = 0;
        Map<String, Object> map = doctorDao.getIntro(dcoID);
        if (map != null) {
            cureNum = map.get("cureNum") != null ? ((Integer) map.get("cureNum") + 1) : 1;
        }
        doctorDao.updateCureNum(dcoID, cureNum);
    }

    @Override
    public void updateMsgDisturb(int doctorUserId, String troubleFree) {
        // UserSession session = ReqUtil.instance.getUser();
        // if (session == null) {
        // throw new ServiceException("未登陆，无法设置我的介绍");
        // } else if (session.getUserType() != UserEnum.UserType.doctor.getIndex()) {
        // throw new ServiceException("不是医生，无法设置我的介绍");
        // }
        // if (StringUtil.isBlank(introduction)) {
        // throw new ServiceException("请填写我的介绍");
        // }
        // else if (introduction.length() > 4000) {
        // throw new ServiceException("我的介绍最多4000字符");
        // }
        // 更新数据库
        doctorDao.updateMsgDisturb(doctorUserId, troubleFree);

        // 多端登录，需要发送指令
        EventEnum _eventEnum = null;
        if (troubleFree != null && troubleFree.equalsIgnoreCase("2")) {
            // 开启免打扰
            _eventEnum = EventEnum.DOCTOR_DISTURB_ON;
        } else {
            _eventEnum = EventEnum.DOCTOR_DISTURB_OFF;
        }
        String userId = String.valueOf(ReqUtil.instance.getUser().getUserId());
        businessMsgService.sendEventDoctorDisturb(_eventEnum, userId, userId);
        // (TODO 不要这行)
        // businessMsgService.userChangeNotify(UserChangeTypeEnum.PROFILE_CHANGE,
        // ReqUtil.instance.getUser().getUserId(), null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PageVO researchDoctors(String doctorName, String hospitalId, Integer pageIndex,
            Integer pageSize) {
        PageVO page = new PageVO();
        if (null == pageIndex || pageIndex.intValue() == 0) {
            pageIndex = 1;
        }
        if (null == pageSize || pageSize.intValue() == 0) {
            pageIndex = 15;
        }
        Map<String, Object> map =
                doctorDao.researchDoctors(doctorName, hospitalId, pageIndex, pageSize);
        page.setPageData((List<NurseVO>) map.get("list"));
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        page.setTotal(Long.parseLong(map.get("count").toString()));
        return page;
    }

    @Override
    public Integer getCheckInfoStatus(int userId) {
        return doctorDao.getCheckInfoStatus(userId);
    }

    @Override
    public void resetCheckInfo(ResetDoctorInfo param) {
        /* 如果有待处理的认证信息 update 否则 insert*/
        if (Objects.equals(this.getCheckInfoStatus(param.getUserId()), 1)) {
            doctorDao.resetCheckInfo(param);
        } else {
            this.addCheckInfo(param);
        }
    }

    @Override
    public void addCheckInfo(ResetDoctorInfo param) {
        doctorDao.addCheckInfo(param);
    }

    @Override
    public PageVO getAfterCheckInfo(GetRecheckInfo getRecheckInfo) {
        if (Objects.isNull(getRecheckInfo.getInfoStatus())) {
            throw new ServiceException("处理状态不能为空");
        }
        PageVO pageVO = doctorDao.getAfterCheckInfo(getRecheckInfo);
        List<DoctorCheckInfoChange> doctorCheckInfoChange = (List<DoctorCheckInfoChange>) pageVO.getPageData();
        if (!CollectionUtils.isEmpty(doctorCheckInfoChange)) {
            List<DoctorRecheckInfoVO> copyList = Lists.newArrayList();
            for (DoctorCheckInfoChange checkInfoChange : doctorCheckInfoChange) {
                DoctorRecheckInfoVO copy = BeanUtil.copy(checkInfoChange, DoctorRecheckInfoVO.class);
                copy.setTelephone(userRepository.getUser(copy.getUserId()).getTelephone());
                copyList.add(copy);
            }
            pageVO.setPageData(copyList);
        }
        return pageVO;
    }

    @Override
    public void handleCheckInfo(ResetDoctorInfo resetDoctorInfo) {
        if (StringUtil.isBlank(resetDoctorInfo.getId())) {
            throw new ServiceException("id不能为空");
        }
        if (Objects.isNull(resetDoctorInfo.getVerifyResult())) {
            throw new ServiceException("处理结果不能为空");
        }
        if (Objects.isNull(resetDoctorInfo.getUserId())) {
            throw new ServiceException("用户id不能为空");
        }
        DoctorCheckInfoChange checkInfoDetail = doctorDao.getCheckInfoDetail(resetDoctorInfo.getId());
        if (Objects.nonNull(checkInfoDetail) && Objects.equals(checkInfoDetail.getInfoStatus(), DoctorInfoChangeEnum.InfoStatus.check.getIndex())) {
            throw new ServiceException("该用户资料变更申请已被审核");
        }
        User oldUser = userRepository.getUser(resetDoctorInfo.getUserId());
        doctorDao.handleCheckInfo(resetDoctorInfo);
        /* 如果资料变更申请审核通过 修改doctor信息和审核信息 */
        if (Objects.equals(resetDoctorInfo.getVerifyResult(), DoctorInfoChangeEnum.VerifyResult.agree.getIndex())) {
            User user = doctorDao.updateUserCheckInfo(resetDoctorInfo);
            /* 发送通知清除缓存 */
            if (StringUtil.isNotBlank(resetDoctorInfo.getHeadPicFileName()) || StringUtil.isNoneBlank(resetDoctorInfo.getName()) || StringUtil.isNotBlank(resetDoctorInfo.getHospitalId()) || StringUtil.isNotBlank(resetDoctorInfo.getDeptPhone()) || StringUtil.isNotBlank(resetDoctorInfo.getDepartments()) || StringUtil.isNotBlank(resetDoctorInfo.getTitle())) {
                logger.info("资料变更申请审核通过，用户信息更新，发送MQ消息。userId={}", user.getUserId());
                userRepository.updateUserSessionCache(user);
                // get set userId
                List<AccessToken> openIdList = auth2Helper.getOpenIdList(Arrays.asList(user.getUserId()));
                if (!CollectionUtils.isEmpty(openIdList)) {
                    user.setOpenId(openIdList.get(0).getOpenId());
                }
                BasicProducer.fanoutMessage("doctorInfoChange", JSON.toJSONString(user));
            }
        }
        resetDoctorInfo.setCheckerId(ReqUtil.instance.getUserId());
        if (Objects.equals(resetDoctorInfo.getVerifyResult(), DoctorInfoChangeEnum.VerifyResult.agree.getIndex())) {
            userOperationLogService.logReChangeDoctorInfoSusRecord(oldUser, resetDoctorInfo);
        } else if (Objects.equals(resetDoctorInfo.getVerifyResult(), DoctorInfoChangeEnum.VerifyResult.disagree.getIndex())) {
            userOperationLogService.logReChangeDoctorInfoFailRecord(resetDoctorInfo);
        }
    }

    @Override
    public DoctorRecheckInfoDetailVO getCheckInfoDetail(String id) {
        if (StringUtil.isBlank(id)) {
            throw new ServiceException("id不能为空");
        }
        DoctorCheckInfoChange doctorCheckInfoChange = doctorDao.getCheckInfoDetail(id);
        DoctorRecheckInfoDetailVO copy = BeanUtil.copy(doctorCheckInfoChange, DoctorRecheckInfoDetailVO.class);
        copy.setTelephone(userRepository.getUser(copy.getUserId()).getTelephone());
        return copy;
    }

    @Override
    public DoctorDealingInfoVO getDealingInfo() {
        DoctorCheckInfoChange doctorCheckInfoChange = doctorDao.getDealingInfo(ReqUtil.instance.getUserId(), DoctorInfoChangeEnum.InfoStatus.uncheck.getIndex());
        List<Map<String, String>> list = Lists.newArrayList();
        /* 满足前端无理要求 强行转map格式 */
        List<String> checkImage = doctorCheckInfoChange.getCheckImage();
        if (!CollectionUtils.isEmpty(checkImage)) {
            for (String s : checkImage) {
                Map<String, String> map = Maps.newHashMap();
                map.put("id", "0");
                map.put("url", s);
                list.add(map);
            }
        }
        DoctorDealingInfoVO copy = BeanUtil.copy(doctorCheckInfoChange, DoctorDealingInfoVO.class);
        copy.setStatus(userRepository.getUser(copy.getUserId()).getStatus());
        copy.setCheckImage(list);
        return copy;
    }

    @Override
    public Long getUncheckInfoCount() {
        return doctorDao.getUncheckInfoCount();
    }

}
