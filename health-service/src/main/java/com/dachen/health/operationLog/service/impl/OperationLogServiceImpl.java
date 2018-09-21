package com.dachen.health.operationLog.service.impl;

import com.dachen.commons.page.PageVO;
import com.dachen.health.operationLog.dao.IOperationLogDao;
import com.dachen.health.operationLog.entity.param.OperationLogParam;
import com.dachen.health.operationLog.entity.po.OperationLog;
import com.dachen.health.operationLog.service.IOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 钟良
 * @desc
 * @date:2017/9/29 10:49 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Service
public class OperationLogServiceImpl implements IOperationLogService {
    @Autowired
    private IOperationLogDao operationLogDao;


    @Override
    public void save(OperationLog operationLog) {
        operationLogDao.save(operationLog);
    }

    @Override
    public PageVO list(OperationLogParam param) {
        return operationLogDao.list(param);
    }

    @Override
    public Long listCount(OperationLogParam param) {
        return operationLogDao.listCount(param);
    }
}
