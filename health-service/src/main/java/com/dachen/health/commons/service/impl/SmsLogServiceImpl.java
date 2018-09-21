package com.dachen.health.commons.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.entity.SmsLog;
import com.dachen.health.commons.service.SmsLogService;
import com.dachen.util.StringUtil;

/**
 * 
 * @author vincent
 *
 */
@Service
public class SmsLogServiceImpl implements SmsLogService {

	@Resource(name = "dsForRW")
	protected AdvancedDatastore dsForRW;

	@Override
	public void save(SmsLog intance) {
		dsForRW.save(intance);

	}

	@Override
	public void log(String userid, String toPhone, String content) {
		log(userid, toPhone, content, null);
	}

	@Override
	public void log(String userid, String toPhone, String content, String result) {
		SmsLog intance = new SmsLog();
		intance.setContent(content);
		intance.setCreateTime(new Date().getTime());
		intance.setToPhone(toPhone);
		intance.setUserid(userid);
		intance.setResult(result);
		save(intance);
	}

	@Override
	public List<SmsLog> find(SmsLog entity) {
		Query<SmsLog> query=dsForRW.createQuery(SmsLog.class);
		if(entity!=null){
			if(!StringUtil.isEmpty(entity.getContent())){
				//query.filter("content", entity.getContent());
				query.field("content").contains(entity.getContent());
			}
			if(!StringUtil.isEmpty(entity.getToPhone())){
				query.filter("toPhone", entity.getToPhone());
			}
		}
		query.order("-createTime");
		query.limit(100);
		return query.asList();
	}

	@Override
	public PageVO findSmsLog(SmsLog entity, int pageIndex, int pageSize) {
		
		Query<SmsLog> query=dsForRW.createQuery(SmsLog.class);
		if(entity!=null){
			if(!StringUtil.isEmpty(entity.getContent())){
				query.field("content").contains(entity.getContent());
			}
			if(!StringUtil.isEmpty(entity.getToPhone())){
				query.filter("toPhone", entity.getToPhone());
			}
		}
		query.order("-createTime");
		int skip = pageIndex * pageSize;
		skip = skip < 0 ? 0 : skip;
		query.offset(skip);
		pageSize = pageSize < 0 ? 15 : pageSize;
		query.limit(pageSize);
		List<SmsLog> list = query.asList();
		
		Query<SmsLog> queryCount=dsForRW.createQuery(SmsLog.class);
		if(entity!=null){
			if(!StringUtil.isEmpty(entity.getContent())){
				queryCount.field("content").contains(entity.getContent());
			}
			if(!StringUtil.isEmpty(entity.getToPhone())){
				queryCount.filter("toPhone", entity.getToPhone());
			}
		}
		long count = queryCount.countAll();
		
		PageVO pageVO = new PageVO();
		pageVO.setPageData(list);
		pageVO.setPageIndex(pageIndex);
		pageVO.setPageSize(pageSize);
		pageVO.setTotal(count);
		
		return pageVO;
	}

	
}
