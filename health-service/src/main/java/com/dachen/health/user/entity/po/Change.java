package com.dachen.health.user.entity.po;

public class Change {
	
	private String paramName;
	
	private String paramAlias;
	
	private String oldValue;
	
	private String newValue;

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamAlias() {
		return paramAlias;
	}

	public void setParamAlias(String paramAlias) {
		this.paramAlias = paramAlias;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public Change(String paramName, String paramAlias, String oldValue, String newValue) {
		super();
		this.paramName = paramName;
		this.paramAlias = paramAlias;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Change(String oldValue, String newValue) {
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Change() {
		super();
	}
	
}
