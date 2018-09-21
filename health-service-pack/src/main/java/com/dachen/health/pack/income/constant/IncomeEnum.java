package com.dachen.health.pack.income.constant;

public class IncomeEnum {
	
	public static double MIN_SETTLE_CONSTANT = 500;
	
	public enum SettleStatus{
		结算(1,"已结算"),
		未结算(2,"未结算");
		
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
	
	/**
	 * 收入表中记录  标记是否付款
	 *@author wangqiao
	 *@date 2016年1月25日
	 *
	 */
	public enum IncomePayStatus{
		未付款("nopay","未付款"),
		已付款("pay","已付款");
		
		private String index;
		private String title;
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
		private IncomePayStatus(String index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum SettleUserType{
		group(1,"集团类型"),
		doctor(2,"医生类型");
		
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
		
		private SettleUserType(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	
	public enum  IncomeType{
		
		直接收益(1,"非分成的，包含下级提成与直接获取的订单"),
		非提成(11,"非提成"),
		提成(12,"提成"),
		分成收益(2,"分成的");
		
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
		
		private IncomeType(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum DeleteStatus{
		
		删除(1,"删除"),
		未删除(2,"未删除");
		
		
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
		
		private DeleteStatus(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	

}
