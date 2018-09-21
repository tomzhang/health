package com.dachen.health.group.group.service;

import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.PlatformDoctor;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.sdk.exception.HttpApiException;

public interface IPlatformDoctorService {

	public boolean doctorOnline(GroupDoctor gdoc) throws HttpApiException;
	
	public void doctorOffline(GroupDoctor gdoc, EventEnum event) throws HttpApiException;
	
	public void doctorOffline(PlatformDoctor pdoc, EventEnum event) throws HttpApiException;
	
	public PlatformDoctor getOne(GroupDoctor gdoc);
	
	public PlatformDoctor getOne(Integer doctorId);
	
	public PlatformDoctor getOne(PlatformDoctor pdoc);
	
}
