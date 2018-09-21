package com.dachen.health.commons.constants;

/**
 * 订单 枚举 
 * @author Administrator
 *
 */
public class OrderEnum {
	
	public enum OrderActivateStatus {
		activate(1, "已激活订单"),
		noActivate(0, "未激活订单");
		
        
        private int index;

        private String title;

        private OrderActivateStatus(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
    }
	
	public enum OrderSessionOpenStatus {
		automatic(1, "自动结束"),
		manually(2, "手动结束");
		
        
        private int index;

        private String title;

        private OrderSessionOpenStatus(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
    }
	
	public enum OrderNoitfyType {
        neworder(1, "neworder"),
        payorder(3, "payorder"),
		editDisesase(4, "editDisesase"),
		appointTime(5, "appointTime"),
		beginService(6, "beginService"),
		endService(7, "endService"),
		cancelOrder(8, "cancelOrder"),
		beginCall(9, "beginCall"),
		endCall(10, "endCall"),
		prepareService(11, "prepareService"),
		abandonService(12,"abandonService"),
		refuseorder(13,"refuseorder"), 
		changeAppointmentTime(14,"changeAppointmentTime");
        
        private int index;

        private String title;

        private OrderNoitfyType(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
    }
	
	public static String getOrderType(Integer type) {
		for (OrderType orderType : OrderType.values()) {
			if (orderType.getIndex() == type.intValue()) {
				return orderType.title;
			}
		}
		throw new IllegalArgumentException("无此订单类型"+type);
	}
	
	public static String getOrderStatus(Integer status){
		for (OrderStatus orderStatus : OrderStatus.values()) {
			if (orderStatus.getIndex() == status.intValue()) {
				return orderStatus.title;
			}
		}
		throw new IllegalArgumentException("无此订单状态"+status);
	}
    /**
     * ProjectName： health-service-pack<br>
     * ClassName： OrderType<br>
     * Description： 订单类型<br>
     * @author fanp
     * @createTime 2015年9月8日
     * @version 1.0.0
     */
    public enum OrderType {
        order(1, "咨询套餐"),
        checkIn(2, "报到套餐"),
    	outPatient(3, "门诊套餐"),
    	care(4, "健康关怀套餐"),
    	followUp(5,"随访套餐"),
    	throughTrain(6, "直通车套餐"),
    	consultation(7, "会诊套餐"),
    	appointment(9, "名医面对面"),
    	feeBill(10, "收费单"),
    	integral(12, "积分问诊");
        
        private int index;

        private String title;

        private OrderType(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
    }
    
	 /**
     * ProjectName： health-service-pack<br>
     * ClassName： OrderCheckStatus<br>
     * Description：订单状态  1:待预约，2：待支付，3：已支付，4：已完成，5：已取消<br>
     * @author xiepei
     * @createTime 2015年8月17日
     * @version 1.0.0
     */
    public enum OrderStatus {
    	待预约(1, "待预约"), 
        待支付(2, "待支付"),
        已支付(3,"已支付"),
        已完成(4,"已完成"),
        已取消(5,"已取消"),
        进行中(6,"进行中"),
        待完善(7,"待完善"),
        已拒绝(8,"已拒绝"),
        预约成功(10, "预约成功");
        
        private int index;

        private String title;

        private OrderStatus(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
    }
    
    public enum OrderRefundType {
    	autoRefund(1, "自动退款"),
    	applyRefund(2, "申请退款"),
    	stateRefund(3, "投诉退款");
        private int index;

        private String title;

        private OrderRefundType(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
    }
    
   
  
    /**
     * ProjectName： health-service-pack<br>
     * ClassName： OrderPayStatus<br>
     * Description：订单支付状态 <br>
     * @author xiePei
     * @createTime 2015年8月17日
     * @version 1.0.0
     */
    public enum OrderRefundStatus {
    	refundWait(1, "未申请退款"), 
    	refundApply(2, "待退款"),
    	refundSuccess(3, "退款成功"),
    	refundFailure(4, "退款失败");
        
        private int index;

        private String title;

        private OrderRefundStatus(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
    }
    
    
    /**
     * ProjectName： health-service-pack<br>
     * ClassName： CheckInStatus<br>
     * Description： 报到处理状态<br>
     * @author fanp
     * @createTime 2015年9月9日
     * @version 1.0.0
     */
    public enum CheckInStatus {
        init(1, "未处理"),
        confirm(2, "确定"),
        cancel(3, "取消");
        
        private int index;

        private String title;

        private CheckInStatus(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
    }
    
    public enum OrderRecordStatus {
    	blank(1, "未填写"),
    	confirming(2, "待确定"),
    	confirmed(3, "已确定"),//导医已确认提交给医生 稍微有点修改 有疑问找：姜宏杰
    	doc_confirmed(4, "已确定");//医生已确认
    	
    	private int index;
    	private String title;
    	private OrderRecordStatus(int index, String title) {
    		this.index = index;
    		this.title = title;
    	}
    	public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
    
    public enum OrderSessionCategory{
    	/** 目前的原型就只有这两种类型 **/
    	text_tel_checkIn_integral(1,"图文 电话 报道 积分"),
        /**
         * 有过一次图文/电话/健康关怀订单就算是复诊
         * 健康关怀会话不重用，但是下订单时需要返回客户端是否复诊信息
         */
    	care(2,"健康关怀"),
    	care_in_text_tel_integral(3,"健康关怀 里面的图文/电话/积分"),
        online(4,"在线门诊");


    	private int index;
    	private String title;
        private OrderSessionCategory(int index, String title) {
    		this.index = index;
    		this.title = title;
    	}
    	public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public enum orderCancelEnum {
        auto(1, "自动取消"),
        manual(2, "手动取消");

        int index;
        String title;

        orderCancelEnum(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
    
}
