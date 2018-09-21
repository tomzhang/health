package com.dachen.health.teachCenter.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "i_article_collect")
public class ArticleCollect {
	
	@Id
	private ObjectId id;
	
	private String articleId;//文章ID
	private Integer collectorType;//收藏者类型1：中心，2：集团，3：个体医生
	private String  collectorId;//收藏者ID
	private Integer collectType;// 	1：收藏，2：新建
	private long  collectTime;//收藏时间
	
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public Integer getCollectorType() {
		return collectorType;
	}
	public void setCollectorType(Integer collectorType) {
		this.collectorType = collectorType;
	}
	public String getCollectorId() {
		return collectorId;
	}
	public void setCollectorId(String collectorId) {
		this.collectorId = collectorId;
	}
	public Integer getCollectType() {
		return collectType;
	}
	public void setCollectType(Integer collectType) {
		this.collectType = collectType;
	}
	public long getCollectTime() {
		return collectTime;
	}
	public void setCollectTime(long collectTime) {
		this.collectTime = collectTime;
	}

	
}
