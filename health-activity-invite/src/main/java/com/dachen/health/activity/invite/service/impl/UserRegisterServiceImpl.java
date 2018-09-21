package com.dachen.health.activity.invite.service.impl;

import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.drugorg.api.entity.CSimpleUser;
import com.dachen.health.activity.invite.api.auth2.Auth2ApiProxy;
import com.dachen.health.activity.invite.api.auth2.AuthSimpleUser;
import com.dachen.health.activity.invite.entity.Activity;
import com.dachen.health.activity.invite.entity.CircleInviteReport;
import com.dachen.health.activity.invite.form.RegistrationReportForm;
import com.dachen.health.activity.invite.service.ActivityService;
import com.dachen.health.activity.invite.service.UserRegisterService;
import com.dachen.health.activity.invite.vo.RegistrationReportVO;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/6 18:00 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Model(value = User.class)
@Service
public class UserRegisterServiceImpl extends IntegerBaseServiceImpl implements UserRegisterService {
    private static final Logger logger = LoggerFactory.getLogger(UserRegisterServiceImpl.class);
    @Autowired
    private ActivityService activityService;
    @Autowired
    @Lazy
    protected UserRepository userRepository;

    @Autowired
    private Auth2ApiProxy auth2ApiProxy;
    @Autowired
    private Auth2Helper auth2Helper;

    @Override
    public Pagination<RegistrationReportVO> userRegistration(RegistrationReportForm form,
        Integer pageIndex, Integer pageSize) {
        String tag = "userRegistration";
        Query<User> query =  this.createQuery();
        setMatchQuery(query, form);

        long total = query.countAll();

        query.order("-createTime");//按注册时间倒序排列

        query.offset(pageIndex * pageSize).limit(pageSize);

        logger.debug("{}. query={}", tag, query);

        List<User> list = query.asList();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<RegistrationReportVO> voList = new ArrayList<>();

        Set<Integer> userIdList = new HashSet<>();
        for (User user : list) {
            if (Objects.nonNull(user) && Objects.nonNull(user.getSource())) {
                userIdList.add(user.getSource().getInviterId());
            }
        }

        Map<Integer, AuthSimpleUser> userMap = null;
        Map<Integer, AccessToken> tokenMap = null;
        if (!CollectionUtils.isEmpty(userIdList)) {
            List<AuthSimpleUser> userList = auth2ApiProxy.getSimpleUserList(new ArrayList<>(userIdList));
            userMap = userList.stream().collect(Collectors.toMap(AuthSimpleUser::getId, Function.identity()));

            List<AccessToken> tokenList = auth2Helper.getOpenIdList(new ArrayList<>(userIdList));
            tokenMap = tokenList.stream().collect(Collectors.toMap(AccessToken::getUserId, Function.identity()));
        }

        for (User user: list) {
            voList.add(user2VO(user, userMap, tokenMap));
        }

        Pagination<RegistrationReportVO> page = new Pagination<>(voList, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public long userRegistrationCount(RegistrationReportForm form) {
        Query<User> query =  this.createQuery();
        setMatchQuery(query, form);

        return query.countAll();
    }

    private RegistrationReportVO user2VO(User user, Map<Integer, AuthSimpleUser> userMap,
        Map<Integer, AccessToken> tokenMap) {
        RegistrationReportVO vo = new RegistrationReportVO();
        vo.setRegistrationTime(user.getCreateTime());
        vo.setUserName(user.getName());
        if (user.getDoctor() != null){
            vo.setHospital(user.getDoctor().getHospital());
            vo.setDept(user.getDoctor().getDepartments());
            vo.setTitle(user.getDoctor().getTitle());
        }
        if (user.getSource() != null){
            if (StringUtil.isNotBlank(user.getSource().getRegisterActivityId())){
                Activity activity = activityService.findById(user.getSource().getRegisterActivityId());
                if (activity != null){
                    vo.setActivityName(activity.getName());
                }
            }
            vo.setSubsystem(user.getSource().getSourceType());
            vo.setWay(user.getSource().getInvateWay());

            if (Objects.nonNull(userMap)) {
                AuthSimpleUser simpleUser = userMap.get(user.getSource().getInviterId());
                if (Objects.nonNull(simpleUser)) {
                    vo.setInviter(simpleUser.getName());
                    vo.setInviterId(simpleUser.getId());
                }
            }

            if (Objects.nonNull(tokenMap)) {
                AccessToken token = tokenMap.get(user.getSource().getInviterId());
                if (Objects.nonNull(token)) {
                    vo.setInviterOpenId(token.getOpenId());
                }
            }
        }
        vo.setStatus(user.getStatus());

        return vo;
    }

    private void setMatchQuery(Query<User> query, RegistrationReportForm form) {
        if (StringUtil.isNotBlank(form.getActivityId())) {
            query.filter("source.registerActivityId", form.getActivityId());
        }
        if (!Objects.isNull(form.getSubsystem())) {
            query.filter("source.sourceType", form.getSubsystem());
        }
        if (StringUtil.isNotBlank(form.getUserName())) {
            query.filter("name", createPattern(form.getUserName()));
        }
        if (!Objects.isNull(form.getStartTime())) {
            query.filter("createTime >=", form.getStartTime());
        }
        if (!Objects.isNull(form.getEndTime())) {
            query.filter("createTime <=", form.getEndTime());
        }
        if (StringUtil.isNotBlank(form.getWay())) {
            query.filter("source.invateWay", form.getWay());
        }
        query.filter("source.inviteActivityId", new BasicDBObject("$ne", null));
        query.filter("source.registerActivityId", new BasicDBObject("$ne", null));


    }

    private Pattern createPattern(String keyword) {
        Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
        return pattern;
    }
}
