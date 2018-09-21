package com.dachen.health.community.dao;

import com.dachen.health.community.entity.po.CommunityUsers;

public interface ICommunityUsersDao extends BaseDao<CommunityUsers>{
	public CommunityUsers getBygroupId(Integer userId,String groupId);

}
