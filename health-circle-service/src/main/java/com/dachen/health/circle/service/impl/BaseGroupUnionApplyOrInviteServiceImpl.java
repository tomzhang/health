package com.dachen.health.circle.service.impl;

import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.circle.entity.BaseGroupUnionApplyOrInvite;
import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupUnion;
import com.dachen.health.circle.entity.GroupUnionInvite;
import com.dachen.health.circle.service.*;
import com.dachen.health.commons.vo.User;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.sdk.util.SdkUtils;
import com.mobsms.sdk.MobSmsSdk;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseGroupUnionApplyOrInviteServiceImpl extends BaseServiceImpl implements BaseGroupUnionApplyOrInviteService {

    @Autowired
    protected GroupUnionService groupUnionService;

    @Autowired
    protected GroupUnionMemberService groupUnionMemberService;

    @Autowired
    protected IBusinessServiceMsg businessServiceMsg;

    @Autowired
    protected Group2Service group2Service;

    @Autowired
    protected IBaseDataService baseDataService;

    @Autowired
    protected MobSmsSdk mobSmsSdk;

    @Autowired
    protected User2Service user2Service;

    @Autowired
    protected GroupUser2Service groupUser2Service;

    @Autowired
    protected ImService imService;

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    protected <T extends BaseGroupUnionApplyOrInvite> void wrapAll(T invite) {
        if (null == invite) {
            return;
        }
        if (null == invite.getGroup()) {
            Group2 group = group2Service.findAndCheckGroupOrDept(invite.getGroupId());
            invite.setGroup(group);
        }

        if (null == invite.getUnion()) {
            GroupUnion groupUnion = groupUnionService.findAndCheckById(invite.getUnionId());
            invite.setUnion(groupUnion);
        }

        if (null == invite.getUser()) {
            User user = user2Service.findById(invite.getUserId());
            invite.setUser(user);
        }
    }

    protected <T extends BaseGroupUnionApplyOrInvite> void wrapAll(List<T> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        this.wrapGroup(list);
        this.wrapGroupUnion(list);
        this.wrapUser(list);
    }

    protected <T extends BaseGroupUnionApplyOrInvite> void wrapUser(List<T> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<Integer> userIdSet = new HashSet<>(list.size());
        for (T apply:list) {
            userIdSet.add(apply.getUserId());
        }
        List<User> groupUnionList = user2Service.findByIds(new ArrayList<>(userIdSet));
        for (T apply:list) {
            for (User user:groupUnionList) {
                if (user.getUserId().equals(apply.getUserId())) {
                    apply.setUser(user);
                    break;
                }
            }
        }
    }

    protected <T extends BaseGroupUnionApplyOrInvite> void wrapGroupUnion(List<T> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<String> unionIdSet = new HashSet<>(list.size());
        for (T apply:list) {
            unionIdSet.add(apply.getUnionId());
        }
        List<GroupUnion> groupUnionList = groupUnionService.findByIds(new ArrayList<>(unionIdSet));
        for (T apply:list) {
            for (GroupUnion groupUnion:groupUnionList) {
                if (groupUnion.getId().toString().equals(apply.getUnionId())) {
                    apply.setUnion(groupUnion);
                    break;
                }
            }
        }
    }

    protected <T extends BaseGroupUnionApplyOrInvite> void wrapGroup(List<T> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<String> groupIdSet = new HashSet<>(list.size());
        for (T apply:list) {
            groupIdSet.add(apply.getGroupId());
        }
        List<Group2> group2List = group2Service.findFullByIds(new ArrayList<>(groupIdSet));
        for (T apply:list) {
            for (Group2 group2:group2List) {
                if (group2.getId().toString().equals(apply.getGroupId())) {
                    apply.setGroup(group2);
                    break;
                }
            }
        }
    }


}
