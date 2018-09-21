package com.dachen.health.pack.account.entity.po;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： Bank<br>
 * Description： 银行卡<br>
 * 
 * @author fanp
 * @createTime 2015年8月17日
 * @version 1.0.0
 */
public class BankCard {

    private Integer id;

    private Integer userId;
    
    private String groupId;//集团ID

    /* 开户人姓名 */
    private String userRealName;

    /* 银行卡号 */
    private String bankNo;

    /* 银行id */
    private Integer bankId;

    /* 银行名称 */
    private String bankName;

    /* 支行 */
    private String subBank;

    /* 是否删除 */
    private Integer isDelete;
    
    /**
     * 是否是默认
     */
    private Boolean isDefault;
    
    /**身份证号**/
    private String personNo;

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

	public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
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

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
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

	
}
