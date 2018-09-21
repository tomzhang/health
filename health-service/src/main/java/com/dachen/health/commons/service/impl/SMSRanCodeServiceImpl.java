package com.dachen.health.commons.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dachen.commons.KeyBuilder;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.entity.SMSRanCode;
import com.dachen.health.commons.service.SMSRanCodeService;
import com.dachen.manager.SMSManager;
import com.dachen.util.StringUtil;

@Service
public class SMSRanCodeServiceImpl extends NoSqlRepository implements SMSRanCodeService{

	
	@Override
	public SMSRanCode save(String telephone, SMSRanCode code) {
		String id=UUID.randomUUID().toString();
		code.setId(id);
		jedisTemplate.set(getRandCodeKey(telephone, id), code.getCode());
		jedisTemplate.expire(getRandCodeKey(telephone, id), SMSManager.forgetPasswordCodeActiveTime);
		//dsForRW.save(code);
		return code;
	}
	

	@Override
	public SMSRanCode findById(String telephone, String id) {
		String ocode=jedisTemplate.get(getRandCodeKey(telephone, id));
		if(StringUtil.isNotBlank(ocode)){
			SMSRanCode smsRanCode=new SMSRanCode();
			smsRanCode.setId(id);
			smsRanCode.setCode(ocode);
			return smsRanCode;
		}
		return null;
		
	}

	@Override
	public boolean isCorrectCode(String telephone, String id, String code) {
		if(!StringUtil.isEmpty(code)&&!StringUtil.isEmpty(id)){
			String ocode=jedisTemplate.get(getRandCodeKey(telephone, id));
			if(code.equals(ocode)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 拼装手机号和UUID为key，防止获取验证码之后修改手机号骗过verifyCode 
	 * @param telephone
	 * @param id
	 * @return
	 */
	private String getRandCodeKey(String telephone, String id) {
		String key = id;
		if (StringUtil.isNotBlank(telephone)) {
			key = telephone + "_" + id;
		}
		return KeyBuilder.getSMSRandCodeKey(key);
	}

}
