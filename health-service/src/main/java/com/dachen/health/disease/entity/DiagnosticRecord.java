package com.dachen.health.disease.entity;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(noClassnameStored=true,value="t_diagnostic_record")
public class DiagnosticRecord {
	@Id
	private String id;
	
	//记录内容
	private String content;
	
	//关联订单
	private String orderId;

	//图片集合
	private List<DiagnosticRecordEntry> entries;
	
}
