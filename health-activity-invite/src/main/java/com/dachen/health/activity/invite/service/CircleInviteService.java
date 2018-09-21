package com.dachen.health.activity.invite.service;


import com.dachen.health.activity.invite.form.CircleInviteForm;
import com.dachen.health.activity.invite.vo.MobileInviteVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.exception.HttpApiException;

public interface CircleInviteService extends ServiceBase {

    MobileInviteVO createInviteShortUrl(Integer doctorId, String inviteActivityId, String registerActivityId, String way, Integer subsystem) throws HttpApiException;
    MobileInviteVO createInviteShortUrl(Integer doctorId, String groupId, String way, Integer subsystem) throws HttpApiException;

    MobileInviteVO createActivityDoctorShortUrl(Integer doctorId, String inviteActivityId,
                                                String registerActivityId, String way, Integer subsystem) throws HttpApiException;

    void save(CircleInviteForm form);
    void save(Integer userId, String activityId, String way, Integer subsystem);
}
