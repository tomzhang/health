package com.dachen.health.user.dao;

import com.dachen.health.commons.vo.OperationPassword;

public interface IValidationDao {
    public OperationPassword getValidation(String type);

}
