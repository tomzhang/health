package com.dachen.health.pack.incomeNew.entity.vo;

public class DoctorMoneyVO {
	
	private double balance;//余额
	private double totalMoney;//总收入
	private double unFinshedMoney;//未完成订单金额
	private int totalNum;//总笔数
	private String month;
	private int year;
	
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public double getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}
	public double getUnFinshedMoney() {
		return unFinshedMoney;
	}
	public void setUnFinshedMoney(double unFinshedMoney) {
		this.unFinshedMoney = unFinshedMoney;
	}
	public int getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
}
