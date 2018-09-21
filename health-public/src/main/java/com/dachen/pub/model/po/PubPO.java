package com.dachen.pub.model.po;

import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

public class PubPO {
	private String pid="";//公共号（）
	private String name;	//名称
	private String nickName;//名称
	/**
	 * 图片地址
	 */
	private String photourl;
	
	/**
	 * 二维码地址
	 */
	private String qrUrl;
	
	/**
	 * 介绍
	 */
	private String note;
	
	private boolean isDefault = false;
	
	private String rtype;
	
	/*会话组Id*/
//	@Indexed(value = IndexDirection.ASC)
//	private String gid="";
	
	/*医生集团Id*/
	@Indexed(value = IndexDirection.ASC)
	private String mid="";
//	public String midname;//医生集团 名称
	
	/*创建时间*/
	private Long creatTime;
	private String creator;
//	private JSONArray menu;
	
	private boolean reply;	//可回复
	private boolean cssb;	//个人可订退
	private boolean allssb;	//全部人员订阅

//    public boolean fold=true;	//折叠消息
//    public boolean remind=false;//推送消息提醒
//    public boolean tmpl=false;//模板
//    public boolean share=false;//分享
	
	private int state=0;//状态: 0-新增 1-提交 2-启用
//    public String tmplid; //母模板

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getPhotourl() {
		return photourl;
	}

	public void setPhotourl(String photourl) {
		this.photourl = photourl;
	}

	public String getQrUrl() {
		return qrUrl;
	}

	public void setQrUrl(String qrUrl) {
		this.qrUrl = qrUrl;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

//	public String getGid() {
//		return gid;
//	}
//
//	public void setGid(String gid) {
//		this.gid = gid;
//	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

//	public JSONArray getMenu() {
//		return menu;
//	}
//
//	public void setMenu(JSONArray menu) {
//		this.menu = menu;
//	}

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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Long getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Long creatTime) {
		this.creatTime = creatTime;
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

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

}
