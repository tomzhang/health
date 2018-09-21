package com.dachen.health.system.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.system.dao.IFeedBackDao;
import com.dachen.health.system.entity.param.FeedBackQuery;
import com.dachen.health.system.entity.po.FeedBack;
import com.dachen.health.system.service.IFeedBackManager;
 





@Service(FeedBackManagerImpl.BEAN_ID)
public class FeedBackManagerImpl extends NoSqlRepository implements IFeedBackManager {
	public static final String BEAN_ID = "FeedBackManagerImpl";
	Logger logger  = LoggerFactory.getLogger(FeedBackManagerImpl.class); 

	@Autowired
	private IFeedBackDao feedBackDao;

	
	/**
	 * 保存意见反馈
	 */
	@Override
	public void saveFeedBack(FeedBack feedBack) {
		 if ( StringUtils.isEmpty(feedBack.getContent())) {
	            throw new ServiceException(1000101,"反馈意见不能为空！");
	        }
		 Date now=new Date();
		 feedBack.setCreateTime(now.getTime());
		 feedBackDao.save(feedBack);
	}


	@Override
	public PageVO queryFeedBack(FeedBackQuery feedBackQuery) {
		return feedBackDao.queryFeedBack(feedBackQuery);
	}


	@Override
	public FeedBack getFeedBackById(String id) {
		return feedBackDao.getFeedBackById(id);
	}



	
	
}
