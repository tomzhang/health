package com.dachen.health.pack.income.entity.vo;

 
public class IncomeVONew implements java.io.Serializable{

    private static final long serialVersionUID = 4746648653422264985L;

    private Integer doctorId;

 

    /*订单状态*/
    private Integer orderStatus;
    
    /**
     * 结算状态
     */
    private Integer settleStatus;
    
    //打款情况
    private String extend2;
    
    /**
	 * 每页数据大小
	 */
	protected int pageSize = 15;
	
	protected int start;

	 private Long createTime;
	 private Long finishTime;//订单结束时间
	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getSettleStatus() {
		return settleStatus;
	}

	public void setSettleStatus(Integer settleStatus) {
		this.settleStatus = settleStatus;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Long finishTime) {
		this.finishTime = finishTime;
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}

     
}