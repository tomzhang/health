package com.dachen.health.user.dao;

import java.util.List;

import com.dachen.health.user.entity.param.FastandReplyParam;
import com.dachen.health.user.entity.po.FastandReply;
import com.dachen.health.user.entity.vo.FastandReplyVO;


/**
 * ProjectName： health-service<br>
 * ClassName： IFastandReplyDao<br>
 * Description： 快捷回复dao<br>
 * 
 * @author xiepei
 * @crateTime 2015年9月23日
 * @version 1.0.0
 */
public interface IFastandReplyDao {
		
	
	
	/**
     * </p>获取用户一个快捷回复记录</p>
     * 
     * @param param
     * @return 
     * @author 谢佩
     * @date 2015年9月23日
     */
	public FastandReplyVO getOne(FastandReplyParam param);
	
	
	/**
     * </p>获取用户所有快捷回复记录</p>
     * 
     * @param param
     * @return 
     * @author 谢佩
     * @date 2015年9月23日
     */
	 List<FastandReplyVO> getAll(FastandReplyParam param);
	
	/**
     * </p>删除一条用户快捷回复记录</p>
     * 
     * @param param
     * @return 
     * @author 谢佩
     * @date 2015年9月23日
     */
	 void delete(FastandReplyParam param);
	
	/**
     * </p>修改快捷回复记录</p>
     * 
     * @param param
     * @return 
     * @author 谢佩
     * @date 2015年9月23日
     */
	 void update(FastandReplyParam param);
	 
	 /**
	     * </p>添加快捷回复记录</p>
	     * 
	     * @param param
	     * @return 
	     * @author 谢佩
	     * @date 2015年9月23日
	     */
	 FastandReply add(FastandReply param);
	
	
}
