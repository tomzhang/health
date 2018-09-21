package com.dachen.medice.vo;

/**
 *  "period": {
                "unit": "Day",
                "text": "1 天",
                "number": 1
            }
 * @author Administrator
 *
 */
public class DrugPeriod {
	private String unit;
	private String text;
	private int number;
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
}
