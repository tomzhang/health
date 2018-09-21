package com.dachen.health.commons.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.GuestUserManager;
import com.dachen.health.commons.vo.GuestUser;
@Service(GuestUserManagerImpl.BEAN_ID)
public class GuestUserManagerImpl implements GuestUserManager {
	public static final String BEAN_ID = "GuestUserManagerImpl";

	Logger logger = LoggerFactory.getLogger(GuestUserManagerImpl.class);
	@Autowired
	private UserRepository userRepository;
	@Override
	public Map<String, Object> getGuestToken(String deviceID,String guest_token) {
		//先判断游客是否记录，存在
		GuestUser guest=userRepository.getGuestByDeviceID(deviceID);
		if(guest==null||guest.getId()==null){
			userRepository.saveGuestUser(deviceID);
		}else{
			//更新最后活动时间
			guest.setActiveTime(System.currentTimeMillis());
			userRepository.updateGuestUser(guest);
			
		}
		
		return userRepository.saveGuest(deviceID,guest_token);
	}
}
