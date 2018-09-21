package com.dachen.health.community.service;

import java.util.List;

import com.dachen.health.community.entity.po.Circle;

public interface ICircleService {
	/**
	 * 获取圈子列表
	 * @param groupId
	 * @return
	 */
	public List<Circle> getByGroupCircle(String groupId);
	/**
	 * 新增圈子
	 * @param name
	 */
	public void addCircle(String name,String groupId);
	/**
	 * 上移圈子
	 * @param id
	 */
	public void topCircle(String id,String type,String groupId);
	
	public void deleteCircle(String id);
	
	public void updateCircle(String id,String name);
	
}
