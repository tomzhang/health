package com.dachen.health.base.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.base.dao.InitAssistantDAO;
import com.dachen.health.base.entity.po.DrugPersonInfo;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.vo.User;
import com.dachen.health.user.entity.po.Assistant;
import com.dachen.util.Md5Util;

@Service
public class InitAssistantService {
	@Autowired
	private InitAssistantDAO assistantDAO;
	
	
	public void impAllAssistant()
	{
		List<DrugPersonInfo> list = assistantDAO.getAllAssistant();
		int useId = 10001;
		for(DrugPersonInfo perInfo:list)
		{
			register(perInfo,useId);
			assistantDAO.updateUserIdForDrupHospital(useId,perInfo.getId());
			useId++;
		}
	}
	
	
	private void register(DrugPersonInfo personInfo,int userId)
	{
		User user = convertToUser(personInfo);
		user.setUserId(userId);
		assistantDAO.addAssistant(user);
		
	
	}
	
	private User convertToUser(DrugPersonInfo personInfo)
	{
		User user = new User();
		user.setName(personInfo.getName());
		user.setTelephone(personInfo.getTelephone());
		user.setPassword(Md5Util.md5Hex("123456"));
		
		user.setCreateTime(System.currentTimeMillis());
		user.setSex(personInfo.getGender());
		user.setStatus(UserStatus.normal.getIndex());
		user.setUserType(UserType.assistant.getIndex());
		
		Assistant assistant = new Assistant();
		assistant.setArea(personInfo.getCityArea());
		assistant.setCompany(personInfo.getCompany());
		assistant.setOnduty(personInfo.getStatus());
		assistant.setPosition(personInfo.getJob());
		user.setAssistant(assistant);
		
		return user;
	}
	
}
