package com.dachen.health.commons.dao.mongo;

import org.springframework.stereotype.Repository;

import com.dachen.health.commons.dao.SmsTemplateRepository;
import com.dachen.health.commons.entity.SmsTemplate;

@Repository
public class SmsTemplateRepositoryImpl extends
		BaseRepositoryImpl<SmsTemplate, String> implements SmsTemplateRepository {

}
