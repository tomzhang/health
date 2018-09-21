package com.dachen.health.commons.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 
 * @author vincent
 *
 */
@Entity(noClassnameStored=true,value="b_sms_template")

public class SmsTemplate {

	@Id
	private String id;
	
	
	//短信内容
	private String content;
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	
}
