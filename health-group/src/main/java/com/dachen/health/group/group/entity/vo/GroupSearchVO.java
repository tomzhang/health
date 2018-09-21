package com.dachen.health.group.group.entity.vo;

public class GroupSearchVO implements java.io.Serializable {

    private static final long serialVersionUID = 7513545742298669453L;

    /* 集团id */
    private String groupId;

    /* 集团名称 */
    private String groupName;

    /* 集团头像 */
    private String certPath;

    /* 集团介绍 */
    private String introduction;

    /* 擅长病种 */
    private String disease;

    /* 擅长介绍 */
    private String skill;
    
    /* 专家数量 */
    private Integer expertNum;

    /* 治疗数量 */
    private Integer cureNum;

    private Integer doctorId;

    private String doctorName;

    private String headPicFileName;

    private String hospital;

    private String departments;

    private String title;
    
    // 医生设置：免打扰（1：正常，2：免打扰）
    private String troubleFree;
    
    /**
     * 医生与医生集团的关系，C=已加入集团，J=申请待确认，A=可申请加入
     */
    private String doctorStatus;
    
    /**
     * 是否是医生集团的管理员  true=管理员，false=非管理员
     */
    private boolean groupAdmin;
    
    /**
     * 申请加入集团状态  A=可以申请，B=申请中，C=已加入集团，D=不允许加入集团
     */
    private String applyStatus;
    
    /**
     * 是否允许成员邀请医生加入
     */
    private boolean memberInvite;
    
    /**
     * 是否允许医生申请加入
     */
    private boolean memberApply;

    
    /* 角色 */
    private Integer role;
    
    /**
     * 加V认证状态
     */
    private String certStatus;
    
    //banner图片的链接
    private String bannerUrl;
    //集团简介链接
    private String contentUrl;
    
    private String groupPageURL;
    
    private String groupQrCodeURL;
    
    public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Integer getExpertNum() {
        return expertNum;
    }

    public void setExpertNum(Integer expertNum) {
        this.expertNum = expertNum;
    }

    public Integer getCureNum() {
        return cureNum;
    }

    public void setCureNum(Integer cureNum) {
        this.cureNum = cureNum;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getHeadPicFileName() {
        return headPicFileName;
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDepartments() {
        return departments;
    }

    public void setDepartments(String departments) {
        this.departments = departments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public String getTroubleFree() {
		return troubleFree;
	}

	public void setTroubleFree(String troubleFree) {
		this.troubleFree = troubleFree;
	}

	public String getDoctorStatus() {
		return doctorStatus;
	}

	public void setDoctorStatus(String doctorStatus) {
		this.doctorStatus = doctorStatus;
	}

	public boolean isGroupAdmin() {
		return groupAdmin;
	}

	public void setGroupAdmin(boolean groupAdmin) {
		this.groupAdmin = groupAdmin;
	}

	public String getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}

	public boolean isMemberInvite() {
		return memberInvite;
	}

	public void setMemberInvite(boolean memberInvite) {
		this.memberInvite = memberInvite;
	}

	public boolean isMemberApply() {
		return memberApply;
	}

	public void setMemberApply(boolean memberApply) {
		this.memberApply = memberApply;
	}

	public String getCertStatus() {
		return certStatus;
	}

	public void setCertStatus(String certStatus) {
		this.certStatus = certStatus;
	}
	
	
	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getGroupPageURL() {
		return groupPageURL;
	}

	public void setGroupPageURL(String groupPageURL) {
		this.groupPageURL = groupPageURL;
	}

	public String getGroupQrCodeURL() {
		return groupQrCodeURL;
	}

	public void setGroupQrCodeURL(String groupQrCodeURL) {
		this.groupQrCodeURL = groupQrCodeURL;
	}

	@Override
	public String toString() {
		return "GroupSearchVO [groupId=" + groupId + ", groupName=" + groupName + ", certPath=" + certPath
				+ ", introduction=" + introduction + ", disease=" + disease + ", skill=" + skill + ", expertNum="
				+ expertNum + ", cureNum=" + cureNum + ", doctorId=" + doctorId + ", doctorName=" + doctorName
				+ ", headPicFileName=" + headPicFileName + ", hospital=" + hospital + ", departments=" + departments
				+ ", title=" + title + ", troubleFree=" + troubleFree + ", doctorStatus=" + doctorStatus
				+ ", groupAdmin=" + groupAdmin + ", applyStatus=" + applyStatus + ", memberInvite=" + memberInvite
				+ ", memberApply=" + memberApply + ", role=" + role + ", certStatus=" + certStatus + "]";
	}



}
