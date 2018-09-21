package com.dachen.health.group.group.entity.vo;

import java.util.List;

/**
 * 
 * @author pijingwei
 * @date 邀请关系
 */
public class InviteRelation {
	
	/**
	 * 邀请人Id
	 */
	private Integer inviterId;
	
	/**
	 * 被邀请人Id
	 */
	private Integer inviteeId;

	/**
	 * 被邀请人所邀请总人数
	 */
	private Integer inviteCount;
	
	/* 头像名称 */
    private String headPicFileName;
    
    /* 头像地址 */
    private String headPicFilePath;
    
    private String name;
	
	/**
	 * 邀请日期
	 */
	private Long inviteDate;
	
	/**
	 * 邀请信息
	 */
	private String inviteMsg;
	
	/* 我邀请了谁 */
	private List<InviteRelation> myInvite;

	public Integer getInviterId() {
		return inviterId;
	}

	public void setInviterId(Integer inviterId) {
		this.inviterId = inviterId;
	}

	public Integer getInviteeId() {
		return inviteeId;
	}

	public void setInviteeId(Integer inviteeId) {
		this.inviteeId = inviteeId;
	}

	public String getHeadPicFileName() {
		return headPicFileName;
	}

	public void setHeadPicFileName(String headPicFileName) {
		this.headPicFileName = headPicFileName;
	}

	public String getHeadPicFilePath() {
		return headPicFilePath;
	}

	public void setHeadPicFilePath(String headPicFilePath) {
		this.headPicFilePath = headPicFilePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getInviteDate() {
		return inviteDate;
	}

	public void setInviteDate(Long inviteDate) {
		this.inviteDate = inviteDate;
	}

	public String getInviteMsg() {
		return inviteMsg;
	}

	public void setInviteMsg(String inviteMsg) {
		this.inviteMsg = inviteMsg;
	}

	public List<InviteRelation> getMyInvite() {
		return myInvite;
	}

	public void setMyInvite(List<InviteRelation> myInvite) {
		this.myInvite = myInvite;
	}

	public Integer getInviteCount() {
		return inviteCount;
	}

	public void setInviteCount(Integer inviteCount) {
		this.inviteCount = inviteCount;
	}

}
