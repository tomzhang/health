package com.dachen.health.activity.invite.service;

import com.dachen.health.activity.invite.form.RegistrationReportForm;
import com.dachen.health.activity.invite.vo.RegistrationReportVO;
import com.dachen.sdk.page.Pagination;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/6 17:57 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface UserRegisterService extends IntegerServiceBase {
    Pagination<RegistrationReportVO> userRegistration(RegistrationReportForm form, Integer pageIndex, Integer pageSize);
    long userRegistrationCount(RegistrationReportForm form);
}
