package com.dachen.health.activity.invite.service;

import com.dachen.health.activity.invite.entity.CircleInviteReport;
import com.dachen.health.activity.invite.form.InvitationReportForm;
import com.dachen.health.activity.invite.vo.InvitationReportVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/5 19:54 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface CircleInviteReportService extends ServiceBase {
    CircleInviteReport getByUserIdAndActivityIdAndSubsystem(Integer userId, String activityId, Integer subsystem);
    void incWechatCount(Integer userId, String activityId, Integer subsystem);
    void incSmsCount(Integer userId, String activityId, Integer subsystem);
    void incQrcodeCount(Integer userId, String activityId, Integer subsystem);
    void incRegisteredCount(Integer userId, String activityId, Integer subsystem);
    void incAutherizedCount(Integer userId, String activityId, Integer subsystem);

    void updateReport(Integer userId, String activityId, Integer subsystem, String way);

    Pagination<InvitationReportVO> listInvitation(InvitationReportForm form, Integer pageIndex, Integer pageSize);
    long listInvitationCount(InvitationReportForm form);
}
