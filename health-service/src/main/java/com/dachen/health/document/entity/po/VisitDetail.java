package com.dachen.health.document.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;


@Entity("t_document_visit")
public class VisitDetail {
	@Id
	private ObjectId id;
	private String documentId;//文档ID
	private Integer documentType;//文档类型
	private String visitor;//浏览者ID
	private long visitTime;//浏览时间
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public Integer getDocumentType() {
		return documentType;
	}
	public void setDocumentType(Integer documentType) {
		this.documentType = documentType;
	}
	public String getVisitor() {
		return visitor;
	}
	public void setVisitor(String visitor) {
		this.visitor = visitor;
	}
	public long getVisitTime() {
		return visitTime;
	}
	public void setVisitTime(long visitTime) {
		this.visitTime = visitTime;
	}
	
	

}
