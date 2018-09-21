package com.dachen.health.activity.invite.service;

import com.dachen.health.activity.invite.form.UserRegisterForm;
import java.util.Map;

public interface DoctorRegisterService extends IntegerServiceBase {
    Map<String, Object> register(UserRegisterForm form);
}
