package com.dachen.health.pack.incomeNew.constant;

public class IncomeEnumNew {
	
	public enum ExpendType{
//		1=提现手续费，2=其它
		carryFee(1,"提现手续费"),
		other(2,"其它");
		
		private int index;
		private String title;
		
		
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}


		private ExpendType(int index,String title){
			this.index=index;
			this.title=title;
		}
	}
	public enum ExpendStatus{
		finished(4,"其它");
		
		private int index;
		private String title;
		
		
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}


		private ExpendStatus(int index,String title){
			this.index=index;
			this.title=title;
		}
	}
	
	
	public enum SettleStatus{
		forbidden(1,"不允许结算"),
		unsettle(2,"未结算"),
		settled(3,"已结算"),
		expired(4,"已过期");
		
		
		private int index;
		private String title;
		
		
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
		
		private SettleStatus(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum ObjectType{
		doctor(1,"医生"),
		group(2,"集团");
		
		private int index;
		private String title;
		
		
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}


		private ObjectType(int index,String title){
			this.index=index;
			this.title=title;
		}
	}
	
	public enum LogType{
//		1=订单收入，2=医生提成收入，3=集团在医生中的提成收入，4=集团在会诊订单中的分成收入；11=订单退款，12=医生提成退款，13=集团提成退款，14=提现，15=平台提成，16=提现手续费
		order(1,"订单收入"),
		doctorCommission(2,"医生提成收入"),
		groupCommission(3,"集团在医生中提成收入"),
		groupOrderCommission(4,"集团会诊订单中分成收入"),
		orderRefund(11,"订单退款"),
		doctorCommiessionRefund(12,"医生提成退款"),
		groupCommiessionRefund(13,"集团提成退款"),
		carryCash(14,"提现"),
		sysCommiession(15,"平台提成"),
		carryFee(16,"提现手续费"),
		groupOrderCommissionRefund(17,"在会诊订单中的分成退款");
		
		private int index;
		private String title;
		
		
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}


		private LogType(int index,String title){
			this.index=index;
			this.title=title;
		}
	}

}
