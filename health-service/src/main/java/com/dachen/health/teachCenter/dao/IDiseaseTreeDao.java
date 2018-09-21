package com.dachen.health.teachCenter.dao;

import java.util.List;

import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.teachCenter.entity.po.GroupDisease;

public interface IDiseaseTreeDao {
	
	/*** begin add by liwei 2016年1月18日 ********/
	List<GroupDisease> getMoreGroupDiseaseTree(List<String> groupIds);

	List<GroupDisease> getAllMoreGroupDisease(List<String> groupIds);

	/*** end add by liwei 2016年1月18日 ********/

	List<GroupDisease> getGroupDiseaseTree(String groupId);

	List<GroupDisease> getAllGroupDisease(String groupId);

	boolean updateCount(GroupDisease gd);

	GroupDisease getGroupDisease(GroupDisease gd);

	GroupDisease saveGroupDisease(GroupDisease gd);

	DiseaseTypeVO getDiseaseById(String id);

	boolean updateBatchGroup(GroupDisease gd, List<String> list, String type);
}
