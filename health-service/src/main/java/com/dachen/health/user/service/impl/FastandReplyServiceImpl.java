package com.dachen.health.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.user.dao.IFastandReplyDao;
import com.dachen.health.user.entity.param.FastandReplyParam;
import com.dachen.health.user.entity.po.FastandReply;
import com.dachen.health.user.entity.vo.FastandReplyVO;
import com.dachen.health.user.service.IFastandReplyService;

@Service
public class FastandReplyServiceImpl implements IFastandReplyService{
	
	@Autowired
	IFastandReplyDao fastandReplyDao;
	
	//查询所有
	public List<FastandReplyVO> getAll(FastandReplyParam param) {
		return fastandReplyDao.getAll(param);
	}
	
	//修改单条
	public void update(FastandReplyParam param) {
		if(param.getReplyContent()==null) {
			 throw new ServiceException("修改参数不能为空 ！");
		}
		fastandReplyDao.update(param);
	}
	
	//删除单挑快捷回复记录
	public void delete(FastandReplyParam param) {
		if(param.getReplyId()==null) {
			 throw new ServiceException("删除ID不能为空!");
		}
		fastandReplyDao.delete(param);
	}
	
	//添加快捷回复记录
	public FastandReplyVO add(FastandReplyParam param) {
		if(param.getReplyContent()==null) {
			 throw new ServiceException("修改参数不能为空 ！");
		}
		FastandReply fastandReply = new FastandReply();
		fastandReply.setReplyContent(param.getReplyContent());
		fastandReply.setReplyTime(System.currentTimeMillis());
		fastandReply.setReplyType(param.getUserType());
		fastandReply.setUserId(param.getUserId());
		fastandReply.setIs_system(param.getIs_system());
		fastandReply = fastandReplyDao.add(fastandReply);
		FastandReplyVO fastandReplyVo = new FastandReplyVO();
		fastandReplyVo.setReplyId(fastandReply.getId().toString());
		fastandReplyVo.setReplyContent(fastandReply.getReplyContent());
		return fastandReplyVo;
	}

}
