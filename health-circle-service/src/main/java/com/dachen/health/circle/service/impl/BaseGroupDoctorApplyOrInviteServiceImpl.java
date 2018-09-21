package com.dachen.health.circle.service.impl;

import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.entity.BaseGroupDoctorApplyOrInvite;
import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupDoctorApply;
import com.dachen.health.circle.entity.GroupDoctorInvite;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.sdk.util.SdkUtils;
import com.mobsms.sdk.MobSmsSdk;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseGroupDoctorApplyOrInviteServiceImpl extends BaseServiceImpl implements BaseGroupDoctorApplyOrInviteService {

    @Autowired
    protected User2Service user2Service;

    @Autowired
    protected Group2Service group2Service;

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    @Autowired
    protected GroupUser2Service groupUser2Service;

    @Autowired
    protected IBaseDataService baseDataService;

    @Autowired
    protected ImService imService;

    @Autowired
    protected MobSmsSdk mobSmsSdk;

    @Autowired
    protected IBusinessServiceMsg businessServiceMsg;

    /*protected  <T extends BaseGroupDoctorApplyOrInvite> MobileGroupDoctorVO convertToGroupDoctorVO(T dbItem) {
        if (null == dbItem) {
            return null;
        }
        this.wrapGroupDoctorApplyStatus(dbItem);
        MobileGroupDoctorVO vo = new MobileGroupDoctorVO(dbItem);
        return vo;
    }*/


    protected <T extends BaseGroupDoctorApplyOrInvite> void wrapAll(T item) {
        if (null == item) {
            return;
        }
        this.wrapUser(item);
        this.wrapGroup(item);
    }

    protected  <T extends BaseGroupDoctorApplyOrInvite> void wrapAll(List<T> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        this.wrapGroup(list);
        this.wrapUser(list);
    }

    protected  <T extends BaseGroupDoctorApplyOrInvite> void wrapGroup(T item) {
        if (null == item) {
            return;
        }
        if (null != item.getGroup()) {
            return;
        }

        Group2 group2 = group2Service.findById(item.getGroupId());
        item.setGroup(group2);
    }

    protected  <T extends BaseGroupDoctorApplyOrInvite> void wrapGroup(List<T> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<String> groupIdSet = new HashSet<String>(list.size());
        for (T groupDoctor2 : list) {
            groupIdSet.add(groupDoctor2.getGroupId());
        }

        List<Group2> group2List = group2Service.findFullByIds(new ArrayList<>(groupIdSet));
        for (T groupDoctor2 : list) {
            for (Group2 group2 : group2List) {
                if (groupDoctor2.getGroupId().equals(group2.getId().toString())) {
                    groupDoctor2.setGroup(group2);
                    break;
                }
            }
        }
    }

    protected  <T extends BaseGroupDoctorApplyOrInvite> void wrapUser(T item) {
        if (null == item) {
            return;
        }
        if (null != item.getUser()) {
            return;
        }
        User user = user2Service.findById(item.getUserId());
        item.setUser(user);
    }

    protected  <T extends BaseGroupDoctorApplyOrInvite> void wrapUser(List<T> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<Integer> doctorIdSet = new HashSet<>(list.size());
        for (T gd : list) {
            doctorIdSet.add(gd.getUserId());
        }

        List<User> userList = user2Service.findByIds(new ArrayList<>(doctorIdSet));
        for (T item : list) {
            for (User user : userList) {
                if (item.getUserId().equals(user.getUserId())) {
                    item.setUser(user);
                    break;
                }
            }
        }
    }

}
