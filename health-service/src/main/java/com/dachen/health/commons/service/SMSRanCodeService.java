package com.dachen.health.commons.service;

import com.dachen.commons.support.nosql.INoSqlRepository;
import com.dachen.health.commons.entity.SMSRanCode;

public interface SMSRanCodeService extends INoSqlRepository{
	
	
	SMSRanCode save(String telephone, SMSRanCode code);
	
	SMSRanCode findById(String telephone, String id);

	boolean isCorrectCode(String telephone, String id,String code);
	

}
