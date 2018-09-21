package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupDoctor2;
import com.dachen.health.circle.entity.GroupDoctorApply;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.exception.HttpApiException;

public interface GroupDoctorApplyService extends BaseGroupDoctorApplyOrInviteService {

    GroupDoctorApply create(Integer currentUserId, String groupId, String msg) throws HttpApiException;

    boolean accept(Integer currentUserId, String id) throws HttpApiException;

    int closeByGroupDoctor(GroupDoctor2 groupDoctor);

    boolean refuse(Integer currentUserId, String id) throws HttpApiException;

    boolean findApplyingByGroupAndDoctor(String groupId, Integer doctorId);

    MobileGroupDoctorVO findDetailByIdAndGroupDoctorVO(Integer currentUserId, String id);

    void wrapGroupDoctorApplyStatus(GroupDoctorApply dbItem);

    GroupDoctor2 findByUKAndGroupDoctor(Integer doctorId, String groupId);
}
