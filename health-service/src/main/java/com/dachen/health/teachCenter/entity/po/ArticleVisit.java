package com.dachen.health.teachCenter.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "i_article_visit")
public class ArticleVisit {

	@Id
	private ObjectId id;
	private String articleId;
	private String visitorId;
	private long visitTime;
	
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getVisitorId() {
		return visitorId;
	}
	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public String getVisitId() {
		return visitorId;
	}
	public void setVisitId(String visitorId) {
		this.visitorId = visitorId;
	}
	public long getVisitTime() {
		return visitTime;
	}
	public void setVisitTime(long visitTime) {
		this.visitTime = visitTime;
	}
	
	
}
