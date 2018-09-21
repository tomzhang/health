package com.dachen.lbs.service;

import java.util.List;

import com.dachen.lbs.vo.NearbyJob;
import com.dachen.lbs.vo.NearbyUser;

public interface NearbyManager {

 

 

	
	void saveJob(NearbyJob poi);

	void updateJob(NearbyJob poi);

	List<NearbyJob> getJobList(NearbyJob poi);

	
	void saveUser(NearbyUser poi);

	void updateUser(NearbyUser poi);

	List<NearbyUser> getTalentsList(NearbyUser poi);

	List<NearbyUser> getUserList(NearbyUser poi);
	

	void saveIMUser(NearbyUser poi);

	void updateIMUser(NearbyUser poi);

	List<NearbyUser> getIMUserList(NearbyUser poi);

}
