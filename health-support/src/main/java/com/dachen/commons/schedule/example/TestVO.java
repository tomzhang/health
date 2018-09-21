package com.dachen.commons.schedule.example;

import java.util.List;

import com.alibaba.fastjson.JSON;

public class TestVO {
	
	private int id;
	
	private String name;
	
	private List<String>list;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	
	public String toString()
	{
		return JSON.toJSONString(this);
	}
}
