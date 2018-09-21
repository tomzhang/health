package com.dachen.health.api.client.order.entity;

public class COrderEnum {
	
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
}
