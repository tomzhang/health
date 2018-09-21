package com.dachen.health.activity.invite.service;

import com.dachen.health.activity.invite.form.QrCodeForm;
import com.dachen.health.base.entity.po.QrScanParamPo;

public interface QrCode2Service {
    String generateDoctorQrCode(QrCodeForm form);

    String generateActivityDoctorQrCode(QrCodeForm form);

    String generateDeptQrCode(QrCodeForm form);

}