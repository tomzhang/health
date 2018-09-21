package com.dachen.health.base.entity.vo;

import java.util.List;

import org.mongodb.morphia.annotations.Property;

/**
 * ProjectName： health-service<br>
 * ClassName： AreaVO<br>
 * Description： 省市县地区分类<br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public class AreaVO {
	
	@Property("_id")
	private String id;

    private Integer code;

    private String name;

    private Integer pcode;
    
    private Long lastUpdatorTime;
    
    private List<AreaVO> children;
    
    private Integer isHot=0;
    
    

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPcode() {
        return pcode;
    }

    public void setPcode(Integer pcode) {
        this.pcode = pcode;
    }

	public Long getLastUpdatorTime() {
		return lastUpdatorTime;
	}

	public void setLastUpdatorTime(Long lastUpdatorTime) {
		this.lastUpdatorTime = lastUpdatorTime;
	}

	public List<AreaVO> getChildren() {
		return children;
	}

	public void setChildren(List<AreaVO> children) {
		this.children = children;
	}

	public Integer getIsHot() {
		return isHot;
	}

	public void setIsHot(Integer isHot) {
		this.isHot = isHot;
	}
	
}
