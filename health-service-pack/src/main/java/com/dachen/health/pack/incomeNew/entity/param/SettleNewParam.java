package com.dachen.health.pack.incomeNew.entity.param;

import com.dachen.health.pack.incomeNew.entity.po.SettleNew;

public class SettleNewParam extends SettleNew {
	
	private Double expandMoney;//扣减金额
	private Double settleMoney;//结算金额
	
	private int pageIndex =0;
	private int pageSize = 15;
	private int start;
	
	
	public Double getExpandMoney() {
		return expandMoney;
	}
	public void setExpandMoney(Double expandMoney) {
		this.expandMoney = expandMoney;
	}
	public Double getSettleMoney() {
		return settleMoney;
	}
	public void setSettleMoney(Double settleMoney) {
		this.settleMoney = settleMoney;
	}
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getStart() {
		return (pageIndex*pageSize);
	}

}
