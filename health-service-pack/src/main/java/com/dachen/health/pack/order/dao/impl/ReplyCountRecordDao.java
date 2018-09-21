package com.dachen.health.pack.order.dao.impl;

import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.pack.order.dao.IReplyCountRecordDao;
import com.dachen.health.pack.order.entity.po.OrderFreeReplyCountRecord;

@Repository
public class ReplyCountRecordDao extends NoSqlRepository implements IReplyCountRecordDao  {

	@Override
	public void insert(OrderFreeReplyCountRecord record) {
		dsForRW.insert(record);
	}

	
}
