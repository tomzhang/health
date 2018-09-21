package com.dachen.health.pack.account.entity.vo;

public class BankCardVO implements java.io.Serializable {

    private static final long serialVersionUID = -6037486187052390059L;

    private Integer id;

    private Integer userId;
    
    private String groupId;//集团ID
    
    private Integer isDelete;

    /* 银行卡号 */
    private String bankNo;

    /* 银行id */
    private Integer bankId;

    /* 银行名称 */
    private String bankName;

    /* 支行名称 */
    private String subBank;
    
    private String bankIoc;
    
    /*身份证号*/
    private String personNo;
    
    private String userRealName;
    
    public String getBankIoc() {
		return bankIoc;
	}

	public void setBankIoc(String bankIoc) {
		this.bankIoc = bankIoc;
	}

	/**
     * 是否是默认
     */
    private Boolean isDefault;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getSubBank() {
        return subBank;
    }

    public void setSubBank(String subBank) {
        this.subBank = subBank;
    }

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getPersonNo() {
		return personNo;
	}

	public void setPersonNo(String personNo) {
		this.personNo = personNo;
	}

	public String getUserRealName() {
		return userRealName;
	}

	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

}
