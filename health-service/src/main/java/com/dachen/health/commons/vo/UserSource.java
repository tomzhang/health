package com.dachen.health.commons.vo;

/**
 * user source info
 *
 * @author 傅永德
 */
public class UserSource {

	/**
	 * 用户来源类型
	 *
	 * @see com.dachen.health.commons.constants.UserEnum.Source
	 */
	private Integer sourceType;
	/**
	 * 邀请人 id
	 **/
	private Integer inviterId;
	/**
	 * 用戶的端來源
	 *
	 * @see com.dachen.health.commons.constants.UserEnum.Terminal
	 */
	private Integer terminal;
	/**
	 * 集团id
	 **/
	private String groupId;
	//第三方id
	private String appId;
	//邀请活动Id
	private String inviteActivityId;
	//注册活动Id
	private String registerActivityId;
	//运营活动名称
	private String activityName;
	/**
	 * 邀请方式：短信、微信、二维码
	 * @see com.dachen.health.commons.constants.UserEnum.InviteWayEnum
	 */
	private String invateWay;

	/**
	 * 是否是科室邀请注册的医生
	 */
	private Boolean deptInvitation;

	public Integer getInviterId() {
		return inviterId;
	}

	public void setInviterId(Integer inviterId) {
		this.inviterId = inviterId;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	public Integer getTerminal() {
		return terminal;
	}

	public void setTerminal(Integer terminal) {
		this.terminal = terminal;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getInviteActivityId() {
		return inviteActivityId;
	}

	public void setInviteActivityId(String inviteActivityId) {
		this.inviteActivityId = inviteActivityId;
	}

	public String getRegisterActivityId() {
		return registerActivityId;
	}

	public void setRegisterActivityId(String registerActivityId) {
		this.registerActivityId = registerActivityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getInvateWay() {
		return invateWay;
	}

	public void setInvateWay(String invateWay) {
		this.invateWay = invateWay;
	}

	public Boolean getDeptInvitation() {
		return deptInvitation;
	}

	public void setDeptInvitation(Boolean deptInvitation) {
		this.deptInvitation = deptInvitation;
	}
}
