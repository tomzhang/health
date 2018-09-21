package com.dachen.health.group.stat.entity.param;

import java.util.List;

import com.dachen.commons.page.PageVO;

/**
 * ProjectName： health-group<br>
 * ClassName： AssessStatParam<br>
 * Description：考核统计param <br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public class StatParam extends PageVO {
	
	/* 病种Id */
	private String diseaseId;

    /* 地区id */
    private Integer areaId;

    /* 医生id */
    private Integer doctorId;

    /* 集团id */
    private String groupId;

    /* 开始时间 */
    private Long startTime;

    /* 结束时间 */
    private Long endTime;

    private Integer status;

    private List<Integer> userIds;
    
    /**
     * 医生id list
     */
    private List<Integer> doctorIds;
    
    /*statPatient：统计维度（1：集团；2：组织机构、3：医生、4：病种*/
    /*statDoctor：统计维度（1：病种、2：职称、3：区域）*/
    private Integer type;
    
    private String typeId;

    /* 病种d */
    private List<String> diseaseIds;
    
    private boolean showOnJob=Boolean.FALSE;// 显示在职医生
    private String keyword;// 关键词 搜索
    private String[] statuses;
    
    public String[] getStatuses() {
		return statuses;
	}

	public void setStatuses(String[] statuses) {
		this.statuses = statuses;
	}

	public boolean isShowOnJob() {
		return showOnJob;
	}

	public void setShowOnJob(boolean showOnJob) {
		this.showOnJob = showOnJob;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}

	public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public List<Integer> getDoctorIds() {
		return doctorIds;
	}

	public void setDoctorIds(List<Integer> doctorIds) {
		this.doctorIds = doctorIds;
	}

    public List<String> getDiseaseIds() {
        return diseaseIds;
    }

    public void setDiseaseIds(List<String> diseaseIds) {
        this.diseaseIds = diseaseIds;
    }
}
