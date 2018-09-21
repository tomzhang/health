package com.dachen.health.user.service;

import java.util.List;

import com.dachen.health.user.entity.param.FastandReplyParam;
import com.dachen.health.user.entity.vo.FastandReplyVO;

public interface IFastandReplyService {
	
	
	List<FastandReplyVO> getAll(FastandReplyParam param);
	
	void update(FastandReplyParam param);
	
	void delete(FastandReplyParam param);
	
	FastandReplyVO add(FastandReplyParam param);
}
