package com.dachen.health.friend.entity.po;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

import com.dachen.health.friend.entity.vo.SalesGoodsFileVO;

@Entity(value = "u_friend_req", noClassnameStored = true)
public class FriendReq {
	
	@Id
	private ObjectId  id;
	private Integer fromUserId;
	
	private @NotSaved String fromUserName;
	
	private @NotSaved String fromHeadPicFileName;
	
	private Integer toUserId;
	
	private @NotSaved String toUserName;
	
	private @NotSaved String toHeadPicFileName;
	/**
	 * 1:等待验证；2：已接受；3：已拒绝
	 * @see FriendReqStatus#
	 */
	private int status;
	private long createTime;
	private long updateTime;
	
	private String applyContent;
	
	//请求方是不是医药代表  0  不是  1 是
	private Integer userReqType=new Integer(0);
	
	public Integer getUserReqType() {
		if (null != saleGoodFileList && saleGoodFileList.size() > 0) {
			userReqType = 1;
		}
		return userReqType;
	}
	
	public void setUserReqType(Integer userReqType) {
		this.userReqType = userReqType;
	}
	//品种文件列表
    private  List<SalesGoodsFileVO> saleGoodFileList = new ArrayList<SalesGoodsFileVO>();
	
	public String getFromHeadPicFileName() {
		return fromHeadPicFileName;
	}
	public void setFromHeadPicFileName(String fromHeadPicFileName) {
		this.fromHeadPicFileName = fromHeadPicFileName;
	}
	public String getToHeadPicFileName() {
		return toHeadPicFileName;
	}
	public void setToHeadPicFileName(String toHeadPicFileName) {
		this.toHeadPicFileName = toHeadPicFileName;
	}
	public List<SalesGoodsFileVO> getSaleGoodFileList() {
		return saleGoodFileList;
	}
	public void setSaleGoodFileList(List<SalesGoodsFileVO> saleGoodFileList) {
		this.saleGoodFileList = saleGoodFileList;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Integer getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(Integer fromUserId) {
		this.fromUserId = fromUserId;
	}
	public Integer getToUserId() {
		return toUserId;
	}
	public void setToUserId(Integer toUserId) {
		this.toUserId = toUserId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getApplyContent() {
		return applyContent;
	}
	public void setApplyContent(String applyContent) {
		this.applyContent = applyContent;
	}
	
	
}
