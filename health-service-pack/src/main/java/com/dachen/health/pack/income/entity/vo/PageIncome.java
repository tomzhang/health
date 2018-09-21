package com.dachen.health.pack.income.entity.vo;

import java.util.LinkedHashSet;
import java.util.List;

import com.dachen.commons.page.PageVO;

public class PageIncome extends PageVO{
	
	private String keyYM;//年月
	private String keyY;//年
	private String keyM;//月
	private List<PageIncomeDetails> list;
	
	private List<PageIncomeDetails> shareList;
	private List<PageIncomeDetails> unshareList;
	
	private LinkedHashSet<PageIncome> yList;//年列表
	private LinkedHashSet<PageIncome> mList;//月列表
	
	private Integer status;
	private String settleTime;
	
	private double finishedMoney ;
	private double unFinishedMoney;
	
	
	

	private String groupId;//集团ID
	private double totalMoney;//总金额
	private int totalNum;//总订单数
	
	
	
	public String getKeyYM() {
		return keyYM;
	}
	public void setKeyYM(String keyYM) {
		this.keyYM = keyYM;
	}
	public String getKeyY() {
		return keyY;
	}
	public void setKeyY(String keyY) {
		this.keyY = keyY;
	}
	public String getKeyM() {
		return keyM;
	}
	public void setKeyM(String keyM) {
		this.keyM = keyM;
	}
	public List<PageIncomeDetails> getList() {
		return list;
	}
	public void setList(List<PageIncomeDetails> list) {
		this.list = list;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getSettleTime() {
		return settleTime;
	}
	public void setSettleTime(String settleTime) {
		this.settleTime = settleTime;
	}
	public double getFinishedMoney() {
		return finishedMoney;
	}
	public void setFinishedMoney(double finishedMoney) {
		this.finishedMoney = finishedMoney;
	}
	public double getUnFinishedMoney() {
		return unFinishedMoney;
	}
	public void setUnFinishedMoney(double unFinishedMoney) {
		this.unFinishedMoney = unFinishedMoney;
	}
	public double getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public int getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	public List<PageIncomeDetails> getShareList() {
		return shareList;
	}
	public void setShareList(List<PageIncomeDetails> shareList) {
		this.shareList = shareList;
	}
	public List<PageIncomeDetails> getUnshareList() {
		return unshareList;
	}
	public void setUnshareList(List<PageIncomeDetails> unshareList) {
		this.unshareList = unshareList;
	}
	public LinkedHashSet<PageIncome> getyList() {
		return yList;
	}
	public void setyList(LinkedHashSet<PageIncome> yList) {
		this.yList = yList;
	}
	public LinkedHashSet<PageIncome> getmList() {
		return mList;
	}
	public void setmList(LinkedHashSet<PageIncome> mList) {
		this.mList = mList;
	}
}
