package com.dachen.manager;

import com.dachen.commons.JSONMessage;

public interface ISMSManager {

	public abstract boolean isAvailable(String telephone, String randcode,
			String templateId);

	public abstract JSONMessage getSMSCode(String telephone, String templateId);
	
	public abstract JSONMessage getVoiceCode(String telephone, String templateId);

	public abstract boolean sendRandCode(String telephone, String code);
	
	public abstract boolean sendVoiceCode(String telephone, String verifyCode);
	
	public boolean sendVoiceNotify(String telephone, String content);
	
	public abstract boolean sendSMS(String phone, String content);
	
	public String getVoiceResult(String telephone, String verifyCode);

	boolean sendSMS(String phone, String content, String signature);

}