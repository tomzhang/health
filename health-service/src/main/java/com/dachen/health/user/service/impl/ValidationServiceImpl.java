package com.dachen.health.user.service.impl;
import com.dachen.health.commons.vo.OperationPassword;
import com.dachen.health.user.dao.IDoctorDao;
import com.dachen.health.user.dao.IValidationDao;
import com.dachen.health.user.service.IValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImpl implements IValidationService {
    @Autowired
    private IValidationDao validationDao;

    public OperationPassword getValidation(String type){
        return validationDao.getValidation(type);
    }
}
