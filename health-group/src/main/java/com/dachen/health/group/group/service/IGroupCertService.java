package com.dachen.health.group.group.service;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.group.entity.param.GroupCertParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupCertification;
import com.dachen.health.group.group.entity.vo.GroupVO;

public interface IGroupCertService {

	/**
	 * 集团提交公司认证资料
	 * @param groupId
	 * @param certInfo
	 */
	boolean submitCert(String groupId, Integer doctorId, GroupCertification certInfo);

	/**
	 * 获取集团信息，包含公司认证资料
	 * @param groupId
	 * @return
	 */
	GroupVO getGroupCert(String groupId);
	
	
	/**
	 * 获取集团信息集合
	 * @param param
	 * @return
	 */
	PageVO getGroupCerts(GroupCertParam param);
	
	/**
	 * 
	 * @param param
	 * @return
	 */
	PageVO getOtherGroupCerts(GroupCertParam param);
	
	/**
	 * 修改认证备注
	 * @param groupId
	 * @param remarks
	 * @return
	 */
	boolean updateRemarks(String groupId, String remarks);
	
	/**
	 * 认证通过
	 * @param groupId
	 * @param doctorId
	 * @return
	 */
	boolean passCert(String groupId);
	
	/**
	 * 未通过
	 * @param groupId
	 * @return
	 */
	boolean noPass(String groupId);
	
	List<Group> getGroups(String companyId);
 	
}
