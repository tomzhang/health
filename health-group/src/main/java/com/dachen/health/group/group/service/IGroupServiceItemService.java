package com.dachen.health.group.group.service;

import java.util.List;

import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.group.group.entity.po.GroupServiceItem;
import com.dachen.health.group.group.entity.vo.GroupServiceItemVO;

public interface IGroupServiceItemService {

	public List<HospitalVO> getHospitals(String groupId);
	
	/**
	 * 获取服务项集合（包含下级节点）
	 * @param hospitalId
	 * @return
	 */
	public List<GroupServiceItemVO> getGroupServiceItem(String groupId, String hospitalId);
	
	/**
	 * 获取服务项
	 * @param groupId
	 * @param hospitalId
	 * @param serviceItemId
	 * @return
	 */
	public GroupServiceItem getGroupServiceItem(String groupId, String hospitalId, String serviceItemId);
}
