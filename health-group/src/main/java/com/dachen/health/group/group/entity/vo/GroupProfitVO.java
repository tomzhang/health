package com.dachen.health.group.group.entity.vo;

import org.mongodb.morphia.annotations.Property;


/**
 * ProjectName： health-group<br>
 * ClassName： GroupProfitVO<br>
 * Description： 集团抽成关系<br>
 * 
 * @author fanp
 * @createTime 2015年9月2日
 * @version 1.0.0
 */
public class GroupProfitVO implements java.io.Serializable{

    private static final long serialVersionUID = -74694124564703479L;

    private String id;

    private Integer doctorId;
    
    /* 父id */
    private Integer parentId;

    /* 集团id */
    private String groupId;

    /*抽成比例*/
    private Integer profit;

    private String treePath;



    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getProfit() {
        return profit;
    }

    public void setProfit(Integer profit) {
        this.profit = profit;
    }

    public String getTreePath() {
        return treePath;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

    
    
}
