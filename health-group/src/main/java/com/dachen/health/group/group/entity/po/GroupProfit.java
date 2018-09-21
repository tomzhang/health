package com.dachen.health.group.group.entity.po;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * ProjectName： health-group<br>
 * ClassName： GroupProfit<br>
 * Description： 集团抽成关系<br>
 * 
 * @author fanp
 * @createTime 2015年9月2日
 * @version 1.0.0
 */
@Entity(value = "c_group_profit", noClassnameStored = true)
public class GroupProfit {

    @Id
    private String id;
    
    private Integer doctorId;

    /* 父id */
    private Integer parentId;

    /* 集团id */
    private String groupId;

    /* 抽成比例 */
    private Integer groupProfit;

    private Integer parentProfit;

    private Integer updator;

    private Long updatorDate;

    private String treePath;
    
    /**
     * 抽成比例配置 信息
     */
    @Embedded
    private GroupProfitConfig config;



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

    public Integer getGroupProfit() {
        return groupProfit;
    }

    public void setGroupProfit(Integer groupProfit) {
        this.groupProfit = groupProfit;
    }

    public Integer getParentProfit() {
        return parentProfit;
    }

    public void setParentProfit(Integer parentProfit) {
        this.parentProfit = parentProfit;
    }

    public Integer getUpdator() {
        return updator;
    }

    public void setUpdator(Integer updator) {
        this.updator = updator;
    }

    public Long getUpdatorDate() {
        return updatorDate;
    }

    public void setUpdatorDate(Long updatorDate) {
        this.updatorDate = updatorDate;
    }

    public String getTreePath() {
        return treePath;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
    }

	public GroupProfitConfig getConfig() {
		return config;
	}

	public void setConfig(GroupProfitConfig config) {
		this.config = config;
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
