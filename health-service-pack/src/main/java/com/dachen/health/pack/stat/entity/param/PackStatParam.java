package com.dachen.health.pack.stat.entity.param;

import java.util.List;

import com.dachen.commons.page.PageVO;

/**
 * ProjectName： health-group<br>
 * ClassName： PackStatParam<br>
 * Description：统计param <br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public class PackStatParam extends PageVO {

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
     * 是否忽略 价格为0的订单
     */
    private boolean ignoreZeroPrice = false;

    
    private boolean showOnJob;// 显示在职医生
    private String keyword;// 关键词 搜索
    private String[] statuses;
    
    
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

	public String[] getStatuses() {
		return statuses;
	}

	public void setStatuses(String[] statuses) {
		this.statuses = statuses;
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

	public boolean isIgnoreZeroPrice() {
		return ignoreZeroPrice;
	}

	public void setIgnoreZeroPrice(boolean ignoreZeroPrice) {
		this.ignoreZeroPrice = ignoreZeroPrice;
	}
    
    

}
