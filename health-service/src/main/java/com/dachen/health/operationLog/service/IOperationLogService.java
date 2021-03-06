package com.dachen.health.operationLog.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.operationLog.entity.param.OperationLogParam;
import com.dachen.health.operationLog.entity.po.OperationLog;

/**
 * @author 钟良
 * @desc
 * @date:2017/9/29 10:48 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface IOperationLogService {
    void save(OperationLog operationLog);
    PageVO list(OperationLogParam param);
    Long listCount(OperationLogParam param);
}
