package com.dachen.manager;

import java.util.List;
import java.util.Map;

import com.dachen.commons.JSONMessage;

public interface IConferenceManager {
	
	public abstract Map<String,Object> createConference(Integer maxMember,Integer duration,Integer playTone);
	
	public abstract Map<String,Object> removeConference(String confId, String callId);
	
	public abstract Map<String,Object> dismissConference(String confId); 
	
	public abstract Map<String,Object> inviteConference(String confId, List<String> list);
	
	public abstract JSONMessage muteConference(String confId,String callId);
	
	public abstract JSONMessage unMuteConference(String confId,String callId);
	
	public abstract Map<String,Object> deafConference(String confId, String callId);
	
	public abstract Map<String,Object> unDeafConference(String confId, String callId);
	
	public abstract Map<String,Object> recordConference(String confId);
	
	public abstract Map<String,Object> stopRecordConference(String confId);
	
	public abstract Map<String,Object> queryConference(String confId);
	
	
	
	
}
