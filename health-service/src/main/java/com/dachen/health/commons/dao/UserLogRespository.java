package com.dachen.health.commons.dao;

import com.dachen.health.commons.vo.UserLog;
import com.dachen.health.user.entity.po.OperationRecord;

public interface UserLogRespository {
	void addUserLog(UserLog userLog);
	
	void addOperationRecord(OperationRecord operationRecord);

	/**
	 * 新增操作记录
	 * @param operationRecord
	 */
	void save(OperationRecord operationRecord);
}
