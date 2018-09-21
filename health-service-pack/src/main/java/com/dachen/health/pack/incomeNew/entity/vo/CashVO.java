package com.dachen.health.pack.incomeNew.entity.vo;

import com.dachen.health.pack.incomeNew.entity.po.Cash;

public class CashVO extends Cash {
		private String bankName;
		private String userRealName;
		private String bankID;
		
		public String getBankName() {
			return bankName;
		}
		public void setBankName(String bankName) {
			this.bankName = bankName;
		}
		public String getUserRealName() {
			return userRealName;
		}
		public void setUserRealName(String userRealName) {
			this.userRealName = userRealName;
		}
		public String getBankID() {
			return bankID;
		}
		public void setBankID(String bankID) {
			this.bankID = bankID;
		}
		
		
}
