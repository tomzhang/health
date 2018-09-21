package com.dachen.health.pack.order.entity.po;

/**
 * @author 李淼淼
 * @version 1.0 2015-10-28
 */
public class OrderDoctor {
    private Integer id;

    private Integer orderId;

    private Integer doctorId;

    private Integer splitRatio;

    //（1接收提醒、0否）
    private Integer receiveRemind;
    
    private Double splitMoney;

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
     * @return order_id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * @param orderId
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    
    public Double getSplitMoney() {
		return splitMoney;
	}

	public void setSplitMoney(Double splitMoney) {
		this.splitMoney = splitMoney;
	}

	/**
     * @return split_ratio
     */
    public Integer getSplitRatio() {
        return splitRatio;
    }

    /**
     * @param splitRatio
     */
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