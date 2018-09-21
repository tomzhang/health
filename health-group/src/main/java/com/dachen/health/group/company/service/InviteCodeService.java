package com.dachen.health.group.company.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.company.entity.param.InviteCodeParam;
import com.dachen.health.group.company.entity.po.InviteCode;

/**
 * 
 * @author pijingwei
 * @date 2015/8/20
 */
@Deprecated //暂时废弃，没有这块业务
public interface InviteCodeService {

	/**
     * </p>保存邀请码</p>
     * @param code
     * @return
     * @author pijingwei
     * @date 2015年8月20日
     */
	InviteCode saveInviteCode(InviteCode code);
	
	/**
     * </p>使用邀请码</p>
     * @param code
     * @return
     * @author pijingwei
     * @date 2015年8月20日
     */
	InviteCode updateInviteCode(String code);
	
	/**
     * </p>获取所有邀请码列表</p>
     * @param param
     * @return PageVO
     * @author pijingwei
     * @date 2015年8月4日
     */
	PageVO searchInviteCode(InviteCodeParam param);
	
}
