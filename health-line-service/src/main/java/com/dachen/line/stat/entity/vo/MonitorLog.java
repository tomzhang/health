package com.dachen.line.stat.entity.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "v_monitor_log", noClassnameStored = true)
public class MonitorLog {
	@Id
	private String id;
	private String serviceId;//护士的服务 id 或者订单id
	private int serviceCode;//服务编码
	private long time;//系统生成，时间戳
	private String remark;
	private int  noticeTimes;//这里是发短信的次数控制
	private int  type;// 0 护士id   1订单id 
	private String content;//短信内容
	private String tel;//电话号码
	
	
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public int getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(int serviceCode) {
		this.serviceCode = serviceCode;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getNoticeTimes() {
		return noticeTimes;
	}
	public void setNoticeTimes(int noticeTimes) {
		this.noticeTimes = noticeTimes;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

}
