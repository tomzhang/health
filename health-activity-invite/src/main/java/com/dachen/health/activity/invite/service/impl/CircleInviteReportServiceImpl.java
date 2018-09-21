package com.dachen.health.activity.invite.service.impl;

import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.exception.ServiceException;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.drugorg.api.entity.CEnterpriseUser;
import com.dachen.drugorg.api.entity.CSimpleUser;
import com.dachen.health.activity.invite.api.auth2.Auth2ApiProxy;
import com.dachen.health.activity.invite.api.auth2.AuthSimpleUser;
import com.dachen.health.activity.invite.entity.Activity;
import com.dachen.health.activity.invite.entity.CircleInviteReport;
import com.dachen.health.activity.invite.form.InvitationReportForm;
import com.dachen.health.activity.invite.service.ActivityService;
import com.dachen.health.activity.invite.service.CircleInviteReportService;
import com.dachen.health.activity.invite.vo.InvitationReportVO;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;
import com.dachen.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/5 20:25 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Model(value = CircleInviteReport.class)
@Service
public class CircleInviteReportServiceImpl extends BaseServiceImpl implements CircleInviteReportService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ActivityService activityService;

    @Autowired
    @Lazy
    protected UserRepository userRepository;

    @Autowired
    private DrugOrgApiClientProxy drugOrgApiClientProxy;

    @Autowired
    private Auth2ApiProxy auth2ApiProxy;

    @Autowired
    private Auth2Helper auth2Helper;

    @Override
    public CircleInviteReport getByUserIdAndActivityIdAndSubsystem(Integer userId, String activityId, Integer subsystem) {
        Query<CircleInviteReport> query = this.createBizQuery(userId, activityId, subsystem);
        return query.get();
    }

    private <T> Query<T> createBizQuery(Integer userId, String activityId, Integer subsystem) {
        if (Objects.isNull(userId)){
            throw new ServiceException("用户Id不能为空");
        }
        if (StringUtil.isBlank(activityId)){
            throw new ServiceException("活动Id不能为空");
        }
        if (Objects.isNull(subsystem)){
            throw new ServiceException("用户Id不能为空");
        }
        Query<T> query = this.createQuery();
        query.filter("userId", userId);
        query.filter("activityId", activityId);
        query.filter("subsystem", subsystem);
        return query;
    }

    @Override
    public void incWechatCount(Integer userId, String activityId, Integer subsystem) {
        incField(userId, activityId, subsystem, "wechatCount");
    }

    private void incField(Integer userId, String activityId, Integer subsystem, String fieldName) {
        synchronized (this) {
            Query<CircleInviteReport> query = this.createBizQuery(userId, activityId, subsystem);
            if (query.get() == null){
                saveCircleInviteReport(userId, activityId, subsystem);
            }
            UpdateOperations<CircleInviteReport> ops = this.createUpdateOperations();
            ops.inc(fieldName);
            this.update(query, ops);
        }
    }

    private void saveCircleInviteReport(Integer userId, String activityId, Integer subsystem) {
        CircleInviteReport report = createReportInfo(userId, activityId, subsystem);
        this.saveEntity(report);
    }

    private CircleInviteReport createReportInfo(Integer userId, String activityId,
        Integer subsystem) {
        CircleInviteReport report = new CircleInviteReport();
        report.setUserId(userId);
        //根据子系统设置邀请人姓名
        report.setUserName(getInviterName(userId, subsystem));
        report.setActivityId(activityId);
        report.setSubsystem(subsystem);
        report.setWechatCount(0);
        report.setSmsCount(0);
        report.setQrcodeCount(0);
        report.setRegisteredCount(0);
        report.setAutherizedCount(0);
        return report;
    }

    private String getInviterName(Integer userId, Integer subsystem) {
        String userName = "";
        if (Source.drugOrg.getIndex() == subsystem){
            CSimpleUser simpleUser = getCSimpleUser(userId);
            if(simpleUser != null){
                userName = simpleUser.getName();
            }
        } else if (Source.doctorCircle.getIndex() == subsystem){
            User user = userRepository.getUser(userId);
            if (user != null){
                userName = user.getName();
            }
        }
        
        return userName;
    }

    private CSimpleUser getCSimpleUser(Integer userId) {
        List<Integer> userIds = new ArrayList<>();
        userIds.add(userId);
        try {
            List<CSimpleUser> simpleUsers = drugOrgApiClientProxy.getSimpleUser(userIds);
            if (!CollectionUtils.isEmpty(simpleUsers)) {
                return simpleUsers.get(0);
            }
        } catch (HttpApiException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void incSmsCount(Integer userId, String activityId, Integer subsystem) {
        incField(userId, activityId, subsystem, "smsCount");
    }

    @Override
    public void incQrcodeCount(Integer userId, String activityId, Integer subsystem) {
        incField(userId, activityId, subsystem, "qrcodeCount");
    }

    @Override
    public void incRegisteredCount(Integer userId, String activityId, Integer subsystem) {
        incField(userId, activityId, subsystem, "registeredCount");
        logger.info("ActivityInviteRegistration incRegisteredCount userId:{}", userId);
    }

    @Override
    public void incAutherizedCount(Integer userId, String activityId, Integer subsystem) {
//        incField(userId, activityId, subsystem, "autherizedCount");
        String key = userId + "_" + activityId + "_" + subsystem;
        logger.info("incAutherizedCount.计算“已认证用户数”，对应的KEY: {}", key);
        Query<CircleInviteReport> query = this.createBizQuery(userId, activityId, subsystem);
        if (query.get() == null){
            logger.error("incAutherizedCount.未找到对应的注册记录，不计入“已认证用户数”，对应的KEY: {}", key);
            return;
        }
        UpdateOperations<CircleInviteReport> ops = this.createUpdateOperations();
        ops.inc("autherizedCount");
        this.update(query, ops);
    }

    @Override
    public void updateReport(Integer userId, String activityId, Integer subsystem, String way) {
        switch (way){
            case "qrcode":
                incQrcodeCount(userId, activityId, subsystem);
                break;
            case "wechat":
                incWechatCount(userId, activityId, subsystem);
                break;
            case "sms":
                incSmsCount(userId, activityId, subsystem);
                break;
            default:
                throw new ServiceException("forbidden");
        }
    }

    @Override
    public Pagination<InvitationReportVO> listInvitation(InvitationReportForm form,
        Integer pageIndex, Integer pageSize) {
        String tag = "listInvitation";
        Query<CircleInviteReport> query =  this.createQuery();
        setMatchQuery(query, form);

        long total = query.countAll();

        query.order("-registeredCount");//按已注册用户数倒序排列

        query.offset(pageIndex * pageSize).limit(pageSize);

        logger.debug("{}. query={}", tag, query);

        List<CircleInviteReport> list = query.asList();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<InvitationReportVO> voList = new ArrayList<>();

        Set<Integer> userIdList = list.stream().map(CircleInviteReport::getUserId).collect(Collectors.toSet());
        Map<Integer, AuthSimpleUser> userMap = null;
        Map<Integer, AccessToken> tokenMap = null;
        if (!CollectionUtils.isEmpty(userIdList)) {
            List<AuthSimpleUser> userList = auth2ApiProxy.getSimpleUserList(new ArrayList<>(userIdList));
            userMap = userList.stream().collect(Collectors.toMap(AuthSimpleUser::getId, Function.identity()));

            List<AccessToken> tokenList = auth2Helper.getOpenIdList(new ArrayList<>(userIdList));
            tokenMap = tokenList.stream().collect(Collectors.toMap(AccessToken::getUserId, Function.identity()));
        }

        for (CircleInviteReport report: list) {
            voList.add(circleInviteReport2VO(report, userMap, tokenMap));
        }

        Pagination<InvitationReportVO> page = new Pagination<>(voList, total, pageIndex, pageSize);
        return page;

    }

    @Override
    public long listInvitationCount(InvitationReportForm form) {
        Query<CircleInviteReport> query =  this.createQuery();
        setMatchQuery(query, form);

        return query.countAll();
    }

    private InvitationReportVO circleInviteReport2VO(CircleInviteReport report, Map<Integer, AuthSimpleUser> userMap,
        Map<Integer, AccessToken> tokenMap) {
        InvitationReportVO vo = new InvitationReportVO();
        if (Objects.nonNull(userMap)) {
            AuthSimpleUser user = userMap.get(report.getUserId());
            if (Objects.nonNull(user)) {
                vo.setUserName(user.getName());
            }
        }
        vo.setUserId(report.getUserId());

        if (Objects.nonNull(tokenMap)) {
            AccessToken token = tokenMap.get(report.getUserId());
            if (Objects.nonNull(token)) {
                vo.setOpenId(token.getOpenId());
            }
        }

        if (StringUtil.isNotBlank(report.getActivityId())){
            Activity activity = activityService.findById(report.getActivityId());
            if (activity != null){
                vo.setActivityName(activity.getName());
            }
        }
        if (report.getUserId() != null){
            User user = userRepository.getUser(report.getUserId());
            if (user != null && user.getDoctor() != null){
                vo.setHospital(user.getDoctor().getHospital());
                vo.setTitle(user.getDoctor().getTitle());
            }
        }
        vo.setSubsystem(report.getSubsystem());
        vo.setWechatCount(report.getWechatCount());
        vo.setSmsCount(report.getSmsCount());
        vo.setQrcodeCount(report.getQrcodeCount());
        vo.setRegisteredCount(report.getRegisteredCount());
        vo.setAutherizedCount(report.getAutherizedCount());
        return vo;
    }

    private void setMatchQuery(Query<CircleInviteReport> query, InvitationReportForm form) {
        if (StringUtil.isNotBlank(form.getActivityId())) {
            query.filter("activityId", form.getActivityId());
        }
        if (!Objects.isNull(form.getSubsystem())) {
            query.filter("subsystem", form.getSubsystem());
        }
        if (StringUtil.isNotBlank(form.getUserName())) {

            List<User> userList = userRepository.searchDoctorByKeyword(form.getUserName(), null);
            List<CEnterpriseUser> CUserList = null;//业务助理，即医药代表
            try {
                CUserList = drugOrgApiClientProxy.listSaleByKeyword(form.getUserName());
            } catch (HttpApiException e) {
                logger.info(e.getMessage(), e);
            }
            if (!CollectionUtils.isEmpty(userList) && !CollectionUtils.isEmpty(CUserList)) {
                Set<Integer> userIdList = userList.stream().map(User::getUserId).collect(Collectors.toSet());
                Set<Integer> CUserIdList = CUserList.stream().map(CEnterpriseUser::getUserId).collect(Collectors.toSet());

                query.or(query.criteria("userId").in(userIdList),
                    query.criteria("userId").in(CUserIdList));
            } else if (!CollectionUtils.isEmpty(userList)) {
                Set<Integer> userIdList = userList.stream().map(User::getUserId).collect(Collectors.toSet());
                query.field("userId").in(userIdList);
            } else if (!CollectionUtils.isEmpty(CUserList)) {
                Set<Integer> CUserIdList = CUserList.stream().map(CEnterpriseUser::getUserId).collect(Collectors.toSet());
                query.field("userId").in(CUserIdList);
            } else {
                query.field("userId").equal(0);
            }

//            query.filter("userName", createPattern(form.getUserName()));
        }
    }

    private Pattern createPattern(String keyword) {
        Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
        return pattern;
    }
}
