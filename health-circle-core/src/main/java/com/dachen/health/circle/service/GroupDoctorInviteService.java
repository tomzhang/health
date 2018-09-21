package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupDoctor2;
import com.dachen.health.circle.entity.GroupDoctorInvite;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.exception.HttpApiException;

public interface GroupDoctorInviteService extends BaseGroupDoctorApplyOrInviteService {

    GroupDoctorInvite create(Integer currentUserId, String groupId, Integer doctorId);

    boolean accept(Integer currentUserId, String id) throws HttpApiException;

    int closeByGroupDoctor(GroupDoctor2 groupDoctor);

    boolean refuse(Integer currentUserId, String id) throws HttpApiException;

    MobileGroupDoctorVO findDetailByIdAndGroupDoctorVO(Integer currentUserId, String id);

    boolean findInviteingByGroupAndDoctor(String groupId, Integer doctorId);

    void wrapGroupDoctorInviteStatus(GroupDoctorInvite dbItem);
}
