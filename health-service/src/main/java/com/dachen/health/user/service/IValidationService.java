package com.dachen.health.user.service;

import com.dachen.health.commons.vo.OperationPassword;

public interface IValidationService {

    OperationPassword getValidation(String type);
}
