package com.dachen.health.community.dao;

import java.util.List;

import com.dachen.health.community.entity.po.Circle;

public interface ICircleDao extends BaseDao<Circle>{
	public List<Circle> getByGroupCircle(String groupId);
	public Circle firstCircle(String groupId);
	public Circle upCircle(String groupId,Long nowTop);
	public Circle nextCircle(String groupId,Long nowTop);
	public Circle mainCircle(String groupId);
	/**
	 * 查找时候有同名的栏目
	 * @param groupId
	 * @param name
	 * @return
	 */
	public Circle getByNameCircle(String groupId,String name);
	
}
