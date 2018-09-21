package com.dachen.health.pack.account.entity.param;

import com.dachen.commons.page.PageVO;

public class BankCardParam extends PageVO{

    private Integer id;

    private Integer userId;
    
    private String bankName;
    
    private String groupId;//集团ID

    /* 银行卡号 */
    private String bankNo;

    /* 银行id */
    private Integer bankId;

    /* 支行 */
    private String subBank;
    
    private String userRealName;

    private Integer isDelete;
    
    private Boolean isDefault;
    
    private String bankCard;

    /*身份证号*/
    private String personNo;
    
    public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

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

    
    
    public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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

	public String getUserRealName() {
		return userRealName;
	}

	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

	public String getPersonNo() {
		return personNo;
	}

	public void setPersonNo(String personNo) {
		this.personNo = personNo;
	}

	@Override
	public String toString() {
		return "BankCardParam [id=" + id + ", userId=" + userId + ", bankName=" + bankName + ", groupId=" + groupId
				+ ", bankNo=" + bankNo + ", bankId=" + bankId + ", subBank=" + subBank + ", userRealName="
				+ userRealName + ", isDelete=" + isDelete + ", isDefault=" + isDefault + ", bankCard=" + bankCard
				+ ", personNo=" + personNo + "]";
	}

}
