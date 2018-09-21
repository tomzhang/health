package com.dachen.health.pack.order.entity.vo;

import com.dachen.health.pack.patient.model.Patient;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： PreOrderVO<br>
 * Description： 初始订单信息，用于放回客户端调用第三方支付接口参数<br>
 * 
 * @author fanp
 * @createTime 2015年8月10日
 * @version 1.0.0
 */
public class PreOrderVO {

    private String payNo;
    
    private String orderInfo;
    
    private Integer orderId;
    
    private AppPayVO payReq;
    
    private Integer orderStatus;
    
    private Integer orderNo;
    
    private String gid;//会话组ID
    
    private Boolean integralOrder;//是否是积分问诊订单
    
    /**
     * 是否是新的订单
     */
    private Boolean ifNewOrder;
    
    private Integer patientId;
    
    private Patient patient;

    @Override
    public String toString() {
        return "PreOrderVO{" +
                "payNo='" + payNo + '\'' +
                ", orderInfo='" + orderInfo + '\'' +
                ", orderId=" + orderId +
                ", payReq=" + payReq +
                ", orderStatus=" + orderStatus +
                ", orderNo=" + orderNo +
                ", gid='" + gid + '\'' +
                ", integralOrder=" + integralOrder +
                ", ifNewOrder=" + ifNewOrder +
                ", patientId=" + patientId +
                ", patient=" + patient +
                '}';
    }

    public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public AppPayVO getPayReq() {
		return payReq;
	}

	public void setPayReq(AppPayVO payReq) {
		this.payReq = payReq;
	}

	public String getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}

	public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public Boolean getIfNewOrder() {
		return ifNewOrder;
	}

	public void setIfNewOrder(Boolean ifNewOrder) {
		this.ifNewOrder = ifNewOrder;
	}

	public Boolean getIntegralOrder() {
		return integralOrder;
	}

	public void setIntegralOrder(Boolean integralOrder) {
		this.integralOrder = integralOrder;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
    
}
