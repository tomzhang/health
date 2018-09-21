package com.dachen.health.group.company.dao;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.company.entity.param.InviteCodeParam;
import com.dachen.health.group.company.entity.po.InviteCode;

/**
 * 
 * @author pijingwei
 * @date 2015/8/20
 */
@Deprecated //暂时废弃，没有这块业务
public interface InviteCodeDao {

	/**
     * </p>保存邀请码</p>
     * @param code
     * @return
     * @author pijingwei
     * @date 2015年8月20日
     */
	InviteCode save(InviteCode code);
	
	/**
     * </p>根据邀请码获取详细信息</p>
     * @param code
     * @return
     * @author pijingwei
     * @date 2015年8月20日
     */
	InviteCode findByCode(String code);
	
	/**
     * </p>使用邀请码</p>
     * @param code
     * @return
     * @author pijingwei
     * @date 2015年8月20日
     */
	InviteCode update(InviteCode code);
	
	/**
     * </p>获取所有邀请码列表</p>
     * @param param
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月4日
     */
	PageVO search(InviteCodeParam param);
	
	/**
     * </p>根据条件删除邀请码</p>
     * @param param
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月22日
     */
	void delete(InviteCode code);
	
}
