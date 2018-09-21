package com.dachen.health.pack.stat.entity.param;

import java.util.List;

import com.dachen.commons.page.PageVO;

/**
 * ProjectName： health-group<br>
 * ClassName： PatientStatParam<br>
 * Description：患者统计param <br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public class PatientStatParam extends PageVO {

    /* 医生id */
    private List<Integer> doctorIds;
    
    /* 病种d */
    private List<String> diseaseIds;

    /* 用户id */
    private Integer userId;

    /*患者id*/
    private Integer patientId;
    
    private List<Integer> patientIds;
    
    /* 订单状态 */
    private Integer status;
    
    private String groupId;
    
    private List<Integer> orderIds;
    
    /**地区信息**/
    private String area;

    public List<Integer> getPatientIds() {
		return patientIds;
	}

	public void setPatientIds(List<Integer> patientIds) {
		this.patientIds = patientIds;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public List<Integer> getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(List<Integer> orderIds) {
		this.orderIds = orderIds;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
