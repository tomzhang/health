package com.dachen.health.api.client.pack.entity;

import java.io.Serializable;

public class CPackDoctor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;

    private Integer packId;

    private Integer doctorId;

    private Integer splitRatio;

    /**
     * 1接收提醒、0否
     */
    private Integer receiveRemind;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPackId() {
		return packId;
	}

	public void setPackId(Integer packId) {
		this.packId = packId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getSplitRatio() {
		return splitRatio;
	}

	public void setSplitRatio(Integer splitRatio) {
		this.splitRatio = splitRatio;
	}

	public Integer getReceiveRemind() {
		return receiveRemind;
	}

	public void setReceiveRemind(Integer receiveRemind) {
		this.receiveRemind = receiveRemind;
	}
    
}
