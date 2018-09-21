package com.dachen.pub.model.param;

import java.util.List;
import java.util.Map;

public class PubParam {
	private String pid;	//名称
	private String name;	//名称
	private String nickName;//名称
	/**
	 * 图片地址
	 */
	private String photourl;
	
	/**
	 * 介绍
	 */
	private String note;
	
	/*医生集团Id*/
	private String mid="";
//	public String midname;//医生集团 名称
	
	private boolean isDefault = false; 
	private String rtype;
	
	private boolean reply;	//可回复
	private boolean cssb;	//个人可订退
	private boolean allssb;	//全部人员订阅

	private String folder;
	
	private String creator;

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	private Map<Integer,List<String>>pubUsers;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhotourl() {
		return photourl;
	}
	public void setPhotourl(String photourl) {
		this.photourl = photourl;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public boolean isReply() {
		return reply;
	}
	public void setReply(boolean reply) {
		this.reply = reply;
	}
	public boolean isCssb() {
		return cssb;
	}
	public void setCssb(boolean cssb) {
		this.cssb = cssb;
	}
	public boolean isAllssb() {
		return allssb;
	}
	public void setAllssb(boolean allssb) {
		this.allssb = allssb;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String id) {
		this.pid = id;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public String getRtype() {
		return rtype;
	}
	public void setRtype(String rtype) {
		this.rtype = rtype;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Map<Integer, List<String>> getPubUsers() {
		return pubUsers;
	}
	public void setPubUsers(Map<Integer, List<String>> pubUsers) {
		this.pubUsers = pubUsers;
	}
}
