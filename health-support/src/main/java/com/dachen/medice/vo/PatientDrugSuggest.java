package com.dachen.medice.vo;

import com.dachen.drug.api.entity.CDrugUsage;
import com.dachen.util.StringUtil;

import java.util.List;

public class PatientDrugSuggest {

	private List<CDrugUsage>   c_drug_usage_list;
	private Unit unit;
	private String pack_specification;
	private String requires_quantity;
	private String general_name;
	//显示全部用这个
	private String title;
	
	private String trade_name;
	private String manufacturer;
	private Drug drug;
	private int days;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public String getPack_specification() {
		return pack_specification;
	}
	public void setPack_specification(String pack_specification) {
		this.pack_specification = pack_specification;
	}
	public String getRequires_quantity() {
		return requires_quantity;
	}
	public void setRequires_quantity(String requires_quantity) {
		this.requires_quantity = requires_quantity;
	}
	public String getGeneral_name() {
		if(StringUtil.isBlank(title)) {
			return general_name;
		}
		return title;
	}
	public void setGeneral_name(String general_name) {
		this.general_name = general_name;
	}
	public String getTrade_name() {
		return trade_name;
	}
	public void setTrade_name(String trade_name) {
		this.trade_name = trade_name;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public Drug getDrug() {
		return drug;
	}
	public void setDrug(Drug drug) {
		this.drug = drug;
	}
	public List<CDrugUsage> getC_drug_usage_list() {
		return c_drug_usage_list;
	}
	public void setC_drug_usage_list(List<CDrugUsage> c_drug_usage_list) {
		this.c_drug_usage_list = c_drug_usage_list;
	}
 
	
	
}
