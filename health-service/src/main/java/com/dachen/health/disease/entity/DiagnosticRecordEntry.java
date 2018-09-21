package com.dachen.health.disease.entity;

import org.mongodb.morphia.annotations.Id;

//@Entity(noClassnameStored=true,value="t_diagnostic_record_entry")
public class DiagnosticRecordEntry {

	@Id
	private String id;
	
	private String content;
	
	
}
