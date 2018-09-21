package com.dachen.health.pack.order.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.NotSaved;

/**
 * Created by qinyuan.chen
 * Date:2017/1/4
 * Time:16:18
 */
@Entity(value="p_assistant_session_relation", noClassnameStored = true)
public class AssistantSessionRelation extends BaseInfo {

    private Integer userId;//用户ID

    private Integer patientId;//患者ID

    private Integer doctorId;//医生ID

    private Integer assistantId;//助手ID

    private String msgGroupId;//会话ID
    
    private Integer type;//会话类型1:医生-助手;2:患者-助手
    
    @NotSaved
    private String doctorName;

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

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(Integer assistantId) {
        this.assistantId = assistantId;
    }

    public String getMsgGroupId() {
        return msgGroupId;
    }

    public void setMsgGroupId(String msgGroupId) {
        this.msgGroupId = msgGroupId;
    }

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
    
}
