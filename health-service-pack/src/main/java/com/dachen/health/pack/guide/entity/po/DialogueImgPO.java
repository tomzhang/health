package com.dachen.health.pack.guide.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "t_dialogue_img",noClassnameStored = true)
public class DialogueImgPO {
	@Id
	private String id;
	//会话组id
	private String guideId;
	//图片地址
    private String  imgs; 
    //订单id
    private String orderId;
    //创建时间
    private long createTime;
    
    public DialogueImgPO() {  
    }
    
    public DialogueImgPO(String imgs) {  
    	this.imgs = imgs;  
    } 
    
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGuideId() {
		return guideId;
	}
	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}
	public String getImgs() {
		return imgs;
	}
	public void setImgs(String  imgs) {
		this.imgs = imgs;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@Override  
	public boolean equals(Object obj) {  
		DialogueImgPO s=(DialogueImgPO)obj;   
		return imgs.equals(s.imgs);   
	}  
	@Override  
	public int hashCode() {  
		String in = imgs;  
		return in.hashCode();  
	}  
}
