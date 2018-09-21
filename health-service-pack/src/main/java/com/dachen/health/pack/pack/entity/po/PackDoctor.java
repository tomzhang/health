package com.dachen.health.pack.pack.entity.po;

/**
 * @author 李淼淼
 * @version 1.0 2015-10-24
 */
public class PackDoctor {
    private Integer id;

    private Integer packId;

    private Integer doctorId;

    private Integer splitRatio;

    /**
     * 1接收提醒、0否
     */
    private Integer receiveRemind;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return pack_id
     */
    public Integer getPackId() {
        return packId;
    }

    /**
     * @param packId
     */
    public void setPackId(Integer packId) {
        this.packId = packId;
    }

    /**
     * @return doctor_id
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * @param doctorId
     */
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
	
	public boolean ifReceiveRemind() {
		if (null != this.getReceiveRemind() && 1 == this.getReceiveRemind()) {
			return true;
		}
		return false;
	}

	public PackDoctor() {
	}
	
	public PackDoctor(Pack pack) {
		this.setPackId(pack.getId());
		this.setDoctorId(pack.getDoctorId());
		this.setSplitRatio(100);	// 只是一名医生，分成比例是100
		this.setReceiveRemind(0);	// 默认不接受提醒
		// TODO: 可以冗余是否主医生字段
	}

	@Override
	public String toString() {
		return "PackDoctor [id=" + id + ", packId=" + packId + ", doctorId=" + doctorId + ", splitRatio=" + splitRatio
				+ ", receiveRemind=" + receiveRemind + "]";
	}
    
}