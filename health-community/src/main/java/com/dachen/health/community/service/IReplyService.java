package com.dachen.health.community.service;


import com.dachen.commons.page.PageVO;
import com.dachen.health.community.entity.param.ReplyParam;

public interface IReplyService {
	/**
	 * 回复主帖
	 * @author liming
	 */
	public void replyTips(ReplyParam reply);
	/**
	 * 回复用户
	 * @author liming
	 */
	public void replyUsers(ReplyParam reply);
	/**
	 * 查询回帖列表
	 * @author liming
	 */
	public PageVO findReplyList(ReplyParam param);
	/**
	 * 删除帖子(假删除)
	 * @author liming
	 */
	public void delete(String id);
	/**
	 * 我的回复列表
	 * @author liming
	 */
	public PageVO findMyReplyList(ReplyParam param);
	/**
	 * 我收到的回复列表
	 * @author liming
	 */
	public PageVO findMyToReplyList(ReplyParam param);
	

}
