package com.dachen.health.base.entity.vo;

import java.util.List;

import org.mongodb.morphia.annotations.Property;

/**
 * ProjectName： health-service<br>
 * ClassName： DeptVO<br>
 * Description：科室VO <br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public class DeptVO {

    @Property("_id")
    private String id;

    private String name;

    private String parentId;
    
    private Integer isLeaf;
    
    private Integer enableStatus;
    
    private List<DeptVO> children;
    
    /**
     * 该节点的父节点
     * @author wangqiao
     * @date 2016年3月30日
     */
    private DeptVO parentDept;
    
    /**
	 * 权重
	 */
	private Integer weight;
	
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }

	public Integer getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(Integer enableStatus) {
		this.enableStatus = enableStatus;
	}

	public List<DeptVO> getChildren() {
		return children;
	}

	public void setChildren(List<DeptVO> children) {
		this.children = children;
	}

	public DeptVO getParentDept() {
		return parentDept;
	}

	public void setParentDept(DeptVO parentDept) {
		this.parentDept = parentDept;
	}

	
}
