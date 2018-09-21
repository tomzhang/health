package com.dachen.health.commons.service;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.entity.SmsLog;

public interface SmsLogService {
	
	void save(SmsLog intance);
	
	void log(String userid,String toPhone, String content);
	
	void log(String userid,String toPhone, String content,String result);
	
	List<SmsLog> find(SmsLog entity);
	
	PageVO findSmsLog(SmsLog entity, int pageIndex, int pageSize);

}
