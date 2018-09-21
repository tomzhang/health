package com.dachen.pub.model.param;

import java.util.List;

import com.dachen.im.server.data.ImgTextMsg;

public class PubMsgParam {
	
	private String pubId;
	
//	private String userId;
	
	private List<String>to;
	
	private boolean toAll;
	/**
	 * 1:文本消息；2：单图文（新闻消息）；3：多图文 4、分享新闻
	 */
	private int model;
	/**
	 * 发送类型
	 * sendType=0：表示回复
     * sendType=1：表示广播(消息只发送给订阅者)
     * sendType=2：表示广播(消息发送给订阅者、客服、管理员等角色)
	 */
	private int sendType=1;

	/**
	 * 是否需要推送 通知栏
	 */
	private Boolean isPush;

	private String content;
	
	private String author=null;
	
	/*
	 * 是否同时发送给健康动态公共号
	 */
	private boolean toNews=false;
	 /**
     * 图文消息内容
     */
    private List<ImgTextMsg>mpt;
    
    private String source;
    
	public String getPubId() {
		return pubId;
	}
	public void setPubId(String pubId) {
		this.pubId = pubId;
	}
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	public int getModel() {
		return model;
	}
	public void setModel(int model) {
		this.model = model;
	}
	public List<ImgTextMsg> getMpt() {
		return mpt;
	}
	public void setMpt(List<ImgTextMsg> mpt) {
		this.mpt = mpt;
	}
	public boolean isToAll() {
		return toAll;
	}
	public void setToAll(boolean toAll) {
		this.toAll = toAll;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
//	public String getUserId() {
//		return userId;
//	}
//	public void setUserId(String userId) {
//		this.userId = userId;
//	}
	public int getSendType() {
		return sendType;
	}
	public void setSendType(int sendType) {
		this.sendType = sendType;
	}
	public boolean isToNews() {
		return toNews;
	}
	public void setToNews(boolean toNews) {
		this.toNews = toNews;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public Boolean getPush() {
		return isPush;
	}

	public void setPush(Boolean push) {
		isPush = push;
	}
}
