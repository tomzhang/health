package com.dachen.health.community.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by liming on 2016/8/17.
 */
@Entity(value="c_community_circle", noClassnameStored = true)
public class Circle {
    @Id
    private String id;
    /**
     * 集团id
     */
    private String groupId;
    /**
     * 圈子名称
     */
    private String name;
    /**
     * 置顶
     */
    private Long top;
    /**
     *状态
     */
    private String state;
    /**
     * 主圈子
     */
    private String main;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getTop() {
		return top;
	}
	public void setTop(Long top) {
		this.top = top;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getMain() {
		return main;
	}
	public void setMain(String main) {
		this.main = main;
	}
	
    



}
