package com.dachen.health.activity.invite.service;

import com.dachen.health.activity.invite.form.InvitationReportForm;
import com.dachen.health.activity.invite.vo.InvitationReportVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/2 11:18 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface CircleInviteDataService extends ServiceBase {
    Pagination<InvitationReportVO> listInvitations(InvitationReportForm form, Integer pageIndex, Integer pageSize);
}
