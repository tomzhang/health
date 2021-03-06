package com.dachen.health.system.dao;

import com.dachen.commons.page.PageVO;
import com.dachen.health.system.entity.param.FeedBackQuery;
import com.dachen.health.system.entity.po.FeedBack;

public interface IFeedBackDao {
	void save(FeedBack feedBack);
	PageVO queryFeedBack(FeedBackQuery feedBackQuery);
	FeedBack getFeedBackById(String id);
}
