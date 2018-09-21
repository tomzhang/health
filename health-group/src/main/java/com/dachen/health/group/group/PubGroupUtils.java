package com.dachen.health.group.group;

import com.dachen.commons.support.spring.SpringBeansUtils;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.pub.model.param.PubParam;
import com.dachen.pub.service.PubAccountService;
import com.dachen.pub.util.PubUtils;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.StringUtil;

public class PubGroupUtils {
	public static void updatePubInfo(Group group) throws HttpApiException {
		String groupId=group.getId(); 
		String groupName=group.getName();
		String groupIntroduction=group.getIntroduction();
		String photourl=group.getLogoUrl();
		
		boolean isUpdate = false;
		//更新集团名称
		if(!StringUtil.isEmpty(groupName)) {
			isUpdate = true;
		}
		//更新集团描述
		if(!StringUtil.isEmpty(groupIntroduction)) {
			isUpdate = true;
		}
		//更新集团logoUrl
		if(!StringUtil.isEmpty(photourl)) {
			isUpdate = true;
		}
		if(isUpdate){
			PubAccountService pubAccountService = SpringBeansUtils.getBeane(PubAccountService.class);
			PubParam pubParam = new PubParam();
			pubParam.setPid(PubUtils.PUB_PATIENT_VOICE+groupId);
			pubParam.setPhotourl(photourl);
			pubParam.setNote(groupIntroduction);
			if(!StringUtil.isEmpty(groupName)) 
			{
				pubParam.setName("患者之声_"+groupName);//
				pubParam.setNickName(groupName);
			}
			pubAccountService.savePub(pubParam);
			
			pubParam = new PubParam();
			pubParam.setPid(PubUtils.PUB_GROUP_NEWS+groupId);
			pubParam.setPhotourl(photourl);
			pubParam.setNote(groupIntroduction);
			if(!StringUtil.isEmpty(groupName)) 
			{
				pubParam.setName("集团动态_"+groupName);//
				pubParam.setNickName(groupName);
			}
			pubAccountService.savePub(pubParam);
		}
	}
	
}
