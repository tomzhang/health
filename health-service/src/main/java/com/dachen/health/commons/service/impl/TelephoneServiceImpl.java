package com.dachen.health.commons.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dachen.health.commons.dao.TelephoneAccountRepository;
import com.dachen.health.commons.entity.TelephoneAccount;
import com.dachen.health.commons.service.TelephoneService;
import com.ucpaas.restsdk.UcPaasRestSdk;

/**
 * 
 * @author vincent
 *
 */
@Service
public class TelephoneServiceImpl implements TelephoneService {

	@Resource
	protected TelephoneAccountRepository telephoneAccountRepository;
	
	@Override
	public TelephoneAccount findByTelePhone(String telephone) {
		TelephoneAccount account=telephoneAccountRepository.findByTelePhone(telephone);
		if(account==null){
			JSONObject client = null;
			JSONObject ret2=UcPaasRestSdk.findClientByMobile(telephone);
			if ("000000".equals(((JSONObject)ret2.get("resp")).get("respCode"))) {
				client = (JSONObject)((JSONObject)ret2.get("resp")).get("client");
			}
			else
			{
				JSONObject ret = UcPaasRestSdk.createClient(null,telephone);
				if ("000000".equals(((JSONObject)ret.get("resp")).get("respCode"))) {
					client = (JSONObject)((JSONObject)ret.get("resp")).get("client");
				} 
			}
			
			if(client!=null){
				account=new TelephoneAccount();
				account.setClientNumber(client.getString("clientNumber"));
				account.setClientPwd(client.getString("clientPwd"));
				account.setTelephone(telephone);
				telephoneAccountRepository.save(account);
				
			}
		}
		return account;
	}


}
