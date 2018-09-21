package com.dachen.health.group.group.dao;

import java.util.List;

import com.dachen.health.group.group.entity.po.PlatformDoctor;

public interface IPlatformDoctorDao {

	PlatformDoctor save(PlatformDoctor pdoc);
	
	PlatformDoctor update(PlatformDoctor pdoc);
	
	PlatformDoctor findAndModify(PlatformDoctor pdoc, Long currentTime);

	PlatformDoctor getById(PlatformDoctor pdoc);
	
	List<PlatformDoctor> getByGroupId(String groupId);
	
	List<PlatformDoctor> getPlatformDoctor(PlatformDoctor pdoc);
	
}
